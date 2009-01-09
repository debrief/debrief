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
 * --------------------
 * ThermometerPlot.java
 * --------------------
 *
 * (C) Copyright 2000-2002,
 *
 * Original Author:  Bryan Scott (based on MeterPlot by Hari).
 * Contributor(s):   David Gilbert (for Simba Management Limited).
 *
 * Changes
 * -------
 * 11-Apr-2002 : Version 1, contributed by Bryan Scott;
 * 15-Apr-2002 : Changed to implement VerticalValuePlot;
 * 29-Apr-2002 : Added getVerticalValueAxis() method (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 17-Sep-2002 : Reviewed with Checkstyle utility (DG);
 * 18-Sep-2002 : Extensive changes made to API, to iron out bugs and inconsistencies (DG);
 * 13-Oct-2002 : Corrected error datasetChanged which would generate exceptions when value set 
 *               to null (BRS).
 *
 */

package com.jrefinery.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.List;
import com.jrefinery.chart.event.PlotChangeEvent;
import com.jrefinery.data.MeterDataset;
import com.jrefinery.data.DefaultMeterDataset;
import com.jrefinery.data.Range;
import com.jrefinery.data.DatasetChangeEvent;

/**
 * A plot that displays a single value in a thermometer type display.
 * <p>
 * NOTE:
 * The Thermometer plot utilises a meter data set, however range options within this data set
 * are not used (instead, the ranges can be set as attributes on the plot).
 *
 * This plot supports a number of options:
 * <ol>
 * <li>three sub-ranges which could be viewed as 'Normal', 'Warning' and 'Critical' ranges.</li>
 * <li>the thermometer can be run in two modes:
 *      <ul>
 *      <li>fixed range, or</li>
 *      <li>range adjusts to current sub-range.</li>
 *      </ul>
 * </li>
 * <li>settable units to be displayed.</li>
 * <li>settable display location for the value text.</li>
 * </ol>
 *
 * @author BRS
 */
public class ThermometerPlot extends Plot implements VerticalValuePlot {

    /** A constant for unit type 'None'. */
    public static final int UNITS_NONE = 0;

    /** A constant for unit type 'Fahrenheit'. */
    public static final int UNITS_FAHRENHEIT = 1;

    /** A constant for unit type 'Celcius'. */
    public static final int UNITS_CELCIUS = 2;

    /** A constant for unit type 'Kelvin'. */
    public static final int UNITS_KELVIN = 3;

    /** A constant for the value label position (no label). */
    public static final int NONE = 0;

    /** A constant for the value label position (right of the thermometer). */
    public static final int RIGHT = 1;

    /** A constant for the value label position (in the thermometer bulb). */
    public static final int BULB = 2;

    /** A constant for the 'normal' range. */
    public static final int NORMAL   = 0;

    /** A constant for the 'warning' range. */
    public static final int WARNING  = 1;

    /** A constant for the 'critical' range. */
    public static final int CRITICAL = 2;

    /** The bulb radius. */
    protected static final int BULB_RADIUS = 40;

    /** The bulb diameter. */
    protected static final int BULB_DIAMETER = BULB_RADIUS * 2;

    /** The column radius. */
    protected static final int COLUMN_RADIUS = 20;

    /** The column diameter.*/
    protected static final int COLUMN_DIAMETER = COLUMN_RADIUS * 2;

    /** The gap radius. */
    protected static final int GAP_RADIUS = 5;

    /** The gap diameter. */
    protected static final int GAP_DIAMETER = GAP_RADIUS * 2;

    /** The axis gap. */
    protected static final int AXIS_GAP = 10;

    /** The unit strings. */
    protected static final String[] UNITS = { "", "°F", "°C", "°K" };

    /** Index for low value in subrangeInfo matrix. */
    protected static final int RANGE_LOW = 0;

    /** Index for high value in subrangeInfo matrix. */
    protected static final int RANGE_HIGH = 1;

    /** Index for display low value in subrangeInfo matrix. */
    protected static final int DISPLAY_LOW = 2;

    /** Index for display high value in subrangeInfo matrix. */
    protected static final int DISPLAY_HIGH  = 3;

    /** The default lower bound. */
    protected static final double DEFAULT_LOWER_BOUND = 0.0;

    /** The default upper bound. */
    protected static final double DEFAULT_UPPER_BOUND = 100.0;

    /** The dataset for the plot. */
    private MeterDataset data;

    /** The range axis. */
    private ValueAxis rangeAxis;

    /** The lower bound for the thermometer. */
    private double lowerBound = DEFAULT_LOWER_BOUND;

    /** The upper bound for the thermometer. */
    private double upperBound = DEFAULT_UPPER_BOUND;

    /** Blank space inside the plot area around the outside of the thermometer. */
    private Spacer padding;

    /** Stroke for drawing the thermometer */
    private Stroke thermometerStroke = new BasicStroke(1.0f);

    /** Paint for drawing the thermometer */
    private Paint thermometerPaint = Color.black;

    /** The display units */
    private int units = UNITS_CELCIUS;

    /** The value label position. */
    private int valueLocation = BULB;

    /** The font to write the value in */
    private Font valueFont = new Font("SansSerif", Font.BOLD, 16);

    /** Colour that the value is written in */
    private Paint valuePaint = Color.white;

    /** Number format for the value */
    private NumberFormat valueFormat = new DecimalFormat();

    /** The default paint for the mercury in the thermometer. */
    private Paint mercuryPaint = Color.lightGray;

    /** A flag that controls whether value lines are drawn. */
    private boolean showValueLines = false;

    /** The display sub-range. */
    private int subrange = -1;

    /** The start and end values for the subranges. */
    private double[][] subrangeInfo = {
        { 0.0,  50.0,  0.0,  50.0 },
        {50.0,  75.0, 50.0,  75.0 },
        {75.0, 100.0, 75.0, 100.0 }
    };

    /** A flag that controls whether or not the axis range adjusts to the sub-ranges. */
    private boolean followDataInSubranges = false;

    /** A flag that controls whether or not the mercury paint changes with the subranges. */
    private boolean useSubrangePaint = true;

    /** Paint for each range */
    private Paint[] subrangePaint = {
        Color.green,
        Color.orange,
        Color.red
    };

    /** A flag that controls whether the sub-range indicators are visible. */
    private boolean subrangeIndicatorsVisible = true;

    /** The stroke for the sub-range indicators. */
    private Stroke subrangeIndicatorStroke = new BasicStroke(2.0f);

    /**
     * Creates a new thermometer plot.
     */
    public ThermometerPlot() {
        this(new DefaultMeterDataset(new Double(Double.MIN_VALUE),
                                     new Double(Double.MAX_VALUE),
                                     null, null
                                     ));
    }

    /**
     * Creates a new thermometer plot, using default attributes where necessary.
     *
     * @param data  the data set.
     */
    public ThermometerPlot(MeterDataset data) {

        this(data,
             DEFAULT_INSETS,
             DEFAULT_BACKGROUND_PAINT,
             null,  // background image
             DEFAULT_BACKGROUND_ALPHA,
             DEFAULT_OUTLINE_STROKE,
             DEFAULT_OUTLINE_PAINT,
             DEFAULT_FOREGROUND_ALPHA);

    }

    /**
     * Constructs a new thermometer plot.
     *
     * @param data  the data set.
     * @param insets  the amount of blank space around the plot area.
     * @param backgroundPaint  an optional color for the plot's background.
     * @param backgroundImage  an optional image for the plot's background.
     * @param backgroundAlpha  the alpha-transparency for the plot's background.
     * @param outlineStroke  the Stroke used to draw an outline around the plot.
     * @param outlinePaint  the color used to draw an outline around the plot.
     * @param foregroundAlpha  the alpha-transparency for the plot foreground.
     */
    public ThermometerPlot(MeterDataset data,
                           Insets insets,
                           Paint backgroundPaint, Image backgroundImage,
                           float backgroundAlpha,
                           Stroke outlineStroke, Paint outlinePaint,
                           float foregroundAlpha) {

        super(data, insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint, foregroundAlpha);

        this.padding = new Spacer(Spacer.RELATIVE, 0.05, 0.05, 0.05, 0.05);
        this.data = data;
        if (data != null) {
            data.addChangeListener(this);
        }
        setInsets(insets);
        VerticalNumberAxis axis = new VerticalNumberAxis(null);
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        setRangeAxis(axis);
        setAxisRange();
    }

    /**
     * Returns the dataset cast to MeterDataset (provided for convenience).
     *
     * @return  the dataset for the plot, cast as a MeterDataset.
     */
    public MeterDataset getData() {
        return data;
    }


    /**
     * Sets the data for the chart, replacing any existing data.
     * <P>
     * Registered listeners are notified that the plot has been modified (this will normally
     * trigger a chart redraw).
     *
     * @param data  the new dataset.
     */
    public void setData(MeterDataset data) {

        // if there is an existing dataset, remove the chart from the list of
        // change listeners...
        MeterDataset existing = this.data;
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        // set the new dataset, and register the plot as a change listener...
        this.data = data;
        if (this.data != null) {
            data.addChangeListener(this);
        }

        // notify plot change listeners...
        PlotChangeEvent event = new PlotChangeEvent(this);
        notifyListeners(event);

    }

    /**
     * Returns the range axis.
     *
     * @return the range axis.
     */
    public ValueAxis getRangeAxis() {
        return this.rangeAxis;
    }

    /**
     * Sets the range axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually compatible.
     *
     * @param axis  the new axis.
     *
     * @throws AxisNotCompatibleException if the axis is not compatible with the plot.
     */
    public void setRangeAxis(ValueAxis axis) throws AxisNotCompatibleException {

        if (isCompatibleRangeAxis(axis)) {

            if (axis != null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "Plot.setRangeAxis(...): plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.rangeAxis != null) {
                this.rangeAxis.removeChangeListener(this);
            }

            this.rangeAxis = axis;

        }
        else {
            throw new AxisNotCompatibleException(
                "Plot.setRangeAxis(...): axis not compatible with plot.");
        }
    }

    /**
     * Returns the lower bound for the thermometer.
     * <p>
     * The data value can be set lower than this, but it will not be shown in the thermometer.
     *
     * @return the lower bound.
     *
     */
    public double getLowerBound() {
        return this.lowerBound;
    }

    /**
     * Sets the lower bound for the thermometer.
     *
     * @param lower the lower bound.
     */
    public void setLowerBound(double lower) {
        this.lowerBound = lower;
        setAxisRange();
    }

    /**
     * Returns the upper bound for the thermometer.
     * <p>
     * The data value can be set higher than this, but it will not be shown in the thermometer.
     *
     * @return the upper bound.
     *
     */
    public double getUpperBound() {
        return this.upperBound;
    }

    /**
     * Sets the upper bound for the thermometer.
     *
     * @param upper the upper bound.
     */
    public void setUpperBound(double upper) {
        this.upperBound = upper;
        setAxisRange();
    }

    /**
     * Sets the lower and upper bounds for the thermometer.
     *
     * @param lower  the lower bound.
     * @param upper  the upper bound.
     */
    public void setRange(double lower, double upper) {
        this.lowerBound = lower;
        this.upperBound = upper;
        setAxisRange();
    }

    /**
     * Returns the padding for the thermometer.  This is the space inside the plot area.
     *
     * @return the padding.
     */
    public Spacer getPadding() {
        return this.padding;
    }

    /**
     * Sets the padding for the thermometer.
     *
     * @param padding  the padding.
     */
    public void setPadding(Spacer padding) {
        this.padding = padding;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the stroke used to draw the thermometer outline.
     *
     * @return the stroke.
     */
    public Stroke getThermometerStroke() {
        return this.thermometerStroke;
    }

    /**
     * Sets the stroke used to draw the thermometer outline.
     *
     * @param s  the new stroke (null ignored).
     */
    public void setThermometerStroke(Stroke s) {
        if (s != null) {
            this.thermometerStroke = s;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the paint used to draw the thermometer outline.
     *
     * @return the paint.
     */
    public Paint getThermometerPaint() {
        return this.thermometerPaint;
    }

    /**
     * Sets the paint used to draw the thermometer outline.
     *
     * @param paint  the new paint (null ignored).
     */
    public void setThermometerPaint(Paint paint) {
        if (paint != null) {
            this.thermometerPaint = paint;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the unit display type (none/Fahrenheit/Celcius/Kelvin).
     *
     * @return  the units type.
     */
    public int getUnits() {
        return units;
    }

    /**
     * Sets the units to be displayed in the thermometer.
     * <p>
     * Use one of the following constants:
     *
     * <ul>
     * <li>UNITS_NONE : no units displayed.</li>
     * <li>UNITS_FAHRENHEIT : units displayed in Fahrenheit.</li>
     * <li>UNITS_CELCIUS : units displayed in Celcius.</li>
     * <li>UNITS_KELVIN : units displayed in Kelvin.</li>
     * </ul>
     *
     * @param u  the new unit type.
     */
    public void setUnits(int u) {
        if ((u >= 0) && (u < UNITS.length)) {
            if (this.units != u) {
                this.units = u;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
    }

    /**
     * Sets the unit type.
     *
     * @param u  the unit type (null ignored).
     */
    public void setUnits(String u) {
        if (u == null) {
            return;
        }

        u = u.toUpperCase().trim();
        for (int i = 0; i < UNITS.length; ++i) {
            if (u.equals(UNITS[i].toUpperCase().trim())) {
                setUnits(i);
                i = UNITS.length;
            }
        }
    }

    /**
     * Returns the value location.
     *
     * @return the location.
     */
    public int getValueLocation() {
        return this.valueLocation;
    }

    /**
     * Sets the location at which the current value is displayed.
     * <P>
     * The location can be one of the constants:  NONE, RIGHT and BULB.
     *
     * @param location  the location.
     *
     * @throws IllegalArgumentException if the location code is not valid.
     */
    public void setValueLocation(int location) {
        if ((location >= 0) && (location < 3)) {
            valueLocation = location;
            notifyListeners(new PlotChangeEvent(this));
        }
        else {
            throw new IllegalArgumentException(
                "ThermometerPlot.setDisplayLocation: location not recognised.");
        }
    }

    /**
     * Gets the font used to display the current value.
     *
     * @return  the font.
     */
    public Font getValueFont() {
        return this.valueFont;
    }

    /**
     * Sets the font used to display the current value.
     *
     * @param f  the new font.
     */
    public void setValueFont(Font f) {
        if ((f != null) && (!this.valueFont.equals(f))) {
            this.valueFont = f;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Gets the paint used to display the current value.
     *
     * @return  the paint.
     */
    public Paint getValuePaint() {
        return this.valuePaint;
    }

    /**
     * Sets the paint used to display the current value.
     *
     * @param p  the new paint.
     */
    public void setValuePaint(Paint p) {
        if ((p != null) && (!this.valuePaint.equals(p))) {
            this.valuePaint = p;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Sets the formatter for the value label.
     *
     * @param formatter  the new formatter.
     */
    public void setValueFormat(NumberFormat formatter) {
        if (formatter != null) {
            this.valueFormat = formatter;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the default mercury paint.
     *
     * @return the paint.
     */
    public Paint getMercuryPaint() {
        return this.mercuryPaint;
    }

    /**
     * Sets the default mercury paint.
     *
     * @param paint  the new paint.
     */
    public void setMercuryPaint(Paint paint) {
        this.mercuryPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the flag that controls whether not value lines are displayed.
     *
     * @return the flag.
     */
    public boolean getShowValueLines() {
        return this.showValueLines;
    }

    /**
     * Sets the display as to whether to show value lines in the output.
     *
     * @param b Whether to show value lines in the thermometer
     */
    public void setShowValueLines(boolean b) {
        this.showValueLines = b;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Sets information for a particular range.
     *
     * @param range  the range to specify information about.
     * @param low  the low value for the range
     * @param hi  the high value for the range
     */
    public void setSubrangeInfo(int range, double low, double hi) {
        setSubrangeInfo(range, low, hi, low, hi);
    }

    /**
     * Sets the subrangeInfo attribute of the ThermometerPlot object
     *
     * @param range  the new rangeInfo value.
     * @param rangeLow  the new rangeInfo value
     * @param rangeHigh  the new rangeInfo value
     * @param displayLow  the new rangeInfo value
     * @param displayHigh  the new rangeInfo value
     */
    public void setSubrangeInfo(int range,
                                double rangeLow, double rangeHigh,
                                double displayLow, double displayHigh) {

        if ((range >= 0) && (range < 3)) {
            setSubrange(range, rangeLow, rangeHigh);
            setDisplayRange(range, displayLow, displayHigh);
            setAxisRange();
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Sets the range.
     *
     * @param range  the range type.
     * @param low  the low value.
     * @param high  the high value.
     */
    public void setSubrange(int range, double low, double high) {
        if ((range >= 0) && (range < 3)) {
            subrangeInfo[range][RANGE_HIGH] = high;
            subrangeInfo[range][RANGE_LOW] = low;
        }
    }

    /**
     * Sets the display range.
     *
     * @param range  the range type.
     * @param low  the low value.
     * @param high  the high value.
     */
    public void setDisplayRange(int range, double low, double high) {

        if ((range >= 0) && (range < subrangeInfo.length)
            && isValidNumber(high) && isValidNumber(low)) {

            if (high > low) {
                subrangeInfo[range][DISPLAY_HIGH] = high;
                subrangeInfo[range][DISPLAY_LOW] = low;
            }
            else {
                subrangeInfo[range][DISPLAY_HIGH] = high;
                subrangeInfo[range][DISPLAY_LOW] = low;
            }

        }

    }

    /**
     * Gets the paint used for a particular subrange.
     *
     * @param range  the range.
     *
     * @return the paint.
     */
    public Paint getSubrangePaint(int range) {

        if ((range >= 0) && (range < subrangePaint.length)) {
            return subrangePaint[range];
        }
        else {
            return this.mercuryPaint;
        }

    }

    /**
     * Sets the paint to be used for a range.
     *
     * @param range  the range.
     * @param paint  the paint to be applied.
     */
    public void setSubrangePaint(int range, Paint paint) {
        if ((range >= 0) && (range < subrangePaint.length) && (paint != null)) {
            subrangePaint[range] = paint;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns a flag that controls whether or not the thermometer axis zooms to display the
     * subrange within which the data value falls.
     *
     * @return the flag.
     */
    public boolean getFollowDataInSubranges() {
        return this.followDataInSubranges;
    }

    /**
     * Sets the flag that controls whether or not the thermometer axis zooms to display the
     * subrange within which the data value falls.
     *
     * @param flag  the flag.
     */
    public void setFollowDataInSubranges(boolean flag) {
        this.followDataInSubranges = flag;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns a flag that controls whether or not the mercury color changes for each
     * subrange.
     *
     * @return the flag.
     */
    public boolean getUseSubrangePaint() {
        return this.useSubrangePaint;
    }

    /**
     * Sets the range colour change option.
     *
     * @param flag The new range colour change option
     */
    public void setUseSubrangePaint(boolean flag) {
        this.useSubrangePaint = flag;
        notifyListeners(new PlotChangeEvent(this));
    }


    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot should be drawn.
     * @param info  collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        RoundRectangle2D outerStem = new RoundRectangle2D.Double();

        RoundRectangle2D innerStem = new RoundRectangle2D.Double();

        RoundRectangle2D mercuryStem = new RoundRectangle2D.Double();

        Ellipse2D outerBulb = new Ellipse2D.Double();

        Ellipse2D innerBulb = new Ellipse2D.Double();;

        String temp = null;
        FontMetrics metrics = null;

        if (info != null) {
            info.setPlotArea(plotArea);
        }

        // adjust for insets...
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
            plotArea.getY() + insets.top,
            plotArea.getWidth() - insets.left - insets.right,
            plotArea.getHeight() - insets.top - insets.bottom);
        }
        drawOutlineAndBackground(g2, plotArea);

        // adjust for padding...
        //this.padding.trim(plotArea);

        int midX = (int) (plotArea.getX() + (plotArea.getWidth() / 2));
        int midY = (int) (plotArea.getY() + (plotArea.getHeight() / 2));
        int stemTop = (int) (plotArea.getMinY() + BULB_RADIUS);
        int stemBottom  = (int) (plotArea.getMaxY() - BULB_DIAMETER);
        Rectangle2D dataArea = new Rectangle2D.Double(midX - COLUMN_RADIUS,
                                                      stemTop,
                                                      COLUMN_RADIUS,
                                                      stemBottom - stemTop);

        outerBulb.setFrame(midX - BULB_RADIUS,
                           stemBottom,
                           BULB_DIAMETER,
                           BULB_DIAMETER);

        outerStem.setRoundRect(midX - COLUMN_RADIUS,
                               plotArea.getMinY(),
                               COLUMN_DIAMETER,
                               stemBottom + BULB_DIAMETER - stemTop,
                               COLUMN_DIAMETER,
                               COLUMN_DIAMETER);

        Area outerThermometer = new Area(outerBulb);
        Area tempArea = new Area(outerStem);
        outerThermometer.add(tempArea);

        innerBulb.setFrame(midX - BULB_RADIUS + GAP_RADIUS,
                           stemBottom + GAP_RADIUS,
                           BULB_DIAMETER - GAP_DIAMETER,
                           BULB_DIAMETER - GAP_DIAMETER);

        innerStem.setRoundRect(midX - COLUMN_RADIUS + GAP_RADIUS,
                               plotArea.getMinY()  + GAP_RADIUS,
                               COLUMN_DIAMETER - GAP_DIAMETER,
                               stemBottom + BULB_DIAMETER - GAP_DIAMETER - stemTop,
                               COLUMN_DIAMETER - GAP_DIAMETER,
                               COLUMN_DIAMETER - GAP_DIAMETER);

        Area innerThermometer = new Area(innerBulb);
        tempArea = new Area(innerStem);
        innerThermometer.add(tempArea);

        if ((data != null) && (data.isValueValid())) {
            double current = data.getValue().doubleValue();

            double ds = rangeAxis.translateValueToJava2D(current, dataArea);

            int i = COLUMN_DIAMETER - GAP_DIAMETER;  // already calculated
            int j = COLUMN_RADIUS - GAP_RADIUS;      // already calculated
            int l = (int) (i / 2);
            //k = (int) (ticksYStop - Math.round(ds));
            int k = (int) Math.round(ds);
            if (k < (GAP_RADIUS + plotArea.getMinY())) {
                k = (int) (GAP_RADIUS + plotArea.getMinY());
                l = BULB_RADIUS;
            }

            Area mercury = new Area(innerBulb);

            if (k < (stemBottom + BULB_RADIUS)) {
                mercuryStem.setRoundRect(midX - j, k, i, (stemBottom + BULB_RADIUS) - k, l, l);
                tempArea = new Area(mercuryStem);
                mercury.add(tempArea);
            }

            g2.setPaint(getCurrentPaint());
            g2.fill(mercury);

            // draw the axis...
            int drawWidth = AXIS_GAP;
            if (showValueLines) {
                drawWidth += COLUMN_DIAMETER;
            }
            Rectangle2D drawArea = new Rectangle2D.Double(midX - COLUMN_RADIUS - AXIS_GAP,
                                                          stemTop,
                                                          drawWidth,
                                                          (stemBottom - stemTop + 1));
            this.rangeAxis.draw(g2, plotArea, drawArea);

            // draw range indicators...
            if (this.subrangeIndicatorsVisible) {
                g2.setStroke(this.subrangeIndicatorStroke);
                Range range = rangeAxis.getRange();

                // draw start of normal range
                double value = this.subrangeInfo[NORMAL][RANGE_LOW];
                if (range.contains(value)) {
                    double x = midX + COLUMN_RADIUS + 2;
                    double y = rangeAxis.translateValueToJava2D(value, dataArea);
                    Line2D line = new Line2D.Double(x, y, x + 10, y);
                    g2.setPaint(subrangePaint[NORMAL]);
                    g2.draw(line);
                }

                // draw start of warning range
                value = this.subrangeInfo[WARNING][RANGE_LOW];
                if (range.contains(value)) {
                    double x = midX + COLUMN_RADIUS + 2;
                    double y = rangeAxis.translateValueToJava2D(value, dataArea);
                    Line2D line = new Line2D.Double(x, y, x + 10, y);
                    g2.setPaint(subrangePaint[WARNING]);
                    g2.draw(line);
                }

                // draw start of critical range
                value = this.subrangeInfo[CRITICAL][RANGE_LOW];
                if (range.contains(value)) {
                    double x = midX + COLUMN_RADIUS + 2;
                    double y = rangeAxis.translateValueToJava2D(value, dataArea);
                    Line2D line = new Line2D.Double(x, y, x + 10, y);
                    g2.setPaint(subrangePaint[CRITICAL]);
                    g2.draw(line);
                }
            }

            // draw text value on screen
            g2.setFont(this.valueFont);
            g2.setPaint(this.valuePaint);
            metrics = g2.getFontMetrics();
            switch (valueLocation) {
                case RIGHT:
                    g2.drawString(valueFormat.format(current),
                                  midX + COLUMN_RADIUS + GAP_RADIUS, midY);
                    break;
                case BULB:
                    temp = valueFormat.format(current);
                    i = (int) (metrics.stringWidth(temp) / 2);
                    g2.drawString(temp, midX - i, stemBottom + BULB_RADIUS + GAP_RADIUS);
                    break;
                default:
            }
        }

        g2.setPaint(thermometerPaint);
        g2.setFont(valueFont);

        //  draw units indicator
        metrics = g2.getFontMetrics();
        int tickX1 = midX - COLUMN_RADIUS - GAP_DIAMETER - metrics.stringWidth(UNITS[units]);
        if (tickX1 > plotArea.getMinX()) {
            g2.drawString(UNITS[units], tickX1, (int) (plotArea.getMinY() + 20));
        }

        // draw thermometer outline
        g2.setStroke(thermometerStroke);
        g2.draw(outerThermometer);
        g2.draw(innerThermometer);

    }

    /**
     * A zoom method that does nothing.
     * <p>
     * Plots are required to support the zoom operation.  In the case of a
     * thermometer chart, it doesn't make sense to zoom in or out, so the
     * method is empty.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) { }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return  a short string describing the type of plot.
     */
    public String getPlotType() {
        return "Thermometer Plot";
    }

    /**
     * Checks to see if a new value means the axis range needs adjusting.
     *
     * @param event  the dataset change event.
     */
    public void datasetChanged(DatasetChangeEvent event) {

        Number vn = data.getValue();
        if (vn != null) {
            double value = vn.doubleValue();
            if (inSubrange(NORMAL, value)) {
                this.subrange = NORMAL;
            }
            else if (inSubrange(WARNING, value)) {
                this.subrange = WARNING;
            }
            else if (inSubrange(CRITICAL, value)) {
                this.subrange = CRITICAL;
            }
			else {
                this.subrange = -1;
            }
            setAxisRange();
        }
        super.datasetChanged(event);
    }

    /**
     * Returns the minimum value in either the domain or the range, whichever
     * is displayed against the vertical axis for the particular type of plot
     * implementing this interface.
     *
     * @return the minimum value in either the domain or the range.
     */
    public Number getMinimumVerticalDataValue() {
        return new Double(this.lowerBound);
    }

    /**
     * Returns the maximum value in either the domain or the range, whichever
     * is displayed against the vertical axis for the particular type of plot
     * implementing this interface.
     *
     * @return the maximum value in either the domain or the range
     */
    public Number getMaximumVerticalDataValue() {
        return new Double(this.upperBound);
    }

    /**
     * Returns the vertical data range.
     *
     * @return the range of data displayed.
     */
    public Range getVerticalDataRange() {
        return new Range(this.lowerBound, this.upperBound);
    }

    /**
     * Returns true if the axis is compatible with the meter plot.  Since a
     * Thermometer plot requires no horizontal axis, only a null axis is compatible.
     *
     * @param axis  the axis.
     *
     * @return  true, if the axis is null, and false otherwise.
     */
    public boolean isCompatibleHorizontalAxis(Axis axis) {
        if (axis == null) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns true if the axis is compatible with the meter plot.  Since a Thermometer plot
     * requires a VerticalNumberAxis, only a VerticalNumberAxis axis is compatible.
     *
     * @param axis  the axis.
     *
     * @return  true, if the axis is compatible with the plot, and false otherwise.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {
        if (axis instanceof VerticalNumberAxis) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Fires a plot change event.
     *
     * @deprecated this method is not required and will be removed.
     */
    public void propertyChange() {
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Sets the axis range to the current values in the rangeInfo array.
     */
    protected void setAxisRange() {
        if ((this.subrange >= 0) && (this.followDataInSubranges)) {
            rangeAxis.setRange(new Range(subrangeInfo[subrange][DISPLAY_LOW],
                                         subrangeInfo[subrange][DISPLAY_HIGH]));
        }
        else {
            rangeAxis.setRange(this.lowerBound, this.upperBound);
        }
    }

    /**
     * Returns null, since the thermometer plot won't require a legend.
     *
     * @return null.
     *
     * @deprecated use getLegendItems().
     */
    @SuppressWarnings("unchecked")
		public List getLegendItemLabels() {
        return null;
    }

    /**
     * Returns the legend items for the plot.
     *
     * @return null.
     */
    public LegendItemCollection getLegendItems() {
        return null;
    }

    /**
     * Checks the compatibility of a range axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis The proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public boolean isCompatibleRangeAxis(ValueAxis axis) {

        if (axis == null) {
            return true;
        }
        if (axis instanceof VerticalAxis) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the vertical value axis.
     * <p>
     * This is required by the VerticalValuePlot interface, but not used in this class.
     *
     * @return the vertical value axis.
     */
    public ValueAxis getVerticalValueAxis() {
        return this.rangeAxis;
    }

    /**
     * Determine whether a number is valid and finite.
     *
     * @param d  the number to be tested.
     *
     * @return true if the number is valid and finite, and false otherwise.
     */
    protected static boolean isValidNumber(double d) {
        return (!(Double.isNaN(d) || Double.isInfinite(d)));
    }

    /**
     * Returns true if the value is in the specified range, and false otherwise.
     *
     * @param subrange1  the subrange.
     * @param value  the value to check.
     *
     * @return true or false.
     */
    private boolean inSubrange(int subrange1, double value) {
        return (value > subrangeInfo[subrange1][RANGE_LOW]
                && value <= subrangeInfo[subrange1][RANGE_HIGH]);
    }

    /**
     * Returns the mercury paint corresponding to the current data value.
     *
     * @return the paint.
     */
    private Paint getCurrentPaint() {

        Paint result = this.mercuryPaint;
        if (this.useSubrangePaint) {
            double value = this.data.getValue().doubleValue();
            if (inSubrange(NORMAL, value)) {
                result = this.subrangePaint[NORMAL];
            }
            else if (inSubrange(WARNING, value)) {
                result = this.subrangePaint[WARNING];
            }
            else if (inSubrange(CRITICAL, value)) {
                result = this.subrangePaint[CRITICAL];
            }
        }
        return result;
    }

}

