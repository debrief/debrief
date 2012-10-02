package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

public class SpeedForecastContribution extends BaseContribution {
	
	protected double _minSpeed;
	
	protected double _maxSpeed;
	
	protected double _estimate;

	public double getMinSpeed() {
		return _minSpeed;
	}

	public void setMinSpeed(double minSpeed) {
		firePropertyChange("minSpeed", _minSpeed, minSpeed);
		String oldConstraints = getHardConstraints();
		this._minSpeed = minSpeed;
		firePropertyChange("hardConstraints", oldConstraints, getHardConstraints());
	}

	public double getMaxSpeed() {
		return _maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		firePropertyChange("maxSpeed", _maxSpeed, maxSpeed);
		String oldConstraints = getHardConstraints();
		this._maxSpeed = maxSpeed;
		firePropertyChange("hardConstraints", oldConstraints, getHardConstraints());
	}

	public double getEstimate() {
		return _estimate;
	}

	public void setEstimate(double estimate) {
		firePropertyChange("estimate", _estimate, estimate);
		this._estimate = estimate;
	}

	@Override
	public String getHardConstraints() {
		return "" + ((int) _minSpeed) + " - " + ((int) _maxSpeed);
	}
	
	/** utility method to create one of these contributions
	 * 
	 * @return
	 */
	public static SpeedForecastContribution getSample()
	{
		SpeedForecastContribution res = new SpeedForecastContribution();
		res.setActive(true);
		res.setWeight(4);
		res.setName("Easterly Leg");
		res.setStartDate(new Date(111111000));
		res.setFinishDate(new Date(System.currentTimeMillis() - 111111000));
		return res;
	}
}
