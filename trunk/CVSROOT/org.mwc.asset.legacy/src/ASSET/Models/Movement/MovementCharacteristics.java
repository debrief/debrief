package ASSET.Models.Movement;

import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * Title:        ASSET Simulator
 * Description:  Advanced Scenario Simulator for Evaluation of Tactics
 * Copyright:    Copyright (c) 2001
 * Company:      PlanetMayo Ltd
 * @author Ian Mayo
 * @version 1.0
 */

/**
 * a set of characteristics describing the movement of a particular type of platform
 */

public abstract class MovementCharacteristics implements MWC.GUI.Editable, java.io.Serializable
{

  //////////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////////

  /**
   * maximum speed (m/sec)
   */
  private WorldSpeed _maxSpeed = null;

  /**
   * minimum speed (m/sec)
   */
  private WorldSpeed _minSpeed = null;

  /**
   * acceleration rate (m/sec/sec)
   */
  private WorldAcceleration _accelRate = null;

  /**
   * deceleration rate (m/sec/sec)
   */
  private WorldAcceleration _decelRate = null;

  /**
   * name of this characteristic type
   */
  private String _myName = "unset";

  /**
   * how much fuel we use (/kt/sec)
   */
  private double _fuel_usage_rate = 0.00;

  /**
   * our editor
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * distance representing the sea surface = for skimmers
   */
  private static final WorldDistance SURFACE_HEIGHT = new WorldDistance(0, WorldDistance.METRES);

  /**
   * speed representing depth change rate
   */
  private static final WorldSpeed SURFACE_CHANGE = new WorldSpeed(0, WorldSpeed.M_sec);

  //////////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////////

  public MovementCharacteristics(final String name)
  {
    _myName = name;
  }

  public MovementCharacteristics(String myName,
                                 WorldAcceleration accelRate, WorldAcceleration decelRate,
                                 double fuel_usage_rate,
                                 WorldSpeed maxSpeed, WorldSpeed minSpeed)
  {
    this._accelRate = accelRate;
    this._decelRate = decelRate;
    this._fuel_usage_rate = fuel_usage_rate;
    this._maxSpeed = maxSpeed;
    this._minSpeed = minSpeed;
    this._myName = myName;
  }

  public MovementCharacteristics()
  {
    this("default");
  }

  //////////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////////

  public String toString()
  {
    return getName();
  }

  public String getName()
  {
    return _myName;
  }

  public void setName(final String val)
  {
    _myName = val;
  }

  /**
   * get the turning circle diameter (m) at this speed (in m/sec)
   */
  abstract public double getTurningCircleDiameter(double m_sec);

  /**
   * return the rate of acceleration (m/sec/sec)
   */
  public WorldAcceleration getAccelRate()
  {
    return _accelRate;
  }

  /**
   * return the rate of deceleration (m/sec/sec)
   */
  public WorldAcceleration getDecelRate()
  {
    return _decelRate;
  }

  /**
   * set the maximum speed for this participant (m/sec)
   *
   * @param val max speed (m/sec)
   */
  public void setMaxSpeed(final WorldSpeed val)
  {
    _maxSpeed = val;
  }

  /**
   * get the maximum speed for this participant (m/sec)
   *
   * @return max speed (m/sec)
   */
  public WorldSpeed getMaxSpeed()
  {
    return _maxSpeed;
  }

  /**
   * set the maximum speed for this participant (m/sec)
   *
   * @param val max speed (m/sec)
   */
  public void setMinSpeed(final WorldSpeed val)
  {
    _minSpeed = val;
  }

  /**
   * get the maximum speed for this participant (m/sec)
   *
   * @return max speed (m/sec)
   */
  public WorldSpeed getMinSpeed()
  {
    return _minSpeed;
  }


  /**
   * set the acceleration rate of this vehicle (in m/sec/second)
   */

  public void setAccelRate(final WorldAcceleration m_sec_sec)
  {
    _accelRate = m_sec_sec;
  }

  /**
   * set the deceleration rate of this vehicle (in m_sec/second)
   */
  public void setDecelRate(final WorldAcceleration m_sec_sec)
  {
    _decelRate = m_sec_sec;
  }

  /**
   * set the rate at which the participant uses fuel (per knot per second)
   */
  public void setFuelUsageRate(final double val)
  {
    _fuel_usage_rate = val;
  }

  /**
   * get the rate at which the participant uses fuel (per knot per second)
   */
  public double getFuelUsageRate()
  {
    return _fuel_usage_rate;
  }

  //////////////////////////////////////////////////
  // Height related accessors
  //////////////////////////////////////////////////
  /**
   * get the min Height at which this platform operates
   *
   * @return min Height(m)
   */
  public WorldDistance getMinHeight()
  {
    return SURFACE_HEIGHT;
  }

  /**
   * get the min Height at which this platform operates
   *
   * @return max Height(m)
   */
  public WorldDistance getMaxHeight()
  {
    return SURFACE_HEIGHT;
  }

  /**
   * get the climb rate
   *
   * @return climb rate (m/sec/sec)
   */
  public WorldSpeed getClimbRate()
  {
    return SURFACE_CHANGE;
  }

  /**
   * get the dive rate
   *
   * @return dive rate (m/sec/sec)
   */
  public WorldSpeed getDiveRate()
  {
    return SURFACE_CHANGE;
  }

  /**
   * get the min Height at which this platform operates
   *
   * @param val min Height(m)
   */
  public void setMinHeight(WorldDistance val)
  {
  }

  /**
   * get the min Height at which this platform operates
   *
   * @param val max Height(m)
   */
  public void setMaxHeight(WorldDistance val)
  {
  }

  /**
   * get the climb rate
   *
   * @param val climb rate (m/sec/sec)
   */
  public void setClimbRate(double val)
  {
  }

  /**
   * get the dive rate
   *
   * @param val dive rate (m/sec/sec)
   */
  public void setDiveRate(double val)
  {
  }


  //////////////////////////////////////////////////////////////////////
  // editable data
  //////////////////////////////////////////////////////////////////////
  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
  public boolean hasEditor()
  {
    return true;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public MWC.GUI.Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new MovementInfo(this);

    return _myEditor;
  }

  /**
   * calculate how long it takes to move to make the specified course change
   *
   * @param mean_speed    the mean speed through the turn (m/sec)
   * @param course_change the change in course required (degs)
   * @return the time taken (seconds)
   */
  public double calculateTurnTime(final double mean_speed, double course_change)
  {
    double turn_time;
    double course_change_rads = MWC.Algorithms.Conversions.Degs2Rads(course_change);

    double turn_rate_rads = MWC.Algorithms.Conversions.Degs2Rads(calculateTurnRate(mean_speed));

    // calculate the turn time
    if (turn_rate_rads == 0)
      turn_time = 0;
    else
      turn_time = Math.abs(course_change_rads) / turn_rate_rads;
    return turn_time;
  }

  /**
   * calculate the turn rate at this speed/turning circle
   *
   * @param curSpeed_m_sec
   * @return turn rate (degs/sec)
   */
  public double calculateTurnRate(double curSpeed_m_sec)
  {
    double turnRadius = getTurningCircleDiameter(curSpeed_m_sec) / 2;
    double res = MWC.Algorithms.Conversions.Rads2Degs(curSpeed_m_sec / turnRadius);
    return res;
  }

  static public class MovementInfo extends MWC.GUI.Editable.EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public MovementInfo(final MovementCharacteristics data)
    {
      super(data, data.getName(), "Movement", "images/icons/MoveChars.gif");
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public java.beans.PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final java.beans.PropertyDescriptor[] res = {
          prop("AccelRate", "the rate of acceleration for this vessel (kts/sec)"),
          prop("FuelUsageRate", "the rate of fuel usage for this vessel (%/kt/sec)"),
          prop("MaxHeight", "the maximum Height which this vessel travels to (m)"),
          prop("MaxSpeed", "the rate of acceleration for this vessel (kts/sec)"),
          prop("Name", "the name of this vessel"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }


  public static MovementCharacteristics getSampleChars()
  {
    return new MovementCharacteristics("the moves", 
                                       new WorldAcceleration(1, WorldAcceleration.Kts_sec),
                                       new WorldAcceleration(1, WorldAcceleration.Kts_sec),
                                       0.0000001,
                                       new WorldSpeed(30, WorldSpeed.Kts),
                                       new WorldSpeed(1, WorldSpeed.Kts)
                                       )
    {
      /**
       * get the turning circle diameter (m) at this speed (in m/sec)
       */
      public double getTurningCircleDiameter(double m_sec)
      {
        return 12;
      }
    };
  }

  //////////////////////////////////////////////////
  // testing
  //////////////////////////////////////////////////
  public static class MoveCharsTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return MovementCharacteristics.getSampleChars();
    }
  }

}