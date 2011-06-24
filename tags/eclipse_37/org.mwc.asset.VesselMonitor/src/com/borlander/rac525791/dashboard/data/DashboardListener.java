package com.borlander.rac525791.dashboard.data;

public interface DashboardListener {

	void statusChanged();

	void nameChanged();

	void speedUnitsChanged();

	void depthUnitsChanged();

	void demandedSpeedChanged();

	void demandedDirectionChanged();

	void demandedDepthChanged();

	void actualDepthChanged();

	void actualDirectionChanged();

	void actualSpeedChanged();
	
	public static final DashboardListener NULL = new DashboardListener(){
	
		public void statusChanged() {
			// 
		}
	
		public void speedUnitsChanged() {
			// 
		}
	
		public void nameChanged() {
			// 
		}
	
		public void depthUnitsChanged() {
			// 
		}
	
		public void demandedSpeedChanged() {
			// 
		}
	
		public void demandedDirectionChanged() {
			// 
		}
	
		public void demandedDepthChanged() {
			// 
		}
	
		public void actualSpeedChanged() {
			// 
		}
	
		public void actualDirectionChanged() {
			// 
		}
	
		public void actualDepthChanged() {
			// 
		}
		
	};

}
