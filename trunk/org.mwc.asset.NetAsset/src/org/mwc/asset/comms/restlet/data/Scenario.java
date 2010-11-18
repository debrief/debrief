package org.mwc.asset.comms.restlet.data;

import java.io.Serializable;
import java.util.Vector;


public class Scenario implements Serializable
{
	public static class ScenarioList extends Vector<Scenario> implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
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
