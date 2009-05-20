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
 * -------------------------
 * HorizontalNumberAxis.java
 * -------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jonathan Nash;
 *
 * $Id: HorizontalNumberAxis.java,v 1.3 2004/11/29 15:22:00 Ian.Mayo Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Updated configure() method (DG);
 * 21-Nov-2001 : Fixed bug on default axis range (DG);
 * 23-Nov-2001 : Overhauled auto tick unit code for all axes (DG);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Added flag to allow axis to be inverted (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 23-Jan-2002 : Fixed bugs causing null pointer exceptions when axis label is null (DG);
 * 25-Feb-2002 : Changed autoAdjustRange() from public to protected, and altered the calculation
 *               to take into account the new autoRangeStickyZero flag (DG);
 * 26-Feb-2002 : Fixed bug 523032 (an incomplete implementation) preventing inverted flag from
 *               having any effect on a HorizontalNumberAxis (DG);
 * 19-Apr-2002 : Added facility to set axis visibility on or off.  Also drawVerticalString(...) is
 *               now drawRotatedString(...) in RefineryUtilities (DG);
 * 01-May-2002 : Improved auto tick unit selection to prevent labels overlapping (DG);
 * 19-Jun-2002 : Added an optional marker band (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 27-Aug-2002 : Changed auto-range calculation to observe fixedAutoRange setting, as suggested
 *               by Bob Orchard (DG);
 * 03-Sep-2002 : Added maximum tick count check (DG);
 * 05-Sep-2002 : Updated constructors to reflect changes in the Axis class, and changed draw
 *               method to observe tickMarkPaint (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.util.Iterator;
import com.jrefinery.legacy.chart.event.AxisChangeEvent;
import com.jrefinery.legacy.data.Range;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A horizontal axis that displays numerical values.
 *
 * @see HorizontalCategoryPlot
 * @see XYPlot
 *
 * @author DG
 */
public class HorizontalNumberAxis extends NumberAxis implements HorizontalAxis, CanBeRelativeToTimeStepper {

    /** A flag indicating whether or not tick labels are drawn vertically. */
    private boolean verticalTickLabels;

    /** An optional band for marking regions on the axis. */
    private HorizontalMarkerAxisBand markerBand;

  /** whether we are working with relative DTGs (affects label plotting)
   *
   */
  private boolean _relativeTimes = false;

    /**
     * Constructs a horizontal number axis, using default values where necessary.
     *
     * @param label  the axis label (null permitted).
     */
    public HorizontalNumberAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             ValueAxis.DEFAULT_LOWER_BOUND,
             ValueAxis.DEFAULT_UPPER_BOUND);

        setAutoRangeAttribute(true);

    }

    /**
     * Constructs a horizontal number axis.
     *
     * @param label  the axis label (null permitted).
     * @param labelFont  the font for displaying the axis label.
     * @param lowerBound  the lowest value shown on the axis.
     * @param upperBound  the highest value shown on the axis.
     */
    public HorizontalNumberAxis(String label,
                                Font labelFont,
                                double lowerBound,
                                double upperBound) {

        this(label,
             labelFont,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true,  // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false,  // tick labels drawn vertically
             true,  // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             Axis.DEFAULT_TICK_PAINT,
             false, // no auto range selection, since the caller specified the bounds
             NumberAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE,
             NumberAxis.DEFAULT_AUTO_RANGE_INCLUDES_ZERO,
             NumberAxis.DEFAULT_AUTO_RANGE_STICKY_ZERO,
             lowerBound,
             upperBound,
             false, // inverted
             true,  // auto tick unit selection
             NumberAxis.DEFAULT_TICK_UNIT,
             true,  // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             0.0,  // anchor value
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             0.0,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT);

    }

    /**
     * Constructs a horizontal number axis.
     *
     * @param label  the axis label.
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the color used to draw the axis label.
     * @param labelInsets  the blank space around the axis label.
     * @param tickLabelsVisible  flag indicating whether or not the tick labels are visible.
     * @param tickLabelFont  font for displaying the tick labels.
     * @param tickLabelPaint  the color used to display the tick labels.
     * @param tickLabelInsets  the blank space around the tick labels.
     * @param verticalTickLabels  a flag indicating whether or not tick labels are drawn
     *                            vertically.
     * @param tickMarksVisible  flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the paint used to draw tick marks (if visible).
     * @param autoRange  flag indicating whether or not the axis range is automatically determined
     *                   to fit the data.
     * @param autoRangeMinimumSize  the smallest range allowed when the axis range is calculated to
     *                              fit the data.
     * @param autoRangeIncludesZero  a flag indicating whether or not the axis range *must* include
     *                               zero.
     * @param autoRangeStickyZero  a flag controlling the axis margins around zero.
     * @param lowerBound  the lowest value shown on the axis.
     * @param upperBound  the highest value shown on the axis.
     * @param inverted  a flag indicating whether the axis is normal or inverted (inverted means
     *        running from positive to negative).
     * @param autoTickUnitSelection  a flag indicating whether or not the tick value is
     *                               automatically selected from the range of standard tick units.
     * @param tickUnit  the tick unit.
     * @param gridLinesVisible  flag indicating whether or not grid lines are visible for this
     *                          axis.
     * @param gridStroke  the Stroke used to display grid lines (if visible).
     * @param gridPaint  the Paint used to display grid lines (if visible).
     * @param anchorValue  the anchor value.
     * @param crosshairVisible  whether to show a crosshair.
     * @param crosshairValue  the value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke  the pen/brush used to draw the crosshair.
     * @param crosshairPaint  the color used to draw the crosshair.
     */
    public HorizontalNumberAxis(String label,
                                Font labelFont, Paint labelPaint, Insets labelInsets,
                                boolean tickLabelsVisible,
                                Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
                                boolean verticalTickLabels,
                                boolean tickMarksVisible,
                                Stroke tickMarkStroke, Paint tickMarkPaint,
                                boolean autoRange,
                                Number autoRangeMinimumSize,
                                boolean autoRangeIncludesZero, boolean autoRangeStickyZero,
                                double lowerBound, double upperBound,
                                boolean inverted,
                                boolean autoTickUnitSelection, NumberTickUnit tickUnit,
                                boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                                double anchorValue,
                                boolean crosshairVisible, double crosshairValue,
                                Stroke crosshairStroke, Paint crosshairPaint) {

        super(label,
              labelFont, labelPaint, labelInsets,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              tickMarksVisible,
              tickMarkStroke, tickMarkPaint,
              autoRange,
              autoRangeMinimumSize,
              autoRangeIncludesZero, autoRangeStickyZero,
              lowerBound, upperBound,
              inverted,
              autoTickUnitSelection, tickUnit,
              gridLinesVisible,
              gridStroke, gridPaint,
              anchorValue,
              crosshairVisible, crosshairValue, crosshairStroke, crosshairPaint);

        this.verticalTickLabels = verticalTickLabels;
        this.markerBand = null;

    }


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

    /**
     * Returns a flag indicating whether the tick labels are drawn 'vertically'.
     *
     * @return The flag.
     */
    public boolean getVerticalTickLabels() {
        return this.verticalTickLabels;
    }

    /**
     * Sets the flag that determines whether the tick labels are drawn 'vertically'.
     *
     * @param flag  the new value of the flag.
     */
    public void setVerticalTickLabels(boolean flag) {
        this.verticalTickLabels = flag;
        this.notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Configures the axis to work with the specified plot.  If the axis has
     * auto-scaling, then sets the maximum and minimum values.
     */
    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the
     * axis runs along one edge of the specified dataArea.
     * <p>
     * Note that it is possible for the coordinate to fall outside the plotArea.
     *
     * @param value  the data value.
     * @param dataArea  the area for plotting the data.
     *
     * @return The Java2D coordinate.
     */
    public double translateValueToJava2D(double value, Rectangle2D dataArea) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double minX = dataArea.getX();
        double maxX = dataArea.getMaxX();
        if (isInverted()) {
            return maxX - ((value - axisMin) / (axisMax - axisMin)) * (maxX - minX);
        }
        else {
            return minX + ((value - axisMin) / (axisMax - axisMin)) * (maxX - minX);
        }

    }

    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param dataArea  the area in which the data is plotted.
     *
     * @return The data value.
     */
    public double translateJava2DtoValue(float java2DValue, Rectangle2D dataArea) {

        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double plotX = dataArea.getX();
        double plotMaxX = dataArea.getMaxX();
        if (isInverted()) {
            return axisMax - (java2DValue - plotX) / (plotMaxX - plotX) * (axisMax - axisMin);
        }
        else {
            return axisMin + (java2DValue - plotX) / (plotMaxX - plotX) * (axisMax - axisMin);
        }

    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    protected void autoAdjustRange() {

        if (plot == null) {
            return;  // no plot, no data
        }

        if (plot instanceof HorizontalValuePlot) {
            HorizontalValuePlot hvp = (HorizontalValuePlot) plot;

            Range r = hvp.getHorizontalDataRange();
            if (r == null) {
                r = new Range(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
            }
            double upper = r.getUpperBound();
            double lower = r.getLowerBound();
            double range = upper - lower;

            // if fixed auto range, then derive lower bound...
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            }
            else {
                // ensure the autorange is at least <minRange> in size...
                double minRange = getAutoRangeMinimumSize().doubleValue();
                if (range < minRange) {
                    upper = (upper + lower + minRange) / 2;
                    lower = (upper + lower - minRange) / 2;
                }


                if (autoRangeIncludesZero()) {
                    if (autoRangeStickyZero()) {
                        if (upper <= 0.0) {
                            upper = 0.0;
                        }
                        else {
                            upper = upper + getUpperMargin() * range;
                        }
                        if (lower >= 0.0) {
                            lower = 0.0;
                        }
                        else {
                            lower = lower - getLowerMargin() * range;
                        }
                    }
                    else {
                        upper = Math.max(0.0, upper + getUpperMargin() * range);
                        lower = Math.min(0.0, lower - getLowerMargin() * range);
                    }
                }
                else {
                    if (autoRangeStickyZero()) {
                        if (upper <= 0.0) {
                            upper = Math.min(0.0, upper + getUpperMargin() * range);
                        }
                        else {
                            upper = upper + getUpperMargin() * range;
                        }
                        if (lower >= 0.0) {
                            lower = Math.max(0.0, lower - getLowerMargin() * range);
                        }
                        else {
                            lower = lower - getLowerMargin() * range;
                        }
                    }
                    else {
                        upper = upper + getUpperMargin() * range;
                        lower = lower - getLowerMargin() * range;
                    }
                }
            }

            setRangeAttribute(new Range(lower, upper));
        }

    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and the axes should be drawn.
     * @param plotArea  the area in which the plot should be drawn.
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        this.ticks.clear();

        g2.setFont(tickLabelFont);

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, drawArea, plotArea);
        }

        double size = getTickUnit().getSize();
        int count = this.calculateVisibleTickCount();
        double lowestTickValue = this.calculateLowestVisibleTickValue();

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double xx = translateValueToJava2D(currentTickValue, plotArea);
                String tickLabel = getTickLabel(currentTickValue);
                Rectangle2D tickLabelBounds =
                    tickLabelFont.getStringBounds(tickLabel, g2.getFontRenderContext());
                float x = 0.0f;
                float y = 0.0f;
                if (this.verticalTickLabels) {
                    x = (float) (xx + tickLabelBounds.getHeight() / 2);
                    y = (float) (plotArea.getMaxY() + tickLabelInsets.top
                                                    + tickLabelBounds.getWidth());
                }
                else {
                    x = (float) (xx - tickLabelBounds.getWidth() / 2);
                    y = (float) (plotArea.getMaxY() + tickLabelInsets.top
                                                    + tickLabelBounds.getHeight());
                }
                Tick tick = new Tick(new Double(currentTickValue), tickLabel, x, y);
                ticks.add(tick);
            }
        }

    }

  /** for this tick, get the text label.  We're making this method modular so that it can be over-ridden in
   * our hi-res processing
   * 
   * @param currentTickValue
   * @return
   */
  public String getTickLabel(double currentTickValue)
  {
    return getTickUnit().valueToString(currentTickValue);
  }

  /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the chart should be drawn.
     * @param plotArea  the area within which the plot should be drawn (a
     *                  subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        if (!visible) {
            return;
        }

        // draw the axis label...
        double y = drawArea.getMaxY();
        if (label != null) {
            g2.setFont(labelFont);
            g2.setPaint(labelPaint);
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
            LineMetrics lm = labelFont.getLineMetrics(label, frc);
            float labelx = (float) (plotArea.getX() + plotArea.getWidth() / 2
                                                    - labelBounds.getWidth() / 2);
            float labely = (float) (y - labelInsets.bottom
                                      - lm.getDescent()
                                      - lm.getLeading());
            y = y - labelInsets.bottom - labelInsets.top - lm.getHeight();
            g2.drawString(label, labelx, labely);
        }

        // draw the marker band (if there is one)...
        if (markerBand != null) {
            y = y - markerBand.getHeight(g2);
            markerBand.draw(g2, drawArea, plotArea, 0, y);
        }


        // draw the tick labels and marks
        refreshTicks(g2, drawArea, plotArea);

        float maxY = (float) plotArea.getMaxY();
        g2.setFont(getTickLabelFont());

        Iterator<Tick> iterator = ticks.iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float xx = (float) translateValueToJava2D(tick.getNumericalValue(), plotArea);
            if (tickLabelsVisible) {
                g2.setPaint(this.tickLabelPaint);
                if (this.verticalTickLabels) {
                    RefineryUtilities.drawRotatedString(tick.getText(), g2,
                        tick.getX(), tick.getY(), -Math.PI / 2);
                }
                else {
                    g2.drawString(tick.getText(), tick.getX(), tick.getY());
                }
            }

            if (tickMarksVisible) {
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                Line2D mark = new Line2D.Float(xx, maxY - 2, xx, maxY + 2);
                g2.draw(mark);
            }

            if (isGridLinesVisible()) {
                g2.setStroke(getGridStroke());
                g2.setPaint(getGridPaint());
                Line2D gridline = new Line2D.Float(xx, (float) plotArea.getMaxY(),
                                                   xx, (float) plotArea.getMinY());
                g2.draw(gridline);
            }

        }
    }

    /**
     * Returns the height required to draw the axis in the specified draw area.
     *
     * @param g2  the graphics device.
     * @param plot1  the plot that the axis belongs to.
     * @param drawArea  the area within which the plot should be drawn.
     *
     * @return the height required to draw the axis in the specified draw area.
     */
    public double reserveHeight(Graphics2D g2, Plot plot1, Rectangle2D drawArea) {

        if (!visible) {
            return 0.0;
        }

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        if (label != null) {
            LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
            labelHeight = this.labelInsets.top + metrics.getHeight()
                                               + this.labelInsets.bottom;
        }

        // calculate the height of the marker band (if there is one)...
        double markerBandHeight = 0.0;
        if (markerBand != null) {
            markerBandHeight = markerBand.getHeight(g2);
        }

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
        if (tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelHeight = tickLabelHeight
                              + getMaxTickLabelHeight(g2, drawArea, this.verticalTickLabels);
        }
        return labelHeight + markerBandHeight + tickLabelHeight;

    }

    /**
     * Returns area in which the axis will be displayed.
     *
     * @param g2  the graphics device.
     * @param plot1  a reference to the plot.
     * @param drawArea  the area within which the plot and axes should be drawn.
     * @param reservedWidth  the space already reserved for the vertical axis.
     *
     * @return area in which the axis will be displayed.
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot1,
                                       Rectangle2D drawArea, double reservedWidth) {

        if (!visible) {
            return new Rectangle2D.Double(drawArea.getX(), drawArea.getMaxY(),
                                          drawArea.getWidth() - reservedWidth,
                                          0.0);
        }

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        if (label != null) {
            LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
            labelHeight = this.labelInsets.top + metrics.getHeight()
                                               + this.labelInsets.bottom;
        }

        // calculate the height of the marker band (if there is one)...
        double markerBandHeight = 0.0;
        if (markerBand != null) {
            markerBandHeight = markerBand.getHeight(g2);
        }

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom;
        if (tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelHeight = tickLabelHeight
                              + getMaxTickLabelHeight(g2, drawArea, this.verticalTickLabels);
        }
        return new Rectangle2D.Double(drawArea.getX(),
                                      drawArea.getMaxY(),
                                      drawArea.getWidth() - reservedWidth,
                                      labelHeight + markerBandHeight + tickLabelHeight);

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param dataArea  the area defined by the axes.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea) {

        double zero = translateValueToJava2D(0.0, dataArea);
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());

        // start with the current tick unit...
        TickUnits tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double x1 = translateValueToJava2D(unit1.getSize(), dataArea);
        double unit1Width = Math.abs(x1 - zero);

        // then extrapolate...
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();

        NumberTickUnit unit2 = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);
        double x2 = translateValueToJava2D(unit2.getSize(), dataArea);
        double unit2Width = Math.abs(x2 - zero);

        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }

        setTickUnitAttribute(unit2);

    }

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
    private double estimateMaximumTickLabelWidth(Graphics2D g2, TickUnit tickUnit) {

        double result = this.tickLabelInsets.left + this.tickLabelInsets.right;

        FontRenderContext frc = g2.getFontRenderContext();
        if (this.verticalTickLabels) {
            // all tick labels have the same width (equal to the height of the font)...
            result += tickLabelFont.getStringBounds("0", frc).getHeight();
        }
        else {
            // look at lower and upper bounds...
            Range range = getRange();
            double lower = range.getLowerBound();
            double upper = range.getUpperBound();
            String lowerStr = tickUnit.valueToString(lower);
            String upperStr = tickUnit.valueToString(upper);
            double w1 = tickLabelFont.getStringBounds(lowerStr, frc).getWidth();
            double w2 = tickLabelFont.getStringBounds(upperStr, frc).getWidth();
            result += Math.max(w1, w2);
        }

        return result;

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the plot and axes should be drawn.
     * @param vertical  a flag that indicates whether or not the tick labels are 'vertical'.
     *
     * @return the height of the tallest tick label.
     */
    private double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        Font font = getTickLabelFont();
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        double maxHeight = 0.0;
        if (vertical) {
            Iterator<Tick> iterator = this.ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
                if (labelBounds.getWidth() > maxHeight) {
                    maxHeight = labelBounds.getWidth();
                }
            }
        }
        else {
            LineMetrics metrics = font.getLineMetrics("Sample", frc);
            maxHeight = metrics.getHeight();
        }
        return maxHeight;
    }

    /**
     * Returns true if a plot is compatible with the axis, and false otherwise.
     * <P>
     * For this axis, the requirement is that the plot implements the HorizontalValuePlot
     * interface.
     *
     * @param plot1  the plot.
     *
     * @return <code>true</code> if the plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot1) {
        if (plot1 instanceof HorizontalValuePlot) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the (optional) marker band for the axis.
     *
     * @return The marker band (possibly null).
     */
    public HorizontalMarkerAxisBand getMarkerBand() {
        return this.markerBand;
    }

    /**
     * Sets the marker band for the axis.
     * <P>
     * The marker band is optional, leave it set to null if you don't require it.
     *
     * @param band the new band (null permitted).
     */
    public void setMarkerBand(HorizontalMarkerAxisBand band) {
        this.markerBand = band;
        notifyListeners(new AxisChangeEvent(this));
    }

}
