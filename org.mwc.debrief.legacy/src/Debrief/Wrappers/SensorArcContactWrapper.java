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
// / Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SensorContactWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.13 $
// $Log: SensorContactWrapper.java,v $
// Revision 1.13  2007/04/25 09:32:44  ian.mayo
// Prevent highlight being plotted for sensor & TMA data
//
// Revision 1.12 2006/02/13 16:19:07 Ian.Mayo
// Sort out problem with creating sensor data
//
// Revision 1.11 2006/01/06 10:37:42 Ian.Mayo
// Reflect tidying of sensor wrapper naming
//
// Revision 1.10 2005/12/13 09:04:59 Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.9 2005/06/07 08:38:30 Ian.Mayo
// Provide efficiency to stop millions of popup menu items representing hidden
// tma solutions.
//
// Revision 1.8 2005/06/06 14:45:05 Ian.Mayo
// Refactor how we support tma & sensor data
//
// Revision 1.7 2005/03/10 09:44:23 Ian.Mayo
// Tidy implementation where we have sensor/tua data beyond time period of
// parent track. Prevent error being thrown.
//
// Revision 1.6 2005/02/22 09:31:57 Ian.Mayo
// Refactor snail plotting sensor & tma data - so that getting & managing valid
// data points are handled in generic fashion. We did have two very similar
// implementations, tracking errors introduced after hi-res-date changes was
// proving expensive/unreliable. All fine now though.
//
// Revision 1.5 2004/12/17 15:53:59 Ian.Mayo
// Get on top of some problems plotting sensor & tma data.
//
// Revision 1.4 2004/12/16 11:33:03 Ian.Mayo
// Handle when sensor data is outside period of parent track - and we can't find
// parent fixes to attached sensor contact line to
//
// Revision 1.3 2004/11/25 10:24:47 Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2 2003/10/27 12:59:47 Ian.Mayo
// Tidy up duplicate comments
//
// Revision 1.1.1.2 2003/07/21 14:49:24 Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.14 2003-07-16 12:50:11+01 ian_mayo
// Improve text in multi-line tooltip
//
// Revision 1.13 2003-07-04 10:59:23+01 ian_mayo
// reflect name change in parent testing class
//
// Revision 1.12 2003-06-30 09:14:15+01 ian_mayo
// Improve labels
//
// Revision 1.11 2003-06-25 10:44:21+01 ian_mayo
// Provide multi-line tooltip
//
// Revision 1.10 2003-06-16 11:58:52+01 ian_mayo
// Correctly implement compareTo method (we weren't returning equality value for
// when same object is being compared).
//
// Revision 1.9 2003-03-19 15:36:53+00 ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.8 2002-10-30 16:27:26+00 ian_mayo
// tidy up (shorten) display names of editables
//
// Revision 1.7 2002-10-01 15:41:41+01 ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.6 2002-07-10 14:59:26+01 ian_mayo
// handle correct returning of nearest points - zero length list instead of null
// when no matches
//
// Revision 1.5 2002-07-08 09:47:45+01 ian_mayo
// <>
//
// Revision 1.4 2002-06-05 12:56:29+01 ian_mayo
// unnecessarily loaded
//
// Revision 1.3 2002-05-31 16:18:44+01 ian_mayo
// Don't store the far end any more
//
// Revision 1.2 2002-05-28 09:25:13+01 ian_mayo
// after switch to new system
//
// Revision 1.1 2002-05-28 09:11:39+01 ian_mayo
// Initial revision
//
// Revision 1.0 2002-04-30 09:14:54+01 ian
// Initial revision
//
// Revision 1.1 2002-04-23 12:28:26+01 ian_mayo
// Initial revision
//
// Revision 1.8 2002-02-26 15:49:08+00 administrator
// Reflect new TextLabel signature
//
// Revision 1.7 2001-10-02 10:08:41+01 administrator
// remove d-line
//
// Revision 1.6 2001-10-01 12:49:49+01 administrator
// the getNearest method of WatchableList now returns an array of points (since
// a contact wrapper may contain several points at the same DTG). We have had to
// reflect this across the application
//
// Revision 1.5 2001-10-01 11:22:15+01 administrator
// Change COMPARABLE so that multiple contacts with the same DTG are not
// rejected
//
// Revision 1.4 2001-08-29 19:17:01+01 administrator
// <>
//
// Revision 1.3 2001-08-21 15:19:54+01 administrator
// Tidy up constructor code
//
// Revision 1.2 2001-08-21 12:05:33+01 administrator
// General tidying up, plus extension of testing to tidily manage missing
// origins for sensor data
//
// Revision 1.1 2001-08-14 14:07:38+01 administrator
// Finishing the implementation
//
// Revision 1.0 2001-08-09 14:16:51+01 administrator
// Initial revision
//
package Debrief.Wrappers;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;

import Debrief.GUI.Tote.Painters.SnailDrawTMAContact;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExcludeFromRightClickEdit;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Shapes.CircleShape;
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
	
	private int _left, _right, _inner, _outer;
	private int _left1, _right1, _inner1, _outer1;
	private boolean _isBeam;
	
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
	
	private WorldDistance _extRadius = new WorldDistance(4000, WorldDistance.YARDS);
	
	private CircleShape _extCircle, _intCircle;
	
	private WorldDistance _intRadius = new WorldDistance(2000, WorldDistance.YARDS);

	private WorldArea _theArea;

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
			int left, int right, int inner, int outer,
			int left1, int right1, int inner1, int outer1,
			boolean isBeam,
			final Color theColor,
			final int theStyle, final String sensorName)
	{
		this();

		_trackName = theTrack;
		_startDTG = startDtg;
		_endDTG = endDtg;
		_left = left;
		_right = right;
		_inner = inner;
		_outer = outer;
		_left1 = left1;
		_right1 = right1;
		_inner1 = inner1;
		_outer1 = outer1;
		_isBeam = isBeam;
		
		// store the origin, and update the far end if required
		//setOrigin(origin);

		// and the gui parameters
		setColor(theColor);
		_myLineStyle = theStyle;
		//_theLabel.setLocation(origin);
		_theLabel.setString(sensorName);
		_sensorName = sensorName;
	}

	private void calculateArea(final WorldLocation centre)
	{
			// calc the radius in degrees
			final double radDegs = _extRadius.getValueIn(WorldDistance.DEGS);

			if (centre == null) 
			{
				// FIXME
				return;
			}
			// create our area
			_theArea = new WorldArea(centre, centre);

			// create & extend to top left
			WorldLocation other = centre.add(new WorldVector(0, radDegs, 0));
			_theArea.extend(other);
			other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270),
					radDegs, 0));
			_theArea.extend(other);

			// create & extend to bottom right
			other = centre.add(new WorldVector(MWC.Algorithms.Conversions
					.Degs2Rads(180), radDegs, 0));
			_theArea.extend(other);
			other.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(90),
					radDegs, 0));
			_theArea.extend(other);
	}

	public void clearCalculatedOrigin()
	{
		_calculatedOrigin = null;
		_extCircle = null;
		_intCircle = null;
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

		if (_calculatedOrigin != null) {
			_extCircle = new CircleShape(_calculatedOrigin, _extRadius);
			_intCircle = new CircleShape(_calculatedOrigin, _intRadius);
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
				
				double radDegs = _extRadius.getValueIn(WorldDistance.DEGS);

				// sort out where to plot it
				if (_theLineLocation == MWC.GUI.Properties.LineLocationPropertyEditor.START)
				{
					WorldLocation topLeft = new WorldLocation(
							origin.add(new WorldVector(0, radDegs, 0)));
					topLeft.addToMe(new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(270),
							radDegs, 0));
					labelPos = topLeft;
				}
				else if (_theLineLocation == MWC.GUI.Properties.LineLocationPropertyEditor.END)
				{
					WorldLocation bottomRight = new WorldLocation(
							origin.add(new WorldVector(MWC.Algorithms.Conversions
									.Degs2Rads(180), radDegs, 0)));
					bottomRight.addToMe(new WorldVector(MWC.Algorithms.Conversions
							.Degs2Rads(90), radDegs, 0));
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
			
		// also plot the origin
		dest.fillRect(pt.x - 1, pt.y - 2, 3, 3);
		// paint circles
		if (_extCircle != null)
		{
			//_extCircle.setColor(getColor());
			_extCircle.paint(dest);
		}
		if (_intCircle != null)
		{
			//_intCircle.setColor(getColor());
			_intCircle.paint(dest);
		}
		
		// paint arc
		
		double radDegs = _extRadius.getValueIn(WorldDistance.DEGS);
		Color oldColor = dest.getBackgroundColor();
		
		paintArc(dest, origin, radDegs, getColor(), _left, _right);
		if (_inner != 0 && _outer != 0)
		{
			paintArc(dest, origin, radDegs * _inner / _outer, oldColor, _left, _right);
		}
		
		if (_isBeam)
		{
			paintArc(dest, origin, radDegs, getColor(), _left1, _right1);
			if (_inner1 != 0 && _outer1 != 0)
			{
				paintArc(dest, origin, radDegs * _inner1 / _outer1, oldColor, _left1, _right1);
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
			// get the range from the origin

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
}
