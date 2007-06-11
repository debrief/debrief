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
 * ----------------------
 * DefaultPieDataset.java
 * ----------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Sam (oldman);
 *
 * $Id: DefaultPieDataset.java,v 1.1.1.1 2003/07/17 10:06:51 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 17-Nov-2001 : Version 1 (DG);
 * 22-Jan-2002 : Removed legend methods from dataset implementations (DG);
 * 07-Apr-2002 : Modified implementation to guarantee data sequence to remain in the order
 *               categories are added (oldman);
 *
 */

package com.jrefinery.data;

import java.util.Collection;
import java.util.List;
import java.util.Arrays;

/**
 * A default implementation of the PieDataset interface.
 *
 * @author DG
 */
public class DefaultPieDataset extends AbstractDataset implements PieDataset {

    /**
     * Storage for keys
     */
    protected List keys = null;

    /**
     * Storage for values
     */
    protected List vals = null;

    /**
     * Constructs a pie dataset, initially empty.
     */
    public DefaultPieDataset() {

        keys = new java.util.ArrayList();
        vals = new java.util.ArrayList();

    }

    /**
     * Constructs a pie dataset and populates it with data from the array.
     *
     * @param data  the data.
     */
    public DefaultPieDataset(Number[] data) {

        this(Arrays.asList(data));

    }

    /**
     * Constructs a pie dataset, and populates it with the given values.
     *
     * @param values  a collection of values.
     */
    public DefaultPieDataset(Collection values) {

        keys = new java.util.ArrayList(values.size());
        vals = new java.util.ArrayList(values);

        for (int i = 0; i < vals.size(); i++) {
            keys.add(String.valueOf(i + 1));
        }

    }

    /**
     * Returns the categories in the dataset.
     *
     * @return the categories in the dataset.
     */
    public List getCategories() {
        return keys;
    }

    /**
     * Returns the data value for a category.
     *
     * @param category  the required category.
     *
     * @return the data value for a category (null possible).
     */
    public Number getValue(Object category) {

        // check arguments...
        if (category == null) {
            throw new IllegalArgumentException("DefaultPieDataset: null category not allowed.");
        }

        // fetch the value...
        return (Number) vals.get(keys.indexOf(category));

    }

    /**
     * Sets the data value for one category in a series.
     *
     * @param category  the category.
     * @param value  the value.
     */
    public void setValue(Object category, Number value) {

        int idx = keys.indexOf(category);

        if (idx == -1) {
            keys.add(category);
            vals.add(value);
        }
        else {
            vals.set(idx, value);
        }

        fireDatasetChanged();

    }

}
