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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
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



abstract public class PropertyHandler extends MWCXMLReader
{
  String _name = null;
  String _value = null;

  public PropertyHandler()
  {
    // inform our parent what type of class we are
    super("property");

    super.addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(final String name, final String val){
        _name = val;
      }
    });

    super.addAttributeHandler(new HandleAttribute("Value")
    {
      public void setValue(final String name, final String val){
        _value = val;
      }
    });


  }

  public void elementClosed()
  {
    setProperty(_name, _value);
    _name = null;
    _value = null;
  }


  abstract public void setProperty(String name, String value);

  public static void exportProperty(final String name, final String value, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element prop  = doc.createElement("property");
    prop.setAttribute("Name", name);
    prop.setAttribute("Value", value);
    parent.appendChild(prop);
  }
}