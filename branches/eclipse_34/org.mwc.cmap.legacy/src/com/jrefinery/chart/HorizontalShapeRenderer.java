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
 * ----------------------------
 * HorizontalShapeRenderer.java
 * ----------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalShapeRenderer.java,v 1.1.1.1 2003/07/17 10:06:24 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 15-Jul-2002 : Version 1, based on LineAndShapeRenderer (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 10-Oct-2002 : Added URL generator (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.CategoryItemEntity;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.Range;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A renderer for a HorizontalCategoryPlot that draws shapes for each data item, and lines between
 * data items.  The renderer is immutable so that the only way to change the renderer for a plot
 * is to call the setRenderer() method.
 *
 * @author DG
 */
public class HorizontalShapeRenderer extends AbstractCategoryItemRenderer {

    /** Useful constant for specifying the type of rendering (shapes only). */
    public static final int SHAPES = 1;

    /** Useful constant for specifying the type of rendering (lines only). */
    public static final int LINES = 2;

    /** Useful constant for specifying the type of rendering (shapes and lines). */
    public static final int SHAPES_AND_LINES = 3;

    /** Constant indicating that labels are to be shown above data points */
    public static final int TOP = 1;

    /** Constant indicating that labels are to be shown below data points */
    public static final int BOTTOM = 2;

    /** Constant indicating that labels are to be shown left of data points */
    public static final int LEFT = 3;

    /** Constant indicating that labels are to be shown right of data points */
    public static final int RIGHT = 4;

    /** A flag indicating whether or not shapes are drawn at each XY point. */
    private boolean plotShapes;

    /** A flag indicating whether or not lines are drawn between XY points. */
    private boolean plotLines;

    /** Scale factor for standard shapes. */
    private double shapeScale = 10;

    /** Location of labels (if shown) relative to the data points. */
    private int labelPosition;

    /**
     * Constructs a default renderer (draws shapes and lines).
     */
    public HorizontalShapeRenderer() {
        this(SHAPES, RIGHT);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type The type of renderer.
     */
    public HorizontalShapeRenderer(int type) {
        this(type, RIGHT);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type The type of renderer.
     * @param labelPosition Location of labels (if shown) relative to the data points
     *                      (TOP, BOTTOM, LEFT, or RIGHT).
     */
    public HorizontalShapeRenderer(int type, int labelPosition) {
        this(type, labelPosition, null, null);
    }

    /**
     * Constructs a renderer of the specified type.
     * <P>
     * Use one of the constants SHAPES, LINES or SHAPES_AND_LINES.
     *
     * @param type The type of renderer.
     * @param labelPosition Location of labels (if shown) relative to the data points
     *                      (TOP, BOTTOM, LEFT, or RIGHT).
     * @param toolTipGenerator  the tool tip generator (null permitted).
     * @param urlGenerator  the URL generator (null permitted).
     */
    public HorizontalShapeRenderer(int type, int labelPosition,
                                   CategoryToolTipGenerator toolTipGenerator,
                                   CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

        if (type == SHAPES) {
            this.plotShapes = true;
        }

        if (type == LINES) {
            this.plotLines = true;
        }

        if (type == SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }

        this.labelPosition = labelPosition;
    }

    /**
     * Returns the area that the axes must fit into.  Often this is the same as the plotArea, but
     * sometimes a smaller region should be used (for example, the 3D charts require the axes to
     * use less space in order to leave room for the 'depth' part of the chart).
     *
     * @param plotArea  the plot area.
     *
     * @return the axis area.
     */
    public Rectangle2D getAxisArea(Rectangle2D plotArea) {
        return plotArea;
    }

    /**
     * Draws a vertical line on the chart to represent the marker.
     *
     * @param g2 The graphics device.
     * @param plot The plot.
     * @param axis The value axis.
     * @param marker The marker line.
     * @param axisDataArea The axis data area.
     * @param dataClipRegion The data clip region.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot, ValueAxis axis, Marker marker,
                                Rectangle2D axisDataArea, Shape dataClipRegion) {

        double value = marker.getValue();
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double x = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(x, axisDataArea.getMinY(),
                                        x, axisDataArea.getMaxY());
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(line);

    }

    /**
     * Draw a single data item.
     *
     * @param g2 The graphics device.
     * @param dataArea The area in which the data is drawn.
     * @param plot The plot.
     * @param axis The range axis.
     * @param data The data.
     * @param series The series number (zero-based index).
     * @param category The category.
     * @param categoryIndex The category number (zero-based index).
     * @param previousCategory The previous category (will be null when the first category is
     *                         drawn).
     */
    public void drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                 CategoryPlot plot,
                                 ValueAxis axis,
                                 CategoryDataset data,
                                 int series,
                                 Object category, int categoryIndex, Object previousCategory) {

        // first check the number we are plotting...
        Number value = data.getValue(series, category);
        if (value != null) {

            // current data point...
            double x1 = axis.translateValueToJava2D(value.doubleValue(), dataArea);
            double y1 = plot.getCategoryCoordinate(categoryIndex, dataArea);

            g2.setPaint(plot.getSeriesPaint(series));
            g2.setStroke(plot.getSeriesStroke(series));

            Shape shape = null;
            if (this.plotShapes) {
                shape = plot.getShape(series, category, x1, y1, shapeScale);
                g2.fill(shape);
            }
            else {
                shape = new Rectangle2D.Double(x1 - 2, y1 - 2, 4.0, 4.0);
            }

            if (this.plotLines) {
                if (previousCategory != null) {

                    Number previousValue = data.getValue(series, previousCategory);
                    if (previousValue != null) {

                        // previous data point...
                        double previous = previousValue.doubleValue();
                        double x0 = axis.translateValueToJava2D(previous, dataArea);
                        double y0 = plot.getCategoryCoordinate(categoryIndex - 1, dataArea);

                        g2.setPaint(plot.getSeriesPaint(series));
                        g2.setStroke(plot.getSeriesStroke(series));
                        Line2D line = new Line2D.Double(x0, y0, x1, y1);
                        g2.draw(line);
                    }

                }
            }

            if (plot.getLabelsVisible()) {
                NumberFormat formatter = plot.getLabelFormatter();
                Font labelFont = plot.getLabelFont();
                g2.setFont(labelFont);
                Paint paint = plot.getLabelPaint();
                g2.setPaint(paint);
                boolean rotate = plot.getVerticalLabels();
                String label = formatter.format(value);
                drawLabel(g2, label, x1, y1, labelFont, rotate);
            }

            // collect entity and tool tip information...
            if (getInfo() != null) {
                EntityCollection entities = getInfo().getEntityCollection();
                if (entities != null && shape != null) {
                    String tip = null;
                    if (getToolTipGenerator() != null) {
                        tip = getToolTipGenerator().generateToolTip(data, series, category);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(data, series, category);
                    }

                    CategoryItemEntity entity = new CategoryItemEntity(shape, tip, series, category,
                                                                       categoryIndex);
                    entities.addEntity(entity);
                }
            }

        }

    }

    /**
     * Draws a value label on the plot.
     *
     * @param g2  the graphics device.
     * @param label  the label text.
     * @param x  the x position of the data point.
     * @param y  the y position of the data point.
     * @param labelFont  the font to draw the label with.
     * @param rotate  true if the label is to be rotated 90 degrees, false otherwise.
     */
    private void drawLabel(Graphics2D g2, String label, double x, double y,
                           Font labelFont, boolean rotate) {

         FontRenderContext frc = g2.getFontRenderContext();
         Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
         LineMetrics lm = labelFont.getLineMetrics(label, frc);
         float lead = lm.getLeading();
         double width = labelBounds.getWidth();
         double height = labelBounds.getHeight();
         float labelx;
         float labely;
         int position = labelPosition;
         if (rotate) {
             if (position == TOP) {
                 labelx = (float) (x + height / 2 - lm.getDescent());
                 labely = (float) (y - shapeScale);
             }
             else if (position == BOTTOM) {
                 labelx = (float) (x + height / 2 - lm.getDescent());
                 labely = (float) (y + shapeScale + width);
             }
             else if (position == LEFT) {
                 labelx = (float) (x - shapeScale / 2 - lead - lm.getDescent());
                 labely = (float) (y + width / 2);
             }
             else {
                 labelx = (float) (x + shapeScale / 2 + lead + lm.getAscent());
                 labely = (float) (y + width / 2);
             }
             RefineryUtilities.drawRotatedString(label, g2, labelx, labely, -Math.PI / 2);
         }
         else {
             if (position == TOP) {
                 labelx = (float) (x - width / 2);
                 labely = (float) (y - shapeScale / 2 - lm.getDescent() - lead);
             }
             else if (position == BOTTOM) {
                 labelx = (float) (x - width / 2);
                 labely = (float) (y + shapeScale / 2 + lm.getAscent() + lead);
             }
             else if (position == LEFT) {
                 labelx = (float) (x - shapeScale - width);
                 labely = (float) (y + height / 2 - lm.getDescent());
             }
             else {
                 labelx = (float) (x + shapeScale);
                 labely = (float) (y + height / 2 - lm.getDescent());
             }
             g2.drawString(label, labelx, labely);
         }

    }

}
