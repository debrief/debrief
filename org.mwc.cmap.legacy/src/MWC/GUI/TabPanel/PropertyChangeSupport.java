/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.TabPanel;

/*
 * @(#)PropertyChangeSupport.java	1.0 5/6/97
 *
 * Copyright (c) 1997 Symantec, Inc. All Rights Reserved.
 *
 */

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;



//	05/06/97	LAB	Created

/**
 * This is a utility class that can be used by beans that support bound
 * properties.  You can either inherit from this class or you can use
 * an instance of this class as a member field of your bean and delegate
 * various work to it.
 * <p>
 * This extension of the java.beans.PropertyChangeSupport class adds
 * functionality to handle individual property changes.
 *
 * @author Symantec
 */
public class PropertyChangeSupport extends java.beans.PropertyChangeSupport
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		/**
     * Constructs a PropertyChangeSupport object.
     * @param sourceBean the bean to be given as the source for any events
     */
    public PropertyChangeSupport(final Object sourceBean)
    {
		super(sourceBean);
		source = sourceBean;
    }

    /**
     * Adds a PropertyChangeListener to the listener list.
     *
     * @param propertyName the name of the property to add a listener for
     * @param listener the PropertyChangeListener to be added
     * @see #removePropertyChangeListener
     */
    public synchronized void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
    {
    	java.util.Vector<PropertyChangeListener> listenerList;

    	if(listenerTable == null)
    	{
    		listenerTable = new java.util.Hashtable<String, Vector<PropertyChangeListener>>();
    	}

    	if(listenerTable.containsKey(propertyName))
    	{
    		listenerList = listenerTable.get(propertyName);
    	}
    	else
    	{
    		listenerList = new java.util.Vector<PropertyChangeListener>();
    	}

    	listenerList.addElement(listener);
    	listenerTable.put(propertyName, listenerList);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     *
     * @param propertyName the name of the property to remove a listener for
     * @param listener the PropertyChangeListener to be removed
     * @see #addPropertyChangeListener
     */
    public synchronized void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
    {
    	java.util.Vector<PropertyChangeListener> listenerList;

		if (listenerTable == null || !listenerTable.containsKey(propertyName))
		{
	    	return;
		}
		listenerList = listenerTable.get(propertyName);
		listenerList.removeElement(listener);
    }

    /**
     * Report a bound property update to any registered listeners.
     * <p>
     * No event is fired if old and new are equal and non-null.
     *
     * @param propertyName the programmatic name of the property
     *		that was changed
     * @param oldValue the old value of the property
     * @param newValue the new value of the property
     */
    @SuppressWarnings("unchecked")
		public void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue)
    {
    	if (oldValue != null && oldValue.equals(newValue))
    	{
    		return;
		}

		super.firePropertyChange(propertyName, oldValue, newValue);

		java.util.Hashtable<String, Vector<PropertyChangeListener>> templistenerTable = null;

		synchronized (this)
		{
			if(listenerTable == null || !listenerTable.containsKey(propertyName))
			{
				return;
			}
		  	templistenerTable = (Hashtable<String, Vector<PropertyChangeListener>>) listenerTable.clone();
		}

		java.util.Vector<PropertyChangeListener> listenerList;

		listenerList = templistenerTable.get(propertyName);

	    final PropertyChangeEvent evt = new PropertyChangeEvent(source, propertyName, oldValue, newValue);

		for (int i = 0; i < listenerList.size(); i++)
		{
			final PropertyChangeListener target = listenerList.elementAt(i);
		    target.propertyChange(evt);
		}
    }

	/* !!! LAB !!!	05/06/97
	If we want to support non-serializable listeners we will have to
	implement the folowing functions and serialize out the listenerTable
	HashTable on our own.

    private void writeObject(ObjectOutputStream s) throws IOException
    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException
    */

    /**
     * The listener list.
     * @see #addPropertyChangeListener
     * @see #removePropertyChangeListener
     */
    protected java.util.Hashtable<String, Vector<PropertyChangeListener>> listenerTable;
    private final Object source;
}




