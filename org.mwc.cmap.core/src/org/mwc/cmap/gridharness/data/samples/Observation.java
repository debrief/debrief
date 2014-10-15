/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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

	public Observation(final HiResDate dtg, final double bearing, final WorldDistance2 range) {
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

	public void setTime(final HiResDate time) {
		_myTime = time;
	}

	public double getBearing() {
		return _bearing;
	}

	public void setBearing(final double _bearing) {
		this._bearing = _bearing;
	}

	public WorldDistance2 getRange() {
		return _range;
	}

	public void setRange(final WorldDistance2 _range) {
		this._range = _range;
	}

	public void setDTG(final HiResDate newTime) {
		this._myTime = newTime;
	}


}
