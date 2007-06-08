package com.borlander.rac525791.dashboard.data;

public interface ThresholdListener {
	public void depthOnThresholdChanged(boolean isOkNow);
	public void speedOnThresholdChanged(boolean isOkNow);
	public void directionOnThresholdChanged(boolean isOkNow);
	
	public static final ThresholdListener NULL = new ThresholdListener(){
	
		public void speedOnThresholdChanged(boolean isOkNow) {
			// 
		}
	
		public void directionOnThresholdChanged(boolean isOkNow) {
			// 
		}
	
		public void depthOnThresholdChanged(boolean isOkNow) {
			// 	
		}
	
	};
}
