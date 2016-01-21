package org.mwc.debrief.dis.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mwc.debrief.dis.core.DISModule;
import org.mwc.debrief.dis.core.IDISModule;
import org.mwc.debrief.dis.core.IDISPreferences;
import org.mwc.debrief.dis.diagnostics.TestFixListener;
import org.mwc.debrief.dis.diagnostics.TestFixListener.Item;
import org.mwc.debrief.dis.diagnostics.TestPrefs;
import org.mwc.debrief.dis.listeners.IDISGeneralPDUListener;
import org.mwc.debrief.dis.listeners.IDISScenarioListener;
import org.mwc.debrief.dis.providers.IPDUProvider;
import org.mwc.debrief.dis.providers.dummy.DummyDataProvider;

import edu.nps.moves.dis.Pdu;

public class DISListenerTest
{

	@Test
	public void testConfig()
	{
		IDISModule subject = new DISModule();
		IDISPreferences prefs = new TestPrefs(true, "file.txt");
		subject.setPrefs(prefs);
		assertNotNull(subject.getPrefs());
		assertNotNull(subject);
	}

	@Test
	public void testScenarioStateMonitoring()
	{
		IDISModule subject = new DISModule();
		IPDUProvider provider = new DummyDataProvider(3, 5, 3000, 5000);

		TestFixListener fixL = new TestFixListener();

		subject.addFixListener(fixL);
		subject.setProvider(provider);
		final List<String> events = new ArrayList<String>();
		subject.addScenarioListener(new IDISScenarioListener()
		{
			@Override
			public void restart()
			{
				events.add("restarted");
			}

			@Override
			public void complete(String reason)
			{
				events.add("complete");
			}
		});

		provider.start();

		assertTrue("received restart", events.contains("restarted"));
		assertTrue("received complete", events.contains("complete"));

	}

	@Test
	public void testActivityMonitoring()
	{
		IDISModule subject = new DISModule();
		IPDUProvider provider = new DummyDataProvider(3, 5, 3000, 5000);

		TestFixListener fixL = new TestFixListener();

		subject.addFixListener(fixL);

		final List<String> events = new ArrayList<String>();

		subject.addGeneralPDUListener(new IDISGeneralPDUListener()
		{

			@Override
			public void logPDU(Pdu pdu)
			{
				events.add("");
			}

			@Override
			public void complete(String reason)
			{
				// TODO Auto-generated method stub
				
			}
		});

		subject.setProvider(provider);
		provider.start();

		assertEquals("got all PDUs", 15, events.size());
	}

	@Test
	public void testESHandling()
	{
		IDISModule subject = new DISModule();
		IDISPreferences prefs = new TestPrefs(true, "file.txt");
		IPDUProvider provider = new DummyDataProvider(3, 10, 3000, 5000);
		TestFixListener fixL = new TestFixListener();

		subject.setPrefs(prefs);
		subject.addFixListener(fixL);
		subject.setProvider(provider);

		provider.start();

		assertEquals("correct num tracks", 3, fixL.getData().keySet().size());
		assertEquals("correct num fixes", 10, fixL.getData().values().iterator()
				.next().size());

		// get a fix & have a look at it
		Item thisF = fixL.getData().values().iterator().next().iterator().next();
		assertEquals("has time", 3000, thisF._time);
		assertTrue("has lat", thisF._lat != 0);
		System.out.println("lat is: " + thisF._lat);
		assertTrue("has long", thisF._long != 0);
		assertTrue("has depth", thisF._depth != 0);
		assertTrue("has course", thisF._course != 0);
		assertTrue("has speed", thisF._speed != 0);
	}

}
