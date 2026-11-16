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

import static Debrief.Wrappers.SensorWrapper.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.Wrappers.SensorWrapper.*;
import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import Debrief.ReaderWriter.Replay.extensions.TA_COG_ABS_DataHandler;
import Debrief.Tools.Properties.ArrayCentreModePropertyEditor;
import Debrief.Wrappers.Extensions.AdditionalData;
import Debrief.Wrappers.Extensions.AdditionalProvider;
import Debrief.Wrappers.Extensions.AdditionalProvider.ExistingChildrenMayNeedToBeWrapped;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble2;
import Debrief.Wrappers.Track.ArrayOffsetHelper;
import Debrief.Wrappers.Track.ArrayOffsetHelper.ArrayCentreMode;
import Debrief.Wrappers.Track.ArrayOffsetHelper.DeferredDatasetArrayMode;
import Debrief.Wrappers.Track.ArrayOffsetHelper.LegacyArrayOffsetModes;
import Debrief.Wrappers.Track.ArrayOffsetHelper.MeasuredDatasetArrayMode;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.PlainWrapper;
import MWC.GUI.SupportsPropertyListeners;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.FullFormatDateTime;
import org.junit.jupiter.api.Test;

public final class SensorWrapperTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testDuplicates() {
		// ok, create the test object
		final SensorWrapper sensor = new SensorWrapper("tester");

		final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

		// and create the list of sensor contact data items
		cal.set(2001, 10, 4, 4, 4, 0);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 23);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 24);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 25);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 25);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 01);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 05);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 55);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		// so, we've now build up the list
		// check it has the correct quantity
		assertEquals(8, sensor._myContacts.size(), "Count of items");

		// check the correct number get returned
		cal.set(2001, 10, 4, 4, 4, 25);
		final MWC.GenericData.Watchable[] list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
		assertEquals(2, list.length, "after end of data");

	}

	@Test
	public void testMeasuredData() throws ParseException {
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("SENSOR");
		final SensorWrapper sw = new SensorWrapper("TA_ARRAY");
		tw.add(sw);
		final Layers layers = new Layers();
		layers.addThisLayer(tw);

		// get some data
		final TA_COG_ABS_DataHandler reader = new TA_COG_ABS_DataHandler();
		reader.setLayers(layers);
		reader.readThisLine(";TA_COG_ABS: 100112 120200 SENSOR TA_ARRAY 4.0 5.0 19.02");
		reader.readThisLine(";TA_COG_ABS: 100112 120300 SENSOR TA_ARRAY 5.0 6.0 19.02");
		reader.readThisLine(";TA_COG_ABS: 100112 120400 SENSOR TA_ARRAY 6.0 7.0 19.02");
		reader.readThisLine(";TA_COG_ABS: 100112 120500 SENSOR TA_ARRAY 7.0 8.0 19.02");
		reader.readThisLine(";TA_COG_ABS: 100112 120600 SENSOR TA_ARRAY 8.0 9.0 19.02");
		reader.readThisLine(";TA_COG_ABS: 100112 120700 SENSOR TA_ARRAY 9.0 10.0 19.02");
		final String dateToken = "100112";

		// create the datasets
		reader.finalise();

		// check data added
		final AdditionalData data = sw.getAdditionalData();

		assertNotNull(data);
		assertEquals(1, data.size());

		// have a go at setting the mode
		final List<ArrayCentreMode> modes = sw.getAdditionalArrayCentreModes();

		final ArrayCentreMode plainMode = modes.get(2);

		// check we found it
		assertNotNull(plainMode);

		// check we've got the right mode
		assertTrue(plainMode instanceof MeasuredDatasetArrayMode, "check we have the correct mode");

		final MeasuredDatasetArrayMode arrayMode = (MeasuredDatasetArrayMode) plainMode;

		// force interpolation off
		arrayMode.setInterpolatePositions(false);

		// check we're not interpolating
		assertFalse(arrayMode.getInterpolatePositions(), "We should not be interpolating");

		HiResDate theDate = DebriefFormatDateTime.parseThis(dateToken, "120430");
		WorldLocation loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
		assertNotNull(loc);
		assertEquals(" 07\u00B000'00.00\"N 008\u00B000\'00.00\"E ", loc.toString(), "next location");

		// try on a time
		theDate = DebriefFormatDateTime.parseThis(dateToken, "120400");
		loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
		assertNotNull(loc);
		assertEquals(" 06\u00B000'00.00\"N 007\u00B000\'00.00\"E ", loc.toString(), "next location");

		// try before start
		theDate = DebriefFormatDateTime.parseThis(dateToken, "120100");
		loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
		assertNull(loc);

		// try after end
		theDate = DebriefFormatDateTime.parseThis(dateToken, "121100");
		loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
		assertNull(loc);

		// try interpolation
		theDate = DebriefFormatDateTime.parseThis(dateToken, "120430");
		arrayMode.setInterpolatePositions(true);
		loc = sw.getMeasuredLocationAt(arrayMode, theDate, null);
		assertNotNull(loc);
		assertEquals(" 06\u00B030'00.00\"N 007\u00B030\'00.00\"E ", loc.toString(), "next location");

		// try before the start

	}

	@Test
	public final void testMergeDiffColors1() {
		// ok, create the test object
		final SensorWrapper sensorA = new SensorWrapper("tester");
		sensorA.setColor(Color.blue);

		final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

		// and create the list of sensor contact data items
		cal.set(2001, 10, 4, 4, 4, 0);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 4, 23);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 4, 25);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 5, 02);
		final SensorWrapper sensorB = new SensorWrapper("tester");
		sensorB.setColor(Color.red);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		cal.set(2001, 10, 4, 4, 5, 03);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				Color.green, null, 1, sensorB.getName()));

		cal.set(2001, 10, 4, 4, 5, 05);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		cal.set(2001, 10, 4, 4, 5, 55);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		final Layer sensorHolder = new BaseLayer();
		sensorHolder.add(sensorA);
		sensorHolder.add(sensorB);

		final Editable[] selection = new Editable[] { sensorA, sensorB };

		assertEquals(3, sensorA.size(), "sensorA has elements");
		assertEquals(4, sensorB.size(), "sensorB has elements");

		// ok, do the merge
		mergeSensors(sensorA, null, sensorHolder, selection);

		assertEquals(7, sensorA.size(), "sensorA has elements");
		assertEquals(0, sensorB.size(), "sensorB has elements");

		// now look at the colors
		final Enumeration<Editable> numer = sensorA.elements();
		int ctr = 0;
		while (numer.hasMoreElements()) {
			final SensorContactWrapper scw = (SensorContactWrapper) numer.nextElement();
			if (ctr < 3) {
				assertEquals(Color.blue, scw.getColor(), "Correct color for A");
				assertEquals(null, scw.getActualColor(), "Correct default for A");
			} else if (ctr == 4) {
				assertEquals(Color.green, scw.getColor(), "Correct color for B");
			} else {
				assertEquals(Color.red, scw.getColor(), "Correct color for B");
			}
			ctr++;
		}
	}

	@Test
	public final void testMergeDiffColors2() {
		// ok, create the test object
		final SensorWrapper sensorA = new SensorWrapper("tester");
		sensorA.setColor(Color.blue);

		final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

		// and create the list of sensor contact data items
		cal.set(2001, 10, 4, 4, 4, 0);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 4, 23);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 4, 25);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 4, 27);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 5, 02);
		final SensorWrapper sensorB = new SensorWrapper("tester");
		sensorB.setColor(Color.red);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		cal.set(2001, 10, 4, 4, 5, 03);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		cal.set(2001, 10, 4, 4, 5, 05);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		final Layer sensorHolder = new BaseLayer();
		sensorHolder.add(sensorA);
		sensorHolder.add(sensorB);

		final Editable[] selection = new Editable[] { sensorA, sensorB };

		assertEquals(4, sensorA.size(), "sensorA has elements");
		assertEquals(3, sensorB.size(), "sensorB has elements");

		// ok, do the merge
		mergeSensors(sensorB, null, sensorHolder, selection);

		assertEquals(0, sensorA.size(), "sensorA has elements");
		assertEquals(7, sensorB.size(), "sensorB has elements");

		// now look at the colors
		final Enumeration<Editable> numer = sensorB.elements();
		int ctr = 0;
		while (numer.hasMoreElements()) {
			final SensorContactWrapper scw = (SensorContactWrapper) numer.nextElement();
			if (ctr++ < 4) {
				assertEquals(Color.blue, scw.getColor(), "Correct color for A");
				assertEquals(Color.blue, scw.getActualColor(), "Correct default for A");
			} else {
				assertEquals(Color.red, scw.getColor(), "Correct color for B");
				assertEquals(null, scw.getActualColor(), "Correct default for A");
			}
		}
	}

	@Test
	public final void testMergeSameColors() {
		// ok, create the test object
		final SensorWrapper sensorA = new SensorWrapper("tester");
		sensorA.setColor(Color.blue);

		final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

		// and create the list of sensor contact data items
		cal.set(2001, 10, 4, 4, 4, 0);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 4, 23);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 4, 25);
		sensorA.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorA.getName()));

		cal.set(2001, 10, 4, 4, 5, 02);
		final SensorWrapper sensorB = new SensorWrapper("tester");
		sensorB.setColor(Color.blue);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		cal.set(2001, 10, 4, 4, 5, 03);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		cal.set(2001, 10, 4, 4, 5, 05);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		cal.set(2001, 10, 4, 4, 5, 55);
		sensorB.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensorB.getName()));

		final Layer sensorHolder = new BaseLayer();
		sensorHolder.add(sensorA);
		sensorHolder.add(sensorB);

		final Editable[] selection = new Editable[] { sensorA, sensorB };

		assertEquals(3, sensorA.size(), "sensorA has elements");
		assertEquals(4, sensorB.size(), "sensorB has elements");

		// ok, do the merge
		mergeSensors(sensorA, null, sensorHolder, selection);

		assertEquals(7, sensorA.size(), "sensorA has elements");
		assertEquals(0, sensorB.size(), "sensorB has elements");

		// now look at the colors
		final Enumeration<Editable> numer = sensorB.elements();
		while (numer.hasMoreElements()) {
			final SensorContactWrapper scw = (SensorContactWrapper) numer.nextElement();
			assertEquals(null, scw.getColor(), "Correct (Default) color for B");
		}
	}

	@Test
	public void testMultipleContacts() {
		final SensorWrapper sw = new SensorWrapper("bbb");
		final SensorContactWrapper sc1 = new SensorContactWrapper("bbb", new HiResDate(0, 9), null, null, null,
				null, "first", 0, sw.getName());
		final SensorContactWrapper sc2 = new SensorContactWrapper("bbb", new HiResDate(0, 12), null, null, null,
				null, "first", 0, sw.getName());
		final SensorContactWrapper sc3 = new SensorContactWrapper("bbb", new HiResDate(0, 7), null, null, null,
				null, "first", 0, sw.getName());
		final SensorContactWrapper sc4 = new SensorContactWrapper("bbb", new HiResDate(0, 13), null, null, null,
				null, "first", 0, sw.getName());

		sw.add(sc1);
		sw.add(sc2);
		sw.add(sc3);
		sw.add(sc4);

		assertEquals(4, sw._myContacts.size(), "four contacts loaded");

		// check we can delete from it
		sw.removeElement(sc3);

		assertEquals(3, sw._myContacts.size(), "now only three contacts loaded");

	}

	@Test
	public final void testMyParams() {
		MWC.GUI.Editable ed = new SensorWrapper("my name");
		MWC.GUI.Editable.editableTesterSupport.testParams(ed);
		ed = null;
	}

	@Test
	public final void testValues() {
		// ok, create the test object
		final SensorWrapper sensor = new SensorWrapper("tester");

		final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

		// and create the list of sensor contact data items
		cal.set(2001, 10, 4, 4, 4, 0);
		final long start_time = cal.getTime().getTime();
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 23);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 25);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 27);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 02);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 01);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 05);
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 55);
		final long end_time = cal.getTime().getTime();
		sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime().getTime()), null, null, null,
				null, null, 1, sensor.getName()));

		// so, we've now build up the list
		// check it has the correct quantity
		assertTrue((sensor._myContacts.size() == 8), "Count of items");

		// check the outer limits
		final HiResDate start = sensor.getStartDTG();
		final HiResDate end = sensor.getEndDTG();
		assertEquals(start.getDate().getTime(), start_time, "first time");
		assertEquals(end.getDate().getTime(), end_time, "last time");

		// //////////////////////////////////////////////////////////////////////
		// finding the nearest entry
		cal.set(2001, 10, 4, 4, 4, 05);
		MWC.GenericData.Watchable[] list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime()));
		SensorContactWrapper nearest = (SensorContactWrapper) list[0];
		assertEquals(nearest.getDTG().getDate().getTime(), cal.getTime().getTime(), "Nearest matching fix");

		final java.util.Calendar cal_other = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);
		cal_other.set(2001, 10, 4, 4, 4, 03);
		list = sensor.getNearestTo(new HiResDate(cal_other.getTime().getTime()));
		nearest = (SensorContactWrapper) list[0];
		assertTrue((nearest.getDTG().getMicros() / 1000 == cal.getTime().getTime()), "Nearest or greater than fix");

		// ///////////////////////////////////////////////////////////////////
		// filter the list
		cal.set(2001, 10, 4, 4, 4, 22);
		cal_other.set(2001, 10, 4, 4, 4, 25);

		// ////////////////////////////////////////////////////////////////////////
		// do the filter
		sensor.filterListTo(new HiResDate(cal.getTime().getTime()), new HiResDate(cal_other.getTime().getTime()));

		// see how many remain visible
		java.util.Enumeration<Editable> iter = sensor.elements();
		int counter = 0;
		while (iter.hasMoreElements()) {
			final SensorContactWrapper contact = (SensorContactWrapper) iter.nextElement();
			if (contact.getVisible())
				counter++;
		}
		// check that the correct number are visible
		assertTrue((counter == 2), "Correct filtering of list");

		// clear the filter
		sensor.filterListTo(sensor.getStartDTG(), sensor.getEndDTG());
		// see how many remain visible
		iter = sensor.elements();
		counter = 0;
		while (iter.hasMoreElements()) {
			final SensorContactWrapper contact = (SensorContactWrapper) iter.nextElement();
			if (contact.getVisible())
				counter++;
		}
		// check that the correct number are visible
		assertTrue((counter == 8), "Correct removal of list filter");

		// //////////////////////////////////////////////////////
		// get items between
		java.util.Collection<Editable> res = sensor.getItemsBetween(new HiResDate(cal.getTime().getTime()),
				new HiResDate(cal_other.getTime().getTime()));
		assertTrue((res.size() == 2), "get items between");

		// do recheck, since this time we will be resetting the working
		// variables, rather and creating them
		cal.set(2001, 10, 4, 4, 4, 5);
		cal_other.set(2001, 10, 4, 4, 4, 27);
		res = sensor.getItemsBetween(new HiResDate(cal.getTime().getTime()),
				new HiResDate(cal_other.getTime().getTime()));
		assertEquals(4, res.size(), "recheck get items between:" + res.size());

		// and show all of the data
		res = sensor.getItemsBetween(sensor.getStartDTG(), sensor.getEndDTG());
		assertTrue((res.size() == 8), "recheck get items between:" + res.size());

		// /////////////////////////////////////////////////////////
		// test the position related stuff
		final TrackWrapper track = new TrackWrapper();

		// and add the fixes
		cal.set(2001, 10, 4, 4, 4, 0);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new MWC.GenericData.WorldLocation(2.0, 2.0, 0.0), 12, 12)));

		cal.set(2001, 10, 4, 4, 4, 01);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new MWC.GenericData.WorldLocation(2.0, 2.25, 0.0), 12, 12)));

		cal.set(2001, 10, 4, 4, 4, 02);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new MWC.GenericData.WorldLocation(2.0, 2.5, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 05);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new MWC.GenericData.WorldLocation(2.0, 2.75, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 23);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new MWC.GenericData.WorldLocation(2.25, 2.0, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 25);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new MWC.GenericData.WorldLocation(2.5, 2.0, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 28);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new MWC.GenericData.WorldLocation(2.75, 2.0, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 55);
		track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
				new MWC.GenericData.WorldLocation(2.25, 2.25, 0.0), 12, 12)));

		// ok, put the sensor data into the track
		track.add(sensor);

		track.setInterpolatePoints(false);

		// now find the location of an item, any item!
		cal.set(2001, 10, 4, 4, 4, 27);
		list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
		nearest = (SensorContactWrapper) list[0];
		WorldLocation nearestPoint = nearest.getCalculatedOrigin(track);
		WorldLocation tgtLoc = new MWC.GenericData.WorldLocation(2.66666, 2.0, 0.0);
		assertEquals(0, tgtLoc.rangeFrom(nearestPoint), 0.001, "first test");

		// ah-ha! what about a contact between two fixes
		cal.set(2001, 10, 4, 4, 4, 26);
		final HiResDate theTime = new HiResDate(cal.getTime().getTime(), 0);
		list = sensor.getNearestTo(theTime);
		nearest = (SensorContactWrapper) list[0];
		nearestPoint = nearest.getCalculatedOrigin(track);
		assertEquals(0, tgtLoc.rangeFrom(nearestPoint), 0.001, "test mid way");

		// ok, that was half-way, what making it nearer to one of the fixes
		cal.set(2001, 10, 4, 4, 4, 25);
		list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
		nearest = (SensorContactWrapper) list[0];
		nearestPoint = nearest.getCalculatedOrigin(track);
		tgtLoc = new MWC.GenericData.WorldLocation(2.5, 2.0, 0.0);
		assertEquals(0, tgtLoc.rangeFrom(nearestPoint), 0.001, "test nearer first point");

		// start point?
		cal.set(2001, 10, 4, 4, 4, 0);
		list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
		nearest = (SensorContactWrapper) list[0];
		nearestPoint = nearest.getCalculatedOrigin(track);
		assertEquals(new MWC.GenericData.WorldLocation(2.0, 2.0, 0.0), nearestPoint, "test start point");

		// end point?
		cal.set(2001, 10, 4, 4, 4, 55);
		list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
		nearest = (SensorContactWrapper) list[0];
		nearestPoint = nearest.getCalculatedOrigin(track);
		assertEquals(nearestPoint, new MWC.GenericData.WorldLocation(2.25, 2.25, 0.0), "test end point");

		// before start of track data?
		cal.set(2001, 10, 4, 4, 3, 0);
		list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
		assertEquals(list.length, 0, "before range of data");

		// after end of track data?
		cal.set(2001, 10, 4, 4, 7, 0);
		list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
		assertEquals(list.length, 1, "after end of data");

	}
}
