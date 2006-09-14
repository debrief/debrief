package ASSET.Util.XML.Movement;

import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAccelerationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

/**
 * Handler for vehicles capable of manouvering in three dimensions
 */
abstract public class SurfaceMovementCharsHandler extends MovementCharsHandler
{
  private final static String type = "SurfaceMovementCharacteristics";

  private final static String TURN_CIRCLE = "TurningCircle";

  protected WorldDistance _myCircle = null;

  public SurfaceMovementCharsHandler()
  {
    this(type);
  }

  public SurfaceMovementCharsHandler(final String theType)
  {
    super(theType);

    addHandler(new WorldDistanceHandler(TURN_CIRCLE)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _myCircle = res;
      }
    });

    // now add the other attribute handlers
  }


  public void elementClosed()
  {
    final ASSET.Models.Movement.SurfaceMovementCharacteristics ss =
      new ASSET.Models.Movement.SurfaceMovementCharacteristics(super._myName,
                                                               super._myAccelRate,
                                                               super._myDecelRate,
                                                               super._myFuel,
                                                               super._myMaxSpd,
                                                               super._myMinSpd,
                                                               _myCircle);
    setMovement(ss);
  }


  //////////////////////////////////////////////////
  // export function
  //////////////////////////////////////////////////
  static public void exportThis(final ASSET.Models.Movement.SurfaceMovementCharacteristics toExport,
                                final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {

    // create the element
    final org.w3c.dom.Element stat = doc.createElement(type);

    stat.setAttribute("Name", toExport.getName());
    stat.setAttribute(FUEL, writeThis(toExport.getFuelUsageRate()));
    //    stat.setAttribute(TURN_CIRCLE, writeThis(toExport.getTurningCircleDiameter(1)));

    WorldSpeedHandler.exportSpeed(MIN_SPEED, toExport.getMinSpeed(), stat, doc);
    WorldSpeedHandler.exportSpeed(MAX_SPEED, toExport.getMaxSpeed(), stat, doc);
    WorldAccelerationHandler.exportAcceleration(ACCEL, toExport.getAccelRate(), stat, doc);
    WorldAccelerationHandler.exportAcceleration(DECEL, toExport.getDecelRate(), stat, doc);
    WorldDistanceHandler.exportDistance(TURN_CIRCLE,
        new WorldDistance(toExport.getTurningCircleDiameter(1), WorldDistance.METRES),
        stat, doc);


    // add to parent
    parent.appendChild(stat);

  }


}
