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
 * -----------
 * Values.java
 * -----------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Values.java,v 1.1.1.1 2003/07/17 10:06:56 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 08-Nov-2001 : Version 1 (DG);
 *
 */

package com.jrefinery.data;

/**
 * An interface through which (single-dimension) data values can be accessed.
 *
 * @author DG
 */
public interface Values {

    /**
     * Returns the number of values in the collection.
     *
     * @return the number of values in the collection.
     */
    public int getValueCount();

    /**
     * Returns a value.
     *
     * @param index  the index of the item of interest (zero-based).
     *
     * @return the value.
     */
    public Number getValue(int index);

}
