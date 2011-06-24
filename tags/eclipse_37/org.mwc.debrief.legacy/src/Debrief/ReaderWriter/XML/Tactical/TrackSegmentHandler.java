package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;

import Debrief.Wrappers.Track.TrackSegment;

abstract public class TrackSegmentHandler extends CoreTrackSegmentHandler
{
	private static final String TRACK_SEGMENT = "TrackSegment";
	public static final String PLOT_RELATIVE = "PlotRelative";

	private boolean _relativeMode = false;

	public TrackSegmentHandler()
	{
		// inform our parent what type of class we are
		super(TRACK_SEGMENT);

		addAttributeHandler(new HandleBooleanAttribute(PLOT_RELATIVE)
		{
			@Override
			public void setValue(String name, boolean val)
			{
				_relativeMode = val;
			}
		});


	}
	
	

	@Override
	protected TrackSegment createTrack()
	{
		TrackSegment res = new TrackSegment();
		res.setPlotRelative(_relativeMode);
		return res;
	}


	public static void exportThisSegment(org.w3c.dom.Document doc, Element trk,
			TrackSegment seg)
	{
		
		final Element segE = CoreTrackSegmentHandler.exportThisSegment(doc, seg, TRACK_SEGMENT);

		// sort out the remaining attributes
		segE.setAttribute(PLOT_RELATIVE, writeThis(seg.getPlotRelative()));
		
		trk.appendChild(segE);
	}

}