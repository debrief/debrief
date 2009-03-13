package ASSET.Models.Movement;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Participants.*;

public class SSKMovement extends CoreMovement{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** how quickly we recharge (per second)
   *
   */
  private double _chargeRate = 0.006;

  /** set the rate at which this SSK recharges (units per second)
   *
   */
  public void setChargeRate(final double val)
  {
    _chargeRate = val;
  }

  /** get the rate at which this SSK recharges (units per second)
   *
   */
  public double getChargeRate()
  {
    return _chargeRate;
  }

  /** step this movement model forward through the indicated period
   *
   */
  public Status step(final long newTime, final Status currentStatus, final DemandedStatus demandedStatus, final MovementCharacteristics moves)
  {

    // what's the old time?
    final long oldTime = currentStatus.getTime();

    // get the actual movement
    final Status res = super.step(newTime, currentStatus, demandedStatus, moves);

    // if we are at or above charge depth, perform recharge
    if(-res.getLocation().getDepth() >= ASSET.Models.Vessels.SSK.CHARGE_HEIGHT)
    {

      // how long did we recharge for?
      final double secs = (newTime - oldTime) / 1000d;

      // how much can we recharge in this period?
      final double charged = secs * getChargeRate();

      // recharge us back up to our maximum
      res.setFuelLevel(Math.min(res.getFuelLevel() + charged, 100d));

    }

    return res;
  }

  ////////////////////////////////////////////////////////////
  // model support
  ////////////////////////////////////////////////////////////

  /** get the version details for this model.
   * <pre>
   * $Log: SSKMovement.java,v $
   * Revision 1.1  2006/08/08 14:21:50  Ian.Mayo
   * Second import
   *
   * Revision 1.1  2006/08/07 12:25:59  Ian.Mayo
   * First versions
   *
   * Revision 1.4  2004/05/24 15:09:12  Ian.Mayo
   * Commit changes conducted at home
   *
   * Revision 1.1.1.1  2004/03/04 20:30:53  ian
   * no message
   *
   * Revision 1.3  2003/11/05 09:19:21  Ian.Mayo
   * Include MWC Model support
   *
   * </pre>
   */
  public String getVersion()
  {
    return "$Date$";
  }


}