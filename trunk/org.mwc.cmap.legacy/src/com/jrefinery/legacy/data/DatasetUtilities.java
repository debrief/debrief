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
 * DatasetUtilities.java
 * ---------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski (bug fix);
 *                   Jonathan Nash (bug fix);
 *
 * $Id: DatasetUtilities.java,v 1.1.1.1 2003/07/17 10:06:51 Ian.Mayo Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 15-Nov-2001 : Moved to package com.jrefinery.data.* in the JCommon class library (DG);
 *               Changed to handle null values from datasets (DG);
 *               Bug fix (thanks to Andrzej Porebski) - initial value now set to positive or
 *               negative infinity when iterating (DG);
 * 22-Nov-2001 : Datasets with containing no data now return null for min and max calculations (DG);
 * 13-Dec-2001 : Extended to handle HighLowDataset and IntervalXYDataset (DG);
 * 15-Feb-2002 : Added getMinimumStackedRangeValue() and getMaximumStackedRangeValue() (DG);
 * 28-Feb-2002 : Renamed Datasets.java --> DatasetUtilities.java (DG);
 * 18-Mar-2002 : Fixed bug in min/max domain calculation for datasets that implement the
 *               CategoryDataset interface AND the XYDataset interface at the same time.  Thanks
 *               to Jonathan Nash for the fix (DG);
 * 23-Apr-2002 : Added getDomainExtent() and getRangeExtent() methods (DG);
 * 13-Jun-2002 : Modified range measurements to handle IntervalCategoryDataset (DG);
 * 12-Jul-2002 : Method name change in DomainInfo interface (DG);
 * 30-Jul-2002 : Added pie dataset summation method (DG);
 * 01-Oct-2002 : Added a method for constructing an XYDataset from a Function2D instance (DG);
 *
 */

package com.jrefinery.legacy.data;

import java.util.Iterator;
import java.util.Collection;
import java.util.List;

/**
 * This class contains static methods that perform various useful functions
 * relating to datasets.
 *
 * @author DG
 */
public class DatasetUtilities {

    /**
     * Constructs an array of Number objects from an array of doubles.
     *
     * @param data  the data.
     *
     * @return data as array of Number.
     */
    public static Number[] createNumberArray(double[] data) {

        Number[] result = new Number[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = new Double(data[i]);
        }

        return result;

    }

    /**
     * Constructs an array of arrays of Number objects from a corresponding
     * structure containing double primitives.
     *
     * @param data  the data.
     *
     * @return data as array of Number.
     */
    public static Number[][] createNumberArray2D(double[][] data) {

        int l1 = data.length;
        int l2 = data[0].length;

        Number[][] result = new Number[l1][l2];

        for (int i = 0; i < l1; i++) {
            result[i] = createNumberArray(data[i]);
        }

        return result;

    }

    /**
     * Returns the range of values in the domain for the dataset.
     *
     * @param data  the dataset.
     *
     * @return the range of values.
     */
    public static Range getDomainExtent(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "Datasets.getMinimumDomainValue: null dataset not allowed.");
        }

        if ((data instanceof CategoryDataset) && !(data instanceof XYDataset)) {
            throw new IllegalArgumentException("Datasets.getMinimumDomainValue(...): "
                +  "CategoryDataset does not have numerical domain.");
        }

        // work out the minimum value...
        if (data instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) data;
            return info.getDomainRange();
        }

        // hasn't implemented DomainInfo, so iterate...
        else if (data instanceof XYDataset) {
            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;
            XYDataset xyData = (XYDataset) data;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number lvalue = null;
                    Number uvalue = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        lvalue = intervalXYData.getStartXValue(series, item);
                        uvalue = intervalXYData.getEndXValue(series, item);
                    }
                    else {
                        lvalue = xyData.getXValue(series, item);
                        uvalue = lvalue;
                    }
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Range(minimum, maximum);
            }
        }
        else {
            return null; // unrecognised dataset...how should this be handled?
        }

    }

    /**
     * Returns the range of values in the range for the dataset.  This method
     * is the partner for the getDomainExtent method.
     *
     * @param data  the dataset.
     *
     * @return the range of values in the range for the dataset.
     */
    public static Range getRangeExtent(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "Datasets.getMinimumRangeValue: null dataset not allowed.");
        }

        // work out the minimum value...
        if (data instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) data;
            return info.getValueRange();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (data instanceof CategoryDataset) {

            CategoryDataset categoryData = (CategoryDataset) data;
            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = categoryData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                Iterator<Object> iterator = categoryData.getCategories().iterator();
                while (iterator.hasNext()) {
                    Object category = iterator.next();
                    Number lvalue = null;
                    Number uvalue = null;
                    if (data instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) data;
                        lvalue = icd.getStartValue(series, category);
                        uvalue = icd.getEndValue(series, category);
                    }
                    else {
                        lvalue = categoryData.getValue(series, category);
                        uvalue = lvalue;
                    }
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Range(minimum, maximum);
            }

        }
        else if (data instanceof XYDataset) {

            // hasn't implemented RangeInfo, so we'll have to iterate...
            XYDataset xyData = (XYDataset) data;
            double minimum = Double.POSITIVE_INFINITY;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number lvalue = null;
                    Number uvalue = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        lvalue = intervalXYData.getStartYValue(series, item);
                        uvalue = intervalXYData.getEndYValue(series, item);
                    }
                    else if (data instanceof HighLowDataset) {
                        HighLowDataset highLowData = (HighLowDataset) data;
                        lvalue = highLowData.getLowValue(series, item);
                        uvalue = highLowData.getHighValue(series, item);
                    }
                    else {
                        lvalue = xyData.getYValue(series, item);
                        uvalue = lvalue;
                    }
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Range(minimum, maximum);
            }

        }
        else {
            return null;
        }

    }

    /**
     * Returns the minimum domain value for the specified dataset.
     * <P>
     * This is easy if the dataset implements the DomainInfo interface (a good
     * idea if there is an efficient way to determine the minimum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns null if all the data values in the dataset are null.
     *
     * @param data  the dataset.
     *
     * @return the minimum domain value in the dataset (or null).
     */
    public static Number getMinimumDomainValue(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "Datasets.getMinimumDomainValue: null dataset not allowed.");
        }

        if ((data instanceof CategoryDataset) && !(data instanceof XYDataset)) {
            throw new IllegalArgumentException("Datasets.getMinimumDomainValue(...): "
                + "CategoryDataset does not have numerical domain.");
        }

        // work out the minimum value...
        if (data instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) data;
            return info.getMinimumDomainValue();
        }

        // hasn't implemented DomainInfo, so iterate...
        else if (data instanceof XYDataset) {
            double minimum = Double.POSITIVE_INFINITY;
            XYDataset xyData = (XYDataset) data;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number value = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        value = intervalXYData.getStartXValue(series, item);
                    }
                    else {
                        value = xyData.getXValue(series, item);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }
        }

        else {
            return null; // unrecognised dataset...how should this be handled?
        }

    }

    /**
     * Returns the maximum domain value for the specified dataset.
     * <P>
     * This is easy if the dataset implements the DomainInfo interface (a good
     * idea if there is an efficient way to determine the maximum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns null if all the data values in the dataset are null.
     *
     * @param data  the dataset.
     *
     * @return the maximum domain value in the dataset (or null).
     */
    public static Number getMaximumDomainValue(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "Datasets.getMaximumDomainValue: null dataset not allowed.");
        }

        if ((data instanceof CategoryDataset) && !(data instanceof XYDataset)) {
            throw new IllegalArgumentException("Datasets.getMaximumDomainValue(...): "
                + "CategoryDataset does not have numerical domain.");
        }

        // work out the maximum value...
        if (data instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) data;
            return info.getMaximumDomainValue();
        }

        // hasn't implemented DomainInfo, so iterate...
        else if (data instanceof XYDataset) {
            XYDataset xyData = (XYDataset) data;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number value = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        value = intervalXYData.getEndXValue(series, item);
                    }
                    else {
                        value = xyData.getXValue(series, item);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }
        else {
            return null; // unrecognised dataset...how should this be handled?
        }

    }

    /**
     * Returns the minimum range value for the specified dataset.
     * <P>
     * This is easy if the dataset implements the RangeInfo interface (a good
     * idea if there is an efficient way to determine the minimum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns null if all the data values in the dataset are null.
     *
     * @param data  the dataset.
     *
     * @return the minimum range value in the dataset (or null).
     */
    public static Number getMinimumRangeValue(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "Datasets.getMinimumRangeValue: null dataset not allowed.");
        }

        // work out the minimum value...
        if (data instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) data;
            return info.getMinimumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (data instanceof CategoryDataset) {

            CategoryDataset categoryData = (CategoryDataset) data;
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = categoryData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                Iterator<Object> iterator = categoryData.getCategories().iterator();
                while (iterator.hasNext()) {
                    Object category = iterator.next();
                    Number value = null;
                    if (data instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) data;
                        value = icd.getStartValue(series, category);
                    }
                    else {
                        value = categoryData.getValue(series, category);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }
        else if (data instanceof XYDataset) {

            // hasn't implemented RangeInfo, so we'll have to iterate...
            XYDataset xyData = (XYDataset) data;
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    Number value = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        value = intervalXYData.getStartYValue(series, item);
                    }
                    else if (data instanceof HighLowDataset) {
                        HighLowDataset highLowData = (HighLowDataset) data;
                        value = highLowData.getLowValue(series, item);
                    }
                    else {
                        value = xyData.getYValue(series, item);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }
        else {
            return null;
        }

    }

    /**
     * Returns the maximum range value for the specified dataset.
     * <P>
     * This is easy if the dataset implements the RangeInfo interface (a good
     * idea if there is an efficient way to determine the maximum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns null if all the data values are null.
     *
     * @param data  the dataset.
     *
     * @return the maximum range value in the dataset (or null).
     */
    public static Number getMaximumRangeValue(Dataset data) {

        // check parameters...
        if (data == null) {
            throw new IllegalArgumentException(
                "Datasets.getMinimumRangeValue: null dataset not allowed.");
        }

        // work out the minimum value...
        if (data instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) data;
            return info.getMaximumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (data instanceof CategoryDataset) {

            CategoryDataset categoryData = (CategoryDataset) data;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = categoryData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                Iterator<Object> iterator = categoryData.getCategories().iterator();
                while (iterator.hasNext()) {
                    Object category = iterator.next();
                    Number value = null;
                    if (data instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) data;
                        value = icd.getEndValue(series, category);
                    }
                    else {
                        value = categoryData.getValue(series, category);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }
        else if (data instanceof XYDataset) {

            XYDataset xyData = (XYDataset) data;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    Number value = null;
                    if (data instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) data;
                        value = intervalXYData.getEndYValue(series, item);
                    }
                    else if (data instanceof HighLowDataset) {
                        HighLowDataset highLowData = (HighLowDataset) data;
                        value = highLowData.getHighValue(series, item);
                    }
                    else {
                        value = xyData.getYValue(series, item);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }
        else {
            return null;
        }

    }

    /**
     * Creates a pie dataset from a category dataset by taking all the values
     * (across series) for a single category.
     *
     * @param data  the data.
     * @param category  the category.
     *
     * @return a pie dataset.
     */
    public static PieDataset createPieDataset(CategoryDataset data, Object category) {

        DefaultPieDataset result = new DefaultPieDataset();
        int seriesCount = data.getSeriesCount();
        for (int i = 0; i < seriesCount; i++) {
            String seriesName = data.getSeriesName(i);
            result.setValue(seriesName, data.getValue(i, category));
        }
        return result;

    }

    /**
     * Creates a pie dataset from a category dataset by taking all the values
     * for a single series.
     *
     * @param data  the data.
     * @param series  the series (zero-based index).
     *
     * @return a pie dataset.
     */
    public static PieDataset createPieDataset(CategoryDataset data, int series) {

        DefaultPieDataset result = new DefaultPieDataset();
        Collection<Object> categories = data.getCategories();
        Iterator<Object> iterator = categories.iterator();
        while (iterator.hasNext()) {
            Object current = iterator.next();
            result.setValue(current, data.getValue(series, current));
        }
        return result;

    }

    /**
     * Calculates the total of all the values in a PieDataset.
     *
     * @param data  the dataset.
     *
     * @return the total.
     */
    public static double getPieDatasetTotal(PieDataset data) {

        // get a list of categories...
        List<Object> categories = data.getCategories();

        // compute the total value of the data series skipping over the
        // negative values
        double totalValue = 0;
        Iterator<Object> iterator = categories.iterator();
        while (iterator.hasNext()) {
            Object current = iterator.next();
            if (current != null) {
                Number value = data.getValue(current);
                double v = 0.0;
                if (value != null) {
                    v = value.doubleValue();
                }
                if (v > 0) {
                    totalValue = totalValue + v;
                }
            }
        }
        return totalValue;
    }

    /**
     * Returns the range of values for the range (as in domain/range) of the
     * dataset, assuming that the series in one category are stacked.
     *
     * @param data   the dataset.
     *
     * @return  the range of values for the range of the dataset.
     */
    public static Range getStackedRangeExtent(CategoryDataset data) {

        Range result = null;

        if (data != null) {

            double minimum = 0.0;
            double maximum = 0.0;

            Iterator<Object> iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {

                Object category = iterator.next();
                double positive = 0.0;
                double negative = 0.0;
                int seriesCount = data.getSeriesCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = data.getValue(series, category);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value > 0.0) {
                            positive = positive + value;
                        }
                        if (value < 0.0) {
                            negative = negative + value;  // '+', remember value is negative
                        }
                    }
                }
                minimum = Math.min(minimum, negative);
                maximum = Math.max(maximum, positive);

            }

            result = new Range(minimum, maximum);

        }

        return result;

    }

    /**
     * Returns the minimum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param data  the dataset.
     *
     * @return the minimum value.
     */
    public static Number getMinimumStackedRangeValue(CategoryDataset data) {

        Number result = null;

        if (data != null) {

            double minimum = 0.0;

            Iterator<Object> iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {

                Object category = iterator.next();
                double total = 0.0;

                int seriesCount = data.getSeriesCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = data.getValue(series, category);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value < 0.0) {
                            total = total + value;  // '+', remember value is negative
                        }
                    }
                }
                minimum = Math.min(minimum, total);

            }

            result = new Double(minimum);

        }

        return result;

    }

    /**
     * Returns the maximum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param data  the dataset.
     *
     * @return the maximum value.
     */
    public static Number getMaximumStackedRangeValue(CategoryDataset data) {

        Number result = null;

        if (data != null) {

            double maximum = 0.0;

            Iterator<Object> iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {

                Object category = iterator.next();
                double total = 0.0;

                int seriesCount = data.getSeriesCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = data.getValue(series, category);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value > 0.0) {
                            total = total + value;
                        }
                    }
                }
                maximum = Math.max(maximum, total);

            }

            result = new Double(maximum);

        }

        return result;

    }

    /**
     * Creates an XYDataset by sampling the specified function over a fixed range.
     *
     * @param f  the function.
     * @param start  the start value for the range.
     * @param end  the end value for the range.
     * @param samples  the number of samples (must be > 1).
     * @param seriesName  the name to give the resulting series.
     *
     * @return  the XYDataset.
     */
    public static XYDataset sampleFunction2D(Function2D f,
                                             double start, double end, int samples,
                                             String seriesName) {

        if (start >= end) {
            throw new IllegalArgumentException("DatasetUtilities.createXYDataset(...): "
                + "start must be before end.");
        }

        if (samples < 2) {
            throw new IllegalArgumentException("DatasetUtilities.createXYDataset(...): "
                + "samples must be at least 2.");
        }

        XYSeries series = new XYSeries(seriesName);

        double step = (end - start) / samples;
        for (int i = 0; i <= samples; i++) {
            double x = start + (step * i);
            series.add(x, f.getValue(x));
        }

        XYSeriesCollection collection = new XYSeriesCollection(series);
        return collection;

    }

}
