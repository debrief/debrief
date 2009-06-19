package org.mwc.cmap.gridharness.data;

public interface ValueInUnits {

	public double getValueIn(UnitsSet.Unit units);
	
	public void setValues(double value, UnitsSet.Unit units);

	public UnitsSet getUnitsSet();
	
	public ValueInUnits makeCopy();
}
