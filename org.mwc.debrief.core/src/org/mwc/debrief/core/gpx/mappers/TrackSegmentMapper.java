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

package org.mwc.debrief.core.gpx.mappers;

import com.topografix.gpx.v10.Gpx.Trk.Trkseg;
import com.topografix.gpx.v11.TrksegType;

import Debrief.Wrappers.Track.TrackSegment;

public class TrackSegmentMapper {
	public TrackSegment fromGpx(final TrksegType segmentType) {
		// nothing to map at segment level
		return newSegment();
	}

	public TrackSegment fromGpx10(final Trkseg gpxSegment) {
		// nothing to map at segment level
		return newSegment();
	}

	private TrackSegment newSegment() {
		final TrackSegment segment = new TrackSegment(TrackSegment.ABSOLUTE);
		return segment;
	}
}