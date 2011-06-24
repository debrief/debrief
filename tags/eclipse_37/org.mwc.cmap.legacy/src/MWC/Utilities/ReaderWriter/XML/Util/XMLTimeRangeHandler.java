package MWC.Utilities.ReaderWriter.XML.Util;


/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class XMLTimeRangeHandler extends MWCXMLReader
{

  private static final String MY_TYPE = "timeRange";
	private static final String START = "Start";
	private static final String END = "End";
	private HiResDate _start = MWC.GenericData.TimePeriod.INVALID_DATE;
  private HiResDate _end = MWC.GenericData.TimePeriod.INVALID_DATE;
  
  public XMLTimeRangeHandler(String type)
  {
    // inform our parent what type of class we are
    super(type);
    
    // sort out the handlers
    addAttributeHandler(new HandleDateTimeAttribute(START)
    {
      public void setValue(String name, final long val)
      {
      	_start = new HiResDate(val);
      }
    });
    // sort out the handlers
    addAttributeHandler(new HandleDateTimeAttribute(END)
    {
      public void setValue(String name, final long val)
      {
      	_end = new HiResDate(val);
      }
    });
  }

  public XMLTimeRangeHandler()
  {
  	this(MY_TYPE);
  }

  public void elementClosed()
  {
    setTimeRange(_start, _end);

    _start = _end = MWC.GenericData.TimePeriod.INVALID_DATE;
  }

  abstract public void setTimeRange(HiResDate start, HiResDate end);

  public static void exportThis(HiResDate start, HiResDate end, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
  	exportThis(start, end, parent, doc, MY_TYPE);
  }

  public static void exportThis(HiResDate start, HiResDate end, org.w3c.dom.Element parent, org.w3c.dom.Document doc, String type)
  {
    boolean useful = false;
    org.w3c.dom.Element eTime = doc.createElement(type);
    if (start != MWC.GenericData.TimePeriod.INVALID_DATE)
    {
      eTime.setAttribute(START, writeThisInXML(start.getDate()));
      useful = true;
    }
    if (end != MWC.GenericData.TimePeriod.INVALID_DATE)
    {
      eTime.setAttribute(END, writeThisInXML(end.getDate()));
      useful = true;
    }

    if (useful)
    {
      parent.appendChild(eTime);
    }
  }


}