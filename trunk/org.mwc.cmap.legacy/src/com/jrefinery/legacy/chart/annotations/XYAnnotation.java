/* ==========================================
 * JFreeChart : a free chart library for Java
 * ==========================================
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
 * -----------------
 * XYAnnotation.java
 * -----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: XYAnnotation.java,v 1.1.1.1 2003/07/17 10:06:30 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 28-Aug-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.legacy.chart.annotations;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.jrefinery.legacy.chart.ValueAxis;

/**
 * A plot annotation that works with XYPlots.
 */
public interface XYAnnotation extends Annotation {

    /**
     * Draws the annotation.
     *
     * @param g2  The graphics device.
     * @param dataArea  The data area.
     * @param domainAxis  The domain axis.
     * @param rangeAxis  The range axis.
     */
    public void draw(Graphics2D g2, Rectangle2D dataArea,
                     ValueAxis domainAxis, ValueAxis rangeAxis);

}
