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
package Debrief.Wrappers.Extensions.Measurements;

import java.io.Serializable;
import java.util.Iterator;

abstract public class TimeSeriesCore implements Serializable, DataItem {

	/**
	 * value used to indicate an invalid index
	 *
	 */
	public static final int INVALID_INDEX = -1;

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * track the parent folder. It's transient since the parent gets assigned as
	 * part of XML restore
	 */
	private transient DataFolder _parent;
	protected final String _units;

	public TimeSeriesCore(final String units) {
		_units = units;
	}

	abstract public int getIndexNearestTo(long time);

	abstract public Iterator<Long> getIndices();

	@Override
	abstract public String getName();

	@Override
	public DataFolder getParent() {
		return _parent;
	}

	public String getPath() {
		String name = "";
		DataFolder parent = this.getParent();
		while (parent != null) {
			name = parent.getName() + " // " + name;
			parent = parent.getParent();
		}
		name += getName();
		return name;
	}

	public String getUnits() {
		return _units;
	}

	/**
	 * sometimes it's useful to know the parent folder for a dataset
	 *
	 * @param parent
	 */
	@Override
	public void setParent(final DataFolder parent) {
		_parent = parent;
	}

	/**
	 * get the the number of elements in this time series
	 *
	 * @return
	 */
	abstract public int size();

	/**
	 * convenience function, to describe this plottable as a string
	 */
	@Override
	public String toString() {
		return getName() + " (" + size() + " items)";
	}
}
