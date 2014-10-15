/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI;

public interface SupportsPropertyListeners
{

	public static final String FORMAT = "Format";
	public static final String EXTENDED = "Extended";

	public void addPropertyChangeListener(String property,
			java.beans.PropertyChangeListener listener);

	/**
	 * add this listener which wants to hear about all changes
	 */
	public void addPropertyChangeListener(
			java.beans.PropertyChangeListener listener);

	/**
	 * remove this listener which wants to hear about all changes
	 */
	public void removePropertyChangeListener(
			java.beans.PropertyChangeListener listener);

	public void removePropertyChangeListener(String property,
			java.beans.PropertyChangeListener listener);

	/**
	 * something has changed - tell everybody
	 * 
	 * @param propertyChanged
	 * @param oldValue
	 * @param newValue
	 */
	public void firePropertyChange(String propertyChanged, Object oldValue,
			Object newValue);

}