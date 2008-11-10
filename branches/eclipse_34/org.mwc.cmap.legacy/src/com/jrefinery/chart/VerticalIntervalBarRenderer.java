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
 * --------------------------------
 * VerticalIntervalBarRenderer.java
 * --------------------------------
 * (C) Copyright 2002, by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: VerticalIntervalBarRenderer.java,v 1.1.1.1 2003/07/17 10:06:28 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 29-Apr-2002 : Version 1, contributed by Jeremy Bowman (DG);
 * 11-May-2002 : Use CategoryPlot.getLabelsVisible() (JB);
 * 29-May-2002 : Added constructors (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 20-Sep-2002 : Added basic support for chart entities (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
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
import com.jrefinery.data.IntervalCategoryDataset;
import com.jrefinery.data.Range;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A renderer that handles the drawing of bars for a vertical bar plot where
 * each bar has a high and low value.
 *
 * @author JB
 */
public class VerticalIntervalBarRenderer extends BarRenderer implements CategoryItemRenderer {

    /** Constant indicating a low value label */
    private static final int LOW_LABEL = 0;

    /** Constant indicating a high value label */
    private static final int HIGH_LABEL = 1;

    /**
     * Constructs a new renderer.
     */
    public VerticalIntervalBarRenderer() {
        this(null, null);
    }

    /**
     * Constructs a new renderer.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public VerticalIntervalBarRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Constructs a new renderer.
     *
     * @param urlGenerator  the url generator.
     */
    public VerticalIntervalBarRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Constructs a new renderer.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public VerticalIntervalBarRenderer(CategoryToolTipGenerator toolTipGenerator,
                                       CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be plotted.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param data  the data.
     * @param info  collects chart rendering information for return to caller.
     *
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ValueAxis axis,
                           CategoryDataset data,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, axis, data, info);
        this.calculateCategoryAndItemSpans(g2, dataArea, plot, data, dataArea.getWidth());

    }

    /**
     * Draws a marker for the range axis.
     * <P>
     * A marker is a constant value, usually represented by a line.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param marker  the marker to be drawn.
     * @param axisDataArea  the area inside the axes.
     * @param dataClipRegion  the data clip area.
     */
    public void drawRangeMarker(Graphics2D g2,
                                CategoryPlot plot, ValueAxis axis, Marker marker,
                                Rectangle2D axisDataArea, Shape dataClipRegion) {

        double value = marker.getValue();
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }

        double y = axis.translateValueToJava2D(marker.getValue(), axisDataArea);
        Line2D line = new Line2D.Double(axisDataArea.getMinX(), y,
                                        axisDataArea.getMaxX(), y);
        g2.setPaint(marker.getOutlinePaint());
        g2.draw(line);

    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param data  the data.
     * @param series  the series number (zero-based index).
     * @param category  the category.
     * @param categoryIndex  the category number (zero-based index).
     * @param previousCategory  the previous category.
     */
    public void drawCategoryItem(Graphics2D g2, Rectangle2D dataArea,
                                 CategoryPlot plot, ValueAxis axis,
                                 CategoryDataset data, int series, Object category,
                                 int categoryIndex, Object previousCategory) {

        IntervalCategoryDataset intervalData = (IntervalCategoryDataset) data;

        // BAR X
        double rectX = dataArea.getX() + dataArea.getWidth() * plot.getIntroGapPercent();

        int categories = data.getCategoryCount();
        int seriesCount = data.getSeriesCount();
        if (categories > 1) {
            rectX = rectX
                    // bars in completed categories
                    + categoryIndex * (categorySpan / categories)
                    // gaps between completed categories
                    + (categoryIndex * (categoryGapSpan / (categories - 1))
                    // bars+gaps completed in current category
                    + (series * itemSpan / (categories * seriesCount)));
            if (seriesCount > 1) {
                rectX = rectX
                        + (series * itemGapSpan / (categories * (seriesCount - 1)));
            }
        }
        else {
            rectX = rectX
                    // bars+gaps completed in current category
                    + (series * itemSpan / (categories * seriesCount));
            if (seriesCount > 1) {
                rectX = rectX
                        + (series * itemGapSpan / (categories * (seriesCount - 1)));
            }
        }

        // Y0
        Number value0 = intervalData.getEndValue(series, category);
        double translatedValue0 = axis.translateValueToJava2D(value0.doubleValue(), dataArea);

        // Y1
        Number value1 = intervalData.getStartValue(series, category);
        double translatedValue1 = axis.translateValueToJava2D(value1.doubleValue(), dataArea);

        if (translatedValue1 < translatedValue0) {
            double temp = translatedValue1;
            translatedValue1 = translatedValue0;
            translatedValue0 = temp;
            Number tempNum = value1;
            value1 = value0;
            value0 = tempNum;
        }
        double rectY = translatedValue0;

        // BAR WIDTH
        double rectWidth = itemWidth;

        // BAR HEIGHT
        double rectHeight = Math.abs(translatedValue1 - translatedValue0);

        Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
        Paint seriesPaint = plot.getSeriesPaint(series);
        g2.setPaint(seriesPaint);
        g2.fill(bar);
        if (itemWidth > 3) {
            g2.setStroke(plot.getSeriesStroke(series));
            g2.setPaint(plot.getSeriesOutlinePaint(series));
            g2.draw(bar);
        }

        if (plot.getLabelsVisible()) {
            NumberFormat formatter = plot.getLabelFormatter();
            Font labelFont = plot.getLabelFont();
            g2.setFont(labelFont);
            Paint paint = plot.getLabelPaint();
            g2.setPaint(paint);
            boolean rotate = plot.getVerticalLabels();

            String lowLabel = formatter.format(value1);
            Rectangle2D lowLabelArea
                = new Rectangle2D.Double(rectX, translatedValue1, rectWidth,
                                         dataArea.getMaxY() - translatedValue1);
            drawLabel(g2, lowLabel, lowLabelArea, labelFont, LOW_LABEL, rotate);

            String highLabel = formatter.format(value0);
            Rectangle2D highLabelArea
                = new Rectangle2D.Double(rectX, dataArea.getY(),
                                         rectWidth, translatedValue0 - dataArea.getY());
            drawLabel(g2, highLabel, highLabelArea, labelFont, HIGH_LABEL, rotate);
        }

        // collect entity and tool tip information...
        if (getInfo() != null) {
            EntityCollection entities = getInfo().getEntityCollection();
            if (entities != null) {
                String tip = null;
                if (getToolTipGenerator() != null) {
                    tip = getToolTipGenerator().generateToolTip(data, series, category);
                }
                String url = null;
                if (getURLGenerator() != null) {
                    url = getURLGenerator().generateURL(data, series, category);
                }
                CategoryItemEntity entity
                    = new CategoryItemEntity(bar, tip, url, series, category, categoryIndex);
                entities.addEntity(entity);
            }
        }

    }

    /**
     * Draws a value label on the plot.
     *
     * @param g2  the graphics device.
     * @param label  the label text.
     * @param labelArea  the area in which to draw the label (it may extend beyond the sides of
     *                   this rectangle).
     * @param labelFont  the font to draw the label with.
     * @param labelType  HIGH_LABEL or LOW_LABEL; determines how to position the label in the
     *                   provided area.
     * @param rotate  if <code>true</code> rotate the label 90 degrees.
     */
    private void drawLabel(Graphics2D g2, String label, Rectangle2D labelArea,
                           Font labelFont, int labelType, boolean rotate) {

         FontRenderContext frc = g2.getFontRenderContext();
         Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
         LineMetrics lm = labelFont.getLineMetrics(label, frc);
         float labelx = (float) labelArea.getCenterX();
         float labely;
         if (rotate) {
             labelx += (float) (labelBounds.getHeight() / 2 - lm.getDescent());
             if (labelType == HIGH_LABEL) {
                 labely = (float) (labelArea.getMaxY() - lm.getLeading());
             }
             else {
                 labely = (float) (labelArea.getY() + labelBounds.getWidth()
                                                    + lm.getLeading());
             }
             RefineryUtilities.drawRotatedString(label, g2, labelx, labely, -Math.PI / 2);
         }
         else {
             labelx -= (float) (labelBounds.getWidth() / 2);
             if (labelType == HIGH_LABEL) {
                 labely = (float) (labelArea.getMaxY() - lm.getDescent()
                                                       - lm.getLeading());
             }
             else {
                 labely = (float) (labelArea.getY() + lm.getAscent()
                                                    + lm.getLeading());
             }
             g2.drawString(label, labelx, labely);
         }

    }

    /**
     * Returns true, since for this renderer there are gaps between the items
     * in one category.
     *
     * @return <code>true</code>.
     */
    public boolean hasItemGaps() {
        return true;
    }

    /**
     * Returns the number of bar-widths displayed in each category.  For this
     * renderer, there is one bar per series, so we return the number of
     * series.
     *
     * @param data  the dataset.
     *
     * @return the number of data series.
     */
    public int barWidthsPerCategory(CategoryDataset data) {
        return data.getSeriesCount();
    }

}
