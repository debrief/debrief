package com.planetmayo.debrief.satc.model.contributions;

import com.planetmayo.debrief.satc.util.GeoPoint;

public class LocationForecastContribution extends BaseContribution
{

	private int _limit;
	private GeoPoint _estimate;

	public int getLimit()
	{
		return _limit;
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

	public GeoPoint getEstimate()
	{
		return _estimate;
	}

	public void setEstimate(GeoPoint estimate)
	{
		firePropertyChange("estimate", _estimate, estimate);
		_estimate = estimate;
	}

	@Override
	public String getHardConstraints()
	{
		return "" + _limit;
	}

	/**
	 * create a sample, for testing
	 * 
	 * @return
	 */
	public static LocationForecastContribution getSample()
	{
		LocationForecastContribution res = new LocationForecastContribution();
		GeoPoint geo = new GeoPoint();
		geo.setLat(52.2);
		geo.setLon(-5.12);
		res.setEstimate(geo);
		return res;
	}
}
