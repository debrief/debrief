package ASSET.Models.Decision.Tactical;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.UserControl;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Vessels.Surface;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class Intercept extends CoreDecision implements java.io.Serializable
{

  //////////////////////////////////////////////////////////////////////
  // Member Variables
  //////////////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * a local copy of our editable object
   */
  protected EditorType _myEditor = null;

  /**
   * the target we are trying to intercept
   */
  private TargetType _targetType;

  /**
   * the current target we are tracking
   */
  private Integer _currentTarget;

  /**
   * whether we let ourselves change speed to intercept the target
   */
  private boolean _speedChangeAllowed;



  //////////////////////////////////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////////////////////////////////

  public Intercept(TargetType targetType, boolean speedChangeAllowed)
  {
    super("Intercept");

    _targetType = targetType;
    _speedChangeAllowed = speedChangeAllowed;
  }

  //////////////////////////////////////////////////
  // the actual intercept calculation
  //////////////////////////////////////////////////


  /**
   * utility function calculating intercept course
   *
   * @param ownship my status
   * @param target  his status
   * @param time    the current time
   * @return demanded status object containing desired course & speed
   */
  public static SimpleDemandedStatus calculateInterceptCourseFor(Status ownship,
                                                                 Status target,
                                                                 long time)
  {

    SimpleDemandedStatus demStat = null;

    // throw in a particularly coarse can/can't test
    if (ownship.getSpeed().getValueIn(WorldSpeed.M_sec) > target.getSpeed().getValueIn(WorldSpeed.M_sec))
    {
      // yes, we're travelling more quickly than him.
      // yes, a result is roughly possible
      double oSpd = ownship.getSpeed().getValueIn(WorldSpeed.M_sec);
      double tSpd = target.getSpeed().getValueIn(WorldSpeed.M_sec);
      double tCourse = target.getCourse();
      WorldVector separation = ownship.getLocation().subtract(target.getLocation());
//      double separation_metres = MWC.Algorithms.Conversions.Degs2m(separation.getRange());
      double brg = MWC.Algorithms.Conversions.Rads2Degs(separation.getBearing());
      if (brg < 0)
        brg += 360;
      if (brg > 360)
        brg -= 360;


      double p1 = Math.toRadians(tCourse - brg);
      while (p1 > Math.PI)
        p1 -= 2 * Math.PI;

      double p2 = tSpd * Math.sin(p1) / oSpd;
      double p3 = Math.asin(p2);
      double p4 = -Math.toDegrees(p3) + brg + 180;
      double res = p4 % 360;

      demStat = new SimpleDemandedStatus(time, ownship);
      demStat.setCourse(res);
    }


    return demStat;
  }



  //////////////////////////////////////////////////////////////////////
  // Member methods
  //////////////////////////////////////////////////////////////////////


  public DemandedStatus decide(final Status status,
                               ASSET.Models.Movement.MovementCharacteristics chars,
                               DemandedStatus demStatus,
                               final ASSET.Models.Detection.DetectionList detections,
                               ASSET.Scenario.ScenarioActivityMonitor monitor,
                               final long time)
  {
    SimpleDemandedStatus res = null;
    String activity = null;

    DetectionEvent targetDetection = null;

    // right, see if we are in contact with anything
    if (detections.size() > 0)
    {
      DetectionList validDets = detections.getDetectionsOf(_currentTarget, _targetType);

      if (validDets != null)
      {
        // remember what we're heading for
        targetDetection = validDets.getMostRecentDetection();

        // and remember which target this is
        _currentTarget = new Integer(targetDetection.getTarget());
      }
    }


    // have we anything to head for?
    if (targetDetection != null)
    {
      // yes, calc the course to it
      ParticipantType pt = monitor.getThisParticipant(targetDetection.getTarget());
      Status tgtStat = pt.getStatus();
      res = calculateInterceptCourseFor(status, tgtStat, time);

      // did it work?
      if (res == null)
      {
        // no, do we let ourselves change speed?
        if (_speedChangeAllowed)
        {
          Status newStat = new Status(status);
          newStat.setSpeed(chars.getMaxSpeed());

          // and try again
          res = calculateInterceptCourseFor(newStat, tgtStat, time);

          // did this work?
          if (res != null)
          {
            // yes, just try to increase speed
            res.setSpeed(chars.getMaxSpeed());
          }
        }
      }

      // ok. have we managed to sort it?
      if (res != null)
      {
        // put in some special processing here.  If the demanded course is only
        // a neglible difference from our current course, just continue on the current course
        //
        // in practice it has turned out that we rarely actually get on course because
        // the intercept calculation varies very slightly each time.  We just
        // insert this dem course change over-ride to smooth this behaviour.

        // yes.  are we changing course?
        double courseDelta = Math.abs(res.getCourse() - status.getCourse());
        //        if (courseDelta > TurnAlgorithm.COURSE_DELTA)
        if (courseDelta > 2)
        {
          activity = "Switching to new intercept course on target:" + pt.getName();
        }
        else
        {
          // error is negligible, continue on current course
          res.setCourse(status.getCourse());
          activity = "Continuing to intercept:" + pt.getName();
        }

        // ok, store it.
        super.setLastActivity(activity);
      }
    }

    // ok, done
    return res;
  }


  /**
   * reset this decision model
   */
  public void restart()
  {
    //
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
  public EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new InterceptInfo(this);

    return _myEditor;
  }


  //////////////////////////////////////////////////
  // getter/setter
  //////////////////////////////////////////////////

  public TargetType getTargetType()
  {
    return _targetType;
  }

  public void setTargetType(TargetType targetType)
  {
    this._targetType = targetType;
  }

  public boolean getSpeedChangeAllowed()
  {
    return _speedChangeAllowed;
  }

  public void setSpeedChangeAllowed(boolean speedChangeAllowed)
  {
    this._speedChangeAllowed = speedChangeAllowed;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Intercept.java,v $
   * Revision 1.1  2006/08/08 14:21:33  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:42  Ian.Mayo
   * First versions
   *
   * Revision 1.10  2005/04/15 14:11:58  Ian.Mayo
   * Update tests to reflect new scenario step cycle
   *
   * Revision 1.9  2004/10/20 09:17:31  Ian.Mayo
   * Correct editable property name
   * <p/>
   * Revision 1.8  2004/09/27 14:54:55  Ian.Mayo
   * Don't calculate reciprocal, correctly bring calc value back in range
   * <p/>
   * Revision 1.7  2004/09/23 07:47:33  Ian.Mayo
   * Re-implement intercept calculation.  Went screwy somehow.
   * <p/>
   * Revision 1.6  2004/09/02 15:33:04  Ian.Mayo
   * More tests, minor tidying.  Assume on course if delta is under 2 degs.  This is more than in turn calc, but necessary because straight section following turn always changes intercept course.
   * <p/>
   * Revision 1.5  2004/09/02 13:17:33  Ian.Mayo
   * Reflect CoreDecision handling the toString method
   * <p/>
   * Revision 1.4  2004/09/02 10:41:23  Ian.Mayo
   * Working implementation, now checking for able to change speed
   * <p/>
   * Revision 1.3  2004/09/02 09:57:55  Ian.Mayo
   * Finish testing, handle when we're nearly on course
   * <p/>
   * Revision 1.2  2004/09/02 08:13:58  Ian.Mayo
   * More implementation, part way through testing
   * <p/>
   * Revision 1.1  2004/08/31 15:28:01  Ian.Mayo
   * Polish off test refactoring, start Intercept behaviour
   * <p/>
   * Revision 1.3  2004/08/31 09:36:14  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.2  2004/08/31 07:24:06  Ian.Mayo
   * Complete test definition
   * <p/>
   * Revision 1.1  2004/08/27 15:10:41  Ian.Mayo
   * (Partially) implement the Avoid behaviour
   * <p/>
   * Revision 1.15  2004/08/26 13:22:21  Ian.Mayo
   * Correct properties description, add property editing
   * <p/>
   * Revision 1.14  2004/08/25 11:20:17  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.13  2004/08/20 13:32:18  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.12  2004/08/17 14:21:57  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.11  2004/08/09 15:50:26  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.10  2004/08/06 12:51:54  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.9  2004/08/06 11:14:16  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.8  2004/05/24 15:46:35  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:51  ian
   * no message
   * <p/>
   * Revision 1.7  2004/02/18 08:47:12  Ian.Mayo
   * Sync from home
   * <p/>
   * Revision 1.5  2003/11/05 09:20:10  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }

  //////////////////////////////////////////////////
  // property editor support
  //////////////////////////////////////////////////

  static public class InterceptInfo extends EditorType
  {


    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public InterceptInfo(final Intercept data)
    {
      super(data, data.getName(), "Intercept");
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
          prop("Name", "the name of this trail model"),
          prop("TargetType", "the type of vehicle to intercept"),
          prop("SpeedChangeAllowed", "whether to allow speed change to intercept target"),
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

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  public static class InterceptTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public InterceptTest(final String val)
    {
      super(val);
    }

    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      TargetType theTgt = new TargetType(Category.Force.RED);
      final Intercept followBlue = new Intercept(theTgt, true);
      return followBlue;
    }

    public void testCalc()
    {
      Status myStat = new Status(12, 12);
      Status hisStat = new Status(12, 12);
      WorldLocation locA = new WorldLocation(0, 0, 0);
      WorldLocation locB = locA.add(new WorldVector(Math.toRadians(240),
                                                    new WorldDistance(2200, WorldDistance.METRES),
                                                    new WorldDistance(0, WorldDistance.METRES)));
      myStat.setSpeed(new WorldSpeed(80, WorldSpeed.M_sec));
      hisStat.setSpeed(new WorldSpeed(22, WorldSpeed.M_sec));
      hisStat.setCourse(330);
      myStat.setLocation(locA);
      hisStat.setLocation(locB);

      SimpleDemandedStatus res = calculateInterceptCourseFor(myStat, hisStat, 1200);
      assertEquals("wrong demanded status calculated", 255.90, res.getCourse(), 0.1);

      // also try it with an unachievable intercept
      myStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
      res = calculateInterceptCourseFor(myStat, hisStat, 1200);
      assertNull("shouldn't have generated demanded state", res);
    }

    protected static DemandedStatus _demStat = null;

    public void testWorking()
    {
      // ok, set up the participants
      final Surface searcher = new Surface(12);
      searcher.setName("Searcher");
      searcher.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FRIGATE));
      searcher.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
      final Surface ffta = new Surface(13);
      ffta.setName("FFTA");
      ffta.setCategory(new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FRIGATE));
      ffta.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

      // states
      Status statA = new Status(12, 12);
      statA.setLocation(SupportTesting.createLocation(0, 0));
      statA.getLocation().setDepth(-400);
      statA.setSpeed(new WorldSpeed(10, WorldSpeed.M_sec));

      Status statB = new Status(12, 12);
      statB.setLocation(SupportTesting.createLocation(1000, 1000));
      statB.setSpeed(new WorldSpeed(5, WorldSpeed.M_sec));

      searcher.setStatus(statA);
      ffta.setStatus(statB);

      // behaviours
      Waterfall wA = new Waterfall("searcher behaviour");
      TargetType theTargetType = new TargetType();
      theTargetType.addTargetType(Category.Force.RED);
      Intercept iA = new Intercept(theTargetType, false)
      {
        /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public DemandedStatus decide(Status status, MovementCharacteristics chars, DemandedStatus demStatus,
                                     DetectionList detections, ScenarioActivityMonitor monitor, long time)
        {
          DemandedStatus res = super.decide(status, chars, demStatus, detections, monitor, time);    //To change body of overridden methods use File | Settings | File Templates.
          _demStat = res;
          return res;
        }
      };
      wA.insertAtFoot(iA);
      searcher.setDecisionModel(wA);

      Waterfall wB = new Waterfall("su behaviour");
      UserControl userBehaviour = new UserControl(330, 3, 0);
      wB.insertAtFoot(userBehaviour);
      ffta.setDecisionModel(wB);


      // sensors
      OpticLookupSensor optic = new OpticLookupSensor(12, "eyes", 1.0, 1000, 2, 0.8,
                                                      new Duration(2, Duration.SECONDS),
                                                      0.6, new Duration(1, Duration.SECONDS));
      searcher.getSensorFit().add(optic);

      // and the scenario
      CoreScenario cs = new CoreScenario()
      {
        /**
         * Move the scenario through a single step
         */
        public void step()
        {
          super.step();
          // SimpleDemandedStatus sds = (SimpleDemandedStatus) searcher.getDemandedStatus();
          //      System.out.println("searcher course:" + searcher.getStatus().getCourse() + " dem:" + sds.getCourse() + " tgt:" + ffta.getStatus().getCourse());
        }
      };
      cs.setSeed(new Integer(12300));
      cs.addParticipant(searcher.getId(), searcher);
      cs.addParticipant(ffta.getId(), ffta);
      cs.setScenarioStepTime(new Duration(5, Duration.SECONDS));
      cs.setTime(0);
      SimpleEnvironment simpleEnv = new SimpleEnvironment(EnvironmentType.CLEAR, 1, EnvironmentType.DAYLIGHT);
      cs.setEnvironment(simpleEnv);


      // start with the sensor off
      optic.setWorking(false);

      // check it's working
      cs.step();

      // ok. with our new scenario stepping, it fires itself once to represent zero tome.
      assertEquals("we haven't performed a step at zero time", 0, cs.getTime(), 0);

      // and now move forward a little
      cs.step();
      assertEquals("we haven't moved forward", 5000, cs.getTime(), 0);

      // check intercept didn't produce anything
      assertNull("we shouldn't have produced dem status - not in contact", _demStat);

      optic.setWorking(true);

      // ok, move forward with working sensor
      cs.step();

      // detection comes after decision, so we need another cycle for the cuts to get to the decision model

      // ok, move forward with working sensor
      cs.step();

      assertEquals("we haven't detected target", 1, searcher.getNewDetections().size(), 0);
      assertNotNull("we haven't received a new demanded status", _demStat);
      assertEquals("we haven't reported properly", "searcher behaviour:Intercept:Switching to new intercept course on target:FFTA", searcher.getActivity());

      // ok, move forward a couple more (until we're on the demanded course
      cs.step();
      cs.step();
      cs.step();
      cs.step();

      // surely we're on course now.

      cs.step();
      cs.step();
      cs.step();
      cs.step();
      cs.step();

      assertEquals("we haven't detected target", 1, searcher.getNewDetections().size(), 0);
      assertNotNull("we haven't received a new demanded status", _demStat);
      assertEquals("we still haven't reported properly", "searcher behaviour:Intercept:Continuing to intercept:FFTA", searcher.getActivity());

//      double oldDemCourse = ((SimpleDemandedStatus) searcher.getDemandedStatus()).getCourse();

      // ok, change the target course & see if we have to change intercept course
      userBehaviour.setCourse(66);
      ffta.getStatus().setCourse(66);

      cs.step();
//      double newDemCourse = ((SimpleDemandedStatus) searcher.getDemandedStatus()).getCourse();
      cs.step();

      assertEquals("we haven't detected target", 1, searcher.getNewDetections().size(), 0);
      assertNotNull("we haven't received a new demanded status", _demStat);
      assertEquals("we haven't reported properly", "searcher behaviour:Intercept:Switching to new intercept course on target:FFTA", searcher.getActivity());


      // now check we don't try to intercept target travelling too fast
      searcher.getStatus().setSpeed(new WorldSpeed(2, WorldSpeed.M_sec));

      cs.step();
      cs.step();

      assertEquals("we haven't detected target", 1, searcher.getNewDetections().size(), 0);
      assertNull("we received a new demanded status", _demStat);
      assertEquals("we haven't reported properly", "inactive", searcher.getActivity());

      // ok, allow speed changes and then see if try to intercept
      iA.setSpeedChangeAllowed(true);

      cs.step();
      cs.step();

      assertEquals("we haven't detected target", 1, searcher.getNewDetections().size(), 0);
      assertNotNull("we haven't received a new demanded status", _demStat);

      // give us back a proper speed
      // now check we don't try to intercept target travelling too fast
      searcher.getStatus().setSpeed(new WorldSpeed(18, WorldSpeed.M_sec));

      for (int i = 0; i < 30; i++)
      {
        cs.step();
      }
      for (int i = 0; i < 30; i++)
      {
        cs.step();
      }

    }


  }


}