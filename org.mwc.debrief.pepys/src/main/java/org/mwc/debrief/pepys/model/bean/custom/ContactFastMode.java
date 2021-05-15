package org.mwc.debrief.pepys.model.bean.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.mwc.debrief.pepys.model.bean.AbstractBean;
import org.mwc.debrief.pepys.model.bean.PlainBean;
import org.mwc.debrief.pepys.model.db.DatabaseConnection;

import MWC.GenericData.WorldLocation;

public class ContactFastMode implements AbstractBean, PlainBean{

	private String contact_id;

	private Timestamp time;

	private String name;

	private String sensor_name;

	private String platform_name;

	private String platform_type_name;

	private String nationality_name;

	private double bearing;

	private double range;

	private WorldLocation location;

	private String reference;

	public ContactFastMode() {

	}

	public double getBearing() {
		return bearing;
	}

	public String getContact_id() {
		return contact_id;
	}

	public WorldLocation getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public String getNationality_name() {
		return nationality_name;
	}

	public String getPlatform_name() {
		return platform_name;
	}

	public String getPlatform_type_name() {
		return platform_type_name;
	}

	public double getRange() {
		return range;
	}

	public String getReference() {
		return reference;
	}

	public String getSensor_name() {
		return sensor_name;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setBearing(final double bearing) {
		this.bearing = bearing;
	}

	public void setContact_id(final String contact_id) {
		this.contact_id = contact_id;
	}

	public void setLocation(final WorldLocation location) {
		this.location = location;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNationality_name(final String nationality_name) {
		this.nationality_name = nationality_name;
	}

	public void setPlatform_name(final String platform_name) {
		this.platform_name = platform_name;
	}

	public void setPlatform_type_name(final String platform_type_name) {
		this.platform_type_name = platform_type_name;
	}

	public void setRange(final double range) {
		this.range = range;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public void setSensor_name(final String sensor_name) {
		this.sensor_name = sensor_name;
	}

	public void setTime(final Timestamp time) {
		this.time = time;
	}

	@Override
	public void retrieveObject(ResultSet resultSet, DatabaseConnection connection) throws SQLException {
		setContact_id(resultSet.getString("contact_id"));
		setTime(resultSet.getTimestamp("time"));
		setName(resultSet.getString("name"));
		setSensor_name(resultSet.getString("sensor_name"));
		setPlatform_name(resultSet.getString("platform_name"));
		setPlatform_type_name(resultSet.getString("platform_type_name"));
		setNationality_name(resultSet.getString("nationality_name"));
		setBearing(resultSet.getDouble("bearing"));
		setRange(resultSet.getDouble("range"));
		setLocation(connection.createWorldLocation(resultSet, "location"));
		setReference(resultSet.getString("reference"));
	}
}
