package org.mwc.asset.comms.restlet.test;

import java.net.URL;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.mwc.asset.comms.restlet.data.AssetEvent;
import org.mwc.asset.comms.restlet.data.Participant;
import org.mwc.asset.comms.restlet.data.ParticipantsResource;
import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.ScenarioListenerResource;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.data.ScenariosResource;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource.ScenarioEvent;
import org.mwc.asset.comms.restlet.host.ASSETGuest;
import org.mwc.asset.comms.restlet.host.ASSETHost;
import org.mwc.asset.comms.restlet.host.GuestServer;
import org.mwc.asset.comms.restlet.host.HostServer;
import org.mwc.asset.comms.restlet.host.MockGuest;
import org.mwc.asset.comms.restlet.host.MockHost;
import org.restlet.Component;
import org.restlet.resource.ClientResource;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class CommsTest extends TestCase
{

	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testStartStop()
	{
		Component comp = null;
		// fire up the server
		HostServer server = new HostServer()
		{

			@Override
			public ASSETHost getHost()
			{
				return null;
			}
		};

		try
		{
			comp = HostServer.go(server);
			assertNotNull("component returned", comp);
		}
		catch (Exception e)
		{
			fail("exception thrown from server go");
			e.printStackTrace();
		}

		assertTrue("comp started", comp.isStarted());

		// and stop it
		try
		{
			HostServer.finish(comp);
		}
		catch (Exception e)
		{
			fail("error thrown in finish method");
			e.printStackTrace();
		}

		assertTrue("comp started", comp.isStopped());

	}

	public class TestHost extends MockHost
	{
		final ScenarioType _myScenario;

		public TestHost(ScenarioType scenario)
		{
			_myScenario = scenario;
			_myScenario.addScenarioSteppedListener(getSteppedListFor(434));
			_myScenario.addParticipantsChangedListener(getSteppedListFor(434));
		}
				

		@Override
		public List<Participant> getParticipantsFor(int scenarioId)
		{
			Vector<Participant> res = new Vector<Participant>();
			Integer[] parts = _myScenario.getListOfParticipants();
			for (int i = 0; i < parts.length; i++)
			{
				ParticipantType thisP = _myScenario.getThisParticipant(parts[i]);
				Participant newP = new Participant(thisP);
				res.add(newP);
			}
			return res;
		}



		@Override
		public void deleteScenarioListener(int scenario, int listenerId)
		{
			super.deleteScenarioListener(scenario, listenerId);
		}

		@Override
		public int newScenarioListener(int scenario, URL url)
		{
			int res = super.newScenarioListener(scenario, url);
			return res;
		}
	}

	private long _time = -1;
	private String _msg = null;
	protected String _name;

	public void testHosting() throws Exception
	{
		final CoreScenario scen = new CoreScenario();
		scen.setScenarioStepTime(5000);
		final TestHost _host = new TestHost(scen);

		Component hostComp = null;
		// fire up the server
		HostServer server = new HostServer()
		{

			@Override
			public ASSETHost getHost()
			{
				return _host;
			}
		};

		try
		{
			hostComp = HostServer.go(server);
			assertNotNull("component returned", hostComp);
		}
		catch (Exception e)
		{
			fail("exception thrown from server go");
			e.printStackTrace();
		}

		assertTrue("comp started", hostComp.isStarted());

		// find some data
		ClientResource cr = new ClientResource("http://localhost:8080/v1/scenario");

		// does it have a scenario?
		ScenariosResource scenR = cr.wrap(ScenariosResource.class);
		List<Scenario> sList = scenR.retrieve();
		assertEquals("right num of scenarios", 4, sList.size());

		// start listening to the first one
		int id = sList.get(0).getId();
		assertEquals("correct id", 434, id);

		assertNotNull("no listeners yet", _host.getSteppedListFor(434));
		assertEquals("no listeners yet", 0, _host.getSteppedListFor(434).size());

		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/listener");
		ScenarioListenerResource sl = cr.wrap(ScenarioListenerResource.class);
		int newId = sl.accept("http://google.com");

		// did it work?
		assertEquals("added listener", 1, _host.getSteppedListFor(434).size());
		assertEquals("new id provided", 1, newId);

		// and ditch the dummy listener
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/listener/" + newId);
		sl = cr.wrap(ScenarioListenerResource.class);
		sl.remove();
		assertEquals("ditched listener", 0, _host.getSteppedListFor(434).size());

		// fire up the client
		Component guestComp = null;
		final ASSETGuest _guest = new MockGuest()
		{

			@Override
			public void newParticipantState(Status newState)
			{
				super.newParticipantState(newState);
			}

			@Override
			public void newScenarioStatus(long time, String eventName,
					String description)
			{
				super.newScenarioStatus(time, eventName, description);
				_time = time;
				_msg = description;
				_name = eventName;
			}
		};
		// fire up the server
		GuestServer guest = new GuestServer()
		{

			@Override
			public ASSETGuest getGuest()
			{
				return _guest;
			}
		};

		try
		{
			guestComp = GuestServer.go(guest);
			assertNotNull("component returned", guestComp);
		}
		catch (Exception e)
		{
			fail("exception thrown from server go");
			e.printStackTrace();
		}

		assertTrue("comp started", guestComp.isStarted());

		// right, now try to register it.
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/listener");
		sl = cr.wrap(ScenarioListenerResource.class);
		newId = sl.accept("http://localhost:8081/v1/scenario/" + 434 + "/event");

		// did it work?
		assertEquals("added listener", 1, _host.getSteppedListFor(434).size());
		assertEquals("new id provided", 2, newId);

		// fire an event
		assertEquals("time shouldn't be set", -1, _time);
		assertNull("msg shouldn't be set", _msg);

		scen.step();

		// fire an event
		assertEquals("time should be set", 0, _time);
		assertNotNull("msg should be set", _msg);

		scen.step();

		// fire an event
		assertEquals("time should be set", 5000, _time);
		assertNotNull("msg should be set", _msg);

		// ////////////////////////////////
		// mess with some participants
		// ////////////////////////////////
		_msg = null;
		Status newStat = new Status(12, 333);
		newStat.setLocation(new WorldLocation(2, 3, 4));
		newStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		Surface surf = new Surface(222, newStat, null, "big surf");
		surf.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
		scen.addParticipant(222, surf);

		assertNotNull("msg should not be blank", _msg);
		assertEquals("time should be same", 0, _time);
		assertTrue("msg should show joined", _name.indexOf(AssetEvent.JOINED)>-1);

		// ahh, but what if we remove it?
		scen.removeParticipant(222);

		assertNotNull("msg should not be blank", _msg);
		assertEquals("time should be same", 0, _time);
		assertTrue("msg should show joined", _name.indexOf(AssetEvent.LEFT)>-1);
		
		// go on, stick it back in...
		scen.addParticipant(222, surf);

		// ////////////////////////////////
		// hmm, what about the participant list?
		// ////////////////////////////////
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/participant");
		ParticipantsResource pr = cr.wrap(ParticipantsResource.class);
		List<Participant> partList = pr.retrieve();
		
		// hmm, how did we get on?
		assertNotNull("got list", partList);
		assertEquals("list right size", 4, partList.size());
		
		
		// ////////////////////////////////
		// ok, stop the guest and see what happens
		// ////////////////////////////////
		GuestServer.finish(guestComp);
		_msg = null;

		scen.step();

		// fire an event
		assertEquals("time should be same", 0, _time);
		assertNull("msg should be blank", _msg);

		// and restart
		guestComp = GuestServer.go(guest);

		// /////////////////////////////////
		// and do some tidying
		// /////////////////////////////////

		assertTrue("guest still running", guestComp.isStarted());
		assertTrue("host still running", hostComp.isStarted());
		GuestServer.finish(guestComp);
		HostServer.finish(hostComp);
		assertTrue("guest not running", guestComp.isStopped());
		assertTrue("host not running", hostComp.isStopped());

	}

	public void testGuest()
	{

		// fire up the client
		Component guestComp = null;
		final ASSETGuest _guest = new MockGuest()
		{

			@Override
			public void newScenarioStatus(long time, String eventName,
					String description)
			{
				super.newScenarioStatus(time, eventName, description);
				_time = time;
				_msg = description;
			}
		};
		// fire up the server
		GuestServer guest = new GuestServer()
		{

			@Override
			public ASSETGuest getGuest()
			{
				return _guest;
			}
		};

		try
		{
			guestComp = GuestServer.go(guest);
			assertNotNull("component returned", guestComp);
		}
		catch (Exception e)
		{
			fail("exception thrown from client go");
			e.printStackTrace();
		}

		assertTrue("comp started", guestComp.isStarted());

		ClientResource cr = new ClientResource("http://localhost:8081/v1/scenario/"
				+ 434 + "/event");
		ScenarioStateResource ssr = cr.wrap(ScenarioStateResource.class);
		ScenarioEvent event = new ScenarioEvent("type", "descr", 12, 1200);
		ssr.accept(event);

	}

}
