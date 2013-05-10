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

	public static final String LOCATION = "location";
	public static final String LIMIT = "limit";

	private Double limit;

	private GeoPoint location = new GeoPoint(0, 0);

	private transient PropertyChangeListener locationDetailsListener;

	private void initForecastListeners()
	{
		if (locationDetailsListener == null)
			locationDetailsListener = new PropertyChangeListener()
			{

				@Override
				public void propertyChange(PropertyChangeEvent evt)
				{
					firePropertyChange(ESTIMATE, new GeoPoint(0, 9), location);
				}
			};

	}

	/**
	 * note: we provide this method so that we can correctly initialise the
	 * transient changeSupport object when we're deserialising a model object
	 * 
	 * @return this
	 */
	private Object readResolve()
	{
		initForecastListeners();
		return this;
	}

	@Override
	public void actUpon(ProblemSpace space) throws IncompatibleStateException
	{
		if (limit == null)
		{
			return;
		}
		Coordinate coordinate = new Coordinate(location.getLon(), location.getLat());
		Geometry geometry = GeoSupport.doBuffer(GeoSupport.getFactory()
				.createPoint(coordinate), GeoSupport.m2deg(limit));
		LocationRange range = new LocationRange((Polygon) geometry);
		for (BoundedState state : space.getBoundedStatesBetween(startDate,
				finishDate))
		{
			state.constrainTo(range);
		}
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}

	public GeoPoint getLocation()
	{
		return location;
	}

	public Double getLimit()
	{
		return limit;
	}

	public void setLocation(GeoPoint newLocation)
	{
		GeoPoint oldEstimate = location;
		location = newLocation;
		if (oldEstimate != null)
		{
			initForecastListeners();

			oldEstimate.removePropertyChangeListener(locationDetailsListener);
		}
		if (newLocation != null)
		{
			initForecastListeners();

			newLocation.addPropertyChangeListener(locationDetailsListener);
		}
		firePropertyChange(LOCATION, oldEstimate, newLocation);
		firePropertyChange(HARD_CONSTRAINTS, oldEstimate, newLocation);
	}

	public void setLimit(Double newLimit)
	{
		Double oldLimit = limit;
		limit = newLimit;
		firePropertyChange(LIMIT, oldLimit, newLimit);
		firePropertyChange(HARD_CONSTRAINTS, oldLimit, newLimit);
	}
}
