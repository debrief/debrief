package ASSET.Models.Movement;

import ASSET.Models.MWCModel;
import ASSET.Participants.Status;
import MWC.GenericData.WorldAcceleration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 19-Aug-2003
 * Time: 14:49:45
 * To change this template use Options | File Templates.
 */
abstract public class WaypointVisitor implements MWCModel
{

  //////////////////////////////////////////////////
  // member fields
  //////////////////////////////////////////////////

  /** if we get interrupted we remember the last location we were at.  When resuming the behaviour we head
   * for the interrupted location and head for it before resuming the track.
   */
  protected WorldLocation _interruptedLocation;

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////
  /**
   * move the participant status forward along the demanded path
   *
   * @param highLevelDemStatus the path, current location and visitor type
   * @param current
   * @param time
   * @param moves
   * @param turner
   * @return
   */
  public abstract Status step(HighLevelDemandedStatus highLevelDemStatus,
                              Status current,
                              long time,
                              MovementCharacteristics moves,
                              TurnAlgorithm turner);



  /** note that our parent behaviour has been interrupted.  If we have a calculated the time/distance
   * to the next waypoint these calculations may become invalid - we need to clear them in order that they
   * be re-calculated next time we actually get called.  On being interrupted we will remember the location
   * so we can head back to it before getting back on our proper route
   * @param currentStatus
   */
  public void routeInterrupted(Status currentStatus)
  {
    // have we already been interrupted?
    if(_interruptedLocation == null)
    {
      // ok, remember where we are
      _interruptedLocation = new WorldLocation(currentStatus.getLocation());
    }
    else
    {
      // we've already been interrupted.  we want to head back to that original location, not another.
    }

    // whatever, we want to forget any immediate route calculations we may have
    forgetCalculatedRoute();
  }

  /** accessor to find out if we've been interrupted
   *
   */
  public boolean hasBeenInterrupted()
  {
    return (_interruptedLocation != null);
  }

  /**
   * note that our parent behaviour has been interrupted.  If we have a calculated the time/distance
   * to the next waypoint these calculations may become invalid - we need to clear them in order that they
   * be re-calculated next time we actually get called.
   */
  protected abstract void forgetCalculatedRoute();



  /**
   * factory method to create visitor of the indicated type
   *
   * @param type type of visitor to create
   * @return new visitor implementation
   */
  public static WaypointVisitor createVisitor(String type)
  {

    WaypointVisitor res = null;
    if (type.equals(OnTopWaypoint._myType))
    {
      res = new OnTopWaypoint();
    }
    else if (type.equals(DirectedOnTopWaypoint._myType))
    {
      res = new DirectedOnTopWaypoint();
    }
    else if (type.equals(MakeWaypoint._myType))
    {
      res = new MakeWaypoint();
    }

    return res;
  }

  abstract public String getType();

  /**
   * process a turn component of a larger manoeuvre
   *
   * @param turnRequired   how far we have to turn (-ve is left)
   * @param moves          the movement chars for this participant
   * @param current        the current status
   * @param endTime        the end time for this step
   * @param turner         the turn algorithm we're using
   * @param demandedHeight the demanded height (normally the same as the current height - to maintain
   *                       height (and therefore speed) in a manoeuvre.
   * @return
   */
  protected Status doThisPartOfTheTurn(double turnRequired, MovementCharacteristics moves,
                                       Status current,
                                       long endTime,
                                       TurnAlgorithm turner,
                                       double demandedHeight)
  {
    // what's our turn rate?
    double turnRate = DirectedOnTopWaypoint.getTurnRateFor(moves,
        current.getSpeed().getValueIn(WorldSpeed.M_sec));
    turnRate = MWC.Algorithms.Conversions.Rads2Degs(turnRate);

    // hmm, so how long will it take?
    long turnTime = (long) (Math.abs(turnRequired) / turnRate * 1000d);

    // hey, trim the turn time to the remaining time
    long thisStep = endTime - current.getTime();
    turnTime = Math.min(turnTime, thisStep);

    // now, to make sure we turn the right way, we're going to turn 1/2 way first, then the second part
    double semiTurn = turnRequired / 2;

    double semiTurnTime = turnTime / 2;

    // hey, make sure we at least have a step to make!
    semiTurnTime = Math.max(1, semiTurnTime);

    // amd do the turn!
    SimpleDemandedStatus sds = new SimpleDemandedStatus(1, (long) (current.getTime() + (semiTurnTime)));
    sds.setCourse(current.getCourse() + semiTurn);
    sds.setSpeed(current.getSpeed());
    sds.setHeight(demandedHeight);
    current = turner.doTurn(current, sds, moves, sds.getTime());

    // and the second part of the turn!
    sds = new SimpleDemandedStatus(1, current.getTime() + (long) (turnTime - semiTurnTime));
    sds.setCourse(current.getCourse() + semiTurn);
    sds.setSpeed(current.getSpeed());
    sds.setHeight(demandedHeight);
    current = turner.doTurn(current, sds, moves, sds.getTime());

    return current;
  }

  protected static Status processStraightCourse(Double requiredDistance, Status current, long endTime,
                                                HighLevelDemandedStatus highLevelDemStat,
                                                TurnAlgorithm turner, MovementCharacteristics moves)
  {

    // How far do we still have to go?
    double straightDistance = requiredDistance.doubleValue();

    // how far will we travel in this time step?

    // hey, how long of the time step is remaining?
    long thisTimeStep = endTime - current.getTime();

    // do we have a demanded height change ('cos this really buggers things up)
    double demHeight = -highLevelDemStat.getCurrentTarget().getDepth();
    double curHeight = -current.getLocation().getDepth();

    WorldLocation origin = new WorldLocation(current.getLocation());

    // take note of the original height.  We may change height during a height change, but wish to return to the original
    // height on completion.  Alternatively we may wish to change to a demanded speed on completion of the height change
    WorldSpeed originalSpeed = null;

    if (demHeight != curHeight)
    {

      // ok, and is there any distance remaining?
      double distanceTravelled = current.getLocation().subtract(origin).getRange();
      distanceTravelled = MWC.Algorithms.Conversions.Degs2m(distanceTravelled);
      double distanceRemaining = straightDistance - distanceTravelled;
      if (distanceRemaining > 0.1)
      {
        // yup, let's continue

        // yup, height change there is...
        double heightDelta = demHeight - curHeight;

        // find out what the height change speed is
        double stepTime = (endTime - current.getTime()) / 1000d;

        // and how long will the height change take?
        long heightChangeTimeMillis = (long) (TurnAlgorithm.calcHeightChangeTime(heightDelta, moves, stepTime) * 1000d);

        // WORKAROUND - a very small height change may require a time less than one millisecond - represented
        // as zero time.  If this is the case, give us a tiny time step
        if (heightChangeTimeMillis == 0)
          heightChangeTimeMillis = 1;

        // hey, trim the height change to our remaining time
        heightChangeTimeMillis = Math.min(heightChangeTimeMillis, thisTimeStep);

        long timeTakenMillis;

        // right then.  Is this a vehicle with standard climb and dive speeds?
        if (moves instanceof ClimbRateCharacteristics)
        {
          // yes, we need to factor in the speed change

          // ok, we're doing a height change with speed consequences.  remember the current speed, so
          // we can return to it on completion
          originalSpeed = new WorldSpeed(current.getSpeed());


          // ok, how far will we travel during this period
          timeTakenMillis = DirectedOnTopWaypoint.calcClippedHeightChangeTimeFor(current.getSpeed(),
              heightDelta,
              moves,
              heightChangeTimeMillis,
              distanceRemaining);

          // todo - double-check that height change is achievable in the time-step,
          // though the turn decision algorithm should only produce achievable height changes

        }
        else
        {
          // right, we're just going to travel at normal speed for the turn.
          // calc how far we are due to travel in the time step

          // how long will it take to cover the remaining distance at this speed
          timeTakenMillis = (long) (1000 * straightDistance / current.getSpeed().getValueIn(WorldSpeed.M_sec));

        }

        // trim to the step time
        timeTakenMillis = Math.min(heightChangeTimeMillis, timeTakenMillis);

        // and create a demanded status
        SimpleDemandedStatus sds = new SimpleDemandedStatus(1, current);

        // and set the height
        sds.setHeight(demHeight);

        // and do the step
        current = turner.doTurn(current, sds, moves, current.getTime() + timeTakenMillis);

      } // if we have distance to run
    }

    // that's the height change done - we should be at our demanded height now.  If there's any time left we will allow
    // a speed change

    // ok, height change is done.  Is there any time left?
    if (current.getTime() < endTime)
    {
      // ok, and is there any distance remaining?
      double distanceTravelled = current.getLocation().subtract(origin).getRange();
      distanceTravelled = MWC.Algorithms.Conversions.Degs2m(distanceTravelled);
      double distanceRemaining = straightDistance - distanceTravelled;
      if (distanceRemaining > 0.1)
      {
        // yup, let's continue

        // is there a demanded speed change?
        WorldSpeed demSpeedObj = highLevelDemStat.getSpeed();
        if (demSpeedObj != null)
        {
          double demSpeed = demSpeedObj.getValueIn(WorldSpeed.M_sec);
          double curSpeed = current.getSpeed().getValueIn(WorldSpeed.M_sec);

          if (curSpeed != demSpeed)
          {
            // ok, process the speed change
            long remainingTime = endTime - current.getTime();

            boolean isAccelerating = TurnAlgorithm.areWeAccelerating(curSpeed, demSpeed);
            WorldAcceleration accelRate = TurnAlgorithm.calcAccelRate(isAccelerating, moves);
            long timeAllowed = DirectedOnTopWaypoint.calcTrimmedAccelTime(demSpeedObj, current.getSpeed(), accelRate, remainingTime, distanceRemaining);

            // and move us forward
            SimpleDemandedStatus sds = new SimpleDemandedStatus(1, current);

            // and set the speed
            sds.setSpeed(demSpeedObj);

            // and do the step
            current = turner.doTurn(current, sds, moves, current.getTime() + timeAllowed);

          }    // if we need a speed change
        } // if we have a demanded speed
        else
        {
          // hey, no demanded speed change.  Was there a dem height change?
          // This may have caused a speed change which we now need to counter
          if (originalSpeed != null)
          {
            // yup, we changed height.  We need to head back to the old speed
            // ok, process the speed change
            long remainingTime = endTime - current.getTime();

            boolean isAccelerating = TurnAlgorithm.areWeAccelerating(current.getSpeed(), originalSpeed);
            WorldAcceleration accelRate = TurnAlgorithm.calcAccelRate(isAccelerating, moves);
            long timeAllowed = DirectedOnTopWaypoint.calcTrimmedAccelTime(current.getSpeed(), current.getSpeed(),
                accelRate, remainingTime, distanceRemaining);
//            long timeAllowed = DirectedOnTopWaypoint.calcTrimmedAccelTime(demSpeedObj, current.getSpeed(),
//                accelRate, remainingTime, distanceRemaining);

            // and move us forward
            SimpleDemandedStatus sds = new SimpleDemandedStatus(1, current);

            // and do the step
            current = turner.doTurn(current, sds, moves, current.getTime() + timeAllowed);

          }
        }
      } // if we have distance to run
    } // if there is time left

    // hmm, that's the speed change over. do we have any time left?
    if (current.getTime() < endTime)
    {
      // how far have we travelled
      double distanceTravelled = current.getLocation().subtract(origin).getRange();
      distanceTravelled = MWC.Algorithms.Conversions.Degs2m(distanceTravelled);

      // which leaves how far?
      double distanceRemaining = straightDistance - distanceTravelled;


      if (distanceRemaining > 0.1)
      {
        // yup, let's continue travelling in a straight line at steady speed/height
        // collate the input data.

        // how long will it take to cover the remaining distance at this speed
        long timeTakenMillis = (long) (1000 * straightDistance / current.getSpeed().getValueIn(WorldSpeed.M_sec));

        // trim to the step time
        timeTakenMillis = Math.min(thisTimeStep, timeTakenMillis);

        // and create a demanded status
        SimpleDemandedStatus sds = new SimpleDemandedStatus(1, current);

        // and do the step
        current = turner.doTurn(current, sds, moves, current.getTime() + timeTakenMillis);

      }

    }

    return current;
  }

  /**
   * We've got a height change manoeuvre. Clip the time if necessary so that we don't overshoot
   * our destination.
   *
   * @param speed                  - start speed
   * @param heightDelta            - the required height change
   * @param moves                  - the vehicle movement characteristics
   * @param heightChangeTimeMillis - the time taken for the height change
   * @param distanceRemaining      the distance left to travel
   * @return the clipped time for this manoeuvre (in millis)
   */
  protected static long calcClippedHeightChangeTimeFor(WorldSpeed speed, double heightDelta,
                                                       MovementCharacteristics moves,
                                                       long heightChangeTimeMillis,
                                                       double distanceRemaining)
  {
    long res = 0;

    // so, what's our climb rate
    ClimbRateCharacteristics climbChars = (ClimbRateCharacteristics) moves;
    WorldSpeed climbSpeed = TurnAlgorithm.calcClimbSpeed(heightDelta, climbChars);

    // do we need to accelerate or decelerate?
    boolean isAccelerating = TurnAlgorithm.areWeAccelerating(speed, climbSpeed);
    WorldAcceleration accelRate = TurnAlgorithm.calcAccelRate(isAccelerating, moves);

    // and find out how far through the time change we allow ourselves to go (without
    // going past the marker)
    res = calcTrimmedAccelTime(climbSpeed, speed, accelRate, heightChangeTimeMillis, distanceRemaining);

    // hey, done.
    return res;
  }

  protected static long calcTrimmedAccelTime(WorldSpeed finalSpeed,
                                             WorldSpeed currentSpeed,
                                             WorldAcceleration accelRate,
                                             long stepTimeMillis,
                                             double distanceRemaining)
  {
    long res;
    // how long will it take to get to the demanded speed
    final double finalSpd = finalSpeed.getValueIn(WorldSpeed.M_sec);
    final double curSpd = currentSpeed.getValueIn(WorldSpeed.M_sec);
    final double accelRt = accelRate.getValueIn(WorldAcceleration.M_sec_sec);
    long accelTimeMillis = (long) (1000d * Math.abs(finalSpd - curSpd) / accelRt);

    // trim this to our time step
    accelTimeMillis = Math.min(stepTimeMillis, accelTimeMillis);

    // so, how far will we travel during the time to get to the new speed
    long straightTimeMillis = stepTimeMillis - accelTimeMillis;
    double distTravelled = accelTimeMillis / 1000d * (curSpd + (finalSpd - curSpd) / 2) + straightTimeMillis / 1000d * finalSpd;

    // is this further than our required distance?
    if (distTravelled > distanceRemaining)
    {
      // ok, trim the time taken so that we don't overshoot
      double theTimeSecs = accelTimeMillis / 1000d + (distanceRemaining -
          (accelTimeMillis / 1000d * (curSpd + (finalSpd - curSpd) / 2))) / finalSpd;

      res = (long) (theTimeSecs * 1000d);
    }
    else
    {
      // all is well, return the time
      res = stepTimeMillis;
    }
    return res;
  }

  /**
   * calculate the turn rate for this vehicle
   *
   * @param moves the movement chars for this vehicle
   * @param speed the current speed
   * @return the turn rate (rads/sec)
   */
  protected static double getTurnRateFor(MovementCharacteristics moves, double speed)
  {
    // just trim the speed (if it's zero - just give us a little forward motion
    if (speed == 0)
      speed = 1e-10;

    double turnRadius = moves.getTurningCircleDiameter(speed) / 2;

    // so, the equation for the radius of a turning circle using the turn rate and speed is
    //
    // speed (m/sec) = radius (m) * angular velocity (radians/sec)
    //
    // radius = speed / angular velocity
    //
    // angular vel = speed / radius;

    double turnRate = speed / turnRadius;

    return turnRate;
  }

}
