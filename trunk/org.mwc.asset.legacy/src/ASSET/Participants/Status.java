package ASSET.Participants;

import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldSpeed;

import java.util.TreeSet;

/**
 * Title:        ASSET Simulator
 * Description:  Advanced Scenario Simulator for Evaluation of Tactics
 * Copyright:    Copyright (c) 2001
 * Company:      PlanetMayo Ltd
 * @author Ian Mayo
 * @version 1.0
 */

/**
 * The status of a participant at a particular moment in time
 * <p/>
 * The status object is the result of a {@link ASSET.Models.MovementType movement model},
 * for which the input was the {@link ASSET.Participants.DemandedStatus demanded status}
 */
public class Status implements java.io.Serializable
{

  ////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * current fuel level (in respective units)
   */
  private double _fuelLevel = 100;

  /**
   * vessel speed (m/sec)
   */
  private WorldSpeed _speed;

  /**
   * vessel course (degs)
   */
  private double _course;

  /**
   * current vessel location
   */
  private MWC.GenericData.WorldLocation _location;

  /**
   * time at which this status was modified
   */
  private long _timeUpdated = TimePeriod.INVALID_TIME;

  /**
   * id of participant sending this message
   */
  private int _id;

  /**
   * the formatter for the status string
   */
  private static java.text.DecimalFormat _df = new java.text.DecimalFormat("0");

  /**
   * the set of states we're in
   */
  private States _myStates = null;


  ////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////


  public Status(final int id, final long time)
  {
    _id = id;
    _timeUpdated = time;

   
//   NOTE: we used to correct the time - we don't any more, since zero is a valid time
//    // just double-check the time value
//    if (_timeUpdated == 0)
//    {
//      _timeUpdated = TimePeriod.INVALID_TIME;
//      System.out.println("corrected initial status time");
//    }

  }

  /**
   * copy constructor
   */
  public Status(final Status other)
  {
    this(other._id, other._timeUpdated);
    _course = other._course;
    _fuelLevel = other._fuelLevel;
    _location = other._location;
    _speed = other._speed;
    if (other._myStates != null)
    {
      if (_myStates == null)
      {
        _myStates = new States();
        // check if we really need to add the other states
        if (other._myStates.size() > 0)
          this._myStates.addAll(other._myStates);
      }
    }

  }

  ////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////


  public boolean equals(final Status other)
  {
    boolean res = false;
    if ((other.getLocation().equals(getLocation())) &&
      (other.getCourse() == getCourse()) &&
      (other.getSpeed() == getSpeed()))
      res = true;

    return res;
  }

  public String toString()
  {
    final String res = _location.toString();
    return res;
  }

  /**
   * produce a status string (course, spd, depth)
   */
  public String statusString()
  {
    double thisDepth = Math.abs(this.getLocation().getDepth());
    String res;
    if (thisDepth < 0.001)
    {
      res = _df.format(this.getCourse()) + "° " +
      _df.format(this.getSpeed().getValueIn(WorldSpeed.Kts)) + "kts";
    }
    else
    {
      res = _df.format(this.getCourse()) + "° " +
      _df.format(this.getSpeed().getValueIn(WorldSpeed.Kts)) + "kts " + _df.format(-this.getLocation().getDepth()) + "m";
    }
    return res;
  }

  /**
   * get the id of the participant whose status this is
   */
  public int getId()
  {
    return _id;
  }

  /**
   * set the id of the participant whose status this is
   */
  public void setId(final int val)
  {
    _id = val;
  }

  /**
   * get the speed (m_sec)
   */
  public WorldSpeed getSpeed()
  {
    return _speed;
  }

  /**
   * set the speed (m_sec)
   */
  public void setSpeed(final WorldSpeed speed)
  {
    _speed = speed;
  }

  /**
   * get the course (degs)
   */
  public double getCourse()
  {
    return _course;
  }

  /**
   * set the course (degs)
   */
  public void setCourse(final double degs)
  {
    _course = degs;
  }

  /**
   * set the world location
   */
  public void setLocation(final MWC.GenericData.WorldLocation val)
  {
    _location = val;
  }

  /**
   * get the world location
   */
  public MWC.GenericData.WorldLocation getLocation()
  {
    return _location;
  }

  /**
   * set the time updated (millis)
   */
  public void setTime(final long millis)
  {
    _timeUpdated = millis;
  }

  /**
   * get the time updated (millis)
   */
  public long getTime()
  {
    return _timeUpdated;
  }

  /**
   * get the Fuel Level
   */
  public double getFuelLevel()
  {
    return _fuelLevel;
  }

  /**
   * set the Fuel Level
   */
  public void setFuelLevel(final double val)
  {
    _fuelLevel = val;
  }

  //////////////////////////////////////////////////
  // state management
  //////////////////////////////////////////////////

  /**
   * are we currently in this state?
   *
   * @param state state to check
   * @return yes/no
   */
  public boolean is(String state)
  {
    boolean res = false;
    if (_myStates == null)
      res = false;
    else
      res = _myStates.contains(state);

    return res;
  }

  /**
   * stop the vessel being in this particular state
   *
   * @param state the state we're ending
   */
  public void unset(String state)
  {
    if (_myStates != null)
    {
      _myStates.remove(state);
    }
  }

  /**
   * set the vessel in this particular state
   *
   * @param state the new state we're entering
   */
  public void set(String state)
  {
    // create the states, if we have to
    if (_myStates == null)
    {
      _myStates = new States();
    }

    _myStates.add(state);
  }

  /**
   * the set of vessel states/properties within the status object
   */
  public static class States extends TreeSet<String>
  {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
    // hey, it's just a normal vector at the end of the day....

  }


}
