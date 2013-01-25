package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;

public class SpeedAnalysisContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;
	
	public SpeedAnalysisContribution() {
		super();
		setName("Speed Analysis");
	}

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
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
					double diffSeconds = (currentState.getTime().getTime() - 
							lastStateWithSpeed.getTime().getTime()) / 1000.0d;					
					
					double minSpeed = lastStateWithSpeed.getSpeed().getMin() - 
							maxDecel * diffSeconds;
					double maxSpeed = lastStateWithSpeed.getSpeed().getMax() + 
							maxAccel * diffSeconds;
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
		/*for (BoundedState state1 : space.states()) 
		{
			if (state1.getLocation() == null) 
			{
				continue;
			}			
			for (BoundedState state2 : space.statesAfter(state1)) 
			{
				if (state2.getLocation() != null) 
				{
					long time = state2.getTime().getTime() - state1.getTime().getTime();
					double timeInSec = time / 1000.d;
					Distance distance = GeoSupport.computeDistance(
							state1.getLocation().getGeometry(),
							state2.getLocation().getGeometry()
					);
					SpeedRange range = new SpeedRange(
							distance.getShortestDistance() / timeInSec,
							distance.getLongestDistance() / timeInSec
					);
					state1.constrainTo(range);
				}
			}
		}*/
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.ANALYSIS;
	}
}
