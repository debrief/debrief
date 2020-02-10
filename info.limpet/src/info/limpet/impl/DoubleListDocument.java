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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;

import info.limpet.ICommand;

public class DoubleListDocument extends Document<List<Double>> {

	private final Unit<?> units;

	public DoubleListDocument(final ObjectDataset dataset, final ICommand predecessor, final Unit<?> units) {
		super(dataset, predecessor);
		this.units = units;
	}

	@Override
	public Iterator<List<Double>> getIterator() {
		throw new IllegalArgumentException("We can't just iterate through a 2-D dataset");
	}

	public Iterator<?> getObjectIterator() {
		final ObjectDataset od = (ObjectDataset) dataset;
		final Object[] lists = od.getData();
		final Iterable<Object> iterable = Arrays.asList(lists);
		return iterable.iterator();
	}

	public Unit<?> getUnits() {
		return units;
	}

	@Override
	public boolean isQuantity() {
		return false;
	}

	@Override
	public String toListing() {
		final StringBuffer res = new StringBuffer();

		final ObjectDataset dataset = (ObjectDataset) this.getDataset();
		final AxesMetadata axesMetadata = dataset.getFirstMetadata(AxesMetadata.class);

		DoubleDataset axisOne = null;
		DoubleDataset axisTwo = null;
		if (axesMetadata != null && axesMetadata.getAxes().length > 0) {
			axisOne = (DoubleDataset) axesMetadata.getAxes()[0];
			axisTwo = (DoubleDataset) axesMetadata.getAxes()[1];
		}

		res.append(dataset.getName() + "\n");

		final int[] dims = dataset.getShape();
		if (dims.length != 2) {
			throw new IllegalArgumentException("Should contain 2d data, but contains " + dims.length + " dims");
		}
		final int xDim = dims[0];
		final int yDim = dims[1];

		final NumberFormat nf = new DecimalFormat(" 000.0;-000.0");
		res.append("       ");
		for (int j = 0; j < yDim; j++) {
			res.append(nf.format(axisTwo.get(0, j)) + "  ");
		}
		res.append("\n");

		for (int i = 0; i < xDim; i++) {
			res.append(nf.format(axisOne.get(i, 0)) + ": ");
			for (int j = 0; j < yDim; j++) {
				@SuppressWarnings("unchecked")
				final List<Double> vals = (List<Double>) dataset.get(i, j);
				if (vals == null) {
					res.append("        ");
				} else {
					res.append(vals.size() + " items ");
					// for (Double d : vals)
					// {
					// res.append(d + ", ");
					// }
				}
			}
			res.append("\n");
		}

		res.append("\n");

		return res.toString();
	}
}
