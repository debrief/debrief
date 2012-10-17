package com.planetmayo.debrief.satc.model.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class LocationForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String LIMIT = "limit";

	/**
	 * create a sample, for testing
	 * 
	 * @return
	 */
	public static LocationForecastContribution getSample()
	{
		LocationForecastContribution res = new LocationForecastContribution();
		res.setName("From ships log");
		GeoPoint geo = new GeoPoint(52.2, -5.12);
		res.setEstimate(geo);
		return res;
	}

	private int _limit;

	private GeoPoint _estimate;

	private PropertyChangeListener estimateDetailsListener = new PropertyChangeListener()
	{

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			firePropertyChange(ESTIMATE, new GeoPoint(0, 9), _estimate);
		}
	};

	@Override
	public void actUpon(ProblemSpace space)
	{
		// TODO implement this
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public GeoPoint getEstimate()
	{
		return _estimate;
	}

	@Override
	public String getEstimateStr()
	{
		return _estimate.toString();
	}

	@Override
	public String getHardConstraints()
	{
		return "" + _limit;
	}

	public int getLimit()
	{
		return _limit;
	}

	public void setEstimate(GeoPoint estimate)
	{
		GeoPoint oldEstimate = _estimate;
		_estimate = estimate;
		if (oldEstimate != null)
		{
			oldEstimate.removePropertyChangeListener(estimateDetailsListener);
		}
		if (estimate != null)
		{
			estimate.addPropertyChangeListener(estimateDetailsListener);
		}
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setLimit(int limit)
	{
		String oldHardConstraints = getHardConstraints();
		int oldLimit = _limit;
		_limit = limit;
		firePropertyChange(LIMIT, oldLimit, limit);
		firePropertyChange(HARD_CONSTRAINTS, oldHardConstraints,
				getHardConstraints());
	}
}
