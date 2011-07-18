package org.mwc.asset.netasset2.plot;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Participants.Status;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class NetPartWrapper implements Layer
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _name;
	private WorldArea _myArea;

	private HashMap<Long, CompositeStatusHolder> history;

	private static class CompositeStatusHolder
	{
		public Status status;
		public DetectionList detections;
	}

	public NetPartWrapper(String name)
	{
		_name = name;
		history = new HashMap<Long, CompositeStatusHolder>();
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
		Collection<CompositeStatusHolder> values = history.values();
		Iterator<CompositeStatusHolder> iter = values.iterator();
		while (iter.hasNext())
		{
			CompositeStatusHolder comp = iter.next();
			Status status = comp.status;
			// ok, draw the positions at this time
			WorldLocation currentLoc = status.getLocation();
			Point p2 = new Point(dest.toScreen(currentLoc));
			dest.drawRect(p2.x, p2.y, 3, 3);

			DetectionList detList = comp.detections;
			if (detList != null)
			{
				Iterator<DetectionEvent> dets = detList.iterator();
				while (dets.hasNext())
				{
					DetectionEvent thisD = dets.next();
					Float brg = thisD.getBearing();
					if (brg != null)
					{
						double brgRads = MWC.Algorithms.Conversions.Degs2Rads(brg);
						WorldDistance rng = new WorldDistance(20, WorldDistance.NM);
						WorldDistance depth = new WorldDistance(0, WorldDistance.METRES);
						WorldLocation otherEnd = currentLoc.add(new WorldVector(brgRads,
								rng, depth));
						Point p3 = dest.toScreen(otherEnd);
						dest.drawLine(p2.x, p2.y, p3.x, p3.y);
					}
				}
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

	private CompositeStatusHolder getStat(long time)
	{
		CompositeStatusHolder stat = history.get(time);
		if (stat == null)
		{
			stat = new CompositeStatusHolder();
			history.put(time, stat);
		}

		return stat;
	}

	public void setStatus(Status status)
	{
		WorldLocation loc = status.getLocation();
		if (_myArea == null)
			_myArea = new WorldArea(loc, loc);
		else
			_myArea.extend(loc);

		getStat(status.getTime()).status = status;
	}

	public void addDetections(DetectionList dets)
	{
		long endTime = dets.getTimeCoverage().getEndDTG().getDate().getTime();
		getStat(endTime).detections = dets;
	}

}
