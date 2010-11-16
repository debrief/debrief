package org.mwc.asset.comms.restlet.data;

import ASSET.Models.SensorType;


public class Sensor
{
	final private String _name;
	final private Integer _id;
	public Sensor(String name, Integer id)
	{
		_name = name;
		_id = id;
	}
	public Sensor(SensorType thisP)
	{
		this(thisP.getName(), thisP.getId());
	}
	public String getName()
	{
		return _name;
	}
	public Integer getId()
	{
		return _id;
	}

}
