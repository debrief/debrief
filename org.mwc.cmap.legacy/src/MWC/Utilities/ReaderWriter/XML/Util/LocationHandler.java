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
package MWC.Utilities.ReaderWriter.XML.Util;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


abstract public class LocationHandler extends MWCXMLReader
{

  MWC.GenericData.WorldLocation _res = null;

   public LocationHandler(final String name)
  {
    // inform our parent what type of class we are
    super(name);

    addHandler(new ShortLocationHandler(){
      public void setLocation(final MWC.GenericData.WorldLocation res)
      {
        _res = res;
      }
    });
    addHandler(new LongLocationHandler(){
      public void setLocation(final MWC.GenericData.WorldLocation res)
      {
        _res = res;
      }
    });

  }


  public void elementClosed()
  {
    // pass on to the listener class
    setLocation(_res);

    _res = null;
  }

  abstract public void setLocation(MWC.GenericData.WorldLocation res);


  public static void exportLocation(final MWC.GenericData.WorldLocation loc, final String title, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element eLoc = doc.createElement(title);
    // for now, stick with exporting locations in short form
    ShortLocationHandler.exportLocation(loc, eLoc, doc);
    parent.appendChild(eLoc);
  }


}