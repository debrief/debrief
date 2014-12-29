package MWC.GenericData;

import java.util.ArrayList;

public class NamedWorldPathList extends ArrayList<WorldPath>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _name;
	private WorldArea _bounds;
	
	public String getName()
	{
		return _name;
	}
	
	public void setName(String name)
	{
		this._name = name;
	}

	@Override
	public boolean add(WorldPath e)
	{
		if(_bounds == null)
		{
			_bounds = new WorldArea(e.getBounds());
		}
		else		
			_bounds.extend(e.getBounds());
		
		return super.add(e);
	}

	public WorldArea getBounds()
	{
		return _bounds;
	}
	
	
	
	
}
