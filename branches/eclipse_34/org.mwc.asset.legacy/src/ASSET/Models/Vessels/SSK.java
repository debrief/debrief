package ASSET.Models.Vessels;

import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.SSKMovement;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class SSK extends SSN {

  /** the Height we snort at
   *
   */
  static final public double CHARGE_HEIGHT = -15;

  public SSK(final int id) {
    this(id, null,null,null);
  }

  public SSK(final int id,
             final ASSET.Participants.Status status,
             final ASSET.Participants.DemandedStatus demStatus,
             final String name)
  {
    super(id, status, demStatus, name);

    super.setMovementModel(new ASSET.Models.Movement.SSKMovement());
  }

  public void setChargeRate(double val)
  {
    SSKMovement theMovement = (SSKMovement)super.getMovementModel();
    theMovement.setChargeRate(val);
  }

  public double getChargeRate()
  {
    SSKMovement theMovement = (SSKMovement)super.getMovementModel();
    return theMovement.getChargeRate();
  }


}