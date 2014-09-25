package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;

public class Range1959ForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String MIN_COURSE = "minCourse";

	public static final String MAX_COURSE = "maxCourse";

	protected Double minCourse = 0d;

	protected Double maxCourse = 2 * Math.PI;

	protected Double estimate = 0d;

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// verify that we have min and max values present
		if ((minCourse != null) && (maxCourse != null))
		{

			// create a bounded state representing our values
			final CourseRange courseRange = new CourseRange(minCourse, maxCourse);
			for (BoundedState state : space.getBoundedStatesBetween(startDate,
					finishDate))
			{
				state.constrainTo(courseRange);
			}
		}
	}

	@Override
	protected double calcError(State thisState)
	{
		double delta = 0;

		// do we have an estimate?
		if (estimate != null  && minCourse != null & maxCourse != null)
		{
			CourseRange cr = new CourseRange(minCourse, maxCourse);
			delta = cr.calcErrorFor(this.getEstimate(), thisState.getCourse());
		}

		return delta;
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public Double getEstimate()
	{
		return estimate;
	}

	public Double getMaxCourse()
	{
		return maxCourse;
	}

	public Double getMinCourse()
	{
		return minCourse;
	}

	public void setEstimate(Double newEstimate)
	{
		Double oldEstimate = estimate;
		this.estimate = newEstimate;
		firePropertyChange(ESTIMATE, oldEstimate, newEstimate);
	}

	public void setMaxCourse(Double newMaxCourse)
	{
		Double oldMaxCourse = maxCourse;
		this.maxCourse = newMaxCourse;
		firePropertyChange(MAX_COURSE, oldMaxCourse, newMaxCourse);
		firePropertyChange(HARD_CONSTRAINTS, oldMaxCourse, newMaxCourse);
	}

	public void setMinCourse(Double newMinCourse)
	{
		Double oldMinCourse = minCourse;
		this.minCourse = newMinCourse;
		firePropertyChange(MIN_COURSE, oldMinCourse, newMinCourse);
		firePropertyChange(HARD_CONSTRAINTS, oldMinCourse, newMinCourse);
	}

}
