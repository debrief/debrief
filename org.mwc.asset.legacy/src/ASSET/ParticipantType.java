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

package ASSET;

import ASSET.Models.Sensor.SensorDataProvider;
import ASSET.Models.Sensor.SensorList;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public interface ParticipantType extends ParticipantDetectedListener, SensorDataProvider, NetworkParticipant {

	/**
	 * listeners which hear when a participant either is using a different behaviour
	 * to the previous step, or if the behaviour stays the same but is returning a
	 * different activity message.
	 *
	 * @param listener the listener to add/remove
	 */
	public void addParticipantDecidedListener(ASSET.Participants.ParticipantDecidedListener listener);

	public void addParticipantMovedListener(ASSET.Participants.ParticipantMovedListener listener);

	/**
	 * add a sensor to this participant
	 */
	public void addSensor(ASSET.Models.SensorType sensor);

	/**
	 * perform the decision portion of the step
	 */
	public void doDecision(long oldTime, long newTime, ASSET.ScenarioType scenario);

	/**
	 * perform the detection portion of the step
	 */
	public void doDetection(long oldtime, long newTime, ASSET.ScenarioType scenario);

	/**
	 * perform the movement portion of the step
	 */
	public void doMovement(long oldtime, long newTime, ASSET.ScenarioType scenario);

	/**
	 * whether this participant is alive yet
	 *
	 * @return
	 */
	public abstract boolean getAlive();

	/**
	 * get the decision model for this participant
	 */
	public ASSET.Models.DecisionType getDecisionModel();

	/**
	 * the demanded status of this participant
	 */
	public DemandedStatus getDemandedStatus();

	/**
	 * the movement characteristics for this participant
	 */
	public ASSET.Models.Movement.MovementCharacteristics getMovementChars();

	/**
	 * the movement characteristics for this participant
	 */
	public ASSET.Models.MovementType getMovementModel();

	/**
	 * find the list of current detections
	 */
	public ASSET.Models.Detection.DetectionList getNewDetections();

	/**
	 * find out how many sensors there are
	 */
	public int getNumSensors();

	/**
	 * whether to paint decisions if this participant is shown graphically
	 *
	 * @param paintDecisions
	 */
	public boolean getPaintDecisions();

	/**
	 * the energy radiation characteristics for this participant
	 */
	public ASSET.Models.Vessels.Radiated.RadiatedCharacteristics getRadiatedChars();

	/**
	 * get the radiated noise of this participant in this bearing in this medium
	 */
	double getRadiatedNoiseFor(int medium, double brg_degs);

	/**
	 * the self noise characteristics characteristics for this participant
	 */
	public ASSET.Models.Vessels.Radiated.RadiatedCharacteristics getSelfNoise();

	/**
	 * get the radiated noise of this participant in this bearing in this medium
	 */
	double getSelfNoiseFor(int medium, double brg_degs);

	/**
	 * get a specific sensor
	 */
	public ASSET.Models.SensorType getSensorAt(int index);

	/**
	 * get the sensors for this participant
	 *
	 * @return
	 */
	public SensorList getSensorFit();

	/**
	 * whether this participant is alive yet
	 *
	 * @return
	 */
	public abstract boolean isAlive();

	/**
	 * find out if this participant radiates this type of noise
	 *
	 * @param medium the medium we're looking for
	 * @return yes/no
	 */
	boolean radiatesThisNoise(int medium);

	/**
	 * find the range of this participant from the specified location (which allows
	 * us to have a participant that has an area, not just a point
	 */
	public WorldDistance rangeFrom(WorldLocation point);

	/**
	 * listeners which hear when a participant either is using a different behaviour
	 * to the previous step, or if the behaviour stays the same but is returning a
	 * different activity message.
	 *
	 * @param listener the listener to add/remove
	 */
	public void removeParticipantDecidedListener(ASSET.Participants.ParticipantDecidedListener listener);

	public void removeParticipantMovedListener(ASSET.Participants.ParticipantMovedListener listener);

	/**
	 * reset, to go back to the initial state
	 */
	@Override
	public void restart(ScenarioType scenario);

	/**
	 * whether this participant is alive yet
	 *
	 * @return
	 */
	public abstract void setAlive(boolean val);

	/**
	 * set the category
	 */
	public void setCategory(Category val);

	/**
	 * set the decision model for this participant
	 */
	public void setDecisionModel(ASSET.Models.DecisionType decision);

	/**
	 * set the demanded status for this participant
	 */
	public void setDemandedStatus(DemandedStatus _myDemandedStatus);

	/**
	 * set the initial status
	 */
	public void setInitialStatus(Status val);

	/**
	 * set the movement characteristics
	 */
	public void setMovementChars(ASSET.Models.Movement.MovementCharacteristics moveChars);

	/**
	 * set the movement model for this participant
	 */
	public void setMovementModel(ASSET.Models.MovementType movement);

	/**
	 * the name of this participant
	 */
	public void setName(String val);

	/**
	 * whether to paint decisions if this participant is shown graphically
	 *
	 * @param paintDecisions
	 */
	public void setPaintDecisions(boolean paintDecisions);

	/**
	 * set the radiated noise characteristics
	 */
	public void setRadiatedChars(ASSET.Models.Vessels.Radiated.RadiatedCharacteristics radChars);

	/**
	 * set the self noise characteristics
	 */
	public void setSelfNoise(ASSET.Models.Vessels.Radiated.RadiatedCharacteristics radChars);

	/**
	 * set the sensor fit
	 */
	public void setSensorFit(ASSET.Models.Sensor.SensorList _mySensorList);

	/**
	 * set the status
	 */
	public void setStatus(Status _myStatus);

}
