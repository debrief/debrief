package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.util.GeoSupport;

public class SpeedForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String MIN_SPEED = "minSpeed";

	public static final String MAX_SPEED = "maxSpeed";

	protected double _minSpeedKts;

	protected double _maxSpeedKts = 200.0;

	protected double _estimateKts;

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

	@Override
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{
		// create a bounded state representing our values
		final SpeedRange myR = new SpeedRange(GeoSupport.kts2MSec(getMinSpeed()),
				GeoSupport.kts2MSec(getMaxSpeed()));

		// remember if we've found items at our start/end times
		boolean needToInjectStart = true;
		boolean needToInjectFinish = true;

		// loop through the states
		final Iterator<BoundedState> sIter = space.states().iterator();
		while (sIter.hasNext())
		{
			// get the next state
			final BoundedState state = sIter.next();

			boolean constrainIt = false;

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

			// ok, special in-range processing
			constrainIt = checkInDatePeriod(thisT);

			if (constrainIt)
				state.constrainTo(myR);

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

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public double getEstimate()
	{
		return _estimateKts;
	}

	@Override
	public String getEstimateStr()
	{
		return "" + (int) _estimateKts;
	}

	@Override
	public String getHardConstraints()
	{
		return "" + ((int) _minSpeedKts) + " - " + ((int) _maxSpeedKts);
	}

	public double getMaxSpeed()
	{
		return _maxSpeedKts;
	}

	public double getMinSpeed()
	{
		return _minSpeedKts;
	}

	public void setEstimate(double estimate)
	{
		double oldEstimate = _estimateKts;
		this._estimateKts = estimate;
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setMaxSpeed(double maxSpeed)
	{
		String oldConstraints = getHardConstraints();
		double oldMaxSpeed = _maxSpeedKts;
		this._maxSpeedKts = maxSpeed;
		firePropertyChange(MAX_SPEED, oldMaxSpeed, maxSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	public void setMinSpeed(double minSpeed)
	{
		double oldMinSpeed = _minSpeedKts;
		String oldConstraints = getHardConstraints();
		this._minSpeedKts = minSpeed;
		firePropertyChange(MIN_SPEED, oldMinSpeed, minSpeed);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

}
