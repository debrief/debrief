
package ASSET.Util.XML.Sensors;

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

import ASSET.Models.SensorType;

public abstract class ActiveInterceptHandler extends CoreSensorHandler {

	private final static String type = "ActiveInterceptSensor";

	static public void exportThis(final Object toExport, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Sensor.Initial.ActiveInterceptSensor bb = (ASSET.Models.Sensor.Initial.ActiveInterceptSensor) toExport;
		CoreSensorHandler.exportCoreSensorBits(thisPart, bb);

		parent.appendChild(thisPart);

	}

	public ActiveInterceptHandler() {
		this(type);
	}

	public ActiveInterceptHandler(final String myType) {
		super(myType);
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
		final ASSET.Models.Sensor.Initial.ActiveInterceptSensor bb = new ASSET.Models.Sensor.Initial.ActiveInterceptSensor(
				myId);
		return bb;
	}
}