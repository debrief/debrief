package ASSET.Models.Vessels;

import ASSET.Models.MovementType;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Participants.*;
import MWC.GenericData.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class Helo extends ASSET.Participants.CoreParticipant {


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public Helo(final int id) {
    this(id, null,null,null);

    // give it a dummy name
    this.setName("Helo");

    // over-ride the radiated noise levels
    _radiatedChars.add(EnvironmentType.VISUAL, new ASSET.Models.Mediums.Optic(15,new WorldDistance(3, WorldDistance.METRES)));
  }

  private Helo(final int id, final ASSET.Participants.Status status, final ASSET.Participants.DemandedStatus demStatus, final String name)
  {
    super(id, status, demStatus, name);
  }

  /** use our state models to update the current set of vessel states
   *
   * @param newTime the time we are stepping to
   */
  protected void updateStates(long newTime)
  {
    super.updateStates(newTime);

    // there's a chance that we've arrived here without a demanded state - have a check and see
    if(this.getDemandedStatus() == null)
    {
      // don't bother, we can't have a demanded state
      System.out.println("null dem status for:" + this + " act:" + this.getActivity());
    }
    else
    {
      // and now look after our hover deployment states

      // is anybody requesting that we go into hover?
      if(this.getDemandedStatus().is(HoverStates.IN_HOVER))
      {
        // yes, are we stationary?
        if(this.getStatus().getSpeed().getValueIn(WorldSpeed.M_sec) == 0)
        {
          // yes, set us in hover
          this.getStatus().set(HoverStates.IN_HOVER);

          // and clear the demanded status
          this.getDemandedStatus().unset(HoverStates.IN_HOVER);
        }
      }
    }
  }


  public static class HoverStates implements MovementType
  {
    public final static String IN_HOVER = "In_Hover";

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
                       MovementCharacteristics moves)
    {
      return null;
    }
    ////////////////////////////////////////////////////////////
    // model support
    ////////////////////////////////////////////////////////////

    /** get the version details for this model.
     * <pre>
     * $Log: Helo.java,v $
     * Revision 1.2  2006/09/21 15:27:51  Ian.Mayo
     * Eclipse tidying
     *
     * Revision 1.1  2006/08/08 14:22:01  Ian.Mayo
     * Second import
     *
     * Revision 1.1  2006/08/07 12:26:10  Ian.Mayo
     * First versions
     *
     * Revision 1.9  2004/05/24 16:03:23  Ian.Mayo
     * Commit updates from home
     *
     * Revision 1.1.1.1  2004/03/04 20:30:55  ian
     * no message
     *
     * Revision 1.8  2003/11/05 09:18:44  Ian.Mayo
     * Include MWC Model support
     *
     * </pre>
     */
    public String getVersion()
    {
      return "$Date$";
    }


  }

}