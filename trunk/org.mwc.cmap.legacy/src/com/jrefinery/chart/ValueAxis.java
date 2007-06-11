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
 * --------------
 * ValueAxis.java
 * --------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jonathan Nash;
 *
 * $Id: ValueAxis.java,v 1.1.1.1 2003/07/17 10:06:28 Ian.Mayo Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 23-Nov-2001 : Overhauled standard tick unit code (DG);
 * 04-Dec-2001 : Changed constructors to protected, and tidied up default values (DG);
 * 12-Dec-2001 : Fixed vertical gridlines bug (DG);
 * 16-Jan-2002 : Added an optional crosshair, based on the implementation by Jonathan Nash (DG);
 * 23-Jan-2002 : Moved the minimum and maximum values to here from NumberAxis, and changed the type
 *               from Number to double (DG);
 * 25-Feb-2002 : Added default value for autoRange. Changed autoAdjustRange from public to
 *               protected. Updated import statements (DG);
 * 23-Apr-2002 : Added setRange(...) method (DG);
 * 29-Apr-2002 : Added range adjustment methods (DG);
 * 13-Jun-2002 : Modified setCrosshairValue(...) to notify listeners only when the crosshairs are
 *               visible, to avoid unnecessary repaints, as suggested by Kees Kuip (DG);
 * 25-Jul-2002 : Moved lower and upper margin attributes from the NumberAxis class (DG);
 * 05-Sep-2002 : Updated constructor for changes in Axis class (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 04-Oct-2002 : Moved standardTickUnits from NumberAxis --> ValueAxis (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.data.Range;

/**
 * The base class for axes that display value data (a "value" can be a Number or a Date).
 *
 * @author DG
 */
public abstract class ValueAxis extends Axis {

    /** The default axis range. */
    public static final Range DEFAULT_RANGE = new Range(0.0, 1.0);

    /** The default auto-range value. */
    public static final boolean DEFAULT_AUTO_RANGE = true;

    /** The default minimum auto range. */
    public static final Number DEFAULT_AUTO_RANGE_MINIMUM_SIZE = new Double(0.0000001);

    /** The default value for the lower margin (0.05 = 5%). */
    public static final double DEFAULT_LOWER_MARGIN = 0.05;

    /** The default value for the upper margin (0.05 = 5%). */
    public static final double DEFAULT_UPPER_MARGIN = 0.05;

    /** The default lower bound for the axis. */
    public static final double DEFAULT_LOWER_BOUND = 0.0;

    /** The default upper bound for the axis. */
    public static final double DEFAULT_UPPER_BOUND = 1.0;

    /** The maximum tick count. */
    public static final int MAXIMUM_TICK_COUNT = 500;

    /** The default grid line stroke. */
    public static final Stroke DEFAULT_GRID_LINE_STROKE = new BasicStroke(0.5f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_BEVEL,
        0.0f,
        new float[] {2.0f, 2.0f},
        0.0f);

    /** The default grid line paint. */
    public static final Paint DEFAULT_GRID_LINE_PAINT = Color.gray;

    /** The default crosshair visibility. */
    public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;

    /** The default crosshair stroke. */
    public static final Stroke DEFAULT_CROSSHAIR_STROKE =
        DEFAULT_GRID_LINE_STROKE;

    /** The default crosshair paint. */
    public static final Paint DEFAULT_CROSSHAIR_PAINT = Color.blue;

    /** The axis range. */
    private Range range;

    /** Flag that indicates whether the axis automatically scales to fit the chart data.
     */
    private boolean autoRange;

    /**
     * The minimum size for the 'auto' axis range (excluding margins).
     */
    private Number autoRangeMinimumSize;

    /**
     * The upper margin percentage.  This indicates the amount by which the maximum axis value
     * exceeds the maximum data value (as a percentage of the range on the axis) when the axis
     * range is determined automatically.
     */
    private double upperMargin;

    /**
     * The lower margin.  This is a percentage that indicates the amount by
     * which the minimum axis value is "less than" the minimum data value when
     * the axis range is determined automatically.
     */
    private double lowerMargin;

    /** The minimum range length for the axis. */
    private double minimumRangeLength;

    /** The maximum range length for the axis. */
    private double maximumRangeLength;

    /**
     * If this value is positive, the amount is subtracted from the maximum
     * data value to determine the lower axis range.  This can be used to
     * provide a fixed "window" on dynamic data.
     */
    private double fixedAutoRange;

    /** Flag that indicates whether or not the tick unit is selected automatically. */
    private boolean autoTickUnitSelection;

    /** The standard tick units for the axis. */
    private TickUnits standardTickUnits;

    /** An index into an array of standard tick values. */
    private int autoTickIndex;

    /** Flag that indicates whether or not grid lines are visible. */
    private boolean gridLinesVisible;

    /** The stroke used to draw grid lines. */
    private Stroke gridStroke;

    /** The paint used to draw grid lines. */
    private Paint gridPaint;

    /** The anchor value for this axis. */
    private double anchorValue;

    /** A flag that controls whether or not a crosshair is drawn for this axis.*/
    private boolean crosshairVisible;

    /** The crosshair value for this axis. */
    private double crosshairValue;

    /** The pen/brush used to draw the crosshair (if any). */
    private Stroke crosshairStroke;

    /** The color used to draw the crosshair (if any). */
    private Paint crosshairPaint;

    /** A flag that controls whether or not the crosshair locks onto actual data points. */
    private boolean crosshairLockedOnData = true;

    /**
     * Constructs a value axis, using default values where necessary.
     *
     * @param label The axis label.
     */
//    protected ValueAxis(String label) {

//        this(label,
//             Axis.DEFAULT_AXIS_LABEL_FONT,
//             Axis.DEFAULT_AXIS_LABEL_PAINT,
//             Axis.DEFAULT_AXIS_LABEL_INSETS,
//             true, // tick labels visible
//             Axis.DEFAULT_TICK_LABEL_FONT,
//             Axis.DEFAULT_TICK_LABEL_PAINT,
//             Axis.DEFAULT_TICK_LABEL_INSETS,
//             true, // tick marks visible
//             Axis.DEFAULT_TICK_STROKE,
//             Axis.DEFAULT_TICK_PAINT,
//             ValueAxis.DEFAULT_RANGE,
//             ValueAxis.DEFAULT_AUTO_RANGE,
//             ValueAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE,
//             true, // auto tick unit
//             true, // show grid lines
//             ValueAxis.DEFAULT_GRID_LINE_STROKE,
//             ValueAxis.DEFAULT_GRID_LINE_PAINT,
//             0.0,  // anchor value
//             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
//             0.0,  // crosshair
//             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
//             ValueAxis.DEFAULT_CROSSHAIR_PAINT);

//    }

    /**
     * Constructs a value axis.
     *
     * @param label  the axis label.
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the paint used to draw the axis label.
     * @param labelInsets  determines the amount of blank space around the label.
     * @param tickLabelsVisible  flag indicating whether or not the tick labels are visible.
     * @param tickLabelFont  the font used to display tick labels.
     * @param tickLabelPaint  the paint used to draw tick labels.
     * @param tickLabelInsets  determines the amount of blank space around tick labels.
     * @param tickMarksVisible  flag indicating whether or not the tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the paint used to draw tick marks (if visible).
     * @param range  the axis range.
     * @param autoRange  a flag indicating whether or not the axis range is automatically adjusted
     *                   to fit the data.
     * @param autoRangeMinimumSize  the smallest range allowed when the axis range is calculated to
     *                              fit the data.
     * @param autoTickUnitSelection  a flag indicating whether or not the tick unit is
     *                               automatically selected.
     * @param standardTickUnits  a collection of standard tick units.
     * @param gridLinesVisible  flag indicating whether or not grid lines are visible.
     * @param gridStroke  the Stroke used to display grid lines (if visible).
     * @param gridPaint  the Paint used to display grid lines (if visible).
     * @param anchorValue  the anchor value.
     * @param crosshairVisible  whether to show a crosshair.
     * @param crosshairValue  the value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke  the pen/brush used to draw the crosshair.
     * @param crosshairPaint  the color used to draw the crosshair.
     */
    protected ValueAxis(String label,
                        Font labelFont, Paint labelPaint, Insets labelInsets,
                        boolean tickLabelsVisible,
                        Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
                        boolean tickMarksVisible,
                        Stroke tickMarkStroke, Paint tickMarkPaint,
                        Range range,
                        boolean autoRange, Number autoRangeMinimumSize,
                        boolean autoTickUnitSelection,
                        TickUnits standardTickUnits,
                        boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                        double anchorValue,
                        boolean crosshairVisible, double crosshairValue,
                        Stroke crosshairStroke, Paint crosshairPaint) {

        super(label,
              labelFont, labelPaint, labelInsets,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              tickMarksVisible, tickMarkStroke, tickMarkPaint);

        this.range = range;
        this.autoRange = autoRange;
        this.autoRangeMinimumSize = autoRangeMinimumSize;
        this.upperMargin = DEFAULT_UPPER_MARGIN;
        this.lowerMargin = DEFAULT_LOWER_MARGIN;

        this.fixedAutoRange = 0.0;
        this.minimumRangeLength = Double.MIN_VALUE;
        this.maximumRangeLength = Double.MAX_VALUE;
        this.autoTickUnitSelection = autoTickUnitSelection;
        this.standardTickUnits = standardTickUnits;
        this.gridLinesVisible = gridLinesVisible;
        this.gridStroke = gridStroke;
        this.gridPaint = gridPaint;
        this.anchorValue = anchorValue;
        this.crosshairValue = crosshairValue;
        this.crosshairStroke = crosshairStroke;
        this.crosshairPaint = crosshairPaint;

    }

    /**
     * Returns true if the axis range is automatically adjusted to fit the data.
     *
     * @return The auto-range flag.
     */
    public boolean isAutoRange() {
        return autoRange;
    }

    /**
     * Sets a flag that determines whether or not the axis range is
     * automatically adjusted to fit the data, and notifies registered
     * listeners that the axis has been modified.
     *
     * @param auto  Flag indicating whether or not the axis is automatically
     *              scaled to fit the data.
     */
    public void setAutoRange(boolean auto) {

        if (this.autoRange != auto) {
            this.autoRange = auto;
            if (autoRange) {
                autoAdjustRange();
            }
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Sets the auto range attribute, with no other side effects.
     *
     * @param auto  the new value of the flag.
     */
    protected void setAutoRangeAttribute(boolean auto) {
        this.autoRange = auto;
    }

    /**
     * Returns the minimum size of the automatic axis range (if indeed it is
     * determined automatically).
     *
     * @return The minimum range.
     */
    public Number getAutoRangeMinimumSize() {
        return this.autoRangeMinimumSize;
    }

    /**
     * Sets the minimum size of the automatic axis range.
     *
     * @param size The new minimum.
     */
    public void setAutoRangeMinimumSize(Number size) {

        // check argument...
        if (size == null) {
            throw new IllegalArgumentException(
                "NumberAxis.setAutoRangeMinimumSize(Number): null not permitted.");
        }

        // make the change...
        if (autoRangeMinimumSize.doubleValue() != size.doubleValue()) {
            this.autoRangeMinimumSize = size;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Sets the auto range minimum size, with no other side effects.
     *
     * @param size  the new size.
     */
    protected void setAutoRangeMinimumSizeAttribute(Number size) {
        this.autoRangeMinimumSize = size;
    }

    /**
     * Returns the margin (a percentage of the current range) by which the upper bound for the
     * axis exceeds the maximum data value.
     *
     * @return the upper margin.
     */
    public double getUpperMargin() {
        return this.upperMargin;
    }

    /**
     * Sets the upper margin for the axis, as a percentage of the current range.
     * <P>
     * This margin is added only when the axis range is auto-calculated.
     * <P>
     * The default is 5 percent.
     *
     * @param margin  the new margin.
     */
    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the margin (a percentage of the current range) by which the lower bound for the
     * axis is less than the minimum data value.
     *
     * @return the lower margin.
     */
    public double getLowerMargin() {
        return this.lowerMargin;
    }

    /**
     * Sets the lower margin for the axis, as a percentage of the current range.
     * <P>
     * This margin is added only when the axis range is auto-calculated.
     * <P>
     * The default is 5 percent.
     *
     * @param margin  the new margin.
     */
    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        if (isAutoRange()) {
            autoAdjustRange();
        }
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the fixed auto range.
     *
     * @return the length.
     */
    public double getFixedAutoRange() {
        return this.fixedAutoRange;
    }

    /**
     * Sets the fixed auto range for the axis.
     *
     * @param length  the range length.
     */
    public void setFixedAutoRange(double length) {

        this.fixedAutoRange = length;
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Returns the minimum value for the axis.
     *
     * @return The minimum value for the axis.
     */
    public double getMinimumAxisValue() {
        return range.getLowerBound();
    }

    /**
     * Sets the minimum value for the axis.
     * <P>
     * Registered listeners are notified that the axis has been modified.
     *
     * @param min     The new minimum.
     */
    public void setMinimumAxisValue(double min) {

        setRange(new Range(min, range.getUpperBound()));

    }

    /**
     * Returns the maximum value for the axis.
     *
     * @return The maximum value.
     */
    public double getMaximumAxisValue() {
        return range.getUpperBound();
    }

    /**
     * Sets the maximum value for the axis.
     * <P>
     * Registered listeners are notified that the axis has been modified.
     *
     * @param max   The new maximum.
     */
    public void setMaximumAxisValue(double max) {

        setRange(new Range(range.getLowerBound(), max));

    }

    /**
     * Returns the range for the axis.
     *
     * @return The axis range.
     */
    public Range getRange() {
        return range;
    }

    /**
     * Sets the upper and lower bounds for the axis.  Registered listeners are
     * notified of the change.
     * <P>
     * As a side-effect, the auto-range flag is set to false.
     *
     * @param range  The new range.
     */
    public void setRange(Range range) {

        // check arguments...
        if (range == null) {
            throw new IllegalArgumentException("ValueAxis.setRange(...): null not permitted.");
        }

        this.autoRange = false;
        this.range = range;
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Sets the range attribute without any other side effects.
     *
     * @param range  the range.
     */
    protected void setRangeAttribute(Range range) {
        this.range = range;
    }

    /**
     * Sets the axis range.
     *
     * @param lower  The lower axis limit.
     * @param upper  The upper axis limit.
     */
    public void setRange(double lower, double upper) {

        setRange(new Range(lower, upper));

    }

    /**
     * Sets the axis range, where the new range is 'size' in length, and centered on 'value'.
     *
     * @param value  the central value.
     * @param length  the range length.
     */
    public void setRangeAboutValue(double value, double length) {

        setRange(new Range(value - length / 2, value + length / 2));

    }

    /**
     * Returns a flag indicating whether or not the tick unit is automatically
     * selected from a range of standard tick units.
     *
     * @return A flag indicating whether or not the tick unit is automatically selected.
     */
    public boolean isAutoTickUnitSelection() {
        return autoTickUnitSelection;
    }

    /**
     * Sets a flag indicating whether or not the tick unit is automatically
     * selected from a range of standard tick units.
     * <P>
     * Registered listeners are notified of a change to the axis.
     *
     * @param flag  The new value of the flag.
     */
    public void setAutoTickUnitSelection(boolean flag) {

        if (this.autoTickUnitSelection != flag) {
            this.autoTickUnitSelection = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Sets the auto tick unit selection flag without any side effects.
     *
     * @param flag  the new value of the flag.
     */
    protected void setAutoTickUnitSelectionAttribute(boolean flag) {
        this.autoTickUnitSelection = flag;
    }

    /**
     * Returns the standard tick units for the axis.
     * <P>
     * If autoTickUnitSelection is on, the tick unit for the axis will be
     * automatically selected from this collection.
     *
     * @return The standard tick units.
     */
    public TickUnits getStandardTickUnits() {
        return this.standardTickUnits;
    }

    /**
     * Sets the collection of tick units for the axis, and notifies registered
     * listeners that the axis has changed.
     * <P>
     * If the autoTickUnitSelection flag is true, a tick unit will be selected
     * from this collection automatically (to ensure that labels do not
     * overlap).
     *
     * @param collection    The tick unit collection.
     */
    public void setStandardTickUnits(TickUnits collection) {

        this.standardTickUnits = collection;
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Returns true if the grid lines are showing, and false otherwise.
     *
     * @return The gridlines flag.
     */
    public boolean isGridLinesVisible() {
        return gridLinesVisible;
    }

    /**
     * Sets the visibility of the grid lines and notifies registered listeners
     * that the axis has been modified.
     *
     * @param flag  The new setting.
     */
    public void setGridLinesVisible(boolean flag) {

        if (gridLinesVisible != flag) {
            gridLinesVisible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the Stroke used to draw the grid lines (if visible).
     *
     * @return The gridline stroke.
     */
    public Stroke getGridStroke() {
        return gridStroke;
    }

    /**
     * Sets the Stroke used to draw the grid lines (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param stroke The new grid line stroke.
     */
    public void setGridStroke(Stroke stroke) {

        // check arguments...
        if (stroke == null) {
            throw new IllegalArgumentException("ValueAxis.setGridStroke(...): null not permitted");
        }

        // make the change...
        gridStroke = stroke;
        notifyListeners(new AxisChangeEvent(this));

    }

    /**
     * Returns the grid line color.
     *
     * @return The grid line color.
     */
    public Paint getGridPaint() {
        return gridPaint;
    }

    /**
     * Sets the Paint used to color the grid lines (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param paint  The new grid paint.
     */
    public void setGridPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException("ValueAxis.setGridPaint(...): null not permitted");
        }
        gridPaint = paint;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the anchor value for this axis.
     *
     * @return The anchor value.
     */
    public double getAnchorValue() {
        return anchorValue;
    }

    /**
     * Sets the anchor value for this axis.
     *
     * @param value  The new anchor value.
     */
    public void setAnchorValue(double value) {
        this.anchorValue = value;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Sets the anchor value, with no other side effects.
     *
     * @param value  the value.
     */
    protected void setAnchorValueAttribute(double value) {
        this.anchorValue = value;
    }

    /**
     * Returns a flag indicating whether or not a crosshair is visible for this axis.
     *
     * @return The flag.
     */
    public boolean isCrosshairVisible() {
        return this.crosshairVisible;
    }

    /**
     * Sets the flag indicating whether or not a crosshair is visible for this axis.
     *
     * @param flag  The new value of the flag.
     */
    public void setCrosshairVisible(boolean flag) {

        if (this.crosshairVisible != flag) {
            this.crosshairVisible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns a flag indicating whether or not the crosshair should "lock-on"
     * to actual data values.
     *
     * @return The flag.
     */
    public boolean isCrosshairLockedOnData() {
        return this.crosshairLockedOnData;
    }

    /**
     * Sets the flag indicating whether or not the crosshair should "lock-on"
     * to actual data values.
     *
     * @param flag  The flag.
     */
    public void setCrosshairLockedOnData(boolean flag) {

        if (this.crosshairLockedOnData != flag) {
            this.crosshairLockedOnData = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the crosshair value.
     *
     * @return The crosshair value.
     */
    public double getCrosshairValue() {
        return this.crosshairValue;
    }

    /**
     * Sets the crosshair value for the axis.
     * <P>
     * Registered listeners are notified that the axis has been modified, but
     * only if the crosshair is visible.
     *
     * @param value The new value (null permitted).
     */
    public void setCrosshairValue(double value) {

        setCrosshairValue(value, true);

    }

    /**
     * Sets the crosshair value, with no other side effects.
     *
     * @param value  the value.
     */
    protected void setCrosshairValueAttribute(double value) {
        this.crosshairValue = value;
    }

    /**
     * Sets the crosshair value for the axis.
     * <P>
     * Registered listeners are notified that the axis has been modified, but
     * only if the crosshair is visible.
     *
     * @param value The new value.
     * @param notify  a flag that controls whether or not listeners are notified.
     */
    public void setCrosshairValue(double value, boolean notify) {

        this.crosshairValue = value;
        if (isCrosshairVisible() && notify) {
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the Stroke used to draw the crosshair (if visible).
     *
     * @return The crosshair stroke.
     */
    public Stroke getCrosshairStroke() {
        return crosshairStroke;
    }

    /**
     * Sets the Stroke used to draw the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param stroke    The new crosshair stroke.
     */
    public void setCrosshairStroke(Stroke stroke) {
        crosshairStroke = stroke;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the crosshair color.
     *
     * @return The crosshair color.
     */
    public Paint getCrosshairPaint() {
        return crosshairPaint;
    }

    /**
     * Sets the Paint used to color the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param paint The new crosshair paint.
     */
    public void setCrosshairPaint(Paint paint) {
        crosshairPaint = paint;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the
     * axis runs along one edge of the specified dataArea.
     * <p>
     * Note that it is possible for the coordinate to fall outside the plotArea.
     *
     * @param dataValue  The data value.
     * @param dataArea  The area for plotting the data.
     *
     * @return The Java2D coordinate.
     */
    public abstract double translateValueToJava2D(double dataValue, Rectangle2D dataArea);

    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue  The coordinate in Java2D space.
     * @param dataArea  The area in which the data is plotted.
     *
     * @return The data value.
     */
    public abstract double translateJava2DtoValue(float java2DValue, Rectangle2D dataArea);

    /**
     * Automatically determines the maximum and minimum values on the axis to
     * 'fit' the data.
     */
    protected abstract void autoAdjustRange();

    /**
     * Sets the axis range so the the anchor value is in the middle of the axis,
     * and the overall range is equal to the value specified.
     *
     * @param range  The range.
     */
    public void setAnchoredRange(double range) {

        double min = this.anchorValue - range / 2;
        double max = this.anchorValue + range / 2;
        setRange(new Range(min, max));

    }

    /**
     * Centers the axis range about the specified value.
     *
     * @param value  The center value.
     */
    public void centerRange(double value) {

        double central = range.getCentralValue();
        Range adjusted = new Range(range.getLowerBound() + value - central,
                                   range.getUpperBound() + value - central);
        setRange(adjusted);

    }

    /**
     * Increases or decreases the axis range by the specified percentage, about the
     * central value.
     * <P>
     * To double the length of the axis range, use 200% (2.0).
     * To halve the length of the axis range, use 50% (0.5).
     *
     * @param percent  The resize factor.
     */
    public void resizeRange(double percent) {

        this.resizeRange(percent, range.getCentralValue());

    }

    /**
     * Increases or decreases the axis range by the specified percentage, about the
     * specified anchor value.
     * <P>
     * To double the length of the axis range, use 200% (2.0).
     * To halve the length of the axis range, use 50% (0.5).
     *
     * @param percent  The resize factor.
     * @param anchorValue  The new central value after the resize.
     */
    public void resizeRange(double percent, double anchorValue) {

        double halfLength = range.getLength() * percent / 2;
        Range adjusted = new Range(anchorValue - halfLength, anchorValue + halfLength);
        setRange(adjusted);

    }

    /**
     * Returns the auto tick index.
     *
     * @return the auto tick index.
     */
    protected int getAutoTickIndex() {
        return this.autoTickIndex;
    }

    /**
     * Sets the auto tick index.
     *
     * @param index  the new value.
     */
    protected void setAutoTickIndex(int index) {
        this.autoTickIndex = index;
    }

}
