package org.mwc.asset.netasset2.plot;

import java.awt.Color;
import java.util.Enumeration;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class NetPartWrapper implements Layer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _name;

	public NetPartWrapper(String name)
	{
		_name = name;
	};

	@Override
	public boolean getVisible()
	{
		return true;
	}

	@Override
	public double rangeFrom(WorldLocation other)
	{
		return -1;
	}

	@Override
	public String getName()
	{
		return _name;
	}

	@Override
	public int compareTo(Plottable arg0)
	{
		// TODO Auto-generated method stub
		return 0;
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
	public void exportShape()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void append(Layer other)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void paint(CanvasType dest)
	{
		dest.setColor(Color.red);
		dest.drawRect(20, 20, 10, 10);
	}

	@Override
	public WorldArea getBounds()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String val)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasOrderedChildren()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLineThickness()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void add(Editable point)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeElement(Editable point)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Enumeration<Editable> elements()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVisible(boolean val)
	{
		// TODO Auto-generated method stub

	}

}
