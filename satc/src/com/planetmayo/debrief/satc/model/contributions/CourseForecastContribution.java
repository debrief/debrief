package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

import com.planetmayo.debrief.satc.model.states.ProblemSpace;

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
	

	@Override
	public void actUpon(ProblemSpace space)
	{
		// TODO implement this
		throw new RuntimeException("Not yet implemented");
	}
	
	/** utility method to create one of these contributions
	 * 
	 * @return
	 */
	public static CourseForecastContribution getSample()
	{
		CourseForecastContribution res = new CourseForecastContribution();
		res.setName("Approaching Buoy");
		res.setActive(true);
		res.setWeight(7);
		res.setStartDate(new Date(1111110033120L));
		res.setFinishDate(new Date(System.currentTimeMillis() - 111111000));
		res.setMinCourse(10);
		res.setMaxCourse(60);
		res.setEstimate(25);
		
		return res;
	}
}
