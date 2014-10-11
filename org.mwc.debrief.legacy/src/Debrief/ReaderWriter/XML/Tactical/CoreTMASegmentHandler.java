/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
	
	public CoreTMASegmentHandler(final String myName)
	{
		// inform our parent what type of class we are
		super(myName);

		
		addAttributeHandler(new HandleDoubleAttribute(COURSE_DEGS)
		{
			@Override
			public void setValue(final String name, final double val)
			{
				_courseDegs = val;
			}
		});
		addAttributeHandler(new HandleDoubleAttribute(BASE_FREQ)
		{
			@Override
			public void setValue(final String name, final double val)
			{
				_baseFrequency = val;
			}
		});
		
		addHandler(new WorldSpeedHandler(SPEED){
			@Override
			public void setSpeed(final WorldSpeed res)
			{
				_speed = res;
			}
		});
	}
	
	public static void exportThisTMASegment(final org.w3c.dom.Document doc, final CoreTMASegment theSegment, final Element theElement)
	{
		// sort out the remaining attributes
		theElement.setAttribute(COURSE_DEGS, writeThis(theSegment.getCourse()));
		theElement.setAttribute(BASE_FREQ, writeThis(theSegment.getBaseFrequency()));
		WorldSpeedHandler.exportSpeed(SPEED, theSegment.getSpeed(), theElement, doc);
	}

}