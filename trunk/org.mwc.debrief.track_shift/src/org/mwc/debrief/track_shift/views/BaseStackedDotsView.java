package org.mwc.debrief.track_shift.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.Range;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackShiftListener;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.actions.DragSegment;
import org.mwc.debrief.track_shift.Activator;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.DatedToolTipGenerator;
import MWC.GUI.Layers.DataListener;

/**
 */

abstract public class BaseStackedDotsView extends ViewPart implements
		ErrorLogger
{

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;

	/**
	 * the errors we're plotting
	 */
	XYPlot _dotPlot;

	/**
	 * and the actual values
	 * 
	 */
	XYPlot _linePlot;

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
	 * buttons for which plots to show
	 * 
	 */
	private Action _showLinePlot;
	private Action _showDotPlot;

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

	JFreeChart _myChart;

	private Vector<Action> _customActions;

	private Action _autoResize;

	private CombinedDomainXYPlot _combined;

	/**
	 * The constructor.
	 */
	public BaseStackedDotsView()
	{
		_myHelper = new StackedDotHelper();

		// create the actions - the 'centre-y axis' action may get called before the
		// interface is shown
		makeActions();
	}

	abstract protected String getUnits();

	abstract protected String getType();

	abstract protected void updateData();

	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager toolBarManager)
	{
		// fit to window
		toolBarManager.add(_autoResize);
		toolBarManager.add(_showLinePlot);
		toolBarManager.add(_showDotPlot);

		// and a separator
		toolBarManager.add(new Separator());

		Vector<Action> actions = DragSegment.getDragModes();
		for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext();)
		{
			Action action = iterator.next();
			toolBarManager.add(action);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{

		// right, we need an SWT.EMBEDDED object to act as a holder
		_holder = new Composite(parent, SWT.EMBEDDED);

		// now we need a Swing object to put our chart into
		final Frame plotControl = SWT_AWT.new_Frame(_holder);
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
	 * method to create a working plot (to contain our data)
	 * 
	 * @return the chart, in it's own panel
	 */
	@SuppressWarnings("deprecation")
	private void createStackedPlot(Frame plotControl)
	{

		// first create the x (time) axis
		SimpleDateFormat _df = new SimpleDateFormat("HHmm:ss");
		_df.setTimeZone(TimeZone.getTimeZone("GMT"));

		final DateAxis xAxis = new DateAxis("");
		xAxis.setDateFormatOverride(_df);

		// xAxis.setStandardTickUnits(DateAxisEditor
		// .createStandardDateTickUnitsAsTickUnits());

		// create the special stepper plot
		_dotPlot = new XYPlot();
		_dotPlot.setRangeAxis(new NumberAxis("Error (" + getUnits() + ")"));
		_dotPlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
		_dotPlot.setRenderer(new ColourStandardXYItemRenderer(
				new DatedToolTipGenerator(), null, _dotPlot));
		
		_linePlot = new XYPlot();
		NumberAxis absBrgAxis = new NumberAxis("Absolute (" + getUnits() + ")");
		_linePlot.setRangeAxis(absBrgAxis);
		absBrgAxis.setAutoRangeIncludesZero(false);
		_linePlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
		DefaultXYItemRenderer lineRend = new ColourStandardXYItemRenderer(
				new DatedToolTipGenerator(), null, _linePlot);
		lineRend.setSeriesShapesVisible(1, false);
		lineRend.setPaint(Color.DARK_GRAY);
		_linePlot.setRenderer(lineRend);

		// give them a fancy backdrop
		 GradientPaint gradientPaint = new GradientPaint(0.0F, 10.0F, Color.LIGHT_GRAY, 10.0F, 0.0F, Color.LIGHT_GRAY.brighter(), true);
		 _dotPlot.setBackgroundPaint(gradientPaint);
		 _linePlot.setBackgroundPaint(gradientPaint);
		
		// set the y axes to autocalculate
		_dotPlot.getRangeAxis().setAutoRange(true);
		_linePlot.getRangeAxis().setAutoRange(true);

		_combined = new CombinedDomainXYPlot(xAxis);
		_combined.add(_linePlot);
		_combined.add(_dotPlot);
		_combined.setOrientation(PlotOrientation.HORIZONTAL);

		// put the plot into a chart
		_myChart = new JFreeChart(getType() + " error", null, _combined, true);

		LegendItemSource[] sources =
		{ _linePlot };
		_myChart.getLegend().setSources(sources);

		final ChartPanel plotHolder = new ChartPanel(_myChart);
		plotHolder.setMouseZoomable(true, true);
		plotHolder.setDisplayToolTips(true);

		// and insert into the panel
		plotControl.add(plotHolder, BorderLayout.CENTER);
	}

	/**
	 * view is closing, shut down, preserve life
	 */
	@Override
	public void dispose()
	{
		System.out.println("disposing of stacked dots");
		// get parent to ditch itself
		super.dispose();

		// ditch the actions
		if(_customActions != null)
			_customActions.removeAllElements();

		// are we listening to any layers?
		if (_ourLayersSubject != null)
			_ourLayersSubject.removeDataReformattedListener(_layersListener);

		if (_theTrackDataListener != null)
		{
			_theTrackDataListener.removeTrackShiftListener(_myShiftListener);
			_theTrackDataListener.removeTrackShiftListener(_myShiftListener);
		}

	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(_centreYAxis);
		manager.add(_onlyVisible);
		// and the help link
		manager.add(new Separator());
		manager.add(CorePlugin.createOpenHelpAction(
				"org.mwc.debrief.help.TrackShifting", null, this));

	}

	private void makeActions()
	{

		_autoResize = new Action("Auto resize", IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				super.run();
				boolean val = _autoResize.isChecked();
				// ok - redraw the plot we may have changed the axis centreing
				_linePlot.getRangeAxis().setAutoRange(val);
				_linePlot.getDomainAxis().setAutoRange(val);
				_dotPlot.getRangeAxis().setAutoRange(val);
				_dotPlot.getDomainAxis().setAutoRange(val);
			}
		};
		_autoResize.setChecked(true);
		_autoResize.setToolTipText("Keep plot sized to show all data");
		_autoResize.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/fit_to_size.png"));

		_centreYAxis = new Action("Center Y axis on origin", IAction.AS_CHECK_BOX)
		{
			@Override
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

		_showLinePlot = new Action("Actuals plot", IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				super.run();
				if (_showLinePlot.isChecked())
				{
					_combined.remove(_linePlot);
					_combined.remove(_dotPlot);

					_combined.add(_linePlot);
					if (_showDotPlot.isChecked())
						_combined.add(_dotPlot);
				}
				else
				{
					if (_combined.getSubplots().size() > 1)
						_combined.remove(_linePlot);
				}
			}
		};
		_showLinePlot.setChecked(true);
		_showLinePlot.setToolTipText("Show the actuals plot");
		_showLinePlot.setImageDescriptor(Activator
				.getImageDescriptor("icons/stacked_lines.png"));

		_showDotPlot = new Action("Error plot", IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				super.run();
				if (_showDotPlot.isChecked())
				{
					_combined.remove(_linePlot);
					_combined.remove(_dotPlot);

					if (_showLinePlot.isChecked())
						_combined.add(_linePlot);
					_combined.add(_dotPlot);
				}
				else
				{
					if (_combined.getSubplots().size() > 1)
						_combined.remove(_dotPlot);
				}
			}
		};
		_showDotPlot.setChecked(true);
		_showDotPlot.setToolTipText("Show the error plot");
		_showDotPlot.setImageDescriptor(Activator
				.getImageDescriptor("icons/stacked_dots.png"));

		// get an error logger
		final ErrorLogger logger = this;

		_onlyVisible = new Action("Only draw dots for visible data points",
				IAction.AS_CHECK_BOX)
		{

			@Override
			public void run()
			{
				super.run();

				// set the title, so there's something useful in there
				_myChart.setTitle(getType() + " Error");

				// we need to get a fresh set of data pairs - the number may have
				// changed
				_myHelper.initialise(_theTrackDataListener, true, _onlyVisible
						.isChecked(), _holder, logger, getType());

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

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
	}

	public void logError(int info, String string, Exception object)
	{
		// somehow, put the message into the UI
		_myChart.setTitle(string);

		// and store the problem into the log
		CorePlugin.logError(info, string, object);
	}

	/**
	 * the track has been moved, update the dots
	 */
	void updateStackedDots()
	{

		if (_autoResize.isChecked())
		{
			if (_showDotPlot.isChecked())
			{
				_dotPlot.getRangeAxis().setAutoRange(true);
				_dotPlot.getDomainAxis().setAutoRange(true);
			}
			if (_showLinePlot.isChecked())
			{
				_linePlot.getRangeAxis().setAutoRange(true);
				_linePlot.getDomainAxis().setAutoRange(true);
			}
		}

		// update the current datasets
		updateData();

		// we will only centre the y-axis if the user hasn't performed a zoom
		// operation
		if (_centreYAxis.isChecked())
		{
			if (_showDotPlot.isChecked())
			{
				// do a quick fudge to make sure zero is in the centre
				final Range rng = _dotPlot.getRangeAxis().getRange();
				final double maxVal = Math.max(Math.abs(rng.getLowerBound()), Math
						.abs(rng.getUpperBound()));
				_dotPlot.getRangeAxis().setRange(-maxVal, maxVal);
			}
		}
	}

	/**
	 * sort out what we're listening to...
	 */
	private void watchMyParts()
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		final ErrorLogger logger = this;

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// is it a new one?
						if (part != _theTrackDataListener)
						{
							// cool, remember about it.
							_theTrackDataListener = (TrackManager) part;

							// set the title, so there's something useful in there
							_myChart.setTitle(getType() + " Error");

							// ok - fire off the event for the new tracks
							_myHelper.initialise(_theTrackDataListener, false, _onlyVisible
									.isChecked(), _holder, logger, getType());

							// just in case we're ready to start plotting, go for it!
							updateStackedDots();
						}

					}
				});
		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// ok, ditch it.
						_theTrackDataListener = null;

						_myHelper.reset();
					}
				});
		_myPartMonitor.addPartListener(TrackDataProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// cool, remember about it.
						final TrackDataProvider dataP = (TrackDataProvider) part;

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
							// ok - let's start off with a clean plot
							_dotPlot.setDataset(null);

							// nope, better stop listening then
							if (_myTrackDataProvider != null)
								_myTrackDataProvider.removeTrackShiftListener(_myShiftListener);

							// ok, start listening to it anyway
							_myTrackDataProvider = dataP;
							_myTrackDataProvider.addTrackShiftListener(_myShiftListener);

							// hey - fire a dot update
							updateStackedDots();
						}
					}
				});

		_myPartMonitor.addPartListener(TrackDataProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						final TrackDataProvider tdp = (TrackDataProvider) part;
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						final Layers theLayers = (Layers) part;

						// do we need to create our listener
						if (_layersListener == null)
						{
							_layersListener = new Layers.DataListener()
							{
								public void dataExtended(Layers theData)
								{
								}

								public void dataModified(Layers theData, Layer changedLayer)
								{
								}

								public void dataReformatted(Layers theData, Layer changedLayer)
								{
									_myHelper.initialise(_theTrackDataListener, false,
											_onlyVisible.isChecked(), _holder, logger, getType());
									updateStackedDots();
								}
							};
						}

						// is this what we're listening to?
						if (_ourLayersSubject != theLayers)
						{
							// nope, stop listening to the old one (if there is one!)
							if (_ourLayersSubject != null)
								_ourLayersSubject
										.removeDataReformattedListener(_layersListener);
						}

						// now start listening to the new one.
						theLayers.addDataReformattedListener(_layersListener);
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						final Layers theLayers = (Layers) part;

						// is this what we're listening to?
						if (_ourLayersSubject == theLayers)
						{
							// yup, stop listening
							_ourLayersSubject.removeDataReformattedListener(_layersListener);
						}
					}

				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}

}