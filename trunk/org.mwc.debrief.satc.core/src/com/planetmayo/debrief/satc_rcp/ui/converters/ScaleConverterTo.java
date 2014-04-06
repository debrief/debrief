package com.planetmayo.debrief.satc_rcp.ui.converters;

public class ScaleConverterTo extends ScaleConverterFrom {
	
	public ScaleConverterTo(int[] increments, int[] borders) {
		super(increments, borders);
	}

	@Override
	public Object convert(Object value) {
		if (value == null) {
			return null;
		}
		int val = ((Double) value).intValue();
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
