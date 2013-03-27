package com.planetmayo.debrief.satc.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public abstract class ModelObject implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	private void initListeners()
	{
		if (changeSupport == null)
			changeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * note: we provide this method so that we can correctly initialise the
	 * transient changeSupport object when we're deserialising a model object
	 * 
	 * @return this
	 */
	private Object readResolve()
	{
		initListeners();
		return this;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		initListeners();

		changeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		initListeners();

		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public int getPropertyChangeListenersCount()
	{
		initListeners();

		return changeSupport.getPropertyChangeListeners().length;
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue)
	{
		initListeners();
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		initListeners();
		changeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		initListeners();
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
