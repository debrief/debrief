package com.planetmayo.debrief.satc.util.calculator;

public enum GeoCalculatorType
{
	ORIGINAL {

		@Override
		public GeodeticCalculator create()
		{
			return new OriginalGeodeticCalculator();
		}		
	},
	FAST {

		@Override
		public GeodeticCalculator create()
		{
			return new FastGeodeticCalculator();
		}		
	};
	
	public abstract GeodeticCalculator create();
}
