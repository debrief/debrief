package com.planetmayo.debrief.satc.model.contributions;

import java.util.ArrayList;
import java.util.Date;

public abstract class CoreMeasurementContribution<Measurement extends CoreMeasurementContribution.CoreMeasurement> extends BaseContribution
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String OBSERVATIONS_NUMBER = "numObservations";

	/**
	 * the set of measurements we store
	 * 
	 */
	protected ArrayList<Measurement> measurements = new ArrayList<Measurement>();
	
	public CoreMeasurementContribution()
	{
		super();
	}
	
	public static class CoreMeasurement
	{

		protected final Date time;
		private boolean isActive = true;
		/**
		 * the (optional) color for this measurement
		 * 
		 */
		private java.awt.Color color = null;

		public CoreMeasurement(Date time)
		{
			this.time = time;
		}
		
		public java.awt.Color getColor()
		{
			return color;
		}

		public void setColor(java.awt.Color color)
		{
			this.color = color;
		}

		public Date getDate()
		{
			return time;
		}

		public boolean isActive()
		{
			return isActive;
		}

		public void setActive(boolean active)
		{
			isActive = active;
		}
		
	}

	
	/**
	 * store this new measurement
	 * 
	 * @param measure
	 */
	public void addMeasurement(Measurement measure)
	{
		// extend the time period accordingly
		if (this.getStartDate() == null)
		{
			this.setStartDate(measure.time);
			this.setFinishDate(measure.time);
		}
		else
		{
			long newTime = measure.time.getTime();
			if (this.getStartDate().getTime() > newTime)
				this.setStartDate(measure.time);
			if (this.getFinishDate().getTime() < newTime)
				this.setFinishDate(measure.time);
		}
		measurements.add(measure);
		firePropertyChange(OBSERVATIONS_NUMBER, measurements.size(),
				measurements.size());		
	}
	


	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.MEASUREMENT;
	}

	public int getNumObservations()
	{
		return measurements.size();
	}

	public ArrayList<Measurement> getMeasurements()
	{
		return measurements;
	}
	

	/**
	 * whether this contribution has any measurements yet
	 * 
	 * @return
	 */
	public boolean hasData()
	{
		return measurements.size() > 0;
	}
}