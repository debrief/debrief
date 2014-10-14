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

import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.Util.LocationHandler;

abstract public class AbsoluteTMASegmentHandler extends CoreTMASegmentHandler
{
	private static final String TMA_SEGMENT = "AbsoluteTMASegment";
	public static final String ORIGIN="Origin";

	protected WorldLocation _origin;
	
	public AbsoluteTMASegmentHandler()
	{
		// inform our parent what type of class we are
		super(TMA_SEGMENT);

		addHandler(new LocationHandler(ORIGIN)
		{
			@Override
			public void setLocation(final WorldLocation val)
			{
				_origin = val;
			}
		});
	}
	
	

	@Override
	protected TrackSegment createTrack()
	{
		final AbsoluteTMASegment res = new AbsoluteTMASegment(_courseDegs, _speed, _origin, null, null);
		res.setBaseFrequency(_baseFrequency);
		return res;
	}


	public static void exportThisTMASegment(final org.w3c.dom.Document doc, final Element trk,
			final AbsoluteTMASegment seg)
	{
		
		final Element segE = CoreTrackSegmentHandler.exportThisSegment(doc, seg, TMA_SEGMENT);
	
		// export the start bits
		CoreTMASegmentHandler.exportThisTMASegment(doc, seg, segE);

		// sort out the remaining attributes
		LocationHandler.exportLocation(seg.getTrackStart(), ORIGIN, segE, doc);
	
		trk.appendChild(segE);
		
	}

}