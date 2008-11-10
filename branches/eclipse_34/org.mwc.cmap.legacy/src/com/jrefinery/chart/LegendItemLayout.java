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
 * LegendItem.java
 * ---------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: LegendItemLayout.java,v 1.1.1.1 2003/07/17 10:06:25 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 07-Feb-2002 : Version 1. INCOMPLETE, PLEASE IGNORE. (DG);
 *
 */

package com.jrefinery.chart;

/**
 * The interface for a legend item layout manager.
 *
 * @author DG
 */
public interface LegendItemLayout {

    /**
     * Updates the locations of all the legend items in the collection,
     * according to some particular layout algorithm.
     *
     * @param collection  the collection of legend items.
     */
    public void layoutLegendItems(LegendItemCollection collection);

}
