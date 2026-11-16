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

package org.mwc.debrief.core.ContextOperations;

import MWC.GenericData.WorldDistance;
import MWC.TacticalData.Fix;
import static org.junit.jupiter.api.Assertions.*;
import static org.mwc.debrief.core.ContextOperations.GenerateInfillSegment.*;
import org.mwc.debrief.core.ContextOperations.GenerateInfillSegment.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.DebriefPlugin;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.ArrayOffsetHelper.LegacyArrayOffsetModes;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GenerateInfillSegmentTest {

	/** a fix at the given time and position, with course (degs) and speed (kts) */
	private static FixWrapper createFix2(final int timeMillis, final int vLat, final int vLong, final int crseDegs,
			final int spdKts) {
		final FixWrapper theFix = new FixWrapper(
				new Fix(new HiResDate(timeMillis), new WorldLocation(vLat, vLong, 0), 1, 1));
		theFix.getFix().setCourse(MWC.Algorithms.Conversions.Degs2Rads(crseDegs));
		theFix.getFix().setSpeed(new WorldSpeed(spdKts, WorldSpeed.Kts).getValueIn(WorldSpeed.ft_sec) / 3);
		return theFix;
	}

	private static SensorContactWrapper createSensorItem(final TrackWrapper tw, final SensorWrapper sw,
			final int sensorDTG) {
		return new SensorContactWrapper(tw.getName(), new HiResDate(sensorDTG), new WorldDistance(12, WorldDistance.NM),
				12d, null, java.awt.Color.red, "some lable", 12, sw.getName());
	}

		@SuppressWarnings("unused")
		private static void listLegs(final TrackWrapper track) {
			System.out.println("=========");
			final Enumeration<Editable> segs = track.getSegments().elements();
			while (segs.hasMoreElements()) {
				final TrackSegment seg = (TrackSegment) segs.nextElement();
				System.out.println(seg.getName() + " " + seg.getClass());
			}
		}

		final private ArrayList<String> messages = new ArrayList<String>();

		final private GenerateInfillSegment gener = new GenerateInfillSegment() {

			@Override
			protected ErrorLogger getLogger() {
				return getMyLogger();
			}
		};

		protected ErrorLogger getMyLogger() {
			return new ErrorLogger() {

				@Override
				public void logError(final int status, final String text, final Exception e) {
					messages.add(text);
				}

				@Override
				public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
					logError(status, text, null);
				}

				@Override
				public void logStack(final int status, final String text) {
					logError(status, text, null);
				}
			};

		}

	@BeforeEach
	public void setUp() throws Exception {

			// clear any messages
			messages.clear();
		}

	@SuppressWarnings("deprecation")
	@Test
	public void testInfillForRegularSensorData() throws ExecutionException, InterruptedException {
			final Layers theLayers = new Layers();
			final TrackWrapper tmaTrack = new TrackWrapper();
			final TrackWrapper hostTrack = new TrackWrapper();

			final WorldLocation origin = new WorldLocation(44, 44, 44);
			final WorldVector offset1 = new WorldVector(12, 0.1, 0);
			final WorldVector offset2 = new WorldVector(12, 0.15, 0.1);

			final SensorWrapper sensor1 = new SensorWrapper("sensor 1");
			sensor1.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
			hostTrack.add(sensor1);
			final SensorContactWrapper[] cuts1 = new SensorContactWrapper[10];
			for (int i = 0; i < 10; i++) {
				final HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 1 + i, 0));
				final SensorContactWrapper contact = new SensorContactWrapper();
				contact.setDTG(date);
				contact.setBearing(i);
				contact.setOrigin(origin);
				sensor1.add(contact);
				cuts1[i] = contact;
			}

			final SensorWrapper sensor2 = new SensorWrapper("sensor 2");
			sensor2.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
			hostTrack.add(sensor2);
			final SensorContactWrapper[] cuts2 = new SensorContactWrapper[10];
			for (int i = 0; i < 10; i++) {
				final HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 30 + i, 0));
				final SensorContactWrapper contact = new SensorContactWrapper();
				contact.setDTG(date);
				contact.setOrigin(origin);
				contact.setBearing(i);
				sensor2.add(contact);
				cuts2[i] = contact;
			}

			final CoreTMASegment legOne = new RelativeTMASegment(cuts1, offset1, new WorldSpeed(13, WorldSpeed.Kts), 12,
					theLayers, null);

			final CoreTMASegment legTwo = new RelativeTMASegment(cuts2, offset2, new WorldSpeed(13, WorldSpeed.Kts), 12,
					theLayers, null);

			tmaTrack.add(legOne);
			tmaTrack.add(legTwo);

			final Editable[] subjects = new Editable[] { legOne, legTwo };

			final GenerateInfillOperation operation = new GenerateInfillOperation("title", subjects, theLayers,
					tmaTrack, getMyLogger(), true);

			assertEquals(10, legTwo.getData().size(), "correct before len");
			assertEquals(2, tmaTrack.getSegments().size(), "correct legs");
			messages.clear();

			operation.execute(null, null);

			assertEquals(0, messages.size(), "got no error message");
			assertEquals(3, tmaTrack.getSegments().size(), "correct legs");

			// check how many entries get deleted
			assertEquals(10, legTwo.getData().size(), "correct after len");

			// have a look at the legs
			final SegmentList segs = tmaTrack.getSegments();
			final Enumeration<Editable> ele = segs.elements();
			while (ele.hasMoreElements()) {
				final TrackSegment seg = (TrackSegment) ele.nextElement();
				// System.out.println(seg.getName());
				final Enumeration<Editable> ele2 = seg.elements();
				while (ele2.hasMoreElements()) {
					@SuppressWarnings("unused")
					final FixWrapper fw = (FixWrapper) ele2.nextElement();
					// System.out.println(fw.getLocation().getLong() + ", " +
					// fw.getLocation().getLat()+ ", "
					// + fw.getDateTimeGroup().getDate());
				}
			}

			// check that the last point in the infill isn't the same as the first point in
			// the second set
			final FixWrapper startOfNext = (FixWrapper) legTwo.first();
			final TrackSegment infill = tmaTrack.getSegments()
					.getSegmentFor(startOfNext.getDateTimeGroup().getDate().getTime() - 120000);
			final FixWrapper endOfInfill = (FixWrapper) infill.last();
			assertFalse(startOfNext.getLocation().equals(endOfInfill.getLocation()), "Locations not equal");

			// also test deleting points
			final List<FixWrapper> infillFixes = new ArrayList<FixWrapper>();
			final Enumeration<Editable> pts = infill.elements();
			while (pts.hasMoreElements()) {
				infillFixes.add((FixWrapper) pts.nextElement());
			}

			assertEquals(3, tmaTrack.getSegments().size(), "correct elements before clearing infill");

			// ok, now delete them
			for (final FixWrapper f : infillFixes) {
				infill.removeElement(f);
			}

//      Thread.sleep(1000);
//
//      // tests causing intermittent issue in Travis. Invetigate.
//      assertEquals("dynamic infill removed", 2, tmaTrack.getSegments().size());
//      assertNull("infill detached", infill.getWrapper());

		}

	@SuppressWarnings("deprecation")
	@Test
	public void testOverlap() {
			final Layers theLayers = new Layers();
			final IMenuManager parent = new MenuManager();
			final TrackWrapper track = new TrackWrapper();
			final Layer[] parentLayers = new Layer[] { track, track };

			final HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
			final HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

			final HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 11, 55, 0));
			final HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

			final WorldLocation origin = new WorldLocation(44, 44, 44);
			final AbsoluteTMASegment legOne = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l1_start, l1_end);
			final AbsoluteTMASegment legTwo = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l2_start, l2_end);

			track.add(legOne);
			track.add(legTwo);

			final Editable[] subjects = new Editable[] { legOne, legTwo };
			gener.generate(parent, theLayers, parentLayers, subjects);
			final IContributionItem[] newItems = parent.getItems();

			assertEquals(3, newItems.length, "newItems");

			final ActionContributionItem first = (ActionContributionItem) newItems[1];
			assertEquals("Generate infill segment", first.getAction().getText(), "right name");
			first.getAction().run();

			assertEquals(1, messages.size(), "got error message");
			assertEquals(GenerateInfillOperation.OVERLAPPING, messages.get(0), "correct error message");
		}

	@Test
	public void testReconnectInfillAfterSplit() throws ExecutionException {
			final TrackWrapper tw = new TrackWrapper();

			final Layers theLayers = new Layers();

			tw.addFix(createFix2(100000, 1, 1, 4, 12));
			tw.addFix(createFix2(200000, 2, 3, 4, 12));
			tw.addFix(createFix2(300000, 3, 3, 4, 12));
			tw.addFix(createFix2(400000, 4, 6, 4, 12));
			tw.addFix(createFix2(500000, 4, 6, 4, 12));
			tw.addFix(createFix2(600000, 4, 6, 4, 12));
			tw.addFix(createFix2(700000, 4, 6, 4, 12));

			final WorldVector offset = new WorldVector(12, 12, 0);
			final WorldSpeed speed = new WorldSpeed(5, WorldSpeed.Kts);
			final double course = 33;

			// ok, create the segment
			CoreTMASegment seg = null;

			// check the before
			FixWrapper firstFix = null;

			final SensorWrapper sw = new SensorWrapper("some sensor");
			sw.setHost(tw);

			// attach the sensor to the host
			tw.add(sw);

			final int senLen = 30;
			final SensorContactWrapper[] items = new SensorContactWrapper[senLen];

			for (int i = 0; i < senLen; i++) {
				items[i] = createSensorItem(tw, sw, 100000 + i * 10000);
			}

			// sort out the host
			for (int i = 0; i < items.length; i++) {
				final SensorContactWrapper sensorContactWrapper = items[i];
				sw.add(sensorContactWrapper);
			}

			// seg = new RelativeTMASegment(sw, offset, speed, course, null);
			seg = new RelativeTMASegment(items, offset, speed, course, null, Color.yellow);
			seg.setWrapper(tw);

			// check the create worked
			assertEquals(30, seg.size(), "enough points created");

			// check the before
			firstFix = (FixWrapper) seg.getData().iterator().next();
			assertEquals(33, seg.getCourse(), 0.001, "correct course before");
			assertEquals(5, seg.getSpeed().getValueIn(WorldSpeed.Kts), 0.001, "correct speed before");
			assertEquals(33, MWC.Algorithms.Conversions.Rads2Degs(firstFix.getCourse()), 0.001,
					"correct course before");
			assertEquals(5, firstFix.getSpeed(), 0.001, "correct speed before");

			// ok, now do the split
			final TrackWrapper segW = new TrackWrapper();
			segW.setName("TMA");
			segW.add(seg);

			// get hold of an item in the segment
			final Enumeration<Editable> enumer = seg.elements();
			enumer.nextElement();
			enumer.nextElement();
			enumer.nextElement();
			enumer.nextElement();
			enumer.nextElement();
			enumer.nextElement();
			enumer.nextElement();
			enumer.nextElement();
			enumer.nextElement();
			final FixWrapper fw = (FixWrapper) enumer.nextElement();
			assertNotNull(fw, "Found a fix");

			// do the split
			final Vector<TrackSegment> segs = segW.splitTrack(fw, false);

			// check we have enough segments
			assertEquals(2, segs.size(), "now two segments");
			assertEquals(10, segs.firstElement().size(), "first is of correct length");
			assertEquals(20, segs.lastElement().size(), "second is of correct length");

			// check they're of the correct type
			final TrackSegment seg1 = segs.firstElement();
			final TrackSegment seg2 = segs.lastElement();
			assertTrue(seg1 instanceof RelativeTMASegment, " is a tma segment");
			assertTrue(seg2 instanceof RelativeTMASegment, " is a tma segment");

			// ok, insert infill
			final Editable[] segments = new Editable[] { seg1, seg2 };
			final Layer parentTrack = segW;
			final GenerateInfillOperation oper = new GenerateInfillOperation("gen it", segments, theLayers, parentTrack,
					null, true);
			oper.execute(null, null);

			// how many legs?
			assertEquals(3, segW.getSegments().size(), "has infill");

			final long midPoint = seg1.endDTG().getDate().getTime()
					+ (seg2.startDTG().getDate().getTime() - seg1.endDTG().getDate().getTime()) / 2;
			final TrackSegment possInfill = segW.getSegments().getSegmentFor(midPoint);
			assertTrue(possInfill instanceof DynamicInfillSegment, "found infill");

			// ok, check the ends
			final DynamicInfillSegment infill = (DynamicInfillSegment) possInfill;
			assertEquals(seg1, infill.getBeforeSegment(), "correct before");
			assertEquals(seg2, infill.getAfterSegment(), "correct after");

			// ok, slice the first one
			final Enumeration<Editable> enumer2 = seg1.elements();
			enumer2.nextElement();
			enumer2.nextElement();
			enumer2.nextElement();
			final FixWrapper fw2 = (FixWrapper) enumer2.nextElement();
			assertNotNull(fw2, "Found a fix");

			// do the split
			final Vector<TrackSegment> segs2 = segW.splitTrack(fw2, false);
			assertEquals(2, segs2.size());

			final TrackSegment newSeg2 = segs2.get(1);

			assertEquals(newSeg2, infill.getBeforeSegment(), "correct before");
			assertEquals(seg2, infill.getAfterSegment(), "correct after");

			// ok, split the second leg
			final Enumeration<Editable> enumer3 = seg2.elements();
			enumer3.nextElement();
			enumer3.nextElement();
			enumer3.nextElement();
			final FixWrapper fw3 = (FixWrapper) enumer3.nextElement();
			assertNotNull(fw3, "Found a fix");

			// do the split
			final Vector<TrackSegment> segs3 = segW.splitTrack(fw3, false);
			assertEquals(2, segs2.size());

			infill.reconstruct();
			System.out.println(infill.getSegmentNames());

			final TrackSegment newSeg3 = segs3.get(0);

			assertEquals(newSeg2, infill.getBeforeSegment(), "correct before");
			assertEquals(newSeg3, infill.getAfterSegment(), "correct after");

		}

	@SuppressWarnings("deprecation")
	@Test
	public void testRepeat() throws ExecutionException {
			final Layers theLayers = new Layers();
			final TrackWrapper track = new TrackWrapper();

			final HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
			final HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

			final HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 0, 1));
			final HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

			final WorldLocation origin = new WorldLocation(44, 44, 44);
			final AbsoluteTMASegment legOne = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l1_start, l1_end);
			final AbsoluteTMASegment legTwo = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l2_start, l2_end);

			track.add(legOne);
			track.add(legTwo);

			Editable[] subjects = new Editable[] { legOne, legTwo };

			GenerateInfillOperation operation = new GenerateInfillOperation("title", subjects, theLayers, track,
					getMyLogger(), true);

			assertEquals(60, legTwo.getData().size(), "correct before len");
			assertEquals(2, track.getSegments().size(), "correct legs");
			messages.clear();

			operation.execute(null, null);

			assertEquals(0, messages.size(), "got no error message");
			assertEquals(3, track.getSegments().size(), "correct legs");

			// ok, now try to do it again
			subjects = new Editable[3];
			int ctr = 0;
			final Iterator<Editable> iter = track.getSegments().getData().iterator();
			while (iter.hasNext()) {
				subjects[ctr++] = iter.next();
			}
			operation = new GenerateInfillOperation("title", subjects, theLayers, track, getMyLogger(), true);

			assertEquals(58, legTwo.getData().size(), "correct before len");
			assertEquals(3, track.getSegments().size(), "correct legs");
			messages.clear();

			operation.execute(null, null);

			assertEquals(0, messages.size(), "got no error message");
			assertEquals(3, track.getSegments().size(), "correct legs");

		}

	@SuppressWarnings("deprecation")
	@Test
	public void testTooSmall() throws ExecutionException {
			final Layers theLayers = new Layers();
			final IMenuManager parent = new MenuManager();
			final TrackWrapper track = new TrackWrapper();
			final Layer[] parentLayers = new Layer[] { track, track };

			final HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
			final HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

			final HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 0, 1));
			final HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

			final WorldLocation origin = new WorldLocation(44, 44, 44);
			final AbsoluteTMASegment legOne = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l1_start, l1_end);
			final AbsoluteTMASegment legTwo = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l2_start, l2_end);

			track.add(legOne);
			track.add(legTwo);

			final Editable[] subjects = new Editable[] { legOne, legTwo };
			gener.generate(parent, theLayers, parentLayers, subjects);
			final IContributionItem[] newItems = parent.getItems();

			assertEquals(3, newItems.length, "newItems");

			ActionContributionItem first = (ActionContributionItem) newItems[1];
			assertEquals("Generate infill segment", first.getAction().getText(), "right name");
			first.getAction().run();

			assertEquals(1, messages.size(), "got error message");
			assertEquals(GenerateInfillOperation.INSUFFICIENT_TIME, messages.get(0), "correct error message");

			assertEquals(60, legTwo.getData().size(), "correct before len");

			messages.clear();
			first = (ActionContributionItem) newItems[2];
			assertEquals("Generate infill segment" + DELETE_SUFFIX, first.getAction().getText(), "right name");
			first.getAction().run();

			assertEquals(0, messages.size(), "got no error message");

			// check how many entries get deleted
			assertEquals(58, legTwo.getData().size(), "correct after len");
		}

	@SuppressWarnings("deprecation")
	@Test
	public void testTooSmallSecondTooShort() throws ExecutionException {
			final Layers theLayers = new Layers();
			final TrackWrapper track = new TrackWrapper();

			final HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
			final HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

			final HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 0, 1));
			final HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 12, 2, 0));

			final WorldLocation origin = new WorldLocation(44, 44, 44);
			final AbsoluteTMASegment legOne = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l1_start, l1_end);
			final AbsoluteTMASegment legTwo = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l2_start, l2_end);

			track.add(legOne);
			track.add(legTwo);

			final Editable[] subjects = new Editable[] { legOne, legTwo };

			final GenerateInfillOperation operation = new GenerateInfillOperation("title", subjects, theLayers, track,
					getMyLogger(), true);

			messages.clear();

			operation.execute(null, null);

			assertEquals(1, messages.size(), "got no error message");
			assertEquals(GenerateInfillOperation.CANT_DELETE, messages.get(0), "correct message");

			// check how many entries get deleted
			assertEquals(2, legTwo.getData().size(), "correct after len");
		}

	@SuppressWarnings("deprecation")
	@Test
	public void testUndo() throws ExecutionException {
			final Layers theLayers = new Layers();
			final TrackWrapper track = new TrackWrapper();

			final HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
			final HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

			final HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 0, 1));
			final HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

			final WorldLocation origin = new WorldLocation(44, 44, 44);
			final AbsoluteTMASegment legOne = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l1_start, l1_end);
			final AbsoluteTMASegment legTwo = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l2_start, l2_end);

			track.add(legOne);
			track.add(legTwo);

			final Editable[] subjects = new Editable[] { legOne, legTwo };

			final GenerateInfillOperation operation = new GenerateInfillOperation("title", subjects, theLayers, track,
					getMyLogger(), true);

			assertEquals(60, legTwo.getData().size(), "correct before len");
			assertEquals(2, track.getSegments().size(), "correct legs");
			messages.clear();

			operation.execute(null, null);

			assertEquals(0, messages.size(), "got no error message");
			assertEquals(3, track.getSegments().size(), "correct legs");

			// check how many entries get deleted
			assertEquals(58, legTwo.getData().size(), "correct after len");

			// test undo processing, check second leg same as original length
			operation.undo(null, null);
			assertEquals(60, legTwo.getData().size(), "correct after len");
			assertEquals(2, track.getSegments().size(), "correct legs");
		}

	@SuppressWarnings("deprecation")
	@Test
	public void testUndoRelative() throws ExecutionException {
			final Layers theLayers = new Layers();
			final TrackWrapper tmaTrack = new TrackWrapper();
			final TrackWrapper hostTrack = new TrackWrapper();

			final WorldLocation origin = new WorldLocation(44, 44, 44);
			final WorldVector _offset = new WorldVector(12, 0.1, 0);

			final SensorWrapper sensor1 = new SensorWrapper("sensor 1");
			sensor1.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
			hostTrack.add(sensor1);
			final SensorContactWrapper[] cuts1 = new SensorContactWrapper[10];
			for (int i = 0; i < 10; i++) {
				final HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 1 + i, 0));
				final SensorContactWrapper contact = new SensorContactWrapper();
				contact.setDTG(date);
				contact.setBearing(i);
				contact.setOrigin(origin);
				sensor1.add(contact);
				cuts1[i] = contact;
			}

			final SensorWrapper sensor2 = new SensorWrapper("sensor 2");
			sensor2.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
			hostTrack.add(sensor2);
			final SensorContactWrapper[] cuts2 = new SensorContactWrapper[10];
			for (int i = 0; i < 10; i++) {
				final HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 21 + i, 0));
				final SensorContactWrapper contact = new SensorContactWrapper();
				contact.setDTG(date);
				contact.setOrigin(origin);
				contact.setBearing(i);
				sensor2.add(contact);
				cuts2[i] = contact;
			}

			final CoreTMASegment legOne = new RelativeTMASegment(cuts1, _offset, new WorldSpeed(13, WorldSpeed.Kts), 12,
					theLayers, null);

			final CoreTMASegment legTwo = new RelativeTMASegment(cuts2, _offset, new WorldSpeed(13, WorldSpeed.Kts), 12,
					theLayers, null);

			tmaTrack.add(legOne);
			tmaTrack.add(legTwo);

			final Editable[] subjects = new Editable[] { legOne, legTwo };

			final GenerateInfillOperation operation = new GenerateInfillOperation("title", subjects, theLayers,
					tmaTrack, getMyLogger(), true);

			assertEquals(10, legTwo.getData().size(), "correct before len");
			assertEquals(2, tmaTrack.getSegments().size(), "correct legs");
			messages.clear();

			operation.execute(null, null);

			assertEquals(0, messages.size(), "got no error message");
			assertEquals(3, tmaTrack.getSegments().size(), "correct legs");

			// check how many entries get deleted
			assertEquals(10, legTwo.getData().size(), "correct after len");

			// TODO - test undo processing, check second leg same as original length
			operation.undo(null, null);
			assertEquals(10, legTwo.getData().size(), "correct after len");
			assertEquals(2, tmaTrack.getSegments().size(), "correct legs");
		}

	@SuppressWarnings("deprecation")
	@Test
	public void testUndoRelative2() throws ExecutionException {
			final Layers theLayers = new Layers();
			final TrackWrapper tmaTrack = new TrackWrapper();
			final TrackWrapper hostTrack = new TrackWrapper();

			final WorldLocation origin = new WorldLocation(44, 44, 44);
			final WorldVector _offset = new WorldVector(12, 0.1, 0);

			final SensorWrapper sensor1 = new SensorWrapper("sensor 1");
			sensor1.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
			hostTrack.add(sensor1);
			final SensorContactWrapper[] cuts1 = new SensorContactWrapper[10];
			for (int i = 0; i < 10; i++) {
				final HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 1 + i, 0));
				final SensorContactWrapper contact = new SensorContactWrapper();
				contact.setDTG(date);
				contact.setBearing(i);
				contact.setOrigin(origin);
				sensor1.add(contact);
				cuts1[i] = contact;
			}

			final SensorWrapper sensor2 = new SensorWrapper("sensor 2");
			sensor2.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
			hostTrack.add(sensor2);
			final SensorContactWrapper[] cuts2 = new SensorContactWrapper[10];
			for (int i = 0; i < 10; i++) {
				final HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 10 + i, 30));
				final SensorContactWrapper contact = new SensorContactWrapper();
				contact.setDTG(date);
				contact.setOrigin(origin);
				contact.setBearing(i);
				sensor2.add(contact);
				cuts2[i] = contact;
			}

			final CoreTMASegment legOne = new RelativeTMASegment(cuts1, _offset, new WorldSpeed(13, WorldSpeed.Kts), 12,
					theLayers, null);

			final CoreTMASegment legTwo = new RelativeTMASegment(cuts2, _offset, new WorldSpeed(13, WorldSpeed.Kts), 12,
					theLayers, null);

			tmaTrack.add(legOne);
			tmaTrack.add(legTwo);

			final Editable[] subjects = new Editable[] { legOne, legTwo };

			final GenerateInfillOperation operation = new GenerateInfillOperation("title", subjects, theLayers,
					tmaTrack, getMyLogger(), true);

			assertEquals(10, legTwo.getData().size(), "correct before len");
			assertEquals(2, tmaTrack.getSegments().size(), "correct legs");
			messages.clear();

			operation.execute(null, null);

			assertEquals(0, messages.size(), "got no error message");
			assertEquals(3, tmaTrack.getSegments().size(), "correct legs");

			// check how many entries get deleted
			assertEquals(8, legTwo.getData().size(), "correct after len");

			// TODO - test undo processing, check second leg same as original length
			operation.undo(null, null);
			assertEquals(10, legTwo.getData().size(), "correct after len");
			assertEquals(2, tmaTrack.getSegments().size(), "correct legs");
		}

	@SuppressWarnings("deprecation")
	@Test
	public void testValid() {
			final Layers theLayers = new Layers();
			final IMenuManager parent = new MenuManager();
			final TrackWrapper track = new TrackWrapper();
			final Layer[] parentLayers = new Layer[] { track, track };

			final HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
			final HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

			final HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 15, 0));
			final HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

			final WorldLocation origin = new WorldLocation(44, 44, 44);
			final AbsoluteTMASegment legOne = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l1_start, l1_end);
			final AbsoluteTMASegment legTwo = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin,
					l2_start, l2_end);

			track.add(legOne);
			track.add(legTwo);

			final Editable[] subjects = new Editable[] { legOne, legTwo };
			gener.generate(parent, theLayers, parentLayers, subjects);
			final IContributionItem[] newItems = parent.getItems();

			assertEquals(3, newItems.length, "newItems");

			final ActionContributionItem first = (ActionContributionItem) newItems[1];
			assertEquals("Generate infill segment", first.getAction().getText(), "right name");

			// check the before len
			assertEquals(46, legTwo.getData().size(), "Correct before len");
			final int beforeLen = legTwo.getData().size();

			first.getAction().run();

			assertEquals(0, messages.size(), "got error message");
			assertEquals(3, track.getSegments().size(), "correct legs");

			// check the len still valid
			assertEquals(beforeLen, legTwo.getData().size(), "Correct before len");

			// ok, check that if we split the second, we don't lose the infill

			// get a point in the second leg
			FixWrapper target = null;
			final Collection<Editable> secondCuts = legTwo.getData();
			final Iterator<Editable> iter = secondCuts.iterator();
			for (int i = 0; i < 6; i++) {
				target = (FixWrapper) iter.next();
			}

			assertNotNull(target, "found it");

			track.splitTrack(target, false);

			// check we now have 4 legs
			assertEquals(4, track.getSegments().size(), "correct legs");
		}
	}
