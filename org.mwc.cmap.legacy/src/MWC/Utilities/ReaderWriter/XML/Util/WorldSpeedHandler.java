/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
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

import MWC.GenericData.WorldSpeed;


abstract public class WorldSpeedHandler extends BaseDataHandler
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  private final static String _myType = "Speed";
 
  //////////////////////////////////////////////////
  // constuctor
  //////////////////////////////////////////////////
  public WorldSpeedHandler()
  {
    super(_myType);   
  }

  public WorldSpeedHandler(final String myType)
  {
    super(myType);
  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////
  public void elementClosed()
  {
    // produce a value using these units
    final int theUnits = WorldSpeed.getUnitIndexFor(_units);
    final WorldSpeed res = new WorldSpeed(_value, theUnits);

    setSpeed(res);

    // and do the reset
    super.elementClosed();
  }

  abstract public void setSpeed(WorldSpeed res);

  public static void exportSpeed(final String element_type,
                                 final WorldSpeed speed,
                                 final org.w3c.dom.Element parent,
                                 final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element eLoc = doc.createElement(element_type);

    // set the attributes
    eLoc.setAttribute(VALUE, writeThis(speed.getValue()));
    eLoc.setAttribute(UNITS, speed.getUnitsLabel());

    parent.appendChild(eLoc);
  }

  public static void exportSpeed(final WorldSpeed speed, final org.w3c.dom.Element parent,
                                    final org.w3c.dom.Document doc)
  {
    exportSpeed(_myType, speed, parent, doc);
  }

}