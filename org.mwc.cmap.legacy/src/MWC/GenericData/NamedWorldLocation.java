package MWC.GenericData;

public class NamedWorldLocation extends WorldLocation
{

	public NamedWorldLocation(WorldLocation other)
	{
		super(other);
	}
	
	
	
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
