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
 * ------------------
 * PointerNeedle.java
 * ------------------
 * (C) Copyright 2002, by the Australian Antarctic Division and Contributors.
 *
 * Original Author:  Bryan Scott (for the Australian Antarctic Division);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: PointerNeedle.java,v 1.1.1.1 2003/07/17 10:06:43 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 25-Sep-2002 : Version 1, contributed by Bryan Scott (DG);
 *
 */

package com.jrefinery.legacy.chart.needle;

import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A needle...
 *
 * @author BS
 */
public class PointerNeedle extends MeterNeedle {

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param rotate  the rotation point.
     * @param angle  the angle.
     */
    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {

        GeneralPath shape1 = new GeneralPath();
        GeneralPath shape2 = new GeneralPath();
        float minX = (float) plotArea.getMinX();
        float minY = (float) plotArea.getMinY();
        float maxX = (float) plotArea.getMaxX();
        float maxY = (float) plotArea.getMaxY();
        float midX = (float) (minX + (plotArea.getWidth() / 2));
        float midY = (float) (minY + (plotArea.getHeight() / 2));

        shape1.moveTo(minX, midY);
        shape1.lineTo(midX, minY);
        shape1.lineTo(maxX, midY);
        shape1.closePath();

        shape2.moveTo(minX, midY);
        shape2.lineTo(midX, maxY);
        shape2.lineTo(maxX, midY);
        shape2.closePath();

        if ((rotate != null) && (angle != 0)) {
            /// we have rotation huston, please spin me
            t.setToRotation(angle, rotate.getX(), rotate.getY());
            shape1.transform(t);
            shape2.transform(t);
        }

        if (fillPaint != null) {
            g2.setPaint(fillPaint);
            g2.fill(shape1);
        }

        if (highlightPaint != null) {
            g2.setPaint(highlightPaint);
            g2.fill(shape2);
        }

        if (outlinePaint != null) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(shape1);
            g2.draw(shape2);
        }
    }

}
