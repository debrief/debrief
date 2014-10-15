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
 * PlanetMayo Ltd.  2003
 * User: Ian Mayo
 * Date: 18 Sep 03
 * Time: 15:30
 * Log:
 *  $Log: WorldAccelerationHandler.java,v $
 *  Revision 1.3  2004/05/24 16:24:55  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:31:28  ian
 *  no message
 *
 *  Revision 1.2  2003/09/19 13:37:01  Ian.Mayo
 *  Provide improved constructors
 *
 *  Revision 1.1  2003/09/18 14:33:33  Ian.Mayo
 *  Initial implementation
 *
 *
 */


import MWC.GenericData.WorldAcceleration;


abstract public class WorldAccelerationHandler extends BaseDataHandler
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  private final static String _myType = "Acceleration";


  //////////////////////////////////////////////////
  // constuctor
  //////////////////////////////////////////////////
  public WorldAccelerationHandler()
  {
    super(_myType);
  }

  public WorldAccelerationHandler(final String myType)
  {
    super(myType);
  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////
  public void elementClosed()
  {
    // produce a value using these units
    final int theUnits = WorldAcceleration.getUnitIndexFor(_units);
    final WorldAcceleration res = new WorldAcceleration(_value, theUnits);

    setAcceleration(res);

    // and do the reset
    super.elementClosed();
  }

  abstract public void setAcceleration(WorldAcceleration res);

  public static void exportAcceleration(final String element_type,
                                 final WorldAcceleration Acceleration,
                                 final org.w3c.dom.Element parent,
                                 final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element eLoc = doc.createElement(element_type);

    // set the attributes
    final int theUnit = Acceleration.getUnits();

    // and get value
    final double value = Acceleration.getValue();

    // get the name of the units
    final String units = WorldAcceleration.getLabelFor(theUnit);

    eLoc.setAttribute(VALUE, writeThis(value));
    eLoc.setAttribute(UNITS, units);

    parent.appendChild(eLoc);
  }

  public static void exportAcceleration(final WorldAcceleration Acceleration, final org.w3c.dom.Element parent,
                                    final org.w3c.dom.Document doc)
  {
    exportAcceleration(_myType, Acceleration, parent, doc);
  }

}