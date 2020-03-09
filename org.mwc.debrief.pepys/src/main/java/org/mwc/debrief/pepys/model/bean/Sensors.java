package org.mwc.debrief.pepys.model.bean;

import java.util.Date;

public class Sensors implements AbstractBean, Comparable<Sensors> {
	private int sensor_id;
	private String name;
	private int sensor_type_id;
	private int host;
	private Date created_date;
	
	public Sensors() {
		
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

	public int getHost() {
		return host;
	}

	public void setHost(int host) {
		this.host = host;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	@Override
	public String getIdField() {
		return "sensor_id";
	}

	@Override
	public int compareTo(Sensors o) {
		return sensor_id - o.sensor_id;
	}
	
	
}
