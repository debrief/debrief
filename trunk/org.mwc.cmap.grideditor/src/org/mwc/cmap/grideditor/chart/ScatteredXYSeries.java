package org.mwc.cmap.grideditor.chart;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

public class ScatteredXYSeries extends XYSeries {

	private static final long serialVersionUID = -8835806118528403984L;

	private transient Method myUpdateBoundsForAddedItemMethod;

	@SuppressWarnings("unchecked")
	public ScatteredXYSeries(Comparable key) {
		super(key, false, true);
	}

	public void insertAt(int index, XYDataItem item) {
		insertAt(index, item, true);
	}

	@Override
	public void setMaximumItemCount(int maximum) {
		if (maximum < Integer.MAX_VALUE) {
			throw new UnsupportedOperationException("I am unlimited by nature");
		}
		super.setMaximumItemCount(maximum);
	}

	@SuppressWarnings("unchecked")
	public void insertAt(int index, XYDataItem item, boolean notify) {
		if (index == getItemCount()) {
			//super class handles this case already 
			add(item, notify);
		} else {
			data.add(index, item);
			callUpdateBoundsForAddedItem(item);
			if (notify) {
				fireSeriesChanged();
			}
		}
	}

	/**
	 * Unfortunately the updateBoundsForAddedItem method is private in super
	 * class as of 1.0.13 release.
	 * 
	 * In order to allow insertion into the arbitrary index (for {@link
	 * ScatteredXYSeries}) we must somehow call this method after insertion in
	 * order to update bounds.
	 * 
	 * We will do it via reflection.
	 */
	protected final void callUpdateBoundsForAddedItem(XYDataItem item) {
		try {
			if (myUpdateBoundsForAddedItemMethod == null) {
				myUpdateBoundsForAddedItemMethod = XYSeries.class.getDeclaredMethod("updateBoundsForAddedItem", XYDataItem.class);
			}
			if (myUpdateBoundsForAddedItemMethod != null) {
				myUpdateBoundsForAddedItemMethod.setAccessible(true);
				myUpdateBoundsForAddedItemMethod.invoke(this, item);
			}
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}
}
