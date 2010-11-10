package org.mwc.cmap.xyplot.views;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.preferences.SelectionHelper;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.xyplot.XYPlotPlugin;

import Debrief.GUI.Tote.StepControl;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Canvas.MetafileCanvasGraphics2d;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.JFreeChart.DatedToolTipGenerator;
import MWC.GUI.JFreeChart.NewFormattedJFreeChart;
import MWC.GUI.JFreeChart.RelativeDateAxis;
import MWC.GUI.JFreeChart.StepperChartPanel;
import MWC.GUI.JFreeChart.StepperXYPlot;
import MWC.GUI.JFreeChart.formattingOperation;
import MWC.GUI.JFreeChart.DateAxisEditor.MWCDateTickUnitWrapper;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;

import com.pietjonas.wmfwriter2d.ClipboardCopy;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XYPlotView extends ViewPart
{

	// //////////////////////////////////////////////////
	// data we store between sessions
	// //////////////////////////////////////////////////

	private static final String FIXED_DURATION = "FixedDuration";

	private static final String DISPLAY_FIXED_DURATION = "DisplayFixedDuration";

	/**
	 * data-type names
	 */

	private static final String PLOT_ID = "PlotId";

	private static final String DO_WATERFALL = "DO_WATERFALL";

	private final String TITLE = "XYPlot_Title";

	private final String UNITS = "XYPlot_Units";

	private final String FORMATTER = "XYPlot_Formatter";

	private final String DATA = "XYPlot_Data";

	private static interface PLOT_ATTRIBUTES
	{
		final String AxisFont = "AxisFont";

		final String TickFont = "TickFont";

		final String TitleFont = "TitleFont";

		final String LineWidth = "LineWidth";

		final String Title = "Title";

		final String X_Title = "X_Title";

		final String Y_Title = "Y_Title";

		final String DateUnits = "DateUnits";

		final String RelativeTimes = "RelativeTimes";

		final String ShowSymbols = "ShowSymbols";
	}

	/**
	 * title of plot
	 */
	private String _myTitle = "Empty";

	/**
	 * the name of the units on the y axis
	 */
	private String _myUnits;

	/**
	 * how to format the data
	 */
	private formattingOperation _theFormatter;

	/**
	 * the data we're storing
	 */
	private AbstractSeriesDataset _dataset;

	/**
	 * resize the data to fill the window
	 */
	private Action _fitToWindow;

	/**
	 * resize the data to fill the window
	 */
	private Action _switchAxes;

	/**
	 * make the plot grow in real time
	 */
	private Action _growPlot;

	/**
	 * output the plot as a WMF
	 */
	private Action _exportToWMF;

	/**
	 * put the plot on the clipboard
	 */
	private Action _exportToClipboard;

	/**
	 * the Swing control we insert the plot into
	 */
	private Frame _plotControl;

	/**
	 * the data-area of the plot
	 */
	StepperXYPlot _thePlot;

	/**
	 * the area surrounding the plot
	 */
	private NewFormattedJFreeChart _thePlotArea;

	/**
	 * object to tie the plot to the step control
	 */
	StepperChartPanel _chartInPanel;

	/**
	 * somebody to listen to the time changes
	 */
	private PropertyChangeListener _timeListener;

	/**
	 * helper - to let the user edit us
	 */
	private SelectionHelper _selectionHelper;

	private Action _editMyProperties;

	/**
	 * store the plot information when we're reloading a plot in a fresh session
	 */
	private IMemento _myMemento = null;

	private String _myId;

	/**
	 * The constructor.
	 */
	public XYPlotView()
	{
	}

	/**
	 * put some data into the view
	 * 
	 * @param title
	 *          - the title for the plot
	 * @param dataset
	 *          - the dataset to plot
	 * @param units
	 *          - the units (for the y axis)
	 * @param theFormatter
	 *          - an object capable of applying formatting to the plot
	 * @param thePlotId
	 */
	public void showPlot(String title, AbstractSeriesDataset dataset,
			String units, formattingOperation theFormatter, String thePlotId)
	{
		// right, store the incoming data, so we can save it when/if
		// Eclipse closes with this view still open
		_myTitle = title;
		_myUnits = units;
		_theFormatter = theFormatter;
		_dataset = dataset;
		_myId = thePlotId;

		// ok, update the plot.
		this.setPartName(_myTitle);

		if (dataset != null)
		{
			// ok, fill in the plot
			fillThePlot(title, units, theFormatter, dataset);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		// right, we need an SWT.EMBEDDED object to act as a holder
		Composite holder = new Composite(parent, SWT.EMBEDDED);
		holder.setLayoutData(new GridData(GridData.FILL_VERTICAL
				| GridData.FILL_HORIZONTAL));

		// now we need a Swing object to put our chart into
		_plotControl = SWT_AWT.new_Frame(holder);

		// and lastly do the remaining bits...
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// put in the plot-copy support
		IActionBars actionBars = getViewSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(),
				_exportToClipboard);

		// and the selection provider bits
		_selectionHelper = new SelectionHelper();
		getSite().setSelectionProvider(_selectionHelper);

		// hey, have we got our data?
		if (_myMemento != null)
		{
			// yup, better restore it then.
			restorePreviousPlot();
		}

	}

	/**
	 * we're restoring a previous plot. retrieve the data from the memento, and
	 * stick it back into the plot
	 * 
	 */
	private void restorePreviousPlot()
	{
		try
		{
			// retrieve the obvious stuff
			_myTitle = _myMemento.getString(TITLE);
			_myUnits = _myMemento.getString(UNITS);
			_myId = _myMemento.getString(PLOT_ID);
			
			
			// get our special streaming library ready
			XStream xs = new XStream(new DomDriver());

			// formatter first
			String theFormatterStr = _myMemento.getString(FORMATTER);

			// hey, do we have a formatter?
			if (theFormatterStr != null)
				_theFormatter = (formattingOperation) xs.fromXML(theFormatterStr);

			// and the data
			String dataStr = _myMemento.getString(DATA);

			// hmm, is there anything in it?
			if (dataStr == null)
				return;

			_dataset = (AbstractSeriesDataset) xs.fromXML(dataStr);

			// right, that's the essential bits, now open the plot
			showPlot(_myTitle, _dataset, _myUnits, _theFormatter, _myId);

			// sort out the fixed duration bits - now we've got our plot
			String theDur = _myMemento.getString(FIXED_DURATION);
			Duration someDur = null;
			if(theDur != null)
			{
				long dur = Long.parseLong(theDur);
				someDur = new Duration(dur, Duration.MILLISECONDS);
			}
			Boolean doFixed = _myMemento.getBoolean(DISPLAY_FIXED_DURATION);
			if(doFixed != null)
			{
				this._thePlotArea.setFixedDuration(someDur);
				this._thePlotArea.setDisplayFixedDuration(doFixed);
			}
			
			// right the plot's done, put back in our fancy formatting bits
			String str;
			str = _myMemento.getString(PLOT_ATTRIBUTES.Title);
			if (str != null)
				_thePlotArea.setTitle((String) xs.fromXML(str));
			Font theF = getFont(_myMemento, PLOT_ATTRIBUTES.AxisFont);
			if (theF != null)
				_thePlotArea.setAxisFont(theF);
			theF = getFont(_myMemento, PLOT_ATTRIBUTES.TickFont);
			if (theF != null)
				_thePlotArea.setTickFont(theF);
			theF = getFont(_myMemento, PLOT_ATTRIBUTES.TitleFont);
			if (theF != null)
				_thePlotArea.setTitleFont(theF);
			str = _myMemento.getString(PLOT_ATTRIBUTES.LineWidth);
			if (str != null)
				_thePlotArea.setDataLineWidth(((Integer) xs.fromXML(str)).intValue());
			str = _myMemento.getString(PLOT_ATTRIBUTES.X_Title);
			if (str != null)
				_thePlotArea.setX_AxisTitle((String) xs.fromXML(str));
			str = _myMemento.getString(PLOT_ATTRIBUTES.Y_Title);
			if (str != null)
				_thePlotArea.setY_AxisTitle((String) xs.fromXML(str));
			str = _myMemento.getString(PLOT_ATTRIBUTES.DateUnits);
			if (str != null)
				_thePlotArea.setDateTickUnits((MWCDateTickUnitWrapper) xs.fromXML(str));
			str = _myMemento.getString(PLOT_ATTRIBUTES.RelativeTimes);
			if (str != null)
				_thePlotArea.setRelativeTimes(((Boolean) xs.fromXML(str))
						.booleanValue());
			str = _myMemento.getString(PLOT_ATTRIBUTES.ShowSymbols);
			if (str != null)
				_thePlotArea.setShowSymbols(((Boolean) xs.fromXML(str)).booleanValue());

			// and the axis orientation
			String doWaterTxt = _myMemento.getString(DO_WATERFALL);
			if (doWaterTxt != null)
			{
				_switchAxes.setChecked(Boolean.parseBoolean(doWaterTxt));
				_switchAxes.run();
			}
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Failed to read in saved XY Plot data",
					e);
		}
	}

	private void fillThePlot(final String title, String units,
			formattingOperation theFormatter, AbstractSeriesDataset dataset)
	{

		StepControl _theStepper = null;

		// the working variables we rely on later
		_thePlotArea = null;
		ValueAxis xAxis = null;

		XYToolTipGenerator tooltipGenerator = null;

		// the y axis is common to hi & lo res. Format it here
		NumberAxis yAxis = new NumberAxis(units);

		// hmm, see if we are in hi-res mode. If we are, don't use a formatted
		// y-axis, just use the plain long microseconds
		// value
		if (HiResDate.inHiResProcessingMode())
		{

			// final SimpleDateFormat _secFormat = new SimpleDateFormat("ss");

			// ok, simple enough for us...
			NumberAxis nAxis = new NumberAxis("time (secs.micros)")
			{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				// public String getTickLabel(double currentTickValue)
				// {
				// long time = (long) currentTickValue;
				// Date dtg = new HiResDate(0, time).getDate();
				// String res = _secFormat.format(dtg) + "."
				// + DebriefFormatDateTime.formatMicros(new HiResDate(0, time));
				// return res;
				// }
			};
			nAxis.setAutoRangeIncludesZero(false);
			xAxis = nAxis;

			// just show the raw data values
			tooltipGenerator = new StandardXYToolTipGenerator();
		}
		else
		{
			// create a date-formatting axis
			final DateAxis dAxis = new RelativeDateAxis();
			dAxis.setStandardTickUnits(DateAxisEditor
					.createStandardDateTickUnitsAsTickUnits());
			xAxis = dAxis;

			// also create the date-knowledgable tooltip writer
			tooltipGenerator = new DatedToolTipGenerator();
		}

		// create the special stepper plot
		ColourStandardXYItemRenderer theRenderer = new ColourStandardXYItemRenderer(
				tooltipGenerator, null, null);
		_thePlot = new StepperXYPlot(null, (RelativeDateAxis) xAxis, yAxis,
				_theStepper, theRenderer);
		theRenderer.setPlot(_thePlot);

		// loop through the datasets, setting the color of each series to the first
		// color in that series
		if (dataset instanceof TimeSeriesCollection)
		{
			Color seriesCol = null;
			TimeSeriesCollection tsc = (TimeSeriesCollection) dataset;
			for (int i = 0; i < dataset.getSeriesCount(); i++)
			{
				TimeSeries ts = tsc.getSeries(i);
				if (ts.getItemCount() > 0)
				{
					TimeSeriesDataItem dataItem = ts.getDataItem(0);
					if (dataItem instanceof ColouredDataItem)
					{
						ColouredDataItem cd = (ColouredDataItem) dataItem;
						seriesCol = cd.getColor();
						_thePlot.getRenderer().setSeriesPaint(i, seriesCol);
					}
				}
			}
		}

		// apply any formatting for this choice
		if (theFormatter != null)
		{
			theFormatter.format(_thePlot);
		}

		_thePlotArea = new NewFormattedJFreeChart(title, null, _thePlot, true,
				_theStepper);

		// set the color of the area surrounding the plot
		// - naah, don't bother. leave it in the application background color.
		_thePlotArea.setBackgroundPaint(Color.white);

		// ////////////////////////////////////////////////
		// put the holder into one of our special items
		// ////////////////////////////////////////////////
		_chartInPanel = new StepperChartPanel(_thePlotArea, true, _theStepper);

		// ok - we need to fire time-changes to the chart
		setupFiringChangesToChart();

		// format the chart
		_chartInPanel.setName(title);
		_chartInPanel.setMouseZoomable(true, true);

		// and insert into the panel
		_plotControl.add(_chartInPanel, BorderLayout.CENTER);

		// get the cross hairs ready
		_thePlot.setDomainCrosshairVisible(true);
		_thePlot.setRangeCrosshairVisible(true);
		_thePlot.setDomainCrosshairPaint(Color.LIGHT_GRAY);
		_thePlot.setRangeCrosshairPaint(Color.LIGHT_GRAY);
		_thePlot.setDomainCrosshairStroke(new BasicStroke(1));
		_thePlot.setRangeCrosshairStroke(new BasicStroke(1));
		
		// and the plot object to display the cross hair value
		final XYTextAnnotation annot = new XYTextAnnotation("-----", 0, 0);
		annot.setTextAnchor(TextAnchor.TOP_LEFT);
		annot.setPaint(Color.black);
		annot.setBackgroundPaint(Color.white);
		_thePlot.addAnnotation(annot);
		
		_thePlotArea.addProgressListener(new ChartProgressListener()
		{
			public void chartProgress(ChartProgressEvent cpe)
			{
				if (cpe.getType() != ChartProgressEvent.DRAWING_FINISHED)
					return;

				// double-check our label is still in the right place
				double xVal = _thePlot.getRangeAxis().getUpperBound();
				double yVal = _thePlot.getDomainAxis().getLowerBound();
				annot.setX(yVal);
				annot.setY(xVal);

				// and write the text
				String numA = MWC.Utilities.TextFormatting.GeneralFormat
				.formatOneDecimalPlace(_thePlot.getRangeCrosshairValue());
				Date newDate = new Date((long)_thePlot.getDomainCrosshairValue());
				final SimpleDateFormat _df = new SimpleDateFormat("HHmm:ss");
				_df.setTimeZone(TimeZone.getTimeZone("GMT"));
				String dateVal = _df.format(newDate);
				String theMessage =  " [" + dateVal + "," + numA + "]";
				annot.setText(theMessage);
				
				// aah, now we have to add and then remove the annotation in order
				// for the new text value to be displayed. Watch and learn...
				_thePlot.removeAnnotation(annot);
				_thePlot.addAnnotation(annot);
				
			}
		});

		// ////////////////////////////////////////////////////
		// put the time series into the plot
		// ////////////////////////////////////////////////////
		_thePlot.setDataset((XYDataset) dataset);
	}

	private void setupFiringChangesToChart()
	{
		
		// see if we've alreay been configured
		if(_timeListener != null)
			return;
		
		// get the document being edited
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		IEditorPart editor = null;

		// the page might not yet be open...
		if (page != null)
		{
			editor = page.getActiveEditor();
			// do we have an active editor?
			if (editor == null)
			{
				// see if there are any editors at all open
				IEditorReference[] theEditors = page.getEditorReferences();
				for (int i = 0; i < theEditors.length; i++)
				{
					IEditorReference thisE = theEditors[i];
					editor = thisE.getEditor(false);

					// right, see if it has a time manager
					TimeProvider tp = (TimeProvider) editor
							.getAdapter(TimeProvider.class);
					if (tp != null)
					{
						String hisId = tp.getId();
						if (hisId == _myId)
							break;
					}
				}

				// nope, drop out.
				return;
			}
		}

		TimeProvider prov = null;
		if (editor != null)
		{
			// get it's time-provider interface
			prov = (TimeProvider) editor.getAdapter(TimeProvider.class);
		}
		else
			CorePlugin.logError(Status.WARNING, "Failed to identify time provider",
					null);

		if (prov != null)
		{
			// create our listener
			_timeListener = new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					// ok - fire the time change to the chart
					HiResDate newDTG = (HiResDate) evt.getNewValue();

					// right tell the plot it's new time
					_thePlot.newTime(null, newDTG, null);

					// and tell the plot holder to redraw everything
					_chartInPanel.newTime(null, newDTG, null);
				}
			};

			// add our listener to the time object
			prov.addListener(_timeListener, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

			// fire the current time to our chart (just to start us off)
			_chartInPanel.newTime(null, prov.getTime(), null);
		}
	}

	final void wmfToFile()
	{

		// create the metafile graphics
		final MetafileCanvasGraphics2d mf = new MetafileCanvasGraphics2d("c:/",
				(Graphics2D) _chartInPanel.getGraphics());

		doWMF(mf);
	}

	final void wmfToClipboard()
	{

		// create the metafile graphics
		final MetafileCanvasGraphics2d mf = new MetafileCanvasGraphics2d("c:/",
				(Graphics2D) _chartInPanel.getGraphics());

		doWMF(mf);

		// try to get the filename
		String fName = MetafileCanvasGraphics2d.getLastFileName();

		// get the dimensions of the last plot operation
		Dimension dim = MetafileCanvasGraphics2d.getLastScreenSize();

		// try to copy the wmf to the clipboard
		try
		{
			// create the clipboard
			ClipboardCopy cc = new ClipboardCopy();

			cc.copyWithPixelSize(fName, dim.width, dim.height, false);
		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e, "Whilst writing WMF to clipboard");
		}

	}

	private final void doWMF(MetafileCanvasGraphics2d mf)
	{

		// get the old background colour
		Paint oldColor = _thePlot.getBackgroundPaint();
		Paint oldAreaColor = _thePlotArea.getBackgroundPaint();

		// set the background to clear
		_thePlotArea.setBackgroundPaint(Color.white);
		_thePlot.setBackgroundPaint(Color.white);

		// copy the projection
		final MWC.Algorithms.Projections.FlatProjection fp = new FlatProjection();
		fp.setScreenArea(_plotControl.getSize());
		mf.setProjection(fp);

		// start drawing
		mf.startDraw(null);

		// sort out the background colour
		Dimension dim = _plotControl.getSize();
		mf.setBackgroundColor(java.awt.Color.white);
		mf.setColor(mf.getBackgroundColor());
		mf.fillRect(0, 0, dim.width, dim.height);

		try
		{
			// ask the canvas to paint the image
			_chartInPanel.paintWMFComponent(mf);
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Problem writing WMF", e);
		}

		// and finish
		mf.endDraw(null);

		// and restore the background colour
		_thePlot.setBackgroundPaint(oldColor);
		_thePlotArea.setBackgroundPaint(oldAreaColor);

	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				XYPlotView.this.fillContextMenu(manager);
			}
		});
		// Menu menu = menuMgr.createContextMenu(viewer.getControl());
		// viewer.getControl().setMenu(menu);
		// getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(_fitToWindow);
		manager.add(_switchAxes);
		manager.add(_growPlot);
		manager.add(new Separator());
		manager.add(_exportToWMF);
		manager.add(_exportToClipboard);
		manager.add(_editMyProperties);
	}

	void fillContextMenu(IMenuManager manager)
	{
		manager.add(_fitToWindow);
		manager.add(_exportToWMF);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_fitToWindow);
		manager.add(_switchAxes);
		manager.add(_growPlot);
		manager.add(_exportToWMF);
		manager.add(_exportToClipboard);
		manager.add(_editMyProperties);
	}

	private void makeActions()
	{

		_editMyProperties = new Action()
		{
			public void run()
			{
				editMeInProperties();
			}
		};
		_editMyProperties.setText("Configure plot");
		_editMyProperties
				.setToolTipText("Change editable properties for this chart");
		_editMyProperties.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/properties.gif"));

		_switchAxes = new Action("Plot as waterfall", SWT.TOGGLE)
		{
			public void run()
			{
				try
				{
					if (_switchAxes.isChecked())
						_thePlot.setOrientation(PlotOrientation.HORIZONTAL);
					else
						_thePlot.setOrientation(PlotOrientation.VERTICAL);

				}
				catch (Exception e)
				{
					MWC.Utilities.Errors.Trace.trace(e,
							"whilst performing resize after loading new plot");
				}
			}
		};
		_switchAxes.setToolTipText("Switch axes");
		_switchAxes.setImageDescriptor(XYPlotPlugin
				.getImageDescriptor("icons/switchAxes.png"));

		_growPlot = new Action("Grow times", SWT.TOGGLE)
		{
			public void run()
			{
				try
				{
					_thePlot.setGrowWithTime(_growPlot.isChecked());
					
					// may aswell trigger a redraw
					_chartInPanel.invalidate();
				}
				catch (Exception e)
				{
					MWC.Utilities.Errors.Trace.trace(e,
							"whilst performing resize after loading new plot");
				}
			}
		};
		_growPlot
				.setToolTipText("Expand period covered in sync with scenario time");
		_growPlot.setImageDescriptor(XYPlotPlugin
				.getImageDescriptor("icons/expandTime.png"));

		_fitToWindow = new Action()
		{
			public void run()
			{
				try
				{
					_thePlot.zoom(0.0);
				}
				catch (Exception e)
				{
					MWC.Utilities.Errors.Trace.trace(e,
							"whilst performing resize after loading new plot");
				}
			}
		};
		_fitToWindow.setText("Fit to window");
		_fitToWindow.setToolTipText("Scale the graph to show all data");
		_fitToWindow.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/fit_to_size.png"));

		_exportToWMF = new Action()
		{
			public void run()
			{
				wmfToFile();
			}
		};
		_exportToWMF.setText("Export to WMF");
		_exportToWMF.setToolTipText("Produce a WMF file of the graph");
		_exportToWMF.setImageDescriptor(XYPlotPlugin
				.getImageDescriptor("icons/photo_scenery.png"));

		_exportToClipboard = new Action()
		{
			public void run()
			{
				wmfToClipboard();
			}
		};
		_exportToClipboard.setText("Copy to Clipboard");
		_exportToClipboard
				.setToolTipText("Place a WMF image of the graph on the clipboard");
		_exportToClipboard.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/copy.png"));

	}

	private void hookDoubleClickAction()
	{
		// viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// }
		// });
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		// viewer.getControl().setFocus();

		if ((_timeListener == null) && (_chartInPanel != null))
		{
			setupFiringChangesToChart();
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void editMeInProperties()
	{
		// do we have any data?
		if (_thePlotArea != null)
		{
			EditableWrapper wrappedEditable = new EditableWrapper(_thePlotArea);
			StructuredSelection _propsAsSelection = new StructuredSelection(
					wrappedEditable);

			_selectionHelper.fireNewSelection(_propsAsSelection);
		}
		else
		{
			System.out.println("we haven't got any properties yet");
		}
	}

	/**
	 * right, load ourselves from the supplied dataset
	 * 
	 * @param site
	 * @param memento
	 * @throws PartInitException
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		// let our parent go for it first
		super.init(site, memento);

		// is there any data waiting? We get an empty memento if this is a fresh
		// view
		if (memento != null)
		{
			_myMemento = memento;
		}
	}

	/**
	 * right - store ourselves into the supplied memento object
	 * 
	 * @param memento
	 */
	public void saveState(IMemento memento)
	{
		// let our parent go for it first
		super.saveState(memento);

		memento.putString(TITLE, _myTitle);
		memento.putString(UNITS, _myUnits);
		memento.putString(PLOT_ID, _myId);
		
		// sort out the fixed duration bits
		memento.putBoolean(DISPLAY_FIXED_DURATION, this._thePlotArea.getDisplayFixedDuration() );
		memento.putString(FIXED_DURATION,"" + this._thePlotArea.getFixedDuration().getMillis() );

		// store whether the axes are switched
		memento.putString(DO_WATERFALL, Boolean.toString(_switchAxes.isChecked()));

		XStream xs = new XStream(new DomDriver());
		String str;

		// String str = xs.toXML(_theFormatter);
		if (_theFormatter != null)
		{
			str = xs.toXML(_theFormatter);
			memento.putString(FORMATTER, str);
		}

		str = xs.toXML(_dataset);
		memento.putString(DATA, str);

		// now the other plot bits
		// @@
		storeFont(memento, PLOT_ATTRIBUTES.AxisFont, _thePlotArea.getAxisFont());
		storeFont(memento, PLOT_ATTRIBUTES.TickFont, _thePlotArea.getTickFont());
		storeFont(memento, PLOT_ATTRIBUTES.TitleFont, _thePlotArea.getTitleFont());
		str = xs.toXML(new Integer(_thePlotArea.getDataLineWidth()));
		memento.putString(PLOT_ATTRIBUTES.LineWidth, str);
		str = xs.toXML(_thePlotArea.getTitle().getText());
		memento.putString(PLOT_ATTRIBUTES.Title, str);
		str = xs.toXML(_thePlotArea.getX_AxisTitle());
		memento.putString(PLOT_ATTRIBUTES.X_Title, str);
		str = xs.toXML(_thePlotArea.getY_AxisTitle());
		memento.putString(PLOT_ATTRIBUTES.Y_Title, str);
		str = xs.toXML(_thePlotArea.getDateTickUnits());
		memento.putString(PLOT_ATTRIBUTES.DateUnits, str);
		str = xs.toXML(new Boolean(_thePlotArea.getRelativeTimes()));
		memento.putString(PLOT_ATTRIBUTES.RelativeTimes, str);
		str = xs.toXML(new Boolean(_thePlotArea.isShowSymbols()));
		memento.putString(PLOT_ATTRIBUTES.ShowSymbols, str);

	}

	private void storeFont(IMemento memento, String entryHeader, Font theFont)
	{
		// write elements
		memento.putInteger(entryHeader + "_SIZE", theFont.getSize());
		memento.putString(entryHeader + "_FAMILY", theFont.getFamily());
		memento.putInteger(entryHeader + "_STYLE", theFont.getStyle());
	}

	private Font getFont(IMemento memento, String entryHeader)
	{

		Font res = null;
		String family = memento.getString(entryHeader + "_FAMILY");
		Integer size = memento.getInteger(entryHeader + "_SIZE");
		Integer style = memento.getInteger(entryHeader + "_STYLE");
		if (family != null)
			res = new Font(family, style, size);
		return res;
	}

	public static class StringHolder
	{
		private String _myString;

		public StringHolder()
		{
		}

		public StringHolder(String theString)
		{
			_myString = theString;
		}

		public String get_myString()
		{
			return _myString;
		}

		public void set_myString(String string)
		{
			_myString = string;
		}
	}

}