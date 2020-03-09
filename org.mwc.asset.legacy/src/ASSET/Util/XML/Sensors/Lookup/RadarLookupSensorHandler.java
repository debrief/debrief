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

package ASSET.Util.XML.Sensors.Lookup;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.RadarLookupSensor;
import MWC.GenericData.Duration;

/**
 * Created by IntelliJ IDEA. User: Ian Date: 03-Feb-2004 Time: 14:31:24 To
 * change this template use Options | File Templates.
 */
abstract public class RadarLookupSensorHandler extends CoreLookupSensorHandler {
	private final static String type = "RadarLookupSensor";
	private final static String K = "K";

	double _k;

	public RadarLookupSensorHandler() {
		super(type);

		super.addAttributeHandler(new HandleDoubleAttribute(K) {
			@Override
			public void setValue(final String name, final double val) {
				_k = val;
			}
		});
	}

	@Override
	protected LookupSensor createLookupSensor(final int id, final double VDR, final long TBDO, final double MRF,
			final double CRF, final Duration CTP, final double IRF, final Duration ITP) {
		return new RadarLookupSensor(id, VDR, TBDO, MRF, CRF, CTP, IRF, ITP, _k);
	}

}