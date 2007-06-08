package ASSET.Util.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.xml.sax.*;
import MWC.Utilities.ReaderWriter.XML.XMLHandler;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


class DetailsHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{


  public DetailsHandler(Object destination)
  {
    // inform our parent what type of class we are
    super("details");
  }


  public static void exportPlot(final String details, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element det = doc.createElement("details");
    det.setAttribute("Text", details);

    parent.appendChild(det);
  }


}