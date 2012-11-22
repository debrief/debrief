package com.planetmayo.debrief.satc.model.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;

public class LocationForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String LIMIT = "limit";

	private Integer _limit;

	private GeoPoint _estimate = new GeoPoint(0, 0);

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
		return "" + (_limit == null ? "" : _limit.intValue());
	}

	public Integer getLimit()
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

	public void setLimit(Integer limit)
	{
		String oldHardConstraints = getHardConstraints();
		Integer oldLimit = _limit;
		_limit = limit;
		firePropertyChange(LIMIT, oldLimit, limit);
		firePropertyChange(HARD_CONSTRAINTS, oldHardConstraints,
				getHardConstraints());
	}
}
