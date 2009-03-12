package ASSET.Models;

/**
 * Title:        ASSET Simulator
 * Description:  Advanced Scenario Simulator for Evaluation of Tactics
 * Copyright:    Copyright (c) 2001
 * Company:      PlanetMayo Ltd
 * @author Ian Mayo
 * @version 1.0
 */

import ASSET.Participants.*;

/** definition of an algorithm that describes the movement of a particular platform.  It takes a
 * {@link ASSET.Participants.Status current status} and a
 * {@link ASSET.Participants.DemandedStatus demanded status} and moves the platform
 * forward in time to produce a {@link ASSET.Participants.Status new status}.  The
 * algorithm is informed of the {@link ASSET.Models.Movement.MovementCharacteristics movement characteristics}
 * of the current vessel.
 *
 */

public interface MovementType extends MWCModel{
  /** move platform forward one time step
   *
   * @param millis the current scenario time
   * @param currentStatus the current platform status
   * @param demandedStatus the current demanded status of the platform
   * @param moves a set of movement characteristics for this particular platform
   * @return the updated status
   */
  public Status step(long millis,
                     Status currentStatus,
                     DemandedStatus demandedStatus,
                     ASSET.Models.Movement.MovementCharacteristics moves);
}