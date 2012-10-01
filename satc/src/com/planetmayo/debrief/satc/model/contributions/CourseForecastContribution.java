package com.planetmayo.debrief.satc.model.contributions;

public class CourseForecastContribution extends BaseContribution {
	
	protected int _minCourse;
	
	protected int _maxCourse;
	
	protected int _estimate;

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

	public int getEstimate() {
		return _estimate;
	}

	public void setEstimate(int estimate) {
		firePropertyChange("estimate", _estimate, estimate);
		this._estimate = estimate;
	}

	@Override
	public String getHardConstraints() {		
		return "" + _minCourse + " - " + _maxCourse;
	}
}
