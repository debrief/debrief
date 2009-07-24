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

import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

abstract public class AbsoluteTMASegmentHandler extends CoreTMASegmentHandler
{
	private static final String TMA_SEGMENT = "AbsoluteTMASegment";
	public static final String ORIGIN="Origin";

	protected WorldLocation _origin;
	
	public AbsoluteTMASegmentHandler(Layers theLayers)
	{
		// inform our parent what type of class we are
		super(theLayers, TMA_SEGMENT);

		addHandler(new LocationHandler(ORIGIN)
		{
			@Override
			public void setLocation(WorldLocation val)
			{
				_origin = val;
			}
		});
	}
	
	

	@Override
	protected TrackSegment createTrack()
	{
		AbsoluteTMASegment res = new AbsoluteTMASegment(_courseDegs, _speed, _origin, null, null);
		res.setBaseFrequency(_baseFrequency);
		return res;
	}


	public static void exportThisTMASegment(org.w3c.dom.Document doc, Element trk,
			AbsoluteTMASegment seg)
	{
		
		final Element segE = CoreTrackSegmentHandler.exportThisSegment(doc, seg, TMA_SEGMENT);
	
		// export the start bits
		CoreTMASegmentHandler.exportThisTMASegment(doc, seg, segE);

		// sort out the remaining attributes
		LocationHandler.exportLocation(seg.getTrackStart(), ORIGIN, segE, doc);
	
		trk.appendChild(segE);
		
	}

}