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

package MWC.GenericData;

import java.io.Serializable;

/**
 * class which represents a speed as a value plus a set of units
 */
final public class WorldSpeed implements Serializable {

	// ///////////////////////////////////////////////////////////////
	// member variables
	// ///////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the types of units we can handle
	 */
	static public final int M_sec = 0;

	static public final int Kts = 1;
	static public final int ft_sec = 2;
	static public final int ft_min = 3;
	/**
	 * the scale factors for the units compared to minutes
	 */
	static private double _scaleVals[] = { 1, MWC.Algorithms.Conversions.Mps2Kts(1), MWC.Algorithms.Conversions.m2ft(1),
			MWC.Algorithms.Conversions.m2ft(1 * 60) };

	/**
	 * the units labels
	 */
	static public final String[] UnitLabels = { "m/s", "kts", "ft/s", "ft/min" };

	/**
	 * perform a units conversion
	 */
	static public double convert(final int from, final int to, final double val) {
		// get this scale value
		double scaleVal = _scaleVals[from];

		// convert to mins
		final double tmpVal = val / scaleVal;

		// get the new scale val
		scaleVal = _scaleVals[to];

		// convert to new value
		return tmpVal * scaleVal;
	}

	// ///////////////////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////////////////

	/**
	 * get the string representing this set of units
	 */
	static public String getLabelFor(final int units) {
		return UnitLabels[units];
	};

	/**
	 * get the SI units for this type
	 */
	public static int getSIUnits() {
		return M_sec;
	}

	/**
	 * get the index for this type of unit
	 */
	static public int getUnitIndexFor(final String units) {
		int res = 0;
		for (int i = 0; i < UnitLabels.length; i++) {
			final String unitLabel = UnitLabels[i];
			if (units.equals(unitLabel)) {
				res = i;
				break;
			}
		}
		return res;
	}

	// ///////////////////////////////////////////////////////////////
	// member methods
	// ///////////////////////////////////////////////////////////////

	/**
	 * the actual speed (in user specified minutes)
	 */
	double _mySpeed;

	/**
	 * the selected units
	 *
	 */
	int _myUnits = Kts;

	/**
	 * SPECIAL constructor created to help Kryo get Speed values across the LAN for
	 * ASSET
	 *
	 */
	@SuppressWarnings("unused")
	private WorldSpeed() {
	}

	/**
	 * normal constructor
	 *
	 * @param value the distance in the supplied units
	 * @param units the units used for this distance
	 */
	public WorldSpeed(final double value, final int units) {
		_mySpeed = value;
		_myUnits = units;
	}

	/**
	 * copy constructor
	 */
	public WorldSpeed(final WorldSpeed other) {
		_mySpeed = other._mySpeed;
		_myUnits = other._myUnits;
	}

	/**
	 * get my units, in int counter form
	 *
	 */
	public int getUnits() {
		return _myUnits;
	}

	/**
	 * get my units, in string form
	 *
	 * @return
	 */
	public String getUnitsLabel() {
		return getLabelFor(_myUnits);
	}

	/**
	 * get this speed, expressed in it's native units
	 *
	 */
	public double getValue() {
		return _mySpeed;
	}

	/**
	 * get this actual distance, expressed in minutes
	 */
	public double getValueIn(final int units) {
		return convert(_myUnits, units, _mySpeed);
	}

	public boolean greaterThan(final WorldSpeed other) {
		return this.getValueIn(Kts) > other.getValueIn(Kts);
	}

	public boolean lessThan(final WorldSpeed other) {
		return this.getValueIn(Kts) < other.getValueIn(Kts);
	}

	/**
	 * produce as a string
	 */
	@Override
	public String toString() {
		return _mySpeed + " " + getUnitsLabel();
	}

}
