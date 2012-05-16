package Debrief.Wrappers.Track;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import Debrief.Wrappers.CompositeTrackWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Properties.PlanningLegCalcModelPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldSpeed;

public class PlanningSegment extends TrackSegment
{

	/**
	 * class containing editable details of a track
	 */
	public class PlanningSegmentInfo extends Editable.EditorType
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
						expertProp("Course", "The course for this leg", SPATIAL),
						expertProp("Length", "The distance travelled along this leg",
								SPATIAL),
						expertProp("Speed", "The speed travelled along this leg", SPATIAL),
						expertProp("Duration", "The duration of travel along this leg",
								SPATIAL),
						expertProp("Name", "Name of this track segment", FORMAT), };

				res[0].setPropertyEditorClass(PlanningLegCalcModelPropertyEditor.class);

				return res;
			}
			catch (final IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
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
	 * how far to travel for (optional)
	 * 
	 */
	Duration _myPeriod = new Duration(12, Duration.MINUTES);

	/**
	 * which calculation model to use
	 * 
	 */
	int _calcModel;

	public int getCalculation()
	{
		return _calcModel;
	}

	public void setCalculation(int calculation)
	{
		_calcModel = calculation;
	}

	public WorldDistance getLength()
	{
		return _myLength;
	}

	public void setLength(WorldDistance length)
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

	public WorldSpeed getSpeed()
	{
		return _mySpeed;
	}

	public void setSpeed(WorldSpeed speed)
	{
		this._mySpeed = speed;
		recalc();
	}

	public Duration getDuration()
	{
		return _myPeriod;
	}

	public void setDuration(Duration period)
	{
		this._myPeriod = period;
		recalc();
	}

	@Override
	public Editable.EditorType getInfo()
	{
		return new PlanningSegmentInfo(this);
	}

	private void recalc()
	{
		if (_parent != null)
			_parent.recalculate();
	}

	@Override
	public void setWrapper(TrackWrapper wrapper)
	{
		// store the parent
		super.setWrapper(wrapper);

		// and store the helper-outer
		_parent = (CompositeTrackWrapper) wrapper;

	}

}
