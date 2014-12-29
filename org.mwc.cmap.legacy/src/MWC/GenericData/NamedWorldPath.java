package MWC.GenericData;

public class NamedWorldPath extends WorldPath
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String _name;

	public NamedWorldPath(WorldPath path)
	{
		super(path);
	}

	public String getName()
	{
		return _name;
	}

	public void setName(String name)
	{
		this._name = name;
	}
	
	
}
