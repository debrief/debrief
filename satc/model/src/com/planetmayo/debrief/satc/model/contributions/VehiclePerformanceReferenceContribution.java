package com.planetmayo.debrief.satc.model.contributions;

import java.util.Iterator;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.SpeedRange;

public class VehiclePerformanceReferenceContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String VEHICLE_TYPE = "vType";

	private VehicleType vType;

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		// does the space already have this state?
		if (space.getVehicleType() != vType)
		{
			// nope, store it then
			space.setVehicleType(vType);
		}

		// also, apply the speed constraint to the states, if we have one
		if (vType != null)
		{
			SpeedRange speed = new SpeedRange(vType.getMinSpeed(),
					vType.getMaxSpeed());
			Iterator<BoundedState> states = space.states().iterator();
			while (states.hasNext())
			{
				BoundedState boundedState = (BoundedState) states.next();
				boundedState.constrainTo(speed);
			}

		}
	}

	public void setVehicleType(VehicleType vType)
	{
		// take a copy of the type - so we can broadcast it
		VehicleType oldType = this.vType;

		// update the value
		this.vType = vType;

		// spread the good news
		firePropertyChange(VEHICLE_TYPE, oldType, vType);
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	@Override
	public String getHardConstraints()
	{
		return "n/a";
	}

	public String getEstimate()
	{
		return "n/a";
	}
}
