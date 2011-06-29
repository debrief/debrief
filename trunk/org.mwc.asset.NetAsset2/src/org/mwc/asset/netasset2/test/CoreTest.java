package org.mwc.asset.netasset2.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.mwc.asset.netasset2.core.AClient;
import org.mwc.asset.netasset2.core.AServer;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

public class CoreTest
{

	public static class TrackWrapper_Test extends junit.framework.TestCase
	{

		private Vector<String> _events;

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
			showEvents(_events);

			assertEquals("events recorded", 5, _events.size());
			assertTrue("correct client event",
					_events.elementAt(2).contains("Discovered server"));
			assertTrue("correct client event",
					_events.elementAt(3).contains("Connecting"));
			assertTrue("correct client event",
					_events.elementAt(4).contains("connected"));
			
			// ok, try to send something

	//		showEvents(_events);

			Thread.sleep(1000);
			client.stop();
			server.stop();
		}

		protected static void showEvents(Vector<String> events)
		{
			synchronized (events)
			{
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

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		AServer server = new AServer();

		AClient client = new AClient();

		client.testSend();

		System.out.println("pausing");
		System.in.read();

		server.stop();
		client.stop();
	}

}
