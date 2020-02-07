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
import java.util.ArrayList;

/**
 * hold a set of datasets, or child stores
 *
 * @author ian
 *
 */
public class DataFolder extends ArrayList<DataItem> implements DataItem, Serializable {
	public static interface DatasetOperator {
		void process(TimeSeriesCore dataset);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_NAME = "Additional data";

	private String _name;

	private DataFolder _parent;

	public DataFolder() {
		this(DEFAULT_NAME);
	}

	public DataFolder(final String name) {
		_name = name;
	}

	@Override
	public boolean add(final DataItem item) {
		// hey, set the parent folder for it
		item.setParent(this);

		// and handle the store
		return super.add(item);
	}

	public DataItem get(final String string) {
		DataItem res = null;
		for (final DataItem item : this) {
			if (item.getName().equals(string)) {
				res = item;
				break;
			}
		}
		return res;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public DataFolder getParent() {
		return _parent;
	}

	@Override
	public void printAll() {
		System.out.println("==" + _name);
		for (final DataItem item : this) {
			item.printAll();
		}
	}

	@Override
	public boolean remove(final Object item) {
		// clear the subject's parent link
		if (item instanceof DataItem) {
			final DataItem di = (DataItem) item;
			di.setParent(null);
		}

		// ok, do the remove
		return super.remove(item);
	}

	@Override
	public void setName(final String name) {
		_name = name;
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

	public void walkThisDataset(final DatasetOperator operator) {
		for (final DataItem item : this) {
			if (item instanceof TimeSeriesCore) {
				final TimeSeriesCore ts = (TimeSeriesCore) item;
				operator.process(ts);
			} else if (item instanceof DataFolder) {
				final DataFolder folder = (DataFolder) item;
				folder.walkThisDataset(operator);
			}
		}
	}
}
