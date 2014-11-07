/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
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

import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.TrackSegment;

abstract public class DynamicInfillSegmentHandler extends CoreTrackSegmentHandler
{
	private static final String DYNAMIC_SEGMENT = "DynamicInfillSegment";
	private static final String BEFORE = "BeforeLeg";
	private static final String AFTER = "AfterLeg";

	private String _beforeName;
	private String _afterName;
	
	public DynamicInfillSegmentHandler()
	{
		// inform our parent what type of class we are
		super(DYNAMIC_SEGMENT);

		addAttributeHandler(new HandleAttribute(BEFORE)
		{
			@Override
			public void setValue(String name, String value)
			{
				_beforeName = value;
			}
		});
		addAttributeHandler(new HandleAttribute(AFTER)
		{
			@Override
			public void setValue(String name, String value)
			{
				_afterName = value;
			}
		});
	}
	
	

	@Override
	protected TrackSegment createTrack()
	{
		final TrackSegment res = new DynamicInfillSegment(_beforeName, _afterName);
		return res;
	}


	public static void exportThisSegment(final org.w3c.dom.Document doc, final Element trk,
			final DynamicInfillSegment seg)
	{		
		final Element segE = CoreTrackSegmentHandler.exportThisSegment(doc, seg, DYNAMIC_SEGMENT);
	
		// sort out the remaining attributes
		segE.setAttribute(BEFORE, seg.getBeforeName());
		segE.setAttribute(AFTER, seg.getAfterName());
	
		trk.appendChild(segE);
	}
}