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
import org.mwc.cmap.core.property_support.WorldSpeedHelper;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptorExtension;
import org.mwc.cmap.gridharness.data.GriddableSeries;
import org.mwc.cmap.gridharness.data.WorldLocation;
import org.mwc.cmap.gridharness.data.WorldSpeed2;
import org.mwc.cmap.gridharness.views.WorldLocationHelper;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;


public class PositionList implements GriddableSeries {

	private GriddableItemDescriptor[] _myAttributes;

	private final Vector<TimeStampedDataItem> _myData;

	private final List<TimeStampedDataItem> _myDataRO;

	private final String _myName;

	private final java.beans.PropertyChangeSupport _pSupport;

	@SuppressWarnings("unchecked")
	public PositionList(final String name, final Vector<TimeStampedDataItem> positions) {
		_myName = name;
		_myData = (Vector<TimeStampedDataItem>) positions.clone();
		_myDataRO = Collections.unmodifiableList(_myData);
		_pSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		_pSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		_pSupport.removePropertyChangeListener(listener);
	}

	public void makeSubtleChange() {
		// choose object fairly near top
		final int range = Math.min(_myData.size(), 10);
		final Position thisP = (Position) _myData.elementAt((int) (Math.random() * range));
		thisP.setCourse(thisP.getCourse() + 20 * (Math.random() < 0.5d ? 1 : -1));

		_pSupport.firePropertyChange(PROPERTY_CHANGED, null, thisP);
	}

	public void insertItem(final TimeStampedDataItem subject) {
		insertItemAt(subject, getItems().size());
	}

	public void insertItemAt(final TimeStampedDataItem subject, final int index) {
		_myData.add(index, subject);
		_pSupport.firePropertyChange(PROPERTY_ADDED, index, subject);
	}

	public void deleteItem(final TimeStampedDataItem subject) {
		final int index = _myData.indexOf(subject);
		if (index < 0) {
			throw new NoSuchElementException();
		}
		_myData.remove(subject);
		_pSupport.firePropertyChange(PROPERTY_DELETED, index, subject);
	}

	public void fireModified(final TimeStampedDataItem subject) {
		_pSupport.firePropertyChange(PROPERTY_CHANGED, null, subject);
	}
	
	public void fireReformatted(final TimeStampedDataItem subject) {
		_pSupport.firePropertyChange(PROPERTY_CHANGED, null, subject);
	}


	public GriddableItemDescriptor[] getAttributes() {
		if (_myAttributes == null) {
			_myAttributes = new GriddableItemDescriptor[5];
			_myAttributes[0] = new GriddableItemDescriptor("Latitude", "Lat", double.class, new DoubleHelper());
			_myAttributes[1] = new GriddableItemDescriptor("Longitude", "Long", double.class, new DoubleHelper());
			_myAttributes[2] = new GriddableItemDescriptor("Course", "Course", double.class, new DoubleHelper());
			_myAttributes[3] = new GriddableItemDescriptor("Speed", "Speed", WorldSpeed2.class, new WorldSpeedHelper());

			final WorldLocationHelper worldLocationHelper = new WorldLocationHelper();
			final WorldLocation sample = new WorldLocation();
			final String sampleLocationText = worldLocationHelper.getLabelFor(sample).getText(sample);
			_myAttributes[4] = new GriddableItemDescriptorExtension("Location", "Location", WorldLocation.class, new WorldLocationHelper(), //
					sampleLocationText);
		}
		return _myAttributes;
	}

	public List<TimeStampedDataItem> getItems() {
		return _myDataRO;
	}

	public String getName() {
		return _myName;
	}

	public TimeStampedDataItem makeCopy(final TimeStampedDataItem item) {
		if (false == item instanceof Position) {
			throw new IllegalArgumentException("I am expecting the Position's, don't know how to copy " + item);
		}
		final Position template = (Position) item;
		final Position result = new Position();
		result.setCourse(template.getCourse());
		result.setLatitude(template.getLatitude());
		result.setLongitude(template.getLongitude());
		result.setSpeed(new WorldSpeed2(template.getSpeed()));
		result.setTime(new HiResDate(template.getTime()));
		return result;
	}

	public String toString() {
		return getName();
	}

	public static PositionList getShortSample(final PropertyChangeListener pcl) {
		final Vector<TimeStampedDataItem> pts = new Vector<TimeStampedDataItem>();
		pts.add(new Position(new HiResDate(10000), 12, 11, 140, new WorldSpeed2(12, WorldSpeed2.M_sec), Color.yellow));
		pts.add(new Position(new HiResDate(20000), 13, 11, 121, new WorldSpeed2(13, WorldSpeed2.M_sec), Color.yellow));
		pts.add(new Position(new HiResDate(30000), 15, 15, 111, new WorldSpeed2(14, WorldSpeed2.M_sec), Color.yellow));
		pts.add(new Position(new HiResDate(40000), 16, 13, 101, new WorldSpeed2(14, WorldSpeed2.M_sec), Color.yellow));
		pts.add(new Position(new HiResDate(50000), 17, 15, 162, new WorldSpeed2(12, WorldSpeed2.M_sec), Color.yellow));
		final PositionList res = new PositionList("Short Pos", pts);
		res.addPropertyChangeListener(pcl);
		return res;
	}

	public static PositionList getLongSample(final PropertyChangeListener pcl) {
		final Vector<TimeStampedDataItem> pts = new Vector<TimeStampedDataItem>();

		for (int i = 0; i < 3000; i++) {
			pts.add(new Position(new HiResDate(10000 * i), 12d, 11, 140, new WorldSpeed2(12, WorldSpeed2.M_sec), Color.yellow));
		}
		final PositionList res = new PositionList("Long Pos", pts);
		res.addPropertyChangeListener(pcl);
		return res;
	}

	public void setOnlyShowVisibleItems(final boolean val)
	{
		
	}

}
