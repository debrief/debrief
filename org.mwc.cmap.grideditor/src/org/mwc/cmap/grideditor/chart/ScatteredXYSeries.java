/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor.chart;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

public class ScatteredXYSeries extends XYSeries {

	private static final long serialVersionUID = -8835806118528403984L;

	private transient Method myUpdateBoundsForAddedItemMethod;

	@SuppressWarnings("rawtypes")
	public ScatteredXYSeries(final Comparable key) {
		super(key, false, true);
	}

	public void insertAt(final int index, final XYDataItem item) {
		insertAt(index, item, true);
	}

	@Override
	public void setMaximumItemCount(final int maximum) {
		if (maximum < Integer.MAX_VALUE) {
			throw new UnsupportedOperationException("I am unlimited by nature");
		}
		super.setMaximumItemCount(maximum);
	}

	@SuppressWarnings("unchecked")
	public void insertAt(final int index, final XYDataItem item, final boolean notify) {
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
	protected final void callUpdateBoundsForAddedItem(final XYDataItem item) {
		try {
			if (myUpdateBoundsForAddedItemMethod == null) {
				myUpdateBoundsForAddedItemMethod = XYSeries.class.getDeclaredMethod("updateBoundsForAddedItem", XYDataItem.class);
			}
			if (myUpdateBoundsForAddedItemMethod != null) {
				myUpdateBoundsForAddedItemMethod.setAccessible(true);
				myUpdateBoundsForAddedItemMethod.invoke(this, item);
			}
		} catch (final NoSuchMethodException e) {
			throw new IllegalStateException(e);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (final InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}
}
