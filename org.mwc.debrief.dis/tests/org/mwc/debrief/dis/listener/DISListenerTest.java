package org.mwc.debrief.dis.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import edu.nps.moves.dis.EntityID;
import edu.nps.moves.dis.EntityStatePdu;
import edu.nps.moves.dis.Pdu;

public class DISListenerTest
{

	@Test
	public void testConfig()
	{
		IDISModule subject = new DISModule();
		IDISPrefs prefs = new TestPrefs();
		subject.setPrefs(prefs);
		assertNotNull(subject.getPrefs());
		assertNotNull(subject);
	}
	
	@Test
	public void testESHandling()
	{
		IDISModule subject = new DISModule();
		IDISPrefs prefs = new TestPrefs();
		IPDUProvider provider = new DummyData(3, 10);
		TestFixListener fixL = new TestFixListener();
		
		subject.setPrefs(prefs);
		subject.addFixListener(fixL);
		subject.setProvider(provider);
		
		assertEquals("correct num tracks", 1, fixL.getData().keySet().size());
		assertEquals("correct num fixes", 30, fixL.getData().values().iterator().next().size());
	}
	
	static class TestFixListener implements IDISFixListener
	{
		HashMap<Long, List<Item>> _items = new HashMap<Long, List<Item>>();
		
		static class Item
		{
			public long _id;
			public long _time;
			public double _lat;
			public double _long;
			public double _depth;
		}

		public Map<Long, List<Item>> getData()
		{
			return _items;
		}

		public Set<Long> getTracks()
		{
			return _items.keySet();
		}

		@Override
		public void add(long id, long time, double dLat, double dLong,
				double depth, double courseDegs, double speedMS)
		{
			Item newI = new Item();
			newI._id = id;
			newI._time = time;
			newI._lat = dLat;
			newI._long = dLong;
			newI._depth = depth;
			
			System.out.println("id is:" + id);
			
			List<Item> thisL = _items.get(id);
			if(thisL == null)
			{
				thisL = new ArrayList<Item>();
				_items.put(id, thisL);
			}
			
			
			thisL.add(newI);
		}

		
	}
	
	static class DummyData implements IPDUProvider
	{

		private int ctr = 0;
		final private int _numTracks;
		final private int _numPoints;

		/**
		 * 
		 * @param num how many data points to generate
		 * @param i 
		 */
		public DummyData(int numTracks, int numPoints)
		{
			_numTracks = numTracks;
			_numPoints = numPoints;
		}

		@Override
		public boolean hasMoreElements()
		{
			return ctr < (_numPoints * _numTracks);
		}

		@Override
		public Pdu next()
		{
			// create our PDU
			EntityStatePdu res = new EntityStatePdu();
			
			// give it an id
			long hisId = (long) Math.floor(Math.random() * (double)_numTracks);
			EntityID eId = new EntityID();
			eId.setEntity((int) hisId);
			res.setEntityID(eId);
			
			System.out.println("his id is:" + hisId);
			
			// increment counter
			ctr++;
			
			// done
			return res;
			
		}
		
	}
	
	interface IPDUProvider
	{
		/** flag for if more PDUs are avaialble
		 * 
		 * @return
		 */
		boolean hasMoreElements();
		
		/** retrieve the next PDU
		 * 
		 * @return
		 */
		Pdu next();
	}
	
	class TestScenarioHandler implements IDISScenarioListener
	{

		@Override
		public void restart(boolean newPlot)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void complete()
		{
			// TODO Auto-generated method stub
			
		}
		
	}

	static class TestPrefs implements IDISPrefs
	{
		final static String IP = "127.0.0.1";
		static final int PORT = 2000;

		@Override
		public String getIPAddress()
		{
			return IP;
		}

		@Override
		public int getPort()
		{
			return PORT;
		}

	}

	static interface IDISPrefs
	{
		String getIPAddress();

		int getPort();
	}

	static interface IDISPerformance
	{

	}

	static interface IDISModule
	{
		void addFixListener(IDISFixListener handler);
		void setProvider(IPDUProvider provider);
		Object getPrefs();
		void addScenarioListener(IDISScenarioListener handler);
		void setPrefs(IDISPrefs prefs);
	}

	static interface IDebriefDISListener
	{
	}

	static interface IDISScenarioListener
	{
		/**
		 * we're starting a new scenario
		 * 
		 * @param newPlot
		 *          if true, a new plot will be started, else clear this one and
		 *          re-use it
		 */
		void restart(boolean newPlot);

		/**
		 * we've been told that the scenario has completed. we may wish to modify
		 * the UI or data accordingly.
		 */
		void complete();
	}

	static interface IDISFixListener
	{
		void add(long id,long time,  double dLat, double dLong, double depth, double courseDegs, double speedMS);
	}

}
