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

package org.mwc.debrief.core.loaders;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Enumeration;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests the GPX 1.1 version
 *
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date August 21, 2012
 * @category gpx
 *
 */
public class JaxbGpx11HelperTest {
	private final JaxbGpxHelper helper = new JaxbGpxHelper();

	/**
	 * Unmarshaller should use the default values specified in the schema
	 */
	@Test
	public void unmarshallShouldNotFailWhenOptionalDataMissing() {
		final Layers layers = helper.unmarshall(getClass().getResourceAsStream("gpx-1.1-missing-optional-data.xml"),
				null);
		assertEquals(1, layers.size(), "Only 1 track is present in the gpx xml");

		// assert track
		final TrackWrapper track = (TrackWrapper) layers.findLayer("NELSON");
		assertNotNull(track);
		assertEquals("NELSON", track.getName());
		assertTrue(track.getNameAtStart());
		assertEquals(1, track.getLineThickness());
		assertTrue(track.getInterpolatePoints());
		assertTrue(track.getLinkPositions());
		assertEquals(1, track.getLineStyle());
		assertEquals(Integer.valueOf(LocationPropertyEditor.RIGHT), track.getNameLocation());
		assertTrue(track.getNameVisible());
		assertTrue(track.getPlotArrayCentre());
		assertTrue(track.getLinkPositions());
		assertTrue(track.getSensors().getVisible());
		assertTrue(track.getSolutions().getVisible());
		assertTrue(track.getPositionsVisible());
		assertEquals("Square", track.getSymbolType());
		assertTrue(track.getVisible());

		// assert segment
		final SegmentList segments = track.getSegments();
		assertNotNull(segments);
		assertEquals(1, segments.size(), "Only 1 segment is present in the gpx xml");

		final TrackSegment segment = (TrackSegment) segments.getData().iterator().next();

		// assert Fix
		final WorldLocation trackStart = segment.getTrackStart();
		assertNotNull(trackStart, "The first track should be start of the track");

		assertEquals(22.1862861, trackStart.getLat(), 0.0000001);
		assertEquals(-21.6978806, trackStart.getLong(), 0.0000001);
		assertEquals(0.000, trackStart.getDepth(), 0.0000001);

		int trackPointCount = 0;
		final Enumeration<Editable> elements = segment.elements();
		while (elements.hasMoreElements()) {
			trackPointCount++;
			elements.nextElement();

		}
		assertEquals(1, trackPointCount, "1 track point is present in the gpx xml");

		final FixWrapper fix = (FixWrapper) segment.elements().nextElement();
		assertNotNull(fix.getTime());
	}

	@Test
	public void unmarshallTrackWithAllData() {
		final Layers layers = helper.unmarshall(getClass().getResourceAsStream("gpx-1.1-data.xml"), null);
		assertEquals(1, layers.size(), "Only 1 track is present in the gpx xml");

		// assert track
		final TrackWrapper track = (TrackWrapper) layers.findLayer("NELSON");
		assertNotNull(track);
		assertEquals("NELSON", track.getName());
		assertFalse(track.getNameAtStart());
		assertEquals(2, track.getLineThickness());
		assertFalse(track.getInterpolatePoints());
		assertFalse(track.getLinkPositions());
		assertEquals(2, track.getLineStyle());
		assertEquals(Integer.valueOf(LocationPropertyEditor.CENTRE), track.getNameLocation());
		assertFalse(track.getNameVisible());
		assertFalse(track.getPlotArrayCentre());
		assertFalse(track.getLinkPositions());
		assertFalse(track.getSensors().getVisible());
		assertFalse(track.getSolutions().getVisible());
		assertFalse(track.getPositionsVisible());
		assertEquals("Square", track.getSymbolType());
		assertFalse(track.getVisible());

		// assert segment
		final SegmentList segments = track.getSegments();
		assertNotNull(segments);
		assertEquals(1, segments.size(), "Only 1 segment is present in the gpx xml");

		final TrackSegment segment = (TrackSegment) segments.getData().iterator().next();

		// assert Fix
		final WorldLocation trackStart = segment.getTrackStart();
		assertNotNull(trackStart, "Since there is only one track it should be the start of the track ");

		assertEquals(22.1862861, trackStart.getLat(), 0.0000001);
		assertEquals(-21.6978806, trackStart.getLong(), 0.0000001);
		assertEquals(0.000, trackStart.getDepth(), 0.0000001);

		int trackPointCount = 0;
		final Enumeration<Editable> elements = segment.elements();
		while (elements.hasMoreElements()) {
			trackPointCount++;
			elements.nextElement();

		}
		assertEquals(5, trackPointCount, "5 track points are present in the gpx xml");

		final FixWrapper fix = (FixWrapper) segment.elements().nextElement();
		assertNotNull(fix.getTime());

		assertEquals(269.700, fix.getCourseDegs(), 0.0000001);
		assertTrue(fix.getArrowShowing());
		assertFalse(fix.getLabelShowing());
		assertFalse(fix.getLineShowing());
		assertFalse(fix.getSymbolShowing());
		assertFalse(fix.getVisible());
		assertEquals("120500", fix.getLabel());
		assertEquals(2.000, fix.getSpeed(), 0.001);
		assertEquals(Integer.valueOf(LocationPropertyEditor.LEFT), fix.getLabelLocation());
	}
}
