/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.TacticalData.GND;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class GTrack extends PlainWrapper implements WatchableList, Layer,
		GriddableSeriesMarker
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

	private PlainSymbol _mySnail;

	private ArrayList<Editable> _myList;

	public GTrack(final GDataset data)
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

	/**
	 * produce a fix at the indicated point in the data array
	 * 
	 * @param i
	 *          array index
	 * @return
	 */
	public Fix getFixAt(final int i)
	{
		final Fix newF = new Fix();
		if (_data.getTimes() != null)
			newF.setTime(new HiResDate(_data.getTimes()[i]));
		final double lat = _data.getDataset(GDataset.LAT)[i];
		final double lon = _data.getDataset(GDataset.LON)[i];
		double depth = 0;
		if (_data.getDataset(GDataset.ELEVATION) != null)
			depth = _data.getDataset(GDataset.ELEVATION)[i];

		final WorldLocation loc = new WorldLocation(lat, lon, -depth);
		newF.setLocation(loc);

		return newF;
	}

	@Override
	public HiResDate getEndDTG()
	{
		return new HiResDate(_data.getTimes()[_data.getTimes().length - 1]);
	}

	@Override
	public Watchable[] getNearestTo(final HiResDate DTG)
	{
		return new Watchable[]
		{};
	}

	@Override
	public void filterListTo(final HiResDate start, final HiResDate end)
	{
		_startDate = start;
		_endDate = end;
	}

	@Override
	public Collection<Editable> getItemsBetween(final HiResDate start, final HiResDate end)
	{
		return null;
	}

	@Override
	public WorldArea getBounds()
	{
		if (_myBounds == null)
		{
			final double[] lats = _data.getDataset("lat");
			final double[] longs = _data.getDataset("lon");

			for (int i = 0; i < lats.length; i++)
			{
				final WorldLocation newLoc = new WorldLocation(lats[i], longs[i], 0);
				if (_myBounds == null)
					_myBounds = new WorldArea(newLoc, newLoc);
				else
					_myBounds.extend(newLoc);
			}

		}
		return _myBounds;
	}

	@Override
	public double rangeFrom(final WorldLocation other)
	{
		return Plottable.INVALID_RANGE;
	}

	@Override
	public int compareTo(final Plottable arg0)
	{
		return this.getName().compareTo(arg0.getName());
	}

	@Override
	public boolean hasEditor()
	{
		return false;
	}

	@Override
	public EditorType getInfo()
	{
		return null;
	}

	@Override
	public void exportShape()
	{

	}

	@Override
	public void append(final Layer other)
	{

	}

	@Override
	public void paint(final CanvasType dest)
	{
		if (!getVisible())
			return;

		// ok, loop through the points
		dest.setColor(super.getColor());

		final double[] lats = _data.getDataset("lat");
		final double[] lons = _data.getDataset("lon");
		final Date[] dates = _data.getTimes();

		final int[] xP = new int[lats.length];
		final int[] yP = new int[lats.length];

		boolean namePlotted = false;
		int ctr = 0;
		for (int i = 0; i < lats.length; i++)
		{
			final WorldLocation thisLoc = new WorldLocation(lats[i], lons[i], 0);
			final Point pt = dest.toScreen(thisLoc);

			boolean plotIt = true;

			// check the times
			if (_startDate != null)
			{
				if (dates != null)
				{
					final Date thisD = dates[i];
					if (thisD.getTime() < _startDate.getDate().getTime()
							|| thisD.getTime() > _endDate.getDate().getTime())
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
	public void setName(final String val)
	{

	}

	@Override
	public boolean hasOrderedChildren()
	{
		return false;
	}

	@Override
	public int getLineThickness()
	{
		return 0;
	}

	@Override
	public void add(final Editable point)
	{

	}

	@Override
	public void removeElement(final Editable point)
	{

	}

	@Override
	public Enumeration<Editable> elements()
	{

		// has the list been initialised yet?
		if (_myList == null)
		{
			// nope, make it so
			_myList = new ArrayList<Editable>();

			// and populate it...
			for (int j = 0; j < _data.size(); j++)
			{
				final int thisIndex = j;
				final Editable fw;
				final GDataItem.Setter setter = new GDataItem.Setter()
				{
					public void setValue(final String name, final Object value)
					{
						// SPECIAL CASE: is this the location?
						if (name.equals(GDataItem.LOCATION))
						{
							final WorldLocation loc = (WorldLocation) value;
							final double lat = loc.getLat();
							final double lon = loc.getLong();
							final double depth = loc.getDepth();

							// store the lat/lon
							_data.getDataset(GDataset.LAT)[thisIndex] = lat;
							_data.getDataset(GDataset.LON)[thisIndex] = lon;

							// are we working with depth?
							if (_data.getDataTypes().contains(GDataset.ELEVATION))
								_data.getDataset(GDataset.ELEVATION)[thisIndex] = -depth;
						}
						else
						{
							// normal data - just store it
							_data.getDataset(name)[thisIndex] = (Double) value;
						}
					}
				};
				final ArrayList<String> fieldList = _data.getDataTypes();
				final HashMap<String, Object> fields = new HashMap<String, Object>();
				final Iterator<String> iter = fieldList.iterator();
				while (iter.hasNext())
				{
					final String thisF = iter.next();
					// special case, is this the time field?
					if (thisF.equals("time"))
					{
						final Date thisD = _data.getTimes()[thisIndex];
						fields.put(thisF, thisD);
					}
					else
					{
						fields.put(thisF, _data.getDataset(thisF)[thisIndex]);
					}
				}

				fw = new GDataItem("Item_" + (thisIndex + 1), fields, setter);
				_myList.add(fw);
			}
		}

		return new Plottables.IteratorWrapper(_myList.iterator());
	}

	@Override
	public PlainSymbol getSnailShape()
	{
		if (_mySnail == null)
			_mySnail = MWC.GUI.Shapes.Symbols.SymbolFactory
					.createSymbol(SymbolFactory.MERCHANT);

		return _mySnail;
	}

	// //////////////////
	// griddable support
	// //////////////////

	@Override
	public Editable getSampleGriddable()
	{
		return elements().nextElement();
	}

	@Override
	public TimeStampedDataItem makeCopy(final TimeStampedDataItem item)
	{
		if (false == item instanceof GDataItem)
		{
			throw new IllegalArgumentException(
					"I am expecting a position, don't know how to copy " + item);
		}

		final GDataItem master = (GDataItem) item;
		final TimeStampedDataItem res = master.makeCopy();

		return res;
	}

	@Override
	public boolean supportsAddRemove()
	{
		return false;
	}

	@Override
	public boolean requiresManualSave()
	{
		return true;
	}

	@Override
	public void doSave(final String message)
	{
		_data.doSave(message);
	}

}
