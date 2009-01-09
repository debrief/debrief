package Debrief.ReaderWriter.XML.GUI;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import Debrief.ReaderWriter.XML.GUIHandler;
import MWC.Utilities.ReaderWriter.XML.Util.PropertyHandler;



abstract public class ComponentHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  GUIHandler.ComponentDetails details = new GUIHandler.ComponentDetails();

  public ComponentHandler()
  {
    // inform our parent what type of class we are
    super("component");

    addHandler(new PropertyHandler()
    {
      public void setProperty(String name, String val)
      {
        details.addProperty(name, val);
      }
    });

    super.addAttributeHandler(new HandleAttribute("Type")
    {
      public void setValue(String name, String val){
        details.type = val;
      }
    });
  }

  public final void elementClosed()
  {
    addComponent(details);
    details = null;
    details = new GUIHandler.ComponentDetails();
  }


  abstract public void addComponent(GUIHandler.ComponentDetails details1);

  // the exporter for this component details item

}