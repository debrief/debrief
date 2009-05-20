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
 * --------------------------
 * CategoryPlotConstants.java
 * --------------------------
 * (C) Copyright 2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryPlotConstants.java,v 1.1.1.1 2003/07/17 10:06:20 Ian.Mayo Exp $
 *
 * Changes
 * -------

 * 06-Jun-2002 : Version 1 (code moved from CategoryPlot) (DG);
 * 28-Aug-2002 : Increased maximum values to make them less likely to get in the way, they are
 *               really just sanity checks anyway (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Font;

/**
 * Useful constants for the CategoryPlot class.
 *
 * @author DG
 */
public interface CategoryPlotConstants {

    /** Default value for the gap before the first bar in the plot. */
    public static final double DEFAULT_INTRO_GAP_PERCENT = 0.05;  // 5 %

    /** Default value for the gap after the last bar in the plot. */
    public static final double DEFAULT_TRAIL_GAP_PERCENT = 0.05;  // 5 %

    /** Default value for the total gap to be distributed between categories. */
    public static final double DEFAULT_CATEGORY_GAPS_PERCENT = 0.20;  // 20 %

    /** Default value for the total gap to be distributed between items within a category.
     */
    public static final double DEFAULT_ITEM_GAPS_PERCENT = 0.15;  // 15 %

    /** The maximum gap before the first bar in the plot (a sanity check). */
    public static final double MAX_INTRO_GAP_PERCENT = 0.50;  // 50 %

    /** The maximum gap after the last bar in the plot (a sanity check). */
    public static final double MAX_TRAIL_GAP_PERCENT = 0.50;  // 50 %

    /** The maximum gap to be distributed between categories. */
    public static final double MAX_CATEGORY_GAPS_PERCENT = 0.50;  // 50 %

    /** The maximum gap to be distributed between items within categories. */
    public static final double MAX_ITEM_GAPS_PERCENT = 0.50;  // 50 %

    /** The default value label font. */
    public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

}
