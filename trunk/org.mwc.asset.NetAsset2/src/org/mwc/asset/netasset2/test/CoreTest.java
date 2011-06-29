package org.mwc.asset.netasset2.test;

import java.io.IOException;
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
			assertEquals("events empty",0, _events.size());
			AServer server = new AServer();
			Thread.sleep(1000);
			
			assertEquals("events recorded",1, _events.size());
			assertEquals("correct start event", "Server opened.", _events.elementAt(0));
			server.stop();
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
