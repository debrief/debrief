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
 * -----------------------------
 * StandardEntityCollection.java
 * -----------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: StandardEntityCollection.java,v 1.1.1.1 2003/07/17 10:06:40 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 23-May-2002 : Version 1 (DG);
 * 26-Jun-2002 : Added iterator() method (DG);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart.entity;

import java.util.Collection;
import java.util.Iterator;

/**
 * A default implementation of the EntityCollection interface.
 *
 * @author DG
 */
public class StandardEntityCollection implements EntityCollection {

    /** Storage for the entities. */
    private Collection<ChartEntity> entities;

    /**
     * Constructs a new entity collection (initially empty).
     */
    public StandardEntityCollection() {
        entities = new java.util.ArrayList<ChartEntity>();
    }

    /**
     * Clears the entities.
     */
    public void clear() {
        entities.clear();
    }

    /**
     * Adds an entity.
     *
     * @param entity  the entity.
     */
    public void addEntity(ChartEntity entity) {
        entities.add(entity);
    }

    /**
     * Returns an entity for the specified coordinates.
     *
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     *
     * @return the entity.
     */
    public ChartEntity getEntity(double x, double y) {

        ChartEntity result = null;

        Iterator<ChartEntity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            ChartEntity entity = (ChartEntity) iterator.next();
            if (entity.getArea().contains(x, y)) {
                result = entity;
            }
        }

        return result;
    }

    /**
     * Returns an iterator for the entities in the collection.
     *
     * @return an iterator.
     */
    @SuppressWarnings("unchecked")
		public Iterator iterator() {
        return entities.iterator();
    }

}
