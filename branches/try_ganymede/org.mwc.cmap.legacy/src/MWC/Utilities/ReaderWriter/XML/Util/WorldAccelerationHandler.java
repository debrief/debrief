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


import MWC.GenericData.*;


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

  public WorldAccelerationHandler(String myType)
  {
    super(myType);
  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////
  public void elementClosed()
  {
    // produce a value using these units
    int theUnits = WorldAcceleration.getUnitIndexFor(_units);
    WorldAcceleration res = new WorldAcceleration(_value, theUnits);

    setAcceleration(res);

    // and do the reset
    super.elementClosed();
  }

  abstract public void setAcceleration(WorldAcceleration res);

  public static void exportAcceleration(String element_type,
                                 WorldAcceleration Acceleration,
                                 org.w3c.dom.Element parent,
                                 org.w3c.dom.Document doc)
  {
    org.w3c.dom.Element eLoc = doc.createElement(element_type);

    // set the attributes
    int theUnit = WorldAcceleration.selectUnitsFor(Acceleration.getValueIn(WorldAcceleration.getSIUnits()));

    // and get value
    double value = Acceleration.getValueIn(theUnit);

    // get the name of the units
    String units = WorldAcceleration.getLabelFor(theUnit);

    eLoc.setAttribute(VALUE, writeThis(value));
    eLoc.setAttribute(UNITS, units);

    parent.appendChild(eLoc);
  }

  public static void exportAcceleration(WorldAcceleration Acceleration, org.w3c.dom.Element parent,
                                    org.w3c.dom.Document doc)
  {
    exportAcceleration(_myType, Acceleration, parent, doc);
  }

}