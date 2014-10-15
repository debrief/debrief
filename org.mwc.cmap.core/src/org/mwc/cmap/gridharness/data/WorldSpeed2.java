/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.gridharness.data;

/**
 * Proposed replacement for {@link WorldSpeed} that specifies units metadata via
 * API and allows charting.
 * <p>
 * While we assume that {@link WorldSpeed} substitutes the public API and can't
 * be changed, we will provide static migration methods that allows to convert
 * instances between {@link WorldSpeed} and {@link WorldSpeed2}
 * 
 * @see WorldDistance2
 */
public class WorldSpeed2 extends AbstractValueInUnits {

	public static final UnitsSet SPEED_UNITS;

	static public final UnitsSet.Unit M_sec;

	static public final UnitsSet.Unit Kts;

	static public final UnitsSet.Unit ft_sec;

	static public final UnitsSet.Unit ft_min;

	static {
		SPEED_UNITS = new UnitsSet("m/s", true);
		M_sec = SPEED_UNITS.getMainUnit();
		Kts = SPEED_UNITS.addUnit("kts", 1 / (1852d / 3600));
		ft_sec = SPEED_UNITS.addUnit("ft/s", 0.3048);
		ft_min = SPEED_UNITS.addUnit("ft/min", 0.3048 * 60);
		SPEED_UNITS.freeze();
	}

	public WorldSpeed2() {
		super(SPEED_UNITS);
	}

	public WorldSpeed2(final WorldSpeed2 copy) {
		this();
		setValues(copy.getValueIn(M_sec), M_sec);
	}

	public WorldSpeed2(final double value, final UnitsSet.Unit units) {
		this();
		setValues(value, units);
	}

	public WorldSpeed2 makeCopy() {
		return new WorldSpeed2(this);
	}

	public static WorldSpeed2 wrap(final WorldSpeed legacySpeed) {
		final WorldSpeed2 result = new WorldSpeed2();
		result.setValues(legacySpeed.getValueIn(WorldSpeed.M_sec), M_sec);
		return result;
	}

	public static WorldSpeed unwrap(final WorldSpeed2 speed) {
		final WorldSpeed result = new WorldSpeed(speed.getValueIn(M_sec), WorldSpeed.M_sec);
		return result;
	}

	//Again, code below this line can be easily removed
	//The only reason it is here is to allow simple migration, 
	//say by just global search/replace WorldSpeed to WorldSpeed2

	/**
	 * perform a units conversion
	 */
	static public double convert(final UnitsSet.Unit from, final UnitsSet.Unit to, final double val) {
		return SPEED_UNITS.convert(from, to, val);
	}

	/**
	 * get the string representing this set of units
	 */
	static public String getLabelFor(final UnitsSet.Unit units) {
		return units.getLabel();
	}

	/**
	 * get the index for this type of unit
	 * <p>
	 * NOTE: there are no "indices" anymore, we assume that this method may be
	 * used in client code to obtain the units from label first and the then get
	 * the value in this units. This calling sequence will work without
	 * syntactical changes.
	 */
	static public UnitsSet.Unit getUnitIndexFor(final String units) {
		return SPEED_UNITS.findUnit(units);
	}

	/**
	 * get the SI units for this type
	 */
	public static UnitsSet.Unit getSIUnits() {
		return SPEED_UNITS.getSIUnit();
	}

	/**
	 * method to find the smallest set of units which will show the indicated
	 * value as a whole or 1/2 value
	 */
	static public UnitsSet.Unit selectUnitsFor(final double value) {
		return SPEED_UNITS.selectUnitsFor(value);
	}

}
