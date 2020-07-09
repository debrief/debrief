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
package info.limpet.impl;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.dataset.StringDataset;
import org.eclipse.january.metadata.AxesMetadata;

import info.limpet.ICommand;

public class StringDocument extends Document<String> {

	public StringDocument(final StringDataset dataset, final ICommand predecessor) {
		super(dataset, predecessor);
	}

	@Override
	public Iterator<String> getIterator() {
		final StringDataset od = (StringDataset) dataset;
		final String[] strings = od.getData();
		final Iterable<String> iterable = Arrays.asList(strings);
		return iterable.iterator();
	}

	public String getString(final int i) {
		final StringDataset od = (StringDataset) dataset;
		return od.getString(i);
	}

	public Double interpolateValue(final long i, final InterpMethod linear) {
		throw new IllegalArgumentException("Not valid for collections of Strings");
	}

	@Override
	public boolean isQuantity() {
		return false;
	}

	@Override
	public void setDataset(final IDataset dataset) {
		if (dataset instanceof ObjectDataset) {
			super.setDataset(dataset);
		} else {
			throw new IllegalArgumentException("We only store object datasets");
		}
	}

	@Override
	public String toListing() {
		final StringBuffer res = new StringBuffer();

		final StringDataset dataset = (StringDataset) this.getDataset();
		final AxesMetadata axesMetadata = dataset.getFirstMetadata(AxesMetadata.class);
		final IndexIterator iterator = dataset.getIterator();

		final DoubleDataset axisDataset;
		if (axesMetadata != null && axesMetadata.getAxes().length > 0) {
			final DoubleDataset doubleAxis = (DoubleDataset) axesMetadata.getAxes()[0];
			axisDataset = doubleAxis != null ? doubleAxis : null;
		} else {
			axisDataset = null;
		}

		res.append(dataset.getName() + ":\n");
		while (iterator.hasNext()) {
			final String indexVal;
			if (axisDataset != null) {
				indexVal = "" + axisDataset.getString(iterator.index);
			} else {
				indexVal = "N/A";
			}

			res.append(indexVal + " : " + dataset.getString(iterator.index));
			res.append(";");
		}
		res.append("\n");

		return res.toString();
	}

}
