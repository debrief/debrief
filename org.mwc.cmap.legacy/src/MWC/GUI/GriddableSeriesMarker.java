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
