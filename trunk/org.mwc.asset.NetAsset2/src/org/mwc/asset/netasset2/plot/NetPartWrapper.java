package org.mwc.asset.netasset2.plot;

import java.awt.Color;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import ASSET.Participants.Status;
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
	private Vector<Status> _history;
	private WorldArea _myArea;

	public NetPartWrapper(String name)
	{
		_name = name;
		_history = new Vector<Status>();
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
	}

	@Override
	public void paint(CanvasType dest)
	{
		dest.setColor(Color.BLUE);
		if (_history.size() > 0)
		{
			// but, we plot all of them.
			Iterator<Status> iter = _history.iterator();
			while (iter.hasNext())
			{
				Status status = (Status) iter.next();
				Point p2 = dest.toScreen(status.getLocation());
				dest.drawRect(p2.x, p2.y, 3, 3);
			}
		}
	}

	@Override
	public WorldArea getBounds()
	{
		return _myArea;
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

	public void setStatus(Status status)
	{
		WorldLocation loc = status.getLocation();
		if (_myArea == null)
			_myArea = new WorldArea(loc, loc);
		else
			_myArea.extend(loc);

		_history.add(status);
	}

}
