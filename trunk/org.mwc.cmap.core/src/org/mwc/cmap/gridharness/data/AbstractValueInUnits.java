package org.mwc.cmap.gridharness.data;

public abstract class AbstractValueInUnits implements ValueInUnits {

	private final UnitsSet myUnitsSet;

	private final UnitsSet.Unit myStorageUnits;

	private double myStorageValue;

	public AbstractValueInUnits(UnitsSet unitsSet) {
		myUnitsSet = unitsSet;
		myStorageUnits = unitsSet.getMainUnit();
	}

	public void setValues(double value, UnitsSet.Unit units) {
		myStorageValue = myUnitsSet.convert(units, myStorageUnits, value);
	}

	@Override
	public UnitsSet getUnitsSet() {
		return myUnitsSet;
	}

	public double getValue() {
		return myStorageValue;
	}
	
	@Override
	public double getValueIn(UnitsSet.Unit units) {
		return myUnitsSet.convert(myStorageUnits, units, myStorageValue);
	}

	/**
	 * produce as a string
	 */
	@Override
	public String toString() {
		// so, what are the preferred units?
		UnitsSet.Unit theUnits = myUnitsSet.selectUnitsFor(myStorageValue);
		double theValue = getValueIn(theUnits);
		String res = theValue + " " + theUnits.getLabel();
		return res;
	}

}
