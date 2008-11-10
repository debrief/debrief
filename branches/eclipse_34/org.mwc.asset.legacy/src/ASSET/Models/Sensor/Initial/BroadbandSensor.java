package ASSET.Models.Sensor.Initial;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class BroadbandSensor extends InitialSensor
{

  ///////////////////////////////////
  // member variables
  //////////////////////////////////
  /**
   * the detection aperture in this sensor
   */
  private double _myDetectionAperture = 120;


  /**
   * *************************************************
   * constructor
   * *************************************************
   */
  public BroadbandSensor(final int id)
  {
    super(id, "BB");
  }

  public BroadbandSensor(final int id, final String defaultName)
  {
    super(id, defaultName);
  }

  public int getMedium()
  {
    return EnvironmentType.BROADBAND_PASSIVE;
  }

  public double getDetectionAperture()
  {
    return _myDetectionAperture;
  }

  public void setDetectionAperture(final double val)
  {
    _myDetectionAperture = val;
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
      _myEditor = new BroadbandInfo(this);

    return _myEditor;
  }

  /**
   * Whether this sensor cannot be used to positively identify a target
   */
  public boolean canIdentifyTarget()
  {
    return false;
  }

  // allow an 'overview' test, just to check if it is worth all of the above processing
  protected boolean canDetectThisType(ASSET.ParticipantType ownship,
                                      final ASSET.ParticipantType other, EnvironmentType env)
  {
    return other.radiatesThisNoise(getMedium());
  }


  protected double getDI(final double courseDegs,
                         final double absBearingDegs)
  {
    //  double res = 74;
    double res = 34; // URICK page 43, for cylindrical sensor 60 inches diameter @ 15Khz

    // convert the bearing to relative bearing
    double relBrg = absBearingDegs - courseDegs;

    // check it's now going round the long way
    if (relBrg > 180)
      relBrg -= 360;

    if (relBrg < -180)
      relBrg += 360;

    if (Math.abs(relBrg) > _myDetectionAperture)
    {
      res = 00;
    }

    return res;
  }

  protected double getLoss(ASSET.Models.Environment.EnvironmentType environment,
                           final MWC.GenericData.WorldLocation target,
                           final MWC.GenericData.WorldLocation host)
  {
    double res = 0;

    // use the environment to determine the loss
    res = environment.getLossBetween(EnvironmentType.BROADBAND_PASSIVE, target, host);

    res = -200;
    
    return res;
  }

  protected double getOSNoise(final ASSET.ParticipantType ownship, final double absBearingDegs)
  {
    return ownship.getSelfNoiseFor(getMedium(), absBearingDegs);
  }

  /**
   * the estimated range for a detection of this type (where applicable)
   */
  public WorldDistance getEstimatedRange()
  {
    return new WorldDistance(1000, WorldDistance.YARDS);
  }

  protected double getRD(ASSET.ParticipantType host, ASSET.ParticipantType target)
  {
    return 2;
  }

  protected double getTgtNoise(final ASSET.ParticipantType target, final double absBearingDegs)
  {
    return target.getRadiatedNoiseFor(getMedium(), absBearingDegs);
  }

  protected double getBkgndNoise(ASSET.Models.Environment.EnvironmentType environment,
                                 MWC.GenericData.WorldLocation host, double absBearingDegs)
  {

    double res;

    // use the environment to determine the loss
    res = environment.getBkgndNoise(EnvironmentType.BROADBAND_PASSIVE, host, absBearingDegs);

    return res;
  }

  /**
   * does this sensor return the course of the target?
   */
  public boolean hasTgtCourse()
  {
    return true;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: BroadbandSensor.java,v $
   * Revision 1.3  2006/11/06 16:08:51  Ian.Mayo
   * Hard-code detection range so we get immediate contact
   *
   * Revision 1.2  2006/09/21 12:20:41  Ian.Mayo
   * Reflect introduction of default names
   *
   * Revision 1.1  2006/08/08 14:21:54  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:26:02  Ian.Mayo
   * First versions
   *
   * Revision 1.8  2004/11/03 15:42:07  Ian.Mayo
   * More support for MAD sensors, better use of canDetectThis method
   *
   * Revision 1.7  2004/10/14 13:38:51  Ian.Mayo
   * Refactor listening to sensors - so that we can listen to a sensor & it's detections in the same way that we can listen to a participant
   * <p/>
   * Revision 1.6  2004/09/06 14:20:05  Ian.Mayo
   * Provide default icons & properties for sensors
   * <p/>
   * Revision 1.5  2004/08/31 09:36:55  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.4  2004/08/26 17:05:35  Ian.Mayo
   * Implement more editable properties
   * <p/>
   * Revision 1.3  2004/08/25 11:21:08  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.2  2004/05/24 15:06:19  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.2  2004/03/25 22:46:55  ian
   * Reflect new simple environment constructor
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:54  ian
   * no message
   * <p/>
   * Revision 1.1  2004/02/16 13:41:39  Ian.Mayo
   * Renamed class structure
   * <p/>
   * Revision 1.4  2003/11/05 09:19:07  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  ////////////////////////////////////////////////////
  // the editor object
  ////////////////////////////////////////////////////
  static public class BroadbandInfo extends BaseSensorInfo
  {
    /**
     * @param data the Layers themselves
     */
    public BroadbandInfo(final BroadbandSensor data)
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
          prop("Name", "the name of this broadband sensor"),
          prop("DetectionAperture", "the size of the aperture of this sensor"),
          prop("Working", "whether this sensor is in use"),
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
  public static class BBSensorTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public BBSensorTest(final String val)
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
      return new BroadbandSensor(12);
    }

    public void testHeloDetection()
    {


      // set up the Ssk
      ASSET.Models.Vessels.SSK ssk = new ASSET.Models.Vessels.SSK(12);
      ASSET.Participants.Status sskStat = new ASSET.Participants.Status(12, 0);
      WorldLocation origin = new WorldLocation(0, 0, 0);
      sskStat.setLocation(origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(35), 40)));
      sskStat.setSpeed(new WorldSpeed(18, WorldSpeed.M_sec));
      ssk.setStatus(sskStat);

      // ok, setup the ssk radiation
      ASSET.Models.Mediums.BroadbandRadNoise brn = new ASSET.Models.Mediums.BroadbandRadNoise(134);
      ASSET.Models.Vessels.Radiated.RadiatedCharacteristics rc = new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
      rc.add(EnvironmentType.BROADBAND_PASSIVE, brn);
      ssk.setRadiatedChars(rc);

      // now setup the helo
      ASSET.Models.Vessels.Helo merlin = new ASSET.Models.Vessels.Helo(33);
      ASSET.Participants.Status merlinStat = new ASSET.Participants.Status(33, 0);
      merlinStat.setLocation(origin);
      merlinStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      merlin.setStatus(merlinStat);

      // and it's sensor
      ASSET.Models.Sensor.SensorList fit = new ASSET.Models.Sensor.SensorList();
      BroadbandSensor bs = new BroadbandSensor(34);
      fit.add(bs);
      merlin.setSensorFit(fit);

      // now setup the su
      ASSET.Models.Vessels.Surface ff = new ASSET.Models.Vessels.Surface(31);
      ASSET.Participants.Status ffStat = new ASSET.Participants.Status(31, 0);
      WorldLocation sskLocation = ssk.getStatus().getLocation();
      ffStat.setLocation(sskLocation.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(1), -40)));
      ffStat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));
      ff.setStatus(ffStat);
      ff.setSensorFit(fit);
      ASSET.Models.Mediums.BroadbandRadNoise ff_brn = new ASSET.Models.Mediums.BroadbandRadNoise(15);
      ASSET.Models.Vessels.Radiated.RadiatedCharacteristics ff_rc =
        new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
      ff_rc.add(EnvironmentType.BROADBAND_PASSIVE, ff_brn);
      ff.setSelfNoise(ff_rc);

      // try a detection
      ASSET.Models.Environment.CoreEnvironment env = new SimpleEnvironment(1, 1, 1);
      ASSET.Models.Detection.DetectionEvent dt;
      dt = bs.detectThis(env, merlin, ssk, 0, null);
      assertTrue("helo able to detect SSK", dt != null);

      dt = bs.detectThis(env, ff, ssk, 0, null);
      assertTrue("frigate able to detect SSK", dt != null);


    }

  }
}