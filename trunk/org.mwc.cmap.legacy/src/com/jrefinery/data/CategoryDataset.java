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
 * --------------------
 * CategoryDataset.java
 * --------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryDataset.java,v 1.1.1.1 2003/07/17 10:06:50 Ian.Mayo Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated e-mail address in header (DG);
 * 15-Oct-2001 : Moved to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 17-Nov-2001 : Updated Javadoc comments (DG);
 * 04-Mar-2002 : Updated import statement (DG);
 *
 */

package com.jrefinery.data;

import java.util.List;

/**
 * The interface for a dataset with one or more series, and values associated
 * with "categories".
 * <P>
 * The categories are represented by any Java object, with the category label
 * being provided by the toString() method.
 * <P>
 * The JFreeChart class library uses this interface to obtain data for bar
 * charts and line charts.
 *
 * @author DG
 */
public interface CategoryDataset extends SeriesDataset {

    /**
     * Returns the number of categories in the dataset.
     *
     * @return the category count.
     */
    public int getCategoryCount();

    /**
     * Returns a list of the categories in the dataset.
     *
     * @return the category list.
     */
		public List<Object> getCategories();

    /**
     * Returns the value for a series and category.
     *
     * @param series  the series (zero-based index).
     * @param category  the category.
     *
     * @return the value for a series and category.
     */
    public Number getValue(int series, Object category);

}
