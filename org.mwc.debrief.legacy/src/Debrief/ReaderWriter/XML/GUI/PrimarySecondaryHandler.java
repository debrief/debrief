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
package Debrief.ReaderWriter.XML.GUI;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */





abstract public class PrimarySecondaryHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
  String _name;

  public PrimarySecondaryHandler(final String type)
  {
    // inform our parent what type of class we are
    super(type);

    addAttributeHandler(new HandleAttribute("Name")
    {
      public void setValue(final String name, final String value)
      {
        _name = value;
      }
    });

  }

  public final void elementClosed()
  {
    setTrack(_name);

    // reset our variables
    _name = null;
  }

  // pass on to the parent the name of this track
  abstract public void setTrack(String name);
}