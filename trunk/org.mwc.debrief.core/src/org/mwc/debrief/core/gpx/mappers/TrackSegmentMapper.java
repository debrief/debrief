package org.mwc.debrief.core.gpx.mappers;

import Debrief.Wrappers.Track.TrackSegment;

import com.topografix.gpx.v11.TrksegType;

public class TrackSegmentMapper
{
	public TrackSegment fromGpx(TrksegType segmentType)
	{
		TrackSegment segment = new TrackSegment();
		return segment;
	}
}