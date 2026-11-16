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

package ASSET.Models.Decision.Movement;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian Mayo
 * Date: 2003
 * Log:
 *  $Log: Transit.java,v $
 *  Revision 1.1  2006/08/08 14:21:28  Ian.Mayo
 *  Second import
 *
 *  Revision 1.1  2006/08/07 12:25:37  Ian.Mayo
 *  First versions
 *
 *  Revision 1.22  2004/10/29 09:23:07  Ian.Mayo
 *  Correct tests
 *
 *  Revision 1.21  2004/10/28 14:52:55  ian
 *  Insert couple of more comments
 *
 *  Revision 1.20  2004/09/02 13:17:27  Ian.Mayo
 *  Reflect CoreDecision handling the toString method
 *
 *  Revision 1.19  2004/08/31 09:36:06  Ian.Mayo
 *  Rename inner static tests to match signature **Test to make automated testing more consistent
 *
 *  Revision 1.18  2004/08/26 16:26:52  Ian.Mayo
 *  Implement editable properties
 *
 *  Revision 1.17  2004/08/25 11:20:19  Ian.Mayo
 *  Remove main methods which just run junit tests
 *
 *  Revision 1.16  2004/08/20 15:08:18  Ian.Mayo
 *  Part way through changing detection cycle so that it doesn't start afresh each time - each sensor removes it's previous calls the next time it is called (to allow for TBDO)
 *
 *  Revision 1.15  2004/08/20 13:32:19  Ian.Mayo
 *  Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
 *
 *  Revision 1.14  2004/08/17 14:21:58  Ian.Mayo
 *  Refactor to introduce parent class capable of storing name & isActive flag
 *
 *  Revision 1.13  2004/08/06 12:51:55  Ian.Mayo
 *  Include current status when firing interruption
 *
 *  Revision 1.12  2004/08/06 11:14:17  Ian.Mayo
 *  Introduce interruptable behaviours, and recalc waypoint route after interruption
 *
 *  Revision 1.11  2004/05/24 15:46:37  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:30:51  ian
 *  no message
 *
 *  Revision 1.10  2003/11/05 09:20:10  Ian.Mayo
 *  Include MWC Model support
 *
 *  Revision 1.9  2003/09/19 13:37:59  Ian.Mayo
 *  Switch to Speed and Distance objects instead of just doubles
 *
 *  Revision 1.8  2003/09/18 14:11:53  Ian.Mayo
 *  Make tests work with new World Speed class
 *
 *  Revision 1.7  2003/09/18 12:12:38  Ian.Mayo
 *  Reflect introduction of World Speed
 *
 *  Revision 1.6  2003/09/11 10:15:04  Ian.Mayo
 *  Change finish time following updated movement cycle
 *
 *  Revision 1.5  2003/09/09 15:55:29  Ian.Mayo
 *  Change signature of decision model
 *
 *  Revision 1.4  2003/09/08 13:08:09  Ian.Mayo
 *  provide is finished accessor
 *
 *  Revision 1.3  2003/09/04 14:41:02  Ian.Mayo
 *  Implement modelling guide tests
 *
 *
 */

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * behaviour to travel through a series of points at an indicated speed.
 * Optionally the journey may be repeated, in forward or reverse direction.
 */
public class Transit extends CoreDecision implements java.io.Serializable {

	//////////////////////////////////////////////////////////////////////
	// Member Variables
	//////////////////////////////////////////////////////////////////////

	static public class TransitInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public TransitInfo(final Transit data) {
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
	 * our transit speed for this passage
	 */
	private WorldSpeed _mySpeed;

	/**
	 * our destination
	 */
	private WorldPath _myDestinations;

	/**
	 * the current destination
	 */
	protected int _currentDestination = 0;

	/**
	 * the threshold to use to decide if we are at our destination (yds)
	 */
	protected WorldDistance _threshold = new WorldDistance(500, WorldDistance.YARDS);

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
	 * whether we are currently performing the "reverse-loop", heading back to the
	 * start
	 */
	protected boolean _in_reverse_transit = false;

	/**
	 * a local copy of our editable object
	 */
	private MWC.GUI.Editable.EditorType _myEditor = null;

	//////////////////////////////////////////////////////////////////////
	// Constructor
	//////////////////////////////////////////////////////////////////////

	/*
	 *
	 * @param destinations
	 *
	 * @param transit_speed
	 *
	 * @param loop
	 */

	/**
	 * whether this represents a single transit, and is now complete
	 */
	protected boolean _transit_complete = false;

	/**
	 * whether we're heading towards the next data point yet. We decide we've passed
	 * it when it's behind us, but we can only do that check once it's come in front
	 * of us - otherwise we'd immediately 'pass' the next point if it's behind us
	 */
	protected boolean _isAhead = false;

	//////////////////////////////////////////////////////////////////////
	// Member methods
	//////////////////////////////////////////////////////////////////////

	public Transit() {
		this(null, null, false);
	}

	public Transit(final WorldPath destinations, final WorldSpeed transit_speed, final boolean loop) {
		super("Transit");
		_myDestinations = destinations;
		_mySpeed = transit_speed;
		_loop = loop;
	}

	@Override
	public ASSET.Participants.DemandedStatus decide(final ASSET.Participants.Status status,
			final ASSET.Models.Movement.MovementCharacteristics chars, final DemandedStatus demStatus,
			final ASSET.Models.Detection.DetectionList detections, final ASSET.Scenario.ScenarioActivityMonitor monitor,
			final long time) {
		// create the output object
		DemandedStatus res;

		// have we completed this manoeuvre?
		if (_transit_complete) {
			res = null;
			return res;
		}

		// ok, determine if we are ready to switch to the next point
		final boolean passedNewPoint = determineProgress(status);

		// ok, determine the demanded status to get to the next point
		res = getDemandedStatus(status, chars, demStatus, detections, monitor, time, passedNewPoint);

		// done.
		return res;

	}

	/**
	 * ok, have a look at where we are, moving us on to the next destination if
	 * necessary
	 *
	 * @param status current participant location/speed
	 * @return the next target
	 */
	protected boolean determineProgress(final Status status) {
		boolean passedNewPoint = false;

		// just check that we aren't heading for the last point, after the last point
		// has been removed
		_currentDestination = Math.min(_currentDestination, _myDestinations.size() - 1);

		// where are we heading for?
		final WorldLocation _myDestination = _myDestinations.getLocationAt(_currentDestination);

		// how far away from it are we?
		final WorldVector offset = _myDestination.subtract(status.getLocation());

		// nope, sort out the bearing to the target
		final double crse = status.getCourse();

		// and what's the bearing
		final double brg = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());

		// what's the delta
		double delta = brg - crse;
		while (delta > 180)
			delta -= 360;
		while (delta < -180)
			delta += 360;

		// right, are we heading towards the next point yet
		if (_isAhead) {
			// yes, we now have to check if we've passed it
			// right, is it ahead of us?
			if (Math.abs(delta) > 90d) {
				// ok, reset the ahead flag
				_isAhead = false;

				// cool, that's that point reached
				passedNewPoint = true;

				// are we heading forwards, or reverse?
				if (_in_reverse_transit) {
					// we are currently performing reverse transit. get the next ppint

					// are we back at the start?
					if (_currentDestination == 0) {
						// yes, switch back to working forwards through the data
						_in_reverse_transit = false;

						// head for the first point (or ourselves, if there's only one item in the list)
						_currentDestination = Math.min(1, _myDestinations.size() - 1);
					} else {
						// work back to the previous point
						_currentDestination--;
					}
				} else {
					// we are working forwards, carry on!

					// have we any destinations left?
					if (_currentDestination < _myDestinations.size() - 1) {
						// ok, now go to the next destination
						_currentDestination++;
					} else {
						// no, we're at the last destination.
						// are we looping?
						if (_loop) {
							// yes, are we going forwards or back?
							if (_inReverse) {
								// ok, start going backwards
								_in_reverse_transit = true;

								// head for the previous destination in the list (we know we are already on the
								// last
								// one)
								_currentDestination--;

								// just check that we aren't in a path containing only one point
								// : ensure it's zero
								_currentDestination = Math.max(_currentDestination, 0);
							} else {
								// head back for the start
								_currentDestination = 0;
							}

							performReset();
						} else {
							// no, we are not looping
							_transit_complete = true;
							_currentDestination = -1;
						} // whether we are looping
					} // whether we have reached the last destination in the list
				} // are we heading forwards or backwards?
			}
		} else {
			// right, is it ahead of us?
			if (Math.abs(delta) < 90d)
				_isAhead = true;
		}

		return passedNewPoint;
	}

	/**
	 * get the current destination
	 */
	public WorldLocation getCurrentDestination() {
		return _myDestinations.getLocationAt(_currentDestination);
	}

	/**
	 * get the inded of the current destination
	 */
	public int getCurrentDestinationIndex() {
		return _currentDestination;
	}
	////////////////////////////////////////////////////////////
	// model support
	////////////////////////////////////////////////////////////

	/**
	 * ok, we now know where we are, and where we're heading for. produce a demanded
	 * status
	 *
	 * @param status         where we are
	 * @param chars          our movement characteristics
	 * @param demStatus      the current demanded status
	 * @param detections     the current set of detections
	 * @param monitor        the scenario helper
	 * @param time           the current time
	 * @param passedNewPoint flag to indicate if we've just passed a new waypoint
	 * @return demanded course to the next location
	 */
	protected DemandedStatus getDemandedStatus(final ASSET.Participants.Status status,
			final ASSET.Models.Movement.MovementCharacteristics chars, final DemandedStatus demStatus,
			final ASSET.Models.Detection.DetectionList detections, final ASSET.Scenario.ScenarioActivityMonitor monitor,
			final long time, final boolean passedNewPoint) {

		DemandedStatus res = null;
		WorldLocation currentDest = null;

		// produce vector to this new location
		// have we finished?
		if (_transit_complete) {
			// hey, don't bother, we're finished
			super.setLastActivity("Transit complete");
		} else {
			// initialise the demanded status
			final SimpleDemandedStatus theRes = new SimpleDemandedStatus(time, status);

			// update the destination, just in case we have just passed one of our points
			currentDest = _myDestinations.getLocationAt(_currentDestination);

			// produce a vector to the destination
			final WorldVector vec = currentDest.subtract(status.getLocation());

			// set the bearing
			final double brg = MWC.Algorithms.Conversions.Rads2Degs(vec.getBearing());
			theRes.setCourse(brg);

			// set the correct depth for this leg
			theRes.setHeight(-currentDest.getDepth());

			// maintain speed
			if (_mySpeed != null)
				theRes.setSpeed(_mySpeed.getValueIn(WorldSpeed.M_sec));

			// and store the new demanded status
			res = theRes;

			super.setLastActivity("Transit heading for waypoint:" + (_currentDestination + 1));
		}

		return res;
	}

	/**
	 * the set of destinations we follow
	 */
	public MWC.GenericData.WorldPath getDestinations() {
		return _myDestinations;
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
		return _mySpeed;
	}

	/**
	 * the threshold we use to decide if we are at our location (yds)
	 */
	public WorldDistance getThreshold() {
		return _threshold;
	}

	/**
	 * get the version details for this model.
	 *
	 * <pre>
	 * $Log: Transit.java,v $
	 * Revision 1.1  2006/08/08 14:21:28  Ian.Mayo
	 * Second import
	 *
	 * Revision 1.1  2006/08/07 12:25:37  Ian.Mayo
	 * First versions
	 *
	 * Revision 1.22  2004/10/29 09:23:07  Ian.Mayo
	 * Correct tests
	 *
	 * Revision 1.21  2004/10/28 14:52:55  ian
	 * Insert couple of more comments
	 *
	 * Revision 1.20  2004/09/02 13:17:27  Ian.Mayo
	 * Reflect CoreDecision handling the toString method
	 * <p/>
	 * Revision 1.19  2004/08/31 09:36:06  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.18  2004/08/26 16:26:52  Ian.Mayo
	 * Implement editable properties
	 * <p/>
	 * Revision 1.17  2004/08/25 11:20:19  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.16  2004/08/20 15:08:18  Ian.Mayo
	 * Part way through changing detection cycle so that it doesn't start afresh each time - each sensor removes it's previous calls the next time it is called (to allow for TBDO)
	 * <p/>
	 * Revision 1.15  2004/08/20 13:32:19  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.14  2004/08/17 14:21:58  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.13  2004/08/06 12:51:55  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.12  2004/08/06 11:14:17  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.11  2004/05/24 15:46:37  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:51  ian
	 * no message
	 * <p/>
	 * Revision 1.10  2003/11/05 09:20:10  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	@Override
	public String getVersion() {
		return "$Date$";
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

	/**
	 * indicate if we have reached the end of the route
	 *
	 * @return yes/no
	 */
	public boolean isFinished() {
		return _transit_complete;
	}

	/**
	 * we've reached the end, and we're set to continue. do any reset necesary
	 *
	 */
	protected void performReset() {
	}

	/**
	 * reset this decision model
	 */
	@Override
	public void restart() {
		_currentDestination = 0;
		_in_reverse_transit = false;
		_transit_complete = false;
	}

	/**
	 * specify which location the vehicle is currently heading to
	 *
	 * @param destination
	 */
	public void setCurrentDestination(final int destination) {
		_currentDestination = destination;
	}

	/**
	 * the set of destinations we follow
	 */
	public void setDestinations(final MWC.GenericData.WorldPath newDestinations) {
		_myDestinations = newDestinations;
	}

	/**
	 * whether we loop through the destinations, switching to the first from the
	 * last
	 */
	public void setLoop(final boolean newLoop) {
		_loop = newLoop;
	}

	/**
	 * when we are looping, whether we go back to the start, or if we pass through
	 * the destinations in reverse
	 */
	public void setReverse(final boolean val) {
		_inReverse = val;
	}

	/**
	 * the speed we transit at (kts)
	 */
	public void setSpeed(final WorldSpeed newSpeed) {
		_mySpeed = newSpeed;
	}

	/**
	 * the threshold we use to decide if we are at our location (yds)
	 */
	public void setThreshold(final WorldDistance val) {
		_threshold = val;
	}

}