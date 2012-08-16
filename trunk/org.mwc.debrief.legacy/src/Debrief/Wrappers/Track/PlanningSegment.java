package Debrief.Wrappers.Track;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;

import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.CreateEditorForParent;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.Griddable;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Properties.CardinalPointsPropertyEditor;
import MWC.GUI.Properties.PlanningLegCalcModelPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

public class PlanningSegment extends TrackSegment implements Cloneable,
		Editable.DoNoInspectChildren, CreateEditorForParent, TimeStampedDataItem
{

	/**
	 * special case that gives us a leg that goes back to the start
	 * 
	 * @author ian
	 * 
	 */
	public static class ClosingSegment extends PlanningSegment
	{

		public ClosingSegment(String name, double courseDegs,
				WorldSpeed worldSpeed, WorldDistance worldDistance)
		{
			super(name, courseDegs, worldSpeed, worldDistance);
			this.setCalculation(PlanningLegCalcModelPropertyEditor.RANGE_SPEED);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	/**
	 * class containing editable details of a track
	 */
	public class PlanningSegmentInfo extends Griddable
	{

		/**
		 * constructor for this editor, takes the actual track as a parameter
		 * 
		 * @param data
		 *          track being edited
		 */
		public PlanningSegmentInfo(final TrackSegment data)
		{
			super(data, data.getName(), "");
		}

		@Override
		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						expertProp("Calculation", "How to calculate the leg length",
								SPATIAL),
						expertProp("Visible", "whether this layer is visible", FORMAT),
						expertProp("Depth", "The depth for this leg", SPATIAL),
						expertProp("Course", "The course for this leg", SPATIAL),
						expertProp("Distance", "The distance travelled along this leg",
								SPATIAL),
						expertProp("Speed", "The speed travelled along this leg", SPATIAL),
						expertProp("Duration", "The duration of travel along this leg",
								SPATIAL),
						expertProp("Name", "Name of this track segment", FORMAT), };

				res[0].setPropertyEditorClass(PlanningLegCalcModelPropertyEditor.class);
				res[3].setPropertyEditorClass(CardinalPointsPropertyEditor.class);

				return res;
			}
			catch (final IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}

		@Override
		public PropertyDescriptor[] getGriddablePropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						prop("Name", "the name for this leg", FORMAT),
						prop("Course", "the course for this leg", SPATIAL),
						prop("Speed", "the speed at which to travel on this leg", SPATIAL),
						prop("Distance", "how long this leg is", SPATIAL),
						prop("Duration", "how long the vessel travels on this leg",
								TEMPORAL),
						prop("Depth", "depth to travel at on this leg", SPATIAL),

				};
				return res;

			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		@Override
		public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors()
		{
			// TODO Auto-generated method stub
			return null;
		}
	}

	private transient CompositeTrackWrapper _parent;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * how far we represent (optional)
	 * 
	 */
	WorldDistance _myLength;

	/**
	 * the course to follow
	 * 
	 */
	double _myCourseDegs;

	/**
	 * the speed to transit at (optional)
	 * 
	 */
	WorldSpeed _mySpeed;

	/**
	 * the date this segment was created - used to force sort order by the order
	 * they were read in
	 * 
	 */
	private long _created = System.nanoTime();

	/**
	 * how far to travel for (optional)
	 * 
	 */
	Duration _myPeriod = new Duration(12, Duration.MINUTES);

	/**
	 * which calculation model to use
	 * 
	 */
	int _calcModel;

	/**
	 * the depth for this leg
	 * 
	 */
	private WorldDistance _myDepth = new WorldDistance(0, WorldDistance.METRES);

	/**
	 * copy constructor
	 * 
	 * @param other
	 */
	public PlanningSegment(PlanningSegment other)
	{
		_calcModel = other._calcModel;
		_created = System.nanoTime();
		_myCourseDegs = other._myCourseDegs;
		_myDepth = new WorldDistance(other._myDepth);
		_myLength = new WorldDistance(other._myLength);
		_myPeriod = new Duration(other._myPeriod);
		_mySpeed = new WorldSpeed(other._mySpeed);
		_parent = other._parent;
		this.setName(other.getName());
	}

	public PlanningSegment(String name, double courseDegs, WorldSpeed worldSpeed,
			WorldDistance worldDistance)
	{
		this.setName(name);
		this.setCourse(courseDegs);
		this.setSpeedSilent(worldSpeed);
		this.setDistanceSilent(worldDistance);

		this.recalc();
	}

	public WorldDistance getDepth()
	{
		return _myDepth;
	}

	public void setDepth(WorldDistance depth)
	{
		_myDepth = depth;
		recalc();
	}

	public void setDepthSilent(WorldDistance depth)
	{
		_myDepth = depth;
	}

	/**
	 * special add-fix, so we don't bother with rename
	 * 
	 */
	public void addFix(final FixWrapper fix)
	{
		// remember the fix
		this.addFixSilent(fix);
	}

	@Override
	public int compareTo(Plottable arg0)
	{
		int res = 1;
		if (arg0 instanceof ClosingSegment)
		{
			// the closing semgent will always come after
			res = -1;
		}
		else if (arg0 instanceof PlanningSegment)
		{
			PlanningSegment other = (PlanningSegment) arg0;
			Long myTime = _created;
			Long hisTime = other._created;
			res = myTime.compareTo(hisTime);
		}
		return res;
	}

	@Override
	public double rangeFrom(WorldLocation other)
	{
		double firstRange = Plottable.INVALID_RANGE;

		Enumeration<Editable> numer = this.elements();
		while (numer.hasMoreElements())
		{
			Editable editable = (Editable) numer.nextElement();
			FixWrapper fw = (FixWrapper) editable;
			double thisR = fw.rangeFrom(other);
			if (firstRange == Plottable.INVALID_RANGE)
				firstRange = thisR;
			else
				firstRange = Math.min(firstRange, thisR);
		}
		return firstRange;
	}

	public int getCalculation()
	{
		return _calcModel;
	}

	@FireExtended
	public void setCalculation(int calculation)
	{
		_calcModel = calculation;
	}

	@FireExtended
	public void setCalculation(Integer calculation)
	{
		_calcModel = calculation;
	}

	public WorldDistance getDistance()
	{
		return _myLength;
	}

	@FireExtended
	public void setDistance(WorldDistance length)
	{
		this._myLength = length;
		recalc();
	}

	public double getCourse()
	{
		return _myCourseDegs;
	}

	public void setCourse(double courseDegs)
	{
		this._myCourseDegs = courseDegs;
		recalc();
	}

	public void setCourseSilent(double courseDegs)
	{
		this._myCourseDegs = courseDegs;
	}

	public WorldSpeed getSpeed()
	{
		return _mySpeed;
	}

	@FireExtended
	public void setSpeed(WorldSpeed speed)
	{
		this._mySpeed = speed;
		recalc();
	}

	public Duration getDuration()
	{
		return _myPeriod;
	}

	@FireExtended
	public void setDuration(Duration period)
	{
		this._myPeriod = period;
		recalc();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PlanningSegment res = new PlanningSegment(this);

		return res;
	}

	@Override
	public Editable.EditorType getInfo()
	{
		return new PlanningSegmentInfo(this);
	}

	/**
	 * does this item have an editor?
	 */
	public boolean hasEditor()
	{
		return true;
	}

	private void recalc()
	{
		if (_parent != null)
			_parent.recalculate();
	}

	@Override
	protected void sortOutDate(HiResDate startDTG)
	{
		// ignore - we want to keep the layer name
	}

	@Override
	public void setWrapper(TrackWrapper wrapper)
	{
		// store the parent
		super.setWrapper(wrapper);

		// and store the helper-outer
		_parent = (CompositeTrackWrapper) wrapper;

	}

	public void setSpeedSilent(WorldSpeed worldSpeed)
	{
		_mySpeed = worldSpeed;
		// don't bother triggering recalc
	}

	public void setDistanceSilent(WorldDistance worldDistance)
	{
		_myLength = worldDistance;
		// don't bother triggering recalc
	}

	public void setDurationSilent(Duration duration)
	{
		_myPeriod = duration;
		// don't bother triggering recalc
	}

	public PlanningSegment createCopy()
	{
		PlanningSegment res = new PlanningSegment(this);
		res._calcModel = _calcModel;
		res._myCourseDegs = _myCourseDegs;
		res._myDepth = _myDepth;
		res._myLength = _myLength;
		res._myPeriod = _myPeriod;
		res._mySpeed = _mySpeed;
		res._parent = _parent;

		return res;
	}

	@Override
	public Editable getParent()
	{
		return getWrapper();
	}

	@Override
	public HiResDate getDTG()
	{
		return this.startDTG();
	}

	@Override
	public void setDTG(HiResDate date)
	{
		// ingore, we don't set the DTG for a planning segment
		System.err.println("Should not set DTG for planning segment");
	}

}
