package com.planetmayo.debrief.satc.model.contributions;

import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class CourseAnalysisContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public CourseAnalysisContribution()
	{
		super();
		setName("Course Analysis");
	}

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// remember the previous state
		BoundedState _lastStateWithCourse = null;

		// PHASE 1 - Produce course bounds when none are present

		// do we have vehicle reference data?
		if (space.getVehicleType() != null)
		{
			// ok, loop through the states, setting course limits for any unbounded
			// courses
			Iterator<BoundedState> iter = space.states().iterator();
			while (iter.hasNext())
			{
				BoundedState thisS = iter.next();

				// does it have course bounds?
				if (thisS.getCourse() == null)
				{
					// nope, let's see if we can calculate one

					// ok, do we have a previous state
					if (_lastStateWithCourse != null)
					{
						// yes we do, let's see how far it could have turned

						// ok, how long since that last observation?
						long millis = thisS.getTime().getTime()
								- _lastStateWithCourse.getTime().getTime();

						// ok, what's the most it could have turned in this time?
						double maxRate = space.getVehicleType().getMaxTurnRate();

						// how many rads?
						double maxTurn = maxRate * millis;

						// ok, we need to produce a new course constraint
						CourseRange lastKnown = _lastStateWithCourse.getCourse();

						final double newMin, newMax;

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
						double newDelta = turnDelta + 2 * maxTurn;
						if (newDelta > Math.PI * 2)
						{
							newMin = 0;
							newMax = Math.PI * 2;
						}
						else
						{
							newMin = lastKnown.getMin() - maxTurn;
							newMax = lastKnown.getMax() + maxTurn;
						}

						// and create a constraint for the new valus
						CourseRange newCourse = new CourseRange(newMin, newMax);

						// and apply it
						thisS.constrainTo(newCourse);
					}
				}
				else
				{
					if (thisS != null)
						_lastStateWithCourse = thisS;
				}
			}
		}
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}
}
