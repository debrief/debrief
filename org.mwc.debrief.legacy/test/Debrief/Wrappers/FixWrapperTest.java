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

import static Debrief.Wrappers.FixWrapper.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.Wrappers.FixWrapper.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.Vector;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Test;
import MWC.GUI.CanPlotFaded;
import MWC.GUI.CanvasType;
import MWC.GUI.CreateEditorForParent;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GUI.Properties.MyDateFormatPropertyEditor;
import MWC.GUI.Properties.NullableLocationPropertyEditor;
import MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import MWC.Utilities.TextFormatting.GMTDateFormat;
import MWC.Utilities.TextFormatting.GeneralFormat;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class FixWrapperTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#add(MWC.GUI.Editable)} .
	 */
	@Test
	public void testCreateMethods() {
		final FixWrapper fw1 = TrackWrapper_Test.createFix2(100, 1, 1, 2, 3);
		final FixWrapper fw2 = TrackWrapper_Test.createFix2(200, 2, 1, 2, 3);
		final FixWrapper fw3 = TrackWrapper_Test.createFix2(300, 2, 1, 2, 3);
		final FixWrapper fw4 = TrackWrapper_Test.createFix2(400, 2, 1, 2, 3);
		final FixWrapper fw5 = TrackWrapper_Test.createFix2(500, 2, 1, 2, 3);
		final FixWrapper fw6 = TrackWrapper_Test.createFix2(600, 2, 1, 2, 3);

		final TrackSegment ats = new TrackSegment(false);
		ats.add(fw1);
		ats.add(fw2);
		ats.add(fw3);
		ats.add(fw4);
		ats.add(fw5);
		ats.add(fw6);

		final TrackWrapper track = new TrackWrapper();
		track.setName("name");
		track.add(ats);

		EditorType info = fw1.getInfo();
		SubjectAction[] methods = info.getUndoableActions();
		assertNotNull(methods, "found methods");
		assertEquals(2, methods.length);
		assertEquals(methods[0].getClass(), new CantDoIt("aa").getClass());
		assertEquals(methods[1].getClass(), new CantDoIt("aa").getClass());

		info = fw2.getInfo();
		methods = info.getUndoableActions();
		assertNotNull(methods, "found methods");
		assertEquals(2, methods.length);
		assertEquals(methods[0].getClass(), new CantDoIt("aa").getClass());
		assertTrue(methods[1].getClass() != new CantDoIt("aa").getClass());

		info = fw3.getInfo();
		methods = info.getUndoableActions();
		assertNotNull(methods, "found methods");
		assertEquals(2, methods.length);
		assertTrue(methods[0].getClass() != new CantDoIt("aa").getClass());
		assertTrue(methods[1].getClass() != new CantDoIt("aa").getClass());

		info = fw4.getInfo();
		methods = info.getUndoableActions();
		assertNotNull(methods, "found methods");
		assertEquals(2, methods.length);
		assertTrue(methods[0].getClass() != new CantDoIt("aa").getClass());
		assertTrue(methods[1].getClass() != new CantDoIt("aa").getClass());

		info = fw5.getInfo();
		methods = info.getUndoableActions();
		assertNotNull(methods, "found methods");
		assertEquals(2, methods.length);
		assertTrue(methods[0].getClass() != new CantDoIt("aa").getClass());
		assertEquals(methods[1].getClass(), new CantDoIt("aa").getClass());

		info = fw6.getInfo();
		methods = info.getUndoableActions();
		assertNotNull(methods, "found methods");
		assertEquals(2, methods.length);
		assertEquals(methods[0].getClass(), new CantDoIt("aa").getClass());
		assertEquals(methods[1].getClass(), new CantDoIt("aa").getClass());

	}

	/**
	 * Test method for {@link Debrief.Wrappers.TrackWrapper#add(MWC.GUI.Editable)} .
	 */
	@Test
	public void testInterpolate() {
		FixWrapper fw1 = TrackWrapper_Test.createFix2(100, 1, 1, 2, 3);
		FixWrapper fw2 = TrackWrapper_Test.createFix2(200, 2, 1, 2, 3);
		FixWrapper fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(150));
		assertEquals(150, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(1.5, fw3.getLocation().getLat(), 0.001, "right lat");
		assertEquals(1, fw3.getLocation().getLong(), 0.0001, "right lat");

		fw1 = TrackWrapper_Test.createFix2(100, 1, 1, 2, 1);
		fw2 = TrackWrapper_Test.createFix2(200, 2, 2, 10, 3);
		fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(150));
		assertEquals(150, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(MWC.Algorithms.Conversions.Degs2Rads(6), fw3.getCourse(), 0.001, "right course");
		assertEquals(2, fw3.getSpeed(), 0.0001, "right speed");

		fw1 = TrackWrapper_Test.createFix2(100, 1, 1, 20, 30);
		fw2 = TrackWrapper_Test.createFix2(200, 2, 2, 10, 10);
		fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(125));
		assertEquals(125, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(MWC.Algorithms.Conversions.Degs2Rads(17.5), fw3.getCourse(), 0.001, "right course");
		assertEquals(25, fw3.getSpeed(), 0.0001, "right speed");

		fw1 = TrackWrapper_Test.createFix2(100, 1, 1, 2, 3);
		fw2 = TrackWrapper_Test.createFix2(200, 2, 1, 2, 3);
		fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(140));
		assertEquals(140, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(1.4, fw3.getLocation().getLat(), 0.001, "right lat");
		assertEquals(1, fw3.getLocation().getLong(), 0.0001, "right lat");

		fw1 = TrackWrapper_Test.createFix2(100, 1, 21, 2, 3);
		fw2 = TrackWrapper_Test.createFix2(200, 2, 21, 2, 3);
		fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(140));
		assertEquals(140, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(1.4, fw3.getLocation().getLat(), 0.001, "right lat");
		assertEquals(21, fw3.getLocation().getLong(), 0.0001, "right lat");

		fw1 = TrackWrapper_Test.createFix2(100, 41, 21, 2, 3);
		fw2 = TrackWrapper_Test.createFix2(200, 42, 21, 2, 3);
		fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(140));
		assertEquals(140, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(41.4, fw3.getLocation().getLat(), 0.001, "right lat");
		assertEquals(21, fw3.getLocation().getLong(), 0.0001, "right lat");

		fw1 = TrackWrapper_Test.createFix1(100, 60, 30, 0, 31, 0, 0, 2, 3);
		fw2 = TrackWrapper_Test.createFix1(200, 60, 15, 0, 31, 30, 0, 2, 3);
		fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(150));
		//
		// System.out.println(fw1.getLocation() + ": " + fw1.getLocation().getLat()
		// + ", " + fw1.getLocation().getLong());
		// System.out.println(fw2.getLocation() + ": " + fw2.getLocation().getLat()
		// + ", " + fw2.getLocation().getLong());
		// System.out.println(fw3.getLocation() + ": " + fw3.getLocation().getLat()
		// + ", " + fw3.getLocation().getLong());

		assertEquals(150, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(31.25, fw3.getLocation().getLong(), 0.0001, "right long");
		assertEquals(60.375, fw3.getLocation().getLat(), 0.001, "right lat");

		// handle the course passing through zero
		fw1 = TrackWrapper_Test.createFix2(100, 1, 1, 0, 1);
		fw2 = TrackWrapper_Test.createFix2(200, 2, 2, 10, 3);
		fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(150));
		assertEquals(150, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(MWC.Algorithms.Conversions.Degs2Rads(5), fw3.getCourse(), 0.001, "right course");

		// handle the course passing through zero
		fw1 = TrackWrapper_Test.createFix2(100, 1, 1, 355, 1);
		fw2 = TrackWrapper_Test.createFix2(200, 2, 2, 15, 3);
		fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(150));
		assertEquals(150, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(5, Math.toDegrees(fw3.getCourse()), 0.001, "right course");

		fw1 = TrackWrapper_Test.createFix2(100, 1, 1, 315, 1);
		fw2 = TrackWrapper_Test.createFix2(200, 2, 2, 15, 3);
		fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(150));
		assertEquals(150, fw3.getTime().getDate().getTime(), "right time");
		assertEquals(345, Math.toDegrees(fw3.getCourse()), 0.001, "right course");

	}

	@Test
	public final void testMyParams() {
		final Fix fx = new Fix(new HiResDate(12, 0), new WorldLocation(2d, 2d, 2d), 2d, 2d);
		final TrackWrapper tw = new TrackWrapper();
		tw.setName("here ew arw");
		FixWrapper ed = new FixWrapper(fx);
		ed.setTrackWrapper(tw);
		editableTesterSupport.testParams(ed);
		ed = null;
	}

	@Test
	public final void testOrientation() {
		assertEquals(LocationPropertyEditor.LEFT, orientationFor(Math.toRadians(0)), "correct orient");
		assertEquals(LocationPropertyEditor.LEFT, orientationFor(Math.toRadians(20)), "correct orient");
		assertEquals(LocationPropertyEditor.LEFT, orientationFor(Math.toRadians(60)), "correct orient");
		assertEquals(LocationPropertyEditor.TOP, orientationFor(Math.toRadians(80)), "correct orient");
		assertEquals(LocationPropertyEditor.TOP, orientationFor(Math.toRadians(115)), "correct orient");
		assertEquals(LocationPropertyEditor.RIGHT, orientationFor(Math.toRadians(135)), "correct orient");
		assertEquals(LocationPropertyEditor.RIGHT, orientationFor(Math.toRadians(160)), "correct orient");
		assertEquals(LocationPropertyEditor.RIGHT, orientationFor(Math.toRadians(190)), "correct orient");
		assertEquals(LocationPropertyEditor.RIGHT, orientationFor(Math.toRadians(220)), "correct orient");
		assertEquals(LocationPropertyEditor.BOTTOM, orientationFor(Math.toRadians(260)), "correct orient");
		assertEquals(LocationPropertyEditor.BOTTOM, orientationFor(Math.toRadians(290)), "correct orient");
		assertEquals(LocationPropertyEditor.LEFT, orientationFor(Math.toRadians(320)), "correct orient");
		assertEquals(LocationPropertyEditor.LEFT, orientationFor(Math.toRadians(360)), "correct orient");
		assertEquals(LocationPropertyEditor.LEFT, orientationFor(Math.toRadians(380)), "correct orient");
		assertEquals(LocationPropertyEditor.RIGHT, orientationFor(Math.toRadians(540)), "correct orient");
		assertEquals(LocationPropertyEditor.BOTTOM, orientationFor(Math.toRadians(-90)), "correct orient");
	}

	@Test
	public final void testResetColor() {
		final TrackWrapper track = new TrackWrapper();
		track.setName("name");
		track.setColor(Color.YELLOW);

		final FixWrapper fw = new FixWrapper(new Fix(new HiResDate(1000), new WorldLocation(1, 1, 0), 12d, 13d));
		fw.setColor(Color.GREEN);

		assertEquals(Color.GREEN, fw.getColor(), "Correct color");

		fw.resetColor();

		assertEquals(Color.GREEN, fw.getColor(), "Correct color (no track)");

		// ok, add the fix to the track
		track.addFix(fw);

		fw.resetColor();

		assertEquals(Color.YELLOW, fw.getColor(), "Correct color (from track)");

		assertEquals(null, fw.getActualColor(), "Correct color");

		// change track color
		track.setColor(Color.red);

		assertEquals(Color.RED, fw.getColor(), "Correct color (After track color change)");
	}

}
