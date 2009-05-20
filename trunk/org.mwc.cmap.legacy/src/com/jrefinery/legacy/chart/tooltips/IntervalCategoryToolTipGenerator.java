/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
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
 * -------------------------------------
 * IntervalCategoryToolTipGenerator.java
 * -------------------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: IntervalCategoryToolTipGenerator.java,v 1.1.1.1 2003/07/17 10:06:44 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 10-Oct-2002 : Version 1 (DG);
 *
 */

package com.jrefinery.legacy.chart.tooltips;

import java.text.NumberFormat;
import java.text.DateFormat;

import com.jrefinery.legacy.data.CategoryDataset;
import com.jrefinery.legacy.data.IntervalCategoryDataset;

/**
 * A tooltip generator for plots that use data from an IntervalCategoryDataset.
 *
 * @author DG
 */
public class IntervalCategoryToolTipGenerator implements CategoryToolTipGenerator {

    /** The number formatter. */
    private NumberFormat numberFormat;

    /** The date formatter. */
    private DateFormat dateFormat;

    /**
     * Creates a new tool tip generator with a default number formatter.
     */
    public IntervalCategoryToolTipGenerator() {
        this(NumberFormat.getInstance());
    }

    /**
     * Creates a tool tip generator with the specified number formatter.
     *
     * @param formatter  the number formatter.
     */
    public IntervalCategoryToolTipGenerator(NumberFormat formatter) {
        this.numberFormat = formatter;
        this.dateFormat = null;
    }

    /**
     * Creates a tool tip generator with the specified date formatter.
     *
     * @param formatter  the date formatter.
     */
    public IntervalCategoryToolTipGenerator(DateFormat formatter) {
        this.numberFormat = null;
        this.dateFormat = formatter;
    }

    /**
     * Returns the number formatter.
     *
     * @return the number formatter.
     */
    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    /**
     * Returns the date formatter.
     *
     * @return the date formatter.
     */
    public DateFormat getDateFormat() {
        return this.dateFormat;
    }

    /**
     * Generates a tooltip text item for a particular category within a series.
     *
     * @param data  the dataset.
     * @param series  the series number (zero-based index).
     * @param category  the category.
     *
     * @return the tooltip text or <code>null</code> if value is <code>null</code>.
     */
    public String generateToolTip(CategoryDataset data, int series, Object category) {

        Number value = data.getValue(series, category);
        Number start = value;
        Number end = value;
        if (data instanceof IntervalCategoryDataset) {
            IntervalCategoryDataset icd = (IntervalCategoryDataset) data;
            start = icd.getStartValue(series, category);
            end = icd.getEndValue(series, category);
        }

        String result = "";
        if ((start != null) && (end != null)) {
            String seriesName = data.getSeriesName(series);
            if (seriesName != null) {
                result += seriesName + ", ";
            }
            String categoryName = category.toString();
            String startString = null;
            String endString = null;
            if (this.numberFormat != null) {
                startString = this.numberFormat.format(start);
                endString = this.numberFormat.format(end);
            }
            else if (this.dateFormat != null) {
                startString = this.dateFormat.format(start);
                endString = this.dateFormat.format(end);
            }
            result += categoryName + " : " + startString + " to " + endString;
        }

        return result;

    }

}
