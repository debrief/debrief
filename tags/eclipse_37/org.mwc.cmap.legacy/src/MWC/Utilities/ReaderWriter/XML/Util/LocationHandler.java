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

   public LocationHandler(String name)
  {
    // inform our parent what type of class we are
    super(name);

    addHandler(new ShortLocationHandler(){
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _res = res;
      }
    });
    addHandler(new LongLocationHandler(){
      public void setLocation(MWC.GenericData.WorldLocation res)
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


  public static void exportLocation(MWC.GenericData.WorldLocation loc, String title, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eLoc = doc.createElement(title);
    // for now, stick with exporting locations in short form
    ShortLocationHandler.exportLocation(loc, eLoc, doc);
    parent.appendChild(eLoc);
  }


}