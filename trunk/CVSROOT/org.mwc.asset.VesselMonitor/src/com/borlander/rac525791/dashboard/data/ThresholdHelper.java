package com.borlander.rac525791.dashboard.data;

/**
 * Intentionally package local -- use ThresholdListener
 * 
 * @see DashboardDataModel#setThresholdListener(ThresholdListener)
 */
class ThresholdHelper {
	private final DashboardDataModel myModel;
	private boolean mySpeedOk;
	private boolean myDepthOk;
	private boolean myDirectionOk;
	private ThresholdListener myListener = ThresholdListener.NULL;

	public ThresholdHelper(DashboardDataModel model) {
		myModel = model;
		mySpeedOk = reloadSpeedOk();
		myDepthOk = reloadDepthOk();
		myDirectionOk = reloadDirectionOk();
	}
	
	public void setListener(ThresholdListener listener){
		myListener = listener;
		if (myListener == null){
			myListener = ThresholdListener.NULL;
		}
	}
	
	/**
	 * intentionally package local -- only Data model should call this
	 */
	void depthOnThresholdMayBeChanged(){
		boolean isOk = reloadDepthOk();
		if (isOk != myDepthOk){
			myDepthOk = isOk;
			myListener.depthOnThresholdChanged(myDepthOk);
		}
	}
	
	void speedOnThresholdMayBeChanged(){
		boolean isOk = reloadSpeedOk();
		if (isOk != mySpeedOk){
			mySpeedOk = isOk;
			myListener.speedOnThresholdChanged(mySpeedOk);
		}
	}
	
	void directionOnThresholdMayBeChanged(){
		boolean isOk = reloadDirectionOk();
		if (isOk != myDirectionOk){
			myDirectionOk = isOk;
			myListener.directionOnThresholdChanged(myDirectionOk);
		}
	}
	
	private boolean reloadDepthOk() {
		if (myModel.isIgnoreDemandedDepth()){
			return true;
		}
		int actual = myModel.getActualDepth();
		int demanded = myModel.getDemandedDepth();
		int threshold = myModel.getDepthThreshold();
		return isOnThreshold(actual, demanded, threshold);
	}

	private boolean reloadSpeedOk() {
		if (myModel.isIgnoreDemandedSpeed()){
			return true;
		}
		int actual = myModel.getActualSpeed();
		int demanded = myModel.getDemandedSpeed();
		int threshold = myModel.getSpeedThreshold();
		return isOnThreshold(actual, demanded, threshold);
	}

	private boolean reloadDirectionOk() {
		if (myModel.isIgnoreDemandedDirection()){
			return true;
		}
		int actual = myModel.getActualDirection();
		int demanded = myModel.getDemandedDirection();
		int threshold = myModel.getDirectionThreshold();
		return isOnThreshold(actual, demanded, threshold);
	}

	private static boolean isOnThreshold(int actual, int demanded, int threshold) {
		return Math.abs(actual - demanded) <= threshold;
	}

}
