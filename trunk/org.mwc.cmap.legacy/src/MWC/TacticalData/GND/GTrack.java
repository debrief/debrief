package MWC.TacticalData.GND;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class GTrack extends PlainWrapper implements WatchableList, Layer
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

	/**
	 * our coverage
	 * 
	 */
	private WorldArea _myBounds;

	private HiResDate _startDate;

	private HiResDate _endDate;

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
		return _data.getPlatform() + "-" + _data.getName();
	}

	@Override
	public HiResDate getStartDTG()
	{
		return new HiResDate(_data.getTimes()[0]);
	}

	public int size()
	{
		return _data.getDataset(_data.getDataTypes().get(0)).length;
	}

	public Fix getFixAt(int i)
	{
		Fix newF = new Fix();
		if (_data.getTimes() != null)
			newF.setTime(new HiResDate(_data.getTimes()[i]));
		double lat = _data.getDataset(GDataset.LAT)[i];
		double lon = _data.getDataset(GDataset.LON)[i];
		double depth = 0;
		if(_data.getDataset(GDataset.ELEVATION) != null)
			depth = _data.getDataset(GDataset.ELEVATION)[i];
		
		WorldLocation loc = new WorldLocation(lat, lon,- depth);
		newF.setLocation(loc);
		
		return newF;
	}

	@Override
	public HiResDate getEndDTG()
	{
		return new HiResDate(_data.getTimes()[_data.getTimes().length - 1]);
	}

	@Override
	public Watchable[] getNearestTo(HiResDate DTG)
	{
		return new Watchable[]
		{};
	}

	@Override
	public void filterListTo(HiResDate start, HiResDate end)
	{
		_startDate = start;
		_endDate = end;
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
		return Plottable.INVALID_RANGE;
	}

	@Override
	public int compareTo(Plottable arg0)
	{
		return this.getName().compareTo(arg0.getName());
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
		if (!getVisible())
			return;

		// ok, loop through the points
		dest.setColor(super.getColor());

		double[] lats = _data.getDataset("lat");
		double[] lons = _data.getDataset("lon");
		Date[] dates = _data.getTimes();

		int[] xP = new int[lats.length];
		int[] yP = new int[lats.length];

		boolean namePlotted = false;
		int ctr = 0;
		for (int i = 0; i < lats.length; i++)
		{
			WorldLocation thisLoc = new WorldLocation(lats[i], lons[i], 0);
			Point pt = dest.toScreen(thisLoc);

			boolean plotIt = true;

			// check the times
			if (_startDate != null)
			{
				if (dates != null)
				{
					Date thisD = dates[i];
					if (thisD.getTime() < _startDate.getDate().getTime()
							&& thisD.getTime() > _endDate.getDate().getTime())
						plotIt = false;
				}
			}

			if (plotIt)
			{
				// is this the first point? if so, plot the name
				if (!namePlotted)
				{
					dest.drawText(getName(), pt.x, pt.y);
					namePlotted = true;
				}

				xP[ctr] = pt.x;
				yP[ctr] = pt.y;

				// increment
				ctr++;
			}
		}

		dest.drawPolyline(xP, yP, ctr);

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

}
