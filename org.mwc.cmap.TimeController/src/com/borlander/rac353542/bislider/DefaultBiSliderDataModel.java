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

package com.borlander.rac353542.bislider;

import java.util.LinkedList;

public class DefaultBiSliderDataModel implements BiSliderDataModel.Writable {
	private static final int DEFAULT_SEGMENTS_COUNT = 25;
	private final double myPrecision;
	private double myTotalMinimum;
	private double myTotalMaximum;
	private double myUserMinimum = Double.NEGATIVE_INFINITY;
	private double myUserMaximum = Double.POSITIVE_INFINITY;
	private final LinkedList<Listener> myListeners;
	private Listener[] myListenersArray;
	private int myCompositeUpdateCounter;

	private double mySegmentLength;
	private int mySegmentsCount;

	public DefaultBiSliderDataModel() {
		this(0);
	}

	public DefaultBiSliderDataModel(final double precision) {
		this(0, 100, precision);
	}

	public DefaultBiSliderDataModel(final double totalMin, final double totalMax, final double precision) {
		myListeners = new LinkedList<Listener>();
		myPrecision = precision;
		setTotalRange(totalMin, totalMax);
		setSegmentCount(DEFAULT_SEGMENTS_COUNT);
	}

	@Override
	public void addListener(final Listener listener) {
		if (listener != null) {
			myListeners.add(listener);
		}
	}

	private double checkTotalRange(double value) {
		value = Math.min(value, myTotalMaximum);
		value = Math.max(value, myTotalMinimum);
		return value;
	}

	/**
	 * Creates separate copy of listeners. It allows listeners to be unregistered
	 * during notification.
	 * <p>
	 * An array instance is cached to avoid unnecessary creation.
	 */
	private Listener[] copyListeners() {
		if (myListenersArray == null) {
			myListenersArray = new Listener[myListeners.size()];
		}
		myListenersArray = myListeners.toArray(myListenersArray);
		return myListenersArray;
	}

	@Override
	public void finishCompositeUpdate() {
		if (myCompositeUpdateCounter <= 0) {
			throw new IllegalStateException("Finish update without start update");
		}
		if (--myCompositeUpdateCounter == 0) {
			// nothing changed since last notification. However, we have to
			// send last notification with moreChangesExpected = false
			fireChanged();
		}
	}

	private void fireChanged() {
		if (!myListeners.isEmpty()) {
			final Listener[] listenersCopy = copyListeners();
			for (int i = 0; i < listenersCopy.length; i++) {
				final Listener next = listenersCopy[i];
				if (next == null) {
					break;
				}
				next.dataModelChanged(this, myCompositeUpdateCounter > 0);
			}
		}
	}

	@Override
	public double getPrecision() {
		return myPrecision;
	}

	@Override
	public double getSegmentLength() {
		if (mySegmentLength <= 0) {
			if (mySegmentsCount <= 0) {
				throw new IllegalStateException();
			}
			return getTotalDelta() / mySegmentsCount;
		} else {
			return mySegmentLength;
		}
	}

	@Override
	public double getTotalDelta() {
		return myTotalMaximum - myTotalMinimum;
	}

	@Override
	public double getTotalMaximum() {
		return myTotalMaximum;
	}

	@Override
	public double getTotalMinimum() {
		return myTotalMinimum;
	}

	@Override
	public double getUserDelta() {
		return myUserMaximum - myUserMinimum;
	}

	@Override
	public double getUserMaximum() {
		return myUserMaximum;
	}

	@Override
	public double getUserMinimum() {
		return myUserMinimum;
	}

	@Override
	public void removeListener(final Listener listener) {
		if (listener != null) {
			myListeners.remove(listener);
		}
	}

	@Override
	public void setSegmentCount(int segmentsCount) {
		if (segmentsCount < 1) {
			segmentsCount = 1;
		}
		if (mySegmentsCount != segmentsCount) {
			mySegmentsCount = segmentsCount;
			mySegmentLength = -1;
			fireChanged();
		}
	}

	@Override
	public void setSegmentLength(double segmentLength) {
		if (segmentLength > getTotalDelta()) {
			segmentLength = getTotalDelta();
		}
		if (mySegmentLength != segmentLength) {
			mySegmentLength = segmentLength;
			mySegmentsCount = -1;
			fireChanged();
		}
	}

	public void setTotalRange(double minValue, double maxValue) {
		if (minValue == maxValue) {
			throw new IllegalArgumentException("Range is too small: (" + minValue + ", " + maxValue + ")");
		}
		if (minValue > maxValue) {
			final double temp = minValue;
			minValue = maxValue;
			maxValue = temp;
		}
		if (myTotalMaximum != maxValue || myTotalMinimum != minValue) {
			myTotalMinimum = minValue;
			myTotalMaximum = maxValue;
			myUserMinimum = Math.max(myUserMinimum, myTotalMinimum);
			myUserMaximum = Math.min(myUserMaximum, myTotalMaximum);
			fireChanged();
		}
	}

	@Override
	public void setUserMaximum(double userMaximum) {
		// userMaximum = Math.max(userMaximum, myUserMinimum);
		userMaximum = Math.min(userMaximum, myTotalMaximum);
		if (userMaximum != myUserMaximum) {
			myUserMaximum = userMaximum;
			fireChanged();
		}
	}

	@Override
	public void setUserMinimum(double userMinimum) {
		// userMinimum = Math.min(userMinimum, myUserMaximum);
		userMinimum = Math.max(userMinimum, myTotalMinimum);
		if (userMinimum != myUserMinimum) {
			myUserMinimum = userMinimum;
			fireChanged();
		}
	}

	@Override
	public void setUserRange(double userMin, double userMax) {
		if (userMin > userMax) {
			final double temp = userMin;
			userMin = userMax;
			userMax = temp;
		}
		userMin = checkTotalRange(userMin);
		userMax = checkTotalRange(userMax);
		if (userMax != myUserMaximum || userMin != myUserMinimum) {
			myUserMaximum = userMax;
			myUserMinimum = userMin;
			fireChanged();
		}
	}

	@Override
	public void startCompositeUpdate() {
		myCompositeUpdateCounter++;
	}

}
