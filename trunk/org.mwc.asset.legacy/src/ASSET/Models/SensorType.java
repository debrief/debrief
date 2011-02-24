package ASSET.Models;

import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Sensor.SensorDataProvider;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import MWC.GenericData.WorldDistance;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public interface SensorType extends MWC.GUI.Editable, SensorDataProvider
{

  /**
   * see if we detect any other vessels
   *
   * @param environment        the environment we're living in
   * @param existingDetections the existing set of detections
   * @param ownship            ourselves
   * @param scenario           the scenario we live in
   * @param time               the current DTG
   */
  public void detects(final ASSET.Models.Environment.EnvironmentType environment,
                      final DetectionList existingDetections,
                      final ASSET.ParticipantType ownship,
                      final ASSET.ScenarioType scenario,
                      long time);

  /**
   * the estimated range for a detection of this type (where applicable)
   */
  public WorldDistance getEstimatedRange();


  /**
   * get the name of this sensor
   */
  public String getName();

  /**
   * get the id of this sensor
   */
  public int getId();

  /**
   * restart this sensors
   */
  public void restart();

  /**
   * the type of medium we look at
   *
   * @return the id of the medium
   * @see ASSET.Models.Environment.EnvironmentType
   */
  public int getMedium();

  /**
   * if this sensor has a dynamic behaviour, update it according to the demanded status
   *
   * @param myDemandedStatus
   * @param myStatus
   * @param newTime
   */
  void update(DemandedStatus myDemandedStatus,
              Status myStatus,
              long newTime);

  /**
   * handle the demanded change in sensor lineup
   *
   * @param status
   */
  public void inform(DemandedSensorStatus status);

  /**
   * control the state of this sensor
   *
   * @param switchOn whether to switch it on or off.
   */
  public void setWorking(boolean switchOn);

  /**
   * determine the state of this sensor
   *
   * @return yes/no for if it's working
   */
  public boolean isWorking();

  /**
   * allow somebody to start listening to the components of our calculation
   *
   * @param listener
   */
  public void addSensorCalculationListener(java.beans.PropertyChangeListener listener);

  /**
   * remove a calculation listener
   *
   * @param listener
   */
  public void removeSensorCalculationListener(java.beans.PropertyChangeListener listener);


  ////////////////////////////////////////////////////
  // interface used for active sensors
  ////////////////////////////////////////////////////

  public static interface ActiveSensor
  {
  	/** state message for going active
  	 * 
  	 */
  	public final String ACTIVE_SENSOR_ON = "ActiveSensorOn";
  	
  	/** state message for de-activating sonar
  	 * 
  	 */
  	public final String ACTIVE_SENSOR_OFF = "ActiveSensorOff";
  	
    /**
     * get the source level
     *
     * @return the source level (in relevant units)
     */
    public double getSourceLevel();
  }


}