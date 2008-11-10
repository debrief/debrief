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
 * ----------------
 * BarRenderer.java
 * ----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: BarRenderer.java,v 1.1.1.1 2003/07/17 10:06:20 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 14-Mar-2002 : Version 1 (DG);
 * 23-May-2002 : Added tooltip generator to renderer (DG);
 * 29-May-2002 : Moved tooltip generator to abstract super-class (DG);
 * 25-Jun-2002 : Changed constructor to protected and removed redundant code (DG);
 * 26-Jun-2002 : Added axis to initialise method, and record upper and lower clip values (DG);
 * 24-Sep-2002 : Added getLegendItem(...) method (DG);
 * 09-Oct-2002 : Modified constructor to include URL generator (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.chart.urls.CategoryURLGenerator;
import com.jrefinery.data.CategoryDataset;

/**
 * A base class for category item renderers that draw bars.
 *
 * @author DG
 */
public abstract class BarRenderer extends AbstractCategoryItemRenderer {

    /** Constant that controls the minimum width before a bar has an outline drawn. */
    protected static final double BAR_OUTLINE_WIDTH_THRESHOLD = 3.0;

    /** The total width of the categories. */
    protected double categorySpan;

    /** The total width of the category gaps. */
    protected double categoryGapSpan;

    /** The total width of the items within a category. */
    protected double itemSpan;

    /** The total width of the item gaps. */
    protected double itemGapSpan;

    /** The width of a single item. */
    protected double itemWidth;

    /** The data value ZERO translated to Java2D user space. */
    protected double zeroInJava2D;

    /** The upper clip (axis) value. */
    protected double upperClip;

    /** The lower clip (axis) value. */
    protected double lowerClip;

    /**
     * Constructs a bar renderer.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    protected BarRenderer(CategoryToolTipGenerator toolTipGenerator,
                          CategoryURLGenerator urlGenerator) {

        super(toolTipGenerator, urlGenerator);

    }

    /**
     * Initialises the renderer.
     * <p>
     * This method gets called once at the start of the process of drawing a
     * chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be plotted.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param data  the data.
     * @param info  collects chart rendering information for return to caller.
     *
     */
    public void initialise(Graphics2D g2,
                           Rectangle2D dataArea,
                           CategoryPlot plot,
                           ValueAxis axis,
                           CategoryDataset data,
                           ChartRenderingInfo info) {

        super.initialise(g2, dataArea, plot, axis, data, info);
        this.lowerClip = axis.getRange().getLowerBound();
        this.upperClip = axis.getRange().getUpperBound();

    }

    /**
     * Returns the number of bar widths per category, which depends on whether
     * or not the renderer stacks bars.
     *
     * @param data  the dataset.
     *
     * @return the number of bar widths per category.
     */
    public abstract int barWidthsPerCategory(CategoryDataset data);

    /**
     * Returns true if there are gaps between items within a category.  Again,
     * this depends on whether or not the bars are stacked.
     *
     * @return <code>true</code> if there are gaps between items within a category.
     */
    public abstract boolean hasItemGaps();

    /**
     * Returns a flag indicating whether or not the renderer stacks values
     * within each category. This has an effect on the minimum and maximum
     * values required for the axis to show all the data values.
     * <P>
     * Subclasses should override this method as necessary.
     *
     * @return always <code>false</code>.
     */
    public boolean isStacked() {
        return false;
    }

    /**
     * Calculates some dimensions required for plotting the bars.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param span  the available width.
     */
    protected void calculateCategoryAndItemSpans(Graphics2D g2,
                                                 Rectangle2D dataArea,
                                                 CategoryPlot plot,
                                                 CategoryDataset data,
                                                 double span) {

        // work out the span dimensions for the categories...
        int seriesCount = data.getSeriesCount();
        int categoryCount = data.getCategoryCount();

        categorySpan = 0.0;
        categoryGapSpan = 0.0;
        if (categoryCount > 1) {
            double used = (1 - plot.getIntroGapPercent()
                             - plot.getTrailGapPercent()
                             - plot.getCategoryGapsPercent());
            categorySpan = span * used;
            categoryGapSpan = span * plot.getCategoryGapsPercent();
        }
        else {
            double used = (1 - plot.getIntroGapPercent() - plot.getTrailGapPercent());
            categorySpan = span * used;
        }

        // work out the item span...
        itemSpan = categorySpan;
        itemGapSpan = 0.0;
        if (seriesCount > 1) {
            if (hasItemGaps()) {
                itemGapSpan = span * plot.getItemGapsPercent();
                itemSpan = itemSpan - itemGapSpan;
            }
        }
        itemWidth = itemSpan / (categoryCount * barWidthsPerCategory(data));

        zeroInJava2D = plot.getRangeAxis().translateValueToJava2D(0.0, dataArea);

    }

}
