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

import org.mwc.debrief.pepys.model.db.annotation.FieldName;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.OneToOne;
import org.mwc.debrief.pepys.model.db.annotation.TableName;

import MWC.GenericData.WorldLocation;

@TableName(name = "Contacts")
public class Contact implements AbstractBean {

	@Id
	private int contact_id;
	private String name;
	
	@ManyToOne
	@FieldName(name = "sensor_id")
	private Sensor sensor;
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
	
	@ManyToOne
	@FieldName(name = "source_id")
	private Datafile datafile;
	
	@OneToOne
	@FieldName(name = "privacy_id")
	private Privacy privacy;
	
	private Date created_date;
	private WorldLocation location;

	public Contact() {

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

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

	public Datafile getDatafile() {
		return datafile;
	}

	public void setDatafile(Datafile datafile) {
		this.datafile = datafile;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	public void setPrivacy(Privacy privacy) {
		this.privacy = privacy;
	}
	

}
