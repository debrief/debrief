package ASSET.Models.Movement;

import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 13-Aug-2003
 * Time: 15:01:47
 * To change this template use Options | File Templates.
 */
public class SSMovementCharacteristics extends ThreeDimMovementCharacteristics
{
  //////////////////////////////////////////////////
  // member objects
  //////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
   * the turning circle for this vessel
   */
  private WorldDistance _turningCircleDiameter;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////


  /**
   * old-constructor, mainly to support testing
   *
   * @deprecated
   */
  public SSMovementCharacteristics(String myName, double accelRate,
                                   double decelRate, double fuel_usage_rate,
                                   double maxSpeed, double minSpeed,
                                   double turningCircleDiam, double defaultClimbRate,
                                   double defaultDiveRate, double maxDepth,
                                   double minDepth)
  {
    this(myName, new WorldAcceleration(accelRate, WorldAcceleration.M_sec_sec),
         new WorldAcceleration(decelRate, WorldAcceleration.M_sec_sec),
         fuel_usage_rate, new WorldSpeed(maxSpeed, WorldSpeed.M_sec),
         new WorldSpeed(minSpeed, WorldSpeed.M_sec), new WorldDistance(turningCircleDiam, WorldDistance.METRES),
         new WorldSpeed(defaultClimbRate, WorldSpeed.M_sec), new WorldSpeed(defaultDiveRate, WorldSpeed.M_sec),
         new WorldDistance(maxDepth, WorldDistance.METRES), new WorldDistance(minDepth, WorldDistance.METRES));
  }

  public SSMovementCharacteristics(String myName, WorldAcceleration accelRate,
                                   WorldAcceleration decelRate, double fuel_usage_rate,
                                   WorldSpeed maxSpeed, WorldSpeed minSpeed,
                                   WorldDistance turningCircleDiam, WorldSpeed defaultClimbRate,
                                   WorldSpeed defaultDiveRate, WorldDistance maxDepth,
                                   WorldDistance minDepth)
  {
    super(myName, accelRate, decelRate, fuel_usage_rate,
          maxSpeed, minSpeed, defaultClimbRate,
          defaultDiveRate, maxDepth, minDepth);
    _turningCircleDiameter = turningCircleDiam;
  }

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /**
   * get the turning circle diameter (m) at this speed (in m/sec)
   */
  public double getTurningCircleDiameter(double m_sec)
  {
    return _turningCircleDiameter.getValueIn(WorldDistance.METRES);
  }


  /**
   * get a sample set of characteristics, largely for testing
   *
   * @return
   */
  public static SSMovementCharacteristics getSampleSSChars()
  {
    return new SSMovementCharacteristics("test", new WorldAcceleration(12, WorldAcceleration.M_sec_sec),
                                         new WorldAcceleration(3, WorldAcceleration.M_sec_sec),
                                         12, new WorldSpeed(12, WorldSpeed.M_sec),
                                         new WorldSpeed(2, WorldSpeed.M_sec), new WorldDistance(12, WorldDistance.METRES), new WorldSpeed(12, WorldSpeed.M_sec),
                                         new WorldSpeed(12, WorldSpeed.M_sec), new WorldDistance(12, WorldDistance.METRES), new WorldDistance(12, WorldDistance.METRES));
  }

}
