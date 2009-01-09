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
 * -----------------------------------
 * DefaultIntervalCategoryDataset.java
 * -----------------------------------
 * (C) Copyright 2002, by Jeremy Bowman.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   -;
 *
 * $Id: DefaultIntervalCategoryDataset.java,v 1.1.1.1 2003/07/17 10:06:51 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 29-Apr-2002 : Version 1, contributed by Jeremy Bowman (DG);
 *
 */

package com.jrefinery.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * A convenience class that provides a default implementation of the
 * IntervalCategoryDataset interface.
 * <p>
 * The standard constructor accepts data in a two dimensional array where the
 * first dimension is the series, and the second dimension is the category.
 *
 * @author JB
 */
public class DefaultIntervalCategoryDataset extends AbstractSeriesDataset
                                            implements IntervalCategoryDataset {

    /** The series names. */
    private String[] seriesNames;

    /** The categories. */
    private Object[] categories;

    /** Storage for the start value data. */
    private Number[][] startData;

    /** Storage for the end value data. */
    private Number[][] endData;

    /**
     * Creates a new dataset.
     *
     * @param starts  the starting values for the intervals.
     * @param ends  the ending values for the intervals.
     */
    public DefaultIntervalCategoryDataset(double[][] starts, double[][] ends) {

        this(DatasetUtilities.createNumberArray2D(starts),
             DatasetUtilities.createNumberArray2D(ends));

    }

    /**
     * Constructs a dataset and populates it with data from the array.
     * <p>
     * The arrays are indexed as data[series][category].  Series and category
     * names are automatically generated - you can change them using the
     * setSeriesName(...) and setCategory(...) methods.
     *
     * @param starts  the start values data.
     * @param ends  the end values data.
     */
    public DefaultIntervalCategoryDataset(Number[][] starts, Number[][] ends) {

        this(null, null, starts, ends);

    }

    /**
     * Constructs a DefaultIntervalCategoryDataset, populates it with data
     * from the arrays, and uses the supplied names for the series.
     * <p>
     * Category names are generated automatically ("Category 1", "Category 2",
     * etc).
     *
     * @param seriesNames  the series names.
     * @param starts  the start values data, indexed as data[series][category].
     * @param ends  the end values data, indexed as data[series][category].
     */
    public DefaultIntervalCategoryDataset(String[] seriesNames,
                                          Number[][] starts,
                                          Number[][] ends) {

        this(seriesNames, null, starts, ends);

    }

    /**
     * Constructs a DefaultIntervalCategoryDataset, populates it with data
     * from the arrays, and uses the supplied names for the series and the
     * supplied objects for the categories.
     *
     * @param seriesNames  the series names.
     * @param categories  the categories.
     * @param starts  the start values data, indexed as data[series][category].
     * @param ends  the end values data, indexed as data[series][category].
     */
    public DefaultIntervalCategoryDataset(String[] seriesNames,
                                          Object[] categories,
                                          Number[][] starts,
                                          Number[][] ends) {

        this.startData = starts;
        this.endData = ends;

        if (starts != null && ends != null) {

            String baseName = "com.jrefinery.data.resources.DataPackageResources";
            ResourceBundle resources = ResourceBundle.getBundle(baseName);

            int seriesCount = starts.length;
            if (seriesCount != ends.length) {
                String errMsg = "DefaultIntervalCategoryDataset: the number "
                    + "of series in the start value dataset does "
                    + "not match the number of series in the end "
                    + "value dataset.";
                throw new IllegalArgumentException(errMsg);
            }
            if (seriesCount > 0) {

                // set up the series names...
                if (seriesNames != null) {

                    if (seriesNames.length != seriesCount) {
                        throw new IllegalArgumentException(
                            "DefaultIntervalCategoryDataset: the number of "
                            + "series names does not match the number "
                            + "of series in the data.");
                    }

                    this.seriesNames = seriesNames;
                }
                else {
                    String prefix = resources.getString("series.default-prefix") + " ";
                    this.seriesNames = this.generateNames(seriesCount, prefix);
                }

                // set up the category names...
                int categoryCount = starts[0].length;
                if (categoryCount != ends[0].length) {
                    String errMsg = "DefaultIntervalCategoryDataset: the "
                                + "number of categories in the start value "
                                + "dataset does not match the number of "
                                + "categories in the end value dataset.";
                    throw new IllegalArgumentException(errMsg);
                }
                if (categories != null) {
                    if (categories.length != categoryCount) {
                        throw new IllegalArgumentException(
                            "DefaultIntervalCategoryDataset: the number of "
                            + "categories does not match the number of "
                            + "categories in the data.");
                    }
                    this.categories = categories;
                }
                else {
                    String prefix = resources.getString("categories.default-prefix") + " ";
                    this.categories = generateNames(categoryCount, prefix);
                }

            }
            else {
                this.seriesNames = null;
                this.categories = null;
            }
        }

    }

    /**
     * Returns the number of series in the dataset (possibly zero).
     *
     * @return The number of series in the dataset.
     */
    public int getSeriesCount() {

        int result = 0;
        if (startData != null) {
            result = startData.length;
        }
        return result;

    }

    /**
     * Returns the name of the specified series.
     *
     * @param series    The index of the required series (zero-based).
     *
     * @return the name of the specified series.
     */
    public String getSeriesName(int series) {

        // check argument...
        if ((series >= getSeriesCount()) || (series < 0)) {

            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.getSeriesName(int): no such series.");
        }

        // return the value...
        return seriesNames[series];

    }

    /**
     * Sets the names of the series in the dataset.
     * @param seriesNames   The names of the series in the dataset.
     */
    public void setSeriesNames(String[] seriesNames) {

        // check argument...
        if (seriesNames == null) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.setSeriesNames(): "
                + "null not permitted.");
        }

        if (seriesNames.length != getSeriesCount()) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.setSeriesNames(): "
                + "the number of series names does not match the data.");
        }

        // make the change...
        this.seriesNames = seriesNames;
        fireDatasetChanged();

    }

    /**
     * Returns the number of categories in the dataset.
     * <P>
     * This method is part of the CategoryDataset interface.
     *
     * @return the number of categories in the dataset.
     */
    public int getCategoryCount() {

        int result = 0;

        if (startData != null) {
            if (getSeriesCount() > 0) {
                result = startData[0].length;
            }
        }

        return result;

    }

    /**
     * Returns a list of the categories in the dataset.
     * <P>
     * Supports the CategoryDataset interface.
     *
     * @return a list of the categories in the dataset.
     */
    public List<Object> getCategories() {

        // the CategoryDataset interface expects a list of categories, but
        // we've stored them in an array...
        if (categories == null) {
            return new ArrayList<Object>();
        }
        else {
            return Collections.unmodifiableList(Arrays.asList(categories));
        }

    }

    /**
     * Sets the categories for the dataset.
     * @param categories    An array of objects representing the categories in
     *      the dataset.
     */
    public void setCategories(Object[] categories) {

        // check arguments...
        if (categories == null) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.setCategories(...): "
                + "null not permitted.");
        }

        if (categories.length != startData[0].length) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.setCategories(...): "
                + "the number of categories does not match the data.");
        }

        for (int i = 0; i < categories.length; i++) {
            if (categories[i] == null) {
                throw new IllegalArgumentException(
                    "DefaultIntervalCategoryDataset.setCategories(...): "
                    + "null category not permitted.");
            }
        }

        // make the change...
        this.categories = categories;
        fireDatasetChanged();

    }

    /**
     * Returns the data value for one category in a series.
     * <P>
     * This method is part of the CategoryDataset interface.  Not particularly
     * meaningful for this class...returns the end value.
     * @param series    The required series (zero based index).
     * @param category  The required category.
     * @return The data value for one category in a series (null possible).
     */
    public Number getValue(int series, Object category) {

        return getEndValue(series, category);

    }

    /**
     * Returns the start data value for one category in a series.
     * <P>
     * This method is part of the IntervalCategoryDataset interface.
     * @param series    The required series (zero based index).
     * @param category  The required category.
     * @return The start data value for one category in a series (null
     *      possible).
     */
    public Number getStartValue(int series, Object category) {
        // check arguments...
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.getValue(...): "
                + "series index out of range.");
        }

        if (category == null) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.getValue(...): "
                + "null category not allowed.");
        }

        int categoryIndex = this.getCategoryIndex(category);

        if (categoryIndex < 0) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.getValue(...): unknown category.");
        }

        // fetch the value...
        return startData[series][categoryIndex];

    }

    /**
     * Returns the end data value for one category in a series.
     * <P>
     * This method is part of the IntervalCategoryDataset interface.
     *
     * @param series  the required series (zero based index).
     * @param category  the required category.
     *
     * @return the end data value for one category in a series (null possible).
     */
    public Number getEndValue(int series, Object category) {

        // check arguments...
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.getValue(...): "
                + "series index out of range.");
        }

        if (category == null) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.getValue(...): "
                + "null category not allowed.");
        }

        int categoryIndex = this.getCategoryIndex(category);

        if (categoryIndex < 0) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.getValue(...): unknown category.");
        }

        // fetch the value...
        return endData[series][categoryIndex];

    }

    /**
     * Sets the start data value for one category in a series.
     * @param series    The series (zero-based index).
     * @param category  The category.
     * @param value The value.
     */
    public void setStartValue(int series, Object category, Number value) {

        // does the series exist?
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.setValue: "
                + "series outside valid range.");
        }

        // is the category valid?
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.setValue: "
                + "unrecognised category.");
        }

        // update the data...
        startData[series][categoryIndex] = value;
        fireDatasetChanged();

    }

    /**
     * Sets the end data value for one category in a series.
     *
     * @param series  the series (zero-based index).
     * @param category  the category.
     *
     * @param value the value.
     */
    public void setEndValue(int series, Object category, Number value) {

        // does the series exist?
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.setValue: "
                + "series outside valid range.");
        }

        // is the category valid?
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException(
                "DefaultIntervalCategoryDataset.setValue: "
                + "unrecognised category.");
        }

        // update the data...
        endData[series][categoryIndex] = value;
        fireDatasetChanged();

    }

    /**
     * Returns the index for the given category.
     *
     * @param category  the category.
     *
     * @return the index.
     */
    private int getCategoryIndex(Object category) {

        int result = -1;
        for (int i = 0; i < categories.length; i++) {
            if (category.equals(categories[i])) {
                result = i;
                break;
            }
        }
        return result;

    }

    /**
     * Generates an array of names, by appending a space plus an integer
     * (starting with 1) to the supplied prefix string.
     *
     * @param count  the number of names required.
     * @param prefix  the name prefix.
     *
     * @return an array of <i>prefixN</i> with N = { 1 .. count}.
     */
    private String[] generateNames(int count, String prefix) {

        String[] result = new String[count];
        String name;
        for (int i = 0; i < count; i++) {
            name = prefix + (i + 1);
            result[i] = name;
        }
        return result;

    }

}
