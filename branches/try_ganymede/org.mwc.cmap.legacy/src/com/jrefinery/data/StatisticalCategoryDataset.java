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
 * -------------------------------
 * StatisticalCategoryDataset.java
 * -------------------------------
 * (C) Copyright 2002, by Pascal Collet and Contributors.
 *
 * Original Author:  Pascal Collet;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: StatisticalCategoryDataset.java,v 1.1.1.1 2003/07/17 10:06:55 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 21-Aug-2002 : Version 1, contributed by Pascal Collet (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data;

/**
 * A category dataset that defines a median and standard deviation value for each
 * series/category combination.
 *
 * @author PC
 *
 */
public interface StatisticalCategoryDataset extends CategoryDataset {

    /**
     * Returns the mean value for the specified series
     * (zero-based index) and category.
     *
     * @param series  the series index (zero-based).
     * @param category  the category.
     *
     * @return the mean value.
     */
    public Number getMeanValue (int series, Object category);

    /**
     * Returns the standard deviation value for the specified series
     * (zero-based index) and category.
     *
     * @param series  the series index (zero-based).
     * @param category  the category.
     *
     * @return the standard deviation.
     */
    public Number getStdDevValue (int series, Object category);

}

