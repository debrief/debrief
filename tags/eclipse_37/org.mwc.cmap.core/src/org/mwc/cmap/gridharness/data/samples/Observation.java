/**
 * 
 */
package org.mwc.cmap.gridharness.data.samples;

import org.mwc.cmap.gridharness.data.WorldDistance2;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;


/**
 * @author Administrator
 * 
 */
public class Observation implements TimeStampedDataItem {

	private HiResDate _myTime;

	private double _bearing;

	private WorldDistance2 _range;

	public Observation() {
	}

	public Observation(HiResDate dtg, double bearing, WorldDistance2 range) {
		_myTime = dtg;
		_bearing = bearing;
		_range = range;
	}

	public String toString() {
		return "Observation:" + _myTime.toString();
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

	public double getBearing() {
		return _bearing;
	}

	public void setBearing(double _bearing) {
		this._bearing = _bearing;
	}

	public WorldDistance2 getRange() {
		return _range;
	}

	public void setRange(WorldDistance2 _range) {
		this._range = _range;
	}

	public void setDTG(HiResDate newTime) {
		this._myTime = newTime;
	}


}
