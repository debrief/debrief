/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.pepys.model.bean;

import java.util.Date;

import MWC.GenericData.WorldLocation;

public class Contacts implements AbstractBean, FilterableBean{

	private int contact_id;
	private String name;
	private int sensor_id;
	private Date time;
	private double bearing;
	private double rel_bearing;
	private double freq;
	private double elevation;
	private double major;
	private double minor;
	private double orientation;
	private String classification;
	private String confidence;
	private String contact_type;
	private double mla;
	private double sla;
	private int subject_id;
	private int source_id;
	private int privacy_id;
	private Date created_date;
	private WorldLocation location;
	
	public Contacts() {
		
	}
	
	public int getContact_id() {
		return contact_id;
	}



	public void setContact_id(int contact_id) {
		this.contact_id = contact_id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public int getSensor_id() {
		return sensor_id;
	}



	public void setSensor_id(int sensor_id) {
		this.sensor_id = sensor_id;
	}



	public Date getTime() {
		return time;
	}



	public void setTime(Date time) {
		this.time = time;
	}



	public double getBearing() {
		return bearing;
	}



	public void setBearing(double bearing) {
		this.bearing = bearing;
	}



	public double getRel_bearing() {
		return rel_bearing;
	}



	public void setRel_bearing(double rel_bearing) {
		this.rel_bearing = rel_bearing;
	}



	public double getFreq() {
		return freq;
	}



	public void setFreq(double freq) {
		this.freq = freq;
	}



	public double getElevation() {
		return elevation;
	}



	public void setElevation(double elevation) {
		this.elevation = elevation;
	}



	public double getMajor() {
		return major;
	}



	public void setMajor(double major) {
		this.major = major;
	}



	public double getMinor() {
		return minor;
	}



	public void setMinor(double minor) {
		this.minor = minor;
	}



	public double getOrientation() {
		return orientation;
	}



	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}



	public String getClassification() {
		return classification;
	}



	public void setClassification(String classification) {
		this.classification = classification;
	}



	public String getConfidence() {
		return confidence;
	}



	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}



	public String getContact_type() {
		return contact_type;
	}



	public void setContact_type(String contact_type) {
		this.contact_type = contact_type;
	}



	public double getMla() {
		return mla;
	}



	public void setMla(double mla) {
		this.mla = mla;
	}



	public double getSla() {
		return sla;
	}



	public void setSla(double sla) {
		this.sla = sla;
	}



	public int getSubject_id() {
		return subject_id;
	}



	public void setSubject_id(int subject_id) {
		this.subject_id = subject_id;
	}



	public int getSource_id() {
		return source_id;
	}



	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}



	public int getPrivacy_id() {
		return privacy_id;
	}



	public void setPrivacy_id(int privacy_id) {
		this.privacy_id = privacy_id;
	}



	public Date getCreated_date() {
		return created_date;
	}



	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}



	public WorldLocation getLocation() {
		return location;
	}

	
	public void setLocation(WorldLocation location) {
		this.location = location;
	}

	@Override
	public String getIdField() {
		return "contact_id";
	}

	@Override
	public String getTimeField() {
		return "time";
	}

	@Override
	public String getLocationField() {
		return "location";
	}

}
