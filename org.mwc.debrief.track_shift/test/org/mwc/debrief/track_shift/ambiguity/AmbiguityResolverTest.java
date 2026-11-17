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

package org.mwc.debrief.track_shift.ambiguity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.track_shift.ambiguity.AmbiguityResolver.*;
import org.mwc.debrief.track_shift.ambiguity.AmbiguityResolver.*;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichBearing;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichPeriod;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.views.BearingResidualsView;
import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import org.junit.jupiter.api.Test;

public final class AmbiguityResolverTest {

	/**
	 * algorithm to quickly generate large quantities of lists of boolean
	 * permutations. Taken from here: https://stackoverflow.com/a/27994579/92441
	 *
	 * @param n number of boolean perms
	 * @return iterator delivers series of boolean arrays, each providing different
	 *         permutation on the possibles
	 */
	public static Iterator<Boolean[]> bool(final int n) {
		return new Iterator<Boolean[]>() {
			final long max = (long) Math.pow(2, n);
			long next = 0L;

			@Override
			public boolean hasNext() {
				return next < max;
			}

			@Override
			public Boolean[] next() {
				final Boolean[] b = new Boolean[n];
				for (int i = 0; i < n; i++) {
					b[i] = (next & (1 << i)) != 0;
				}
				next++;
				return b;
			}

			@Override
			public void remove() {
				// placeholder, to allow JAva8 compile

			}

		};
	}

	private static TrackWrapper getData(final String name, final String trackName) throws FileNotFoundException {
		// get our sample data-file
		final ImportReplay importer = new ImportReplay();
		final Layers theLayers = new Layers();
		final String fName = "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/" + name;
		final File inFile = new File(fName);
		assertTrue(inFile.exists(), "input file exists");
		final FileInputStream is = new FileInputStream(fName);
		importer.importThis(fName, is, theLayers);

		// sort out the sensors
		importer.storePendingSensors();

		// get the sensor track
		final TrackWrapper track = (TrackWrapper) theLayers.findLayer(trackName);

		assertNotNull(track, "found sensor track");

		return track;
	}

	@Test
	public static void testChunkFor() {
		final int minRemainder = 9;
		for (int i = minRemainder; i < 50; i++) {
			System.out.println(i + " " + chunkFor(i, 9) + " - " + (i % chunkFor(i, 9)));
			assertTrue(i % chunkFor(i, 9) > minRemainder || i % chunkFor(i, 9) == 0);
		}
	}

	@Test
	public static void testGetCurve() throws FileNotFoundException {
		final LegOfCuts leg4 = new LegOfCuts();
		final SensorWrapper sensor = new SensorWrapper("name");
		leg4.add(wrapMe(sensor, 80, 92d, 260d));
		leg4.add(wrapMe(sensor, 90, 82d, 280d));
		leg4.add(wrapMe(sensor, 100, 72d, 300d));
		leg4.add(wrapMe(sensor, 110, 62d, 320d));
		leg4.add(wrapMe(sensor, 120, 52d, 340d));
		leg4.add(wrapMe(sensor, 130, 42d, 360d));
		leg4.add(wrapMe(sensor, 140, 32d, 380d));

		final double[] curve = leg4.getCurve(WhichPeriod.ALL, WhichBearing.AMBIGUOUS);
		assertNotNull(curve, "produced curve");
		assertEquals(3, curve.length, "curve correct length");
		assertEquals(100d, curve[0], 0.001, "correct offset");

		leg4.set(5, wrapMe(sensor, 130, 42d, 0d));
		leg4.set(6, wrapMe(sensor, 140, 32d, 20d));

		final double[] curve2Ambig = leg4.getCurve(WhichPeriod.ALL, WhichBearing.AMBIGUOUS);
		final double[] curve2 = leg4.getCurve(WhichPeriod.ALL, WhichBearing.CORE);
		assertNotNull(curve2Ambig, "produced curve");
		assertEquals(3, curve2Ambig.length, "curve correct length");
		assertEquals(100d, curve2Ambig[0], 0.001, "correct offset");

		double beforeValue = valueAt(60, curve2Ambig);
		assertEquals(beforeValue, 220d, 0.001, "correct next value");

		double afterValue = valueAt(150, curve2Ambig);
		assertEquals(afterValue, 400d, 0.001, "correct next value");

		beforeValue = valueAt(60, curve2);
		assertEquals(beforeValue, 112d, 0.001, "correct next value");

		afterValue = valueAt(150, curve2);
		assertEquals(afterValue, 22d, 0.001, "correct next value");
	}

	@Test
	public static void testGetCurveSector() throws FileNotFoundException {
		final LegOfCuts leg4 = new LegOfCuts();
		final SensorWrapper sensor = new SensorWrapper("name");
		leg4.add(wrapMe(sensor, 80, 92d, 260d));
		leg4.add(wrapMe(sensor, 90, 82d, 280d));
		leg4.add(wrapMe(sensor, 100, 72d, 300d));
		leg4.add(wrapMe(sensor, 110, 62d, 320d));
		leg4.add(wrapMe(sensor, 120, 52d, 340d));

		// start off with it too short
		List<SensorContactWrapper> leg = leg4.extractPortion(WhichPeriod.EARLY);
		assertNotNull(leg, "leg retrieved");
		assertEquals(5, leg.size(), "correct length");

		// ok, add more data, so that we need to trim the data
		leg4.add(wrapMe(sensor, 130, 42d, 0d));
		leg4.add(wrapMe(sensor, 140, 32d, 20d));
		leg4.add(wrapMe(sensor, 150, 22d, 40d));
		leg4.add(wrapMe(sensor, 160, 12d, 60d));
		leg4.add(wrapMe(sensor, 170, 2d, 80d));
		leg4.add(wrapMe(sensor, 180, 12d, 60d));
		leg4.add(wrapMe(sensor, 190, 22d, 40d));
		leg4.add(wrapMe(sensor, 200, 32d, 20d));
		leg4.add(wrapMe(sensor, 210, 42d, 0d));
		leg4.add(wrapMe(sensor, 220, 52d, 340d));
		leg4.add(wrapMe(sensor, 230, 62d, 320d));
		leg4.add(wrapMe(sensor, 240, 72d, 300d));

		// check we retrieve the expected data
		leg = leg4.extractPortion(WhichPeriod.ALL);
		assertNotNull(leg, "leg retrieved");
		assertEquals(17, leg.size(), "correct length");
		assertEquals(80, leg.get(0).getDTG().getDate().getTime(), "correct start");
		assertEquals(240, leg.get(leg.size() - 1).getDTG().getDate().getTime(), "correct end");

		leg = leg4.extractPortion(WhichPeriod.EARLY);
		assertNotNull(leg, "leg retrieved");
		assertEquals(8, LegOfCuts.LEG_LENGTH, "correct length");
		assertEquals(80, leg.get(0).getDTG().getDate().getTime(), "correct start");
		assertEquals(150, leg.get(leg.size() - 1).getDTG().getDate().getTime(), "correct end");

		leg = leg4.extractPortion(WhichPeriod.LATE);
		assertNotNull(leg, "leg retrieved");
		assertEquals(8, LegOfCuts.LEG_LENGTH, "correct length");
		assertEquals(170, leg.get(0).getDTG().getDate().getTime(), "correct start");
		assertEquals(240, leg.get(leg.size() - 1).getDTG().getDate().getTime(), "correct end");

		// try the calculated values
		double[] curve = leg4.getCurve(WhichPeriod.ALL, WhichBearing.CORE);
		assertEquals(119.20, valueAt(80, curve), 0.01, "correct value (since we can't fit good curve)");
		assertEquals(78.79, valueAt(240, curve), 0.01, "correct value (since we can't fit good curve)");

		curve = leg4.getCurve(WhichPeriod.EARLY, WhichBearing.CORE);
		assertEquals(92, valueAt(80, curve), 0.01, "correct value");

		curve = leg4.getCurve(WhichPeriod.EARLY, WhichBearing.AMBIGUOUS);
		assertEquals(260, valueAt(80, curve), 0.01, "correct value");

		curve = leg4.getCurve(WhichPeriod.LATE, WhichBearing.CORE);
		assertEquals(72, valueAt(240, curve), 0.01, "correct value");

		curve = leg4.getCurve(WhichPeriod.LATE, WhichBearing.AMBIGUOUS);
		assertEquals(-60, valueAt(240, curve), 0.01, "correct value");
	}

	@Test
	public static void testHandleWiggle() throws FileNotFoundException {
		final TrackWrapper track = getData("Ambig_tracks_hover_north.rep", "SENSOR");
		assertNotNull(track, "found track");

		// has sensors
		assertEquals(2, track.getSensors().size(), "has sensors");

		final Enumeration<Editable> sensorList = track.getSensors().elements();
		@SuppressWarnings("unused")
		final SensorWrapper hmSensor = (SensorWrapper) sensorList.nextElement();
		// make the sensor visible

		final SensorWrapper sensor = (SensorWrapper) sensorList.nextElement();
		sensor.setVisible(true);
		final TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor.getEndDTG());

		// check we have the correct sensor
		assertEquals("TA", sensor.getName(), "correct name");

		final Logger logger = Logger.getLogger("Logger");
		// try to get zones using ambiguity delta
		final LegsAndZigs res = AmbiguityResolver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, logger,
				null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL, timePeriod, null);
		final List<LegOfCuts> legs = res.legs;
		final LegOfCuts zigs = res.zigCuts;

		assertNotNull(legs, "found zones");
		assertEquals(3, legs.size(), "found correct number of zones");

		assertNotNull(zigs, "found zigs");
		assertEquals(10, zigs.size(), "found correct number of zig cuts");
	}

	@Test
	public static void testMaxLegs() throws FileNotFoundException {
		final TrackWrapper track = getData("Ambig_tracks2.rep", "SENSOR");
		assertNotNull(track, "found track");

		// has sensors
		assertEquals(1, track.getSensors().size(), "has sensors");

		final Enumeration<Editable> sensorList = track.getSensors().elements();

		// make the sensor visible
		final SensorWrapper sensor = (SensorWrapper) sensorList.nextElement();
		sensor.setVisible(true);
		final TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor.getEndDTG());

		// check we have the correct sensor
		assertEquals("TA", sensor.getName(), "correct name");

		final Logger logger = Logger.getLogger("Logger");
		// try to get zones using ambiguity delta
		final LegsAndZigs res = AmbiguityResolver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, logger,
				null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL, timePeriod, 4);
		final List<LegOfCuts> legs = res.legs;
		final LegOfCuts zigs = res.zigCuts;

		assertNotNull(legs, "found zones");
		assertEquals(4, legs.size(), "found correct number of zones");

		assertNotNull(zigs, "found zigs");
		assertEquals(8, zigs.size(), "found correct number of zig cuts");

		assertEquals(121, sensor.size(), "cuts present");

		// ok, simulate moving along. Now delete the cuts.
		final HiResDate firstLegStart = legs.get(0).getStartDTG();
		final HiResDate lastLegEnd = legs.get(legs.size() - 1).getEndDTG();
		final TimePeriod period = new TimePeriod.BaseTimePeriod(firstLegStart, lastLegEnd);
		final List<Zone> zones = new ArrayList<Zone>();
		for (final LegOfCuts leg : legs) {
			final long startT = leg.getStartDTG().getDate().getTime();
			final long endT = leg.getEndDTG().getDate().getTime();
			final Zone newZone = new Zone(startT, endT, null);
			zones.add(newZone);
		}
		final LegOfCuts toDelete = BearingResidualsView.findCutsNotInZones(zones, period, track);

		// and delete them
		for (final SensorContactWrapper cut : toDelete) {
			cut.getSensor().removeElement(cut);
		}

		// check the cuts got deleted Note - this isn't the same as cuts.size() -
		// zigs.size(),
		// since I suspect we're constraining the cut period to the period of the
		// identified legs, which may ignore a period of zig cuts are the
		// last leg
		assertEquals(115, sensor.size(), "cuts deleted");

		// now we run the slicer gain

	}

	@Test
	public static void testMaxLegsUnspecified() throws FileNotFoundException {
		final TrackWrapper track = getData("Ambig_tracks2.rep", "SENSOR");
		assertNotNull(track, "found track");

		// has sensors
		assertEquals(1, track.getSensors().size(), "has sensors");

		final Enumeration<Editable> sensorList = track.getSensors().elements();

		// make the sensor visible
		final SensorWrapper sensor = (SensorWrapper) sensorList.nextElement();
		sensor.setVisible(true);
		final TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor.getEndDTG());

		// check we have the correct sensor
		assertEquals("TA", sensor.getName(), "correct name");

		final Logger logger = Logger.getLogger("Logger");
		// try to get zones using ambiguity delta
		final LegsAndZigs res = AmbiguityResolver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, logger,
				null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL, timePeriod, null);
		final List<LegOfCuts> legs = res.legs;
		final LegOfCuts zigs = res.zigCuts;

		assertNotNull(legs, "found zones");
		assertEquals(12, legs.size(), "found correct number of zones");

		assertNotNull(zigs, "found zigs");
		assertEquals(22, zigs.size(), "found correct number of zig cuts");
	}

	@Test
	public static void testOnlyDitchVisible() throws FileNotFoundException {
		final TrackWrapper track = getData("Ambig_tracks2.rep", "SENSOR");
		assertNotNull(track, "found track");

		// has sensors
		assertEquals(1, track.getSensors().size(), "has sensor");

		// make the sensor visible
		final SensorWrapper sensor = (SensorWrapper) track.getSensors().elements().nextElement();
		sensor.setVisible(true);
		final TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor.getEndDTG());

		// set some cuts to hidden
		int ctr = 0;
		final Enumeration<Editable> numer = sensor.elements();
		while (numer.hasMoreElements()) {
			final SensorContactWrapper scw = (SensorContactWrapper) numer.nextElement();
			if (ctr > 20 && ctr < 50) {
				scw.setVisible(false);
			}

			ctr++;
		}

		// try to get zones using ambiguity delta
		final LegsAndZigs res = AmbiguityResolver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, null, null,
				OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL, timePeriod, null);
		final List<LegOfCuts> legs = res.legs;
		final LegOfCuts zigs = res.zigCuts;

		assertNotNull(legs, "found zones");
		assertEquals(9, legs.size(), "found correct number of zones");

		assertNotNull(zigs, "found zigs");
		assertEquals(16, zigs.size(), "found correct number of zig cuts");
	}

	@Test
	public static void testOver20Legs() throws FileNotFoundException {
		final TrackWrapper track = getData("legs_20.rep", "T23");
		assertNotNull(track, "found track");

		// has sensors
		assertEquals(2, track.getSensors().size(), "has sensors");

		final Enumeration<Editable> sensorList = track.getSensors().elements();

		// skip the first sensor
		sensorList.nextElement();

		// make the sensor visible
		final SensorWrapper sensor = (SensorWrapper) sensorList.nextElement();
		sensor.setVisible(true);
		final TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor.getEndDTG());

		// check we have the correct sensor
		assertEquals("TA", sensor.getName(), "correct name");

		final Logger logger = null;// Logger.getLogger("Logger");
		// try to get zones using ambiguity delta
		final LegsAndZigs res = AmbiguityResolver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, logger,
				null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL, timePeriod, null);
		final List<LegOfCuts> legs = res.legs;
		final LegOfCuts zigs = res.zigCuts;

		assertNotNull(zigs, "found zigs");
		// note: before the (ambig - core) domain fold was corrected (CR-033) this
		// produced 6 zig cuts and 25 legs, including two spurious leg breaks
		// (13th, 22:43 & 23:23) while ownship was steady on 303.5
		assertEquals(4, zigs.size(), "found correct number of zig cuts");

		assertNotNull(legs, "found zones");
		assertEquals(23, legs.size(), "found correct number of zones");

		final List<ResolvedLeg> resolved = AmbiguityResolver.resolve(legs);
		assertEquals(23, resolved.size(), "resolved");

		// check they're all resolved
		for (final ResolvedLeg leg : resolved) {
			for (final SensorContactWrapper cut : leg.leg) {
				assertFalse(cut.getHasAmbiguousBearing(), "ambig cut silenced");
			}
		}
	}

	@Test
	public static void testResolve() throws FileNotFoundException {
		final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
		final SensorWrapper sensor = new SensorWrapper("name");
		final LegOfCuts leg1 = new LegOfCuts();
		leg1.add(wrapMe(sensor, 100, 180d, 270d));
		leg1.add(wrapMe(sensor, 110, 170d, 280d));
		leg1.add(wrapMe(sensor, 120, 160d, 290d));
		leg1.add(wrapMe(sensor, 130, 150d, 300d));
		leg1.add(wrapMe(sensor, 140, 140d, 310d));
		legs.add(leg1);

		final LegOfCuts leg2 = new LegOfCuts();
		leg2.add(wrapMe(sensor, 160, 182d, 220d));
		leg2.add(wrapMe(sensor, 170, 183d, 221d));
		leg2.add(wrapMe(sensor, 180, 184d, 222d));
		leg2.add(wrapMe(sensor, 190, 185d, 223d));
		leg2.add(wrapMe(sensor, 200, 186d, 224d));
		legs.add(leg2);

		final LegOfCuts leg3 = new LegOfCuts();
		leg3.add(wrapMe(sensor, 220, 92d, 200d));
		leg3.add(wrapMe(sensor, 230, 83d, 210d));
		leg3.add(wrapMe(sensor, 240, 74d, 220d));
		leg3.add(wrapMe(sensor, 250, 65d, 230d));
		leg3.add(wrapMe(sensor, 260, 56d, 240d));
		legs.add(leg3);

		final LegOfCuts leg4 = new LegOfCuts();
		leg4.add(wrapMe(sensor, 280, 92d, 260d));
		leg4.add(wrapMe(sensor, 290, 73d, 280d));
		leg4.add(wrapMe(sensor, 300, 54d, 300d));
		leg4.add(wrapMe(sensor, 310, 35d, 320d));
		leg4.add(wrapMe(sensor, 320, 16d, 340d));
		leg4.add(wrapMe(sensor, 330, 9d, 0d));
		leg4.add(wrapMe(sensor, 340, 355d, 20d));
		legs.add(leg4);

		// put the good cut in the wrong domain
		final LegOfCuts leg5 = new LegOfCuts();
		leg5.add(wrapMe(sensor, 360, 41d, 260d));
		leg5.add(wrapMe(sensor, 370, 43d, 240d));
		leg5.add(wrapMe(sensor, 380, 45d, 220d));
		leg5.add(wrapMe(sensor, 390, 47d, 200d));
		leg5.add(wrapMe(sensor, 400, 49d, 180d));
		leg5.add(wrapMe(sensor, 410, 51d, 160d));
		leg5.add(wrapMe(sensor, 420, 53d, 140d));
		legs.add(leg5);

		// make the first cuts very wonky
		final LegOfCuts leg6 = new LegOfCuts();
		leg6.add(wrapMe(sensor, 440, 141d, 350d));
		leg6.add(wrapMe(sensor, 450, 143d, 20d));
		leg6.add(wrapMe(sensor, 460, 145d, 70d));
		leg6.add(wrapMe(sensor, 470, 147d, 80d));
		leg6.add(wrapMe(sensor, 480, 149d, 90d));
		leg6.add(wrapMe(sensor, 490, 151d, 100d));
		leg6.add(wrapMe(sensor, 500, 153d, 110d));
		legs.add(leg6);

		final List<ResolvedLeg> resolvedLegs = AmbiguityResolver.resolve(legs);

		assertNotNull(resolvedLegs, "have list of resolved");
		assertEquals(6, resolvedLegs.size(), "correct num legs");

		// ok, check the legs
		assertFalse(leg1.get(0).getHasAmbiguousBearing(), "not ambig");
		assertFalse(leg2.get(0).getHasAmbiguousBearing(), "not ambig");
		assertFalse(leg3.get(0).getHasAmbiguousBearing(), "not ambig");
		// assertFalse("not ambig", leg4.get(0).getHasAmbiguousBearing());
		// assertFalse("not ambig", leg5.get(0).getHasAmbiguousBearing());
		// assertFalse("not ambig", leg6.get(0).getHasAmbiguousBearing());
		//
		// assertEquals("correct bearing", 180d, leg1.get(0).getBearing());
		// assertEquals("correct bearing", 182d, leg2.get(0).getBearing());
		// assertEquals("correct bearing", 200d, leg3.get(0).getBearing());
		// assertEquals("correct bearing", 260d, leg4.get(0).getBearing());
		// assertEquals("correct bearing", 41d, leg5.get(0).getBearing());
		// assertEquals("correct bearing", 350d, leg6.get(0).getBearing());

	}

	/**
	 * check that we allow a couple of apparently steady cuts during a turn
	 *
	 * @throws FileNotFoundException
	 */
	@Test
	public static void testSteadyInTurn() throws FileNotFoundException {
		final SensorWrapper sensor = new SensorWrapper("name");
		sensor.add(wrapMe(sensor, 100000, 180d, 270d));
		sensor.add(wrapMe(sensor, 110000, 170d, 280d));
		sensor.add(wrapMe(sensor, 120000, 160d, 290d));
		sensor.add(wrapMe(sensor, 130000, 150d, 300d));
		sensor.add(wrapMe(sensor, 140000, 140d, 310d));
		sensor.add(wrapMe(sensor, 150000, 130d, 310d));
		sensor.add(wrapMe(sensor, 160000, 122d, 220d));
		sensor.add(wrapMe(sensor, 170000, 113d, 221d));
		sensor.add(wrapMe(sensor, 180000, 104d, 222d));
		sensor.add(wrapMe(sensor, 190000, 095d, 223d));
		sensor.add(wrapMe(sensor, 200000, 086d, 224d));
		sensor.add(wrapMe(sensor, 210000, 076d, 224d));
		sensor.add(wrapMe(sensor, 220000, 62d, 200d));
		sensor.add(wrapMe(sensor, 230000, 53d, 210d));
		sensor.add(wrapMe(sensor, 240000, 44d, 220d));
		sensor.add(wrapMe(sensor, 250000, 35d, 230d));
		sensor.add(wrapMe(sensor, 260000, 26d, 240d));
		sensor.add(wrapMe(sensor, 270000, 36d, 240d));
		sensor.add(wrapMe(sensor, 280000, 42d, 260d));
		sensor.add(wrapMe(sensor, 290000, 53d, 280d));
		sensor.add(wrapMe(sensor, 300000, 64d, 300d));
		sensor.add(wrapMe(sensor, 310000, 75d, 320d));
		sensor.add(wrapMe(sensor, 320000, 66d, 340d));
		sensor.add(wrapMe(sensor, 330000, 56d, 0d));
		sensor.add(wrapMe(sensor, 340000, 45d, 20d));
		sensor.add(wrapMe(sensor, 350000, 35d, 30d));
		sensor.add(wrapMe(sensor, 360000, 35d, 30d));
		sensor.add(wrapMe(sensor, 370000, 15d, 70d));
		sensor.add(wrapMe(sensor, 380000, 355d, 20d));
		sensor.add(wrapMe(sensor, 390000, 355d, 20d));
		sensor.add(wrapMe(sensor, 400000, 345d, 20d));
		// sensor.add(wrapMe(sensor, 410000, 345d, 20d));

		sensor.setVisible(true);
		final TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor.getEndDTG());

		final TrackWrapper host = new TrackWrapper();
		host.setName("Host");
		host.add(sensor);

		final Logger logger = Logger.getLogger("Test output");
		logger.setUseParentHandlers(false);
		logger.addHandler(new ConsoleHandler() {

			@Override
			public void publish(final LogRecord record) {
				System.out.println(record.getMessage());
			}
		});

		final LegsAndZigs sliced = AmbiguityResolver.sliceTrackIntoLegsUsingAmbiguity(host, 2.2, 0.2, 22, logger,
				null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL, timePeriod, null);

		assertNotNull(sliced, "produced slices");
		assertEquals(4, sliced.legs.size(), "correct legs");
		assertEquals(13, sliced.zigCuts.size(), "correct turning cuts");
	}

	/**
	 * ambiguous bearing passes through north (and out of the 'near north' zone)
	 * on a steady leg. The (ambig - core) delta changes domain, which shouldn't
	 * be treated as a zig.
	 */
	@Test
	public static void testSteadyAmbigThroughNorth() {
		final SensorWrapper sensor = new SensorWrapper("name");
		for (int i = 0; i <= 24; i++) {
			final double core = 100d + i;
			final double ambig = Conversions.normaliseDegs(60d - 5d * i);
			sensor.add(wrapMe(sensor, 100000 + i * 10000, core, ambig));
		}

		sensor.setVisible(true);
		final TimePeriod timePeriod = new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor.getEndDTG());

		final TrackWrapper host = new TrackWrapper();
		host.setName("Host");
		host.add(sensor);

		final LegsAndZigs sliced = AmbiguityResolver.sliceTrackIntoLegsUsingAmbiguity(host, 2.2, 0.2, 22, null,
				null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL, timePeriod, null);

		assertNotNull(sliced, "produced slices");
		assertEquals(0, sliced.zigCuts.size(), "no turning cuts");
		assertEquals(1, sliced.legs.size(), "single leg");
	}

	/**
	 * the permutation score should be the smallest angle between the bearings,
	 * even if the curves have unwrapped a long way
	 */
	@Test
	public static void testPermScoreFold() {
		final LegPermutation last = new LegPermutation(null, null, null, null, null);
		final LegPermutation next = new LegPermutation(null, null, null, null, null);
		last.coreAfter = 10;
		last.ambigAfter = 10;

		next.coreBefore = 10 + 720 + 5;
		next.ambigBefore = 10 - 720 - 5;
		assertEquals(5d, new PermScore(last, next, WhichBearing.CORE, WhichBearing.CORE).thisScore, 0.0001,
				"core folded");
		assertEquals(5d, new PermScore(last, next, WhichBearing.AMBIGUOUS, WhichBearing.AMBIGUOUS).thisScore,
				0.0001, "ambig folded");

		next.coreBefore = 10 + 350;
		assertEquals(10d, new PermScore(last, next, WhichBearing.CORE, WhichBearing.CORE).thisScore,
				0.0001, "simple fold");
	}

	@Test
	public static void testTrim() {
		assertEquals(5d, trim(365, null), "trim as normal");
		assertEquals(-5d, trim(-365, null), "trim as normal");
		assertEquals(65d, trim(65, null), "trim as normal");
		assertEquals(-65d, trim(-65, null), "trim as normal");
		assertEquals(-10d, trim(-370, null), "trim as normal");

		// ok, give it a target value, and check we're in the correct domain
		assertEquals(365d, trim(365, 340d), "trim as normal");
		assertEquals(-300d, trim(-300, -340d), "trim as normal");

		// ooh, what if we're looking for a high value,
		// but only receive a low value
		assertEquals(365d, trim(5, 340d), "trim as normal");
		assertEquals(-370d, trim(-10, -340d), "trim as normal");
	}

	@Test
	public static void testWeighting() {
		final SensorWrapper sensor = new SensorWrapper("name");
		final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();

		final LegOfCuts leg1 = new LegOfCuts();
		leg1.add(wrapMe(sensor, 280, 92d, 260d));
		leg1.add(wrapMe(sensor, 290, 73d, 280d));
		leg1.add(wrapMe(sensor, 300, 54d, 300d));
		leg1.add(wrapMe(sensor, 310, 35d, 320d));
		leg1.add(wrapMe(sensor, 320, 16d, 340d));
		leg1.add(wrapMe(sensor, 330, 9d, 0d));
		leg1.add(wrapMe(sensor, 340, 355d, 20d));
		legs.add(leg1);

		// put the good cut in the wrong domain
		final LegOfCuts leg2 = new LegOfCuts();
		leg2.add(wrapMe(sensor, 360, 42d, 260d));
		leg2.add(wrapMe(sensor, 370, 43d, 240d));
		leg2.add(wrapMe(sensor, 380, 45d, 220d));
		leg2.add(wrapMe(sensor, 390, 47d, 200d));
		leg2.add(wrapMe(sensor, 400, 49d, 180d));
		leg2.add(wrapMe(sensor, 410, 51d, 160d));
		leg2.add(wrapMe(sensor, 420, 53d, 140d));
		legs.add(leg2);

		// make the first cuts very wonky
		final LegOfCuts leg3 = new LegOfCuts();
		leg3.add(wrapMe(sensor, 440, 141d, 350d));
		leg3.add(wrapMe(sensor, 450, 143d, 20d));
		leg3.add(wrapMe(sensor, 460, 145d, 70d));
		leg3.add(wrapMe(sensor, 470, 147d, 80d));
		leg3.add(wrapMe(sensor, 480, 149d, 90d));
		leg3.add(wrapMe(sensor, 490, 151d, 100d));
		leg3.add(wrapMe(sensor, 500, 153d, 110d));
		legs.add(leg3);

		final List<ResolvedLeg> resolvedLegs = AmbiguityResolver.resolve(legs);

		assertNotNull(resolvedLegs, "have legs");
		assertEquals(260d, leg1.get(0).getBearing(), "correct bearing");
		assertEquals(42d, leg2.get(0).getBearing(), "correct bearing");
		assertEquals(350d, leg3.get(0).getBearing(), "correct bearing");
	}

	private static SensorContactWrapper wrapMe(final SensorWrapper sensor, final long dtg, final double bearing1,
			final double bearing2) {
		return new SensorContactWrapper("track", new HiResDate(dtg), null, bearing1, bearing2, null, null,
				Color.RED, "label", 0, sensor.getName());
	}

	private void getPerms(final List<ArrayList<WhichBearing>> results, final int ctr,
			final List<WhichBearing> thisList, final WhichBearing newPerm) {
		final ArrayList<WhichBearing> newList = new ArrayList<WhichBearing>();

		if (thisList != null) {
			newList.addAll(thisList);
		}

		if (newPerm != null) {
			newList.add(newPerm);
		}

		if (ctr > 0) {
			// ok, add the two new perms
			final int thisCtr = ctr - 1;
			getPerms(results, thisCtr, newList, WhichBearing.CORE);
			getPerms(results, thisCtr, newList, WhichBearing.AMBIGUOUS);
		} else {
			// ok, finished. store the results
			results.add(newList);
		}
	}

	@Test
	public void testGenPerms() {
		final List<ArrayList<WhichBearing>> results = new ArrayList<ArrayList<WhichBearing>>();
		final int ctr = 6;
		final List<WhichBearing> thisList = null;
		final WhichBearing newPerm = null;

		getPerms(results, ctr, thisList, newPerm);
		System.out.println(results.size());
		assertEquals((int) Math.pow(2, ctr), results.size(), "enough perms");

		results.clear();

		final ArrayList<WhichBearing> starter = new ArrayList<WhichBearing>();
		starter.add(WhichBearing.CORE);
		starter.add(WhichBearing.AMBIGUOUS);

		results.add(starter);

		// try another way
		for (int i = 0; i < ctr; i++) {
			final ArrayList<ArrayList<WhichBearing>> newList = new ArrayList<ArrayList<WhichBearing>>();
			for (final ArrayList<WhichBearing> list : results) {
				final ArrayList<WhichBearing> permA = new ArrayList<WhichBearing>();
				permA.addAll(list);
				final ArrayList<WhichBearing> permB = new ArrayList<WhichBearing>();
				permB.addAll(list);
				permA.add(WhichBearing.CORE);
				permB.add(WhichBearing.AMBIGUOUS);

				newList.add(permA);
				newList.add(permB);
			}

			results.clear();
			results.addAll(newList);
		}

		System.out.println(results.size());
		assertEquals((int) Math.pow(2, ctr), results.size(), "enough perms");

		final Iterator<Boolean[]> iter = bool(ctr);
		int numGenerated = 0;
		while (iter.hasNext()) {
			@SuppressWarnings("unused")
			final Boolean[] perm = iter.next();
			numGenerated++;
		}

		assertEquals((int) Math.pow(2, ctr), numGenerated, "enough perms");

	}
}
