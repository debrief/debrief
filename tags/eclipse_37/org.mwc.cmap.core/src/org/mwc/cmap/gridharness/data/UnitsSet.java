package org.mwc.cmap.gridharness.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UnitsSet {

	private final LinkedHashMap<String, Unit> myUnits = new LinkedHashMap<String, Unit>();

	private final Unit myMainUnit;

	private Unit mySIUnit;

	private boolean myIsFrosen;

	/**
	 * Creates the Units set for given "main" unit. All other units in set will
	 * provide scale factors to this "main" unit.
	 * 
	 * @param mainUnitLabel
	 * 		label for main unit
	 * @param isSIUnit
	 * 		<code>true</code> if the main unit belongs to SI units system
	 */
	public UnitsSet(String mainUnitLabel, boolean isSIUnit) {
		myMainUnit = new Unit(this, mainUnitLabel, 1.0d);
		myUnits.put(mainUnitLabel, myMainUnit);
		if (isSIUnit) {
			mySIUnit = myMainUnit;
		}
	}

	public Unit[] getAllUnits() {
		List<Unit> result = new ArrayList<Unit>(myUnits.values());
		return result.toArray(new Unit[result.size()]);
	}

	public String[] getAllUnitLabels() {
		List<String> result = new ArrayList<String>(myUnits.keySet());
		return result.toArray(new String[result.size()]);
	}

	public Unit getMainUnit() {
		return myMainUnit;
	}

	public Unit getSIUnit() {
		return mySIUnit;
	}

	public Unit findUnit(String label) {
		return myUnits.get(label);
	}

	public double convert(Unit from, Unit to, double val) {
		ensureUnitKnown(from);
		ensureUnitKnown(to);

		if (from == to) {
			return val;
		}

		// get this scale value
		double scaleVal = from.getScaleFactor();

		// convert to main unit
		double tmpVal = val / scaleVal;

		// get the new scale val
		scaleVal = to.getScaleFactor();

		// convert to new value
		return tmpVal * scaleVal;
	}

	/**
	 * method to find the smallest set of units which will show the indicated
	 * value as a whole or 1/2 value
	 */
	public Unit selectUnitsFor(double value) {
		for (Unit nextUnit : myUnits.values()) {
			double newVal = convert(myMainUnit, nextUnit, value);

			// double the value, so that 1/2 values are valid
			newVal *= 2;

			// is this a whole number?
			if (Math.abs(newVal - (int) newVal) < 0.0000000001) {
				return nextUnit;
			}
		}
		//  no, just use the main unit
		return myMainUnit;
	}

	public Unit addUnit(String label, double scaleFactor, boolean isSIUnit) {
		ensureNotFrozen();
		if (scaleFactor == 0) {
			throw new IllegalArgumentException("ScaleFactor can't be 0");
		}
		if (myMainUnit.getLabel().equals(label)) {
			throw new IllegalArgumentException("You can't change main unit: " + label);
		}
		//allow unit replacement for now
		Unit unit = new Unit(this, label, scaleFactor);
		myUnits.put(label, unit);

		if (isSIUnit) {
			//allow replacement? 
			mySIUnit = unit;
		}
		return unit;
	}

	public Unit addUnit(String label, double scaleFactor) {
		return addUnit(label, scaleFactor, false);
	}

	@Override
	public String toString() {
		return "UnitsSet for " + myMainUnit.getLabel();
	}

	/**
	 * Makes this {@link UnitsSet} immutable. Any future attempts to modify this
	 * unit set will fail with {@link IllegalStateException}
	 */
	public void freeze() {
		myIsFrosen = true;
	}

	private void ensureNotFrozen() {
		if (myIsFrosen) {
			throw new IllegalStateException("I am frozen: " + this);
		}
	}

	private void ensureUnitKnown(Unit unit) {
		if (unit == null) {
			throw new NullPointerException();
		}
		if (unit.getUnitSet() != this) {
			throw new IllegalArgumentException("" + this + " is not compatible with unit " + unit.getLabel() + ", from " + unit.getUnitSet());
		}
	}

	public static final class Unit {

		private final UnitsSet myUnitSet;

		private final String myLabel;

		private final double myScaleFactor;

		private Unit(UnitsSet unitSet, String label, double scaleFactor) {
			myUnitSet = unitSet;
			myLabel = label;
			myScaleFactor = scaleFactor;
		}

		public String getLabel() {
			return myLabel;
		}

		public double getScaleFactor() {
			return myScaleFactor;
		}

		public boolean isSIUnit() {
			return myScaleFactor == 1.0d;
		}

		public UnitsSet getUnitSet() {
			return myUnitSet;
		}
	}
}
