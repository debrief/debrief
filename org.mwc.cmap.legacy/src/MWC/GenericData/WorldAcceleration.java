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

/**
 * class which represents a speed as a value plus a set of units
 */
final public class WorldAcceleration {

	/**
	 * the types of units we can handle
	 */
	static public final int M_sec_sec = 0;

	static public final int Kts_sec = 1;
	/**
	 * the scale factors for the units compared to minutes
	 */
	static private double _scaleVals[] = { 1, MWC.Algorithms.Conversions.Mps2Kts(1), };

	/**
	 * the units labels
	 */
	static public final String[] UnitLabels = { "m/s/s", "kts/s", };

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

	/////////////////////////////////////////////////////////////////
	// constructor
	/////////////////////////////////////////////////////////////////

	/**
	 * get the string representing this set of units
	 */
	static public String getLabelFor(final int units) {
		return UnitLabels[units];
	}

	/**
	 * get the SI units for this type
	 */
	public static int getSIUnits() {
		return M_sec_sec;
	}

	/////////////////////////////////////////////////////////////////
	// member methods
	/////////////////////////////////////////////////////////////////

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

	/**
	 * the actual speed (in user selected units)
	 */
	double _myAccel;

	/**
	 * the user's selection of units
	 *
	 */
	int _myUnits;

	/**
	 * normal constructor
	 *
	 * @param value the distance in the supplied units
	 * @param units the units used for this distance
	 */
	public WorldAcceleration(final double value, final int units) {
		_myAccel = value;
		_myUnits = units;
	}

	/**
	 * copy constructor
	 */
	public WorldAcceleration(final WorldAcceleration other) {
		_myAccel = other._myAccel;
		_myUnits = other._myUnits;
	}

	public int getUnits() {
		return _myUnits;
	}

	public String getUnitsLabel() {
		return getLabelFor(_myUnits);
	}

	public double getValue() {
		return _myAccel;
	}

	/**
	 * get this actual distance, expressed in minutes
	 */
	public double getValueIn(final int units) {
		return convert(_myUnits, units, _myAccel);
	}

	/**
	 * produce as a string
	 */
	@Override
	public String toString() {
		final String res = _myAccel + " " + getUnitsLabel();
		return res;
	}

}
