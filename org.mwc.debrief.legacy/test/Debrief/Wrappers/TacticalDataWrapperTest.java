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

package Debrief.Wrappers;

import static Debrief.Wrappers.TacticalDataWrapper.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.Wrappers.TacticalDataWrapper.*;
import java.awt.Color;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact;
import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import MWC.Algorithms.Conversions;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.HasEditables;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class TacticalDataWrapperTest {
	private static SensorWrapper getList() throws ParseException {
		final SensorWrapper sw = new SensorWrapper("mySensor");

		SensorContactWrapper scw = new SensorContactWrapper("parent",
				new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:34:04 PM GMT")
						.getTime()),
				new WorldDistance(2, WorldDistance.NM), 13.1, new WorldLocation(1, 3, 0), Color.RED, "the label", 0,
				"other label");
		sw.add(scw);

		scw = new SensorContactWrapper("parent",
				new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:34:54 PM GMT")
						.getTime()),
				new WorldDistance(2, WorldDistance.NM), 7.9, new WorldLocation(1, 3, 0), Color.RED, "the label", 0,
				"other label");
		sw.add(scw);

		scw = new SensorContactWrapper("parent",
				new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:35:32 PM GMT")
						.getTime()),
				new WorldDistance(2, WorldDistance.NM), 4.8, new WorldLocation(1, 3, 0), Color.RED, "the label", 0,
				"other label");
		sw.add(scw);

		scw = new SensorContactWrapper("parent",
				new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:36:36 PM GMT")
						.getTime()),
				new WorldDistance(2, WorldDistance.NM), 359.1, new WorldLocation(1, 3, 0), Color.RED, "the label",
				0, "other label");
		sw.add(scw);

		scw = new SensorContactWrapper("parent",
				new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:37:24 PM GMT")
						.getTime()),
				new WorldDistance(2, WorldDistance.NM), 355.2, new WorldLocation(1, 3, 0), Color.RED, "the label",
				0, "other label");
		sw.add(scw);

		scw = new SensorContactWrapper("parent",
				new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:38:18 PM GMT")
						.getTime()),
				new WorldDistance(2, WorldDistance.NM), 348.6, new WorldLocation(1, 3, 0), Color.RED, "the label",
				0, "other label");
		sw.add(scw);

		scw = new SensorContactWrapper("parent",
				new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:39:04 PM GMT")
						.getTime()),
				new WorldDistance(2, WorldDistance.NM), 345.1, new WorldLocation(1, 3, 0), Color.RED, "the label",
				0, "other label");
		sw.add(scw);
		return sw;
	}

	@Test
	public static void testDecimate() {
		final SensorWrapper sw = new SensorWrapper("mySensor");
		long nowMillis = 3000;
		for (int i = 0; i < 6; i++) {
			final SensorContactWrapper scw = new SensorContactWrapper("parent", new HiResDate(nowMillis),
					new WorldDistance(i, WorldDistance.NM), (double) i, new WorldLocation(1, i, 0), Color.RED,
					"the label", 0, "other label");
			sw.add(scw);
			nowMillis += 16 * 1000;
		}

		// jump forward a while
		nowMillis += 5 * 60 * 1000;

		for (int j = 0; j < 6; j++) {
			final SensorContactWrapper scw = new SensorContactWrapper("parent", new HiResDate(nowMillis),
					new WorldDistance(j, WorldDistance.NM), (double) j, new WorldLocation(1, j, 0), Color.RED,
					"the label", 0, "other label");
			sw.add(scw);
			nowMillis += 16 * 1000;
		}

		assertEquals(12, sw._myContacts.size(), "correct number before");

		sw.decimate(new HiResDate(20000), 4000000);

		assertEquals(24, sw._myContacts.size(), "correct number of decimated");
	}

	/**
	 * range, frequency and bearing are interpolated with the correct rules:
	 * linear for range/frequency, shortest-turn for bearings
	 */
	@Test
	public static void testDecimateNonAngularValues() {
		final SensorWrapper sw = new SensorWrapper("mySensor");
		final long t0 = 60000;
		sw.add(new SensorContactWrapper("parent", new HiResDate(t0), new WorldDistance(100, WorldDistance.YARDS),
				350d, 340d, 150d, null, Color.RED, "the label", 0, "other label"));
		sw.add(new SensorContactWrapper("parent", new HiResDate(t0 + 60000),
				new WorldDistance(5000, WorldDistance.YARDS), 10d, 20d, 400d, null, Color.RED, "the label", 0,
				"other label"));

		// resample every 30 secs (micros)
		sw.decimate(new HiResDate(30000), t0 * 1000);

		assertEquals(3, sw._myContacts.size(), "correct number of decimated");

		SensorContactWrapper mid = null;
		for (final Editable ed : sw._myContacts) {
			final SensorContactWrapper sc = (SensorContactWrapper) ed;
			if (sc.getDTG().getDate().getTime() == t0 + 30000) {
				mid = sc;
			}
		}
		assertNotNull(mid, "found mid-point");
		assertEquals(2550d, mid.getRange().getValueIn(WorldDistance.YARDS), 0.001, "range interpolated linearly");
		assertEquals(275d, mid.getFrequency(), 0.001, "frequency interpolated linearly");
		assertEquals(0d, MWC.Algorithms.Conversions.signedDegs(mid.getBearing()),
				0.001, "bearing interpolated through north");
		assertEquals(0d, MWC.Algorithms.Conversions.signedDegs(mid.getAmbiguousBearing()),
				0.001, "ambig bearing interpolated through north");
		assertTrue(mid.getBearing() >= 0 && mid.getBearing() < 360, "bearing in [0,360)");
	}

	@Test
	public static void testInterpolatorZeroTimeDelta() {
		final SensorContactWrapper s1 = new SensorContactWrapper("parent", new HiResDate(1000), null, 10d, null,
				Color.RED, "a", 0, "s");
		final SensorContactWrapper s2 = new SensorContactWrapper("parent", new HiResDate(1000), null, 20d, null,
				Color.RED, "b", 0, "s");
		final LinearInterpolator interp = new LinearInterpolator(s1, s2, 1000 * 1000L);
		final double res = interp.interp(100, 200);
		assertFalse(Double.isNaN(res) || Double.isInfinite(res), "finite result for equal times");
		assertEquals(100d, res, 0.0001, "takes start value");
	}

	@Test
	public static void testDecimateThroughZero() throws ParseException {
		final SensorWrapper sw = getList();

		Iterator<Editable> iter = sw._myContacts.iterator();
		while (iter.hasNext()) {
			final SensorContactWrapper sc = (SensorContactWrapper) iter.next();
			// System.out.println(sc.getDTG().getDate() + ", " + sc.getBearing());
		}

		// System.out.println("=================");

		assertEquals(7, sw._myContacts.size(), "correct number before");

		final Date startDate = new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z")
				.parse("July 7, 2011 12:34:00 PM GMT");

		sw.decimate(new HiResDate(60000), startDate.getTime());

		assertEquals(6, sw._myContacts.size(), "correct number of decimated");

		iter = sw._myContacts.iterator();
		while (iter.hasNext()) {
			final SensorContactWrapper sc = (SensorContactWrapper) iter.next();
			// System.out.println(sc.getDTG().getDate() + ", " + sc.getBearing());
		}
	}

	@Test
	public static void testTrim() throws ParseException {
		SensorWrapper ts0 = getList();

		TimePeriod newP = new TimePeriod.BaseTimePeriod(
				new HiResDate(
						new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:34:04 PM GMT")),
				new HiResDate(
						new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:39:04 PM GMT")));
		assertEquals(7, ts0._myContacts.size(), "correct len");
		ts0.trimTo(newP);
		assertEquals(7, ts0._myContacts.size(), "correct new len");

		ts0 = getList();
		newP = new TimePeriod.BaseTimePeriod(
				new HiResDate(
						new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:34:04 PM GMT")),
				new HiResDate(
						new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:35:32 PM GMT")));
		assertEquals(7, ts0._myContacts.size(), "correct len");
		ts0.trimTo(newP);
		assertEquals(3, ts0._myContacts.size(), "correct new len");

		ts0 = getList();
		newP = new TimePeriod.BaseTimePeriod(
				new HiResDate(
						new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:35:32 PM GMT")),
				new HiResDate(
						new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:38:18 PM GMT")));
		assertEquals(7, ts0._myContacts.size(), "correct len");
		ts0.trimTo(newP);
		assertEquals(4, ts0._myContacts.size(), "correct new len");

		ts0 = getList();
		newP = new TimePeriod.BaseTimePeriod(
				new HiResDate(
						new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:41:04 PM GMT")),
				new HiResDate(
						new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2012 12:39:04 PM GMT")));
		assertEquals(7, ts0._myContacts.size(), "correct len");
		ts0.trimTo(newP);
		assertEquals(0, ts0._myContacts.size(), "correct new len");

	}

}
