
package ASSET.Models.Movement;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

import ASSET.Models.MovementType;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;

/**
 * high level class which converts high level demanded statuses into the
 * detailed once necessary for the turn algorith, where applicable
 */
public class CoreMovement implements MovementType, java.io.Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the turn algorithm we're using
	 */
	private static TurnAlgorithm _turner = new TurnAlgorithm();

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: CoreMovement.java,v $
	 * Revision 1.2  2006/09/14 14:11:10  Ian.Mayo
	 * Source tidying
	 *
	 * Revision 1.1  2006/08/08 14:21:46  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:54  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.19  2004/11/01 15:54:57  Ian.Mayo
	 * Reflect new signature of Track Plot Observer
	 *
	 * Revision 1.18  2004/08/31 09:36:40  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 *
	 * Revision 1.17  2004/08/25 11:20:54  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.16  2004/08/16 10:10:36  Ian.Mayo
	 * Correctly set dummy scenario for track plot observer
	 * <p/>
	 * Revision 1.15  2004/08/16 09:32:53  Ian.Mayo
	 * Respect changed processing of tester recording to file (it needed a valid scenario object)
	 * <p/>
	 * Revision 1.14  2004/08/09 15:50:38  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.13  2004/05/24 15:09:00  Ian.Mayo
	 * Commit changes conducted at home
	 * <p/>
	 * Revision 1.2  2004/04/08 20:27:18  ian
	 * Restructured contructor for CoreObserver
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:53  ian
	 * no message
	 * <p/>
	 * Revision 1.12  2003/11/05 09:19:18  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	/**
	 * move platform forward one time step
	 *
	 * @param newTime        the current scenario time
	 * @param currentStatus  the current platform status
	 * @param demandedStatus the current demanded status of the platform
	 * @param moves          a set of movement characteristics for this particular
	 *                       platform
	 * @return the updated status
	 */

	@Override
	public Status step(final long newTime, final Status currentStatus, final DemandedStatus demandedStatus,
			final MovementCharacteristics moves) {

		Status res = null;

		SimpleDemandedStatus simpleDem = null;

		// see if we have a high level demanded status
		if (demandedStatus instanceof HighLevelDemandedStatus) {
			// switch us back to a high level dem status
			final HighLevelDemandedStatus hl = (HighLevelDemandedStatus) demandedStatus;

			// ok, see what the simple demanded status would be
			final WaypointVisitor visitType = hl.getVisitType();

			// and determine the path
			res = visitType.step(hl, currentStatus, newTime, moves, _turner);
		} else {
			// hey, it's just a simple one - go for it!
			simpleDem = (SimpleDemandedStatus) demandedStatus;

			// now pass the dem status to the turn algorithm
			res = _turner.doTurn(currentStatus, simpleDem, moves, newTime);
		}

		return res;
	}

}