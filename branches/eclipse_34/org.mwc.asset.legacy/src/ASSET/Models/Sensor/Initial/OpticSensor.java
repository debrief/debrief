package ASSET.Models.Sensor.Initial;

import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Mediums.Optic;
import ASSET.Models.Sensor.SensorList;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class OpticSensor extends ASSET.Models.Sensor.Initial.InitialSensor
{

  ////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * the loss we return when there's no line of sight
   */
  private static final double _LOSS_NOT_IN_LINE_OF_SIGHT = 6000d;

  /**
   * the loss we return when there's line of sight
   */
  private static final double _LOSS_IN_LINE_OF_SIGHT = 5000d;

  /**
   * the offset we apply to the loss to allow close, small things to be seen
   */
  private static final double _CLOSE_OFFSET = 700d;

  /**
   * the minimum Height at which optic sensors may be used (which may be overridden by specific vessels)
   */
  private WorldDistance _minHeight = new WorldDistance(-20d, WorldDistance.METRES);

  /**
   * the minimum Height at which target may be detected by an optic sensor (on the surface
   * or at PD)
   */
  final private WorldDistance _minDetectableHeight = new WorldDistance(-20d, WorldDistance.METRES);

  ///////////////////////////////////
  // member variables
  //////////////////////////////////

  public OpticSensor(final int id)
  {
    super(id, "Optic");
  }

  public int getMedium()
  {
    return EnvironmentType.VISUAL;
  }


  /**
   * does this sensor return the course of the target?
   */
  public boolean hasTgtCourse()
  {
    return true;
  }

  /**
   * does this sensor return the speed of the target?
   */
  public boolean hasTgtSpeed()
  {
    return true;
  }


  public WorldDistance getMinHeight()
  {
    return _minHeight;
  }

  public void setMinHeight(final WorldDistance val)
  {
    _minHeight = val;
  }

  /**
   * Whether this sensor cannot be used to positively identify a target
   */
  public boolean canIdentifyTarget()
  {
    return true;
  }


  // allow an 'overview' test, just to check if it is worth all of the above processing
  protected boolean canDetectThisType(final ASSET.ParticipantType ownship,
                                      final ASSET.ParticipantType other, EnvironmentType env)
  {
    boolean res = false;

    // are we on surface?
    final double ourHeight = -ownship.getStatus().getLocation().getDepth();

    // is he on the surface
    final double hisHeight = -other.getStatus().getLocation().getDepth();

    if ((ourHeight < _minHeight.getValueIn(WorldDistance.METRES)) || (hisHeight < _minDetectableHeight.getValueIn(WorldDistance.METRES)))
    {
      // no, we're too deep
      res = false;
    }
    else
    {
      // see if the target radiates this
      res = other.radiatesThisNoise(getMedium());
    }

    return res;
  }


  protected double getDI(double courseDegs,
                         double absBearingDegs)
  {
    final double res = 300;

    return res;
  }

  /**
   * the estimated range for a detection of this type (where applicable)
   */
  public WorldDistance getEstimatedRange()
  {
    return new WorldDistance(5, WorldDistance.NM);
  }

  protected double getLoss(ASSET.Models.Environment.EnvironmentType environment,
                           final MWC.GenericData.WorldLocation target,
                           final MWC.GenericData.WorldLocation host)
  {

    /* todo create optic medium, and get the loss from there
     */

    double rng = target.subtract(host).getRange();
    rng = MWC.Algorithms.Conversions.Degs2m(rng);
    double res = 0;

    // get the depth of the sensor/participant, converting them to altitude
    double hostHt = host.getDepth() * -1d;
    double tgtHt = target.getDepth() * -1d;

    // trim either to 2m, if they are -ve (referring to periscope)
    hostHt = Math.max(2, hostHt);
    tgtHt = Math.max(2, tgtHt);

    // to meet requirements of equation
    // perform equation:
    // Range(nm) = 2.22790993470682 * (sqrt(hostHt) + sqrt(tgtHt)

    // calc range at these heights
    final double maxRangeMetres = Math.sqrt(2 * (hostHt + tgtHt) * 6371229);

    if (rng < maxRangeMetres)
    {
      // so, what's our proportion of the range?
      double propRange = rng / maxRangeMetres;

      // calc our proportion of the max loss
      double ourLoss = propRange * _LOSS_IN_LINE_OF_SIGHT;

      // subtract the offset to allow close, small things to be seen
      ourLoss -= _CLOSE_OFFSET;

      // hey, just check we're not returning a -ve loss
      ourLoss = Math.max(ourLoss, 0);

      // ok, done
      res = ourLoss;
    }
    else
    {
      res = _LOSS_NOT_IN_LINE_OF_SIGHT;
    }

    return res;
  }

  protected double getOSNoise(ASSET.ParticipantType target,
                              final double absBearingDegs)
  {
    // we don't consider ownship noise in optic detections, so always return zero
    // (it doesn't matter how large we are when we're looking for something else)
    return 0;
  }


  protected double getRD(ASSET.ParticipantType host, ASSET.ParticipantType target)
  {
    return 2;
  }

  protected double getTgtNoise(ASSET.ParticipantType target,
                               final double absBearingDegs)
  {
    return target.getRadiatedNoiseFor(getMedium(), absBearingDegs);
  }

  protected double getBkgndNoise(ASSET.Models.Environment.EnvironmentType environment,
                                 MWC.GenericData.WorldLocation host, double absBearingDegs)
  {
    return 0;
  }

  /**
   * over-ride the default get location method, since we return the actual altitude of the
   * object, plus half of it's height value
   *
   * @param participant the object we're looking at
   * @return it's height
   */
  protected WorldLocation getLocationFor(ASSET.ParticipantType participant)
  {

    ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rads = participant.getRadiatedChars();
    Optic opticNoise = (Optic) rads.getMedium(new Integer(EnvironmentType.VISUAL));

    double height;

    // introduce special handling for where a participant doesn't actually radiate
    // optic noise itself - so just return the height of the object
    if (opticNoise == null)
    {
      // no - this one doesn't rad optic - guess it's an SSN
      height = -participant.getStatus().getLocation().getDepth();
    }
    else
    {
      height = opticNoise.getHeight().getValueIn(WorldDistance.METRES);
    }

    // get it's actual location
    WorldLocation theLoc = participant.getStatus().getLocation();
    WorldLocation newLoc = theLoc.add(new WorldVector(0, 0, -height / 2));

    // done
    return newLoc;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: OpticSensor.java,v $
   * Revision 1.3  2006/09/21 12:20:42  Ian.Mayo
   * Reflect introduction of default names
   *
   * Revision 1.2  2006/08/31 14:34:09  Ian.Mayo
   * Undeprecate old models = we'll no longer rely on lookup sensors
   *
   * Revision 1.1  2006/08/08 14:21:56  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:26:04  Ian.Mayo
   * First versions
   *
   * Revision 1.9  2004/11/03 15:42:08  Ian.Mayo
   * More support for MAD sensors, better use of canDetectThis method
   *
   * Revision 1.8  2004/10/18 13:42:37  Ian.Mayo
   * Mark as deprecated - we will use lookup model for optic detections
   * <p/>
   * Revision 1.7  2004/10/14 13:38:52  Ian.Mayo
   * Refactor listening to sensors - so that we can listen to a sensor & it's detections in the same way that we can listen to a participant
   * <p/>
   * Revision 1.6  2004/09/06 14:20:07  Ian.Mayo
   * Provide default icons & properties for sensors
   * <p/>
   * Revision 1.5  2004/08/31 09:36:57  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.4  2004/08/26 17:05:38  Ian.Mayo
   * Implement more editable properties
   * <p/>
   * Revision 1.3  2004/08/25 11:21:11  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.2  2004/05/24 15:06:23  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.2  2004/03/25 22:46:55  ian
   * Reflect new simple environment constructor
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:54  ian
   * no message
   * <p/>
   * Revision 1.1  2004/02/16 13:41:41  Ian.Mayo
   * Renamed class structure
   * <p/>
   * Revision 1.6  2003/11/05 09:19:09  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  ////////////////////////////////////////////////////
  // editor support
  ////////////////////////////////////////////////////
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
      _myEditor = new OpticInfo(this);

    return _myEditor;
  }


  ////////////////////////////////////////////////////
  // the editor object
  ////////////////////////////////////////////////////
  static public class OpticInfo extends BaseSensorInfo
  {
    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public OpticInfo(final OpticSensor data)
    {
      super(data);
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
          prop("Name", "the name of this optic sensor"),
          prop("Working", "whether this sensor is in use"),
          prop("MinHeight", "the minimum Height at which this sensor is active"),
        };
        return res;
      }
      catch (java.beans.IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }

  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class OpticTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public OpticTest(final String val)
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
      return new OpticSensor(12);
    }

    public void testOpticSensor()
    {

      // reset the earth model
      WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

      // ok, create the sensor
      OpticSensor os = new OpticSensor(12);

      // now the objects
      WorldLocation l1 = new WorldLocation(0, 0, 0);
      WorldLocation l2 = l1.add(new WorldVector(0, MWC.Algorithms.Conversions.m2Degs(2000), 0));

      ASSET.Models.Vessels.Surface su = new ASSET.Models.Vessels.Surface(12);
      Status theStat = new Status(12, 0);
      theStat.setLocation(l1);
      theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      su.setStatus(theStat);
      SensorList sl = new SensorList();
      sl.add(os);

      ASSET.Models.Vessels.SSN ssn = new ASSET.Models.Vessels.SSN(14);
      Status otherStat = new Status(theStat);
      otherStat.setLocation(l2);
      otherStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rc = new
        ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
      Optic opticRadNoise = new Optic(2, new WorldDistance(2, WorldDistance.METRES));
      rc.add(EnvironmentType.VISUAL, opticRadNoise);
      ssn.setRadiatedChars(rc);
      ssn.setStatus(otherStat);

      EnvironmentType env = new SimpleEnvironment(1, 1, 1);

      // first check the components
      assertTrue("knows what target is", os.canIdentifyTarget());

      // can we do detection when we're dived
      su.getStatus().getLocation().setDepth(40);
      assertTrue("we can't do detection when we're dived", !os.canDetectThisType(su, ssn, env));
      su.getStatus().getLocation().setDepth(0);
      assertTrue("we can do detection when we're dived", os.canDetectThisType(su, ssn, env));
      ssn.getStatus().getLocation().setDepth(40);
      assertTrue("we can't do detection when he's dived", !os.canDetectThisType(su, ssn, env));

      // put us both back on the surface
      ssn.getStatus().getLocation().setDepth(0);

      // find the loss
      double loss = os.getLoss(env, os.getLocationFor(ssn), os.getLocationFor(su));
      assertTrue("we are in contact when close", OpticSensor._LOSS_IN_LINE_OF_SIGHT > loss);
      assertTrue("we are in contact when close", 0 <= loss);

      // put them further away
      l2 = new WorldLocation(su.getStatus().getLocation().add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(19), 0)));
      ssn.getStatus().setLocation(l2);
      loss = os.getLoss(env, os.getLocationFor(ssn), os.getLocationFor(su));
      assertEquals("we not are in contact when further away, loss:" + loss, OpticSensor._LOSS_NOT_IN_LINE_OF_SIGHT, loss, 0.001);

      // put it a bit higher
      l2 = new WorldLocation(su.getStatus().getLocation().add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(19), -80)));
      su.getStatus().setLocation(l2);
      loss = os.getLoss(env, os.getLocationFor(ssn), os.getLocationFor(su));
      assertTrue("we are back in contact when higher up", loss < OpticSensor._LOSS_IN_LINE_OF_SIGHT);

      // ok, we're in range - now start looking at the rad noise
      double radNoise = os.getTgtNoise(ssn, 0);
      assertEquals("check we've got the correct rad noise", 2, radNoise, 0.01);

      // just check he's not transmitting it when he's deep
      ssn.getStatus().getLocation().setDepth(100);
      radNoise = os.getTgtNoise(ssn, 0);
      assertEquals("check he's not trasnmitting when deep", 0, radNoise, 0.01);

      // back to normal
      su.getStatus().setLocation(new WorldLocation(0, 0, 0));
      ssn.getStatus().setLocation(su.getStatus().getLocation().add(new WorldVector(0, MWC.Algorithms.Conversions.m2Degs(500), 12)));

      // ok, do the detection
      DetectionEvent de = os.detectThis(env, su, ssn, 200, null);

      assertNotNull("Valid detection produced", de);


    }
  }
}