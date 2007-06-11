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
 * ---------------------
 * EntityCollection.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: EntityCollection.java,v 1.1.1.1 2003/07/17 10:06:40 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 23-May-2002 : Version 1 (DG);
 * 25-Jun-2002 : Removed unnecessary import (DG);
 * 26-Jun-2002 : Added iterator() method (DG);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.entity;

import java.util.Iterator;

/**
 * Defines the methods that a collection of entities is required to implement.
 * <P>
 * The StandardEntityCollection class provides one implementation of this interface.
 *
 * @author DG
 */
public interface EntityCollection {

    /**
     * Clears all entities.
     */
    public void clear();

    /**
     * Adds an entity to the collection.
     *
     * @param entity  the entity.
     */
    public void addEntity(ChartEntity entity);

    /**
     * Returns an entity whose area contains the specified point.
     *
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     *
     * @return The entity.
     */
    public ChartEntity getEntity(double x, double y);

    /**
     * Returns an iterator for the entities in the collection.
     *
     * @return an iterator.
     */
    public Iterator iterator();

}
