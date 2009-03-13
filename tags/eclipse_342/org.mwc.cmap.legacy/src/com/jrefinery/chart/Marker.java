/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
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
 * -----------
 * Marker.java
 * -----------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Marker.java,v 1.1.1.1 2003/07/17 10:06:25 Ian.Mayo Exp $
 *
 * Changes (since 2-Jul-2002)
 * --------------------------
 * 02-Jul-2002 : Added extra constructor, standard header and Javadoc comments (DG);
 * 20-Aug-2002 : Added the outline stroke attribute (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Added new constructor (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Color;

/**
 * A constant value that is drawn on a chart as a marker, usually as a horizontal or a vertical
 * line.
 * <P>
 * In addition to a value, this class defines paint attributes to give some control over the
 * appearance of the marker.  The render can, however, override these settings if it chooses.
 * <P>
 * This class is immutable.
 *
 * @author DG
 */
public class Marker {

    /** The constant value. */
    private double value;

    /** The outline paint. */
    private Paint outlinePaint;

    /** The outline stroke. */
    private Stroke outlineStroke;

    /** The paint. */
    private Paint paint;

    /** The alpha transparency. */
    private float alpha;

    /**
     * Constructs a new marker.
     *
     * @param value  the value.
     */
    public Marker(double value) {
        this(value, Color.gray, new java.awt.BasicStroke(0.5f), Color.gray, 0.80f);
    }

    /**
     * Constructs a new marker.
     *
     * @param value  the value.
     * @param outlinePaint  the paint.
     */
    public Marker(double value, Paint outlinePaint) {
        this(value, outlinePaint, new java.awt.BasicStroke(0.5f), Color.red, 0.80f);
    }

    /**
     * Constructs a new marker.
     *
     * @param value  the value.
     * @param outlinePaint  the outline paint.
     * @param outlineStroke  the outline stroke.
     * @param paint  the paint.
     * @param alpha  the alpha transparency.
     */
    public Marker(double value, Paint outlinePaint, Stroke outlineStroke,
                  Paint paint, float alpha) {

        this.value = value;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.paint = paint;
        this.alpha = alpha;

    }

    /**
     * Returns the value.
     *
     * @return the value.
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns the outline paint.
     *
     * @return the outline paint.
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Returns the outline stroke.
     *
     * @return the outline stroke.
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Returns the paint.
     *
     * @return the paint.
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Returns the alpha transparency.
     *
     * @return the alpha transparency.
     */
    public float getAlpha() {
        return this.alpha;
    }

}
