package org.mwc.debrief.track_shift.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackShiftListener;
import org.mwc.cmap.core.ui_support.PartMonitor;
import Debrief.Tools.Tote.Watchable;
import Debrief.Tools.Tote.WatchableList;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.ptplot.jfreeChart.DateAxisEditor;
import MWC.GUI.ptplot.jfreeChart.FormattedJFreeChart;
import MWC.GUI.ptplot.jfreeChart.Utils.ColourStandardXYItemRenderer;
import MWC.GUI.ptplot.jfreeChart.Utils.ColouredDataItem;
import MWC.GUI.ptplot.jfreeChart.Utils.DatedToolTipGenerator;
import MWC.GUI.ptplot.jfreeChart.Utils.ModifiedVerticalNumberAxis;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

import com.jrefinery.chart.ChartPanel;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.StandardXYItemRenderer;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.FixedMillisecond;
import com.jrefinery.data.Range;
import com.jrefinery.data.SeriesException;
import com.jrefinery.data.TimeSeriesCollection;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class StackedDotsView extends ViewPart
{

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;

	/**
	 * the plot we're plotting to plot
	 */
	XYPlot _myPlot;

	/**
	 * legacy helper class
	 */
	StackedDotHelper _myHelper;

	/**
	 * our track-data provider
	 */
	protected TrackManager _theTrackDataListener;

	/**
	 * our listener for tracks being shifted...
	 */
	protected TrackShiftListener _myShiftListener;

	/**
	 * flag indicating whether we should override the y-axis to ensure that zero
	 * is always in the centre
	 */
	private Action _centreYAxis;

	/**
	 * flag indicating whether we should only show stacked dots for visible fixes
	 */
	Action _onlyVisible;

	/**
	 * our layers listener...
	 */
	protected DataListener _layersListener;

	/**
	 * the set of layers we're currently listening to
	 */
	protected Layers _ourLayersSubject;

	protected TrackDataProvider _myTrackDataProvider;

	Composite _holder;

	FormattedJFreeChart _myChart;

	/**
	 * The constructor.
	 */
	public StackedDotsView()
	{
		_myHelper = new StackedDotHelper();

		// create the actions - the 'centre-y axis' action may get called before the
		// interface is shown
		makeActions();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		// right, we need an SWT.EMBEDDED object to act as a holder
		_holder = new Composite(parent, SWT.EMBEDDED);

		// now we need a Swing object to put our chart into
		Frame plotControl = SWT_AWT.new_Frame(_holder);
		plotControl.setLayout(new BorderLayout());

		// hey - now create the stacked plot!
		createStackedPlot(plotControl);

		// /////////////////////////////////////////
		// ok - listen out for changes in the view
		// /////////////////////////////////////////
		watchMyParts();

		// put the actions in the UI
		contributeToActionBars();
	}

	/**
	 * view is closing, shut down, preserve life
	 */
	public void dispose()
	{
		System.out.println("disposing of stacked dots");
		// get parent to ditch itself
		super.dispose();

		// are we listening to any layers?
		if (_ourLayersSubject != null)
			_ourLayersSubject.removeDataReformattedListener(_layersListener);

		if (_theTrackDataListener != null)
		{
			_theTrackDataListener.removeTrackShiftListener(_myShiftListener);
			_theTrackDataListener.removeTrackShiftListener(_myShiftListener);
		}

	}

	private void makeActions()
	{
		_centreYAxis = new Action("Center Y axis on origin", Action.AS_CHECK_BOX)
		{
			public void run()
			{
				super.run();
				// ok - redraw the plot we may have changed the axis centreing
				updateStackedDots();
			}
		};
		_centreYAxis.setText("Center Y Axis");
		_centreYAxis.setChecked(true);
		_centreYAxis.setToolTipText("Keep Y origin in centre of axis");
		_centreYAxis.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/follow_selection.gif"));

		_onlyVisible = new Action("Only draw dots for visible data points",
				Action.AS_CHECK_BOX)
		{

			public void run()
			{
				super.run();
				// we need to get a fresh set of data pairs - the number may have
				// changed
				_myHelper.initialise(_theTrackDataListener, true);

				// and a new plot please
				updateStackedDots();
			}
		};
		_onlyVisible.setText("Only plot visible data");
		_onlyVisible.setChecked(true);
		_onlyVisible.setToolTipText("Only draw dots for visible data points");
		_onlyVisible.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/follow_selection.gif"));
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(_centreYAxis);
		manager.add(_onlyVisible);
		// and the help link
		manager.add(new Separator());
		manager.add(CorePlugin.createOpenHelpAction("org.mwc.debrief.help.TrackShifting", null, this));
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
	}

	/**
	 * method to create a working plot (to contain our data)
	 * 
	 * @return the chart, in it's own panel
	 */
	private void createStackedPlot(Frame plotControl)
	{

		// first create the x (time) axis
		final HorizontalDateAxis xAxis = new HorizontalDateAxis("time");

		xAxis.setStandardTickUnits(DateAxisEditor.createStandardDateTickUnitsAsTickUnits());

		// now the y axis, inverting it if applicable
		final ModifiedVerticalNumberAxis yAxis = new ModifiedVerticalNumberAxis(
				"Error (degs)");

		// create the special stepper plot
		_myPlot = new XYPlot(null, xAxis, yAxis);
		// create the bit to create custom tooltips
		final XYToolTipGenerator tooltipGenerator = new DatedToolTipGenerator();

		// and the bit to plot individual points in discrete colours
		_myPlot.setRenderer(new ColourStandardXYItemRenderer(
				StandardXYItemRenderer.SHAPES_AND_LINES, tooltipGenerator, null));
		// put the plot into a chart
		_myChart = new FormattedJFreeChart("Bearing error", null,
				_myPlot, false);
		_myChart.setShowSymbols(true);
		// shrink the title
		_myChart.setTitleFont(_myChart.getTitleFont().deriveFont(8));

		final ChartPanel plotHolder = new ChartPanel(_myChart);
		plotHolder.setMouseZoomable(true, true);
		plotHolder.setDisplayToolTips(false);

		// and insert into the panel
		plotControl.add(plotHolder, BorderLayout.CENTER);
	}

	/**
	 * the track has been moved, update the dots
	 */
	void updateStackedDots()
	{

		// get the current set of data to plot
		final TimeSeriesCollection newData = _myHelper
				.getUpdatedSeries(_theTrackDataListener);

		if (_centreYAxis.isChecked())
		{
			// set the y axis to autocalculate
			_myPlot.getVerticalValueAxis().setAutoRange(true);
		}

		// store the new data (letting it autocalcualte)
		_myPlot.setDataset(newData);

		// we will only centre the y-axis if the user hasn't performed a zoom
		// operation
		if (_centreYAxis.isChecked())
		{
			// do a quick fudge to make sure zero is in the centre
			final Range rng = _myPlot.getVerticalValueAxis().getRange();
			final double maxVal = Math.max(Math.abs(rng.getLowerBound()), Math.abs(rng
					.getUpperBound()));
			_myPlot.getVerticalValueAxis().setRange(-maxVal, maxVal);
		}
	}

	/**
	 * sort out what we're listening to...
	 */
	private void watchMyParts()
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// cool, remember about it.
						_theTrackDataListener = (TrackManager) part;

						// ok - fire off the event for the new tracks
						_myHelper.initialise(_theTrackDataListener, false);

					}
				});
		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// ok, ditch it.
						_theTrackDataListener = null;

						_myHelper.reset();
					}
				});
		_myPartMonitor.addPartListener(TrackDataProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// ok - let's start off with a clean plot
						_myPlot.setDataset(null);

						// cool, remember about it.
						TrackDataProvider dataP = (TrackDataProvider) part;

						// do we need to generate the shift listener?
						if (_myShiftListener == null)
						{
							_myShiftListener = new TrackShiftListener()
							{
								public void trackShifted(TrackWrapper subject)
								{
									updateStackedDots();
								}
							};
						}

						// is this the one we're already listening to?
						if (_myTrackDataProvider != dataP)
						{
							// nope, better stop listening then
							if (_myTrackDataProvider != null)
								_myTrackDataProvider.removeTrackShiftListener(_myShiftListener);
						}

						// ok, start listening to it anyway
						_myTrackDataProvider = dataP;
						_myTrackDataProvider.addTrackShiftListener(_myShiftListener);

						// hey - fire a dot update
						updateStackedDots();

					}
				});

		_myPartMonitor.addPartListener(TrackDataProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						TrackDataProvider tdp = (TrackDataProvider) part;
						tdp.removeTrackShiftListener(_myShiftListener);

						if (tdp == _myTrackDataProvider)
							_myTrackDataProvider = null;

						// hey - lets clear our plot
						updateStackedDots();
					}
				});

		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						Layers theLayers = (Layers) part;

						// do we need to create our listener
						if (_layersListener == null)
						{
							_layersListener = new Layers.DataListener()
							{
								public void dataModified(Layers theData, Layer changedLayer)
								{
								}

								public void dataExtended(Layers theData)
								{
								}

								public void dataReformatted(Layers theData, Layer changedLayer)
								{
									_myHelper.initialise(_theTrackDataListener, false);
									updateStackedDots();
								}
							};
						}

						// is this what we're listening to?
						if (_ourLayersSubject != theLayers)
						{
							// nope, stop listening to the old one (if there is one!)
							if (_ourLayersSubject != null)
								_ourLayersSubject.removeDataReformattedListener(_layersListener);
						}

						// now start listening to the new one.
						theLayers.addDataReformattedListener(_layersListener);
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						Layers theLayers = (Layers) part;

						// is this what we're listening to?
						if (_ourLayersSubject == theLayers)
						{
							// yup, stop listening
							_ourLayersSubject.removeDataReformattedListener(_layersListener);
						}
					}

				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());
	}

	// ////////////////////////////////////////////////
	// helper class to provide support to the stacked dots
	// ////////////////////////////////////////////////
	public final class StackedDotHelper
	{
		/**
		 * the track being dragged
		 */
		private TrackWrapper _primaryTrack;

		/**
		 * the secondary track we're monitoring
		 */
		private TrackWrapper _secondaryTrack;

		/**
		 * the set of points to watch on the primary track
		 */
		private Vector<Doublet> _primaryDoublets;

		/**
		 * the set of points to watch on the secondary track
		 */
		private Vector<Doublet> _secondaryDoublets;

		// ////////////////////////////////////////////////
		// CONSTRUCTOR
		// ////////////////////////////////////////////////

		/**
		 * constructor - takes a set of layers, within which it identifies the track
		 * wrappers
		 * 
		 * @param theData
		 *          the set of data to provide stacked dots for
		 * @param myTrack
		 *          the track being dragged
		 */
		StackedDotHelper()
		{
		}

		// ////////////////////////////////////////////////
		// MEMBER METHODS
		// ////////////////////////////////////////////////

		/**
		 * ok, our track has been dragged, calculate the new series of offsets
		 * 
		 * @param currentOffset
		 *          how far the current track has been dragged
		 * @return the set of data items to plot
		 */
		public TimeSeriesCollection getUpdatedSeries(TrackManager tracks)
		{		
			// ok, find the track wrappers
			if (_secondaryTrack == null)
				initialise(tracks, false);

			// did it work?
			if (_secondaryTrack == null)
				return null;

			if (_primaryDoublets == null)
				return null;
			
			// ok - the tracks have moved. better update the doublets
			updateDoublets();
			
			// create the collection of series
			final TimeSeriesCollection theTimeSeries = new TimeSeriesCollection();

			// produce a dataset for each track
			final BasicTimeSeries primarySeries = new BasicTimeSeries(_primaryTrack.getName(),
					FixedMillisecond.class);
			final BasicTimeSeries secondarySeries = new BasicTimeSeries(_secondaryTrack
					.getName(), FixedMillisecond.class);

			// ok, run through the points on the primary track
			Iterator<Doublet> iter = _primaryDoublets.iterator();
			while (iter.hasNext())
			{
				final Doublet thisD = (Doublet) iter.next();

				final Color thisColor = thisD.getColor();
				final double thisValue = thisD.calculateError(null, null);
				final HiResDate currentTime = thisD.getDTG();

				// create a new, correctly coloured data item
				// HI-RES NOT DONE - should provide FixedMicrosecond structure
				final ColouredDataItem newItem = new ColouredDataItem(new FixedMillisecond(
						currentTime.getDate().getTime()), thisValue, thisColor, false, null);

				try
				{
					// and add it to the series
					primarySeries.add(newItem);
				}
				catch (SeriesException e)
				{
					// hack: we shouldn't be allowing this exception. Look at why we're
					// getting the same
					// time period being entered twice for this track.

					// Stop catching the error, load Dave W's holistic approach plot file,
					// and check the track/fix which is causing the problem.

					// e.printStackTrace(); //To change body of catch statement use File |
					// Settings | File Templates.
				}

			}

			// ok, run through the points on the primary track
			iter = _secondaryDoublets.iterator();
			while (iter.hasNext())
			{
				final Doublet thisD = (Doublet) iter.next();

				final Color thisColor = thisD.getColor();
				final double thisValue = thisD.calculateError(null, null);
				final HiResDate currentTime = thisD.getDTG();

				// create a new, correctly coloured data item
				// HI-RES NOT DONE - should have FixedMicrosecond structure
				final ColouredDataItem newItem = new ColouredDataItem(new FixedMillisecond(
						currentTime.getDate().getTime()), thisValue, thisColor, false, null);

				try
				{
					// and add it to the series
					secondarySeries.add(newItem);
				}
				catch (SeriesException e)
				{
					CorePlugin.logError(Status.WARNING,
							"Multiple fixes at same DTG when producing stacked dots - prob ignored",
							null);
				}
			}

			// ok, add these new series
			theTimeSeries.addSeries(primarySeries);
			theTimeSeries.addSeries(secondarySeries);

			return theTimeSeries;
		}

		/**
		 * initialise the data, check we've got sensor data & the correct number of
		 * visible tracks
		 * 
		 * @param showError
		 *          TODO
		 */
		void initialise(TrackManager tracks, boolean showError)
		{

			// have we been created?
			if (_holder == null)
				return;

			// are we visible?
			if (_holder.isDisposed())
				return;

			_secondaryTrack = null;
			_primaryTrack = null;

			// do we have some data?
			if (tracks == null)
			{
				// output error message
				showError(Status.INFO, "Please open a Debrief plot",
						null);
				// showMessage("Sorry, a Debrief plot must be selected", showError);
				return;
			}

			// check we have a primary track
			WatchableList priTrk = tracks.getPrimaryTrack();
			if (priTrk == null)
			{
				showError(Status.INFO,
						"A primary track must be placed on the Tote",
						null);
				return;
			}
			else
			{
				if (!(priTrk instanceof TrackWrapper))
				{
					showError(Status.INFO,
							"The primary track must be a vehicle track",
							null);
					return;
				}
				else
					_primaryTrack = (TrackWrapper) priTrk;
			}

			// now the sec track
			WatchableList[] secs = tracks.getSecondaryTracks();

			// any?
			if ((secs == null) || (secs.length == 0))
			{
				showError(Status.INFO,
						"A secondary track must be present on the tote",
						null);
				return;
			}

			// too many?
			if (secs.length > 1)
			{
				showError(Status.INFO,
						"Only 1 secondary track may be on the tote",
						null);
				return;
			}

			// correct sort?
			WatchableList secTrk = secs[0];
			if (!(secTrk instanceof TrackWrapper))
			{
				showError(Status.INFO,
						"The secondary track must be a vehicle track",
						null);
				return;
			}
			else
			{
				_secondaryTrack = (TrackWrapper) secTrk;
			}

			if (_primaryTrack.getSensors() == null)
			{
				showError(Status.INFO,
						"There must be sensor data available",
						null);
				return;
			}

			// hey, we've got this far. show the right title
			_myChart.setTitle("Bearing Error");
			
			// ok, get the positions
			updateDoublets();

		}

		/** go through the tracks, finding the relevant position on the other track.
		 * 
		 */
		private void updateDoublets()
		{
			// ok - we're now there
			// so, do we have primary and secondary tracks?
			if (_primaryTrack != null && _secondaryTrack != null)
			{
				// cool sort out the list of sensor locations for these tracks
				_primaryDoublets = getDoublets(_primaryTrack, _secondaryTrack);
				_secondaryDoublets = getDoublets(_secondaryTrack, _primaryTrack);
			}
		}

		private Vector<Doublet> getDoublets(final TrackWrapper sensorHost,
				final TrackWrapper targetTrack)
		{
			final Vector<Doublet> res = new Vector<Doublet>(0, 1);

			// ok, cycle through the sensor points on the host track
			final Enumeration<Editable> iter = sensorHost.elements();
			while (iter.hasMoreElements())
			{
				final Plottable pw = (Plottable) iter.nextElement();

				if (pw.getVisible())
				{
					if (pw instanceof SensorWrapper)
					{
						final SensorWrapper sw = (SensorWrapper) pw;

						// right, work through the contacts in this sensor
						final Enumeration<Editable> theContacts = sw.elements();
						while (theContacts.hasMoreElements())
						{
							final SensorContactWrapper scw = (SensorContactWrapper) theContacts
									.nextElement();

							boolean thisVis = scw.getVisible();
							boolean onlyVis = _onlyVisible.isChecked();
							if (!onlyVis || (onlyVis && thisVis))
							{

								final Watchable[] matches = targetTrack.getNearestTo(scw.getDTG());
								FixWrapper targetFix = null;
								final int len = matches.length;
								if (len > 0)
								{
									for (int i = 0; i < len; i++)
									{
										final Watchable thisOne = matches[i];
										if (thisOne instanceof FixWrapper)
										{
											targetFix = (FixWrapper) thisOne;
											continue;
										}
									}
								}
								if (targetFix != null)
								{
									// ok. found match. store it
									final Doublet thisDub = new Doublet(scw, targetFix.getLocation());
									res.add(thisDub);
								}
							} // if this sensor contact is visible
						} // looping through these sensor contacts
					} // is this is a sensor wrapper
				} // if this item is visible
			} // looping through the items on this track

			return res;
		}

		/**
		 * clear our data, all is finished
		 */
		public void reset()
		{
			if (_primaryDoublets != null)
				_primaryDoublets.removeAllElements();
			_primaryDoublets = null;
			if (_secondaryDoublets != null)
				_secondaryDoublets.removeAllElements();
			_secondaryDoublets = null;
			_primaryTrack = null;
			_secondaryTrack = null;
		}

		// ////////////////////////////////////////////////
		// class to store combination of sensor & target at same time stamp
		// ////////////////////////////////////////////////
		public final class Doublet
		{
			private final SensorContactWrapper _sensor;

			private final WorldLocation _targetLocation;

			// ////////////////////////////////////////////////
			// working variables to help us along.
			// ////////////////////////////////////////////////
			private final WorldLocation _workingSensorLocation = new WorldLocation(0.0, 0.0,
					0.0);

			private final WorldLocation _workingTargetLocation = new WorldLocation(0.0, 0.0,
					0.0);

			// ////////////////////////////////////////////////
			// constructor
			// ////////////////////////////////////////////////
			Doublet(final SensorContactWrapper sensor, final WorldLocation targetLocation)
			{
				_sensor = sensor;
				_targetLocation = targetLocation;
			}

			// ////////////////////////////////////////////////
			// member methods
			// ////////////////////////////////////////////////
			/**
			 * get the DTG of this contact
			 * 
			 * @return the DTG
			 */
			public HiResDate getDTG()
			{
				return _sensor.getDTG();
			}

			/**
			 * get the colour of this sensor fix
			 */
			public Color getColor()
			{
				return _sensor.getColor();
			}

			/**
			 * ok find what the current bearing error is for this track
			 * 
			 * @param sensorOffset
			 *          if the sensor track has been dragged
			 * @param targetOffset
			 *          if the target track has been dragged
			 * @return
			 */
			public double calculateError(final WorldVector sensorOffset,
					final WorldVector targetOffset)
			{
				// copy our locations
				_workingSensorLocation.copy(_sensor.getCalculatedOrigin(null));
				_workingTargetLocation.copy(_targetLocation);

				// apply the offsets
				if (sensorOffset != null)
					_workingSensorLocation.addToMe(sensorOffset);
				if (targetOffset != null)
					_workingTargetLocation.addToMe(targetOffset);

				// calculate the current bearing
				final WorldVector error = _workingTargetLocation.subtract(_workingSensorLocation);
				double thisError = error.getBearing();
				thisError = Conversions.Rads2Degs(thisError);

				// and calculate the bearing error
				final double measuredBearing = _sensor.getBearing();
				thisError = measuredBearing - thisError;

				while (thisError > 180)
					thisError -= 360.0;

				while (thisError < -180)
					thisError += 360.0;

				return thisError;
			}
		}

	}

	public void showError(int info, String string, Throwable object)
	{
		// somehow, put the message into the UI
		_myChart.setTitle(string);
		
		// and store the problem into the log
		CorePlugin.logError(info, string, object);
	}

}