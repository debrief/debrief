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

import Debrief.Wrappers.Track.TMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Layers;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldVectorHandler;

abstract public class TMASegmentHandler extends CoreTrackSegmentHandler
{
	private static final String TMA_SEGMENT = "TMASegment";
	public static final String COURSE_DEGS = "CourseDegs";
	public static final String SPEED= "Speed";
	public static final String HOST="HostTrack";
	public static final String OFFSET="Offset";
	public static final String BASE_FREQ="BaseFrequency";
	

	private double _courseDegs = 0d;
	protected WorldSpeed _speed;
	protected String _host;
	protected WorldVector _offset;
	private final Layers _theLayers;
	protected double _baseFrequency;
	
	public TMASegmentHandler(Layers theLayers)
	{
		// inform our parent what type of class we are
		super(TMA_SEGMENT);

		_theLayers = theLayers;
		
		addAttributeHandler(new HandleDoubleAttribute(COURSE_DEGS)
		{
			@Override
			public void setValue(String name, double val)
			{
				_courseDegs = val;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(BASE_FREQ)
		{
			@Override
			public void setValue(String name, double val)
			{
				_baseFrequency = val;
			}
		});
		addAttributeHandler(new HandleAttribute(HOST)
		{
			@Override
			public void setValue(String name, String val)
			{
				_host = val;
			}
		});
		
		addHandler(new WorldSpeedHandler(SPEED){
			@Override
			public void setSpeed(WorldSpeed res)
			{
				_speed = res;
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
		TMASegment res = new TMASegment(_courseDegs, _speed, _offset, _theLayers);
		res.setBaseFrequency(_baseFrequency);
		res.setHostName(_host);
		return res;
	}


	public static void exportThisTMASegment(org.w3c.dom.Document doc, Element trk,
			TMASegment seg)
	{
		
		final Element segE = CoreTrackSegmentHandler.exportThisSegment(doc, seg, TMA_SEGMENT);

		// sort out the remaining attributes
		segE.setAttribute(COURSE_DEGS, writeThis(seg.getCourse()));
		segE.setAttribute(HOST, seg.getReferenceTrack().getName());
		segE.setAttribute(BASE_FREQ, writeThis( seg.getBaseFrequency()));
		
		WorldSpeedHandler.exportSpeed(SPEED, seg.getSpeed(), segE, doc);
		WorldVectorHandler.exportVector(OFFSET, seg.getOffset(), segE, doc);
		
		trk.appendChild(segE);
		
	}

}