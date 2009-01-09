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
 * ---------------------------
 * DefaultCategoryDataset.java
 * ---------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: DefaultCategoryDataset.java,v 1.1.1.1 2003/07/17 10:06:51 Ian.Mayo Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Moved to new package (com.jrefinery.data.*) (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc (DG);
 * 15-Nov-2001 : Added argument checking code (DG);
 * 16-Jan-2002 : Implemented setValue(...) method (DG);
 * 05-Feb-2002 : Fix for bug mapping category to index value (DG);
 * 07-Jun-2002 : Fixed bug ID 565819, minor issue in setValue method (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.data;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * A convenience class that provides a default implementation of the
 * CategoryDataset interface.
 * <p>
 * The standard constructor accepts data in a two dimensional array where the
 * first dimension is the series, and the second dimension is the category.
 *
 * @author DG
 */
public class DefaultCategoryDataset extends AbstractSeriesDataset implements CategoryDataset {

    /** The series names. */
    protected String[] seriesNames;

    /** The categories. */
    protected Object[] categories;

    /** Storage for the data. */
    protected Number[][] data;

    /**
     * Creates a new category dataset.
     * <p>
     * The array is indexed as data[series][category].  Series and category
     * names are automatically generated, but you can change them using the
     * setSeriesName(...) and setCategory(...) methods.
     *
     * @param data  the data.
     */
    public DefaultCategoryDataset(double[][] data) {

        this(DatasetUtilities.createNumberArray2D(data));

    }

    /**
     * Constructs a dataset and populates it with data from the array.
     * <p>
     * The array is indexed as data[series][category].  Series and category
     * names are automatically generated, but you can change them using the
     * setSeriesName(...) and setCategory(...) methods.
     *
     * @param data The data.
     */
    public DefaultCategoryDataset(Number[][] data) {

        this(null, null, data);

    }

    /**
     * Constructs a DefaultCategoryDataset, populates it with data from the
     * array, and uses the supplied names for the series.
     * <p>
     * Category names are generated automatically ("Category 1", "Category 2",
     * etc).
     *
     * @param seriesNames  the series names.
     * @param data  the data, indexed as data[series][category].
     */
    public DefaultCategoryDataset(String[] seriesNames, Number[][] data) {

        this(seriesNames, null, data);

    }

    /**
     * Constructs a DefaultCategoryDataset, populates it with data from the
     * array, and uses the supplied names for the series and the supplied
     * objects for the categories.
     *
     * @param seriesNames  the series names.
     * @param categories  the categories.
     * @param data  the data, indexed as data[series][category].
     */
    public DefaultCategoryDataset(String[] seriesNames, Object[] categories, Number[][] data) {

        this.data = data;

        if (data != null) {

            String baseName = "com.jrefinery.data.resources.DataPackageResources";
            ResourceBundle resources = ResourceBundle.getBundle(baseName);

            int seriesCount = data.length;
            if (seriesCount > 0) {

                // set up the series names...
                if (seriesNames != null) {

                    if (seriesNames.length != seriesCount) {
                        throw new IllegalArgumentException(
                            "DefaultCategoryDataset: the number of "
                            + "series names does not match the number of "
                            + "series in the data.");
                    }

                    this.seriesNames = seriesNames;
                }
                else {
                    String prefix = resources.getString("series.default-prefix") + " ";
                    this.seriesNames = generateNames(seriesCount, prefix);
                }

                // set up the category names...
                int categoryCount = data[0].length;
                if (categories != null) {
                    if (categories.length != categoryCount) {
                        throw new IllegalArgumentException(
                            "DefaultCategoryDataset: the number of "
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
     * @return the number of series in the dataset.
     */
    public int getSeriesCount() {

        int result = 0;
        if (data != null) {
            result = data.length;
        }
        return result;

    }

    /**
     * Returns the name of the specified series.
     *
     * @param series  the index of the required series (zero-based).
     *
     * @return the name of the specified series.
     */
    public String getSeriesName(int series) {

        // check argument...
        if ((series >= getSeriesCount()) || (series < 0)) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.getSeriesName(int): no such series.");
        }

        // return the value...
        return seriesNames[series];

    }

    /**
     * Sets the name of a series.
     *
     * @param series  the series (zero-based index).
     * @param name  the series name.
     */
    public void setSeriesName(int series, String name) {

        // check arguments...
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.setSeriesName(...): no such series.");
        }

        if (name == null) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.setSeriesName(...): null not permitted.");
        }

        seriesNames[series] = name;
        fireDatasetChanged();

    }

    /**
     * Sets the names of all the series in the dataset.
     *
     * @param seriesNames  the series names.
     */
    public void setSeriesNames(String[] seriesNames) {

        // check argument...
        if (seriesNames == null) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.setSeriesNames(): null not permitted.");
        }

        if (seriesNames.length != getSeriesCount()) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.setSeriesNames(): the "
                + "number of series names does not match the data.");
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

        if (data != null) {
            if (getSeriesCount() > 0) {
                result = data[0].length;
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
            return new java.util.ArrayList<Object>();
        }
        else {
            return Collections.unmodifiableList(Arrays.asList(categories));
        }

    }

    /**
     * Sets the categories for the dataset.
     *
     * @param categories  an array of objects representing the categories in the dataset.
     */
    public void setCategories(Object[] categories) {

        // check arguments...
        if (categories == null) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.setCategories(...): null not permitted.");
        }

        if (categories.length != data[0].length) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.setCategories(...): "
                + "the number of categories does not match the data.");
        }

        for (int i = 0; i < categories.length; i++) {
            if (categories[i] == null) {
                throw new IllegalArgumentException(
                    "DefaultCategoryDataset.setCategories(...): null category not permitted.");
            }
        }

        // make the change...
        this.categories = categories;
        fireDatasetChanged();

    }

    /**
     * Returns the data value for one category in a series.
     * <P>
     * This method is part of the CategoryDataset interface.
     *
     * @param series  the required series (zero based index).
     * @param category  the required category.
     * @return  the data value for one category in a series (null possible).
     */
    public Number getValue(int series, Object category) {

        // check arguments...
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.getValue(...): series index out of range.");
        }

        if (category == null) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.getValue(...): null category not allowed.");
        }

        int categoryIndex = this.getCategoryIndex(category);

        if (categoryIndex < 0) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.getValue(...): unknown category.");
        }

        // fetch the value...
        return data[series][categoryIndex];

    }

    /**
     * Sets the data value for one category in a series.
     *
     * @param series  the series (zero-based index).
     * @param category  the category.
     * @param value  the value.
     */
    public void setValue(int series, Object category, Number value) {

        // does the series exist?
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.setValue: series outside valid range.");
        }

        // is the category valid?
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException(
                "DefaultCategoryDataset.setValue: unrecognised category.");
        }

        // update the data...
        data[series][categoryIndex] = value;
        fireDatasetChanged();

    }

    /**
     * Returns the category index for the given category.
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
     * @return an array of <i>prefixN</i> with N = { 1 .. count }.
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
