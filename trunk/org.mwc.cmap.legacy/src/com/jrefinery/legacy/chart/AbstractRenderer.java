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
 * ---------------------
 * AbstractRenderer.java
 * ---------------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: AbstractRenderer.java,v 1.1.1.1 2003/07/17 10:06:19 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 22-Aug-2002 : Version 1, draws code out of AbstractXYItemRenderer to share with
 *               AbstractCategoryItemRenderer (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * Base class providing essential services for renderers.
 *
 * @author DG
 */
public class AbstractRenderer {

    /** Support class for the property change listener mechanism. */
    private PropertyChangeSupport listeners;

    /** A temporary reference to chart rendering info (may be null). */
    private ChartRenderingInfo info;

    /**
     * Default constructor.
     */
    public AbstractRenderer() {
        this.listeners = new PropertyChangeSupport(this);
        this.info = null;
    }

    /**
     * Returns the chart rendering info.
     *
     * @return the chart rendering info.
     */
    public ChartRenderingInfo getInfo() {
        return this.info;
    }

    /**
     * Sets the chart rendering info.
     *
     * @param info  the chart rendering info.
     */
    public void setInfo(ChartRenderingInfo info) {
        this.info = info;
    }

    /**
     * Adds a property change listener to the renderer.
     *
     * @param listener  the listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener from the renderer.
     *
     * @param listener  the listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.removePropertyChangeListener(listener);
    }

    /**
     * Notifies registered listeners that a property of the renderer has changed.
     *
     * @param propertyName  the name of the property.
     * @param oldValue  the old value.
     * @param newValue  the new value.
     */
    protected void firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
        listeners.firePropertyChange(propertyName, oldValue, newValue);
    }

}
