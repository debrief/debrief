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

import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Layers;
import MWC.GenericData.WorldVector;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldVectorHandler;

abstract public class RelativeTMASegmentHandler extends CoreTMASegmentHandler
{
	private static final String TMA_SEGMENT = "RelativeTMASegment";
	public static final String HOST="HostTrack";
	public static final String OFFSET="Offset";	

	protected String _host;
	protected WorldVector _offset;
	private final Layers _theLayers;
	
	public RelativeTMASegmentHandler(Layers theLayers)
	{
		// inform our parent what type of class we are
		super(theLayers, TMA_SEGMENT);

		_theLayers = theLayers;
		
		addAttributeHandler(new HandleAttribute(HOST)
		{
			@Override
			public void setValue(String name, String val)
			{
				_host = val;
			}
		});
		
		addHandler(new WorldVectorHandler(OFFSET){
		
			@Override
			public void setWorldVector(WorldVector res)
			{
				_offset = res;
			}
		});
		

	}
	
	

	@Override
	protected TrackSegment createTrack()
	{
		RelativeTMASegment res = new RelativeTMASegment(_courseDegs, _speed, _offset, _theLayers);
		res.setBaseFrequency(_baseFrequency);
		res.setHostName(_host);
		return res;
	}


	public static void exportThisTMASegment(org.w3c.dom.Document doc, Element trk,
			RelativeTMASegment seg)
	{
		
		final Element segE = CoreTrackSegmentHandler.exportThisSegment(doc, seg, TMA_SEGMENT);
	
		// export the start bits
		CoreTMASegmentHandler.exportThisTMASegment(doc, seg, segE);

		// sort out the remaining attributes
		segE.setAttribute(HOST, seg.getReferenceTrack().getName());		
		WorldSpeedHandler.exportSpeed(SPEED, seg.getSpeed(), segE, doc);
	
		trk.appendChild(segE);
		
	}

}