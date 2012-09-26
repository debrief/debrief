package com.planetmayo.debrief.satc.model.contributions;

public class CourseForecastContribution extends BaseContribution {
	
	protected int _minCourse;
	
	protected int _maxCourse;
	
	protected int _estimation;

	public int getMinCourse() {
		return _minCourse;
	}

	public void setMinCourse(int minCourse) {
		firePropertyChange("minCourse", _minCourse, minCourse);
		String oldConstraints = getHardConstraints();
		this._minCourse = minCourse;
		firePropertyChange("hardConstraints", oldConstraints, getHardConstraints());
	}

	public int getMaxCourse() {
		return _maxCourse;
	}

	public void setMaxCourse(int maxCourse) {
		firePropertyChange("maxCourse", _maxCourse, maxCourse);
		String oldConstraints = getHardConstraints();
		this._maxCourse = maxCourse;
		firePropertyChange("hardConstraints", oldConstraints, getHardConstraints());
	}

	public int getEstimation() {
		return _estimation;
	}

	public void setEstimation(int estimation) {
		firePropertyChange("estimation", _estimation, estimation);
		this._estimation = estimation;
	}

	@Override
	public String getHardConstraints() {		
		return "" + _minCourse + " - " + _maxCourse;
	}
}
