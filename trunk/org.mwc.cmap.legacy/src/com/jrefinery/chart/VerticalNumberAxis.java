/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
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
 * VerticalNumberAxis.java
 * -----------------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   David Li;
 *                   Jonathan Nash;
 *                   Richard Atkinson;
 *
 * $Id: VerticalNumberAxis.java,v 1.2 2007/01/04 16:32:07 ian.mayo Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Updated configure() method.  Replaced some hard-coded defaults. (DG);
 * 12-Dec-2001 : Minor change due to grid lines bug fix (DG);
 * 08-Jan-2002 : Added flag to allow axis to be inverted (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 25-Feb-2002 : Changed autoAdjustRange() from public to protected, and modified the calculation
 *               to take into account the new autoRangeStickyZero flag.  Updated constructors for
 *               new flag.  Updated import statements (DG);
 * 06-Mar-2002 : Changed labelDrawnVertical --> verticalLabel to be consistent with other
 *               vertical axes (DG);
 * 22-Apr-2002 : drawVerticalString(...) is now drawRotatedString(...) in RefineryUtilities.
 *               Simplified autoAdjustRange() method (DG);
 * 01-May-2002 : Updated for changes in TickUnit class (DG);
 * 25-Jun-2002 : Fixed bug in auto range calculation...when auto range includes zero, the upper
 *               and lower margins were being calculated on the range not including zero (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 06-Aug-2002 : Modified draw method to not draw axis label if label is empty String (RA);
 * 05-Sep-2002 : Updated constructors to reflect changes in the Axis class, and changed draw method
 *               to observe tickMarkPaint (DG);
 * 22-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Changed calculation for vertical position of tick labels (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.util.Iterator;

import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.data.Range;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A standard linear value axis, for values displayed vertically.
 * <P>
 * Note that bug 4273469 on the Java Developer Connection talks about why the
 * grid lines don't always line up with the tick marks precisely.
 *
 * @author DG
 */
public class VerticalNumberAxis extends NumberAxis implements VerticalAxis {

    /** A flag indicating whether or not the axis label is drawn vertically. */
    private boolean verticalLabel;

    /**
     * Constructs a vertical number axis, using default values where necessary.
     */
    public VerticalNumberAxis() {

        this(null);

    }

    /**
     * Constructs a vertical number axis, using default attribute values where necessary.
     *
     * @param label  the axis label (null permitted).
     */
    public VerticalNumberAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             ValueAxis.DEFAULT_LOWER_BOUND,
             ValueAxis.DEFAULT_UPPER_BOUND);

        setAutoRangeAttribute(true);

    }

    /**
     * Constructs a vertical number axis, using default attribute values where necessary.
     *
     * @param label  the axis label (null permitted).
     * @param labelFont  the font for displaying the axis label.
     * @param lowerBound  the lowest value shown on the axis.
     * @param upperBound  the highest value shown on the axis.
     */
    public VerticalNumberAxis(String label,
                              Font labelFont,
                              double lowerBound,
                              double upperBound) {

        this(label,
             labelFont,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // vertical axis label
             true, // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             true, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             Axis.DEFAULT_TICK_PAINT,
             false, // auto range set to false because the caller has specified bounds
             ValueAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE,
             NumberAxis.DEFAULT_AUTO_RANGE_INCLUDES_ZERO,
             NumberAxis.DEFAULT_AUTO_RANGE_STICKY_ZERO,
             lowerBound,
             upperBound,
             false, // inverted
             true, // auto tick unit selection
             NumberAxis.DEFAULT_TICK_UNIT,
             true, // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             0.0,  // anchor value
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             0.0,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT);

    }

    /**
     * Constructs a new VerticalNumberAxis.
     *
     * @param label  the axis label.
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the paint used to draw the axis label.
     * @param labelInsets  determines the amount of blank space around the label.
     * @param verticalLabel  flag indicating whether or not the label is drawn vertically.
     * @param tickLabelsVisible  flag indicating whether or not tick labels are visible.
     * @param tickLabelFont  the font used to display tick labels.
     * @param tickLabelPaint  the paint used to draw tick labels.
     * @param tickLabelInsets  determines the amount of blank space around tick labels.
     * @param tickMarksVisible  flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the paint used to draw tick marks (if visible).
     * @param autoRange  flag indicating whether or not the axis is automatically scaled to fit
     *                   the data.
     * @param autoRangeMinimumSize  the smallest range allowed when the axis range is calculated
     *                              to fit the data.
     * @param autoRangeIncludesZero  a flag indicating whether or not zero *must* be displayed on
     *                               axis.
     * @param autoRangeStickyZero  a flag controlling the axis margins around zero.
     * @param lowerBound  the lowest value shown on the axis.
     * @param upperBound  the highest value shown on the axis.
     * @param inverted  a flag indicating whether the axis is normal or inverted (inverted means
     *                  running from positive to negative).
     * @param autoTickUnitSelection  a flag indicating whether or not the tick units are selected
     *                               automatically.
     * @param tickUnit  the tick unit.
     * @param gridLinesVisible  flag indicating whether or not grid lines are visible for this
     *                          axis.
     * @param gridStroke  the pen/brush used to display grid lines (if visible).
     * @param gridPaint  the color used to display grid lines (if visible).
     * @param anchorValue  the anchor value.
     * @param crosshairVisible  whether to show a crosshair.
     * @param crosshairValue  the value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke  the pen/brush used to draw the crosshair.
     * @param crosshairPaint  the color used to draw the crosshair.
     */
    public VerticalNumberAxis(String label,
                              Font labelFont, Paint labelPaint, Insets labelInsets,
                              boolean verticalLabel,
                              boolean tickLabelsVisible,
                              Font tickLabelFont, Paint tickLabelPaint,
                              Insets tickLabelInsets,
                              boolean tickMarksVisible,
                              Stroke tickMarkStroke, Paint tickMarkPaint,
                              boolean autoRange,
                              Number autoRangeMinimumSize,
                              boolean autoRangeIncludesZero, boolean autoRangeStickyZero,
                              double lowerBound, double upperBound,
                              boolean inverted,
                              boolean autoTickUnitSelection,
                              NumberTickUnit tickUnit,
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
              gridLinesVisible, gridStroke, gridPaint,
              anchorValue,
              crosshairVisible, crosshairValue, crosshairStroke, crosshairPaint);

        this.verticalLabel = verticalLabel;

    }

    /**
     * Returns a flag indicating whether or not the axis label is drawn vertically.
     *
     * @return  the flag.
     */
    public boolean isVerticalLabel() {
        return this.verticalLabel;
    }

    /**
     * Sets a flag indicating whether or not the axis label is drawn vertically.
     * If the setting is changed, registered listeners are notified that the
     * axis has changed.
     *
     * @param flag  the new flag.
     */
    public void setVerticalLabel(boolean flag) {

        if (this.verticalLabel != flag) {
            this.verticalLabel = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

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

        double maxY = dataArea.getMaxY();
        double minY = dataArea.getMinY();

        if (isInverted()) {
            return minY + (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
        }
        else {
            return maxY - (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
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
        double plotY = dataArea.getY();
        double plotMaxY = dataArea.getMaxY();

        if (isInverted()) {
            return axisMin + (java2DValue - plotY) / (plotMaxY - plotY) * (axisMax - axisMin);
        }
        else {
            return axisMax - (java2DValue - plotY) / (plotMaxY - plotY) * (axisMax - axisMin);
        }

    }

    /**
     * Sets the axis minimum and maximum values so that all the data is visible.
     * <P>
     * You can control the range calculation in several ways.  First, you can
     * define upper and lower margins as a percentage of the data range (the
     * default is a 5% margin for each). Second, you can set a flag that forces
     * the range to include zero.  Finally, you can set another flag, the
     * 'sticky zero' flag, that only affects the range when zero falls within
     * the axis margins.  When this happens, the margin is truncated so that
     * zero is the upper or lower limit for the axis.
     */
    protected void autoAdjustRange() {

        if (plot == null) {
            return;  // no plot, no data
        }

        if (plot instanceof VerticalValuePlot) {

            VerticalValuePlot vvp = (VerticalValuePlot) plot;
            Range r = vvp.getVerticalDataRange();
            if (r == null) {
                r = new Range(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
            }
            double lower = r.getLowerBound();
            double upper = r.getUpperBound();
            double range = upper - lower;

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
                        upper = upper + getUpperMargin() * (upper - Math.min(lower, 0.0));
                    }
                    if (lower >= 0.0) {
                        lower = 0.0;
                    }
                    else {
                        lower = lower - getLowerMargin() * (upper - lower);
                    }
                }
                else {
                    range = Math.max(0.0, upper) - Math.min(0.0, lower);
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

            setRangeAttribute(new Range(lower, upper));
        }

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the chart should be drawn.
     * @param dataArea  the area within which the plot should be drawn (a
     *                  subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea) {

        if (!visible) {
            return;
        }

        // draw the axis label
        if (this.label == null ? false : !this.label.equals("")) {
            g2.setFont(labelFont);
            g2.setPaint(labelPaint);

            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            if (verticalLabel) {
                double xx = drawArea.getX() + labelInsets.left + labelBounds.getHeight();
                double yy = dataArea.getY() + dataArea.getHeight() / 2
                                            + (labelBounds.getWidth() / 2);
                RefineryUtilities.drawRotatedString(label, g2,
                                                    (float) xx, (float) yy, -Math.PI / 2);
            }
            else {
                double xx = drawArea.getX() + labelInsets.left;
                double yy = drawArea.getY() + drawArea.getHeight() / 2
                                            - labelBounds.getHeight() / 2;
                g2.drawString(label, (float) xx, (float) yy);
            }
        }

        // draw the tick labels and marks and gridlines
        refreshTicks(g2, drawArea, dataArea);
        double xx = dataArea.getX();
        g2.setFont(tickLabelFont);

        Iterator<Tick> iterator = ticks.iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            float yy = (float) translateValueToJava2D(tick.getNumericalValue(), dataArea);
            if (tickLabelsVisible) {
                g2.setPaint(this.tickLabelPaint);
                g2.drawString(tick.getText(), tick.getX(), tick.getY());
            }

            if (tickMarksVisible) {
                g2.setStroke(getTickMarkStroke());
                g2.setPaint(getTickMarkPaint());
                Line2D mark = new Line2D.Double(dataArea.getX() - 2, yy, dataArea.getX() + 2, yy);
                g2.draw(mark);
            }

            if (isGridLinesVisible()) {
                g2.setStroke(getGridStroke());
                g2.setPaint(getGridPaint());
                Line2D gridline = new Line2D.Double(xx, yy, dataArea.getMaxX(), yy);
                g2.draw(gridline);

            }
        }

    }

    /**
     * Returns the width required to draw the axis in the specified draw area.
     *
     * @param g2  the graphics device.
     * @param plot1  a reference to the plot;
     * @param drawArea  the area within which the plot should be drawn.
     * @return  width required to draw the axis.
     */
    public double reserveWidth(Graphics2D g2, Plot plot1, Rectangle2D drawArea) {

        if (!visible) {
            return 0.0;
        }

        if (fixedDimension > 0.0) {
            return fixedDimension;
        }

        // calculate the width of the axis label...
        double labelWidth = 0.0;
        if (label != null) {
            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            labelWidth = labelInsets.left + labelInsets.right;
            if (this.verticalLabel) {
                // assume width == height before rotation
                labelWidth = labelWidth + labelBounds.getHeight();
            }
            else {
                labelWidth = labelWidth + labelBounds.getWidth();
            }
        }

        // calculate the width required for the tick labels (if visible);
        double tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
        if (tickLabelsVisible) {
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelWidth = tickLabelWidth + getMaxTickLabelWidth(g2, drawArea);
        }
        return labelWidth + tickLabelWidth;

    }

    /**
     * Returns area in which the axis will be displayed.
     *
     * @param g2  the graphics device.
     * @param plot1  a reference to the plot.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param reservedHeight  the height reserved for the horizontal axis.
     * @return  area in which the axis will be displayed.
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot1,
                                       Rectangle2D drawArea, double reservedHeight) {

        if (!visible) {
            return new Rectangle2D.Double(drawArea.getX(), drawArea.getY(),
                                          0.0, drawArea.getHeight() - reservedHeight);
        }

        if (fixedDimension > 0.0) {
            return new Rectangle2D.Double(drawArea.getX(), drawArea.getY(),
                                          fixedDimension, drawArea.getHeight() - reservedHeight);
        }

        // calculate the width of the axis label...
        double labelWidth = 0.0;
        if (label != null) {
            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            labelWidth = labelInsets.left + labelInsets.right;
            if (this.verticalLabel) {
                // assume width == height before rotation
                labelWidth = labelWidth + labelBounds.getHeight();
            }
            else {
                labelWidth = labelWidth + labelBounds.getWidth();
            }
        }

        // calculate the width of the tick labels
        double tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
        if (tickLabelsVisible) {
            Rectangle2D approximatePlotArea =
                new Rectangle2D.Double(drawArea.getX(), drawArea.getY(),
                                       drawArea.getWidth(),
                                       drawArea.getHeight() - reservedHeight);
            this.refreshTicks(g2, drawArea, approximatePlotArea);
            tickLabelWidth = tickLabelWidth + getMaxTickLabelWidth(g2, approximatePlotArea);
        }

        return new Rectangle2D.Double(drawArea.getX(), drawArea.getY(),
                                      labelWidth + tickLabelWidth,
                                      drawArea.getHeight() - reservedHeight);

    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param plotArea  the area in which the plot should be drawn.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        // calculate the tick label height...
        FontRenderContext frc = g2.getFontRenderContext();
        double tickLabelHeight = tickLabelFont.getLineMetrics("123", frc).getHeight()
                                 + this.tickLabelInsets.top
                                 + this.tickLabelInsets.bottom;

        // now find the smallest tick unit that will accommodate the labels...
        double zero = translateValueToJava2D(0.0, plotArea);

        // start with the current tick unit...
        TickUnits tickUnits = getStandardTickUnits();
        NumberTickUnit candidate1 = (NumberTickUnit) tickUnits.getCeilingTickUnit(getTickUnit());
        double y = translateValueToJava2D(candidate1.getSize(), plotArea);
        double unitHeight = Math.abs(y - zero);

        // then extrapolate...
        double bestguess = (tickLabelHeight / unitHeight) * candidate1.getSize();
        NumberTickUnit guess = new NumberTickUnit(bestguess, null);
        NumberTickUnit candidate2 = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);

        setTickUnitAttribute(candidate2);

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

        g2.setFont(this.tickLabelFont);

        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, drawArea, plotArea);
        }

        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();

        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double yy = translateValueToJava2D(currentTickValue, plotArea);
                String tickLabel = getTickUnit().valueToString(currentTickValue);
                FontRenderContext frc = g2.getFontRenderContext();
                Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel, frc);
                LineMetrics lm = this.tickLabelFont.getLineMetrics(tickLabel, frc);
                float x = (float) (plotArea.getX()
                                   - tickLabelBounds.getWidth() - tickLabelInsets.right);
                float y = (float) (yy + (lm.getAscent() / 2));
                Tick tick = new Tick(new Double(currentTickValue), tickLabel, x, y);
                ticks.add(tick);
            }
        }

    }

    /**
     * Returns true if the specified plot is compatible with the axis.
     * <P>
     * This class (VerticalNumberAxis) requires that the plot implements the
     * VerticalValuePlot interface.
     *
     * @param plot1      The plot.
     * @return <code>true</code> if the specified plot is compatible with the
     *      axis.
     */
    protected boolean isCompatiblePlot(Plot plot1) {

        if (plot1 instanceof VerticalValuePlot) {
            return true;
        }
        else {
            return false;
        }
    }

}