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
 * --------------------------------------
 * DefaultStatisticalCategoryDataset.java
 * --------------------------------------
 * (C) Copyright 2002, by Pascal Collet.
 *
 * Original Author:  Pascal Collet;
 * Contributor(s):   -;
 *
 * $Id: DefaultStatisticalCategoryDataset.java,v 1.1.1.1 2003/07/17 10:06:52 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 21-Aug-2002 : Version 1, contributed by Pascal Collet (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * A convenience class that provides a default implementation of the
 * <code>StatisticalCategoryDataset</code> interface.
 * <p>
 * The standard constructor accepts data in a two dimensional array where the
 * first dimension is the series, and the second dimension is the category.
 *
 * @author PC
 */
public class DefaultStatisticalCategoryDataset extends AbstractSeriesDataset
                                               implements StatisticalCategoryDataset {

    /** The series names. */
    private String[] seriesNames;

    /** The categories. */
    private Object[] categories;

    /** Storage for the mean value data. */
    private Number[][] meanData;

    /** Storage for the standard deviation value data. */
    private Number[][] stdDevData;

    /**
     * Creates a new dataset.
     *
     * @param mean  the mean value data.
     * @param stdDev  the standard deviation value data.
     */
    public DefaultStatisticalCategoryDataset(double[][] mean, double[][] stdDev) {

        this(DatasetUtilities.createNumberArray2D(mean),
             DatasetUtilities.createNumberArray2D(stdDev));

    }

    /**
     * Constructs a dataset and populates it with data from the array.
     * <p>
     * The arrays are indexed as data[series][category].  Series and category
     * names are automatically generated - you can change them using the
     * setSeriesName(...) and setCategory(...) methods.
     *
     * @param mean  the mean value data
     * @param stdDev  the standard deviation value data
     */
    public DefaultStatisticalCategoryDataset(Number[][] mean, Number[][] stdDev) {

        this(null, null, mean, stdDev);

    }

    /**
     * Constructs a DefaultStatisticalCategoryDataset, populates it with data
     * from the arrays, and uses the supplied names for the series.
     * <p>
     * Category names are generated automatically ("Category 1", "Category 2", etc).
     *
     * @param seriesNames  the series names.
     * @param mean  the mean values data, indexed as data[series][category].
     * @param stdDev  the stdDev values data, indexed as data[series][category].
     */
    public DefaultStatisticalCategoryDataset(String[] seriesNames,
                                             Number[][] mean,
                                             Number[][] stdDev) {

        this(seriesNames, null, mean, stdDev);

    }

    /**
     * Constructs a DefaultStatisticalCategoryDataset, populates it with data
     * from the arrays, and uses the supplied names for the series and the
     * supplied objects for the categories.
     *
     * @param seriesNames  the series names.
     * @param categories  the categories.
     * @param mean  the mean values data, indexed as data[series][category].
     * @param stdDev  the stdDev values data, indexed as data[series][category].
     */
    public DefaultStatisticalCategoryDataset(String[] seriesNames,
                                             Object[] categories,
                                             Number[][] mean,
                                             Number[][] stdDev) {

        this.meanData = mean;
        this.stdDevData = stdDev;

        if (mean != null && stdDev != null) {

            String baseName = "com.jrefinery.legacy.data.resources.DataPackageResources";
            ResourceBundle resources = ResourceBundle.getBundle(baseName);

            int seriesCount = mean.length;
            if (seriesCount != stdDev.length) {
                String errMsg = "DefaultStatisticalCategoryDataset: the number "
                      + "of series in the start value dataset does "
                      + "not match the number of series in the end "
                      + "value dataset.";
                throw new IllegalArgumentException(errMsg);
            }
            if (seriesCount > 0) {

                // set up the series names...
                if (seriesNames != null) {

                    if (seriesNames.length != seriesCount) {
                        throw new IllegalArgumentException("DefaultStatisticalCategoryDataset: "
                            + "the number of series names does not match the number of series in "
                            + "the data.");
                    }

                    this.seriesNames = seriesNames;
                }
                else {
                    String prefix = resources.getString("series.default-prefix") + " ";
                    this.seriesNames = generateNames(seriesCount, prefix);
                }

                // set up the category names...
                int categoryCount = mean[0].length;
                if (categoryCount != stdDev[0].length) {
                    String errMsg = "DefaultStatisticalCategoryDataset: the number of categories "
                        + "in the mean value dataset does not match the number of "
                        + "categories in the standard deviation value dataset.";
                    throw new IllegalArgumentException(errMsg);
                }
                if (categories != null) {
                    if (categories.length != categoryCount) {
                        throw new IllegalArgumentException("DefaultStatisticalCategoryDataset: "
                            + "the number of categories does not match the number of categories "
                            + "in the data.");
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
        if (meanData != null) {
            result = meanData.length;
        }
        return result;

    }

    /**
     * Returns the name of the specified series.
     *
     * @param series the index of the required series (zero-based).
     *
     * @return the series name
     */
    public String getSeriesName(int series) {

        // check argument...
        if ((series >= getSeriesCount()) || (series < 0)) {

            throw new IllegalArgumentException("DefaultStatisticalCategoryDataset"
                + ".getSeriesName(int): no such series.");
        }

        // return the value...
        return seriesNames[series];

    }

    /**
     * Sets the names of the series in the dataset.
     * @param seriesNames The names of the series in the dataset.
     */
    public void setSeriesNames(String[] seriesNames) {

        // check argument...
        if (seriesNames == null) {
            throw new IllegalArgumentException("DefaultStatisticalCategoryDataset"
                + ".setSeriesNames(): null not permitted.");
        }

        if (seriesNames.length != getSeriesCount()) {
            throw new IllegalArgumentException("DefaultStatisticalCategoryDataset"
                + ".setSeriesNames(): the number of series names does not match the data.");
        }

        // make the change...
        this.seriesNames = seriesNames;
        fireDatasetChanged();

    }

    /**
     * Returns the number of categories in the dataset.
     * <P>
     * This method is part of the CategoryDataset interface.
     * @return The number of categories in the dataset.
     */
    public int getCategoryCount() {

        int result = 0;

        if (meanData != null) {
            if (getSeriesCount() > 0) {
                result = meanData[0].length;
            }
        }

        return result;

    }

    /**
     * Returns a list of the categories in the dataset.
     * <P>
     * Supports the CategoryDataset interface.
     * @return A list of the categories in the dataset.
     */
    public List<Object> getCategories() {

      // the CategoryDataset interface expects a list of categories, but we've stored them in
      // an array...
      if (categories == null) {
        return new ArrayList<Object>();
      }
      else {
        return Collections.unmodifiableList(Arrays.asList(categories));
      }

    }

    /**
     * Sets the categories for the dataset.
     * @param categories An array of objects representing the categories in the dataset.
     */
    public void setCategories(Object[] categories) {

      // check arguments...
      if (categories == null) {
        throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.setCategories(...): "
            + "null not permitted.");
      }

      if (categories.length != meanData[0].length) {
        throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.setCategories(...): "
            + "the number of categories does not match the data.");
      }

      for (int i = 0; i < categories.length; i++) {
        if (categories[i] == null) {
          throw new IllegalArgumentException("DefaultStatisticalCategoryDataset"
              + ".setCategories(...): null category not permitted.");
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
     * meaningful for this class...but it is used in the DatasetUtilities when
     * computing the range extent so we should be careful to return a value large
     * enough so that the mean+stdDev value will always be plotted properly.
     *
     * @param series  the required series (zero based index).
     * @param category  the required category.
     *
     * @return the data value for one category in a series (null possible).
     */
    public Number getValue(int series, Object category) {

        double m = getMeanValue(series, category).doubleValue();
        double s = getStdDevValue(series, category).doubleValue();
        return new Double(m + s);

    }

    /**
     * Returns the mean data value for one category in a series.
     * <P>
     * This method is part of the StatisticalCategoryDataset interface.
     * @param series The required series (zero based index).
     * @param category The required category.
     * @return The mean data value for one category in a series (null possible).
     */
    public Number getMeanValue(int series, Object category) {
      // check arguments...
      if ((series < 0) || (series >= getSeriesCount())) {
          throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.getValue(...): "
              + "series index out of range.");
      }

      if (category == null) {
          throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.getValue(...): "
              + "null category not allowed.");
      }

      int categoryIndex = getCategoryIndex(category);

      if (categoryIndex < 0) {
          throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.getValue(...): "
              + "unknown category.");
      }

      // fetch the value...
      return meanData[series][categoryIndex];

    }

    /**
     * Returns the standard deviation data value for one category in a series.
     * <P>
     * This method is part of the IntervalCategoryDataset interface.
     * @param series The required series (zero based index).
     * @param category The required category.
     * @return The standard deviation data value for one category in a series (null possible).
     */
    public Number getStdDevValue(int series, Object category) {

      // check arguments...
      if ((series < 0) || (series >= getSeriesCount())) {
          throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.getValue(...): "
              + "series index out of range.");
      }

      if (category == null) {
          throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.getValue(...): "
              + "null category not allowed.");
      }

      int categoryIndex = this.getCategoryIndex(category);

      if (categoryIndex < 0) {
          throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.getValue(...): "
              + "unknown category.");
      }

      // fetch the value...
      return stdDevData[series][categoryIndex];

    }

    /**
     * Sets the mean data value for one category in a series.
     *
     * @param series The series (zero-based index).
     * @param category The category.
     * @param value The value.
     */
    public void setMeanValue(int series, Object category, Number value) {

      // does the series exist?
      if ((series < 0) || (series > getSeriesCount())) {
          throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.setValue: "
              + "series outside valid range.");
      }

      // is the category valid?
      int categoryIndex = getCategoryIndex(category);
      if (categoryIndex < 0) {
          throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.setValue: "
              + "unrecognised category.");
      }

      // update the data...
      meanData[series][categoryIndex] = value;
      fireDatasetChanged();

    }

    /**
     * Sets the mean data values.
     *
     * @param value  the data.
     */
    public void setMeanValue(Number[][] value) {

        meanData = value;
        fireDatasetChanged();

    }

    /**
     * Sets the standard deviation data value for one category in a series.
     *
     * @param series The series (zero-based index).
     * @param category The category.
     * @param value The value.
     */
    public void setStdDevValue(int series, Object category, Number value) {

        // does the series exist?
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.setValue: "
                + "series outside valid range.");
        }

        // is the category valid?
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException("DefaultStatisticalCategoryDataset.setValue: "
                + "unrecognised category.");
        }

        // update the data...
        stdDevData[series][categoryIndex] = value;
        fireDatasetChanged();

    }

    /**
     * Sets the standard deviation values.
     *
     * @param value  the values.
     */
    public void setStdDevValue(Number[][] value) {
        stdDevData = value;
        fireDatasetChanged();
    }

    /**
     * Returns the index of the specified category.
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
     * Generates an array of names, by appending a space plus an integer (starting with 1)
     * to the supplied prefix string.
     *
     * @param count the number of names required.
     * @param prefix the name prefix.
     *
     * @return  an array of names.
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
