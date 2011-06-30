package org.mwc.asset.netasset2.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Vector;

import org.mwc.asset.netasset2.common.Network.AHandler;
import org.mwc.asset.netasset2.common.Network.LightParticipant;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.core.AClient;
import org.mwc.asset.netasset2.core.AServer;

import ASSET.NetworkScenario;
import ASSET.Scenario.MultiScenarioLister;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

public class CoreTest
{

	public static class TrackWrapper_Test extends junit.framework.TestCase
	{

		private Vector<String> _events;
		protected Vector<LightScenario> _myList;

		@Override
		protected void setUp() throws Exception
		{
			// TODO Auto-generated method stub
			super.setUp();

			_events = new Vector<String>();
			Logger logger = new Logger()
			{

				@Override
				public void log(int level, String category, String message, Throwable ex)
				{
					_events.add(message);
				}
			};
			Log.setLogger(logger);
		}

		public void testKryo()
		{
			Kryo kryo = new Kryo();
			ByteBuffer buffer = ByteBuffer.allocate(200);
			buffer.clear();
			kryo.writeObject(buffer, new Double(12));
			buffer.flip();
			Double res = kryo.readObject(buffer, Double.class);
			assertEquals("correct transfer", new Double(12), res);

			kryo.register(NetworkScenario.class);
			buffer.clear();
			NetworkScenario ns = new NetworkScenario();
			ns.name = "aaa";
			kryo.writeObject(buffer, ns);
			buffer.flip();
			NetworkScenario ns2 = kryo.readObject(buffer, NetworkScenario.class);
			assertEquals("right name", "aaa", ns2.name);
		}

		public void testZConnect() throws InterruptedException, IOException
		{
			// check events empty
			assertEquals("events empty", 0, _events.size());
			AServer server = new AServer();
			assertEquals("events recorded", 1, _events.size());
			assertEquals("correct start event", "Server opened.",
					_events.elementAt(0));

			// and now the client
			AClient client = new AClient();
			assertEquals("still no more events", 1, _events.size());

			// try with dodgy target
			boolean errorThrown = false;
			try
			{
				client.connect("128.3.3.3");
			}
			catch (IOException e)
			{
				errorThrown = true;
			}
			assertTrue("Exception thrown with dodgy target", errorThrown);
			assertEquals("events recorded", 2, _events.size());
			assertTrue("correct client event",
					_events.elementAt(1).contains("Connecting: /128.3.3.3:54555/54777"));

			// now real connection
			client.connect(null);
			Thread.sleep(100);

			client.stop();
			server.stop();
		}

		public void testStartup() throws InterruptedException, IOException
		{
			// check events empty
			assertEquals("events empty", 0, _events.size());
			AServer server = new AServer();
			assertEquals("events recorded", 1, _events.size());
			assertEquals("correct start event", "Server opened.",
					_events.elementAt(0));

			// and now the client
			AClient client = new AClient();
			assertEquals("still no more events", 1, _events.size());

			// now real connection
			client.connect(null);
			Thread.sleep(100);

			assertEquals("events recorded", 5, _events.size());
			assertTrue("correct client event",
					_events.elementAt(1).contains("Discovered server"));
			assertTrue("correct client event",
					_events.elementAt(2).contains("Connecting"));
			assertTrue("correct client event",
					_events.elementAt(3).contains("connected"));
			assertTrue("correct client event",
					_events.elementAt(4).contains("connected"));

			// ok, give the server some data
			MultiScenarioLister lister = new MultiScenarioLister()
			{

				@Override
				public Vector<NetworkScenario> getScenarios()
				{
					return null;
				}
			};
			server.setDataProvider(lister);

			assertNull("scen list should be empty", _myList);
			AHandler<Vector<LightScenario>> sHandler = new AHandler<Vector<LightScenario>>()
			{
				@Override
				public void onSuccess(Vector<LightScenario> result)
				{
					_myList = result;
				}
			};
			// fire in request for scenarios
			System.err.println("about to request scenarios");
			client.getScenarioList(sHandler);
			Thread.sleep(300);

			Thread.sleep(1000);
			assertNotNull("scen list should have data", _myList);
			assertEquals("scen has correct number", 3, _myList.size());
			LightScenario scen = _myList.elementAt(0);
			assertEquals("scen has correct name", "zaa", scen.name);
			assertEquals("has correct parts", 3, scen.listOfParticipants.size());
			LightParticipant firstPart = scen.listOfParticipants.firstElement();
			assertEquals("has correct first part", "aa2", firstPart.name);
			assertEquals("has correct first id", 2, firstPart.Id);

			// ok, now try to control the participant
			client.controlParticipant(scen.name, firstPart.Id);

			// showEvents(_events);
			// assertEquals("events recorded", 6, _events.size());

			System.out.println("pausing");
			showEvents(_events);
			Thread.sleep(1000);
			client.stop();
			server.stop();
		}

		protected static void showEvents(Vector<String> events)
		{
			System.out.println("=================");
			Iterator<String> iter = events.iterator();
			int ctr = 0;
			while (iter.hasNext())
			{
				String string = (String) iter.next();
				System.out.println(++ctr + " - " + string);
			}
		}
	}

}
