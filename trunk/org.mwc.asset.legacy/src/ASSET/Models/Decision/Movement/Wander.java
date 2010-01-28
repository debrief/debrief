package ASSET.Models.Decision.Movement;

import ASSET.Models.Decision.CoreDecision;
import ASSET.Models.Decision.Responses.Response;
import ASSET.Models.Detection.DetectionList;
import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;
import ASSET.Scenario.ScenarioActivityMonitor;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * Title: Wander Description: Wander around an origin, passing up to an
 * indicated range from it Copyright: Copyright (c) 2001 Company:
 * 
 * @author Ian Mayo
 * @version 1.0
 */

public class Wander extends CoreDecision implements Response,
		java.io.Serializable
{

	/***********************************************************************
	 * member variables
	 ***********************************************************************/

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * when we are still in our wander area
	 */
	protected static final String IN_AREA = "Still in area";

	/**
	 * when we have left the area, and setting a new demanded course
	 */
	protected static final String NEW_COURSE = "New dem course assigned";

	/**
	 * when we have left the area, and setting a new demanded course
	 */
	protected static final String MANOEUVERING_TO_NEW_COURSE = "Manoeuvering to new course";

	/**
	 * when we have left the area, and setting a new demanded course
	 */
	protected static final String OUT_OF_AREA = "Outside area";

	/**
	 * when we are outside the area, but heading back in on steady course
	 */
	protected static String HEADING_FOR_AREA = "Returning to area";

	/**
	 * the origin of our wandeirng
	 */
	private MWC.GenericData.WorldLocation _origin;

	/**
	 * the range we wander out to (degs)
	 */
	private WorldDistance _range;

	/**
	 * a local copy of our editable object
	 */
	protected MWC.GUI.Editable.EditorType _myEditor = null;

	/**
	 * the speed we wander at (kts)
	 */
	private WorldSpeed _speed;

	/**
	 * the Height we wander at (m)
	 */
	private WorldDistance _Height;

	/**
	 * an offset to randomly apply to the course to the centre of the area - to
	 * give the wandering effect
	 */
	final double OFFSET_RANGE = 120;

	/**
	 * ********************************************************************
	 * constructor
	 * *********************************************************************
	 */

	public Wander(final MWC.GenericData.WorldLocation origin,
			final WorldDistance range_yds)
	{
		this("unset");
		setOrigin(origin);
		setRange(range_yds);
	}

	public Wander(String myName)
	{
		super(myName);
	}

	/**
	 * @param conditionResult
	 *          the object returned from the condition object
	 * @param status
	 *          the current status of the participant
	 * @param detections
	 *          the current set of detections
	 * @param monitor
	 *          the monitor object listening out for significant activity
	 * @param time
	 *          the current time step
	 * @return the DemandedStatus for this vessel
	 */
	public final DemandedStatus direct(final Object conditionResult,
			final Status status, final DemandedStatus demStat,
			final DetectionList detections, final ScenarioActivityMonitor monitor,
			final long time)
	{

		SimpleDemandedStatus res;
		Double oldDemCourse = null;

		boolean changingCourse = false;

		String thisActivity = null;

		// do we have an existing simple demanded status?
		if (demStat != null)
		{
			// if we have an existing demanded status, we will continue with it -
			// there's no need to change it if
			// we don't need to
			if (demStat instanceof SimpleDemandedStatus)
			{
				SimpleDemandedStatus sds = (SimpleDemandedStatus) demStat;
				oldDemCourse = new Double(sds.getCourse());

				double courseError = sds.getCourse() - status.getCourse();
				if (courseError > 180)
					courseError -= 360;

				if (courseError < -180)
					courseError += 360;

				// is the error acceptable?
				if (Math.abs(courseError) > 1)
				{
					// yes, put us on course
					changingCourse = true;
				}

			}
		}

		// and did we find a value?
		if (oldDemCourse != null)
		{
			res = new SimpleDemandedStatus(time, (SimpleDemandedStatus) demStat);
		}
		else
		{
			res = new SimpleDemandedStatus(time, status);
		}

		// update the course and speed
		if (_Height != null)
			res.setHeight(_Height.getValueIn(WorldDistance.METRES));

		if (_speed != null)
			res.setSpeed(_speed.getValueIn(WorldSpeed.M_sec));

		// ok, find out if we have passed the limit
		boolean beyondLimit = isBeyondLimit(status);

		if (changingCourse)
		{
			// just continue, until we're on course
			if (oldDemCourse != null)
				res.setCourse(oldDemCourse.doubleValue());
			thisActivity += " " + Wander.OUT_OF_AREA + ":"
					+ Wander.MANOEUVERING_TO_NEW_COURSE;
		}
		else
		{
			if (beyondLimit)
			{
				// we're on course. find a new one
				thisActivity = Wander.OUT_OF_AREA;

				// ok, set the new course
				setNewCourse(status, oldDemCourse, res);
			}
			else
			{
				// no, continue in course
				res.setCourse(status.getCourse());

				thisActivity = Wander.IN_AREA;

			}
		}

		super.setLastActivity(thisActivity);

		return res;

	}

	/**
	 * function to determine if it's time to turn to the new course
	 * 
	 * @param current
	 *          the current status
	 * @return
	 */
	protected boolean isBeyondLimit(Status current)
	{

		// how far are we from the centre?
		final MWC.GenericData.WorldVector offset = _origin.subtract(current
				.getLocation());

		// have we passed our outer range?
		final boolean beyondLimit = offset.getRange() > _range
				.getValueIn(WorldDistance.DEGS);
		return beyondLimit;
	}

	/**
	 * ok, plot a new course because we're at the edge
	 * 
	 * @param status
	 *          our current status
	 * @param oldDemCourse
	 *          the old demanded course
	 * @param res
	 *          the demanded status object (to place our data into)
	 * @param origin
	 * @return the status message
	 */
	protected String setNewCourse(final Status status, Double oldDemCourse,
			SimpleDemandedStatus res)
	{
		return setNewCourse(status, oldDemCourse, res, _origin);
	}

	protected String setNewCourse(final Status status, Double oldDemCourse,
			SimpleDemandedStatus res, WorldLocation origin)
	{
		StringBuffer activity = new StringBuffer();

		// what's the range to the origin?
		final MWC.GenericData.WorldVector offset = origin.subtract(status
				.getLocation());

		// plot a course back to the centre
		double courseRequired = MWC.Algorithms.Conversions.Rads2Degs(offset
				.getBearing());

		// just double-check whether we are currently heading for the wander area,
		// but just taking a while to get there
		double courseError = Math.abs(courseRequired - status.getCourse());
		if (courseError < OFFSET_RANGE / 2)
		{
			// do we know our old course?
			if (oldDemCourse != null)
			{
				// hey, just stay as we are!!
				res.setCourse(oldDemCourse.doubleValue());
			}

			activity.append(":");
			activity.append(Wander.HEADING_FOR_AREA);
		}
		else
		{
			// add a slight offset to this demanded course (+/- 30 degs)
			courseRequired += (-OFFSET_RANGE / 2 + RandomGenerator.nextRandom()
					* OFFSET_RANGE);

			res.setCourse(courseRequired);
			activity.append(":");
			activity.append(Wander.NEW_COURSE);

		}

		return activity.toString();
	}

	public final ASSET.Participants.DemandedStatus decide(
			final ASSET.Participants.Status status,
			ASSET.Models.Movement.MovementCharacteristics chars,
			final DemandedStatus demStatus,
			final ASSET.Models.Detection.DetectionList detections,
			final ASSET.Scenario.ScenarioActivityMonitor monitor, final long time)
	{
		return direct(null, status, demStatus, detections, monitor, time);
	}

	/**
	 * reset this decision model
	 */
	public final void restart()
	{
		//
	}

	/**
	 * indicate to this model that its execution has been interrupted by another
	 * (prob higher priority) model
	 * 
	 * @param currentStatus
	 */
	public void interrupted(Status currentStatus)
	{
		// ignore.
	}

	/**
	 * the origin we wander about
	 */
	public final void setOrigin(final MWC.GenericData.WorldLocation origin)
	{
		_origin = origin;
	}

	/**
	 * the origin we wander about
	 */
	public final MWC.GenericData.WorldLocation getOrigin()
	{
		return _origin;
	}

	/**
	 * the range we wander out to (yds)
	 */
	public final void setRange(final WorldDistance range_yds)
	{
		_range = range_yds;
	}

	/**
	 * the range we wander out to
	 */
	public final WorldDistance getRange()
	{
		return _range;
	}

	/**
	 * the speed we wander at (kts)
	 */
	public final void setSpeed(final WorldSpeed kts)
	{
		_speed = kts;
	}

	/**
	 * the speed we wander at (kts)
	 */
	public final WorldSpeed getSpeed()
	{
		return _speed;
	}

	/**
	 * the Height we wander at
	 */
	public final void setHeight(final WorldDistance val)
	{
		_Height = val;
	}

	/**
	 * the Height we wander at
	 */
	public final WorldDistance getHeight()
	{
		return _Height;
	}

	// ////////////////////////////////////////////////////////////////////
	// editable data
	// ////////////////////////////////////////////////////////////////////
	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
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
	public MWC.GUI.Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new WanderInfo(this);

		return _myEditor;
	}

	// //////////////////////////////////////////////////////////
	// model support
	// //////////////////////////////////////////////////////////

	/**
	 * get the version details for this model.
	 * 
	 * <pre>
	 * $Log: Wander.java,v $
	 * Revision 1.2  2006/08/29 13:12:38  Ian.Mayo
	 * Debugging, tidying, fix getter for BottomRight corner
	 * Revision 1.1  2006/08/08 14:21:29  Ian.Mayo
	 * Second import
	 * Revision 1.1  2006/08/07 12:25:38  Ian.Mayo
	 * First versions
	 * Revision 1.17  2004/10/20 14:05:03  Ian.Mayo
	 * Optimisations to reduce String creation
	 * Revision 1.16  2004/09/02 13:17:30  Ian.Mayo
	 * Reflect CoreDecision handling the toString method
	 * Revision 1.15  2004/08/31 09:36:09  Ian.Mayo
	 * Rename inner static tests to match signature **Test to make automated testing more consistent
	 * <p/>
	 * Revision 1.14  2004/08/26 11:05:08  Ian.Mayo
	 * Refactor editable testing
	 * <p/>
	 * Revision 1.13  2004/08/26 11:01:43  Ian.Mayo
	 * Implement core editable property testing
	 * <p/>
	 * Revision 1.12  2004/08/26 09:40:47  Ian.Mayo
	 * Add speed property editor, add property editors for RectWander behaviour, add TL & BR setters for WorldArea
	 * <p/>
	 * Revision 1.11  2004/08/25 11:20:23  Ian.Mayo
	 * Remove main methods which just run junit tests
	 * <p/>
	 * Revision 1.10  2004/08/20 13:32:22  Ian.Mayo
	 * Implement inspection recommendations to overcome hidden parent objects, let CoreDecision handle the activity bits.
	 * <p/>
	 * Revision 1.9  2004/08/17 14:22:01  Ian.Mayo
	 * Refactor to introduce parent class capable of storing name & isActive flag
	 * <p/>
	 * Revision 1.8  2004/08/06 12:51:58  Ian.Mayo
	 * Include current status when firing interruption
	 * <p/>
	 * Revision 1.7  2004/08/06 11:14:20  Ian.Mayo
	 * Introduce interruptable behaviours, and recalc waypoint route after interruption
	 * <p/>
	 * Revision 1.6  2004/05/24 15:46:41  Ian.Mayo
	 * Commit updates from home
	 * <p/>
	 * Revision 1.2  2004/03/04 22:31:50  ian
	 * Refactored to allow sub-classing
	 * <p/>
	 * Revision 1.1.1.1  2004/03/04 20:30:51  ian
	 * no message
	 * <p/>
	 * Revision 1.5  2003/11/19 15:49:08  Ian.Mayo
	 * Sort out for when we haven't got simple status
	 * <p/>
	 * Revision 1.4  2003/11/05 09:20:12  Ian.Mayo
	 * Include MWC Model support
	 * <p/>
	 * </pre>
	 */
	public String getVersion()
	{
		return "$Date$";
	}

	static public final class WanderInfo extends MWC.GUI.Editable.EditorType
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public WanderInfo(final Wander data)
		{
			super(data, data.getName(), "Wander");
		}

		/**
		 * editable GUI properties for our participant
		 * 
		 * @return property descriptions
		 */
		public final java.beans.PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final java.beans.PropertyDescriptor[] res =
				{ prop("Origin", "the center about which we wander"),
						prop("Range", "the distance we wander out to (yds)"),
						prop("Name", "the name of this wandering model"),
						prop("Speed", "the speed we wander at (kts)"),
						prop("Height", "the Height we wander at (m)"), };
				return res;
			}
			catch (java.beans.IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	public static class Wander2Test extends SupportTesting.EditableTesting
	{
		public Wander2Test(final String val)
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
			Wander newWander = new Wander(createLocation(0, 0), new WorldDistance(21,
					WorldDistance.KM));
			newWander.setName("testing wander");
			return newWander;
		}

		/**
		 * test we handle missing demanded speed
		 */
		public final void testNoDemCourseSpeed()
		{
			final WorldLocation origin = SupportTesting.createLocation(0, 0);
			final Wander tw = new Wander(origin, new WorldDistance(21,
					WorldDistance.KM));

			final Status stat = new Status(1, 0);
			stat.setLocation(origin
					.add(new WorldVector(100, WorldDistance.METRES, 0)));
			stat.setCourse(22);
			stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

			final DemandedStatus dem = null;

			final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat,
					null, dem, null, null, 100);

			assertNotNull("dem returned", ds);
			assertEquals("still on old course", 22, ds.getCourse(), 0);
			assertEquals("still on old speed", 12, ds.getSpeed(), 0);
		}

		public final void testWithDemCourseSpeed()
		{
			final WorldLocation origin = SupportTesting.createLocation(0, 0);
			final Wander tw = new Wander(origin, new WorldDistance(21,
					WorldDistance.KM));
			tw.setSpeed(new WorldSpeed(44, WorldSpeed.M_sec));
			tw.setHeight(new WorldDistance(45, WorldDistance.METRES));

			final Status stat = new Status(1, 0);
			stat.setLocation(origin
					.add(new WorldVector(100, WorldDistance.METRES, 0)));
			stat.setCourse(22);
			stat.setSpeed(new WorldSpeed(12, WorldSpeed.M_sec));

			final DemandedStatus dem = null;

			final SimpleDemandedStatus ds = (SimpleDemandedStatus) tw.decide(stat,
					null, dem, null, null, 100);

			assertNotNull("dem returned", ds);
			assertEquals("new demanded Height", 45, ds.getHeight(), 0);
			assertEquals("new demanded speed", 44, ds.getSpeed(), 0);
		}
	}

}