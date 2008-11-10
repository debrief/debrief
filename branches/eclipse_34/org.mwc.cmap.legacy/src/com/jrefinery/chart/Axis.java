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
 * ---------
 * Axis.java
 * ---------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Bill Kelemen;
 *
 * $Id: Axis.java,v 1.2 2007/01/04 16:30:52 ian.mayo Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header, fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 07-Nov-2001 : Allow null axis labels (DG);
 *             : Added default font values (DG);
 * 13-Nov-2001 : Modified the setPlot(...) method to check compatibility between the axis and the
 *               plot (DG);
 * 30-Nov-2001 : Changed default font from "Arial" --> "SansSerif" (DG);
 * 06-Dec-2001 : Allow null in setPlot(...) method (BK);
 * 06-Mar-2002 : Added AxisConstants interface (DG);
 * 23-Apr-2002 : Added a visible property.  Moved drawVerticalString to RefineryUtilities.  Added
 *               fixedDimension property for use in combined plots (DG);
 * 25-Jun-2002 : Removed unnecessary imports (DG);
 * 05-Sep-2002 : Added attribute for tick mark paint (DG);
 * 18-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.*;
import javax.swing.event.EventListenerList;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.chart.event.AxisChangeListener;

/**
 * The base class for all axes in JFreeChart.
 *
 * @see CategoryAxis
 * @see ValueAxis
 *
 * @author DG
 */
public abstract class Axis implements AxisConstants {

    /** A flag indicating whether or not the axis is visible. */
    protected boolean visible;

    /** The label for the axis. */
    protected String label;

    /** The font for displaying the axis label. */
    protected Font labelFont;

    /** The paint for drawing the axis label. */
    protected Paint labelPaint;

    /** The insets for the axis label. */
    protected Insets labelInsets;

    /** A flag that indicates whether or not tick labels are visible for the axis. */
    protected boolean tickLabelsVisible;

    /** The font used to display the tick labels. */
    protected Font tickLabelFont;

    /** The color used to display the tick labels. */
    protected Paint tickLabelPaint;

    /** The blank space around each tick label. */
    protected Insets tickLabelInsets;

    /** A flag that indicates whether or not tick marks are visible for the axis. */
    protected boolean tickMarksVisible;

    /** The stroke used to draw tick marks. */
    protected Stroke tickMarkStroke;

    /** The paint used to draw tick marks. */
    protected Paint tickMarkPaint;

    /** A working list of ticks - this list is refreshed as required. */
    protected List<Tick> ticks;

    /** A reference back to the plot that the axis is assigned to (can be null). */
    protected Plot plot;

    /** The fixed (horizontal or vertical) dimension for the axis. */
    protected double fixedDimension;

    /** Storage for registered listeners. */
    private EventListenerList listenerList;

    /**
     * Constructs an axis, using default values where necessary.
     *
     * @param label  the axis label (null permitted).
     */
    protected Axis(String label) {

        this(label,
             DEFAULT_AXIS_LABEL_FONT,
             DEFAULT_AXIS_LABEL_PAINT,
             DEFAULT_AXIS_LABEL_INSETS,
             true,  // tick labels visible
             DEFAULT_TICK_LABEL_FONT,
             DEFAULT_TICK_LABEL_PAINT,
             DEFAULT_TICK_LABEL_INSETS,
             true,  // tick marks visible
             DEFAULT_TICK_STROKE,
             DEFAULT_TICK_PAINT);

    }

    /**
     * Constructs an axis.
     *
     * @param label  the axis label.
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the paint used to draw the axis label.
     * @param labelInsets  determines the amount of blank space around the label.
     * @param tickLabelsVisible  a flag indicating whether or not the tick labels are visible.
     * @param tickLabelFont  the font used to display tick labels.
     * @param tickLabelPaint  the paint used to draw tick labels.
     * @param tickLabelInsets  determines the amount of blank space around tick labels.
     * @param tickMarksVisible  flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the paint used to draw tick marks (if visible).
     */
    protected Axis(String label,
                   Font labelFont, Paint labelPaint, Insets labelInsets,
                   boolean tickLabelsVisible,
                   Font tickLabelFont, Paint tickLabelPaint, Insets tickLabelInsets,
                   boolean tickMarksVisible,
                   Stroke tickMarkStroke, Paint tickMarkPaint) {

        this.label = label;
        this.labelFont = labelFont;
        this.labelPaint = labelPaint;
        this.labelInsets = labelInsets;
        this.tickLabelsVisible = tickLabelsVisible;
        this.tickLabelFont = tickLabelFont;
        this.tickLabelPaint = tickLabelPaint;
        this.tickLabelInsets = tickLabelInsets;
        this.tickMarksVisible = tickMarksVisible;
        this.tickMarkStroke = tickMarkStroke;
        this.tickMarkPaint = tickMarkPaint;

        this.ticks = new java.util.ArrayList<Tick>();

        this.visible = true;

        this.listenerList = new EventListenerList();

    }

    /**
     * Returns true if the axis is visible, and false otherwise.
     *
     * @return a flag indicating whether or not the axis is visible.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets a flag that controls whether or not the axis is drawn on the chart.
     *
     * @param flag  the flag.
     */
    public void setVisible(boolean flag) {

        if (flag != this.visible) {
            this.visible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the label for the axis.
     *
     * @return the label for the axis (null possible).
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label for the axis (null permitted).
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param label  the new label.
     */
    public void setLabel(String label) {

        String existing = this.label;
        if (existing != null) {
            if (!existing.equals(label)) {
                this.label = label;
                notifyListeners(new AxisChangeEvent(this));
            }
        }
        else {
            if (label != null) {
                this.label = label;
                notifyListeners(new AxisChangeEvent(this));
            }
        }

    }

    /**
     * Returns the font for the axis label.
     *
     * @return the font.
     */
    public Font getLabelFont() {
        return labelFont;
    }

    /**
     * Sets the font for the axis label.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param font  the new label font.
     */
    public void setLabelFont(Font font) {

        // check arguments...
        if (font == null) {
            throw new IllegalArgumentException("Axis.setLabelFont(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.labelFont.equals(font)) {
            this.labelFont = font;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the color/shade used to draw the axis label.
     *
     * @return the color/shade used to draw the axis label.
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the color/shade used to draw the axis label.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param paint  the new color/shade for the axis label.
     */
    public void setLabelPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException("Axis.setLabelPaint(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.labelPaint.equals(paint)) {
            this.labelPaint = paint;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the insets for the label (that is, the amount of blank space
     * that should be left around the label).
     *
     * @return the label insets.
     */
    public Insets getLabelInsets() {
        return this.labelInsets;
    }

    /**
     * Sets the insets for the axis label, and notifies registered listeners
     * that the axis has been modified.
     *
     * @param insets  the new label insets.
     */
    public void setLabelInsets(Insets insets) {
        if (!insets.equals(this.labelInsets)) {
            this.labelInsets = insets;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns a flag indicating whether or not the tick labels are visible.
     *
     * @return the flag.
     */
    public boolean isTickLabelsVisible() {
        return tickLabelsVisible;
    }

    /**
     * Sets the flag that determines whether or not the tick labels are visible.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param flag  the flag.
     */
    public void setTickLabelsVisible(boolean flag) {

        if (flag != tickLabelsVisible) {
            tickLabelsVisible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the font used for the tick labels (if showing).
     *
     * @return the font used for the tick labels.
     */
    public Font getTickLabelFont() {
        return tickLabelFont;
    }

    /**
     * Sets the font for the tick labels.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param font  the new tick label font.
     */
    public void setTickLabelFont(Font font) {

        // check arguments...
        if (font == null) {
            throw new IllegalArgumentException("Axis.setTickLabelFont(...): null not permitted.");
        }

        // apply change if necessary...
        if (!this.tickLabelFont.equals(font)) {
            this.tickLabelFont = font;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the color/shade used for the tick labels.
     *
     * @return the color/shade used for the tick labels.
     */
    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    /**
     * Sets the color/shade used to draw tick labels (if they are showing).
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param paint  the new color/shade.
     */
    public void setTickLabelPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException("Axis.setTickLabelPaint(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.tickLabelPaint.equals(paint)) {
            this.tickLabelPaint = paint;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the insets for the tick labels.
     *
     * @return the insets for the tick labels.
     */
    public Insets getTickLabelInsets() {
        return this.tickLabelInsets;
    }

    /**
     * Sets the insets for the tick labels, and notifies registered listeners
     * that the axis has been modified.
     *
     * @param insets  the new tick label insets.
     */
    public void setTickLabelInsets(Insets insets) {

        // check arguments...
        if (insets == null) {
            throw new IllegalArgumentException("Axis.setTickLabelInsets(...): null not permitted.");
        }

        // apply change if necessary...
        if (!this.tickLabelInsets.equals(insets)) {
            this.tickLabelInsets = insets;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the flag that indicates whether or not the tick marks are
     * showing.
     *
     * @return the flag that indicates whether or not the tick marks are showing.
     */
    public boolean isTickMarksVisible() {
        return tickMarksVisible;
    }

    /**
     * Sets the flag that indicates whether or not the tick marks are showing.
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param flag  the flag.
     */
    public void setTickMarksVisible(boolean flag) {

        if (flag != tickMarksVisible) {
            tickMarksVisible = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Returns the pen/brush used to draw tick marks (if they are showing).
     *
     * @return the pen/brush used to draw tick marks.
     */
    public Stroke getTickMarkStroke() {
        return tickMarkStroke;
    }

    /**
     * Sets the pen/brush used to draw tick marks (if they are showing).
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param stroke  the new pen/brush (null not permitted).
     */
    public void setTickMarkStroke(Stroke stroke) {

        // check arguments...
        if (stroke == null) {
            throw new IllegalArgumentException("Axis.setTickMarkStroke(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.tickMarkStroke.equals(stroke)) {
            this.tickMarkStroke = stroke;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the paint used to draw tick marks (if they are showing).
     *
     * @return the paint.
     */
    public Paint getTickMarkPaint() {
        return tickMarkPaint;
    }

    /**
     * Sets the paint used to draw tick marks (if they are showing).
     * <P>
     * Registered listeners are notified of a general change to the axis.
     *
     * @param paint  the new paint (null not permitted).
     */
    public void setTickMarkPaint(Paint paint) {

        // check arguments...
        if (paint == null) {
            throw new IllegalArgumentException("Axis.setTickMarkPaint(...): null not permitted.");
        }

        // make the change (if necessary)...
        if (!this.tickMarkPaint.equals(paint)) {
            this.tickMarkPaint = paint;
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the plot that the axis is assigned to.
     * <P>
     * This method will return null if the axis is not currently assigned to a
     * plot.
     *
     * @return The plot that the axis is assigned to.
     */
    public Plot getPlot() {
        return plot;
    }

    /**
     * Sets a reference to the plot that the axis is assigned to.
     * <P>
     * This method is called by Plot in the setHorizontalAxis() and
     * setVerticalAxis() methods. You shouldn't need to call the method
     * yourself.
     *
     * @param plot  the plot that the axis belongs to.
     *
     * @throws PlotNotCompatibleException if plot is not compatible.
     */
    public void setPlot(Plot plot) throws PlotNotCompatibleException {

        if (this.isCompatiblePlot(plot) || plot == null) {
            this.plot = plot;
            this.configure();
        }
        else {
            throw new PlotNotCompatibleException(
                "Axis.setPlot(...): plot not compatible with axis.");
        }
    }

    /**
     * Returns the fixed dimension for the axis.
     *
     * @return the fixed dimension.
     */
    public double getFixedDimension() {
        return this.fixedDimension;
    }

    /**
     * Sets the fixed dimension for the axis.
     * <P>
     * This is used when combining more than one plot on a chart.  In this case,
     * there may be several axes that need to have the same height or width so
     * that they are aligned.  This method is used to fix a dimension for the
     * axis (the context determines whether the dimension is horizontal or
     * vertical).
     *
     * @param dimension  the fixed dimension.
     */
    public void setFixedDimension(double dimension) {
        this.fixedDimension = dimension;
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the axes and plot should be drawn.
     * @param dataArea  the area within which the data should be drawn.
     */
    public abstract void draw(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea);

    /**
     * Calculates the positions of the ticks for the axis, storing the results
     * in the tick list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the axes and plot should be drawn.
     * @param dataArea  the area within which the plot should be drawn.
     */
    public abstract void refreshTicks(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea);

    /**
     * Configures the axis to work with the cuurent plot.  Override this method
     * to perform any special processing (such as auto-rescaling).
     */
    public abstract void configure();

    /**
     * Returns the maximum width of the ticks in the working list (that is set
     * up by refreshTicks()).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot is to be drawn.
     *
     * @return the maximum width of the ticks in the working list.
     */
    protected double getMaxTickLabelWidth(Graphics2D g2, Rectangle2D plotArea) {

        double maxWidth = 0.0;
        Font font = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();

        Iterator iterator = ticks.iterator();
        while (iterator.hasNext()) {
            Tick tick = (Tick) iterator.next();
            Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
            if (labelBounds.getWidth() > maxWidth) {
                maxWidth = labelBounds.getWidth();
            }
        }
        return maxWidth;

    }

    /**
     * Returns true if the plot is compatible with the axis.
     *
     * @param plot  the plot.
     *
     * @return <code>true</code> if the plot is compatible with the axis.
     */
    protected abstract boolean isCompatiblePlot(Plot plot);

    /**
     * Registers an object for notification of changes to the axis.
     *
     * @param listener  the object that is being registered.
     */
    public void addChangeListener(AxisChangeListener listener) {
        this.listenerList.add(AxisChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the axis.
     *
     * @param listener  the object to deregister.
     */
    public void removeChangeListener(AxisChangeListener listener) {
        this.listenerList.remove(AxisChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the axis has changed.
     * The AxisChangeEvent provides information about the change.
     *
     * @param event  information about the change to the axis.
     */
    protected void notifyListeners(AxisChangeEvent event) {

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == AxisChangeListener.class) {
                ((AxisChangeListener) listeners[i + 1]).axisChanged(event);
            }
        }

    }

}
