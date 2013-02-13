package com.planetmayo.debrief.satc.model.contributions;

import java.util.ArrayList;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.VehicleType;
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

		// remember the current bounded course
		CourseRange currentLegCourse = null;

		// keep track of the states in this leg - so we can apply the common course
		// bounds to them
		ArrayList<BoundedState> statesInThisLeg = new ArrayList<BoundedState>();

		// get the vehicle type
		VehicleType vType = space.getVehicleType();

		// PHASE 1 - Produce course bounds when none are present

		// ok, loop through the states, setting course limits for any unbounded
		// courses
		for (BoundedState currentState : space.states())
		{
			String thisLeg = currentState.getMemberOf();

			// ok, is this in a leg?
			if (thisLeg == null)
			{
				// ok, have we just finished a leg?
				if (!statesInThisLeg.isEmpty())
				{
					assignCommonState(currentLegCourse, statesInThisLeg);
					currentLegCourse = null;
				}

				// ok, we're not in a leg. relax it.
				lastStateWithCourse = applyRelaxedCourseBounds(lastStateWithCourse,
						currentState, vType);
			}
			else
			{
				// ok, we're in a leg - see if this is the first point (in which case we
				// allow relaxation),
				// or a successive point (in which case we just constrain)

				// is this the first item in this leg
				if (statesInThisLeg.isEmpty())
				{
					// yes. this is the first leg. We do need to allow some relaxation
					// on course from the previous one
					lastStateWithCourse = applyRelaxedCourseBounds(lastStateWithCourse,
							currentState, vType);

					// now we can probably store this state
					if (lastStateWithCourse != null)
						currentLegCourse = lastStateWithCourse.getCourse();
				}
				else
				{
					// ok, we won't be relaxing this leg - we're just going to further
					// constrain the course range

					// does this have a course consraint?
					CourseRange thisC = currentState.getCourse();
					if (thisC != null)
					{
						if (currentLegCourse == null)
						{
							// ok, store it
							currentLegCourse = new CourseRange(thisC);
						}
						else
						{
							// ok, constrain it.
							currentLegCourse.constrainTo(thisC);
						}
					}
				}

				// ok, remmber this leg
				statesInThisLeg.add(currentState);
			}
		}

		// ok, do we have a dangling set of states to be assigned?
		if (!statesInThisLeg.isEmpty())
		{
			assignCommonState(currentLegCourse, statesInThisLeg);
			currentLegCourse = null;
		}
	}

	/**
	 * apply this course constraint to all to all of the states in the list
	 * 
	 * @param commonCourse
	 * @param statesInThisLeg
	 * @throws IncompatibleStateException
	 */
	private void assignCommonState(CourseRange commonCourse,
			ArrayList<BoundedState> statesInThisLeg)
			throws IncompatibleStateException
	{
		// ok - apply the common bounded course to all the states in the leg

		// have we produced a constrained course?
		if (commonCourse != null)
		{
			// yes. we need to apply the min constraints to all the points in
			// that leg
			Iterator<BoundedState> iter = statesInThisLeg.iterator();
			while (iter.hasNext())
			{
				BoundedState boundedState = (BoundedState) iter.next();
				boundedState.constrainTo(commonCourse);
			}
		}

		// now we need to clear the remembered states, so we're ready for the
		// next leg
		statesInThisLeg.clear();
	}

	public BoundedState applyRelaxedCourseBounds(
			BoundedState lastStateWithCourse, BoundedState currentState,
			VehicleType vehicleType) throws IncompatibleStateException
	{
		// do we have vehicle reference data?
		if (vehicleType != null)
		{
			double maxRate = vehicleType.getMaxTurnRate();

			// do we have a previous state?
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

				// special case. if the delta is -ve, put it into the +ve domain. We
				// need to do this
				// to handle the case when we have a very large pie slice (over
				// 1/2).
				// it's quite possible that once expanded, this large slice will
				// cover
				// the whole circle
				if (turnDelta < 0)
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
				currentState.constrainTo(new CourseRange(newMin, newMax));

			}
		}

		BoundedState newLastState = null;

		// did we find one?
		if (currentState.getCourse() != null)
		{
			newLastState = currentState;
		}
		else
		{
			newLastState = lastStateWithCourse;
		}

		return newLastState;

	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}
}
