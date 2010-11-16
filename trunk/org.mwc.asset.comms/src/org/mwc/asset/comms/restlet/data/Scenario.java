package org.mwc.asset.comms.restlet.data;

public class Scenario
{
	final private Integer _id;
	final private String _name;

	public Scenario(final String name, final int id)
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
