package com.planetmayo.debrief.satc.model.contributions;

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
}
