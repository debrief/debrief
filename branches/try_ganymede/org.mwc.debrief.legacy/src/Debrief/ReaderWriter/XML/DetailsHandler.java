package Debrief.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


public final class DetailsHandler extends MWCXMLReader
{

//  private Debrief.GUI.Frames.Session _session;

  public DetailsHandler(Object destination)
  {
    // inform our parent what type of class we are
    super("details");

//    if(destination instanceof Debrief.GUI.Frames.Session)
//    {
//      _session = (Debrief.GUI.Frames.Session)destination;
//    }

  }


  public static void exportPlot(String details, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element det = doc.createElement("details");
    det.setAttribute("Text", details);

    parent.appendChild(det);
  }


}