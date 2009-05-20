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
 * -----------------------
 * CombinationDataset.java
 * -----------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   -;
 *
 * $Id: CombinationDataset.java,v 1.1.1.1 2003/07/17 10:06:50 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 06-Dec-2001 : Version 1 (BK);
 *
 */

package com.jrefinery.legacy.data;

/**
 * Interface that describes the new methods that any combined dataset needs to
 * implement. A combined dataset object will combine one or more datasets and
 * expose a sub-set or union of the combined datasets.
 * @author Bill Kelemenm (bill@kelemen-usa.com)
 */
public interface CombinationDataset {

    ///////////////////////////////////////////////////////////////////////////
    // New methods from CombinationDataset
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns the parent Dataset of this combination. If there is more than
     * one parent, or a child is found that is not a combination, then returns
     * <code>null</code>.
     *
     * @return the parent Dataset of this combination.
     */
    public SeriesDataset getParent();

    /**
     * Returns a map or indirect indexing form our series into parent's series.
     *
     * @return a map or indirect indexing form our series into parent's series.
     */
    public int[] getMap();

}
