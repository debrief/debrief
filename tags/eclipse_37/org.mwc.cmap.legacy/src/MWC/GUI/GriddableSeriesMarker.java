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

  public void removeElement(final MWC.GUI.Editable plottable);

  /** insert the item into our dataset
   * 
   * @param subject
   */
	public void add(Editable subject);
}
