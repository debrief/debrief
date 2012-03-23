package MWC.TacticalData.GND;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.Enumeration;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class GTrack implements WatchableList, Layer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the data object we wrap
	 * 
	 */
	private final GDataset _data;

	private boolean _isVisible = true;

	private WorldArea _myBounds;

	public GTrack(GDataset data)
	{
		_data = data;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public String getName()
	{
		return _data.getName();
	}

	@Override
	public HiResDate getStartDTG()
	{
		return new HiResDate(_data.getTimes()[0]);
	}

	@Override
	public HiResDate getEndDTG()
	{
		return new HiResDate(_data.getTimes()[_data.getTimes().length - 1]);
	}

	@Override
	public boolean getVisible()
	{
		return _isVisible;
	}

	@Override
	public Watchable[] getNearestTo(HiResDate DTG)
	{
		return new Watchable[]{};
	}

	@Override
	public void filterListTo(HiResDate start, HiResDate end)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Editable> getItemsBetween(HiResDate start, HiResDate end)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorldArea getBounds()
	{
		if (_myBounds == null)
		{
			double[] lats = _data.getDataset("lat");
			double[] longs = _data.getDataset("lon");

			for (int i = 0; i < lats.length; i++)
			{
				WorldLocation newLoc = new WorldLocation(lats[i], longs[i], 0);
				if (_myBounds == null)
					_myBounds = new WorldArea(newLoc, newLoc);
				else
					_myBounds.extend(newLoc);
			}

		}
		return _myBounds;
	}

	@Override
	public Color getColor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PlainSymbol getSnailShape()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double rangeFrom(WorldLocation other)
	{
		// TODO Auto-generated method stub
		return 0;
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
		// ok, loop through the points
		dest.setColor(Color.red);

		double[] lats = _data.getDataset("lat");
		double[] lons = _data.getDataset("lon");

		int[] xP = new int[lats.length];
		int[] yP = new int[lats.length];

		for (int i = 0; i < lats.length; i++)
		{
			WorldLocation thisLoc = new WorldLocation(lats[i], lons[i], 0);
			Point pt = dest.toScreen(thisLoc);
			xP[i] = pt.x;
			yP[i] = pt.y;
		}

		dest.drawPolyline(xP, yP, xP.length);

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
		_isVisible = val;
	}

}
