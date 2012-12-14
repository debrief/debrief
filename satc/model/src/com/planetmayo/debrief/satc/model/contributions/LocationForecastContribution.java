package com.planetmayo.debrief.satc.model.contributions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class LocationForecastContribution extends BaseContribution
{
	private static final long serialVersionUID = 1L;

	public static final String LIMIT = "limit";

	private Double _limit;

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
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		if (_limit == null) {
			return;			
		}
		Coordinate coordinate = new Coordinate(_estimate.getLon(), _estimate.getLat());
		Geometry geometry = GeoSupport.getFactory().createPoint(coordinate).buffer(GeoSupport.m2deg(_limit));
		LocationRange range = new LocationRange((Polygon) geometry);
		for (BoundedState state : space.getBoundedStatesBetween(_startDate, _finishDate))
		{
			state.constrainTo(range);
		}
		if (_startDate != null && space.getBoundedStateAt(_startDate) == null)
		{
			final BoundedState startState = new BoundedState(this.getStartDate());
			startState.constrainTo(range);
			space.add(startState);
		}
		if (_finishDate != null && space.getBoundedStateAt(_finishDate) == null)
		{
			final BoundedState endState = new BoundedState(this.getFinishDate());
			endState.constrainTo(range);
			space.add(endState);
		}
		
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

	public Double getLimit()
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
		firePropertyChange(HARD_CONSTRAINTS, oldEstimate, estimate);
	}

	public void setLimit(Double limit)
	{
		Double oldLimit = _limit;
		_limit = limit;
		firePropertyChange(LIMIT, oldLimit, limit);
		firePropertyChange(HARD_CONSTRAINTS, oldLimit, limit);
	}
}
