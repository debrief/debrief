package com.planetmayo.debrief.satc.model.contributions;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.State;

public class Range1959ForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<FrequencyMeasurement> measurements = new ArrayList<FrequencyMeasurement>();
	public static final String OBSERVATIONS_NUMBER = "numObservations";

	public Range1959ForecastContribution()
	{
	}

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
	}

	@Override
	protected double calcError(State thisState)
	{
		double delta = 0;

		return delta;
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.MEASUREMENT;
	}

	public void addMeasurement(FrequencyMeasurement measure)
	{
		// extend the time period accordingly
		if (this.getStartDate() == null)
		{
			this.setStartDate(measure.getTime());
			this.setFinishDate(measure.getTime());
		}
		else
		{
			long newTime = measure.getTime().getTime();
			if (this.getStartDate().getTime() > newTime)
				this.setStartDate(measure.getTime());
			if (this.getFinishDate().getTime() < newTime)
				this.setFinishDate(measure.getTime());
		}
		measurements.add(measure);
		firePropertyChange(OBSERVATIONS_NUMBER, measurements.size(),
				measurements.size());		
	}

	public int size()
	{
		return measurements.size();
	}

}
