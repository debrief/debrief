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
		double angleA = watchableLocation.rangeFrom(end);
		double hypotenus = new WorldDistance(watchableLocation.subtract(end)).getValueIn(_units);
		return hypotenus / Math.cos(angleA);
	}
	
	
	static public final class LocationTest extends junit.framework.TestCase
	{
		WorldLocation start, end, watch;
		@Override
		public final void setUp()
		{
			// set the earth model we are expecting
			MWC.GenericData.WorldLocation.setModel(new CompletelyFlatEarth());

			start = new WorldLocation(12.3, 12.4, 12.5);
			end = new WorldLocation(12.3, 12.4, 12.5);			
			watch = new WorldLocation(13.3, 12.4, 12.5);
			
		}
		
		public void testX()
		{
			
		}
		
	}
}
