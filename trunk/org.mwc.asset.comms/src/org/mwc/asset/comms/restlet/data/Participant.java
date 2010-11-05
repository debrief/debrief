package org.mwc.asset.comms.restlet.data;


public class Participant
{
	final private String _name;
	final private Integer _id;
	public Participant(String name, Integer id)
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
