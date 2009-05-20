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
 * ---------------
 * ShipNeedle.java
 * ---------------
 * (C) Copyright 2002, by the Australian Antarctic Division and Contributors.
 *
 * Original Author:  Bryan Scott (for the Australian Antarctic Division);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: ShipNeedle.java,v 1.1.1.1 2003/07/17 10:06:43 Ian.Mayo Exp $
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
import java.awt.geom.Arc2D;

/**
 * A needle...
 *
 * @author BS
 */
public class ShipNeedle extends MeterNeedle {

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param rotate  the rotation point.
     * @param angle  the angle.
     */
    protected void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {

        GeneralPath shape = new GeneralPath();
        shape.append(new Arc2D.Double(-9.0, -7.0, 10,
                                      14, 0.0, 25.5,  Arc2D.OPEN), true);
        shape.append(new Arc2D.Double(0.0, -7.0, 10,
                                      14, 154.5, 25.5,  Arc2D.OPEN), true);
        shape.closePath();
        t.setToTranslation(plotArea.getMinX(), plotArea.getMaxY());
        t.scale(plotArea.getWidth(), plotArea.getHeight() / 3);
        shape.transform(t);

        if ((rotate != null) && (angle != 0)) {
            /// we have rotation
            t.setToRotation(angle, rotate.getX(), rotate.getY());
            shape.transform(t);
        }

        defaultDisplay(g2, shape);
    }

}

