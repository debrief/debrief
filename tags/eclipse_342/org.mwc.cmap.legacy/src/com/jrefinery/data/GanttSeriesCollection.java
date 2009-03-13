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
 * --------------------------
 * GanttSeriesCollection.java
 * --------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: GanttSeriesCollection.java,v 1.1.1.1 2003/07/17 10:06:53 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 06-Jun-2002 : Version 1 (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data;

import java.util.List;
import java.util.Iterator;

/**
 * A collection of GanttSeries objects.
 * <P>
 * This class provides one implementation of the IntervalCategoryDataset
 * interface.  It is sufficient to provide a demonstration of a simple
 * Gantt chart.
 *
 * @author DG
 */
public class GanttSeriesCollection extends AbstractSeriesDataset
                                   implements IntervalCategoryDataset {

    /** Storage for the aggregate task list. */
    private List<Object> tasks;

    /** Storage for the series. */
    private List<GanttSeries> data;

    /**
     * Default constructor.
     */
    public GanttSeriesCollection() {
        this.tasks = new java.util.ArrayList<Object>();
        this.data = new java.util.ArrayList<GanttSeries>();
    }

    /**
     * Returns the name of a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the name of a series.
     */
    public String getSeriesName(int series) {
        GanttSeries gs = (GanttSeries) data.get(series);
        return gs.getName();
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return the series count.
     */
    public int getSeriesCount() {
        return data.size();
    }

    /**
     * Returns the number of categories in the dataset.
     *
     * @return The category count.
     */
    public int getCategoryCount() {
        return tasks.size();
    }

    /**
     * Returns a list of the categories in the dataset.
     *
     * @return the category list.
     */
    public List<Object> getCategories() {
        return this.tasks;
    }

    /**
     * Adds a series to the dataset.
     *
     * @param series  the series.
     */
    public void add(GanttSeries series) {
        data.add(series);
        Iterator<Object> iterator = series.getTasks().iterator();
        while (iterator.hasNext()) {
            Object category = iterator.next();
            int index = this.tasks.indexOf(category);
            if (index < 0) {
                this.tasks.add(category);
            }
        }
    }

    /**
     * Returns the value for a series and category.
     *
     * @param series  the series (zero-based index).
     * @param category  the category.
     *
     * @return the value for a series and category.
     */
    public Number getValue(int series, Object category) {
        GanttSeries gs = (GanttSeries) this.data.get(series);
        TimeAllocation ta = gs.getTimeAllocation(category);
        return new Long(ta.getStart().getTime());
    }

    /**
     * Returns the min value for the specified series (zero-based index) and
     * category.
     *
     * @param series  the series index (zero-based).
     * @param category  the category.
     *
     * @return the min value for the specified series and category.
     */
    public Number getStartValue (int series, Object category) {
        GanttSeries gs = (GanttSeries) this.data.get(series);
        TimeAllocation ta = gs.getTimeAllocation(category);
        return new Long(ta.getStart().getTime());
    }

    /**
     * Returns the max value for the specified series (zero-based index) and category.
     *
     * @param series  the series index (zero-based).
     * @param category  the category.
     *
     * @return the max value for the specified series and category.
     */
    public Number getEndValue (int series, Object category) {
        GanttSeries gs = (GanttSeries) this.data.get(series);
        TimeAllocation ta = gs.getTimeAllocation(category);
        return new Long(ta.getEnd().getTime());
    }

}
