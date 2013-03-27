package com.planetmayo.debrief.satc.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public abstract class ModelObject implements Serializable
{
	private static final long serialVersionUID = 1L;

	private transient PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	/** note: we provide this method so that we can correctly initialise the 
	 * transient changeSupport object when we're deserialising a model object
	 * 
	 * @return this
	 */
	private Object readResolve()
	{
		changeSupport = new PropertyChangeSupport(this);
		return this;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public int getPropertyChangeListenersCount()
	{
		return changeSupport.getPropertyChangeListeners().length;
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue)
	{
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		changeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
