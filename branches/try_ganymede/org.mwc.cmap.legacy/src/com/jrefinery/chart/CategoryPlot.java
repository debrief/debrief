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
 * -----------------
 * CategoryPlot.java
 * -----------------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jeremy Bowman;
 *
 * $Id: CategoryPlot.java,v 1.1.1.1 2003/07/17 10:06:20 Ian.Mayo Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 15-Oct-2001 : Data source classes moved to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Oct-2001 : Changed intro and trail gaps on bar plots to use percentage of available space
 *               rather than a fixed number of units (DG);
 * 12-Dec-2001 : Changed constructors to protected (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 16-Jan-2002 : Increased maximum intro and trail gap percents, plus added some argument checking
 *               code.  Thanks to Taoufik Romdhane for suggesting this (DG);
 * 05-Feb-2002 : Added accessor methods for the tooltip generator, incorporated alpha-transparency
 *               for Plot and subclasses (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 * 14-Mar-2002 : Renamed BarPlot.java --> CategoryPlot.java, and changed code to use the
 *               CategoryItemRenderer interface (DG);
 * 22-Mar-2002 : Dropped the getCategories() method (DG);
 * 23-Apr-2002 : Moved the dataset from the JFreeChart class to the Plot class (DG);
 * 29-Apr-2002 : New methods to support printing values at the end of bars, contributed by
 *               Jeremy Bowman (DG);
 * 11-May-2002 : New methods for label visibility and overlaid plot support, contributed by
 *               Jeremy Bowman (DG);
 * 06-Jun-2002 : Removed the tooltip generator, this is now stored with the renderer.  Moved
 *               constants into the CategoryPlotConstants interface.  Updated Javadoc
 *               comments (DG);
 * 10-Jun-2002 : Overridden datasetChanged(...) method to update the upper and lower bound on the
 *               range axis (if necessary), updated Javadocs (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 20-Aug-2002 : Changed the constructor for Marker (DG);
 * 28-Aug-2002 : Added listener notification to setDomainAxis(...) and setRangeAxis(...) (DG);
 * 23-Sep-2002 : Added getLegendItems() method and fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Insets;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.List;
import com.jrefinery.data.SeriesDataset;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.data.DatasetChangeEvent;
import com.jrefinery.chart.event.PlotChangeEvent;


/**
 * A general plotting class that uses data from a CategoryDataset, and uses a
 * plug-in renderer to draw individual data items.
 *
 * @see Plot
 * @see CategoryItemRenderer
 *
 */
public abstract class CategoryPlot extends Plot implements CategoryPlotConstants {

    /** The parent plot (or null if this is not a subplot). */
    protected CategoryPlot parent;

    /** The domain axis. */
    protected CategoryAxis domainAxis;

    /** The range axis. */
    protected ValueAxis rangeAxis;

    /** The renderer for the data items. */
    protected CategoryItemRenderer renderer;

    /** A list of markers (optional) for the range axis. */
    protected List<Marker> rangeMarkers;

    /** The gap before the first item in the plot. */
    protected double introGapPercent;

    /** The gap after the last item in the plot. */
    protected double trailGapPercent;

    /**
     * The percentage of the overall drawing space allocated to providing gaps
     * between the last item in one category and the first item in the next category.
     */
    protected double categoryGapsPercent;

    /** The gap between items within the same category. */
    protected double itemGapsPercent;

    /** A flag indicating whether or not value labels are shown. */
    protected boolean valueLabelsVisible;

    /** The value label font. */
    protected Font labelFont;

    /** The value label paint. */
    protected Paint labelPaint;

    /** The value label format pattern String. */
    protected String labelFormatPattern;

    /** The value label format. */
    protected NumberFormat labelFormatter;

    /** A flag indicating whether or not value labels are drawn vertically. */
    protected boolean verticalLabels;

    /**
     * Constructs a category plot, using default values where necessary.
     *
     * @param data  The dataset.
     * @param domainAxis  The domain axis.
     * @param rangeAxis  The range axis.
     * @param renderer  The item renderer.
     *
     */
    protected CategoryPlot(CategoryDataset data,
                           CategoryAxis domainAxis,
                           ValueAxis rangeAxis,
                           CategoryItemRenderer renderer) {

        this(data, domainAxis, rangeAxis, renderer,
             Plot.DEFAULT_INSETS,
             Plot.DEFAULT_BACKGROUND_PAINT,
             null, // background image
             Plot.DEFAULT_BACKGROUND_ALPHA,
             Plot.DEFAULT_OUTLINE_STROKE,
             Plot.DEFAULT_OUTLINE_PAINT,
             Plot.DEFAULT_FOREGROUND_ALPHA,
             DEFAULT_INTRO_GAP_PERCENT,
             DEFAULT_TRAIL_GAP_PERCENT,
             DEFAULT_CATEGORY_GAPS_PERCENT,
             DEFAULT_ITEM_GAPS_PERCENT);

    }

    /**
     * Constructs a category plot.
     *
     * @param data  The dataset.
     * @param domainAxis  The domain axis.
     * @param rangeAxis  The range axis.
     * @param renderer  The item renderer.
     * @param insets  The insets for the plot.
     * @param backgroundPaint  An optional color for the plot's background.
     * @param backgroundImage  An optional image for the plot's background.
     * @param backgroundAlpha  Alpha-transparency for the plot's background.
     * @param outlineStroke  The stroke used to draw the plot outline.
     * @param outlinePaint  The paint used to draw the plot outline.
     * @param foregroundAlpha  The alpha transparency.
     * @param introGapPercent  The gap before the first item in the plot, as a
     *                         percentage of the available drawing space.
     * @param trailGapPercent  The gap after the last item in the plot, as a
     *                         percentage of the available drawing space.
     * @param categoryGapsPercent  The percentage of drawing space allocated
     *                             to the gap between the last item in one category
     *                             and the first item in the next category.
     * @param itemGapsPercent  The gap between items within the same category.
     *
     */
    protected CategoryPlot(CategoryDataset data,
                           CategoryAxis domainAxis,
                           ValueAxis rangeAxis,
                           CategoryItemRenderer renderer,
                           Insets insets,
                           Paint backgroundPaint,
                           Image backgroundImage,
                           float backgroundAlpha,
                           Stroke outlineStroke,
                           Paint outlinePaint,
                           float foregroundAlpha,
                           double introGapPercent,
                           double trailGapPercent,
                           double categoryGapsPercent,
                           double itemGapsPercent) {

        super(data,
              insets,
              backgroundPaint, backgroundImage, backgroundAlpha,
              outlineStroke, outlinePaint,
              foregroundAlpha
              );

        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }

        this.domainAxis = domainAxis;
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }

        this.rangeAxis = rangeAxis;
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }

        this.insets = insets;
        this.introGapPercent = introGapPercent;
        this.trailGapPercent = trailGapPercent;
        this.categoryGapsPercent = categoryGapsPercent;
        this.itemGapsPercent = itemGapsPercent;
        this.valueLabelsVisible = false;
        this.labelFont = DEFAULT_LABEL_FONT;
        this.labelPaint = Color.black;
        this.labelFormatter = java.text.NumberFormat.getInstance();
        this.labelFormatPattern = null;
        this.verticalLabels = false;

        this.rangeMarkers = null;

        Marker baseline = new Marker(0.0,
                                     new Color(0.8f, 0.8f, 0.8f, 0.5f),
                                     new java.awt.BasicStroke(1.0f),
                                     new Color(0.85f, 0.85f, 0.95f, 0.5f), 0.6f);
        addRangeMarker(baseline);

    }

    /**
     * Returns true if this plot is part of a combined plot structure.
     *
     * @return <code>true</code> if this plot is part of a combined plot structure.
     */
    public boolean isSubplot() {
        return (this.parent != null);
    }

    /**
     * Returns the parent plot, or null if this is not a sub-plot.
     *
     * @return The parent plot.
     */
    public CategoryPlot getParent() {
        return this.parent;
    }

    /**
     * Sets the parent plot.
     * <P>
     * Only used if this plot is a sub-plot within a combination plot.
     *
     * @param parent The parent plot.
     */
    public void setParent(CategoryPlot parent) {
        this.parent = parent;
    }

    /**
     * Returns the number of series in this plot.
     * <P>
     * This gets used when the plot is part of a combined chart... there may be
     * a better mechanism in the future.
     *
     * @return The series count.
     */
    public int getSeriesCount() {

        int result = 0;

        SeriesDataset data = this.getCategoryDataset();
        if (data != null) {
            result = data.getSeriesCount();
        }

        return result;

    }

    /**
     * Returns a list of labels for the legend.
     *
     * @return The list of labels.
     *
     * @deprecated use getLegendItems().
     */
    @SuppressWarnings("unchecked")
		public List getLegendItemLabels() {

        List<String> result = new java.util.ArrayList<String>();

        SeriesDataset data = getCategoryDataset();
        if (data != null) {
            int seriesCount = data.getSeriesCount();
            for (int i = 0; i < seriesCount; i++) {
                result.add(data.getSeriesName(i));
            }
        }

        return result;

    }

    /**
     * Returns the legend items for the plot.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        SeriesDataset data = getCategoryDataset();
        if (data != null) {
            int seriesCount = data.getSeriesCount();
            for (int i = 0; i < seriesCount; i++) {
                LegendItem item = this.renderer.getLegendItem(i);
                result.add(item);
            }
        }

        return result;

    }

    /**
     * Returns a reference to the renderer for the plot.
     *
     * @return The renderer.
     */
    public CategoryItemRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Sets the renderer for the plot.
     * <p>
     * If you set the renderer to null, no data will be plotted on the chart.
     *
     * @param renderer      The renderer (null permitted).
     */
    public void setRenderer(CategoryItemRenderer renderer) {

        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }
        this.notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the gap before the first bar on the chart, as a percentage of
     * the available drawing space (0.05 = 5 percent).
     *
     * @return The "intro gap" (percentage).
     */
    public double getIntroGapPercent() {
        return introGapPercent;
    }

    /**
     * Sets the gap before the first bar on the chart, and notifies registered
     * listeners that the plot has been modified.
     *
     * @param percent  The new gap value, expressed as a percentage of the
     *                 width of the plot area (0.05 = 5 percent).
     */
    public void setIntroGapPercent(double percent) {

        // check argument...
        if ((percent < 0.0) || (percent > MAX_INTRO_GAP_PERCENT)) {
            throw new IllegalArgumentException(
                "CategoryPlot.setIntroGapPercent(double): argument outside valid range.");
        }

        // make the change...
        if (this.introGapPercent != percent) {
            this.introGapPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Returns the gap following the last bar on the chart, as a percentage of
     * the available drawing space.
     *
     * @return The "trail gap" (percentage);
     */
    public double getTrailGapPercent() {
        return trailGapPercent;
    }

    /**
     * Sets the gap after the last bar on the chart, and notifies registered
     * listeners that the plot has been modified.
     *
     * @param percent  The new gap value, expressed as a percentage of the
     *                 width of the plot area (0.05 = 5 percent).
     */
    public void setTrailGapPercent(double percent) {

        // check argument...
        if ((percent < 0.0) || (percent > MAX_TRAIL_GAP_PERCENT)) {
            throw new IllegalArgumentException(
                "CategoryPlot.setTrailGapPercent(double): argument outside valid range.");
        }

        // make the change...
        if (this.trailGapPercent != percent) {
            trailGapPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the percentage of the drawing space that is allocated to
     * providing gaps between the categories.
     *
     * @return The "category gaps" (percentage).
     */
    public double getCategoryGapsPercent() {
        return categoryGapsPercent;
    }

    /**
     * Sets the gap between the last bar in one category and the first bar in
     * the next category, and notifies registered listeners that the plot has
     * been modified.
     *
     * @param percent  The new gap value, expressed as a percentage of the
     *                 width of the plot area (0.05 = 5 percent).
     */
    public void setCategoryGapsPercent(double percent) {

        // check argument...
        if ((percent < 0.0) || (percent > MAX_CATEGORY_GAPS_PERCENT)) {
            throw new IllegalArgumentException(
                "CategoryPlot.setCategoryGapsPercent(double): argument outside valid range.");
        }

        // make the change...
        if (this.categoryGapsPercent != percent) {
            this.categoryGapsPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the percentage of the drawing space that is allocated to
     * providing gaps between the items in a category.
     *
     * @return The "item gaps" (percentage).
     */
    public double getItemGapsPercent() {
        return itemGapsPercent;
    }

    /**
     * Sets the gap between one bar and the next within the same category, and
     * notifies registered listeners that the plot has been modified.
     *
     * @param percent  The new gap value, expressed as a percentage of the width
     *                 of the plot area (0.05 = 5 percent).
     */
    public void setItemGapsPercent(double percent) {

        // check argument...
        if ((percent < 0.0) || (percent > MAX_ITEM_GAPS_PERCENT)) {
            throw new IllegalArgumentException(
                "CategoryPlot.setItemGapsPercent(double): argument outside valid range.");
        }

        // make the change...
        if (percent != this.itemGapsPercent) {
            this.itemGapsPercent = percent;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * A convenience method that returns the dataset for the plot, cast as a CategoryDataset.
     *
     * @return The dataset.
     */
    public CategoryDataset getCategoryDataset() {
        return (CategoryDataset) dataset;
    }

    /**
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is null, then the method will return the parent plot's domain axis (if
     * there is a parent plot).
     *
     * @return The domain axis.
     */
    public CategoryAxis getDomainAxis() {

        CategoryAxis result = domainAxis;

        if ((result == null) && (this.parent != null)) {
            result = parent.getDomainAxis();
        }

        return result;

    }

    /**
     * Sets the domain axis for the plot (this must be compatible with the
     * plot type or an exception is thrown).
     *
     * @param axis  The new axis.
     *
     * @throws AxisNotCompatibleException  if axis are not compatible.
     */
    public void setDomainAxis(CategoryAxis axis) throws AxisNotCompatibleException {

        if (isCompatibleDomainAxis(axis)) {

            if (axis != null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "CategoryPlot.setDomainAxis(...): plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.domainAxis != null) {
                this.domainAxis.removeChangeListener(this);
            }

            this.domainAxis = axis;
            notifyListeners(new PlotChangeEvent(this));

        }
        else {
            throw new AxisNotCompatibleException(
                "CategoryPlot.setDomainAxis(...): axis not compatible with plot.");
        }

    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if there
     * is a parent plot).
     *
     * @return  The range axis.
     */
    public ValueAxis getRangeAxis() {

        ValueAxis result = rangeAxis;

        if ((result == null) && (this.parent != null)) {
            result = parent.getRangeAxis();
        }

        return result;

    }

    /**
     * Sets the range axis for the plot.
     * <P>
     * An exception is thrown if the new axis and the plot are not mutually compatible.
     *
     * @param axis  The new axis.
     *
     * @throws AxisNotCompatibleException  if axis are not compatible.
     */
    public void setRangeAxis(ValueAxis axis)
        throws AxisNotCompatibleException {

        if (isCompatibleRangeAxis(axis)) {

            if (axis != null) {
                try {
                    axis.setPlot(this);
                }
                catch (PlotNotCompatibleException e) {
                    throw new AxisNotCompatibleException(
                        "CategoryPlot.setRangeAxis(...): "
                            + "plot not compatible with axis.");
                }
                axis.addChangeListener(this);
            }

            // plot is likely registered as a listener with the existing axis...
            if (this.rangeAxis != null) {
                this.rangeAxis.removeChangeListener(this);
            }

            this.rangeAxis = axis;
            notifyListeners(new PlotChangeEvent(this));

        }
        else {
            throw new AxisNotCompatibleException(
                "Plot.setRangeAxis(...): axis not compatible with plot.");
        }
    }

    /**
     * Checks the compatibility of a domain axis, returning true if the axis
     * is compatible with the plot, and false otherwise.
     *
     * @param axis  The proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public abstract boolean isCompatibleDomainAxis(CategoryAxis axis);

    /**
     * Checks the compatibility of a range axis, returning true if the axis is
     * compatible with the plot, and false otherwise.
     *
     * @param axis  The proposed axis.
     *
     * @return <code>true</code> if the axis is compatible with the plot.
     */
    public abstract boolean isCompatibleRangeAxis(ValueAxis axis);

    /**
     * Returns the x or y coordinate (depending on the orientation of the plot)
     * in Java 2D User Space of the center of the specified category.
     *
     * @param category  The category (zero-based index).
     * @param area  The region within which the plot will be drawn.
     *
     * @return the center of the specified category.
     */
    public abstract double getCategoryCoordinate(int category, Rectangle2D area);

    /**
     * Zooms (in or out) on the plot's value axis.
     * <p>
     * If the value 0.0 is passed in as the zoom percent, the auto-range
     * calculation for the axis is restored (which sets the range to include
     * the minimum and maximum data values, thus displaying all the data).
     *
     * @param percent  The zoom amount.
     */
    public void zoom(double percent) {

        ValueAxis rangeAxis11 = this.getRangeAxis();
        if (percent > 0.0) {
            double range = rangeAxis11.getMaximumAxisValue() - rangeAxis11.getMinimumAxisValue();
            double scaledRange = range * percent;
            rangeAxis11.setAnchoredRange(scaledRange);
        }
        else {
            rangeAxis11.setAutoRange(true);
        }

    }

    /**
     * Returns a flag that indicates whether or not the value labels are showing.
     *
     * @return the flag.
     *
     * @deprecated use getValueLabelsVisible().
     */
    public boolean getLabelsVisible() {
        return getValueLabelsVisible();
    }

    /**
     * Returns a flag that indicates whether or not the value labels are showing.
     *
     * @return the flag.
     */
    public boolean getValueLabelsVisible() {
        return this.valueLabelsVisible;
    }

    /**
     * Sets the flag that indicates whether or not the value labels are showing.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param flag  the flag.
     *
     * @deprecated use setValueLabelsVisible(boolean).
     */
    public void setLabelsVisible(boolean flag) {
        setValueLabelsVisible(flag);
    }

    /**
     * Sets the flag that indicates whether or not the value labels are showing.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     * <P>
     * Not all renderers support this yet.
     *
     * @param flag  the flag.
     */
    public void setValueLabelsVisible(boolean flag) {
        if (this.valueLabelsVisible != flag) {
            this.valueLabelsVisible = flag;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Sets the value label font.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param font  The new value label font.
     */
    public void setLabelFont(Font font) {

        // check arguments...
        if (labelFont == null) {
            throw new IllegalArgumentException(
                "CategoryPlot.setLabelFont(...): null font not allowed.");
        }

        // make the change...
        if (!this.labelFont.equals(font)) {
            this.labelFont = font;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the value label font.
     *
     * @return The value label font.
     */
    public Font getLabelFont() {
        return labelFont;
    }

    /**
     * Sets the value label paint.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param paint  The new value label paint.
     */
    public void setLabelPaint(Paint paint) {

        // check arguments...
        if (labelPaint == null) {
            throw new IllegalArgumentException(
                "CategoryPlot.setLabelPaint(...): null paint not allowed.");
        }

        // make the change...
        if (!this.labelPaint.equals(paint)) {
            this.labelPaint = paint;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the value label paint.
     *
     * @return The value label paint
     */
    public Paint getLabelPaint() {
        return labelPaint;
    }

    /**
     * Sets the format string for the value labels.
     * <P>
     * Notifies registered listeners that the plot has been changed.
     *
     * @param format  The new value label format pattern. Use <code>null</code> if labels are
     *                not to be shown.
     */
    public void setLabelFormatString(String format) {

        boolean changed = false;

        if (format == null) {
             if (labelFormatter != null) {
                 this.labelFormatPattern = null;
                 this.labelFormatter = null;
                 changed = true;
             }
        }
        else if (labelFormatter == null || !format.equals(labelFormatPattern)) {
            this.labelFormatPattern = format;
            this.labelFormatter = new DecimalFormat(format);
            changed = true;
        }

        if (changed) {
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the value label formatter.
     *
     * @return The value label formatter
     */
    public NumberFormat getLabelFormatter() {
        return labelFormatter;
    }

    /**
     * Returns true if the value labels should be rotated to vertical, and
     * false for standard horizontal labels.
     *
     * @return A flag indicating the orientation of the value labels.
     */
    public boolean getVerticalLabels() {
        return this.verticalLabels;
    }

    /**
     * Sets the flag that determines the orientation of the value labels.
     * Registered listeners are notified that the plot has been changed.
     *
     * @param flag  The flag.
     */
    public void setVerticalLabels(boolean flag) {
        if (this.verticalLabels != flag) {
            this.verticalLabels = flag;
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The chart reacts by passing on a chart change event to all registered
     * listeners.
     *
     * @param event  Information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {

        if (this.rangeAxis != null) {
            this.rangeAxis.configure();
        }
        super.datasetChanged(event);

    }

    /**
     * Adds a marker for the range axis.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker The marker.
     */
    public void addRangeMarker(Marker marker) {

        if (this.rangeMarkers == null) {
            this.rangeMarkers = new java.util.ArrayList<Marker>();
        }
        this.rangeMarkers.add(marker);
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Clears all the range markers for the plot.
     */
    public void clearRangeMarkers() {
        if (this.rangeMarkers != null) {
            this.rangeMarkers.clear();
            notifyListeners(new PlotChangeEvent(this));
        }
    }

}
