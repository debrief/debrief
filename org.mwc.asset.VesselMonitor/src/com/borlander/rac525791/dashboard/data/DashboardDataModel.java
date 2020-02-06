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

public class DashboardDataModel {
	private int myActualSpeed;
	private int myActualDepth;
	private int myActualDirection;
	private int myDemandedSpeed;
	private int myDemandedDepth;
	private int myDemandedDirection;
	private int mySpeedThreshold;
	private int myDepthThreshold;
	private int myDirectionThreshold;
	private String mySpeedUnits;
	private String myDepthUnits;
	private String myVesselName;
	private String myVesselStatus;
	private boolean myIgnoreDemandedDepth;
	private boolean myIgnoreDemandedSpeed;
	private boolean myIgnoreDemandedDirection;
	private DashboardListener myListener = DashboardListener.NULL;
	private final ThresholdHelper myThresholdHelper = new ThresholdHelper(this);

	public int getActualDepth() {
		return myActualDepth;
	}

	public int getActualDirection() {
		return myActualDirection;
	}

	public int getActualSpeed() {
		return myActualSpeed;
	}

	public int getDemandedDepth() {
		return myDemandedDepth;
	}

	public int getDemandedDirection() {
		return myDemandedDirection;
	}

	public int getDemandedSpeed() {
		return myDemandedSpeed;
	}

	public int getDepthThreshold() {
		return myDepthThreshold;
	}

	public String getDepthUnits() {
		return myDepthUnits;
	}

	public int getDirectionThreshold() {
		return myDirectionThreshold;
	}

	public int getSpeedThreshold() {
		return mySpeedThreshold;
	}

	public String getSpeedUnits() {
		return mySpeedUnits;
	}

	public ThresholdHelper getThresholdHelper() {
		return myThresholdHelper;
	}

	public String getVesselName() {
		return myVesselName;
	}

	public String getVesselStatus() {
		return myVesselStatus;
	}

	public boolean isIgnoreDemandedDepth() {
		return myIgnoreDemandedDepth;
	}

	public boolean isIgnoreDemandedDirection() {
		return myIgnoreDemandedDirection;
	}

	public boolean isIgnoreDemandedSpeed() {
		return myIgnoreDemandedSpeed;
	}

	private boolean safeEquals(final int first, final int second) {
		return first == second;
	}

	private boolean safeEquals(final String first, final String second) {
		return first == null ? second == null : first.equals(second);
	}

	public void setActualDepth(final int actualDepth) {
		if (!safeEquals(myActualDepth, actualDepth)) {
			myActualDepth = actualDepth;
			myListener.actualDepthChanged();
			myThresholdHelper.depthOnThresholdMayBeChanged();
		}
	}

	public void setActualDirection(final int actualDirection) {
		if (!safeEquals(myActualDirection, actualDirection)) {
			myActualDirection = actualDirection;
			myListener.actualDirectionChanged();
			myThresholdHelper.directionOnThresholdMayBeChanged();
		}
	}

	public void setActualSpeed(final int actualSpeed) {
		if (!safeEquals(myActualSpeed, actualSpeed)) {
			myActualSpeed = actualSpeed;
			myListener.actualSpeedChanged();
			myThresholdHelper.speedOnThresholdMayBeChanged();
		}
	}

	public void setDemandedDepth(final int demandedDepth) {
		if (!safeEquals(myDemandedDepth, demandedDepth)) {
			myDemandedDepth = demandedDepth;
			myListener.demandedDepthChanged();
			myThresholdHelper.depthOnThresholdMayBeChanged();
		}
	}

	public void setDemandedDirection(final int demandedDirection) {
		if (!safeEquals(myDemandedDirection, demandedDirection)) {
			myDemandedDirection = demandedDirection;
			myListener.demandedDirectionChanged();
			myThresholdHelper.directionOnThresholdMayBeChanged();
		}
	}

	public void setDemandedSpeed(final int demandedSpeed) {
		if (!safeEquals(myDemandedSpeed, demandedSpeed)) {
			myDemandedSpeed = demandedSpeed;
			myListener.demandedSpeedChanged();
			myThresholdHelper.speedOnThresholdMayBeChanged();
		}
	}

	public void setDepthThreshold(final int depthThreshold) {
		if (!safeEquals(myDepthThreshold, depthThreshold)) {
			myDepthThreshold = depthThreshold;
			// no such method in listener -- see also below
			myThresholdHelper.depthOnThresholdMayBeChanged();
		}
	}

	public void setDepthUnits(final String depthUnits) {
		if (!safeEquals(myDepthUnits, depthUnits)) {
			myDepthUnits = depthUnits;
			myListener.depthUnitsChanged();
		}
	}

	public void setDirectionThreshold(final int directionThreshold) {
		if (!safeEquals(myDirectionThreshold, directionThreshold)) {
			myDirectionThreshold = directionThreshold;
			// do not call listener directly
			// myListener.directionThresholdChanged();
			myThresholdHelper.directionOnThresholdMayBeChanged();
		}
	}

	public void setIgnoreDemandedDepth(final boolean ignore) {
		if (myIgnoreDemandedDepth != ignore) {
			myIgnoreDemandedDepth = ignore;
			myListener.demandedDepthChanged();
			myThresholdHelper.depthOnThresholdMayBeChanged();
		}
	}

	public void setIgnoreDemandedDirection(final boolean ignore) {
		if (myIgnoreDemandedDirection != ignore) {
			myIgnoreDemandedDirection = ignore;
			myListener.demandedDirectionChanged();
			myThresholdHelper.directionOnThresholdMayBeChanged();
		}
	}

	public void setIgnoreDemandedSpeed(final boolean ignore) {
		if (myIgnoreDemandedSpeed != ignore) {
			myIgnoreDemandedSpeed = ignore;
			myListener.demandedSpeedChanged();
			myThresholdHelper.speedOnThresholdMayBeChanged();
		}
	}

	public void setListener(final DashboardListener listener) {
		myListener = listener;
		if (myListener == null) {
			myListener = DashboardListener.NULL;
		}
	}

	public void setSpeedThreshold(final int speedThreshold) {
		if (!safeEquals(mySpeedThreshold, speedThreshold)) {
			mySpeedThreshold = speedThreshold;
			// listener is intentionally not called
			myThresholdHelper.speedOnThresholdMayBeChanged();
		}
	}

	public void setSpeedUnits(final String speedUnits) {
		if (!safeEquals(mySpeedUnits, speedUnits)) {
			mySpeedUnits = speedUnits;
			myListener.speedUnitsChanged();
		}
	}

	public void setThresholdListener(final ThresholdListener listener) {
		myThresholdHelper.setListener(listener);
	}

	public void setVesselName(final String vesselName) {
		if (!safeEquals(myVesselName, vesselName)) {
			myVesselName = vesselName;
			myListener.nameChanged();
		}
	}

	public void setVesselStatus(final String vesselStatus) {
		if (!safeEquals(myVesselStatus, vesselStatus)) {
			myVesselStatus = vesselStatus;
			myListener.statusChanged();
		}
	}

}
