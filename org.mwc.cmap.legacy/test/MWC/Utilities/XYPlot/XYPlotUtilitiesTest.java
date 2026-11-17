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

package MWC.Utilities.XYPlot;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.Utilities.XYPlot.XYPlotUtilities.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.Utilities.XYPlot.XYPlotUtilities.*;
import org.junit.jupiter.api.Test;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.text.DateFormat;
import java.util.Date;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public final class XYPlotUtilitiesTest {
	@Test
	public synchronized void testTwoSeriesDataset() {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries series1 = new TimeSeries("name1");
		final Date time1 = new Date();
		final Date time2 = new Date(time1.getTime() + 60000);
		series1.add(new FixedMillisecond((long) (time1.getTime() / 1d)), 1.0);
		series1.add(new FixedMillisecond((long) (time2.getTime() / 1d)), 2.0);
		final TimeSeries series2 = new TimeSeries("name2");
		series2.add(new FixedMillisecond((long) (time1.getTime() / 1d)), 3.0);
		series2.add(new FixedMillisecond((long) (time2.getTime() / 1d)), 4.0);
		dataset.addSeries(series1);
		dataset.addSeries(series2);

		final String result = XYPlotUtilities.textMatrix("test", dataset);
		System.out.println(result);
		final String expected = "test, name1\n" + df.format(time1) + ", 1.0\n" + df.format(time2) + ", 2.0\n"
				+ "test, name2\n" + df.format(time1) + ", 3.0\n" + df.format(time2) + ", 4.0\n";
		assertEquals(expected, result);
	}
}
