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

package Debrief.Wrappers.Track;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.XML.DebriefLayersHandler;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackCoverageWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackCoverageWrapper.DynamicCoverageShape;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeSetWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper.DynamicShape;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import junit.framework.TestCase;

/**
 * check that dynamic shapes (sensor arcs) can be attached to planning tracks
 */
public class CompositeTrackWrapper_Test extends TestCase {

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	private static final String TRACK_NAME = "PLAN";

	/** 12 Dec 1995 05:00:00 */
	private static final long START;

	static {
		final java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT"));
		cal.clear();
		cal.set(1995, java.util.Calendar.DECEMBER, 12, 5, 0, 0);
		START = cal.getTimeInMillis();
	}

	private static final long ONE_MIN = 60 * 1000;

	private static DynamicTrackShapeWrapper createArc(final String name, final HiResDate start,
			final HiResDate end) {
		final List<DynamicShape> values = new ArrayList<DynamicShape>();
		values.add(new DynamicCoverageShape(-45, 45, 0, 1000));
		return new DynamicTrackCoverageWrapper(TRACK_NAME, start, end, values, Color.RED, 0, name);
	}

	private static CompositeTrackWrapper createPlan() {
		final CompositeTrackWrapper track = new CompositeTrackWrapper(new HiResDate(START),
				new WorldLocation(50, -4, 0));
		track.setName(TRACK_NAME);
		// 2nm at 12kts = 10 mins each
		track.add(new PlanningSegment("leg 1", 0, new WorldSpeed(12, WorldSpeed.Kts),
				new WorldDistance(2, WorldDistance.NM)));
		track.add(new PlanningSegment("leg 2", 90, new WorldSpeed(12, WorldSpeed.Kts),
				new WorldDistance(2, WorldDistance.NM)));
		track.addClosingLeg();
		return track;
	}

	private static DynamicTrackShapeSetWrapper getSet(final CompositeTrackWrapper track, final String name) {
		final Enumeration<Editable> iter = track.getDynamicShapes().elements();
		while (iter.hasMoreElements()) {
			final DynamicTrackShapeSetWrapper set = (DynamicTrackShapeSetWrapper) iter.nextElement();
			if (set.getName().equals(name)) {
				return set;
			}
		}
		return null;
	}

	private static DynamicTrackShapeWrapper first(final DynamicTrackShapeSetWrapper set) {
		return (DynamicTrackShapeWrapper) set.elements().nextElement();
	}

	public void testAddArc() {
		final CompositeTrackWrapper track = createPlan();
		assertFalse("no shapes in outline yet", contains(track.elements(), track.getDynamicShapes()));

		track.add(createArc("fwd", new HiResDate(START + ONE_MIN), new HiResDate(START + 5 * ONE_MIN)));

		final DynamicTrackShapeSetWrapper set = getSet(track, "fwd");
		assertNotNull("arc set created", set);
		assertEquals("host is the planning track", track, set.getHost());
		assertEquals("one arc", 1, set.size());

		// the outline shows the legs, plus the shapes
		assertTrue("shapes shown in outline", contains(track.elements(), track.getDynamicShapes()));
		int legs = 0;
		final Enumeration<Editable> iter = track.elements();
		while (iter.hasMoreElements()) {
			if (iter.nextElement() instanceof PlanningSegment) {
				legs++;
			}
		}
		assertEquals("legs still in outline", 3, legs);

		// and we still reject types that don't make sense on a plan
		try {
			track.add(new SensorWrapper("sensor"));
			fail("should have rejected sensor");
		} catch (final RuntimeException re) {
			// ok
		}
	}

	public void testArcFollowsLegs() {
		final CompositeTrackWrapper track = createPlan();
		track.add(createArc("fwd", null, null));

		// arcs are positioned against the host's nearest fix at paint time, so check
		// the host resolves to the right leg, including the closing one
		final PlanningSegment closing = (PlanningSegment) track.getSegments().last();
		assertTrue("have closing segment", closing instanceof PlanningSegment.ClosingSegment);
		final HiResDate inClosing = new HiResDate(START + 25 * ONE_MIN);
		Watchable[] nearest = track.getNearestTo(inClosing);
		assertEquals("rotates with closing leg", closing.getCourse(),
				MWC.Algorithms.Conversions.Rads2Degs(nearest[0].getCourse()), 0.01);

		// now edit the first leg, and check we resolve against the new fixes
		final PlanningSegment leg1 = (PlanningSegment) track.getSegments().first();
		leg1.setCourse(45);
		track.recalculate();
		nearest = track.getNearestTo(new HiResDate(START + 5 * ONE_MIN));
		assertEquals("follows edited leg", 45, MWC.Algorithms.Conversions.Rads2Degs(nearest[0].getCourse()), 0.01);
		assertEquals("host still the planning track", track, getSet(track, "fwd").getHost());
	}

	public void testShiftStartTime() {
		final CompositeTrackWrapper track = createPlan();
		track.add(createArc("timed", new HiResDate(START + ONE_MIN), new HiResDate(START + 5 * ONE_MIN)));
		track.add(createArc("whole", null, null));

		track.setStartDate(new HiResDate(START + 60 * ONE_MIN));

		final DynamicTrackShapeWrapper timed = first(getSet(track, "timed"));
		assertEquals("start moved", START + 61 * ONE_MIN, timed.getStartDTG().getDate().getTime());
		assertEquals("end moved", START + 65 * ONE_MIN, timed.getEndDTG().getDate().getTime());

		final DynamicTrackShapeWrapper whole = first(getSet(track, "whole"));
		assertNull("whole-track arc untouched", whole.getStartDTG());
		assertNull("whole-track arc untouched", whole.getEndDTG());
	}

	public void testXmlRoundTrip() throws Exception {
		final CompositeTrackWrapper track = createPlan();
		track.add(createArc("fwd", new HiResDate(START + ONE_MIN), new HiResDate(START + 5 * ONE_MIN)));
		final Layers layers = new Layers();
		layers.addThisLayer(track);

		// export
		final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		final Element root = doc.createElement("plot");
		doc.appendChild(root);
		DebriefLayersHandler.exportThis(layers, root, doc, null);
		final StringWriter writer = new StringWriter();
		TransformerFactory.newInstance().newTransformer()
				.transform(new DOMSource(root.getElementsByTagName("layers").item(0)), new StreamResult(writer));
		final String xml = writer.toString();

		// and import
		final Layers res = new Layers();
		new DebriefXMLReaderWriter(null).importThis("test.xml", new ByteArrayInputStream(xml.getBytes("UTF-8")),
				res);

		final CompositeTrackWrapper loaded = (CompositeTrackWrapper) res.findLayer(TRACK_NAME);
		assertNotNull("planning track loaded", loaded);
		assertEquals("legs loaded", 3, loaded.getSegments().size());
		final DynamicTrackShapeSetWrapper set = getSet(loaded, "fwd");
		assertNotNull("arc loaded", set);
		assertEquals("host set", loaded, set.getHost());
		final DynamicTrackShapeWrapper arc = first(set);
		assertEquals("start kept", START + ONE_MIN, arc.getStartDTG().getDate().getTime());
		assertEquals("end kept", START + 5 * ONE_MIN, arc.getEndDTG().getDate().getTime());
	}

	public void testReplayImport() throws Exception {
		final CompositeTrackWrapper track = createPlan();
		final Layers layers = new Layers();
		layers.addThisLayer(track);

		final ImportReplay importer = new ImportReplay();
		importer.setLayers(layers);
		importer.readLine(";SENSORARC: 951212 050100 951212 050500 " + TRACK_NAME + " @A -45 45 0 1000 \"fwd\"");

		final DynamicTrackShapeSetWrapper set = getSet(track, "fwd");
		assertNotNull("arc attached from REP", set);
		assertEquals("one arc", 1, set.size());
	}

	private static boolean contains(final Enumeration<Editable> iter, final BaseLayer item) {
		while (iter.hasMoreElements()) {
			if (iter.nextElement() == item) {
				return true;
			}
		}
		return false;
	}
}
