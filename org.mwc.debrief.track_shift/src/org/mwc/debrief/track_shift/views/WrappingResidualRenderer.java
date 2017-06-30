package org.mwc.debrief.track_shift.views;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.chart.util.LineUtilities;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

public class WrappingResidualRenderer extends ResidualXYItemRenderer
{
  
  double min;
  double max;
  double range;
  LinearInterpolator interpolator = new LinearInterpolator();
  final OverflowCondition overflowCondition;

  public WrappingResidualRenderer(XYToolTipGenerator toolTipGenerator,
      XYURLGenerator urlGenerator, TimeSeriesCollection dataset, final double min, final double max)
  {
    super(toolTipGenerator, urlGenerator, dataset);
    
    this.min = min;
    this.max = max;
    this.range = max - min;

    overflowCondition = new OverflowCondition()
    {
      @Override
      public boolean isOverflow(double y0, double x0, double y1, double x1)
      {
        return Math.abs(y1 - y0) > 180d;
      }
    };
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  /**
   * This an immutable class for containing two values together.
   * @author aris
   */
  private static class ImmutablePair<T0, T1> {
    protected final T0 left;
    protected final T1 right;

    public ImmutablePair(T0 left, T1 right) {
      this.left = left;
      this.right = right;
    }

    /**
     * @return the left value of the pair
     */
    public  T0 getLeft() {
      return left;
    }

    /**
     * @return the right value of the pair
     */
    public T1 getRight() {
      return right;
    }
  }
  
  /**
   *
   * This class do linear interpolation
   * @author aris
   */
  private static class LinearFunction {

    private final double[] xs;
    private final double[] ys;

    /**
     * Creates a new instance of LinearFunction with the given x array and y array.
     * Please see {@code LinearInterpolator.java}
     * @param x x values.  Must be of length == 2
     * @param y y values.  Must be of length == 2
     */
    public LinearFunction(double[] x, double[] y) {
      if (x.length != 2 || y.length != 2){
        throw new IllegalArgumentException("x and y need to be an array of length == 2");
      }
      if (x[0] > x[1]){
        throw new IllegalArgumentException("x needs to be increasing value");
      }
      this.xs = x;
      this.ys = y;
    }

    public double value(double x) {
      if (x < xs[0] || x > xs[1]){
        throw new IllegalArgumentException(String.format("x needs to be between %s and %s, found %s", xs[0], xs[1], x));
      }
      return ys[0]+(ys[1]-ys[0])*(x-xs[0])/(xs[1]-xs[0]);
    }
    
  }
  
  /**
   * /**
   * An interface for creating custom logic for drawing lines between
   * points for XYLineAndShapeRenderer.
   */
  private static interface OverflowCondition {

    /**
     * Custom logic for detecting overflow between points.
     *
     * @param y0 previous y
     * @param x0 previous x
     * @param y1 current y
     * @param x1 current x
     * @return true, if you there is an overflow detected.
     * Otherwise, return false
     */
    public boolean isOverflow(double y0, double x0, double y1, double x1);
  }

  
  /**
   * This is a utility class for doing linear interpolation.
   * @author aris
   */
  private static class LinearInterpolator {

    /**
     * Creates a new instance of LinearFunction for computing linear interpolation
     * @param x x values.  Must be of length == 2
     * @param y y values.  Must be of length == 2
     * @return an instance of LinearFunction
     */
    public LinearFunction interpolate(double[] x, double[] y) {
      return new LinearFunction(x, y);
    }
    
  }


  @Override
  protected void
      drawPrimaryLineAsPath(XYItemRendererState state, Graphics2D g2,
          XYPlot plot, XYDataset dataset, int pass, int series, int item,
          ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea)
  {

    // get the data point...
    State s = (State) state;
    try
    {
      double x1 = dataset.getXValue(series, item);
      double y1 = dataset.getYValue(series, item);
      if (Double.isNaN(x1) && Double.isNaN(y1))
      {
        s.setLastPointGood(false);
        return;
      }

      if (!s.isLastPointGood())
      {
        ImmutablePair<Float, Float> xy =
            translate(plot, domainAxis, rangeAxis, dataArea, x1, y1);
        s.seriesPath.moveTo(xy.getLeft(), xy.getRight());
        s.setLastPointGood(true);
        return;
      }

      double x0 = dataset.getXValue(series, item - 1);
      double y0 = dataset.getYValue(series, item - 1);
      if (overflowCondition.isOverflow(y0, x0, y1, x1))
      {
        boolean overflowAtMax = y1 < y0;
        if (overflowAtMax)
        {
          // double check values valid (not greater than max)
          y0 = y0 > max ? y0 - range : y0;
          y1 = y1 > max ? y1 - range : y1;

          LinearFunction lf = interpolator.interpolate(new double[]{y0, y1 + (max - min)}, new double[]{x0, x1});
          double xmid = lf.value(max);
          ImmutablePair<Float, Float> xy = translate(plot, domainAxis, rangeAxis, dataArea, xmid, max);
          s.seriesPath.lineTo(xy.getLeft(), xy.getRight());
          xy = translate(plot, domainAxis, rangeAxis, dataArea, xmid, min);
          s.seriesPath.moveTo(xy.getLeft(), xy.getRight());
          xy = translate(plot, domainAxis, rangeAxis, dataArea, x1, y1);
          s.seriesPath.lineTo(xy.getLeft(), xy.getRight());
        }
        else
        {
          // double check values valid (not less than min)
          y0 = y0 < min ? y0 + range : y0;
          y1 = y1 < min ? y1 + range : y1;
          
          LinearFunction lf = interpolator.interpolate(new double[]{y1 - (max - min), y0}, new double[]{x1, x0});
          double xmid = lf.value(min);
          ImmutablePair<Float, Float> xy = translate(plot, domainAxis, rangeAxis, dataArea, xmid, min);
          s.seriesPath.lineTo(xy.getLeft(), xy.getRight());
          xy = translate(plot, domainAxis, rangeAxis, dataArea, xmid, max);
          s.seriesPath.moveTo(xy.getLeft(), xy.getRight());
          xy = translate(plot, domainAxis, rangeAxis, dataArea, x1, y1);
          s.seriesPath.lineTo(xy.getLeft(), xy.getRight());
        }
      }
      else
      {
        ImmutablePair<Float, Float> xy =
            translate(plot, domainAxis, rangeAxis, dataArea, x1, y1);
        s.seriesPath.lineTo(xy.getLeft(), xy.getRight());
      }

      s.setLastPointGood(true);
    }
    finally
    {
      // if this is the last item, draw the path ...
      if (item == s.getLastItemIndex())
      {
        // draw path
        drawFirstPassShape(g2, pass, series, item, s.seriesPath);
      }

    }
  }

  private ImmutablePair<Float, Float> translate(XYPlot plot,
      ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea,
      double x, double y)
  {
    RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
    RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
    double transX1 = domainAxis.valueToJava2D(x, dataArea, xAxisLocation);
    double transY1 = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation);
    // update path to reflect latest point
    float xtrans = (float) transX1;
    float ytrans = (float) transY1;
    PlotOrientation orientation = plot.getOrientation();
    if (orientation == PlotOrientation.HORIZONTAL)
    {
      xtrans = (float) transY1;
      ytrans = (float) transX1;
    }
    return new ImmutablePair<>(xtrans, ytrans);
  }

  @Override
  protected void drawPrimaryLine(XYItemRendererState state, Graphics2D g2,
      XYPlot plot, XYDataset dataset, int pass, int series, int item,
      ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea)
  {
    if (item == 0)
    {
      return;
    }

    // get the data point...
    double x1 = dataset.getXValue(series, item);
    double y1 = dataset.getYValue(series, item);
    if (Double.isNaN(y1) || Double.isNaN(x1))
    {
      return;
    }

    double x0 = dataset.getXValue(series, item - 1);
    double y0 = dataset.getYValue(series, item - 1);
    if (Double.isNaN(y0) || Double.isNaN(x0))
    {
      return;
    }

    if (overflowCondition.isOverflow(y0, x0, y1, x1))
    {
      boolean overflowAtMax = y1 < y0;
      if (overflowAtMax)
      {
        // double check values valid (not greater than max)
        y0 = y0 > max ? y0 - range : y0;
        y1 = y1 > max ? y1 - range : y1;

        LinearFunction lf = interpolator.interpolate(new double[]{y0, y1 + (max - min)}, new double[]{x0, x1});
        double xmid = lf.value(max);
        drawPrimaryLine(state, g2, plot, x0, y0, xmid, max, pass, series, item,
            domainAxis, rangeAxis, dataArea);
        drawPrimaryLine(state, g2, plot, xmid, min, x1, y1, pass, series, item,
            domainAxis, rangeAxis, dataArea);
      }
      else
      {
        // double check values valid (not less than min)
        y0 = y0 < min ? y0 + range : y0;
        y1 = y1 < min ? y1 + range : y1;
        
        LinearFunction lf = interpolator.interpolate(new double[]{y1 - (max - min), y0}, new double[]{x1, x0});
        double xmid = lf.value(min);
        drawPrimaryLine(state, g2, plot, x0, y0, xmid, min, pass, series, item,
            domainAxis, rangeAxis, dataArea);
        drawPrimaryLine(state, g2, plot, xmid, max, x1, y1, pass, series, item,
            domainAxis, rangeAxis, dataArea);
      }
    }
    else
    {
      drawPrimaryLine(state, g2, plot, x0, y0, x1, y1, pass, series, item,
          domainAxis, rangeAxis, dataArea);
    }

  }

  private void drawPrimaryLine(XYItemRendererState state, Graphics2D g2,
      XYPlot plot, double x0, double y0, double x1, double y1, int pass,
      int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis,
      Rectangle2D dataArea)
  {
    RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
    RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
    double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
    double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
    double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
    double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
    // only draw if we have good values
    if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1)
        || Double.isNaN(transY1))
    {
      return;
    }
    PlotOrientation orientation = plot.getOrientation();
    boolean visible;
    if (orientation == PlotOrientation.HORIZONTAL)
    {
      state.workingLine.setLine(transY0, transX0, transY1, transX1);
    }
    else if (orientation == PlotOrientation.VERTICAL)
    {
      state.workingLine.setLine(transX0, transY0, transX1, transY1);
    }
    visible = LineUtilities.clipLine(state.workingLine, dataArea);
    if (visible)
    {
      drawFirstPassShape(g2, pass, series, item, state.workingLine);
    }
  }

  /** update the wrapping ranges
   * 
   * @param minVal
   * @param maxVal
   */
  public void setRange(final double minVal, final double maxVal)
  {
    min = minVal;
    max = maxVal;
    range = max - min;
  }
}
