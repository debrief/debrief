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
 * ---------------
 * ChartPanel.java
 * ---------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributors:     -;
 *
 * $Id: ChartPanelConstants.java,v 1.1.1.1 2003/07/17 10:06:21 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 27-May-2002 : Version 1 (code moved from the ChartPanel class);
 * 25-Jun-2002 : Increased max draw width and height (DG);
 *
 */

package com.jrefinery.chart;

/**
 * Useful constants for the ChartPanel class.
 *
 * @author DG
 */
public interface ChartPanelConstants {

    /** Default setting for buffer usage. */
    public static final boolean DEFAULT_BUFFER_USED = false;

    /** The default panel width. */
    public static final int DEFAULT_WIDTH = 680;

    /** The default panel height. */
    public static final int DEFAULT_HEIGHT = 420;

    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 300;

    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 200;

    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 800;

    /** The default limit below which chart scaling kicks in. */
    public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 600;

    /** The minimum size required to perform a zoom on a rectangle */
    public static final int MINIMUM_DRAG_ZOOM_SIZE = 20;

    /** Properties action command. */
    public static final String PROPERTIES_ACTION_COMMAND = "PROPERTIES";

    /** Save action command. */
    public static final String SAVE_ACTION_COMMAND = "SAVE";

    /** Print action command. */
    public static final String PRINT_ACTION_COMMAND = "PRINT";

    /** Zoom in (both axes) action command. */
    public static final String ZOOM_IN_BOTH_ACTION_COMMAND = "ZOOM_IN_BOTH";

    /** Zoom in (horizontal axis only) action command. */
    public static final String ZOOM_IN_HORIZONTAL_ACTION_COMMAND = "ZOOM_IN_HORIZONTAL";

    /** Zoom in (vertical axis only) action command. */
    public static final String ZOOM_IN_VERTICAL_ACTION_COMMAND = "ZOOM_IN_VERTICAL";

    /** Zoom out (both axes) action command. */
    public static final String ZOOM_OUT_BOTH_ACTION_COMMAND = "ZOOM_OUT_BOTH";

    /** Zoom out (horizontal axis only) action command. */
    public static final String ZOOM_OUT_HORIZONTAL_ACTION_COMMAND = "ZOOM_HORIZONTAL_BOTH";

    /** Zoom out (vertical axis only) action command. */
    public static final String ZOOM_OUT_VERTICAL_ACTION_COMMAND = "ZOOM_VERTICAL_BOTH";

    /** Zoom reset (both axes) action command. */
    public static final String AUTO_RANGE_BOTH_ACTION_COMMAND = "AUTO_RANGE_BOTH";

    /** Zoom reset (horizontal axis only) action command. */
    public static final String AUTO_RANGE_HORIZONTAL_ACTION_COMMAND = "AUTO_RANGE_HORIZONTAL";

    /** Zoom reset (vertical axis only) action command. */
    public static final String AUTO_RANGE_VERTICAL_ACTION_COMMAND = "AUTO_RANGE_VERTICAL";

}
