package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;
import java.util.Iterator;

import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.util.GeoPoint;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class RangeForecastContribution extends BaseContribution
{

	public static final String MIN_RANGE = "minRange";

	public static final String MAX_RANGE = "maxRange";

	@SuppressWarnings("unused")
	private static double ABSOLUTELY_HUGE_RANGE_M = 500000;

	/**
	 * utility method to create one of these contributions
	 * 
	 * @return
	 */
	public static BaseContribution getSample()
	{
		BaseContribution res = new RangeForecastContribution();
		res.setActive(true);
		res.setWeight(4);
		res.setName("Easterly Leg");
		res.setStartDate(new Date(111111000));
		res.setFinishDate(new Date(System.currentTimeMillis() - 111111000));
		return res;
	}

	protected double _minRange;

	protected double _maxRange;

	protected double _estimate;

	@Override
	public void actUpon(final ProblemSpace space)
			throws IncompatibleStateException
	{
		// ok, we can only act on data that already has an origin. So,
		// start looping through
		Iterator<BoundedState> iter = space.states();

		while (iter.hasNext())
		{
			BoundedState boundedState = (BoundedState) iter.next();

			// does this state have an origin?
			if (true)
			{

				// TODO: HOW DO WE GET THE ORIGIN IN?
				GeoPoint gp = new GeoPoint(3,2);
				Point pt = gp.asPoint();

				// yes, ok we can centre our donut on that
				Polygon thePolygon = getOuterRing(pt);
				Polygon inner = getInnerRing(pt);
				
				// did we generate an inner?
				if (inner != null)
				{
					// yes, better delete it then
					thePolygon = (Polygon) thePolygon.difference(inner);
				}

						
				// create a LocationRange for the poly
				// now define the polygon
				final LocationRange myRa = new LocationRange(thePolygon);

				// apply the range
				boundedState.constrainTo(myRa);
			}
		}
	}

	private Polygon getOuterRing(Point pt)
	{
		// TODO: handle case where range not provided

		// do we have a max range?
		double theRange;

		// yes, ok we have an outer ring
		theRange = getMaxRange();

		// no, ok, just choose an absolutely monster range
//		theRange = ABSOLUTELY_HUGE_RANGE_M;

		// ok, now we create the inner circle
		Geometry res = pt.buffer(theRange);
		
		return (Polygon) res;
	}

	private Polygon getInnerRing(Point pt)
	{
		// TODO: handle case where range not provided

		// do we have a min range?
		double theRange;

		// yes, ok we have an inner ring
		theRange = getMinRange();

		// no, ok, just choose a zero range
	//	theRange = 0;
		
		// ok, now we create the inner circle
		Geometry res = pt.buffer(theRange);		

		return (Polygon) res;
	}

	public double getEstimate()
	{
		return _estimate;
	}

	@Override
	public String getHardConstraints()
	{
		return "" + ((int) _minRange) + " - " + ((int) _maxRange);
	}

	public double getMaxRange()
	{
		return _maxRange;
	}

	public double getMinRange()
	{
		return _minRange;
	}

	public void setEstimate(double estimate)
	{
		double oldEstimate = _estimate;
		this._estimate = estimate;
		firePropertyChange(ESTIMATE, oldEstimate, estimate);
	}

	public void setMaxRange(double maxRngDegs)
	{
		double oldMaxRange = _maxRange;
		String oldConstraints = getHardConstraints();
		this._maxRange = maxRngDegs;
		firePropertyChange(MAX_RANGE, oldMaxRange, maxRngDegs);		
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	public void setMinRange(double minRngDegs)
	{
		double oldMinRange = _minRange;
		String oldConstraints = getHardConstraints();
		this._minRange = minRngDegs;
		firePropertyChange(MIN_RANGE, oldMinRange, minRngDegs);
		firePropertyChange(HARD_CONSTRAINTS, oldConstraints, getHardConstraints());
	}

	@Override
	public ContributionDataType getDataType()
	{
		return ContributionDataType.FORECAST;
	}
	
	
}
