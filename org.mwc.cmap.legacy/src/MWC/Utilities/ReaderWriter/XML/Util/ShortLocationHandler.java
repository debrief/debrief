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

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class ShortLocationHandler extends MWCXMLReader
{

  private double _lat;
  private double _long;
  private double _depth;

  public ShortLocationHandler()
  {
    // inform our parent what type of class we are
    super("shortLocation");

  }

  // this is one of ours, so get on with it!
  protected void handleOurselves(final String name, final Attributes attributes)
  {
    // initialise data
    _lat = _long = _depth = 0.0;

    final int len = attributes.getLength();
    for(int i=0; i<len;i++){

      final String nm = attributes.getQName(i);// getLocalName(i);
      final String val = attributes.getValue(i);
      try{
        if(nm.equals("Lat"))
          _lat = readThisDouble(val);
        else
          if(nm.equals("Long"))
          _long = readThisDouble(val);
        else
          if(nm.equals("Depth"))
          _depth = readThisDouble(val);
      }
      catch(final java.text.ParseException e)
      {
        MWC.Utilities.Errors.Trace.trace(e, "Failed reading in:" + nm + " value is:" + val);
      }
    }
  }



  public void elementClosed()
  {
    final MWC.GenericData.WorldLocation res = new MWC.GenericData.WorldLocation(_lat, _long, _depth);
    setLocation(res);
  }

  abstract public void setLocation(MWC.GenericData.WorldLocation res);

  public static void exportLocation(final MWC.GenericData.WorldLocation loc, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    final Element eLoc = doc.createElement("shortLocation");
    eLoc.setAttribute("Lat", writeThisLong(loc.getLat()));
    eLoc.setAttribute("Long", writeThisLong(loc.getLong()));
    eLoc.setAttribute("Depth", writeThis(loc.getDepth()));
    parent.appendChild(eLoc);
  }

}