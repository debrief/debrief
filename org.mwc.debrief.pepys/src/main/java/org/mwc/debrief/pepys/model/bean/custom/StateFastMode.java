package org.mwc.debrief.pepys.model.bean.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.bean.PlainBean;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;
import org.mwc.debrief.pepys.model.db.annotation.FieldName;

import MWC.GenericData.WorldLocation;

public class StateFastMode implements AbstractBean, PlainBean {

	@FieldName(name = "state_id")
	private String stateId;

	private Timestamp time;

	@FieldName(name = "sensor_name")
	private String sensorName;

	@FieldName(name = "platform_name")
	private String platformName;

	private String sourceid;

	private String reference;

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

	public double getCourse() {
		return course;
	}

	public double getElevation() {
		return elevation;
	}

	public double getHeading() {
		return heading;
	}

	public WorldLocation getLocation() {
		return location;
	}

	public String getNationalityName() {
		return nationalityName;
	}

	public String getPlatformName() {
		return platformName;
	}

	public String getPlatformType() {
		return platformType;
	}

	public String getReference() {
		return reference;
	}

	public String getSensorName() {
		return sensorName;
	}

	public String getSourceid() {
		return sourceid;
	}

	public double getSpeed() {
		return speed;
	}

	public String getStateId() {
		return stateId;
	}

	public Timestamp getTime() {
		return time;
	}

	@Override
	public void retrieveObject(final ResultSet resultSet, final DatabaseConnection connection) throws SQLException {
		setStateId(resultSet.getString("state_id"));
		setTime(resultSet.getTimestamp("time"));
		setSensorName(resultSet.getString("sensor_name"));
		setPlatformName(resultSet.getString("platform_name"));
		setSourceid(resultSet.getString("sourceid"));
		setReference(resultSet.getString("reference"));
		setPlatformType(resultSet.getString("platform_type"));
		setNationalityName(resultSet.getString("nationality_name"));
		setLocation(connection.createWorldLocation(resultSet, "location"));
		setElevation(resultSet.getDouble("elevation"));
		setHeading(resultSet.getDouble("heading"));
		setCourse(resultSet.getDouble("course"));
		setSpeed(resultSet.getDouble("speed"));
	}

	public void setCourse(final double course) {
		this.course = course;
	}

	public void setElevation(final double elevation) {
		this.elevation = elevation;
	}

	public void setHeading(final double heading) {
		this.heading = heading;
	}

	public void setLocation(final WorldLocation location) {
		this.location = location;
	}

	public void setNationalityName(final String nationalityName) {
		this.nationalityName = nationalityName;
	}

	public void setPlatformName(final String platformName) {
		this.platformName = platformName;
	}

	public void setPlatformType(final String platformType) {
		this.platformType = platformType;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public void setSensorName(final String sensorName) {
		this.sensorName = sensorName;
	}

	public void setSourceid(final String sourceid) {
		this.sourceid = sourceid;
	}

	public void setSpeed(final double speed) {
		this.speed = speed;
	}

	public void setStateId(final String stateId) {
		this.stateId = stateId;
	}

	public void setTime(final Timestamp time) {
		this.time = time;
	}

}
