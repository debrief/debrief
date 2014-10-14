/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;

public class CourseAnalysisContribution extends
		BaseAnalysisContribution<CourseRange>
{
	private static final long serialVersionUID = 1L;

	public CourseAnalysisContribution()
	{
		super();
		setName("Course Analysis");
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}

	@Override
	protected void applyThis(BoundedState state, CourseRange thisState)
			throws IncompatibleStateException
	{
		state.constrainTo(thisState);
	}

	@Override
	protected CourseRange getRangeFor(BoundedState lastStateWithRange)
	{
		return lastStateWithRange.getCourse();
	}

	@Override
	protected CourseRange cloneRange(CourseRange thisRange)
	{
		return new CourseRange(thisRange);
	}
		
	@Override
	protected void furtherConstrain(CourseRange currentLegCourse,
			CourseRange thisRange) throws IncompatibleStateException
	{
		currentLegCourse.constrainTo(thisRange);
	}
	
	protected CourseRange calcRelaxedRange(BoundedState lastStateWithRange,
			VehicleType vType, long millis)
	{
		// just in case we're doing a reverse pass, use the abs millis
		millis = Math.abs(millis);
		
		double maxRate = vType.getMaxTurnRate();

		// ok, we need to produce a new course constraint
		CourseRange lastKnown = lastStateWithRange.getCourse();

		double newMin, newMax;

		// there's a challenging problem with expanding course data:
		// 1. if the range is 5 to 100, and maxTurn is 15, then the new
		// range is -10 to 111. fine.
		// 2. but, if the range is 5 to 355 and maxTurn is 14 then the new
		// range would be -10 to +10, which doesn't make sense.
		// 3. we need a fancy bit of logic/clipping to resolve this.
		// I suspect that it's something like if the outer range delta plus
		// two times maxTurn is greater than 360, then the acceptable range
		// is 0 to 360.
		double turnDelta = lastKnown.getMax() - lastKnown.getMin();

		// special case. if the delta is -ve, put it into the +ve domain. We
		// need to do this
		// to handle the case when we have a very large pie slice (over
		// 1/2).
		// it's quite possible that once expanded, this large slice will
		// cover
		// the whole circle
		if (turnDelta < 0)
			turnDelta += Math.PI * 2;

		// how many rads?
		double maxTurn = maxRate * millis / 1000.0d;

		double newDelta = turnDelta + 2 * maxTurn;
		if (newDelta >= Math.PI * 2)
		{
			newMin = 0;
			newMax = Math.PI * 2;
		}
		else
		{
			newMin = lastKnown.getMin() - maxTurn;
			newMax = lastKnown.getMax() + maxTurn;
			if (newMax > Math.PI * 2)
				newMax -= Math.PI * 2;
		}

		CourseRange newRange = new CourseRange(newMin, newMax);
		return newRange;
	}
}
