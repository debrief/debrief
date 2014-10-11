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

  private static final String MY_TYPE = "timeRange";
	private HiResDate _start = MWC.GenericData.TimePeriod.INVALID_DATE;
  private HiResDate _end = MWC.GenericData.TimePeriod.INVALID_DATE;

  private java.text.SimpleDateFormat sdf = null;

  public TimeRangeHandler(final String type)
  {
    // inform our parent what type of class we are
    super(type);

    sdf = new java.text.SimpleDateFormat("dd MMM yy HH:mm:ss");
    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
  	
  }

  public TimeRangeHandler()
  {
  	this(MY_TYPE);
  }

  // this is one of ours, so get on with it!
  protected void handleOurselves(final String name, final Attributes attributes)
  {
    _start = _end = MWC.GenericData.TimePeriod.INVALID_DATE;

    final int len = attributes.getLength();
    for (int i = 0; i < len; i++)
    {

      final String nm = attributes.getLocalName(i);
      final String val = attributes.getValue(i);
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

  public static void exportThis(final HiResDate start, final HiResDate end, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
  	exportThis(start, end, parent, doc, MY_TYPE);
  }

  public static void exportThis(final HiResDate start, final HiResDate end, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc, final String type)
  {
    boolean useful = false;
    final org.w3c.dom.Element eTime = doc.createElement(type);
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