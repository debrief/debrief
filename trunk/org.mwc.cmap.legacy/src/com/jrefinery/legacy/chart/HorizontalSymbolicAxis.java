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
 * ---------------------------
 * HorizontalSymbolicAxis.java
 * ---------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 29-Mar-2002 : First version (AB);
 * 19-Apr-2002 : Updated formatting and import statements (DG);
 * 21-Jun-2002 : Make change to use the class TickUnit - remove valueToString(...) method and
 *               add SymbolicTickUnit (AB);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 05-Sep-2002 : Updated constructor to reflect changes in the Axis class (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Vector;
import com.jrefinery.legacy.chart.event.AxisChangeEvent;
import com.jrefinery.legacy.data.Range;

/**
 * A standard linear value axis, for SYMBOLIC values displayed horizontally.
 *
 * @author AB
 */
public class HorizontalSymbolicAxis extends HorizontalNumberAxis implements HorizontalAxis {

    /** The default symbolic grid line paint. */
    public static final Paint DEFAULT_SYMBOLIC_GRID_LINE_PAINT = new Color(232, 234, 232);

    /** The list of symbolic value to display instead of the numeric values. */
    private java.util.List<String> symbolicValue;

    /** Enable or not the zoom. **/
    private boolean xSymbolicZoomIsAccepted = false;

    /** List of the symbolic grid lines shapes. */
    private java.util.List<Rectangle2D> symbolicGridLineList = null;

    /** Color of the dark part of the symbolic grid line. **/
    private Paint symbolicGridPaint;

    /** Flag that indicates whether or not symbolic grid lines are visible. */
    private boolean symbolicGridLinesVisible;

    /**
     * Constructs a horizontal symbolic axis, using default attribute values where necessary.
     *
     * @param label  the axis label (null permitted).
     * @param sv  the list of symbolic values to display instead of the numeric value.
     */
    public HorizontalSymbolicAxis(String label, String[] sv) {

        this(label, sv,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             true, //symbolic grid line visible
             DEFAULT_SYMBOLIC_GRID_LINE_PAINT);

        setAutoRangeAttribute(true);

    }

    /**
     * Constructs a horizontal symbolic axis, using default attribute values where necessary.
     *
     * @param label  the axis label (null permitted).
     * @param sv  the list of symbolic value to display instead of the numeric value.
     * @param labelFont  the font for displaying the axis label.
     * @param symbolicGridLinesVisible  flag that indicates whether or not symbolic grid lines are
     *                                  visible.
     * @param symbolicGridPaint  color of the dark part of the symbolic grid line.
     */
    public HorizontalSymbolicAxis(String label, String[] sv, Font labelFont,
                                  boolean symbolicGridLinesVisible,
                                  Paint symbolicGridPaint) {
        this(label, sv,
             labelFont,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false, // tick labels drawn vertically
             true, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             Axis.DEFAULT_TICK_PAINT,
             false, // no auto range selection, since the caller specified bounds
             ValueAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE,
             false, // auto range includes zero
             false, // auto range sticky zero
             ValueAxis.DEFAULT_LOWER_BOUND,
             ValueAxis.DEFAULT_UPPER_BOUND,
             false, // inverted
             false, // auto tick unit
             new SymbolicTickUnit(DEFAULT_TICK_UNIT.getSize(), sv),
             false, // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             0.0,  // anchor value
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             0.0,  // crosshair value
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT,
             symbolicGridLinesVisible,
             symbolicGridPaint);

    }

    /**
     * Constructs a new horizontal symbolic axis.
     *
     * @param label  the axis label (null permitted).
     * @param sv  the list of symbolic value to display instead of the numeric value
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the paint used to draw the axis label.
     * @param labelInsets  determines the amount of blank space around the label.
     * @param tickLabelsVisible  flag indicating whether or not tick labels are visible.
     * @param tickLabelFont  the font used to display tick labels.
     * @param tickLabelPaint  the paint used to draw tick labels.
     * @param tickLabelInsets  determines the amount of blank space around tick labels.
     * @param verticalTickLabels  flag indicating whether or not the label is drawn vertically.
     * @param tickMarksVisible  flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the paint used to draw tick marks (if visible).
     * @param autoRange  flag indicating whether or not the axis is automatically scaled to fit
     *                   the data.
     * @param autoRangeMinimumSize  the smallest range allowed when the axis range is calculated
     *                              to fit the data.
     * @param autoRangeIncludesZero  a flag indicating whether zero *must* be displayed on axis.
     * @param autoRangeStickyZero  a flag that affects the size of the margins added to the axis
     *                             range when the range is determined automatically.  If the value
     *                             0 falls within the margin and this flag is <code>true</code>,
     *                             then the margin is truncated at zero.
     * @param lowerBound  the lowest value shown on the axis.
     * @param upperBound  the highest value shown on the axis.
     * @param inverted  a flag indicating whether the axis is normal or inverted (inverted means
     *                  running from positive to negative).
     * @param autoTickUnitSelection  a flag indicating whether or not the tick units are selected
     *                               automatically.
     * @param tickUnit  the tick unit.
     * @param gridLinesVisible  flag indicating whether grid lines are visible for this axis.
     * @param gridStroke  the pen/brush used to display grid lines (if visible).
     * @param gridPaint  the color used to display grid lines (if visible).
     * @param anchorValue  the anchor value.
     * @param crosshairVisible  a flag indicating whether or not a crosshair is visible.
     * @param crosshairValue  the value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke  the pen/brush used to draw the crosshair.
     * @param crosshairPaint  the color used to draw the crosshair.
     * @param symbolicGridLinesVisible  flag that indicates whether or not symbolic grid lines are
     *                                  visible.
     * @param symbolicGridPaint  color of the dark part of the symbolic grid line.
     */
    public HorizontalSymbolicAxis(String label, String[] sv,
                                  Font labelFont, Paint labelPaint, Insets labelInsets,
                                  boolean tickLabelsVisible,
                                  Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
                                  boolean verticalTickLabels,
                                  boolean tickMarksVisible,
                                  Stroke tickMarkStroke, Paint tickMarkPaint,
                                  boolean autoRange,
                                  Number autoRangeMinimumSize,
                                  boolean autoRangeIncludesZero,
                                  boolean autoRangeStickyZero,
                                  double lowerBound, double upperBound,
                                  boolean inverted,
                                  boolean autoTickUnitSelection,
                                  NumberTickUnit tickUnit,
                                  boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                                  double anchorValue,
                                  boolean crosshairVisible,
                                  double crosshairValue,
                                  Stroke crosshairStroke, Paint crosshairPaint,
                                  boolean symbolicGridLinesVisible,
                                  Paint symbolicGridPaint) {

        super(label,
              labelFont, labelPaint, labelInsets,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              verticalTickLabels,
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
              crosshairVisible,
              crosshairValue, crosshairStroke, crosshairPaint);

        //initialization of symbolic value
        this.symbolicValue = Arrays.asList(sv);
        this.symbolicGridLinesVisible = symbolicGridLinesVisible;
        this.symbolicGridPaint = symbolicGridPaint;

    }

    /**
     * Returns the list of the symbolic values to display.
     *
     * @return list of symbolic values.
     */
    public String[] getSymbolicValue() {

        String[] strToReturn = new String[symbolicValue.size()];
        strToReturn = (String[]) symbolicValue.toArray(strToReturn);
        return strToReturn;
    }

    /**
     * Returns the symbolic grid line color.
     *
     * @return the grid line color.
     */
    public Paint getSymbolicGridPaint() {
        return symbolicGridPaint;
    }

    /**
     * Returns <CODE>true</CODE> if the symbolic grid lines are showing, and
     * false otherwise.
     *
     * @return true if the symbolic grid lines are showing, and false otherwise.
     */
    public boolean isGridLinesVisible() {
        return symbolicGridLinesVisible;
    }

    /**
     * Sets the visibility of the symbolic grid lines and notifies registered
     * listeners that the axis has been modified.
     *
     * @param flag  the new setting.
     */
    public void setSymbolicGridLinesVisible(boolean flag) {

        if (symbolicGridLinesVisible != flag) {
            symbolicGridLinesVisible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Redefinition of setAnchoredRange for the symbolicvalues.
     *
     * @param range  the new range.
     */
    public void setAnchoredRange(double range) {

        if (xSymbolicZoomIsAccepted) {
            //compute the corresponding integer corresponding to the anchor
            //position
            double anchor = Math.rint(getAnchorValue());
            double min = Math.rint(anchor - range / 2) - 0.5;
            double max = Math.rint(anchor + range / 2) + 0.5;
            if (min < -0.5) {
                min = -0.5;
            }
            if (max > symbolicValue.size() - 0.5) {
                max = symbolicValue.size() - 0.5;
            }
            this.setRange(min, max);
        }

    }

    /**
     * This operation is not supported by the symbolic values.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area in which the plot and axes should be drawn.
     * @param plotArea  the area in which the plot should be drawn.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
        throw new UnsupportedOperationException();
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the chart should be drawn.
     * @param plotArea  the area within which the plot should be drawn (a subset of the drawArea).
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
        if (visible) {
            super.draw(g2, drawArea, plotArea);
        }
        if (symbolicGridLinesVisible) {
            drawSymbolicGridLines(g2, drawArea, plotArea);
        }
    }

    /**
     * Draws the symbolic grid lines.
     * <P>
     * The colors are consecutively the color specified by
     * <CODE>symbolicGridPaint<CODE>
     * (<CODE>DEFAULT_SYMBOLIC_GRID_LINE_PAINT</CODE> by default) and white.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the chart should be drawn.
     * @param plotArea  the area within which the plot should be drawn (a subset of the drawArea).
     */
    public void drawSymbolicGridLines(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
        drawSymbolicGridLines(g2, drawArea, plotArea, true);
    }

    /**
     * Draws the symbolic grid lines.
     * <P>
     * The colors are consecutively the color specified by
     * <CODE>symbolicGridPaint<CODE>
     * (<CODE>DEFAULT_SYMBOLIC_GRID_LINE_PAINT</CODE> by default) and white.
     * or if <CODE>firstGridLineIsDark</CODE> is <CODE>true</CODE> white and
     * the color specified by <CODE>symbolicGridPaint<CODE>.
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the chart should be drawn.
     * @param plotArea  the area within which the plot should be drawn
     *                  (a subset of the drawArea).
     * @param firstGridLineIsDark  True: the first symbolic grid line take the
     *                             color of <CODE>symbolicGridPaint<CODE>.
     *                             False: the first symbolic grid line is white.
     */
    public void drawSymbolicGridLines(Graphics2D g2,
                                      Rectangle2D drawArea, Rectangle2D plotArea,
                                      boolean firstGridLineIsDark) {

        this.symbolicGridLineList = new Vector<Rectangle2D>(ticks.size());
        boolean currentGridLineIsDark = firstGridLineIsDark;
        double yy = plotArea.getY();
        double xx1, xx2;

        //gets the outline stroke width of the plot
        double outlineStrokeWidth;
        if (plot.outlineStroke !=  null) {
            outlineStrokeWidth = ((BasicStroke) plot.outlineStroke).getLineWidth();
        }
        else {
            outlineStrokeWidth = 1d;
        }

        Iterator<Tick> iterator = ticks.iterator();
        Tick tick;
        Rectangle2D symbolicGridLine;
        while (iterator.hasNext()) {
            tick = (Tick) iterator.next();
            xx1 = translateValueToJava2D(tick.getNumericalValue() - 0.5d, plotArea);
            xx2 = translateValueToJava2D(tick.getNumericalValue() + 0.5d, plotArea);
            if (currentGridLineIsDark) {
                g2.setPaint(Color.white);
                g2.setXORMode((Color) symbolicGridPaint);
            }
            else {
                g2.setPaint(Color.white);
                g2.setXORMode(Color.white);
            }
            symbolicGridLine =
                new Rectangle2D.Double(xx1,
                                       yy + outlineStrokeWidth, xx2 - xx1,
                                       plotArea.getMaxY() - yy - outlineStrokeWidth);
            g2.fill(symbolicGridLine);
            symbolicGridLineList.add(symbolicGridLine);
            currentGridLineIsDark = !currentGridLineIsDark;
        }
        g2.setPaintMode();
    }

    /**
     * Get the symbolic grid line corresponding to the specified position.
     *
     * @param position  position of the grid line, startinf from 0.
     *
     * @return the symbolic grid line corresponding to the specified position.
     */
    public Rectangle2D.Double getSymbolicGridLine(int position) {

        if (symbolicGridLineList != null) {
            return (Rectangle2D.Double) symbolicGridLineList.get(position);
        }
        else {
            return null;
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

            //ensure that all the symbolic value are displayed
            double upper = symbolicValue.size() - 1;
            double lower = 0;
            double range = upper - lower;

            // ensure the autorange is at least <minRange> in size...
            double minRange = getAutoRangeMinimumSize().doubleValue();
            if (range < minRange) {
                upper = (upper + lower + minRange) / 2;
                lower = (upper + lower - minRange) / 2;
            }

            //this ensure that the symbolic grid lines will be displayed
            //correctly.
            double upperMargin = 0.5;
            double lowerMargin = 0.5;

            if (autoRangeIncludesZero()) {
                if (autoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = 0.0;
                    }
                    else {
                        upper = upper + upperMargin;
                    }
                    if (lower >= 0.0) {
                        lower = 0.0;
                    }
                    else {
                        lower = lower - lowerMargin;
                    }
                }
                else {
                    upper = Math.max(0.0, upper + upperMargin);
                    lower = Math.min(0.0, lower - lowerMargin);
                }
            }
            else {
                if (autoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = Math.min(0.0, upper + upperMargin);
                    }
                    else {
                        upper = upper + upperMargin * range;
                    }
                    if (lower >= 0.0) {
                        lower = Math.max(0.0, lower - lowerMargin);
                    }
                    else {
                        lower = lower - lowerMargin;
                    }
                }
                else {
                    upper = upper + upperMargin;
                    lower = lower - lowerMargin;
                }
            }

            setRangeAttribute(new Range(lower, upper));

        }

    }

}
