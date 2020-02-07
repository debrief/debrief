/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package com.planetmayo.debrief.satc.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public abstract class ModelObject implements Serializable {
	private static final long serialVersionUID = 1L;

	private transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		initListeners();

		changeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		initListeners();

		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		initListeners();
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public int getPropertyChangeListenersCount() {
		initListeners();

		return changeSupport.getPropertyChangeListeners().length;
	}

	private void initListeners() {
		if (changeSupport == null)
			changeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * note: we provide this method so that we can correctly initialise the
	 * transient changeSupport object when we're deserialising a model object
	 *
	 * @return this
	 */
	private Object readResolve() {
		initListeners();
		return this;
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		initListeners();
		changeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		initListeners();
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
