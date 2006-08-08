package ASSET.Models.Vessels;

import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import ASSET.Participants.DemandedStatus;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Models.Sensor.Initial.BroadbandSensor;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA
 */
public class SonarBuoy extends CoreParticipant {
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  /** normal constructor
   *
   */
  public SonarBuoy(final int id,
                   final Status status,
                   final String name) {
    // create the participant bits
    super(id, status, null, name);

  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  public void initialise()
  {
    // fill up the tanks
    this.getStatus().setFuelLevel(100);

    // indicate that we are restricted to a particular depth
    this.getMovementChars().setMinHeight(new WorldDistance(this.getStatus().getLocation().getDepth(), WorldDistance.METRES));

    // set our max speed
    this.getMovementChars().setMaxSpeed(new WorldSpeed(0, WorldSpeed.M_sec));

    // and set the sensor
    BroadbandSensor bb = new BroadbandSensor(12);
    bb.setDetectionAperture(180d);
    bb.setName("Sonar Buoy BB");
    this.getSensorFit().add(bb);
  }

  /** return what this participant is currently doing
   *
   */
  public String getActivity() {
    return "Active";
  }

}
