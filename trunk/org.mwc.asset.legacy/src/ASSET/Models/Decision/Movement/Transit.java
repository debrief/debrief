package ASSET.Models.Decision.Movement;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian Mayo
 * Date: 2003
 * Log:
 *  $Log: Transit.java,v $
 *  Revision 1.1  2006/08/08 14:21:28  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:37  Ian.Mayo
 *  First versions
 *
 *  Revision 1.22  2004/10/29 09:23:07  Ian.Mayo
 *  Correct tests
 *
 *  Revision 1.21  2004/10/28 14:52:55  ian
 *  Insert couple of more comments
 *
 *  Revision 1.20  2004/09/02 13:17:27  Ian.Mayo
 *  Reflect CoreDecision handling the toString method
 *
 *  Revision 1.19  2004/08/31 09:36:06  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.18  2004/08/26 16:26:52  Ian.Mayo
 *  Implement editable properties
 *
 *  Revision 1.17  2004/08/25 11:20:19  Ian.Mayo
 *  Remove main methods which just run junit tests
 *
 *  Revision 1.16  2004/08/20 15:08:18  Ian.Mayo
 *  Part way through changing detection cycle so that it doesn't start afresh each time - each sensor removes it's previous calls the next time it is called (to allow for TBDO)
 *
 *  Revision 1.15  2004/08/20 13:32:19  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.14  2004/08/17 14:21:58  Ian.Mayo
 *  Refactor to introduce parent class capable of storing name & isActive flag
 *
 *  Revision 1.13  2004/08/06 12:51:55  Ian.Mayo
 *  Include current status when firing interruption
 *
 *  Revision 1.12  2004/08/06 11:14:17  Ian.Mayo
 *  Introduce interruptable behaviours, and recalc waypoint route after interruption
 *
 *  Revision 1.11  2004/05/24 15:46:37  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:51  ian
 *  no message
 *
 *  Revision 1.10  2003/11/05 09:20:10  Ian.Mayo
 *  Include MWC Model support
 *
 *  Revision 1.9  2003/09/19 13:37:59  Ian.Mayo
 *  Switch to Speed and Distance objects instead of just doubles
 *
 *  Revision 1.8  2003/09/18 14:11:53  Ian.Mayo
 *  Make tests work with new World Speed class
 *
 *  Revision 1.7  2003/09/18 12:12:38  Ian.Mayo
 *  Reflect introduction of World Speed
 *
 *  Revision 1.6  2003/09/11 10:15:04  Ian.Mayo
 *  Change finish time following updated movement cycle
 *
 *  Revision 1.5  2003/09/09 15:55:29  Ian.Mayo
 *  Change signature of decision model
 *
 *  Revision 1.4  2003/09/08 13:08:09  Ian.Mayo
 *  provide is finished accessor
 *
 *  Revision 1.3  2003/09/04 14:41:02  Ian.Mayo
 *  Implement modelling guide tests
 *
 *
 */

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Vessels.SSN;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.*;

/**
 * behaviour to travel through a series of points at an indicated speed. Optionally the journey
 * may be repeated, in forward or reverse direction.
 */
public class Transit extends CoreDecision implements java.io.Serializable
{

  //////////////////////////////////////////////////////////////////////
  // Member Variables
  //////////////////////////////////////////////////////////////////////

  /**
   * our transit speed for this passage
   */
  private WorldSpeed _mySpeed;

  /**
   * our destination
   */
  private WorldPath _myDestinations;

  /**
   * the current destination
   */
  private int _currentDestination = 0;

  /**
   * the threshold to use to decide if we are at our destination (yds)
   */
  private WorldDistance _threshold = new WorldDistance(500, WorldDistance.YARDS);

  /**
   * loop through destinations
   */
  private boolean _loop = true;

  /**
   * when we are looping, whether we go back to start, or
   * pass through them in reverse;
   */
  private boolean _inReverse = true;

  /**
   * whether we are currently performing the "reverse-loop", heading back
   * to the start
   */
  private boolean _in_reverse_transit = false;

  /**
   * a local copy of our editable object
   */
  private MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * whether this represents a single transit, and is now complete
   */
  protected boolean _transit_complete = false;

  //////////////////////////////////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////////////////////////////////


  public Transit(final WorldPath destinations, final WorldSpeed transit_speed, final boolean loop)
  {
    super("Transit");
    _myDestinations = destinations;
    _mySpeed = transit_speed;
    _loop = loop;
  }

  public Transit()
  {
    this(null, null, false);
  }


  //////////////////////////////////////////////////////////////////////
  // Member methods
  //////////////////////////////////////////////////////////////////////

  public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
                                                  ASSET.Models.Movement.MovementCharacteristics chars, DemandedStatus demStatus, ASSET.Models.Detection.DetectionList detections,
                                                  ASSET.Scenario.ScenarioActivityMonitor monitor,
                                                  final long time)
  {
    // create the output object
    DemandedStatus res;

    // have we completed this manoeuvre?
    if (_transit_complete)
    {
      res = null;
      return res;
    }

    // ok, determine if we are ready to switch to the next point
    boolean passedNewPoint = determineProgress(status);

    // ok, determine the demanded status to get to the next point
    res = getDemandedStatus(status, chars, demStatus, detections, monitor, time, passedNewPoint);


    // done.
    return res;

  }

  /**
   * ok, have a look at where we are, moving us on to the next
   * destination if necessary
   *
   * @param status current participant location/speed
   * @return the next target
   */
  protected boolean determineProgress(final Status status)
  {
    boolean passedNewPoint = false;

    // just check that we aren't heading for the last point, after the last point
    // has been removed
    _currentDestination = Math.min(_currentDestination, _myDestinations.size() - 1);

    // where are we heading for?
    WorldLocation _myDestination = _myDestinations.getLocationAt(_currentDestination);

    // how far away from it are we?
    final WorldVector offset = _myDestination.subtract(status.getLocation());

    // find the distance in yards
    double dist_yds = MWC.Algorithms.Conversions.Degs2Yds(offset.getRange());

    // are we at our destination?
    if (dist_yds < _threshold.getValueIn(WorldDistance.YARDS))
    {
      // cool, that's that point reached
      passedNewPoint = true;

      // are we heading forwards, or reverse?
      if (_in_reverse_transit)
      {
        // we are currently performing reverse transit.  get the next ppint

        // are we back at the start?
        if (_currentDestination == 0)
        {
          // yes, switch back to working forwards through the data
          _in_reverse_transit = false;

          // head for the first point (or ourselves, if there's only one item in the list)
          _currentDestination = Math.min(1, _myDestinations.size() - 1);
        }
        else
        {
          // work back to the previous point
          _currentDestination--;
        }
      }
      else
      {
        // we are working forwards, carry on!

        // have we any destinations left?
        if (_currentDestination < _myDestinations.size() - 1)
        {
          // ok, now go to the next destination
          _currentDestination++;
        }
        else
        {
          // no, we're at the last destination.
          // are we looping?
          if (_loop)
          {
            // yes, are we going forwards or back?
            if (_inReverse)
            {
              // ok, start going backwards
              _in_reverse_transit = true;

              // head for the previous destination in the list (we know we are already on the last one)
              _currentDestination--;

              // just check that we aren't in a path containing only one point
              // : ensure it's zero
              _currentDestination = Math.max(_currentDestination, 0);
            }
            else
            {
              // head back for the start
              _currentDestination = 0;
            }
          }
          else
          {
            // no, we are not looping
            _transit_complete = true;
            _currentDestination = -1;
          } // whether we are looping
        } // whether we have reached the last destination in the list
      } // are we heading forwards or backwards?

    } // have we passed our next destination
    return passedNewPoint;
  }

  /**
   * ok, we now know where we are, and where we're heading for. produce a demanded status
   *
   * @param status         where we are
   * @param chars          our movement characteristics
   * @param demStatus      the current demanded status
   * @param detections     the current set of detections
   * @param monitor        the scenario helper
   * @param time           the current time
   * @param passedNewPoint flag to indicate if we've just passed a new waypoint
   * @return demanded course to the next location
   */
  protected DemandedStatus getDemandedStatus(final ASSET.Participants.Status status,
                                             ASSET.Models.Movement.MovementCharacteristics chars, DemandedStatus demStatus, ASSET.Models.Detection.DetectionList detections,
                                             ASSET.Scenario.ScenarioActivityMonitor monitor,
                                             final long time, boolean passedNewPoint)
  {

    DemandedStatus res = null;
    WorldLocation currentDest = null;

    // produce vector to this new location
    // have we finished?
    if (_transit_complete)
    {
      // hey, don't bother, we're finished
      super.setLastActivity("Transit complete");
    }
    else
    {
      // initialise the demanded status
      SimpleDemandedStatus theRes = new SimpleDemandedStatus(time, status);

      // update the destination, just in case we have just passed one of our points
      currentDest = _myDestinations.getLocationAt(_currentDestination);

      // produce a vector to the destination
      final WorldVector vec = currentDest.subtract(status.getLocation());

      // set the bearing
      final double brg = MWC.Algorithms.Conversions.Rads2Degs(vec.getBearing());
      theRes.setCourse(brg);

      // set the correct depth for this leg
      theRes.setHeight(-currentDest.getDepth());

      // maintain speed
      if (_mySpeed != null)
        theRes.setSpeed(_mySpeed.getValueIn(WorldSpeed.M_sec));

      // and store the new demanded status
      res = theRes;

      super.setLastActivity("Transit heading for waypoint:" + (_currentDestination + 1));
    }


    return res;
  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    _currentDestination = 0;
    _in_reverse_transit = false;
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
      _myEditor = new TransitInfo(this);

    return _myEditor;
  }

  /**
   * whether we loop through the destinations, switching
   * to the first from the last
   */
  public void setLoop(final boolean newLoop)
  {
    _loop = newLoop;
  }

  /**
   * whether we loop through the destinations, switching
   * to the first from the last
   */
  public boolean getLoop()
  {
    return _loop;
  }

  /**
   * when we are looping, whether we go back to the start, or if we
   * pass through the destinations in reverse
   */
  public void setReverse(final boolean val)
  {
    _inReverse = val;
  }


  /**
   * when we are looping, whether we go back to the start, or if we
   * pass through the destinations in reverse
   */
  public boolean getReverse()
  {
    return _inReverse;
  }


  /**
   * indicate if we have reached the end of the route
   *
   * @return yes/no
   */
  public boolean isFinished()
  {
    return _transit_complete;
  }

  /**
   * the speed we transit at (kts)
   */
  public void setSpeed(final WorldSpeed newSpeed)
  {
    _mySpeed = newSpeed;
  }

  /**
   * the speed we transit at (kts)
   */
  public WorldSpeed getSpeed()
  {
    return _mySpeed;
  }

  /**
   * the threshold we use to decide if we are at our location (yds)
   */
  public WorldDistance getThreshold()
  {
    return _threshold;
  }

  /**
   * the threshold we use to decide if we are at our location (yds)
   */
  public void setThreshold(final WorldDistance val)
  {
    _threshold = val;
  }


  /**
   * the set of destinations we follow
   */
  public void setDestinations(final MWC.GenericData.WorldPath newDestinations)
  {
    _myDestinations = newDestinations;
  }

  /**
   * the set of destinations we follow
   */
  public MWC.GenericData.WorldPath getDestinations()
  {
    return _myDestinations;
  }

  /**
   * get the current destination
   */
  public WorldLocation getCurrentDestination()
  {
    return _myDestinations.getLocationAt(_currentDestination);
  }

  /**
   * get the inded of the current destination
   */
  public int getCurrentDestinationIndex()
  {
    return _currentDestination;
  }
  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Transit.java,v $
   * Revision 1.1  2006/08/08 14:21:28  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:37  Ian.Mayo
   * First versions
   *
   * Revision 1.22  2004/10/29 09:23:07  Ian.Mayo
   * Correct tests
   *
   * Revision 1.21  2004/10/28 14:52:55  ian
   * Insert couple of more comments
   *
   * Revision 1.20  2004/09/02 13:17:27  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   * <p/>
   * Revision 1.19  2004/08/31 09:36:06  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.18  2004/08/26 16:26:52  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.17  2004/08/25 11:20:19  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.16  2004/08/20 15:08:18  Ian.Mayo
   * Part way through changing detection cycle so that it doesn't start afresh each time - each sensor removes it's previous calls the next time it is called (to allow for TBDO)
   * <p/>
   * Revision 1.15  2004/08/20 13:32:19  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.14  2004/08/17 14:21:58  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.13  2004/08/06 12:51:55  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.12  2004/08/06 11:14:17  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.11  2004/05/24 15:46:37  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   * <p/>
   * Revision 1.10  2003/11/05 09:20:10  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  static public class TransitInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public TransitInfo(final Transit data)
    {
      super(data, data.getName(), "Transit");
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
          prop("Destinations", "the list of destinations we follow"),
          prop("Loop", "whether we loop back to the first point"),
          prop("Reverse", "when we loop, whether we follow the points in reverse order"),
          prop("Speed", "the speed we transit at (kts)"),
          prop("Name", "the name of this transit model"),
        };
        res[0].setPropertyEditorClass(MWC.GUI.Properties.Swing.SwingWorldPathPropertyEditor.class);

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

  public static class TransitTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public TransitTest(final String name)
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
      MWC.GUI.Editable ed = new Transit();
      return ed;
    }

    public void testLooping()
    {

      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());
      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(1, 1, 0);

      // create the paths
      final MWC.GenericData.WorldPath path = new MWC.GenericData.WorldPath();

      final MWC.GenericData.WorldLocation loc2 = origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
        MWC.Algorithms.Conversions.Yds2Degs(2000), 0));
      path.addPoint(loc2);

      final MWC.GenericData.WorldLocation loc3 = origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(180),
        MWC.Algorithms.Conversions.Yds2Degs(2000), 0));

      path.addPoint(loc3);

      final MWC.GenericData.WorldLocation loc4 = origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270),
        MWC.Algorithms.Conversions.Yds2Degs(2000), 0));

      path.addPoint(loc4);

      final MWC.GenericData.WorldLocation loc5 = origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(0),
        MWC.Algorithms.Conversions.Yds2Degs(2000), 0));


      path.addPoint(loc5);

      // create the objects
      final Transit transit = new Transit(path, new WorldSpeed(12, WorldSpeed.M_sec), true);

      // configure the transit
      transit.setLoop(true);
      transit.setReverse(false);

      // get going
      final ASSET.Participants.Status stat = new ASSET.Participants.Status(1, 1000);
      stat.setLocation(origin);
      stat.setCourse(0);
      stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
      ASSET.Participants.DemandedStatus dem = transit.decide(stat, null, null, null, null, 1000l);

      // where are we heading?
      assertEquals("moving to first destination", 0, transit._currentDestination);
      assertEquals("on correct course", 90, ((SimpleDemandedStatus) dem).getCourse(), 0.001);
      assertEquals("on correct speed", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      // let's position ourselves somewhere and check we're still heading for it
      stat.setLocation(origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(45),
        MWC.Algorithms.Conversions.Yds2Degs(2819), 0)));
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // where are we heading?
      assertEquals("on correct course", 180, ((SimpleDemandedStatus) dem).getCourse(), 0.2);
      assertEquals("on correct speed", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      // put us just outside the threshold , check we don't move onto the next one
      final MWC.GenericData.WorldLocation loc10 = loc2.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(0),
        MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) + 100), 0));
      stat.setLocation(loc10);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // where are we heading?
      assertEquals("on correct course", 180, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      // put us just inside the threshold , check we move onto the next one
      final MWC.GenericData.WorldLocation loc11 = loc2.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(225),
        MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
      stat.setLocation(loc11);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 1, transit._currentDestination);
      assertEquals("on correct course for new location", 225, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      /////////////////////////////////////////////////
      // check still on track
      stat.setLocation(loc11);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we still heading for the next leg?
      assertEquals("moving to next destination", 1, transit._currentDestination);
      assertEquals("on correct course for new location", 225, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);


      /////////////////////////////////////////////////
      // head for the next leg
      final MWC.GenericData.WorldLocation loc12 = loc3.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(315),
        MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
      stat.setLocation(loc12);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 2, transit._currentDestination);
      assertEquals("on correct course for new location", 315, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      /////////////////////////////////////////////////
      // and the next leg
      final MWC.GenericData.WorldLocation loc13 = loc4.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(225),
        MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
      stat.setLocation(loc13);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 3, transit._currentDestination);
      assertEquals("on correct course for new location", 45, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      /////////////////////////////////////////////////
      // and the last leg
      final MWC.GenericData.WorldLocation loc14 = loc5.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(135),
        MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
      stat.setLocation(loc14);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 0, transit._currentDestination);
      assertEquals("on correct course for new location", 135, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      /////////////////////////////////////////////////
      // what if we are running in reverse?
      transit.setReverse(true);

      // put ourselves back one (so we are still heading for the last point)
      transit._currentDestination = 3;

      final MWC.GenericData.WorldLocation loc15 = loc5.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(45),
        MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
      stat.setLocation(loc15);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 2, transit._currentDestination);
      assertEquals("on correct course for new location", 225, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      /////////////////////////////////////////////////
      // carry on going backwards
      final MWC.GenericData.WorldLocation loc16 = loc4.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(135),
        MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
      stat.setLocation(loc16);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 1, transit._currentDestination);
      assertEquals("on correct course for new location", 135, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);


      /////////////////////////////////////////////////
      // carry on going backwards
      final MWC.GenericData.WorldLocation loc17 = loc3.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(45),
        MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
      stat.setLocation(loc17);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 0, transit._currentDestination);
      assertEquals("on correct course for new location", 45, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);



      /////////////////////////////////////////////////
      // and what happens when we get to the start?
      final MWC.GenericData.WorldLocation loc18 = loc2.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(45),
        MWC.Algorithms.Conversions.Yds2Degs(transit._threshold.getValueIn(WorldDistance.METRES) - 100), 0));
      stat.setLocation(loc18);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 1, transit._currentDestination);
      assertEquals("on correct course for new location", 225, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      /////////////////////////////////////////////////
      // continue forwards again
      stat.setLocation(loc12);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 2, transit._currentDestination);
      assertEquals("on correct course for new location", 315, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      /////////////////////////////////////////////////
      // and again
      stat.setLocation(loc13);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", 3, transit._currentDestination);
      assertEquals("on correct course for new location", 45, ((SimpleDemandedStatus) dem).getCourse(), 0.1);
      assertEquals("on correct speed for new destination", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      // tell it to stop looping
      transit.setLoop(false);

      /////////////////////////////////////////////////
      // and the last one
      stat.setLocation(loc14);
      dem = transit.decide(stat, null, null, null, null, 1000l);

      // are we heading for the next leg?
      assertEquals("moving to next destination", -1, transit._currentDestination);
      assertEquals("drop out of decision", null, dem);
    }

    public void testSingleLocation()
    {

      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());
      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(1, 1, 0);

      // create the paths
      final MWC.GenericData.WorldPath path = new MWC.GenericData.WorldPath();

      final MWC.GenericData.WorldLocation loc2 =
        origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
          MWC.Algorithms.Conversions.Yds2Degs(2000), 0));

      path.addPoint(loc2);

      // create the objects
      final Transit transit = new Transit(path, new WorldSpeed(12, WorldSpeed.M_sec), true);

      // configure the transit
      transit.setLoop(true);
      transit.setReverse(false);

      // get going
      final ASSET.Participants.Status stat = new ASSET.Participants.Status(1, 1000);
      stat.setLocation(origin);
      stat.setCourse(0);
      stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
      ASSET.Participants.DemandedStatus dem = transit.decide(stat, null, null, null, null, 1000l);

      // where are we heading?
      assertEquals("moving to first destination", 0, transit._currentDestination);
      assertEquals("on correct course", 90, ((SimpleDemandedStatus) dem).getCourse(), 0.001);
      assertEquals("on correct speed", 12, ((SimpleDemandedStatus) dem).getSpeed(), 0.001);

      // let's position ourselves somewhere and check we're still heading for it
      stat.setLocation(origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
        MWC.Algorithms.Conversions.Yds2Degs(1909), 0)));
      dem = transit.decide(stat, null, null, null, null, 1000l);
      assertEquals("moving to first destination", 0, transit._currentDestination);
      assertEquals("on correct course", 90, ((SimpleDemandedStatus) dem).getCourse(), 0.001);

      // let's position ourselves somewhere and check we're still heading for it
      stat.setLocation(origin.add(new MWC.GenericData.WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
        MWC.Algorithms.Conversions.Yds2Degs(1909), 0)));
      dem = transit.decide(stat, null, null, null, null, 1000l);
      assertEquals("moving to first destination", 0, transit._currentDestination);
      assertEquals("on correct course", 90, ((SimpleDemandedStatus) dem).getCourse(), 0.001);

    }

    public void testModelling1()
    {
      WorldLocation origin = new WorldLocation(0, 0, 0);
      WorldLocation first = createLocation(0, 10000);
      WorldLocation second = createLocation(10000, 10000);
      WorldLocation third = createLocation(10000, 0);

      WorldPath wp = new WorldPath();
      wp.addPoint(first);
      wp.addPoint(second);
      wp.addPoint(third);
      Transit tr = new Transit(wp, new WorldSpeed(12, WorldSpeed.M_sec), false);

      CoreParticipant cp = new SSN(12);
      final String myName = "SSK Trial";
      final double accelRate = 2;
      final double decelRate = 2;
      final double fuel_usage_rate = 2;
      final double maxSpeed = 21;
      final double minSpeed = -5;
      final double defaultClimbRate = 2;
      final double defaultDiveRate = 2;
      final double maxDepth = 200;
      final double minDepth = 10;
      final double turningCircle = 400;


      final ASSET.Models.Movement.MovementCharacteristics chars =
        new ASSET.Models.Movement.SSMovementCharacteristics(myName, accelRate,
          decelRate, fuel_usage_rate,
          maxSpeed, minSpeed, turningCircle, defaultClimbRate,
          defaultDiveRate, maxDepth,
          minDepth);
      cp.setMovementChars(chars);

      Status stat = new Status(0, 1000);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      stat.setLocation(origin);

      cp.setDecisionModel(tr);
      cp.setStatus(stat);

      CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, cp);
      cs.setScenarioStepTime(5000);
      cs.step();

      while ((cp.getActivity() != CoreParticipant.INACTIVE_DESCRIPTOR) && (cs.getTime() < 5555500))
      {
        cs.step();
      }

      assertEquals("we've finished going through the series of points", CoreParticipant.INACTIVE_DESCRIPTOR, cp.getActivity());
      assertEquals("finished on time", 2415000, cs.getTime(), 0);
    }

    public void testModelling2()
    {
      WorldLocation origin = new WorldLocation(0, 0, 0);
      WorldLocation first = createLocation(0, 10000);
      WorldLocation second = createLocation(10000, 10000);
      WorldLocation third = createLocation(10000, 0);

      WorldPath wp = new WorldPath();
      wp.addPoint(first);
      wp.addPoint(second);
      wp.addPoint(third);
      Transit tr = new Transit(wp, new WorldSpeed(12, WorldSpeed.M_sec), true);
      tr.setName("Transit");

      CoreParticipant cp = new SSN(12);
      final String myName = "SSK Trial";
      final double accelRate = 2;
      final double decelRate = 2;
      final double fuel_usage_rate = 2;
      final double maxSpeed = 21;
      final double minSpeed = -5;
      final double defaultClimbRate = 2;
      final double defaultDiveRate = 2;
      final double maxDepth = 200;
      final double minDepth = 10;
      final double turningCircle = 400;


      final ASSET.Models.Movement.MovementCharacteristics chars =
        new ASSET.Models.Movement.SSMovementCharacteristics(myName, accelRate,
          decelRate, fuel_usage_rate,
          maxSpeed, minSpeed, turningCircle, defaultClimbRate,
          defaultDiveRate, maxDepth,
          minDepth);
      cp.setMovementChars(chars);

      Status stat = new Status(0, 1000);
      stat.setLocation(origin);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      cp.setDecisionModel(tr);
      cp.setStatus(stat);

      CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, cp);
      cs.setScenarioStepTime(5000);
      cs.step();

      System.out.println("about to start test 2");

      while ((cp.getActivity() != CoreParticipant.INACTIVE_DESCRIPTOR) && (cs.getTime() < 2415500))
      {
        cs.step();
      }

      assertEquals("we're still going through the transit", "Transit:Transit heading for waypoint:2", cp.getActivity());

      // check we're heading back to the start
      int nextPoint = 1;  // last point would be size-1, but the transit heads back to the one before this
      assertEquals("heading for first point", nextPoint, tr._currentDestination);
      assertTrue("it knows we're going backwards", tr._in_reverse_transit);

      // wait until we get back to the start
      while (tr._currentDestination != 0)
      {
        cs.step();
      }

      // right, now we're heading for the start- wait until we reach it
      while (tr._currentDestination == 0)
      {
        cs.step();
      }

      // right, see where we're going now
      assertEquals("going to next step", 1, tr._currentDestination, 0);
      assertFalse("it knows we're going forward through them", tr._in_reverse_transit);

      // now wait until we're getting to the end
      while (tr._currentDestination != 2)
      {
        cs.step();
      }

      // right, now we're heading for the end - wait until we reach it
      while (tr._currentDestination == 2)
      {
        cs.step();
      }

      // now see where it's taking us now
      assertEquals("heading for last but one point", 1, tr._currentDestination);
      assertTrue("it knows we're going backward through them", tr._in_reverse_transit);


    }

    public void testModelling3()
    {
      WorldLocation origin = new WorldLocation(0, 0, 0);
      WorldLocation first = createLocation(0, 10000);
      WorldLocation second = createLocation(10000, 10000);
      WorldLocation third = createLocation(10000, 0);

      WorldPath wp = new WorldPath();
      wp.addPoint(first);
      wp.addPoint(second);
      wp.addPoint(third);
      Transit tr = new Transit(wp, new WorldSpeed(12, WorldSpeed.M_sec), true);
      tr.setReverse(false);
      tr.setName("Transit");

      CoreParticipant cp = new SSN(12);
      final String myName = "SSK Trial";
      final double accelRate = 2;
      final double decelRate = 2;
      final double fuel_usage_rate = 2;
      final double maxSpeed = 21;
      final double minSpeed = -5;
      final double defaultClimbRate = 2;
      final double defaultDiveRate = 2;
      final double maxDepth = 200;
      final double minDepth = 10;
      final double turningCircle = 400;


      final ASSET.Models.Movement.MovementCharacteristics chars =
        new ASSET.Models.Movement.SSMovementCharacteristics(myName, accelRate,
          decelRate, fuel_usage_rate,
          maxSpeed, minSpeed, turningCircle, defaultClimbRate,
          defaultDiveRate, maxDepth,
          minDepth);
      cp.setMovementChars(chars);

      Status stat = new Status(0, 1000);
      stat.setLocation(origin);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      cp.setDecisionModel(tr);
      cp.setStatus(stat);

      CoreScenario cs = new CoreScenario();
      cs.addParticipant(12, cp);
      cs.setScenarioStepTime(5000);
      cs.step();

      System.out.println("about to start test 3");
      while ((cp.getActivity() != CoreParticipant.INACTIVE_DESCRIPTOR) && (cs.getTime() < 2416000))
      {
        cs.step();
      }

      assertEquals("we're still going through the transit", "Transit:Transit heading for waypoint:1", cp.getActivity());

      // check we're heading back to the start
      int nextPoint = 0; // we head for the first point
      assertEquals("heading for first point", nextPoint, tr._currentDestination);

      // wait until we've got back to the start
      while ((tr._currentDestination == 0) && (cs.getTime() < 2416000 * 2))
      {
        cs.step();
      }

      assertEquals("heading for first point", 1, tr._currentDestination);


      // now run through to the end again, and see where we're going
      while ((tr._currentDestination != 2))
      {
        cs.step();
      }

      // right - now we're heading for the last point
      while ((tr._currentDestination == 2))
      {
        cs.step();
      }

      assertEquals("we're still going through the transit", "Transit:Transit heading for waypoint:1", cp.getActivity());

      // check we're heading back to the start
      nextPoint = 0; // we head for the first point
      assertEquals("heading for first point", nextPoint, tr._currentDestination);

    }


  }


}