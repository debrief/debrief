package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;

public class SpeedForecastContribution extends BaseContribution
{

	public static final String MIN_SPEED = "minSpeed";

	public static final String MAX_SPEED = "maxSpeed";

	/**
	 * utility method to create one of these contributions
	 * 
	 * @return
	 */
	public static BaseContribution getSample()
	{
		BaseContribution res = new SpeedForecastContribution();
		res.setActive(true);
		res.setWeight(4);
		res.setName("Easterly Leg");
		res.setStartDate(new Date(111111000));
		res.setFinishDate(new Date(System.currentTimeMillis() - 111111000));
		return res;
	}

	protected double _minSpeed;

	protected double _maxSpeed;

	protected double _estimate;

	@Override
	public void actUpon(final ProblemSpace space) throws IncompatibleStateException
	{
		// create a bounded state representing our values
		final SpeedRange myR = new SpeedRange(getMinSpeed(), getMaxSpeed());

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

	public double getEstimate()
	{
		return _estimate;
	}

	@Override
	public String getHardConstraints()
	{
		return "" + ((int) _minSpeed) + " - " + ((int) _maxSpeed);
	}

	public double getMaxSpeed()
	{
		return _maxSpeed;
	}

	public double getMinSpeed()
	{
		return _minSpeed;
	}

	public void setEstimate(double estimate)
	{
		double oldEstimate = _estimate;
		this._estimate = estimate;
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setMaxSpeed(double maxSpeed)
	{
		String oldConstraints = getHardConstraints();
		double oldMaxSpeed = _maxSpeed;
		this._maxSpeed = maxSpeed;
		firePropertyChange(MAX_SPEED, oldMaxSpeed, maxSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	public void setMinSpeed(double minSpeed)
	{
		double oldMinSpeed = _minSpeed;
		String oldConstraints = getHardConstraints();
		this._minSpeed = minSpeed;
		firePropertyChange(MIN_SPEED, oldMinSpeed, minSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}
	
	
}
