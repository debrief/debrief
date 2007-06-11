package ASSET.Models.Decision.Movement;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * behaviour to travel at an indicated speed and course for an indicated distance.
 * An optional depth value is available
 */
public class Move extends CoreDecision implements java.io.Serializable
{

  //////////////////////////////////////////////////////////////////////
  // Member Variables
  //////////////////////////////////////////////////////////////////////

  /**
   * the depth to use as INVALID_DEPTH
   */
  public final static double INVALID_DEPTH = -999;

  /**
   * our transit speed for this passage
   */
  private WorldSpeed _mySpeed = null;

  /**
   * the (optional) depth to travel at
   */
  private WorldDistance _myHeight = null;

  /**
   * the course to travel on (degs)
   */
  private Double _myCourse = null;

  /**
   * the distance to travel
   */
  private WorldDistance _theDistance;

  /**
   * the point we're heading for
   */
  private WorldLocation _theDestination = null;

  /**
   * the threshold to use to decide if we are at our destination (yds)
   */
  private double _threshold = 500;

  /**
   * a local copy of our editable object
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * whether this represents a single transit, and is now complete
   */
  private boolean _transit_complete = false;

  //////////////////////////////////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////////////////////////////////

  public Move()
  {
    super("Move");
  }


  //////////////////////////////////////////////////////////////////////
  // Member methods
  //////////////////////////////////////////////////////////////////////

  public DemandedStatus decide(final Status status,
                               ASSET.Models.Movement.MovementCharacteristics chars,
                               DemandedStatus demStatus,
                               ASSET.Models.Detection.DetectionList detections,
                               ASSET.Scenario.ScenarioActivityMonitor monitor,
                               final long time)
  {
    // create the output object
    SimpleDemandedStatus res = null;

    String thisActivity = null;

    // have we completed this manoeuvre?
    if (_transit_complete)
    {
      res = null;
      return res;
    }

    // find out where we are
    WorldLocation currentLoc = status.getLocation();

    // are we running towards a destination?
    if (_theDistance == null)
    {
      // ok, we're not heading towards a particular point, just put us onto the correct course and speed
      double curSpeed = status.getSpeed().getValueIn(WorldSpeed.M_sec);
      double curCourse = status.getCourse();
      double curHeight = -status.getLocation().getDepth();


      thisActivity = "";

      // are we already working to a simple demanded course/speed/depth?
      if (demStatus instanceof SimpleDemandedStatus)
      {
        res = new SimpleDemandedStatus(time, (SimpleDemandedStatus) demStatus);
      }
      else
      {
        res = new SimpleDemandedStatus(time, status);
      }


      if (_mySpeed != null)
      {
        if (curSpeed != _mySpeed.getValueIn(WorldSpeed.M_sec))
        {
          thisActivity += " speed to:" + (int) _mySpeed.getValueIn(WorldSpeed.Kts);
          res.setSpeed(_mySpeed.getValueIn(WorldSpeed.M_sec));
        }
      }

      if (_myCourse != null)
      {
        if (curCourse != _myCourse.doubleValue())
        {
          thisActivity += " course to:" + _myCourse.doubleValue();
          res.setCourse(_myCourse.doubleValue());
        }
      }

      if (_myHeight != null)
      {
        if (curHeight != _myHeight.getValueIn(WorldDistance.METRES))
        {
          thisActivity += " height to:" + _myHeight.getValueIn(WorldDistance.METRES);
          res.setHeight(_myHeight.getValueIn(WorldDistance.METRES));
        }
      }

      // did we update any?
      if (thisActivity == "")
      {
        // no - we must be ok,
        res = null;
      }
      else
      {
        // cool, the res is set already
      }

    }
    else
    {

      // do we have our destination?
      if (_theDestination == null)
      {
        // no, this is the first time we've been called.  Calculate where we're going to
        WorldVector vector = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(this._myCourse.doubleValue()),
                                             _theDistance.getValueIn(WorldDistance.DEGS), 0);

        _theDestination = new WorldLocation(currentLoc);
        _theDestination.addToMe(vector);
      }
      else
      {
        // ok, we're up and running, have we reached our destination

        // how far to the target
        double rngDegs = currentLoc.subtract(_theDestination).getRange();

        // and in yards
        double rngYds = MWC.Algorithms.Conversions.Degs2Yds(rngDegs);

        if (rngYds < _threshold)
        {
          // right, we've got there. Mark complete
          _transit_complete = true;

          super.setLastActivity(thisActivity);

          // and drop out
          return res;
        }

      }

      // ok, now steer to the destination
      double brg_rads = _theDestination.subtract(currentLoc).getBearing();
      final double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(brg_rads);

      // and set the course in degs
      res = new SimpleDemandedStatus(time, status);
      res.setCourse(brgDegs);

      // do we have depth?
      if (_myHeight != null)
        res.setHeight(_myHeight.getValueIn(WorldDistance.METRES));

      // and the speed
      if (_mySpeed != null)
        res.setSpeed(_mySpeed.getValueIn(WorldSpeed.M_sec));

      thisActivity = "Heading for target location";

    }

    super.setLastActivity(thisActivity);

    return res;

  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    _transit_complete = false;
  }


  /**
   * indicate to this model that its execution has been interrupted by another (prob higher priority) model
   *
   * @param currentStatus
   */
  public void interrupted(Status currentStatus)
  {
    // ignore.
  }


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
      _myEditor = new MoveInfo(this);

    return _myEditor;
  }

  /**
   * the speed we transit at (m/sec)
   */
  public void setSpeed(final WorldSpeed newSpeed)
  {
    _mySpeed = newSpeed;
  }

  /**
   * the speed we transit at (m/sec)
   */
  public WorldSpeed getSpeed()
  {
    return _mySpeed;
  }

  /**
   * the threshold we use to decide if we are at our location (yds)
   */
  public double getThreshold()
  {
    return _threshold;
  }

  /**
   * the threshold we use to decide if we are at our location (yds)
   */
  public void setThreshold(final double val)
  {
    _threshold = val;
  }

  /**
   * set the course to travel (degs)
   *
   * @return the course (degs)
   */
  public Double getCourse()
  {
    return _myCourse;
  }

  /**
   * get the depth to travel at
   *
   * @return
   */
  public WorldDistance getHeight()
  {
    return _myHeight;
  }

  /**
   * set the depth to travel at (though -999 is treated as null value)
   *
   * @param theHeight
   */
  public void setHeight(WorldDistance theHeight)
  {
    // just see if it's an invalid depth
    if (theHeight.getValueIn(WorldDistance.METRES) == INVALID_DEPTH)
    {
      _myHeight = null;
    }
    else
      _myHeight = theHeight;
  }

  /**
   * set the course to travel (degs)
   *
   * @param theCourse the course (degs)
   */
  public void setCourse(Double theCourse)
  {
    this._myCourse = theCourse;
  }

  /**
   * get the distance to travel
   *
   * @return the Distance to travel
   */
  public WorldDistance getDistance()
  {
    return _theDistance;
  }

  /**
   * set the distance to travel
   *
   * @param theDistance
   */
  public void setDistance(WorldDistance theDistance)
  {
    this._theDistance = theDistance;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Move.java,v $
   * Revision 1.1  2006/08/08 14:21:27  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:35  Ian.Mayo
   * First versions
   *
   * Revision 1.16  2004/09/02 13:17:25  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   *
   * Revision 1.15  2004/08/31 09:36:03  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.14  2004/08/26 16:26:50  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.13  2004/08/25 11:20:14  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.12  2004/08/20 13:32:15  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.11  2004/08/17 14:21:55  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.10  2004/08/06 12:51:53  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.9  2004/08/06 11:14:15  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.8  2004/05/24 15:46:34  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   * <p/>
   * Revision 1.7  2003/11/06 12:17:59  Ian.Mayo
   * Introduce convenience variable to aid debugging
   * <p/>
   * Revision 1.6  2003/11/05 09:20:09  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  static private class MoveInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public MoveInfo(final Move data)
    {
      super(data, data.getName(), "Move");
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
          prop("Speed", "the speed we transit at (kts)"),
          prop("Course", "the course to travel at (degs)"),
          prop("Distance", "the distance we travel"),
          prop("Height", "the height to travel at (or -999 to ignore depth)"),
          prop("Name", "the name of this transit model"),
        };

        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }


  /**
   * **********************************************************************
   * test this class
   * **********************************************************************
   */

  public static class MoveTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public MoveTest(final String name)
    {
      super(name);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      MWC.GUI.Editable ed = new Move();
      return ed;
    }

    public void testTheMove()
    {
      // ok, create some locations
      WorldLocation locA = new WorldLocation(0, 0, 12);
      WorldLocation locC = new WorldLocation(0, 3, 12);

      Move move = new Move();
      move.setCourse(new Double(60));
      move.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
      move.setDistance(new WorldDistance(3, WorldDistance.DEGS));

      // check the initial values are correct
      assertEquals("the course is set", move.getCourse().doubleValue(), 60, 0.01);
      assertEquals("the speed is set", move.getSpeed().getValueIn(WorldSpeed.M_sec), 12, 0.01);
      assertEquals("the distance is set", move.getDistance().getValueIn(WorldDistance.DEGS), 3, 0.01);

      // change course to head east (to ease our testing)
      move.setCourse(new Double(90));

      Status theStat = new Status(12, 12000);
      theStat.setLocation(locA);
      theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      DemandedStatus res = move.decide(theStat, null, null, null, null, 1000);

      // did it work?
      assertEquals("course set correctly", ((SimpleDemandedStatus) res).getCourse(), 90, 0.01);
      assertEquals("speed set correctly", ((SimpleDemandedStatus) res).getSpeed(), 12, 0.01);
      assertEquals("depth not set", ((SimpleDemandedStatus) res).getHeight(), -12, 0.01);

      assertEquals("target location set correctly", move._theDestination.subtract(locC).getRange(), 0, 0.001);

      // fiddle with the depth
      move.setHeight(new WorldDistance(-55, WorldDistance.METRES));

      res = move.decide(theStat, null, null, null, null, 1000);

      // did it work?
      assertEquals("course set correctly", ((SimpleDemandedStatus) res).getCourse(), 90, 0.01);
      assertEquals("speed set correctly", ((SimpleDemandedStatus) res).getSpeed(), 12, 0.01);
      assertEquals("depth set", ((SimpleDemandedStatus) res).getHeight(), -55, 0.01);

    }
  }

}