package org.mwc.cmap.xyplot.views;

import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;


public class LocationCalculator implements ILocationCalculator
{
	
	private int _units;
	
	public LocationCalculator()
	{
		this(WorldDistance.DEGS);
	}
	
	public LocationCalculator(final int units)
	{
		setUnits(units);
	}
	
	public void setUnits(final int units)
	{
		this._units = units;
	}
	
	@Override
	public double getDistance(final LineShape line, final Watchable watchable)
	{
		return getDistance(line.getLine_Start(), line.getLineEnd(), 
				watchable.getLocation());		
	}
	
		
	private double getDistance(final WorldLocation start, final WorldLocation end,
			final WorldLocation watchableLocation)
	{
		// perpendicular distance from watchable to the line
		final double leg = watchableLocation.rangeFrom(start, end).getValueIn(_units);
		final double hypotenus = new WorldDistance(watchableLocation.subtract(end)).getValueIn(_units);
		final double angleA = Math.asin(leg / hypotenus);		
		return hypotenus * Math.cos(Math.PI*angleA/180);
	}
	
	
	static public final class LocationTest extends junit.framework.TestCase
	{
		WorldLocation start, end, watch;
		LocationCalculator calc = new LocationCalculator(WorldDistance.KM);
		
		@Override
		public final void setUp()
		{
			// set the earth model we are expecting
			MWC.GenericData.WorldLocation.setModel(new CompletelyFlatEarth());

			start = new WorldLocation(0, 0, 0);
			end = new WorldLocation(0, 61, 0);			
			watch = new WorldLocation(0, 1, 0);			
		}
		
		public void testGetDistance()
		{
			final double lineLenKM = new WorldDistance(end.subtract(start))
					.getValueIn(WorldDistance.KM);
			assertEquals(6778.320000000001, lineLenKM);
		
			final double angleA = watch.rangeFrom(end); 
			assertEquals(60.0, angleA);
						
			final double d = calc.getDistance(start, end, watch);
			assertEquals(6667.200000000001, d);
		}
		
	}
}
