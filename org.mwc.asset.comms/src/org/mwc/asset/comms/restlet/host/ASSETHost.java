/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.asset.comms.restlet.host;

import java.net.URI;
import java.util.List;
import java.util.Vector;

import org.mwc.asset.comms.restlet.data.Participant;
import org.mwc.asset.comms.restlet.data.Scenario;
import org.mwc.asset.comms.restlet.data.Sensor;

import ASSET.ScenarioType;
import ASSET.Participants.DemandedStatus;

/**
 * methods exposed by object capable of acting as ASSET Host in networked
 * simulation
 *
 * @author ianmayo
 *
 */
public interface ASSETHost {
	/**
	 * how to get at the host object
	 * 
	 * @author ianmayo
	 *
	 */
	interface HostProvider {
		/**
		 * get the host object
		 * 
		 * @return
		 */
		public ASSETHost getHost();
	}

	/**
	 * someone wants to stop listening to new decisions
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @param theId
	 */
	public void deleteParticipantDecisionListener(int scenarioId, int participantId, int theId);

	/**
	 * ditch this detection listener
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @param sensorId
	 */
	public void deleteParticipantDetectionListener(int scenarioId, int participantId, int sensorId);

	/**
	 * somebody wants to stop listening to us
	 * 
	 * @param scenarioId TODO
	 * @param listenerId
	 */
	public void deleteParticipantListener(int scenarioId, int participantId, int listenerId);

	/**
	 * somebody wants to stop listening to us
	 * 
	 * @param scenario   subject scenario
	 * @param listenerId
	 */
	public void deleteScenarioListener(int scenario, int listenerId);

	/**
	 * find out the current status of this participant
	 * 
	 * @param parseInt
	 * @param parseInt2
	 * @return
	 */
	public DemandedStatus getDemandedStatus(int scenario, int participant);

	public List<Participant> getParticipantsFor(int scenarioId);

	/**
	 * get hold of the specified scenario
	 * 
	 * @param scenarioId
	 * @return
	 */
	public ScenarioType getScenario(int scenarioId);

	/**
	 * get a list of scenarios we know about
	 * 
	 * @return
	 */
	public Vector<Scenario> getScenarios();

	/**
	 * retrive the sensors for this participant
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @return the list
	 */
	public List<Sensor> getSensorsFor(int scenarioId, int participantId);

	/**
	 * someone wants to listen to new decisions
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @param listener
	 * @return
	 */
	public int newParticipantDecisionListener(int scenarioId, int participantId, URI listener);

	/**
	 * a new detection listener for this sensor
	 * 
	 * @param scenarioId
	 * @param participantId
	 * @param listener
	 * @return
	 */
	public int newParticipantDetectionListener(int scenarioId, int participantId, URI listener);

	/**
	 * somebody new wants to listen to us
	 * 
	 * @param scenario
	 * @param url
	 * @return
	 */
	public int newParticipantListener(int scenarioId, int participantId, URI url);

	/**
	 * somebody new wants to listen to us
	 * 
	 * @param scenario
	 * @param url
	 * @return
	 */
	public int newScenarioListener(int scenario, URI url);

	/**
	 * record a new demanded status for the supplied participant
	 * 
	 * @param scenario
	 * @param participant
	 * @param demState
	 */
	public void setDemandedStatus(int scenario, int participant, DemandedStatus demState);

	/**
	 * somebody wants to change how this scenario is running
	 * 
	 * @param scenarioId
	 * @param newState
	 */
	public void setScenarioStatus(int scenarioId, String newState);
}
