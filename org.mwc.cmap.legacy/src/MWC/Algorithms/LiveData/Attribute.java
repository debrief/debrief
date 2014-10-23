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
package MWC.Algorithms.LiveData;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Vector;


public class Attribute implements IAttribute
{

	public static final String VALUE = "VALUE";

	/**
	 * name of this attribute
	 * 
	 */
	private final String _name;
	
	/** the units for this attribute type
	 * 
	 */
	private final String _units;

	/**
	 * support for property listeners
	 * 
	 */
	private final PropertyChangeSupport _pSupport;

	/** whether this attribute is worthy of display by default
	 * 
	 */
	private final boolean _isSignificant;
	
	/** daddies little helper
	 * 
	 */
	private final AttributeHelper _myHelper;

	/**
	 * constructor for an instance
	 * 
	 * @param name
	 */
	public Attribute(final String name, final String units, final boolean isSignificant)
	{
		_isSignificant = isSignificant;
		
		// remember the name
		_name = name;
		_units = units;

		// get the support ready
		_pSupport = new PropertyChangeSupport(this);

		// get our helper ready.
		_myHelper = new AttributeHelper(_pSupport);

	}

	public void addPropertyChangeListener(final PropertyChangeListener listener)
	{
		_pSupport.addPropertyChangeListener(listener);
	}

	public String getName()
	{
		return _name;
	}

	public Vector<DataDoublet> getHistoricValues(final Object index)
	{
		return _myHelper.getValuesFor(index);
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(listener);
	}

	/** this has changed, store it & tell the listeners
	 * 
	 * @param time
	 * @param newValue
	 */
	public void fireUpdate(final Object index, final long time, final Object newValue)
	{
		_myHelper.newData(index, time, newValue);
	}

	public DataDoublet getCurrent(final Object index)
	{
		return _myHelper.getCurrent(index);
	}

	public boolean isSignificant()
	{
		return _isSignificant;
	}

	/** return the current value of the specified index as a string
	 * 
	 * @param index
	 * @return
	 */
	public String toString(final Object index)
	{
		return getName() + ":" + getCurrent(index).getValue();
	}
	
	/** convenience class to help objects that can't inherit from Attribute class
	 * 
	 * @author ianmayo
	 *
	 */
	public static class AttributeHelper
	{
    
    /** the property listeners for this object
     * 
     */
    private final PropertyChangeSupport _pSupport;

		/** the collection of data items
		 * 
		 */
    private HashMap<Object, Vector<DataDoublet>> _indexedData;
		
    /** constructor, to get us moving
     * 
     * @param pSupport
     */
		public AttributeHelper(final PropertyChangeSupport pSupport)
		{
			_pSupport = pSupport;
		}
		
		/** store the new data update
		 * 
		 * @param time
		 * @param value
		 */
		public void newData(final Object index, final long time, final Object value)
		{		
			final DataDoublet newD = new DataDoublet(time, value);
			final DataDoublet oldD = getCurrent(index);

			// store the new value
			getValuesFor(index).add(newD);
			
			// and tell the chaps
			final PropertyChangeEvent pe = new PropertyChangeEvent(index, VALUE, oldD, newD);
			_pSupport.firePropertyChange(pe);
			
		}
		
		public Vector<DataDoublet> getValuesFor(final Object index)
		{
			if(_indexedData == null)
				_indexedData = new HashMap<Object, Vector<DataDoublet>>();
			
			Vector<DataDoublet> res = _indexedData.get(index);
			
			if(res == null)
			{
				res = new Vector<DataDoublet>();
				_indexedData.put(index, res);
			}
			return res;
				
		}
		
		/** find the most recent data item
		 * 
		 * @return
		 */
		public DataDoublet getCurrent(final Object index)
		{
			DataDoublet res = null;
			if(getValuesFor(index).size() > 0)
				res = getValuesFor(index).lastElement();
			
			return res;
		}

	}

	public String getUnits()
	{
		return _units;
	}

}
