package org.mwc.debrief.multipath.model;

import java.util.Iterator;
import java.util.Vector;

public class SVP
{
	private class Observation
	{
		double _depth;
		double _speed;
		public Observation(double depth, double speed)
		{
			_depth = depth;
			_speed = speed;
		}
	}
	
	private Vector<Observation> _myData;

	public SVP()
	{
		load(null);
	}
	
	public void load(String source)
	{
		_myData = new Vector<Observation>();
		_myData.add(new Observation(0,0));
		_myData.add(new Observation(10,20));
		_myData.add(new Observation(30,60));
		_myData.add(new Observation(50,80));
		_myData.add(new Observation(100,100));
	}
	
	public double getMeanSpeedBetween(double shallowDepth, double deepDepth)
	{
		// ok, first find the point before the shallow depth
		
		// now find the point after the deep depth
		
		return 0.0;
	}
	
	private Observation pointBefore(double depth)
	{
		Observation res  = _myData.firstElement();
		Iterator<Observation> iter = _myData.iterator();
		while(iter.hasNext())
		{
			Observation obs = iter.next();
			if(obs._depth > depth)
			{
				// ok, passed our data value
				break;
			}
			res = obs;
		}
		
		return res;
	}
	
	private Observation pointAfter(double depth)
	{
		Observation res  = null;
		Iterator<Observation> iter = _myData.iterator();
		while(iter.hasNext())
		{
			Observation obs = iter.next();
			if(obs._depth > depth)
			{
				res = obs;
				break;
			}
		}
		
		return res;
	}
	
	///////////////////////////////////////////////////
	// and the testing goes here
	///////////////////////////////////////////////////
	public static class SVP_Test extends junit.framework.TestCase
	{
		public void testIndex()
		{
			SVP svp = new SVP();
			
			assertEquals("got data", 5, svp._myData.size());
			
			Observation t1 = svp.pointBefore(0);
			assertEquals("correct depth", 0d, t1._depth);
			
			t1 = svp.pointBefore(5);
			assertEquals("correct depth", 0d, t1._depth);

			t1 = svp.pointBefore(15);
			assertEquals("correct depth", 10d, t1._depth);

			t1 = svp.pointBefore(35);
			assertEquals("correct depth", 30d, t1._depth);
			
			t1 = svp.pointBefore(50);
			assertEquals("correct depth", 50d, t1._depth);
			
			
			 t1 = svp.pointAfter(0);
			assertEquals("correct depth", 10d, t1._depth);
			
			t1 = svp.pointAfter(5);
			assertEquals("correct depth", 10d, t1._depth);

			t1 = svp.pointAfter(15);
			assertEquals("correct depth", 30d, t1._depth);

			t1 = svp.pointAfter(35);
			assertEquals("correct depth", 50d, t1._depth);
			
			t1 = svp.pointAfter(50);
			assertEquals("correct depth", 100d, t1._depth);
			
			t1 = svp.pointAfter(150);
			assertEquals("correct depth", null, t1);
		}
	}
}
