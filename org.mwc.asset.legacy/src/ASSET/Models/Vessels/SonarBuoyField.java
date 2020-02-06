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

package ASSET.Models.Vessels;

import ASSET.ScenarioType;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.Status;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * Created by IntelliJ IDEA
 */
public class SonarBuoyField extends CoreParticipant {
	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the area covered by this buoy field
	 *
	 */
	private final WorldArea _myCoverage;

	private TimePeriod _myPeriod;

	/**
	 * normal constructor
	 *
	 * @param coverage
	 *
	 */
	public SonarBuoyField(final int id, final WorldArea coverage) {
		// create the participant bits
		super(id, null, null, null);

		// store the coverage
		_myCoverage = coverage;

		// and set our location to be the centre of the buoyfield
		this.getStatus().setLocation(getCoverage().getCentre());
	}

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	@Override
	public void doDetection(final long oldtime, final long newTime, final ScenarioType scenario) {
		// aaah, are we active?
		if (isActiveAt(newTime))
			super.doDetection(oldtime, newTime, scenario);
	}

	@Override
	public void doMovement(final long oldtime, final long newTime, final ScenarioType scenario) {
		super.doMovement(oldtime, newTime, scenario);

		// update our status
		this.getStatus().setTime(newTime);
	}

	/**
	 * return what this participant is currently doing
	 *
	 */
	@Override
	public String getActivity() {
		return "Active";
	}

	public WorldArea getCoverage() {
		return _myCoverage;
	}

	public TimePeriod getTimePeriod() {
		return _myPeriod;
	}

	/**
	 * convenience funtion for if we're currently active
	 *
	 * @param newTime current time
	 * @return yes/no
	 */
	public boolean isActiveAt(final long newTime) {
		boolean res = true;
		if (_myPeriod != null)
			res = _myPeriod.contains(new HiResDate(newTime));
		return res;
	}

	/**
	 * the range calculation goes from our area, not just a single point
	 *
	 */
	@Override
	public WorldDistance rangeFrom(final WorldLocation point) {
		double dist = 0;

		// right, measure the distance from the sides
		dist = getCoverage().rangeFromEdge(point);

		return new WorldDistance(dist, WorldDistance.DEGS);
	}

	@Override
	public void setStatus(final Status val) {
		if (val != null) {
			super.setStatus(val);
		} else {
			super.setStatus(new Status(0, 0));
		}
	}

	public void setTimePeriod(final TimePeriod period) {
		_myPeriod = period;
	}

}
