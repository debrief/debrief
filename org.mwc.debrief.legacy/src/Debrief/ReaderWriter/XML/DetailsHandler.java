/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

  public DetailsHandler(final Object destination)
  {
    // inform our parent what type of class we are
    super("details");

//    if(destination instanceof Debrief.GUI.Frames.Session)
//    {
//      _session = (Debrief.GUI.Frames.Session)destination;
//    }

  }


  public static void exportPlot(final String details, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element det = doc.createElement("details");
    det.setAttribute("Text", details);

    parent.appendChild(det);
  }


}