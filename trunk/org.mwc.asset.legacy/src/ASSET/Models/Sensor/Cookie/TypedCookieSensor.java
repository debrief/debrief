package ASSET.Models.Sensor.Cookie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import ASSET.ParticipantType;
import ASSET.ScenarioType;
import ASSET.Models.Detection.DetectionEvent;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Environment.EnvironmentType;
import ASSET.Models.Environment.SimpleEnvironment;
import ASSET.Models.Sensor.CoreSensor;
import ASSET.Models.Sensor.SensorList;
import ASSET.Participants.Category;
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
 * Extension of the simple cookie cutter model that allows different cookie cutter ranges to
 * be specified for different target types
 * 
 * @author ianmayo
 * 
 */
public class TypedCookieSensor extends CoreSensor
{

	private Vector<TypedRangeDoublet> _rangeDoublets;
	
	private HashMap<TypedRangeDoublet, DetectionList> _typedDetections;

	private Integer _detectionState = DetectionEvent.DETECTED;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public TypedCookieSensor(int id, Vector<TypedRangeDoublet> rangeDoublets, Integer detectionState)
	{
		super(id, 0, "Plain Cookie");

		_rangeDoublets = rangeDoublets;
		_typedDetections = new HashMap<TypedRangeDoublet, DetectionList>();
		_detectionState  = detectionState;
		
	}

	public TypedCookieSensor(int id, Vector<TypedRangeDoublet> rangeDoublets)
	{
		this(id, rangeDoublets, DetectionEvent.DETECTED);
	}
	
	public Vector<TypedRangeDoublet> getRanges()
	{
		return _rangeDoublets;
	}

	@Override
	protected boolean canDetectThisType(ParticipantType ownship,
			ParticipantType other, EnvironmentType env)
	{
		return true;
	}

	protected void addRangeDoublet(TypedRangeDoublet myRangeDoublet)
	{
		if(_rangeDoublets == null)
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
		WorldVector sep = target.getStatus().getLocation().subtract(
				host.getStatus().getLocation());
		WorldDistance range = new WorldDistance(sep.getRange(), WorldDistance.DEGS);

		// loop through our detection types
		for (Iterator<TypedRangeDoublet> iterator = _rangeDoublets.iterator(); iterator
				.hasNext();)
		{
			TypedRangeDoublet doublet = iterator.next();
			if (doublet.canDetect(target.getCategory(), range))
			{
				double brgDegs = MWC.Algorithms.Conversions.Rads2Degs(sep.getBearing());

				// cool, in contact. write it up.
				res = new DetectionEvent(time, host.getId(), host.getStatus()
						.getLocation(), this, range, range, new Float(brgDegs), new Float(
						super.relativeBearing(host.getStatus().getCourse(), brgDegs)),
						new Float(1), target.getCategory(), new Float(target.getStatus()
								.getSpeed().getValueIn(WorldSpeed.Kts)), new Float(target
								.getStatus().getCourse()), target);
				
				res.setDetectionState(_detectionState);
				
				// store this detection
				storeThisDetection(doublet, res);

			}

		}

		return res;
	}

	
	public DetectionList getDetectionsFor(TypedRangeDoublet doublet)
	{
		return _typedDetections.get(doublet);
	}
	
	/** store this detection in our typed collections
	 * 
	 * @param doublet
	 * @param detection
	 */
	private void storeThisDetection(TypedRangeDoublet doublet, DetectionEvent detection)
	{
		DetectionList dl = _typedDetections.get(doublet);
		if(dl == null)
		{
			dl = new DetectionList();
			_typedDetections.put(doublet, dl);
		}
		
		dl.add(detection);
	}

	@Override
	public WorldDistance getEstimatedRange()
	{
		return null;
	}

	@Override
	public int getMedium()
	{
		return 0;
	}

	@Override
	public void update(DemandedStatus myDemandedStatus, Status myStatus,
			long newTime)
	{
		// don't bother, nothing to do here
	}

	@Override
	public EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new TypedCookieInfo(this);
		return _myEditor;
	}

	@Override
	public boolean hasEditor()
	{
		return true;
	}

	@Override
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
		public boolean canDetect(Category targetCat, WorldDistance targetRange)
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

			if (res)
			{
				// right, we can detect it - see if we are in range
				if (targetRange.lessThan(_range))
					res = true;
				else
					res = false;
			}

			return res;
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
			assertTrue("Matches item", td.canDetect(tCat, tRange));
			assertFalse("too far", td.canDetect(tCat, longRange));
			
			tCat = new Category(Force.RED, Environment.SURFACE, Type.HELO);
			assertFalse("doesn't match item", td.canDetect(tCat, tRange));
			
			tCat = new Category(Force.RED, Environment.SURFACE, Type.FISHING_VESSEL);
			assertTrue("Matches item", td.canDetect(tCat, tRange));
			
			tCat = new Category(Force.BLUE, Environment.SURFACE, Type.HELO);
			assertTrue("doesn't match item", td.canDetect(tCat, tRange));
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
