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
 * HighLowRenderer.java
 * --------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: HighLowRenderer.java,v 1.1.1.1 2003/07/17 10:06:22 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers no longer need to be
 *               immutable (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem(...) method, and changed the return
 *               type of the drawItem method to void, reflecting a change in the XYItemRenderer
 *               interface.  Added tooltip code to drawItem(...) method (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for HTML image maps (RA);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.legacy.chart.entity.EntityCollection;
import com.jrefinery.legacy.chart.entity.XYItemEntity;
import com.jrefinery.legacy.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.legacy.data.HighLowDataset;
import com.jrefinery.legacy.data.XYDataset;

/**
 * A renderer that draws high/low/open/close markers on an XY plot (requires
 * a HighLowDataset).
 *
 * @author DG
 */
public class HighLowRenderer extends AbstractXYItemRenderer
                             implements XYItemRenderer {

    /**
     * The default constructor.
     */
    public HighLowRenderer() {
        this(null);
    }

    /**
     * Creates a new renderer with the specified tool tip generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    public HighLowRenderer(XYToolTipGenerator toolTipGenerator) {
        super(toolTipGenerator);
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param data  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairInfo  information about crosshairs on a plot.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset data, int series, int item,
                         CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        HighLowDataset highLowData = (HighLowDataset) data;

        Number x = highLowData.getXValue(series, item);
        Number yHigh  = highLowData.getHighValue(series, item);
        Number yLow   = highLowData.getLowValue(series, item);
        Number yOpen  = highLowData.getOpenValue(series, item);
        Number yClose = highLowData.getCloseValue(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea);
        double yyHigh = rangeAxis.translateValueToJava2D(yHigh.doubleValue(), dataArea);
        double yyLow = rangeAxis.translateValueToJava2D(yLow.doubleValue(), dataArea);
        double yyOpen = rangeAxis.translateValueToJava2D(yOpen.doubleValue(), dataArea);
        double yyClose = rangeAxis.translateValueToJava2D(yClose.doubleValue(), dataArea);

        Paint p = plot.getSeriesPaint(series);
        Stroke s = plot.getSeriesStroke(series);

        HighLow hl = new HighLow(xx, yyHigh, yyLow, yyOpen, yyClose, s, p);
        Line2D l1 = hl.getOpenTickLine();
        Line2D l2 = hl.getLine();
        Line2D l3 = hl.getCloseTickLine();

        g2.setPaint(p);
        g2.setStroke(s);
        g2.draw(l1);
        g2.draw(l2);
        g2.draw(l3);

        // add an entity for the item...
        if (entities != null) {
            if (entityArea == null) {
                entityArea = hl.getBounds();
            }
            String tip = "";
            if (getToolTipGenerator() != null) {
                tip = getToolTipGenerator().generateToolTip(data, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(data, series, item);
            }
            XYItemEntity entity = new XYItemEntity(entityArea, tip, url, series, item);
            entities.addEntity(entity);
        }

    }

}
