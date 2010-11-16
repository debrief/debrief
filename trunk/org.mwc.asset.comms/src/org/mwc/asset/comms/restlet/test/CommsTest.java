package org.mwc.asset.comms.restlet.test;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.mwc.asset.comms.restlet.data.AssetEvent;
import org.mwc.asset.comms.restlet.data.ListenerResource;
import org.mwc.asset.comms.restlet.data.Participant;
import org.mwc.asset.comms.restlet.data.ParticipantsResource;
import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.data.ScenariosResource;
import org.mwc.asset.comms.restlet.data.Sensor;
import org.mwc.asset.comms.restlet.data.SensorsResource;
import org.mwc.asset.comms.restlet.data.DecisionResource.DecidedEvent;
import org.mwc.asset.comms.restlet.data.DetectionResource.DetectionEvent;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource.ScenarioEvent;
import org.mwc.asset.comms.restlet.host.ASSETGuest;
import org.mwc.asset.comms.restlet.host.ASSETHost;
import org.mwc.asset.comms.restlet.host.BaseHost;
import org.mwc.asset.comms.restlet.host.GuestServer;
import org.mwc.asset.comms.restlet.host.HostServer;
import org.mwc.asset.comms.restlet.host.MockGuest;
import org.restlet.Component;
import org.restlet.resource.ClientResource;

import ASSET.ScenarioType;
import ASSET.Models.DecisionType;
import ASSET.Models.SensorType;
import ASSET.Models.Decision.UserControl;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.MovementCharacteristics;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.Cookie.PlainCookieSensor;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioActivityMonitor;
import MWC.GenericData.WorldDistance;
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

	public class TestHost extends BaseHost
	{
		final ScenarioType _myScenario;

		@Override
		public ScenarioType getScenario(int scenarioId)
		{
			return _myScenario;
		}

		public TestHost(ScenarioType scenario)
		{
			_myScenario = scenario;
			_myScenario.addScenarioSteppedListener(getSteppedListFor(434));
			_myScenario.addParticipantsChangedListener(getSteppedListFor(434));
		}

		@Override
		public DemandedStatus getDemandedStatus(int scenario, int participant)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Vector<Scenario> getScenarios()
		{
			Vector<Scenario> res = new Vector<Scenario>(0, 1);
			res.add(new Scenario(_myScenario.getName(), 434));
			return res;
		}

		@Override
		public void setDemandedStatus(int scenario, int participant,
				DemandedStatus demState)
		{
			// TODO Auto-generated method stub

		}


	}

	private long _time = -1;
	private String _msg = null;
	protected String _name;
	protected Status _pState;
	protected DecidedEvent _dEvent;
	protected DetectionList _dList;
	protected DetectionEvent _detEvent;

	public void testGuest() throws Exception
	{

		// fire up the client
		Component guestComp = null;
		final ASSETGuest _guest = new MockGuest()
		{

			@Override
			public void newParticipantState(int scenarioId, int participantId,
					Status newState)
			{
				super.newParticipantState(scenarioId, participantId, newState);
				_pState = newState;
			}

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

		// kill the guest, so we can do other tests
		GuestServer.finish(guestComp);

	}

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
		assertEquals("right num of scenarios", 1, sList.size());

		// start listening to the first one
		int id = sList.get(0).getId();
		assertEquals("correct id", 434, id);

		assertNotNull("no listeners yet", _host.getSteppedListFor(434));
		assertEquals("no listeners yet", 0, _host.getSteppedListFor(434).size());

		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/listener");
		ListenerResource sl = cr.wrap(ListenerResource.class);
		int newId = sl.accept("http://google.com");

		// did it work?
		assertEquals("added listener", 1, _host.getSteppedListFor(434).size());
		assertEquals("new id provided", 1, newId);

		// and ditch the dummy listener
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/listener/" + newId);
		sl = cr.wrap(ListenerResource.class);
		sl.remove();
		assertEquals("ditched listener", 0, _host.getSteppedListFor(434).size());

		// fire up the client
		Component guestComp = null;
		final ASSETGuest _guest = new ASSETGuest()
		{

			@Override
			public void newParticipantState(int scenarioId, int participantId,
					Status newState)
			{
				_pState = newState;
			}

			@Override
			public void newScenarioStatus(long time, String eventName,
					String description)
			{
				_time = time;
				_msg = description;
				_name = eventName;
			}

			@Override
			public void newParticipantDecision(int scenarioId, int participantId,
					DecidedEvent event)
			{
				_dEvent = event;
			}

			@Override
			public void newParticipantDetection(int scenarioId, int participantId,
					 DetectionEvent event)
			{
				_detEvent = event;
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
			e.printStackTrace();
			fail("exception thrown from server go");
		}

		assertTrue("comp started", guestComp.isStarted());

		// right, now try to register it.
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/listener");
		sl = cr.wrap(ListenerResource.class);
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
		Status newStat1 = new Status(12, 333);
		newStat1.setLocation(new WorldLocation(2, 3, 4));
		newStat1.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		Category newCat2 = new Category(Category.Force.RED,
				Category.Environment.SURFACE, Category.Type.FRIGATE);
		Surface surf2 = new Surface(222, newStat1, null, "big surf");
		surf2.setCategory(newCat2);
		surf2.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
		scen.addParticipant(222, surf2);

		assertNotNull("msg should not be blank", _msg);
		assertEquals("time should be same", 0, _time);
		assertTrue("msg should show joined", _name.indexOf(AssetEvent.JOINED) > -1);

		// ahh, but what if we remove it?
		scen.removeParticipant(222);

		assertNotNull("msg should not be blank", _msg);
		assertEquals("time should be same", 0, _time);
		assertTrue("msg should show joined", _name.indexOf(AssetEvent.LEFT) > -1);

		// go on, stick it back in...
		scen.addParticipant(222, surf2);

		// ////////////////////////////////
		// hmm, what about the participant list?
		// ////////////////////////////////
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/participant");
		ParticipantsResource pr = cr.wrap(ParticipantsResource.class);
		List<Participant> partList = pr.retrieve();

		// hmm, how did we get on?
		assertNotNull("got list", partList);
		assertEquals("list right size", 1, partList.size());
		Participant partOne = partList.get(0);
		assertEquals("right name", "big surf", partOne.getName());
		assertEquals("right id", 222, partOne.getId(), 0);
		assertEquals("right category", newCat2, partOne.getCategory());

		// ////////////////////////////////
		// see what happens about movement
		// ////////////////////////////////
		// right, now try to register it.
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/participant/222/listener");
		sl = cr.wrap(ListenerResource.class);
		newId = sl.accept("http://localhost:8081/v1/scenario/" + 434
				+ "/participant/" + 222 + "/status");

		// is the listener registered?

		// check we're starting with clean slate
		assertNull("empty status", _pState);

		// do a step
		scen.step();
		assertNotNull("status there", _pState);

		assertTrue("status has changed", !_pState.equals(newStat1));

		// check we can remove ourselves
		_pState = null;
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/participant/222/listener/" + newId);
		sl = cr.wrap(ListenerResource.class);
		sl.remove();

		// do a step, hopefully we won't hear about it
		scen.step();

		assertNull("not heard anything", _pState);

		// ////////////////////////////////
		// see what happens about decisions
		// ////////////////////////////////
		DecisionType decide = new ASSET.Models.Decision.UserControl(45,
				new WorldSpeed(12, WorldSpeed.Kts), new WorldDistance(0,
						WorldDistance.METRES)){
							private static final long serialVersionUID = 1L;
							public DemandedStatus decide(Status status,
									MovementCharacteristics chars, DemandedStatus demStatus,
									DetectionList detections, ScenarioActivityMonitor monitor,
									long time)
							{
								this.setLastActivity("TESTS");
								return new SimpleDemandedStatus(time, status);
							}							
		};
		surf2.setDecisionModel(decide);
		
		// ok, register as a decision listener
		// right, now try to register it.
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/participant/222/decisionListener");
		sl = cr.wrap(ListenerResource.class);
		newId = sl.accept("http://localhost:8081/v1/scenario/" + 434
				+ "/participant/" + 222 + "/decision");
		
		
		// right then, what happens
		assertNull("empty decision", _dEvent);
		
		scen.step();
		
		assertNotNull("decision there", _dEvent);
		assertTrue("right decision model", _dEvent._activity.indexOf(UserControl.NAME) > -1);
		assertTrue("right decision", _dEvent._activity.indexOf("TESTS") > -1);
		
		// ok, get ready to stop listening
		_dEvent = null;
		
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/participant/222/decisionListener/" + newId);
		sl = cr.wrap(ListenerResource.class);
		sl.remove();

		assertNull("decision still empty there", _dEvent);

		// ////////////////////////////////
		// go on, stick in another participant (look at detections)
		// ////////////////////////////////
		Status newStat = new Status(12, 333);
		newStat.setLocation(new WorldLocation(2, 3, 4));
		newStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
		Category newCat = new Category(Category.Force.BLUE,
				Category.Environment.SURFACE, Category.Type.FRIGATE);
		Surface surf = new Surface(111, newStat, null, "big surf2");
		surf.setCategory(newCat);
		surf.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
		scen.addParticipant(111, surf);
		SensorType mySensor = new PlainCookieSensor(54, new WorldDistance(12000,
				WorldDistance.DEGS));
		surf.addSensor(mySensor);
		
		
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/participant/" + 111 + "/sensor");
		SensorsResource sr = cr.wrap(SensorsResource.class);
		List<Sensor> sensorList = sr.retrieve();

		assertEquals("found sensors", 1, sensorList.size());
		assertEquals("correct sensor",54,(int) sensorList.get(0).getId());
		
		// right, now add ourselves as a detection listener
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + id
				+ "/participant/111/detectionListener");
		sl = cr.wrap(ListenerResource.class);
		newId = sl.accept("http://localhost:8081/v1/scenario/" + 434
				+ "/participant/" + 222 + "/detection");
		
		// ok, have a step
		assertNull("empty detection list", _detEvent);
		
		scen.step();
		
		assertNotNull("have a detection", _detEvent);
		assertEquals("have correct num of detections",1,  _detEvent._list.size());
		

		// ////////////////////////////////
		// ok, stop the guest and see what happens
		// ////////////////////////////////
		GuestServer.finish(guestComp);
		_msg = null;

		scen.step();

		// fire an event
		assertEquals("time should be same", 25000, _time);
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

}
