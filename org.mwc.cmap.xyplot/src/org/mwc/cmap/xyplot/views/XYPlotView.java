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
package org.mwc.cmap.xyplot.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.JComponent;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
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
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.TextAnchor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.preferences.SelectionHelper;
import org.mwc.cmap.core.preferences.WMFExportPrefsPage.PreferenceConstants;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.property_support.FontHelper;
import org.mwc.cmap.plotViewer.PlotViewerPlugin;
import org.mwc.cmap.plotViewer.actions.RTFWriter;
import org.mwc.cmap.xyplot.XYPlotPlugin;

import com.pietjonas.wmfwriter2d.ClipboardCopy;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import Debrief.GUI.Tote.StepControl;
import MWC.Algorithms.Projections.FlatProjection;
import MWC.GUI.Defaults;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.Canvas.MetafileCanvasGraphics2d;
import MWC.GUI.JFreeChart.AttractiveDataItem;
import MWC.GUI.JFreeChart.ColourStandardXYItemRenderer;
import MWC.GUI.JFreeChart.DateAxisEditor;
import MWC.GUI.JFreeChart.DatedToolTipGenerator;
import MWC.GUI.JFreeChart.NewFormattedJFreeChart;
import MWC.GUI.JFreeChart.RelativeDateAxis;
import MWC.GUI.JFreeChart.StepperChartPanel;
import MWC.GUI.JFreeChart.StepperXYPlot;
import MWC.GUI.JFreeChart.formattingOperation;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.temporal.TimeProvider;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class XYPlotView extends ViewPart
{

  /**
   * hide the implemetnation of something that can provide a dataset.
   * 
   * @author ian
   * 
   */
  public static interface DatasetProvider
  {
    /**
     * get (recalculate) the dataset
     * 
     * @param liveUpdates
     *          whether to include newest updstes
     * 
     * @return
     */
    public AbstractSeriesDataset getDataset(boolean liveUpdates);

    /**
     * get the layers that this data comes from
     * 
     * @return
     */
    Layers getLayers();
  }

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

    final String LabelFormat = NewFormattedJFreeChart.CHART_LABEL_FORMAT;

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
   * whether to show/hide the crosshairs
   */
  private Action _hideCrosshairs;

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
   * put the plot on the clipboard as String matrix
   */
  private Action _copyToClipboard;

  /**
   * the SWT control we insert the plot into
   */
  private ChartComposite _plotControl;

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

  private IPartListener editorListener = new IPartListener()
  {
    @Override
    public void partOpened(IWorkbenchPart part)
    {
    }

    @Override
    public void partDeactivated(IWorkbenchPart part)
    {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part)
    {
    }

    @Override
    public void partActivated(IWorkbenchPart part)
    {
    }

    @Override
    public void partClosed(IWorkbenchPart part)
    {
      if (part == editor)
      {
        getSite().getPage().hideView(XYPlotView.this);
      }
    }
  };

  /**
   * helper - to let the user edit us
   */
  private SelectionHelper _selectionHelper;

  private Action _editMyProperties;

  private ShowSymbolAction _showSymbols;

  /**
   * store the plot information when we're reloading a plot in a fresh session
   */
  private IMemento _myMemento = null;

  private String _myId;

  /**
   * the special action that indicates if we should listen to our data changing, but only if we have
   * a data provider
   */
  private Action _listenForDataChanges;

  /**
   * a helper that's able to generate a dataset
   * 
   */
  private transient DatasetProvider _provider;

  /**
   * our pre-generated layer listener
   * 
   */
  protected DataListener _modifiedListener;

  private IEditorPart editor;

  private XYTextAnnotation _crosshairValueText;

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
  public void showPlot(final String title, final AbstractSeriesDataset dataset,
      final String units, final formattingOperation theFormatter,
      final String thePlotId)
  {

    _listenForDataChanges.setEnabled(false);

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
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  public void createPartControl(final Composite parent)
  {
    _plotControl =
        new ChartComposite(parent, SWT.NONE, null, 400, 600, 300, 200, 1800,
            1800, true, false, true, true, true, true)
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
    _plotControl.setFont(FontHelper.convertFontFromAWT(Defaults.getFont()));

    // and lastly do the remaining bits...
    makeActions();
    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();

    // put in the plot-copy support
    final IActionBars actionBars = getViewSite().getActionBars();
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

    _modifiedListener = new DataListener()
    {

      @Override
      public void dataModified(final Layers theData, final Layer changedLayer)
      {
        regenerateData();
      }

      @Override
      public void dataExtended(final Layers theData)
      {
        regenerateData();
      }

      @Override
      public void
          dataReformatted(final Layers theData, final Layer changedLayer)
      {
        try
        {
          regenerateData();
        }
        catch (org.eclipse.swt.SWTException se)
        {
          CorePlugin.logError(Status.WARNING, "Trouble redrawing XT Plot", se);
        }
      }
    };

    getSite().getWorkbenchWindow().getPartService().addPartListener(
        editorListener);
  }

  /**
   * we're restoring a previous plot. retrieve the data from the memento, and stick it back into the
   * plot
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
      final XStream xs = new XStream(new DomDriver());

      // formatter first
      final String theFormatterStr = _myMemento.getString(FORMATTER);

      // hey, do we have a formatter?
      if (theFormatterStr != null)
        _theFormatter = (formattingOperation) xs.fromXML(theFormatterStr);

      // and the data
      final String dataStr = _myMemento.getString(DATA);

      // hmm, is there anything in it?
      if (dataStr == null)
        return;

      _dataset = (AbstractSeriesDataset) xs.fromXML(dataStr);

      // right, that's the essential bits, now open the plot
      showPlot(_myTitle, _dataset, _myUnits, _theFormatter, _myId);

      // sort out the fixed duration bits - now we've got our plot
      final String theDur = _myMemento.getString(FIXED_DURATION);
      Duration someDur = null;
      if (theDur != null)
      {
        final long dur = Long.parseLong(theDur);
        someDur = new Duration(dur, Duration.MILLISECONDS);
      }
      final Boolean doFixed = _myMemento.getBoolean(DISPLAY_FIXED_DURATION);
      if (doFixed != null)
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
      str = _myMemento.getString(PLOT_ATTRIBUTES.LabelFormat);
      if (str != null)
        _thePlotArea.setLabelFormat((String) xs.fromXML(str));
      str = _myMemento.getString(PLOT_ATTRIBUTES.RelativeTimes);
      if (str != null)
        _thePlotArea.setRelativeTimes(((Boolean) xs.fromXML(str))
            .booleanValue());
      str = _myMemento.getString(PLOT_ATTRIBUTES.ShowSymbols);
      if (str != null)
        _thePlotArea.setShowSymbols(((Boolean) xs.fromXML(str)).booleanValue());

      // and the axis orientation
      final String doWaterTxt = _myMemento.getString(DO_WATERFALL);
      if (doWaterTxt != null)
      {
        _switchAxes.setChecked(Boolean.parseBoolean(doWaterTxt));
        _switchAxes.run();
      }
    }
    catch (final Exception e)
    {
      CorePlugin.logError(Status.ERROR, "Failed to read in saved XY Plot data",
          e);
    }
  }

  @SuppressWarnings("deprecation")
  private void fillThePlot(final String title, final String units,
      final formattingOperation theFormatter,
      final AbstractSeriesDataset dataset)
  {

    final StepControl _theStepper = null;

    // the working variables we rely on later
    _thePlotArea = null;
    ValueAxis xAxis = null;

    XYToolTipGenerator tooltipGenerator = null;

    // the y axis is common to hi & lo res. Format it here
    final NumberAxis yAxis = new NumberAxis(units);
    final Font tickFont = Defaults.getFont();
    Font labelFont = Defaults.getFont();
    yAxis.setLabelFont(labelFont);
    yAxis.setTickLabelFont(tickFont);

    // hmm, see if we are in hi-res mode. If we are, don't use a formatted
    // y-axis, just use the plain long microseconds
    // value
    if (HiResDate.inHiResProcessingMode())
    {

      // final SimpleDateFormat _secFormat = new SimpleDateFormat("ss");

      // ok, simple enough for us...
      final NumberAxis nAxis = new NumberAxis("time (secs.micros)")
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

    xAxis.setTickLabelFont(tickFont);
    xAxis.setLabelFont(labelFont);

    // create the special stepper plot
    final ColourStandardXYItemRenderer theRenderer =
        new ColourStandardXYItemRenderer(tooltipGenerator, null, null);
    _thePlot =
        new StepperXYPlot(null, (RelativeDateAxis) xAxis, yAxis, _theStepper,
            theRenderer);
    theRenderer.setPlot(_thePlot);
    theRenderer.setStroke(new BasicStroke(3.0f));

    _thePlot.setRangeGridlineStroke(new BasicStroke(1f));
    _thePlot.setDomainGridlineStroke(new BasicStroke(1f));
    _thePlot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    xAxis.setTickMarkStroke(new BasicStroke(1f));
    yAxis.setTickMarkStroke(new BasicStroke(1f));
    _thePlot.setOutlineStroke(new BasicStroke(2f));

    // loop through the datasets, setting the color of each series to the first
    // color in that series
    if (dataset instanceof TimeSeriesCollection)
    {
      Color seriesCol = null;
      final TimeSeriesCollection tsc = (TimeSeriesCollection) dataset;
      for (int i = 0; i < dataset.getSeriesCount(); i++)
      {
        final TimeSeries ts = tsc.getSeries(i);
        if (ts.getItemCount() > 0)
        {
          final TimeSeriesDataItem dataItem = ts.getDataItem(0);
          if (dataItem instanceof AttractiveDataItem)
          {
            final AttractiveDataItem cd = (AttractiveDataItem) dataItem;
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

    boolean createLegend = dataset.getSeriesCount() > 1;
    _thePlotArea =
        new NewFormattedJFreeChart(title, null, _thePlot, createLegend,
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

    // and insert into the composite
    _plotControl.setChart(_thePlotArea);

    // get the cross hairs ready
    _thePlot.setDomainCrosshairVisible(true);
    _thePlot.setRangeCrosshairVisible(true);
    _thePlot.setDomainCrosshairPaint(Color.GRAY);
    _thePlot.setRangeCrosshairPaint(Color.GRAY);
    _thePlot.setDomainCrosshairStroke(new BasicStroke(2));
    _thePlot.setRangeCrosshairStroke(new BasicStroke(2));

    // and the plot object to display the cross hair value
    _crosshairValueText = new XYTextAnnotation(" ", 0, 0);
    _crosshairValueText.setTextAnchor(TextAnchor.TOP_LEFT);
    _crosshairValueText.setFont(Defaults.getFont());
    _crosshairValueText.setPaint(Color.black);
    _crosshairValueText.setBackgroundPaint(Color.white);
    _thePlot.addAnnotation(_crosshairValueText);

    _thePlotArea.addChangeListener(new ChartChangeListener()
    {

      @Override
      public void chartChanged(ChartChangeEvent event)
      {
        if (_showSymbols.isShowSymbols() != _thePlotArea.isShowSymbols())
        {
          _showSymbols.updateAction();
        }
      }
    });
    _showSymbols.updateAction();
    _thePlotArea.addProgressListener(new ChartProgressListener()
    {
      public void chartProgress(final ChartProgressEvent cpe)
      {
        if (cpe.getType() != ChartProgressEvent.DRAWING_FINISHED)
          return;

        // double-check our label is still in the right place
        final double xVal = _thePlot.getRangeAxis().getUpperBound();
        final double yVal = _thePlot.getDomainAxis().getLowerBound();

        boolean annotChanged = false;
        if (_crosshairValueText.getX() != yVal)
        {
          _crosshairValueText.setX(yVal);
          annotChanged = true;
        }
        if (_crosshairValueText.getY() != xVal)
        {
          _crosshairValueText.setY(xVal);
          annotChanged = true;
        }

        // and write the text
        final String numA =
            MWC.Utilities.TextFormatting.GeneralFormat
                .formatOneDecimalPlace(_thePlot.getRangeCrosshairValue());
        final Date newDate =
            new Date((long) _thePlot.getDomainCrosshairValue());
        final DateFormat _df = new GMTDateFormat("HHmm:ss");
        final String dateVal = _df.format(newDate);
        final String theMessage = " [" + dateVal + "," + numA + "]";
        if (!theMessage.equals(_crosshairValueText.getText()))
        {
          _crosshairValueText.setText(theMessage);
          annotChanged = true;
        }

        if (annotChanged)
        {
          _plotControl.getChart().setNotify(true);
        }
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
    if (_timeListener != null)
      return;

    // get the document being edited
    final IWorkbench wb = PlatformUI.getWorkbench();
    final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
    final IWorkbenchPage page = win.getActivePage();
    editor = null;

    // the page might not yet be open...
    if (page != null)
    {
      editor = page.getActiveEditor();
      // do we have an active editor?
      if (editor == null)
      {
        // see if there are any editors at all open
        final IEditorReference[] theEditors = page.getEditorReferences();
        for (int i = 0; i < theEditors.length; i++)
        {
          final IEditorReference thisE = theEditors[i];
          editor = thisE.getEditor(false);

          // right, see if it has a time manager
          final TimeProvider tp =
              (TimeProvider) editor.getAdapter(TimeProvider.class);
          if (tp != null)
          {
            final String hisId = tp.getId();
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
        public void propertyChange(final PropertyChangeEvent evt)
        {
          // ok - fire the time change to the chart
          final HiResDate newDTG = (HiResDate) evt.getNewValue();

          // right tell the plot it's new time
          _thePlot.newTime(null, newDTG, null);

          // and tell the plot holder to redraw everything
          _chartInPanel.newTime(null, newDTG, null);

          refreshPlot();
        }
      };

      // add our listener to the time object
      prov.addListener(_timeListener, TimeProvider.TIME_CHANGED_PROPERTY_NAME);

      // fire the current time to our chart (just to start us off)
      _chartInPanel.newTime(null, prov.getTime(), null);
      refreshPlot();
    }
  }

  final void wmfToFile()
  {
    // create the metafile graphics
    final String dir;

    // retrieve the prefs location for writing WMF
    String tmpDir =
        CorePlugin.getDefault().getPreferenceStore().getString(
            PreferenceConstants.WMF_DIRECTORY);

    // did we find the preference for the WMF location?
    if (tmpDir != null)
    {
      dir = tmpDir;
    }
    else
    {
      dir = System.getProperty("java.io.tmpdir");
    }

    if (_plotControl == null || _plotControl.isDisposed())
    {
      return;
    }
    // ok, setup the export
    Point size = _plotControl.getSize();
    BufferedImage image =
        new BufferedImage(size.x, size.x, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = image.createGraphics();
    _chartInPanel.setSize(size.x, size.y);
    _chartInPanel.paint(g2d);
    final MetafileCanvasGraphics2d mf =
        new MetafileCanvasGraphics2d(dir, (Graphics2D) g2d);

    doWMF(mf);
  }

  final void wmfToClipboard()
  {
    // create the metafile graphics
    final String dir = System.getProperty("java.io.tmpdir");
    final MetafileCanvasGraphics2d mf =
        new MetafileCanvasGraphics2d(dir, (Graphics2D) _chartInPanel
            .getGraphics());

    doWMF(mf);

    // try to get the filename
    final String fName = MetafileCanvasGraphics2d.getLastFileName();

    // get the dimensions of the last plot operation
    final Dimension dim = MetafileCanvasGraphics2d.getLastScreenSize();

    // try to copy the wmf to the clipboard
    if (Platform.OS_WIN32.equals(Platform.getOS())
        && Platform.ARCH_X86.equals(Platform.getOSArch()))
    {
      try
      {
        // create the clipboard
        final ClipboardCopy cc = new ClipboardCopy();

        cc.copyWithPixelSize(fName, dim.width, dim.height, false);
      }
      catch (final Exception e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Whilst writing WMF to clipboard");
      }
    }
    else
    {
      rtfToClipboard(fName, dim);
    }

  }

  private void rtfToClipboard(final String fName, final Dimension dim)
  {
    // Issue #520 - Copy WMF embedded in RTF
    ByteArrayOutputStream os = null;
    DataInputStream dis = null;
    try
    {
      os = new ByteArrayOutputStream();
      RTFWriter writer = new RTFWriter(os);
      File file = new File(fName);
      byte[] data = new byte[(int) file.length()];
      dis = new DataInputStream(new FileInputStream(file));
      dis.readFully(data);
      writer.writeHeader();
      writer.writeEmfPicture(data, dim.getWidth(), dim.getHeight());
      writer.writeTail();

      RTFTransfer rtfTransfer = RTFTransfer.getInstance();
      Clipboard clipboard = new Clipboard(Display.getDefault());
      Object[] rtfData = new Object[]
      {os.toString()};
      clipboard.setContents(rtfData, new Transfer[]
      {rtfTransfer});
    }
    catch (final Exception e1)
    {
      IStatus status =
          new Status(IStatus.ERROR, PlotViewerPlugin.PLUGIN_ID, e1
              .getLocalizedMessage(), e1);
      XYPlotPlugin.getDefault().getLog().log(status);
    }
    finally
    {
      if (os != null)
      {
        try
        {
          os.close();
        }
        catch (IOException e1)
        {
          // ignore
        }
      }
      if (dis != null)
      {
        try
        {
          dis.close();
        }
        catch (IOException e1)
        {
          // ignore
        }
      }
    }

  }

  private final void doWMF(final MetafileCanvasGraphics2d mf)
  {

    // get the old background colour
    final Paint oldColor = _thePlot.getBackgroundPaint();
    final Paint oldAreaColor = _thePlotArea.getBackgroundPaint();

    // set the background to clear
    _thePlotArea.setBackgroundPaint(Color.white);
    _thePlot.setBackgroundPaint(Color.white);

    // copy the projection
    final MWC.Algorithms.Projections.FlatProjection fp = new FlatProjection();
    Point size = _plotControl.getSize();
    Dimension dim = new Dimension(size.x, size.y);
    fp.setScreenArea(dim);
    mf.setProjection(fp);

    // start drawing
    mf.startDraw(null);

    // sort out the background colour
    // final Dimension dim = _plotControl.getSize();
    mf.setBackgroundColor(DebriefColors.WHITE);
    mf.setColor(mf.getBackgroundColor());
    mf.fillRect(0, 0, dim.width, dim.height);

    try
    {
      // ask the canvas to paint the image
      _chartInPanel.paintWMFComponent(mf);
    }
    catch (final Exception e)
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
    final MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener()
    {
      public void menuAboutToShow(final IMenuManager manager)
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
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(final IMenuManager manager)
  {
    manager.add(_fitToWindow);
    manager.add(_switchAxes);
    manager.add(_hideCrosshairs);
    manager.add(_showSymbols);
    manager.add(_growPlot);
    manager.add(new Separator());
    manager.add(_exportToWMF);
    manager.add(_exportToClipboard);
    manager.add(_editMyProperties);
  }

  void fillContextMenu(final IMenuManager manager)
  {
    manager.add(_fitToWindow);
    manager.add(_exportToWMF);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void fillLocalToolBar(final IToolBarManager manager)
  {
    manager.add(_fitToWindow);
    manager.add(_switchAxes);
    manager.add(_showSymbols);
    manager.add(_hideCrosshairs);
    manager.add(_growPlot);
    manager.add(_exportToWMF);
    manager.add(_exportToClipboard);
    manager.add(_copyToClipboard);
    manager.add(_editMyProperties);
    manager.add(_listenForDataChanges);
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
        .getImageDescriptor("icons/16/properties.png"));

    _listenForDataChanges = new Action("Listen for data changes", SWT.TOGGLE)
    {
      public void run()
      {
        super.run();
        doListenStatusUpdate();
      }
    };
    _listenForDataChanges
        .setToolTipText("Auto-sync with calculated track data.");
    _listenForDataChanges.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/follow_time.png"));

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
        catch (final Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e,
              "whilst performing resize after loading new plot");
        }
      }
    };
    _switchAxes.setToolTipText("Switch axes");
    _switchAxes.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/swap_axis.png"));

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
        catch (final Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e,
              "whilst performing resize after loading new plot");
        }
      }
    };
    _growPlot
        .setToolTipText("Expand period covered in sync with scenario time");
    _growPlot.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/clock.png"));

    _fitToWindow = new Action()
    {
      public void run()
      {
        try
        {
          _thePlot.zoom(0.0);
        }
        catch (final Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e,
              "whilst performing resize after loading new plot");
        }
      }
    };
    _fitToWindow.setText("Fit to window");
    _fitToWindow.setToolTipText("Scale the graph to show all data");
    _fitToWindow.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/fit_to_win.png"));

    _exportToWMF = new Action()
    {
      public void run()
      {
        wmfToFile();
      }
    };
    _exportToWMF.setText("Export to WMF file");
    _exportToWMF.setToolTipText("Produce a WMF file of the graph");
    _exportToWMF.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/ex_2word.png"));

    _exportToClipboard = new Action()
    {
      public void run()
      {
        bitmapToClipBoard(_chartInPanel);
      }
    };
    _exportToClipboard.setText("Copy bitmap to Clipboard");
    _exportToClipboard
        .setToolTipText("Place a bitmap image of the graph on the clipboard");
    _exportToClipboard.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/copy.png"));

    _hideCrosshairs = new Action("Hide crosshairs", SWT.TOGGLE)
    {
      public void run()
      {
        try
        {
          _thePlot.setDomainCrosshairVisible(!_hideCrosshairs.isChecked());
          _thePlot.setRangeCrosshairVisible(!_hideCrosshairs.isChecked());

          if (_hideCrosshairs.isChecked())
            _thePlot.removeAnnotation(_crosshairValueText);
          else
            _thePlot.addAnnotation(_crosshairValueText);

          // may aswell trigger a redraw
          _chartInPanel.invalidate();
        }
        catch (final Exception e)
        {
          MWC.Utilities.Errors.Trace.trace(e,
              "whilst changing crosshair visibility");
        }
      }
    };
    _hideCrosshairs.setChecked(false);
    _hideCrosshairs
        .setToolTipText("Hide the crosshair from the graph (for printing)");
    _hideCrosshairs.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/fix.png"));

    _copyToClipboard = new Action()
    {
      public void run()
      {
        if (_thePlot != null)
        {
          final TimeSeriesCollection dataset =
              (TimeSeriesCollection) _thePlot.getDataset();
          XYPlotUtilities.copyToClipboard(_chartInPanel.getName(), dataset);
        }
      }
    };
    _copyToClipboard.setText("Copy to Clipboard");
    _copyToClipboard
        .setToolTipText("Copies the graph as a text matrix to the clipboard");
    _copyToClipboard.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/export.png"));

    _showSymbols = new ShowSymbolAction();
  }

  protected void bitmapToClipBoard(JComponent component)
  {
    Point size = _plotControl.getSize();
    final BufferedImage img =
        new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
    Graphics g = img.getGraphics();
    g.setColor(component.getForeground());
    g.setFont(component.getFont());
    component.setSize(size.x, size.y);
    component.paint(g);
    Transferable t = new Transferable()
    {

      public DataFlavor[] getTransferDataFlavors()
      {
        return new DataFlavor[]
        {DataFlavor.imageFlavor};
      }

      public boolean isDataFlavorSupported(DataFlavor flavor)
      {
        if (flavor == DataFlavor.imageFlavor)
          return true;
        return false;
      }

      public Object getTransferData(DataFlavor flavor)
          throws UnsupportedFlavorException, IOException
      {
        if (isDataFlavorSupported(flavor))
        {
          return img;
        }
        return null;
      }

    };

    ClipboardOwner co = new ClipboardOwner()
    {

      public void lostOwnership(java.awt.datatransfer.Clipboard clipboard,
          Transferable contents)
      {
      }

    };
    java.awt.datatransfer.Clipboard cb =
        Toolkit.getDefaultToolkit().getSystemClipboard();
    cb.setContents(t, co);

  }

  protected void doListenStatusUpdate()
  {
    final boolean doListen = _listenForDataChanges.isChecked();
    if (_provider != null)
    {
      final Layers layers = _provider.getLayers();
      if (layers != null)
      {
        if (doListen)
        {
          layers.addDataModifiedListener(_modifiedListener);
          layers.addDataReformattedListener(_modifiedListener);
          layers.addDataExtendedListener(_modifiedListener);
          regenerateData();
        }
        else
        {
          layers.removeDataReformattedListener(_modifiedListener);
          layers.removeDataExtendedListener(_modifiedListener);
          layers.removeDataModifiedListener(_modifiedListener);
        }
      }
    }

  }

  @Override
  public void dispose()
  {
    super.dispose();

    if (editorListener != null)
    {
      getSite().getWorkbenchWindow().getPartService().removePartListener(
          editorListener);
      editorListener = null;
    }
    // closing, ditch the listenesr
    ditchListeners();
  }

  protected void ditchListeners()
  {
    if (_provider != null)
    {
      final Layers layers = _provider.getLayers();
      if (layers != null)
      {
        layers.removeDataExtendedListener(_modifiedListener);
        layers.removeDataReformattedListener(_modifiedListener);
        layers.removeDataModifiedListener(_modifiedListener);
      }
    }

  }

  protected void regenerateData()
  {
    if (_provider != null)
    {
      // flag for if we're extending for live data
      final boolean liveUpdates = _listenForDataChanges.isChecked();

      final AbstractSeriesDataset ds = _provider.getDataset(liveUpdates);
      if (ds != null)
      {
        // store the dataset
        _dataset = ds;

        // if we failed in plot creation (maybe a track was empty),
        // then _thePlot may not have been created.
        if (_thePlot != null)
        {
          Display.getDefault().asyncExec(new Runnable()
          {
            
            @Override
            public void run()
            {
              _thePlot.setDataset((XYDataset) _dataset);
            }
          });
        }
      }
    }
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
      final EditableWrapper wrappedEditable = new EditableWrapper(_thePlotArea);
      final StructuredSelection _propsAsSelection =
          new StructuredSelection(wrappedEditable);

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
  public void init(final IViewSite site, final IMemento memento)
      throws PartInitException
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
  public void saveState(final IMemento memento)
  {
    // check we have some data
    if (_thePlotArea == null)
      return;

    // let our parent go for it first
    super.saveState(memento);

    memento.putString(TITLE, _myTitle);
    memento.putString(UNITS, _myUnits);
    memento.putString(PLOT_ID, _myId);

    // sort out the fixed duration bits
    memento.putBoolean(DISPLAY_FIXED_DURATION, this._thePlotArea
        .getDisplayFixedDuration());
    memento.putString(FIXED_DURATION, ""
        + this._thePlotArea.getFixedDuration().getMillis());

    // store whether the axes are switched
    memento.putString(DO_WATERFALL, Boolean.toString(_switchAxes.isChecked()));

    final XStream xs = new XStream(new DomDriver());
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
    str = xs.toXML(_thePlotArea.getLabelFormat());
    memento.putString(PLOT_ATTRIBUTES.LabelFormat, str);
    str = xs.toXML(new Boolean(_thePlotArea.getRelativeTimes()));
    memento.putString(PLOT_ATTRIBUTES.RelativeTimes, str);
    str = xs.toXML(new Boolean(_thePlotArea.isShowSymbols()));
    memento.putString(PLOT_ATTRIBUTES.ShowSymbols, str);

  }

  private void storeFont(final IMemento memento, final String entryHeader,
      final Font theFont)
  {
    // write elements
    memento.putInteger(entryHeader + "_SIZE", theFont.getSize());
    memento.putString(entryHeader + "_FAMILY", theFont.getFamily());
    memento.putInteger(entryHeader + "_STYLE", theFont.getStyle());
  }

  private Font getFont(final IMemento memento, final String entryHeader)
  {

    Font res = null;
    final String family = memento.getString(entryHeader + "_FAMILY");
    final Integer size = memento.getInteger(entryHeader + "_SIZE");
    final Integer style = memento.getInteger(entryHeader + "_STYLE");
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

    public StringHolder(final String theString)
    {
      _myString = theString;
    }

    public String get_myString()
    {
      return _myString;
    }

    public void set_myString(final String string)
    {
      _myString = string;
    }
  }

  public void showPlot(final String theTitle, final DatasetProvider prov,
      final String units2, final formattingOperation theFormatter,
      final String thePlotId)

  {
    // right, store the incoming data, so we can save it when/if
    // Eclipse closes with this view still open
    _myTitle = theTitle;
    _myUnits = units2;
    _theFormatter = theFormatter;
    _myId = thePlotId;

    // ok, update the plot.
    this.setPartName(_myTitle);

    _provider = prov;
    if (_provider != null)
    {
      final AbstractSeriesDataset ds = _provider.getDataset(true);
      if (ds != null)
      {
        // store the dataset
        _dataset = ds;
        // ok, fill in the plot
        fillThePlot(_myTitle, _myUnits, _theFormatter, ds);
      }
    }

    // and set the right value
    _listenForDataChanges.setEnabled(true);

    // and tell it to start listening
    _listenForDataChanges.setChecked(true);

    // and process the new state
    doListenStatusUpdate();
  }

  private void refreshPlot()
  {
    Runnable runnable = new Runnable()
    {

      @Override
      public void run()
      {
        if (_plotControl != null && !_plotControl.isDisposed())
        {
          JFreeChart c = _plotControl.getChart();
          if (c != null)
          {
            c.setNotify(true);
          }
        }
      }
    };
    if (Display.getCurrent() != null)
    {
      runnable.run();
    }
    else
    {
      Display.getDefault().syncExec(runnable);
    }
  }

  private class ShowSymbolAction extends Action
  {

    private static final String SYMBOL_ON = "icons/16/symbol_on.png";
    private static final String SYMBOL_OFF = "icons/16/symbol_off.png";
    private boolean showSymbols;

    public ShowSymbolAction()
    {
      super();
      showSymbols = false;
      setText("Show symbols");
      setToolTipText("Show symbols");
      setImageDescriptor(CorePlugin.getImageDescriptor(SYMBOL_OFF));
    }

    @Override
    public void run()
    {
      if (_thePlotArea != null)
      {
        if (!_thePlotArea.isShowSymbols())
        {
          _thePlotArea.setShowSymbols(true);
          setText("Hide symbols");
          setToolTipText("Hide symbols");
          setImageDescriptor(CorePlugin.getImageDescriptor(SYMBOL_ON));
        }
        else
        {
          _thePlotArea.setShowSymbols(false);
          setText("Show symbols");
          setToolTipText("Show symbols");
          setImageDescriptor(CorePlugin.getImageDescriptor(SYMBOL_OFF));
        }
        showSymbols = _thePlotArea.isShowSymbols();
      }
    }

    public void updateAction()
    {
      if (_thePlotArea != null)
      {
        if (_thePlotArea.isShowSymbols())
        {
          setText("Hide symbols");
          setToolTipText("Hide symbols");
          setImageDescriptor(CorePlugin.getImageDescriptor(SYMBOL_ON));
        }
        else
        {
          setText("Show symbols");
          setToolTipText("Show symbols");
          setImageDescriptor(CorePlugin.getImageDescriptor(SYMBOL_OFF));
        }
        showSymbols = _thePlotArea.isShowSymbols();
      }
    }

    public boolean isShowSymbols()
    {
      return showSymbols;
    }
  }
}
