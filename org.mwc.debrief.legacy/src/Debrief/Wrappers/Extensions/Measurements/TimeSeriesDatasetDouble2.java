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

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.eclipse.january.dataset.CompoundDataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * time series that stores double measurements
 *
 * @author ian
 *
 */
public class TimeSeriesDatasetDouble2 extends TimeSeriesDatasetCore {
	public static class TestMe {
		@Before
		public void suppressSLF4JError() {
			final PrintStream saved = System.err;
			try {
				System.setErr(new PrintStream(new OutputStream() {
					@Override
					public void write(final int b) {
					}
				}));

				LoggerFactory.getLogger(String.class);

			} finally {
				System.setErr(saved);
			}
		}

		@Test
		public void testCreate() {
			final long[] times = new long[] { 0L, 100L, 200L, 300L };
			final double[] v1 = new double[] { 12.2, 12.3, 12.4, 12.5 };
			final double[] v2 = new double[] { 22.2, 22.3, 22.4, 22.5 };

			final LongDataset ld = (LongDataset) DatasetFactory.createFromObject(times);
			System.out.println(ld.toString(true));

			final TimeSeriesDatasetDouble2 dd = new TimeSeriesDatasetDouble2("Test data", "units", "val1 name",
					"val2 name", times, v1, v2);
			dd.printAll();
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final String _value1Name;

	private final String _value2Name;

	public TimeSeriesDatasetDouble2(final String name, final String units, final String value1Name,
			final String value2Name, final long[] times, final double[] values1, final double[] values2) {
		super(units);
		_value1Name = value1Name;
		_value2Name = value2Name;

		// ok create the data items
		final LongDataset dTimes = (LongDataset) DatasetFactory.createFromObject(times);
		final DoubleDataset data1 = (DoubleDataset) DatasetFactory.createFromObject(values1);
		final DoubleDataset data2 = (DoubleDataset) DatasetFactory.createFromObject(values2);

		// and sort the name out
		data1.setName(name + ":" + value1Name);
		data2.setName(name + ":" + value2Name);

		// combine the two into a single compound dataset
		_data = DatasetUtils.createCompoundDataset(data1, data2);

		// we've defined the dataset. now we can centrally name it
		setName(name);

		// put the time in as an axis
		final AxesMetadata axis = new AxesMetadataImpl();
		axis.initialize(1);
		axis.setAxis(0, dTimes);
		_data.addMetadata(axis);
	}

	public double getValue1At(final int index) {
		final CompoundDataset cd = (CompoundDataset) _data;
		final DoubleDataset dataset = (DoubleDataset) cd.getElements(0);
		return dataset.get(index);
	}

	public String getValue1Name() {
		return _value1Name;
	}

	public double getValue2At(final int index) {
		final CompoundDataset cd = (CompoundDataset) _data;
		final DoubleDataset dataset = (DoubleDataset) cd.getElements(1);
		return dataset.get(index);
	}

	public String getValue2Name() {
		return _value2Name;
	}

	public Iterator<Double> getValues1() {
		final CompoundDataset cd = (CompoundDataset) _data;
		final DoubleDataset dataset = (DoubleDataset) cd.getElements(0);
		return new DoubleIterator(dataset);
	}

	public Iterator<Double> getValues2() {
		final CompoundDataset cd = (CompoundDataset) _data;
		final DoubleDataset v2Dataset = (DoubleDataset) cd.getElements(1);
		return new DoubleIterator(v2Dataset);
	}

	@Override
	public void printAll() {
		final LongDataset times = getTimes();
		System.out.println(":" + getName());
		final int len = times.getSize();
		for (int i = 0; i < len; i++) {
			System.out.println("i:" + times.get(i) + " v1:" + getValue1At(i) + " v2:" + getValue2At(i));
		}
	}

	/**
	 * convenience function, to describe this plottable as a string
	 */
	@Override
	public String toString() {
		return getName() + " (" + size() + " items)";
	}
}
