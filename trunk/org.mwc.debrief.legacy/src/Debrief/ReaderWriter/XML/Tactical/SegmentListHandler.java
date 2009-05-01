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

import Debrief.Wrappers.TrackWrapper_Support.SegmentList;
import Debrief.Wrappers.TrackWrapper_Support.TrackSegment;


abstract public class SegmentListHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  Debrief.Wrappers.SensorWrapper _mySensor;
	public static final String SEGMENT_LIST = "SegmentList";

	SegmentList _list;
	
  public SegmentListHandler()
  {
    // inform our parent what type of class we are
    super(SEGMENT_LIST);


    addHandler(new TrackSegmentHandler()
    {
      public void addSegment(TrackSegment segment)
      {
      	addThisSegment(segment);
      }
    });
  }

  // this is one of ours, so get on with it!
  protected final void handleOurselves(String name, Attributes attributes)
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