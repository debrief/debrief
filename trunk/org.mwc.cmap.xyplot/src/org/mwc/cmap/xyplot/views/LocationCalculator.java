package org.mwc.cmap.xyplot.views;

import java.awt.Point;

import MWC.Algorithms.EarthModels.CompletelyFlatEarth;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class LocationCalculator implements ILocationCalculator
{
	
	private int _units;
	
	public LocationCalculator()
	{
		this(WorldDistance.DEGS);
	}
	
	public LocationCalculator(int units)
	{
		setUnits(units);
	}
	
	public void setUnits(int units)
	{
		this._units = units;
	}
	
	@Override
	public double getDistance(final LineShape line, final Watchable watchable)
	{
		return getDistance(line.getLine_Start(), line.getLineEnd(), 
				watchable.getLocation());		
	}
	
	@Override
	public double getAngle(final LineShape line, final Watchable watchable)
	{
		return getAngle(line.getLine_Start(), line.getLineEnd(), 
				watchable.getLocation());		
	}
	
	
	private double getAngle(final WorldLocation start, final WorldLocation end,
			final WorldLocation watchableLocation)
	{
		//TODO: implement
		WorldVector wv = end.subtract(start);
		
		return 0;
	}
	
	
	private double getDistance(final WorldLocation start, final WorldLocation end,
			final WorldLocation watchableLocation)
	{
		WorldDistance res =  watchableLocation.rangeFrom(start, end);
		return res.getValueIn(_units);
	}
	
	
	protected double getDistance(final double x_begin, final double y_begin, 
			final double x_end, final double y_end,
			final double x, final double y)
	{
		return new Segment(x_begin, y_begin, x_end, y_end).getDistance(x, y);
	}

	final class Segment
	{
		private double Ox_begin;
		private double Oy_begin;
		  
		private double Ox_end;
		private double Oy_end;
		 
		Segment(final double x_begin, final double y_begin, 
				final double x_end, final double y_end)
		{
		    this.Ox_begin = x_begin;
		    this.Oy_begin = y_begin;
		    
		    this.Ox_end = x_end;
		    this.Oy_end = y_end;		    
		}
		
		/**
		 * Returns distance from the Point(x, y) to the segment.
		 * @param x
		 * @param y
		 * @return
		 */
		double getDistance(double x, double y)
		{
		    double a = Math.sqrt(Math.pow((Ox_begin - Ox_end), 2.0) + Math.pow((Oy_begin - Oy_end), 2.0));
		    double b = Math.sqrt(Math.pow((Ox_begin - x), 2.0) + Math.pow((Oy_begin - y), 2.0));
		    double c = Math.sqrt(Math.pow((x - Ox_end), 2.0) + Math.pow((y - Oy_end), 2.0));
		    return (a + b + c)/2;
		} 
	}
	
	//TODO: unit tests
	public static void main(String[] args)
	{
		LocationCalculator calc = new LocationCalculator();
		System.out.println(calc.getDistance(0.82, 1.28, 4.7, 5.14, 4.8, 2.42));
		
		System.out.println(calc.getDistance(1.7, 2.8, 3.5, 24.2, 10, 10));
	}
	
	static public final class LocationTest extends junit.framework.TestCase
	{
		WorldLocation start, end, watch;
		@Override
		public final void setUp()
		{
			// set the earth model we are expecting
			MWC.GenericData.WorldLocation.setModel(new MWC.Algorithms.EarthModels.FlatEarth());

			start = new WorldLocation(12.3, 12.4, 12.5);
			end = new WorldLocation(12.3, 12.4, 12.5);			
			watch = new WorldLocation(13.3, 12.4, 12.5);
	
		}
		
	}
}
