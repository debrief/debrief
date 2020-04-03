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

package org.mwc.debrief.pepys.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.mwc.debrief.pepys.model.tree.TreeStructurable;

/**
 * Class used for the Data Type Filtering Model Item.
 *
 */
public class TypeDomain {
	public static String CHECKED_PROPERTY = "CHECKED_CHANGED";
	private final Class<TreeStructurable> datatype;
	private final String name;
	private final String imagePath;

	private boolean checked;

	private PropertyChangeSupport _pSupport = null;

	public TypeDomain(final Class datatype, final String name, final boolean checked, final String _imagePath) {
		super();
		this.datatype = datatype;
		this.name = name;
		this.checked = checked;
		this.imagePath = _imagePath;
	}

	public void addPropertyChangeListener(final PropertyChangeListener l) {
		if (_pSupport == null) {
			_pSupport = new PropertyChangeSupport(this);
		}
		_pSupport.addPropertyChangeListener(l);
	}

	public Class<TreeStructurable> getDatatype() {
		return datatype;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getName() {
		return name;
	}

	public boolean isChecked() {
		return checked;
	}

	public void removeAllPropertyChangeListeners() {
		_pSupport = null;
	}

	public void removePropertyChangeListener(final PropertyChangeListener l) {
		if (_pSupport != null) {
			_pSupport.removePropertyChangeListener(l);
		}
	}

	public void setChecked(final boolean checked) {
		final boolean oldValue = this.checked;
		this.checked = checked;

		if (_pSupport != null) {
			final java.beans.PropertyChangeEvent pce = new PropertyChangeEvent(this, CHECKED_PROPERTY, oldValue,
					checked);
			_pSupport.firePropertyChange(pce);
		}
	}

}
