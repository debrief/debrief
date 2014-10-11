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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


abstract public class WorldDistanceHandler extends MWCXMLReader
{

  String _units = WorldDistance.UnitLabels[WorldDistance.NM];
  double _value;
  private static final String UNITS = "Units";
  private static final String VALUE = "Value";

  public WorldDistanceHandler(final String myType)
  {
    super(myType);

    addAttributeHandler(new HandleAttribute(UNITS)
    {
      public void setValue(final String name, final String val)
      {
        _units = val;
      }
    });
    addAttributeHandler(new HandleDoubleAttribute(VALUE)
    {
      public void setValue(final String name, final double val)
      {
        _value = val;
      }
    });
  }


  public WorldDistanceHandler()
  {
    // inform our parent what type of class we are
    this("WorldDistance");
  }


  public void elementClosed()
  {
    // produce a value using these units
    final int theUnits = MWC.GenericData.WorldDistance.getUnitIndexFor(_units);
    final WorldDistance res = new WorldDistance(_value, theUnits);

    setWorldDistance(res);

    // reset the units.  If it doesn't get overwritten we continue to use NM
    _units = WorldDistance.UnitLabels[WorldDistance.NM];
    _value = -1;


  }


  public  static WorldDistance extractWorldDistance(final Element topL, final String name)
  {
	Element theTopL = topL;
    WorldDistance myDist = null;
    final NodeList list = theTopL.getElementsByTagName(name);
    if (list.getLength() > 0)
    {
      theTopL = (Element) list.item(0);
      final String unitsVal = theTopL.getAttribute(UNITS);
      final int units = WorldDistance.getUnitIndexFor(unitsVal);
      final double value = Integer.parseInt(theTopL.getAttribute(VALUE));

      myDist = new WorldDistance(value, units);
    }
    return myDist;
  }


  abstract public void setWorldDistance(WorldDistance res);


  public static void exportDistance(final WorldDistance distance, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    exportDistance("WorldDistance", distance, parent, doc);
  }

  public static void exportDistance(final String myType, final WorldDistance distance, final org.w3c.dom.Element parent,
                                    final org.w3c.dom.Document doc)
  {
  	// create the object
    final org.w3c.dom.Element eLoc = doc.createElement(myType);
    
    // store the fields
    eLoc.setAttribute(VALUE, writeThisLong(distance.getValue()));
    eLoc.setAttribute(UNITS, distance.getUnitsLabel());

    // remember it
    parent.appendChild(eLoc);
  }

}