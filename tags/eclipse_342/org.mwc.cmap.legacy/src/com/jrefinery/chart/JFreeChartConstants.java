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
 * ------------------------
 * JFreeChartConstants.java
 * ------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: JFreeChartConstants.java,v 1.1.1.1 2003/07/17 10:06:24 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 06-Mar-2002 : Version 1 (DG);
 * 11-Mar-2002 : Moved some constants into JFreeChartInfo.java (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Paint;
import java.awt.SystemColor;
import java.awt.Image;

/**
 * Useful constants relating to the JFreeChart class.
 *
 * @author DG
 */
public interface JFreeChartConstants {

    /** The default font for titles. */
    public static final Font DEFAULT_TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);

    /** The default background color. */
    public static final Paint DEFAULT_BACKGROUND_PAINT = SystemColor.control;

    /** The default background image. */
    public static final Image DEFAULT_BACKGROUND_IMAGE = null;

    /** The default background image alpha. */
    public static float DEFAULT_BACKGROUND_IMAGE_ALPHA = 0.5f;

}
