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
 * -----------
 * Series.java
 * -----------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: Series.java,v 1.1.1.1 2003/07/17 10:06:55 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 15-Nov-2001 : Version 1 (DG);
 * 29-Nov-2001 : Added cloning and property change support (DG);
 * 30-Jan-2002 : Added a description attribute and changed the constructors to protected (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 */

package com.jrefinery.legacy.data;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Iterator;

/**
 * Base class representing a data series.  Subclasses are left to implement the
 * actual data structures.
 * <P>
 * The series has two properties ("Name" and "Description") for which you can
 * register a PropertyChangeListener.
 * <P>
 * You can also register a SeriesChangeListener to receive notification of
 * changes to the series data.
 *
 * @author DG
 */
public class Series implements Cloneable {

    /** The name of the series. */
    private String name;

    /** A description of the series. */
    private String description;

    /** Storage for registered change listeners. */
    private List<SeriesChangeListener> listeners;

    /** Object to support property change notification. */
    private PropertyChangeSupport propertyChangeSupport;

    /**
     * Constructs a series.
     *
     * @param name The series name.
     */
    protected Series(String name) {
        this(name, null);
    }

    /**
     * Constructs a series.
     *
     * @param name  the series name.
     * @param description  the series description.
     */
    protected Series(String name, String description) {

        this.name = name;
        this.description = description;
        this.listeners = new java.util.ArrayList<SeriesChangeListener>();
        propertyChangeSupport = new PropertyChangeSupport(this);

    }

    /**
     * Returns the name of the series.
     *
     * @return the name of the series.
     */
    public String getName() {

        return this.name;

    }

    /**
     * Sets the name of the series.
     *
     * @param name the name.
     */
    public void setName(String name) {

        String old = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange("Name", old, name);

    }

    /**
     * Returns the description of the series (possibly null).
     *
     * @return the description of the series.
     */
    public String getDescription() {

        return this.description;

    }

    /**
     * Sets the description of the series.
     *
     * @param description the new description (null permitted).
     */
    public void setDescription(String description) {

        String old = this.description;
        this.description = description;
        propertyChangeSupport.firePropertyChange("Description", old, description);

    }

    /**
     * Returns a clone of the series.
     * <P>
     * Notes:
     * 1.  No need to clone the name or description, since String object is immutable.
     * 2.  We set the listener list to empty, since the listeners did not register with the clone.
     * 3.  Same applies to the PropertyChangeSupport instance.
     *
     * @return a clone of the series.
     */
    public Object clone() {

        Object obj = null;

        try {
            obj = super.clone();
        }
        catch (CloneNotSupportedException e) { // won't get here...
            System.err.println("Series.clone(): unexpected exception.");
        }

        Series clone = (Series) obj;
        clone.listeners = new java.util.ArrayList<SeriesChangeListener>();
        clone.propertyChangeSupport = new PropertyChangeSupport(clone);

        return clone;

    }

    /**
     * Registers an object with this series, to receive notification whenever
     * the series changes.
     * <P>
     * Objects being registered must implement the SeriesChangeListener
     * interface.
     *
     * @param listener The object to register.
     */
    public void addChangeListener(SeriesChangeListener listener) {

        this.listeners.add(listener);

    }

    /**
     * Deregisters an object, so that it not longer receives notification
     * whenever the series changes.
     * <P>
     * Call this method when an object no longer needs to be notified of
     * changes to the series.
     *
     * @param listener The object to deregister.
     */
    public void removeChangeListener(SeriesChangeListener listener) {

        this.listeners.remove(listener);

    }

    /**
     * General method for signalling to registered listeners that the series
     * has been changed.
     */
    public void fireSeriesChanged() {

        notifyListeners(new SeriesChangeEvent(this));

    }

    /**
     * Sends a change event to all registered listeners.
     *
     * @param event Contains information about the event that triggered the notification.
     */
    protected void notifyListeners(SeriesChangeEvent event) {

        Iterator<SeriesChangeListener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            SeriesChangeListener listener = iterator.next();
            listener.seriesChanged(event);
        }

    }

    /**
     * Adds a property change listener to the series.
     *
     * @param listener The listener.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener from the series.
     *
     * @param listener The listener.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Fires a property change event.
     *
     * @param property  the property key.
     * @param oldValue  the old value.
     * @param newValue  the new value.
     */
    protected void firePropertyChange(String property, Object oldValue, Object newValue) {
       this.propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
    }

}
