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
 * -----------
 * Spacer.java
 * -----------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Spacer.java,v 1.1.1.1 2003/07/17 10:06:26 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 07-Feb-2002 : Version 1 (DG);
 * 18-Sep-2002 : Added trim(..) method, completed Javadocs and fixed Checkstyle issues (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.geom.Rectangle2D;

/**
 * Represents an amount of blank space inside (or sometimes outside) a
 * rectangle.  This class is similar in function to the Insets class, but
 * allows for the space to be specified in relative terms as well as absolute
 * terms.
 * <P>
 * Immutable.
 *
 * @author DG
 */
public class Spacer implements Cloneable {

    /** A constant for 'relative' spacing. */
    public static final int RELATIVE = 0;

    /** A constant for 'absolute' spacing. */
    public static final int ABSOLUTE = 1;

    /** The spacing type (relative or absolute). */
    private int type;

    /** The space on the left. */
    private double left;

    /** The space on the right. */
    private double right;

    /** The space at the top. */
    private double top;

    /** The space at the bottom. */
    private double bottom;

    /**
     * Creates a new Spacer object.
     * <p>
     * The space can be specified in relative or absolute terms (using the constants
     * <code>RELATIVE</code> and <code>ABSOLUTE</code> for the <code>type</code> argument.
     * For relative spacing, the margins are specified as percentages (of the overall height
     * or width).  For absolute spacing, the margins are specified in points (1/72 inch).
     *
     * @param type  the type of spacing (relative or absolute).
     * @param left  the left margin.
     * @param top  the top margin.
     * @param right  the right margin.
     * @param bottom  the bottom margin.
     */
    public Spacer(int type, double left, double top, double right, double bottom) {

        this.type = type;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

    }

    /**
     * Returns the amount of space for the left hand side of a rectangular area.
     * <p>
     * The width argument is only used for calculating 'relative' spacing.
     *
     * @param width  the overall width of the rectangular area.
     *
     * @return  the space (in points).
     */
    public double getLeftSpace(double width) {

        double result = 0.0;

        if (type == ABSOLUTE) {
            result = left;
        }
        else if (type == RELATIVE) {
            result = left * width;
        }

        return result;

    }

    /**
     * Returns the amount of space for the right hand side of a rectangular area.
     * <p>
     * The width argument is only used for calculating 'relative' spacing.
     *
     * @param width  the overall width of the rectangular area.
     *
     * @return  the space (in points).
     */
    public double getRightSpace(double width) {

        double result = 0.0;

        if (type == ABSOLUTE) {
            result = right;
        }
        else if (type == RELATIVE) {
            result = right * width;
        }

        return result;

    }

    /**
     * Returns the amount of space for the top of a rectangular area.
     * <p>
     * The height argument is only used for calculating 'relative' spacing.
     *
     * @param height  the overall height of the rectangular area.
     *
     * @return  the space (in points).
     */
    public double getTopSpace(double height) {

        double result = 0.0;

        if (type == ABSOLUTE) {
            result = top;
        }
        else if (type == RELATIVE) {
            result = top * height;
        }

        return result;

    }

    /**
     * Returns the amount of space for the bottom of a rectangular area.
     * <p>
     * The height argument is only used for calculating 'relative' spacing.
     *
     * @param height  the overall height of the rectangular area.
     *
     * @return  the space (in points).
     */
    public double getBottomSpace(double height) {

        double result = 0.0;

        if (type == ABSOLUTE) {
            result = bottom;
        }
        else if (type == RELATIVE) {
            result = bottom * height;
        }

        return result;

    }

    /**
     * Returns the width after adding the left and right spacing amounts.
     *
     * @param width  the original width.
     *
     * @return the adjusted width.
     */
    public double getAdjustedWidth(double width) {

        double result = width;

        if (type == ABSOLUTE) {
            result = result + left + right;
        }
        else if (type == RELATIVE) {
            result = result + (left * width) + (right * width);
        }

        return result;

    }

    /**
     * Returns the height after adding the top and bottom spacing amounts.
     *
     * @param height  the original height.
     *
     * @return the adjusted height.
     */
    public double getAdjustedHeight(double height) {

        double result = height;

        if (type == ABSOLUTE) {
            result = result + top + bottom;
        }
        else if (type == RELATIVE) {
            result = result + (top * height) + (bottom * height);
        }

        return result;

    }

    /**
     * Calculates the margins and trims them from the supplied area.
     *
     * @param area  the area to be trimmed.
     */
    public void trim(Rectangle2D area) {
        double x = area.getX();
        double y = area.getY();
        double h = area.getHeight();
        double w = area.getWidth();
        double l = getLeftSpace(w);
        double r = getRightSpace(w);
        double t = getTopSpace(h);
        double b = getBottomSpace(h);
        area.setRect(x + l, y + t, w - l - r, h - t - b);
    }

}
