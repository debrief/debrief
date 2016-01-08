package Debrief.Wrappers.Measurements;

import MWC.GUI.CanvasType;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class MeasurementBlock implements SupplementalDataBlock
{
	private String _name;
	private Object _parent;

	public void setName(String name)
	{
		_name = name;
	}
	
	@Override
	public String getName()
	{
		return _name;
	}
	
	

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public boolean hasEditor()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EditorType getInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWrapper(Object parent)
	{
		_parent = parent;
	}

	@Override
	public void paint(CanvasType dest)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public WorldArea getBounds()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setVisible(boolean val)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public double rangeFrom(WorldLocation other)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int compareTo(Plottable o)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
