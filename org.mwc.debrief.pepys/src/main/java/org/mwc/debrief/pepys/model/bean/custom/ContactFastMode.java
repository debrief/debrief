package org.mwc.debrief.pepys.model.bean.custom;

import java.sql.Timestamp;

import MWC.GenericData.WorldLocation;

public class ContactFastMode {

	public static final String CONTACTS_FILE = "/contacts.sql";
	
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

	public String getContact_id() {
		return contact_id;
	}

	public void setContact_id(String contact_id) {
		this.contact_id = contact_id;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSensor_name() {
		return sensor_name;
	}

	public void setSensor_name(String sensor_name) {
		this.sensor_name = sensor_name;
	}

	public String getPlatform_name() {
		return platform_name;
	}

	public void setPlatform_name(String platform_name) {
		this.platform_name = platform_name;
	}

	public String getPlatform_type_name() {
		return platform_type_name;
	}

	public void setPlatform_type_name(String platform_type_name) {
		this.platform_type_name = platform_type_name;
	}

	public String getNationality_name() {
		return nationality_name;
	}

	public void setNationality_name(String nationality_name) {
		this.nationality_name = nationality_name;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public WorldLocation getLocation() {
		return location;
	}

	public void setLocation(WorldLocation location) {
		this.location = location;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
}
