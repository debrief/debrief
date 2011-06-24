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

import Debrief.Wrappers.Track.CoreTMASegment;
import MWC.GUI.Layers;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

abstract public class CoreTMASegmentHandler extends CoreTrackSegmentHandler
{
	public static final String COURSE_DEGS = "CourseDegs";
	public static final String SPEED= "Speed";
	public static final String BASE_FREQ="BaseFrequency";
	

	protected double _courseDegs = 0d;
	protected  WorldSpeed _speed;
	protected double _baseFrequency;
	
	public CoreTMASegmentHandler(Layers theLayers, String myName)
	{
		// inform our parent what type of class we are
		super(myName);

		
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
		
		addHandler(new WorldSpeedHandler(SPEED){
			@Override
			public void setSpeed(WorldSpeed res)
			{
				_speed = res;
			}
		});
	}
	
	public static void exportThisTMASegment(org.w3c.dom.Document doc, CoreTMASegment theSegment, Element theElement)
	{
		// sort out the remaining attributes
		theElement.setAttribute(COURSE_DEGS, writeThis(theSegment.getCourse()));
		theElement.setAttribute(BASE_FREQ, writeThis(theSegment.getBaseFrequency()));
		WorldSpeedHandler.exportSpeed(SPEED, theSegment.getSpeed(), theElement, doc);
	}

}