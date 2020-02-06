
package ASSET.Util.XML;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/



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