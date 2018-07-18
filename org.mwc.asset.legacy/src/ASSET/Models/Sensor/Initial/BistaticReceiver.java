package ASSET.Models.Sensor.Initial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Mediums.NarrowbandRadNoise;
import ASSET.Models.Mediums.Optic;
import ASSET.Models.Movement.SSMovementCharacteristics;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Vessels.Buoy;
import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Util.SupportTesting;
import MWC.Algorithms.FrequencyCalcs;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

public class BistaticReceiver extends CoreSensor
{

  public BistaticReceiver(int id)
  {
    super(id, 0, "Bistatic Receiver");
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * detection threshold
   * 
   */
  private final double THRESHOLD = 5; // db

  /**
   * whether to suppress bistatic hits when they would be obscured by direct path
   */
  private boolean _suppressDirect = true;

  /** the angle either side (Degs) that we suppress
   * direct path bearings
   */
  private double _obscureAngle;

  @Override
  public WorldDistance getEstimatedRange()
  {
    return null;
  }

  @Override
  protected boolean canProduceBearing()
  {
    return true;
  }

  @Override
  protected boolean canProduceRange()
  {
    return false;
  }

  @Override
  public int getMedium()
  {
    return EnvironmentType.NARROWBAND;
  }

  @Override
  public boolean hasEditor()
  {
    return false;
  }

  @Override
  public EditorType getInfo()
  {
    return null;
  }

  @Override
  public String getVersion()
  {
    return "1.0";
  }

  @Override
  protected DetectionEvent detectThis(EnvironmentType environment,
      ParticipantType host, ParticipantType target, long time,
      ScenarioType scenario)
  {
    DetectionEvent res = null;

    // is this an active buoy?
    if (Category.Type.BUOY.equals(target.getCategory().getType()))
    {
      // ok, we don't produce detections of buoys
    }
    else
    {
      // ok, now see if we can find any active transmitters
      ArrayList<ParticipantType> transmitters =
          new ArrayList<ParticipantType>();

      // loop through the participants
      final Collection<ParticipantType> parts = scenario
          .getListOfVisibleParticipants();

      for (Iterator<ParticipantType> iterator = parts.iterator(); iterator
          .hasNext();)
      {
        ParticipantType thisP = (ParticipantType) iterator.next();

        // see if this is a buoy
        if (Category.Type.BUOY.equals(thisP.getCategory().getType()))
        {
          // is it active?
          if (thisP.radiatesThisNoise(EnvironmentType.NARROWBAND))
          {
            // ok, remember it
            transmitters.add(thisP);
          }
        }
      }

      // did we find any?
      if (transmitters.size() > 0)
      {
        // ok, now consider the two way travel journey
        Iterator<ParticipantType> iter = transmitters.iterator();
        while (iter.hasNext())
        {
          ParticipantType transmitter = (ParticipantType) iter.next();

          // sort out the locations
          WorldLocation txLoc = transmitter.getStatus().getLocation();
          WorldLocation contactLoc = target.getStatus().getLocation();
          WorldLocation myLoc = getHostLocationFor(host);

          // what's the bearing to the tx from ux?
          double bearingToTxDegs = MWC.Algorithms.Conversions.Rads2Degs(txLoc.bearingFrom(myLoc));
          if(bearingToTxDegs > 180)
          {
            bearingToTxDegs -= 180;
          }

          // determine the bistatic propagation loss
          RadiatedCharacteristics txChars = transmitter.getRadiatedChars();
          NarrowbandRadNoise radNoise = (NarrowbandRadNoise) txChars.getMedium(
              EnvironmentType.NARROWBAND);
          double radLevel = radNoise.getBaseNoiseLevel();
          double baseFreq = radNoise.getFrequency();

          // NOTE:: don't can't just combine the two separate propagation losses. Calculate
          // the loss for the overall range
          final double sep1 = txLoc.rangeFrom(contactLoc);
          final double sep2 = contactLoc.rangeFrom(myLoc);
          final double total_sep = sep1 + sep2;
          // convert to yds
          final double rng_yds = MWC.Algorithms.Conversions.Degs2m(total_sep);

          // produce a transmission loss
          final float remainingNoise;
          if (rng_yds == 0)
          {
            // zero range is a special occurence, since it doesn't get through our log calcs
            remainingNoise = (float) radLevel;
          }
          else
          {
            // first sort out the spreading
            remainingNoise = (float) (radLevel - (20f * Math.log(rng_yds) / Math
                .log(10d)));
          }

          // is this sufficient?
          if (remainingNoise > THRESHOLD)
          {
            float bearing = (float) MWC.Algorithms.Conversions.Rads2Degs(
                contactLoc.subtract(myLoc).getBearing());

            final boolean obscured;
            if (_suppressDirect)
            {
              final double trimmedBearing = bearing > 180 ? bearing - 360 : bearing;
                            
              double brgDelta = trimmedBearing - bearingToTxDegs;
              if(brgDelta > 180)
              {
                brgDelta -= 360d;
              }
              if(brgDelta < -180)
              {
                brgDelta += 360d;
              }
                
              if (Math.abs(brgDelta) < _obscureAngle)
              {
                obscured = true;
              }
              else
              {
                obscured = false;
              }
            }
            else
            {
              obscured = false;
            }

            if (!obscured)
            {
              res = new DetectionEvent(time, host.getId(), myLoc, this, null,
                  null, bearing, null, remainingNoise, target.getCategory(),
                  null, null, target);

              // start off by changing the freq from the tx to the target
              double SpeedOfSound = 1500;
              double osHeadingRads = MWC.Algorithms.Conversions.Degs2Rads(
                  transmitter.getStatus().getCourse());
              double tgtHeadingRads = MWC.Algorithms.Conversions.Degs2Rads(
                  target.getStatus().getCourse());
              double osSpeed = transmitter.getStatus().getSpeed().getValueIn(
                  WorldSpeed.M_sec);
              double tgtSpeed = target.getStatus().getSpeed().getValueIn(
                  WorldSpeed.M_sec);
              double bearingRads = txLoc.subtract(contactLoc).getBearing();
              double freq = FrequencyCalcs.calcPredictedFreqSI(SpeedOfSound,
                  osHeadingRads, tgtHeadingRads, osSpeed, tgtSpeed, bearingRads,
                  baseFreq);

              // now change the freq from the target to the rx
              osHeadingRads = MWC.Algorithms.Conversions.Degs2Rads(target
                  .getStatus().getCourse());
              tgtHeadingRads = MWC.Algorithms.Conversions.Degs2Rads(host
                  .getStatus().getCourse());
              osSpeed = target.getStatus().getSpeed().getValueIn(
                  WorldSpeed.M_sec);
              tgtSpeed = host.getStatus().getSpeed().getValueIn(
                  WorldSpeed.M_sec);
              bearingRads = contactLoc.subtract(myLoc).getBearing();
              freq = FrequencyCalcs.calcPredictedFreqSI(SpeedOfSound,
                  osHeadingRads, tgtHeadingRads, osSpeed, tgtSpeed, bearingRads,
                  freq);

              // and store the overall result
              res.setFreq((float) freq);

              // ok - this method can only return one detection.
              return res;
            }
          }
        }
      }
    }

    return res;
  }

  @Override
  protected boolean canDetectThisType(NetworkParticipant ownship,
      ParticipantType other, EnvironmentType env)
  {
    // hey, we can probably detect anything that's not airborne
    return !Category.Environment.AIRBORNE.equals(other.getCategory()
        .getEnvironment());
  }

  @Override
  public boolean canIdentifyTarget()
  {
    return true;
  }

  static public class BistaticTest extends SupportTesting.EditableTesting
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public BistaticTest(final String name)
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
      BistaticReceiver theDet = new BistaticReceiver(12);
      return theDet;
    }

    public void testBistatics()
    {
      final CoreScenario scenario = new CoreScenario();
      scenario.setScenarioStepTime(10000);

      // reset the earth model
      WorldLocation.setModel(
          new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

      // now the objects
      WorldLocation l1 = new WorldLocation(0, 0, 0);
      WorldLocation l2 = l1.add(new WorldVector(0, MWC.Algorithms.Conversions
          .m2Degs(2000), 0));

      final Status theStat = new Status(12, 0);
      theStat.setLocation(l1);
      theStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));

      ASSET.Models.Vessels.SSN ssn = new ASSET.Models.Vessels.SSN(14);
      ssn.setCategory(new Category(Category.Force.BLUE,
          Category.Environment.SUBSURFACE, Category.Type.SUBMARINE));
      Status otherStat = new Status(theStat);
      otherStat.setLocation(l2);
      otherStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
      RadiatedCharacteristics rc = new RadiatedCharacteristics();
      Optic opticRadNoise = new Optic(2, new WorldDistance(2,
          WorldDistance.METRES));
      rc.add(EnvironmentType.VISUAL, opticRadNoise);
      ssn.setMovementChars(SSMovementCharacteristics.getSampleChars());
      ssn.setRadiatedChars(rc);
      ssn.setStatus(otherStat);

      EnvironmentType env = new SimpleEnvironment(1, 1, 1);

      // ok, generate the transmitter
      Buoy tx = new Buoy(2);
      tx.setName("TX");
      Status txStat = new Status(theStat);
      tx.setCategory(new Category(Category.Force.BLUE,
          Category.Environment.SUBSURFACE, Category.Type.BUOY));

      txStat.setLocation(txStat.getLocation().add(new WorldVector(3, 0.004,
          0)));
      txStat.setSpeed(new WorldSpeed(0, WorldSpeed.Kts));
      NarrowbandSensor noiseSource = new NarrowbandSensor(55);
      NarrowbandRadNoise nrn = new NarrowbandRadNoise(150, 150);
      RadiatedCharacteristics txRadChars = new RadiatedCharacteristics();
      txRadChars.add(EnvironmentType.NARROWBAND, nrn);
      tx.setRadiatedChars(txRadChars);
      tx.getSensorFit().add(noiseSource);
      tx.setStatus(txStat);

      Buoy rx = new Buoy(3);
      rx.setName("RX");
      rx.setCategory(new Category(Category.Force.BLUE,
          Category.Environment.SUBSURFACE, Category.Type.BUOY));
      Status rxStat = new Status(theStat);
      rxStat.setLocation(rxStat.getLocation().add(new WorldVector(1, 0.004,
          0)));
      rxStat.setSpeed(new WorldSpeed(0, WorldSpeed.Kts));
      BistaticReceiver receiver = new BistaticReceiver(44);
      rx.getSensorFit().add(receiver);
      rx.setStatus(rxStat);

      scenario.addParticipant(ssn.getId(), ssn);
      scenario.addParticipant(tx.getId(), tx);
      scenario.addParticipant(rx.getId(), rx);

      DetectionList detections = new DetectionList();
      receiver.detects(env, detections, rx, scenario, 20000);

      assertEquals("got some detections", 1, detections.size());

      showDetections(detections);

      detections.clear();
      receiver.detects(env, detections, rx, scenario, 40000);

      assertEquals("got some detections", 1, detections.size());

      // ok, do some steps
      for (int i = 0; i < 10; i++)
      {
        detections = receiver.getAllDetections();
        detections.clear();

        scenario.step();
        showDetections(detections);
      }

    }

    private void showDetections(DetectionList detections)
    {
      Iterator<DetectionEvent> iter = detections.iterator();
      while (iter.hasNext())
      {
        DetectionEvent de = (DetectionEvent) iter.next();
        System.out.println(de.getTargetType().getType() + " bearing:" + de
            .getBearing());
      }

    }
  }

  @Override
  public void update(DemandedStatus myDemandedStatus, Status myStatus,
      long newTime)
  {
    // don't bother...
  }

  public boolean isSuppressDirect()
  {
    return _suppressDirect;
  }

  public void setSuppressDirect(boolean suppressDirect)
  {
    this._suppressDirect = suppressDirect;
  }

  public double getObscureAngle()
  {
    return _obscureAngle;
  }

  public void setObscureAngle(double obscureAngle)
  {
    this._obscureAngle = obscureAngle;
  }
}
