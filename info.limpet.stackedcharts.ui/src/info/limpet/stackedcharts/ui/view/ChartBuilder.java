package info.limpet.stackedcharts.ui.view;

import info.limpet.stackedcharts.model.AbstractAnnotation;
import info.limpet.stackedcharts.model.AngleAxis;
import info.limpet.stackedcharts.model.AxisDirection;
import info.limpet.stackedcharts.model.AxisType;
import info.limpet.stackedcharts.model.Chart;
import info.limpet.stackedcharts.model.ChartSet;
import info.limpet.stackedcharts.model.DataItem;
import info.limpet.stackedcharts.model.Dataset;
import info.limpet.stackedcharts.model.Datum;
import info.limpet.stackedcharts.model.DependentAxis;
import info.limpet.stackedcharts.model.IndependentAxis;
import info.limpet.stackedcharts.model.LineType;
import info.limpet.stackedcharts.model.MarkerStyle;
import info.limpet.stackedcharts.model.Orientation;
import info.limpet.stackedcharts.model.PlainStyling;
import info.limpet.stackedcharts.model.SelectiveAnnotation;
import info.limpet.stackedcharts.model.Styling;
import info.limpet.stackedcharts.ui.view.StackedChartsView.ControllableDate;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.LogTick;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.ValueTick;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueAxisPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.AttrStringUtils;
import org.jfree.data.Range;
import org.jfree.data.general.Series;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.text.TextUtilities;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.ShapeUtilities;

public class ChartBuilder
{

  /**
   * helper class that can handle either temporal or non-temporal datasets
   * 
   * @author ian
   * 
   */
  private static interface ChartHelper
  {
    /**
     * add this item to this series
     * 
     * @param series
     * @param item
     */
    void addItem(Series series, DataItem item);

    /**
     * clear the contents of the series
     * 
     */
    void clear(Series series);

    /**
     * create the correct type of axis
     * 
     * @param name
     * @return
     */
    ValueAxis createAxis(String name);

    /**
     * create a new collection of datasets (seroes)
     * 
     * @return
     */
    XYDataset createCollection();

    /**
     * create a series with the specified name
     * 
     * @param name
     * @return
     */
    Series createSeries(String name);

    /**
     * put the series into the collection
     * 
     * @param collection
     * @param series
     */
    void storeSeries(XYDataset collection, Series series);
  }

  /**
   * support generation of a stacked chart with a shared time axis
   * 
   * @author ian
   * 
   */
  private static class DateHelper implements ChartHelper
  {

    @Override
    public void addItem(final Series series, final DataItem item)
    {
      final TimeSeries ns = (TimeSeries) series;
      final long time = (long) item.getIndependentVal();
      final Millisecond milli = new Millisecond(new Date(time));
      ns.addOrUpdate(milli, item.getDependentVal());
    }

    @Override
    public void clear(final Series series)
    {
      final TimeSeries ns = (TimeSeries) series;
      ns.clear();
    }

    @Override
    public ValueAxis createAxis(final String name)
    {
      return new DateAxis(name);
    }

    @Override
    public XYDataset createCollection()
    {
      return new TimeSeriesCollection();
    }

    @Override
    public Series createSeries(final String name)
    {
      return new TimeSeries(name);
    }

    @Override
    public void storeSeries(final XYDataset collection, final Series series)
    {
      final TimeSeriesCollection cc = (TimeSeriesCollection) collection;
      cc.addSeries((TimeSeries) series);
    }
  }

  /**
   * support generation of a stacked chart with a shared number axis
   * 
   * @author ian
   * 
   */
  private static class NumberHelper implements ChartHelper
  {

    @Override
    public void addItem(final Series series, final DataItem item)
    {
      final XYSeries ns = (XYSeries) series;
      ns.add(item.getIndependentVal(), item.getDependentVal());
    }

    @Override
    public void clear(final Series series)
    {
      final XYSeries ns = (XYSeries) series;
      ns.clear();
    }

    @Override
    public ValueAxis createAxis(final String name)
    {
      return new NumberAxis(name);
    }

    @Override
    public XYDataset createCollection()
    {
      return new XYSeriesCollection();
    }

    @Override
    public Series createSeries(final String name)
    {
      return new XYSeries(name);
    }

    @Override
    public void storeSeries(final XYDataset collection, final Series series)
    {
      final XYSeriesCollection cc = (XYSeriesCollection) collection;
      cc.addSeries((XYSeries) series);
    }

  }

  /**
   * 
   * @param subplot
   *          target plot for annotation
   * @param annotations
   *          annotation list to be added to plot eg: Marker,Zone
   * @param isRangeAnnotation
   *          is annotation added to Range or Domain
   */

  private static void addAnnotationToPlot(final XYPlot subplot,
      final List<AbstractAnnotation> annotations,
      final boolean isRangeAnnotation)
  {
    for (final AbstractAnnotation annotation : annotations)
    {
      final Color color = annotation.getColor();

      if (annotation instanceof info.limpet.stackedcharts.model.Marker)
      {
        // build value Marker
        final info.limpet.stackedcharts.model.Marker marker =
            (info.limpet.stackedcharts.model.Marker) annotation;

        final Marker mrk = new ValueMarker(marker.getValue());
        mrk.setLabel(annotation.getName());

        mrk.setPaint(color == null ? Color.GRAY : color);

        // move Text Anchor
        mrk.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
        mrk.setLabelAnchor(RectangleAnchor.TOP);

        mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));
        if (isRangeAnnotation)
        {
          subplot.addRangeMarker(mrk, Layer.FOREGROUND);
        }
        else
        {
          subplot.addDomainMarker(mrk, Layer.FOREGROUND);
        }
      }
      else if (annotation instanceof info.limpet.stackedcharts.model.Zone)
      {
        // build Zone
        final info.limpet.stackedcharts.model.Zone zone =
            (info.limpet.stackedcharts.model.Zone) annotation;

        final Marker mrk = new IntervalMarker(zone.getStart(), zone.getEnd());
        mrk.setLabel(annotation.getName());

        if (color != null)
        {
          mrk.setPaint(color);
        }

        // move Text & Label Anchor
        mrk.setLabelTextAnchor(TextAnchor.CENTER);
        mrk.setLabelAnchor(RectangleAnchor.CENTER);
        mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));

        if (isRangeAnnotation)
        {
          subplot.addRangeMarker(mrk, Layer.FOREGROUND);
        }
        else
        {
          subplot.addDomainMarker(mrk, Layer.FOREGROUND);
        }
      }
      else if (annotation instanceof info.limpet.stackedcharts.model.ScatterSet)
      {
        // build ScatterSet
        final info.limpet.stackedcharts.model.ScatterSet marker =
            (info.limpet.stackedcharts.model.ScatterSet) annotation;

        final EList<Datum> datums = marker.getDatums();
        boolean addLabel = true;
        for (final Datum datum : datums)
        {
          final Marker mrk = new ValueMarker(datum.getVal());
          // only add label for first Marker
          if (addLabel)
          {
            mrk.setLabel(annotation.getName());
            addLabel = false;
          }

          final Color thisColor = datum.getColor();
          final Color colorToUse = thisColor == null ? color : thisColor;

          // apply some transparency to the color
          if (colorToUse != null)
          {
            final Color transColor =
                new Color(colorToUse.getRed(), colorToUse.getGreen(),
                    colorToUse.getBlue(), 120);

            mrk.setPaint(transColor);
          }

          // move Text Anchor
          mrk.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
          mrk.setLabelAnchor(RectangleAnchor.TOP);

          mrk.setLabelOffset(new RectangleInsets(2, 2, 2, 2));
          if (isRangeAnnotation)
          {
            subplot.addRangeMarker(mrk, Layer.FOREGROUND);
          }
          else
          {
            subplot.addDomainMarker(mrk, Layer.FOREGROUND);
          }
        }

      }

    }

  }

  /**
   * 
   * @param helper
   *          Helper object to map between axis types
   * @param datasets
   *          list of datasets to add to this axis
   * @param collection
   *          XYDataset need to be fill with Series
   * @param renderer
   *          axis renderer
   * @param seriesIndex
   *          counter for current series being assigned
   * 
   *          build axis dataset via adding series to Series & config renderer for styles
   */
  private static void addDatasetToAxis(final ChartHelper helper,
      final EList<Dataset> datasets, final XYDataset collection,
      final XYLineAndShapeRenderer renderer, int seriesIndex)
  {
    for (final Dataset dataset : datasets)
    {
      final Series series = helper.createSeries(dataset.getName());
      final Styling styling = dataset.getStyling();
      if (styling != null)
      {
        if (styling instanceof PlainStyling)
        {
          final PlainStyling ps = (PlainStyling) styling;
          renderer.setSeriesPaint(seriesIndex, ps.getColor());
        }
        else
        {
          System.err.println("Linear colors not implemented");
        }

        // legend visibility
        final boolean isInLegend = styling.isIncludeInLegend();
        renderer.setSeriesVisibleInLegend(seriesIndex, isInLegend);

        // line thickness
        // line style
        final LineType lineType = styling.getLineStyle();
        if (lineType != null)
        {
          final float thickness = (float) styling.getLineThickness();
          Stroke stroke;
          float[] pattern;
          switch (lineType)
          {
          case NONE:
            renderer.setSeriesLinesVisible(seriesIndex, false);
            break;
          case SOLID:
            renderer.setSeriesLinesVisible(seriesIndex, true);
            stroke = new BasicStroke(thickness);
            renderer.setSeriesStroke(seriesIndex, stroke);
            break;
          case DOTTED:
            renderer.setSeriesLinesVisible(seriesIndex, true);
            pattern = new float[]
            {3f, 3f};
            stroke =
                new BasicStroke(thickness, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, pattern, 0);
            renderer.setSeriesStroke(seriesIndex, stroke);
            break;
          case DASHED:
            renderer.setSeriesLinesVisible(seriesIndex, true);
            pattern = new float[]
            {8.0f, 4.0f};
            stroke =
                new BasicStroke(thickness, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, pattern, 0);
            renderer.setSeriesStroke(seriesIndex, stroke);
            break;
          }
        }

        // marker size
        double size = styling.getMarkerSize();
        if (size == 0)
        {
          size = 2;// default
        }

        // marker style
        final MarkerStyle marker = styling.getMarkerStyle();
        if (marker != null)
        {
          switch (marker)
          {
          case NONE:
            renderer.setSeriesShapesVisible(seriesIndex, false);
            break;
          case SQUARE:
            renderer.setSeriesShape(seriesIndex, new Rectangle2D.Double(0, 0,
                size, size));
            break;
          case CIRCLE:
            renderer.setSeriesShape(seriesIndex, new Ellipse2D.Double(0, 0,
                size, size));
            break;
          case TRIANGLE:
            renderer.setSeriesShape(seriesIndex, ShapeUtilities
                .createUpTriangle((float) size));
            break;
          case CROSS:
            renderer.setSeriesShape(seriesIndex, ShapeUtilities
                .createRegularCross((float) size, (float) size));
            break;
          case DIAMOND:
            renderer.setSeriesShape(seriesIndex, ShapeUtilities
                .createDiamond((float) size));
            break;
          default:
            renderer.setSeriesShapesVisible(seriesIndex, false);
          }
        }
        seriesIndex++;
      }
      helper.storeSeries(collection, series);

      // also register as a listener
      final Adapter adapter = new AdapterImpl()
      {
        @Override
        public void notifyChanged(final Notification notification)
        {
          // switch off chart refreshes, we don't
          // want to do it for every new data point
          series.setNotify(false);
          populateCollection(helper, dataset, series);
          series.setNotify(true);
          series.fireSeriesChanged();
        }
      };
      dataset.eAdapters().add(adapter);

      // perform the initial population
      adapter.notifyChanged(null);

    }
  }

  /**
   * create a chart from chart object for preview
   * 
   * @param chart
   * 
   * @return
   */
  public static JFreeChart build(final Chart chart)
  {

    final IndependentAxis sharedAxisModel = chart.getParent().getSharedAxis();
    final ChartHelper helper;
    final ValueAxis sharedAxis;

    if (sharedAxisModel == null)
    {
      sharedAxis = new DateAxis("Time");
      helper = new NumberHelper();
    }
    else
    {
      final AxisType axisType = sharedAxisModel.getAxisType();
      if (axisType instanceof info.limpet.stackedcharts.model.NumberAxis)
      {
        helper = new NumberHelper();
      }
      else if (axisType instanceof info.limpet.stackedcharts.model.AngleAxis)
      {
        helper = new NumberHelper();
      }
      else if (axisType instanceof info.limpet.stackedcharts.model.DateAxis)
      {
        helper = new DateHelper();
      }
      else
      {
        System.err.println("UNEXPECTED AXIS TYPE RECEIVED");
        helper = new NumberHelper();
      }
      sharedAxis = helper.createAxis(sharedAxisModel.getName());
      if (sharedAxisModel.getDirection() == AxisDirection.DESCENDING)
      {
        sharedAxis.setInverted(true);
      }
    }
    sharedAxis.setVisible(false);
    final CombinedDomainXYPlot plot = new TimeBarPlot(sharedAxis);

    // create this chart
    final XYPlot subplot = createChart(sharedAxisModel, chart, helper);
    
    // hide the y axis labels - since our GEF editors show the value
    int aCnt = subplot.getRangeAxisCount();
    for(int ctr = 0; ctr < aCnt; ctr++)
    {
      ValueAxis axis = subplot.getRangeAxis(ctr);
      if (axis != null) {
        axis.setLabel(null);
      }
    }

    // add chart to stack
    plot.add(subplot);

    plot.setOrientation(chart.getParent()
        .getOrientation() == Orientation.VERTICAL ? PlotOrientation.VERTICAL
            : PlotOrientation.HORIZONTAL);

    final JFreeChart jFreeChart = new JFreeChart(plot);
    jFreeChart.getLegend().setVisible(false);
    return jFreeChart;
  }

  /**
   * create a chart from our dataset
   * 
   * @param chartsSet
   * @param controllableDate
   * 
   * @return
   */
  public static JFreeChart build(final ChartSet chartsSet,
      ControllableDate controllableDate)
  {
    final IndependentAxis sharedAxisModel = chartsSet.getSharedAxis();
    final ChartHelper helper;
    final ValueAxis sharedAxis;

    if (sharedAxisModel == null)
    {
      sharedAxis = new DateAxis("Time");
      helper = new NumberHelper();
    }
    else
    {
      final AxisType axisType = sharedAxisModel.getAxisType();
      if (axisType instanceof info.limpet.stackedcharts.model.NumberAxis)
      {
        helper = new NumberHelper();
      }
      else if (axisType instanceof info.limpet.stackedcharts.model.AngleAxis)
      {
        helper = new NumberHelper();
      }
      else if (axisType instanceof info.limpet.stackedcharts.model.DateAxis)
      {
        helper = new DateHelper();
      }
      else
      {
        System.err.println("UNEXPECTED AXIS TYPE RECEIVED");
        helper = new NumberHelper();
      }
      sharedAxis = helper.createAxis(sharedAxisModel.getName());
      if (sharedAxisModel.getDirection() == AxisDirection.DESCENDING)
      {
        sharedAxis.setInverted(true);
      }
    }

    final TimeBarPlot plot = new TimeBarPlot(sharedAxis);

    // now loop through the charts
    final EList<Chart> charts = chartsSet.getCharts();
    for (final Chart chart : charts)
    {
      // create this chart
      final XYPlot subplot = createChart(sharedAxisModel, chart, helper);

      // add chart to stack
      plot.add(subplot);
    }

    // do we know a date?
    if (controllableDate != null)
    {
      // ok, initialise it
      Date theTime = controllableDate.getDate();
      if (theTime != null)
      {
        plot.setTime(theTime);
      }
    }

    plot.setGap(5.0);
    plot.setOrientation(chartsSet.getOrientation() == Orientation.VERTICAL
        ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL);

    return new JFreeChart(plot);
  }

  protected static XYPlot createChart(final IndependentAxis sharedAxisModel,
      final Chart chart, ChartHelper helper)
  {
    final XYPlot subplot = new XYPlot(null, null, null, null);

    // do some basic plot formatting
    BasicStroke stroke =
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
            new float[]
            {8.0f, 2.0f}, 0);
    subplot.setDomainGridlineStroke(stroke);
    subplot.setRangeGridlineStroke(stroke);

    // keep track of how many axes we create
    int indexAxis = 0;

    // min axis create on bottom or left
    final EList<DependentAxis> minAxes = chart.getMinAxes();
    for (final DependentAxis axis : minAxes)
    {
      createDependentAxis(subplot, indexAxis, axis, helper);
      subplot.setRangeAxisLocation(indexAxis, AxisLocation.BOTTOM_OR_LEFT);
      indexAxis++;
    }

    // max axis create on top or right
    final EList<DependentAxis> maxAxes = chart.getMaxAxes();
    for (final DependentAxis axis : maxAxes)
    {
      createDependentAxis(subplot, indexAxis, axis, helper);
      subplot.setRangeAxisLocation(indexAxis, AxisLocation.TOP_OR_RIGHT);
      indexAxis++;
    }

    if (sharedAxisModel != null)
    {
      // build selective annotations to plot
      final EList<SelectiveAnnotation> selectiveAnnotations =
          sharedAxisModel.getAnnotations();

      final List<AbstractAnnotation> annotations = new ArrayList<>();
      for (final SelectiveAnnotation selectiveAnnotation : selectiveAnnotations)
      {
        final EList<Chart> appearsIn = selectiveAnnotation.getAppearsIn();
        // check selective option to see is this applicable to current
        // chart
        if (appearsIn == null || appearsIn.isEmpty()
            || appearsIn.contains(chart))
        {
          annotations.add(selectiveAnnotation.getAnnotation());
        }
      }
      addAnnotationToPlot(subplot, annotations, false);
    }

    return subplot;
  }

  /**
   * 
   * @param subplot
   *          target plot for index
   * @param indexAxis
   *          index of new axis
   * @param dependentAxis
   *          model object of DependentAxis
   */
  private static void createDependentAxis(final XYPlot subplot,
      final int indexAxis, final DependentAxis dependentAxis,
      ChartHelper axeshelper)
  {
    final XYLineAndShapeRenderer renderer;

    // is this a special axis type
    final AxisType axisType = dependentAxis.getAxisType();

    // sort out the name
    final String axisName;
    if (axisType instanceof info.limpet.stackedcharts.model.NumberAxis)
    {
      info.limpet.stackedcharts.model.NumberAxis number =
          (info.limpet.stackedcharts.model.NumberAxis) axisType;
      if (number.getUnits() != null)
      {
        if (dependentAxis.getName() == number.getUnits())
        {
          axisName = dependentAxis.getName();
        }
        else
        {
          axisName = dependentAxis.getName() + " (" + number.getUnits() + ")";
        }
      }
      else
      {
        axisName = dependentAxis.getName();
      }
    }
    else
    {
      axisName = dependentAxis.getName();
    }

    final ValueAxis chartAxis;
    if (axisType instanceof AngleAxis)
    {
      AngleAxis angle = (AngleAxis) axisType;

      // ok, quick check for red-green
      if (angle.isRedGreen())
      {
        final double min = angle.getMinVal();
        final double max = angle.getMaxVal();

        // use the renderer that "jumps" across zero/360 barrier
        renderer = new WrappingRenderer(min, max);

        // use the angular axis
        chartAxis = new RedGreenAngularUnitAxis(axisName, min, max);

        // put in the reg/green background shading
        IntervalMarker greenM = new IntervalMarker(0, max);
        greenM.setPaint(Color.green);
        greenM.setStroke(new BasicStroke(3));
        greenM.setAlpha(0.03f);
        subplot.addRangeMarker(1, greenM, Layer.BACKGROUND);

        IntervalMarker im = new IntervalMarker(min, 0);
        im.setPaint(Color.red);
        im.setStroke(new BasicStroke(3));
        im.setAlpha(0.03f);
        subplot.addRangeMarker(1, im, Layer.BACKGROUND);

      }
      else
      {
        // hmm, should it have a zero centre
        final boolean midOrigin = angle.isMidOrigin();

        final double min;
        final double max;
        if (midOrigin)
        {
          final double range = angle.getMaxVal() - angle.getMinVal();
          final double demiRange = range / 2d;
          min = angle.getMinVal() - demiRange;
          max = angle.getMaxVal() - demiRange;
        }
        else
        {
          min = angle.getMinVal();
          max = angle.getMaxVal();
        }
        // use the renderer that "jumps" across zero/360 barrier
        renderer = new WrappingRenderer(min, max);

        // use the angular axis
        chartAxis = new AngularUnitAxis(axisName, min, max);
      }

    }
    else
    {
      renderer = new XYLineAndShapeRenderer();

      chartAxis = new NumberAxis(axisName);
    }

    renderer.setDrawSeriesLineAsPath(true);
    final int indexSeries = 0;

    // set the line width
    renderer.setAutoPopulateSeriesPaint(false);
    renderer.setAutoPopulateSeriesStroke(false);

    final XYDataset collection = axeshelper.createCollection();

    if (dependentAxis.getDirection() == AxisDirection.DESCENDING)
    {
      chartAxis.setInverted(true);
    }
    addDatasetToAxis(axeshelper, dependentAxis.getDatasets(), collection,
        renderer, indexSeries);

    final EList<AbstractAnnotation> annotations =
        dependentAxis.getAnnotations();
    addAnnotationToPlot(subplot, annotations, true);
    subplot.setDataset(indexAxis, collection);
    subplot.setRangeAxis(indexAxis, chartAxis);
    subplot.setRenderer(indexAxis, renderer);
    subplot.mapDatasetToRangeAxis(indexAxis, indexAxis);
  }

  protected static void populateCollection(final ChartHelper helper,
      final Dataset dataset, final Series series)
  {
    helper.clear(series);
    final EList<DataItem> measurements = dataset.getMeasurements();
    for (final DataItem dataItem : measurements)
    {
      helper.addItem(series, dataItem);
    }
  }

  /**
   * marker interface for an axis that uses fancy label formatting
   * 
   * @author ian
   * 
   */
  public static interface FancyFormattedAxis
  {

  }

  /**
   * interface for axis that provides color for specific values
   * 
   * @author ian
   * 
   */
  public static interface ColorProvider
  {
    public Color getColorFor(double val);
  }

  private static class CoreAngularAxis extends NumberAxis
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CoreAngularAxis(String axisName, final double min, final double max)
    {
      super(axisName);

      this.setDefaultAutoRange(new Range(min, max));
      this.setRange(min, max);
    }

    /**
     * when the plot is resized, we want to return to the default auto range, not the freshly
     * calculated one.
     */
    protected final void autoAdjustRange()
    {
      Plot plot = getPlot();
      if (plot != null && plot instanceof ValueAxisPlot)
      {
        Range r = getDefaultAutoRange();
        setRange(r, false, false);
      }
      else
      {
        super.autoAdjustRange();
      }
    }

    @Override
    public final NumberTickUnit getTickUnit()
    {
      final NumberTickUnit tickUnit = super.getTickUnit();
      if (tickUnit.getSize() < 15)
      {
        return tickUnit;
      }
      else if (tickUnit.getSize() < 45)
      {
        return new NumberTickUnit(30);
      }
      else if (tickUnit.getSize() < 90)
      {
        return new NumberTickUnit(45);
      }
      else if (tickUnit.getSize() < 180)
      {
        return new NumberTickUnit(90);
      }
      else
      {
        return new NumberTickUnit(180);
      }
    }
  }

  /**
   * modified version of angle axis that prefers to use angular metric units.
   * 
   * @author ian
   * 
   */
  private static class AngularUnitAxis extends CoreAngularAxis implements
      FancyFormattedAxis
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static class AnglularWrappingFormatter extends NumberFormat
    {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;
      private DecimalFormat df;
      private double range;

      public AnglularWrappingFormatter(final double range)
      {
        this.range = range;
        df = new DecimalFormat();
      }

      @Override
      public StringBuffer format(double number, StringBuffer toAppendTo,
          FieldPosition pos)
      {
        // check if we're doing a mid-origin arrangement
        if (number < 0)
        {
          number += range;
        }

        return toAppendTo.append(df.format(number));
      }

      @Override
      public StringBuffer format(long number, StringBuffer toAppendTo,
          FieldPosition pos)
      {
        throw new UnsupportedOperationException(
            "Formatter not implemented in WrappingFormatter");
      }

      @Override
      public Number parse(String source, ParsePosition parsePosition)
      {
        throw new UnsupportedOperationException(
            "Formatter not implemented in WrappingFormatter");
      }

    }

    public AngularUnitAxis(final String axisName, double min, double max)
    {
      super(axisName, min, max);

      this.setNumberFormatOverride(new AnglularWrappingFormatter(max - min));
    }

  }

  /**
   * modified version of angle axis that prefers to use angular metric units.
   * 
   * @author ian
   * 
   */
  private static class RedGreenAngularUnitAxis extends CoreAngularAxis
      implements FancyFormattedAxis
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private class RedGreenAnglularWrappingFormatter extends NumberFormat
        implements ColorProvider
    {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;
      private DecimalFormat df;

      public RedGreenAnglularWrappingFormatter()
      {
        df = new DecimalFormat();
      }

      @Override
      public StringBuffer format(double number, StringBuffer toAppendTo,
          FieldPosition pos)
      {
        // check if we're doing a mid-origin arrangement
        final String prefix;
        if (number < 0)
        {
          prefix = "R";
        }
        else if (number > 0)
        {
          prefix = "G";
        }
        else
        {
          prefix = "";
        }

        return toAppendTo.append(prefix + df.format(Math.abs(number)));
      }

      @Override
      public StringBuffer format(long number, StringBuffer toAppendTo,
          FieldPosition pos)
      {
        throw new UnsupportedOperationException(
            "Formatter not implemented in WrappingFormatter");
      }

      @Override
      public Number parse(String source, ParsePosition parsePosition)
      {
        throw new UnsupportedOperationException(
            "Formatter not implemented in WrappingFormatter");
      }

      @Override
      public Color getColorFor(double val)
      {
        final Color res;
        if (val < 0)
        {
          res = Color.red;
        }
        else if (val > 0)
        {
          res = Color.green.darker();
        }
        else
        {
          res = Color.black;
        }
        return res;
      }

    }

    private final RedGreenAnglularWrappingFormatter _formatter;

    public RedGreenAngularUnitAxis(final String axisName, double min, double max)
    {
      super(axisName, min, max);

      _formatter = new RedGreenAnglularWrappingFormatter();
      this.setNumberFormatOverride(_formatter);
    }

    /**
     * Draws the axis line, tick marks and tick mark labels.
     * 
     * @param g2
     *          the graphics device (<code>null</code> not permitted).
     * @param cursor
     *          the cursor.
     * @param plotArea
     *          the plot area (<code>null</code> not permitted).
     * @param dataArea
     *          the data area (<code>null</code> not permitted).
     * @param edge
     *          the edge that the axis is aligned with (<code>null</code> not permitted).
     * 
     * @return The width or height used to draw the axis.
     */
    protected AxisState drawTickMarksAndLabels(Graphics2D g2, double cursor,
        Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge)
    {

      AxisState state = new AxisState(cursor);
      if (isAxisLineVisible())
      {
        drawAxisLine(g2, cursor, dataArea, edge);
      }
      @SuppressWarnings("rawtypes")
      List ticks = refreshTicks(g2, state, dataArea, edge);
      state.setTicks(ticks);
      g2.setFont(getTickLabelFont());
      Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
      g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
          RenderingHints.VALUE_STROKE_NORMALIZE);
      @SuppressWarnings("rawtypes")
      Iterator iterator = ticks.iterator();
      while (iterator.hasNext())
      {
        ValueTick tick = (ValueTick) iterator.next();
        if (isTickLabelsVisible())
        {
          g2.setPaint(getTickLabelPaintFor(tick.getText()));
          float[] anchorPoint =
              calculateAnchorPoint(tick, cursor, dataArea, edge);
          if (tick instanceof LogTick)
          {
            LogTick lt = (LogTick) tick;
            if (lt.getAttributedLabel() == null)
            {
              continue;
            }
            AttrStringUtils.drawRotatedString(lt.getAttributedLabel(), g2,
                anchorPoint[0], anchorPoint[1], tick.getTextAnchor(), tick
                    .getAngle(), tick.getRotationAnchor());
          }
          else
          {
            if (tick.getText() == null)
            {
              continue;
            }
            TextUtilities.drawRotatedString(tick.getText(), g2, anchorPoint[0],
                anchorPoint[1], tick.getTextAnchor(), tick.getAngle(), tick
                    .getRotationAnchor());
          }
        }

        if ((isTickMarksVisible() && tick.getTickType().equals(TickType.MAJOR))
            || (isMinorTickMarksVisible() && tick.getTickType().equals(
                TickType.MINOR)))
        {

          double ol =
              (tick.getTickType().equals(TickType.MINOR))
                  ? getMinorTickMarkOutsideLength()
                  : getTickMarkOutsideLength();

          double il =
              (tick.getTickType().equals(TickType.MINOR))
                  ? getMinorTickMarkInsideLength() : getTickMarkInsideLength();

          float xx = (float) valueToJava2D(tick.getValue(), dataArea, edge);
          Line2D mark = null;
          g2.setStroke(getTickMarkStroke());
          g2.setPaint(getTickMarkPaint());
          if (edge == RectangleEdge.LEFT)
          {
            mark = new Line2D.Double(cursor - ol, xx, cursor + il, xx);
          }
          else if (edge == RectangleEdge.RIGHT)
          {
            mark = new Line2D.Double(cursor + ol, xx, cursor - il, xx);
          }
          else if (edge == RectangleEdge.TOP)
          {
            mark = new Line2D.Double(xx, cursor - ol, xx, cursor + il);
          }
          else if (edge == RectangleEdge.BOTTOM)
          {
            mark = new Line2D.Double(xx, cursor + ol, xx, cursor - il);
          }
          g2.draw(mark);
        }
      }
      g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);

      // need to work out the space used by the tick labels...
      // so we can update the cursor...
      double used = 0.0;
      if (isTickLabelsVisible())
      {
        if (edge == RectangleEdge.LEFT)
        {
          used +=
              findMaximumTickLabelWidth(ticks, g2, plotArea,
                  isVerticalTickLabels());
          state.cursorLeft(used);
        }
        else if (edge == RectangleEdge.RIGHT)
        {
          used =
              findMaximumTickLabelWidth(ticks, g2, plotArea,
                  isVerticalTickLabels());
          state.cursorRight(used);
        }
        else if (edge == RectangleEdge.TOP)
        {
          used =
              findMaximumTickLabelHeight(ticks, g2, plotArea,
                  isVerticalTickLabels());
          state.cursorUp(used);
        }
        else if (edge == RectangleEdge.BOTTOM)
        {
          used =
              findMaximumTickLabelHeight(ticks, g2, plotArea,
                  isVerticalTickLabels());
          state.cursorDown(used);
        }
      }

      return state;
    }

    private Paint getTickLabelPaintFor(String string)
    {
      // ok, see if we have a leading character
      double factor = 1;
      if (string.startsWith("R") || string.startsWith("G"))
      {
        if (string.startsWith("R"))
        {
          factor = -1;
        }
        string = string.substring(1);
      }
      return _formatter.getColorFor(Double.parseDouble(string) * factor);
    }

  }

  private ChartBuilder()
  {
  }
}
