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
 * -----------------------------
 * CategoryToolTipGenerator.java
 * -----------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryToolTipGenerator.java,v 1.1.1.1 2003/07/17 10:06:44 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1 (DG);
 * 16-Jan-2002 : Completed Javadocs (DG);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.tooltips;

import com.jrefinery.data.CategoryDataset;

/**
 * Interface for a tool tip generator for plots that use data from a CategoryDataset.
 *
 * @author DG
 */
public interface CategoryToolTipGenerator extends ToolTipGenerator {

    /**
     * Generates a tooltip text item for a particular category within a series.
     *
     * @param data  the dataset.
     * @param series  the series (zero-based index).
     * @param category  the category.
     *
     * @return the tooltip text.
     */
    public String generateToolTip(CategoryDataset data, int series, Object category);

}
