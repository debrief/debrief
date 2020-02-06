
package ASSET.Util.XML.Sensors;

import ASSET.Models.SensorType;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

public abstract class ActiveBroadbandHandler extends BroadbandHandler {

	protected final static String type = "ActiveBroadbandSensor";
	protected final static String SOURCE_LEVEL = "SourceLevel";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Sensor.Initial.ActiveBroadbandSensor bb = (ASSET.Models.Sensor.Initial.ActiveBroadbandSensor) toExport;

		CoreSensorHandler.exportCoreSensorBits(thisPart, bb);
		thisPart.setAttribute(APERTURE, writeThis(bb.getDetectionAperture()));
		thisPart.setAttribute(SOURCE_LEVEL, writeThis(bb.getSourceLevel()));

		parent.appendChild(thisPart);

	}

	protected double _mySourceLevel;

	public ActiveBroadbandHandler() {
		this(type);
	}

	public ActiveBroadbandHandler(final String thisType) {
		super(thisType);

		super.addAttributeHandler(new HandleDoubleAttribute(SOURCE_LEVEL) {
			@Override
			public void setValue(final String name, final double val) {
				_mySourceLevel = val;
			}
		});

	}

	@Override
	public void elementClosed() {
		// let the parent do it's stuff
		super.elementClosed();

		// and clear our values
		_working = true;
	}

	/**
	 * method for child class to instantiate sensor
	 *
	 * @param myId
	 * @param myName
	 * @return the new sensor
	 */
	@Override
	protected SensorType getSensor(final int myId) {
		// get this instance
		final ASSET.Models.Sensor.Initial.ActiveBroadbandSensor bb = new ASSET.Models.Sensor.Initial.ActiveBroadbandSensor(
				myId);

		bb.setDetectionAperture(_myAperture);
		bb.setSourceLevel(_mySourceLevel);
		bb.setWorking(_working);
		return bb;
	}
}