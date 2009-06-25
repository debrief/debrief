package org.mwc.cmap.gridharness.data.samples;

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


public class ObservationList implements GriddableSeries {

	private GriddableItemDescriptor[] _myAttributes;

	private Vector<TimeStampedDataItem> _myData;

	private final List<TimeStampedDataItem> _myDataRO;

	private String _myName;

	private java.beans.PropertyChangeSupport _pSupport;

	@SuppressWarnings("unchecked")
	public ObservationList(String name, Vector<TimeStampedDataItem> positions) {
		_myName = name;
		_myData = (Vector) positions.clone();
		_myDataRO = Collections.unmodifiableList(_myData);
		_pSupport = new PropertyChangeSupport(this);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_pSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_pSupport.removePropertyChangeListener(listener);
	}

	public void makeSubtleChange() {
		// choose object fairly near top
		int range = Math.min(_myData.size(), 10);
		Observation thisP = (Observation) _myData.elementAt((int) (Math.random() * range));
		thisP.setBearing(thisP.getBearing() + 1.4 * (Math.random() > 0.5 ? 1 : -1));
		_pSupport.firePropertyChange(PROPERTY_CHANGED, null, thisP);
	}

	@Override
	public void deleteItem(TimeStampedDataItem subject) {
		int index = _myData.indexOf(subject);
		if (index < 0) {
			throw new NoSuchElementException();
		}
		_myData.remove(subject);
		_pSupport.firePropertyChange(PROPERTY_DELETED, index, subject);
	}

	@Override
	public void insertItem(TimeStampedDataItem subject) {
		insertItemAt(subject, getItems().size());
	}

	@Override
	public void insertItemAt(TimeStampedDataItem subject, int index) {
		_myData.add(index, subject);
		_pSupport.firePropertyChange(PROPERTY_ADDED, index, subject);
	}

	@Override
	public void fireModified(TimeStampedDataItem subject) {
		_pSupport.firePropertyChange(PROPERTY_CHANGED, null, subject);
	}

	@Override
	public GriddableItemDescriptor[] getAttributes() {
		if (_myAttributes == null) {
			_myAttributes = new GriddableItemDescriptor[2];
			_myAttributes[0] = new GriddableItemDescriptor("Bearing", "Bearing", double.class, new DoubleHelper());
			_myAttributes[1] = new GriddableItemDescriptor("Range", "Range", WorldDistance2.class, new WorldDistanceHelper());
		}
		return _myAttributes;
	}

	@Override
	public List<TimeStampedDataItem> getItems() {
		return _myDataRO;
	}

	@Override
	public String getName() {
		return _myName;
	}

	@Override
	public TimeStampedDataItem makeCopy(TimeStampedDataItem item) {
		if (false == item instanceof Observation) {
			throw new IllegalArgumentException("I am expecting the Observation's, don't know how to copy " + item);
		}
		Observation template = (Observation) item;
		Observation result = new Observation();
		result.setRange(new WorldDistance2(template.getRange()));
		result.setBearing(template.getBearing());
		result.setTime(new HiResDate(template.getTime()));
		return result;
	}

	public String toString() {
		return getName();
	}

	public static ObservationList getShortSample(PropertyChangeListener pcl) {
		Vector<TimeStampedDataItem> pts = new Vector<TimeStampedDataItem>();
		pts.add(new Observation(new HiResDate(10000), 12, new WorldDistance2(12, WorldDistance2.METRES)));
		pts.add(new Observation(new HiResDate(20000), 10, new WorldDistance2(14, WorldDistance2.METRES)));
		pts.add(new Observation(new HiResDate(30000), 11, new WorldDistance2(15, WorldDistance2.METRES)));
		pts.add(new Observation(new HiResDate(45000), 11, new WorldDistance2(16, WorldDistance2.METRES)));
		pts.add(new Observation(new HiResDate(50000), 10, new WorldDistance2(18, WorldDistance2.METRES)));
		ObservationList res = new ObservationList("Short Obs", pts);
		res.addPropertyChangeListener(pcl);
		return res;
	}

	public static ObservationList getLongSample(PropertyChangeListener pcl) {
		Vector<TimeStampedDataItem> pts = new Vector<TimeStampedDataItem>();

		for (int i = 0; i < 3000; i++) {
			pts.add(new Observation(new HiResDate(i * 10000), 12, new WorldDistance2(2d + Math.random() * 600, WorldDistance2.METRES)));
		}
		ObservationList res = new ObservationList("Long Obs", pts);
		res.addPropertyChangeListener(pcl);
		return res;
	}

	@Override
	public void setOnlyShowVisibleItems(boolean val)
	{
		// TODO Auto-generated method stub
		
	}

}
