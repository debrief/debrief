package ASSET.Models.Sensor.Lookup;

import java.io.*;
import java.util.Vector;

import ASSET.*;
import ASSET.Models.SensorType;
import ASSET.Models.Detection.*;
import ASSET.Models.Environment.*;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.*;
import ASSET.Participants.*;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 14-Jan-2004
 * Time: 21:37:30
 * To change this template use Options | File Templates.
 */
public class RadarLookupSensor extends LookupSensor
{


  ////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
   * list of integer values detailing a relative bearing
   */
  public static final int DEAD_AHEAD = 0;
  public static final int BOW = 1;
  public static final int BEAM = 2;
  public static final int QUARTER = 3;
  public static final int ASTERN = 4;

  // the value of K for this sensor
  private double _k;

  /**
   * our default environment, when necessary.
   */
  private RadarEnvironment _defaultLookups;

  ////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /**
   * constructor
   *
   * @param VDR  variability in detection range
   * @param TBDO time between detection opportunities (millis)
   * @param MRF  maximum range factor
   * @param CRF  classification range factor
   * @param CTP  classification time period
   * @param IRF  identification range factor
   * @param ITP  identification time period
   */
  public RadarLookupSensor(int id, String myName,
                           double VDR, long TBDO, double MRF, double CRF, Duration CTP, double IRF, Duration ITP,
                           double K)
  {
    super(id, myName, VDR, TBDO, MRF, CRF, CTP, IRF, ITP, "Radar Lookup");

    // and produce the lookup tables
    setDefaultLookups();

    _k = K;


  }

  private void setDefaultLookups()
  {

    // start off with the aspect dependency 
  	Vector<NamedList> aspects = new Vector<NamedList>(0,1);
//  	datums.add(new Double())

  	aspects.add(new NamedList(Category.Type.CARRIER, new double[]{2000, 8000, 10000, 8000, 2000}));
  	aspects.add( new NamedList(Category.Type.FRIGATE, new double[]{1000, 3000, 4000, 3000, 1000}));
  	aspects.add(new NamedList(Category.Type.SUBMARINE, new double[]{0.5, 0.5, 0.5, 0.5, 0.5}));
  	aspects.add(new NamedList(Category.Type.FISHING_VESSEL, new double[]{5,8,10,8,5}));
  	
    // setup the sigma values
    IntegerTargetTypeLookup sigmaValues = new IntegerTargetTypeLookup(aspects, new Double(1000d));

    // start off with the sea state
  	Vector<NamedList> states = new Vector<NamedList>(0,1);

  	states.add(new NamedList(Category.Type.FRIGATE, new double[]{1.00, 1.00, 1.00, 1.00, 1.00, 0.95, 0.90}));
  	states.add(new NamedList(Category.Type.CARRIER, new double[]{1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 0.95}));
  	states.add(new NamedList(Category.Type.SUBMARINE, new double[]{1.00, 1.00, 0.80, 0.75, 0.70, 0.50, 0.30}));
  	states.add(new NamedList(Category.Type.FISHING_VESSEL, new double[]{1.00, 1.00, 0.96, 0.80, 0.75, 0.70, 0.50, 0.90}));
    

  IntegerTargetTypeLookup seaStates = new IntegerTargetTypeLookup(states, new Double(1.0));
  	
//  	
//  	
//    IntegerTargetTypeLookup seaStates = new IntegerTargetTypeLookup(new int[]{0, 1, 2, 3, 4, 5, 6},
//                                                                    new StringLookup[]{
//                                                                      new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE, Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
//                                                                                       new double[]{1, 1, 1, 1}, null),
//                                                                      new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE, Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
//                                                                                       new double[]{1, 1, 1, 1}, null),
//                                                                      new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE, Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
//                                                                                       new double[]{1, 1, 0.8, 0.9}, null),
//                                                                      new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE, Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
//                                                                                       new double[]{1, 1, 0.75, 0.8}, null),
//                                                                      new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE, Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
//                                                                                       new double[]{1, 1, 0.7, 0.75}, null),
//                                                                      new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE, Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
//                                                                                       new double[]{0.95, 1, 0.5, 0.7}, null),
//                                                                      new StringLookup(new String[]{Category.Type.CARRIER, Category.Type.FRIGATE, Category.Type.SUBMARINE, Category.Type.FISHING_VESSEL},
//                                                                                       new double[]{0.9, 0.95, 0.3, 0.5}, null),
//                                                                    }, new Double(1.0));


    _defaultLookups = new RadarEnvironment("sample", seaStates, sigmaValues);


  }


  ////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////
  /**
   * determine the lookup parameters applicable to this sensor
   *
   * @param ownship     us
   * @param target      them
   * @param scenario    the scenario
   * @param environment the environment
   * @param time        current time
   * @return the set of lookup parameters applicable to this sensor
   */
  protected LookupSensor.LastTargetContact parametersFor(NetworkParticipant ownship,
                                                         NetworkParticipant target,
                                                         ScenarioType scenario,
                                                         EnvironmentType environment,
                                                         long time)
  {
    LookupSensor.LastTargetContact res = new LookupSensor.LastTargetContact();

    RadarEnvironment lookups = environment.getRadarEnvironment();

    // did we find one?
    if (lookups == null)
    {
      // nope, do we have defaults?
      if (_defaultLookups == null)
      {
        // nope, better create them
        setDefaultLookups();
      }

      lookups = _defaultLookups;
    }

    // what sort of target is it?
    String targetType = target.getCategory().getType();

    // first the vis attenuation
    int relAspect = determineAspectOf(ownship.getStatus().getLocation(), target.getStatus().getLocation(), target.getStatus().getCourse());
    Double sigmaVal = lookups._sigmaValues.find(relAspect, targetType);

    // now the sea-state effects
    int seaState = environment.getSeaStateFor(time, ownship.getStatus().getLocation());
    Double seaStateVal = lookups._seaStates.find(seaState, targetType);

    // and store the parameters
    res.insertElementAt(seaStateVal, SEA_STATE_INDEX);
    res.insertElementAt(sigmaVal, SIGMA_VAL);

    // done
    return res;
  }


  private final static int SEA_STATE_INDEX = 0;
  private final static int SIGMA_VAL = 1;

  /**
   * calculate the predicted range for this contact
   *
   * @param ownship
   * @param target
   * @param scenario
   * @param environment
   * @return
   */
  protected WorldDistance calculateRP(NetworkParticipant ownship, NetworkParticipant target,
                                      ScenarioType scenario, EnvironmentType environment,
                                      long time, LookupSensor.LastTargetContact params)
  {
    WorldDistance res = null;

    // retrieve our parameters
    Double seaStateVal = (Double) params.get(SEA_STATE_INDEX);
    Double sigmaVal = (Double) params.get(SIGMA_VAL);

    // find out the ownship and target height
    final double EARTH_RADIUS = 6371950;
    final double B_FACTOR = 0.7366; // is back calculated from the radar horizon formula in the Merlin Tacman
    double myHeight = -ownship.getStatus().getLocation().getDepth();
    double hisHeight = -target.getStatus().getLocation().getDepth();

    // do the calc
    double RP_1 = Math.sqrt(2 * EARTH_RADIUS * myHeight / B_FACTOR) + Math.sqrt(2 * EARTH_RADIUS * hisHeight / B_FACTOR);

    // and now for the second range calc
    double RP_2 = _k * Math.pow(sigmaVal.doubleValue() * seaStateVal.doubleValue(), 0.25);

    // and use the min value.
    double RP = Math.min(RP_1, RP_2);

    // done.
    res = new WorldDistance(RP, WorldDistance.METRES);

    return res;
  }

  public boolean canIdentifyTarget()
  {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }


  /**
   * examine the relative geometries of the two participants, return the aspect setting
   *
   * @param ownship
   * @param target
   * @param targetCourseDegs
   * @return
   */
  protected static int determineAspectOf(WorldLocation ownship, WorldLocation target, double targetCourseDegs)
  {
    // ok, what's the ATB
    double ATB = calcAspect(ownship, target, targetCourseDegs);

    // trim to absolue
    ATB = Math.abs(ATB);

    int res = 0;

    if (ATB < 22.5)
      res = DEAD_AHEAD;
    else if (ATB < 67.5)
      res = BOW;
    else if (ATB < 112.5)
      res = BEAM;
    else if (ATB < 157.5)
      res = QUARTER;
    else
      res = ASTERN;

    return res;
  }


  /**
   * calculate the angle-on-the-bow for the target ship compared to ownship
   *
   * @param ownship   our location
   * @param target    his location
   * @param hisCourse his course (degs)
   * @return ATB (degs)
   */
  protected static double calcAspect(WorldLocation ownship, WorldLocation target, double hisCourse)
  {
    double hisRel = ownship.subtract(target).getBearing();
    hisRel = MWC.Algorithms.Conversions.Rads2Degs(hisRel);
    double res = hisCourse - hisRel;
    if (res > 180)
      res -= 360;
    return res;
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
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new RadarLookupInfo(this);
    return _myEditor;
  }


  /**
   * get the version details for this model.
   * <pre>
   * $Log: RadarLookupSensor.java,v $
   * Revision 1.3  2006/09/21 12:20:45  Ian.Mayo
   * Reflect introduction of default names
   *
   * Revision 1.2  2006/09/12 15:15:34  Ian.Mayo
   * Sorting out XML import/export & lookup data structures
   *
   * Revision 1.1  2006/08/08 14:21:58  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:26:06  Ian.Mayo
   * First versions
   *
   * Revision 1.21  2004/11/04 09:30:23  Ian.Mayo
   * Handle sensors which can't provide range/bearing
   *
   * Revision 1.20  2004/10/27 15:22:10  Ian.Mayo
   * Fall back on local lookups if environmental ones not set
   * <p/>
   * Revision 1.19  2004/10/27 15:14:13  Ian.Mayo
   * Reflect changed structure of RadarEnvironment
   * <p/>
   * Revision 1.18  2004/10/27 14:07:06  Ian.Mayo
   * Handle default values better
   * <p/>
   * Revision 1.17  2004/10/27 13:30:10  Ian.Mayo
   * More environment handling
   * <p/>
   * Revision 1.16  2004/10/26 10:21:57  Ian.Mayo
   * Move my lookups back up to Env, minor refactoring
   * <p/>
   * Revision 1.15  2004/10/25 15:30:24  Ian.Mayo
   * Start incorporating lookup data tables in environment
   * <p/>
   * Revision 1.14  2004/09/06 14:20:10  Ian.Mayo
   * Provide default icons & properties for sensors
   * <p/>
   * Revision 1.13  2004/09/06 14:04:19  Ian.Mayo
   * Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
   * <p/>
   * Revision 1.12  2004/08/31 09:37:02  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.11  2004/08/26 17:05:40  Ian.Mayo
   * Implement more editable properties
   * <p/>
   * Revision 1.10  2004/08/25 11:21:18  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.9  2004/08/23 09:12:34  Ian.Mayo
   * Update tests to reflect new detection list processing
   * <p/>
   * Revision 1.8  2004/08/23 08:06:12  Ian.Mayo
   * Implement clearing old detection lists, minor refactoring
   * <p/>
   * Revision 1.7  2004/08/20 15:08:23  Ian.Mayo
   * Part way through changing detection cycle so that it doesn't start afresh each time - each sensor removes it's previous calls the next time it is called (to allow for TBDO)
   * <p/>
   * Revision 1.6  2004/08/16 10:10:56  Ian.Mayo
   * Reflect improved sensor performance
   * <p/>
   * Revision 1.5  2004/08/12 11:09:29  Ian.Mayo
   * Respect observer classes refactored into tidy directories
   * <p/>
   * Revision 1.4  2004/08/09 15:50:47  Ian.Mayo
   * Refactor category types into Force, Environment, Type sub-classes
   * <p/>
   * Revision 1.3  2004/05/24 15:05:35  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.3  2004/04/22 21:38:19  ian
   * Use corrected algs
   * <p/>
   * Revision 1.2  2004/04/08 20:27:29  ian
   * Restructured contructor for CoreObserver
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:54  ian
   * no message
   * <p/>
   * Revision 1.2  2004/02/18 08:59:52  Ian.Mayo
   * Tidying
   * <p/>
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }

  //////////////////////////////////////////////////
  // the structure defining the environmental factors common
  // to radar sensors
  //////////////////////////////////////////////////
  public static class RadarEnvironment
  {
    public IntegerTargetTypeLookup _seaStates;
    public IntegerTargetTypeLookup _sigmaValues;
    private String _myName;

    //////////////////////////////////////////////////
    // constructor
    //////////////////////////////////////////////////
    public RadarEnvironment(String name, IntegerTargetTypeLookup seaStates,
                            IntegerTargetTypeLookup sigmaValues)
    {
      _myName = name;
      _seaStates = seaStates;
      _sigmaValues = sigmaValues;
    }

    //////////////////////////////////////////////////
    // accessors
    //////////////////////////////////////////////////
    public String getName()
    {
      return _myName;
    }

    public void setName(String name)
    {
      _myName = name;
    }

    public IntegerTargetTypeLookup getSeaStates()
    {
      return _seaStates;
    }

    public IntegerTargetTypeLookup getSigmaValues()
    {
      return _sigmaValues;
    }


  }


  ////////////////////////////////////////////////////
  // the editor object
  ////////////////////////////////////////////////////
  static public class RadarLookupInfo extends BaseSensorInfo
  {
    /**
     * @param data the Layers themselves
     */
    public RadarLookupInfo(final RadarLookupSensor data)
    {
      super(data);
    }
  }

  ////////////////////////////////////////////////////////////
  // test the optic sensor
  ////////////////////////////////////////////////////////////
  static public final class RadarLookupTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";


    public RadarLookupTest(String val)
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
      return getTestRadarSensor(12, 12, 12, 12, 12, 12);
    }

    public static RadarLookupSensor getTestRadarSensor(double rp_m, double ri_m,
                                                       double cr_m, double ir_m, double max_m, double k)
    {
      return new RadarLookupSensor(21, "radar", 0.05, 10, 1.05, 0.8, new Duration(20, Duration.SECONDS), 0.2,
                                   new Duration(30, Duration.SECONDS), k);
    }


    protected class MyEnvironment extends CoreEnvironment
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			protected int _lightLevel = 1;
      protected int _seaState = 4;
      protected int _atten = EnvironmentType.VERY_CLEAR;

      /**
       * get the light level at this location
       *
       * @param time     the time we're talking about
       * @param location the location we're talking about
       * @return the current light level
       */
      public int getLightLevelFor(long time, WorldLocation location)
      {
        return _lightLevel;
      }

      /**
       * get the atmospheric attenuation
       *
       * @param time     current time
       * @param location place to get data for
       * @return one of the atmospheric attenuation factors
       */
      public int getAtmosphericAttentuationFor(long time, WorldLocation location)
      {
        return _atten;
      }

      /**
       * get the sea state
       *
       * @param time     current time
       * @param location place to get data for
       * @return sea state, from 0 to 10
       */
      public int getSeaStateFor(long time, WorldLocation location)
      {
        return _seaState;
      }
    }

    public final void testFirst()
    {
      RadarLookupSensor ts = getTestRadarSensor(4000, 3100, 2500, 1900, 4700, 600);
      Status statA = new Status(12, 0);
      statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      Helo alpha = new Helo(12);
      alpha.setName("Alpha");
      alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE, Category.Type.HELO));
      WorldLocation originA = SupportTesting.createLocation(0, 0);
      statA.setLocation(originA);
      statA.getLocation().setDepth(-400);
      alpha.setStatus(statA);
      Surface bravo = new Surface(11);
      Status statB = new Status(12, 0);
      statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      bravo.setName("Bravo");
      bravo.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE, Category.Type.FISHING_VESSEL));
      WorldLocation originB = SupportTesting.createLocation(6000, 6000);
      statB.setLocation(originB);
      statB.getLocation().setDepth(-20);
      bravo.setStatus(statB);

      alpha.addSensor(ts);
      CoreScenario scenario = new CoreScenario();
      scenario.addParticipant(alpha.getId(), alpha);
      scenario.addParticipant(bravo.getId(), bravo);

      scenario.setScenarioStepTime(1000);

      // create our dummy environment object
      MyEnvironment env = new MyEnvironment();


      // ok, start off with us miles apart - and check we can't see each other.
      long time = 1000;
      DetectionList res = new DetectionList();
      ts.detects(env, res, alpha, scenario, time);
      assertEquals("null detections returned", 0, res.size());

      // ok. move the targets a little closer (but still out of range)
      statB.setLocation(SupportTesting.createLocation(800, 600));
      ts.detects(env, res, alpha, scenario, time);
      assertEquals("null detections returned", 0, res.size());

      // and closer still (to gain contact)
      statB.setLocation(SupportTesting.createLocation(200, 200));
      time += 1000;
      ts.detects(env, res, alpha, scenario, time);
      assertEquals("detections returned", 1, res.size());
      assertEquals("only one detection produced", 1, res.size());
      assertEquals("check detected state", DetectionEvent.DETECTED, ((LastTargetContact) ts._pastContacts.get(bravo)).getDetectionState());

      // check that the parameters have stayed the same
      statB.setLocation(SupportTesting.createLocation(200, 200));
      time += 1000;
      ts.detects(env, res, alpha, scenario, time);
      assertEquals("detections returned", 1, res.size());
      assertEquals("only one detection produced", 1, res.size());
      assertEquals("check detected state", DetectionEvent.DETECTED, ((LastTargetContact) ts._pastContacts.get(bravo)).getDetectionState());

      // stay close, move forward enough elapsed time so that we can move to classified
      time += 21000;
      ts.detects(env, res, alpha, scenario, time);
      assertNotNull("detections returned", res);
      assertEquals("only one detection produced", 1, res.size());
      assertEquals("check detected state", DetectionEvent.CLASSIFIED, ((LastTargetContact) ts._pastContacts.get(bravo)).getDetectionState());
    }


    public void testRead()
    {
      String fileName = "lookup_test_scenario.xml";
      fileName = "src/ASSET/Models/Sensor/Lookup/" + fileName;

      CoreScenario scen = new CoreScenario();
      try
      {
        FileInputStream is = new FileInputStream(fileName);
        final InputStream bi = new BufferedInputStream(is);
        ASSET.Util.XML.ASSETReaderWriter.importThis(scen,
                                                    fileName,
                                                    bi);
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }

      // ok, try to check that the data got loaded.
      Integer[] participants = scen.getListOfParticipants();

      NetworkParticipant partA = scen.getThisParticipant(participants[0].intValue());
      NetworkParticipant partB = scen.getThisParticipant(participants[1].intValue());
      NetworkParticipant part;
      if (partA instanceof Helo)
        part = partA;
      else
        part = partB;


      assertTrue("we've loaded and found the helo", part instanceof Helo);
      Helo helo = (Helo) part;
      SensorList theSensors = helo.getSensorFit();
      assertEquals("has sensors loaded", 3, theSensors.getNumSensors());
      SensorType firstSensor = theSensors.getSensor(555);
      assertNotNull("found first sensor", firstSensor);
      assertTrue(firstSensor instanceof RadarLookupSensor);
      RadarLookupSensor opticS = (RadarLookupSensor) firstSensor;

      // check the values
      assertEquals("VDR correct", 0.04, opticS.VDR, 0.0);
      assertEquals("name correct", "The radar", opticS.getName());
      assertEquals("MRF correct", 1.2, opticS.MRF, 0.0);
      assertEquals("CRF correct", 0.0, opticS.CRF, 0.0);
      assertEquals("IRF correct", 0.0, opticS.IRF, 0.0);

      assertEquals("tbdo correct", 11000, opticS.getTimeBetweenDetectionOpportunities(), 0);
      assertEquals("ctp correct", 0, opticS.CTP.getMillis(), 0);
      assertEquals("itp correct", 0, opticS.ITP.getMillis(), 0);

      helo.addParticipantDetectedListener(new ParticipantDetectedListener()
      {
        public void newDetections(DetectionList detections)
        {
          if (detections.size() > 0)
          {
            detectedOther = true;

            final DetectionEvent det = detections.getDetection(0);

            maxClassification = Math.max(maxClassification, det.getDetectionState());
          }
          else
          {
            if (detectedOther)
              targetLost = true;
          }
        }

        public void restart(ScenarioType scenario)
        {
        }
      });

      DebriefReplayObserver dro = new DebriefReplayObserver("test_reports", "radar_lookup.rep", true, "test observer", true);
      dro.setup(scen);


      // hey, let's run through it a little
      for (int i = 0; i < 900; i++)
      {
        scen.step();
      }

      dro.tearDown(scen);

      assertTrue("yes, we made a detection", detectedOther);
      assertTrue("yes, we also lost contact", targetLost);
      assertEquals("managed to classify", 2, maxClassification, 0);


    }


    public void testAspectCalcs()
    {
      WorldLocation loc1 = SupportTesting.createLocation(0, 0);
      WorldLocation loc2 = SupportTesting.createLocation(1000, 1000);

      double course = 0;
      double res = calcAspect(loc1, loc2, course);
      assertEquals("do the first one", 135, Math.abs(res), 0.001);

      course = 45;
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 180, Math.abs(res), 0.001);

      course = 90;
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 135, Math.abs(res), 0.001);

      course = 135;
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 90, Math.abs(res), 0.001);

      course = 180;
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 45, Math.abs(res), 0.001);

      course = 225;
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 0, Math.abs(res), 0.001);

      // move ownship to the east of the target
      course = 0;
      loc1 = SupportTesting.createLocation(2000, 0);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 135, Math.abs(res), 0.001);

      course = 90;
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 45, Math.abs(res), 0.001);

      course = 180;
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 45, Math.abs(res), 0.001);

      // and move to the north
      course = 0;
      loc1 = SupportTesting.createLocation(2000, 2000);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 45, Math.abs(res), 0.001);

      course = 90;
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 45, Math.abs(res), 0.001);

      course = 180;
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 135, Math.abs(res), 0.001);

      // and wierd ones
      course = 0;
      loc1 = SupportTesting.createLocation(2000, 1000);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 90, Math.abs(res), 0.001);
      assertEquals("and the aspect", BEAM, determineAspectOf(loc1, loc2, course), 0);

      loc1 = SupportTesting.createLocation(0, 1000);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 90, Math.abs(res), 0.001);

      loc1 = SupportTesting.createLocation(1000, 0);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 180, Math.abs(res), 0.001);
      assertEquals("and the aspect", ASTERN, determineAspectOf(loc1, loc2, course), 0);

      loc1 = SupportTesting.createLocation(1000, 2000);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 0, Math.abs(res), 0.001);
      assertEquals("and the aspect", DEAD_AHEAD, determineAspectOf(loc1, loc2, course), 0);

      loc1 = SupportTesting.createLocation(1200, 1400);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 26.565, Math.abs(res), 0.01);
      assertEquals("and the aspect", BOW, determineAspectOf(loc1, loc2, course), 0);

      loc1 = SupportTesting.createLocation(800, 930);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 109.29, Math.abs(res), 0.01);
      assertEquals("and the aspect", BEAM, determineAspectOf(loc1, loc2, course), 0);

      loc1 = SupportTesting.createLocation(400, 600);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 123.69, Math.abs(res), 0.01);
      assertEquals("and the aspect", QUARTER, determineAspectOf(loc1, loc2, course), 0);

      loc1 = SupportTesting.createLocation(800, 400);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 161.56, Math.abs(res), 0.01);
      assertEquals("and the aspect", ASTERN, determineAspectOf(loc1, loc2, course), 0);

      loc1 = SupportTesting.createLocation(1400, 400);
      res = calcAspect(loc1, loc2, course);
      assertEquals("do the second one", 146.30, Math.abs(res), 0.01);
      assertEquals("and the aspect", QUARTER, determineAspectOf(loc1, loc2, course), 0);


    }

    protected boolean detectedOther = false;
    protected boolean targetLost = false;
    protected int maxClassification = 0;
  }
}
