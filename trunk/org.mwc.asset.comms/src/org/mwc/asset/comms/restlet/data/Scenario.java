package org.mwc.asset.comms.restlet.data;

public class Scenario
{
	final private String _name;
	final private Integer _id;
	public Scenario(String name, int id)
	{
		_name = name;
		_id = id;
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
