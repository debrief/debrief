package org.mwc.debrief.core.gpx.mappers;

import Debrief.Wrappers.Track.TrackSegment;

import com.topografix.gpx.v10.Gpx.Trk.Trkseg;
import com.topografix.gpx.v11.TrksegType;

public class TrackSegmentMapper
{
	public TrackSegment fromGpx(TrksegType segmentType)
	{
		// nothing to map at segment level
		return newSegment();
	}

	public TrackSegment fromGpx10(Trkseg gpxSegment)
	{
		// nothing to map at segment level
		return newSegment();
	}

	private TrackSegment newSegment()
	{
		TrackSegment segment = new TrackSegment();
		return segment;
	}
}