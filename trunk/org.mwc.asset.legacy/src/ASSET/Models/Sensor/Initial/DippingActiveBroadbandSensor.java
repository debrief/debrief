package ASSET.Models.Sensor.Initial;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Sensor.DeployableSensor;
import ASSET.Models.Vessels.Helo;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 03-Sep-2003
 * Time: 09:55:35
 * Log:  $Log:
 *
 */

/**
 * Implementation of a dipping active broadband sensor - such as the ADS deployed by Merlin
 */
public class DippingActiveBroadbandSensor extends ActiveBroadbandSensor implements DeployableSensor
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  public static final String ADS_DEPLOYED = "ADS_DEPLOYED";
  public static final String ADS_HOUSED = "ADS_HOUSED";
  public static final String ADS_AT_DEPTH = "ADS_AT_DEPTH";

  /**
   * the period which we've waited already (secs)
   */
  private double _remainingPausePeriod = 0;

  /**
   * the depth we are instructed to deploy to
   */
  private double _demandedDeployDepth;

  /**
   * the current deployment depth
   */
  private double _cableLength;

  /**
   * the lower rate in water
   */
  private double _waterLowerRate;

  /**
   * the lower rate in air (m/sec)
   */
  private double _airLowerRate;

  /**
   * the raise rate in water (m/sec)
   */
  private double _waterRaiseRate;

  /**
   * the raise rate in air (m/sec)
   */
  private double _airRaiseRate;

  /**
   * the pause prior to lowering the sensor  (secs)
   */
  private double _lowerPause;

  /**
   * the pause prior to housing the sensor  (secs)
   */
  private double _raisePause;

  /**
   * the host id
   */
  private int _hostId = -1;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  public DippingActiveBroadbandSensor(final int id)
  {
    super(id, "Dipping_Active");

    // initialise the cable length
    _cableLength = 0;
  }

  /**
   * constructor for dipping sensor
   *
   * @param id
   * @param airLowerRate   how fast we lower in air (m/sec)
   * @param airRaiseRate   how fast we raise in air (m/sec)
   * @param lowerPause     how long we pause before housing
   * @param raisePause     how long we pause before lowering
   * @param waterLowerRate how fast we raise in water (m/sec)
   * @param waterRaiseRate how fast we lower in water (m/sec)
   */
  public DippingActiveBroadbandSensor(final int id, WorldSpeed airLowerRate,
                                      WorldSpeed airRaiseRate, Duration lowerPause,
                                      Duration raisePause, WorldSpeed waterLowerRate,
                                      WorldSpeed waterRaiseRate)
  {
    this(id);
    this._airLowerRate = airLowerRate.getValueIn(WorldSpeed.M_sec);
    this._airRaiseRate = airRaiseRate.getValueIn(WorldSpeed.M_sec);
    this._lowerPause = lowerPause.getValueIn(Duration.SECONDS);
    this._raisePause = raisePause.getValueIn(Duration.SECONDS);
    this._waterLowerRate = waterLowerRate.getValueIn(WorldSpeed.M_sec);
    this._waterRaiseRate = waterRaiseRate.getValueIn(WorldSpeed.M_sec);
  }

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////
  /**
   * if this sensor has a dynamic behaviour, update it according to the demanded status
   *
   * @param myDemandedStatus
   * @param myStatus
   * @param newTime
   */
  public void update(DemandedStatus myDemandedStatus,
                     Status myStatus,
                     long newTime)
  {
    // handle the active/inactive requests
    super.update(myDemandedStatus, myStatus, newTime);

    // remember the host id
    _hostId = myStatus.getId();

    if (myDemandedStatus == null)
    {
      // hey, just ignore it
    }
    else
    {
      // are we meant to be heading for the dip?
      if (myDemandedStatus.is(ADS_DEPLOYED))
      {
        // yes - are we stationary yet?
        if (myStatus.is(Helo.HoverStates.IN_HOVER))
        {
          // is this the start of the deployment
          if (this.isHoused())
          {
            // insert the lowering delay
            setLoweringPausePeriod();
          }

          // yes, start deploying
          lower(newTime - myStatus.getTime(), myStatus.getLocation().getDepth());

          // update the status to show it's not housed
          myStatus.unset(ADS_HOUSED);

          // and update the actual status
          myStatus.set(ADS_DEPLOYED);

          // did we finish ?
          if (isAtDepth(myStatus.getLocation().getDepth()))
          {
            // remove from dem status
            myDemandedStatus.unset(ADS_DEPLOYED);

            myStatus.set(ADS_AT_DEPTH);

            // and reset the pause period
            _remainingPausePeriod = _lowerPause;

          }
          else
          {
            // nothing to update in the state as yet
          }
        }
        else
        {
          // no, we're not in hover - carry on waiting
        }
      }
      else if (myDemandedStatus.is(ADS_HOUSED))
      {

        // indicate we're no longer deploying
        myStatus.unset(ADS_AT_DEPTH);

        // have we just started to raise?
        if (this._remainingPausePeriod == 0)
          setRaisingPausePeriod();

        // just check we're not already housed
        if (!isHoused())
        {
          // yes, start reeling
          raise(newTime - myStatus.getTime(), myStatus.getLocation().getDepth());

          // did we finish?
          if (isHoused())
          {

            // yes,
            // remove from dem status
            myDemandedStatus.unset(ADS_HOUSED);

            // and update the actual status
            myStatus.set(ADS_HOUSED);

            // update the status to show it's not deployed
            myStatus.unset(ADS_DEPLOYED);

          }
        }
      }
      else
      {
        // hey, we're not being told to raise or lower the body - we must
        // be off doing something else.
        // Reset stuff to leave ourselves in a tidy state
        // todo: find out what to reset
      }
    }

  }

  /**
   * set the period to wait prior to housing the body
   */
  private void setRaisingPausePeriod()
  {
    _remainingPausePeriod = _raisePause;
    // todo: insert the sea state variable
  }

  private void setLoweringPausePeriod()
  {
    _remainingPausePeriod = _lowerPause;
  }

  /**
   * whether the sensor is at the demanded depth
   *
   * @param myDepth the platform depth/altitude
   * @return yes/no
   */
  private boolean isAtDepth(double myDepth)
  {
    // what's our altitude?
    double alt = Math.abs(myDepth);

    // what's the buoy depth?
    double depth = _cableLength - alt;

    // are we there?
    final boolean res = (depth == _demandedDeployDepth);

    return res;
  }

  /**
   * whether the sensor is currently stowed.
   * Stowage depends on the cable length being zero
   * and no remaining pause period
   *
   * @return yes/no
   */
  private boolean isHoused()
  {
    return (_cableLength == 0) && (_remainingPausePeriod == 0);
  }

  /**
   * handle the demanded change in sensor lineup
   *
   * @param status
   */
  public void inform(DemandedSensorStatus status)
  {
    // handle being switched off/on by the core class
    super.inform(status);

    // now handle the depth
    if (status.getDeployDepth() != null)
    {
      _demandedDeployDepth = status.getDeployDepth().doubleValue();
    }
  }

  /**
   * move the body down through the air/water column
   *
   * @param period how long to descend for
   * @param depth  the current depth/height of the host platform
   */
  private void lower(long period, double depth)
  {

    // get the period in millis
    double period_secs = (double) period / 1000d;

    // have we included the pause yet?
    if (_remainingPausePeriod > 0)
    {
      double pauseObtained = _remainingPausePeriod;

      // yes - we need to include the pause -
      pauseObtained = Math.min(pauseObtained, period_secs);

      // shrink the remaining time
      period_secs -= pauseObtained;

      // and update how long we have paused for
      _remainingPausePeriod -= pauseObtained;

    }

    // how long will we descend in air?
    double airTime = 0;

    // are we currently in air?
    if (_cableLength < Math.abs(depth))
    {
      // how much more do we need to extend in air?
      double airExtensionRequired = Math.abs(depth) - _cableLength;

      // yes, how long will it take to drop in air?
      airTime = airExtensionRequired / _airLowerRate;

      // trim it
      airTime = Math.min(airTime, period_secs);

      // drop by the air dist
      _cableLength += airTime * _airLowerRate;
    }

    double waterTime = period_secs - airTime;

    // do we have any time left
    if (waterTime > 0)
    {
      // how much further will we have to go?
      double depthChange = _demandedDeployDepth - (_cableLength - Math.abs(depth));

      // how long will it take to get to the demanded depth
      double depthChangeTime = depthChange / _waterLowerRate;

      // and trim it
      depthChangeTime = Math.min(depthChangeTime, waterTime);

      // and do the drop
      _cableLength += depthChangeTime * _waterLowerRate;

    }

  }

  /**
   * move the body up through the air/water column
   *
   * @param period how long to ascend for
   * @param depth  the current depth/height of the host platform
   */
  private void raise(long period, double depth)
  {

    // get the period in millis
    double period_secs = (double) period / 1000d;

    // how long will we ascend in water?
    double waterTime = 0;

    // are we currently in air?
    if (_cableLength > Math.abs(depth))
    {
      // how much more do we need to extend in water?
      double waterContractionRequired = _cableLength - Math.abs(depth);

      // yes, how long will it take to drop in water?
      waterTime = waterContractionRequired / _waterRaiseRate;

      // trim it
      waterTime = Math.min(waterTime, period_secs);

      // drop by the air dist
      _cableLength -= waterTime * _waterRaiseRate;
    }

    double airTime = period_secs - waterTime;

    // do we have any time left
    if (airTime > 0)
    {
      // how much further will we have to go?
      double depthChange = _cableLength;

      // how long will it take to get to the demanded depth
      double depthChangeTime = depthChange / _airRaiseRate;

      // and trim it
      depthChangeTime = Math.min(depthChangeTime, airTime);

      // and do the drop
      _cableLength -= depthChangeTime * _airRaiseRate;

    }



    // SPECIAL PROCESSING
    //  the raise pause is inserted just before we house the sensor - so
    // keep the sensor just below the housing for the pause period

    // is it in all the way yet?
    if (_cableLength == 0)
    {
      if (_remainingPausePeriod > 0)
      {
        double pauseObtained = _remainingPausePeriod;

        // yes - we need to include the pause -
        pauseObtained = Math.min(pauseObtained, period_secs);

        // shrink the remaining time
        period_secs -= pauseObtained;

        // and update how long we have paused for
        _remainingPausePeriod -= pauseObtained;
      }
    }
  }

  //////////////////////////////////////////////////
  // getter/setters
  //////////////////////////////////////////////////
  public WorldSpeed getAirLowerRate()
  {
    return new WorldSpeed(_airLowerRate, WorldSpeed.M_sec);
  }

  public void setAirLowerRate(WorldSpeed airLowerRate)
  {
    this._airLowerRate = airLowerRate.getValueIn(WorldSpeed.M_sec);
  }

  public WorldSpeed getAirRaiseRate()
  {
    return new WorldSpeed(_airRaiseRate, WorldSpeed.M_sec);
  }

  public void setAirRaiseRate(WorldSpeed airRaiseRate)
  {
    this._airRaiseRate = airRaiseRate.getValueIn(WorldSpeed.M_sec);
  }

  public Duration getLowerPause()
  {
    return new Duration(_lowerPause, Duration.SECONDS);
  }

  public void setLowerPause(Duration lowerPause)
  {
    this._lowerPause = lowerPause.getValueIn(Duration.SECONDS);
  }

  public Duration getRaisePause()
  {
    return new Duration(_raisePause, Duration.SECONDS);
  }

  public void setRaisePause(Duration raisePause)
  {
    this._raisePause = raisePause.getValueIn(Duration.SECONDS);
  }

  public WorldSpeed getWaterLowerRate()
  {
    return new WorldSpeed(_waterLowerRate, WorldSpeed.M_sec);
  }

  public void setWaterLowerRate(WorldSpeed waterLowerRate)
  {
    this._waterLowerRate = waterLowerRate.getValueIn(WorldSpeed.M_sec);
  }

  public WorldSpeed getWaterRaiseRate()
  {
    return new WorldSpeed(_waterRaiseRate, WorldSpeed.M_sec);
  }

  public void setWaterRaiseRate(WorldSpeed waterRaiseRate)
  {
    this._waterRaiseRate = waterRaiseRate.getValueIn(WorldSpeed.M_sec);
  }

  public double getDemandedDeployDepth()
  {
    return _demandedDeployDepth;
  }

  public void setDemandedDeployDepth(double demandedDeployDepth)
  {
    this._demandedDeployDepth = demandedDeployDepth;
  }

  public double getCableLength()
  {
    return _cableLength;
  }

  /**
   * retrieve the current location of the sensor
   */
  public WorldLocation getLocation(WorldLocation hostLocation)
  {
    // create a new location where we've descended by the cable length
    WorldLocation res = hostLocation.add(new WorldVector(0, 0, _cableLength));
    return res;
  }

  /**
   * retrieve the host id
   */
  public int getHostId()
  {
    return _hostId;
  }


  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /**
   * get the version details for this model.
   * <pre>
   * $Log: DippingActiveBroadbandSensor.java,v $
   * Revision 1.2  2006/09/21 12:20:41  Ian.Mayo
   * Reflect introduction of default names
   *
   * Revision 1.1  2006/08/08 14:21:54  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:26:03  Ian.Mayo
   * First versions
   *
   * Revision 1.8  2004/09/24 11:08:11  Ian.Mayo
   * Tidy test names
   *
   * Revision 1.7  2004/09/06 14:20:06  Ian.Mayo
   * Provide default icons & properties for sensors
   * <p/>
   * Revision 1.6  2004/08/31 09:36:56  Ian.Mayo
   * Rename inner static tests to match signature **Test to make automated testing more consistent
   * <p/>
   * Revision 1.5  2004/08/27 09:53:32  Ian.Mayo
   * Continue to implement testing
   * <p/>
   * Revision 1.4  2004/08/26 17:05:36  Ian.Mayo
   * Implement more editable properties
   * <p/>
   * Revision 1.3  2004/08/25 11:21:09  Ian.Mayo
   * Remove main methods which just run junit tests
   * <p/>
   * Revision 1.2  2004/05/24 15:06:21  Ian.Mayo
   * Commit changes conducted at home
   * <p/>
   * Revision 1.1.1.1  2004/03/04 20:30:54  ian
   * no message
   * <p/>
   * Revision 1.1  2004/02/16 13:41:40  Ian.Mayo
   * Renamed class structure
   * <p/>
   * Revision 1.12  2003/11/05 09:19:08  Ian.Mayo
   * Include MWC Model support
   * <p/>
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


  //////////////////////////////////////////////////
  // property editing
  //////////////////////////////////////////////////

  private EditorType _myEditor;

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
      _myEditor = new DippingActiveBroadbandSensorInfo(this);

    return _myEditor;
  }

  //////////////////////////////////////////////////
  // editable properties
  //////////////////////////////////////////////////
  static public class DippingActiveBroadbandSensorInfo extends BaseSensorInfo
  {


    /**
     * constructor for editable details
     *
     * @param data the object we're going to edit
     */
    public DippingActiveBroadbandSensorInfo(final DippingActiveBroadbandSensor data)
    {
      super(data);
    }

    /**
     * editable GUI properties for our participant
     *
     * @return property descriptions
     */
    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          prop("Name", "the name of this sensor"),
          prop("AirLowerRate", "the rate at which the sensor descends in air"),
          prop("WaterLowerRate", "the rate at which the sensor descends in water"),
          prop("AirRaiseRate", "the rate at which the sensor ascends in air"),
          prop("WaterRaiseRate", "the rate at which the sensor ascends in water"),
          prop("DemandedDeployDepth", "the depth to deploy the sensor to"),
          prop("RaisePause", "the period to wait for before pulling the sensor into the host"),
          prop("LowerPause", "the period to wait for before the sensor leaves the host"),
        };
        return res;
      }
      catch (IntrospectionException e)
      {
        e.printStackTrace();
        return super.getPropertyDescriptors();
      }
    }
  }


  //////////////////////////////////////////////////
  // testing
  //////////////////////////////////////////////////
  static public class DippingActiveTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public DippingActiveTest(final String val)
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
      return new DippingActiveBroadbandSensor(12);
    }

    public void testLower()
    {
      double waterLowerRate = 4;
      double airLowerRate = 6;
      double waterRaiseRate = 1;
      double airRaiseRate = 3;
      double lowerPause = 3;
      double raisePause = 5;

      DippingActiveBroadbandSensor dip =
        new DippingActiveBroadbandSensor(12,
                                         new WorldSpeed(airLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
                                         new Duration(lowerPause, Duration.SECONDS),
                                         new Duration(raisePause, Duration.SECONDS),
                                         new WorldSpeed(waterLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));

      // check we're up and running
      assertNotNull("object created", dip);

      // check we've stored the objects correctly
      assertEquals("it", dip.getAirLowerRate().getValueIn(WorldSpeed.M_sec), airLowerRate, 0.1);
      assertEquals("it", dip.getWaterLowerRate().getValueIn(WorldSpeed.M_sec), waterLowerRate, 0.1);
      assertEquals("it", dip.getAirRaiseRate().getValueIn(WorldSpeed.M_sec), airRaiseRate, 0.1);
      assertEquals("it", dip.getWaterRaiseRate().getValueIn(WorldSpeed.M_sec), waterRaiseRate, 0.1);
      assertEquals("it", dip.getRaisePause().getValueIn(Duration.SECONDS), raisePause, 0.1);
      assertEquals("it", dip.getLowerPause().getValueIn(Duration.SECONDS), lowerPause, 0.1);

      // and the cable length
      assertEquals("still inside", dip._cableLength, 0, 0.0);

      // start the lower sequence
      dip._remainingPausePeriod = dip._lowerPause;

      dip.setDemandedDeployDepth(300);

      dip.lower(2000, -60);

      //
      assertEquals("still pausing", 0, dip._cableLength, 0.0);

      dip.lower(3000, -60);
      //
      assertEquals("started dropping through air", 12, dip._cableLength, 0.0);

      // and some more
      dip.lower(2000, -60);

      //
      assertEquals("still dropping through air", 24, dip._cableLength, 0.0);

      // and some more
      dip.lower(5000, -60);

      //
      assertEquals("still dropping through air", 54, dip._cableLength, 0.0);

      // span the two mediums
      dip.lower(2000, -60);

      //
      assertEquals("still dropping through air", 64, dip._cableLength, 0.0);

      // carry on diving
      dip.lower(76000, -60);

      //
      assertEquals("reached bottom", 360, dip._cableLength, 0.0);
    }

    public void testRaise()
    {
      double waterLowerRate = 4;
      double airLowerRate = 6;
      double waterRaiseRate = 1;
      double airRaiseRate = 3;
      double lowerPause = 3;
      double raisePause = 5;

      DippingActiveBroadbandSensor dip =
        new DippingActiveBroadbandSensor(12,
                                         new WorldSpeed(airLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
                                         new Duration(lowerPause, Duration.SECONDS),
                                         new Duration(raisePause, Duration.SECONDS),
                                         new WorldSpeed(waterLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));

      // initialise the pause period
      dip._remainingPausePeriod = dip._raisePause;

      dip._cableLength = 150;
      dip.raise(2000, -30);

      //
      assertEquals("starting to come up", 148, dip._cableLength, 0.0);

      //
      dip.raise(117000, -30);
      //
      assertEquals("near the surface", 31, dip._cableLength, 0.0);

      //
      //
      dip.raise(2000, -30);
      // answer should be 27 - from 31m under we travel a 1m/s for 1 sec then 3m/sec for 1 sec - 4 m/sec total
      assertEquals("in air", 27, dip._cableLength, 0.0);

      //
      dip.raise(8000, -30);
      assertEquals("in air", 3, dip._cableLength, 0.0);

      //
      dip.raise(4000, -30);
      assertEquals("in air", 0, dip._cableLength, 0.0);

      //
      assertFalse("pausing before house", dip.isHoused());

      //
      dip.raise(2000, -30);
      assertTrue("now housed", dip.isHoused());

      assertFalse(dip.isAtDepth(-30));


    }


    public void testBig()
    {
      DemandedStatus ds = new SimpleDemandedStatus(12, 6000);
      DemandedSensorStatus dss = new DemandedSensorStatus(EnvironmentType.BROADBAND_ACTIVE, true);
      dss.setDeployDepth(new Double(300));
      ds.set(DippingActiveBroadbandSensor.ADS_HOUSED);
      ds.add(dss);

      Status st = new Status(11, 0);
      st.setLocation(createLocation(0, 0));

      dss.setDeployDepth(new Double(300));
      st.getLocation().setDepth(-100);
      double waterLowerRate = 4;
      double airLowerRate = 6;
      double waterRaiseRate = 1;
      double airRaiseRate = 3;
      double lowerPause = 3;
      double raisePause = 5;

      DippingActiveBroadbandSensor dip =
        new DippingActiveBroadbandSensor(12,
                                         new WorldSpeed(airLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
                                         new Duration(lowerPause, Duration.SECONDS),
                                         new Duration(raisePause, Duration.SECONDS),
                                         new WorldSpeed(waterLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));

      // tell the dipper that we want it to deploy
      dip.inform(dss);

      // let's see what happens
      dip.update(ds, st, 30000);

      // start by checking what happens when we try to re-house it
      assertTrue(dip.isHoused());
      assertFalse(dip.isAtDepth(st.getLocation().getDepth()));
      assertEquals("no pause required", dip._remainingPausePeriod, 0, 0.001);

      ds.unset(DippingActiveBroadbandSensor.ADS_HOUSED);
      ds.set(DippingActiveBroadbandSensor.ADS_DEPLOYED);

      // let's see what happens when we're not in hover
      dip.update(ds, st, 30000);

      // has it started to deploy?
      assertTrue("not started to deploy when not in hover", dip.isHoused());
      assertFalse("not started to deploy when not in hover", dip.isAtDepth(st.getLocation().getDepth()));
      assertEquals("no pause inserted", dip._remainingPausePeriod, 0, 0.001);
      assertEquals("no cable extended", dip._cableLength, 0, 0.001);

      // let's see what happens when we are in hover
      st.set(Helo.HoverStates.IN_HOVER);
      dip.update(ds, st, 30000);

      // has it started to deploy?
      assertFalse("not housed any more", dip.isHoused());
      assertFalse("still not deployed", dip.isAtDepth(st.getLocation().getDepth()));

      // move the state forward
      st.setTime(30000);

      dip.update(ds, st, 3000000);

      // has it started to deploy?
      assertFalse("not housed any more", dip.isHoused());
      assertTrue("deploy deployed", dip.isAtDepth(st.getLocation().getDepth()));

    }

    public void testTimedLowerTest()
    {

      Status st = new Status(11, 0);
      st.setLocation(createLocation(0, 0));
      st.getLocation().setDepth(-100);
      st.set(Helo.HoverStates.IN_HOVER);

      DemandedStatus ds = new SimpleDemandedStatus(12, 6000);
      DemandedSensorStatus dss = new DemandedSensorStatus(EnvironmentType.BROADBAND_ACTIVE, true);
      dss.setDeployDepth(new Double(300));
      ds.set(DippingActiveBroadbandSensor.ADS_DEPLOYED);

      double waterLowerRate = 4;
      double airLowerRate = 6;
      double waterRaiseRate = 1;
      double airRaiseRate = 3;
      double lowerPause = 3;
      double raisePause = 5;

      DippingActiveBroadbandSensor dip =
        new DippingActiveBroadbandSensor(12,
                                         new WorldSpeed(airLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
                                         new Duration(lowerPause, Duration.SECONDS),
                                         new Duration(raisePause, Duration.SECONDS),
                                         new WorldSpeed(waterLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));
      // check we're up and running
      assertNotNull("object created", dip);


      dip.inform(dss);

      long timeStep = 100;
      long newTime = 0;

      // ok, now loop through second by second and see how we get on...
      while (!dip.isAtDepth(st.getLocation().getDepth()))
      {
        dip.update(ds, st, newTime += timeStep);
        st.setTime(newTime);
      }
      assertEquals("took correct time", 94700, newTime, 0);

    }

    public void testTimedRaiseTest()
    {

      Status st = new Status(11, 0);
      st.setLocation(createLocation(0, 0));
      st.getLocation().setDepth(-100);
      st.set(Helo.HoverStates.IN_HOVER);

      DemandedStatus ds = new SimpleDemandedStatus(12, 6000);
      ds.set(DippingActiveBroadbandSensor.ADS_HOUSED);

      double waterLowerRate = 4;
      double airLowerRate = 6;
      double waterRaiseRate = 1;
      double airRaiseRate = 3;
      double lowerPause = 3;
      double raisePause = 5;

      DippingActiveBroadbandSensor dip =
        new DippingActiveBroadbandSensor(12,
                                         new WorldSpeed(airLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(airRaiseRate, WorldSpeed.M_sec),
                                         new Duration(lowerPause, Duration.SECONDS),
                                         new Duration(raisePause, Duration.SECONDS),
                                         new WorldSpeed(waterLowerRate, WorldSpeed.M_sec),
                                         new WorldSpeed(waterRaiseRate, WorldSpeed.M_sec));


      // check we're up and running
      assertNotNull("object created", dip);

      dip._cableLength = 400;

      long timeStep = 100;
      long newTime = 0;

      // ok, now loop through second by second and see how we get on...
      while (!dip.isHoused())
      {
        dip.update(ds, st, newTime += timeStep);
        st.setTime(newTime);
      }
      assertEquals("took correct time", 338400, newTime, 0);
    }

  }


}
