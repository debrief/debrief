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

import static Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble2.*;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble2.*;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class TimeSeriesDatasetDouble2Test {
	@BeforeEach
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
