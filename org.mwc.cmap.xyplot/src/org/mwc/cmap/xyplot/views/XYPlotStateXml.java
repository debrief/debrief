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

import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import Debrief.Testing.GoldenFile;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GUI.JFreeChart.CourseFormatter;
import MWC.GUI.JFreeChart.formattingOperation;
import junit.framework.TestCase;

/**
 * The XStream set-up {@link XYPlotView} uses to keep a plot's dataset and
 * formatter in the workbench memento, so a plot survives a restart. Held in
 * one place so that an XStream upgrade (and its security allow-list) changes
 * one class, and so the round trip can be tested without building the view.
 */
public final class XYPlotStateXml {

	public static final class TestMe extends TestCase {

		private static final String GOLDEN = "xyplot/time_series_memento.xml";

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
			assertTrue("is a time series collection", actual instanceof TimeSeriesCollection);
			final TimeSeriesCollection got = (TimeSeriesCollection) actual;
			assertEquals("series count", expected.getSeriesCount(), got.getSeriesCount());
			for (int s = 0; s < expected.getSeriesCount(); s++) {
				final TimeSeries e = expected.getSeries(s);
				final TimeSeries a = got.getSeries(s);
				assertEquals("series key", e.getKey(), a.getKey());
				assertEquals("items in " + e.getKey(), e.getItemCount(), a.getItemCount());
				for (int i = 0; i < e.getItemCount(); i++) {
					final ColouredDataItem ei = (ColouredDataItem) e.getDataItem(i);
					final ColouredDataItem ai = (ColouredDataItem) a.getDataItem(i);
					assertEquals("time", ei.getPeriod().getFirstMillisecond(), ai.getPeriod().getFirstMillisecond());
					assertEquals("value", ei.getValue().doubleValue(), ai.getValue().doubleValue(), 1e-12);
					assertEquals("colour", ei.getColor(), ai.getColor());
					assertEquals("connect", ei.connectToPrevious(), ai.connectToPrevious());
					assertEquals("filled", ei.isShapeFilled(), ai.isShapeFilled());
				}
			}
		}

		public void testRoundTrip() {
			final TimeSeriesCollection dataset = sampleDataset();
			final formattingOperation formatter = new CourseFormatter();

			final String dataXml = writer().toXML(dataset);
			final String formatterXml = writer().toXML(formatter);

			assertSameDataset(dataset, reader().fromXML(dataXml));
			assertTrue("formatter restored", reader().fromXML(formatterXml) instanceof CourseFormatter);

			// the scalar plot attributes the view also stores
			assertEquals("title", "Range plot", reader().fromXML(writer().toXML("Range plot")));
			assertEquals("line width", Integer.valueOf(3), reader().fromXML(writer().toXML(Integer.valueOf(3))));
			assertEquals("flag", Boolean.TRUE, reader().fromXML(writer().toXML(Boolean.TRUE)));
		}

		/** a memento naming a type outside the allow-list is refused, not instantiated */
		public void testRefusesOtherTypes() {
			final String xml = writer().toXML(new java.io.File("x"));
			try {
				reader().fromXML(xml);
				fail("java.io.File is not on the allow-list");
			} catch (final com.thoughtworks.xstream.XStreamException e) {
				assertEquals("refused by the security framework", "ForbiddenClassException",
						e.getClass().getSimpleName());
			}
		}

		/**
		 * a memento written by the current XStream and JFreeChart is committed as a
		 * golden file. Whatever library versions come later must still read it,
		 * otherwise a user's saved workspace loses its plots.
		 */
		public void testSavedMementoStillLoads() throws Exception {
			final TimeSeriesCollection dataset = sampleDataset();
			final File golden = new File(GoldenFile.dir(), GOLDEN);
			if (GoldenFile.isRegenerating()) {
				GoldenFile.assertMatches(GOLDEN, writer().toXML(dataset) + "\n");
			}
			assertTrue("golden memento " + golden + " exists; regenerate with -D" + GoldenFile.REGENERATE_PROPERTY
					+ "=true", golden.isFile());
			final String xml = new String(Files.readAllBytes(golden.toPath()), StandardCharsets.UTF_8);
			assertSameDataset(dataset, reader().fromXML(xml));
		}
	}

	/**
	 * the types a memento may hold, beyond the plain Java ones (strings, numbers,
	 * collections) that XStream allows by default: JFreeChart's datasets and
	 * series, Debrief's data items and formatters, and colours. Anything else in
	 * the memento is refused rather than instantiated.
	 */
	private static final String[] ALLOWED_WILDCARDS = { "org.jfree.**", "MWC.GUI.JFreeChart.**" };

	private static final Class<?>[] ALLOWED_TYPES = { Color.class };

	/** the XStream that reads a dataset, formatter or attribute back from the memento */
	public static XStream reader() {
		final XStream xs = new XStream(new DomDriver());
		xs.allowTypesByWildcard(ALLOWED_WILDCARDS);
		xs.allowTypes(ALLOWED_TYPES);
		return xs;
	}

	/** the XStream that writes a dataset, formatter or attribute into the memento */
	public static XStream writer() {
		return new XStream(new StaxDriver());
	}

	private XYPlotStateXml() {
	}
}
