package org.mwc.asset.netasset.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mwc.asset.comms.restlet.data.DemandedStatusResource;
import org.mwc.asset.comms.restlet.data.ListenerResource;
import org.mwc.asset.comms.restlet.data.ParticipantsResource;
import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.ScenarioStateResource;
import org.mwc.asset.comms.restlet.data.ScenariosResource;
import org.mwc.asset.comms.restlet.data.DemandedStatusResource.NetDemStatus;
import org.mwc.asset.comms.restlet.data.ParticipantsResource.ParticipantsList;
import org.mwc.asset.comms.restlet.host.ASSETGuest;
import org.mwc.asset.comms.restlet.host.GuestServer;
import org.restlet.resource.ClientResource;

public class RestSupport
{

	private int NULL_INT = -1;
	final private ASSETGuest _myGuest;
	private int _scenarioId = -1;
	private int _scenarioListenerId = NULL_INT;
	private GuestServer guestS;
	private int _partId;
	private int _partListenerId;

	public RestSupport(ASSETGuest guest)
	{
		_myGuest = guest;
	}

	/**
	 * connect to a server
	 * 
	 */
	public boolean doConnect()
	{
		// find some data
		ClientResource cr = new ClientResource("http://localhost:8080/v1/scenario");

		// does it have a scenario?
		ScenariosResource scenR = cr.wrap(ScenariosResource.class);
		List<Scenario> sList = scenR.retrieve();
		boolean res = (sList != null);

		if (res)
			_myGuest.newScenarioEvent(0, "Setup", "scenarios found:" + sList.size());
		else
			_myGuest.newScenarioEvent(0, "Setup", "scenarios not found");

		_scenarioId = sList.get(0).getId();
		_myGuest.newScenarioEvent(0, "Setup", "Listening to scenario:"
				+ _scenarioId);

		// get scenario going
		startMyServer();

		// start listening to time events
		// right, now try to register it.
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + _scenarioId
				+ "/listener");
		String theAddress = "http://localhost:8081/v1/scenario/" + _scenarioId
				+ "/event";
		System.out.println("providing listener for" + theAddress);
		// Representation rep = cr.post(theAddress, MediaType.TEXT_PLAIN);
		ListenerResource lr = cr.wrap(ListenerResource.class);
		_scenarioListenerId = lr.accept(theAddress);
		cr.release();

		return res;
	}

	private void startMyServer()
	{
		// ok, register ourselves as a listener
		if (guestS == null)
		{
			guestS = new GuestServer()
			{
				@Override
				public ASSETGuest getGuest()
				{
					return _myGuest;
				}
			};
			Logger logger = this.guestS.getLogger();
			logger.setLevel(Level.WARNING);

		}

		if (guestS.isStopped())
			try
			{
				GuestServer.go(guestS);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		/**
		 * give the server time to get started
		 * 
		 */
		try
		{
			while (guestS.isStopped())
			{
				System.out.println("give it a few secs....");
				Thread.currentThread().wait(500);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public void doDisconnect()
	{
		if (_scenarioListenerId != NULL_INT)
		{
			ClientResource cr = new ClientResource(
					"http://localhost:8080/v1/scenario/" + _scenarioId + "/listener/"
							+ _scenarioListenerId);
			ListenerResource lr = cr.wrap(ListenerResource.class);
			lr.remove();
			cr.release();
		}
	}

	private void changeState(String theState)
	{
		// find some data
		ClientResource cr = new ClientResource("http://localhost:8080/v1/scenario/"
				+ _scenarioId + "/state");

		// does it have a scenario?
		ScenarioStateResource scenR = cr.wrap(ScenarioStateResource.class);
		scenR.store(theState);
		cr.release();

	}

	public void play(boolean play)
	{
		String theState;
		if (play)
			theState = ScenarioStateResource.START;
		else
			theState = ScenarioStateResource.STOP;

		changeState(theState);
	}

	public void doGoFaster(boolean faster)
	{
		String theState;
		if (faster)
			theState = ScenarioStateResource.FASTER;
		else
			theState = ScenarioStateResource.SLOWER;

		changeState(theState);
	}

	public void doTakeControl()
	{
		// get the participants
		// ////////////////////////////////
		// hmm, what about the participant list?
		// ////////////////////////////////
		ClientResource cr = new ClientResource("http://localhost:8080/v1/scenario/"
				+ _scenarioId + "/participant");
		ParticipantsResource pr = cr.wrap(ParticipantsResource.class);
		ParticipantsList partList = pr.retrieve();

		_myGuest.newScenarioEvent(0, "part", "list is:" + partList);

		// get the id for the first one
		_partId = partList.get(0).getId();

		// create the new state listener
		// right, now try to register it.
		cr = new ClientResource("http://localhost:8080/v1/scenario/" + _scenarioId
				+ "/participant/" + _partId + "/listener");
		ListenerResource sl = cr.wrap(ListenerResource.class);
		_partListenerId = sl.accept("http://localhost:8081/v1/scenario/"
				+ _scenarioId + "/participant/" + _partId + "/status");
		cr.release();
	}

	public void doReleaseControl()
	{
		// create the new state listener
		// right, now try to register it.
		ClientResource cr = new ClientResource("http://localhost:8080/v1/scenario/"
				+ _scenarioId + "/participant/" + _partId + "/listener/"
				+ _partListenerId);
		ListenerResource sl = cr.wrap(ListenerResource.class);
		sl.remove();
		cr.release();
	}

	public void doDemStatus(double courseDegs, double speedKts, double depthM)
	{
		ClientResource cr = new ClientResource("http://localhost:8080/v1/scenario/"
				+ _scenarioId + "/participant/" + _partId + "/demState");
		DemandedStatusResource sl = cr.wrap(DemandedStatusResource.class);
		NetDemStatus newStat = new NetDemStatus(courseDegs, speedKts, depthM);
		try
		{
			sl.store(newStat);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		cr.release();
	}

}
