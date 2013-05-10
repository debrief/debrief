package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;

public class SpeedAnalysisContribution extends
		BaseAnalysisContribution<SpeedRange>
{
	private static final long serialVersionUID = 1L;

	public SpeedAnalysisContribution()
	{
		super();
		setName("Speed Analysis");
	}

	@Override
	protected void applyThis(BoundedState state, SpeedRange thisState)
			throws IncompatibleStateException
	{
		state.constrainTo(thisState);
	}

	@Override
	protected SpeedRange getRangeFor(BoundedState lastStateWithRange)
	{
		return lastStateWithRange.getSpeed();
	}

	@Override
	protected SpeedRange duplicateThis(SpeedRange thisRange)
	{
		return new SpeedRange(thisRange);
	}

	@Override
	protected void furtherConstrain(SpeedRange currentLegState,
			SpeedRange thisRange) throws IncompatibleStateException
	{
		currentLegState.constrainTo(thisRange);
	}

	public void actUpon2(ProblemSpace space) throws IncompatibleStateException
	{
		BoundedState lastStateWithSpeed = null;
		if (space.getVehicleType() != null)
		{
			double maxDecel = space.getVehicleType().getMaxDecelRate();
			double maxAccel = space.getVehicleType().getMaxAccelRate();
			for (BoundedState currentState : space.states())
			{
				if (lastStateWithSpeed != null)
				{
					double diffSeconds = (currentState.getTime().getTime() - lastStateWithSpeed
							.getTime().getTime()) / 1000.0d;

					double minSpeed = lastStateWithSpeed.getSpeed().getMin() - maxDecel
							* diffSeconds;
					double maxSpeed = lastStateWithSpeed.getSpeed().getMax() + maxAccel
							* diffSeconds;
					if (minSpeed < 0)
					{
						minSpeed = 0;
					}
					currentState.constrainTo(new SpeedRange(minSpeed, maxSpeed));
				}
				if (currentState.getSpeed() != null)
					lastStateWithSpeed = currentState;
			}
		}
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}

	@Override
	protected SpeedRange calcRelaxedRange(BoundedState lastStateWithRange,
			VehicleType vType, long millis)
	{
		final double maxAccel;
		final double maxDecel;
		
		// see if we are running fwd or bwds
		if(millis < 0)
		{
			maxAccel = vType.getMaxDecelRate();
			maxDecel = vType.getMaxAccelRate();
		}
		else
		{
			maxDecel = vType.getMaxDecelRate();
			maxAccel = vType.getMaxAccelRate();			
		}
		
		// just in case we're doing a reverse pass, use the abs millis
		millis = Math.abs(millis);
		
		double diffSeconds = millis / 1000.0d;

		double minSpeed = lastStateWithRange.getSpeed().getMin() - maxDecel
				* diffSeconds;
		double maxSpeed = lastStateWithRange.getSpeed().getMax() + maxAccel
				* diffSeconds;
		if (minSpeed < 0)
		{
			minSpeed = 0;
		}
		return new SpeedRange(minSpeed, maxSpeed);
	}

	@Override
	protected void relaxConstraint(BoundedState currentState, SpeedRange newRange)
	{
		currentState.setSpeed(newRange);
	}
}
