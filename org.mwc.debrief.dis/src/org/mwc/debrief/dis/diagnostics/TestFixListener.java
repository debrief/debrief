package org.mwc.debrief.dis.diagnostics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mwc.debrief.dis.listeners.IDISFixListener;

public class TestFixListener implements IDISFixListener
{
	HashMap<Long, List<TestFixListener.Item>> _items = new HashMap<Long, List<TestFixListener.Item>>();

	public static class Item
	{
		public long _id;
		public long _time;
		public double _lat;
		public double _long;
		public double _depth;
		public double _course;
		public double _speed;
	}

	public Map<Long, List<TestFixListener.Item>> getData()
	{
		return _items;
	}

	public Set<Long> getTracks()
	{
		return _items.keySet();
	}

	@Override
	public void add(long time, short exerciseId, long id, String eName,
			short force, short kind, short domain, short category, boolean isHighlighted, double dLat, double dLong, double depth, double courseDegs, double speedMS, final int damage)
	{
		TestFixListener.Item newI = new Item();
		newI._id = id;
		newI._time = time;
		newI._lat = dLat;
		newI._long = dLong;
		newI._depth = depth;
		newI._course = courseDegs;
		newI._speed = speedMS;
		
		
		System.out.println("id:" + id);

		List<TestFixListener.Item> thisL = _items.get(id);
		if (thisL == null)
		{
			thisL = new ArrayList<TestFixListener.Item>();
			_items.put(id, thisL);
		}

		thisL.add(newI);
	}

}