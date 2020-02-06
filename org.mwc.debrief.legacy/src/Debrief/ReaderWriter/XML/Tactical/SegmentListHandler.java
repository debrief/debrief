
package Debrief.ReaderWriter.XML.Tactical;

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

import org.xml.sax.Attributes;

import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Layers;

abstract public class SegmentListHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	public static final String SEGMENT_LIST = "SegmentList";
	Debrief.Wrappers.SensorWrapper _mySensor;

	SegmentList _list;

	public SegmentListHandler(final Layers theLayers) {
		// inform our parent what type of class we are
		super(SEGMENT_LIST);

		addHandler(new RelativeTMASegmentHandler(theLayers) {
			@Override
			public void addSegment(final TrackSegment segment) {
				addThisSegment(segment);
			}
		});
		addHandler(new AbsoluteTMASegmentHandler() {
			@Override
			public void addSegment(final TrackSegment segment) {
				addThisSegment(segment);
			}
		});
		addHandler(new DynamicInfillSegmentHandler() {
			@Override
			public void addSegment(final TrackSegment segment) {
				addThisSegment(segment);
			}
		});

		addHandler(new TrackSegmentHandler() {
			@Override
			public void addSegment(final TrackSegment segment) {
				addThisSegment(segment);
			}
		});
	}

	abstract public void addThisSegment(TrackSegment list);

	@Override
	public final void elementClosed() {
		_list = null;
	}

	// this is one of ours, so get on with it!
	@Override
	protected final void handleOurselves(final String name, final Attributes attributes) {
		_list = new SegmentList();

		super.handleOurselves(name, attributes);

	}
}