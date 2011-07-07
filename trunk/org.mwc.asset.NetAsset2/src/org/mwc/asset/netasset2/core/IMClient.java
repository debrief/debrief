package org.mwc.asset.netasset2.core;

import java.io.IOException;
import java.util.Vector;

import org.mwc.asset.netasset2.common.Network;
import org.mwc.asset.netasset2.common.Network.LightScenario;
import org.mwc.asset.netasset2.common.Network.ScenControl;

import ASSET.Participants.ParticipantDecidedListener;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.ParticipantMovedListener;
import ASSET.Scenario.ScenarioSteppedListener;

public interface IMClient
{

	public abstract void releasePart(String scenario, int partId);

	public abstract void controlPart(String scenario, int id, double courseDegs,
			double speedKts, double depthM);

	public abstract void getScenarioList(final Network.AHandler<Vector<LightScenario>> handler);

	public abstract void stopListenPart(String scenarioName, int participantId);

	public abstract void step(String scenarioName);

	public abstract void stopListenScen(String scenarioName);

	public abstract void listenScen(String scenarioName, ScenarioSteppedListener listener);

	public abstract void listenPart(String scenarioName, int participantId, ParticipantMovedListener moveL,
			ParticipantDecidedListener decider, ParticipantDetectedListener detector);

	public abstract void stop();

	public abstract void connect(String target) throws IOException;

	public abstract java.util.List<java.net.InetAddress> discoverHosts();

	public abstract void controlScen(ScenControl sc);

	void stop(String scenarioName);

}
