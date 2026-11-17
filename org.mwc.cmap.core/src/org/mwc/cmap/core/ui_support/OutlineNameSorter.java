/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/
package org.mwc.cmap.core.ui_support;

import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

public class OutlineNameSorter extends ViewerComparator {

	public static class EditableComparer implements Comparator<Editable> {
		@Override
		public int compare(final Editable arg0, final Editable arg1) {
			return compareEditables(arg0, arg1);
		}
	}

	public static interface NameSortHelper {
		boolean sortByDate();
	}

	@SuppressWarnings("unchecked")
	protected static int compareEditables(final Editable e1, final Editable e2) {
		// ha. if they're watchables, sort them in time order. Items without a
		// time go after those with one.
		int res = compareTimes(timeOf(e1), timeOf(e2));

		if (res == 0) {
			// neither has a time (or they share one), so fall back to the
			// items' own ordering, or failing that their names
			if ((e1 instanceof Comparable) && (e2 instanceof Comparable)) {
				@SuppressWarnings("rawtypes")
				final Comparable p1c = (Comparable) e1;
				@SuppressWarnings("rawtypes")
				final Comparable p2c = (Comparable) e2;
				res = p1c.compareTo(p2c);
			} else {
				final String p1Name = e1.getName();
				final String p2Name = e2.getName();
				res = p1Name.compareTo(p2Name);
			}
		}

		return res;
	}

	/**
	 * compare two optional times. Items with a time come before items without one,
	 * so a list that mixes them still has a consistent order: comparing such
	 * pairs by name instead would let the name order contradict the time order,
	 * breaking the Comparator contract (transitivity) and making the JDK sort
	 * throw "Comparison method violates its general contract!" (issue #5199).
	 *
	 * @return zero when neither has a time, or both have the same time, so the
	 *         caller can fall back to another key
	 */
	private static int compareTimes(final HiResDate ha, final HiResDate hb) {
		final int res;
		if ((ha != null) && (hb != null)) {
			res = ha.compareTo(hb);
		} else if (ha != null) {
			res = -1;
		} else if (hb != null) {
			res = 1;
		} else {
			res = 0;
		}
		return res;
	}

	private static HiResDate timeOf(final Editable item) {
		final HiResDate res;
		if (item instanceof Watchable) {
			res = ((Watchable) item).getTime();
		} else {
			res = null;
		}
		return res;
	}

	private final NameSortHelper _sortHelper;

	public OutlineNameSorter() {
		this(new NameSortHelper() {

			@Override
			public boolean sortByDate() {
				return false;
			}
		});
	}

	public OutlineNameSorter(final NameSortHelper helper) {
		_sortHelper = helper;
	}

	@SuppressWarnings("unchecked")
	public int compare(final Object e1, final Object e2) {
		final int res;

		if ((e1 instanceof Comparable) && (e2 instanceof Comparable)) {
			// special case. Just double-check they aren't sensor wrappers
			if (e1 instanceof EditableWrapper && e2 instanceof EditableWrapper) {
				final EditableWrapper p1 = (EditableWrapper) e1;
				final EditableWrapper p2 = (EditableWrapper) e2;

				if (p1.getEditable() instanceof SensorWrapper && p2.getEditable() instanceof SensorWrapper) {
					return compareSensors((SensorWrapper) p1.getEditable(), (SensorWrapper) p2.getEditable());
				} else {
					return compareEditables(p1.getEditable(), p2.getEditable());
				}
			} else {
				// just see if we have sorted editables
				final Comparable<Object> w1 = (Comparable<Object>) e1;
				final Comparable<Object> w2 = (Comparable<Object>) e2;
				res = w1.compareTo(w2);
			}
		} else {
			if (e1 instanceof EditableWrapper && e2 instanceof EditableWrapper) {
				final EditableWrapper p1 = (EditableWrapper) e1;
				final EditableWrapper p2 = (EditableWrapper) e2;

				return compareEditables(p1.getEditable(), p2.getEditable());
			} else {
				return e1.toString().compareTo(e2.toString());
			}
		}

		return res;
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		return compare(e1, e2);
	}

	private int compareSensors(final SensorWrapper s1, final SensorWrapper s2) {
		int res = 0;

		if (_sortHelper.sortByDate()) {
			// sensors without a start time (no cuts) go after those with one
			res = compareTimes(s1.getStartDTG(), s2.getStartDTG());
		}

		if (res == 0) {
			res = s1.getName().compareTo(s2.getName());
		}

		return res;
	}
}
