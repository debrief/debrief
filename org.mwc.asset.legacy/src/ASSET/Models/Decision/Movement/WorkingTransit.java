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

import ASSET.Models.Decision.Sequence;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.GenericData.WorldPath;
import MWC.GenericData.WorldSpeed;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 28-Oct-2004 Time: 11:43:09 To
 * change this template use File | Settings | File Templates.
 */
public class WorkingTransit extends Transit {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * what we're doing
	 *
	 */
	private static final String PERFORMING_ACTIVITY = "Performing activity:";

	/**
	 * what we do each time we reach a waypoint
	 */
	private Sequence _myActivity;

	/**
	 * whether we are currently doing one of our activities
	 */
	private boolean _busy;

	/****************************************************
	 * constructor
	 ***************************************************/

	/**
	 * the number of stops to take along the way
	 *
	 */
	private int _numStops;

	/****************************************************
	 * member methods
	 ***************************************************/

	/**
	 * constructor = more realistic usage
	 *
	 * @param myActivity    what to do each time we stop
	 * @param destinations  the core path of points to pass through
	 * @param transit_speed how fast to travel
	 * @param loop          whether to loop around again at the end
	 * @param numStops      how many stops to make
	 */
	public WorkingTransit(final Sequence myActivity, final WorldPath destinations, final WorldSpeed transit_speed,
			final boolean loop, final int numStops) {
		// ok, store the things we understand
		this._myActivity = myActivity;
		super.setSpeed(transit_speed);
		super.setLoop(loop);

		// do we have a number of stops
		if (numStops > 0) {
			// ok, break down the path into the correct number of stops
			final WorldPath brokenDownPath = destinations.breakIntoStraightSections(numStops);
			super.setDestinations(brokenDownPath);
		} else {
			super.setDestinations(destinations);
		}
	}

	/**
	 * ok, have a look at where we are, moving us on to the next destination if
	 * necessary
	 *
	 * @param status current participant location/speed
	 * @return the next target
	 */
	@Override
	protected boolean determineProgress(final Status status) {
		boolean passedNewPoint = false;

		// ok, see if we are currently doing one of our activities
		if (!_busy) {
			passedNewPoint = super.determineProgress(status);

			// hmm, did we reach the next check point?
		}

		return passedNewPoint;
	}

	@Override
	protected DemandedStatus getDemandedStatus(final ASSET.Participants.Status status,
			final ASSET.Models.Movement.MovementCharacteristics chars, final DemandedStatus demStatus,
			final ASSET.Models.Detection.DetectionList detections, final ASSET.Scenario.ScenarioActivityMonitor monitor,
			final long time, final boolean passedNewPoint) {
		DemandedStatus res = null;

		// first, check that we haven't finished
		if (!isFinished()) {

			// have we just passed a new waypoint?
			if (passedNewPoint) {
				// yes, mark ourselves as busy
				_busy = true;

				// and reset the sequence - so we start to run through it again.
				_myActivity.restart();
			}

			// ok, are we currently doing our busy processing?
			if (_busy) {
				// yes we are. fire it again
				res = _myActivity.decide(status, chars, demStatus, detections, monitor, time);

				// check if we have now finished
				if (res == null) {
					// yup, we're not busy any more then
					_busy = false;
				} else {
					super.setLastActivity(PERFORMING_ACTIVITY + _myActivity.getActivity());
				}

			}

			// ok, are we busy in our activity
			if (res == null) {
				// no, carry on and
				// plot a course to the next destination
				res = super.getDemandedStatus(status, chars, demStatus, detections, monitor, time, passedNewPoint);
			}
		}

		return res;
	}

	/**
	 * control the number of times we stop along the route
	 *
	 * @return _numStops
	 */
	public int getNumStops() {
		return _numStops;
	}

	/**
	 * control what we do at each point
	 *
	 * @return
	 */
	public Sequence getWorkingActivity() {
		return _myActivity;
	}

	@Override
	protected void performReset() {
		super.performReset();

		// and reset our activity
		if (_myActivity != null)
			_myActivity.restart();
	}

	/**
	 * control the number of times we stop along the route
	 *
	 * @param _numStops
	 */
	public void setNumStops(final int _numStops) {
		this._numStops = _numStops;
	}

	/**
	 * control what we do at each point
	 *
	 * @param myActivity what we will do
	 */
	public void setWorkingActivity(final Sequence myActivity) {
		this._myActivity = myActivity;
	}

}
