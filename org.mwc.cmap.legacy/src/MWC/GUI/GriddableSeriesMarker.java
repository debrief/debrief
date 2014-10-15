/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI;

public interface GriddableSeriesMarker
{
	/**
	 * get an example of the things we edit - so the editor can be created
	 * 
	 * @return
	 */
	public Editable getSampleGriddable();

	/**
	 * Creates the side-copy of the given item.
	 * <p>
	 * NOTE: the result item is not automatically inserted to the series items
	 * list, and may be used as a snapshot of the item state.
	 * 
	 * @param item
	 *          element to clone
	 * @return the copy of the given item
	 * @throws IllegalArgumentException
	 *           if item is not applicable to this series
	 */
	public TimeStampedDataItem makeCopy(TimeStampedDataItem item);
	
	/** whether the gridded editor can add/remote items from this dataset
	 * 
	 * @return yes/no
	 */
	public boolean supportsAddRemove();
	
	/** whether this dataset requires an explicit save operation
	 * 
	 * @return yes if the gridded editor should provide a save method
 	 */
	public boolean requiresManualSave();
	
	/** force an explicit save operation
	 * 
	 * @param message
	 */
	public void doSave(String message);
	

  public void removeElement(final MWC.GUI.Editable plottable);

  /** insert the item into our dataset
   * 
   * @param subject
   */
	public void add(Editable subject);
}
