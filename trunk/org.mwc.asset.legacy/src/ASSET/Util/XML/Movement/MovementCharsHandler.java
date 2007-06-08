package ASSET.Util.XML.Movement;

import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAccelerationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */


abstract public class MovementCharsHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

  private final static String type = "MovementCharacteristics";

  protected final static String ACCEL = "AccelerationRate";
  protected final static String DECEL = "DecelerationRate";
  protected final static String FUEL = "FuelUsageRate";
  protected final static String MAX_SPEED = "MaxSpeed";
  protected final static String MIN_SPEED = "MinSpeed";


  protected WorldAcceleration _myAccelRate;
  protected WorldAcceleration _myDecelRate;
  protected double _myFuel;
  protected WorldSpeed _myMaxSpd;
  protected WorldSpeed _myMinSpd;
  protected String _myName;

  public MovementCharsHandler()
  {
    this(type);
  }

  public MovementCharsHandler(String theType)
  {
    super(theType);

    addAttributeHandler(new MWCXMLReader.HandleAttribute("Name")
    {
      public void setValue(String name, final String val)
      {
        _myName = val;
      }
    });

    addHandler(new WorldAccelerationHandler(ACCEL)
    {
      public void setAcceleration(WorldAcceleration res)
      {
        _myAccelRate = res;
      }
    });
    addHandler(new WorldAccelerationHandler(DECEL)
    {
      public void setAcceleration(WorldAcceleration res)
      {
        _myDecelRate = res;
      }
    });
    addHandler(new WorldSpeedHandler(MAX_SPEED)
    {
      public void setSpeed(WorldSpeed res)
      {
        _myMaxSpd = res;
      }
    });
    addHandler(new WorldSpeedHandler(MIN_SPEED)
    {
      public void setSpeed(WorldSpeed res)
      {
        _myMinSpd = res;
      }
    });
    addAttributeHandler(new MWCXMLReader.HandleDoubleAttribute(FUEL)
    {
      public void setValue(String name, final double val)
      {
        _myFuel = val;
      }
    });

  }

  abstract public void elementClosed();

  abstract public void setMovement(ASSET.Models.Movement.MovementCharacteristics chars);


  static public void exportThis(final ASSET.Models.Movement.MovementCharacteristics toExport,
                                final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {

    // create the element
    final org.w3c.dom.Element stat = doc.createElement(type);

    stat.setAttribute("Name", toExport.getName());
    stat.setAttribute(FUEL, writeThis(toExport.getFuelUsageRate()));

    WorldAccelerationHandler.exportAcceleration(ACCEL, toExport.getAccelRate(), stat, doc);
    WorldAccelerationHandler.exportAcceleration(DECEL, toExport.getDecelRate(), stat, doc);
    WorldSpeedHandler.exportSpeed(MAX_SPEED, toExport.getMaxSpeed(), stat, doc);
    WorldSpeedHandler.exportSpeed(MIN_SPEED, toExport.getMinSpeed(), stat, doc);


    // add to parent
    parent.appendChild(stat);

  }

}