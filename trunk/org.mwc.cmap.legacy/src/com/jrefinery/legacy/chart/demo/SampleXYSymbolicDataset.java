/* ===============
 * JFreeChart Demo
 * ===============
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
 * ----------------------------
 * SampleXYSymbolicDataset.java
 * ----------------------------
 * (C) Copyright 2000-2002, by Anthony Boulestreau and Contributors;
 *
 * Original Author:  Anthony Boulestreau.
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * Changes
 * -------
 * 29-Mar-2002 : Version 1 (AB);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart.demo;

import com.jrefinery.legacy.data.AbstractSeriesDataset;
import com.jrefinery.legacy.data.XYDataset;
import com.jrefinery.legacy.data.XisSymbolic;
import com.jrefinery.legacy.data.YisSymbolic;

import java.util.List;
import java.util.Vector;
import java.lang.reflect.Array;

/**
 * Random data for a symbolic plot demo.
 *
 * @author AB
 */
public class SampleXYSymbolicDataset extends AbstractSeriesDataset
                                     implements XYDataset, XisSymbolic, YisSymbolic {

    /** Series names. */
    private String[] seriesName;

    /** Items. */
    private int[] item;

    /** A series index. */
    private int serie;

    /** X values. */
    private Integer[][] xValues;

    /** Y values. */
    private Integer[][] yValues;

    /** X symbolic values. */
    private String[] xSymbolicValues;

    /** Y symbolic values. */
    private String[] ySymbolicValues;

    /** The dataset name. */
    private String datasetName;

    /**
     * Creates a new dataset.
     *
     * @param datasetName  the dataset name.
     * @param xValues  the x values.
     * @param yValues  the y values.
     * @param xSymbolicValues  the x symbols.
     * @param ySymbolicValues  the y symbols.
     * @param seriesName  the series name.
     */
    public SampleXYSymbolicDataset(String datasetName,
                                   Integer[][] xValues,
                                   Integer[][] yValues,
                                   String[] xSymbolicValues,
                                   String[] ySymbolicValues,
                                   String[] seriesName) {

        this.datasetName = datasetName;
        this.xValues = xValues;
        this.yValues = yValues;
        this.xSymbolicValues = xSymbolicValues;
        this.ySymbolicValues = ySymbolicValues;
        this.serie = xValues.length;
        this.item = new int[serie];
        for (int i = 0; i < serie; i++) {
            this.item[i] = xValues[i].length;
        }
        this.seriesName = seriesName;

    }

    /**
     * Returns the x-value for the specified series and item.  Series are
     * numbered 0, 1, ...
     *
     * @param series  the index (zero-based) of the series.
     * @param item1  the index (zero-based) of the required item.
     *
     * @return the x-value for the specified series and item.
     */
    public Number getXValue(int series, int item1) {
        return xValues[series][item1];
    }

    /**
     * Returns the y-value for the specified series and item.  Series are
     * numbered 0, 1, ...
     *
     * @param series  the index (zero-based) of the series.
     * @param item1  the index (zero-based) of the required item.
     *
     * @return the y-value for the specified series and item.
     */
    public Number getYValue(int series, int item1) {
        return yValues[series][item1];
    }

    /**
     * Sets the x-value for the specified series and item with the specified
     * new <CODE>Number</CODE> value.  Series are numbered 0, 1, ...
     * <P>
     * This method is used by combineXSymbolicDataset to modify the reference
     * to the symbolic value ...
     *
     * @param series  the index (zero-based) of the series.
     * @param item  the index (zero-based) of the required item.
     * @param newValue  the value to set.
     */
    public void setXValue(int series, int item, Number newValue) {
        xValues[series][item] = (Integer) newValue;
    }

    /**
     * Sets the y-value for the specified series and item with the specified
     * new <CODE>Number</CODE> value.  Series are numbered 0, 1, ...
     * <P>
     * This method is used by combineYSymbolicDataset to modify the reference
     * to the symbolic value ...
     *
     * @param series  the index (zero-based) of the series.
     * @param item  the index (zero-based) of the required item.
     * @param newValue  the value to set.
     */
    public void setYValue(int series, int item, Number newValue) {
        yValues[series][item] = (Integer) newValue;
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The number of series in the dataset.
     */
    public int getSeriesCount() {
        return serie;
    }

    /**
     * Returns the name of the series.
     *
     * @param series  the index (zero-based) of the series.
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
        if (seriesName != null) {
            return seriesName[series];
        }
        else {
            return datasetName + series;
        }
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the index (zero-based) of the series.
     * @return the number of items in the specified series.
     */
    public int getItemCount(int series) {
        return item[series];
    }

    /**
     * Returns the list of X symbolic values.
     *
     * @return array of symbolic value.
     */
    public String[] getXSymbolicValues() {
        return xSymbolicValues;
    }

    /**
     * Returns the list of Y symbolic values.
     *
     * @return array of symbolic value.
     */
    public String[] getYSymbolicValues() {
        return ySymbolicValues;
    }

    /**
     * Sets the list of X symbolic values.
     *
     * @param sValues the new list of symbolic value.
     */
    public void setXSymbolicValues(String[] sValues) {
        xSymbolicValues = sValues;
    }

    /**
     * Sets the list of Y symbolic values.
     *
     * @param sValues the new list of symbolic value.
     */
    public void setYSymbolicValues(String[] sValues) {
        ySymbolicValues = sValues;
    }

    /**
     * Returns the X symbolic value of the data set specified by
     * <CODE>series</CODE> and <CODE>item</CODE> parameters.
     *
     * @param series value of the serie.
     * @param item1 value of the item.
     *
     * @return the symbolic value.
     */
    public String getXSymbolicValue(int series, int item1) {
        Integer intValue = (Integer) getXValue(series, item1);
        return getXSymbolicValue(intValue);
    }

    /**
     * Returns the Y symbolic value of the data set specified by
     * <CODE>series</CODE> and <CODE>item</CODE> parameters.
     *
     * @param series value of the serie.
     * @param item1 value of the item.
     * @return the symbolic value.
     */
    public String getYSymbolicValue(int series, int item1) {
        Integer intValue = (Integer) getYValue(series, item1);
        return getYSymbolicValue(intValue);
    }

    /**
     * Returns the X symbolic value linked with the specified
     * <CODE>Integer</CODE>.
     *
     * @param val value of the integer linked with the symbolic value.
     * @return the symbolic value.
     */
    public String getXSymbolicValue(Integer val) {
        return xSymbolicValues[val.intValue()];
    }

    /**
     * Returns the Y symbolic value linked with the specified
     * <CODE>Integer</CODE>.
     *
     * @param val value of the integer linked with the symbolic value.
     * @return the symbolic value.
     */
    public String getYSymbolicValue(Integer val) {
        return ySymbolicValues[val.intValue()];
    }

    /**
     * This function modify <CODE>dataset1</CODE> and <CODE>dataset1</CODE> in
     * order that they share the same Y symbolic value list.
     * <P>
     * The sharing Y symbolic value list is obtained adding the Y symbolic data
     * list of the fist data set to the Y symbolic data list of the second data
     * set.
     * <P>
     * This function is use with the <I>combined plot</I> functions of
     * JFreeChart.
     *
     * @param dataset1  the first data set to combine.
     * @param dataset2  the second data set to combine.
     *
     * @throws ClassCastException if <CODE>dataset1</CODE> and
     *         <CODE>dataset2</CODE> is not an instance of SampleYSymbolicDataset.
     *
     * @return  the shared Y symbolic array.
     */
    @SuppressWarnings("unchecked")
		public static String[] combineYSymbolicDataset(YisSymbolic dataset1, YisSymbolic dataset2) {

        SampleXYSymbolicDataset sDataset1 = (SampleXYSymbolicDataset) dataset1;
        SampleXYSymbolicDataset sDataset2 = (SampleXYSymbolicDataset) dataset2;
        String[] sDatasetSymbolicValues1 = sDataset1.getYSymbolicValues();
        String[] sDatasetSymbolicValues2 = sDataset2.getYSymbolicValues();

        //Combine the two list of symbolic value of the two data set
        int s1length = sDatasetSymbolicValues1.length;
        int s2length = sDatasetSymbolicValues2.length;
        List ySymbolicValuesCombined = new Vector();
        for (int i = 0; i < s1length; i++) {
            ySymbolicValuesCombined.add(sDatasetSymbolicValues1[i]);
        }
        for (int i = 0; i < s2length; i++) {
            if (!ySymbolicValuesCombined.contains(sDatasetSymbolicValues2[i])) {
                ySymbolicValuesCombined.add(sDatasetSymbolicValues2[i]);
            }
        }

        //Change the Integer reference of the second data set
        int newIndex;
        for (int i = 0; i < sDataset2.getSeriesCount(); i++) {
            for (int j = 0; j < sDataset2.getItemCount(i); j++) {
                newIndex = ySymbolicValuesCombined.indexOf(sDataset2.getYSymbolicValue(i, j));
                sDataset2.setYValue(i, j, new Integer(newIndex));
            }
        }

        //Set the new list of symbolic value on the two data sets
        String[] ySymbolicValuesCombinedA = new String[ySymbolicValuesCombined.size()];
        ySymbolicValuesCombined.toArray(ySymbolicValuesCombinedA);
        sDataset1.setYSymbolicValues(ySymbolicValuesCombinedA);
        sDataset2.setYSymbolicValues(ySymbolicValuesCombinedA);

        return ySymbolicValuesCombinedA;
    }

    /**
     * This function modify <CODE>dataset1</CODE> and <CODE>dataset1</CODE> in
     * order that they share the same X symbolic value list.
     * <P>
     * The sharing X symbolic value list is obtained adding the X symbolic data
     * list of the fist data set to the X symbolic data list of the second data
     * set.
     * <P>
     * This function is use with the <I>combined plot</I> functions of
     * JFreeChart.
     *
     * @param dataset1 the first data set to combine.
     * @param dataset2 the second data set to combine.
     * @throws ClassCastException if <CODE>dataset1</CODE> and
     *      <CODE>dataset2</CODE> is not an instance of SampleYSymbolicDataset.
     *
     * @return  the shared X symbolic array.
     */
    @SuppressWarnings("unchecked")
		public static String[] combineXSymbolicDataset(XisSymbolic dataset1, XisSymbolic dataset2) {
        SampleXYSymbolicDataset sDataset1 = (SampleXYSymbolicDataset) dataset1;
        SampleXYSymbolicDataset sDataset2 = (SampleXYSymbolicDataset) dataset2;
        String[] sDatasetSymbolicValues1 = sDataset1.getXSymbolicValues();
        String[] sDatasetSymbolicValues2 = sDataset2.getXSymbolicValues();

        //Combine the two list of symbolic value of the two data set
        int s1length = sDatasetSymbolicValues1.length;
        int s2length = sDatasetSymbolicValues2.length;
        List xSymbolicValuesCombined = new Vector();
        for (int i = 0; i < s1length; i++) {
            xSymbolicValuesCombined.add(sDatasetSymbolicValues1[i]);
        }
        for (int i = 0; i < s2length; i++) {
            if (!xSymbolicValuesCombined.contains(sDatasetSymbolicValues2[i])) {
                xSymbolicValuesCombined.add(sDatasetSymbolicValues2[i]);
            }
        }

        //Change the Integer reference of the second data set
        int newIndex;
        for (int i = 0; i < sDataset2.getSeriesCount(); i++) {
            for (int j = 0; j < sDataset2.getItemCount(i); j++) {
                newIndex = xSymbolicValuesCombined.indexOf(sDataset2.getXSymbolicValue(i, j));
                sDataset2.setXValue(i, j, new Integer(newIndex));
            }
        }

        //Set the new list of symbolic value on the two data sets
        String[] xSymbolicValuesCombinedA = new String[xSymbolicValuesCombined.size()];
        xSymbolicValuesCombined.toArray(xSymbolicValuesCombinedA);
        sDataset1.setXSymbolicValues(xSymbolicValuesCombinedA);
        sDataset2.setXSymbolicValues(xSymbolicValuesCombinedA);

        return xSymbolicValuesCombinedA;
    }

    /**
     * Clone the SampleXYSymbolicDataset object
     *
     * @return the cloned object.
     */
    public Object clone() {
        String nDatasetName = new String(this.datasetName);
        Integer[][] nXValues = (Integer[][]) cloneArray(this.xValues);
        Integer[][] nYValues = (Integer[][]) cloneArray(this.yValues);
        String[] nXSymbolicValues = (String[]) cloneArray(this.xSymbolicValues);
        String[] nYSymbolicValues = (String[]) cloneArray(this.ySymbolicValues);
        String[] seriesName1 = (String[]) cloneArray(this.seriesName);
        return new SampleXYSymbolicDataset(nDatasetName, nXValues, nYValues,
                                           nXSymbolicValues, nYSymbolicValues, seriesName1);
    }

    /**
     * Returns a clone of the array.
     *
     * @param arr the array.
     *
     * @return a clone.
     */
    @SuppressWarnings("unchecked")
		private static Object cloneArray(Object arr) {

        if (arr == null) {
            return arr;
        }

        Class cls = arr.getClass();
        if (!cls.isArray()) {
            return arr;
        }

        int length = Array.getLength(arr);
        Object[] newarr = (Object[]) Array.newInstance(cls.getComponentType(), length);

        Object obj;

        for (int i = 0; i < length; i++) {
            obj = Array.get(arr, i);
            if (obj.getClass().isArray()) {
                newarr[i] = cloneArray(obj);
            }
            else {
                newarr[i] = obj;
            }
        }

        return newarr;
    }

}
