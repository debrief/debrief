/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.Wrappers;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import Debrief.GUI.Tote.Painters.SnailDrawTMAContact;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

@SuppressWarnings("serial")
public final class SensorArcContactWrapper extends
		SnailDrawTMAContact.PlottableWrapperWithTimeAndOverrideableColor implements
		MWC.GenericData.Watchable, CanvasType.MultiLineTooltipProvider,
		Editable.DoNotHighlightMe, TimeStampedDataItem, ExcludeFromRightClickEdit
{
	// ///////////////////////////////////////////
	// member variables
	/**
	 * /////////////////////////////////////////////
	 */
	private String _trackName;

	/**
	 * long _DTG
	 */
	private HiResDate _startDTG, _endDTG;
	
	//private int _left, _right, _inner, _outer;
	//private int _left1, _right1, _inner1, _outer1;
	//private boolean _isBeam;
	
	private List<SensorArcValue> _values = new ArrayList<SensorArcValue>();

	/**
	 * origin of the target, or null to read origin from host vessel
	 */
	private WorldLocation _absoluteOrigin;

	/**
	 * the calculated origin for this item, when we're dependent on a parent track
	 */
	private WorldLocation _calculatedOrigin;

	/**
	 * whether to show the label
	 */
	private boolean _showLabel = false;

	/**
	 * our editor
	 */
	transient private MWC.GUI.Editable.EditorType _myEditor;

	/**
	 * the style to plot this line
	 */
	private int _myLineStyle = 0;

	/**
	 * the parent object (which supplies our colour, should we need it)
	 */
	private transient SensorArcWrapper _mySensor;

	/**
	 * the label describing this contact
	 */
	private final MWC.GUI.Shapes.TextLabel _theLabel;

	/**
	 * whereabouts on the line where we plot the label
	 */
	private int _theLineLocation = MWC.GUI.Properties.LineLocationPropertyEditor.MIDDLE;

	private String _sensorName;
	
	private WorldArea _theArea;

	private double _radDegs = 0;

	/**
	 * default constructor, used when we read in from XML
	 */
	public SensorArcContactWrapper()
	{
		// create the label
		_theLabel = new MWC.GUI.Shapes.TextLabel(new WorldLocation(0, 0, 0), null);

		// by default, objects based on plain wrapper are coloured yellow.
		// but, we use a null colour value to indicate 'use parent color'
		setColor(null);

		setVisible(true);
		setLabelVisible(false);
	}

	/**
	 * build a new sensorarc contact wrapper
	 * 
	 */

	public SensorArcContactWrapper(final String theTrack, final HiResDate startDtg,
			final HiResDate endDtg,
			List<SensorArcValue> values,
			final Color theColor,
			final int theStyle, final String sensorName)
	{
		this();

		_trackName = theTrack;
		_startDTG = startDtg;
		_endDTG = endDtg;
		_values = values;
		
		// and the gui parameters
		setColor(theColor);
		_myLineStyle = theStyle;
		_theLabel.setString(sensorName);
		_sensorName = sensorName;
	}

	private void calculateArea(final WorldLocation centre)
	{
		// calc the outer radius
		calculateExtRadius();

		if (centre == null)
		{
			// FIXME
			return;
		}
		// create our area
		_theArea = new WorldArea(centre, centre);

		// create & extend to top left
		WorldLocation other = centre.add(new WorldVector(0, _radDegs, 0));
		_theArea.extend(other);
		other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270),
				_radDegs, 0));
		_theArea.extend(other);

		// create & extend to bottom right
		other = centre.add(new WorldVector(MWC.Algorithms.Conversions
				.Degs2Rads(180), _radDegs, 0));
		_theArea.extend(other);
		other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
				_radDegs, 0));
		_theArea.extend(other);
	}

	private void calculateExtRadius()
	{
		if (_radDegs != 0)
		{
			return;
		}
		int outer = 0;
		for (SensorArcValue value : _values)
		{
			if (value.outer > outer)
			{
				outer = value.outer;
			}
		}
		_radDegs = new WorldDistance(outer, WorldDistance.YARDS).getValueIn(WorldDistance.DEGS);
	}

	public void clearCalculatedOrigin()
	{
		_calculatedOrigin = null;
	}

	/**
	 * set the origin for this object
	 */
	public final void setOrigin(final WorldLocation val)
	{
		_absoluteOrigin = val;
	}

	public WorldLocation getOrigin()
	{
		return _absoluteOrigin;
	}

	/**
	 * return the coordinates for the start of the line
	 */
	public final WorldLocation getCalculatedOrigin(
			final MWC.GenericData.WatchableList parent)
	{
		MWC.GenericData.WatchableList theParent = parent;
		if (theParent == null)
			theParent = _mySensor.getHost();

		if ((_calculatedOrigin == null))
		{
			if (_absoluteOrigin != null)
			{
				// note, we don't bother with the offset if we have an absolute origin
				_calculatedOrigin = new WorldLocation(_absoluteOrigin);
			}
			else
			{
				if (theParent != null)
				{

					// better calculate it ourselves then
					final TrackWrapper parentTrack = (TrackWrapper) theParent;

					// get the origin
					HiResDate dtg = _startDTG != null ? _startDTG : _endDTG;
					if (dtg == null)
					{
						dtg = parentTrack.getStartDTG();
					}
					if (dtg != null)
					{
						final FixWrapper backtrack = parentTrack.getBacktraceTo(dtg,
								_mySensor.getSensorOffset(), _mySensor.getWormInHole());
						if (backtrack != null)
							_calculatedOrigin = backtrack.getLocation();
					}
				}
			}

		}

		return _calculatedOrigin;
	}

	/**
	 * getTrackName
	 * 
	 * @return the returned String
	 */
	public final String getTrackName()
	{
		return _trackName;
	}


	/**
	 * member function to meet requirements of comparable interface *
	 */
	public final int compareTo(final Plottable o)
	{
		final SensorArcContactWrapper other = (SensorArcContactWrapper) o;
		if (_startDTG == null || other == null || other._startDTG == null) {
			return 1;
		}
		int res = 0;
		if (_startDTG.lessThan(other._startDTG))
			res = -1;
		else if (_startDTG.greaterThan(other._startDTG))
			res = 1;
		else
		{
			// just check if this is actually the same object (in which case return 0)
			if (o == this)
			{
				// we need a correct implementation of compare to for when we're finding
				// the position
				// of an item which is actually in the list - otherwise it won't get
				// found and we can't
				// delete it.
				res = 0;
			}
			else
			{
				// same times, make the newer item appear later. This is to overcome the
				// problem we experience where only the first contact at a particular
				// DTG gets recorded for a sensor
				res = 1;
			}
		}

		return res;

	}

	// ///////////////////////////////////////////
	// member methods to meet requirements of Plottable interface
	// ///////////////////////////////////////////

	/**
	 * paint this object to the specified canvas
	 */
	public final void paint(final MWC.GUI.CanvasType dest)
	{
		// DUFF METHOD TO MEET INTERFACE REQUIREMENTS
	}

	/**
	 * paint this object to the specified canvas
	 * 
	 * @param track
	 *          the parent list (from which we calculate origins if required)
	 * @param dest
	 *          where we're painting it to
	 * @param keep_simple
	 *          whether to allow a change in line style
	 */
	public final void paint(final MWC.GenericData.WatchableList track,
			final MWC.GUI.CanvasType dest, final boolean keep_simple)
	{
		// ignore
		if (!getVisible())
			return;

		// do we know our track?
		if (track == null)
		{
			MWC.Utilities.Errors.Trace.trace("failed to find track for sensor data:"
					+ this.getLabel());
			return;
		}

		// do we need an origin
		final WorldLocation origin = getCalculatedOrigin(track);
		calculateArea(origin);

		if (origin == null)
		{
			// FIXME
			return;
		}
		HiResDate dtg = _startDTG != null ? _startDTG : _endDTG;
		if (dtg != null)
		{
			final TimePeriod trackPeriod = new TimePeriod.BaseTimePeriod(
					track.getStartDTG(), track.getEndDTG());
			if (!trackPeriod.contains(dtg))
			{
				// don't bother trying to plot it, we're outside the parent period
				return;
			}
		}

		if (!keep_simple)
		{

			// restore the solid line style, for the next poor bugger
			dest.setLineStyle(MWC.GUI.CanvasType.SOLID);

			// now draw the label
			if (getLabelVisible())
			{
				WorldLocation labelPos = null;
				
				// sort out where to plot it
				if (_theLineLocation == MWC.GUI.Properties.LineLocationPropertyEditor.START)
				{
					WorldLocation topLeft = new WorldLocation(
							origin.add(new WorldVector(0, _radDegs, 0)));
					topLeft.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270),
							_radDegs, 0));
					labelPos = topLeft;
				}
				else if (_theLineLocation == MWC.GUI.Properties.LineLocationPropertyEditor.END)
				{
					WorldLocation bottomRight = new WorldLocation(
							origin.add(new WorldVector(MWC.Algorithms.Conversions
									.Degs2Rads(180), _radDegs, 0)));
					bottomRight.addToMe(new WorldVector(MWC.Algorithms.Conversions
							.Degs2Rads(90), _radDegs, 0));
					labelPos = bottomRight;
				}
				else if (_theLineLocation == MWC.GUI.Properties.LineLocationPropertyEditor.MIDDLE)
				{
					// calculate the centre point
					labelPos = origin;
				}

				// update it's location
				_theLabel.setLocation(labelPos);
				_theLabel.setColor(getColor());
				_theLabel.paint(dest);
			}
		}
		// ok, we have the start - convert it to a point
		final Point pt = new Point(dest.toScreen(origin));
			
		// FIXME also plot the origin
		dest.fillRect(pt.x - 1, pt.y - 2, 3, 3);
		
		// paint arc
		
		Color oldColor = dest.getBackgroundColor();
		
		for (SensorArcValue value : _values)
		{
			paintArc(dest, origin, _radDegs, getColor(), value.left, value.right);
			if (value.inner != 0 && value.outer != 0)
			{
				paintArc(dest, origin, _radDegs * value.inner / value.outer, oldColor,
						value.left, value.right);
			}
		}

		dest.setBackgroundColor(oldColor);
		
	}

	private void paintArc(final MWC.GUI.CanvasType dest,
			final WorldLocation origin, double radDegs, Color color, int left, int right)
	{
		WorldLocation topLeft = new WorldLocation(
				origin.add(new WorldVector(0, radDegs, 0)));
		topLeft.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270),
				radDegs, 0));

		// create & extend to bottom right
		WorldLocation bottomRight = new WorldLocation(
				origin.add(new WorldVector(MWC.Algorithms.Conversions
						.Degs2Rads(180), radDegs, 0)));
		bottomRight.addToMe(new WorldVector(MWC.Algorithms.Conversions
				.Degs2Rads(90), radDegs, 0));

		Point tl = dest.toScreen(topLeft);

		int tlx = tl.x;
		int tly = tl.y;

		// get the width and height
		Point br = dest.toScreen(bottomRight);
				
		int startAngle = - (left - 90);
		int arcWidth = right - left;

		int wid = br.x - tlx;
		int height = br.y - tly;

		// and now draw it

		dest.setBackgroundColor(color);
		dest.fillArc(tlx, tly, wid, height, startAngle, -arcWidth);
	}

	/**
	 * method to reset the colour, so that we take that of our parent
	 */
  @FireReformatted
	public final void resetColor()
	{
		setColor(null);
	}

	/**
	 * find the name of the sensor which recorded this contact
	 */
	public final String getSensorName()
	{
		String res;
		if (_mySensor != null)
		{
			res = _mySensor.getName();
		}
		else
		{
			res = _sensorName;
		}
		return res;
	}

	/**
	 * find the data area occupied by this item
	 */
	public final MWC.GenericData.WorldArea getBounds()
	{
		if (_theArea !=null) 
		{
			return _theArea;
		}
		final WorldLocation origin = getCalculatedOrigin(null);

		calculateArea(origin);

		// done.
		return _theArea;
	}

	/**
	 * it this Label item currently visible?
	 */
	public final boolean getLabelVisible()
	{
		return _showLabel;
	}

	/**
	 * set the Label visibility
	 */
	public final void setLabelVisible(final boolean val)
	{
		_showLabel = val;
	}

	/**
	 * return the location of the label
	 */
	public final void setLabelLocation(final Integer loc)
	{
		_theLabel.setRelativeLocation(loc);
	}

	/**
	 * update the location of the label
	 */
	public final Integer getLabelLocation()
	{
		return _theLabel.getRelativeLocation();
	}

	/**
	 * return the location of the label
	 */
	public final void setPutLabelAt(final Integer loc)
	{
		_theLineLocation = loc.intValue();
	}

	/**
	 * update the location of the label
	 */
	public final Integer getPutLabelAt()
	{
		return new Integer(_theLineLocation);
	}

	/**
	 * update the line style
	 */
	public final void setLineStyle(final Integer style)
	{
		_myLineStyle = style.intValue();
	}

	/**
	 * retrieve the line style
	 */
	public final Integer getLineStyle()
	{
		return new Integer(_myLineStyle);
	}

	/**
	 * inform us of our sensor
	 */
	public final void setSensor(final SensorArcWrapper sensor)
	{
		_mySensor = sensor;
	}

	public final SensorArcWrapper getSensor()
	{
		return _mySensor;
	}

	/**
	 * get the label for this data item
	 */
	public final String getLabel()
	{
		return _theLabel.getString();
	}

	/**
	 * set the label for this data item
	 */
	public final void setLabel(final String val)
	{
		_theLabel.setString(val);
	}

	/**
	 * how far away are we from this point? or return null if it can't be
	 * calculated
	 */
	public final double rangeFrom(final MWC.GenericData.WorldLocation other)
	{
		// return the distance from each end
		double res = Plottables.INVALID_RANGE;

		if (getVisible())
		{
			// find our origin
			final WorldLocation theOrigin = getCalculatedOrigin(null);

			// did we manage it?
			if (theOrigin == null)
			{
				// nope, poss because we don't have a position for this time
				return INVALID_RANGE;
			}
			else
				res = theOrigin.rangeFrom(other);
		}

		return res;
	}

	/**
	 * getInfo
	 * 
	 * @return the returned MWC.GUI.Editable.EditorType
	 */
	public final MWC.GUI.Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new SensorArcContactInfo(this);

		return _myEditor;
	}

	/**
	 * method to provide the actual colour value stored in this fix
	 * 
	 * @return fix colour, including null if applicable
	 */
	public final Color getActualColor()
	{
		return super.getColor();
	}

	/**
	 * get the colour (or that of our parent, if we don't have one
	 */
	public final java.awt.Color getColor()
	{
		java.awt.Color res = super.getColor();

		// has our colour been set?
		if (res == null)
		{
			// no, get the colour from our parent
			res = _mySensor.getColor();
		}

		return res;
	}

	/**
	 * whether there is any edit information for this item this is a convenience
	 * function to save creating the EditorType data first
	 * 
	 * @return yes/no
	 */
	public final boolean hasEditor()
	{
		return true;
	}

	/**
	 * get the name of this entry, using the formatted DTG
	 */
	public final String getName()
	{
		StringBuilder builder = new StringBuilder();
		if (_startDTG != null)
		{
			builder.append(DebriefFormatDateTime.toStringHiRes(_startDTG));
		}
		builder.append("-");
		if (_endDTG != null)
		{
			builder.append(DebriefFormatDateTime.toStringHiRes(_endDTG));
		}
		return builder.toString();
	}

	/**
	 * get the data name in multi-line format (for tooltips)
	 * 
	 * @return multi-line text label
	 */
	public String getMultiLineName()
	{
		return "SensorArc:" + getName() + "\nTrack:"
				+ getLabel();
	}

	/**
	 * toString
	 * 
	 * @return the returned String
	 */
	public final String toString()
	{
		return getName();
	}

	// ////////////////////////////////////////////////////////////
	// methods to support Watchable interface
	// ////////////////////////////////////////////////////////////
	/**
	 * get the current location of the watchable
	 * 
	 * @return the location
	 */
	public final WorldLocation getLocation()
	{
		return this.getCalculatedOrigin(null);
	}

	/**
	 * get the current course of the watchable (rads)
	 * 
	 * @return course in radians
	 */
	public final double getCourse()
	{
		return -1;
	}

	/**
	 * get the current speed of the watchable (kts)
	 * 
	 * @return speed in knots
	 */
	public final double getSpeed()
	{
		return -1;
	}

	/**
	 * get the current depth of the watchable (m)
	 * 
	 * @return depth in metres
	 */
	public final double getDepth()
	{
		return 0;
	}

	/**
	 * find out the time of this watchable
	 */
	public final HiResDate getTime()
	{
		return this.getDTG();
	}

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public static final class SensorArcContactInfo extends Griddable
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public SensorArcContactInfo(final SensorArcContactWrapper data)
		{
			super(data, data.getName(), "SensorArc");
		}

		@Override
		public PropertyDescriptor[] getGriddablePropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						prop("Label", "the label for this data item", FORMAT),
						prop("Visible", "whether this sensor contact data is visible",
								FORMAT),
						prop("LabelVisible",
								"whether the label for this contact is visible", FORMAT),
						prop("Color", "the color for this sensor contact", FORMAT),
						longProp("LabelLocation", "the label location",
								MWC.GUI.Properties.LocationPropertyEditor.class),
						longProp("PutLabelAt",
								"whereabouts on the line to position the label",
								MWC.GUI.Properties.LineLocationPropertyEditor.class),
						longProp("LineStyle", "style to use to plot the line",
								MWC.GUI.Properties.LineStylePropertyEditor.class), };

				return res;

			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		/**
		 * The things about these Layers which are editable. We don't really use
		 * this list, since we have our own custom editor anyway
		 * 
		 * @return property descriptions
		 */
		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						prop("Label", "the label for this data item", FORMAT),
						prop("Visible", "whether this sensor contact data is visible",
								FORMAT),
						prop("LabelVisible",
								"whether the label for this contact is visible", FORMAT),
						prop("Color", "the color for this sensor contact", FORMAT),
						longProp("LabelLocation", "the label location",
								MWC.GUI.Properties.LocationPropertyEditor.class),
						longProp("PutLabelAt",
								"whereabouts on the line to position the label",
								MWC.GUI.Properties.LineLocationPropertyEditor.class),
						longProp("LineStyle", "style to use to plot the line",
								MWC.GUI.Properties.LineStylePropertyEditor.class),
						
				};
				return res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		/**
		 * getMethodDescriptors
		 * 
		 * @return the returned MethodDescriptor[]
		 */
		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class<SensorArcContactWrapper> c = SensorArcContactWrapper.class;
			final MethodDescriptor[] mds =
			{ method(c, "resetColor", null, "Reset Color") };
			return mds;
		}

		public final SubjectAction[] getUndoableActions()
		{
			final SubjectAction[] res = new SubjectAction[0];
			// FIXME
			return res;
		}

		@Override
		public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors()
		{
			// don't worry - we do the bean-based method
			return null;
		}

	}

	

	// ////////////////////////////////////////////////////
	// nested class
	// /////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	static public final class testSensorArcContact extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testSensorArcContact(final String val)
		{
			super(val);
		}

		// TODO
		public final void testMyCode()
		{
			
		}

	}

	@Override
	public void setDTG(HiResDate date)
	{
		_startDTG = date;
	}

	@Override
	public HiResDate getDTG()
	{
		return _startDTG;
	}
	
	public HiResDate getStartDTG()
	{
		return _startDTG;
	}
	
	public HiResDate getEndDTG()
	{
		return _endDTG;
	}
	
	public List<SensorArcValue> getValues()
	{
		return _values;
	}
}
