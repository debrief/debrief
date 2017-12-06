/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package ASSET.Util.XML.Sensors;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import ASSET.Models.SensorType;
import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.Util.DurationHandler;

public abstract class NarrowbandHandler extends CoreSensorHandler
{

	private final static String type = "NarrowbandSensor";
	protected final static String STEADY_TIME = "SteadyTime";
	protected final static String HAS_BEARING = "HasBearing";
  protected final static String SECOND_HARMONIC = "SecondHarmonic";

	protected Duration _mySteadyTime;
	protected Boolean _hasBearing = false;
	protected Boolean _secondHarmonic = null;

	public NarrowbandHandler(String myType)
	{
		super(myType);

		super.addHandler(new DurationHandler(STEADY_TIME)
		{
			public void setDuration(Duration res)
			{
				_mySteadyTime = res;
			}
		});
		super.addAttributeHandler(new HandleBooleanAttribute(HAS_BEARING)
		{
			@Override
			public void setValue(String name, boolean value)
			{
				_hasBearing = value;
			}
		});
    super.addAttributeHandler(new HandleBooleanAttribute(SECOND_HARMONIC)
    {
      @Override
      public void setValue(String name, boolean value)
      {
        _secondHarmonic = value;
      }
    });
	}

	public NarrowbandHandler()
	{
		this(type);
	}

	/**
	 * method for child class to instantiate sensor
	 * 
	 * @param myId
	 * @param myName
	 * @return the new sensor
	 */
	protected SensorType getSensor(int myId)
	{
		// get this instance
		final ASSET.Models.Sensor.Initial.NarrowbandSensor bb = new ASSET.Models.Sensor.Initial.NarrowbandSensor(
				myId);

		bb.setSteadyTime(_mySteadyTime);
		bb.setHasBearing(_hasBearing);
		if(_secondHarmonic != null)
		{
		  bb.setSecondHarmonic(_secondHarmonic.booleanValue());
		}

		_secondHarmonic = null;
		_hasBearing = false;
		_mySteadyTime = null;
		
		return bb;
	}

	static public void exportThis(final Object toExport,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		// create ourselves
		final org.w3c.dom.Element thisPart = doc.createElement(type);

		// get data item
		final ASSET.Models.Sensor.Initial.NarrowbandSensor bb = (ASSET.Models.Sensor.Initial.NarrowbandSensor) toExport;
		CoreSensorHandler.exportCoreSensorBits(thisPart, bb);

		DurationHandler.exportDuration(STEADY_TIME, bb.getSteadyTime(), thisPart,
				doc);
		thisPart.setAttribute(HAS_BEARING, writeThis(bb.getHasBearing()));

		parent.appendChild(thisPart);

	}
}