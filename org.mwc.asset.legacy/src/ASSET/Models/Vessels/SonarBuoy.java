/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package ASSET.Models.Vessels;

import ASSET.Models.Sensor.Initial.BroadbandSensor;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
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

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
