package org.mwc.debrief.pepys.model.bean;

import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.TableName;

@TableName(name = "SensorTypes")
public class SensorType implements AbstractBean {

	@Id
	private String sensor_type_id;
	private String name;

	public SensorType() {

	}

	public String getName() {
		return name;
	}

	public String getSensor_type_id() {
		return sensor_type_id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setSensor_type_id(final String sensor_type_id) {
		this.sensor_type_id = sensor_type_id;
	}

}
