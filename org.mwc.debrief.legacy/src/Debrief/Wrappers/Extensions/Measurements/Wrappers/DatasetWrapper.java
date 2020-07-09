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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import Debrief.Wrappers.Extensions.Measurements.DataItem;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import junit.framework.TestCase;

public class DatasetWrapper implements Editable, Serializable, DataItemWrapper {

	// ////////////////////////////////////////////////////
// bean info for this class
// ///////////////////////////////////////////////////
	public final class DatasetWrapperInfo extends Editable.EditorType {

		public DatasetWrapperInfo(final DatasetWrapper data, final String theName) {
			super(data, theName, data.toString());
		}

		@Override
		public final PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] myRes = {
						displayProp("ItemCount", "Number of items", "Number of items in this dataset", FORMAT),
						displayProp("Units", "Units for data", "Units for this dataset", FORMAT),
						displayProp("Name", "Name", "Name for this dataset", FORMAT) };

				return myRes;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	public static class TestSerialize extends TestCase {
		public void testSerialise() {
			final long[] times = new long[] { 12L, 14L };
			final double[] values = new double[] { 100d, 200d };

			final TimeSeriesDatasetDouble original = new TimeSeriesDatasetDouble("Data", "Seconds", times, values);

			final DatasetWrapper wrapper = new DatasetWrapper(original);

			try {
				final java.io.ByteArrayOutputStream bas = new ByteArrayOutputStream();
				final java.io.ObjectOutputStream oos = new ObjectOutputStream(bas);
				oos.writeObject(wrapper);
				// get closure
				oos.close();
				bas.close();

				// now get the item
				final byte[] bt = bas.toByteArray();

				// and read it back in as a new item
				final java.io.ByteArrayInputStream bis = new ByteArrayInputStream(bt);

				// create the reader
				final java.io.ObjectInputStream iis = new ObjectInputStream(bis);

				// and read it in
				final Object oj = iis.readObject();

				// get more closure
				bis.close();
				iis.close();

				final DatasetWrapper clone = (DatasetWrapper) oj;

				clone._data.printAll();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	     *
	     */
	private static final long serialVersionUID = 1L;

	final TimeSeriesCore _data;

	private transient DatasetWrapperInfo _myEditor = null;

	public DatasetWrapper(final TimeSeriesCore folder) {
		_data = folder;
	}

	@Override
	public DataItem getDataItem() {
		return _data;
	}

	public TimeSeriesCore getDataset() {
		return _data;
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new DatasetWrapperInfo(this, this.getName());

		return _myEditor;
	}

	public String getItemCount() {
		return "" + _data.size();
	}

	@Override
	public String getName() {
		return _data.getName();
	}

	public String getUnits() {
		return _data.getUnits();
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	public void setItemCount(final String val) {
		// ignore
	}

	@FireReformatted
	public void setName(final String name) {
		_data.setName(name);
	}

	public void setUnits(final String val) {
		// ignore
	}

	@Override
	public String toString() {
		return getName() + " (" + _data.size() + " items)";
	}
}