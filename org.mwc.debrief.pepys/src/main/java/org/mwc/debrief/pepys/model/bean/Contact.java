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

import java.sql.Timestamp;
import java.util.Date;

import org.mwc.debrief.pepys.model.db.annotation.FieldName;
import org.mwc.debrief.pepys.model.db.annotation.Id;
import org.mwc.debrief.pepys.model.db.annotation.Location;
import org.mwc.debrief.pepys.model.db.annotation.ManyToOne;
import org.mwc.debrief.pepys.model.db.annotation.OneToOne;
import org.mwc.debrief.pepys.model.db.annotation.TableName;
import org.mwc.debrief.pepys.model.db.annotation.Time;
import org.mwc.debrief.pepys.model.db.annotation.Transient;
import org.mwc.debrief.pepys.model.tree.TreeStructurable;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;

@TableName(name = "Contacts")
public class Contact implements AbstractBean, TreeStructurable {

	@Id
	private String contact_id;
	private String name;

	@ManyToOne
	@FieldName(name = "sensor_id")
	private Sensor sensor;

	@Time
	private Timestamp time;
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
	private double soa;

	@ManyToOne
	@FieldName(name = "source_id")
	private Datafile datafile;

	@OneToOne
	@FieldName(name = "privacy_id")
	private Privacy privacy;

	private Date created_date;

	@Location
	private WorldLocation location;

	@Transient
	private String reference;

	@Transient
	private int count;

	public Contact() {

	}

	@Override
	public void doImport(final Layers _layers) {
		final String layerName = getDatafile().getReference();
		final Layer target = _layers.findLayer(layerName, false);

		if (target != null && target instanceof BaseLayer) {
			final BaseLayer folder = (BaseLayer) target;
			final Editable found = folder.find(getPlatform().getTrackName());
			if (found != null && found instanceof TrackWrapper) {
				final TrackWrapper track = (TrackWrapper) found;

				final SensorWrapper newSensorWrapper = new SensorWrapper(getSensor().getName());
				final SensorContactWrapper contact = new SensorContactWrapper(track.getName(), new HiResDate(getTime()),
						null, bearing, location, null, getName(), 0, getSensor().getName());
				newSensorWrapper.add(contact);
				track.add(newSensorWrapper);
			}
		}
	}

	public double getBearing() {
		return bearing;
	}

	public String getClassification() {
		return classification;
	}

	public String getConfidence() {
		return confidence;
	}

	public String getContact_id() {
		return contact_id;
	}

	public String getContact_type() {
		return contact_type;
	}

	@Override
	public int getCount() {
		return count;
	}

	public Date getCreated_date() {
		return created_date;
	}

	@Override
	public Datafile getDatafile() {
		return datafile;
	}

	public double getElevation() {
		return elevation;
	}

	public double getFreq() {
		return freq;
	}

	public WorldLocation getLocation() {
		return location;
	}

	public double getMajor() {
		return major;
	}

	public double getMinor() {
		return minor;
	}

	public double getMla() {
		return mla;
	}

	public String getName() {
		return name;
	}

	public double getOrientation() {
		return orientation;
	}

	@Override
	public Platform getPlatform() {
		if (sensor != null) {
			return sensor.getPlatform();
		}
		return null;
	}

	public Privacy getPrivacy() {
		return privacy;
	}

	public String getReference() {
		return reference;
	}

	public double getRel_bearing() {
		return rel_bearing;
	}

	public Sensor getSensor() {
		return sensor;
	}

	@Override
	public SensorType getSensorType() {
		if (sensor != null) {
			return sensor.getSensorType();
		}
		return null;
	}

	public double getSoa() {
		return soa;
	}

	@Override
	public Date getTime() {
		return time;
	}

	public void setBearing(final double bearing) {
		this.bearing = bearing;
	}

	public void setClassification(final String classification) {
		this.classification = classification;
	}

	public void setConfidence(final String confidence) {
		this.confidence = confidence;
	}

	public void setContact_id(final String contact_id) {
		this.contact_id = contact_id;
	}

	public void setContact_type(final String contact_type) {
		this.contact_type = contact_type;
	}

	public void setCount(final int count) {
		this.count = count;
	}

	public void setCreated_date(final Date created_date) {
		this.created_date = created_date;
	}

	public void setDatafile(final Datafile datafile) {
		this.datafile = datafile;
	}

	public void setElevation(final double elevation) {
		this.elevation = elevation;
	}

	public void setFreq(final double freq) {
		this.freq = freq;
	}

	public void setLocation(final WorldLocation location) {
		this.location = location;
	}

	public void setMajor(final double major) {
		this.major = major;
	}

	public void setMinor(final double minor) {
		this.minor = minor;
	}

	public void setMla(final double mla) {
		this.mla = mla;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOrientation(final double orientation) {
		this.orientation = orientation;
	}

	public void setPrivacy(final Privacy privacy) {
		this.privacy = privacy;
	}

	public void setReference(final String reference) {
		this.reference = reference;
	}

	public void setRel_bearing(final double rel_bearing) {
		this.rel_bearing = rel_bearing;
	}

	public void setSensor(final Sensor sensor) {
		this.sensor = sensor;
	}

	public void setSoa(final double soa) {
		this.soa = soa;
	}

	public void setTime(final Timestamp time) {
		this.time = time;
	}

}
