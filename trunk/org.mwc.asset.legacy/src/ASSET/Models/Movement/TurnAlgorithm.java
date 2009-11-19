package ASSET.Models.Movement;

import ASSET.Models.MWCModel;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.SSK;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.Conversions;
import MWC.GenericData.*;

/**
 * Class providing turning circle algorithm
 */
public class TurnAlgorithm implements MWCModel
{

  /**
   * the speed delta we use to decide if we are at demanded speed or not
   */
  public static final double SPEED_DELTA = 0.001;

  /**
   * the course delta we use to decide if we are at demanded course or not
   */
  public static final double COURSE_DELTA = 0.01;

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  // get our working data items (m/sec)
  private double curSpeed_m_sec;
  private double curCourse_rads;
  private double curHeight;

  private double demSpeed_m_sec;
  private double demCourseRads;
  private double demHeight;

  private double latVal_m = 0.0; // this will store the change in lat (m)
  private double longVal_m = 0.0; // this will store the change in long (m)


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  public ASSET.Participants.Status doTurn(final ASSET.Participants.Status status,
                                          final ASSET.Models.Movement.SimpleDemandedStatus demStatus,
                                          final ASSET.Models.Movement.MovementCharacteristics chars,
                                          final long newTime)
  {
    Status res = null;

    // so, lets see if we are handling a vehicle which travels at default speeds
    if (chars instanceof ClimbRateCharacteristics)
    {
      ClimbRateCharacteristics climber = (ClimbRateCharacteristics) chars;

      // does the manoeuvre include a depth change
      if (demStatus != null)
      {
        // get the demanded depth
       // double demHeight = demStatus.getHeight();

        // ok - what's the delta
        double heightDelta = demHeight - (-status.getLocation().getDepth());

        // now sort out the default
        final double defaultSpeed;

        // how long is this step
        double secs = ((double) newTime - status.getTime()) / 1000;

        // is there a depth change
        if (heightDelta != 0)
        {
          // yes - break it down
          if (heightDelta < 0)
          {
            // we're diving
            defaultSpeed = climber.getDefaultDiveSpeed().getValueIn(WorldSpeed.M_sec);
          }
          else
          {
            // we're ascending
            defaultSpeed = climber.getDefaultClimbSpeed().getValueIn(WorldSpeed.M_sec);
          }

          // how long will the depth change take?
          double HeightTime = calcHeightChangeTime(heightDelta, chars, secs);

          // convert to long millis
          long HeightMillis = (long) (HeightTime * 1000d);

          // remember the current dem speed
          double oldDemSpeed = demStatus.getSpeed();

          // use the new default speed
          demStatus.setSpeed(defaultSpeed);

          // do this first movement
          res = doTurn2(status, demStatus, chars, status.getTime() + HeightMillis);

          // restore the former demanded speed
          demStatus.setSpeed(oldDemSpeed);

          // and now the remaining time (once we're at the depth
          res = doTurn2(res, demStatus, chars, newTime);

        }
      }
    }

    // did we manage to do a special case?
    if (res == null)
    {
      // no - just do our normal old turn move
      res = doTurn2(status, demStatus, chars, newTime);
    }

    return res;
  }

  public ASSET.Participants.Status doTurn2(final ASSET.Participants.Status status,
                                           final ASSET.Models.Movement.SimpleDemandedStatus demStatus,
                                           final ASSET.Models.Movement.MovementCharacteristics chars,
                                           final long newTime)
  {

    // convert millis to secs
    final long millis = newTime - status.getTime();
    final double secs = millis / 1000d;

    // SPECIAL CASE - CHECK IF WE RECEIVED ZERO STEP TIME
    if (secs == 0)
    {
      return status;
    }


    // get our working data items (m/sec)
    curSpeed_m_sec = status.getSpeed().getValueIn(WorldSpeed.M_sec);
    curCourse_rads = MWC.Algorithms.Conversions.Degs2Rads(status.getCourse());
    curHeight = -status.getLocation().getDepth();

    demSpeed_m_sec = 0;
    demCourseRads = 0;
    demHeight = 0;

    double fuelLevel = status.getFuelLevel();

    // check that we have a demanded status, else we'll continue in steady state
    if (demStatus == null)
    {
      demSpeed_m_sec = status.getSpeed().getValueIn(WorldSpeed.M_sec);
      demCourseRads = MWC.Algorithms.Conversions.Degs2Rads(status.getCourse());
      demHeight = -status.getLocation().getDepth();
    }
    else
    {
      demSpeed_m_sec = demStatus.getSpeed();
      demCourseRads = MWC.Algorithms.Conversions.Degs2Rads(demStatus.getCourse());
      demHeight = demStatus.getHeight();
    }

    latVal_m = 0.0; // this will store the change in lat (m)
    longVal_m = 0.0; // this will store the change in long (m)

    double speed_change_rate_m_sec_sec = 0; // value in m/sec/sec
    final double accel_rate_m_sec_sec = chars.getAccelRate().getValueIn(WorldAcceleration.M_sec_sec); // value in m/sec/sec
    final double decel_rate_m_sec_sec = chars.getDecelRate().getValueIn(WorldAcceleration.M_sec_sec); // value in m/sec/sec

    final double max_speed_m_sec = chars.getMaxSpeed().getValueIn(WorldSpeed.M_sec);
    final double min_speed_m_sec = chars.getMinSpeed().getValueIn(WorldSpeed.M_sec);

    final double min_height_m = chars.getMinHeight().getValueIn(WorldDistance.METRES);
    final double max_height_m = chars.getMaxHeight().getValueIn(WorldDistance.METRES);

    // declare working variables
    int accelerating = 0;

    final double step = secs;
    double straight_time = 0;

    double turn_time = 0;
    double turn_speed = 0;

    double accel_time = 0;
    double accel_speed = 0;

    double course_change = 0;
    double turn_direction = 0;
    double speed_at_end_of_step = 0;

    // stop us exceeding maximum speed
    demSpeed_m_sec = Math.min(demSpeed_m_sec, max_speed_m_sec);

    // stop us exceeding minimum speed
    demSpeed_m_sec = Math.max(demSpeed_m_sec, min_speed_m_sec);

    // trim the height hcanges max height
    demHeight = Math.min(demHeight, max_height_m);
    demHeight = Math.max(demHeight, min_height_m);


    /////////////////////////////////////////////////////////
    // conduct Height change
    /////////////////////////////////////////////////////////
    double HeightDelta = 0;
    if (demHeight != curHeight)
    {
      // Height change required
      // see how much
      final double dHeight = demHeight - curHeight;

      // how long will the time take?
      final double HeightChangeTime = calcHeightChangeTime(dHeight, chars, secs);

      final double changeRate;

      // go as far as we can, in the correct direction

      // just see if it's a worthwhile height change
      if (Math.abs(dHeight) < 0.001)
      {
        HeightDelta = dHeight;
      }
      else
      {
        // are we climbing or diving?
        if (dHeight > 0)
        {
          // hey, we're going up - get the climb rate
          changeRate = chars.getClimbRate().getValueIn(WorldSpeed.M_sec);
        }
        else
        {
          // oooh, diving, get dive rate
          changeRate = chars.getDiveRate().getValueIn(WorldSpeed.M_sec);
        }

        // see how far we can dive in this time period
        final double HeightThisCycle = changeRate * HeightChangeTime;

        // yes, it's a significant height change.  Do it justice and give it a proper
        // calculation
        if (dHeight > 0)
          HeightDelta = HeightThisCycle;
        else
          HeightDelta = -HeightThisCycle;
      }

    }

    //////////////////////////////////////////////////
    // handle the speed change
    //////////////////////////////////////////////////

    // see if we require an acceleration
    final double deltaSpeed = Math.abs(demSpeed_m_sec - curSpeed_m_sec);
    if (deltaSpeed > SPEED_DELTA)
    {
      // acceleration is required

      // see if we are accelerating or not
      if (demSpeed_m_sec > curSpeed_m_sec)
      {
        speed_change_rate_m_sec_sec = accel_rate_m_sec_sec;
        accelerating = 1;
      }
      else
      {
        speed_change_rate_m_sec_sec = decel_rate_m_sec_sec;
        accelerating = -1;
      }

      // calculate the time taken to perform the acceleration
      accel_time = deltaSpeed / speed_change_rate_m_sec_sec;

      // clip the acceleration time to the step time
      if (accel_time > step)
      {
        accel_time = step;
      }
    }
    else if (deltaSpeed > 0)
    {
      // ok, we're not as much as the speed delta, but we are still more than zero - set
      // to demanded speed
      curSpeed_m_sec = demSpeed_m_sec;
    }

    //calculate the speed which we will be travelling at
    //at the end of the step
    speed_at_end_of_step = curSpeed_m_sec + accel_time * speed_change_rate_m_sec_sec * accelerating;

    // see if we require a turn
    course_change = demCourseRads - curCourse_rads;

    // are we trying to go the long way around?
    if (course_change > Math.PI)
      course_change -= 2 * Math.PI;

    if (course_change < -Math.PI)
      course_change += 2 * Math.PI;

    if (Math.abs(course_change) > COURSE_DELTA)
    {
      // we do require a turn.
      // determine the rate of course change at the average speed
      // through this step
      final double mean_speed = (curSpeed_m_sec + speed_at_end_of_step) / 2;

      // find out how long it takes to get to the new course
      turn_time = chars.calculateTurnTime(mean_speed, MWC.Algorithms.Conversions.Rads2Degs(course_change));

      // clip turn time period
      if (turn_time > step)
        turn_time = step;

      // see if we are turning to port or stbd
      double sinChange = Math.sin(course_change);

      if (sinChange > 0)
        turn_direction = 1; // stbd
      else
        turn_direction = -1;
    }
    else
    {
      // practically on course, make the final adjustment
      curCourse_rads = demCourseRads;
    }

    // briefly check whether a manoeuvre is required at all
    if ((accel_time == 0) && (turn_time == 0))
    {
      this.performStraightLineTravel(step, curSpeed_m_sec);
    }
    else
    {

      // check which manoeuvre overlaps which
      if (accel_time >= turn_time)
      {

        // we will probably be accelerating uniformly throughout
        // the turn, { poss continue in a straight line
        if (turn_time > 0)
        {

          // prepare for the turn
          turn_speed = curSpeed_m_sec + (accelerating * turn_time *
            speed_change_rate_m_sec_sec / 2);

          // ok, now do the turn
          performConstantSpeedTurn(chars, turn_speed, turn_time, turn_direction);


          // update the current speed
          curSpeed_m_sec = (curSpeed_m_sec + accelerating * turn_time * speed_change_rate_m_sec_sec);

        } // end of turn inside acceleration

        // now do the straight line portion of the manoeuvre

        // determine the average speed during the straight line
        // acceleration
        accel_speed = curSpeed_m_sec + accelerating * (accel_time - turn_time) * speed_change_rate_m_sec_sec / 2;

        // do the travel whilst changing speed
        this.performStraightLineTravel(accel_time - turn_time, accel_speed);

        // update the current speed as a product of the straight line acceleration
        curSpeed_m_sec = (curSpeed_m_sec + accelerating * (accel_time - turn_time) * speed_change_rate_m_sec_sec);

        // we've now completed the acceleration.  do the straight line portion
        straight_time = step - accel_time;

        // and do the travel once we're at the final speed
        this.performStraightLineTravel(straight_time, curSpeed_m_sec);
      }
      else
      {
        // the turn will take longer than the acceleration

        // check if there was an acceleration
        if (accel_time > 0)
        {

          // prepare for accelerating turn
          turn_speed = curSpeed_m_sec + accelerating * accel_time *
            speed_change_rate_m_sec_sec / 2;

          // now do the speed change part of the turn
          this.performConstantSpeedTurn(chars, turn_speed, accel_time, turn_direction);

          // and update the speed
          curSpeed_m_sec = (curSpeed_m_sec + (accelerating * accel_time * speed_change_rate_m_sec_sec));

        } // there was an acceleration in the turn


        // and now perform the steady speed turn
        this.performConstantSpeedTurn(chars, curSpeed_m_sec, (turn_time - accel_time), turn_direction);

        // prepare for the constant speed in straight line
        straight_time = step - turn_time;

        // now do the constant straight line
        performStraightLineTravel(straight_time, curSpeed_m_sec);

      } // end of acceleration takes longer than turn


      // briefly check that the current course is in the correct range
      if (curCourse_rads > Conversions.Degs2Rads(360))
      {
        curCourse_rads = (curCourse_rads - Conversions.Degs2Rads(360));
      }
      if (curCourse_rads < 0)
      {
        curCourse_rads = (curCourse_rads + Conversions.Degs2Rads(360));
      }
    }

    // sort out the fuel usage
    final double rate = chars.getFuelUsageRate();
    final double used = rate * secs * MWC.Algorithms.Conversions.Mps2Kts(curSpeed_m_sec);
    fuelLevel = fuelLevel - used;

    // update the vectors
    // now produce rng & brg for the lat/long deltas
    final double dRng_m = Math.sqrt(latVal_m * latVal_m + longVal_m * longVal_m);
    final double dBrg = Math.atan2(longVal_m, latVal_m);

    // create a new location
    final WorldLocation newLoc = status.getLocation().add(new WorldVector(dBrg, Conversions.m2Degs(dRng_m), -HeightDelta));

    final Status res = new Status(status);
    res.setTime(newTime);
    res.setLocation(new WorldLocation(newLoc));

    res.setCourse(MWC.Algorithms.Conversions.Rads2Degs(curCourse_rads));
    res.setSpeed(new WorldSpeed(curSpeed_m_sec, WorldSpeed.M_sec));
    res.setFuelLevel(fuelLevel);

    // sometimes we never get onto the demanded course because of rads 2 degs not being commutative
    double courseDegs = MWC.Algorithms.Conversions.Rads2Degs(demCourseRads);
    double courseDelta = Math.abs(res.getCourse() - courseDegs);
    if(courseDelta < 0.00001)
      res.setCourse(courseDegs);

    return res;
  }

  private void performStraightLineTravel(final double straight_time, final double travelSpeed_m_sec)
  {
    if (straight_time > 0)
    {
      longVal_m = (longVal_m + travelSpeed_m_sec * straight_time *
        Math.sin(curCourse_rads));

      latVal_m = (latVal_m + travelSpeed_m_sec * straight_time *
        Math.cos(curCourse_rads));
    }
  }

  private void performConstantSpeedTurn(final MovementCharacteristics chars, final double turn_speed,
                                        final double turn_time, final double turn_direction)
  {

    if (turn_time > 0)
    {

      final double TCircle;
      final double turn_rate_rads;
      final double course_change;

      TCircle = chars.getTurningCircleDiameter(turn_speed) / 2;

      // find the overall course change
      turn_rate_rads = MWC.Algorithms.Conversions.Degs2Rads(chars.calculateTurnRate(curSpeed_m_sec));

      // how far do we turn?
      course_change = turn_rate_rads * turn_time;

      // now actually perform the turn

      // update the current positions as a product of
      // the turn
      longVal_m = (longVal_m + 2 * TCircle *
        Math.sin(curCourse_rads + turn_direction * course_change / 2)
        * Math.sin(course_change / 2));

      latVal_m = (latVal_m + 2 * TCircle *
        Math.cos(curCourse_rads + turn_direction * course_change / 2)
        * Math.sin(course_change / 2));

      // update the current course as a product of the turn
      curCourse_rads = (curCourse_rads + turn_direction * course_change);
    }
  }


  //////////////////////////////////////////////////
  // convenience methods
  //////////////////////////////////////////////////

  /**
   * convenience method to return boolean depending on if we're accelerating or not
   *
   * @param currentSpeed  current speed (m/sec)
   * @param demandedSpeed demanded speed (m/sec)
   * @return true/false for is accelerating
   */
  public static boolean areWeAccelerating(double currentSpeed, double demandedSpeed)
  {
    boolean res;
    if (demandedSpeed > currentSpeed)
      res = true;
    else
      res = false;

    return res;
  }

  /**
   * convenience method to calculate the accelerating/deceleration rate
   *
   * @param isAccelerating whether we're accelerating or not
   * @param moves          the movement characteristics of this vehicle
   * @return the relevant accelerating/deceleration rate
   */
  public static WorldAcceleration calcAccelRate(boolean isAccelerating, MovementCharacteristics moves)
  {
    WorldAcceleration res = null;
    WorldAcceleration accelRate = moves.getAccelRate();
    WorldAcceleration decelRate = moves.getDecelRate();
    res = calcAccelRate(isAccelerating, accelRate, decelRate);
    return res;
  }

  /**
   * convenience  method to calculate the accelerating/deceleration rate
   *
   * @param isAccelerating whether we're accelerating or not
   * @param accelRate      the current acceleration rate
   * @param decelRate      the current deceleration rate
   * @return the relevant accel/decel value
   */
  private static WorldAcceleration calcAccelRate(boolean isAccelerating, WorldAcceleration accelRate,
                                                 WorldAcceleration decelRate)
  {
    WorldAcceleration res;
    if (isAccelerating)
      res = accelRate;
    else
      res = decelRate;
    return res;
  }


  /**
   * calculate how long the Height change takes
   *
   * @param dHeight the Height change
   * @param chars   the movement characteristics
   * @param secs    the time step
   * @return the time taken (secs)
   */
  protected static double calcHeightChangeTime(double dHeight,
                                               MovementCharacteristics chars,
                                               double secs)
  {
    final double changeRate;

    // are we climbing or diving?
    if (dHeight > 0)
    {
      // hey, we're going up - get the climb rate
      changeRate = chars.getClimbRate().getValueIn(WorldSpeed.M_sec);
    }
    else
    {
      // oooh, diving, get dive rate
      changeRate = chars.getDiveRate().getValueIn(WorldSpeed.M_sec);
    }

    // see how long the Height change will take at this rate
    double HeightChangeTime = Math.abs(dHeight / changeRate);

    // and trim the Height change time
    HeightChangeTime = Math.min(HeightChangeTime, secs);

    return HeightChangeTime;
  }


  //////////////////////////////////////////////////
  // model support
  //////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: TurnAlgorithm.java,v $
   * Revision 1.1  2006/08/08 14:21:52  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:26:00  Ian.Mayo
   * First versions
   *
   * Revision 1.39  2004/10/07 12:49:28  Ian.Mayo
   * Overcome units conversion problem where we don't end up on target course
   *
   * Revision 1.38  2004/09/02 09:42:41  Ian.Mayo
   * Add new test, minor formatting
   *
   * Revision 1.37  2004/08/31 09:36:50  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.36  2004/08/25 11:21:03  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.35  2004/08/16 09:16:21  Ian.Mayo
   * Respect changed processing of tester recording to file (it needed a valid scenario object)
   * <p/>
   * Revision 1.34  2004/08/09 15:50:43  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.33  2004/05/24 15:09:15  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:54  ian
   * no message
   * <p/>
   * Revision 1.32  2003/12/15 09:03:20  Ian.Mayo
   * Add new convenience method
   * <p/>
   * Revision 1.31  2003/12/08 13:17:06  Ian.Mayo
   * Implement OnTop alg
   * <p/>
   * Revision 1.30  2003/11/28 09:55:43  Ian.Mayo
   * Minor javadoc change
   * <p/>
   * Revision 1.29  2003/11/21 14:58:34  Ian.Mayo
   * Add convenience methods
   * <p/>
   * Revision 1.28  2003/11/21 08:49:02  Ian.Mayo
   * Rename dem course to specify rads, extend handling for turning correct way round
   * <p/>
   * Revision 1.27  2003/09/19 13:38:21  Ian.Mayo
   * Switch to Speed and Distance objects instead of just doubles
   * <p/>
   * Revision 1.26  2003/09/19 07:39:43  Ian.Mayo
   * New manoeuvering characteristics
   * <p/>
   * Revision 1.25  2003/09/18 12:13:10  Ian.Mayo
   * Reflect introduction of World Speed
   * <p/>
   * Revision 1.24  2003/09/15 10:16:19  Ian.Mayo
   * remove d-lines
   * <p/>
   * Revision 1.23  2003/09/12 13:15:57  Ian.Mayo
   * Pass scenario time to recorders
   * <p/>
   * Revision 1.22  2003/09/12 09:55:29  Ian.Mayo
   * Implement changing altitude at default climb/dive rates
   * <p/>
   * Revision 1.21  2003/09/09 15:59:33  Ian.Mayo
   * new signature of position recorder
   * <p/>
   * Revision 1.20  2003/09/02 15:44:27  Ian.Mayo
   * switch around test for if we're climbing or diving
   * <p/>
   * Revision 1.19  2003/09/02 10:28:43  Ian.Mayo
   * refactor turns
   * <p/>
   * Revision 1.18  2003/09/01 15:32:47  Ian.Mayo
   * put us on the correct speed when we're really near it anyway
   * <p/>
   * Revision 1.17  2003/09/01 14:43:54  Ian.Mayo
   * put ssn turn tests back in (including delayed turn)
   * <p/>
   * Revision 1.16  2003/09/01 13:39:41  Ian.Mayo
   * put ssn turn tests back in
   * <p/>
   * Revision 1.15  2003/09/01 08:22:51  Ian.Mayo
   * Correctly implement decelerations
   * <p/>
   * Revision 1.14  2003/08/29 15:45:56  Ian.Mayo
   * copy the previous status, don't re-use the old one
   * <p/>
   * Revision 1.13  2003/08/21 15:52:26  Ian.Mayo
   * stop it being a static method
   * <p/>
   * Revision 1.12  2003/08/20 16:08:18  Ian.Mayo
   * refactor turn time calculation
   * <p/>
   * Revision 1.11  2003/08/20 14:17:41  Ian.Mayo
   * remove unused turn tests (from old implementation)
   * <p/>
   * Revision 1.10  2003/08/20 10:57:14  Ian.Mayo
   * Correct places where we store turning circle
   * <p/>
   * Revision 1.9  2003/08/19 15:56:23  Ian.Mayo
   * Part way through checking turning circles
   * <p/>
   * Revision 1.8  2003/08/19 14:43:24  Ian.Mayo
   * Switch turning circle method call to use
   * average speed through turn instead of speed at
   * start of turn
   * <p/>
   * Revision 1.7  2003/08/19 14:30:41  Ian.Mayo
   * Handle new simple demanded status - so we know we have dem course
   * <p/>
   * Revision 1.6  2003/08/19 12:43:07  Ian.Mayo
   * Switch turn/course thresholds into final class variables
   * <p/>
   * Revision 1.5  2003/08/14 15:37:05  Ian.Mayo
   * Improve testing, recognise min permitted speeds
   * <p/>
   * Revision 1.4  2003/08/13 15:58:50  Ian.Mayo
   * Improve testing (for helo)
   * <p/>
   * Revision 1.3  2003/08/13 13:49:38  Ian.Mayo
   * Switch algorithm units to SI
   * <p/>
   * Revision 1.2  2003/08/13 13:41:09  Ian.Mayo
   * Part way through transition to new manoeuvring
   * conventions
   * <p/>
   * Revision 1.1.1.1  2003/07/25 09:58:13  Ian.Mayo
   * Repository rebuild.
   * <p/>
   * Revision 1.9  2003/06/11 15:01:41  ian_mayo
   * tidy doc comments
   * <p/>
   * Revision 1.8  2003-05-29 11:15:11+01  ian_mayo
   * Try to prepend with br marker
   * <p/>
   * Revision 1.7  2003-05-16 15:16:40+01  ian_mayo
   * move template around
   * <p/>
   * Revision 1.6  2003-05-16 15:08:03+01  ian_mayo
   * Settled on using the pre formatter
   * <p/>
   * Revision 1.5  2003-05-16 15:02:14+01  ian_mayo
   * Try to include log in javadoc
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////


  static public class TurnTest extends SupportTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public TurnTest(final String val)
    {
      super(val);
    }

    public void testHeloStraight()
    {

      final TurnAlgorithm turner = new TurnAlgorithm();

      // put the earth model into flat mode
      final MWC.Algorithms.EarthModels.CompletelyFlatEarth flatModel = new MWC.Algorithms.EarthModels.CompletelyFlatEarth();
      MWC.GenericData.WorldLocation.setModel(flatModel);
      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);

      // ok, set up our input data
      final Status stat = new Status(0, 5);
      stat.setCourse(0);
      stat.setFuelLevel(100);
      stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

      final Status dem_stat = new Status(0, 5);
      dem_stat.setCourse(0);
      dem_stat.setFuelLevel(100);
      dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
      dem_stat.setTime(0);


      ASSET.Models.Movement.SimpleDemandedStatus dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);

      final String myName = "Merlin Trial";
      final double accelRate = 10;
      final double decelRate = 25;
      final double fuel_usage_rate = 0;
      final double maxSpeed = 100;
      final double minSpeed = -5;
      final double defaultClimbRate = 15;
      final double defaultDiveRate = 15;
      final double maxHeight = 400;
      final double minHeight = 0;
      final double myTurnRate = 3;
      final double defaultClimbSpeed = 15;
      final double defaultDiveSpeed = 20;

      final ASSET.Models.Movement.MovementCharacteristics chars =
         ASSET.Models.Movement.HeloMovementCharacteristics.generateDebug(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxHeight,
                                                              minHeight, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);

      ASSET.Participants.Status res = null;

      // start with straight line
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
      WorldVector wv = res.getLocation().subtract(origin);
      super.assertEquals("Straight, const spd", MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 40, 0.001);

      // now do decelerating (fitting in to single time step)
      dem_stat.setSpeed(new WorldSpeed(10, WorldSpeed.M_sec));
      dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(35, WorldSpeed.M_sec));
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
      wv = res.getLocation().subtract(origin);

      // so, how far should we have travelled: change of 25 m/s takes 1 secs at 25 m/sec/sec.
      // so we travel at 22.5 m/sec for 1 secs then 10 m/sec for remaining 9 secs
      // total distance is thus 105m
      final double t1_expected_range = 112.5;
      super.assertEquals("Straight, decelerating", t1_expected_range, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);
      super.assertEquals("Straight, decelerating", res.getSpeed().getValueIn(WorldSpeed.M_sec), 10, 0.001);
      super.assertEquals("Straight, decelerating", res.getCourse(), stat.getCourse(), 0.001);


      // now do decelerating (not fitting in single time step)
      dem_stat.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));
      dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(100, WorldSpeed.M_sec));
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 2000);
      wv = res.getLocation().subtract(origin);
      // so, how far should we have traveled?  It will take 4 secs to slow from 100 to 0 at 25 m/sec/sec
      // the step is only 1/2 this, so we will slow down to 50 kts - travelling at an average of 75 m_sec through
      // the time step. So, we will travel at 75 m_sec for 2 secs for total of 375m.
      final int t2_expected_range = 150;
      super.assertEquals("Straight, decelerating", t2_expected_range, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);
      super.assertEquals("Straight, decelerating", 50, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001);

      // now do decelerating (limited by minimum speed)
      dem_stat.setSpeed(new WorldSpeed(-10, WorldSpeed.M_sec));
      dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(20, WorldSpeed.M_sec));
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
      wv = res.getLocation().subtract(origin);
      // so, how far should we have traveled?  It will take 1 second to slow from
      // 20 kts to -5, leaving 9 seconds at -5 m/sec.  So 1 at 2.5 (12.5 m), 9 at
      // -5 (-45 m), total is -37.5m
      final double t3_expected_range = 37.5;
      super.assertEquals("Straight, decelerating - travelled correct distance", t3_expected_range, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);
      super.assertEquals("Straight, decelerating - travelled backwards", 180, MWC.Algorithms.Conversions.Rads2Degs(wv.getBearing()), 0.001);
      super.assertEquals("Straight, decelerating", -5, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001);


      // now do accelerating (not fitting in single time step)
      dem_stat.setSpeed(new WorldSpeed(100, WorldSpeed.M_sec));
      dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(0, WorldSpeed.M_sec));
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 5000);
      wv = res.getLocation().subtract(origin);
      // how far will we go?
      // it would take 10 secs to get to 100 from zero.  We only have 5 - so we will only get up to 50 m_sec.
      // it we accelerate linearly, we will travel at average of 25 m_secs for 5 secs = 125m
      super.assertEquals("Straight, accelerating", 125, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);
      super.assertEquals("Straight, accelerating", 50, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001);

      // now do accelerating (limited by max spd)
      dem_stat.setSpeed(new WorldSpeed(150, WorldSpeed.M_sec));
      dem = new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(90, WorldSpeed.M_sec));
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
      wv = res.getLocation().subtract(origin);
      // how far?  We will take 1 sec to get to 100 m_sec, so 95 m_sec for 1 sec plus 100 m_sec for 9 secs, total
      // dist of 995m
      super.assertEquals("Straight, accelerating", 995, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);
    }


    public void testHeloTurnShape()
    {
      final TurnAlgorithm turner = new TurnAlgorithm();

      // put the earth model into flat mode
      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(0, 0, 0);

      // ok, set up our input data
      final Status stat = new Status(0, 5);
      stat.setCourse(0);
      stat.setFuelLevel(100);
      stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      stat.setSpeed(new WorldSpeed(10, WorldSpeed.M_sec));

      final Status dem_stat = new Status(0, 5);
      dem_stat.setCourse(180);
      dem_stat.setFuelLevel(100);
      dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      dem_stat.setSpeed(new WorldSpeed(10, WorldSpeed.M_sec));


      final ASSET.Models.Movement.SimpleDemandedStatus dem =
        new ASSET.Models.Movement.SimpleDemandedStatus(0, dem_stat);

      final String myName = "Merlin Trial";
      final double accelRate = 10;
      final double decelRate = 25;
      final double fuel_usage_rate = 0;
      final double maxSpeed = 100;
      final double minSpeed = -5;
      final double defaultClimbRate = 15;
      final double defaultDiveRate = 15;
      final double maxHeight = 400;
      final double minHeight = 0;
      final double myTurnRate = 3;
      final double defaultClimbSpeed = 15;
      final double defaultDiveSpeed = 20;

      final ASSET.Models.Movement.MovementCharacteristics chars =
         ASSET.Models.Movement.HeloMovementCharacteristics.generateDebug(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxHeight,
                                                              minHeight, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);

      ASSET.Participants.Status res = null;

      final double turn_diam = 381.97186342054886;
      // check we've calculated it correctly
      assertEquals("we're using correct turn diameter", turn_diam, chars.getTurningCircleDiameter(10), 0.01);

      // clockwise at steady speed, fit in one cycle
      res = new Status(stat);

      final CoreParticipant cp = new SSK(12);
      cp.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      cp.setName("test SSK");

      // move forward the first couple of steps
      res = turner.doTurn(res, dem, chars, res.getTime() + 1000);

      // check we've changed course by the correct amount
      assertEquals("changed course by correct amount", 3, res.getCourse(), 0.001);
      assertEquals("maintained speed", 10, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001);

      // and again
      res = turner.doTurn(res, dem, chars, res.getTime() + 1000);
      // check we've changed course by the correct amount
      assertEquals("changed course by correct amount", 6, res.getCourse(), 0.001);
      assertEquals("maintained speed", 10, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001);

      // move forward another 58 steps (to get around to 180 degs)
      for (int j = 0; j < 58; j++)
      {
        res = turner.doTurn(res, dem, chars, res.getTime() + 1000);
      }

      // are we at correct location?
      final WorldLocation thisEndPoint = stat.getLocation().add(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
                                                                                MWC.Algorithms.Conversions.m2Degs(turn_diam),
                                                                                0));
      double range = thisEndPoint.subtract(res.getLocation()).getRange();
      range = MWC.Algorithms.Conversions.Degs2m(range);
      assertEquals("we're following the correct circle", 0, range, 0.001);

      // now see what happens when we slow down
      res.setLocation(new WorldLocation(origin));
      res.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
      res.setCourse(0);
      res.setTime(0);

      dem.setSpeed(0);
      dem.setCourse(0);

      res = turner.doTurn(res, dem, chars, res.getTime() + 1000);


    }


    public void testSSNStraight()
    {
      final TurnAlgorithm turner = new TurnAlgorithm();

      // put the earth model into flat mode
      final MWC.Algorithms.EarthModels.CompletelyFlatEarth flatModel = new MWC.Algorithms.EarthModels.CompletelyFlatEarth();
      MWC.GenericData.WorldLocation.setModel(flatModel);
      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);

      // ok, set up our input data
      final Status stat = new Status(0, 0);
      stat.setCourse(0);
      stat.setFuelLevel(100);
      stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

      final Status dem_stat = new Status(0, 0);
      dem_stat.setCourse(0);
      dem_stat.setFuelLevel(100);
      dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
      dem_stat.setTime(0);

      SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

      final String myName = "SSK Trial";
      final double accelRate = 0.5;
      final double decelRate = 0.2;
      final double fuel_usage_rate = 2;
      final double maxSpeed = 21;
      final double minSpeed = -5;
      final double defaultClimbRate = 2;
      final double defaultDiveRate = 2;
      final double maxHeight = 200;
      final double minHeight = 10;
      final double turningCircle = 200;


      final ASSET.Models.Movement.MovementCharacteristics chars =
         ASSET.Models.Movement.SSMovementCharacteristics.generateDebug(myName, accelRate,
                                                            decelRate, fuel_usage_rate,
                                                            maxSpeed, minSpeed, defaultClimbRate,
                                                            defaultDiveRate, maxHeight,
                                                            minHeight, turningCircle);


      ASSET.Participants.Status res = null;

      // start with straight line
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
      WorldVector wv = res.getLocation().subtract(origin);
      super.assertEquals("Straight, const spd", 40, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);

      // now do decelerating (not fitting in single time step)
      dem_stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
      dem = new SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(19, WorldSpeed.M_sec));
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
      wv = res.getLocation().subtract(origin);
      // so, how far should we go?
      // if we decelerate at 0.2 m/sec/sec and we want to decelerate 7 m/s.  This will take 35 secs.  We've only
      // got 10 = so it's a uniform deceleration.  After 10 secs will have dec by 2 m/sec.  Therefore mean speed is
      // (78 + 19)/2 = 18.  So, 10 secs at 18 (180)
      super.assertEquals("Straight, decelerating", 180, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);
      super.assertEquals("Straight, decelerating", 17, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0.001);
      super.assertEquals("Straight, decelerating", res.getCourse(), stat.getCourse(), 0.001);

      // now do decelerating (fitting in single time step)
      dem_stat.setSpeed(new WorldSpeed(15, WorldSpeed.M_sec));
      dem = new SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(19, WorldSpeed.M_sec));
      // how far should we be?  Drop to 17 m/sec will take 20 secs.  Mean speed through this
      // period will be 17 m/sec (340m).  Leaving 20 secs at 15 m/sec (300m).
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 40000);
      wv = res.getLocation().subtract(origin);
      super.assertEquals("Straight, decelerating", 640, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);

      // now do accelerating (fitting in single time step)
      dem_stat.setSpeed(new WorldSpeed(14, WorldSpeed.M_sec));
      dem = new SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
      // how far?  Accelerate will take 4 secs - average is 13 (52m), leaving 6 secs at 14 m/sec (84)
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
      wv = res.getLocation().subtract(origin);
      super.assertEquals("Straight, accelerating", 136, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);

      // now do accelerating (not fitting in single time step)
      dem_stat.setSpeed(new WorldSpeed(19, WorldSpeed.M_sec));
      dem = new SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
      // how far?  We need to accelerate by 7 m/sec.  This will take 14 secs at 0.5 m/sec/sec.  We will accelerate
      // for 10 secs, taking us to 17 m/sec - average is 14.5 m/sec for 10 secs.  Distance is:  145m
      wv = res.getLocation().subtract(origin);
      super.assertEquals("Straight, accelerating", 145, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);

      // now do accelerating (limited by max spd)
      dem_stat.setSpeed(new WorldSpeed(31, WorldSpeed.M_sec));
      dem = new SimpleDemandedStatus(0, dem_stat);
      stat.setSpeed(new WorldSpeed(19, WorldSpeed.M_sec));
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 10000);
      // so, how far?  Our max speed is 21.  It will take 4 secs to do this 2m/sec change (4 * 20 = 80), leaving
      // 6 secs at 21 m/sec (126).
      wv = res.getLocation().subtract(origin);
      super.assertEquals("Straight, accelerating", 206, MWC.Algorithms.Conversions.Degs2m(wv.getRange()), 0.001);

    }


    public void testAlmostOnCourseTurn()
    {
      final TurnAlgorithm turner = new TurnAlgorithm();

      // put the earth model into flat mode
      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);


      double demCourse = 307.2185479616559;
      double curCourse = 307.2185479359266;

      // ok, set up our input data
      Status stat = new Status(0, 0);
      stat.setCourse(curCourse);
      stat.setFuelLevel(100);
      stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

      final Status dem_stat = new Status(0, 0);
      dem_stat.setCourse(demCourse);
      dem_stat.setFuelLevel(100);
      dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));


      final SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

      final String myName = "SSK Trial";
      final double accelRate = 2;
      final double decelRate = 2;
      final double fuel_usage_rate = 2;
      final double maxSpeed = 21;
      final double minSpeed = -5;
      final double defaultClimbRate = 2;
      final double defaultDiveRate = 2;
      final double maxHeight = 200;
      final double minHeight = 10;
      final double turningCircle = 400;


      final ASSET.Models.Movement.MovementCharacteristics chars =
        ASSET.Models.Movement.SSMovementCharacteristics.generateDebug(myName, accelRate,
                                                            decelRate, fuel_usage_rate,
                                                            maxSpeed, minSpeed, turningCircle, defaultClimbRate,
                                                            defaultDiveRate, maxHeight,
                                                            minHeight);

      // so, how far should we turn in a second?
 //     final double sec_turn = chars.calculateTurnRate(stat.getSpeed().getValueIn(WorldSpeed.M_sec));

      stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);

      assertEquals("not on demanded course", demCourse, stat.getCourse(), 0);

    }

    public void testSSNSimpleTurn()
    {

      final TurnAlgorithm turner = new TurnAlgorithm();

      // put the earth model into flat mode
      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);

      // ok, set up our input data
      Status stat = new Status(0, 0);
      stat.setCourse(0);
      stat.setFuelLevel(100);
      stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

      final Status dem_stat = new Status(0, 0);
      dem_stat.setCourse(90);
      dem_stat.setFuelLevel(100);
      dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));


      final SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

      final String myName = "SSK Trial";
      final double accelRate = 2;
      final double decelRate = 2;
      final double fuel_usage_rate = 2;
      final double maxSpeed = 21;
      final double minSpeed = -5;
      final double defaultClimbRate = 2;
      final double defaultDiveRate = 2;
      final double maxHeight = 200;
      final double minHeight = 10;
      final double turningCircle = 400;


      final ASSET.Models.Movement.MovementCharacteristics chars =
        ASSET.Models.Movement.SSMovementCharacteristics.generateDebug(myName, accelRate,
                                                            decelRate, fuel_usage_rate,
                                                            maxSpeed, minSpeed, turningCircle, defaultClimbRate,
                                                            defaultDiveRate, maxHeight,
                                                            minHeight);

      // so, how far should we turn in a second?
      final double sec_turn = chars.calculateTurnRate(stat.getSpeed().getValueIn(WorldSpeed.M_sec));

      for (int i = 1; i < 30; i++)
      {
        stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);

        // check we've turned the correct amount
        assertEquals("turned correct amount", i * sec_turn, stat.getCourse(), 0.001);
      }

    }

    public void testSSNTurn()
    {

      final TurnAlgorithm turner = new TurnAlgorithm();

      // put the earth model into flat mode
      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(3, 3, 0);

      // ok, set up our input data
      final Status stat = new Status(0, 0);
      stat.setCourse(0);
      stat.setFuelLevel(100);
      stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

      final Status dem_stat = new Status(0, 0);
      dem_stat.setCourse(90);
      dem_stat.setFuelLevel(100);
      dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));


      SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

      final String myName = "SSK Trial";
      final double accelRate = 2;
      final double decelRate = 2;
      final double fuel_usage_rate = 2;
      final double maxSpeed = 21;
      final double minSpeed = -5;
      final double defaultClimbRate = 2;
      final double defaultDiveRate = 2;
      final double maxHeight = 200;
      final double minHeight = 10;
      final double turningCircle = 400;


      final ASSET.Models.Movement.MovementCharacteristics chars =
        ASSET.Models.Movement.SSMovementCharacteristics.generateDebug(myName, accelRate,
                                                            decelRate, fuel_usage_rate,
                                                            maxSpeed, minSpeed, turningCircle, defaultClimbRate,
                                                            defaultDiveRate, maxHeight,
                                                            minHeight);


      ASSET.Participants.Status res = null;

      // clockwise at steady speed, fit in one cycle
      // how long to travel through turn?
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 78539);
      assertEquals("Turn clock, steady speed", 200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()), 0.01);
      assertEquals("Turn clock, steady speed", 200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()), 0.01);

      // clockwise at steady speed, not fit in one cycle
      dem_stat.setCourse(95);
      dem = new SimpleDemandedStatus(0, dem_stat);
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 78539);
      assertEquals("Turn anti-clock, steady speed", 200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()), 0.01);
      assertEquals("Turn clock, steady speed", 200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()), 0.01);

      // anti-clockwise at steady speed, fit in one cycle
      dem_stat.setCourse(-90);
      dem = new SimpleDemandedStatus(0, dem_stat);
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 78539);
      assertEquals("Turn anti-clock, steady speed", 200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()), 0.01);
      assertEquals("Turn anti-clock, steady speed", -200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()), 0.01);

      // anticlockwise at steady speed, not fit in one cycle
      dem_stat.setCourse(-95);
      dem = new SimpleDemandedStatus(0, dem_stat);
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 78539);
      assertEquals("Turn anti-clock, steady speed", 200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()), 0.01);
      assertEquals("Turn anti-clock, steady speed", -200, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()), 0.01);

      // clockwise accelerating
      dem_stat.setCourse(179.9999);
      dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
      dem = new SimpleDemandedStatus(0, dem_stat);
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 157079);
      assertEquals("long turn, final speed", res.getSpeed().getValueIn(WorldSpeed.M_sec), 4, 0.001);
      assertEquals("Long turn, final head", res.getCourse(), 179.999, 0.001);
      assertEquals("Long turn vert travel", 0.000, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()), 0.01);
      assertEquals("Long turn horiz travel", 400, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()), 0.01);

      // clockwise accelerating
      dem_stat.setCourse(-179.9999);
      dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));
      dem = new SimpleDemandedStatus(0, dem_stat);
      res = turner.doTurn(stat, dem, chars, stat.getTime() + 157079);
      assertEquals("long turn, final speed", res.getSpeed().getValueIn(WorldSpeed.M_sec), 4, 0.001);
      assertEquals("Long turn, final head", res.getCourse(), 180.000, 0.001);
      assertEquals("Long turn vert travel", 0.0000, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLat() - origin.getLat()), 0.01);
      assertEquals("Long turn horiz travel", -400, MWC.Algorithms.Conversions.Degs2m(res.getLocation().getLong() - origin.getLong()), 0.01);

    }

    public void testSSNDelayedTurn()
    {

      final TurnAlgorithm turner = new TurnAlgorithm();

      // put the earth model into flat mode
      MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

      final MWC.GenericData.WorldLocation origin = new MWC.GenericData.WorldLocation(0, 0, 0);

      // ok, set up our input data
      Status stat = new Status(0, 0);
      stat.setCourse(0);
      stat.setFuelLevel(100);
      stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));

      final Status dem_stat = new Status(0, 0);
      dem_stat.setCourse(0);
      dem_stat.setFuelLevel(100);
      dem_stat.setLocation(new MWC.GenericData.WorldLocation(origin));
      dem_stat.setSpeed(new WorldSpeed(4, WorldSpeed.M_sec));


      final SimpleDemandedStatus dem = new SimpleDemandedStatus(0, dem_stat);

      final String myName = "SSK Trial";
      final double accelRate = 2;
      final double decelRate = 2;
      final double fuel_usage_rate = 2;
      final double maxSpeed = 21;
      final double minSpeed = -5;
      final double defaultClimbRate = 2;
      final double defaultDiveRate = 2;
      final double maxHeight = 200;
      final double minHeight = 10;
      final double turningCircle = 400;


      final ASSET.Models.Movement.MovementCharacteristics chars =
         ASSET.Models.Movement.SSMovementCharacteristics.generateDebug(myName, accelRate,
                                                            decelRate, fuel_usage_rate,
                                                            maxSpeed, minSpeed, turningCircle, defaultClimbRate,
                                                            defaultDiveRate, maxHeight,
                                                            minHeight);

      final CoreParticipant cp = new CoreParticipant(23);
      cp.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      cp.setName("SSK");

      // create a scenario object just to please the track recorder
      CoreScenario dummyScenario = new CoreScenario();

      startRecording("delayed_turn", true, true, false, dummyScenario);
      recordThis(stat, cp, stat.getTime());

      // more forwards, then turn
      for (int i = 0; i < 4; i++)
      {
        stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);
        recordThis(stat, cp, stat.getTime());
      }

      // check we've travelled the correct distance
      final WorldVector vec = stat.getLocation().subtract(origin);
      assertEquals("travelled correct dist", 16, MWC.Algorithms.Conversions.Degs2m(vec.getRange()), 0.001);

      dem.setCourse(180);

      // more forwards, then turn
      for (int i = 0; i < 158; i++)
      {
        stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);
        recordThis(stat, cp, stat.getTime());
      }

      assertEquals("now on course", 180, stat.getCourse(), 0.001);


      // and down a bit
      for (int i = 0; i < 4; i++)
      {
        stat = turner.doTurn(stat, dem, chars, stat.getTime() + 1000);
        recordThis(stat, cp, stat.getTime());
      }

      endRecording(dummyScenario);

      assertEquals("still on course", 180, stat.getCourse(), 0.001);


    }

    public void testHeloHeightChange()
    {

      final String myName = "Merlin Trial";
      final double accelRate = 10;
      final double decelRate = 40;
      final double fuel_usage_rate = 0;
      final double maxSpeed = 100;
      final double minSpeed = -5;
      final double defaultClimbRate = 10;
      final double defaultDiveRate = 15;
      final double maxHeight = 400;
      final double minHeight = 0;
      final double myTurnRate = 3;
      final double defaultClimbSpeed = 20;
      final double defaultDiveSpeed = 30;

      final ASSET.Models.Movement.MovementCharacteristics chars =
         ASSET.Models.Movement.HeloMovementCharacteristics.generateDebug(myName, accelRate,
                                                              decelRate, fuel_usage_rate,
                                                              maxSpeed, minSpeed, defaultClimbRate,
                                                              defaultDiveRate, maxHeight,
                                                              minHeight, myTurnRate,
                                                              defaultClimbSpeed, defaultDiveSpeed);

      Helo bravo = new Helo(12);
      bravo.setMovementChars(chars);
      bravo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));

      Status stat = new Status(12, 0);
      WorldLocation origin = createLocation(0, 0);
      stat.setLocation(origin);
      stat.setCourse(0);
      stat.setTime(0);
      stat.setSpeed(new WorldSpeed(110, WorldSpeed.M_sec));
      origin.setDepth(-140);

      SimpleDemandedStatus sds = new SimpleDemandedStatus(1000, stat);
      sds.setHeight(80);
      sds.setSpeed(110);

      TurnAlgorithm turner = new TurnAlgorithm();

      // do the first step
      Status res = turner.doTurn(stat, sds, chars, 1000);

      // see how it looks
      assertEquals("decelerated", 70, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -125, res.getLocation().getDepth(), 0);

      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 2000);

      // see how it looks
      assertEquals("decelerated", 30, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -110, res.getLocation().getDepth(), 0);

      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 3000);

      // see how it looks
      assertEquals("decelerated", 30, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -95, res.getLocation().getDepth(), 0);

      //      // ok, now move forward a little more
      //      res = turner.doTurn(res, sds, chars, 4000);
      //
      //      // see how it looks
      //      assertEquals("decelerated", 30, res.getSpeed(), 0);
      //      assertEquals("dived", -80, res.getLocation().getHeight(), 0);

      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 5000);

      // see how it looks
      assertEquals("decelerated", 40, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -80, res.getLocation().getDepth(), 0);

      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 6000);

      // see how it looks
      assertEquals("decelerated", 50, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -80, res.getLocation().getDepth(), 0);

      // hey, let's ask to climb
      sds.setHeight(120);

      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 6250);

      // see how it looks
      assertEquals("decelerated", 52.5, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -82.5, res.getLocation().getDepth(), 0);

      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 6750);

      // see how it looks
      assertEquals("decelerated", 32.5, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -87.5, res.getLocation().getDepth(), 0);

      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 7000);

      // see how it looks
      assertEquals("decelerated", 22.5, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -90, res.getLocation().getDepth(), 0);

      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 8000);

      // see how it looks
      assertEquals("decelerated", 20, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -100, res.getLocation().getDepth(), 0);


      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 9000);

      // see how it looks
      assertEquals("decelerated", 20, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -110, res.getLocation().getDepth(), 0);


      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 10000);

      // see how it looks
      assertEquals("decelerated", 20, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -120, res.getLocation().getDepth(), 0);


      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 11000);

      // see how it looks
      assertEquals("decelerated", 30, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -120, res.getLocation().getDepth(), 0);

      //////////////////////////////////////////////////
      // lets repeat it all using just three steps
      //////////////////////////////////////////////////

      stat.setTime(0);
      stat.getLocation().setDepth(-140);
      stat.setSpeed(new WorldSpeed(110, WorldSpeed.M_sec));

      sds.setSpeed(110);
      sds.setHeight(80);

      // ok, now move forward a little more
      res = turner.doTurn(stat, sds, chars, 6000);

      // see how it looks
      assertEquals("decelerated", 100, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -80, res.getLocation().getDepth(), 0);

      sds.setHeight(120);

      // ok, now move forward a little more
      res = turner.doTurn(res, sds, chars, 11000);

      // see how it looks
      assertEquals("decelerated", 100, res.getSpeed().getValueIn(WorldSpeed.M_sec), 0);
      assertEquals("dived", -120, res.getLocation().getDepth(), 0);


    }

    public void testZeroStepTime()
    {
      TurnAlgorithm turner = new TurnAlgorithm();
      Status oldStat = new Status(12, 12000);
      SimpleDemandedStatus oldDem = null;
      MovementCharacteristics chars = null;
      Status res = turner.doTurn2(oldStat, oldDem, chars, oldStat.getTime());

      // check we're still at the old val
      assertEquals("kept old value", res, oldStat);
      assertEquals("time kept same", res.getTime(), oldStat.getTime(), 0);
    }

  }

  /**
   * determine the speed at which we either climb or dive
   *
   * @param heightDelta the height change we're after
   * @param moves       the movement chars for this platform
   * @return the speed we travel at
   */
  public static WorldSpeed calcClimbSpeed(double heightDelta, ClimbRateCharacteristics moves)
  {
    WorldSpeed res;

    if (heightDelta > 0)
      res = moves.getDefaultClimbSpeed();
    else
      res = moves.getDefaultDiveSpeed();

    return res;
  }


  /**
   * determine what height change we will make in the indicated period
   *
   * @param timeSecs    the time available for the height change
   * @param moves       the climb characteristics for the vehicle
   * @param heightDelta the change in height
   */
  public static double calcHeightChange(double timeSecs, MovementCharacteristics moves, double heightDelta)
  {
    double res = 0;

    double climbRate;

    if (heightDelta > 0)
    {
      climbRate = moves.getClimbRate().getValueIn(WorldSpeed.M_sec);
    }
    else
    {
      climbRate = -moves.getDiveRate().getValueIn(WorldSpeed.M_sec);
    }

    // ok, how far will we go?
    res = timeSecs * climbRate;

    return res;
  }


  /**
   * find out if we are going to do a speed change
   *
   * @param speed
   * @param climbSpeed
   * @return
   */

  public static boolean areWeAccelerating(WorldSpeed speed, WorldSpeed climbSpeed)
  {
    return areWeAccelerating(speed.getValueIn(WorldSpeed.M_sec), climbSpeed.getValueIn(WorldSpeed.M_sec));
  }


}




