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
		return hypotenus * Math.cos(angleA);
	}
	
	
	static public final class LocationTest extends junit.framework.TestCase
	{
		WorldLocation start, end, watch;
		LocationCalculator calc = new LocationCalculator(WorldDistance.MINUTES);
		
		@Override
		public final void setUp()
		{
			// set the earth model we are expecting
			MWC.GenericData.WorldLocation.setModel(new CompletelyFlatEarth());	
		}
		
		public void testGetDistanceMiddle()
		{
			start = new WorldLocation(0, 0, 0);
			end = new WorldLocation(0, 1, 0);			
			watch = new WorldLocation(1, 0.5, 0);	
			
			final double lineLen = new WorldDistance(end.subtract(start))
				.getValueIn(WorldDistance.MINUTES);
			assertEquals(60.0, lineLen);
			
			final double perpendicular = watch.rangeFrom(start, end).getValueIn(WorldDistance.MINUTES);
			assertEquals(60.0, perpendicular);
		
			final double d = calc.getDistance(start, end, watch);
			assertEquals(30.0, d, 0.00001 /* epsilon */);
		}	
		
		public void testGetDistanceQuarter()
		{
			start = new WorldLocation(0, 0, 0);
			end = new WorldLocation(0, 2, 0);			
			watch = new WorldLocation(0, 1.5, 0);	
			
			final double lineLen = new WorldDistance(end.subtract(start))
				.getValueIn(WorldDistance.MINUTES);
			assertEquals(60.0 * 2, lineLen);
						
			final double d = calc.getDistance(start, end, watch);
			assertEquals(30.0, d, 0.00001 /* epsilon */);
		}	
		
		public void testDistanceJump()
		{
			calc = new LocationCalculator(WorldDistance.KM);
			start = new WorldLocation(25.0000000, 51.6889495, 0);
			end = new WorldLocation(26.3595475, 51.6882503, 0);
			
			watch = new WorldLocation(26.2152611, 51.862166, 40);	//0240		
			System.out.println(calc.getDistance(start, end, watch));
						
			watch = new WorldLocation(26.2144472, 51.8585917, 40);			
			System.out.println(calc.getDistance(start, end, watch));
			
			watch = new WorldLocation(26.2140611, 51.8572444, 15);
			System.out.println(calc.getDistance(start, end, watch));
			
			watch = new WorldLocation(26.2137222, 51.8560667, 15); //0243
			System.out.println(calc.getDistance(start, end, watch));
			
			watch = new WorldLocation(26.2133806, 51.8548861, 15); //0244
			System.out.println(calc.getDistance(start, end, watch));
			
			//Till this moment the distance is increased.
			//There is a "jump" here (the distance is decreased)
			watch = new WorldLocation(26.2283833, 51.5456056, 15); //0259
			System.out.println(calc.getDistance(start, end, watch));
		}
		
	}
}
