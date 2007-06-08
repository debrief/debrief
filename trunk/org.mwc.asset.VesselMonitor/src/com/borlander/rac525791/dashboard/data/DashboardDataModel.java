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
	
	public ThresholdHelper getThresholdHelper() {
		return myThresholdHelper;
	}
	
	public void setListener(DashboardListener listener) {
		myListener = listener;
		if (myListener == null){
			myListener = DashboardListener.NULL;
		}
	}
	
	public void setThresholdListener(ThresholdListener listener){
		myThresholdHelper.setListener(listener);
	}

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

	public String getDepthUnits() {
		return myDepthUnits;
	}

	public String getSpeedUnits() {
		return mySpeedUnits;
	}

	public String getVesselName() {
		return myVesselName;
	}

	public String getVesselStatus() {
		return myVesselStatus;
	}
	
	public int getDepthThreshold() {
		return myDepthThreshold;
	}
	
	public int getSpeedThreshold() {
		return mySpeedThreshold;
	}
	
	public int getDirectionThreshold() {
		return myDirectionThreshold;
	}

	public void setActualDepth(int actualDepth) {
		if (!safeEquals(myActualDepth, actualDepth)) {
			myActualDepth = actualDepth;
			myListener.actualDepthChanged();
			myThresholdHelper.depthOnThresholdMayBeChanged();
		}
	}

	public void setActualDirection(int actualDirection) {
		if (!safeEquals(myActualDirection, actualDirection)) {
			myActualDirection = actualDirection;
			myListener.actualDirectionChanged();
			myThresholdHelper.directionOnThresholdMayBeChanged();
		}
	}

	public void setActualSpeed(int actualSpeed) {
		if (!safeEquals(myActualSpeed, actualSpeed)) {
			myActualSpeed = actualSpeed;
			myListener.actualSpeedChanged();
			myThresholdHelper.speedOnThresholdMayBeChanged();
		}
	}

	public void setDemandedDepth(int demandedDepth) {
		if (!safeEquals(myDemandedDepth, demandedDepth)) {
			myDemandedDepth = demandedDepth;
			myListener.demandedDepthChanged();
			myThresholdHelper.depthOnThresholdMayBeChanged();
		}
	}

	public void setDemandedDirection(int demandedDirection) {
		if (!safeEquals(myDemandedDirection, demandedDirection)) {
			myDemandedDirection = demandedDirection;
			myListener.demandedDirectionChanged();
			myThresholdHelper.directionOnThresholdMayBeChanged();
		}
	}

	public void setDemandedSpeed(int demandedSpeed) {
		if (!safeEquals(myDemandedSpeed, demandedSpeed)) {
			myDemandedSpeed = demandedSpeed;
			myListener.demandedSpeedChanged();
			myThresholdHelper.speedOnThresholdMayBeChanged();
		}
	}
	
	public void setDepthUnits(String depthUnits) {
		if (!safeEquals(myDepthUnits, depthUnits)) {
			myDepthUnits = depthUnits;
			myListener.depthUnitsChanged();
		}
	}

	public void setSpeedUnits(String speedUnits) {
		if (!safeEquals(mySpeedUnits, speedUnits)) {
			mySpeedUnits = speedUnits;
			myListener.speedUnitsChanged();
		}
	}

	public void setVesselName(String vesselName) {
		if (!safeEquals(myVesselName, vesselName)) {
			myVesselName = vesselName;
			myListener.nameChanged();
		}
	}

	public void setVesselStatus(String vesselStatus) {
		if (!safeEquals(myVesselStatus, vesselStatus)) {
			myVesselStatus = vesselStatus;
			myListener.statusChanged();
		}
	}
	
	public void setDepthThreshold(int depthThreshold) {
		if (!safeEquals(myDepthThreshold, depthThreshold)) {
			myDepthThreshold = depthThreshold;
			//no such method in listener -- see also below
			myThresholdHelper.depthOnThresholdMayBeChanged();
		}
	}

	public void setSpeedThreshold(int speedThreshold) {
		if (!safeEquals(mySpeedThreshold, speedThreshold)) {
			mySpeedThreshold = speedThreshold;
			//listener is intentionally not called
			myThresholdHelper.speedOnThresholdMayBeChanged();
		}
	}
	
	public void setDirectionThreshold(int directionThreshold) {
		if (!safeEquals(myDirectionThreshold, directionThreshold)) {
			myDirectionThreshold = directionThreshold;
			//do not call listener directly
			//myListener.directionThresholdChanged();
			myThresholdHelper.directionOnThresholdMayBeChanged();
		}
	}
	
	public void setIgnoreDemandedDepth(boolean ignore) {
		if (myIgnoreDemandedDepth != ignore){
			myIgnoreDemandedDepth = ignore;
			myListener.demandedDepthChanged();
			myThresholdHelper.depthOnThresholdMayBeChanged();
		}
	}
	
	public void setIgnoreDemandedDirection(boolean ignore) {
		if (myIgnoreDemandedDirection != ignore){
			myIgnoreDemandedDirection = ignore;
			myListener.demandedDirectionChanged();
			myThresholdHelper.directionOnThresholdMayBeChanged();
		}
	}
	
	public void setIgnoreDemandedSpeed(boolean ignore) {
		if (myIgnoreDemandedSpeed != ignore){
			myIgnoreDemandedSpeed = ignore;
			myListener.demandedSpeedChanged();
			myThresholdHelper.speedOnThresholdMayBeChanged();
		}
	}

	public boolean isIgnoreDemandedDirection() {
		return myIgnoreDemandedDirection;
	}
	
	public boolean isIgnoreDemandedDepth() {
		return myIgnoreDemandedDepth;
	}
	
	public boolean isIgnoreDemandedSpeed() {
		return myIgnoreDemandedSpeed;
	}

	private boolean safeEquals(String first, String second){
		return first == null ? second == null : first.equals(second);
	}

	private boolean safeEquals(int first, int second){
		return first == second;
	}

}
