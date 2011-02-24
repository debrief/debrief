package ASSET.Models.Sensor.Cookie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import ASSET.NetworkParticipant;
import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Participants.Category;
import ASSET.Participants.DemandedSensorStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Participants.Category.Environment;
import ASSET.Participants.Category.Force;
import ASSET.Participants.Category.Type;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * Extension of the simple cookie cutter model that allows different cookie
 * cutter ranges to be specified for different target types
 * 
 * @author ianmayo
 * 
 */
public class TypedCookieSensor extends CoreSensor
{

	private Vector<TypedRangeDoublet> _rangeDoublets;

	private HashMap<TypedRangeDoublet, DetectionList> _typedDetections;

	private Integer _detectionState = DetectionEvent.DETECTED;
	
	
	/** optional sensor medium
	 * 
	 */
	private int _medium;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TypedCookieSensor(int id, Vector<TypedRangeDoublet> rangeDoublets,
			Integer detectionState)
	{
		super(id, 0, "Plain Cookie");

		_rangeDoublets = rangeDoublets;
		_typedDetections = new HashMap<TypedRangeDoublet, DetectionList>();
		_detectionState = detectionState;

	}

	public TypedCookieSensor(int id, Vector<TypedRangeDoublet> rangeDoublets)
	{
		this(id, rangeDoublets, DetectionEvent.DETECTED);
	}


	public int getMedium()
	{
		return _medium;
	}

	public void setMedium(int medium)
	{
		_medium = medium;
	}
	
	public Vector<TypedRangeDoublet> getRanges()
	{
		return _rangeDoublets;
	}

	@Override
	protected boolean canDetectThisType(NetworkParticipant ownship,
			ParticipantType other, EnvironmentType env)
	{
		return true;
	}

	protected void addRangeDoublet(TypedRangeDoublet myRangeDoublet)
	{
		if (_rangeDoublets == null)
			_rangeDoublets = new Vector<TypedRangeDoublet>();

		_rangeDoublets.add(myRangeDoublet);
	}

	@Override
	public boolean canIdentifyTarget()
	{
		return true;
	}

	@Override
	protected DetectionEvent detectThis(EnvironmentType environment,
			ParticipantType host, ParticipantType target, long time,
			ScenarioType scenario)
	{
		DetectionEvent res = null;

		// right, what's the distance?
		WorldDistance range = null; // defer calculation until we need it
		WorldVector sep = null;

		// loop through our detection types
		for (Iterator<TypedRangeDoublet> iterator = _rangeDoublets.iterator(); iterator
				.hasNext();)
		{
			boolean detected = false;

			TypedRangeDoublet doublet = iterator.next();
			if (doublet.mayDetect(target.getCategory()))
			{
				// ok, it's worth sorting out the range
				if (range == null)
				{
					sep = target.getStatus().getLocation()
							.subtract(host.getStatus().getLocation());
					range = new WorldDistance(sep.getRange(), WorldDistance.DEGS);
				}

				if (doublet.canDetect(range))
				{

					detected = true;

					double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(sep
							.getBearing());

					// cool, in contact. write it up.
					res = new DetectionEvent(time, host.getId(), host.getStatus()
							.getLocation(), this, range, range, new Float(brgDegs),
							new Float(super.relativeBearing(host.getStatus().getCourse(),
									brgDegs)), new Float(1), target.getCategory(), new Float(
									target.getStatus().getSpeed().getValueIn(WorldSpeed.Kts)),
							new Float(target.getStatus().getCourse()), target);

					res.setDetectionState(_detectionState);

					// store this detection
					storeThisDetection(doublet, res);
				}
			}

			// ok, just see if there are any pSupport listners
			if (_pSupport != null)
			{
				if (_pSupport.hasListeners(SENSOR_COMPONENT_EVENT))
				{
					String typeList = "";
					Vector<String> types = doublet.getMyTypes();
					if (types != null)
					{
						for (Iterator<String> iterator2 = types.iterator(); iterator2
								.hasNext();)
						{
							String string = iterator2.next();
							typeList += string + ",";
						}
					}

					if (typeList.length() == 0)
						typeList = "Unset";

					// create the event
					TypedSensorComponentsEvent sev = new TypedSensorComponentsEvent(time,
							range, doublet.getRange(), typeList, detected, target.getName());

					// and fire it!
					_pSupport.firePropertyChange(SENSOR_COMPONENT_EVENT, null, sev);
				}
			}

		}

		return res;
	}

	public DetectionList getDetectionsFor(TypedRangeDoublet doublet)
	{
		return _typedDetections.get(doublet);
	}

	/**
	 * store this detection in our typed collections
	 * 
	 * @param doublet
	 * @param detection
	 */
	private void storeThisDetection(TypedRangeDoublet doublet,
			DetectionEvent detection)
	{
		DetectionList dl = _typedDetections.get(doublet);
		if (dl == null)
		{
			dl = new DetectionList();
			_typedDetections.put(doublet, dl);
		}

		dl.add(detection);
	}

	public WorldDistance getEstimatedRange()
	{
		return null;
	}

	public void update(DemandedStatus myDemandedStatus, Status myStatus,
			long newTime)
	{
		Vector<DemandedSensorStatus> states = myDemandedStatus.getSensorStates();
		if (states != null)
		{
			Iterator<DemandedSensorStatus> iter = states.iterator();
			while (iter.hasNext())
			{
				DemandedSensorStatus ds = (DemandedSensorStatus) iter.next();
				if (ds.getMedium() == getMedium())
				{
					if (ds.getSwitchOn())
						this.setWorking(true);
					else
						this.setWorking(false);
				}
			}
		}
	}

	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new TypedCookieInfo(this);
		return _myEditor;
	}

	public boolean hasEditor()
	{
		return true;
	}

	public String getVersion()
	{
		return "$Date: 2010-01-19 15:34:14 +0100  $";
	}

	// //////////////////////////////////////////////////
	// the editor object
	// //////////////////////////////////////////////////
	static public class TypedCookieInfo extends BaseSensorInfo
	{
		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public TypedCookieInfo(final TypedCookieSensor data)
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
				final java.beans.PropertyDescriptor[] res =
				{ prop("Name", "the name of this sensor"),
						prop("DetectionRange", "detection range of this sensor") };
				return res;
			}
			catch (java.beans.IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

	}

	/**
	 * store the detection distance for the specified category types
	 * 
	 * @author ianmayo
	 * 
	 */
	public static class TypedRangeDoublet
	{
		/**
		 * the types this range applies to
		 * 
		 */
		private Vector<String> _types;

		/**
		 * this detection range
		 * 
		 */
		private WorldDistance _range;

		public WorldDistance getRange()
		{
			return _range;
		}

		public void setRange(WorldDistance range)
		{
			_range = range;
		}

		public TypedRangeDoublet(Vector<String> types, WorldDistance range)
		{
			_types = types;
			_range = range;
		}

		public Vector<String> getMyTypes()
		{
			return _types;
		}

		/**
		 * see if we can detect the target
		 * 
		 * @param targetCat
		 *          the type of target we're looking at
		 * @param targetRange
		 *          the range to the target
		 * @return
		 */
		public boolean canDetect(WorldDistance targetRange)
		{
			boolean res = false;

			// right, we can detect it - see if we are in range
			if (targetRange.lessThan(_range))
				res = true;
			else
				res = false;

			return res;
		}

		/**
		 * see if we can detect the target
		 * 
		 * @param targetCat
		 *          the type of target we're looking at
		 * @param targetRange
		 *          the range to the target
		 * @return
		 */
		public boolean mayDetect(Category targetCat)
		{
			boolean res = false;

			// do we have our own types object
			if (_types == null)
			{
				// nope, in that case, use this as a default and just use the range item
				res = true;
			}
			else
			{

				// loop through our types
				for (Iterator<String> iterator = _types.iterator(); iterator.hasNext();)
				{
					String thisType = iterator.next();
					// right, does this match
					if (targetCat.getEnvironment().equals(thisType))
						res = true;
					if (targetCat.getForce().equals(thisType))
						res = true;
					if (targetCat.getType().equals(thisType))
						res = true;
				}
			}

			return res;
		}
	}

	// //////////////////////////////////////////////////
	// embedded class for event fired after each detection step
	// //////////////////////////////////////////////////
	public static class TypedSensorComponentsEvent
	{
		// //////////////////////////////////////////////////
		// member objects
		// //////////////////////////////////////////////////
		final private String _tgtName;
		final private long _time;
		private final WorldDistance _detRange;
		private final WorldDistance _tgtRange;
		private final String _typeCriteria;
		private final boolean _detected;

		/**
		 * constructor
		 */
		public TypedSensorComponentsEvent(long time, WorldDistance detRange,
				WorldDistance tgtRange, String typeCriteria, boolean detected,
				String tgtName)
		{

			_detRange = detRange;
			_tgtRange = tgtRange;
			_typeCriteria = typeCriteria;
			_tgtName = tgtName;
			_time = time;
			_detected = detected;
		}

		public long getTime()
		{
			return _time;
		}

		public String getTgtName()
		{
			return _tgtName;
		}

		public WorldDistance getDetRange()
		{
			return _detRange;
		}

		public WorldDistance getTgtRange()
		{
			return _tgtRange;
		}

		public String getTypeCriteria()
		{
			return _typeCriteria;
		}

		public String toString()
		{
			String res;

			res = _typeCriteria + " det:"
					+ (int) _detRange.getValueIn(WorldDistance.YARDS) + "actual:"
					+ (int) _tgtRange.getValueIn(WorldDistance.YARDS) + " " + _tgtName;

			return res;
		}

		public boolean getDetected()
		{
			return _detected;
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class PlainCookieTest extends SupportTesting.EditableTesting
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public PlainCookieTest(final String val)
		{
			super(val);
		}

		/**
		 * get an object which we can test
		 * 
		 * @return Editable object which we can check the properties for
		 */
		public Editable getEditable()
		{
			return new PlainCookieSensor(1, new WorldDistance(12, WorldDistance.DEGS));
		}

		public void testRangeDoublet()
		{
			Vector<String> listOne = new Vector<String>();
			listOne.add(Category.Force.BLUE);
			listOne.add(Category.Force.GREEN);
			listOne.add(Category.Environment.AIRBORNE);
			listOne.add(Category.Type.FISHING_VESSEL);

			WorldDistance dist = new WorldDistance(2, WorldDistance.NM);

			TypedRangeDoublet td = new TypedRangeDoublet(listOne, dist);
			WorldDistance tRange = new WorldDistance(1, WorldDistance.NM);
			WorldDistance longRange = new WorldDistance(3, WorldDistance.NM);

			Category tCat = new Category(Force.RED, Environment.AIRBORNE, Type.HELO);
			assertTrue("Matches item", td.mayDetect(tCat));
			assertFalse("too far", td.canDetect(longRange));

			tCat = new Category(Force.RED, Environment.SURFACE, Type.HELO);
			assertFalse("doesn't match item", td.mayDetect(tCat));

			tCat = new Category(Force.RED, Environment.SURFACE, Type.FISHING_VESSEL);
			assertTrue("Matches item", td.mayDetect(tCat));
			assertTrue("Matches item", td.canDetect(tRange));

			tCat = new Category(Force.BLUE, Environment.SURFACE, Type.HELO);
			assertTrue("doesn't match item", td.mayDetect(tCat));
		}

		public void testPlainSensor()
		{

			// reset the earth model
			WorldLocation
					.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

			// ok, create the sensor
			PlainCookieSensor ps = new PlainCookieSensor(12, new WorldDistance(12,
					WorldDistance.DEGS));

			// now the objects
			WorldLocation l1 = new WorldLocation(0, 0, 0);
			WorldLocation l2 = l1.add(new WorldVector(0, 10, 0));
			WorldLocation l3 = l1.add(new WorldVector(0, 13, 0));

			ASSET.Models.Vessels.Surface su1 = new ASSET.Models.Vessels.Surface(12);
			Status theStat1 = new Status(12, 0);
			theStat1.setLocation(l1);
			theStat1.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			su1.setStatus(theStat1);
			SensorList sl = new SensorList();
			sl.add(ps);

			ASSET.Models.Vessels.Surface su2 = new ASSET.Models.Vessels.Surface(14);
			Status theStat2 = new Status(12, 0);
			theStat2.setLocation(l2);
			theStat2.setSpeed(new WorldSpeed(12, WorldSpeed.Kts));
			su2.setStatus(theStat2);

			EnvironmentType env = new SimpleEnvironment(1, 1, 1);

			DetectionEvent de = ps.detectThis(env, su1, su2, 1000, null);

			assertNotNull("Made a detection", de);

			su2.getStatus().setLocation(l3);

			de = ps.detectThis(env, su1, su2, 1000, null);

			assertNull("Not made a detection", de);

		}
	}

}
