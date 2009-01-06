package ASSET.Models.Sensor;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.beans.PropertyChangeListener;
import java.util.*;

import ASSET.Models.SensorType;
import ASSET.Models.Detection.*;
import ASSET.Participants.*;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;

public class SensorList implements ASSET.Models.SensorType, java.io.Serializable
{

  ////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * our editor
   */
  protected MWC.GUI.Editable.EditorType _myEditor = null;

  /**
   * our sensors
   */
  private HashMap<Integer, SensorType> _mySensors = new HashMap<Integer, SensorType>();

  /**
   * whether we are currently active or not
   */
  private boolean _isWorking = true;

  /**
   * our name
   */
  private String _myName;

  /**
   * our historic list of detections
   */
  private ASSET.Models.Detection.DetectionList _myDetections = new DetectionList();


  /**
   * people listening out for my detections
   */
  private Vector<ParticipantDetectedListener> _participantDetectedListeners;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public SensorList()
  {
    _myName = "Sensor List";
  }

  ////////////////////////////////////////////////////////////
  // member methods
  ////////////////////////////////////////////////////////////


  /**********************************************************************
   * sensor data provider methods
   *********************************************************************/
  /**
   * somebody wants to stop listening to us
   *
   * @param listener
   */
  public void addParticipantDetectedListener(ParticipantDetectedListener listener)
  {
    if (_participantDetectedListeners == null)
      _participantDetectedListeners = new Vector<ParticipantDetectedListener>(1, 2);

    _participantDetectedListeners.add(listener);
  }

  /**
   * find the list of all detections for this participant since the start of the scenario
   */
  public DetectionList getAllDetections()
  {
    return _myDetections;
  }

  /**
   * somebody wants to start listening to us
   *
   * @param listener
   */
  public void removeParticipantDetectedListener(ParticipantDetectedListener listener)
  {
    _participantDetectedListeners.remove(listener);
  }


  /**
   * get the id of this sensor list
   *
   * @return
   */
  public int getId()
  {
    return -1;
  }

  /**
   * get the medium for this list. Hey, it doesn't make sense so throw a
   * wobbly if we're asked to do it
   *
   * @return
   */
  public int getMedium()
  {
    if (true)
      throw new RuntimeException("Shouldn't be asking SensorFit for medium");

    return 1;
  }


  /**
   * the estimated range for a detection of this type (where applicable)
   */
  public WorldDistance getEstimatedRange()
  {
    return null;
  }

  /**
   * switch one of our sensors on or off
   *
   * @param medium   the medium to use
   * @param switchOn whether to switch on or off
   * @see ASSET.Models.Environment.EnvironmentType
   */
  public void changeSensorLineup(int medium, boolean switchOn)
  {
    // find any sensors of this medium
    // step through our sensors
    final Iterator<SensorType> it = _mySensors.values().iterator();

    while (it.hasNext())
    {
      final SensorType st = (SensorType) it.next();

      if (st.getMedium() == medium)
      {
        // ok, found one. do it!
        st.setWorking(switchOn);
      }
    }
  }

  /**
   * get the list of sensors we contain
   *
   * @return
   */
  public Collection<SensorType> getSensors()
  {
    return _mySensors.values();
  }

  /**
   * reset all of the sensors
   */
  public void restart()
  {
    // step through the sensors
    final Iterator<SensorType> it = _mySensors.values().iterator();

    while (it.hasNext())
    {
      final SensorType st = (SensorType) it.next();

      // ok, now restart it
      st.restart();
    }

    // and clear our list
    _myDetections.removeAllElements();

  }

  /**
   * if this sensor has a dynamic behaviour, update it according to the demanded status
   *
   * @param myDemandedStatus
   * @param myStatus
   * @param newTime
   */
  public void update(DemandedStatus myDemandedStatus,
                     Status myStatus,
                     long newTime)
  {
    // don't bother.  let classes over-ride as necessary
  }

  /**
   * allow somebody to start listening to the components of our calculation
   *
   * @param listener
   */
  public void addSensorCalculationListener(PropertyChangeListener listener)
  {
    // ignore - let the sensors do it themselves
  }

  /**
   * remove a calculation listener
   *
   * @param listener
   */
  public void removeSensorCalculationListener(PropertyChangeListener listener)
  {
    // ignore - let the sensors do it themselves  }
  }

  /**
   * control the state of this sensor
   *
   * @param switchOn whether to switch it on or off.
   */
  public void setWorking(boolean switchOn)
  {
    _isWorking = switchOn;
  }

  /**
   * determine the state of this sensor
   *
   * @return yes/no for if it's working
   */
  public boolean isWorking()
  {
    return _isWorking;
  }

  /**
   * handle the demanded change in sensor lineup
   *
   * @param status
   */
  public void inform(DemandedSensorStatus status)
  {
    _isWorking = status.getSwitchOn();
  }

  public String getName()
  {
    return _myName;
  }

  public void setName(String val)
  {
  	_myName = val;
  }
  
  public String toString()
  {
    return getName();
  }


  //////////////////////////////////////////////////
  // layer support (for drill-down)
  //////////////////////////////////////////////////

  //////////////////////////////////////////////////
  // utility methods
  //////////////////////////////////////////////////

  /**
   * add this new sensor
   *
   * @param sensor
   */
  public void add(final SensorType sensor)
  {
    // does the sensor have an id?
    if (sensor.getId() <= 0)
    {
      throw new RuntimeException("missing sensor id for sensor:" + sensor + ". Unable to store");
    }

    // check if we already hold this sensor
    Integer thisId = new Integer(sensor.getId());

    if (_mySensors.get(thisId) != null)
    {
      throw new RuntimeException("none-unique sensor id created");
    }

    _mySensors.put(new Integer(sensor.getId()), sensor);
  }

  /**
   * @param medium the medium we're looking at
   * @return yes/no
   */
  public boolean radiatesThisMedium(final int medium)
  {
    // step through our sensors
    final Iterator<SensorType> it = _mySensors.values().iterator();
    boolean res = false;

    while (it.hasNext())
    {
      final SensorType st = (SensorType) it.next();

      if (st instanceof SensorType.ActiveSensor)
        if (st.isWorking())
        {
          res = true;
        }
    }

    return res;
  }

  /**
   * find out the total radiated energy on this medium
   */
  public double getRadiatedNoiseFor(final int medium)
  {
    // step through our sensors
    final Iterator<SensorType> it = _mySensors.values().iterator();
    double res = 0;

    while (it.hasNext())
    {
      final SensorType st = (SensorType) it.next();

      // is this of the medium we're looking at?
      if (st.getMedium() == medium)
      {
        // is it an active sensor?
        if (st instanceof SensorType.ActiveSensor)
        // and is it switched on?
          if (st.isWorking())
          {
            // is this the correct type of medium
            SensorType.ActiveSensor at = (SensorType.ActiveSensor) st;
            res += at.getSourceLevel();
          }
      }
    }

    return res;
  }

  /**
   * see if we detect any other vessels
   */
  public void detects(final ASSET.Models.Environment.EnvironmentType environment,
                      DetectionList existingDetections, final ASSET.ParticipantType ownship,
                      final ASSET.ScenarioType scenario,
                      final long time)
  {
    // see if we are working
    if (_isWorking)
    {
      // don't worry about clearing out the old detections, the sensors themselves do it.
      Vector<DetectionEvent> oldDetections;
      oldDetections = new Vector<DetectionEvent>(existingDetections);

      // step through the sensors
      final Iterator<SensorType> it = _mySensors.values().iterator();

      while (it.hasNext())
      {
        final SensorType st = (SensorType) it.next();

        // do the detections for this sensor (they will add themselves to the existingDetetions object
        st.detects(environment, existingDetections, ownship, scenario, time);
      }

      // did we find any new detections
      if (!existingDetections.equals(oldDetections))
      {
        // well, we must have done, find out what they are
        DetectionList newCopy = new DetectionList(existingDetections);
        newCopy.remove(oldDetections);

        // add the new ones to our list
        for (int i = 0; i < newCopy.size(); i++)
        {
          DetectionEvent event = (DetectionEvent) newCopy.elementAt(i);
          _myDetections.add(event);
        }

        // this must be the new list.
        fireNewDetections(newCopy);
      }
    }

    // did
  }

  /**
   * fire off the list of new detections
   *
   * @param list
   */
  private void fireNewDetections(DetectionList list)
  {
    if (_participantDetectedListeners != null)
    {
      for (int i = 0; i < _participantDetectedListeners.size(); i++)
      {
        ParticipantDetectedListener listener = (ParticipantDetectedListener) _participantDetectedListeners.elementAt(i);
        listener.newDetections(list);
      }
    }
  }


  /**
   * get the specific sensor at the indicated index
   */
  public ASSET.Models.SensorType getSensor(final int index)
  {
    return (ASSET.Models.SensorType) _mySensors.get(new Integer(index));
  }

  /**
   * get the sensor with the supplied id (not index)
   *
   * @param sensorId the id of the sensor to retrieve
   * @return that sensor
   */
  public ASSET.Models.SensorType getSensorWithId(final int sensorId)
  {
    SensorType res = null;
    for (Iterator<SensorType> iter = this.getSensors().iterator(); iter.hasNext();)
    {
      SensorType se = (SensorType) iter.next();
      if (se.getId() == sensorId)
      {
        res = se;
        break;
      }
    }
    return res;
  }

  /**
   * find out how many sensors we hold
   */
  public int getNumSensors()
  {
    return _mySensors.size();
  }

  /**
   * whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   *
   * @return yes/no
   */
  public boolean hasEditor()
  {
    return true;
  }

  /**
   * get the editor for this item
   *
   * @return the BeanInfo data for this editable object
   */
  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
    {
      _myEditor = new SensorFitInfo(this);
    }

    return _myEditor;
  }

  ////////////////////////////////////////////////////////////////////////////
  //  embedded class, used for editing the object
  ////////////////////////////////////////////////////////////////////////////
  /**
   * the definition of what is editable about this object
   */
  public class SensorFitInfo extends CoreSensor.BaseSensorInfo
  {

    /**
     * constructor for editable details of a set of Layers
     *
     * @param data the Layers themselves
     */
    public SensorFitInfo(final SensorList data)
    {
      super(data);
    }

		/**
		 * editable GUI properties for our participant
		 * 
		 * @return property descriptions
		 */
		public java.beans.PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final java.beans.PropertyDescriptor[] res = {
						prop("Working", "whether this set of sensors are active"), };
				return res;
			}
			catch (java.beans.IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
		
    /**
     * return a description of this bean, also specifies the custom editor we use
     *
     * @return the BeanDescriptor
     */
    public java.beans.BeanDescriptor getBeanDescriptor()
    {
      final java.beans.BeanDescriptor bp =
        new java.beans.BeanDescriptor(SensorList.class,
                                      ASSET.GUI.Editors.Sensors.SensorFitEditor.class);
      bp.setDisplayName("Sensor Fit");
      return bp;
    }
  }

  //////////////////////////////////////////////////
  // test suport
  //////////////////////////////////////////////////
  public static class SensorListTest extends SupportTesting.EditableTesting
  {
    /**
     * get an object which we can test
     *
     * @return Editable object which we can check the properties for
     */
    public Editable getEditable()
    {
      return new SensorList();
    }
  }

}