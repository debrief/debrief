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
 * -------------------------
 * LegendItemCollection.java
 * -------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: LegendItemCollection.java,v 1.1.1.1 2003/07/17 10:06:24 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 07-Feb-2002 : Version 1 (DG);
 * 24-Sep-2002 : Added get(int) and getItemCount() methods (DG);
 * 02-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.util.List;
import java.util.Iterator;

/**
 * A collection of legend items.
 *
 * @author DG
 */
public class LegendItemCollection {

    /** Storage for the legend items. */
    private List items;

    /**
     * Constructs a new legend item collection, initially empty.
     */
    public LegendItemCollection() {
        this.items = new java.util.ArrayList();
    }

    /**
     * Adds a legend item to the collection.
     *
     * @param item  the item to add.
     */
    public void add(LegendItem item) {
        this.items.add(item);
    }

    /**
     * Adds the legend items from another collection to this collection.
     *
     * @param collection  the other collection.
     */
    public void addAll(LegendItemCollection collection) {
        this.items.addAll(collection.items);
    }

    /**
     * Returns a legend item from the collection.
     *
     * @param index  the legend item index (zero-based).
     *
     * @return the legend item.
     */
    public LegendItem get(int index) {
        return (LegendItem) this.items.get(index);
    }

    /**
     * Returns the number of legend items in the collection.
     *
     * @return the item count.
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Returns an iterator that provides access to all the legend items.
     *
     * @return an iterator.
     */
    public Iterator iterator() {
        return items.iterator();
    }

}
