package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;

public class SpeedForecastContribution extends BaseContribution
{

	protected double _minSpeed;

	protected double _maxSpeed;

	protected double _estimate;

	public double getMinSpeed()
	{
		return _minSpeed;
	}

	public void setMinSpeed(double minSpeed)
	{
		firePropertyChange("minSpeed", _minSpeed, minSpeed);
		String oldConstraints = getHardConstraints();
		this._minSpeed = minSpeed;
		firePropertyChange("hardConstraints", oldConstraints, getHardConstraints());
	}

	public double getMaxSpeed()
	{
		return _maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed)
	{
		firePropertyChange("maxSpeed", _maxSpeed, maxSpeed);
		String oldConstraints = getHardConstraints();
		this._maxSpeed = maxSpeed;
		firePropertyChange("hardConstraints", oldConstraints, getHardConstraints());
	}

	public double getEstimate()
	{
		return _estimate;
	}

	public void setEstimate(double estimate)
	{
		firePropertyChange("estimate", _estimate, estimate);
		this._estimate = estimate;
	}

	@Override
	public String getHardConstraints()
	{
		return "" + ((int) _minSpeed) + " - " + ((int) _maxSpeed);
	}

	/**
	 * utility method to create one of these contributions
	 * 
	 * @return
	 */
	public static SpeedForecastContribution getSample()
	{
		SpeedForecastContribution res = new SpeedForecastContribution();
		res.setActive(true);
		res.setWeight(4);
		res.setName("Easterly Leg");
		res.setStartDate(new Date(111111000));
		res.setFinishDate(new Date(System.currentTimeMillis() - 111111000));
		return res;
	}

	@Override
	public void actUpon(final ProblemSpace space)
	{
		// create a bounded state representing our values
		SpeedRange myR = new SpeedRange(getMinSpeed(), getMaxSpeed());

		// remember if we've found items at our start/end times
		boolean foundStart = false;
		boolean foundEnd = false;

		// loop through the states
		Iterator<BoundedState> sIter = space.iterator();
		while (sIter.hasNext())
		{
			// get the next state
			BoundedState state = (BoundedState) sIter.next();

			// apply our bounds
			state.getSpeed().constrainTo(myR);

			// is this one of our end-terms?
			Date thisT = state.getTime();
			
			// do we have a start time?
			if (this.getStartDate() != null)
			{
				if (thisT.equals(this.getStartDate()))
				{
					// cool, store it
					foundStart = true;
				}
			}

			// do we have an end time?
			if (this.getFinishDate() != null)
			{
				if (thisT.equals(this.getFinishDate()))
				{
					foundEnd = true;
				}
			}
		}

		// ok, did we find our end terms?
		if (!foundStart)
		{
			BoundedState startState = new BoundedState(this.getStartDate());
			startState.constrainTo(myR);
			space.add(startState);
		}

		// ok, did we find our end terms?
		if (!foundEnd)
		{
			BoundedState endState = new BoundedState(this.getFinishDate());
			endState.constrainTo(myR);
			space.add(endState);
		}

	}
}
