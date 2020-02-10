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
package Debrief.Wrappers.Extensions.Measurements.Wrappers;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.HasEditables;

public class FolderWrapper implements Editable, HasEditables, Serializable, DataItemWrapper {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	final private DataFolder _folder;

	public FolderWrapper(final DataFolder folder) {
		_folder = folder;
	}

	@Override
	@FireExtended
	public void add(final Editable item) {
		if (item instanceof FolderWrapper) {
			final FolderWrapper df = (FolderWrapper) item;
			_folder.add(df._folder);
		} else if (item instanceof DatasetWrapper) {
			final DatasetWrapper dw = (DatasetWrapper) item;
			_folder.add(dw._data);
		}
	}

	@Override
	public Enumeration<Editable> elements() {
		final Vector<Editable> res = new Vector<Editable>();

		// get the folder contents
		final List<Editable> items = MeasuredDataProvider.getItemsFor(_folder);

		// put into our vector
		res.addAll(items);

		return res.elements();
	}

	@Override
	public boolean equals(final Object arg0) {
		if (arg0 instanceof FolderWrapper) {
			final FolderWrapper other = (FolderWrapper) arg0;
			return _folder.equals(other._folder);
		} else
			return super.equals(arg0);
	}

	@Override
	public DataItem getDataItem() {
		return _folder;
	}

	@Override
	public EditorType getInfo() {
		return null;
	}

	@Override
	public String getName() {
		return _folder.getName();
	}

	@Override
	public boolean hasEditor() {
		return false;
	}

	@Override
	public int hashCode() {
		return _folder.hashCode();
	}

	@Override
	public boolean hasOrderedChildren() {
		return false;
	}

	@Override
	@FireExtended
	public void removeElement(final Editable item) {
		if (item instanceof FolderWrapper) {
			final FolderWrapper df = (FolderWrapper) item;
			_folder.remove(df._folder);
		} else if (item instanceof DatasetWrapper) {
			final DatasetWrapper dw = (DatasetWrapper) item;
			_folder.remove(dw._data);
		}
	}

	@Override
	public String toString() {
		return getName() + " (" + _folder.size() + " items)";
	}
}