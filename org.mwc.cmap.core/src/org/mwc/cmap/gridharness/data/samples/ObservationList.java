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
package org.mwc.cmap.gridharness.data.samples;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.mwc.cmap.core.property_support.DoubleHelper;
import org.mwc.cmap.core.property_support.WorldDistanceHelper;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableSeries;
import org.mwc.cmap.gridharness.data.WorldDistance2;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;

public class ObservationList implements GriddableSeries
{

	private GriddableItemDescriptor[] _myAttributes;

	private final Vector<TimeStampedDataItem> _myData;

	private final List<TimeStampedDataItem> _myDataRO;

	private final String _myName;

	private final java.beans.PropertyChangeSupport _pSupport;

	@SuppressWarnings("unchecked")
	public ObservationList(final String name, final Vector<TimeStampedDataItem> positions)
	{
		_myName = name;
		_myData = (Vector<TimeStampedDataItem>) positions.clone();
		_myDataRO = Collections.unmodifiableList(_myData);
		_pSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener)
	{
		_pSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(listener);
	}

	public void makeSubtleChange()
	{
		// choose object fairly near top
		final int range = Math.min(_myData.size(), 10);
		final Observation thisP = (Observation) _myData
				.elementAt((int) (Math.random() * range));
		thisP.setBearing(thisP.getBearing() + 1.4 * (Math.random() > 0.5 ? 1 : -1));
		_pSupport.firePropertyChange(PROPERTY_CHANGED, null, thisP);
	}

	public void deleteItem(final TimeStampedDataItem subject)
	{
		final int index = _myData.indexOf(subject);
		if (index < 0)
		{
			throw new NoSuchElementException();
		}
		_myData.remove(subject);
		_pSupport.firePropertyChange(PROPERTY_DELETED, index, subject);
	}

	public void insertItem(final TimeStampedDataItem subject)
	{
		insertItemAt(subject, getItems().size());
	}

	public void insertItemAt(final TimeStampedDataItem subject, final int index)
	{
		_myData.add(index, subject);
		_pSupport.firePropertyChange(PROPERTY_ADDED, index, subject);
	}

	public void fireModified(final TimeStampedDataItem subject)
	{
		_pSupport.firePropertyChange(PROPERTY_CHANGED, null, subject);
	}

	public void fireReformatted(final TimeStampedDataItem subject)
	{
		_pSupport.firePropertyChange(PROPERTY_CHANGED, null, subject);
	}

	public GriddableItemDescriptor[] getAttributes()
	{
		if (_myAttributes == null)
		{
			_myAttributes = new GriddableItemDescriptor[2];
			_myAttributes[0] = new GriddableItemDescriptor("Bearing", "Bearing",
					double.class, new DoubleHelper());
			_myAttributes[1] = new GriddableItemDescriptor("Range", "Range",
					WorldDistance2.class, new WorldDistanceHelper());
		}
		return _myAttributes;
	}

	public List<TimeStampedDataItem> getItems()
	{
		return _myDataRO;
	}

	public String getName()
	{
		return _myName;
	}

	public TimeStampedDataItem makeCopy(final TimeStampedDataItem item)
	{
		if (false == item instanceof Observation)
		{
			throw new IllegalArgumentException(
					"I am expecting the Observation's, don't know how to copy " + item);
		}
		final Observation template = (Observation) item;
		final Observation result = new Observation();
		result.setRange(new WorldDistance2(template.getRange()));
		result.setBearing(template.getBearing());
		result.setTime(new HiResDate(template.getTime()));
		return result;
	}

	public String toString()
	{
		return getName();
	}

	public static ObservationList getShortSample(final PropertyChangeListener pcl)
	{
		final Vector<TimeStampedDataItem> pts = new Vector<TimeStampedDataItem>();
		pts.add(new Observation(new HiResDate(10000), 12, new WorldDistance2(12,
				WorldDistance2.METRES), Color.green));
		pts.add(new Observation(new HiResDate(20000), 10, new WorldDistance2(14,
				WorldDistance2.METRES), Color.green));
		pts.add(new Observation(new HiResDate(30000), 11, new WorldDistance2(15,
				WorldDistance2.METRES), Color.green));
		pts.add(new Observation(new HiResDate(45000), 11, new WorldDistance2(16,
				WorldDistance2.METRES), Color.green));
		pts.add(new Observation(new HiResDate(50000), 10, new WorldDistance2(18,
				WorldDistance2.METRES), Color.green));
		final ObservationList res = new ObservationList("Short Obs", pts);
		res.addPropertyChangeListener(pcl);
		return res;
	}

	public static ObservationList getLongSample(final PropertyChangeListener pcl)
	{
		final Vector<TimeStampedDataItem> pts = new Vector<TimeStampedDataItem>();

		for (int i = 0; i < 3000; i++)
		{
			pts.add(new Observation(new HiResDate(i * 10000), 12, new WorldDistance2(
					2d + Math.random() * 600, WorldDistance2.METRES), Color.green));
		}
		final ObservationList res = new ObservationList("Long Obs", pts);
		res.addPropertyChangeListener(pcl);
		return res;
	}

	public void setOnlyShowVisibleItems(final boolean val)
	{

	}

}
