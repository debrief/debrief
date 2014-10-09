package com.planetmayo.debrief.satc_rcp.ui.converters;

import com.planetmayo.debrief.satc_rcp.ui.converters.units.MeterToYds;

public class ScaleConverterTo extends ScaleConverterFrom {
	
	public ScaleConverterTo(int[] increments, int[] borders) {
		super(increments, borders);
	}

	@Override
	public Object convert(Object value) {
		if (! (value instanceof Double) ) {
			return null;
		}
		Double d = new MeterToYds().safeConvert((Double)value);
		//int val = ((Double) value).intValue();
		int val = d.intValue();
		int current = startValue;
		int result = 0;
		for (int i = 0; i < values.length; i++) {
			if (current + values[i] * increments[i] >= val) {
				result += (val - current) / increments[i];
				break;
			} else {
				result += values[i];
				current += values[i] * increments[i];
			}
		}
		return result;
	}

	@Override
	public Object getFromType() {
		return Double.class;
	}

	@Override
	public Object getToType() {
		return Integer.class;
	}
	
	
}
