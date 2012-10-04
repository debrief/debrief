package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.util.GeoPoint;

public class LocationForecastContribution extends BaseContribution implements BaseContribution.ForecastMarker
{

	/**
	 * create a sample, for testing
	 * 
	 * @return
	 */
	public static LocationForecastContribution getSample()
	{
		LocationForecastContribution res = new LocationForecastContribution();
		res.setName("From ships log");
		GeoPoint geo = new GeoPoint();
		geo.setLat(52.2);
		geo.setLon(-5.12);
		res.setEstimate(geo);
		return res;
	}

	private int _limit;

	private GeoPoint _estimate;

	@Override
	public void actUpon(ProblemSpace space)
	{
		// TODO implement this
		throw new RuntimeException("Not yet implemented");
	}

	public GeoPoint getEstimate()
	{
		return _estimate;
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
		firePropertyChange("estimate", _estimate, estimate);
		_estimate = estimate;
	}

	public void setLimit(int limit)
	{
		String oldHardConstraints = getHardConstraints();
		int oldLimit = _limit;
		_limit = limit;
		firePropertyChange("limit", oldLimit, limit);
		firePropertyChange("hardConstraints", oldHardConstraints,
				getHardConstraints());
	}
}
