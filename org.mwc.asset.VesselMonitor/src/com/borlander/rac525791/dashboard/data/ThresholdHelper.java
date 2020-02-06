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

package com.borlander.rac525791.dashboard.data;

/**
 * Intentionally package local -- use ThresholdListener
 *
 * @see DashboardDataModel#setThresholdListener(ThresholdListener)
 */
class ThresholdHelper {
	private static boolean isOnThreshold(final int actual, final int demanded, final int threshold) {
		return Math.abs(actual - demanded) <= threshold;
	}

	private final DashboardDataModel myModel;
	private boolean mySpeedOk;
	private boolean myDepthOk;
	private boolean myDirectionOk;

	private ThresholdListener myListener = ThresholdListener.NULL;

	public ThresholdHelper(final DashboardDataModel model) {
		myModel = model;
		mySpeedOk = reloadSpeedOk();
		myDepthOk = reloadDepthOk();
		myDirectionOk = reloadDirectionOk();
	}

	/**
	 * intentionally package local -- only Data model should call this
	 */
	void depthOnThresholdMayBeChanged() {
		final boolean isOk = reloadDepthOk();
		if (isOk != myDepthOk) {
			myDepthOk = isOk;
			myListener.depthOnThresholdChanged(myDepthOk);
		}
	}

	void directionOnThresholdMayBeChanged() {
		final boolean isOk = reloadDirectionOk();
		if (isOk != myDirectionOk) {
			myDirectionOk = isOk;
			myListener.directionOnThresholdChanged(myDirectionOk);
		}
	}

	private boolean reloadDepthOk() {
		if (myModel.isIgnoreDemandedDepth()) {
			return true;
		}
		final int actual = myModel.getActualDepth();
		final int demanded = myModel.getDemandedDepth();
		final int threshold = myModel.getDepthThreshold();
		return isOnThreshold(actual, demanded, threshold);
	}

	private boolean reloadDirectionOk() {
		if (myModel.isIgnoreDemandedDirection()) {
			return true;
		}
		final int actual = myModel.getActualDirection();
		final int demanded = myModel.getDemandedDirection();
		final int threshold = myModel.getDirectionThreshold();
		return isOnThreshold(actual, demanded, threshold);
	}

	private boolean reloadSpeedOk() {
		if (myModel.isIgnoreDemandedSpeed()) {
			return true;
		}
		final int actual = myModel.getActualSpeed();
		final int demanded = myModel.getDemandedSpeed();
		final int threshold = myModel.getSpeedThreshold();
		return isOnThreshold(actual, demanded, threshold);
	}

	public void setListener(final ThresholdListener listener) {
		myListener = listener;
		if (myListener == null) {
			myListener = ThresholdListener.NULL;
		}
	}

	void speedOnThresholdMayBeChanged() {
		final boolean isOk = reloadSpeedOk();
		if (isOk != mySpeedOk) {
			mySpeedOk = isOk;
			myListener.speedOnThresholdChanged(mySpeedOk);
		}
	}

}
