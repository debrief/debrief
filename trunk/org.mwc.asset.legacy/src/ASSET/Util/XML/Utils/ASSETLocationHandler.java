package ASSET.Util.XML.Utils;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.xml.sax.Attributes;

abstract public class ASSETLocationHandler extends MWCXMLReader
{

  MWC.GenericData.WorldLocation _res = null;

  public ASSETLocationHandler(String name)
  {
    // inform our parent what type of class we are
    super(name);

    addHandler(new ASSETShortLocationHandler()
    {
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _res = res;
      }
    });
    addHandler(new ASSETLongLocationHandler()
    {
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _res = res;
      }
    });
    addHandler(new ASSETRelativeLocationHandler()
    {
      public void setLocation(MWC.GenericData.WorldLocation res)
      {
        _res = res;
      }
    });

  }

  // this is one of ours, so get on with it!
  protected void handleOurselves(String name, Attributes attributes)
  {
  	// ignore attributes
  }


  public void elementClosed()
  {
    // pass on to the listener class
    setLocation(_res);
  }

  abstract public void setLocation(MWC.GenericData.WorldLocation res);


  public static void exportLocation(MWC.GenericData.WorldLocation loc, String title, org.w3c.dom.Element parent,
                                    org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eLoc = doc.createElement(title);
    // for now, stick with exporting locations in short form
    ASSETShortLocationHandler.exportLocation(loc, eLoc, doc);
    parent.appendChild(eLoc);
  }


}