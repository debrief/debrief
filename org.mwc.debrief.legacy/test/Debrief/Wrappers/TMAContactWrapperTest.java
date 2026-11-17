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

import static Debrief.Wrappers.TMAContactWrapper.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.Wrappers.TMAContactWrapper.*;
import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import Debrief.GUI.Tote.Painters.SnailDrawTMAContact;
import MWC.Algorithms.Conversions;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GeneralFormat;
import org.junit.jupiter.api.Test;

// ///////////////////////////////////////////
// member variables
// ///////////////////////////////////////////

public final class TMAContactWrapperTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * several solutions at the same DTG must all be stored, and each one must be
	 * removable
	 */
	@Test
	public final void testEqualTimeSolutions() {
		final TMAWrapper tw = new TMAWrapper("sol");
		final java.util.List<TMAContactWrapper> items = new java.util.ArrayList<TMAContactWrapper>();
		items.add(new TMAContactWrapper("sol", "trk", new HiResDate(5000), 1, 0, 0, 0, 0, null, "a", null, null));
		for (int i = 0; i < 5; i++) {
			items.add(new TMAContactWrapper("sol", "trk", new HiResDate(10000), 1, i, 0, 0, 0, null, "b" + i, null,
					null));
		}
		items.add(new TMAContactWrapper("sol", "trk", new HiResDate(20000), 1, 0, 0, 0, 0, null, "c", null, null));
		for (final TMAContactWrapper item : items) {
			tw.add(item);
		}
		assertEquals(items.size(), tw._myContacts.size(), "all stored");

		for (final TMAContactWrapper a : items) {
			for (final TMAContactWrapper b : items) {
				if (a != b) {
					assertTrue(a.compareTo(b) != 0, "distinct");
					assertEquals(Integer.signum(a.compareTo(b)), -Integer.signum(b.compareTo(a)), "antisymmetric");
				}
			}
		}

		int expected = items.size();
		for (final int i : new int[] { 3, 1, 5, 2, 4, 0, 6 }) {
			final TMAContactWrapper item = items.get(i);
			tw.removeElement(item);
			expected--;
			assertEquals(expected, tw._myContacts.size(), "removed item " + i);
			for (final Editable ed : tw._myContacts) {
				assertNotSame(item, ed, "item " + i + " gone");
			}
		}
	}

	@Test
	public final void testMyCalcs() {
		// setup our object to be tested using an absolute location
		final WorldLocation origin = new WorldLocation(2, 2, 0);
		final EllipseShape es = new EllipseShape(origin, 0,
				new WorldDistance(Conversions.Yds2Degs(100), WorldDistance.DEGS),
				new WorldDistance(Conversions.Yds2Degs(50), WorldDistance.DEGS));
		final HiResDate theDTG = new HiResDate(new java.util.Date().getTime());
		final TMAContactWrapper ed = new TMAContactWrapper("blank sensor", "blank track", theDTG, origin, 5d, 6d,
				1d, Color.red, "my label", es, "some symbol");

		/**
		 * test the distance calcs
		 */

		// ok, now test that we find the distance from the origin
		final double dist = Conversions.Degs2Yds(ed.rangeFrom(origin));
		assertEquals(dist, 0d, 0.001, "find nearest from origin");

	}

	@Test
	public final void testMyParams() {
		final HiResDate theDTG = new HiResDate(new java.util.Date().getTime());

		// setup our object to be tested using an absolute location
		final TMAContactWrapper ed = new TMAContactWrapper("blank sensor", "blank track", theDTG, 3000, 55, 5d, 6d,
				1d, Color.red, "my label", null, "some symbol");

		final TMAWrapper wrap = new TMAWrapper("tma");
		ed.setTMATrack(wrap);

		// check the editable parameters
		Editable.editableTesterSupport.testParams(ed);
	}

	@Test
	public final void testObjectConstruction() {
		// setup our object to be tested using an absolute location
		final WorldLocation origin = new WorldLocation(2, 2, 0);
		final HiResDate theDTG = new HiResDate(new java.util.Date().getTime());
		final EllipseShape theEllipse = new EllipseShape(origin, 45, new WorldDistance(10, WorldDistance.DEGS),
				new WorldDistance(5, WorldDistance.DEGS));
		final TMAContactWrapper ed_abs = new TMAContactWrapper("blank sensor", "blank track", theDTG, origin, 5d,
				6d, 1d, Color.pink, "my label", theEllipse, "some symbol");

		assertEquals(ed_abs._trackName, "blank sensor", "correct sensor name");
		assertEquals(ed_abs._parentTrackName, "blank track", "correct track name");
		assertEquals(ed_abs._DTG, theDTG, "correct DTG");
		assertEquals(ed_abs._theEllipse.getCentre(), origin, "correct origin");
		assertEquals(ed_abs._targetCourseDegs, 5d, 0.001, "right course");
		assertEquals(ed_abs.getCourse(), Conversions.Degs2Rads(5d), 0.001, "right course");
		assertEquals(ed_abs._targetSpeedKts, 6d, 0.001, "right speed");
		assertEquals(ed_abs.getSpeed(), 6d, 0.001, "right speed");
		assertEquals(ed_abs._targetDepth, 1d, 0.001, "right depth");
		assertEquals(ed_abs.getDepth(), 1d, 0.001, "right depth");
		assertEquals(ed_abs._labelledEllipse.getColor(), Color.pink, "correct colour");
		assertEquals(ed_abs.getColor(), Color.pink, "correct colour");
		assertEquals(ed_abs._labelledEllipse.getLabel(), "my label", "correct label");
		assertEquals(ed_abs.getLabel(), "my label", "correct label");
		assertEquals(ed_abs._theEllipse, theEllipse, "correct ellipse");
		assertEquals(ed_abs._labelledEllipse.getShape(), theEllipse, "correct ellipse");
		assertEquals(ed_abs._theSymbol, "some symbol", "correct symbol");

		// setup our object to be tested using an relative location
		final TMAContactWrapper ed_rel = new TMAContactWrapper("blank sensor", "blank track", theDTG, 3000, 55, 5d,
				6d, 1d, Color.pink, "my label", theEllipse, "some symbol");

		assertEquals(ed_rel._trackName, "blank sensor", "correct sensor name");
		assertEquals(ed_rel._parentTrackName, "blank track", "correct track name");
		assertEquals(ed_rel._DTG, theDTG, "correct DTG");
		assertEquals(ed_rel._theEllipse.getCentre(), null, "correct origin");
		assertEquals(3000, ed_rel._targetRange.getValueIn(WorldDistance.YARDS), 0.001, "correct range");
		assertEquals(ed_rel._targetCourseDegs, 5d, 0.001, "correct bearing");
		assertEquals(ed_rel._targetCourseDegs, 5d, 0.001, "right course");
		assertEquals(ed_rel.getCourse(), Conversions.Degs2Rads(5d), 0.001, "right course");
		assertEquals(ed_rel._targetSpeedKts, 6d, 0.001, "right speed");
		assertEquals(ed_rel.getSpeed(), 6d, 0.001, "right speed");
		assertEquals(ed_rel._targetDepth, 1d, 0.001, "right depth");
		assertEquals(ed_rel.getDepth(), 1d, 0.001, "right depth");
		assertEquals(ed_rel._labelledEllipse.getColor(), Color.pink, "correct colour");
		assertEquals(ed_rel.getColor(), Color.pink, "correct colour");
		assertEquals(ed_rel._labelledEllipse.getLabel(), "my label", "correct label");
		assertEquals(ed_rel.getLabel(), "my label", "correct label");
		assertEquals(ed_rel._theEllipse, theEllipse, "correct ellipse");
		assertEquals(ed_rel._labelledEllipse.getShape(), theEllipse, "correct ellipse");
		assertEquals(ed_rel._theSymbol, "some symbol", "correct symbol");

	}

}
