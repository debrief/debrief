package ASSET.Models.Decision.Tactical;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Movement.RectangleWander;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.Surface;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Scenario.Observers.TrackPlotObserver;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.*;

import java.util.Vector;

/**
 * Our implementation of investigation.  The host platform will close on a candidate target until the required
 * classification level is achieved, after which it will break off.
 */

public class Investigate extends CoreDecision implements java.io.Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the type of target to investigate
   */
  private TargetType _myTarget = new TargetType();

  /**
   * the desired level of detection required to satisfy investigation
   *
   * @see ASSET.Models.Detection.DetectionEvent.CLASSIFIED et.al
   */
  private int _detectionLevel;

  /**
   * a local copy of our editable object
   */
  private InvestigateInfo _myEditor = null;

  /**
   * list of targets we've already found
   */
  protected Vector<Integer> _targetsDone;

  /**
   * the target id we're currently investigating
   */
  protected Integer _currentTarget;

  /**
   * the height at which we conduct investigation
   */
  private WorldDistance _investigateHeight;

  /****************************************************
   * constructor
   ***************************************************/

  /**
   * constructor.
   *
   * @param myName         the name of this behaviour
   * @param myTarget       the target type to investigate
   * @param detectionLevel the level of detection to achieve
   */
  public Investigate(String myName, TargetType myTarget, int detectionLevel, WorldDistance investigateHeight)
  {
    super(myName);
    this._myTarget = myTarget;
    this._detectionLevel = detectionLevel;
    this._investigateHeight = investigateHeight;

    init();
  }


  /**
   * member method to initialise our local data
   */
  private void init()
  {
    _targetsDone = new Vector<Integer>(0, 1);
    _currentTarget = null;
  }

  /**
   * decide
   *
   * @param status parameter for decide
   * @param time   parameter for decide
   * @return the returned ASSET.Participants.DemandedStatus
   */
  public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
                                                  ASSET.Models.Movement.MovementCharacteristics chars,
                                                  ASSET.Participants.DemandedStatus demStatus,
                                                  final ASSET.Models.Detection.DetectionList detections,
                                                  final ScenarioActivityMonitor monitor,
                                                  long time)
  {

    SimpleDemandedStatus res = null;

    DetectionEvent validDetection = null;

    String activity = "";

    // do we have any detections?
    if (detections != null)
    {
      // get bearing to first detection
      final int len = detections.size();
      if (len > 0)
      {

        // first see if we are still in contact with our existing target
        // do we have an existing target?
        if (_currentTarget != null)
        {

          // see if we can find it
          DetectionList dl = detections.getDetectionsOf(_currentTarget.intValue());
          if (dl != null)
          {
            validDetection = detections.getBestDetection();

            // ok. have we reached the correct detection level?
            if (validDetection.getDetectionState() >= _detectionLevel)
            {
              // done.  ready to move onto another
              // note that we check for greater or equal than detection state = just in case
              // we've jumped a detection state
              _targetsDone.add(_currentTarget);

              // ok, output any results needed
              handleInvestigationComplete(validDetection, time, monitor);

              activity += "Current investigation complete. ";

              // and forget the current target
              _currentTarget = null;

              // just reinforce the fact that we need to look for another target
              validDetection = null;

              // also output a message to any listeners - saying what we've done
              if (_myEditor != null)
              {
                if (_myEditor.hasReportListeners())
                {
                  String msg = _targetsDone.size() + " Targets identified";
                  _myEditor.fireReport(this, msg);
                }
              }
            }


          } // if we have are still in contact with our target
          else
          {
            // oh well, current target lost. output message as necessary
            handleTargetLost(time, monitor, _currentTarget.intValue());

            activity += "Current target lost. ";
          }
        } // whether we have a current target

        // do we still need to look for another target?
        if (validDetection == null)
        {
          // yes, go and find one amongst the remaining targets
          validDetection = findNewTarget(len, detections, time, monitor);

          if (validDetection != null)
            activity += "New target found.";

        } // whether we've found the existing target
      } // if we have any detections
    } // if the detections object was received


    // ok. did we find a new target?
    if (validDetection != null)
    {
      // great, plot a bearing to it
//      float brgToTarget = validDetection.getBearing().floatValue();

      // NO, GENERATE INTERCEPT BEARING
      // yes, calc the course to it
      ParticipantType pt = monitor.getThisParticipant(validDetection.getTarget());
      Status tgtStat = pt.getStatus();
      res = Intercept.calculateInterceptCourseFor(status, tgtStat, time);


      // create the results object
      //      res = new SimpleDemandedStatus(time, status);
      //
      //      // and over-ride the bearing
      //      res.setCourse(brgToTarget);

      // ok, do we have a demanded height?
      if (_investigateHeight != null)
        res.setHeight(_investigateHeight);

      activity += "Turning towards target. Dem brg:" + MWC.Utilities.TextFormatting.GeneralFormat.formatBearing(res.getCourse());

      // output results to any listeners
      handleNewDemCourseChange((float) res.getCourse(), time, monitor, validDetection.getTarget());

      // and remember that we're heading for it
      _currentTarget = new Integer(validDetection.getTarget());

    }

    if (activity == "")
      activity = "Inactive";

    super.setLastActivity(activity);

    // ok done.  If we didn't find a valid target we aren't active.
    return res;
  }

  /**
   * provide any requested reporting of new demanded course
   *
   * @param brgToTarget the new bearing to head down
   * @param time        the current time
   * @param monitor     the scenario activity monitor
   * @param targetId
   */
  protected void handleNewDemCourseChange(float brgToTarget, long time, final ScenarioActivityMonitor monitor,
                                          int targetId)
  {
  }

  /**
   * provide any necessary reporting of investigation complete
   *
   * @param de      the target we were investigating
   * @param time    the time at which investigation was complete
   * @param monitor the scenario activity monitor
   */
  protected void handleInvestigationComplete(final DetectionEvent de, long time,
                                             final ScenarioActivityMonitor monitor)
  {
  }

  /**
   * provide any necessary reporting for target not being found amongst current contacts
   *
   * @param time    the current time
   * @param monitor the scenario activity monitor
   */
  protected void handleTargetLost(long time, final ScenarioActivityMonitor monitor, int currentTarget)
  {
  }

  /**
   * provide any necessary reporting for we have identified a new target
   *
   * @param de      the new target we're going to head for
   * @param time    the current time
   * @param monitor the scenario activity monitor
   */
  protected void handleNewTarget(final DetectionEvent de, long time, final ScenarioActivityMonitor monitor)
  {
  }


  /**
   * scan through the available detections, to see if there's a new, valid one
   *
   * @param len        how many detections there are
   * @param detections our current set of detetions
   * @param time       the current time
   * @param monitor    our scenario activity monitor
   * @return a contact representing our new target
   */
  protected DetectionEvent findNewTarget(final int len, final DetectionList detections, long time,
                                         final ScenarioActivityMonitor monitor)
  {
    DetectionEvent res = null;
    THROUGH_DETECTIONS: for (int i = 0; i < len; i++)
    {

      final DetectionEvent de = detections.getDetection(i);
      // do we know the range
      final Float brg = de.getBearing();
      if (brg != null)
      {
        // is this of our target type
        final Category thisTarget = de.getTargetType();
        if (_myTarget.matches(thisTarget))
        {

          // have we found it already?
          Integer tgtId = new Integer(de.getTarget());

          if (_targetsDone.contains(tgtId))
          {
            // already found it. continue
          }
          else
          {
            // haven't found it yet. head for it.
            res = de;

            // ok, output any message as necessary
            handleNewTarget(de, time, monitor);

            // done, drop out
            break THROUGH_DETECTIONS;
          }
        }
        else
        {
          // drop out to the next detections
          continue THROUGH_DETECTIONS;
        }

      } // if we know the bearing
    } // looping through the detections
    return res;
  }

  /**
   * reset this decision model
   */
  public void restart()
  {
    init();
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
   * setTargetToEvade
   *
   * @param target parameter for setTargetToEvade
   */
  public void setTargetType(final TargetType target)
  {
    _myTarget = target;
  }

  /**
   * getTargetToEvade
   *
   * @return the returned TargetType
   */
  public TargetType getTargetType()
  {
    return _myTarget;
  }

  /**
   * return the required level of detection
   *
   * @return
   * @see DetectionEvent.DETECTED
   */
  public int getDetectionLevel()
  {
    return _detectionLevel;
  }

  /**
   * set the required level of detection
   *
   * @param detectionLevel
   */
  public void setDetectionLevel(int detectionLevel)
  {
    this._detectionLevel = detectionLevel;
  }

  /**
   * get the (possibly null) height at which to conduct investigation
   *
   * @return height
   */
  public WorldDistance getInvestigateHeight()
  {
    return _investigateHeight;
  }

  /**
   * set the (optional) height at which to conduct investigation
   *
   * @param investigateHeight
   */
  public void setInvestigateHeight(WorldDistance investigateHeight)
  {
    this._investigateHeight = investigateHeight;
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
      _myEditor = new InvestigateInfo(this);

    return _myEditor;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: Investigate.java,v $
   * Revision 1.1  2006/08/08 14:21:34  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:42  Ian.Mayo
   * First versions
   *
   * Revision 1.34  2004/11/03 09:54:49  Ian.Mayo
   * Allow search speed to be set
   *
   * Revision 1.33  2004/11/01 15:54:55  Ian.Mayo
   * Reflect new signature of Track Plot Observer
   * <p/>
   * Revision 1.32  2004/10/16 14:10:40  ian
   * Implement progress reporting to editable subject
   * <p/>
   * Revision 1.31  2004/10/16 13:49:26  ian
   * Implement firing report when we find a new target (viewable from editor pane)
   * <p/>
   * Revision 1.30  2004/10/15 11:11:30  Ian.Mayo
   * Configure proper property editor for target category
   * <p/>
   * Revision 1.29  2004/09/27 14:53:56  Ian.Mayo
   * Reflect fact that we now use intercept course rather than just head down bearing to tgt
   * <p/>
   * Revision 1.28  2004/09/24 11:08:10  Ian.Mayo
   * Tidy test names
   * <p/>
   * Revision 1.27  2004/09/03 15:10:30  Ian.Mayo
   * Output correct status messaging
   * <p/>
   * Revision 1.26  2004/09/02 15:59:02  Ian.Mayo
   * Fix bug introduced in refactoring, then fix test
   * <p/>
   * Revision 1.25  2004/09/02 13:19:04  Ian.Mayo
   * Tidy up, use DetectionList convenience methods
   * <p/>
   * Revision 1.24  2004/08/31 09:36:20  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.23  2004/08/26 16:27:02  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.22  2004/08/25 11:20:35  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.21  2004/08/20 13:32:27  Ian.Mayo
   * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
   * <p/>
   * Revision 1.20  2004/08/18 07:27:47  Ian.Mayo
   * Don't run through huge scenario generation sample
   * <p/>
   * Revision 1.19  2004/08/17 14:22:04  Ian.Mayo
   * Refactor to introduce parent class capable of storing name & isActive flag
   * <p/>
   * Revision 1.18  2004/08/12 11:09:22  Ian.Mayo
   * Respect observer classes refactored into tidy directories
   * <p/>
   * Revision 1.17  2004/08/10 14:00:32  Ian.Mayo
   * Ditch the narrative observer, it was absorbed into Debrief Replay observer
   * <p/>
   * Revision 1.16  2004/08/10 13:51:44  Ian.Mayo
   * Provide better activity message
   * <p/>
   * Revision 1.15  2004/08/10 08:50:06  Ian.Mayo
   * Change functionality of Debrief replay observer so that it can record decisions & detections aswell.  Also include ability to track particular type of target
   * <p/>
   * Revision 1.14  2004/08/09 15:27:35  Ian.Mayo
   * More testing
   * <p/>
   * Revision 1.13  2004/08/09 13:13:45  Ian.Mayo
   * Getting closer with testing
   * <p/>
   * Revision 1.12  2004/08/06 12:52:01  Ian.Mayo
   * Include current status when firing interruption
   * <p/>
   * Revision 1.11  2004/08/06 11:14:23  Ian.Mayo
   * Introduce interruptable behaviours, and recalc waypoint route after interruption
   * <p/>
   * Revision 1.10  2004/08/06 10:27:45  Ian.Mayo
   * Manage investigate height
   * <p/>
   * Revision 1.9  2004/08/05 15:20:14  Ian.Mayo
   * Update Activity descriptor, change how we drop out of loop on finding matching contact, tidying.
   * <p/>
   * Revision 1.8  2004/08/04 14:26:33  Ian.Mayo
   * Switch to ladder search, more tidying
   * <p/>
   * Revision 1.7  2004/08/04 09:52:10  Ian.Mayo
   * Improve tests, test for height transition
   * <p/>
   * Revision 1.6  2004/08/03 15:18:59  Ian.Mayo
   * Refactor to tidy message output
   * <p/>
   * Revision 1.5  2004/08/03 10:27:54  Ian.Mayo
   * (Further) relax tests
   * <p/>
   * Revision 1.4  2004/08/02 15:31:19  Ian.Mayo
   * Relax tests
   * <p/>
   * Revision 1.3  2004/08/02 15:00:53  Ian.Mayo
   * Try to get comments working
   * <p/>
   * Revision 1.2  2004/08/02 10:05:04  Ian.Mayo
   * Minor mod.
   * <p/>
   * Revision 1.1  2004/05/24 15:56:14  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.9  2004/05/05 20:39:58  ian
   * Record demanded course
   * <p/>
   * Revision 1.8  2004/05/05 20:26:56  ian
   * Switch to new narrative recorder
   * <p/>
   * Revision 1.7  2004/05/05 19:14:18  ian
   * Use more typical flight height
   * <p/>
   * Revision 1.6  2004/04/28 21:39:04  ian
   * Lots more changes during development
   * <p/>
   * Revision 1.5  2004/04/22 21:36:50  ian
   * Tidying
   * <p/>
   * Revision 1.4  2004/04/19 19:00:12  ian
   * add a note
   * <p/>
   * Revision 1.3  2004/04/15 21:59:41  ian
   * Lost more
   * <p/>
   * Revision 1.2  2004/04/13 21:41:41  ian
   * Getting closer
   * <p/>
   * Revision 1.1  2004/04/13 21:10:53  ian
   * First version
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  static public class InvestigateInfo extends MWC.GUI.Editable.EditorType
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public InvestigateInfo(final Investigate data)
    {
      super(data, data.getName(), "Investigate", true);
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
          prop("TargetType", "the type of vessel this model is evading"),
          prop("Name", "the name of this detonation model"),
          prop("DetectionLevel", "the name of this detonation model"),
        };
        res[2].setPropertyEditorClass(DetectionEvent.DetectionStatePropertyEditor.class);
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }


  public static final class InvestigateTest extends SupportTesting.EditableTesting
  {
    public InvestigateTest(final String val)
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
      TargetType theTarget = new TargetType(Category.Force.RED);
      Investigate investigate = new Investigate("investigating red targets", theTarget, DetectionEvent.IDENTIFIED, null);
      return investigate;
    }

    boolean hasStopped = false;

    public void testStatic()
    {
      TargetType theTarget = new TargetType(Category.Force.RED);
      Investigate investigate = new Investigate("investigating red targets", theTarget, DetectionEvent.IDENTIFIED, null);


      int TGT_ID = 567;
      int NOT_TGT_ID = 123;


      Status myStat = new Status(12, 0);
      myStat.setSpeed(new WorldSpeed(21, WorldSpeed.Kts));
      myStat.setLocation(new WorldLocation(2, 2, 2));

      Status tgtStat = new Status(222, 0);
      tgtStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      tgtStat.setLocation(new WorldLocation(2, 2, 2));
      tgtStat.setCourse(22);

      MovementCharacteristics theChars = HeloMovementCharacteristics.getSampleChars();
      SimpleDemandedStatus theDemStat = new SimpleDemandedStatus(12, 12000);
      DetectionList theDetections = null;


      LookupSensor duffSensor = new RadarLookupSensor(12, "sensor", 0, 0, 0, 0, null, 0, null, 0);
      final Surface target = new Surface(TGT_ID, null, null, "fisher");
      target.setStatus(tgtStat);

      ScenarioActivityMonitor theMonitor = new ScenarioActivityMonitor()
      {
        public void detonationAt(int id, WorldLocation loc, double power)
        {
        }

        public void createParticipant(ParticipantType newPart)
        {
        }

        public ParticipantType getThisParticipant(int id)
        {
          return target;
        }
      };

      DemandedStatus res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      SimpleDemandedStatus simple = (SimpleDemandedStatus) res;
      assertNull("null dem stat when null detections", res);

      // ok. now use zero length detections
      theDetections = new DetectionList();

      res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      assertNull("null dem stat when empty detections", res);

      // and put in an invalid detection
      Category tgtCategory = new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL);
      double target_brg = 12;
      DetectionEvent de = new DetectionEvent(1200, 12, null, duffSensor, null, null, new Float(target_brg),
                                             null, null, tgtCategory, new WorldSpeed(12, WorldSpeed.Kts), null, target, DetectionEvent.DETECTED);
      theDetections.add(de);

      res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      assertNull("null dem stat when invalid target", res);
      assertNull("inv target still empty", investigate._currentTarget);

      // now try a valid target
      tgtCategory.setForce(Category.Force.RED);
      res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      assertNotNull("dem stat when valid target", res);
      assertNotNull("inv target not still empty", investigate._currentTarget);

      // ok. let's lose the target and see what happens
      theDetections.clear();
      res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      assertNull("null dem stat when target lost", res);
      assertNotNull("remembered target", investigate._currentTarget);
      assertEquals("got correct tgt id", TGT_ID, investigate._currentTarget.intValue());

      // and offer another target
      theDetections.add(de);
      de.setTarget(NOT_TGT_ID);
      res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      assertNotNull("new dem stat when valid target", res);
      assertEquals("got new tgt id", NOT_TGT_ID, investigate._currentTarget.intValue());

      // back to our target
      de.setTarget(TGT_ID);
      res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      simple = (SimpleDemandedStatus) res;
      assertNotNull("new dem stat when valid target", res);
      assertTrue("on a good bearing", simple.getCourse() > 0);
      assertEquals("got new tgt id", TGT_ID, investigate._currentTarget.intValue());

      // check that we can tick off target when found
      de.setDetectionState(DetectionEvent.CLASSIFIED);
      res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      assertNotNull("dem stat when valid target", res);
      assertEquals("got new tgt id", TGT_ID, investigate._currentTarget.intValue());

      // check that we can tick off target when found
      de.setDetectionState(DetectionEvent.IDENTIFIED);
      res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      assertNull("no dem status", res);
      assertNull("ditched current target", investigate._currentTarget);
      assertTrue("got something in found targets", investigate._targetsDone.size() == 1);

      // check that we can tick off target when found
      de.setDetectionState(DetectionEvent.CLASSIFIED);
      res = investigate.decide(myStat, theChars, theDemStat, theDetections, theMonitor, 1000);
      assertNull("no dem status when only existing target found", res);
      assertNull("ditched current target", investigate._currentTarget);
      assertTrue("still got something in found targets", investigate._targetsDone.size() == 1);


    }

    public void testRunning()
    {
      final WorldLocation topLeft = SupportTesting.createLocation(0, 10000);
      topLeft.setDepth(-1000);
      final WorldLocation bottomRight = SupportTesting.createLocation(10000, 0);
      bottomRight.setDepth(1000);
      final WorldArea theArea = new WorldArea(topLeft, bottomRight);
      final RectangleWander heloWander = new RectangleWander(theArea, "rect wander");
      heloWander.setSpeed(new WorldSpeed(20, WorldSpeed.Kts));
      final RectangleWander fishWander = new RectangleWander(theArea, "rect wander 2");

      RandomGenerator.seed(12);

      Waterfall searchPattern = new Waterfall();
      searchPattern.setName("Searching");

      TargetType theTarget = new TargetType(Category.Force.RED);
      Investigate investigate = new Investigate("investigating red targets", theTarget, DetectionEvent.IDENTIFIED, null);
      searchPattern.insertAtHead(investigate);
      searchPattern.insertAtFoot(heloWander);

      final Status heloStat = new Status(1, 0);
      heloStat.setLocation(SupportTesting.createLocation(2000, 4000));
      heloStat.getLocation().setDepth(-200);
      heloStat.setCourse(270);
      heloStat.setSpeed(new WorldSpeed(100, WorldSpeed.Kts));

      final Status fisherStat = new Status(1, 0);
      fisherStat.setLocation(SupportTesting.createLocation(4000, 2000));
      fisherStat.setCourse(155);
      fisherStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      final DemandedStatus dem = null;

      final SimpleDemandedStatus ds = (SimpleDemandedStatus) heloWander.decide(heloStat, null, dem, null, null, 100);

      assertNotNull("dem returned", ds);

      // ok. the radar first
      ASSET.Models.Sensor.Lookup.RadarLookupSensor radar = new
        RadarLookupSensor(12, "radar", 0.04, 11000, 1.2, 0, new Duration(0, Duration.SECONDS),
                          0, new Duration(0, Duration.SECONDS), 9200);

      ASSET.Models.Sensor.Lookup.OpticLookupSensor optic = new
        OpticLookupSensor(333, "optic", 0.05, 10000, 1.05, 0.8, new Duration(20, Duration.SECONDS), 0.2,
                          new Duration(30, Duration.SECONDS));


      Helo helo = new Helo(23);
      helo.setName("Merlin");
      helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      helo.setStatus(heloStat);
      helo.setDecisionModel(searchPattern);
      helo.setMovementChars(HeloMovementCharacteristics.getSampleChars());
      helo.getSensorFit().add(radar);
      helo.getSensorFit().add(optic);

      Surface fisher2 = new Surface(25);
      fisher2.setName("Fisher2");
      fisher2.setCategory(new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
      fisher2.setStatus(fisherStat);
      fisher2.setDecisionModel(fishWander);
      fisher2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

      CoreScenario cs = new CoreScenario();
      cs.setScenarioStepTime(5000);
      cs.addParticipant(helo.getId(), helo);
      cs.addParticipant(fisher2.getId(), fisher2);


      // ok.  just do a couple of steps, to check how things pan out.
      cs.step();

      System.out.println("started at:" + cs.getTime());

      DebriefReplayObserver dro = new DebriefReplayObserver("./test_reports/", "investigate_search.rep", false, true, true, null, "plotter", true);
      TrackPlotObserver tpo = new TrackPlotObserver("./test_reports/", 300, 300, "investigate_search.png", null, false, true, false, "tester", true);
      dro.setup(cs);
      tpo.setup(cs);
      //
      dro.outputThisArea(theArea);

      // now run through to completion
      int counter = 0;
      while ((cs.getTime() < 12000000) && (investigate._currentTarget == null))
      {
        cs.step();
        counter++;
      }

      // so, we should have found our tartget
      assertNotNull("found target", investigate._currentTarget);

      // ok. we've found it.  check that we do transition to detected
      counter = 0;
      while ((counter++ < 100) && (investigate._currentTarget != null))
      {
        cs.step();
      }


      dro.tearDown(cs);
      tpo.tearDown(cs);

      // so, we should have cleared our tartget
      assertNull("found target", investigate._currentTarget);
      assertEquals("remembered contact", 1, investigate._targetsDone.size(), 0);


    }


    public void testRunningMultipleTargets()
    {

      // initialise the random genny
      ASSET.Util.RandomGenerator.seed(12);

      final WorldLocation topLeft = SupportTesting.createLocation(0, 0);
      topLeft.setDepth(-1000);
      final WorldLocation bottomRight = SupportTesting.createLocation(new WorldDistance(40, WorldDistance.NM),
                                                                      new WorldDistance(40, WorldDistance.NM));
      bottomRight.setDepth(1000);
      final WorldArea theArea = new WorldArea(topLeft, bottomRight);


      ASSET.Models.Decision.Tactical.LadderSearch ladder = new LadderSearch(90.0, new Integer(4), new WorldLocation(0, 0, 0), new WorldDistance(10, WorldDistance.NM),
                                                                            new WorldDistance(30, WorldDistance.NM),
                                                                            new WorldDistance(500, WorldDistance.FT), null, "Force Protection Ladder");


      final RectangleWander heloWander = new RectangleWander(theArea, "rect wander");
      heloWander.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));
      heloWander.setHeight(new WorldDistance(800, WorldDistance.METRES));
      final RectangleWander fishWander = new RectangleWander(theArea, "rect wander 2");

      Waterfall searchPattern = new Waterfall();
      searchPattern.setName("Searching");

      TargetType theTarget = new TargetType(Category.Force.RED);
      WorldDistance detectionHeight = new WorldDistance(100, WorldDistance.FT);
      Investigate investigate = new Investigate("investigating red targets", theTarget, DetectionEvent.IDENTIFIED,
                                                detectionHeight);

      searchPattern.insertAtHead(investigate);
      searchPattern.insertAtFoot(ladder);

      final Status heloStat = new Status(1, 10);
      heloStat.setLocation(SupportTesting.createLocation(2000, 4000));
      heloStat.getLocation().setDepth(-MWC.Algorithms.Conversions.ft2m(900));
      heloStat.setCourse(270);
      heloStat.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));

      // get the helo sensors ready
      // ok. the radar first
      ASSET.Models.Sensor.Lookup.RadarLookupSensor radar = new
        RadarLookupSensor(12, "radar", 0.04, 5000, 1.2, 0, new Duration(0, Duration.SECONDS),
                          0, new Duration(0, Duration.SECONDS), 9200);

      ASSET.Models.Sensor.Lookup.OpticLookupSensor optic = new
        OpticLookupSensor(333, "optic", 0.05, 5000, 1.05, 0.8, new Duration(20, Duration.SECONDS), 0.2,
                          new Duration(30, Duration.SECONDS));


      Helo helo = new Helo(93);
      helo.setName("Merlin");
      helo.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      helo.setStatus(heloStat);
      helo.setDecisionModel(searchPattern);
      helo.setMovementChars(HeloMovementCharacteristics.getSampleChars());
      helo.getSensorFit().add(radar);
      helo.getSensorFit().add(optic);

      //////////////////////////////////////////////////
      // scenario
      //////////////////////////////////////////////////

      CoreScenario cs = new CoreScenario();
      cs.setScenarioStepTime(5000);
      cs.setSeed(new Integer(251));
      cs.addParticipant(helo.getId(), helo);

      //////////////////////////////////////////////////
      // fishing vessels
      //////////////////////////////////////////////////


      // create the fishing vessels
      for (int i = 0; i < 6; i++)
      {
        int my_id = 10 + i;
        Surface fisher2 = new Surface(my_id);
        fisher2.setName("Fisher_" + my_id);
        fisher2.setCategory(new Category(Category.Force.RED, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
        final Status fisherStat = new Status(21, 10);
        fisherStat.setLocation(SupportTesting.createLocation(theArea));
        fisherStat.setCourse(RandomGenerator.nextRandom() * 360);
        fisherStat.setSpeed(new WorldSpeed(3 + RandomGenerator.nextRandom() * 5, WorldSpeed.Kts));
        fisher2.setStatus(fisherStat);
        fisher2.setDecisionModel(fishWander);
        fisher2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
        //      if(i==5)
        cs.addParticipant(fisher2.getId(), fisher2);
      }


      // also add some duff targets
      Surface fisher3 = new Surface(123);
      fisher3.setName("Safe_fisher_1");
      fisher3.setCategory(new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
      Status fisherStat = new Status(21, 10);
      fisherStat.setLocation(SupportTesting.createLocation(ASSET.Util.RandomGenerator.nextRandom() * 20000,
                                                           ASSET.Util.RandomGenerator.nextRandom() * 20000));
      fisherStat.setCourse(RandomGenerator.nextRandom() * 360);
      fisherStat.setSpeed(new WorldSpeed(3 + RandomGenerator.nextRandom() * 5, WorldSpeed.Kts));
      fisher3.setStatus(fisherStat);
      fisher3.setDecisionModel(fishWander);
      fisher3.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
      //  cs.addParticipant(fisher3.getId(), fisher3);

      fisher3 = new Surface(124);
      fisher3.setName("Safe_fisher_2");
      fisher3.setCategory(new Category(Category.Force.GREEN, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
      fisherStat = new Status(21, 10);
      fisherStat.setLocation(SupportTesting.createLocation(ASSET.Util.RandomGenerator.nextRandom() * 20000,
                                                           ASSET.Util.RandomGenerator.nextRandom() * 20000));
      fisherStat.setCourse(RandomGenerator.nextRandom() * 360);
      fisherStat.setSpeed(new WorldSpeed(3 + RandomGenerator.nextRandom() * 5, WorldSpeed.Kts));
      fisher3.setStatus(fisherStat);
      fisher3.setDecisionModel(fishWander);
      fisher3.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
      // cs.addParticipant(fisher3.getId(), fisher3);


      // ok.  just do a couple of steps, to check how things pan out.
      cs.step();

      System.out.println("started at:" + cs.getTime());

      // and the other observers
      DebriefReplayObserver dro = new DebriefReplayObserver("test_reports", "investigate_tracks.rep",
                                                            false, false, true, null, "plotter", true);
      DebriefReplayObserver dr2 = new DebriefReplayObserver("test_reports", "investigate_helo_decisions.rep",
                                                            false, true, false, new TargetType(Category.Type.HELO), "decisions", true);
      TrackPlotObserver tpo = new TrackPlotObserver("test_reports", 300, 400, "investigate_mult_search.png",
                                                    null, false, true, false, "tester", true);

      dro.setup(cs);
      dr2.setup(cs);
      tpo.setup(cs);

      dro.outputThisArea(theArea);
      dro.outputTheseLocations(ladder.getRoute());

      // ok. we've found it.  check that we do transition to detected
      int counter = 0;
      while ((counter++ < 1200))
      {
        cs.step();
      }


      dro.tearDown(cs);
      dr2.tearDown(cs);
      tpo.tearDown(cs);

      // hack: suspect we shouldn't be allowing relaxed number of remembered contacts.
      assertEquals("remembered contacts", 5, investigate._targetsDone.size(), 1);

      // check we've found one or more of correct targets
      assertTrue("we haven't found hostile target", investigate._targetsDone.contains(new Integer(15)));
      assertTrue("we haven't found hostile target", investigate._targetsDone.contains(new Integer(10)));
    //  assertTrue("we haven't found hostile target", investigate._targetsDone.contains(new Integer(11)));

      // also check that we haven't detected the friendly targets
      assertTrue("we've found friendly target", !investigate._targetsDone.contains(new Integer(123)));

      // so, we should have cleared our tartget
      assertNull("still have link to target", investigate._currentTarget);


    }


    //    public void testReadFromCommandLine()
    //    {
    //      String test_path = "../src/java/ASSET_SRC/ASSET/Util/MonteCarlo/";
    //      String scen_file = test_path + "test_variance_scenario_area.xml";
    //      String var_file = test_path + "test_variance_area.xml";
    //
    //      CommandLine.main(new String[]{" " + scen_file, var_file});
    //    }
    //

  }

}