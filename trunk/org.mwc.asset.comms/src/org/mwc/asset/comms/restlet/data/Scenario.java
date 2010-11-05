package org.mwc.asset.comms.restlet.data;

public class Scenario
{
	final private String _name;
	public Scenario(String name)
	{
		_name = name;
	}
	public String getName()
	{
		return _name;
	}
	public Integer[] getListOfParticipants()
	{
		return new Integer[]{1,2,3};
	}
}
