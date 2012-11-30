package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class CourseForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String MIN_COURSE = "minCourse";

	public static final String MAX_COURSE = "maxCourse";

	protected Integer _minCourse = 0;

	protected Integer _maxCourse = 360;

	protected Integer _estimate = 0;

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// create a bounded state representing our values
		final CourseRange courseRange = new CourseRange(_minCourse, _maxCourse);
		for (BoundedState state : space.getBoundedStatesBetween(_startDate, _finishDate))
		{
			state.constrainTo(courseRange);
		}
		if (_startDate != null && space.getBoundedStateAt(_startDate) == null)
		{
			final BoundedState startState = new BoundedState(this.getStartDate());
			startState.constrainTo(courseRange);
			space.add(startState);
		}
		if (_finishDate != null && space.getBoundedStateAt(_finishDate) == null)
		{
			final BoundedState endState = new BoundedState(this.getFinishDate());
			endState.constrainTo(courseRange);
			space.add(endState);
		}		
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public Integer getEstimate()
	{
		return _estimate;
	}

	@Override
	public String getEstimateStr()
	{
		return "" + (_estimate == null ? "" : _estimate.intValue());
	}

	@Override
	public String getHardConstraints()
	{
		String min = _minCourse == null ? "0" : "" + _minCourse.intValue();
		String max = _maxCourse == null ? "360" : "" + _maxCourse.intValue();
		return min + (min.equals(max) ? "" : " - " + max);
	}

	public Integer getMaxCourse()
	{
		return _maxCourse;
	}

	public Integer getMinCourse()
	{
		return _minCourse;
	}

	public void setEstimate(Integer estimate)
	{
		Integer oldEstimate = _estimate;
		this._estimate = estimate;
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setMaxCourse(Integer maxCourse)
	{
		Integer oldMaxCourse = _maxCourse;
		String oldConstraints = getHardConstraints();
		this._maxCourse = maxCourse;
		firePropertyChange(MAX_COURSE, oldMaxCourse, maxCourse);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	public void setMinCourse(Integer minCourse)
	{
		Integer oldMinCourse = _minCourse;
		String oldConstraints = getHardConstraints();
		this._minCourse = minCourse;
		firePropertyChange(MIN_COURSE, oldMinCourse, minCourse);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

}
