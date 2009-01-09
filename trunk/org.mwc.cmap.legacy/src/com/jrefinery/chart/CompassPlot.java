/* ============================================
 * JFreeChart : a free Java chart class library
 * ============================================
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
 * ----------------
 * CompassPlot.java
 * ----------------
 * (C) Copyright 2002, by the Australian Antarctic Division and Contributors.
 *
 * Original Author:  Bryan Scott (for the Australian Antarctic Division);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: CompassPlot.java,v 1.1.1.1 2003/07/17 10:06:21 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 25-Sep-2002 : Version 1, contributed by Bryan Scott (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import com.jrefinery.data.DefaultMeterDataset;
import com.jrefinery.data.MeterDataset;
import com.jrefinery.chart.needle.MeterNeedle;
import com.jrefinery.chart.needle.PointerNeedle;
import com.jrefinery.chart.needle.LineNeedle;
import com.jrefinery.chart.needle.LongNeedle;
import com.jrefinery.chart.needle.PinNeedle;
import com.jrefinery.chart.needle.PlumNeedle;
import com.jrefinery.chart.needle.ShipNeedle;
import com.jrefinery.chart.needle.WindNeedle;
import com.jrefinery.chart.needle.ArrowNeedle;
import com.jrefinery.chart.event.PlotChangeEvent;

/**
 * A compass plot...
 *
 * @author BS
 */
public class CompassPlot extends Plot {

    /** The default label font. */
    public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.BOLD, 10);

    /** A constant for the label type. */
    public static final int NO_LABELS = 0;

    /** A constant for the label type. */
    public static final int VALUE_LABELS = 1;

    /** The label type (NO_LABELS, VALUE_LABELS). */
    private int labelType;

    /** The label font. */
    private Font labelFont;

    /** A flag that controls whether or not a border is drawn. */
    private boolean drawBorder = false;

    /** The rose highlight color. */
    private Color roseHighlightColour = Color.black;

    /** The rose color. */
    private Color roseColour = Color.yellow;

    /** The rose center color. */
    private Color roseCenterColour = Color.white;

    /** The compass font. */
    private Font compassFont = new Font("Arial", Font.PLAIN, 10);

    /** A working shape. */
    private Ellipse2D.Double circle1;

    /** A working shape. */
    private Ellipse2D.Double circle2;

    /** A working area. */
    private Area a1, a2;

    /** An array of meter datasets. */
    private MeterDataset[] datasets = new MeterDataset[1];

    /** An array of needles. */
    private MeterNeedle[] seriesNeedle = new MeterNeedle[1];

    /**
     * Default constructor.
     */
    public CompassPlot() {
        this(new DefaultMeterDataset());
    }

    /**
     * Constructs a new compass plot.
     *
     * @param data  the dataset for the plot.
     */
    public CompassPlot(MeterDataset data) {

        this(data,
             DEFAULT_INSETS,
             DEFAULT_BACKGROUND_PAINT,
             null,
             DEFAULT_BACKGROUND_ALPHA,
             DEFAULT_OUTLINE_STROKE,
             DEFAULT_OUTLINE_PAINT,
             DEFAULT_FOREGROUND_ALPHA);

    }

    /**
     * Constructs a new plot.
     *
     * @param data  the dataset.
     * @param insets  amount of blank space around the plot area.
     * @param backgroundPaint  an optional color for the plot's background.
     * @param backgroundImage  an optional image for the plot's background.
     * @param backgroundAlpha  alpha-transparency for the plot's background.
     * @param outlineStroke  the Stroke used to draw an outline around the plot.
     * @param outlinePaint  the color used to draw an outline around the plot.
     * @param foregroundAlpha  the alpha-transparency for the plot foreground.
     */
    public CompassPlot(MeterDataset data, Insets insets, Paint backgroundPaint,
                       Image backgroundImage, float backgroundAlpha, Stroke outlineStroke,
                       Paint outlinePaint, float foregroundAlpha) {

        super(data, insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint, foregroundAlpha);

        if (data != null) {
           datasets[0] = data;
           data.addChangeListener(this);
        }
        setInsets(insets);

        circle1 = new Ellipse2D.Double();
        circle2 = new Ellipse2D.Double();
        new Rectangle2D.Double();
        setSeriesNeedle(0);
    }

    /**
     * Returns the label type.  Defined by the constants: NO_LABELS, VALUE_LABELS.
     *
     * @return The label type.
     */
    public int getLabelType() {
        return this.labelType;
    }

    /**
     * Sets the label type.
     * <P>
     * Valid types are defined by the following constants: NO_LABELS, VALUE_LABELS.
     *
     * @param type  the type.
     */
    public void setLabelType(int type) {

        // check the argument...
        if ((type != NO_LABELS) && (type != VALUE_LABELS)) {

            throw new IllegalArgumentException("MeterPlot.setLabelType(int): unrecognised type.");

        }

        // make the change...
        if (labelType != type) {
            this.labelType = type;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the label font.
     *
     * @return the label font.
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the label font.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param font  the new label font.
     */
    public void setLabelFont(Font font) {

        // check arguments...
        if (font == null) {
            throw new IllegalArgumentException("MeterPlot.setLabelFont(...): "
                                               + "null font not allowed.");
        }

        // make the change...
        if (!this.labelFont.equals(font)) {
            this.labelFont = font;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a flag that controls whether or not a border is drawn.
     *
     * @return the flag.
     */
    public boolean getDrawBorder() {
        return drawBorder;
    }

    /**
     * Sets a flag that controls whether or not a border is drawn.
     *
     * @param status  the flag status.
     */
    public void setDrawBorder(boolean status) {
        drawBorder = status;
    }

    /**
     * Sets the series paint.
     *
     * @param series  the series index.
     * @param paint  the paint.
     */
    public void setSeriesPaint(int series, Paint paint) {
        super.setSeriesPaint(series, paint);
        if ((series >= 0) && (series < seriesNeedle.length)) {
            seriesNeedle[series].setFillPaint(paint);
        }
    }

    /**
     * Sets the series outline paint.
     *
     * @param series  the series index.
     * @param p  the paint.
     */
    public void setSeriesOutlinePaint(int series, Paint p) {

        if ((series >= 0) && (series < seriesNeedle.length)) {
            seriesNeedle[series].setOutlinePaint(p);
        }

    }

    /**
     * Sets the series outline stroke.
     *
     * @param series  the series index.
     * @param stroke  the stroke.
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke) {

      super.setSeriesOutlineStroke(series, stroke);
      if ((series >= 0) && (series < seriesNeedle.length)) {
          seriesNeedle[series].setOutlineStroke(stroke);
      }

    }

    /**
     * Sets the needle type.
     *
     * @param type  the type.
     */
    public void setSeriesNeedle(int type) {
        setSeriesNeedle(0, type);
    }

    /**
     * Sets the needle for a series.
     *
     * @param index  the series index.
     * @param type  the needle type.
     */
    public void setSeriesNeedle(int index, int type) {
        switch (type) {
            case 0:
                setSeriesNeedle(index, new ArrowNeedle(true));
                setSeriesPaint(index, Color.red);
                seriesNeedle[index].setHighlightPaint(Color.white);
                break;
            case 1:
                setSeriesNeedle(index, new LineNeedle());
                break;
            case 2:
                setSeriesNeedle(index, new LongNeedle());
                break;
            case 3:
                setSeriesNeedle(index, new PinNeedle());
                break;
            case 4:
                setSeriesNeedle(index, new PlumNeedle());
                break;
            case 5:
                setSeriesNeedle(index, new PointerNeedle());
                break;
            case 6:
                setSeriesPaint(index, null);
                setSeriesOutlineStroke(index, new BasicStroke(3));
                setSeriesNeedle(index, new ShipNeedle());
                break;
            case 7:
                setSeriesPaint(index, Color.blue);
                setSeriesNeedle(index, new WindNeedle());
                break;
            case 8:
                setSeriesNeedle(index, new ArrowNeedle(true));
                break;
        }
    }

    /**
     * Sets the needle for a series.
     *
     * @param index  the series index.
     * @param needle  the needle.
     */
    public void setSeriesNeedle(int index, MeterNeedle needle) {

        if ((needle != null) && (index < seriesNeedle.length)) {
            seriesNeedle[index] = needle;
        }

    }

    /**
     * Returns the dataset.
     * <P>
     * Provided for convenience.
     *
     * @return    The dataset for the plot, cast as a MeterDataset.
     */
    public MeterDataset[] getData() {
        return datasets;
    }

    /**
     * Adds a dataset to the compass.
     *
     * @param data  the new dataset.
     */
    public void addData(MeterDataset data) {
        addData(data, null);
    }

    /**
     * Adds a dataset to the compass.
     *
     * @param data  the new dataset.
     * @param needle  the needle.
     */
    public void addData(MeterDataset data, MeterNeedle needle) {

        if (data != null) {
            int i = datasets.length + 1;
            MeterDataset[] t = new MeterDataset[i];
            MeterNeedle[] p = new MeterNeedle[i];
            i = i - 2;
            for (; i >= 0; --i) {
                t[i] = datasets[i];
                p[i] = seriesNeedle[i];
            }
            i = datasets.length;
            t[i] = data;
            p[i] = ((needle != null) ? needle : p[i - 1]);

            MeterDataset[] a = datasets;
            MeterNeedle[] b = seriesNeedle;
            datasets = t;
            seriesNeedle = p;

            for (--i; i >= 0; --i) {
                a[i] = null;
                b[i] = null;
            }
            data.addChangeListener(this);
        }
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot should be drawn.
     * @param info  collects info about the drawing.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        int outerRadius = 0;
        int innerRadius = 0;
        int x1, y1, x2, y2;
        double a;

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

        // draw the outline and background
        if (drawBorder) {
            drawOutlineAndBackground(g2, plotArea);
        }

        int midX = (int) (plotArea.getWidth() / 2);
        int midY = (int) (plotArea.getHeight() / 2);
        int radius = midX;
        if (midY < midX) {
            radius = midY;
        }
        --radius;
        int diameter = 2 * radius;

        midX += (int) plotArea.getMinX();
        midY += (int) plotArea.getMinY();

        circle1.setFrame(midX - radius, midY - radius, diameter, diameter);
        circle2.setFrame(midX - radius + 15, midY - radius + 15, diameter - 30, diameter - 30);
        g2.setColor(roseColour);
        a1 = new Area(circle1);
        a2 = new Area(circle2);
        a1.subtract(a2);
        g2.fill(a1);

        g2.setColor(roseCenterColour);
        x1 = diameter - 30;
        g2.fillOval(midX - radius + 15, midY - radius + 15, x1, x1);
        g2.setColor(roseHighlightColour);
        g2.drawOval(midX - radius, midY - radius, diameter, diameter);
        x1 = diameter - 20;
        g2.drawOval(midX - radius + 10, midY - radius + 10, x1, x1);
        x1 = diameter - 30;
        g2.drawOval(midX - radius + 15, midY - radius + 15, x1, x1);
        x1 = diameter - 80;
        g2.drawOval(midX - radius + 40, midY - radius + 40, x1, x1);

        outerRadius = radius - 20;
        innerRadius = radius - 32;
        for (int w = 0; w < 360; w += 15) {
            a = Math.toRadians(w);
            x1 = midX - ((int) (Math.sin(a) * innerRadius));
            x2 = midX - ((int) (Math.sin(a) * outerRadius));
            y1 = midY - ((int) (Math.cos(a) * innerRadius));
            y2 = midY - ((int) (Math.cos(a) * outerRadius));
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setColor(roseHighlightColour);
        innerRadius = radius - 26;
        outerRadius = 7;
        for (int w = 45; w < 360; w += 90) {
            a = Math.toRadians(w);
            x1 = midX - ((int) (Math.sin(a) * innerRadius));
            y1 = midY - ((int) (Math.cos(a) * innerRadius));
            g2.fillOval(x1 - outerRadius, y1 - outerRadius, 2 * outerRadius, 2 * outerRadius);
        }

        /// Squares
        for (int w = 0; w < 360; w += 90) {
            a = Math.toRadians(w);
            x1 = midX - ((int) (Math.sin(a) * innerRadius));
            y1 = midY - ((int) (Math.cos(a) * innerRadius));

            Polygon p = new Polygon();
            p.addPoint(x1 - outerRadius, y1);
            p.addPoint(x1, y1 + outerRadius);
            p.addPoint(x1 + outerRadius, y1);
            p.addPoint(x1, y1 - outerRadius);
            g2.fillPolygon(p);
        }

        /// Draw N, S, E, W
        innerRadius = radius - 42;
        Font f = getCompassFont(radius);
        g2.setFont(f);
        g2.drawString("N", midX - 5, midY - innerRadius + f.getSize());
        g2.drawString("S", midX - 5, midY + innerRadius - 5);
        g2.drawString("W", midX - innerRadius + 5, midY + 5);
        g2.drawString("E", midX + innerRadius - f.getSize(), midY + 5);

        // plot the data (unless the dataset is null)...
        y1 = radius / 2;
        x1 = radius / 6;
        Rectangle2D needleArea = new Rectangle2D.Double((midX - x1), (midY - y1),
                                                        (2 * x1), (2 * y1));
        int x = seriesNeedle.length;
        int current = 0;
        double value = 0;
        int i = (datasets.length - 1);
        for (; i >= 0; --i) {
            MeterDataset data = datasets[i];

            if ((data != null) && (data.isValueValid())) {
                value = (data.getValue().doubleValue()) % 360;
                current = i % x;
                seriesNeedle[current].draw(g2, needleArea, value);
            }
        }

    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return a string describing the plot.
     */
    public String getPlotType() {
        return "Compass Plot";
    }

    /**
     * Returns true if the axis is compatible with the compass plot, and false
     * otherwise.  Since a compass plot requires no axes, only a null axis is
     * compatible.
     *
     * @param axis  the axis.
     *
     * @return true if the axis is compatible, and false otherwise.
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
     * Returns true if the axis is compatible with the compass plot, and false
     * otherwise.  Since a compass plot requires no axes, only a null axis is
     * compatible.
     *
     * @param axis  the axis.
     *
     * @return true if the axis is compatible, and false otherwise.
     */
    public boolean isCompatibleVerticalAxis(Axis axis) {

        if (axis == null) {
            return true;
        }
        else {
           return false;
        }

    }

    /**
     * Returns the legend items for the plot.  For now, no legend is available - this method
     * returns null.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {
        return null;
    }

    /**
     * No zooming is implemented for compass plot, so this method is empty.
     *
     * @param percent  the zoom amount.
     */
    public void zoom(double percent) {
    }

    /**
     * Returns the font for the compass.
     *
     * @param radius the radius.
     *
     * @return the font.
     */
    protected Font getCompassFont(int radius) {

        float fontSize = radius / 10;
        if (fontSize < 8) {
            fontSize = 8;
        }

        Font newFont = compassFont.deriveFont(fontSize);
        return newFont;

    }

    /**
     * Returns a list of legend item labels.
     *
     * @return a list of legend item labels.
     *
     * @deprecated use getLegendItems().
     */
    @SuppressWarnings("unchecked")
		public java.util.List getLegendItemLabels() {
        return null;
    }

}
