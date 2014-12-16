package MWC.GenericData;

import java.util.ArrayList;

public class NamedWorldPathList extends ArrayList<WorldPath>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _name;
	
	public String getName()
	{
		return _name;
	}
	
	public void setName(String name)
	{
		this._name = name;
	}
	
	
	
	
}
