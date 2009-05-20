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
 * -----------
 * Legend.java
 * -----------
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Andrzej Porebski;
 *                   Jim Moore;
 *
 * $Id: Legend.java,v 1.1.1.1 2003/07/17 10:06:24 Ian.Mayo Exp $
 *
 * Changes (from 20-Jun-2001)
 * --------------------------
 * 20-Jun-2001 : Modifications submitted by Andrzej Porebski for legend placement;
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Tidied up Javadoc comments (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 * 20-Jun-2002 : Added outlineKeyBoxes attribute suggested by Jim Moore (DG);
 * 14-Oct-2002 : Changed listener storage structure (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.event.EventListenerList;

import com.jrefinery.legacy.chart.event.LegendChangeEvent;
import com.jrefinery.legacy.chart.event.LegendChangeListener;

/**
 * A chart legend shows the names and visual representations of the series that
 * are plotted in a chart.
 *
 * @see StandardLegend
 *
 * @author DG
 */
public abstract class Legend {

    /** Constant anchor value for legend position WEST. */
    public static final int WEST = 0x00;

    /** Constant anchor value for legend position NORTH. */
    public static final int NORTH = 0x01;

    /** Constant anchor value for legend position EAST. */
    public static final int EAST = 0x02;

    /** Constant anchor value for legend position SOUTH. */
    public static final int SOUTH = 0x03;

    /** Internal value indicating the bit holding the value of interest in the anchor value. */
    protected static final int INVERTED = 1 << 1;

    /** Internal value indicating the bit holding the value of interest in the anchor value. */
    protected static final int HORIZONTAL = 1 << 0;

    /** The current location anchor of the legend. */
    private int anchor = SOUTH;

    /** A reference to the chart that the legend belongs to (used for access to the dataset). */
    private JFreeChart chart;

    /** The amount of blank space around the legend. */
    private int outerGap;

    /** A flag controlling whether or not outlines are drawn around key boxes.*/
    private boolean outlineKeyBoxes;

    /** Storage for registered change listeners. */
    private EventListenerList listenerList;

    /**
     * Static factory method that returns a concrete subclass of Legend.
     *
     * @param chart  the chart that the legend belongs to.
     *
     * @return a StandardLegend.
     */
    public static Legend createInstance(JFreeChart chart) {
        return new StandardLegend(chart);
    }

    /**
     * Default constructor: returns a new legend.
     *
     * @param chart  the chart that the legend belongs to.
     * @param outerGap  the blank space around the legend.
     */
    public Legend(JFreeChart chart, int outerGap) {
        this.chart = chart;
        this.outerGap = outerGap;
        this.listenerList = new EventListenerList();
    }

    /**
     * Returns the chart that this legend belongs to.
     *
     * @return the chart.
     */
    public JFreeChart getChart() {
        return this.chart;
    }

    /**
     * Returns the outer gap for the legend.
     * <P>
     * This is the amount of blank space around the outside of the legend.
     *
     * @return the gap.
     */
    public double getOuterGap() {
        return this.outerGap;
    }

    /**
     * Returns the flag that indicates whether or not outlines are drawn around key boxes.
     *
     * @return the flag.
     */
    public boolean getOutlineKeyBoxes() {
        return this.outlineKeyBoxes;
    }

    /**
     * Sets the flag that controls whether or not outlines are drawn around key boxes.
     *
     * @param flag The flag.
     */
    public void setOutlineKeyBoxes(boolean flag) {
        this.outlineKeyBoxes = flag;
        notifyListeners(new LegendChangeEvent(this));
    }

    /**
     * Draws the legend on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device.
     * @param available  the area within which the legend (and plot) should be drawn.
     *
     * @return the area remaining after the legend has drawn itself.
     */
    public abstract Rectangle2D draw(Graphics2D g2, Rectangle2D available);

    /**
     * Registers an object for notification of changes to the legend.
     *
     * @param listener  the object that is being registered.
     */
    public void addChangeListener(LegendChangeListener listener) {
        this.listenerList.add(LegendChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the legend.
     *
     * @param listener  the object that is being deregistered.
     */
    public void removeChangeListener(LegendChangeListener listener) {
        this.listenerList.remove(LegendChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the chart legend has changed in some way.
     *
     * @param event  information about the change to the legend.
     */
    protected void notifyListeners(LegendChangeEvent event) {

        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == LegendChangeListener.class) {
                ((LegendChangeListener) listeners[i + 1]).legendChanged(event);
            }
        }

    }

    /**
     * Returns the current anchor of this legend.
     * <p>
     * The default anchor for this legend is SOUTH.
     *
     * @return the current anchor.
     */
    public int getAnchor() {
        return this.anchor;
    }

    /**
     * Sets the current anchor of this legend.
     * <P>
     * The anchor can be one of: NORTH, SOUTH, EAST, WEST.  If a valid anchor
     * value is provided, the current anchor is set and an update event is
     * triggered. Otherwise, no change is made.
     *
     * @param anchor  thenew anchor value.
     */
    public void setAnchor(int anchor) {
         switch(anchor) {
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                this.anchor = anchor;
                notifyListeners(new LegendChangeEvent(this));
                break;
            default:
        }
    }

}
