/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2026, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.cmap.xyplot.views;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.cmap.xyplot.views.XYPlotStateXml.*;
import org.mwc.cmap.xyplot.views.XYPlotStateXml.*;
import java.awt.Color;
import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.swing.event.EventListenerList;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeriesCollection;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import Debrief.Testing.GoldenFile;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GUI.JFreeChart.CourseFormatter;
import MWC.GUI.JFreeChart.formattingOperation;
import org.junit.jupiter.api.Test;

public final class XYPlotStateXmlTest {

	private static final String GOLDEN = "xyplot/time_series_memento.xml";

	/** a type outside the allow-list, with no XStream converter of its own */
	private static final class NotAllowed {
		@SuppressWarnings("unused")
		private final int value = 1;
	}

	/** a dataset shaped like the ones Debrief's time-variable plots build */
	private static TimeSeriesCollection sampleDataset() {
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries course = new TimeSeries("NELSON course");
		final TimeSeries range = new TimeSeries("COLLINGWOOD range");
		for (int i = 0; i < 5; i++) {
			final long t = 818740800000L + i * 60_000L;
			course.add(new ColouredDataItem(new FixedMillisecond(t), 270 + i * 0.5, Color.red, i > 0, null, true,
					i % 2 == 0));
			range.add(new ColouredDataItem(new FixedMillisecond(t), 4000 - i * 125.25, Color.blue, true, null,
					true, false));
		}
		dataset.addSeries(course);
		dataset.addSeries(range);
		return dataset;
	}

	private static void assertSameDataset(final TimeSeriesCollection expected, final Object actual) {
		assertTrue(actual instanceof TimeSeriesCollection, "is a time series collection");
		final TimeSeriesCollection got = (TimeSeriesCollection) actual;
		assertEquals(expected.getSeriesCount(), got.getSeriesCount(), "series count");
		for (int s = 0; s < expected.getSeriesCount(); s++) {
			final TimeSeries e = expected.getSeries(s);
			final TimeSeries a = got.getSeries(s);
			assertEquals(e.getKey(), a.getKey(), "series key");
			assertEquals(e.getItemCount(), a.getItemCount(), "items in " + e.getKey());
			for (int i = 0; i < e.getItemCount(); i++) {
				final ColouredDataItem ei = (ColouredDataItem) e.getDataItem(i);
				final ColouredDataItem ai = (ColouredDataItem) a.getDataItem(i);
				assertEquals(ei.getPeriod().getFirstMillisecond(), ai.getPeriod().getFirstMillisecond(), "time");
				assertEquals(ei.getValue().doubleValue(), ai.getValue().doubleValue(), 1e-12, "value");
				assertEquals(ei.getColor(), ai.getColor(), "colour");
				assertEquals(ei.connectToPrevious(), ai.connectToPrevious(), "connect");
				assertEquals(ei.isShapeFilled(), ai.isShapeFilled(), "filled");
			}
		}
	}

	@Test
	public void testRoundTrip() {
		final TimeSeriesCollection dataset = sampleDataset();
		final formattingOperation formatter = new CourseFormatter();

		final String dataXml = writer().toXML(dataset);
		final String formatterXml = writer().toXML(formatter);

		final Object restored = readDataset(reader(), dataXml);
		assertSameDataset(dataset, restored);

		// the restored series are wired to their collection, as addSeries() does
		final TimeSeries series = ((TimeSeriesCollection) restored).getSeries(0);
		series.add(new FixedMillisecond(818740800000L + 3_600_000L), 1d);
		assertEquals(6, ((TimeSeriesCollection) restored).getItemCount(0), "collection sees the new item");
		try {
			series.setKey("COLLINGWOOD range");
			fail("the collection vetoes a duplicate series key");
		} catch (final IllegalArgumentException e) {
			// expected: the collection is listening again
		}
		assertTrue(reader().fromXML(formatterXml) instanceof CourseFormatter, "formatter restored");

		// the scalar plot attributes the view also stores
		assertEquals("Range plot", reader().fromXML(writer().toXML("Range plot")), "title");
		assertEquals(Integer.valueOf(3), reader().fromXML(writer().toXML(Integer.valueOf(3))), "line width");
		assertEquals(Boolean.TRUE, reader().fromXML(writer().toXML(Boolean.TRUE)), "flag");
	}

	/** a memento naming a type outside the allow-list is refused, not instantiated */
	@Test
	public void testRefusesOtherTypes() {
		final String xml = writer().toXML(new NotAllowed());
		try {
			reader().fromXML(xml);
			fail("NotAllowed is not on the allow-list");
		} catch (final com.thoughtworks.xstream.XStreamException e) {
			assertEquals("ForbiddenClassException", e.getClass().getSimpleName(),
					"refused by the security framework");
		}
	}

	/**
	 * a memento written by the current XStream and JFreeChart is committed as a
	 * golden file. Whatever library versions come later must still read it,
	 * otherwise a user's saved workspace loses its plots.
	 */
	@Test
	public void testSavedMementoStillLoads() throws Exception {
		final TimeSeriesCollection dataset = sampleDataset();
		final File golden = new File(GoldenFile.dir(), GOLDEN);
		if (GoldenFile.isRegenerating()) {
			GoldenFile.assertMatches(GOLDEN, writer().toXML(dataset) + "\n");
		}
		assertTrue(golden.isFile(), "golden memento " + golden + " exists; regenerate with -D" + GoldenFile.REGENERATE_PROPERTY
				+ "=true");
		final String xml = new String(Files.readAllBytes(golden.toPath()), StandardCharsets.UTF_8);
		assertSameDataset(dataset, readDataset(reader(), xml));
	}
}
