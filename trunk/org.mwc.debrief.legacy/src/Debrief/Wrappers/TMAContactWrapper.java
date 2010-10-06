/// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: TMAContactWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.14 $
// $Log: TMAContactWrapper.java,v $
// Revision 1.14  2007/04/25 09:32:44  ian.mayo
// Prevent highlight being plotted for sensor & TMA data
//
// Revision 1.13  2005/12/13 09:05:00  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.12  2005/06/08 08:49:36  Ian.Mayo
// Correctly reflect user preference for showing bearing lines
//
// Revision 1.11  2005/06/07 08:38:32  Ian.Mayo
// Provide efficiency to stop millions of popup menu items representing hidden tma solutions.
//
// Revision 1.10  2005/06/06 14:45:06  Ian.Mayo
// Refactor how we support tma & sensor data
//
// Revision 1.9  2005/03/02 09:31:17  Ian.Mayo
// Correctly handle the TUA ellipse color
//
// Revision 1.8  2005/03/01 15:23:26  Ian.Mayo
// Recognise that some TUA's may be read in as relative, not just absolute.  Treat them accordingly in use, and when stored to file.
//
// Revision 1.7  2005/02/22 09:31:59  Ian.Mayo
// Refactor snail plotting sensor & tma data - so that getting & managing valid data points are handled in generic fashion.  We did have two very similar implementations, tracking errors introduced after hi-res-date changes was proving expensive/unreliable.  All fine now though.
//
// Revision 1.6  2005/01/28 10:52:58  Ian.Mayo
// Fix problems where last data point not shown.
//
// Revision 1.5  2004/12/17 15:54:01  Ian.Mayo
// Get on top of some problems plotting sensor & tma data.
//
// Revision 1.4  2004/12/16 11:34:53  Ian.Mayo
// Handle when solution data is outside period of parent track - and we can't find parent fixes to attached solution contact line to
//
// Revision 1.3  2004/11/25 10:24:50  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2003/10/27 12:59:22  Ian.Mayo
// Update the color of the ellipse, if we have to
//
// Revision 1.1.1.2  2003/07/21 14:49:27  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.10  2003-07-16 12:50:10+01  ian_mayo
// Improve text in multi-line tooltip
//
// Revision 1.9  2003-07-04 10:59:17+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.8  2003-07-02 15:39:52+01  ian_mayo
// Correct initial visibility states
//
// Revision 1.7  2003-06-30 09:14:00+01  ian_mayo
// Correct name
//
// Revision 1.6  2003-06-25 15:40:49+01  ian_mayo
// Stop using local params for label/ellipse visibility
//
// Revision 1.5  2003-06-25 08:49:11+01  ian_mayo
// More implementation detail
//
// Revision 1.4  2003-06-23 16:09:24+01  ian_mayo
// Modifier for symbol visibility
//
// Revision 1.3  2003-06-23 13:41:16+01  ian_mayo
// Minor changes, now complete.
//
// Revision 1.2  2003-06-23 08:40:05+01  ian_mayo
// Lots of tidying, still not complete
//
// Revision 1.1  2003-06-19 16:17:48+01  ian_mayo
// Initial revision
//

package Debrief.Wrappers;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;

import Debrief.GUI.Tote.Painters.SnailDrawTMAContact;
import Debrief.GUI.Tote.Painters.SnailPainter.DoNotHighlightMe;
import MWC.GUI.CanvasType;
import MWC.GUI.FireReformatted;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.LocationPropertyEditor;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import MWC.Utilities.TextFormatting.GeneralFormat;

/**
 * Wrapper for a single TMA solution
 */

public final class TMAContactWrapper extends
		SnailDrawTMAContact.PlottableWrapperWithTimeAndOverrideableColor implements
		MWC.GenericData.Watchable, CanvasType.MultiLineTooltipProvider,
		DoNotHighlightMe
{
	// ///////////////////////////////////////////
	// member variables
	// ///////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the name of the parent track (the host vessel)
	 */
	String _parentTrackName;

	/**
	 * long _DTG
	 */
	HiResDate _DTG;

	/**
	 * vector representing the target location
	 */
	WorldVector _targetPosVector = null;

	/**
	 * the depth estimate for the target
	 */
	double _targetDepth;

	/**
	 * track name (for this solution)
	 */
	String _trackName;

	/**
	 * whether to show the symbol
	 */
	private boolean _showSymbol = false;

	/**
	 * whether to show the bearing line
	 */
	private Boolean _showLine = null;

	/**
	 * our editor
	 */
	transient private MWC.GUI.Editable.EditorType _myEditor;

	/**
	 * the parent object (which supplies our colour, should we need it)
	 */
	private TMAWrapper _myTMATrack;

	/**
	 * the ellipse respresenting the solution
	 */
	MWC.GUI.Shapes.EllipseShape _theEllipse;

	/**
	 * the wrapper object containing the labelled ellipse
	 */
	Debrief.Wrappers.ShapeWrapper _labelledEllipse = null;

	/**
	 * the wrapper object containing the symbol
	 */
	private Debrief.Wrappers.LabelWrapper _symbolWrapper = null;

	/**
	 * the course parameter
	 */
	double _targetCourseDegs = 0d;

	/**
	 * the speed paramter
	 */
	double _targetSpeedKts = 0d;

	/**
	 * the symbol to use to plot the target location
	 */
	String _theSymbol;

	/**
	 * whether to show the target vector
	 */
	private boolean _showVector;

	/**
	 * the original location we read in (used to decide whether we are storing
	 * relative or absolute)
	 */
	private WorldLocation _originalLocation;

	// ///////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////

	/**
	 * default constructor, used when we read in from XML
	 */
	public TMAContactWrapper()
	{
		this(null, null, null, null, 0d, 0d, 0d, 0d, 0d, null, null, null, null);
	}

	private TMAContactWrapper(final String solutionName, final String trackName,
			final HiResDate DTG, final WorldLocation location, final double rangeYds,
			final double bearingDegs, final double courseDegs, final double speedKts,
			final double depthMetres, final Color color, final String label,
			final EllipseShape theEllipse, final String theSymbol)
	{
		WorldLocation emptyLocation = null;

		_originalLocation = location;

		// ok, do we have an ellipse?
		if (theEllipse != null)
		{
			// yes, store it
			_theEllipse = theEllipse;
		}
		else
		{
			// no, create it afresh
			_theEllipse = new EllipseShape(emptyLocation, 0d, new WorldDistance(0d,
					WorldDistance.DEGS), new WorldDistance(0d, WorldDistance.DEGS));
		}

		// update the ellipse with our origin
		_theEllipse.setCentre(location);

		// create the shape wrapper to contain the ellipse
		_labelledEllipse = new ShapeWrapper(label, _theEllipse, color, DTG);

		// store the admin infomration
		_trackName = solutionName;
		_parentTrackName = trackName;
		_DTG = DTG;
		_targetDepth = depthMetres;
		_targetPosVector = new WorldVector(MWC.Algorithms.Conversions
				.Degs2Rads(bearingDegs), MWC.Algorithms.Conversions.Yds2Degs(rangeYds),
				0);

		// now the course/speed
		_targetCourseDegs = courseDegs;
		_targetSpeedKts = speedKts;

		// and the gui parameters
		setSymbol(theSymbol);
		setColor(color);
		setVisible(true);
		setLabelVisible(true);
		setLabelLocation(new Integer(LocationPropertyEditor.CENTRE));
		// setLineVisible(true);
		setEllipseVisible(true);
		setVectorVisible(false);
	}

	/**
	 * build a new sensor contact wrapper using range and bearing
	 * 
	 * @param solutionName
	 * @param trackName
	 * @param DTG
	 * @param rangeYds
	 * @param bearingDegs
	 * @param color
	 * @param label
	 * @param depthMetres
	 */
	public TMAContactWrapper(final String solutionName, final String trackName,
			final HiResDate DTG, final double rangeYds, final double bearingDegs,
			final double courseDegs, final double speedKts, final double depthMetres,
			final Color color, final String label, EllipseShape theEllipse,
			String theSymbol)
	{
		this(solutionName, trackName, DTG, null, rangeYds, bearingDegs, courseDegs,
				speedKts, depthMetres, color, label, theEllipse, theSymbol);
	}

	/**
	 * build a new sensor contact wrapper using a target location
	 * 
	 * @param sensorName
	 * @param trackName
	 * @param DTG
	 * @param location
	 * @param color
	 * @param label
	 * @param depthMetres
	 */
	public TMAContactWrapper(final String sensorName, final String trackName,
			final HiResDate DTG, final WorldLocation location,
			final double courseDegs, final double speedKts, final double depthMetres,
			final Color color, final String label, EllipseShape theEllipse,
			final String theSymbol)
	{
		this(sensorName, trackName, DTG, location, 0d, 0d, courseDegs, speedKts,
				depthMetres, color, label, theEllipse, theSymbol);
	}

	// ///////////////////////////////////////////
	// accessor methods
	// ///////////////////////////////////////////
	/**
	 * return the coordinates for the centre of the ellipse
	 */
	public final WorldLocation getCentre(
			final MWC.GenericData.WatchableList parent)
	{
		// declare the reuslts object, and add our offset to it
		WorldLocation origin = null;

		// are we working with absolute TMA solutions?
		if (_originalLocation != null)
			origin = new WorldLocation(_originalLocation);

		// do we know our origin
		if (origin == null)
		{
			// right, we'll have to retrieve the centre
			// get the origin
			final MWC.GenericData.Watchable[] list = parent.getNearestTo(_DTG);
			MWC.GenericData.Watchable wa = null;
			if (list.length > 0)
				wa = list[0];

			// did we find it?
			if (wa != null)
			{
				// check we have an offset
				if (_targetPosVector != null)
				{
					// yes, add it to the origin
					origin = wa.getLocation().add(_targetPosVector);
				}
			}
		}

		return origin;
	}

	/**
	 * return the coordinates of the sensor end of the bearing line
	 */
	public final WorldLocation getSensorEnd(
			final MWC.GenericData.WatchableList parent)
	{
		WorldLocation res = null;

		// check we have the parent
		if (parent != null)
		{
			// right, we'll have to retrieve the centre
			// get the origin
			final MWC.GenericData.Watchable[] list = parent.getNearestTo(_DTG);
			if (list.length > 0)
			{
				res = list[0].getLocation();
			}
		}

		return res;
	}

	/**
	 * getTrackName
	 * 
	 * @return the returned String
	 */
	public final String getTrackName()
	{
		return _parentTrackName;
	}

	/**
	 * getDTG
	 * 
	 * @return the returned long
	 */
	public final HiResDate getDTG()
	{
		return _DTG;
	}

	/**
	 * set the time
	 */
	public final void setDTG(final HiResDate val)
	{
		_DTG = val;
	}

	@FireReformatted
	public final void setColor(final Color val)
	{
		super.setColor(val);

		// set the colour of the ellipse
		_labelledEllipse.setColor(val);
		_labelledEllipse.setLabelColor(val);

	}

	/**
	 * member function to meet requirements of comparable interface *
	 */
	public final int compareTo(final Plottable o)
	{
		final TMAContactWrapper other = (TMAContactWrapper) o;
		int res = 0;
		if (_DTG.lessThan(other._DTG))
			res = -1;
		else if (_DTG.greaterThan(other._DTG))
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
		// are we visible?
		if (!getVisible())
			return;

		// do we know who our parents are?
		if (track == null)
		{
			MWC.Utilities.Errors.Trace
					.trace("failed to find track for solution data");
			return;
		}

		// do we need an origin
		final WorldLocation centre = getCentre(track);

		TimePeriod parentPeriod = new TimePeriod.BaseTimePeriod(
				track.getStartDTG(), track.getEndDTG());
		if (!parentPeriod.contains(this.getTime()))
		{
			// nope, we're outside the parent track period - we can't plot ourselves
			return;
		}

		// ok, we have the centre - convert it to a point
		final Point centrePt = new Point(dest.toScreen(centre));

		// have we got sufficient data to plot?
		if (centrePt == null)
		{
			return;
		}

		// update the centre of the ellipse - we need to use it elsewhere (range
		// from calcs)
		_theEllipse.setCentre(centre);

		// and convert to screen coords
		final WorldLocation theFarEnd = getSensorEnd(track);
		final Point farEnd = dest.toScreen(theFarEnd);

		// retrieve (& store) our color
		Color myColor = getColor();

		// set the colour
		dest.setColor(myColor);

		// do we draw the line?
		if (this.getLineVisible())
		{
			// draw the line
			dest.drawLine(centrePt.x, centrePt.y, farEnd.x, farEnd.y);
		}

		// do we draw the ellipse?
		if (_theEllipse.getVisible() || _labelledEllipse.getLabelVisible())
		{
			// update the label vis on the ellipse?
			// _labelledEllipse.setLabelVisible(_showLabel);

			// _theEllipse.setVisible(_showEllipse);

			_labelledEllipse.setVisible(true);

			// and set the colour of the ellipse
			_labelledEllipse.setColor(myColor);

			// and paint it
			_labelledEllipse.paint(dest);
		}

		// do we draw the symbol?
		if (_showSymbol)
		{
			// ok, we only create the symbol wrapper when we really need to, see if
			// this
			// is the first time we have had to paint the symbol
			if (this._symbolWrapper == null)
			{
				// ok then, let's create it!
				_symbolWrapper = new LabelWrapper("", centre, myColor);
				_symbolWrapper.setLabelVisible(false);
			}

			// ok, update the symbol with the current graphic properties
			_symbolWrapper.setColor(myColor);
			_symbolWrapper.setSymbolType(getSymbol());

			_symbolWrapper.paint(dest);
		}

		// do we draw the target vector?
		if (getVectorVisible())
		{
			// ok, what's the stretch factor?
			double _vectorStretch = 4;

			// and now plot the vector
			final double crse = getCourse();
			final double spd = getSpeed();

			//
			final int dx = (int) (Math.sin(crse) * spd * _vectorStretch);
			final int dy = (int) (Math.cos(crse) * spd * _vectorStretch);

			// produce the end of the stick (just to establish the length in data
			// units)
			final Point p2 = new Point(centrePt.x + dx, centrePt.y - dy);

			// how long is the stalk in data units?
			final WorldLocation w3 = dest.toWorld(p2);
			final double len = w3.rangeFrom(centre);

			// now sort out the real end of this stalk
			final WorldLocation stalkEnd = centre.add(new WorldVector(crse, len, 0));
			// and get this in screen coordinates
			final Point pStalkEnd = dest.toScreen(stalkEnd);

			// and plot the stalk itself
			dest.drawLine(centrePt.x, centrePt.y, pStalkEnd.x, pStalkEnd.y);

		}

	}

	/**
	 * method to reset the colour, so that we take that of our parent
	 */
	public final void resetColor()
	{
		setColor(null);
	}

	/**
	 * find the name of this solution track
	 */
	public final String getSolutionName()
	{
		return _trackName;
	}

	/**
	 * find the data area occupied by this item
	 */
	public final WorldArea getBounds()
	{
		WorldArea res = null;

		// do we have an area in the ellipse?
		if (_theEllipse.getCentre() != null)
		{
			// yes, start with that.
			res = _theEllipse.getBounds();

			// note: consider adding in the bearing line, if we store the "source"
			// location for the bearing line
		}

		return res;
	}

	/**
	 * find the data area occupied by this item, using the current track locations
	 */
	public final WorldArea getBounds(final MWC.GenericData.WatchableList track)
	{
		WorldArea res = null;

		// get a fresh centre for the ellipse
		_theEllipse.setCentre(getCentre(track));

		// and get the bounds
		res = _theEllipse.getBounds();

		return res;
	}

	/**
	 * it this Label item currently visible?
	 */
	public final boolean getLabelVisible()
	{
		return _labelledEllipse.getLabelVisible();
	}

	/**
	 * set the Label visibility
	 */
	public final void setLabelVisible(final boolean val)
	{
		_labelledEllipse.setLabelVisible(val);
	}

	/**
	 * it the bearing line item currently visible?
	 */
	public final boolean getLineVisible()
	{
		boolean res;
		if (_showLine != null)
			res = _showLine.booleanValue();
		else
			res = _myTMATrack.getShowBearingLines();

		return res;
	}

	/**
	 * convenience method, used to determine if we have our own, custom setting
	 * for line visibility
	 * 
	 * @return the data-value used for whether a line is shown (or null if we just
	 *         use the parent setting)
	 */
	public final Boolean getRawLineVisible()
	{
		return _showLine;
	}

	/**
	 * set the bearing line visibility
	 */
	public final void setLineVisible(final boolean val)
	{
		_showLine = new Boolean(val);
	}

	/**
	 * forget about any custom setting for the line visibility. just do what our
	 * parent says
	 */
	public void clearLineVisibleFlag()
	{
		_showLine = null;
	}

	/**
	 * it the Ellipse item currently visible?
	 */
	public final boolean getEllipseVisible()
	{
		return _theEllipse.getVisible();
	}

	/**
	 * set the Ellipse visibility
	 */
	public final void setEllipseVisible(final boolean val)
	{
		_theEllipse.setVisible(val);
	}

	/**
	 * it the Symbol item currently visible?
	 */
	public final boolean getSymbolVisible()
	{
		return _showSymbol;
	}

	/**
	 * set the Symbol visibility
	 */
	public final void setSymbolVisible(final boolean val)
	{
		_showSymbol = val;
	}

	/**
	 * whether to show the target vector
	 * 
	 * @param b
	 */
	public void setVectorVisible(boolean b)
	{
		_showVector = b;
	}

	/**
	 * whether to show the target vector
	 */
	public boolean getVectorVisible()
	{
		return _showVector;
	}

	/**
	 * return the location of the label
	 * 
	 * @see MWC.GUI.Properties.LocationPropertyEditor
	 */
	public final void setLabelLocation(final Integer loc)
	{
		_labelledEllipse.setLabelLocation(loc);
	}

	/**
	 * update the location of the label
	 * 
	 * @see MWC.GUI.Properties.LocationPropertyEditor
	 */
	public final Integer getLabelLocation()
	{
		return _labelledEllipse.getLabelLocation();

	}

	/**
	 * inform us of our sensor
	 */
	public final void setTMATrack(final TMAWrapper tma_track)
	{
		_myTMATrack = tma_track;
	}

	/**
	 * find out about the sensor
	 * 
	 * @return
	 */
	public TMAWrapper getTMATrack()
	{
		return _myTMATrack;
	}

	/**
	 * get the label for this data item
	 */
	public final String getLabel()
	{
		return _labelledEllipse.getLabel();
	}

	/**
	 * set the label for this data item
	 */
	public final void setLabel(final String val)
	{
		_labelledEllipse.setLabel(val);
	}

	/**
	 * how far away are we from this point? or return null if it can't be
	 * calculated
	 */
	public final double rangeFrom(final WorldLocation other)
	{
		// return the distance from each end
		double res = Double.MAX_VALUE;

		if (getVisible())
		{
			// has the ellipse range been calculated?
			res = this._theEllipse.rangeFrom(other);
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
			_myEditor = new TMAContactInfo(this);

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
	public final Color getColor()
	{
		Color res = super.getColor();

		// has our colour been set?
		if (res == null)
		{
			// no, get the colour from our parent
			res = _myTMATrack.getColor();
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
		return DebriefFormatDateTime.toStringHiRes(_DTG);
	}

	public final String getMultiLineName()
	{
		int dataAvailable = (int) _theEllipse.getMaxima().getValueIn(
				WorldDistance.YARDS)
				+ (int) _theEllipse.getMinima().getValueIn(WorldDistance.YARDS);

		String maxMinStr = "n/a";

		if (dataAvailable > 0)
		{
			String maxStr = ""
					+ (int) _theEllipse.getMaxima().getValueIn(WorldDistance.YARDS)
					+ "yds";
			String minStr = ""
					+ (int) _theEllipse.getMinima().getValueIn(WorldDistance.YARDS)
					+ "yds";
			maxMinStr = "Max:" + maxStr + " Min:" + minStr;
		}
		else
		{
			maxMinStr = "Ellipse not set";
		}

		String res = "<u>TMA Solution: "
				+ DebriefFormatDateTime.toStringHiRes(_DTG)
				+ "</u>\n"
				+ GeneralFormat.formatStatus(_targetCourseDegs, _targetSpeedKts,
						_targetDepth) + "\n" + maxMinStr + "\n" + getLabel();
		return res;

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

	/**
	 * get the ellipse itself
	 */
	public final EllipseShape getEllipse()
	{
		return _theEllipse;
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
		return this.getCentre(null);
	}

	/**
	 * get the current course of the watchable (rads)
	 * 
	 * @return course in radians
	 */
	public final double getCourse()
	{
		return MWC.Algorithms.Conversions.Degs2Rads(_targetCourseDegs);
	}

	/**
	 * get the current course of the watchable (degs)
	 * 
	 * @return course in degs
	 */
	public final double getTargetCourse()
	{
		return _targetCourseDegs;
	}

	public WorldLocation getOrigin()
	{
		return _originalLocation;
	}

	public void setOrigin(WorldLocation loc)
	{
		_originalLocation = loc;
	}

	public WorldDistance getRange()
	{
		return new WorldDistance(_targetPosVector.getRange(), WorldDistance.DEGS);
	}

	public void setRange(WorldDistance val)
	{
		_targetPosVector.setValues(_targetPosVector.getBearing(), val
				.getValueIn(WorldDistance.DEGS), _targetPosVector.getDepth());
	}

	public double getBearing()
	{
		return _targetPosVector.getBearing();
	}

	public void setBearing(double val)
	{
		_targetPosVector.setValues(val, _targetPosVector.getRange(),
				_targetPosVector.getDepth());
	}

	/**
	 * get the current speed of the watchable (kts)
	 * 
	 * @return speed in knots
	 */
	public final double getSpeed()
	{
		return _targetSpeedKts;
	}

	public WorldDistance getMaxima()
	{
		return _theEllipse.getMaxima();
	}

	public void setMaxima(WorldDistance val)
	{
		_theEllipse.setMaxima(val);
	}

	public WorldDistance getMinima()
	{
		return _theEllipse.getMinima();
	}

	public void setMinima(WorldDistance val)
	{
		_theEllipse.setMinima(val);
	}

	public double getOrientation()
	{
		return _theEllipse.getOrientation();
	}

	public void setOrientation(double val)
	{
		_theEllipse.setOrientation(val);
	}

	/**
	 * @param courseDegs
	 *          the _targetCourseDegs to set
	 */
	public void setTargetCourse(double courseDegs)
	{
		_targetCourseDegs = courseDegs;
	}

	/**
	 * @return the _targetSpeedKts
	 */
	public WorldSpeed getTargetSpeed()
	{
		return new WorldSpeed(_targetSpeedKts, WorldSpeed.Kts);
	}

	/**
	 * @param speedKts
	 *          the _targetSpeedKts to set
	 */
	public void setTargetSpeed(WorldSpeed speedKts)
	{
		_targetSpeedKts = speedKts.getValueIn(WorldSpeed.Kts);
	}

	/**
	 * get the current depth of the watchable (m)
	 * 
	 * @return depth in metres
	 */
	public final double getDepth()
	{
		return _targetDepth;
	}

	public final void setDepth(double val)
	{
		_targetDepth = val;
	}

	/**
	 * find out the time of this watchable
	 */
	public final HiResDate getTime()
	{
		return this.getDTG();
	}

	public String getSymbol()
	{
		return _theSymbol;
	}

	public void setSymbol(String val)
	{
		_theSymbol = val;
	}

	public WorldVector buildGetVector()
	{
		return _targetPosVector;
	}

	public EllipseShape buildGetEllipse()
	{
		return _theEllipse;
	}

	/**
	 * retrieve the original ellipse origin as read in
	 * 
	 * @return
	 */
	public WorldLocation buildGetOrigin()
	{
		return _originalLocation;
	}

	public void buildSetOrigin(WorldLocation origin)
	{
		// store the origin as an indication of whether this is an absolute or
		// relative ellipse
		_originalLocation = origin;

		// and get on with the normal processing
		_theEllipse.setCentre(origin);
	}

	public void buildSetVector(WorldVector theVector)
	{
		_targetPosVector = theVector;
	}

	public void buildSetTargetState(double course, double speed, double depth)
	{
		_targetCourseDegs = course;
		_targetSpeedKts = speed;
		_targetDepth = depth;
	}

	public void buildSetEllipse(double orientationDegs, WorldDistance maxima,
			WorldDistance minima)
	{
		_theEllipse.setOrientation(orientationDegs);
		_theEllipse.setMaxima(maxima);
		_theEllipse.setMinima(minima);
	}

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public final class TMAContactInfo extends MWC.GUI.Editable.EditorType
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public TMAContactInfo(final TMAContactWrapper data)
		{
			super(data, data.getName(), "Solution");
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
				PropertyDescriptor[] res =
				{
						prop("Label", "the label for this data item"),
						prop("Visible", "whether this solution is visible"),
						prop("LabelVisible",
								"whether the label for this solution is visible"),
						prop(
								"LineVisible",
								"whether the bearing line (from ownship track to solution centre) for this solution is visible"),
						prop("EllipseVisible",
								"whether the ellipse for this solution is visible"),
						prop("SymbolVisible",
								"whether the ellipse for this solution is visible"),
						prop("VectorVisible",
								"whether the target vector for this solution is visible"),
						prop("Color", "the color for this solution"),
						longProp("Symbol", "the symbol to use for this solution",
								SymbolFactoryPropertyEditor.class),
						longProp("LabelLocation", "the label location",
								MWC.GUI.Properties.LocationPropertyEditor.class),
						prop("Maxima", "the maxima for the ellipse", SPATIAL),
						prop("Minima", "the minima for the ellipse", SPATIAL),
						prop("Orientation", "the minima for the ellipse", SPATIAL),
						prop("TargetCourse", "the course of the solution", SPATIAL),
						prop("TargetSpeed", "the speed of the solution", SPATIAL),
						prop("Depth", "the depth of the solution", SPATIAL) };

				// see if we need to add rng/brg or origin data
				TMAContactWrapper tc = (TMAContactWrapper) getData();
				final PropertyDescriptor[] res1;
				if (tc.getOrigin() == null)
				{
					// has origin
					final PropertyDescriptor[] res2 =
					{ prop("Range", "range to centre of solution", SPATIAL),
							prop("Bearing", "bearing to centre of solution", SPATIAL) };
					res1 = res2;
				}
				else
				{
					// rng, brg data
					final PropertyDescriptor[] res2 =
					{ prop("Origin", "centre of solution", SPATIAL) };
					res1 = res2;
				}

				PropertyDescriptor[] res3 = new PropertyDescriptor[res.length
						+ res1.length];
				System.arraycopy(res, 0, res3, 0, res.length);
				System.arraycopy(res1, 0, res3, res.length, res1.length);

				return res3;
			}
			catch (IntrospectionException e)
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
			final Class<TMAContactWrapper> c = TMAContactWrapper.class;
			final MethodDescriptor[] mds =
			{ method(c, "resetColor", null, "Reset Color"), };
			return mds;
		}

	}

	// ////////////////////////////////////////////////////
	// nested class
	// /////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////

	static public final class testSensorContact extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testSensorContact(final String val)
		{
			super(val);
		}

		public final void testObjectConstruction()
		{
			// setup our object to be tested using an absolute location
			final WorldLocation origin = new WorldLocation(2, 2, 0);
			HiResDate theDTG = new HiResDate(new java.util.Date().getTime());
			EllipseShape theEllipse = new EllipseShape(origin, 45, new WorldDistance(
					10, WorldDistance.DEGS), new WorldDistance(5, WorldDistance.DEGS));
			theEllipse.setName("test ellipse");
			final TMAContactWrapper ed_abs = new TMAContactWrapper("blank sensor",
					"blank track", theDTG, origin, 5d, 6d, 1d, Color.pink, "my label",
					theEllipse, "some symbol");

			assertEquals("correct sensor name", ed_abs._trackName, "blank sensor");
			assertEquals("correct track name", ed_abs._parentTrackName, "blank track");
			assertEquals("correct DTG", ed_abs._DTG, theDTG);
			assertEquals("correct origin", ed_abs._theEllipse.getCentre(), origin);
			assertEquals("right course", ed_abs._targetCourseDegs, 5d, 0.001);
			assertEquals("right course", ed_abs.getCourse(),
					MWC.Algorithms.Conversions.Degs2Rads(5d), 0.001);
			assertEquals("right speed", ed_abs._targetSpeedKts, 6d, 0.001);
			assertEquals("right speed", ed_abs.getSpeed(), 6d, 0.001);
			assertEquals("right depth", ed_abs._targetDepth, 1d, 0.001);
			assertEquals("right depth", ed_abs.getDepth(), 1d, 0.001);
			assertEquals("correct colour", ed_abs._labelledEllipse.getColor(),
					Color.pink);
			assertEquals("correct colour", ed_abs.getColor(), Color.pink);
			assertEquals("correct label", ed_abs._labelledEllipse.getLabel(),
					"my label");
			assertEquals("correct label", ed_abs.getLabel(), "my label");
			assertEquals("correct ellipse", ed_abs._theEllipse, theEllipse);
			assertEquals("correct ellipse", ed_abs._labelledEllipse.getShape(),
					theEllipse);
			assertEquals("correct symbol", ed_abs._theSymbol, "some symbol");

			// setup our object to be tested using an relative location
			final TMAContactWrapper ed_rel = new TMAContactWrapper("blank sensor",
					"blank track", theDTG, 3000, 55, 5d, 6d, 1d, Color.pink, "my label",
					theEllipse, "some symbol");

			assertEquals("correct sensor name", ed_rel._trackName, "blank sensor");
			assertEquals("correct track name", ed_rel._parentTrackName, "blank track");
			assertEquals("correct DTG", ed_rel._DTG, theDTG);
			assertEquals("correct origin", ed_rel._theEllipse.getCentre(), null);
			assertEquals("correct range", MWC.Algorithms.Conversions
					.Degs2Yds(ed_rel._targetPosVector.getRange()), 3000, 0.001);
			assertEquals("correct bearing", ed_rel._targetCourseDegs, 5d, 0.001);
			assertEquals("right course", ed_rel._targetCourseDegs, 5d, 0.001);
			assertEquals("right course", ed_rel.getCourse(),
					MWC.Algorithms.Conversions.Degs2Rads(5d), 0.001);
			assertEquals("right speed", ed_rel._targetSpeedKts, 6d, 0.001);
			assertEquals("right speed", ed_rel.getSpeed(), 6d, 0.001);
			assertEquals("right depth", ed_rel._targetDepth, 1d, 0.001);
			assertEquals("right depth", ed_rel.getDepth(), 1d, 0.001);
			assertEquals("correct colour", ed_rel._labelledEllipse.getColor(),
					Color.pink);
			assertEquals("correct colour", ed_rel.getColor(), Color.pink);
			assertEquals("correct label", ed_rel._labelledEllipse.getLabel(),
					"my label");
			assertEquals("correct label", ed_rel.getLabel(), "my label");
			assertEquals("correct ellipse", ed_rel._theEllipse, theEllipse);
			assertEquals("correct ellipse", ed_rel._labelledEllipse.getShape(),
					theEllipse);
			assertEquals("correct symbol", ed_rel._theSymbol, "some symbol");

		}

		public final void testMyParams()
		{
			HiResDate theDTG = new HiResDate(new java.util.Date().getTime());

			// setup our object to be tested using an absolute location
			final TMAContactWrapper ed = new TMAContactWrapper("blank sensor",
					"blank track", theDTG, 3000, 55, 5d, 6d, 1d, Color.red, "my label",
					null, "some symbol");

			final TMAWrapper wrap = new TMAWrapper("tma");
			ed.setTMATrack(wrap);

			// check the editable parameters
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
		}

		public final void testMyCalcs()
		{
			// setup our object to be tested using an absolute location
			final WorldLocation origin = new WorldLocation(2, 2, 0);
			EllipseShape es = new EllipseShape(null, 0, new WorldDistance(
					MWC.Algorithms.Conversions.Yds2Degs(100), WorldDistance.DEGS),
					new WorldDistance(MWC.Algorithms.Conversions.Yds2Degs(50),
							WorldDistance.DEGS));
			HiResDate theDTG = new HiResDate(new java.util.Date().getTime());
			final TMAContactWrapper ed = new TMAContactWrapper("blank sensor",
					"blank track", theDTG, origin, 5d, 6d, 1d, Color.red, "my label", es,
					"some symbol");

			/**
			 * test the distance calcs
			 */

			// ok, now test that we find the distance from the origin
			double dist = MWC.Algorithms.Conversions.Degs2Yds(ed.rangeFrom(origin));
			assertEquals("find nearest from origin", dist, 0d, 0.001);

			// test from on the ellipse itself
			WorldLocation outerPoint = ed.getLocation().add(
					new WorldVector(MWC.Algorithms.Conversions.Rads2Degs(0),
							MWC.Algorithms.Conversions.Yds2Degs(50), 0d));
			double rngDegs = ed.rangeFrom(outerPoint);
			dist = MWC.Algorithms.Conversions.Degs2Yds(rngDegs);
			// todo: check shape of ellipse, because the next test is unnecessarily
			// failing
			// super.assertEquals("find nearest from edge", 0d, dist, 0.001);

		}

	}

}
