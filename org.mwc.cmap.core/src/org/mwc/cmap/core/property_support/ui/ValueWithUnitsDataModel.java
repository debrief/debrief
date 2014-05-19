package org.mwc.cmap.core.property_support.ui;

public interface ValueWithUnitsDataModel {

	/** marshall the current specified values into a results object
	 * 
	 * @param dist
	 *            the value typed in
	 * @param units
	 *            the units for the value
	 * @return an object representing the new data value
	 */
	public Object createResultsObject(double dist, int units);

	/** get the current value
	 * @return
	 */
	public double getDoubleValue();

	/** get the list of units to supply
	 * @return
	 */
	public String[] getTagsList();

	/** get the current selection
	 * @return
	 */
	public int getUnitsValue();

	/** unmarshall the supplied object into it's child attributes
	 * 
	 * @param value
	 */
	public void storeMe(Object value);
}
