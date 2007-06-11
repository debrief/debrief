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
 * JFreeChart.java
 * ---------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *
 * $Id: LegendItem.java,v 1.1.1.1 2003/07/17 10:06:24 Ian.Mayo Exp $
 *
 * Changes (from 2-Oct-2002)
 * -------------------------
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 */

package com.jrefinery.chart;

import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;

/**
 * A legend item.
 * <P>
 * Records all the properties of a legend item, but is not concerned about the display location.
 *
 * @author DG
 */
public class LegendItem {

    /** The label. */
    private String label;

    /** The description. */
    private String description;

    /** The shape. */
    private Shape shape;

    /** The paint. */
    private Paint paint;

    /** The outline paint. */
    private Paint outlinePaint;

    /** The stroke. */
    private Stroke stroke;

    /** The outline stroke. */
    private Stroke outlineStroke;

    /**
     * Creates a new legend item.
     *
     * @param label  the label.
     * @param description  the description.
     * @param shape  the shape.
     * @param paint  the paint.
     * @param outlinePaint  the outline paint.
     * @param stroke  the stroke.
     * @param outlineStroke  the outline stroke.
     */
    public LegendItem(String label,
                      String description,
                      Shape shape,
                      Paint paint, Paint outlinePaint,
                      Stroke stroke, Stroke outlineStroke) {

        this.label = label;
        this.description = description;
        this.shape = shape;
        this.paint = paint;
        this.outlinePaint = outlinePaint;
        this.stroke = stroke;
        this.outlineStroke = outlineStroke;

    }

    /**
     * Returns the label.
     *
     * @return the label.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Returns the paint.
     *
     * @return the paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

}
