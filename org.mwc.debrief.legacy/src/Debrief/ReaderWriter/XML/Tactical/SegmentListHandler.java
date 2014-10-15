/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
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

import org.xml.sax.Attributes;

import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Layers;


abstract public class SegmentListHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  Debrief.Wrappers.SensorWrapper _mySensor;
	public static final String SEGMENT_LIST = "SegmentList";

	SegmentList _list;
	
  public SegmentListHandler(final Layers theLayers)
  {
    // inform our parent what type of class we are
    super(SEGMENT_LIST);

    addHandler(new RelativeTMASegmentHandler(theLayers)
    {
      public void addSegment(final TrackSegment segment)
      {
      	addThisSegment(segment);
      }
    });
    addHandler(new AbsoluteTMASegmentHandler()
    {
      public void addSegment(final TrackSegment segment)
      {
      	addThisSegment(segment);
      }
    });

    addHandler(new TrackSegmentHandler()
    {
      public void addSegment(final TrackSegment segment)
      {
      	addThisSegment(segment);
      }
    });
  }

  // this is one of ours, so get on with it!
  protected final void handleOurselves(final String name, final Attributes attributes)
  {
    _list = new SegmentList();

    super.handleOurselves(name, attributes);

  }

  public final void elementClosed()
  {
    _list = null;
  }

	abstract public void addThisSegment(TrackSegment list);
}