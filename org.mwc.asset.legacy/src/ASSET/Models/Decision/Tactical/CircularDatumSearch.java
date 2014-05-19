package ASSET.Models.Decision.Tactical;


/**
 * PlanetMayo Ltd.  2003
 * User: Ian Mayo
 * Date: Spring 03
 * Time: ${TIME}
 * Log:
 *  $Log: CircularDatumSearch.java,v $
 *  Revision 1.1  2006/08/08 14:21:32  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:40  Ian.Mayo
 *  First versions
 *
 *  Revision 1.9  2004/09/24 11:08:09  Ian.Mayo
 *  Tidy test names
 *
 *  Revision 1.8  2004/09/02 08:13:12  Ian.Mayo
 *  Reflect improved DetectionList utility functions
 *
 *  Revision 1.7  2004/08/31 09:36:17  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.6  2004/08/26 16:27:00  Ian.Mayo
 *  Implement editable properties
 *
 *  Revision 1.5  2004/08/25 11:20:31  Ian.Mayo
 *  Remove main methods which just run junit tests
 *
 *  Revision 1.4  2004/05/24 15:57:05  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:52  ian
 *  no message
 *
 *  Revision 1.3  2003/11/05 09:19:52  Ian.Mayo
 *  Include MWC Model support
 *
 *  Revision 1.2  2003/09/19 13:37:56  Ian.Mayo
 *  Switch to Speed and Distance objects instead of just doubles
 *
 *  Revision 1.1  2003/09/11 13:40:40  Ian.Mayo
 *  Classes moved around
 *
 *  Revision 1.8  2003/09/09 15:55:12  Ian.Mayo
 *  Change signature of decision model
 *
 *  Revision 1.7  2003/09/04 15:17:15  Ian.Mayo
 *  Switch to optional parameter values for Move
 *
 *  Revision 1.6  2003/09/04 14:33:11  Ian.Mayo
 *  Reflect new speed type for Transit
 *
 *  Revision 1.5  2003/09/04 13:28:25  Ian.Mayo
 *  minor reformatting, reflect new Wander getters/setters
 *
 *
 */

import ASSET.Models.Decision.Composite;
import ASSET.Models.Decision.Conditions.ElapsedTime;
import ASSET.Models.Decision.Movement.Move;
import ASSET.Models.Decision.Movement.Trail;
import ASSET.Models.Decision.Movement.Transit;
import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Decision.Sequence;
import ASSET.Models.Decision.TargetType;
import ASSET.Models.Decision.Waterfall;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.*;

/**
 * Behaviour representing a datum search of a particular detection.
 * It is a high-level behaviour which itself is a composite of
 * other behaviours
 */
public class CircularDatumSearch extends Sequence
{

  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the number of buoys in the circle
   */
  private int _numBuoys;

  /**
   * the type of target we're looking for
   */
  protected TargetType _theTarget;

  /**
   * the radius of the circle to drop
   */
  private WorldDistance _theRadius;

  /**
   * the type of weapon to launch
   */
  protected String _launchWeaponType;

  /**
   * the filename of the weapon
   */
  protected String _launchWeaponFileName;

  /**
   * the type of buoy to launch
   */
  protected String _launchBuoyType;

  /**
   * the filename of the buoy
   */
  protected String _launchBuoyFileName;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public CircularDatumSearch(TargetType theTarget,
                             WorldDistance theRadius,
                             int numBuoys,
                             String theLaunchWeaponType,
                             String theLaunchWeaponFileName,
                             String theLaunchBuoyType,
                             String theLaunchBuoFileName)
  {
    _theRadius = theRadius;
    _theTarget = theTarget;
    _numBuoys = numBuoys;

    // and store the create weapon/buoy information
    _launchWeaponType = theLaunchWeaponType;
    _launchWeaponFileName = theLaunchWeaponFileName;
    _launchBuoyType = theLaunchBuoyType;
    _launchBuoyFileName = theLaunchBuoFileName;
  }

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /**
   * performed the waterfalled decision, if a model does not return
   * a demanded status, we move on to the next one
   */
  public DemandedStatus decide(Status status,
                               ASSET.Models.Movement.MovementCharacteristics chars, DemandedStatus demStatus, DetectionList detections,
                               ScenarioActivityMonitor monitor,
                               long time)
  {

    DemandedStatus res;

    // are we already in a prosecution?
    if (super.getIsAlive())
    {
      // let it continue
    }
    else
    {
      // ok, see if we have detected our new target
      if (detections != null)
      {
        // does it contain our target?
        DetectionList theDets = detections.getDetectionsOf(_theTarget);


        if (theDets != null)
        {
          DetectionEvent myDetection = theDets.getMostRecentDetection();

          // hey, valid detection, build up the behaviour!!!
          double theRng = myDetection.getRange().getValueIn(WorldDistance.DEGS);
          double theBrg = MWC.Algorithms.Conversions.Degs2Rads(myDetection.getBearing().doubleValue());
          WorldLocation dest = status.getLocation().add(new WorldVector(theBrg, theRng, 0));

          // wake up our parent
          super.setIsAlive(true);

          // create the behaviours, and stick them into our parent
          createBehaviours(dest);
        }
      }
    }

    // and let the parent continue
    res = super.decide(status, chars, demStatus, detections, monitor, time);

    return res;
  }

  /**
   * setup the list of behaviours for this datum search
   *
   * @param dest the datum of the target
   */
  private void createBehaviours(WorldLocation dest)
  {
    // setup the transit
    Transit transit = new Transit();
    WorldPath path = new WorldPath();
    path.addPoint(dest);
    transit.setDestinations(path);
    transit.setName("Travel to datum");
    transit.setLoop(false);
    transit.setReverse(false);
    transit.setSpeed(new WorldSpeed(40, WorldSpeed.M_sec));
    transit.setThreshold(new WorldDistance(1200, WorldDistance.YARDS));

    // lay the buoy field
    Sequence createBuoys = createBuoyField(_launchBuoyFileName, _launchBuoyType);

    // create the launch behaviour
    LaunchWeapon lw = new LaunchWeapon();
    lw.setLaunchFilename(_launchWeaponFileName);
    lw.setLaunchType(_launchWeaponType);
    lw.setTargetType(_theTarget);
    lw.setCoolOffTime(new Duration(30, Duration.MINUTES));
    lw.setLaunchRange(new WorldDistance(1, WorldDistance.NM));
    lw.setName("launch weapon");

    // and the trail behaviour
    Trail trailIt = new Trail(new WorldDistance(1, WorldDistance.NM));
    trailIt.setTargetType(_theTarget);
    trailIt.setStayOnBearingTime(new Duration(5, Duration.MINUTES));
    trailIt.setName("trail target");
    trailIt.setAllowableError(new WorldDistance(0, WorldDistance.NM));

    // and the wander behaviour
    WorldDistance wanderRadius =
      new WorldDistance(_theRadius.getValueIn(WorldDistance.METRES) * 2, WorldDistance.METRES);
    Wander wander = new Wander(dest, wanderRadius);
    wander.setSpeed(new WorldSpeed(40, WorldSpeed.M_sec));

    // how long do we wander for?
    ElapsedTime wanderPeriod = new ElapsedTime(new Duration(30, Duration.MINUTES));
    Composite timedWander = new Composite(wanderPeriod, wander);
    timedWander.setName("Loiter on Datum");

    // now the prosecute behaviour
    Waterfall prosecuteTarget = new Waterfall();
    prosecuteTarget.setName("Prosecute");

    prosecuteTarget.insertAtFoot(lw);
    prosecuteTarget.insertAtFoot(trailIt);
    prosecuteTarget.insertAtFoot(timedWander);

    // collate them all
    this.insertAtHead(transit);
    this.insertAtFoot(createBuoys);
    this.insertAtFoot(prosecuteTarget);
  }


  /**
   * lay the buoyfield itself
   */
  protected Sequence createBuoyField(final String buoyName, final String buoyType)
  {
    Sequence res = new Sequence();
    res.setName("Lay buoy field");

    // move to the first buoy
    Move move = new Move();
    move.setName("North to first point");
    move.setDistance(_theRadius);
    move.setCourse(new Double(0));
    move.setSpeed(new WorldSpeed(30, WorldSpeed.M_sec));
    res.insertAtFoot(move);

    // calculate the spread of buoys
    double radMetres = _theRadius.getValueIn(WorldDistance.METRES);
    double internalAngle = 2 * Math.PI / _numBuoys;

    // and now half of the outside length
    double len = Math.sin(internalAngle / 2) * radMetres;

    // double it to get the full length of the outer edge of the pit
    double legSize = len * 2;
    WorldDistance theLeg = new WorldDistance(legSize, WorldDistance.METRES);

    // convert the internal angle to degs
    internalAngle = MWC.Algorithms.Conversions.Rads2Degs(internalAngle);

    // find out how far around the circle we move at each step
    double directionDelta = internalAngle;

    // and produce the starting vector (half-way through the first pie)
    double thisDirection = internalAngle / 2;

    // drop the first buoy
    LaunchSensor ls = new LaunchSensor();
    ls.setStayAlive(false);
    ls.setName("Launch buoy:" + 1);
    res.insertAtFoot(ls);

    // now step through the buoys
    for (int thisSlice = 0; thisSlice < _numBuoys - 1; thisSlice++)
    {
      // the course to the next buoy location
      double thisCourse = thisDirection + 90;

      // create this transit
      move = new Move();
      move.setName("Move to drop buoy:" + (thisSlice + 2));
      move.setDistance(theLeg);
      move.setCourse(new Double(thisCourse));
      move.setSpeed(new WorldSpeed(30, WorldSpeed.M_sec));
      res.insertAtFoot(move);

      // drop the buoy here
      ls = new LaunchSensor();
      ls.setStayAlive(false);
      ls.setName("Launch buoy:" + (thisSlice + 1));
      res.insertAtFoot(ls);

      // what's the course for the next leg?
      thisDirection += directionDelta;
    }

    return res;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: CircularDatumSearch.java,v $
   * Revision 1.1  2006/08/08 14:21:32  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:40  Ian.Mayo
   * First versions
   *
   * Revision 1.9  2004/09/24 11:08:09  Ian.Mayo
   * Tidy test names
   *
   * Revision 1.8  2004/09/02 08:13:12  Ian.Mayo
   * Reflect improved DetectionList utility functions
   * <p/>
   * Revision 1.7  2004/08/31 09:36:17  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.6  2004/08/26 16:27:00  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.5  2004/08/25 11:20:31  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.4  2004/05/24 15:57:05  Ian.Mayo
   * Commit updates from home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:52  ian
   * no message
   * <p/>
   * Revision 1.3  2003/11/05 09:19:52  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  /**
   * **********************************************************************
   * test this class
   * **********************************************************************
   */

  public static class CircSearchTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public CircSearchTest(final String name)
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

    public void testCreateBuoys()
    {
      // ok, create the search
      CircularDatumSearch cs = new CircularDatumSearch(null, new WorldDistance(1, WorldDistance.KM), 6,
                                                       null, null, null, null);

      Sequence field = cs.createBuoyField(null, null);

      // check we created the correct amount (one more than number dropped, since it includes the transit)
      assertEquals("correct num buoys created", field.getModels().size(), 12);

    }
  }
}
