package ASSET.Util.XML.Movement;

import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.WorldAccelerationHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldDistanceHandler;
import MWC.Utilities.ReaderWriter.XML.Util.WorldSpeedHandler;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 13-Aug-2003
 * Time: 15:06:57
 * To change this template use Options | File Templates.
 */
abstract public class SSMovementCharsHandler extends ThreeDimMovementCharsHandler
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  private final static String type = "SSMovementCharacteristics";

  private final static String TURN_CIRCLE = "TurningCircle";

  WorldDistance _turningCircle;




  //////////////////////////////////////////////////
  // setup handlers
  //////////////////////////////////////////////////

  public SSMovementCharsHandler()
  {
    super(type);


    addHandler(new WorldDistanceHandler(TURN_CIRCLE)
    {
      public void setWorldDistance(WorldDistance res)
      {
        _turningCircle = res;
      }
    });
  }


  public void elementClosed()
  {
    // get this instance
    final ASSET.Models.Movement.MovementCharacteristics moves =
      new ASSET.Models.Movement.SSMovementCharacteristics(super._myName,
                                                          super._myAccelRate,
                                                          super._myDecelRate,
                                                          super._myFuel,
                                                          super._myMaxSpd,
                                                          super._myMinSpd,
                                                          _turningCircle,
                                                          super._climbRate,
                                                          super._diveRate,
                                                          super._maxHeight,
                                                          super._minHeight);

    // pass it to the parent
    setMovement(moves);
    
    // forget the data
    _turningCircle = null;
  }


  //////////////////////////////////////////////////
  // export function
  //////////////////////////////////////////////////
  static public void exportThis(final ASSET.Models.Movement.SSMovementCharacteristics toExport,
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

    WorldDistance theD = new WorldDistance(toExport.getTurningCircleDiameter(1), WorldDistance.METRES);
    WorldDistanceHandler.exportDistance(TURN_CIRCLE,theD , stat, doc);
    
    // add to parent
    parent.appendChild(stat);

  }


}
