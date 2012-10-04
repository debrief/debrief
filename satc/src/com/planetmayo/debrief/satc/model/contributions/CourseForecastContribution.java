package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.CourseRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class CourseForecastContribution extends BaseContribution implements BaseContribution.ForecastMarker
{

	public static final String MIN_COURSE = "minCourse";

	public static final String MAX_COURSE = "maxCourse";

	/**
	 * utility method to create one of these contributions
	 * 
	 * @return
	 */
	public static CourseForecastContribution getSample()
	{
		CourseForecastContribution res = new CourseForecastContribution();
		res.setName("Approaching Buoy");
		res.setActive(true);
		res.setWeight(7);
		res.setStartDate(new Date(1111110033120L));
		res.setFinishDate(new Date(System.currentTimeMillis() - 111111000));
		res.setMinCourse(10);
		res.setMaxCourse(60);
		res.setEstimate(25);

		return res;
	}

	protected int _minCourse;

	protected int _maxCourse;

	protected int _estimate;

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// create a bounded state representing our values
		final CourseRange myR = new CourseRange(_minCourse, _maxCourse);

		// remember if we've found items at our start/end times
		boolean needToInjectStart = true;
		boolean needToInjectFinish = true;

		// loop through the states
		final Iterator<BoundedState> sIter = space.states();
		while (sIter.hasNext())
		{
			// get the next state
			final BoundedState state = sIter.next();

			// apply our bounds
			state.constrainTo(myR);

			// is this one of our end-terms?
			final Date thisT = state.getTime();

			// do we have a start time?
			if (this.getStartDate() != null)
			{
				if (thisT.equals(this.getStartDate()))
				{
					// cool, store it
					needToInjectStart = false;
				}
			}

			// do we have an end time?
			if (this.getFinishDate() != null)
			{
				if (thisT.equals(this.getFinishDate()))
				{
					needToInjectFinish = false;
				}
			}
		}

		// ok, did we find our end terms?
		if (needToInjectStart)
		{
			final BoundedState startState = new BoundedState(this.getStartDate());
			startState.constrainTo(myR);
			space.add(startState);
		}

		// ok, did we find our end terms?
		if (needToInjectFinish)
		{
			final BoundedState endState = new BoundedState(this.getFinishDate());
			endState.constrainTo(myR);
			space.add(endState);
		}

	}

	public int getEstimate()
	{
		return _estimate;
	}

	@Override
	public String getHardConstraints()
	{
		return "" + _minCourse + " - " + _maxCourse;
	}

	public int getMaxCourse()
	{
		return _maxCourse;
	}

	public int getMinCourse()
	{
		return _minCourse;
	}

	public void setEstimate(int estimate)
	{
		firePropertyChange(ESTIMATE, _estimate, estimate);
		this._estimate = estimate;
	}

	public void setMaxCourse(int maxCourse)
	{
		firePropertyChange(MAX_COURSE, _maxCourse, maxCourse);
		String oldConstraints = getHardConstraints();
		this._maxCourse = maxCourse;
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	public void setMinCourse(int minCourse)
	{
		firePropertyChange(MIN_COURSE, _minCourse, minCourse);
		String oldConstraints = getHardConstraints();
		this._minCourse = minCourse;
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}
}
