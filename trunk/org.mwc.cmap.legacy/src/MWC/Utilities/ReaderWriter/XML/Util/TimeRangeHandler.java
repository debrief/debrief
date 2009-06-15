package MWC.Utilities.ReaderWriter.XML.Util;


/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.xml.sax.Attributes;

import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class TimeRangeHandler extends MWCXMLReader
{

  private HiResDate _start = MWC.GenericData.TimePeriod.INVALID_DATE;
  private HiResDate _end = MWC.GenericData.TimePeriod.INVALID_DATE;

  private java.text.SimpleDateFormat sdf = null;


  public TimeRangeHandler()
  {
    // inform our parent what type of class we are
    super("timeRange");

    sdf = new java.text.SimpleDateFormat("dd MMM yy HH:mm:ss");
    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
  }

  // this is one of ours, so get on with it!
  protected void handleOurselves(String name, Attributes attributes)
  {
    _start = _end = MWC.GenericData.TimePeriod.INVALID_DATE;

    int len = attributes.getLength();
    for (int i = 0; i < len; i++)
    {

      String nm = attributes.getLocalName(i);
      String val = attributes.getValue(i);
      if (nm.equals("Start"))
        _start = parseThisDate(val);
      else if (nm.equals("End"))
        _end = parseThisDate(val);

    }

  }

  public void elementClosed()
  {
    setTimeRange(_start, _end);

    _start = _end = MWC.GenericData.TimePeriod.INVALID_DATE;
  }

  abstract public void setTimeRange(HiResDate start, HiResDate end);


  public static void exportThis(HiResDate start, HiResDate end, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    boolean useful = false;
    org.w3c.dom.Element eTime = doc.createElement("timeRange");
    if (start != MWC.GenericData.TimePeriod.INVALID_DATE)
    {
      eTime.setAttribute("Start", writeThis(start));
      useful = true;
    }
    if (end != MWC.GenericData.TimePeriod.INVALID_DATE)
    {
      eTime.setAttribute("End", writeThis(end));
      useful = true;
    }

    if (useful)
    {
      parent.appendChild(eTime);
    }
  }


}