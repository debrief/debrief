package ASSET.Util.XML.Movement;

import MWC.GenericData.WorldSpeed;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAccelerationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 13-Aug-2003
 * Time: 15:06:57
 * To change this template use Options | File Templates.
 */
abstract public class FixedWingMovementCharsHandler extends ThreeDimMovementCharsHandler
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  private final static String type = "FixedWingMovementCharacteristics";

  private final static String DEFAULT_CLIMB_SPEED = "DefaultClimbSpeed";
  private final static String DEFAULT_DIVE_SPEED = "DefaultDiveSpeed";
  private final static String DEFAULT_TURN_RATE = "DefaultTurnRate";

  double _defaultTurnRate;
  WorldSpeed _defaultClimbSpeed;
  WorldSpeed _defaultDiveSpeed;




  //////////////////////////////////////////////////
  // setup handlers
  //////////////////////////////////////////////////

  public FixedWingMovementCharsHandler()
  {
    super(type);


    addHandler(new WorldSpeedHandler(DEFAULT_CLIMB_SPEED)
    {
      public void setSpeed(WorldSpeed res)
      {
        _defaultClimbSpeed = res;
      }
    });
    addHandler(new WorldSpeedHandler(DEFAULT_DIVE_SPEED)
    {
      public void setSpeed(WorldSpeed res)
      {
        _defaultDiveSpeed = res;
      }
    });

    // now add the other attribute handlers
    addAttributeHandler(new HandleDoubleAttribute(DEFAULT_TURN_RATE)
    {
      public void setValue(String name, final double val)
      {
        _defaultTurnRate = val;
      }
    });
  }


  public void elementClosed()
  {
    // get this instance
    final ASSET.Models.Movement.MovementCharacteristics moves =
      new ASSET.Models.Movement.FixedWingMovementCharacteristics(super._myName,
                                                                 super._myAccelRate,
                                                                 super._myDecelRate,
                                                                 super._myFuel,
                                                                 super._myMaxSpd,
                                                                 super._myMinSpd,
                                                                 super._climbRate,
                                                                 super._diveRate,
                                                                 super._maxHeight,
                                                                 super._minHeight,
                                                                 _defaultTurnRate,
                                                                 _defaultClimbSpeed,
                                                                 _defaultDiveSpeed);

    // pass it to the parent
    setMovement(moves);
  }


  //////////////////////////////////////////////////
  // export function
  //////////////////////////////////////////////////
  static public void exportThis(final ASSET.Models.Movement.FixedWingMovementCharacteristics toExport,
                                final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {

    // create the element
    final org.w3c.dom.Element stat = doc.createElement(type);

    stat.setAttribute("Name", toExport.getName());
    stat.setAttribute(FUEL, writeThis(toExport.getFuelUsageRate()));
    stat.setAttribute(DEFAULT_TURN_RATE, writeThis(toExport.getTurnRate()));

    WorldAccelerationHandler.exportAcceleration(ACCEL, toExport.getAccelRate(), stat, doc);
    WorldAccelerationHandler.exportAcceleration(DECEL, toExport.getDecelRate(), stat, doc);
    WorldSpeedHandler.exportSpeed(MAX_SPEED, toExport.getMaxSpeed(), stat, doc);
    WorldSpeedHandler.exportSpeed(MIN_SPEED, toExport.getMinSpeed(), stat, doc);
    WorldSpeedHandler.exportSpeed(DEFAULT_CLIMB_SPEED, toExport.getDefaultClimbSpeed(), stat, doc);
    WorldSpeedHandler.exportSpeed(DEFAULT_DIVE_SPEED, toExport.getDefaultDiveSpeed(), stat, doc);



    // add to parent
    parent.appendChild(stat);

  }


}
