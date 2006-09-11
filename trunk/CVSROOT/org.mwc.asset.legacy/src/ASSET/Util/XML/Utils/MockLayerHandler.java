package ASSET.Util.XML.Utils;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GUI.BaseLayer;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.xml.sax.Attributes;

abstract public class MockLayerHandler extends MWCXMLReader
{
	BaseLayer _res = null;

  public MockLayerHandler(String name)
  {
    // inform our parent what type of class we are
    super(name);
  }

  // this is one of ours, so get on with it!
  protected void handleOurselves(String name, Attributes attributes)
  {
  	// right, get the guts of the element
  }


  public void elementClosed()
  {
    // pass on to the listener class
    setLayer(_res);
  }

  abstract public void setLayer(BaseLayer theLayer);


  public static void exportLocation(MWC.GenericData.WorldLocation loc, String title, org.w3c.dom.Element parent,
                                    org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eLoc = doc.createElement(title);
    // for now, stick with exporting locations in short form
    ASSETShortLocationHandler.exportLocation(loc, eLoc, doc);
    parent.appendChild(eLoc);
  }


}