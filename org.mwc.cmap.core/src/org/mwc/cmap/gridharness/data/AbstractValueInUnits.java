package org.mwc.cmap.gridharness.data;

public abstract class AbstractValueInUnits implements ValueInUnits {

	private final UnitsSet myUnitsSet;

	private final UnitsSet.Unit myStorageUnits;

	private double myStorageValue;

	public AbstractValueInUnits(final UnitsSet unitsSet) {
		myUnitsSet = unitsSet;
		myStorageUnits = unitsSet.getMainUnit();
	}

	public void setValues(final double value, final UnitsSet.Unit units) {
		myStorageValue = myUnitsSet.convert(units, myStorageUnits, value);
	}

	public UnitsSet getUnitsSet() {
		return myUnitsSet;
	}

	public double getValue() {
		return myStorageValue;
	}
	
	public double getValueIn(final UnitsSet.Unit units) {
		return myUnitsSet.convert(myStorageUnits, units, myStorageValue);
	}

	/**
	 * produce as a string
	 */
	@Override
	public String toString() {
		// so, what are the preferred units?
		final UnitsSet.Unit theUnits = myUnitsSet.selectUnitsFor(myStorageValue);
		final double theValue = getValueIn(theUnits);
		final String res = theValue + " " + theUnits.getLabel();
		return res;
	}

}
