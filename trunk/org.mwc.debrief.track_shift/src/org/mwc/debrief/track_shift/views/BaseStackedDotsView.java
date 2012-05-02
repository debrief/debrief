package org.mwc.debrief.track_shift.views;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeSet;
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
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartPanel;
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
import org.jfree.data.Range;
import org.jfree.ui.TextAnchor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackShiftListener;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.actions.DragFeature.DragFeatureAction;
import org.mwc.debrief.core.actions.DragFeature.DragOperation;
import org.mwc.debrief.core.actions.DragSegment;
import org.mwc.debrief.track_shift.Activator;
import org.mwc.debrief.track_shift.magic.OptimiseTest;
import org.mwc.debrief.track_shift.magic.OptimiseTest.TryOffsetFunction;

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.Doublet;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldVector;
import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

/**
 */

abstract public class BaseStackedDotsView extends ViewPart implements
		ErrorLogger
{

	private static final String SHOW_DOT_PLOT = "SHOW_DOT_PLOT";

	private static final String SHOW_LINE_PLOT = "SHOW_LINE_PLOT";

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
	 * flag indicating whether we should override the y-axis to ensure that zero
	 * is always in the centre
	 */
	private Action _centreYAxis;

	/**
	 * buttons for which plots to show
	 * 
	 */
	protected Action _showLinePlot;
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

	private Action _magicBtn;

	protected Vector<ISelectionProvider> _selProviders;

	protected ISelectionChangedListener _mySelListener;

	protected Vector<DraggableItem> _draggableSelection;

	/**
	 * 
	 * @param needBrg
	 *          if the algorithm needs bearing data
	 * @param needFreq
	 *          if the agorithm needs frequency data
	 */
	protected BaseStackedDotsView(boolean needBrg, boolean needFreq)
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

	/**
	 * do some wonder magic on the plot
	 * 
	 */
	abstract protected void optimise();

	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void fillLocalToolBar(IToolBarManager toolBarManager)
	{
		// fit to window
		toolBarManager.add(_onlyVisible);
		toolBarManager.add(_autoResize);
		toolBarManager.add(_showLinePlot);
		toolBarManager.add(_showDotPlot);
		toolBarManager.add(_magicBtn);

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
		_selProviders = new Vector<ISelectionProvider>();
		_mySelListener = new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				ISelection sel = event.getSelection();
				Vector<DraggableItem> dragees = new Vector<DraggableItem>();
				if (sel instanceof StructuredSelection)
				{
					StructuredSelection str = (StructuredSelection) sel;
					Iterator<?> iter = str.iterator();
					while (iter.hasNext())
					{
						Object object = (Object) iter.next();
						if (object instanceof EditableWrapper)
						{
							EditableWrapper ew = (EditableWrapper) object;
							Editable item = ew.getEditable();
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

					// ok, we've just got draggable items - override the current item
					_draggableSelection = dragees;
				}
			}
		};

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
	protected void createStackedPlot(Frame plotControl)
	{

		// first create the x (time) axis
		final SimpleDateFormat _df = new SimpleDateFormat("HHmm:ss");
		_df.setTimeZone(TimeZone.getTimeZone("GMT"));

		final DateAxis xAxis = new CachedTickDateAxis("");
		xAxis.setDateFormatOverride(_df);

		xAxis.setStandardTickUnits(DateAxisEditor
				.createStandardDateTickUnitsAsTickUnits());
		xAxis.setAutoTickUnitSelection(true);

		// create the special stepper plot
		_dotPlot = new XYPlot();
		_dotPlot.setRangeAxis(new NumberAxis("Error (" + getUnits() + ")"));
		_dotPlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
		_dotPlot
				.setRenderer(new ColourStandardXYItemRenderer(null, null, _dotPlot));

		// now try to do add a zero marker on the error bar
		Paint thePaint = Color.LIGHT_GRAY;
		Stroke theStroke = new BasicStroke(2);
		final ValueMarker zeroMarker = new ValueMarker(0.0, thePaint, theStroke);
		_dotPlot.addRangeMarker(zeroMarker);

		_linePlot = new XYPlot();
		NumberAxis absBrgAxis = new NumberAxis("Absolute (" + getUnits() + ")");
		_linePlot.setRangeAxis(absBrgAxis);
		absBrgAxis.setAutoRangeIncludesZero(false);
		_linePlot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
		DefaultXYItemRenderer lineRend = new ColourStandardXYItemRenderer(null,
				null, _linePlot);
		lineRend.setPaint(Color.DARK_GRAY);
		_linePlot.setRenderer(lineRend);

		_linePlot.setDomainCrosshairVisible(true);
		_linePlot.setRangeCrosshairVisible(true);
		_linePlot.setDomainCrosshairPaint(Color.LIGHT_GRAY);
		_linePlot.setRangeCrosshairPaint(Color.LIGHT_GRAY);
		_linePlot.setDomainCrosshairStroke(new BasicStroke(1));
		_linePlot.setRangeCrosshairStroke(new BasicStroke(1));

		// and the plot object to display the cross hair value
		final XYTextAnnotation annot = new XYTextAnnotation("-----", 0, 0);
		annot.setTextAnchor(TextAnchor.TOP_LEFT);
		annot.setPaint(Color.white);
		annot.setBackgroundPaint(Color.black);
		_linePlot.addAnnotation(annot);

		// give them a high contrast backdrop
		_dotPlot.setBackgroundPaint(Color.black);
		_linePlot.setBackgroundPaint(Color.black);

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

		_myChart.addProgressListener(new ChartProgressListener()
		{
			public void chartProgress(ChartProgressEvent cpe)
			{
				if (cpe.getType() != ChartProgressEvent.DRAWING_FINISHED)
					return;

				// is hte line plot visible?
				if (!_showLinePlot.isChecked())
					return;

				// double-check our label is still in the right place
				double xVal = _linePlot.getRangeAxis().getLowerBound();
				double yVal = _linePlot.getDomainAxis().getUpperBound();
				annot.setX(yVal);
				annot.setY(xVal);

				// and write the text
				String numA = MWC.Utilities.TextFormatting.GeneralFormat
						.formatOneDecimalPlace(_linePlot.getRangeCrosshairValue());
				Date newDate = new Date((long) _linePlot.getDomainCrosshairValue());
				final SimpleDateFormat _df = new SimpleDateFormat("HHmm:ss");
				_df.setTimeZone(TimeZone.getTimeZone("GMT"));
				String dateVal = _df.format(newDate);
				String theMessage = " [" + dateVal + "," + numA + "]";
				annot.setText(theMessage);

				_linePlot.removeAnnotation(annot);
				_linePlot.addAnnotation(annot);
			}
		});

		// and insert into the panel
		plotControl.add(plotHolder, BorderLayout.CENTER);

		plotControl.addMouseMotionListener(new MouseMotionListener()
		{

			public void mouseDragged(MouseEvent e)
			{
				// ignore
			}

			public void mouseMoved(MouseEvent e)
			{
				// Point pt = e.getPoint();
				// // suspend this development. we need to sort out which plot
				// the mouse
				// is over
				// int mouseX = pt.y;
				// int mouseY = pt.x;
				// Point2D p = plotHolder
				// .translateScreenToJava2D(new Point(mouseX, mouseY));
				// System.out.println(pt.x + ", " + pt.y + " to: x = " + mouseX
				// +
				// ", y = "
				// + mouseY);
				// // Rectangle2D plotArea = chartPanel.getScreenDataArea();
				//
				// CombinedDomainXYPlot comb = (CombinedDomainXYPlot)
				// _dotPlot.getParent();
				// // XYPlot thisPlot = comb.findSubplot(comb.getParent().getp,
				// p);
				//
				// Component comp = plotHolder.getComponentAt(pt);
				// ChartEntity entity = plotHolder.getEntityForPoint(mouseX,
				// mouseY);
				// System.err.println("comp:" + comp + "// entity:" + entity);
				//
				// Rectangle2D plotArea =
				// plotHolder.getChartRenderingInfo().getPlotInfo()
				// .getDataArea();
				// ValueAxis domainAxis = _dotPlot.getDomainAxis();
				// RectangleEdge domainAxisEdge = _dotPlot.getDomainAxisEdge();
				// ValueAxis rangeAxis = _dotPlot.getRangeAxis();
				// RectangleEdge rangeAxisEdge = _dotPlot.getRangeAxisEdge();
				//
				// if (domainAxis != null)
				// {
				// double chartX = domainAxis.java2DToValue(p.getX(), plotArea,
				// domainAxisEdge);
				// double chartY = rangeAxis.java2DToValue(p.getY(), plotArea,
				// rangeAxisEdge);
				// System.out.println("Chart: x = " + chartX + ", y = " +
				// chartY);
				// }
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
			_theTrackDataListener.removeTrackShiftListener(_myShiftListener);
		}

	}

	protected void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(_centreYAxis);
		manager.add(_onlyVisible);
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
				boolean val = _autoResize.isChecked();
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
				.getImageDescriptor("icons/fit_to_size.png"));

		_centreYAxis = new Action("Center Y axis on origin", IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				super.run();
				// ok - redraw the plot we may have changed the axis centreing
				updateStackedDots(false);
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
		_onlyVisible.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/reveal.gif"));

		// now the course action
		_magicBtn = new Action("Magic", IAction.AS_PUSH_BUTTON)
		{
			@Override
			public void run()
			{
				super.run();
				doMagic();
			}
		};

	}

	protected void doMagic()
	{
		// right, find the layer manager
		IViewPart mgr = this.getViewSite().getPage().findView(CorePlugin.LAYER_MANAGER);
		ISelectionProvider selProvider = (ISelectionProvider) mgr.getAdapter(ISelectionProvider.class);
		IStructuredSelection sel = (IStructuredSelection) selProvider.getSelection();
		
		// ok, see if we've got something of value
		Vector<DraggableItem> dragees = new Vector<DraggableItem>();
		String troubles = OptimiseTest.getDraggables(dragees, sel, _myHelper.getSecondaryTrack());
		
		if(troubles != null)
		{
			CorePlugin.showMessage("Optimise solution", troubles);
			return;
		}
		
		
		if (_draggableSelection != null)
		{
			Iterator<DraggableItem> iter = _draggableSelection.iterator();
			while (iter.hasNext())
			{
				DraggableItem draggableItem = (DraggableItem) iter.next();
				System.out.println(draggableItem);
			}
			return;
		}

		// cool sort out the list of sensor locations for these tracks
		TreeSet<Doublet> doublets = _myHelper.getDoublets(_onlyVisible.isChecked(),
				true, false);

		// Create instance of Minimisation
		Minimisation min = new Minimisation();
		MinimisationFunction funct = new TryOffsetFunction(doublets);

		// initial estimates
		double[] start =
		{ 0, 0 };

		// initial step sizes
		double[] step =
		{ 20, 400 };

		// convergence tolerance
		double ftol = 1e-8;

		// set the min/max bearing
		min.addConstraint(0, -1, 0d);
		min.addConstraint(0, 1, 360d);

		// set the min/max ranges
		min.addConstraint(1, -1, 0d);
		min.addConstraint(1, 1, 6000d);

		// Nelder and Mead minimisation procedure
		min.nelderMead(funct, start, step, ftol, 4000);

		// get the results out
		double[] param = min.getParamValues();

		double bearing = param[0];
		double range = param[1];

		System.out.println("calc result is brg:" + bearing + " rng:" + range);

		// produce a world-vector
		WorldVector vec = new WorldVector(
				MWC.Algorithms.Conversions.Degs2Rads(bearing),
				MWC.Algorithms.Conversions.m2Degs(range), 0);

		// get the secondary track
		TrackWrapper secTrack = _myHelper.getSecondaryTrack();

		DragOperation shiftIt = new DragOperation()
		{
			public void apply(DraggableItem item, WorldVector offset)
			{
				item.shift(offset);
			}
		};

		// put it into our action
		DragFeatureAction dta = new DragFeatureAction(vec, secTrack,
				_ourLayersSubject, secTrack, shiftIt);

		// and wrap it
		DebriefActionWrapper daw = new DebriefActionWrapper(dta, _ourLayersSubject, secTrack);

		// and add it to the clipboard
		CorePlugin.run(daw);

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
	}

	public void logError(int statusCode, String string, Exception object)
	{
		// somehow, put the message into the UI
		_myChart.setTitle(string);

		// is it a fail status
		if (statusCode != Status.OK)
		{
			// and store the problem into the log
			CorePlugin.logError(statusCode, string, object);

			// also ditch the data in the plots - to blank them out
			_dotPlot.setDataset(null);
			_linePlot.setDataset(null);
		}
	}

	/**
	 * the track has been moved, update the dots
	 */
	void updateStackedDots(boolean updateDoublets)
	{

		// update the current datasets
		updateData(updateDoublets);

		// we will only centre the y-axis if the user hasn't performed a zoom
		// operation
		if (_centreYAxis.isChecked())
		{
			if (_showDotPlot.isChecked())
			{
				// do a quick fudge to make sure zero is in the centre
				final Range rng = _dotPlot.getRangeAxis().getRange();
				final double maxVal = Math.max(Math.abs(rng.getLowerBound()),
						Math.abs(rng.getUpperBound()));
				_dotPlot.getRangeAxis().setRange(-maxVal, maxVal);
			}
		}

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
			CachedTickDateAxis date = (CachedTickDateAxis) _combined.getDomainAxis();
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
	private void watchMyParts()
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		final ErrorLogger logger = this;

		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						ISelectionProvider prov = (ISelectionProvider) part;

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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						ISelectionProvider prov = (ISelectionProvider) part;

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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// is it a new one?
						if (part != _theTrackDataListener)
						{
							// cool, remember about it.
							_theTrackDataListener = (TrackManager) part;

							// set the title, so there's something useful in
							// there
							_myChart.setTitle(getType() + " Error");

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
									// the tracks have moved, we haven't changed
									// the tracks or
									// anything like that...
									updateStackedDots(false);
								}
							};

							_myTrackDataListener = new TrackDataListener()
							{

								public void tracksUpdated(WatchableList primary,
										WatchableList[] secondaries)
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
											_onlyVisible.isChecked(), _holder, logger, getType(),
											_needBrg, _needFreq);
									updateStackedDots(false);
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		super.init(site, memento);

		if (memento != null)
		{

			Boolean showLineVal = memento.getBoolean(SHOW_LINE_PLOT);
			Boolean showDotVal = memento.getBoolean(SHOW_DOT_PLOT);
			if (showLineVal != null)
			{
				_showLinePlot.setChecked(showLineVal);
			}
			if (showDotVal != null)
			{
				_showDotPlot.setChecked(showDotVal);
			}
		}
	}

	@Override
	public void saveState(IMemento memento)
	{
		super.saveState(memento);

		// remember if we're showing the error plot
		memento.putBoolean(SHOW_LINE_PLOT, _showLinePlot.isChecked());
		memento.putBoolean(SHOW_DOT_PLOT, _showDotPlot.isChecked());

	}

}