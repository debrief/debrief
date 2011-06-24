package org.mwc.asset.comms.restlet.data;

import ASSET.Models.SensorType;

public class Sensor
{
	final private Integer _id;
	final private String _name;

	public Sensor(final SensorType thisP)
	{
		this(thisP.getName(), thisP.getId());
	}

	public Sensor(final String name, final Integer id)
	{
		_name = name;
		_id = id;
	}

	public Integer getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

}
