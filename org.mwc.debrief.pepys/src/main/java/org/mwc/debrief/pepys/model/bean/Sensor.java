package org.mwc.debrief.pepys.model.bean;

import java.util.Date;

import org.mwc.debrief.pepys.model.db.annotation.FieldName;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.TableName;

@TableName(name = "Sensors")
public class Sensor implements AbstractBean, Comparable<Sensor> {

	@Id
	private int sensor_id;
	private String name;

	@ManyToOne
	@FieldName(name = "sensor_type_id")
	private SensorType sensorType;

	@ManyToOne
	@FieldName(name = "host")
	private Platform platform;
	private Date created_date;

	public Sensor() {

	}

	@Override
	public int compareTo(final Sensor o) {
		return sensor_id - o.sensor_id;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public String getName() {
		return name;
	}

	public Platform getPlatform() {
		return platform;
	}

	public int getSensor_id() {
		return sensor_id;
	}

	public SensorType getSensorType() {
		return sensorType;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPlatform(final Platform platform) {
		this.platform = platform;
	}

	public void setSensor_id(final int sensor_id) {
		this.sensor_id = sensor_id;
	}

	public void setSensorType(final SensorType sensorType) {
		this.sensorType = sensorType;
	}

}
