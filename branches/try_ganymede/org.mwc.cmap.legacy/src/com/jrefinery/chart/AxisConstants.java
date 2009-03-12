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
 * ------------------
 * AxisConstants.java
 * ------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AxisConstants.java,v 1.1.1.1 2003/07/17 10:06:20 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 06-Mar-2002 : Version 1 (DG);
 * 25-Apr-2002 : Removed redundant HORIZONTAL and VERTICAL constants (DG);
 * 05-Sep-2002 : Added DEFAULT_TICK_PAINT (DG);
 * 16-Oct-2002 : Changed default tick paint to light gray (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Insets;

/**
 * Useful constants relating to axes.
 *
 * @author DG
 */
public interface AxisConstants {

    /** The default axis label font. */
    public static final Font DEFAULT_AXIS_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    /** The default axis label paint. */
    public static final Paint DEFAULT_AXIS_LABEL_PAINT = Color.black;

    /** The default axis label insets. */
    public static final Insets DEFAULT_AXIS_LABEL_INSETS = new Insets(3, 3, 3, 3);

    /** The default tick label font. */
    public static final Font DEFAULT_TICK_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /** The default tick label paint. */
    public static final Paint DEFAULT_TICK_LABEL_PAINT = Color.black;

    /** The default tick label insets. */
    public static final Insets DEFAULT_TICK_LABEL_INSETS = new Insets(2, 2, 2, 2);

    /** The default tick stroke. */
    public static final Stroke DEFAULT_TICK_STROKE = new BasicStroke(1);

    /** The default tick paint. */
    public static final Paint DEFAULT_TICK_PAINT = Color.lightGray;

}
