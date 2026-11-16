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

import static Debrief.Wrappers.SensorContactWrapper.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.Wrappers.SensorContactWrapper.*;
import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import Debrief.GUI.Frames.Application;
import Debrief.GUI.Tote.Painters.SnailDrawTMAContact;
import Debrief.Wrappers.Track.ArrayOffsetHelper;
import Debrief.Wrappers.Track.ArrayOffsetHelper.LegacyArrayOffsetModes;
import Debrief.Wrappers.Track.Doublet;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.Algorithms.Conversions;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineLocationPropertyEditor;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GUI.Shapes.TextLabel;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

public final class SensorContactWrapperTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	private static void assertOrderingContract(final java.util.List<? extends Plottable> items) {
		for (final Plottable a : items) {
			assertEquals(0, a.compareTo(a), "reflexive");
			for (final Plottable b : items) {
				if (a != b) {
					final int ab = a.compareTo(b);
					assertTrue(ab != 0, "distinct items not equal");
					assertEquals(Integer.signum(ab), -Integer.signum(b.compareTo(a)), "antisymmetric");
				}
			}
		}
	}

	private static SensorContactWrapper cutAt(final long millis, final double brg) {
		return new SensorContactWrapper("trk", new HiResDate(millis), null, brg, null, Color.RED, "c" + brg, 0,
				"sensor");
	}

	/**
	 * several cuts at the same DTG (e.g. multi-target sonar) must all be stored,
	 * and each one must be removable
	 */
	@Test
	public final void testEqualTimeCuts() {
		final SensorWrapper sw = new SensorWrapper("sensor");
		final java.util.List<SensorContactWrapper> cuts = new java.util.ArrayList<SensorContactWrapper>();
		cuts.add(cutAt(5000, 1));
		for (int i = 0; i < 5; i++) {
			cuts.add(cutAt(10000, 10 + i));
		}
		cuts.add(cutAt(20000, 20));
		for (final SensorContactWrapper cut : cuts) {
			sw.add(cut);
		}
		assertEquals(cuts.size(), sw._myContacts.size(), "all stored");
		assertOrderingContract(cuts);

		// equal-time items stay in the order they were created
		final java.util.Iterator<Editable> iter = sw._myContacts.iterator();
		for (final SensorContactWrapper cut : cuts) {
			assertSame(cut, iter.next(), "insertion order kept");
		}

		// and a serialized copy (as used by copy/paste) keeps them all, and they
		// can be removed
		final SensorWrapper copy = (SensorWrapper) MWC.GUI.Tools.Operations.CloneUtil.cloneThis(sw);
		assertEquals(cuts.size(), copy._myContacts.size(), "all copied");
		final java.util.List<Editable> copiedCuts = new java.util.ArrayList<Editable>(copy._myContacts);
		for (final Editable cut : copiedCuts) {
			copy.removeElement(cut);
			assertFalse(copy._myContacts.contains(cut), "copied cut removed");
		}
		assertEquals(0, copy._myContacts.size(), "copy emptied");

		// a copy of a cut can be pasted back alongside the original
		final SensorContactWrapper dupe = (SensorContactWrapper) MWC.GUI.Tools.Operations.CloneUtil
				.cloneThis(cuts.get(2));
		sw.add(dupe);
		assertEquals(cuts.size() + 1, sw._myContacts.size(), "duplicate stored");
		sw.removeElement(dupe);
		assertEquals(cuts.size(), sw._myContacts.size(), "duplicate removed");

		// now delete them, one at a time, from the middle outwards
		int expected = cuts.size();
		for (final int i : new int[] { 3, 1, 5, 2, 4, 0, 6 }) {
			final SensorContactWrapper cut = cuts.get(i);
			sw.removeElement(cut);
			expected--;
			assertEquals(expected, sw._myContacts.size(), "removed cut " + i);
			for (final Editable ed : sw._myContacts) {
				assertNotSame(cut, ed, "cut " + i + " gone");
			}
		}
	}

	@Test
	public final void testMyCode() {
		// setup our object to be tested
		final WorldLocation origin = new WorldLocation(0, 0, 0);
		final SensorContactWrapper ed = new SensorContactWrapper("blank track",
				new HiResDate(new java.util.Date().getTime()), new WorldDistance(1, WorldDistance.DEGS), 55d,
				origin, DebriefColors.RED, "my label", 1, "theSensorName");

		// check the editable parameters
		editableTesterSupport.testParams(ed);

		/**
		 * test the distance calcs
		 */
		final WorldVector test_other_vector = new WorldVector(Conversions.Degs2Rads(55), 1, 0);
		final WorldLocation test_other_end = origin.add(test_other_vector);

		final SensorWrapper sw = new SensorWrapper("dummy");
		sw.setHost(new TrackWrapper());
		ed.setSensor(sw);
		ed.setOrigin(origin);

		// ok, now test that we find the distance from the indicated point
		final double dist = ed.rangeFrom(test_other_end);
		assertEquals(0d, dist, 0.001, "find nearest from origin");

	}

	@Test
	public final void testRelBearingCalc() {
		assertEquals(120d, relBearing(0, 120), "test  1");
		assertEquals(-120d, relBearing(0, -120), "test  1");
		assertEquals(20d, relBearing(100, 120), "test  1");
		assertEquals(-120d, relBearing(100, -20), "test  1");
		assertEquals(20d, relBearing(100, -240), "test  1");
		assertEquals(-120d, relBearing(-260, 340), "test  1");
		assertEquals(20d, relBearing(-260, 120), "test  1");
		assertEquals(-120d, relBearing(100, 340), "test  1");
		assertEquals(-10d, relBearing(350, -20), "test  1");
		assertEquals(5d, relBearing(350, -5), "test  1");
		assertEquals(170d, relBearing(170, -20), "test  1");
		assertEquals(-175d, relBearing(170, -5), "test  1");
		assertEquals(-20d, relBearing(170, 150), "test  1");
		assertEquals(20d, relBearing(170, 190), "test  1");
	}

	@Test
	public void testSensorOffset() {
		final WorldLocation locationRes = new WorldLocation(0, 0, 0);
		final WorldLocation locationBitNorth = new WorldLocation(1, 0, 0);
		final WorldLocation locationBitSouth = new WorldLocation(-1, 0, 0);
		final WorldLocation locationBitEast = new WorldLocation(0, 1, 0);
		final WorldLocation location = new WorldLocation(0, 0, 0);
		final WorldLocation location2 = new WorldLocation(0, 1, 0);
		final WorldLocation locationBitNorth2 = new WorldLocation(1, 1, 0);
		final HiResDate theDate = new HiResDate(1000);

		final SensorWrapper sw = new SensorWrapper("some sensor");
		final SensorContactWrapper scw = new SensorContactWrapper();
		scw.setDTG(theDate);
		sw.add(scw);

		final Fix fx1 = new Fix();
		fx1.setLocation(location);
		fx1.setTime(theDate);
		fx1.setCourse(0);
		final FixWrapper fw1 = new FixWrapper(fx1);
		final TrackWrapper host = new TrackWrapper();
		host.addFix(fw1);
		final Fix fx2 = new Fix();
		fx2.setLocation(location);
		fx2.setTime(new HiResDate(2000));
		fx2.setCourse(0);
		final FixWrapper fw2 = new FixWrapper(fx2);
		host.add(fw2);

		host.add(sw);

		WorldLocation wl = scw.getOrigin();
		assertNull(wl, "should not be a location yet");

		// ok, and try to do it from the parent
		wl = scw.getCalculatedOrigin(host);
		assertNotNull(wl, "should be a location");
		assertEquals(locationRes, wl, "should be fix location");

		// now give it an offset
		sw.setSensorOffset(new WorldDistance.ArrayLength(new WorldDistance(1, WorldDistance.DEGS)));
		sw.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);

		// and try again
		wl = scw.getCalculatedOrigin(host);
		assertNotNull(wl, "should be a location");
		assertEquals(locationBitNorth, wl, "should be offset location");

		// and try again, with a negative offset
		sw.setSensorOffset(new WorldDistance.ArrayLength(new WorldDistance(-1, WorldDistance.DEGS)));
		wl = scw.getCalculatedOrigin(host);
		assertNotNull(wl, "should be a location");
		assertEquals(locationBitSouth, wl, "should be offset location");

		// and try again, giving the host a course this time
		fx1.setCourse(Conversions.Degs2Rads(90.0));
		fx2.setCourse(Conversions.Degs2Rads(90.0));
		sw.setSensorOffset(new WorldDistance.ArrayLength(new WorldDistance(1, WorldDistance.DEGS)));
		wl = scw.getCalculatedOrigin(host);
		assertNotNull(wl, "should be a location");
		assertEquals(locationBitEast, wl, "should be centre of rectangle");

		// check offset knows about track being shifted
		fx1.setCourse(Conversions.Degs2Rads(0.0));
		fw1.setFixLocation(location2);
		fx2.setCourse(Conversions.Degs2Rads(0.0));
		fw2.setFixLocation(location2);
		wl = scw.getCalculatedOrigin(host);
		assertNotNull(wl, "should be a location");
		assertEquals(locationBitNorth2, wl, "should be centre of rectangle");
	}

}
