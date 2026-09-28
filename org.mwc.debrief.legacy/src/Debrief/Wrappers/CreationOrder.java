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

package Debrief.Wrappers;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides a creation sequence number for time-ordered items (sensor cuts, TMA
 * solutions, dynamic shapes, track segments), so that items with the same time
 * can be given a stable, total ordering in compareTo(). Without this, sorted
 * sets silently drop or fail to remove items that share a time.
 *
 * Note: the sequence number should be stored in a (serialized) instance field,
 * so a copy made by serialization (copy/paste) keeps the same relative order
 * as its original.
 */
public final class CreationOrder {

	private static final AtomicLong COUNTER = new AtomicLong();

	/**
	 * compare two distinct items that have the same time
	 *
	 * @param me       the first item
	 * @param mySeq    its creation sequence number
	 * @param other    the second item
	 * @param otherSeq its creation sequence number
	 * @return negative, zero or positive, as for compareTo(). Only returns zero if
	 *         the two are the same object.
	 */
	public static int compare(final Object me, final long mySeq, final Object other, final long otherSeq) {
		if (me == other) {
			return 0;
		}
		int res = Long.compare(mySeq, otherSeq);
		if (res == 0) {
			// can happen for a serialized copy of an item stored alongside its original.
			// Fall back on identity, which is at least consistent for the life of the
			// objects
			res = Integer.compare(System.identityHashCode(me), System.identityHashCode(other));
		}
		return res;
	}

	/**
	 * @return the next sequence number (always positive)
	 */
	public static long next() {
		return COUNTER.incrementAndGet();
	}

	private CreationOrder() {
		// utility class
	}
}
