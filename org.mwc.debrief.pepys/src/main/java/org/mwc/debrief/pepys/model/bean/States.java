package org.mwc.debrief.pepys.model.bean;

import java.sql.Timestamp;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class States implements AbstractBean {

	private int state_id;
	private Timestamp time;
	private int sensor_id;
	private double heading;
	private double course;
	private double speed;
	private double source_id;
	private double privacy_id;
	private Timestamp created_date;
	private WorldLocation location;
	
	public States() {
		
	}
	
	public int getState_id() {
		return state_id;
	}



	public void setState_id(int state_id) {
		this.state_id = state_id;
	}



	public Timestamp getTime() {
		return time;
	}



	public void setTime(Timestamp time) {
		this.time = time;
	}



	public int getSensor_id() {
		return sensor_id;
	}



	public void setSensor_id(int sensor_id) {
		this.sensor_id = sensor_id;
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



	public double getSource_id() {
		return source_id;
	}



	public void setSource_id(double source_id) {
		this.source_id = source_id;
	}



	public double getPrivacy_id() {
		return privacy_id;
	}



	public void setPrivacy_id(double privacy_id) {
		this.privacy_id = privacy_id;
	}



	public Timestamp getCreated_date() {
		return created_date;
	}



	public void setCreated_date(Timestamp created_date) {
		this.created_date = created_date;
	}



	@Override
	public String getIdField() {
		return "state_id";
	}

	public WorldLocation getLocation() {
		return location;
	}

	public void setLocation(WorldLocation location) {
		this.location = location;
	}

	public TrackWrapper createTrackWrapper() {
		final TrackWrapper newTrackWrapper = new TrackWrapper();
		final FixWrapper fixWrapper = new FixWrapper(new Fix(new HiResDate(created_date.getTime()), location, course, speed));
		newTrackWrapper.add(fixWrapper);
		return newTrackWrapper;
	}
}
