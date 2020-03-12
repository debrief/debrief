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
	private int sensor_type_id;
	
	@ManyToOne
	@FieldName(name = "host")
	private Platform platform;
	private Date created_date;
	
	public Sensor() {
		
	}

	public int getSensor_id() {
		return sensor_id;
	}

	public void setSensor_id(int sensor_id) {
		this.sensor_id = sensor_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSensor_type_id() {
		return sensor_type_id;
	}

	public void setSensor_type_id(int sensor_type_id) {
		this.sensor_type_id = sensor_type_id;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	@Override
	public int compareTo(Sensor o) {
		return sensor_id - o.sensor_id;
	}
	
	
}
