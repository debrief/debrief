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
 * ---------------------------------
 * AbstractCategoryItemRenderer.java
 * ---------------------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: AbstractCategoryItemRenderer.java,v 1.1.1.1 2003/07/17 10:06:19 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 29-May-2002 : Version 1 (DG);
 * 06-Jun-2002 : Added accessor methods for the tool tip generator (DG);
 * 11-Jun-2002 : Made constructors protected (DG);
 * 26-Jun-2002 : Added axis to initialise method (DG);
 * 05-Aug-2002 : Added urlGenerator member variable plus accessors (RA);
 * 22-Aug-2002 : Added categoriesPaint attribute, based on code submitted by Janet Banks.
 *               This can be used when there is only one series, and you want each category
 *               item to have a different color (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import com.jrefinery.legacy.chart.tooltips.CategoryToolTipGenerator;
import com.jrefinery.legacy.chart.urls.CategoryURLGenerator;
import com.jrefinery.legacy.data.CategoryDataset;

/**
 * Abstract base class for category item renderers.
 *
 * @author DG
 */
public abstract class AbstractCategoryItemRenderer extends AbstractRenderer
                                                   implements CategoryItemRenderer {

    /** The plot. */
    private CategoryPlot plot;

    /** Paint objects for categories (null permitted). */
    private Paint[] categoriesPaint;

    /** The tooltip generator. */
    private CategoryToolTipGenerator toolTipGenerator;

    /** The URL generator. */
    private CategoryURLGenerator urlGenerator;

    /** An internal flag whether to use the categoriesPaint array. */
    private boolean useCategoriesPaint;

    /**
     * Creates a renderer with no tool tip generator and no URL generator.
     * <P>
     * The defaults (no tool tip or URL generators) have been chosen to minimise the processing
     * required to generate a default chart.  If you require tool tips or URLs, then you can
     * easily add the required generators.
     */
    protected AbstractCategoryItemRenderer() {
        this(null, null);
    }

    /**
     * Constructs a new renderer with the specified tooltip generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     */
    protected AbstractCategoryItemRenderer(CategoryToolTipGenerator toolTipGenerator) {
        this(toolTipGenerator, null);
    }

    /**
     * Constructs a new renderer with the specified URL generator.
     *
     * @param urlGenerator  the URL generator.
     */
    protected AbstractCategoryItemRenderer(CategoryURLGenerator urlGenerator) {
        this(null, urlGenerator);
    }

    /**
     * Constructs a new renderer with the specified tooltip generator.
     *
     * @param toolTipGenerator  the tool tip generator.
     * @param urlGenerator  the URL generator.
     */
    protected AbstractCategoryItemRenderer(CategoryToolTipGenerator toolTipGenerator,
                                           CategoryURLGenerator urlGenerator) {

        this.plot = null;
        this.categoriesPaint = null;
        this.toolTipGenerator = toolTipGenerator;
        this.urlGenerator = urlGenerator;

    }

    /**
     * Returns the plot.
     *
     * @return the plot.
     */
    public CategoryPlot getPlot() {
        return this.plot;
    }

    /**
     * Sets the plot.
     *
     * @param plot the plot.
     */
    public void setPlot(CategoryPlot plot) {
        this.plot = plot;
    }

    /**
     * Returns the flag that controls whether or not the renderer uses the category paint settings.
     *
     * @return the flag.
     */
    public boolean getUseCategoriesPaint() {
        return this.useCategoriesPaint;
    }

    /**
     * Returns the paint to use for the categories when there is just one series.
     * <P>
     * If this is null, the categories will all have the same color (that of the series).
     *
     * @return The paint for the categories.
     */
    public Paint[] getCategoriesPaint() {
        return this.categoriesPaint;
    }

    /**
     * Sets the paint to be used for categories under special circumstances.
     * <P>
     * This attribute is provided for the situation where there is just one series, and you want
     * each category item to be plotted using a different color (ordinarily, the series color is
     * used for all the items in the series).
     * <P>
     * May not be observed by all subclasses yet.
     *
     * @param paint The colors.
     */
    public void setCategoriesPaint(Paint[] paint) {

        Object oldValue = this.categoriesPaint;
        this.categoriesPaint = paint;
        firePropertyChanged("renderer.CategoriesPaint", oldValue, paint);

    }

    /**
     * Returns the paint for a specific category (possibly null).
     *
     * @param index The category index.
     *
     * @return The paint for the category.
     */
    public Paint getCategoryPaint(int index) {

        Paint result = null;
        if (this.categoriesPaint != null) {
            result = this.categoriesPaint[index % categoriesPaint.length];
        }
        return result;
    }

    /**
     * Returns the tool tip generator.
     *
     * @return The tool tip generator.
     */
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator.
     *
     * @param generator  the tool tip generator.
     */
    public void setToolTipGenerator(CategoryToolTipGenerator generator) {

        Object oldValue = this.toolTipGenerator;
        this.toolTipGenerator = generator;
        firePropertyChanged("renderer.ToolTipGenerator", oldValue, generator);

    }

    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return The URL generator.
     */
    public CategoryURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator for HTML image maps.
     *
     * @param urlGenerator The URL generator.
     */
    public void setURLGenerator(CategoryURLGenerator urlGenerator) {

        Object oldValue = this.urlGenerator;
        this.urlGenerator = urlGenerator;
        firePropertyChanged("renderer.URLGenerator", oldValue, urlGenerator);

    }

    /**
     * Initialises the renderer.
     * <P>
     * Stores a reference to the ChartRenderingInfo object (which might be null), and then
     * sets the useCategoriesPaint flag according to the special case conditions a) there is
     * only one series and b) the categoriesPaint array is not null.
     *
     * @param g2  The graphics device.
     * @param dataArea  The data area.
     * @param plot1  The plot.
     * @param axis  The axis.
     * @param data  The data.
     * @param info  An object for returning information about the structure of the chart.
     *
     */
    public void initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot1,
                           ValueAxis axis, CategoryDataset data, ChartRenderingInfo info) {

        this.plot = plot1;
        setInfo(info);

        // the renderer can use different colors for the categories if there is one series and
        // the categoriesPaint array has been populated...
        if ((data.getSeriesCount() == 1) && (this.categoriesPaint != null)) {
            this.useCategoriesPaint = this.categoriesPaint.length > 0;
        }

    }

    /**
     * Returns a flag indicating whether the items within one category are
     * stacked up when represented by the renderer.
     *
     * @return The flag.
     */
    public boolean isStacked() {
        return false;
    }

    /**
     * Returns the area that the axes (and data) must fit into.
     * <P>
     * Often this is the same as the plotArea, but sometimes a smaller region
     * should be used (for example, the 3D charts require the axes to use less
     * space in order to leave room for the 'depth' part of the chart).
     *
     * @param plotArea The plot area.
     *
     * @return the area that the axes (and date) must fit into.
     */
    public Rectangle2D getAxisArea(Rectangle2D plotArea) {
        return plotArea;
    }

    /**
     * Returns the clip region... usually returns the dataArea, but some charts
     * (e.g. 3D) have non rectangular clip regions.
     *
     * @param dataArea The data area.
     *
     * @return  the clip region.
     */
    public Shape getDataClipRegion(Rectangle2D dataArea) {
        return dataArea;
    }

    /**
     * Draws the background for the plot.
     * <P>
     * For most charts, the axisDataArea and the dataClipArea are the same.
     *
     * @param g2  The graphics device.
     * @param plot1  The plot.
     * @param axisDataArea  The area inside the axes.
     * @param dataClipArea  The data clip area.
     */
    public void drawPlotBackground(Graphics2D g2, CategoryPlot plot1,
                                   Rectangle2D axisDataArea, Shape dataClipArea) {

        if (plot1.getBackgroundPaint() != null) {
            g2.setPaint(plot1.getBackgroundPaint());
            g2.fill(dataClipArea);
        }

        if ((plot1.getOutlineStroke() != null) && (plot1.getOutlinePaint() != null)) {
            g2.setStroke(plot1.getOutlineStroke());
            g2.setPaint(plot1.getOutlinePaint());
            g2.draw(dataClipArea);
        }

    }

    /**
     * Returns a legend item for a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return the legend item.
     */
    public LegendItem getLegendItem(int series) {

        if (this.plot == null) {
            return null;
        }

        CategoryDataset dataset = this.plot.getCategoryDataset();
        String label = dataset.getSeriesName(series);
        String description = label;
        Shape shape = null;
        Paint paint = plot.getSeriesPaint(series);
        Paint outlinePaint = plot.getSeriesOutlinePaint(series);
        Stroke stroke = plot.getSeriesStroke(series);
        Stroke outlineStroke = plot.getSeriesOutlineStroke(series);

        return new LegendItem(label, description,
                              shape, paint, outlinePaint, stroke, outlineStroke);

    }

}
