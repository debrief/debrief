package org.mwc.debrief.pepys.model.bean.custom;

import java.sql.Timestamp;

import org.mwc.debrief.pepys.model.db.annotation.FieldName;

import MWC.GenericData.WorldLocation;

public class StateFastMode {
	
	public static final String STATES_FILE = "/states.sql";

	@FieldName(name = "state_id")
	private String stateId;

	private Timestamp time;

	@FieldName(name = "sensor_name")
	private String sensorName;

	@FieldName(name = "platform_name")
	private String platformName;

	@FieldName(name = "platform_type")
	private String platformType;

	@FieldName(name = "nationality_name")
	private String nationalityName;

	private WorldLocation location;

	private double elevation;
	
	private double heading;
	
	private double course;

	private double speed;
	
	public StateFastMode() {
		
	}

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public String getPlatformType() {
		return platformType;
	}

	public void setPlatformType(String platformType) {
		this.platformType = platformType;
	}

	public String getNationalityName() {
		return nationalityName;
	}

	public void setNationalityName(String nationalityName) {
		this.nationalityName = nationalityName;
	}

	public WorldLocation getLocation() {
		return location;
	}

	public void setLocation(WorldLocation location) {
		this.location = location;
	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

	public double getCourse() {
		return course;
	}

	public void setCourse(double course) {
		this.course = course;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	
}
