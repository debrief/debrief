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

  public LayersHandler(MWC.GUI.Layers theLayers)
  {
    // inform our parent what type of class we are
    super("layers");

    addHandler(new LayerHandler(theLayers));
  }
  
	public static void exportThis(MWC.GUI.Layers data, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
  {
    // create ourselves
    Element layers = doc.createElement("layers");

    if(data == null)
      return;

    //
    int len = data.size();

    for(int i=0;i<len;i++)
    {
      MWC.GUI.Layer ly = data.elementAt(i);

      // find out which sort of layer this is
      if(ly instanceof MWC.GUI.BaseLayer)
      {
        LayerHandler.exportLayer((MWC.GUI.BaseLayer) ly, layers, doc);
      }

    }

    parent.appendChild(layers);

  }




}