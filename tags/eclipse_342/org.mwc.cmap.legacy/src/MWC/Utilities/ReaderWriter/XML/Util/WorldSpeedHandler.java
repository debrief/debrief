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

  public WorldSpeedHandler(String myType)
  {
    super(myType);
  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////
  public void elementClosed()
  {
    // produce a value using these units
    int theUnits = WorldSpeed.getUnitIndexFor(_units);
    WorldSpeed res = new WorldSpeed(_value, theUnits);

    setSpeed(res);

    // and do the reset
    super.elementClosed();
  }

  abstract public void setSpeed(WorldSpeed res);

  public static void exportSpeed(String element_type,
                                 WorldSpeed speed,
                                 org.w3c.dom.Element parent,
                                 org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eLoc = doc.createElement(element_type);

    // set the attributes
    int theUnit = WorldSpeed.selectUnitsFor(speed.getValueIn(WorldSpeed.getSIUnits()));

    // and get value
    double value = speed.getValueIn(theUnit);

    // get the name of the units
    String units = WorldSpeed.getLabelFor(theUnit);

    eLoc.setAttribute(VALUE, writeThis(value));
    eLoc.setAttribute(UNITS, units);

    parent.appendChild(eLoc);
  }

  public static void exportSpeed(WorldSpeed speed, org.w3c.dom.Element parent,
                                    org.w3c.dom.Document doc)
  {
    exportSpeed(_myType, speed, parent, doc);
  }

}