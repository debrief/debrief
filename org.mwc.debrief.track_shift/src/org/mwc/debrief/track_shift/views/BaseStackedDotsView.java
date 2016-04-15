/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.track_shift.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.TextAnchor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackShiftListener;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.actions.DragSegment;
import org.mwc.debrief.core.editors.PlotOutlinePage;
import org.mwc.debrief.track_shift.Activator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WatchableList;

/**
 */

abstract public class BaseStackedDotsView extends ViewPart implements
		ErrorLogger
{

	private static final String SHOW_DOT_PLOT = "SHOW_DOT_PLOT";

	private static final String SHOW_LINE_PLOT = "SHOW_LINE_PLOT";
  private static final String SELECT_ON_CLICK = "SELECT_ON_CLICK";
  private static final String SHOW_ONLY_VIS = "ONLY_SHOW_VIS";
	
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
	final StackedDotHelper _myHelper;

	/**
	 * our track-data provider
	 */
	protected TrackManager _theTrackDataListener;

	/**
	 * our listener for tracks being shifted...
	 */
	protected TrackShiftListener _myShiftListener;

	/**
	 * buttons for which plots to show
	 * 
	 */
	protected Action _showLinePlot;
	Action _showDotPlot;

	/**
	 * flag indicating whether we should only show stacked dots for visible fixes
	 */
	Action _onlyVisible;

  /**
   * flag indicating whether we should select the clicked item
   * in the Outline View
   */
  Action _selectOnClick;

	/**
	 * our layers listener...
	 */
	protected DataListener _layersListener;

	/**
	 * the set of layers we're currently listening to
	 */
	protected Layers _ourLayersSubject;

	protected TrackDataProvider _myTrackDataProvider;

	ChartComposite _holder;

	JFreeChart _myChart;

	private Vector<Action> _customActions;

	protected Action _autoResize;

	private CombinedDomainXYPlot _combined;

	protected TrackDataListener _myTrackDataListener;

	/**
	 * does our output need bearing in the data?
	 * 
	 */
	private final boolean _needBrg;

	/**
	 * does our output need frequency in the data?
	 * 
	 */
	private final boolean _needFreq;

	// private Action _magicBtn;

	protected Vector<ISelectionProvider> _selProviders;

	protected ISelectionChangedListener _mySelListener;

	protected Vector<DraggableItem> _draggableSelection;

  protected boolean _itemSelectedPending = false;

	/**
	 * 
	 * @param needBrg
	 *          if the algorithm needs bearing data
	 * @param needFreq
	 *          if the agorithm needs frequency data
	 */
	protected BaseStackedDotsView(final boolean needBrg, final boolean needFreq)
	{
		_myHelper = new StackedDotHelper();

		_needBrg = needBrg;
		_needFreq = needFreq;

		// create the actions - the 'centre-y axis' action may get called before
		// the
		// interface is shown
		makeActions();
	}

	abstract protected String getUnits();

	abstract protected String getType();

	abstract protected void updateData(boolean updateDoublets);

	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalToolBar(final IToolBarManager toolBarManager)
	{
		// fit to window
		toolBarManager.add(_autoResize);
		toolBarManager.add(_onlyVisible);
		toolBarManager.add(_selectOnClick);
		toolBarManager.add(_showLinePlot);
		toolBarManager.add(_showDotPlot);
		// toolBarManager.add(_magicBtn);

		// and a separator
		toolBarManager.add(new Separator());

		final Vector<Action> actions = DragSegment.getDragModes();
		for (final Iterator<Action> iterator = actions.iterator(); iterator
				.hasNext();)
		{
			final Action action = iterator.next();
			toolBarManager.add(action);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent)
	{

		_holder = new ChartComposite(parent, SWT.NONE, null, 400, 600, 300, 200,
				1800, 1800, true, true, true, true, true, true)
		{
			@Override
			public void mouseUp(MouseEvent event)
			{
				super.mouseUp(event);
				JFreeChart c = getChart();
				if (c != null)
				{
					c.setNotify(true); // force redraw
				}
			}
		};

		// hey - now create the stacked plot!
		createStackedPlot();

		// /////////////////////////////////////////
		// ok - listen out for changes in the view
		// /////////////////////////////////////////
		_selProviders = new Vector<ISelectionProvider>();
		_mySelListener = new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(final SelectionChangedEvent event)
			{
				final ISelection sel = event.getSelection();
				final Vector<DraggableItem> dragees = new Vector<DraggableItem>();
				if (sel instanceof StructuredSelection)
				{
					final StructuredSelection str = (StructuredSelection) sel;
					final Iterator<?> iter = str.iterator();
					while (iter.hasNext())
					{
						final Object object = (Object) iter.next();
						if (object instanceof EditableWrapper)
						{
							final EditableWrapper ew = (EditableWrapper) object;
							final Editable item = ew.getEditable();
							if (item instanceof DraggableItem)
							{

								dragees.add((DraggableItem) item);
							}
						}
						else
						{
							return;
						}
					}

					// ok, we've just got draggable items - override the current
					// item
					_draggableSelection = dragees;
				}
			}
		};

		// sort out the part monitor
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		// now start listening out for people's parts
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
	protected void createStackedPlot()
	{

		// first create the x (time) axis
		final SimpleDateFormat _df = new SimpleDateFormat("HHmm:ss");
		_df.setTimeZone(TimeZone.getTimeZone("GMT"));

		final DateAxis xAxis = new CachedTickDateAxis("");
		xAxis.setDateFormatOverride(_df);
		Font tickLabelFont = new Font("Courier", Font.PLAIN, 13);
		xAxis.setTickLabelFont(tickLabelFont);
		xAxis.setTickLabelPaint(Color.BLACK);

		xAxis.setStandardTickUnits(DateAxisEditor
				.createStandardDateTickUnitsAsTickUnits());
		xAxis.setAutoTickUnitSelection(true);

		// create the special stepper plot
		_dotPlot = new XYPlot();
		NumberAxis errorAxis = new NumberAxis("Error (" + getUnits() + ")");
		Font axisLabelFont = new Font("Courier", Font.PLAIN, 16);
		errorAxis.setLabelFont(axisLabelFont);
		errorAxis.setTickLabelFont(tickLabelFont);
		_dotPlot.setRangeAxis(errorAxis);
		_dotPlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
		_dotPlot
				.setRenderer(new ColourStandardXYItemRenderer(null, null, _dotPlot));

		_dotPlot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		_dotPlot.setRangeGridlineStroke(new BasicStroke(2));
		_dotPlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		_dotPlot.setDomainGridlineStroke(new BasicStroke(2));

		// now try to do add a zero marker on the error bar
		final Paint thePaint = Color.DARK_GRAY;
		final Stroke theStroke = new BasicStroke(3);
		final ValueMarker zeroMarker = new ValueMarker(0.0, thePaint, theStroke);
		_dotPlot.addRangeMarker(zeroMarker);

		_linePlot = new XYPlot();
		final NumberAxis absBrgAxis = new NumberAxis("Absolute (" + getUnits()
				+ ")");
		absBrgAxis.setLabelFont(axisLabelFont);
		absBrgAxis.setTickLabelFont(tickLabelFont);
		_linePlot.setRangeAxis(absBrgAxis);
		absBrgAxis.setAutoRangeIncludesZero(false);
		_linePlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
		final DefaultXYItemRenderer lineRend = new ColourStandardXYItemRenderer(
				null, null, _linePlot);
		lineRend.setPaint(Color.DARK_GRAY);
		_linePlot.setRenderer(lineRend);

		_linePlot.setDomainCrosshairVisible(true);
		_linePlot.setRangeCrosshairVisible(true);
		_linePlot.setDomainCrosshairPaint(Color.GRAY);
		_linePlot.setRangeCrosshairPaint(Color.GRAY);
		_linePlot.setDomainCrosshairStroke(new BasicStroke(3.0f));
		_linePlot.setRangeCrosshairStroke(new BasicStroke(3.0f));

		_linePlot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		_linePlot.setRangeGridlineStroke(new BasicStroke(2));
		_linePlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		_linePlot.setDomainGridlineStroke(new BasicStroke(2));
		
		// and the plot object to display the cross hair value
		final XYTextAnnotation annot = new XYTextAnnotation("-----", 2, 2);
		annot.setTextAnchor(TextAnchor.TOP_LEFT);

		Font annotationFont = new Font("Courier", Font.BOLD, 16);
		annot.setFont(annotationFont);
		annot.setPaint(Color.DARK_GRAY);
		annot.setBackgroundPaint(Color.white);
		_linePlot.addAnnotation(annot);

		// give them a high contrast backdrop
		_dotPlot.setBackgroundPaint(Color.white);
		_linePlot.setBackgroundPaint(Color.white);

		// set the y axes to autocalculate
		_dotPlot.getRangeAxis().setAutoRange(true);
		_linePlot.getRangeAxis().setAutoRange(true);

		_combined = new CombinedDomainXYPlot(xAxis);

		_combined.add(_linePlot);
		_combined.add(_dotPlot);

		_combined.setOrientation(PlotOrientation.HORIZONTAL);
		
		// put the plot into a chart
		_myChart = new JFreeChart(null, null, _combined, true);
		
		final LegendItemSource[] sources = { _linePlot };
		_myChart.getLegend().setSources(sources);

		_myChart.addProgressListener(new ChartProgressListener()
		{
			public void chartProgress(final ChartProgressEvent cpe)
			{
				if (cpe.getType() != ChartProgressEvent.DRAWING_FINISHED)
					return;

				// is hte line plot visible?
				if (!_showLinePlot.isChecked())
					return;

				// double-check our label is still in the right place
				final double xVal = _linePlot.getRangeAxis().getLowerBound();
				final double yVal = _linePlot.getDomainAxis().getUpperBound();
				boolean annotChanged = false;
				if (annot.getX() != yVal)
				{
					annot.setX(yVal);
					annotChanged = true;
				}
				if (annot.getY() != xVal)
				{
					annot.setY(xVal);
					annotChanged = true;
				}
				// and write the text
				final String numA = MWC.Utilities.TextFormatting.GeneralFormat
						.formatOneDecimalPlace(_linePlot.getRangeCrosshairValue());
				final Date newDate = new Date((long) _linePlot
						.getDomainCrosshairValue());
				final SimpleDateFormat _df = new SimpleDateFormat("HHmm:ss");
				_df.setTimeZone(TimeZone.getTimeZone("GMT"));
				final String dateVal = _df.format(newDate);
				final String theMessage = " [" + dateVal + "," + numA + "]";
				if (!theMessage.equals(annot.getText()))
				{
					annot.setText(theMessage);
					annotChanged = true;
				}
				if (annotChanged)
				{
					_linePlot.removeAnnotation(annot);
					_linePlot.addAnnotation(annot);
				}
				
				// ok, do we also have a selection event pending
				if(_itemSelectedPending && _selectOnClick.isChecked())
				{
          _itemSelectedPending = false;
          
				  showFixAtThisTime(newDate);
				}
			}
		});

		// and insert into the panel
		_holder.setChart(_myChart);
		
		_holder.addChartMouseListener(new ChartMouseListener()
    {
      @Override
      public void chartMouseMoved(ChartMouseEvent arg0)
      {
      }
      
      @Override
      public void chartMouseClicked(ChartMouseEvent arg0)
      {
        // ok, remember it was clicked
        _itemSelectedPending = true;
      }
    });

		// do a little tidying to reflect the memento settings
		if (!_showLinePlot.isChecked())
			_combined.remove(_linePlot);
		if (!_showDotPlot.isChecked() && _showLinePlot.isChecked())
			_combined.remove(_dotPlot);
	}

	/**
	 * view is closing, shut down, preserve life
	 */
	@Override
	public void dispose()
	{
		// get parent to ditch itself
		super.dispose();

		// ditch the actions
		if (_customActions != null)
			_customActions.removeAllElements();

		// are we listening to any layers?
		if (_ourLayersSubject != null)
			_ourLayersSubject.removeDataReformattedListener(_layersListener);

		if (_theTrackDataListener != null)
		{
			_theTrackDataListener.removeTrackShiftListener(_myShiftListener);
			_theTrackDataListener.removeTrackDataListener(_myTrackDataListener);
		}

		// stop the part monitor
		_myPartMonitor.ditch();

	}

	protected void fillLocalPullDown(final IMenuManager manager)
	{
		manager.add(_onlyVisible);
    manager.add(_selectOnClick);
		// and the help link
		manager.add(new Separator());
		manager.add(CorePlugin.createOpenHelpAction(
				"org.mwc.debrief.help.TrackShifting", null, this));

	}

	protected void makeActions()
	{

		_autoResize = new Action("Auto resize", IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				super.run();
				final boolean val = _autoResize.isChecked();
				if (_showLinePlot.isChecked())
				{
					// ok - redraw the plot we may have changed the axis
					// centreing
					_linePlot.getRangeAxis().setAutoRange(val);
					_linePlot.getDomainAxis().setAutoRange(val);
				}
				if (_showDotPlot.isChecked())
				{
					_dotPlot.getRangeAxis().setAutoRange(val);
					_dotPlot.getDomainAxis().setAutoRange(val);
				}
			}
		};
		_autoResize.setChecked(true);
		_autoResize.setToolTipText("Keep plot sized to show all data");
		_autoResize.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/24/fit_to_win.png"));

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
				.getImageDescriptor("icons/24/stacked_lines.png"));

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
				.getImageDescriptor("icons/24/stacked_dots.png"));

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
				_myChart.setTitle("");

				// we need to get a fresh set of data pairs - the number may
				// have
				// changed
				_myHelper.initialise(_theTrackDataListener, true,
						_onlyVisible.isChecked(), _holder, logger, getType(), _needBrg,
						_needFreq);

				// and a new plot please
				updateStackedDots(true);
			}
		};
		_onlyVisible.setText("Only plot visible data");
		_onlyVisible.setChecked(true);
		_onlyVisible.setToolTipText("Only draw dots for visible data points");
		_onlyVisible.setImageDescriptor(Activator
				.getImageDescriptor("icons/24/reveal.png"));
		

    _selectOnClick = new Action("Select TMA Fix in outline when clicked",
        IAction.AS_CHECK_BOX)
    {
    };
    _selectOnClick.setChecked(true);
    _selectOnClick.setToolTipText("Reveal the relevant TMA Fix when an error clicked on plot");
    _selectOnClick.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/24/outline.png"));
		
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
	}

	public void logError(final int statusCode, final String string,
			final Exception object)
	{
		// somehow, put the message into the UI
		_myChart.setTitle(string);

		// is it a fail status
		if (statusCode != Status.OK)
		{
			// and store the problem into the log
			CorePlugin.logError(statusCode, string, object);

			// also ditch the data in the plots - to blank them out
			clearPlots();
		}
	}

	 

  @Override
  public void logStack(int status, String text)
  {
    CorePlugin.logError(status, text, null, true);
  }

	
	/**
	 * the track has been moved, update the dots
	 */
	void clearPlots()
	{
		if (Thread.currentThread() == Display.getDefault().getThread())
		{
			// it's ok we're already in a display thread
			_dotPlot.setDataset(null);
			_linePlot.setDataset(null);
		}
		else
		{
			// we're not in the display thread - make it so!
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					_dotPlot.setDataset(null);
					_linePlot.setDataset(null);
				}
			});
		}
	}

	/**
	 * the track has been moved, update the dots
	 */
	void updateStackedDots(final boolean updateDoublets)
	{
		if (Thread.currentThread() == Display.getDefault().getThread())
		{
			// it's ok we're already in a display thread
			wrappedUpdateStackedDots(updateDoublets);
		}
		else
		{
			// we're not in the display thread - make it so!
			Display.getDefault().syncExec(new Runnable()
			{
				public void run()
				{
					// update the current datasets
					wrappedUpdateStackedDots(updateDoublets);
				}
			});
		}
	}

	/**
	 * the track has been moved, update the dots
	 */
	void wrappedUpdateStackedDots(final boolean updateDoublets)
	{

		// update the current datasets
		updateData(updateDoublets);

		// right, are we updating the range data?
		if (_autoResize.isChecked())
		{
			if (_showDotPlot.isChecked())
			{
				_dotPlot.getRangeAxis().setAutoRange(false);
				_dotPlot.getRangeAxis().setAutoRange(true);
			}
			if (_showLinePlot.isChecked())
			{
				_linePlot.getRangeAxis().setAutoRange(false);
				_linePlot.getRangeAxis().setAutoRange(true);
			}
		}

		// note, we also update the domain axis if we're updating the data in
		// question
		if (updateDoublets)
		{
			// trigger recalculation of date axis ticks
			final CachedTickDateAxis date = (CachedTickDateAxis) _combined
					.getDomainAxis();
			date.clearTicks();

			if (_showDotPlot.isChecked())
			{
				_dotPlot.getDomainAxis().setAutoRange(false);
				_dotPlot.getDomainAxis().setAutoRange(true);
				_dotPlot.getDomainAxis().setAutoRange(false);
			}
			if (_showLinePlot.isChecked())
			{
				_linePlot.getDomainAxis().setAutoRange(false);
				_linePlot.getDomainAxis().setAutoRange(true);
				_linePlot.getDomainAxis().setAutoRange(false);
			}
		}
	}

	/**
	 * sort out what we're listening to...
	 */
	private final void watchMyParts()
	{

		final ErrorLogger logger = this;

		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final ISelectionProvider prov = (ISelectionProvider) part;

						// am I already listning to this
						if (_selProviders.contains(prov))
						{
							// ignore, we're already listening to it
						}
						else
						{
							prov.addSelectionChangedListener(_mySelListener);
							_selProviders.add(prov);
						}
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.CLOSED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final ISelectionProvider prov = (ISelectionProvider) part;

						// am I already listning to this
						if (_selProviders.contains(prov))
						{
							// ok, ditch this listener
							_selProviders.remove(prov);

							// and stop listening
							prov.removeSelectionChangedListener(_mySelListener);
						}
						else
						{
							// hey, we're not even listening to it.
						}
					}
				});

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// is it a new one?
						if (part != _theTrackDataListener)
						{
							// cool, remember about it.
							_theTrackDataListener = (TrackManager) part;

							// set the title, so there's something useful in
							// there
							_myChart.setTitle("");

							// ok - fire off the event for the new tracks
							_myHelper.initialise(_theTrackDataListener, false,
									_onlyVisible.isChecked(), _holder, logger, getType(),
									_needBrg, _needFreq);

							// just in case we're ready to start plotting, go
							// for it!
							updateStackedDots(true);
						}

					}
				});
		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// ok, ditch it.
						_theTrackDataListener = null;

						_myHelper.reset();
					}
				});
		_myPartMonitor.addPartListener(TrackDataProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// cool, remember about it.
						final TrackDataProvider dataP = (TrackDataProvider) part;

						// do we need to generate the shift listener?
						if (_myShiftListener == null)
						{
							_myShiftListener = new TrackShiftListener()
							{
								public void trackShifted(final WatchableList subject)
								{
									// the tracks have moved, we haven't changed
									// the tracks or
									// anything like that...
									updateStackedDots(false);
								}
							};

							_myTrackDataListener = new TrackDataListener()
							{

								public void tracksUpdated(final WatchableList primary,
										final WatchableList[] secondaries)
								{
									_myHelper.initialise(_theTrackDataListener, false,
											_onlyVisible.isChecked(), _holder, logger, getType(),
											_needBrg, _needFreq);

									// ahh, the tracks have changed, better
									// update the doublets

									// ok, do the recalc
									updateStackedDots(true);

									// ok - if we're on auto update, do the
									// update
									updateLinePlotRanges();

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
							{
								_myTrackDataProvider.removeTrackShiftListener(_myShiftListener);
								_myTrackDataProvider
										.removeTrackDataListener(_myTrackDataListener);
							}

							// ok, start listening to it anyway
							_myTrackDataProvider = dataP;
							_myTrackDataProvider.addTrackShiftListener(_myShiftListener);
							_myTrackDataProvider.addTrackDataListener(_myTrackDataListener);

							// hey - fire a dot update
							updateStackedDots(true);
						}
					}
				});

		_myPartMonitor.addPartListener(TrackDataProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final TrackDataProvider tdp = (TrackDataProvider) part;
						tdp.removeTrackShiftListener(_myShiftListener);
						tdp.removeTrackDataListener(_myTrackDataListener);

						if (tdp == _myTrackDataProvider)
						{
							_myTrackDataProvider = null;
						}

						// hey - lets clear our plot
						updateStackedDots(true);
					}
				});

		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final Layers theLayers = (Layers) part;

						// do we need to create our listener
						if (_layersListener == null)
						{
							_layersListener = new Layers.DataListener()
							{
								public void dataExtended(final Layers theData)
								{
								}

								public void dataModified(final Layers theData,
										final Layer changedLayer)
								{
								}

								public void dataReformatted(final Layers theData,
										final Layer changedLayer)
								{
									_myHelper.initialise(_theTrackDataListener, false,
											_onlyVisible.isChecked(), _holder, logger, getType(),
											_needBrg, _needFreq);

									updateStackedDots(true);

								}
							};
						}

						// is this what we're listening to?
						if (_ourLayersSubject != theLayers)
						{
							// nope, stop listening to the old one (if there is
							// one!)
							if (_ourLayersSubject != null)
								_ourLayersSubject
										.removeDataReformattedListener(_layersListener);

							// and remember the new one
							_ourLayersSubject = theLayers;
						}

						// now start listening to the new one.
						theLayers.addDataReformattedListener(_layersListener);
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final Layers theLayers = (Layers) part;

						// is this what we're listening to?
						if (_ourLayersSubject == theLayers)
						{
							// yup, stop listening
							_ourLayersSubject.removeDataReformattedListener(_layersListener);

							_linePlot.setDataset(null);
							_dotPlot.setDataset(null);
						}
					}

				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}

	/**
	 * some data has changed. if we're auto ranging, update the axes
	 * 
	 */
	protected void updateLinePlotRanges()
	{
		// have a look at the auto resize
		if (_autoResize.isChecked())
		{
			if (_showLinePlot.isChecked())
			{
				_linePlot.getRangeAxis().setAutoRange(false);
				_linePlot.getDomainAxis().setAutoRange(false);
				_linePlot.getRangeAxis().setAutoRange(true);
				_linePlot.getDomainAxis().setAutoRange(true);
			}
		}
	}

	@Override
	public void init(final IViewSite site, final IMemento memento)
			throws PartInitException
	{
		super.init(site, memento);

		if (memento != null)
		{

			final Boolean showLineVal = memento.getBoolean(SHOW_LINE_PLOT);
			final Boolean showDotVal = memento.getBoolean(SHOW_DOT_PLOT);
			final Boolean doSelectOnClick = memento.getBoolean(SELECT_ON_CLICK);
      final Boolean showOnlyVis = memento.getBoolean(SHOW_ONLY_VIS);
			if (showLineVal != null)
			{
				_showLinePlot.setChecked(showLineVal);
			}
			if (showDotVal != null)
			{
				_showDotPlot.setChecked(showDotVal);
			}
      if (doSelectOnClick != null)
      {
        _selectOnClick.setChecked(doSelectOnClick);
      }
      if (showOnlyVis != null)
      {
        _onlyVisible.setChecked(showOnlyVis);
      }
		}
	}

	@Override
	public void saveState(final IMemento memento)
	{
		super.saveState(memento);

		// remember if we're showing the error plot
		memento.putBoolean(SHOW_LINE_PLOT, _showLinePlot.isChecked());
		memento.putBoolean(SHOW_DOT_PLOT, _showDotPlot.isChecked());
    memento.putBoolean(SELECT_ON_CLICK, _selectOnClick.isChecked());
    memento.putBoolean(SHOW_ONLY_VIS, _onlyVisible.isChecked());

	}

  private void showFixAtThisTime(final Date newDate)
  {
    if(_myTrackDataProvider != null)
    {
      if(_myTrackDataProvider.getSecondaryTracks().length != 1)
        return;
      
      HiResDate theDate = new HiResDate(newDate);
      
      EditableWrapper subject = null;
      
      // ok, get the editor
      final IWorkbench wb = PlatformUI.getWorkbench();
      final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
      final IWorkbenchPage page = win.getActivePage();
      final IEditorPart editor = page.getActiveEditor();

      Layers layers = (Layers) editor.getAdapter(Layers.class);

      // did we find the layers
      if(layers == null)
        return;
      
      TrackWrapper secTrack =
          (TrackWrapper) _myTrackDataProvider.getSecondaryTracks()[0];				    
      SegmentList segs = secTrack.getSegments();
      Enumeration<Editable> sIter = segs.elements();
      while (sIter.hasMoreElements())
      {
        TrackSegment thisSeg = (TrackSegment) sIter.nextElement();
        if(thisSeg.startDTG().lessThanOrEqualTo(theDate) && 
            thisSeg.endDTG().greaterThanOrEqualTo(theDate))
        {
          // ok, loop through them
          Enumeration<Editable> pts = thisSeg.elements();
          while (pts.hasMoreElements())
          {
            FixWrapper fix = (FixWrapper) pts.nextElement();
            if(fix.getDTG().equals(theDate))
            {
              // done.
              EditableWrapper parentP = new EditableWrapper(secTrack, null, layers);
              subject = new EditableWrapper(fix, parentP, null);
              break;
            }
          }
        }
      }
      
      if(subject != null)
      {
        IStructuredSelection selection = new StructuredSelection(subject);
        
        IContentOutlinePage outline =
            (IContentOutlinePage) editor.getAdapter(IContentOutlinePage.class);
        // did we find an outline?
        if (outline != null)
        {
          // now set the selection
          outline.setSelection(selection);

          // see uf we can expand the selection
          if (outline instanceof PlotOutlinePage)
          {
            PlotOutlinePage plotOutline = (PlotOutlinePage) outline;
            plotOutline.editableSelected(selection, subject);
          }
        }
      }
    }
  }
}