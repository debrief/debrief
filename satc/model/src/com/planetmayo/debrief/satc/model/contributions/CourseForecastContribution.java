package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;

public class CourseForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String MIN_COURSE = "minCourse";

	public static final String MAX_COURSE = "maxCourse";

	protected Double _minCourse = 0d;

	protected Double _maxCourse = 2 * Math.PI;

	protected Double _estimate = 0d;

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// create a bounded state representing our values
		final CourseRange courseRange = new CourseRange(_minCourse, _maxCourse);
		for (BoundedState state : space.getBoundedStatesBetween(_startDate, _finishDate))
		{
			state.constrainTo(courseRange);
		}
	}
	
	@Override
	protected double calcError(State thisState)
	{
		// TODO IAN HIGH calculate the course error (making sure they're in the same 'domain')
		return super.calcError(thisState);
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public Double getEstimate()
	{
		return _estimate;
	}

	public Double getMaxCourse()
	{
		return _maxCourse;
	}

	public Double getMinCourse()
	{
		return _minCourse;
	}

	public void setEstimate(Double estimate)
	{
		Double oldEstimate = _estimate;
		this._estimate = estimate;
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setMaxCourse(Double maxCourse)
	{
		Double oldMaxCourse = _maxCourse;
		this._maxCourse = maxCourse;
		firePropertyChange(MAX_COURSE, oldMaxCourse, maxCourse);
		firePropertyChange(HARD_CONSTRAINTS,  oldMaxCourse, maxCourse);
	}

	public void setMinCourse(Double minCourse)
	{
		Double oldMinCourse = _minCourse;
		this._minCourse = minCourse;
		firePropertyChange(MIN_COURSE, oldMinCourse, minCourse);
		firePropertyChange(HARD_CONSTRAINTS,  oldMinCourse, minCourse);
	}

}
