/**
 * 
 */
package org.mwc.cmap.gridharness.data.samples;

import org.mwc.cmap.gridharness.data.WorldLocation;
import org.mwc.cmap.gridharness.data.WorldSpeed2;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;

/**
 * @author Administrator
 * 
 */
public class Position implements TimeStampedDataItem {

	private HiResDate _myTime;

	private double _course;

	private WorldSpeed2 _speed;

	private WorldLocation _location = WorldLocation.NULL;

	public Position() {
	}

	public Position(HiResDate time, double latitude, double longitude, double course, WorldSpeed2 speed) {
		_myTime = time;
		_course = course;
		_location = new WorldLocation(latitude, longitude);
		_speed = speed;
	}

	public String toString() {
		return "Position:" + _myTime.toString();
	}

	public HiResDate getDTG() {
		return _myTime;
	}

	public HiResDate getTime() {
		return _myTime;
	}

	public void setTime(HiResDate time) {
		_myTime = time;
	}

	public double getLatitude() {
		return _location.getLatitude();
	}

	public void setLatitude(double latitude) {
		this._location = new WorldLocation(latitude, _location.getLongitude());
	}

	public double getLongitude() {
		return _location.getLongitude();
	}

	public void setLongitude(double _longitude) {
		this._location = new WorldLocation(_location.getLatitude(), _longitude);
	}

	public void setLocation(WorldLocation location) {
		this._location = location;
	}

	public WorldLocation getLocation() {
		return this._location;
	}

	public double getCourse() {
		return _course;
	}

	public void setCourse(double _course) {
		this._course = _course;
	}

	public WorldSpeed2 getSpeed() {
		return _speed;
	}

	public void setSpeed(WorldSpeed2 _speed) {
		this._speed = _speed;
	}

	public void setDTG(HiResDate newTime) {
		this._myTime = newTime;
	}

}
