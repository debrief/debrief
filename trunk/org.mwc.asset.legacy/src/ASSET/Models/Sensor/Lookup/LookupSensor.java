package ASSET.Models.Sensor.Lookup;

import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Movement.HeloMovementCharacteristics;
import ASSET.Models.Movement.SurfaceMovementCharacteristics;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Models.Vessels.Helo;
import ASSET.Models.Vessels.Surface;
import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.ParticipantDetectedListener;
import ASSET.Participants.Status;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.Recording.DebriefReplayObserver;
import ASSET.Scenario.Observers.StopOnElapsedObserver;
import ASSET.ScenarioType;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GenericData.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Title: ASSET Simulator Description: Advanced Scenario Simulator for
 * Evaluation of Tactics Copyright: Copyright (c) 2004 Company: PlanetMayo Ltd
 * 
 * @author Ian Mayo
 * @version 1.0
 */

public abstract class LookupSensor extends CoreSensor
{

	// //////////////////////////////////////////////////////////
	// standard lookup parameters
	// //////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * our record of past detections
	 */
	protected Hashtable<ParticipantType, LastTargetContact> _pastContacts;

	/**
	 * variability in detection range
	 */
	protected double VDR = 0;

	/**
	 * max range factor
	 */
	protected double MRF = 0;

	/**
	 * classification range factor
	 */
	protected double CRF = 0;

	/**
	 * classification time period (secs)
	 */
	protected Duration CTP = null;

	/**
	 * identification range factor
	 */
	protected double IRF = 0;

	/**
	 * identification time period (secs)
	 */
	protected Duration ITP = null;

	/**
	 * the minimum height at which this sensor is operable (m)
	 */
	private final static double MIN_HEIGHT = -18;

	// //////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// //////////////////////////////////////////////////////////

	/**
	 * constructor for a lookup sensor
	 * 
	 * @param VDR
	 *          variability in detection range
	 * @param TBDO
	 *          time between detection opportunities (millis)
	 * @param MRF
	 *          maximum range factor
	 * @param CRF
	 *          classification range factor
	 * @param CTP
	 *          classification time period
	 * @param IRF
	 *          identification range factor
	 * @param ITP
	 *          identification time period
	 */
	public LookupSensor(int id, String name, double VDR, long TBDO, double MRF, double CRF,
			Duration CTP, double IRF, Duration ITP, String defaultName)
	{
		super(id, TBDO, defaultName);
		setName(name);
		this.VDR = VDR;
		this.MRF = MRF;
		this.CRF = CRF;
		this.CTP = CTP;
		this.IRF = IRF;
		this.ITP = ITP;

		_pastContacts = new Hashtable<ParticipantType, LastTargetContact>();
	}

	// what is the detection strength for this target?
	protected DetectionEvent detectThis(EnvironmentType environment,
			ParticipantType ownship, ParticipantType target, long time, ScenarioType scenario)
	{
		DetectionEvent res = null;

		// store the range for later on, if we want to.
		WorldDistance actualRange = null;

		// //////////////////////////////////////////////////////////
		// LOOK FOR ANY EXISTING CONTACT WITH THIS PARTICIPANT
		// //////////////////////////////////////////////////////////

		// have we already detected this?
		LastTargetContact oldParameters = _pastContacts.get(target);

		// //////////////////////////////////////////////////////////
		// DETERMINE THE CURRENT SET OF LOOKUP PARAMETERS
		// //////////////////////////////////////////////////////////

		// sort out the new set of parameters
		LastTargetContact newParameters = parametersFor(ownship, target, scenario,
				environment, time);

		// //////////////////////////////////////////////////////////
		// ESTABLISH IF OUR CONTACT PARAMETERS HAVE CHANGED
		// //////////////////////////////////////////////////////////

		// do we have any old parameters? (have we detected this target before?)
		if (oldParameters != null)
		{
			// fill in the known fields from the last dataset
			newParameters.setDetectionState(oldParameters.getDetectionState());
			newParameters.setTransitionTime(oldParameters.getTimeOfThisTransition());

			// do they match
			if (!oldParameters.matchesThis(newParameters))
			{
				// nope, they've changed. Clear the value
				oldParameters = null;
			}
			else
			{
				// yup, they sure to match, make a copy of them
				newParameters.setRanges(oldParameters.getRI(), oldParameters.getRP());
			}
		}

		// //////////////////////////////////////////////////////////
		// CALCULATE NEW RANGES IF NECESSARY
		// //////////////////////////////////////////////////////////

		// ok, do we need to recalculate our ranges?
		if (oldParameters == null)
		{
			// calculate the RP/RI first
			WorldDistance RP = calculateRP(ownship, target, scenario, environment, time,
					newParameters);

			// and now calculate the RI
			WorldDistance RI = calculateRI(RP, VDR);

			// and remember them
			newParameters.setRanges(RI, RP);
		}

		// //////////////////////////////////////////////////////////
		// CALCULATE THE ACTUAL RANGE
		// //////////////////////////////////////////////////////////

		// ok, now calculate the slant range
		actualRange = calculateSlantRangeFor(ownship.getStatus().getLocation(), target
				.getStatus().getLocation());

		WorldVector offset = target.getStatus().getLocation().subtract(
				ownship.getStatus().getLocation());
		double bearing = MWC.Algorithms.Conversions.Rads2Degs(offset.getBearing());
		double relBearing = bearing - ownship.getStatus().getCourse();

		// store the last detection state
		int currentState = newParameters.getDetectionState();

		// how does this compare with the IR
		WorldDistance RI = newParameters.getRI();
		if (!actualRange.greaterThan(RI))
		{
			// yes - in range - handle the classification.

			// handle our current detection state
			switch (currentState)
			{
			case DetectionEvent.IDENTIFIED:
			{
				// hey - it just couldn't get any better.
				break;
			}
			case DetectionEvent.CLASSIFIED:
			{
				WorldDistance nextThreshold = calculateIR(newParameters.getRP(), IRF);
				checkAndMakeTransition(actualRange, nextThreshold, newParameters, time, ITP,
						DetectionEvent.IDENTIFIED);
				break;
			}
			case DetectionEvent.DETECTED:
			{
				WorldDistance nextThreshold = calculateCR(newParameters.getRP(), CRF);
				checkAndMakeTransition(actualRange, nextThreshold, newParameters, time, CTP,
						DetectionEvent.CLASSIFIED);
				break;
			}
			case DetectionEvent.UNDETECTED:
			{
				newParameters.setDetectionState(DetectionEvent.DETECTED);
				newParameters.setTransitionTime(time);
				break;
			}
			}

			// only produce bearing data if this sensor is capable of it.
			Float bearingVal = null;
			Float relBearingVal = null;
			if (canProduceBearing())
			{
				bearingVal = new Float(bearing);
				relBearingVal = new Float(relBearing);
			}

			// and only produce range if we're capable of it
			WorldDistance theRange = null;
			if (canProduceRange())
			{
				theRange = actualRange;
			}
			else
			{
				theRange = null;
			}

			// and create the detection
			res = new DetectionEvent(time, ownship.getId(), ownship.getStatus().getLocation(),
					this, theRange, null, bearingVal, relBearingVal, null, target.getCategory(),
					target.getStatus().getSpeed(), new Float(target.getStatus().getCourse()),
					target, newParameters.getDetectionState());

		}
		else
		{

			// hmm, are we in contact at least?
			if (newParameters.getDetectionState() == DetectionEvent.UNDETECTED)
			{
				// hey, forget about it. We're even in contact. No chance
				newParameters = null;
			}
			else
			{

				// so. we're outside the instantaneous range, but if we're already in
				// contact it can stretch
				// out to the max range - let it be.

				// no, we're not in range. See if we have passed the maximum range
				WorldDistance MaxRange = calculateMaxRange(newParameters.getRP(), MRF);

				if (actualRange.greaterThan(MaxRange))
				{
					// NO CONTACT. Dead. Forget about the new parameters
					newParameters = null;

				}
				else
				{
					// hey - we're on the hairy edge of being in contact. Things aren't
					// going to get any
					// better here. Create a detection with the new relative data, but
					// maintaining the same
					// detection state
					res = new DetectionEvent(time, ownship.getId(), ownship.getStatus()
							.getLocation(), this, actualRange, null, new Float(bearing), new Float(
							relBearing), null, target.getCategory(), target.getStatus().getSpeed(),
							new Float(target.getStatus().getCourse()), target, currentState);

				}
			}
		}

		// ok. do we have a contact?
		if (newParameters != null)
		{
			// ok, done - remember how we got on
			_pastContacts.put(target, newParameters);

			// ok, just see if there are any pSupport listners
			if (_pSupport != null)
			{
				if (_pSupport.hasListeners(SENSOR_COMPONENT_EVENT))
				{
					// create the event
					LookupSensorComponentsEvent sev = new LookupSensorComponentsEvent(time, newParameters
							.getDetectionState(), newParameters.getRI(), newParameters.getRP(),
							actualRange, target.getName());

					// and fire it!
					_pSupport.firePropertyChange(SENSOR_COMPONENT_EVENT, null, sev);
				}
			}

		}
		else
		{
			_pastContacts.remove(target);
		}

		return res;
	}

	protected WorldDistance calculateRI(WorldDistance RP, double VDRa)
	{
		double newRandom = RandomGenerator.generateNormalValue(VDRa);
		final double res = RP.getValueIn(WorldDistance.METRES) * (1 + newRandom);
		WorldDistance RI = new WorldDistance(res, WorldDistance.METRES);
		return RI;
	}

	protected WorldDistance calculateCR(WorldDistance RP, double CRFa)
	{
		WorldDistance classRng = new WorldDistance(RP.getValueIn(WorldDistance.METRES) * CRFa,
				WorldDistance.METRES);
		return classRng;
	}

	protected WorldDistance calculateIR(WorldDistance RP, double IRFa)
	{
		WorldDistance identRng = new WorldDistance(RP.getValueIn(WorldDistance.METRES) * IRFa,
				WorldDistance.METRES);
		return identRng;
	}

	protected WorldDistance calculateMaxRange(WorldDistance RP, double MRFa)
	{
		WorldDistance MaxRange = new WorldDistance(RP.getValueIn(WorldDistance.METRES) * MRFa,
				WorldDistance.METRES);
		return MaxRange;
	}

	/**
	 * see if we are ready to make the transition to the next state
	 * 
	 * @param actualRange
	 *          the current range
	 * @param nextThreshold
	 *          the range at which detection can pass to the next state
	 * @param newParameters
	 *          the current situation
	 * @param time
	 *          the current time
	 * @param waitingPeriod
	 *          how long we have to wait before moving to the next state
	 * @param newState
	 *          the next State we will move to
	 * @see DetectionEvent.UNDETECTED
	 */
	private static void checkAndMakeTransition(WorldDistance actualRange,
			WorldDistance nextThreshold, LastTargetContact newParameters, long time,
			Duration waitingPeriod, int newState)
	{
		// ok. see if we are within the classification range factor
		// WorldDistance classRng = new
		// WorldDistance(oldParameters.getRP().getValueIn(WorldDistance.METRES) *
		// rangeFactor,
		// WorldDistance.METRES);

		if (!actualRange.greaterThan(nextThreshold))
		{
			// hey, less than or equal to the identification range -
			// -- check if we've passed the identification period
			long lastTransitionTime = newParameters.getTimeOfThisTransition();
			final long identPeriod = (long) waitingPeriod.getValueIn(Duration.MILLISECONDS);
			long elapsedTime = time - lastTransitionTime;

			if (elapsedTime > identPeriod)
			{
				// yup - make the transition!
				newParameters.setTransitionTime(lastTransitionTime + identPeriod);
				newParameters.setDetectionState(newState);
			} // whether sufficient time elapsed
		} // whether within range
	}

	protected static WorldDistance calculateSlantRangeFor(WorldLocation ownship,
			WorldLocation target)
	{
		WorldDistance res = null;
		WorldVector flat = ownship.subtract(target);

		// ok. what's the range
		double flatM = MWC.Algorithms.Conversions.Degs2m(flat.getRange());
		double heightM = Math.abs(target.getDepth() - ownship.getDepth());

		// and apply the slant
		res = new WorldDistance(Math.sqrt(flatM * flatM + heightM * heightM),
				WorldDistance.METRES);

		// ok, done.
		return res;
	}

	/**
	 * determine the lookup parameters applicable to this sensor
	 * 
	 * @param ownship
	 *          us
	 * @param target
	 *          them
	 * @param scenario
	 *          the scenario
	 * @param environment
	 *          the environment
	 * @param time
	 *          current time
	 * @return the set of lookup parameters applicable to this sensor
	 */
	protected abstract LastTargetContact parametersFor(NetworkParticipant ownship,
			NetworkParticipant target, ScenarioType scenario, EnvironmentType environment,
			long time);

	/**
	 * calculate the predicted range for this contact
	 * 
	 * @param ownship
	 * @param target
	 * @param scenario
	 * @param environment
	 * @return
	 */
	abstract protected WorldDistance calculateRP(NetworkParticipant ownship,
			NetworkParticipant target, ScenarioType scenario, EnvironmentType environment,
			long time, LastTargetContact params);

	public WorldDistance getEstimatedRange()
	{
		return null;
	}

	public int getMedium()
	{
		return 0;
	}

	/**
	 * allow an 'overview' test, just to check if it is worth all of the above
	 * processing
	 * 
	 * @param ownship
	 * @param target
	 * @param env
	 * @return
	 */
	protected boolean canDetectThisType(NetworkParticipant ownship, ParticipantType target,
			EnvironmentType env)
	{
		double height = -target.getStatus().getLocation().getDepth();
		return height > MIN_HEIGHT;
	}

	/**
	 * decide if this sensor is operable (if it's out of the water
	 * 
	 * @param target
	 *          the current target location
	 * @return yes/no
	 */
	protected boolean isOperable(WorldLocation target)
	{
		double height = -target.getDepth();
		return height > MIN_HEIGHT;
	}

	/**
	 * if this sensor has a dynamic behaviour, update it according to the demanded
	 * status
	 * 
	 * @param myDemandedStatus
	 * @param myStatus
	 * @param newTime
	 */
	public void update(DemandedStatus myDemandedStatus, Status myStatus, long newTime)
	{
		// don't bother. let classes over-ride as necessary
	}

	/**
	 * base class providing unknown result functionality
	 */
	protected static class BaseLookup
	{
		/**
		 * the value to return for an unknown target type
		 */
		protected Double _unknownResult;

		/**
		 * specify what value to return for an unknown target type
		 * 
		 * @param unknownResult
		 *          value to return.
		 */
		public void setUnknownResult(Double unknownResult)
		{
			this._unknownResult = unknownResult;
		}

		public Double getUnknownResult()
		{
			return _unknownResult;
		}

		/**
		 * return the unknown result value, if we have to
		 * 
		 * @param val
		 *          the value to check against
		 * @return either the valid result, or our unknown value
		 */
		protected Double checkResult(Double val, Double unknownResult)
		{
			if (val == null)
				val = unknownResult;

			return val;
		}
	}

	// //////////////////////////////////////////////////////////
	// embedded class which provides a lookup table of string values, returning
	// a double value for the matching string
	// //////////////////////////////////////////////////////////
	public static class StringLookup extends BaseLookup
	{
		/**
		 * store the list of double values indexed by string
		 */
		private HashMap<String, Double> _myTable;

		/**
		 * constructor
		 * 
		 * @param strs
		 *          list of string indices
		 * @param vals
		 *          list of double values
		 */
		private StringLookup(String[] strs, double[] vals)
		{
			if (strs.length != vals.length)
			{
				System.err.println("STRING LOOKUP PARAMETERS OF UNEQUAL LENGTH!!!");
			}

			_myTable = new HashMap<String, Double>();
			for (int i = 0; i < strs.length; i++)
			{
				String str = strs[i];
				double val = vals[i];
				_myTable.put(str, new Double(val));
			}
		}

		/**
		 * provide the indexes we currently hold
		 * 
		 * @return
		 */
		public Collection<String> getIndices()
		{
			return _myTable.keySet();
		}

		/**
		 * @param strs
		 *          the string values to compare against
		 * @param vals
		 *          the double values to return for them
		 * @param unknownResult
		 *          what to return if we don't match the string
		 */
		public StringLookup(String[] strs, double[] vals, Double unknownResult)
		{
			this(strs, vals);

			super.setUnknownResult(unknownResult);
		}

		/**
		 * determine if we have an index for the supplied value
		 * 
		 * @param index
		 * @return
		 */
		public boolean containsValueFor(String index)
		{
			return _myTable.keySet().contains(index);
		}

		/**
		 * find the double value at the supplied index
		 * 
		 * @param index
		 *          the string to index against
		 * @param defaultValue -
		 *          the value to return if a matching one isn't found. Throws
		 *          runtime error if matching type isn't found and a default value
		 *          isn's supplied
		 * @return the matching double value (or null)
		 */
		public Double find(String index, Double defaultValue)
		{
			Double res = null;
			Iterator<String> iterator = _myTable.keySet().iterator();
			while (iterator.hasNext() && res == null)
			{
				String s = iterator.next();
				if (s.equals(index))
				{
					res = _myTable.get(index);
					break;
				}

			}
			// did we find anything?
			res = checkResult(res, defaultValue);

			// did it work?
			if (res == null)
			{
				throw new RuntimeException("Lookup sensor: data value not supplied for:" + index);
			}

			return res;
		}

		/**
		 * find the double value at the supplied index
		 * 
		 * @param index
		 *          the string to index against
		 * @return the matching double value (or null)
		 */
		public Double find(String index)
		{
			return find(index, this._unknownResult);
		}

	}

	// //////////////////////////////////////////////////////////
	// embedded class which provides a lookup table of integer values, returning
	// a double value for the matching string
	// //////////////////////////////////////////////////////////
	public static class IntegerLookup
	{
		/**
		 * store the list of double values indexed by an integer
		 */
		private HashMap<Integer, Double> _myTable;

		/**
		 * constructor
		 * 
		 * @param indices
		 *          list of integer indices
		 * @param vals
		 *          list of double values
		 */
		public IntegerLookup(int[] indices, double[] vals)
		{
			this();
			if (indices.length != vals.length)
			{
				System.err.println("INTEGER LOOKUP PARAMETERS OF UNEQUAL LENGTH!!!");
			}

			for (int i = 0; i < indices.length; i++)
			{
				Integer key = new Integer(indices[i]);
				double val = vals[i];
				_myTable.put(key, new Double(val));
			}
		}

		/**
		 * constructor - create our array
		 */
		public IntegerLookup()
		{
			_myTable = new HashMap<Integer, Double>();
		}

		public void add(int index, double value)
		{
			_myTable.put(new Integer(index), new Double(value));
		}

		public Collection<Integer> indices()
		{
			return _myTable.keySet();
		}

		/**
		 * find the double value at the supplied index
		 * 
		 * @param index
		 *          the integer to index against
		 * @return the matching double value (or null)
		 */
		public Double find(int index)
		{
			Integer indexVal = new Integer(index);

			Double res = null;
			Iterator<Integer> iterator = _myTable.keySet().iterator();
			while (iterator.hasNext() && res == null)
			{
				Integer s = iterator.next();
				if (s.equals(indexVal))
				{
					res = _myTable.get(indexVal);
					break;
				}
			}

			return res;
		}
	}

	// //////////////////////////////////////////////////////////
	// embedded class which provides a two-dimensional lookup table of string
	// values against sea-state, returning
	// a double value for the matching string
	// //////////////////////////////////////////////////////////
	public static class IntegerTargetTypeLookup
	{
		/**
		 * store the list of double values indexed by string
		 */
		private Vector<NamedList> _datums;

		/**
		 * the default value to use if none of the types are recognised
		 */
		private Double _defaultValue;

		// /**
		// * constructor
		// *
		// * @param states list of integer indices
		// * @param vals list of double values
		// * @param defaultValue the value to use if the target type hasn't been
		// represented in the table
		// */
		// public IntegerTargetTypeLookup(int[] states, StringLookup[] vals, Double
		// defaultValue)
		// {
		// if (states.length != vals.length)
		// {
		// System.err.println("INTEGER LOOKUP PARAMETERS OF UNEQUAL LENGTH!!!");
		// }
		//
		// _defaultValue = defaultValue;
		//
		// _myTable = new HashMap();
		// for (int i = 0; i < states.length; i++)
		// {
		// Integer key = new Integer(states[i]);
		// _myTable.put(key, vals[i]);
		// }
		// }

		public IntegerTargetTypeLookup(Vector<NamedList> datums, Double value)
		{
			_defaultValue = value;

			// and store the datums
			_datums = datums;
		}

		/**
		 * find the double value at the supplied index
		 * 
		 * @param index
		 *          the integer to index against
		 * @return the matching double value (or our null value)
		 */
		public Double find(int index, String type)
		{
			Double res = _defaultValue;
			// right, first get the correct set of datums
			for (Iterator<NamedList> iterator = _datums.iterator(); iterator.hasNext();)
			{
				// get the next series
				NamedList thisList = (NamedList) iterator.next();

				// is this the correct one
				if (thisList._myType.equals(type))
				{
					// cool, here we are, get the correct type
					Double val = thisList._myValues.get(index);
					if (val != null)
						res = val;
					break;
				}
			}

			return res;
		}

		public NamedList getThisSeries(String name)
		{
			NamedList thisSet = null;
			// right, first get the correct set of datums
			for (Iterator<NamedList> iterator = _datums.iterator(); iterator.hasNext();)
			{
				// get the next series
				thisSet = (NamedList) iterator.next();

				// is this the correct one
				if (thisSet._myType == name)
					break;
			}

			return thisSet;

		}
		
		public Collection<String> getNames()
		{
			Vector<String> res = new Vector<String>(0,1);
			for (Iterator<NamedList> iterator = _datums.iterator(); iterator.hasNext();)
			{
				// get the next series
				NamedList thisList = (NamedList) iterator.next();
				// and remember it
				res.add(thisList._myType);
			}
			
			return res;
		}

		/**
		 * specify what value to return for an unknown target type
		 * 
		 * @param unknownResult
		 *          value to return.
		 */
		public void setUnknownResult(Double unknownResult)
		{
			this._defaultValue = unknownResult;
		}

		/**
		 * find out the default value
		 */
		public Double getUnknownResult()
		{
			return _defaultValue;
		}
	}

	// //////////////////////////////////////////////////
	// embedded class for event fired after each detection step
	// //////////////////////////////////////////////////
	public static class LookupSensorComponentsEvent
	{
		// //////////////////////////////////////////////////
		// member objects
		// //////////////////////////////////////////////////

		/**
		 * the name of the target we're looking at
		 */
		final private String _tgtName;

		/**
		 * the current detection state (string)
		 */
		final private String _stateString;

		/**
		 * the current detection state
		 */
		final private int _state;

		/**
		 * a utility class to convert from state to text
		 */
		private DetectionEvent.DetectionStatePropertyEditor converter = new DetectionEvent.DetectionStatePropertyEditor();

		/**
		 * the ranges we want
		 */
		final private WorldDistance _RI;

		final private WorldDistance _RP;

		final private WorldDistance _actual;

		/** the time at which this was recorded
		 * 
		 */
		final private long _time;

		// //////////////////////////////////////////////////////////
		// constructor
		// //////////////////////////////////////////////////////////

		/**
		 * constructor
		 */
		public LookupSensorComponentsEvent(long time, int state, WorldDistance RI, WorldDistance RP,
				WorldDistance actual, String tgtName)
		{
			_time = time;
			_tgtName = tgtName;
			converter.setIndex(state);
			_stateString = converter.getAsText();
			_state = state;
			_actual = actual;
			_RP = RP;
			_RI = RI;
		}

		public String getTgtName()
		{
			return _tgtName;
		}
		
		public long getTime()
		{
			return _time;
		}

		public String getStateString()
		{
			return _stateString;
		}

		public int getState()
		{
			return _state;
		}

		public String toString()
		{
			String res;

			res = _tgtName;

			return res;
		}

		public WorldDistance getRI()
		{
			return _RI;
		}

		public WorldDistance getRP()
		{
			return _RP;
		}

		public WorldDistance getActual()
		{
			return _actual;
		}
	}

	// //////////////////////////////////////////////////////////
	// embedded utility class which contains a set of lookup parameters for the
	// lookup
	// sensor in question
	// //////////////////////////////////////////////////////////

	protected class LastTargetContact
	{

		/**
		 * our list of properties
		 */
		protected Vector<Number> _myList;

		/**
		 * the instantaneous range calculated
		 */
		WorldDistance _theInstantaneousRange;

		/**
		 * the predicted range
		 */
		WorldDistance _thePredictedRange;

		/**
		 * the last detection we obtained
		 */
		int _currentState = DetectionEvent.UNDETECTED;

		/**
		 * the time we made the transition to this state (in order to allow the
		 * time-related transition to the next)
		 */
		private long _thisTransition = -1;

		// //////////////////////////////////////////////////////////
		// constructor
		// //////////////////////////////////////////////////////////
		public LastTargetContact()
		{
			this._myList = new Vector<Number>(1, 1);
		}

		/**
		 * get the instantaneous range
		 * 
		 * @return
		 */
		public WorldDistance getRI()
		{
			return _theInstantaneousRange;
		}

		/**
		 * get the predicted range
		 * 
		 * @return
		 */
		public WorldDistance getRP()
		{
			return _thePredictedRange;
		}

		/**
		 * set the predicted and instantaneous ranges
		 */
		public void setRanges(WorldDistance RI, WorldDistance RP)
		{
			_theInstantaneousRange = RI;
			_thePredictedRange = RP;
		}

		/**
		 * store the last detection (so we can advance the detection state if we
		 * want to
		 */
		public void setDetectionState(int state)
		{
			_currentState = state;
		}

		/**
		 * set the time of this transition
		 */
		public void setTransitionTime(long val)
		{
			_thisTransition = val;
		}

		/**
		 * get the time of the last transition
		 */
		public long getTimeOfThisTransition()
		{
			return _thisTransition;
		}

		/**
		 * get the last detection
		 * 
		 * @return the last detection state
		 * @see DetectionEvent.UNDETECTED for an example
		 */
		public int getDetectionState()
		{
			return _currentState;
		}

		public int size()
		{
			return _myList.size();
		}

		public Object elementAt(int i)
		{
			return _myList.elementAt(i);
		}

		/**
		 * find out if the other set of parameters matches this one
		 * 
		 * @param otherParams
		 *          the other set of parameters
		 * @return yes/no
		 */
		public boolean matchesThis(LastTargetContact otherParams)
		{
			boolean res = true;

			// first do "idiot test", if list are of different length they must be
			// different
			if (otherParams.size() != size())
				res = false;
			else
			{
				for (int i = 0; i < otherParams.size(); i++)
				{
					Object otherObject = otherParams.elementAt(i);
					Object thisObject = elementAt(i);

					// do they match?
					if (!otherObject.equals(thisObject))
					{
						res = false;
						continue;
					}

				}
			}
			return res;
		}

		public void insertElementAt(Double value, int index)
		{
			_myList.insertElementAt(value, index);
		}

		public Object get(int index)
		{
			return _myList.get(index);
		}
	}

	// //////////////////////////////////////////////////////////
	// test instance to prove class
	// //////////////////////////////////////////////////////////
	private static class TestSensor extends LookupSensor
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public static Vector<Integer> myParams = new Vector<Integer>();

		double _rp_m;

		double _ri_m;

		double _cr_m;

		double _ir_m;

		double _max_m;

		public TestSensor(double VDR, long TBDO, double MRF, double CRF, Duration CTP,
				double IRF, Duration ITP, double rp_m, double ri_m, double cr_m, double ir_m,
				double max_m)
		{
			super(12, "test", VDR, TBDO, MRF, CRF, CTP, IRF, ITP, "Test sensor");
			_rp_m = rp_m;
			_ri_m = ri_m;
			_cr_m = cr_m;
			_ir_m = ir_m;
			_max_m = max_m;
		}

	
		public String getVersion()
		{
			return "not applicable";
		}

		/**
		 * get the editor for this item
		 * 
		 * @return the BeanInfo data for this editable object
		 */
		public EditorType getInfo()
		{
			return null; // To change body of implemented methods use File | Settings
										// | File Templates.
		}

		/**
		 * whether there is any edit information for this item this is a convenience
		 * function to save creating the EditorType data first
		 * 
		 * @return yes/no
		 */
		public boolean hasEditor()
		{
			return false; // To change body of implemented methods use File | Settings
										// | File Templates.
		}

		public boolean canIdentifyTarget()
		{
			return true; // To change body of implemented methods use File | Settings
										// | File Templates.
		}

		// allow an 'overview' test, just to check if it is worth all of the above
		// processing
		protected boolean canDetectThisType(NetworkParticipant ownship, ParticipantType other,
				EnvironmentType env)
		{
			return true; // To change body of implemented methods use File | Settings
										// | File Templates.
		}

		protected LastTargetContact parametersFor(NetworkParticipant ownship,
				NetworkParticipant target, ScenarioType scenario, EnvironmentType environment,
				long time)
		{
			LastTargetContact res = new LastTargetContact();
			if (myParams != null)
				res._myList.addAll(myParams);
			return res;
		}

		protected WorldDistance calculateRP(NetworkParticipant ownship, NetworkParticipant target,
				ScenarioType scenario, EnvironmentType environment, long time,
				LastTargetContact params)
		{
			return new WorldDistance(_rp_m, WorldDistance.METRES);
		}

		protected WorldDistance calculateMaxRange(WorldDistance RP, double VDRa)
		{
			return new WorldDistance(_max_m, WorldDistance.METRES);
		}

		protected WorldDistance calculateRI(WorldDistance RP, double VDRa)
		{
			return new WorldDistance(_ri_m, WorldDistance.METRES);
		}

		protected WorldDistance calculateCR(WorldDistance RP, double VDRa)
		{
			return new WorldDistance(_cr_m, WorldDistance.METRES);
		}

		protected WorldDistance calculateIR(WorldDistance RP, double VDRa)
		{
			return new WorldDistance(_ir_m, WorldDistance.METRES);
		}

	}

	// //////////////////////////////////////////////////////////
	// TESTING CODE
	// //////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class LookupSensorTest extends SupportTesting
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public LookupSensorTest(String val)
		{
			super(val);
		}

		private static TestSensor getTestSensor(double rp_m, double ri_m, double cr_m,
				double ir_m, double max_m)
		{
			return new TestSensor(0.05, 1000, 1.05, 0.8, new Duration(20, Duration.SECONDS),
					0.2, new Duration(30, Duration.SECONDS), rp_m, ri_m, cr_m, ir_m, max_m);
		}

		public void testClearingOldDetections()
		{
			// create the sensors
			OpticLookupSensor os = OpticLookupSensor.OpticLookupTest.getTestOpticSensor();
			os.setTimeBetweenDetectionOpportunities(5000);
			RadarLookupSensor rs = RadarLookupSensor.RadarLookupTest.getTestRadarSensor(4000,
					3100, 2500, 1900, 4700, 600);
			rs.setTimeBetweenDetectionOpportunities(8000);

			// and the participants
			Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			Helo alpha = new Helo(12);
			alpha.setName("Alpha");
			alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
					Category.Type.HELO));
			WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			statA.getLocation().setDepth(-900);
			alpha.setStatus(statA);
			Surface bravo = new Surface(11);
			Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			bravo.setName("Bravo");
			bravo.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE,
					Category.Type.FISHING_VESSEL));
			WorldLocation originB = SupportTesting.createLocation(5000, 5000);
			statB.setLocation(originB);
			statB.getLocation().setDepth(-20);
			bravo.setStatus(statB);
			Surface charlie = new Surface(22);
			Status statC = new Status(12, 0);
			statC.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			charlie.setName("charlie");
			charlie.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE,
					Category.Type.FISHING_VESSEL));
			WorldLocation originC = SupportTesting.createLocation(5050, 5000);
			statC.setLocation(originC);
			statC.getLocation().setDepth(-20);
			charlie.setStatus(statC);

			SensorList sl = new SensorList();
			sl.add(os);
			sl.add(rs);

			alpha.setSensorFit(sl);

			// lastly the scenario
			CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);
			scenario.addParticipant(charlie.getId(), charlie);

			scenario.setScenarioStepTime(1000);

			// create our dummy environment object
			OpticLookupSensor.OpticLookupTest.MyEnvironment env = new OpticLookupSensor.OpticLookupTest.MyEnvironment();

			// ok. create the empty detectionList
			DetectionList dets = new DetectionList();

			// make env sensor friendly
			env.setSeaState(1);

			long curTime = 1000;

			// do we get anything?
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that list still empty
			assertEquals("Obtained first detections", dets.size(), 2);

			// right, get the detections
			DetectionEvent d1 = (DetectionEvent) dets.elementAt(0);
			DetectionEvent d2 = (DetectionEvent) dets.elementAt(1);

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we still have the old detections
			assertTrue("still have last contact", dets.contains(d1));
			assertTrue("still have last contact", dets.contains(d2));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we still have the old detections
			assertTrue("still have last contact", dets.contains(d1));
			assertTrue("still have last contact", dets.contains(d2));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we still have the old detections
			assertTrue("still have last contact", dets.contains(d1));
			assertTrue("still have last contact", dets.contains(d2));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we still have the old detections
			assertTrue("still have last contact", dets.contains(d1));
			assertTrue("still have last contact", dets.contains(d2));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 1000);

			// check that we have removed the old detections
			assertFalse("should have ditched last contact", dets.contains(d1));
			assertFalse("should have ditched last contact", dets.contains(d2));
			d1 = (DetectionEvent) dets.elementAt(0);
			d2 = (DetectionEvent) dets.elementAt(1);

			// check that we still have the old detections
			assertTrue("still have last contact", dets.contains(d1));
			assertTrue("still have last contact", dets.contains(d2));

			// check that we have new contacts
			assertEquals("have new contacts", 2, dets.size());

			// change range so that we know we won't get detection
			alpha.getStatus().setLocation(SupportTesting.createLocation(90000, 90000));

			// keep moving forward,
			sl.detects(env, dets, alpha, scenario, curTime += 3000);

			// check that we have removed the old detections and that we still have
			// the old detections
			assertTrue("still have last contact", dets.contains(d1));
			assertTrue("still have last contact", dets.contains(d2));
			assertEquals("should have kept contacts, havne't done next scan yet", 2, dets
					.size());

			// keep moving forward,
			curTime += 3000;
			sl.detects(env, dets, alpha, scenario, curTime += 3000);

			// right, passed time for next scan. check that old detections removed,
			// and no new ones added.
			assertFalse("still have last contact", dets.contains(d1));
			assertFalse("still have last contact", dets.contains(d2));
			assertEquals("should have lost contacts", 0, dets.size());

		}

		public final void testFirst()
		{
			TestSensor ts = getTestSensor(4000, 3100, 2500, 1900, 4700);
			long time = 1000;
			Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			Helo alpha = new Helo(12);
			alpha.setName("Alpha");
			WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			alpha.setStatus(statA);
			Surface bravo = new Surface(11);
			Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			bravo.setName("Bravo");
			WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			bravo.setStatus(statB);

			alpha.addSensor(ts);
			CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);

			EnvironmentType env = new SimpleEnvironment(1, 1, 1);

			Vector<Integer> newParams = new Vector<Integer>();
			newParams.add(new Integer(12));
			TestSensor.myParams = newParams;

			DetectionList res = new DetectionList();
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("null detections returned", 0, res.size());

			// ok. move the targets a little closer
			statB.setLocation(SupportTesting.createLocation(800, 800));

			// just do a check that no returns are produced if it's not time f
			// for another scan
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("zero detections returned (because it's not time yet)", 0, res.size());

			time += 1000;
			ts.detects(env, res, alpha, scenario, time);

			// ok, now move forward and try again
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// and move closer still!!
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// do another check for when we haven't moved forward enough
			time += 100;
			ts.detects(env, res, alpha, scenario, time);
			assertEquals(
					"old detections should have been returned (because it's not time yet)", 1, res
							.size());

			// now move forward in time though
			time += 1000;

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// time should be elapsed by now!!!
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, alpha, scenario, time);
			time += 9000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			// INCREASE THE RANGE AGAIN!
			statB.setLocation(SupportTesting.createLocation(900, 900));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			// AND AGAIN
			statB.setLocation(SupportTesting.createLocation(1200, 1200));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(bravo).getDetectionState());

			// AND YET AGAIN (out of range!)
			statB.setLocation(SupportTesting.createLocation(5200, 5200));
			ts.detects(env, res, alpha, scenario, time);
			time += 5000;
			assertEquals("no detections returned (out of range)", 0, res.size());

			// Let's go into range again!
			statB.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertEquals("detections returned", 1, res.size());
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

		}

		public void testMultipleContacts()
		{

			TestSensor ts = getTestSensor(4000, 3100, 2500, 1900, 4700);
			long time = 1000;
			Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			Helo test2alpha = new Helo(12);
			test2alpha.setName("Alpha");
			WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			test2alpha.setStatus(statA);

			Surface test2bravo = new Surface(11);
			Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			test2bravo.setName("Bravo");
			WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			test2bravo.setStatus(statB);

			Surface test2charlie = new Surface(21);
			Status statC = new Status(12, 0);
			statC.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			test2charlie.setName("Charlie");
			WorldLocation originC = SupportTesting.createLocation(4000, 4000);
			statC.setLocation(originC);
			test2charlie.setStatus(statC);

			test2alpha.addSensor(ts);
			CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(test2alpha.getId(), test2alpha);
			scenario.addParticipant(test2bravo.getId(), test2bravo);
			scenario.addParticipant(test2charlie.getId(), test2charlie);

			EnvironmentType env = new SimpleEnvironment(1, 1, 1);

			Vector<Integer> newParams = new Vector<Integer>();
			newParams.add(new Integer(12));
			TestSensor.myParams = newParams;

			DetectionList res = new DetectionList();
			ts.detects(env, res, test2alpha, scenario, time);
			assertEquals("zero detections returned (because it's not time yet)", 0, res.size());
			time += 1000;

			// ok. move the targets a little closer
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());

			// and move closer still!!
			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(5800, 5800));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(4800, 4800));
			statC.setLocation(SupportTesting.createLocation(500, 500));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(400, 400));
			statC.setLocation(SupportTesting.createLocation(200, 200));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.CLASSIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(400, 400));
			statC.setLocation(SupportTesting.createLocation(200, 200));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(2400, 2400));
			statC.setLocation(SupportTesting.createLocation(2200, 2200));
			ts.detects(env, res, test2alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("two detections produced", 2, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(test2bravo).getDetectionState());
			assertEquals("check detected state", DetectionEvent.IDENTIFIED,
					ts._pastContacts.get(test2charlie).getDetectionState());

		}

		public void testThresholds()
		{
			final int RP__M = 4000;
			final int ri_m = 3100;
			final int cr_m = 2500;
			final int ir_m = 1900;
			final int max_m = 4700;
			TestSensor ts = getTestSensor(RP__M, ri_m, cr_m, ir_m, max_m);
			Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			statA.setCourse(28);
			final Helo alpha = new Helo(12);
			alpha.setCategory(new Category(Category.Force.BLUE, Category.Environment.AIRBORNE,
					Category.Type.HELO));
			alpha.setName("Alpha");
			WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			alpha.setStatus(statA);
			final Surface bravo = new Surface(11);
			bravo.setCategory(new Category(Category.Force.BLUE, Category.Environment.SURFACE,
					Category.Type.FRIGATE));
			Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			statB.setCourse(225);
			bravo.setName("Bravo");
			WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			bravo.setStatus(statB);

			alpha.setMovementChars(HeloMovementCharacteristics.getSampleChars());
			bravo.setMovementChars(SurfaceMovementCharacteristics.getSampleChars());

			alpha.addSensor(ts);
			CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);

			scenario.setStepTime(5000);
			scenario.setScenarioStepTime(1000);

			alpha.addParticipantDetectedListener(new ParticipantDetectedListener()
			{

				int lastState = DetectionEvent.UNDETECTED;

				public void newDetections(DetectionList detections)
				{
					if (detections.size() > 0)
					{
						DetectionEvent de = detections.getDetection(0);

						if (de.getDetectionState() != lastState)
						{
							lastState = de.getDetectionState();
							WorldDistance wd = LookupSensor.calculateSlantRangeFor(alpha.getStatus()
									.getLocation(), bravo.getStatus().getLocation());
							checkDetectionThreshold(de.getDetectionState(), wd
									.getValueIn(WorldDistance.METRES));
						}
					}
				}

				public void restart(ScenarioType scenario)
				{
				}

				public void checkDetectionThreshold(int newState, double range_m)
				{
					switch (newState)
					{
					case (DetectionEvent.DETECTED):
						assertEquals("detection range", range_m, ri_m, 30);
						break;
					case (DetectionEvent.CLASSIFIED):
						assertEquals("detection range", range_m, cr_m, 30);
						break;
					case (DetectionEvent.IDENTIFIED):
						assertEquals("detection range", range_m, ir_m, 30);
						break;
					case (DetectionEvent.UNDETECTED):
						assertEquals("detection range", range_m, max_m, 30);
						break;
					}

				}

			});

			ASSET.Scenario.Observers.Recording.DebriefReplayObserver dr = new DebriefReplayObserver(
					"test_reports", null, true, "record file", true);
			dr.setup(scenario);

			for (int i = 0; i < 4000; i++)
			{
				if (scenario.getTime() == 690000)
					System.out.println("here");

				scenario.step();
			}

			dr.tearDown(scenario);

		}

		public final void testPersistentData()
		{
			TestSensor ts = getTestSensor(4000, 3100, 2500, 1900, 4700);
			long time = 1000;
			Status statA = new Status(12, 0);
			statA.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			Helo alpha = new Helo(12);
			alpha.setName("Alpha");
			WorldLocation originA = SupportTesting.createLocation(0, 0);
			statA.setLocation(originA);
			alpha.setStatus(statA);
			Surface bravo = new Surface(11);
			Status statB = new Status(12, 0);
			statB.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			bravo.setName("Bravo");
			WorldLocation originB = SupportTesting.createLocation(6000, 6000);
			statB.setLocation(originB);
			bravo.setStatus(statB);

			alpha.addSensor(ts);
			CoreScenario scenario = new CoreScenario();
			scenario.addParticipant(alpha.getId(), alpha);
			scenario.addParticipant(bravo.getId(), bravo);

			EnvironmentType env = new SimpleEnvironment(1, 1, 1);

			Vector<Integer> newParams = new Vector<Integer>();
			newParams.add(new Integer(12));
			TestSensor.myParams = newParams;

			DetectionList res = new DetectionList();
			ts.detects(env, res, alpha, scenario, time);
			assertEquals("zero detections returned (because it's not time yet)", 0, res.size());
			time += 1000;

			// ok. move the targets a little closer
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// and move closer still!!
			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			statB.setLocation(SupportTesting.createLocation(800, 800));
			ts.detects(env, res, alpha, scenario, time);
			time += 10000;
			assertNotNull("detections returned", res);
			assertEquals("only one detection produced", 1, res.size());
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

			// change the detection parameters
			newParams.add(new Integer(32));
			TestSensor.myParams = newParams;
			ts._ri_m = 3300;

			// find out what the current RI is.
			LastTargetContact tc = ts._pastContacts.get(bravo);
			WorldDistance lastRI = tc.getRI();
			ts.detects(env, res, alpha, scenario, time);
			time += 1000;
			// check that the RI has changed
			tc = ts._pastContacts.get(bravo);
			WorldDistance newRI = tc.getRI();
			
			boolean isSame = newRI.getValueIn(WorldDistance.METRES) == lastRI
					.getValueIn(WorldDistance.METRES);
			assertTrue("RI has changed", !isSame);
			assertNotNull("detections returned", res);
			assertEquals("check detected state", DetectionEvent.DETECTED,
					ts._pastContacts.get(bravo).getDetectionState());

		}

		public void testTutorialScenario()
		{
			CoreScenario cs = new CoreScenario();
			try
			{
				String fName = "../org.mwc.asset.core.feature/root_installs/Workspace/AssetData/Samples/legacy/lookup_tutorial_scenario.xml";
				File tFile = new File(fName);
				assertTrue("sceanrio not found", tFile.exists());
				java.io.FileInputStream fis = new java.io.FileInputStream(fName);
				ASSET.Util.XML.ASSETReaderWriter.importThis(cs, fName, fis);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace(); // To change body of catch statement use File |
															// Settings | File Templates.
			}

			// so, do we have our scenario?
			assertNotNull("scenario loaded", cs);

			// hmm, have all the vessels loaded?
			assertEquals("all parts loaded", 4, cs.getListOfParticipants().length, 0);

			// run through for a few hours, recording to file
			DebriefReplayObserver dro = new DebriefReplayObserver("./test_reports", "test_tutorial.rep",
					true, "test observer", true);
			dro.setup(cs);

			ASSET.Scenario.Observers.StopOnElapsedObserver to = new StopOnElapsedObserver(0, 3, 0, 0,
					"Test time observer", true);
			to.setup(cs);

			while (!to.hasStopped())
			{
				cs.step();
			}

			to.tearDown(cs);
			dro.tearDown(cs);

		}

		public void testForceProtection()
		{
			Status start = new Status(12, 0);
			WorldLocation origin = SupportTesting.createLocation(0, 0);
			origin.setDepth(-500);
			start.setLocation(origin);
			start.setSpeed(new WorldSpeed(140, WorldSpeed.Kts));

		}
	}

	// ////////////////////////////////////////////////
	// class to hold a series of values for a single named type
	// ////////////////////////////////////////////////
	public static class NamedList
	{
		final protected  String _myType;

		final protected Vector<Double> _myValues;
		
		public NamedList(String type, Vector<Double> values)
		{
			_myType = type;
			_myValues = values;
		}
		
		public NamedList(String type, double[] vals)
		{
			_myType = type;
			_myValues = new Vector<Double>(0,1);
			for (int i = 0; i < vals.length; i++)
			{
				Double thisD = new Double(vals[i]);
				_myValues.add(thisD);
			}
		}
		public String getName()
		{
			return _myType;
		}
		public Collection<Double> getValues()
		{
			return _myValues;
		}
	}

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

}