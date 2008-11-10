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
 * -------------------
 * CombinedXYPlot.java
 * -------------------
 * (C) Copyright 2001, 2002, by Bill Kelemen and Contributors.
 *
 * Original Author:  Bill Kelemen;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *                   Anthony Boulestreau;
 *                   David Basten;
 *                   Kevin Frechette (for ISTI);
 *
 * $Id: CombinedXYPlot.java,v 1.1.1.1 2003/07/17 10:06:21 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 06-Dec-2001 : Version 1 (BK);
 * 12-Dec-2001 : Removed unnecessary 'throws' clause from constructor (DG);
 * 18-Dec-2001 : Added plotArea attribute and get/set methods (BK);
 * 22-Dec-2001 : Fixed bug in chartChanged with multiple combinations of CombinedPlots (BK);
 * 08-Jan-2002 : Moved to new package com.jrefinery.chart.combination (DG);
 * 25-Feb-2002 : Updated import statements (DG);
 * 28-Feb-2002 : Readded "this.plotArea = plotArea" that was deleted from draw() method (BK);
 * 26-Mar-2002 : Added an empty zoom method (this method needs to be written so that combined
 *               plots will support zooming (DG);
 * 29-Mar-2002 : Changed the method createCombinedAxis adding the creation of OverlaidSymbolicAxis
 *               and CombinedSymbolicAxis(AB);
 * 23-Apr-2002 : Renamed CombinedPlot-->MultiXYPlot, and simplified the structure (DG);
 * 23-May-2002 : Renamed (again) MultiXYPlot-->CombinedXYPlot (DG);
 * 19-Jun-2002 : Added get/setGap() methods suggested by David Basten (DG);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 16-Jul-2002 : Draws shared axis after subplots (to fix missing gridlines),
 *               added overrides of 'setSeriesPaint()' and 'setXYItemRenderer()'
 *               that pass changes down to subplots (KF);
 * 09-Oct-2002 : Added add(XYPlot) method (DG);
 *
 */

package com.jrefinery.chart;

import com.jrefinery.data.Range;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.awt.Paint;
import java.util.List;
import java.util.Iterator;

/**
 * An extension of XYPlot that can contain multiple subplots, laid out horizontally or vertically.
 * <P>
 * This class was originally written by Bill Kelemen, and has since been modified extensively by
 * David Gilbert.
 *
 * @author Bill Kelemen (bill@kelemen-usa.com).
 */
public class CombinedXYPlot extends XYPlot {

    /** Constant used to indicate horizontal layout. */
    public static final int HORIZONTAL = 0;

    /** Constant used to indicate vertical layout. */
    public static final int VERTICAL = 1;

    /** The layout type (HORIZONTAL or VERTICAL). */
    private int type;

    /** Storage for the subplot references. */
    private List subplots;

    /** Total weight of all charts. */
    private int totalWeight = 0;

    /** The gap between subplots. */
    private double gap = 5.0;

    /**
     * Creates a new MultiXYPlot.
     * <P>
     * If the layout type is HORIZONTAL, you need to supply a vertical axis to be shared by the
     * subplots.  If the layout type is VERTICAL, you need to supply a horizontal axis to be shared
     * by the subplots.
     *
     * @param axis  the shared axis.
     * @param type  the layout type (HORIZONTAL or VERTICAL).
     */
    public CombinedXYPlot(ValueAxis axis, int type) {

        super(null, // no data in the parent plot
             (type == VERTICAL ? axis : null),
             (type == HORIZONTAL ? axis : null));

        if (type != HORIZONTAL && type != VERTICAL) {
            throw new IllegalArgumentException("Invalid type (" + type + ")");
        }

        this.type = type;
        this.subplots = new java.util.ArrayList();

    }

    /**
     * Sets the amount of space between subplots.
     *
     * @param gap  the gap between subplots
     */
    public void setGap(double gap) {
        this.gap = gap;
    }

    /**
     * Returns the space between subplots.
     *
     * @return the gap
     */
    public double getGap() {
        return gap;
    }

    /**
     * Adds a subplot, with a default 'weight' of 1.
     * <P>
     * The subplot should have a null horizontal axis (for VERTICAL layout) or a null vertical
     * axis (for HORIZONTAL layout).
     *
     * @param subplot  the subplot.
     */
    public void add(XYPlot subplot) {
        add(subplot, 1);
    }

    /**
     * Adds a subplot with a particular weight (greater than or equal to one).  The weight
     * determines how much space is allocated to the subplot relative to all the other subplots.
     * <P>
     * The subplot should have a null horizontal axis (for VERTICAL layout) or a
     * null vertical axis (for HORIZONTAL layout).
     *
     * @param subplot  the subplot.
     * @param weight  the weight.
     *
     * @throws AxisNotCompatibleException if axis are not compatible.
     * @throws IllegalArgumentException if weight <code>&lt; 1</code>
     */
    public void add(XYPlot subplot, int weight) throws AxisNotCompatibleException,
                                                       IllegalArgumentException {

        if (!isValidSubHorizontalAxis((Axis) subplot.getHorizontalAxis())) {
            String msg = "CombinedXYPlot.add(...): invalid horizontal axis.";
            throw new AxisNotCompatibleException(msg);
        }
        else if (!isValidSubVerticalAxis((Axis) subplot.getVerticalAxis())) {
            String msg = "CombinedXYPlot.add(...): invalid vertical axis.";
            throw new AxisNotCompatibleException(msg);
        }

        // verify valid weight
        if (weight <= 0) {
            String msg = "CombinedXYPlot.add(...): weight must be positive.";
            throw new IllegalArgumentException(msg);
        }

        // store the plot and its weight
        subplot.setParent(this);
        subplot.setWeight(weight);
        subplot.setInsets(new Insets(0, 0, 0, 0));
        if (type == VERTICAL) {
            subplot.setDomainAxis(null);
        }
        if (type == HORIZONTAL) {
            subplot.setRangeAxis(null);
        }
        subplots.add(subplot);

        // keep track of total weights
        totalWeight += weight;

        if (type == HORIZONTAL) {
            getRangeAxis().configure();
        }
        if (type == VERTICAL) {
            getDomainAxis().configure();
        }

    }

    /**
     * Checks that the horizontal axis for the subplot is valid.
     * <P>
     * Note that for a VERTICAL layout, the horizontal axis must be null (since each subplot
     * shares the horizontal axis maintained by this class).
     *
     * @param axis  the horizontal axis.
     *
     * @return <code>true</code> if the horizontal axis for the subplot is valid.
     */
    public boolean isValidSubHorizontalAxis(Axis axis) {

        boolean result = true;

        if (type == VERTICAL) {
            result = (axis == null);
        }

        return result;

    }

    /**
     * Checks that the vertical axis for the subplot is valid.
     * <P>
     * Note that for a HORIZONTAL layout, the vertical axis must be null (since each subplot
     * shares the vertical axis maintained by this class).
     *
     * @param axis  the vertical axis.
     *
     * @return <code>true</code> if the vertical axis for the subplot is valid.
     */
    public boolean isValidSubVerticalAxis(Axis axis) {

        boolean result = true;

        if (type == HORIZONTAL) {
            result = (axis == null);
        }

        return result;

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
     * Will perform all the placement calculations for each sub-plots and then tell these to draw
     * themselves.
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot (including axis labels) should be drawn.
     * @param info  collects information about the drawing (null permitted).
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info) {

        // set up info collection...
        if (info != null) {
            info.setPlotArea(plotArea);
        }

        // adjust the drawing area for plot insets (if any)...
        if (insets != null) {
            plotArea.setRect(plotArea.getX() + insets.left,
                             plotArea.getY() + insets.top,
                             plotArea.getWidth() - insets.left - insets.right,
                             plotArea.getHeight() - insets.top - insets.bottom);
        }

        // reserve the height or width of the shared axis...
        double sharedAxisDimension = 0;
        if (type == CombinedXYPlot.VERTICAL) {
            HorizontalAxis hAxis = getHorizontalAxis();
            if (hAxis != null) {
                sharedAxisDimension = hAxis.reserveHeight(g2, this, plotArea);
            }
        }
        else { // VERTICAL
            VerticalAxis vAxis = getVerticalAxis();
            if (vAxis != null) {
                sharedAxisDimension = vAxis.reserveWidth(g2, this, plotArea);
            }
        }

        Rectangle2D dataArea;
        if (type == HORIZONTAL) {
            dataArea = new Rectangle2D.Double(plotArea.getX() + sharedAxisDimension,
                                              plotArea.getY(),
                                              plotArea.getWidth() - sharedAxisDimension,
                                              plotArea.getHeight());
        }
        else {
            dataArea = new Rectangle2D.Double(plotArea.getX(),
                                              plotArea.getY(),
                                              plotArea.getWidth(),
                                              plotArea.getHeight() - sharedAxisDimension);
        }

        // work out the maximum height or width of the non-shared axes...
        int n = subplots.size();

        // calculate plotAreas of all sub-plots, maximum vertical/horizontal axis width/height
        Rectangle2D[] subPlotArea = new Rectangle2D[n];
        double x = dataArea.getX();
        double y = dataArea.getY();
        double usableWidth = dataArea.getWidth();
        if (type == HORIZONTAL) {
            usableWidth = usableWidth - gap * (n - 1);
        }
        double usableHeight = dataArea.getHeight();
        if (type == VERTICAL) {
            usableHeight = usableHeight - gap * (n - 1);
        }
        double maxAxisWidth = Double.MIN_VALUE;
        double maxAxisHeight = Double.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            XYPlot plot = (XYPlot) subplots.get(i);

            // calculate sub-plot height
            double subPlotAreaHeight = usableHeight;
            if (type == VERTICAL) {
                subPlotAreaHeight *= (double) plot.getWeight() / totalWeight;
            }

            // calculate sub-plot width
            double subPlotAreaWidth = usableWidth;
            if (type == HORIZONTAL) {
                subPlotAreaWidth *= (double) plot.getWeight() / totalWeight;
            }

            // calculate sub-plot area
            subPlotArea[i] = new Rectangle2D.Double(x, y, subPlotAreaWidth, subPlotAreaHeight);

            // calculate sub-plot max axis width and height if needed
            if (type == VERTICAL) {
                maxAxisWidth =
                    Math.max(maxAxisWidth,
                             plot.getVerticalAxis().reserveWidth(g2, plot, subPlotArea[i]));
            }
            else if (type == HORIZONTAL) {
                maxAxisHeight =
                    Math.max(maxAxisHeight,
                             plot.getHorizontalAxis().reserveHeight(g2, plot, subPlotArea[i]));
            }


            // calculat next (x, y)
            if (type == VERTICAL) {
                y += subPlotAreaHeight + gap;
            }
            else if (type == HORIZONTAL) {
                x += subPlotAreaWidth + gap;
            }
        }

        // set the width and height of non-shared axis of all sub-plots
        if (type == VERTICAL) {
            setVerticalAxisWidth(maxAxisWidth);
            dataArea.setRect(dataArea.getX() + maxAxisWidth, dataArea.getY(),
                             dataArea.getWidth() - maxAxisWidth, dataArea.getHeight());
        }
        else if (type == HORIZONTAL) {
            setHorizontalAxisHeight(maxAxisHeight);
            dataArea.setRect(dataArea.getX(), dataArea.getY(),
                             dataArea.getWidth(), dataArea.getHeight() - maxAxisHeight);
        }

        if (info != null) {
            info.setDataArea(dataArea);
        }

        // draw all the charts
        for (int i = 0; i < n; i++) {
            XYPlot plot = (XYPlot) subplots.get(i);
            plot.draw(g2, subPlotArea[i], info);
        }

        // draw the shared axis
        if (type == VERTICAL) {
            getDomainAxis().draw(g2, plotArea, dataArea);
        }
        else if (type == HORIZONTAL) {
            getRangeAxis().draw(g2, plotArea, dataArea);
        }

    }

    /**
     * Sets the height for the horizontal axis of each subplot.
     *
     * @param height  the height.
     */
    protected void setHorizontalAxisHeight(double height) {

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            Axis axis = plot.getDomainAxis();
            axis.setFixedDimension(height);
        }

    }

    /**
     * Sets the width for the vertical axis of each subplot.
     *
     * @param width  the width.
     */
    protected void setVerticalAxisWidth(double width) {

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            Axis axis = plot.getRangeAxis();
            axis.setFixedDimension(width);
        }

    }

    /**
     * Returns a collection of legend items for the plot.
     *
     * @return the legend items.
     */
    public LegendItemCollection getLegendItems() {

        LegendItemCollection result = new LegendItemCollection();

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot plot = (XYPlot) iterator.next();
                LegendItemCollection more = plot.getLegendItems();
                result.addAll(more);
            }
        }

        return result;

    }

    /**
     * Returns a string describing the type of plot.
     *
     * @return the type of plot.
     */
    public String getPlotType() {

        switch (type) {
          case HORIZONTAL: return "Horizontal CombinedXYPlot";
          case VERTICAL:   return "Vertical CombinedXYPlot";
          default:         return "Unknown";
        }

    }

    /**
     * A zoom method that (currently) does nothing.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) {
        // need to doDecide how to handle zooming...
    }

    /**
     * Sets the paint used to color any shapes representing series FOR ALL SUBPLOTS.
     * Registered listeners are notified that the plot has been modified.
     * <P>
     * Note: usually you will want to set the colors independently for each subplot, which is
     * NOT what this method does.
     *
     * @param paint an array of Paint objects used to color series.
     */
    public void setSeriesPaint(Paint[] paint) {

        super.setSeriesPaint(paint);  // not strictly necessary, since the colors set for the
                                      // parent plot are not used now

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            plot.setSeriesPaint(paint);
        }

    }

    /**
     * Sets the item renderer FOR ALL SUBPLOTS.  Registered listeners are notified that the plot
     * has been modified.
     * <P>
     * Note: usually you will want to set the renderer independently for each subplot, which is
     * NOT what this method does.
     *
     * @param renderer the new renderer.
     */
    public void setRenderer(XYItemRenderer renderer) {

        super.setRenderer(renderer);  // not strictly necessary, since the renderer set for the
                                      // parent plot is not used

        Iterator iterator = subplots.iterator();
        while (iterator.hasNext()) {
            XYPlot plot = (XYPlot) iterator.next();
            plot.setRenderer(renderer);
        }

    }

    //////////////////////////////////////////////////////////////////////////////
    // From HorizontalValuePlot and VerticalValuePlot
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the range for the horizontal axis.  This is the combined range of all the subplots.
     *
     * @return the range.
     */
    public Range getHorizontalDataRange() {

        Range result = null;

        if (type == VERTICAL) {
            if (subplots != null) {
                Iterator iterator = subplots.iterator();
                while (iterator.hasNext()) {
                    XYPlot subplot = (XYPlot) iterator.next();
                    result = Range.combine(result, subplot.getHorizontalDataRange());
                }
            }
        }

        return result;

    }

    /**
     * Returns the range for the vertical axis.  This is the combined range of all the subplots.
     *
     * @return the range.
     */
    public Range getVerticalDataRange() {

        Range result = null;

        if (type == HORIZONTAL) {
            if (subplots != null) {
                Iterator iterator = subplots.iterator();
                while (iterator.hasNext()) {
                    XYPlot subplot = (XYPlot) iterator.next();
                    result = Range.combine(result, subplot.getVerticalDataRange());
                }
            }
        }

        return result;

    }

    /**
     * Sets the XYItemRenderer for the plot.
     *
     * @param renderer  the renderer.
     *
     * @deprecated use setRenderer(...) method.
     */
    public void setXYItemRenderer(XYItemRenderer renderer) {
        setRenderer(renderer);
    }

    /**
     * Returns an array of labels to be displayed by the legend.
     *
     * @return an array of legend item labels (or null).
     *
     * @deprecated use getLegendItems.
     */
    public List getLegendItemLabels() {

        List result = new java.util.ArrayList();

        if (subplots != null) {
            Iterator iterator = subplots.iterator();
            while (iterator.hasNext()) {
                XYPlot plot = (XYPlot) iterator.next();
                List more = plot.getLegendItemLabels();
                result.addAll(more);
            }
        }

        return result;

    }

}
