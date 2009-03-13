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
 * ---------------------
 * WindItemRenderer.java
 * ---------------------
 * (C) Copyright 2001, 2002, by Achilleus Mantzios and Contributors.
 *
 * Original Author:  Achilleus Mantzios;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: WindItemRenderer.java,v 1.1.1.1 2003/07/17 10:06:29 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 06-Feb-2002 : Version 1, based on code contributed by Achilleus Mantzios (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers no longer need to be
 *               immutable.  Changed StrictMath-->Math to retain JDK1.2 compatibility (DG);
 * 09-Apr-2002 : Changed return type of the drawItem method to void, reflecting the change in the
 *               XYItemRenderer method (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.Font;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.WindDataset;

/**
 * A specialised renderer for displaying wind intensity/direction data.
 *
 * @author AM
 */
public class WindItemRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

    /**
     * Default constructor.
     */
    public WindItemRenderer() {
        super();
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot is being drawn.
     * @param info  optional information collection.
     * @param plot  the plot (can be used to obtain standard color information etc).
     * @param horizontalAxis  the horizontal axis.
     * @param verticalAxis  the vertical axis.
     * @param data  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairs  collects information about crosshairs.
     */
    public void drawItem(Graphics2D g2, Rectangle2D plotArea,
                         ChartRenderingInfo info, XYPlot plot, ValueAxis horizontalAxis,
                         ValueAxis verticalAxis, XYDataset data, int series, int item,
                         CrosshairInfo crosshairs) {

        WindDataset windData = (WindDataset) data;

        Paint seriesPaint = plot.getSeriesPaint(series);
        Stroke seriesStroke = plot.getSeriesStroke(series);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        // get the data point...

        Number x = windData.getXValue(series, item);
        Number windDir = windData.getWindDirection(series, item);
        Number wforce = windData.getWindForce(series, item);
        double windForce = wforce.doubleValue();

        double wdirt = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0);

        double ax1, ax2, ay1, ay2, rax2, ray2;

        //rax1 = x.doubleValue();
        //ray1 = 0.0;

        ax1 = horizontalAxis.translateValueToJava2D(x.doubleValue(), plotArea);
        ay1 = verticalAxis.translateValueToJava2D(0.0, plotArea);

        rax2 = x.doubleValue() + (windForce * Math.cos(wdirt) * 8000000.0);
        ray2 = windForce * Math.sin(wdirt);

        ax2 = horizontalAxis.translateValueToJava2D(rax2, plotArea);
        ay2 = verticalAxis.translateValueToJava2D(ray2, plotArea);

        int diri = windDir.intValue();
        int forcei = wforce.intValue();
        String dirforce = diri + "-" + forcei;
        Line2D line = new Line2D.Double(ax1, ay1, ax2, ay2);

        g2.draw(line);
        g2.setPaint(Color.blue);
        g2.setFont(new Font("foo", 1, 9));

        g2.drawString(dirforce, (float) ax1, (float) ay1);

        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        double alx2, aly2, arx2, ary2;
        double ralx2, raly2, rarx2, rary2;

        double aldir = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0 - 5.0);
        ralx2 = wforce.doubleValue() * Math.cos(aldir) * (double) 8000000 * 0.8 + x.doubleValue();
        raly2 = wforce.doubleValue() * Math.sin(aldir) * 0.8;

        //double fac= (wforce.doubleValue()>1.0)?wforce.doubleValue()-2.0:0;

        alx2 = horizontalAxis.translateValueToJava2D(ralx2, plotArea);
        aly2 = verticalAxis.translateValueToJava2D(raly2, plotArea);

        line = new Line2D.Double(alx2, aly2, ax2, ay2);
        g2.draw(line);

        double ardir = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0 + 5.0);
        rarx2 = wforce.doubleValue() * Math.cos(ardir) * (double) 8000000 * 0.8 + x.doubleValue();
        rary2 = wforce.doubleValue() * Math.sin(ardir) * 0.8;

        arx2 = horizontalAxis.translateValueToJava2D(rarx2, plotArea);
        ary2 = verticalAxis.translateValueToJava2D(rary2, plotArea);

        line = new Line2D.Double(arx2, ary2, ax2, ay2);
        g2.draw(line);

    }

}
