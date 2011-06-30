package org.mwc.asset.netasset2.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.mwc.asset.netasset2.common.Network.AHandler;
import org.mwc.asset.netasset2.common.Network.LightParticipant;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.core.AClient;
import org.mwc.asset.netasset2.core.AServer;

import ASSET.ScenarioType;
import ASSET.Models.DecisionType;
import ASSET.Models.Decision.Movement.Wander;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Vessels.Surface;
import ASSET.Participants.Category;
import ASSET.Participants.CoreParticipant;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDecidedListener;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.MultiScenarioLister;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;

public class CoreTest
{

	public static class TrackWrapper_Test extends junit.framework.TestCase
	{

		private Vector<String> _events;
		protected Vector<LightScenario> _myList;
		private Vector<ScenarioType> _myScenarios;

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
				public Vector<ScenarioType> getScenarios()
				{
					return getScenarioList();
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

			showEvents(_events);

			assertNotNull("scen list should have data", _myList);
			assertEquals("scen has correct number", 3, _myList.size());
			LightScenario scen = _myList.elementAt(0);
			assertEquals("scen has correct name", "aac", scen.name);
			assertEquals("has correct parts", 3, scen.listOfParticipants.size());
			LightParticipant firstPart = scen.listOfParticipants.firstElement();
			assertEquals("has correct first part", "p12", firstPart.name);
			assertEquals("has correct first id", 12, firstPart.id);

			// check we have no lsitenser....
			assertEquals("no listeners", 0, server.getPartListeners().size());

			// ok, now try to control the participant
			Vector<String> moveLog = new Vector<String>();
			CombinedListener combi = new CombinedListener(moveLog);
			client.controlParticipant(scen.name, firstPart.id, combi, combi, combi);
			Thread.sleep(600);

			assertEquals("a listener", 1, server.getPartListeners().size());

			// ok, now try to release
			client.releaseParticipant(scen.name, firstPart.id);
			Thread.sleep(600);

			// System.in.read();

			assertEquals("no listener", 0, server.getPartListeners().size());

			// connect again
			// ok, now try to control the participant
			client.controlParticipant(scen.name, firstPart.id, combi, combi, combi);
			Thread.sleep(600);

			assertEquals("a listener", 1, server.getPartListeners().size());

			// move scenario & see if movement occurs...
			server.step(scen.name);
			Thread.sleep(200);
			server.step(scen.name);
			Thread.sleep(200);
			
			// check we've seen some mvoement
			assertEquals("movement detected", 2, moveLog.size());
			assertTrue("has movement",moveLog.firstElement().startsWith("move") );

			// showEvents(_events);
			// assertEquals("events recorded", 6, _events.size());

			showEvents(_events);
			System.out.println("pausing");
			Thread.sleep(1000);
			System.in.read();
			client.stop();
			server.stop();
		}

		protected static class MyScen extends CoreScenario
		{
			public MyScen()
			{
				this.setScenarioStepTime(new Duration(5, Duration.MINUTES));
			}
		}

		protected static class MyPart extends Surface
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public MyPart(int id)
			{
				super(id);
				setName("p" + id);
				this.setCategory(new Category(Category.Force.BLUE,
						Category.Environment.SURFACE, Category.Type.FRIGATE));
				WorldLocation centre = new WorldLocation(12, 12, 2);
				WorldDistance area = new WorldDistance(12, WorldDistance.NM);
				DecisionType wander = new Wander(centre, area);
				this.setDecisionModel(wander);
				Status newStat = new Status(12, 0);
				newStat.setLocation(new WorldLocation(11, 11, 11));
				newStat.setCourse(12);
				newStat.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
				setMovementChars(SurfaceMovementCharacteristics.getSampleChars());
				this.setStatus(newStat);
			}
		}

		private static class CombinedListener implements ParticipantMovedListener, ParticipantDetectedListener, ParticipantDecidedListener
		{
			private Vector<String> _items;

			public CombinedListener(Vector<String> items)
			{
				_items = items;
			}

			@Override
			public void newDecision(String description, DemandedStatus dem_status)
			{
				_items.add("dec:" + description);
			}

			@Override
			public void newDetections(DetectionList detections)
			{
				_items.add("det:" + detections.size());
			}

			@Override
			public void moved(Status newStatus)
			{
				_items.add("move:" + newStatus.getTime());
			}

			@Override
			public void restart(ScenarioType scenario)
			{
			}
			
		}
		
		protected Vector<ScenarioType> getScenarioList()
		{
			if (_myScenarios == null)
			{
				_myScenarios = new Vector<ScenarioType>();

				CoreScenario scen = new MyScen();
				scen.setName("aaa");
				CoreParticipant cp = new MyPart(12);
				CoreParticipant cp2 = new MyPart(13);
				CoreParticipant cp3 = new MyPart(14);
				CoreParticipant cp4 = new MyPart(22);
				CoreParticipant cp5 = new MyPart(23);
				CoreParticipant cp6 = new MyPart(24);
				CoreParticipant cp7 = new MyPart(32);
				CoreParticipant cp8 = new MyPart(33);
				CoreParticipant cp9 = new MyPart(34);
				scen.addParticipant(cp.getId(), cp);
				scen.addParticipant(cp2.getId(), cp2);
				scen.addParticipant(cp3.getId(), cp3);
				CoreScenario scen2 = new MyScen();
				scen2.addParticipant(cp4.getId(), cp4);
				scen2.addParticipant(cp5.getId(), cp5);
				scen2.addParticipant(cp6.getId(), cp6);
				scen.setName("aab");
				CoreScenario scen3 = new MyScen();
				scen3.addParticipant(cp7.getId(), cp7);
				scen3.addParticipant(cp8.getId(), cp8);
				scen3.addParticipant(cp9.getId(), cp9);
				scen.setName("aac");

				_myScenarios.add(scen);
				_myScenarios.add(scen2);
				_myScenarios.add(scen3);
			}

			return _myScenarios;
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
