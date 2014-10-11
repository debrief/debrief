/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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


abstract public class FontHandler extends MWCXMLReader
{

  String _family;
  int _size;
  boolean _isBold;
  boolean _isItalic;

  public FontHandler()
  {
    // inform our parent what type of class we are
    super("font");

    addAttributeHandler(new HandleAttribute("Family")
    { public void setValue(final String name, final String val)
      {
        _family = val;
      }});
    addAttributeHandler(new HandleAttribute("Size")
    { public void setValue(final String name, final String val)
      {
        _size = Integer.valueOf(val).intValue();
      }});
    addAttributeHandler(new HandleAttribute("Italic")
    { public void setValue(final String name, final String val)
      {
        _isItalic = Boolean.valueOf(val).booleanValue();
      }});
    addAttributeHandler(new HandleAttribute("Bold")
    { public void setValue(final String name, final String val)
      {
        _isBold = Boolean.valueOf(val).booleanValue();
      }});
  }



  public void elementClosed()
  {
    // produce a font from these parameters
    // create the plain font
    java.awt.Font _res = new java.awt.Font(_family, java.awt.Font.PLAIN, _size);

    // correct the styling, if there is any!
    if(_isBold)
      _res = _res.deriveFont(java.awt.Font.BOLD);
    if(_isItalic)
      _res = _res.deriveFont(java.awt.Font.ITALIC);

    // pass the font onto the listener
    setFont(_res);

    _family = null;
    _size = 0;
    _isBold = _isItalic = false;

  }


  abstract public void setFont(java.awt.Font res);


  public static void exportFont(final java.awt.Font font, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {

    final org.w3c.dom.Element eLoc = doc.createElement("font");
    // set the attributes
    eLoc.setAttribute("Family", font.getName());
    eLoc.setAttribute("Size", writeThis(font.getSize()));
    eLoc.setAttribute("Bold", writeThis(font.isBold()));
    eLoc.setAttribute("Italic", writeThis(font.isItalic()));

    parent.appendChild(eLoc);
  }

}