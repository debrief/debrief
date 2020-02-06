/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package MWC.GUI.Properties;

/**
 *
 * @author IAN MAYO
 * @version
 */
final public class SteppingBoundedInteger extends java.beans.PropertyEditorSupport {

	///////////////////////////////////
	// member variables
	//////////////////////////////////
	protected int _current;
	protected int _min;
	protected int _max;
	protected int _step;

	///////////////////////////////////
	// constructor
	//////////////////////////////////
	/** Creates new SteppingBoundedInteger */
	public SteppingBoundedInteger(final int current, final int min, final int max, final int step) {
		_current = current;
		_min = min;
		_max = max;
		_step = step;
	}

	@Override
	public boolean equals(final Object o) {
		boolean res = false;
		if (o instanceof SteppingBoundedInteger) {
			final SteppingBoundedInteger other = (SteppingBoundedInteger) o;
			res = (other.getCurrent() == this.getCurrent());
		}

		return res;
	}

	///////////////////////////////////
	// member functions
	//////////////////////////////////
	public int getCurrent() {
		return _current;
	}

	public int getMax() {
		return _max;
	}

	public int getMin() {
		return _min;
	}

	public int getStep() {
		return _step;
	}

	public void setCurrent(final int val) {
		_current = val;
	}

}
