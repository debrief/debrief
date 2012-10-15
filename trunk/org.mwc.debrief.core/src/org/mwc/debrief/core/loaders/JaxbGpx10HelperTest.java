package org.mwc.debrief.core.loaders;

import static org.junit.Assert.*;

import java.util.Enumeration;

import org.junit.Test;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;

/**
 * Tests the GPX 1.0 version
 * 
 * @author Aravind R. Yarram <yaravind@gmail.com>
 * @date October 12, 2012
 * @category gpx
 */
public class JaxbGpx10HelperTest
{
	private final JaxbGpxHelper helper = new JaxbGpxHelper();

	@Test
	public void unmarshallTrackWithAllData()
	{
		Layers layers = helper.unmarshall(getClass().getResourceAsStream("gpx_1_0.xml"), null);
		assertEquals("Only 1 track is present in the gpx xml", 1, layers.size());

		// assert track
		TrackWrapper track = (TrackWrapper) layers.findLayer("NELSON");
		assertNotNull(track);
		assertEquals("NELSON", track.getName());

		// assert segment
		SegmentList segments = track.getSegments();
		assertNotNull(segments);
		assertEquals("Only 1 segment is present in the gpx xml", 1, segments.size());

		TrackSegment segment = (TrackSegment) segments.getData().iterator().next();

		// assert Fix
		WorldLocation trackStart = segment.getTrackStart();
		assertNotNull("Since there is only one track it should be the start of the track ", trackStart);

		assertEquals(22.1862861, trackStart.getLat(), 0.0000001);
		assertEquals(-21.6978806, trackStart.getLong(), 0.0000001);
		assertEquals(0.000, trackStart.getDepth(), 0.0000001);

		int trackPointCount = 0;
		Enumeration<Editable> elements = segment.elements();
		while (elements.hasMoreElements())
		{
			trackPointCount++;
			elements.nextElement();

		}
		assertEquals("5 track points are present in the gpx xml", 5, trackPointCount);

		FixWrapper fix = (FixWrapper) segment.elements().nextElement();
		assertNotNull(fix.getTime());

		assertEquals(268.7, fix.getCourse(), 0.1);
		assertEquals(4.5, fix.getFix().getSpeed(), 0.1);
	}

}
