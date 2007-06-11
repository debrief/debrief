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
 * -----------------------------
 * HorizontalMarkerAxisBand.java
 * -----------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: HorizontalMarkerAxisBand.java,v 1.1.1.1 2003/07/17 10:06:23 Ian.Mayo Exp $
 *
 * Changes (from 03-Sep-2002)
 * --------------------------
 * 03-Sep-2002 : Updated Javadoc comments (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Iterator;

/**
 * A band that can be added to a horizontal axis to display regions.
 *
 * @author DG
 */
public class HorizontalMarkerAxisBand {

    /** The axis that the band belongs to. */
    private HorizontalNumberAxis axis;

    /** The top outer gap. */
    private double topOuterGap;

    /** The top inner gap. */
    private double topInnerGap;

    /** The bottom outer gap. */
    private double bottomOuterGap;

    /** The bottom inner gap. */
    private double bottomInnerGap;

    /** The font. */
    private Font font;

    /** Storage for the markers. */
    private List markers;

    /**
     * Constructs a new axis band.
     *
     * @param axis  the owner.
     * @param topOuterGap  the top outer gap.
     * @param topInnerGap  the top inner gap.
     * @param bottomOuterGap  the bottom outer gap.
     * @param bottomInnerGap  the bottom inner gap.
     * @param font  the font.
     */
    public HorizontalMarkerAxisBand(HorizontalNumberAxis axis,
                                    double topOuterGap, double topInnerGap,
                                    double bottomOuterGap, double bottomInnerGap,
                                    Font font) {
        this.axis = axis;
        this.topOuterGap = topOuterGap;
        this.topInnerGap = topInnerGap;
        this.bottomOuterGap = bottomOuterGap;
        this.bottomInnerGap = bottomInnerGap;
        this.font = font;
        this.markers = new java.util.ArrayList();
    }

    /**
     * Adds a marker to the band.
     *
     * @param marker  the marker.
     */
    public void addMarker(Marker marker) {
        markers.add(marker);
    }

    /**
     * Returns the height of the band.
     *
     * @param g2  the graphics device.
     *
     * @return the height of the band.
     */
    public double getHeight(Graphics2D g2) {

        // calculate the height of the band...
        double result = 0.0;
        if (markers.size() > 0) {
            LineMetrics metrics = font.getLineMetrics("123g", g2.getFontRenderContext());
            result = this.topOuterGap + this.topInnerGap + metrics.getHeight()
                     + this.bottomInnerGap + this.bottomOuterGap;
        }
        return result;

    }

    /**
     * A utility method that draws a string inside a rectangle.
     *
     * @param g2  the graphics device.
     * @param bounds  the rectangle.
     * @param font  the font.
     * @param text  the text.
     */
    private void drawStringInRect(Graphics2D g2, Rectangle2D bounds, Font font,
                                  String text) {

        g2.setFont(font);
        Rectangle2D r = font.getStringBounds(text, g2.getFontRenderContext());
        double x = bounds.getX();
        if (r.getWidth() < bounds.getWidth()) {
            x = x + (bounds.getWidth() - r.getWidth()) / 2;
        }
        LineMetrics metrics = font.getLineMetrics(text, g2.getFontRenderContext());
        g2.drawString(text,
                      (float) x,
                      (float) (bounds.getMaxY() - this.bottomInnerGap - metrics.getDescent()));
    }

    /**
     * Draws the band.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea,
                     double x, double y) {

        double h = getHeight(g2);
        Iterator iterator = this.markers.iterator();
        while (iterator.hasNext()) {
            IntervalMarker marker = (IntervalMarker) iterator.next();
            double start =  Math.max(marker.getStartValue(), axis.getRange().getLowerBound());
            double end = Math.min(marker.getEndValue(), axis.getRange().getUpperBound());
            double s = axis.translateValueToJava2D(start, dataArea);
            double e = axis.translateValueToJava2D(end, dataArea);
            Rectangle2D r = new Rectangle2D.Double(s, y + topOuterGap,
                                                   e - s, h - topOuterGap - bottomOuterGap);

            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.fill(r);
            g2.setPaint(marker.getOutlinePaint());
            g2.draw(r);
            g2.setComposite(originalComposite);

            g2.setPaint(Color.black);
            drawStringInRect(g2, r, this.font, marker.getLabel());
        }

    }

}
