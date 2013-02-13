package com.planetmayo.debrief.satc.model.contributions;

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
		BoundedState lastStateWithCourse = null;

		// PHASE 1 - Produce course bounds when none are present

		// do we have vehicle reference data?
		if (space.getVehicleType() != null)
		{
			double maxRate = space.getVehicleType().getMaxTurnRate();			
			// ok, loop through the states, setting course limits for any unbounded
			// courses			
			for (BoundedState currentState : space.states())
			{
				// ok, do we have a previous state
				if (lastStateWithCourse != null)
				{
					// ok, how long since that last observation?
					long millis = currentState.getTime().getTime()
							- lastStateWithCourse.getTime().getTime();

					// how many rads?
					double maxTurn = maxRate * millis / 1000.0d;

					// ok, we need to produce a new course constraint
					CourseRange lastKnown = lastStateWithCourse.getCourse();

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
					
					// special case. if the delta is -ve, put it into the +ve domain. We need to do this
					// to handle the case when we have a very large pie slice (over 1/2). 
					// it's quite possible that once expanded, this large slice will cover the whole circle
					if(turnDelta < 0)
						turnDelta += Math.PI * 2;
					
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
						if (newMax > Math.PI * 2)
							newMax -= Math.PI * 2;
					}

					// and create a constraint for the new valus
					CourseRange newCourse = new CourseRange(newMin, newMax);

					// and apply it
					currentState.constrainTo(newCourse);
				}
				// ok, do we now have course data?
				if (currentState.getCourse() != null)
					lastStateWithCourse = currentState;
			}
		}

		// PHASE 2: look at successive bounded locations. Constrain course to what
		// is
		// achievable to get between location bounds

	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}
}
