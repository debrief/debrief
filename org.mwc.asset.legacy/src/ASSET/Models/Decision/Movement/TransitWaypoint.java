
package ASSET.Models.Decision.Movement;

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

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Movement.HighLevelDemandedStatus;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.WaypointVisitor;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.GUI.Editable;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

/**
 * behaviour to travel through a series of points at an indicated speed.
 * Optionally the journey may be repeated, in forward or reverse direction.
 */
public class TransitWaypoint extends CoreDecision implements java.io.Serializable {

	//////////////////////////////////////////////////////////////////////
	// Member Variables
	//////////////////////////////////////////////////////////////////////

	static public class TransitInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public TransitInfo(final TransitWaypoint data) {
			super(data, data.getName(), "Transit");
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Loop", "whether we loop back to the first point"),
						prop("Reverse", "when we loop, whether we follow the points in reverse order"),
						prop("Speed", "the speed we transit at (kts)"),
						prop("Name", "the name of this transit model"), };

				return res;
			} catch (final java.beans.IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * our name.
	 */
	private static final String WAYPOINT_NAME = "Transit Waypoint";

	/**
	 * loop through destinations
	 */
	private boolean _loop = true;

	/**
	 * when we are looping, whether we go back to start, or pass through them in
	 * reverse;
	 */
	private boolean _inReverse = true;

	/**
	 * a local copy of our editable object
	 */
	private MWC.GUI.Editable.EditorType _myEditor = null;

	//////////////////////////////////////////////////////////////////////
	// Constructor
	//////////////////////////////////////////////////////////////////////

	/**
	 * whether this represents a single transit, and is now complete
	 */
	private boolean _transit_complete = false;

	/**
	 * the route we travel through
	 */
	private HighLevelDemandedStatus _demStatus = null;

	//////////////////////////////////////////////////////////////////////
	// Member methods
	//////////////////////////////////////////////////////////////////////

	public TransitWaypoint() {
		super(WAYPOINT_NAME);
		_demStatus = null;
		_loop = true;
		_demStatus = new HighLevelDemandedStatus(-1, -1);
	}

	public TransitWaypoint(final WorldPath destinations, final WorldSpeed transit_speed, final boolean loop,
			final WaypointVisitor visitor) {
		super(WAYPOINT_NAME);
		_loop = loop;

		// collate the status object
		_demStatus = new HighLevelDemandedStatus(-1, -1, 0, destinations, visitor, transit_speed);
	}

	@Override
	public DemandedStatus decide(final Status status, final MovementCharacteristics chars,
			final DemandedStatus demStatus, final ASSET.Models.Detection.DetectionList detections,
			final ASSET.Scenario.ScenarioActivityMonitor monitor, final long newTime) {

		DemandedStatus res = null;

		String thisActivity;

		if (_transit_complete) {
			thisActivity = "Transit complete";
			return res;
		}

		// are we already up and running?
		if (_demStatus.getTime() != -1) {
			// yes, have we finished
			if (_demStatus.getCurrentTarget() == null) {
				// yes, do we want to go around again?
				if (_loop) {
					// yes, do we want to go in reverse?
					if (_inReverse) {
						// are we already running in reverse?
						if (_demStatus.getRunInReverse()) {
							// ok, set to run forwards
							_demStatus.setRunInReverse(false);
							_demStatus.setCurrentTargetIndex(1);
							thisActivity = "Starting to run forwards again from start";
						} else {
							// ok. we've finished the route, and are going to run back in reverse. set
							// the current target to be the last but one.
							_demStatus.setRunInReverse(true);
							_demStatus.setCurrentTargetIndex(_demStatus.size() - 2);
							thisActivity = "Starting to run backwards from end";
						}
					} else {
						// no, we want to run forward
						// ok, set to run forwards
						_demStatus.setRunInReverse(false);
						_demStatus.setCurrentTargetIndex(1);
						thisActivity = "Starting to run forwards again from start";

					}

					// ok, just return our dem status object
					res = _demStatus;
					res.setTime(newTime);
					// thisActivity = "Starting to run forwards again from start";

				} else {
					// no, we don't want to go around again.
					res = null;
					_transit_complete = true;
					thisActivity = "Transit complete";
				}
			} else {
				// no, our route isn't marked as finished - just carry on
				// ok, just return our dem status object

				res = _demStatus;
				res.setTime(newTime);
				thisActivity = " Continuing to waypoint:" + (_demStatus.getCurrentTargetIndex() + 1);
			}
		} // whether our demanded status object is already in use
		else {
			// configure and use our demanded status objet
			// ok, just return our dem status object

			_demStatus.setCurrentTargetIndex(0);
			_demStatus.setRunInReverse(false);
			thisActivity = "Starting to run towards first waypoint";

			res = _demStatus;
			res.setTime(newTime);

		}

		super.setLastActivity(thisActivity);

		return res;

	}

	public int getCurrentDestinationIndex() {
		return _demStatus.getCurrentTargetIndex();
	}

	/**
	 * the set of destinations we follow
	 */
	public WorldPath getDestinations() {
		return _demStatus.getPath();
	}

	/**
	 * get the editor for this item
	 *
	 * @return the BeanInfo data for this editable object
	 */
	@Override
	public MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new TransitInfo(this);

		return _myEditor;
	}

	/**
	 * whether we loop through the destinations, switching to the first from the
	 * last
	 */
	public boolean getLoop() {
		return _loop;
	}

	/**
	 * when we are looping, whether we go back to the start, or if we pass through
	 * the destinations in reverse
	 */
	public boolean getReverse() {
		return _inReverse;
	}

	/**
	 * the speed we transit at (kts)
	 */
	public WorldSpeed getSpeed() {
		return _demStatus.getSpeed();
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: TransitWaypoint.java,v $
	 * Revision 1.1  2006/08/08 14:21:29  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:37  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.29  2005/04/15 14:11:57  Ian.Mayo
	 * Update tests to reflect new scenario step cycle
	 *
	 * Revision 1.28  2004/11/01 15:54:53  Ian.Mayo
	 * Reflect new signature of Track Plot Observer
	 * <p/>
	 * Revision 1.27  2004/09/02 13:17:28  Ian.Mayo
	 * Reflect CoreDecision handling the toString method
	 * <p/>
	 * Revision 1.26  2004/08/31 09:36:08  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.25  2004/08/26 16:26:54  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.24  2004/08/25 11:20:22  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.23  2004/08/20 13:32:21  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.22  2004/08/17 14:21:59  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.21  2004/08/16 09:16:13  Ian.Mayo
	 * Respect changed processing of tester recording to file (it needed a valid scenario object)
	 * <p/>
	 * Revision 1.20  2004/08/12 11:09:19  Ian.Mayo
	 * Respect observer classes refactored into tidy directories
	 * <p/>
	 * Revision 1.19  2004/08/09 15:50:27  Ian.Mayo
	 * Refactor category types into Force, Environment, Type sub-classes
	 * <p/>
	 * Revision 1.18  2004/08/06 12:51:56  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.17  2004/08/06 11:14:18  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.16  2004/05/24 15:46:39  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.2  2004/04/08 20:27:17  ian
	 * Restructured contructor for CoreObserver
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:51  ian
	 * no message
	 * <p/>
	 * Revision 1.15  2004/02/18 08:47:13  Ian.Mayo
	 * Sync from home
	 * <p/>
	 * Revision 1.13  2003/12/10 16:15:40  Ian.Mayo
	 * More tests
	 * <p/>
	 * Revision 1.12  2003/12/08 13:17:50  Ian.Mayo
	 * Update testing for completed OnTop alg
	 * <p/>
	 * Revision 1.11  2003/11/19 15:48:47  Ian.Mayo
	 * minor tidying
	 * <p/>
	 * Revision 1.10  2003/11/19 10:10:12  Ian.Mayo
	 * Clear demanded status at end of route
	 * <p/>
	 * Revision 1.9  2003/11/05 09:20:11  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
	}

	public WaypointVisitor getVisitor() {
		return _demStatus.getVisitType();
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 *
	 * @return yes/no
	 */
	@Override
	public boolean hasEditor() {
		return true;
	}

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 *
	 * @param currentStatus
	 */
	@Override
	public void interrupted(final Status currentStatus) {
		// ignore.
	}

	public boolean isFinished() {
		return _transit_complete;
	}

	/**
	 * reset this decision model
	 */
	@Override
	public void restart() {
		_transit_complete = false;
		_demStatus.setCurrentTargetIndex(0);
	}

	/**
	 * the set of destinations we follow
	 */
	public void setDestinations(final WorldPath newDestinations) {
		_demStatus.setPath(newDestinations);
	}

	/**
	 * whether we loop through the destinations, switching to the first from the
	 * last
	 */
	public void setLoop(final boolean newLoop) {
		_loop = newLoop;
	}

	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * when we are looping, whether we go back to the start, or if we pass through
	 * the destinations in reverse
	 */
	public void setReverse(final boolean val) {
		_inReverse = val;
	}

	/**
	 * the speed we transit at (m_sec)
	 */
	public void setSpeed(final WorldSpeed newSpeed) {
		_demStatus.setSpeed(newSpeed);

	}

}