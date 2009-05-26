/* =======================================
* JFreeChart : a Java Chart Class Library
* =======================================
*
* Project Info:  http://www.object-refinery.com/jfreechart/index.html
* Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
*
* (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
*
* This library is free software; you can redistribute it and/or modify it under the terms
* of the GNU Lesser General Public License as published by the Free Software Foundation;
* either version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
* Boston, MA 02111-1307, USA.
*
* -----------------------
* HorizontalDateAxis.java
* -----------------------
* (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
*
* Original Author:  David Gilbert (for Simba Management Limited);
* Contributor(s):   David Li;
*                   Jonathan Nash;
*
* $Id: HorizontalDateAxis.java,v 1.3 2007/01/04 16:32:07 ian.mayo Exp $
*
* Changes (from 23-Jun-2001)
* --------------------------
* 23-Jun-2001 : Modified to work with null data source (DG);
* 18-Sep-2001 : Updated header (DG);
* 07-Nov-2001 : Updated configure() method (DG);
* 30-Nov-2001 : Cleaned up default values in constructor (DG);
* 12-Dec-2001 : Grid lines bug fix (DG);
* 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
* 20-Feb-2002 : Modified x-coordinate for vertical tick labels (DG);
* 25-Feb-2002 : Updated import statements (DG);
* 19-Apr-2002 : Added facility to set axis visibility on or off.  Also drawVerticalString(...) is
*               now drawRotatedString(...) in RefineryUtilities (DG);
* 22-Apr-2002 : Changed autoAdjustRange() from public to protected (DG);
* 25-Jul-2002 : Changed the auto-range calculation to use the lower and upper margin percentages,
*               which have been moved up one level from NumberAxis to ValueAxis (DG);
* 05-Aug-2002 : Modified check for fit of tick labels to take into account the insets (DG);
* 03-Sep-2002 : Added check for null label in reserveAxisArea method, suggested by Achilleus
*               Mantzios (DG);
* 05-Sep-2002 : Updated constructor to reflect changes in the Axis class, and changed the draw
*               method to observe tickMarkPaint (DG);
* 19-Sep-2002 : Fixed errors reported by Checkstyle (DG);
* 04-Oct-2002 : Changed auto tick mechanism to parallel that used by the number axis classes (DG);
*
*/

package MWC.GUI.JFreeChart;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;

import com.jrefinery.legacy.chart.HorizontalCategoryPlot;

/**
 * A horizontal axis that displays date values.
 * <P>
 * Used in XY plots where the x-values in the dataset are interpreted as milliseconds, encoded in
 * the same way as java.util.Date.
 * <P>
 * You can also use this axis as the range axis in a HorizontalCategoryPlot.
 *
 * @see XYPlot
 * @see HorizontalCategoryPlot
 *
 * @author DG
 */
public class RelativeDateAxis extends DateAxis implements CanBeRelativeToTimeStepper{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** A flag indicating whether or not tick labels are drawn vertically. */
 // private boolean verticalTickLabels;

  /** whether we are working with relative DTGs (affects label plotting)
   *
   */
  private boolean _relativeTimes = false;

  /** whether we are working with relative times
   *
   * @return
   */
  public boolean isRelativeTimes()
  {
    return _relativeTimes;
  }

  /** set whether we are working with relative times
   *
   * @param relativeTimes
   */
  public void setRelativeTimes(boolean relativeTimes)
  {
    _relativeTimes = relativeTimes;
  }

//  /**
//   * Returns true if the tick labels should be rotated to vertical, and false
//   * for standard horizontal labels.
//   *
//   * @return a flag indicating the orientation of the tick labels.
//   */
//  public boolean getVerticalTickLabels() {
//    return this.verticalTickLabels;
//  }
//
//  /**
//   * Sets the flag that determines the orientation of the tick labels.
//   * <P>
//   * Registered listeners are notified that the axis has been changed.
//   *
//   * @param flag  the flag.
//   */
//  public void setVerticalTickLabels(boolean flag) {
//    this.verticalTickLabels = flag;
//    this.notifyListeners(new AxisChangeEvent(this));
//  }



  /**
   * Translates a date to Java2D coordinates, based on the range displayed by
   * this axis for the specified data area.
   *
   * @param date  the date.
   * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
   *
   * @return the horizontal coordinate corresponding to the supplied date.
   */
//  public double translateDateToJava2D(Date date, Rectangle2D dataArea) {
//
//    Range range = getRange();
//    double value = (double) date.getTime();
//    double axisMin = range.getLowerBound();
//    double axisMax = range.getUpperBound();
//    double plotX = dataArea.getX();
//    double plotMaxX = dataArea.getMaxX();
//    return plotX + ((value - axisMin) / (axisMax - axisMin)) * (plotMaxX - plotX);
//
//  }

//  /**
//   * Translates the data value to the display coordinates (Java 2D User Space)
//   * of the chart.
//   *
//   * @param value  the date to be plotted.
//   * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
//   *
//   * @return the horizontal coordinate corresponding to the supplied data value.
//   */
//  public double translateValueToJava2D(double value, Rectangle2D dataArea) {
//    Range range = getRange();
//
//    double axisMin = range.getLowerBound();
//    double axisMax = range.getUpperBound();
//    double plotX = dataArea.getX();
//    double plotMaxX = dataArea.getMaxX();
//    return plotX + ((value - axisMin) / (axisMax - axisMin)) * (plotMaxX - plotX);
//
//  }

//  /**
//   * Translates the Java2D (horizontal) coordinate back to the corresponding
//   * data value.
//   *
//   * @param java2DValue  the coordinate in Java2D space.
//   * @param dataArea  the rectangle (in Java2D space) where the data is to be plotted.
//   *
//   * @return the data value corresponding to the Java2D coordinate.
//   */
//  public double translateJava2DtoValue(float java2DValue, Rectangle2D dataArea) {
//
//    Range range = getRange();
//    double axisMin = range.getLowerBound();
//    double axisMax = range.getUpperBound();
//    double plotX = dataArea.getX();
//    double plotMaxX = dataArea.getMaxX();
//    double result = axisMin
//      + ((java2DValue - plotX) / (plotMaxX - plotX) * (axisMax - axisMin));
//    return result;
//
//  }

//  /**
//   * Rescales the axis to ensure that all data is visible.
//   */
//  protected void autoAdjustRange() {
//
//    if (plot == null) {
//      return;  // no plot, no data
//    }
//
//    if (plot instanceof HorizontalValuePlot) {
//      HorizontalValuePlot hvp = (HorizontalValuePlot) plot;
//
//      Range r = hvp.getHorizontalDataRange();
//      if (r == null) {
//        r = new DateRange();
//      }
//
//      long upper = (long) r.getUpperBound();
//      long lower;
//      long fixedAutoRange = (long) getFixedAutoRange();
//      if (fixedAutoRange > 0.0) {
//        lower = upper - fixedAutoRange;
//      }
//      else {
//        lower = (long) r.getLowerBound();
//        double range = upper - lower;
//        long minRange = getAutoRangeMinimumSize().longValue();
//        if (range < minRange) {
//          upper = (upper + lower + minRange) / 2;
//          lower = (upper + lower - minRange) / 2;
//        }
//        upper = upper + (long) (range * getUpperMargin());
//        lower = lower - (long) (range * getLowerMargin());
//      }
//      setRangeAttribute(new DateRange(new Date(lower), new Date(upper)));
//    }
//
//  }

  /**
   * Recalculates the ticks for the date axis.
   *
   * @param g2  the graphics device.
   * @param drawArea  the area in which the axes and data are to be drawn.
   * @param plotArea  the area in which the data is to be drawn.
   *
   */
//  public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
//
//    this.ticks.clear();
//
//    g2.setFont(tickLabelFont);
//    FontRenderContext frc = g2.getFontRenderContext();
//
//    if (isAutoTickUnitSelection()) {
//      selectAutoTickUnit(g2, drawArea, plotArea);
//    }
//
//    Rectangle2D labelBounds = null;
//    DateTickUnit tickUnit = getTickUnit();
//
//    Date tickDate = calculateLowestVisibleTickValue(tickUnit);
//    Date upperDate = this.getMaximumDate();
//
//    while (tickDate.before(upperDate)) {
//      // work out the value, label and position
//      double xx = translateDateToJava2D(tickDate, plotArea);
//
//      // ok, what's the value
//      String tickLabel = null;
//      if(isRelativeTimes())
//      {
//        long thisTime = tickDate.getTime();
//
//        // ok, produce the date in absolute values
//        tickLabel = tickUnit.dateToString(new Date(Math.abs(thisTime)));
//
//        // is it +ve?
//        if(thisTime > 0)
//        {
//          tickLabel = "T+" + tickLabel;
//        }
//        else if(thisTime < 0)
//        {
//          tickLabel = "T-" + tickLabel;
//        }
//        else
//          tickLabel = "T-Zero";
//      }
//      else
//      {
//        tickLabel = tickUnit.dateToString(tickDate);
//      }
//
//      labelBounds = tickLabelFont.getStringBounds(tickLabel, g2.getFontRenderContext());
//      LineMetrics metrics = tickLabelFont.getLineMetrics(tickLabel, frc);
//      float x = 0.0f;
//      float y = 0.0f;
//      if (this.verticalTickLabels) {
//        x = (float) (xx + labelBounds.getHeight() / 2 - metrics.getDescent());
//        y = (float) (plotArea.getMaxY() + tickLabelInsets.top
//          + labelBounds.getWidth());
//      }
//      else {
//        x = (float) (xx - labelBounds.getWidth() / 2);
//        y = (float) (plotArea.getMaxY() + tickLabelInsets.top
//          + labelBounds.getHeight());
//      }
//      Tick tick = new Tick(tickDate, tickLabel, x, y);
//      ticks.add(tick);
//      tickDate = tickUnit.addToDate(tickDate);
//    }
//
//  }
  
  /**
   * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
   *
   * @param g2  the graphics device.
   * @param drawArea  the area within which the chart should be drawn.
   * @param plotArea  the area within which the plot should be drawn (a subset of the drawArea).
   */
//  public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
//  	
//    if (!visible) {
//      return;
//    }
//    
//    // draw the axis label
//    if (this.label != null) {
//      g2.setFont(labelFont);
//      g2.setPaint(labelPaint);
//      FontRenderContext frc = g2.getFontRenderContext();
//      Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
//      LineMetrics lm = labelFont.getLineMetrics(label, frc);
//      float labelx
//        = (float) (plotArea.getX() + plotArea.getWidth() / 2 - labelBounds.getWidth() / 2);
//      float labely = (float) (drawArea.getMaxY() - labelInsets.bottom
//        - lm.getDescent()
//        - lm.getLeading());
//      g2.drawString(label, labelx, labely);
//    }
//
//    // draw the tick labels and marks
//    this.refreshTicks(g2, drawArea, plotArea);
//    float maxY = (float) plotArea.getMaxY();
//    g2.setFont(getTickLabelFont());
//
//    Iterator<Tick> iterator = ticks.iterator();
//    while (iterator.hasNext()) {
//      Tick tick = (Tick) iterator.next();
//      float xx = (float) translateValueToJava2D(tick.getNumericalValue(), plotArea);
//
//      if (tickLabelsVisible) {
//        g2.setPaint(this.tickLabelPaint);
//        if (this.verticalTickLabels) {
//          RefineryUtilities.drawRotatedString(tick.getText(), g2,
//                                              tick.getX(), tick.getY(), -Math.PI / 2);
//        }
//        else {
//          g2.drawString(tick.getText(), tick.getX(), tick.getY());
//        }
//      }
//
//      if (this.tickMarksVisible) {
//        g2.setStroke(getTickMarkStroke());
//        g2.setPaint(getTickMarkPaint());
//        Line2D mark = new Line2D.Float(xx, maxY - 2, xx, maxY + 2);
//        g2.draw(mark);
//      }
//
//      if (isGridLinesVisible()) {
//        g2.setStroke(getGridStroke());
//        g2.setPaint(getGridPaint());
//        Line2D gridline = new Line2D.Float(xx, (float) plotArea.getMaxY(),
//                                           xx, (float) plotArea.getMinY());
//        g2.draw(gridline);
//      }
//
//    }
//
//  }

  /**
   * Returns the height required to draw the axis in the specified draw area.
   *
   * @param g2  the graphics device.
   * @param plot1  the plot that the axis belongs to.
   * @param drawArea  the area within which the plot should be drawn.
   *
   * @return the height.
   */
//  public double reserveHeight(Graphics2D g2, Plot plot1, Rectangle2D drawArea) {
//
//    if (!visible) {
//      return 0.0;
//    }
//
//    // calculate the height of the axis label...
//    double labelHeight = 0.0;
//    if (label != null) {
//      LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
//      labelHeight = this.labelInsets.top + metrics.getHeight() + this.labelInsets.bottom;
//    }
//
//    // calculate the height required for the tick labels (if visible);
//    double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
//    if (tickLabelsVisible) {
//      g2.setFont(tickLabelFont);
//      this.refreshTicks(g2, drawArea, drawArea);
//      tickLabelHeight = tickLabelHeight
//        + getMaxTickLabelHeight(g2, drawArea, this.verticalTickLabels);
//    }
//    return labelHeight + tickLabelHeight;
//
//  }

  /**
   * Returns area in which the axis will be displayed.
   *
   * @param g2  the graphics device.
   * @param plot1  the plot.
   * @param drawArea  the drawing area.
   * @param reservedWidth  the width already reserved for the vertical axis.
   *
//   * @return the area to reserve for the horizontal axis.
//   */
//  public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot1,
//                                     Rectangle2D drawArea, double reservedWidth) {
//
//    if (!visible) {
//      return new Rectangle2D.Double(drawArea.getX(), drawArea.getMaxY(),
//                                    drawArea.getWidth() - reservedWidth,
//                                    0.0);
//    }
//
//    // calculate the height of the axis label...
//    double labelHeight = 0.0;
//    if (this.label != null) {
//      LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
//      labelHeight = metrics.getHeight();
//      if (this.labelInsets != null) {
//        labelHeight += this.labelInsets.top + this.labelInsets.bottom;
//      }
//    }
//
//    // calculate the height required for the tick labels (if visible);
//    double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
//    if (tickLabelsVisible) {
//      g2.setFont(tickLabelFont);
//      refreshTicks(g2, drawArea, drawArea);
//      tickLabelHeight += getMaxTickLabelHeight(g2, drawArea, this.verticalTickLabels);
//    }
//
//    return new Rectangle2D.Double(drawArea.getX(), drawArea.getMaxY(),
//                                  drawArea.getWidth() - reservedWidth,
//                                  labelHeight + tickLabelHeight);
//
//  }

  /**
   * Selects an appropriate tick value for the axis.  The strategy is to
   * display as many ticks as possible (selected from an array of 'standard'
   * tick units) without the labels overlapping.
   *
   * @param g2  the graphics device.
   * @param drawArea  the area in which the plot and axes should be drawn.
   * @param dataArea  the area defined by the axes.
   */
//  private void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea) {
//
//    double zero = translateValueToJava2D(0.0, dataArea);
//    double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());
//
//    // start with the current tick unit...
//    TickUnits tickUnits = getStandardTickUnits();
//    TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
//    double x1 = translateValueToJava2D(unit1.getSize(), dataArea);
//    double unit1Width = Math.abs(x1 - zero);
//
//    // then extrapolate...
//    double guess = (tickLabelWidth / unit1Width) * unit1.getSize();
//
//    DateTickUnit unit2 = (DateTickUnit) tickUnits.getCeilingTickUnit(guess);
//    double x2 = translateValueToJava2D(unit2.getSize(), dataArea);
//    double unit2Width = Math.abs(x2 - zero);
//
//    tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
//    if (tickLabelWidth > unit2Width) {
//      unit2 = (DateTickUnit) tickUnits.getLargerTickUnit(unit2);
//    }
//
//    setTickUnitAttribute(unit2);
//
//
//  }


  /**
   * Estimates the maximum width of the tick labels, assuming the specified tick unit is used.
   * <P>
   * Rather than computing the string bounds of every tick on the axis, we just look at two
   * values: the lower bound and the upper bound for the axis.  These two values will usually
   * be representative.
   *
   * @param g2  the graphics device.
   * @param tickUnit  the tick unit to use for calculation.
   *
   * @return the estimated maximum width of the tick labels.
   */
//  private double estimateMaximumTickLabelWidth(Graphics2D g2, DateTickUnit tickUnit) {
//
//    double result = this.tickLabelInsets.left + this.tickLabelInsets.right;
//
//    FontRenderContext frc = g2.getFontRenderContext();
//    if (this.verticalTickLabels) {
//      // all tick labels have the same width (equal to the height of the font)...
//      result += tickLabelFont.getStringBounds("1-Jan-2002", frc).getHeight();
//    }
//    else {
//      // look at lower and upper bounds...
//      DateRange range = (DateRange) getRange();
//      Date lower = range.getLowerDate();
//      Date upper = range.getUpperDate();
//      String lowerStr = tickUnit.dateToString(lower);
//      String upperStr = tickUnit.dateToString(upper);
//      double w1 = tickLabelFont.getStringBounds(lowerStr, frc).getWidth();
//      double w2 = tickLabelFont.getStringBounds(upperStr, frc).getWidth();
//      result += Math.max(w1, w2);
//    }
//
//    return result;
//
//  }

  /**
   * A utility method for determining the height of the tallest tick label.
   *
   * @param g2  the graphics device.
   * @param drawArea  the drawing area.
   * @param vertical  a flag indicating whether or not the tick labels are rotated to vertical.
   *
   * @return the maximum tick label height.
   */
//  private double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
//
//    Font font = getTickLabelFont();
//    g2.setFont(font);
//    FontRenderContext frc = g2.getFontRenderContext();
//    double maxHeight = 0.0;
//    if (vertical) {
//      Iterator<Tick> iterator = this.ticks.iterator();
//      while (iterator.hasNext()) {
//        Tick tick = (Tick) iterator.next();
//        Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
//        if (labelBounds.getWidth() > maxHeight) {
//          maxHeight = labelBounds.getWidth();
//        }
//      }
//    }
//    else {
//      LineMetrics metrics = font.getLineMetrics("Sample", frc);
//      maxHeight = metrics.getHeight();
//    }
//    return maxHeight;
//
//  }

  /**
   * Returns true if the specified plot is compatible with the axis.
   * <p>
   * The HorizontalDateAxis class expects the plot to implement the
   * HorizontalValuePlot interface.
   *
   * @param plot1  the plot.
   *
   * @return a flag indicating whether or not the plot is compatible with the axis.
   */
//  protected boolean isCompatiblePlot(Plot plot1) {
//    if (plot1 instanceof HorizontalValuePlot) {
//      return true;
//    }
//    else {
//      return false;
//    }
//  }

}