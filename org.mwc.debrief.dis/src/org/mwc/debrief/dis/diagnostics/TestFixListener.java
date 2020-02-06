package org.mwc.debrief.dis.diagnostics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mwc.debrief.dis.listeners.IDISFixListener;

public class TestFixListener implements IDISFixListener {
	public static class Item {
		public long _id;
		public long _time;
		public double _lat;
		public double _long;
		public double _depth;
		public double _course;
		public double _speed;
	}

	HashMap<Long, List<TestFixListener.Item>> _items = new HashMap<Long, List<TestFixListener.Item>>();

	@Override
	public void add(final long time, final short exerciseId, final long id, final String eName, final short force,
			final short kind, final short domain, final short category, final boolean isHighlighted, final double dLat,
			final double dLong, final double depth, final double courseDegs, final double speedMS, final int damage) {
		final TestFixListener.Item newI = new Item();
		newI._id = id;
		newI._time = time;
		newI._lat = dLat;
		newI._long = dLong;
		newI._depth = depth;
		newI._course = courseDegs;
		newI._speed = speedMS;

		System.out.println("id:" + id);

		List<TestFixListener.Item> thisL = _items.get(id);
		if (thisL == null) {
			thisL = new ArrayList<TestFixListener.Item>();
			_items.put(id, thisL);
		}

		thisL.add(newI);
	}

	public Map<Long, List<TestFixListener.Item>> getData() {
		return _items;
	}

	public Set<Long> getTracks() {
		return _items.keySet();
	}

}