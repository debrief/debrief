package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.xml.sax.Attributes;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Plottable;

abstract public class TrackSegmentHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	Debrief.Wrappers.SensorWrapper _mySensor;
	private static final String TRACK_SEGMENT = "TrackSegment";

	private TrackSegment _segment;

	public TrackSegmentHandler()
	{
		// inform our parent what type of class we are
		super(TRACK_SEGMENT);

		addHandler(new FixHandler()
		{
			public void addPlottable(Plottable plottable)
			{
				_segment.addFix((FixWrapper) plottable);
			}
		});

	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(String name, Attributes attributes)
	{
		_segment = new TrackSegment();
		super.handleOurselves(name, attributes);
	}

	public final void elementClosed()
	{
		addSegment(_segment);
		_segment = null;
	}

	abstract public void addSegment(TrackSegment segment);

}