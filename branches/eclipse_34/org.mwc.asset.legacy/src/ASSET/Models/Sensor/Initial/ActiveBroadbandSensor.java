/*
 * Created by Ian Mayo, PlanetMayo Ltd.
 * User: Ian.Mayo
 * Date: 29-Oct-2002
 * Time: 11:44:35
 */
package ASSET.Models.Sensor.Initial;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.SensorType;
import ASSET.ParticipantType;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class ActiveBroadbandSensor extends BroadbandSensor implements SensorType.ActiveSensor
{
  ////////////////////////////////////////////////////
  // member objects
  ////////////////////////////////////////////////////
  private double _sourceLevel; // dB

  ////////////////////////////////////////////////////
  // member constructor
  ////////////////////////////////////////////////////

  public ActiveBroadbandSensor(final int id)
  {
    super(id, "Active BB");
  }

  public ActiveBroadbandSensor(final int id, final String defaultName)
  {
    super(id, defaultName);
  }

  ////////////////////////////////////////////////////
  // accessors
  ////////////////////////////////////////////////////

  public int getMedium()
  {
    return EnvironmentType.BROADBAND_ACTIVE;
  }

  // allow an 'overview' test, just to check if it is worth all of the above processing
  protected boolean canDetectThisType(ParticipantType ownship,
                                      ParticipantType other, EnvironmentType env)
  {
    return other.radiatesThisNoise(EnvironmentType.BROADBAND_PASSIVE);
  }

  /**
   * @return the source level (in dB)
   */
  public double getSourceLevel()
  {
    return _sourceLevel;
  }

  /**
   * @param sourceLevel the source level (in dB)
   * @param sourceLevel the source level (in dB)
   */
  public void setSourceLevel(double sourceLevel)
  {
    this._sourceLevel = sourceLevel;
  }


  ////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////

  protected double getLoss(EnvironmentType environment,
                           WorldLocation target,
                           WorldLocation host)
  {
    // we just double the normal loss to get the two-way loss
    return 2 * super.getLoss(environment, target, host);
  }

  protected double getTgtNoise(ASSET.ParticipantType target,
                               double absBearingDegs)
  {
    // here we return the source level for this sensor
    return _sourceLevel;
  }

  /**
   * when running active sonar, we only determine the broadband radiated
   * noise for ownship (we don't measure the bb_active noise)
   *
   * @param ownship
   * @param absBearingDegs
   * @return
   */
  protected double getOSNoise(ParticipantType ownship, double absBearingDegs)
  {
    return ownship.getSelfNoiseFor(EnvironmentType.BROADBAND_PASSIVE, absBearingDegs);
  }

  protected double getBkgndNoise(EnvironmentType environment,
                                 WorldLocation host, double absBearingDegs)
  {
    return super.getBkgndNoise(environment, host, absBearingDegs);
  }


  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: ActiveBroadbandSensor.java,v $
   * Revision 1.2  2006/09/21 12:20:40  Ian.Mayo
   * Reflect introduction of default names
   *
   * Revision 1.1  2006/08/08 14:21:53  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:26:02  Ian.Mayo
   * First versions
   *
   * Revision 1.7  2004/11/03 15:42:06  Ian.Mayo
   * More support for MAD sensors, better use of canDetectThis method
   *
   * Revision 1.6  2004/09/06 14:20:03  Ian.Mayo
   * Provide default icons & properties for sensors
   * <p/>
   * Revision 1.5  2004/08/31 09:36:52  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.4  2004/08/26 16:27:19  Ian.Mayo
   * Implement editable properties
   * <p/>
   * Revision 1.3  2004/08/25 11:21:06  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.2  2004/05/24 15:06:16  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.2  2004/03/25 22:46:55  ian
   * Reflect new simple environment constructor
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:54  ian
   * no message
   * <p/>
   * Revision 1.1  2004/02/16 13:41:38  Ian.Mayo
   * Renamed class structure
   * <p/>
   * Revision 1.5  2003/11/05 09:19:05  Ian.Mayo
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
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new ActiveBroadbandInfo(this);

    return _myEditor;
  }


  ////////////////////////////////////////////////////
  // the editor object
  ////////////////////////////////////////////////////
  static public class ActiveBroadbandInfo extends BaseSensorInfo
  {
    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public ActiveBroadbandInfo(final ActiveBroadbandSensor data)
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
          prop("Working", "whether this sensor is in use"),
          prop("SourceLevel", "source level of this sensor"),
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
  public static class ActiveBBTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public ActiveBBTest(final String val)
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
      return new ActiveBroadbandSensor(12);
    }

    public void testHeloDetection()
    {


      // set up the Ssk
      ASSET.Models.Vessels.SSK ssk = new ASSET.Models.Vessels.SSK(12);
      ASSET.Participants.Status sskStat = new ASSET.Participants.Status(12, 0);
      WorldLocation origin = new WorldLocation(0, 0, 0);
      sskStat.setLocation(origin.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(5), 40)));
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
      merlinStat.setSpeed(new WorldSpeed(1, WorldSpeed.Kts));
      merlin.setStatus(merlinStat);

      // and it's sensor
      ASSET.Models.Sensor.SensorList fit = new ASSET.Models.Sensor.SensorList();
      ActiveBroadbandSensor bs = new ActiveBroadbandSensor(34);
      bs.setSourceLevel(210);
      fit.add(bs);
      merlin.setSensorFit(fit);

      // now setup the su
      ASSET.Models.Vessels.Surface ff = new ASSET.Models.Vessels.Surface(31);
      ASSET.Participants.Status ffStat = new ASSET.Participants.Status(31, 0);
      WorldLocation sskLocation = ssk.getStatus().getLocation();
      ffStat.setLocation(sskLocation.add(new WorldVector(0, MWC.Algorithms.Conversions.Nm2Degs(1), -40)));
      ffStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      ff.setStatus(ffStat);
      ff.setSensorFit(fit);
      ASSET.Models.Mediums.BroadbandRadNoise ff_brn = new ASSET.Models.Mediums.BroadbandRadNoise(15);
      ASSET.Models.Vessels.Radiated.RadiatedCharacteristics ff_rc =
        new ASSET.Models.Vessels.Radiated.RadiatedCharacteristics();
      ff_rc.add(EnvironmentType.BROADBAND_PASSIVE, ff_brn);
      ff.setSelfNoise(ff_rc);
      ff.setRadiatedChars(ff_rc);


      // try a detection
      ASSET.Models.Environment.CoreEnvironment env = new ASSET.Models.Environment.SimpleEnvironment(1, 1, 1);
      ASSET.Models.Detection.DetectionEvent dt;
      dt = bs.detectThis(env, merlin, ssk, 0, null);
      assertTrue("helo able to detect SSK", dt != null);

      dt = bs.detectThis(env, ff, ssk, 0, null);
      assertTrue("frigate able to detect SSK", dt != null);


    }

  }

}
