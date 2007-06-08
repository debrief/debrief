package ASSET.Models.Vessels;

import ASSET.Models.Environment.EnvironmentType;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class Surface extends ASSET.Participants.CoreParticipant {


  public Surface(final int id) {
    this(id, null,null,null);

    // over-ride the radiated noise levels
    _radiatedChars.add(EnvironmentType.BROADBAND_PASSIVE, new ASSET.Models.Mediums.BroadbandRadNoise(194.78));
    _radiatedChars.add(EnvironmentType.VISUAL, new ASSET.Models.Mediums.Optic(1600, new WorldDistance(40, WorldDistance.METRES)));
  }

  public Surface(final int id, final ASSET.Participants.Status status, final ASSET.Participants.DemandedStatus demStatus, final String name)
  {
    super(id, status, demStatus, name);
  }

  public void initialise()
  {

    // fill up the tanks
    this.getStatus().setFuelLevel(100);

    // indicate that we are restricted to the surface
    this.getMovementChars().setMaxHeight(new WorldDistance(0, WorldDistance.METRES));
    this.getMovementChars().setMinHeight(new WorldDistance(0, WorldDistance.METRES));

    // set our max speed
    this.getMovementChars().setMaxSpeed(new WorldSpeed(20,WorldSpeed.M_sec));

  }
}