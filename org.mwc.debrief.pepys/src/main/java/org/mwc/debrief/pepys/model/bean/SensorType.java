package org.mwc.debrief.pepys.model.bean;

import org.mwc.debrief.pepys.model.db.annotation.Id;

public class SensorType implements AbstractBean {
	
	@Id
	private int sensor_type_id;
	private String name;
	
	public SensorType() {
		
	}

	public int getSensor_type_id() {
		return sensor_type_id;
	}

	public void setSensor_type_id(int sensor_type_id) {
		this.sensor_type_id = sensor_type_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
