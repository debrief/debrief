/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
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
 * ---------
 * Plot.java
 * ---------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Sylvain Vieujot;
 *                   Jeremy Bowman;
 *                   Andreas Schneider;
 *
 * $Id: Plot.java,v 1.1.1.1 2003/07/17 10:06:26 Ian.Mayo Exp $
 *
 * Changes (from 21-Jun-2001)
 * --------------------------
 * 21-Jun-2001 : Removed redundant JFreeChart parameter from constructors (DG);
 * 18-Sep-2001 : Updated header info and fixed DOS encoding problem (DG);
 * 19-Oct-2001 : Moved series paint and stroke methods from JFreeChart class (DG);
 * 23-Oct-2001 : Created renderer for LinePlot class (DG);
 * 07-Nov-2001 : Changed type names for ChartChangeEvent (DG);
 *               Tidied up some Javadoc comments (DG);
 * 13-Nov-2001 : Changes to allow for null axes on plots such as PiePlot (DG);
 *               Added plot/axis compatibility checks (DG);
 * 12-Dec-2001 : Changed constructors to protected, and removed unnecessary 'throws' clauses (DG);
 * 13-Dec-2001 : Added tooltips (DG);
 * 22-Jan-2002 : Added handleClick(...) method, as part of implementation for crosshairs (DG);
 *               Moved tooltips reference into ChartInfo class (DG);
 * 23-Jan-2002 : Added test for null axes in chartChanged(...) method, thanks to Barry Evans for
 *               the bug report (number 506979 on SourceForge) (DG);
 *               Added a zoom(...) method (DG);
 * 05-Feb-2002 : Updated setBackgroundPaint(), setOutlineStroke() and setOutlinePaint() to better
 *               handle null values, as suggested by Sylvain Vieujot (DG);
 * 06-Feb-2002 : Added background image, plus alpha transparency for background and foreground (DG);
 * 06-Mar-2002 : Added AxisConstants interface (DG);
 * 26-Mar-2002 : Changed zoom method from empty to abstract (DG);
 * 23-Apr-2002 : Moved dataset from JFreeChart class (DG);
 * 11-May-2002 : Added ShapeFactory interface for getShape() methods, contributed by Jeremy
 *               Bowman (DG);
 * 28-May-2002 : Fixed bug in setSeriesPaint(int, Paint) for subplots (AS);
 * 25-Jun-2002 : Removed redundant imports (DG);
 * 30-Jul-2002 : Added 'no data' message for charts with null or empty datasets (DG);
 * 21-Aug-2002 : Added code to extend series array if necessary (refer to SourceForge bug
 *               id 594547 for details) (DG);
 * 17-Sep-2002 : Fixed bug in getSeriesOutlineStroke(...) method, reported by Andreas
 *               Schroeder (DG);
 * 23-Sep-2002 : Added getLegendItems() abstract method (DG);
 * 24-Sep-2002 : Removed firstSeriesIndex, subplots now use their own paint settings, there is a
 *               new mechanism for the legend to collect the legend items (DG);
 * 27-Sep-2002 : Added dataset group (DG);
 * 14-Oct-2002 : Moved listener storage into EventListenerList.  Changed some abstract methods
 *               to empty implementations (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.event.EventListenerList;
import com.jrefinery.legacy.chart.event.AxisChangeEvent;
import com.jrefinery.legacy.chart.event.AxisChangeListener;
import com.jrefinery.legacy.chart.event.PlotChangeEvent;
import com.jrefinery.legacy.chart.event.PlotChangeListener;
import com.jrefinery.legacy.data.Dataset;
import com.jrefinery.legacy.data.DatasetChangeEvent;
import com.jrefinery.legacy.data.DatasetChangeListener;
import com.jrefinery.legacy.data.DatasetGroup;

/**
 * The base class for all plots in JFreeChart.  The JFreeChart class delegates the drawing of
 * axes and data to the plot.  This base class provides facilities common to most plot types.
 *
 * @author DG
 */
public abstract class Plot implements AxisChangeListener, DatasetChangeListener, AxisConstants {

    /** Useful constant representing zero. */
    public static final Number ZERO = new Integer(0);

    /** The default insets. */
    protected static final Insets DEFAULT_INSETS = new Insets(2, 2, 2, 10);

    /** The default outline stroke. */
    protected static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(1);

    /** The default outline color. */
    protected static final Paint DEFAULT_OUTLINE_PAINT = Color.gray;

    /** The default foreground alpha transparency. */
    protected static final float DEFAULT_FOREGROUND_ALPHA = 1.0f;

    /** The default background alpha transparency. */
    protected static final float DEFAULT_BACKGROUND_ALPHA = 1.0f;

    /** The default background color. */
    protected static final Paint DEFAULT_BACKGROUND_PAINT = Color.white;

    /** The minimum width for the plot, any less space than this and it should
     *  not be drawn (not fully implemented). */
    protected static final int MINIMUM_WIDTH_TO_DRAW = 10;

    /** The minimum height for the plot, any less space than this and it should
     *  not be drawn (not fully implemented. */
    protected static final int MINIMUM_HEIGHT_TO_DRAW = 10;

    /** The data. */
    protected Dataset dataset;

    /**
     * The dataset group for the plot's dataset (or datasets in the case of combined and overlaid
     * plots.
     */
    private DatasetGroup datasetGroup;

    /** The message to display if no data is available. */
    protected String noDataMessage;

    /** The font used to display the 'no data' message. */
    protected Font noDataMessageFont;

    /** Amount of blank space around the plot area. */
    protected Insets insets;

    /** The Stroke used to draw an outline around the plot. */
    protected Stroke outlineStroke;

    /** The Paint used to draw an outline around the plot. */
    protected Paint outlinePaint;

    /** An optional color used to fill the plot background. */
    protected Paint backgroundPaint;

    /** An optional image for the plot background. */
    protected Image backgroundImage;

    /** The alpha-transparency for the plot. */
    protected float foregroundAlpha;

    /** The alpha transparency for the background paint. */
    protected float backgroundAlpha;

    /** Paint objects used to color each series in the chart. */
    protected Paint[] seriesPaint;

    /** Stroke objects used to draw each series in the chart. */
    protected Stroke[] seriesStroke;

    /** Paint objects used to draw the outline of each series in the chart. */
    protected Paint[] seriesOutlinePaint;

    /** Stroke objects used to draw the outline of each series in the chart. */
    protected Stroke[] seriesOutlineStroke;

    /** Factory for shapes used to represent data points */
    protected ShapeFactory shapeFactory;

    /** Storage for registered change listeners. */
    protected EventListenerList listenerList;

    /**
     * Constructs a new plot with the specified axes.
     *
     * @param data  the dataset.
     */
    protected Plot(Dataset data) {

        this(data,
             DEFAULT_INSETS,
             DEFAULT_BACKGROUND_PAINT,
             null, // background image
             DEFAULT_BACKGROUND_ALPHA,
             DEFAULT_OUTLINE_STROKE,
             DEFAULT_OUTLINE_PAINT,
             DEFAULT_FOREGROUND_ALPHA
             );

    }

    /**
     * Constructs a new plot.
     *
     * @param data  the dataset.
     * @param insets  the amount of blank space around the plot area.
     * @param backgroundPaint  an optional color for the plot's background.
     * @param backgroundImage  an optional image for the plot's background.
     * @param backgroundAlpha  alpha-transparency for the plot's background.
     * @param outlineStroke  the Stroke used to draw an outline around the plot.
     * @param outlinePaint  the color used to draw an outline around the plot.
     * @param foregroundAlpha  the alpha-transparency for the plot foreground.
     */
    protected Plot(Dataset data,
                   Insets insets,
                   Paint backgroundPaint, Image backgroundImage, float backgroundAlpha,
                   Stroke outlineStroke, Paint outlinePaint,
                   float foregroundAlpha) {

        // set the data and register to receive change notifications...
        this.dataset = data;
        if (data != null) {
            this.datasetGroup = data.getGroup();
            data.addChangeListener(this);
        }

        this.noDataMessage = null;
        this.noDataMessageFont = new Font("SansSerif", Font.PLAIN, 12);

        this.insets = insets;
        this.backgroundPaint = backgroundPaint;
        this.backgroundAlpha = backgroundAlpha;
        this.outlineStroke = outlineStroke;
        this.outlinePaint = outlinePaint;
        this.foregroundAlpha = foregroundAlpha;

        this.seriesStroke = new Stroke[] { new BasicStroke(1.0f) };
        this.seriesPaint = new Paint[] {Color.red, Color.blue, Color.green,
                                        Color.yellow, Color.orange, Color.magenta,
                                        Color.cyan, Color.pink, Color.lightGray};

        this.seriesOutlinePaint = new Paint[] { Color.gray };
        this.seriesOutlineStroke = new Stroke[] { new BasicStroke(0.5f) };

        this.shapeFactory = new SeriesShapeFactory();
        this.listenerList = new EventListenerList();

    }

    /**
     * Returns the dataset for the plot.
     *
     * @return the dataset.
     */
    public Dataset getDataset() {
        return dataset;
    }

    /**
     * Sets the data for the chart, replacing any existing data.  Registered
     * listeners are notified that the data has been modified.
     * <P>
     * The plot is automatically registered with the new dataset, to listen for
     * any changes.
     *
     * @param data  the new dataset.
     */
    public void setDataset(Dataset data) {

        // if there is an existing dataset, remove the chart from the list of
        // change listeners...
        Dataset existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }

        // set the new dataset, and register the chart as a change listener...
        this.dataset = data;
        if (data != null) {
            this.datasetGroup = data.getGroup();
            data.addChangeListener(this);
        }

        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, data);
        datasetChanged(event);

    }

    /**
     * Returns the dataset group for the plot.
     */
    public DatasetGroup getDatasetGroup() {
        return this.datasetGroup;
    }

    /**
     * Sets the dataset group.
     *
     * @param group  the dataset group.
     */
    protected void setDatasetGroup(DatasetGroup group) {
        this.datasetGroup = group;
    }

    /**
     * Returns the string that is displayed when the dataset is empty or null.
     *
     * @return the 'no data' message (null possible).
     */
    public String getNoDataMessage() {
        return this.noDataMessage;
    }

    /**
     * Sets the message that is displayed when the dataset is empty or null.
     *
     * @param message  the message (null permitted).
     */
    public void setNoDataMessage(String message) {
        this.noDataMessage = message;
    }

    /**
     * Returns the font used to display the 'no data' message.
     *
     * @return the font.
     */
    public Font getNoDataMessageFont() {
        return this.noDataMessageFont;
    }

    /**
     * Sets the font used to display the 'no data' message.
     *
     * @param font  the font.
     */
    public void setNoDataMessageFont(Font font) {
        this.noDataMessageFont = font;
    }

    /**
     * Returns a short string describing the plot type.
     * <P>
     * Note: this gets used in the chart property editing user interface,
     * but there needs to be a better mechanism for identifying the plot type.
     *
     * @return a short string describing the plot type.
     */
    public abstract String getPlotType();

    /**
     * Returns true if this plot is part of a combined plot structure.
     *
     * @return a flag indicating if this plot is a subplot.
     */
    public boolean isSubplot() {
        return false;
    }

    /**
     * Returns the insets for the plot area.
     *
     * @return the insets.
     */
    public Insets getInsets() {
        return this.insets;
    }

    /**
     * Sets the insets for the plot and notifies registered listeners that the
     * plot has been modified.
     *
     * @param insets  the new insets.
     */
    public void setInsets(Insets insets) {

        if (!this.insets.equals(insets)) {
            this.insets = insets;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the background color of the plot area.
     *
     * @return the background color (null possible).
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background color of the plot area, and notifies registered
     * listeners that the plot has been modified.
     *
     * @param paint  the new background color (null permitted).
     */
    public void setBackgroundPaint(Paint paint) {

        if (paint == null) {
            if (this.backgroundPaint != null) {
                this.backgroundPaint = null;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        else {
            if (this.backgroundPaint != null) {
                if (this.backgroundPaint.equals(paint)) {
                    return;  // nothing to do
                }
            }
            this.backgroundPaint = paint;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the alpha transparency of the plot area background.
     *
     * @return the alpha transparency.
     */
    public float getBackgroundAlpha() {
        return this.backgroundAlpha;
    }

    /**
     * Sets the alpha transparency of the plot area background, and notifies
     * registered listeners that the plot has been modified.
     *
     * @param alpha the new alpha value.
     */
    public void setBackgroundAlpha(float alpha) {

        if (this.backgroundAlpha != alpha) {
            this.backgroundAlpha = alpha;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Sets the background image for the plot.
     *
     * @param image  the background image.
     */
    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the pen/brush used to outline the plot area.
     *
     * @return the outline stroke (possibly null).
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the pen/brush used to outline the plot area, and notifies
     * registered listeners that the plot has been modified.
     *
     * @param stroke  the new outline pen/brush (null permitted).
     */
    public void setOutlineStroke(Stroke stroke) {

        if (stroke == null) {
            if (this.outlineStroke != null) {
                this.outlineStroke = null;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        else {
            if (this.outlineStroke != null) {
                if (this.outlineStroke.equals(stroke)) {
                    return;  // nothing to do
                }
            }
            this.outlineStroke = stroke;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the color used to draw the outline of the plot area.
     *
     * @return the color (possibly null).
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the color of the outline of the plot area, and notifies registered
     * listeners that the Plot has been modified.
     *
     * @param paint  the new outline paint (null permitted).
     */
    public void setOutlinePaint(Paint paint) {

        if (paint == null) {
            if (this.outlinePaint != null) {
                this.outlinePaint = null;
                notifyListeners(new PlotChangeEvent(this));
            }
        }
        else {
            if (this.outlinePaint != null) {
                if (this.outlinePaint.equals(paint)) {
                    return;  // nothing to do
                }
            }
            this.outlinePaint = paint;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns the alpha-transparency for the plot foreground.
     *
     * @return the alpha-transparency.
     */
    public float getForegroundAlpha() {
        return this.foregroundAlpha;
    }

    /**
     * Sets the alpha-transparency for the plot.
     *
     * @param alpha  the new alpha transparency.
     */
    public void setForegroundAlpha(float alpha) {

        if (this.foregroundAlpha != alpha) {
            this.foregroundAlpha = alpha;
            notifyListeners(new PlotChangeEvent(this));
        }

    }

    /**
     * Returns a list of labels for the legend.
     * <P>
     * Most plots will return a list of series names, but there are some plots (e.g. PiePlot)
     * that do not have series so they will return something else.
     *
     * @return the series labels.
     *
     * @deprecated use getLegendItems().
     */
    @SuppressWarnings("unchecked")
		public List getLegendItemLabels() {
        // by default, return an empty list (subclasses will override).
        return new java.util.ArrayList();
    }

    /**
     * Returns the legend items for the plot.
     * <P>
     * By default, this method returns null.  Subclasses should override to return a collection
     * of legend items.
     *
     * @return the legend items for the plot.
     */
    public LegendItemCollection getLegendItems() {
        return null;
    }

    /**
     * Returns a Paint object used as the main color for a series.
     *
     * @param index  the series index (zero-based).
     *
     * @return a Paint object used as the main color for a series.
     */
    public Paint getSeriesPaint(int index) {

        return seriesPaint[index % seriesPaint.length];

    }

    /**
     * Sets the paint used to color any shapes representing series, and
     * notifies registered listeners that the plot has been modified.
     *
     * @param paint  an array of Paint objects used to color series.
     */
    public void setSeriesPaint(Paint[] paint) {
        this.seriesPaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Sets the paint used to color any shapes representing a specific series,
     * and notifies registered listeners that the plot has been modified.
     *
     * @param index  the series (zero-based index).
     * @param paint  an array of Paint objects used to color series.
     */
    public void setSeriesPaint(int index, Paint paint) {

        // check to see if the array is being extended
        int count = this.seriesPaint.length;
        if (index >= count) {
            Paint[] extended = new Paint[index + 1];
            for (int i = 0; i < index; i++) {
                extended[i] = getSeriesPaint(i);
            }
            this.seriesPaint = extended;
        }

        this.seriesPaint[index] = paint;
        notifyListeners(new PlotChangeEvent(this));

    }

    /**
     * Returns the Stroke used to draw any shapes for a series.
     *
     * @param index  the series (zero-based index).
     *
     * @return the Stroke used to draw any shapes for a series.
     */
    public Stroke getSeriesStroke(int index) {
        return seriesStroke[index % seriesStroke.length];
    }

    /**
     * Sets the stroke used to draw any shapes representing series, and
     * notifies registered listeners that the chart has been modified.
     *
     * @param stroke  an array of Stroke objects used to draw series.
     */
    public void setSeriesStroke(Stroke[] stroke) {
        this.seriesStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Sets the stroke used to draw any shapes representing a specific series,
     * and notifies registered listeners that the chart has been modified.
     *
     * @param index  the series (zero-based index)
     * @param stroke  an array of Stroke objects used to draw series.
     */
    public void setSeriesStroke(int index, Stroke stroke) {
        this.seriesStroke[index] = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the Paint used to outline any shapes for the specified series.
     *
     * @param index  the series (zero-based index).
     *
     * @return the Paint.
     */
    public Paint getSeriesOutlinePaint(int index) {
        return seriesOutlinePaint[index % seriesOutlinePaint.length];
    }

    /**
     * Sets the paint used to outline any shapes representing series, and
     * notifies registered listeners that the chart has been modified.
     *
     * @param paint  an array of Paint objects for drawing the outline of series shapes.
     */
    public void setSeriesOutlinePaint(Paint[] paint) {
        this.seriesOutlinePaint = paint;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the Stroke used to outline any shapes for the specified series.
     *
     * @param index  the series (zero-based index).
     *
     * @return the Stroke used to outline any shapes for the specified series.
     */
    public Stroke getSeriesOutlineStroke(int index) {
        return seriesOutlineStroke[index % seriesOutlineStroke.length];
    }

    /**
     * Sets the stroke used to draw any shapes representing series, and
     * notifies registered listeners that the chart has been modified.
     *
     * @param stroke  an array of Stroke objects.
     */
    public void setSeriesOutlineStroke(Stroke[] stroke) {
        this.seriesOutlineStroke = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Sets the stroke used to draw any shapes representing a specific series,
     * and notifies registered listeners that the chart has been modified.
     *
     * @param index  the series index (zero-based).
     * @param stroke  an array of Stroke objects.
     */
    public void setSeriesOutlineStroke(int index, Stroke stroke) {
        this.seriesOutlineStroke[index] = stroke;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns the object used to generate shapes for marking data points.
     *
     * @return the object used to generate shapes for marking data points.
     */
    public ShapeFactory getShapeFactory() {
        return shapeFactory;
    }

    /**
     * Sets the object used to generate shapes for marking data points.
     *
     * @param factory  the new shape factory.
     */
    public void setShapeFactory(ShapeFactory factory) {
        this.shapeFactory = factory;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns a Shape that can be used in plotting data.  Used in XYPlots.
     *
     * @param series  the index of the series.
     * @param item  the index of the item.
     * @param x  x-coordinate of the item.
     * @param y  y-coordinate of the item.
     * @param scale  the size.
     *
     * @return a Shape that can be used in plotting data.
     */
    public Shape getShape(int series, int item, double x, double y, double scale) {

        Shape shape = null;
        if (shapeFactory != null) {
            shape = shapeFactory.getShape(series, item, x, y, scale);
        }
        return shape;

    }

    /**
     * Returns a Shape that can be used in plotting data.  Should allow a
     * plug-in object to determine the shape...
     *
     * @param series  the index of the series.
     * @param category  the category.
     * @param x  x-coordinate of the category.
     * @param y  y-coordinate of the category.
     * @param scale  the size.
     *
     * @return a Shape that can be used in plotting data.
     */
    public Shape getShape(int series, Object category, double x, double y, double scale) {

        Shape shape = null;
        if (shapeFactory != null) {
            shape = shapeFactory.getShape(series, category, x, y, scale);
        }
        return shape;

    }

    /**
     * Registers an object for notification of changes to the plot.
     *
     * @param listener  the object to be registered.
     */
    public void addChangeListener(PlotChangeListener listener) {
        listenerList.add(PlotChangeListener.class, listener);
    }

    /**
     * Unregisters an object for notification of changes to the plot.
     *
     * @param listener  the object to be unregistered.
     */
    public void removeChangeListener(PlotChangeListener listener) {
        listenerList.remove(PlotChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the plot has been modified.
     *
     * @param event  information about the change event.
     */
    public void notifyListeners(PlotChangeEvent event) {

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PlotChangeListener.class) {
                ((PlotChangeListener) listeners[i + 1]).plotChanged(event);
            }
        }

    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This class does not store any information about where the individual
     * items that make up the plot are actually drawn.  If you want to collect
     * this information, pass in a ChartRenderingInfo object.  After the
     * drawing is complete, the info object will contain lots of information
     * about the chart.  If you don't want the information, pass in null.
     * *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot should be drawn.
     * @param info  an object for collecting information about the drawing of the chart.
     */
    public abstract void draw(Graphics2D g2, Rectangle2D plotArea, ChartRenderingInfo info);

    /**
     * Draw the plot outline and background.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     */
    public void drawOutlineAndBackground(Graphics2D g2, Rectangle2D area) {

        if (backgroundPaint != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       this.backgroundAlpha));
            g2.setPaint(backgroundPaint);
            g2.fill(area);
            g2.setComposite(originalComposite);
        }

        if (backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, this.backgroundAlpha));
            g2.drawImage(this.backgroundImage,
                         (int) area.getX(), (int) area.getY(),
                         (int) area.getWidth(), (int) area.getHeight(), null);
            g2.setComposite(originalComposite);
        }

        if ((outlineStroke != null) && (outlinePaint != null)) {
            g2.setStroke(outlineStroke);
            g2.setPaint(outlinePaint);
            g2.draw(area);
        }

    }

    /**
     * Draws a message to state that there is no data to plot.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     */
    protected void drawNoDataMessage(Graphics2D g2, Rectangle2D area) {

        Shape savedClip = g2.getClip();
        g2.clip(area);
        String message = this.noDataMessage;
        if (message != null) {
            g2.setFont(this.noDataMessageFont);
            //g2.setPaint(labelPaint);
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D bounds = noDataMessageFont.getStringBounds(message, frc);
            float x = (float) (area.getX() + area.getWidth() / 2 - bounds.getWidth() / 2);
            float y = (float) (area.getMinY() + (area.getHeight() / 2) - (bounds.getHeight() / 2));
            g2.drawString(message, x, y);
        }
        g2.clip(savedClip);

    }

    /**
     * Handles a 'click' on the plot.  Since the plot does not maintain any
     * information about where it has been drawn, the plot area is supplied as
     * an argument.
     *
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     * @param info  an object for collecting information about the drawing of the chart.
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

    }

    /**
     * Performs a zoom on the plot.  Subclasses should override if zooming is appropriate for
     * the type of plot.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) {
        // do nothing by default.
    }

    /**
     * Receives notification of a change to one of the plot's axes.
     *
     * @param event  information about the event (not used here).
     */
    public void axisChanged(AxisChangeEvent event) {
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The plot reacts by passing on a plot change event to all registered listeners.
     *
     * @param event  information about the event (not used here).
     */
    public void datasetChanged(DatasetChangeEvent event) {

        PlotChangeEvent newEvent = new PlotChangeEvent(this);
        notifyListeners(newEvent);

    }

}