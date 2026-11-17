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

package ASSET.Models.Sensor.Cookie;

import MWC.GenericData.WorldDistance;

/**
 * simple sensor that uses cookie cutter model for detections
 *
 * @author ianmayo
 *
 */
public class PlainCookieSensor extends TypedCookieSensor {

	////////////////////////////////////////////////////
	// the editor object
	////////////////////////////////////////////////////
	static public class PlainCookieInfo extends BaseSensorInfo {
		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public PlainCookieInfo(final PlainCookieSensor data) {
			super(data);
		}

		/**
		 * editable GUI properties for our participant
		 *
		 * @return property descriptions
		 */
		@Override
		public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final java.beans.PropertyDescriptor[] res = { prop("Name", "the name of this sensor"),
						prop("DetectionRange", "detection range of this sensor") };
				return res;
			} catch (final java.beans.IntrospectionException e) {
				return super.getPropertyDescriptors();
			}
		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final TypedRangeDoublet _myRangeDoublet;

	public PlainCookieSensor(final int id, final WorldDistance detRange) {
		super(id, null);

		_myRangeDoublet = new TypedRangeDoublet(null, detRange);
		addRangeDoublet(_myRangeDoublet);
	}

	public WorldDistance getDetectionRange() {
		return _myRangeDoublet.getRange();
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new PlainCookieInfo(this);
		return _myEditor;
	}

	@Override
	public String getVersion() {
		return "$Date: 2010-01-19 15:34:14 +0100  $";
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	public void setDetectionRange(final WorldDistance range) {
		_myRangeDoublet.setRange(range);
	}

}
