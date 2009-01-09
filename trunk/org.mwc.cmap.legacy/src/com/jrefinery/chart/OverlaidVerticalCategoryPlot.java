/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * ---------------------------------
 * OverlaidVerticalCategoryPlot.java
 * ---------------------------------
 * (C) Copyright 2002, by Jeremy Bowman and Contributors.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: OverlaidVerticalCategoryPlot.java,v 1.1.1.1 2003/07/17 10:06:25 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 13-May-2002 : Version 1 (JB);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 18-Sep-2002 : Overided the setSeriesPaint, setSeriesStroke, setSeriesOutlinePaint,
 *               setSeriesOutlineStroke methods to ensure better functionality and to keep
 *               the legend colors consistent with the plot colors.
 * 20-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 27-Sep-2002 : Removed obsolete methods (AS)
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.util.List;
import java.util.Iterator;
import com.jrefinery.data.DefaultCategoryDataset;
import com.jrefinery.data.Range;

/**
 * An extension of VerticalCategoryPlot that allows multiple
 * VerticalCategoryPlots to be overlaid in one space, using common axes.
 *
 * @author Jeremy Bowman
 */
public class OverlaidVerticalCategoryPlot extends VerticalCategoryPlot {

    /** Storage for the subplot references. */
    private List<CategoryPlot> subplots;

    /** The total number of series. */
    private int seriesCount = 0;

    /**
     * Constructs a new overlaid vertical category plot.
     *
     * @param domainAxisLabel  the label for the domain axis.
     * @param rangeAxisLabel  the label for the range axis.
     * @param categories  the categories to be shown on the domain axis.
     */
    public OverlaidVerticalCategoryPlot(String domainAxisLabel, String rangeAxisLabel,
                                        Object[] categories) {

        this(new HorizontalCategoryAxis(domainAxisLabel),
             new VerticalNumberAxis(rangeAxisLabel),
             categories);

    }

    /**
     * Constructs a new overlaid vertical category plot.
     *
     * @param domain  horizontal axis to use for all sub-plots.
     * @param range  vertical axis to use for all sub-plots.
     * @param categories  the categories to be shown on the domain axis.
     */
    public OverlaidVerticalCategoryPlot(CategoryAxis domain, ValueAxis range,
                                        Object categories[]) {

        super(null, domain, range, null);
        // create an empty dataset to hold the category labels
        double[][] emptyArray = new double[1][categories.length];
        DefaultCategoryDataset empty = new DefaultCategoryDataset(emptyArray);
        empty.setCategories(categories);
        setDataset(empty);
        this.subplots = new java.util.ArrayList<CategoryPlot>();

    }

    /**
     * Adds a subplot.
     * <P>
     * This method sets the axes of the subplot to null.
     *
     * @param subplot  the subplot.
     */
    public void add(VerticalCategoryPlot subplot) {

        subplot.setParent(this);
        subplot.setDomainAxis(null);
        subplot.setRangeAxis(null);
        seriesCount = seriesCount + subplot.getSeriesCount();
        subplots.add(subplot);
        CategoryAxis domain = this.getDomainAxis();
        if (domain != null) {
            domain.configure();
        }
        ValueAxis range = this.getRangeAxis();
        if (range != null) {
            range.configure();
        }

    }

    /**
     * Returns an array of labels to be displayed by the legend.
     *
     * @return An array of legend item labels (or null).
     *
     * @deprecated use getLegendItems().
     */
    @SuppressWarnings("unchecked")
		public List getLegendItemLabels() {

        List result = new java.util.ArrayList();

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                VerticalCategoryPlot plot = (VerticalCategoryPlot) iterator.next();
                List more = plot.getLegendItemLabels();
                result.addAll(more);
            }
        }

        return result;

    }

    /***
     * Returns the legend items.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        if (subplots != null) {
            Iterator<CategoryPlot> iterator = subplots.iterator();
            while (iterator.hasNext()) {
                CategoryPlot plot = (CategoryPlot) iterator.next();
                LegendItemCollection more = plot.getLegendItems();
                result.addAll(more);
            }
        }

        return result;

    }
    /**
     * Performs the actual drawing of the  data.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param info  the chart rendering info.
     * @param backgroundPlotArea  ??
     */
    public void render(Graphics2D g2, Rectangle2D dataArea,
                       ChartRenderingInfo info, Shape backgroundPlotArea) {

        Iterator<CategoryPlot> iterator = subplots.iterator();
        while (iterator.hasNext()) {
            VerticalCategoryPlot subplot = (VerticalCategoryPlot) iterator.next();
            subplot.render(g2, dataArea, info, backgroundPlotArea);
        }
    }

    /**
     * Returns a string for the plot type.
     *
     * @return a string for the plot type.
     */
    public String getPlotType() {
        return "Overlaid Vertical Category Plot";
    }

    /**
     * Returns the number of series in the plot.
     *
     * @return the series count.
     */
    public int getSeriesCount() {

        int result = 0;

        Iterator<CategoryPlot> iterator = subplots.iterator();
        while (iterator.hasNext()) {
            VerticalCategoryPlot subplot = (VerticalCategoryPlot) iterator.next();
            result = result + subplot.getSeriesCount();
        }

        return result;

    }

    /**
     * Returns the range of data values that will be plotted against the range
     * axis.
     * <P>
     * If the dataset is null, this method returns null.
     *
     * @return the data range.
     */
    public Range getVerticalDataRange() {

        Range result = null;

        if (subplots != null) {
            Iterator<CategoryPlot> iterator = subplots.iterator();
            while (iterator.hasNext()) {
                VerticalCategoryPlot plot = (VerticalCategoryPlot) iterator.next();
                result = Range.combine(result, plot.getVerticalDataRange());
            }
        }

        return result;

    }

    /**
     * Returns the minimum value in the range (since this is plotted against
     * the vertical axis by VerticalBarPlot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return The minimum value.
     */
    public Number getMinimumVerticalDataValue() {

        Number result = null;

        if (subplots != null) {
            Iterator<CategoryPlot> iterator = subplots.iterator();
            while (iterator.hasNext()) {
                VerticalCategoryPlot plot = (VerticalCategoryPlot) iterator.next();
                Number subMin = plot.getMinimumVerticalDataValue();
                if (result == null) {
                    result = subMin;
                }
                else {
                    if (subMin != null) {
                        result = new Double(Math.min(result.doubleValue(), subMin.doubleValue()));
                    }
                }
            }
        }

        return result;

    }

    /**
     * Returns the maximum value in the range (since the range values are
     * plotted against the vertical axis by this plot).
     * <P>
     * This method will return null if the dataset is null.
     *
     * @return The maximum value.
     */
    public Number getMaximumVerticalDataValue() {

        Number result = null;

        if (subplots != null) {
            Iterator<CategoryPlot> iterator = subplots.iterator();
            while (iterator.hasNext()) {
                VerticalCategoryPlot plot = (VerticalCategoryPlot) iterator.next();
                Number subMax = plot.getMaximumVerticalDataValue();
                if (result == null) {
                    result = subMax;
                }
                else {
                    if (subMax != null) {
                        result = new Double(Math.max(result.doubleValue(), subMax.doubleValue()));
                    }
                }
            }
        }

        return result;

    }
}
