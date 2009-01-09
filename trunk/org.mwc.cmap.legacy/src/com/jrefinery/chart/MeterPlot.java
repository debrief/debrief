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
 * MeterPlot.java
 * --------------
 * (C) Copyright 2000-2002, by Hari and Contributors.
 *
 * Original Author:  Hari (ourhari@hotmail.com);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Bob Orchard;
 *
 * $Id: MeterPlot.java,v 1.1.1.1 2003/07/17 10:06:25 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 01-Apr-2002 : Version 1, contributed by Hari (DG);
 * 23-Apr-2002 : Moved dataset from JFreeChart to Plot (DG);
 * 22-Aug-2002 : Added changes suggest by Bob Orchard, changed Color to Paint for consistency,
 *               plus added Javadoc comments (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Shape;
import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.List;
import com.jrefinery.data.MeterDataset;
import com.jrefinery.chart.event.PlotChangeEvent;

/**
 * A plot that displays a single value in the context of several ranges ('normal', 'warning'
 * and 'critical').
 *
 * @author Hari
 */
public class MeterPlot extends Plot {

    /** Constant for meter type 'pie'. */
    public static final int DIALTYPE_PIE = 0;

    /** Constant for meter type 'circle'. */
    public static final int DIALTYPE_CIRCLE = 1;

    /** Constant for meter type 'chord'. */
    public static final int DIALTYPE_CHORD = 2;

    /** The default text for the normal level. */
    public static final String NORMAL_TEXT = "Normal";

    /** The default text for the warning level. */
    public static final String WARNING_TEXT = "Warning";

    /** The default text for the critical level. */
    public static final String CRITICAL_TEXT = "Critical";

    /** The default 'normal' level color. */
    static final Paint DEFAULT_NORMAL_PAINT = Color.green;

    /** The default 'warning' level color. */
    static final Paint DEFAULT_WARNING_PAINT = Color.yellow;

    /** The default 'critical' level color. */
    static final Paint DEFAULT_CRITICAL_PAINT = Color.red;

    /** The default background paint. */
    static final Paint DEFAULT_DIAL_BACKGROUND_PAINT = Color.black;

    /** The default needle paint. */
    static final Paint DEFAULT_NEEDLE_PAINT = Color.green;

    /** The default value font. */
    static final Font DEFAULT_VALUE_FONT = new Font("SansSerif", Font.BOLD, 12);

    /** The default value paint. */
    static final Paint DEFAULT_VALUE_PAINT = Color.yellow;

    /** The default meter angle. */
    public static final int DEFAULT_METER_ANGLE = 270;

    /** The default border size. */
    public static final float DEFAULT_BORDER_SIZE = 3f;

    /** The default circle size. */
    public static final float DEFAULT_CIRCLE_SIZE = 10f;

    /** The default background color. */
    public static final Paint DEFAULT_BACKGROUND_PAINT1 = Color.lightGray;

    /** The default label font. */
    public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.BOLD, 10);

    /** Constant for the label type. */
    public static final int NO_LABELS = 0;

    /** Constant for the label type. */
    public static final int VALUE_LABELS = 1;

    /** The dial type (background shape). */
    private int dialType = DIALTYPE_CIRCLE;

    /** The paint for the dial background. */
    private Paint dialBackgroundPaint;

    /** The paint for the needle. */
    private Paint needlePaint;

    /** The font for the value displayed in the center of the dial. */
    private Font valueFont;

    /** The paint for the value displayed in the center of the dial. */
    private Paint valuePaint;

    /** The tick label type (NO_LABELS, VALUE_LABELS). */
    private int tickLabelType;

    /** The tick label font. */
    private Font tickLabelFont;

    /** The 'normal' level color. */
    private Paint normalPaint = DEFAULT_NORMAL_PAINT;

    /** The 'warning' level color. */
    private Paint warningPaint = DEFAULT_WARNING_PAINT;

    /** The 'critical' level color. */
    private Paint criticalPaint = DEFAULT_CRITICAL_PAINT;

    /** The color of the border around the dial. */
    private Color dialBorderColor;

    /** A flag that controls whether or not the border is drawn. */
    private boolean drawBorder;

    /** ??? */
    private int meterCalcAngle = -1;

    /** ??? */
    private double meterRange = -1;

    /** The dial extent. */
    private int meterAngle = DEFAULT_METER_ANGLE;

    /** The minimum meter value. */
    private double minMeterValue = 0.0;

    /**
     * Default constructor.
     *
     * @param data  The dataset.
     */
    public MeterPlot(MeterDataset data) {

        this(data,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             MeterPlot.VALUE_LABELS,
             MeterPlot.DEFAULT_LABEL_FONT);

    }

    /**
     * Constructs a new meter plot.
     *
     * @param data  the data series.
     * @param insets  the plot insets.
     * @param backgroundPaint  the background color.
     * @param backgroundImage  the background image.
     * @param backgroundAlpha  the background alpha-transparency.
     * @param outlineStroke  the outline stroke.
     * @param outlinePaint  the outline paint.
     * @param foregroundAlpha  the foreground alpha-transparency.
     * @param tickLabelType  the label type.
     * @param tickLabelFont  the label font.
     *
     */
    public MeterPlot(MeterDataset data,
                     Insets insets,
                     Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                     Stroke outlineStroke, Paint outlinePaint,
                     float foregroundAlpha,
                     int tickLabelType,
                     Font tickLabelFont) {

        super(data,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint, foregroundAlpha);

        this.dialBackgroundPaint = MeterPlot.DEFAULT_DIAL_BACKGROUND_PAINT;
        this.needlePaint = MeterPlot.DEFAULT_NEEDLE_PAINT;
        this.valueFont = MeterPlot.DEFAULT_VALUE_FONT;
        this.valuePaint = MeterPlot.DEFAULT_VALUE_PAINT;
        this.tickLabelType = tickLabelType;
        this.tickLabelFont = tickLabelFont;

    }

    /**
     * Returns the type of dial (DIALTYPE_PIE, DIALTYPE_CIRCLE, DIALTYPE_CHORD).
     *
     * @return The dial type.
     */
    public int getDialType() {
        return this.dialType;
    }

    /**
     * Sets the dial type (background shape).
     * <P>
     * This controls the shape of the dial background.  Use one of the constants:
     * DIALTYPE_PIE, DIALTYPE_CIRCLE, or DIALTYPE_CHORD.
     *
     * @param type The dial type.
     */
    public void setDialType(int type) {
        this.dialType = type;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the dial background.
     *
     * @return The paint.
     */
    public Paint getDialBackgroundPaint() {
        return this.dialBackgroundPaint;
    }

    /**
     * Sets the paint used to fill the dial background.
     * <P>
     * If you set this to null, it will revert to the default color.
     *
     * @param paint The paint.
     */
    public void setDialBackgroundPaint(Paint paint) {
        this.dialBackgroundPaint = paint == null ? DEFAULT_DIAL_BACKGROUND_PAINT : paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the needle.
     *
     * @return The paint.
     */
    public Paint getNeedlePaint() {
        return this.needlePaint;
    }

    /**
     * Sets the paint used to display the needle.
     * <P>
     * If you set this to null, it will revert to the default color.
     *
     * @param paint The paint.
     */
    public void setNeedlePaint(Paint paint) {
        this.needlePaint = paint == null ? DEFAULT_NEEDLE_PAINT : paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the font for the value label.
     *
     * @return The font.
     */
    public Font getValueFont() {
        return this.valueFont;
    }

    /**
     * Sets the font used to display the value label.
     * <P>
     * If you set this to null, it will revert to the default font.
     *
     * @param font The font.
     */
    public void setValueFont(Font font) {
        this.valueFont = (font == null) ? DEFAULT_VALUE_FONT : font;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the value label.
     *
     * @return The paint.
     */
    public Paint getValuePaint() {
        return this.valuePaint;
    }

    /**
     * Sets the paint used to display the value label.
     * <P>
     * If you set this to null, it will revert to the default paint.
     *
     * @param paint The paint.
     */
    public void setValuePaint(Paint paint) {
        this.valuePaint = paint == null ? DEFAULT_VALUE_PAINT : paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint for the 'normal' level.
     *
     * @return The paint.
     */
    public Paint getNormalPaint() {
        return this.normalPaint;
    }

    /**
     * Sets the paint used to display the 'normal' range.
     * <P>
     * If you set this to null, it will revert to the default color.
     *
     * @param paint The paint.
     */
    public void setNormalPaint(Paint paint) {
        this.normalPaint = (paint == null) ? DEFAULT_NORMAL_PAINT : paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint used to display the 'warning' range.
     *
     * @return The paint.
     */
    public Paint getWarningPaint() {
        return this.warningPaint;
    }

    /**
     * Sets the paint used to display the 'warning' range.
     * <P>
     * If you set this to null, it will revert to the default color.
     *
     * @param paint The paint.
     */
    public void setWarningPaint(Paint paint) {
        this.warningPaint = (paint == null) ? DEFAULT_WARNING_PAINT : paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the paint used to display the 'critical' range.
     *
     * @return The paint.
     */
    public Paint getCriticalPaint() {
        return this.criticalPaint;
    }

    /**
     * Sets the paint used to display the 'critical' range.
     * <P>
     * If you set this to null, it will revert to the default color.
     *
     * @param paint The paint.
     */
    public void setCriticalPaint(Paint paint) {
        this.criticalPaint = (paint == null) ? DEFAULT_CRITICAL_PAINT : paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the tick label type.  Defined by the constants: NO_LABELS,
     * VALUE_LABELS.
     *
     * @return The tick label type.
     */
    public int getTickLabelType() {
        return this.tickLabelType;
    }

    /**
     * Sets the tick label type.
     *
     * @param type  the type of tick labels - either <code>NO_LABELS</code> or
     *      <code>VALUE_LABELS</code>
     */
    public void setTickLabelType(int type) {

        // check the argument...
        if ((type != NO_LABELS) && (type != VALUE_LABELS)) {
            throw new IllegalArgumentException(
                "MeterPlot.setLabelType(int): unrecognised type.");
        }

        // make the change...
        if (tickLabelType != type) {
            this.tickLabelType = type;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the tick label font.
     *
     * @return The tick label font.
     */
    public Font getTickLabelFont() {
        return this.tickLabelFont;
    }

    /**
     * Sets the tick label font and notifies registered listeners that the plot has been changed.
     *
     * @param font  The new tick label font.
     */
    public void setTickLabelFont(Font font) {

        // check arguments...
        if (font == null) {
            throw new IllegalArgumentException(
                "MeterPlot.setTickLabelFont(...): null font not allowed.");
        }

        // make the change...
        if (!this.tickLabelFont.equals(font)) {
            this.tickLabelFont = font;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag that controls whether or not a rectangular border is drawn around the plot
     * area.
     *
     * @return A flag.
     */
    public boolean getDrawBorder() {
        return this.drawBorder;
    }

    /**
     * Sets the flag that controls whether or not a rectangular border is drawn around the plot
     * area.
     * <P>
     * Note:  it looks like the true setting needs some work to provide some insets.
     *
     * @param draw The flag.
     */
    public void setDrawBorder(boolean draw) {
        this.drawBorder = draw;
    }

    /**
     * Returns the meter angle.
     *
     * @return the meter angle.
     */
    public int getMeterAngle() {
        return this.meterAngle;
    }

    /**
     * Sets the range through which the dial's needle is free to rotate.
     *
     * @param angle  the angle.
     */
    public void setMeterAngle(int angle) {
        this.meterAngle = angle;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the color of the border for the dial.
     *
     * @return the color of the border for the dial.
     */
    public Color getDialBorderColor() {
        return this.dialBorderColor;
    }

    /**
     * Sets the color for the border of the dial.
     *
     * @param color  the color.
     */
    public void setDialBorderColor(Color color) {
        this.dialBorderColor = color;
    }

    /**
     * Returns the dataset for the plot, cast as a MeterDataset.
     * <P>
     * Provided for convenience.
     *
     * @return the dataset for the plot, cast as a MeterDataset.
     */
    public MeterDataset getMeterDataset() {
        return (MeterDataset) dataset;
    }

    /**
     * Returns a list of legend item labels.
     *
     * @return the legend item labels.
     *
     * @deprecated use getLegendItems().
     */
    @SuppressWarnings("unchecked")
		public List getLegendItemLabels() {
        return null;
    }

    /**
     * Returns null.
     *
     * @return null.
     */
    public LegendItemCollection getLegendItems() {
        return null;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  The graphics device.
     * @param plotArea  The area within which the plot should be drawn.
     * @param info  Collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

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

        plotArea.setRect(plotArea.getX() + 4,
                         plotArea.getY() + 4,
                         plotArea.getWidth() - 8,
                         plotArea.getHeight() - 8);

        // draw the outline and background
        if (drawBorder) {
            drawOutlineAndBackground(g2, plotArea);
        }

        // adjust the plot area by the interior spacing value
        double gapHorizontal = (2 * DEFAULT_BORDER_SIZE);
        double gapVertical = (2 * DEFAULT_BORDER_SIZE);
        double meterX = plotArea.getX() + gapHorizontal / 2;
        double meterY = plotArea.getY() + gapVertical / 2;
        double meterW = plotArea.getWidth() - gapHorizontal;
        double meterH = plotArea.getHeight() - gapVertical
                        + ((meterAngle <= 180) && (dialType != DIALTYPE_CIRCLE)
                           ? plotArea.getHeight() / 1.25 : 0);

        {
            double min = Math.min(meterW, meterH) / 2;
            meterX = (meterX + meterX + meterW) / 2 - min;
            meterY = (meterY + meterY + meterH) / 2 - min;
            meterW = 2 * min;
            meterH = 2 * min;
        }

        Rectangle2D meterArea = new Rectangle2D.Double(meterX,
                                                       meterY,
                                                       meterW,
                                                       meterH);

        Rectangle2D.Double originalArea = new Rectangle2D.Double(meterArea.getX() - 4,
                                                                 meterArea.getY() - 4,
                                                                 meterArea.getWidth() + 8,
                                                                 meterArea.getHeight() + 8);

        double meterMiddleX = meterArea.getCenterX();
        double meterMiddleY = meterArea.getCenterY();

        // plot the data (unless the dataset is null)...
        MeterDataset data = getMeterDataset();
        if (data != null) {
            double dataMin = data.getMinimumValue().doubleValue();
            double dataMax = data.getMaximumValue().doubleValue();
            minMeterValue = dataMin;

            meterCalcAngle = 180 + ((meterAngle - 180) / 2);
            meterRange = dataMax - dataMin;

            Shape savedClip = g2.getClip();
            g2.clip(originalArea);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.foregroundAlpha));

            drawArc(g2, originalArea, dataMin, dataMax, this.dialBackgroundPaint, 1);
            drawTicks(g2, meterArea, dataMin, dataMax);
            drawArcFor(g2, meterArea, data, MeterDataset.FULL_DATA);
            drawArcFor(g2, meterArea, data, MeterDataset.NORMAL_DATA);
            drawArcFor(g2, meterArea, data, MeterDataset.WARNING_DATA);
            drawArcFor(g2, meterArea, data, MeterDataset.CRITICAL_DATA);

            if (data.isValueValid()) {

                double dataVal = data.getValue().doubleValue();
                drawTick(g2, meterArea, dataVal, true, this.valuePaint, true, data.getUnits());

                g2.setPaint(this.needlePaint);
                g2.setStroke(new BasicStroke(2.0f));

                double radius = (meterArea.getWidth() / 2) + DEFAULT_BORDER_SIZE + 15;
                double valueAngle = calculateAngle(dataVal);
                double valueP1 = meterMiddleX + (radius * Math.cos(Math.PI * (valueAngle / 180)));
                double valueP2 = meterMiddleY - (radius * Math.sin(Math.PI * (valueAngle / 180)));

                Polygon arrow = new Polygon();
                if ((valueAngle > 135 && valueAngle < 225)
                    || (valueAngle < 45 && valueAngle > -45)) {

                    double valueP3 = (meterMiddleY - DEFAULT_CIRCLE_SIZE / 4);
                    double valueP4 = (meterMiddleY + DEFAULT_CIRCLE_SIZE / 4);
                    arrow.addPoint((int) meterMiddleX, (int) valueP3);
                    arrow.addPoint((int) meterMiddleX, (int) valueP4);

                }
                else {
                    arrow.addPoint((int) (meterMiddleX - DEFAULT_CIRCLE_SIZE / 4),
                                   (int) meterMiddleY);
                    arrow.addPoint((int) (meterMiddleX + DEFAULT_CIRCLE_SIZE / 4),
                                   (int) meterMiddleY);
                }
                arrow.addPoint((int) valueP1, (int) valueP2);

                Ellipse2D circle = new Ellipse2D.Double(meterMiddleX - DEFAULT_CIRCLE_SIZE / 2,
                                                        meterMiddleY - DEFAULT_CIRCLE_SIZE / 2,
                                                        DEFAULT_CIRCLE_SIZE,
                                                        DEFAULT_CIRCLE_SIZE);
                g2.fill(arrow);
                g2.fill(circle);

            }

            g2.clip(savedClip);
            g2.setComposite(originalComposite);

        }

    }

    /**
     * Draws a colored range (arc) for one level.
     *
     * @param g2 The graphics device.
     * @param meterArea The drawing area.
     * @param data The dataset.
     * @param type The level.
     */
    void drawArcFor(Graphics2D g2, Rectangle2D meterArea, MeterDataset data, int type) {

        Number minValue = null;
        Number maxValue = null;
        Paint paint = null;

        switch (type) {

            case MeterDataset.NORMAL_DATA:
                minValue = data.getMinimumNormalValue();
                maxValue = data.getMaximumNormalValue();
                paint = getNormalPaint();
                break;

            case MeterDataset.WARNING_DATA:
                minValue = data.getMinimumWarningValue();
                maxValue = data.getMaximumWarningValue();
                paint = getWarningPaint();
                break;

            case MeterDataset.CRITICAL_DATA:
                minValue = data.getMinimumCriticalValue();
                maxValue = data.getMaximumCriticalValue();
                paint = getCriticalPaint();
                break;

            case MeterDataset.FULL_DATA:
                minValue = data.getMinimumValue();
                maxValue = data.getMaximumValue();
                paint = DEFAULT_BACKGROUND_PAINT1;
                break;

            default:
                return;
        }

        if (minValue != null && maxValue != null) {
            if (data.getBorderType() == type) {
                drawArc(g2, meterArea,
                        minValue.doubleValue(),
                        data.getMinimumValue().doubleValue(),
                        paint);
                drawArc(g2, meterArea,
                        data.getMaximumValue().doubleValue(),
                        maxValue.doubleValue(),
                        paint);
            }
            else {
                drawArc(g2, meterArea,
                        minValue.doubleValue(),
                        maxValue.doubleValue(),
                        paint);
            }

            // draw a tick at each end of the range...
            drawTick(g2, meterArea, minValue.doubleValue(), true, paint);
            drawTick(g2, meterArea, maxValue.doubleValue(), true, paint);
        }

    }

    /**
     * Draws an arc.
     *
     * @param g2  the graphics device.
     * @param area  the plot area.
     * @param minValue  the minimum value.
     * @param maxValue  the maximum value.
     * @param paint  the paint.
     */
    void drawArc(Graphics2D g2, Rectangle2D area, double minValue, double maxValue, Paint paint) {
        drawArc(g2, area, minValue, maxValue, paint, 0);
    }

    /**
     * Draws an arc.
     *
     * @param g2  the graphics device.
     * @param area  the plot area.
     * @param minValue  the minimum value.
     * @param maxValue  the maximum value.
     * @param paint  the paint.
     * @param outlineType  the outline type.
     */
    void drawArc(Graphics2D g2, Rectangle2D area, double minValue, double maxValue,
                 Paint paint, int outlineType) {

        double startAngle = calculateAngle(maxValue);
        double endAngle = calculateAngle(minValue);
        double extent = endAngle - startAngle;

        double x = area.getX();
        double y = area.getY();
        double w = area.getWidth();
        double h = area.getHeight();
        g2.setPaint(paint);

        if (outlineType > 0) {
            g2.setStroke(new BasicStroke(10.0f));
        }
        else {
            g2.setStroke(new BasicStroke(DEFAULT_BORDER_SIZE));
        }

        int joinType = Arc2D.OPEN;
        if (outlineType > 0) {
            switch (dialType) {
                case DIALTYPE_PIE:
                    joinType = Arc2D.PIE;
                    break;
                case DIALTYPE_CHORD:
                    if (meterAngle > 180) {
                        joinType = Arc2D.CHORD;
                    }
                    else {
                        joinType = Arc2D.PIE;
                    }
                    break;
                case DIALTYPE_CIRCLE:
                    joinType = Arc2D.PIE;
                    extent = 360;
                    break;
            }
        }
        Arc2D.Double arc = new Arc2D.Double(x, y, w, h, startAngle, extent, joinType);
        if (outlineType > 0) {
            g2.fill(arc);
        }
        else {
            g2.draw(arc);
        }

    }

    /**
     * Calculate an angle ???
     *
     * @param value  the value.
     *
     * @return the result.
     */
    double calculateAngle(double value) {
        value -= minMeterValue;
        double ret = meterCalcAngle - ((value / meterRange) * meterAngle);
        return ret;
    }

    /**
     * Draws the ticks.
     *
     * @param g2  the graphics device.
     * @param meterArea  the meter area.
     * @param minValue  the minimum value.
     * @param maxValue  the maximum value.
     */
    void drawTicks(Graphics2D g2, Rectangle2D meterArea, double minValue, double maxValue) {

        int numberOfTicks = 20;
        double diff = (maxValue - minValue) / numberOfTicks;

        for (double i = minValue; i <= maxValue; i += diff) {
            drawTick(g2, meterArea, i);
        }

    }

    /**
     * Draws a tick.
     *
     * @param g2  the graphics device.
     * @param meterArea  the meter area.
     * @param value  the value.
     */
    void drawTick(Graphics2D g2, Rectangle2D meterArea, double value) {
        drawTick(g2, meterArea, value, false, null, false, null);
    }

    /**
     * Draws a tick.
     *
     * @param g2  the graphics device.
     * @param meterArea  the meter area.
     * @param value  the value.
     * @param label  the label.
     * @param color  the color.
     */
    void drawTick(Graphics2D g2, Rectangle2D meterArea, double value, boolean label, Paint color) {
        drawTick(g2, meterArea, value, label, color, false, null);
    }

    /**
     * Draws a tick on the chart (also handles a special case [curValue=true] that draws the
     * value in the middle of the dial).
     *
     * @param g2  the graphics device.
     * @param meterArea  the meter area.
     * @param value  the tick value.
     * @param label  a flag that controls whether or not a value label is drawn.
     * @param labelPaint  the label color.
     * @param curValue  a flag for the special case of the current value.
     * @param units  the unit-of-measure for the dial.
     */
    void drawTick(Graphics2D g2, Rectangle2D meterArea,
                  double value, boolean label, Paint labelPaint, boolean curValue, String units) {

        double valueAngle = calculateAngle(value);

        double meterMiddleX = meterArea.getCenterX();
        double meterMiddleY = meterArea.getCenterY();

        if (labelPaint == null) {
            labelPaint = Color.white;
        }
        g2.setPaint(labelPaint);
        g2.setStroke(new BasicStroke(2.0f));

        double valueP2X = 0;
        double valueP2Y = 0;

        if (!curValue) {
            double radius = (meterArea.getWidth() / 2) + DEFAULT_BORDER_SIZE;
            double radius1 = radius - 15;

            double valueP1X = meterMiddleX + (radius * Math.cos(Math.PI * (valueAngle / 180)));
            double valueP1Y = meterMiddleY - (radius * Math.sin(Math.PI * (valueAngle / 180)));

            valueP2X = meterMiddleX + (radius1 * Math.cos(Math.PI * (valueAngle / 180)));
            valueP2Y = meterMiddleY - (radius1 * Math.sin(Math.PI * (valueAngle / 180)));

            Line2D.Double line = new Line2D.Double(valueP1X, valueP1Y, valueP2X, valueP2Y);
            g2.draw(line);
        }
        else {
            valueP2X = meterMiddleX;
            valueP2Y = meterMiddleY;
            valueAngle = 90;
        }

        if (this.tickLabelType == VALUE_LABELS && label) {

            DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
            String tickLabel =  df.format(value);
            if (curValue && units != null) {
                tickLabel += " " + units;
            }
            if (curValue) {
                g2.setFont(this.getValueFont());
            }
            else {
                if (tickLabelFont != null) {
                    g2.setFont(tickLabelFont);
                }
            }

            Rectangle2D tickLabelBounds = g2.getFont().getStringBounds(tickLabel,
                                                                       g2.getFontRenderContext());

            double x = valueP2X;
            double y = valueP2Y;
            if (curValue) {
                y += DEFAULT_CIRCLE_SIZE;
            }
            if (valueAngle == 90 || valueAngle == 270) {
                x = x - tickLabelBounds.getWidth() / 2;
            }
            else if (valueAngle < 90 || valueAngle > 270) {
                x = x - tickLabelBounds.getWidth();
            }
            if ((valueAngle > 135 && valueAngle < 225) || valueAngle > 315 || valueAngle < 45) {
                y = y - tickLabelBounds.getHeight() / 2;
            }
            else {
                y = y + tickLabelBounds.getHeight() / 2;
            }
            g2.drawString(tickLabel, (float) x, (float) y);
        }
    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return always <i>Meter Plot</i>.
     */
    public String getPlotType() {
        return "Meter Plot";
    }

    /**
     * A zoom method that does nothing.
     * <p>
     * Plots are required to support the zoom operation.  In the case of a pie
     * chart, it doesn't make sense to zoom in or out, so the method is empty.
     *
     * @param percent   The zoom percentage.
     */
    public void zoom(double percent) {
    }

}
