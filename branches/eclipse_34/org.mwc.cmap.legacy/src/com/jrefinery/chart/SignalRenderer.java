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
 * -------------------
 * SignalRenderer.java
 * -------------------
 * (C) Copyright 2001, 2002, by Sylvain Viuejot and Contributors.
 *
 * Original Author:  Sylvain Vieujot;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Richard Atkinson;
 *
 * $Id: SignalRenderer.java,v 1.1.1.1 2003/07/17 10:06:26 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 08-Jan-2002 : Version 1.  Based on code in the SignalsPlot class, written by Sylvain
 *               Vieujot (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 14-Feb-2002 : Added small fix from Sylvain (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers no longer need to be
 *               immutable (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem(...) method, and changed the return
 *               type of the drawItem method to void, reflecting a change in the XYItemRenderer
 *               interface.  Added tooltip code to drawItem(...) method (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for HTML image maps (RA);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.SignalsDataset;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;

/**
 * A renderer that draws signals on an XY plot (requires a SignalsDataset).
 *
 * @author SV
 */
public class SignalRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

    /** The mark offset. */
    private double markOffset = 5;

    /** The shape width. */
    private double shapeWidth = 15;

    /** The shape height. */
    private double shapeHeight = 25;

    /**
     * Creates a new renderer.
     */
    public SignalRenderer() {
    }

    /**
     * Returns the mark offset.
     *
     * @return the mark offset.
     */
    public double getMarkOffset() {
        return this.markOffset;
    }

    /**
     * Sets the mark offset.
     *
     * @param offset  the mark offset.
     */
    public void setMarkOffset(double offset) {
        this.markOffset = offset;
    }

    /**
     * Returns the shape width.
     *
     * @return the shape width.
     */
    public double getShapeWidth() {
        return this.shapeWidth;
    }

    /**
     * Sets the shape width.
     *
     * @param width  the shape width.
     */
    public void setShapeWidth(double width) {
        this.shapeWidth = width;
    }

    /**
     * Returns the shape height.
     *
     * @return the shape height.
     */
    public double getShapeHeight() {
        return this.shapeHeight;
    }

    /**
     * Sets the shape height.
     *
     * @param height  the shape height.
     */
    public void setShapeHeight(double height) {
        this.shapeHeight = height;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param horizontalAxis  the horizontal axis.
     * @param verticalAxis  the vertical axis.
     * @param data  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairInfo  information about crosshairs on a plot.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot, ValueAxis horizontalAxis, ValueAxis verticalAxis,
                         XYDataset data, int series, int item,
                         CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        SignalsDataset signalData = (SignalsDataset) data;

        Number x = signalData.getXValue(series, item);
        Number y = signalData.getYValue(series, item);
        int type = signalData.getType(series, item);
        //double level = signalData.getLevel(series, item);

        double xx = horizontalAxis.translateValueToJava2D(x.doubleValue(), dataArea);
        double yy = verticalAxis.translateValueToJava2D(y.doubleValue(), dataArea);

        Paint p = plot.getSeriesPaint(series);
        Stroke s = plot.getSeriesStroke(series);
        g2.setPaint(p);
        g2.setStroke(s);

        int direction = 1;
        if ((type == SignalsDataset.ENTER_LONG) || (type == SignalsDataset.EXIT_SHORT)) {
            yy = yy + markOffset;
            direction = -1;
        }
        else {
            yy = yy - markOffset;
        }

        GeneralPath path = new GeneralPath();
        if ((type == SignalsDataset.ENTER_LONG) || (type == SignalsDataset.ENTER_SHORT)) {
            path.moveTo((float) xx, (float) yy);
            path.lineTo((float) (xx + shapeWidth / 2), (float) (yy - direction * shapeHeight / 3));
            path.lineTo((float) (xx + shapeWidth / 6), (float) (yy - direction * shapeHeight / 3));
            path.lineTo((float) (xx + shapeWidth / 6), (float) (yy - direction * shapeHeight));
            path.lineTo((float) (xx - shapeWidth / 6), (float) (yy - direction * shapeHeight));
            path.lineTo((float) (xx - shapeWidth / 6), (float) (yy - direction * shapeHeight / 3));
            path.lineTo((float) (xx - shapeWidth / 2), (float) (yy - direction * shapeHeight / 3));
            path.lineTo((float) xx, (float) yy);
        }
        else {
            path.moveTo((float) xx, (float) yy);
            path.lineTo((float) xx, (float) (yy - direction * shapeHeight));
            Ellipse2D.Double ellipse =
                new Ellipse2D.Double(xx - shapeWidth / 2,
                                     yy
                                     + (direction == 1 ? -shapeHeight : shapeHeight - shapeWidth),
                                     shapeWidth, shapeWidth);
            path.append(ellipse, false);
        }

        g2.fill(path);
        g2.setPaint(Color.black);
        g2.draw(path);

        // add an entity for the item...
        if (entities != null) {
            String tip = "";
            if (getToolTipGenerator() != null) {
                tip = getToolTipGenerator().generateToolTip(data, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(data, series, item);
            }
            XYItemEntity entity = new XYItemEntity(path, tip, url, series, item);
            entities.addEntity(entity);
        }

    }

}
