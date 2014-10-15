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
package MWC.Utilities.ReaderWriter.XML;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;

public class LayersHandler extends MWCXMLReader
{

  public LayersHandler(final MWC.GUI.Layers theLayers)
  {
    // inform our parent what type of class we are
    super("layers");

    addHandler(new LayerHandler(theLayers));
  }
  
	public static void exportThis(final MWC.GUI.Layers data, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    // create ourselves
    final Element layers = doc.createElement("layers");

    if(data == null)
      return;

    //
    final int len = data.size();

    for(int i=0;i<len;i++)
    {
      final MWC.GUI.Layer ly = data.elementAt(i);

      // find out which sort of layer this is
      if(ly instanceof MWC.GUI.BaseLayer)
      {
        LayerHandler.exportLayer((MWC.GUI.BaseLayer) ly, layers, doc);
      }

    }

    parent.appendChild(layers);

  }




}