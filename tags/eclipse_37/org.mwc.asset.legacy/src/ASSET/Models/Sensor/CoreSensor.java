package ASSET.Models.Sensor;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

import java.beans.MethodDescriptor;
import java.util.*;

import ASSET.*;
import ASSET.Models.*;
import ASSET.Models.Detection.*;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Participants.*;
import MWC.GenericData.WorldLocation;

/**
 * base implementation of a sensor
 */
abstract public class CoreSensor implements ASSET.Models.SensorType,
		java.io.Serializable, MWCModel
{

	// //////////////////////////////////////////////////
	// member bariables
	// //////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the event fired to show the calculations in a detection
	 */
	final public static String SENSOR_COMPONENT_EVENT = "SensorComponents";

	/**
	 * the event fired to show we have finished a detection cycle
	 */
	final public static String DETECTION_CYCLE_COMPLETE = "CycleComplete";

	/**
	 * our editor
	 */
	protected MWC.GUI.Editable.EditorType _myEditor = null;

	/**
	 * the id of this sensor
	 */
	private int _myId;

	/**
	 * our property change support
	 */
	protected java.beans.PropertyChangeSupport _pSupport;

	/**
	 * whether this sensor is active
	 */
	private boolean _isWorking = true;

	/**
	 * the name of this sensor
	 */
	private String _myName;

	/**
	 * time between detection opportunities (millis)
	 */
	private long _TBDO = -1;

	/**
	 * the time of the last scan (which we remember so that we can correctly
	 * observe the _TBDO value
	 */
	private long _lastScan = -1;

	/**
	 * our history of detections
	 */
	private DetectionList _pastDetections = new DetectionList();

	/**
	 * people listening out for my detections
	 */
	private Vector<ParticipantDetectedListener> _participantDetectedListeners;

	/**
	 * name to use as a fallback
	 */
	private String _defaultName;

	/**
	 * externally suppled operation to fire when somebody wants to watch us.
	 */
	protected static SensorOperation _watchMethod;

	/**
	 * ok, we keep track of new detections in each cycle. use a static list so
	 * that we don't forget them
	 */
	private static DetectionList _newDetections;

	/**
	 * ************************************************* constructor
	 * *************************************************
	 */
	public CoreSensor(final int id, final long TBDO, final String defaultName)
	{
		_myId = id;
		_TBDO = TBDO;

		_defaultName = defaultName;

		// check our new detections listener is there
		if (_newDetections == null)
			_newDetections = new DetectionList();
	}

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	public static void setWatchMethod(SensorOperation method)
	{
		_watchMethod = method;
	}

	/*****************************************************************************
	 * scenario data provider methods
	 ****************************************************************************/

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
	 * somebody wants to start listening to us
	 * 
	 * @param listener
	 */
	public void removeParticipantDetectedListener(ParticipantDetectedListener listener)
	{
		_participantDetectedListeners.remove(listener);
	}

	/**
	 * find the list of all detections for this participant since the start of the
	 * scenario
	 */
	public DetectionList getAllDetections()
	{
		return _pastDetections;
	}

	/**
	 * restart this sensors
	 */
	public void restart()
	{
		// clear out the past detections
		_pastDetections.removeAllElements();
	}

	// let the user watch this sensor
	public void watchMe()
	{
		if (_watchMethod != null)
			_watchMethod.run(this);
	}

	/**
	 * do the detections for this step
	 * 
	 * @param environment
	 * @param existingDetections
	 * @param ownship
	 * @param scenario
	 * @param time
	 */
	final public void detects(final ASSET.Models.Environment.EnvironmentType environment,
			final DetectionList existingDetections, final ASSET.ParticipantType ownship,
			final ASSET.ScenarioType scenario, final long time)
	{

		// ok, see if the current time fits in between our _TBDO
		boolean canRun = false;

		// see if we are due to run
		canRun = isDueToScan(time);

		// can we?
		if (canRun)
		{
			// ditch any old detections, since we're about to supercede them
			removeMyOldDetections(existingDetections);

			// are we active?
			if (isWorking())
			{
				// just check that our list is empty
				_newDetections.removeAllElements();

				// step through the participants in the scenario
				final Collection<ParticipantType> parts = scenario.getListOfVisibleParticipants();

				for (Iterator<ParticipantType> iterator = parts.iterator(); iterator.hasNext();)
				{
					ParticipantType target = (ParticipantType) iterator.next();

					// is this us?
					if (target != ownship)
					{
						// can we detect it?
						boolean canDetectHim = canDetectThisType(ownship, target, environment);

						if (canDetectHim)
						{

							final DetectionEvent thisD = detectThis(environment, ownship, target, time,
									scenario);

							if (thisD != null)
							{
								// add to our current list of detections
								existingDetections.add(thisD);

								// and add to our historic list of detections
								_pastDetections.add(thisD);

								// and remember for our new list
								_newDetections.add(thisD);
							} // whether we made a detection
						} // whether we should even try to detect a participant of this type
					} // of this is not us
				} // if index was valid

				// ok, we've performed all of our detections
				if (_pSupport != null)
				{
					Long theTime = new Long(time);
					_pSupport.firePropertyChange(DETECTION_CYCLE_COMPLETE, null, theTime);
				}

				// and have we got anything to report?
				if (_newDetections.size() > 0)
				{
					// yup, fire them off
					fireTheseDetections(_newDetections);

					// and clear the list
					_newDetections.removeAllElements();
				}
			}
		}
	}

	/**
	 * fire off our detections to anybody who is interested
	 * 
	 * @param thisD
	 */
	private void fireTheseDetections(DetectionList thisD)
	{
		if (_participantDetectedListeners != null)
		{
			for (int i = 0; i < _participantDetectedListeners.size(); i++)
			{
				ParticipantDetectedListener listener = _participantDetectedListeners
						.elementAt(i);
				listener.newDetections(thisD);
			}
		}
	}

	/**
	 * remove any of our past detections from the list. Detections stay valid
	 * between TBDOs, to give some degree of persistnce
	 * 
	 * @param existingDetections
	 *          the old list of detections
	 */
	private void removeMyOldDetections(DetectionList existingDetections)
	{
		// are there any at all?
		if (existingDetections != null)
		{

			// keep track of the detections to remove
			Vector<DetectionEvent> ditchThese = new Vector<DetectionEvent>(0, 1);

			// ditch any old detections we produced
			int len = existingDetections.size();
			for (int i = 0; i < len; i++)
			{
				DetectionEvent de = (DetectionEvent) existingDetections.elementAt(i);
				if (de.getSensor() == this.getId())
				{
					// get rid of this one
					ditchThese.add(de);
				}
			}

			// ok, were there any?
			if (ditchThese.size() > 0)
			{
				// yup, get on with it
				existingDetections.removeAll(ditchThese);
			}
		}
	}

	/**
	 * decide if it's time for us to run agin
	 * 
	 * @param time
	 *          the current time
	 * @return yes/no
	 */
	private boolean isDueToScan(final long time)
	{
		boolean canRun = false;

		if ((_lastScan == -1) || (_TBDO <= 0))
		{
			// hey, we haven't run yet. Get going.
			canRun = true;
		}
		else
		{
			// ok, find the elapsed time
			long elapsed = time - _lastScan;

			// are we due for another?
			if (elapsed >= _TBDO)
			{
				canRun = true;
			}
		}

		// ok, are we going to go for it?
		if (canRun)
		{
			// remember the current time
			_lastScan = time;
		}

		return canRun;
	}

	// what is the detection strength for this target?
	abstract protected DetectionEvent detectThis(
			final ASSET.Models.Environment.EnvironmentType environment,
			final ASSET.ParticipantType host, final ASSET.ParticipantType target,
			final long time, ScenarioType scenario);

	public void addSensorCalculationListener(java.beans.PropertyChangeListener listener)
	{
		if (_pSupport == null)
			_pSupport = new java.beans.PropertyChangeSupport(this);

		_pSupport.addPropertyChangeListener(SENSOR_COMPONENT_EVENT, listener);
		_pSupport.addPropertyChangeListener(DETECTION_CYCLE_COMPLETE, listener);
	}

	public void removeSensorCalculationListener(java.beans.PropertyChangeListener listener)
	{
		_pSupport.removePropertyChangeListener(SENSOR_COMPONENT_EVENT, listener);
		_pSupport.removePropertyChangeListener(DETECTION_CYCLE_COMPLETE, listener);
	}

	public String toString()
	{
		return getName();
	}

	final public String getName()
	{
		final String res;

		// do we have specific name?
		if (_myName != null)
			res = _myName;
		else
		{
			// no, use default name for this type of sensor
			res = _defaultName;
		}

		return res;
	}

	final public void setName(String name)
	{
		_myName = name;
	}

	final public int getId()
	{
		return _myId;
	}

	final public void setId(final int val)
	{
		_myId = val;
	}

	/**
	 * whether this sensor is currently active
	 * 
	 * @return
	 */
	final public boolean isWorking()
	{
		return _isWorking;
	}

	/**
	 * whether this sensor is currently working
	 * 
	 * @param isWorking
	 *          yes/no
	 */
	final public void setWorking(boolean isWorking)
	{
		// is the new value actually new?
		if (isWorking != _isWorking)
		{
			// ok, change our state
			_isWorking = isWorking;

			// and update the editor
			if (_myEditor != null)
			{
				_myEditor.fireChanged(this, "Working", new Boolean(!isWorking), new Boolean(
						isWorking));
			}
		}
	}

	// allow an 'overview' test, just to check if it is worth all of the above
	// processing
	abstract protected boolean canDetectThisType(NetworkParticipant ownship,
			ASSET.ParticipantType other, EnvironmentType env);

	/**
	 * convenience method to indicate if this type of sensor is capable of
	 * identifying a target
	 * 
	 * @return yes/no
	 */
	abstract public boolean canIdentifyTarget();

	/**
	 * convenience method to indicate if this sensor produces a bearing from the
	 * sensor to the target. Very few sensors do not produce bearing.
	 * 
	 * @return yes/no
	 */
	protected boolean canProduceBearing()
	{
		return true;
	}

	/**
	 * whether this sensor produces range in its output
	 * 
	 * @return yes/no
	 */
	protected boolean canProduceRange()
	{
		return true;
	}

	/**
	 * get the target location
	 * 
	 * @param participant
	 * @return
	 */
	protected WorldLocation getLocationFor(ASSET.ParticipantType participant)
	{
		return participant.getStatus().getLocation();
	}

	/**
	 * handle the demanded change in sensor lineup
	 * 
	 * @param status
	 */
	public void inform(DemandedSensorStatus status)
	{
		this.setWorking(status.getSwitchOn());
	}

	/**
	 * get how long between occasions where we are able to produce detection
	 * 
	 * @return millis
	 */
	final public long getTimeBetweenDetectionOpportunities()
	{
		return _TBDO;
	}

	/**
	 * set how long between occasions where we are able to produce detection
	 * 
	 * @param millis
	 */
	final public void setTimeBetweenDetectionOpportunities(long millis)
	{
		_TBDO = millis;
	}

	/**
	 * calculate relative bearing of specified bearing from this vessel course
	 */
	protected double relativeBearing(final double course, final double bearing)
	{
	  return bearing - course;
	}

	// //////////////////////////////////////////////////
	// the editor object
	// //////////////////////////////////////////////////
	public static class BaseSensorInfo extends MWC.GUI.Editable.EditorType
	{
		/**
		 * @param data
		 *          the Layers themselves
		 */
		public BaseSensorInfo(final SensorType data)
		{
			this(data, false);
		}

		/**
		 * @param data
		 *          the Layers themselves
		 */
		public BaseSensorInfo(final SensorType data, boolean firesReports)
		{
			super(data, data.getName(), "Edit", "images/icons/Sensor.gif", firesReports);
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
						prop("Name", "the name of this optic sensor"),
						prop("Working", "whether this sensor is in use"), };
				return res;
			}
			catch (java.beans.IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class<CoreSensor> c = CoreSensor.class;
			MethodDescriptor[] res = null;
			if (_watchMethod != null)
			{
				final MethodDescriptor[] mds = { method(c, "watchMe", null, "Monitor this sensor"), };
				res = mds;
			}
			return res;
		}
	}

	/**
	 * ************************************************* utility class
	 * *************************************************
	 */
	public static class SensorUtils
	{
		/**
		 * calculate the sum of the two decibel values
		 */
		static public double powerSum(final double x, final double y)
		{
			double res = 0;

			// move them to the normal domain
			final double xNorm = Math.pow(10, x / 10d);
			final double yNorm = Math.pow(10, y / 10d);

			// add them
			final double sum = xNorm + yNorm;

			// move back to the log domain
			res = (Math.log(sum) / Math.log(10)) * 10;

			// done
			return res;
		}
	}

	public static interface SensorOperation
	{
		public void run(SensorType me);
	}
}