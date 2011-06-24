package org.mwc.cmap.gridharness.data;

import java.beans.PropertyChangeListener;
import java.util.List;

import MWC.GUI.TimeStampedDataItem;

public interface GriddableSeries {

	/**
	 * Property name for {@link PropertyChangeListener} that is triggered by
	 * item {@link GriddableSeries#fireModified(TimeStampedDataItem)}
	 * <p>
	 * The event triggered for this property consists of the subject {@link
	 * TimeStampedDataItem} which value have been changed. No additional details
	 * provided on which particular descriptor has been changed, so the item is
	 * considered to be changed as a whole.
	 * <p>
	 * The constant value is <code>"Changed"</code>
	 */
	public static final String PROPERTY_CHANGED = "Changed";

	/**
	 * Property name for {@link PropertyChangeListener} that is triggered by
	 * item {@link GriddableSeries#deleteItem(TimeStampedDataItem)}
	 * <p>
	 * The event triggered for this property consists of the subject {@link
	 * TimeStampedDataItem} being deleted (passed as newValue) and an integer
	 * index of the deleted element (as it was before the actual deletion)
	 * passed as an oldValue. This scheme allows listeners to avoid storing the
	 * whole copy of the items list only to determine the old index after
	 * deletion is broadcasted.
	 * <p>
	 * The constant value is <code>"Deleted"</code>
	 */
	public static final String PROPERTY_DELETED = "Deleted";

	/**
	 * Property name for {@link PropertyChangeListener} that is triggered by
	 * item {@link GriddableSeries#insertItemAt(TimeStampedDataItem)}
	 * <p>
	 * The event triggered for this property consists of the subject {@link
	 * TimeStampedDataItem} being inserted (passed as newValue), and an integer
	 * index of the inserted element passed as an oldValue.
	 * <p>
	 * This scheme allows listeners to avoid searching for the just inserted
	 * item when its position is well-known.
	 * <p>
	 * The constant value is <code>"Added"</code>
	 */
	public static final String PROPERTY_ADDED = "Added";

	public String getName(); // return the title of this object

	public GriddableItemDescriptor[] getAttributes(); // the columns to use

	public List<TimeStampedDataItem> getItems(); // the items to be edited
	
	public void setOnlyShowVisibleItems(final boolean val);	

	public void fireModified(TimeStampedDataItem subject); // indicate that a row has been changed

	public void deleteItem(TimeStampedDataItem subject); // managed the deletion of the item, broadcasting the DELETED event

	public void insertItemAt(TimeStampedDataItem subject, int index); // managed the insertion of the item, broadcasting the ADDED event

	public void insertItem(TimeStampedDataItem subject); // inserts item to the end of the items list, shortcut for s.insertItemAt(subject, s.getItems().size())

	public void addPropertyChangeListener(PropertyChangeListener listener); // to listen for data point changes

	public void removePropertyChangeListener(PropertyChangeListener listener); // to avoid memory leaks

	/**
	 * Creates the side-copy of the given item.
	 * <p>
	 * NOTE: the result item is not automatically inserted to the series items
	 * list, and may be used as a snapshot of the item state.
	 * 
	 * @param item
	 * 		element to clone
	 * @return the copy of the given item
	 * @throws IllegalArgumentException
	 * 		if item is not applicable to this series
	 */
	public TimeStampedDataItem makeCopy(TimeStampedDataItem item);
}
