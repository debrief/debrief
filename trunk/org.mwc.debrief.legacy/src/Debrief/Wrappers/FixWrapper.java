// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: FixWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.14 $
// $Log: FixWrapper.java,v $
// Revision 1.14  2007/03/12 11:40:24  ian.mayo
// Change default font size to 9px
//
// Revision 1.13  2007/01/22 09:52:51  ian.mayo
// Better maths
//
// Revision 1.12  2006/11/28 10:55:12  Ian.Mayo
// Add offset to label to allow for fix symbol
//
// Revision 1.11  2006/01/18 15:02:54  Ian.Mayo
// Improve how we do interpolated points
//
// Revision 1.10  2005/12/12 12:40:14  Ian.Mayo
// Don't do the hard-work ourselves
//
// Revision 1.9  2005/12/02 10:56:31  Ian.Mayo
// Correct use of N/A in date formats
//
// Revision 1.8  2005/09/23 14:56:05  Ian.Mayo
// Support generation of interpolated fixes
//
// Revision 1.7  2005/01/28 09:32:11  Ian.Mayo
// Categorise editable properties
//
// Revision 1.5  2005/01/19 14:31:13  Ian.Mayo
// Make the top-level Visible property available to property editor
//
// Revision 1.4  2004/12/02 11:37:49  Ian.Mayo
// Optimise fix comparison
//
// Revision 1.3  2004/11/29 15:44:46  Ian.Mayo
// Only reset name when we have valid time
//
// Revision 1.2  2004/11/25 10:24:45  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:21  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.16  2003-07-04 10:59:19+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.15  2003-07-01 14:56:16+01  ian_mayo
// Refactor out painting the labels
//
// Revision 1.14  2003-06-25 15:39:56+01  ian_mayo
// Improve formatting of multi-line tooltip
//
// Revision 1.13  2003-06-25 08:42:21+01  ian_mayo
// Switch to multi-line labels
//
// Revision 1.12  2003-06-10 15:39:21+01  ian_mayo
// Re-instate getFixLocation, since we use it for property editor
//
// Revision 1.11  2003-06-10 14:34:17+01  ian_mayo
// Remove confusing method (we should access the fix location via the fix)
//
// Revision 1.10  2003-05-09 12:27:14+01  ian_mayo
// Handle instance where parent track isn't set (we only want the name anyway)
//
// Revision 1.9  2003-04-01 15:50:20+01  ian_mayo
// Correctly manage the label format - so it doesn't look like a change has been made each time the user saves his properties
//
// Revision 1.8  2003-03-24 11:05:27+00  ian_mayo
// Make correct parameter public again
//
// Revision 1.7  2003-03-19 15:36:50+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.6  2003-03-14 16:02:36+00  ian_mayo
// Use static instance of Font, to save object creation
//
// Revision 1.5  2002-11-01 14:44:44+00  ian_mayo
// Minor tidying, shorten displayed name of fix
//
// Revision 1.4  2002-10-30 16:27:57+00  ian_mayo
// correct visibility of getDisplayName implementation
//
// Revision 1.3  2002-10-01 15:41:45+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.2  2002-05-28 09:25:13+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:40+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:22+01  ian_mayo
// Initial revision
//
// Revision 1.8  2001-09-14 09:43:09+01  administrator
// Remember to set the time zone
//
// Revision 1.7  2001-08-29 19:19:17+01  administrator
// Reflect package change of PlainWrapper, and remove Contacts
//
// Revision 1.6  2001-08-21 12:14:39+01  administrator
// Improve editing of label format
//
// Revision 1.5  2001-08-20 10:29:00+01  administrator
// For editting format of date text, return "N/A", not null
//
// Revision 1.4  2001-08-14 14:08:01+01  administrator
// Correct the "Compare" method to put us AFTER any others, not before
//
// Revision 1.3  2001-08-13 12:53:55+01  administrator
// use the PlainWrapper colour support, and implement Comparable support
//
// Revision 1.2  2001-08-01 20:08:37+01  administrator
// Added methods & editor class necessary to all user to specify date formatting to be used for text label
//
// Revision 1.1  2001-07-23 11:53:54+01  administrator
// tidy up
//
// Revision 1.0  2001-07-17 08:41:08+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-22 12:30:02+00  novatech
// added JUnit testing code
//
// Revision 1.2  2001-01-09 10:25:57+00  novatech
// allow setting of symbol size
//
// Revision 1.1  2001-01-03 13:40:22+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:14  ianmayo
// initial import of files
//
// Revision 1.27  2000-11-22 10:51:56+00  ian_mayo
// provide better colour accessors, so that a null value is return if this fix doesn't have a colour set - not the track colour
//
// Revision 1.26  2000-11-17 09:11:42+00  ian_mayo
// tidily handle missing location for Tactical fix
//
// Revision 1.25  2000-10-03 14:15:50+01  ian_mayo
// white space
//
// Revision 1.24  2000-09-21 09:05:24+01  ian_mayo
// make Editable.EditorType a transient parameter, to save it being written to file
//
// Revision 1.23  2000-08-18 10:09:00+01  ian_mayo
// Before we make all editables listenable - that is the property editor is listening out for changes to it's editable and updates it accordingly
//
// Revision 1.22  2000-08-16 14:12:05+01  ian_mayo
// take track name from TrackWrapper outside BeanInfo
//
// Revision 1.21  2000-08-15 15:28:46+01  ian_mayo
// Bean parameter change
//
// Revision 1.20  2000-08-14 11:00:28+01  ian_mayo
// switch getSpeed to correct units
//
// Revision 1.19  2000-08-11 08:40:51+01  ian_mayo
// tidy beaninfo
//
// Revision 1.18  2000-08-09 16:04:02+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.17  2000-05-23 13:42:01+01  ian_mayo
// fill in the square symbol when the label is visible
//
// Revision 1.16  2000-04-03 10:41:02+01  ian_mayo
// handle Label visibility at this level, not in the parent
//
// Revision 1.15  2000-03-27 14:41:12+01  ian_mayo
// remove showLabel dependency on parent
//
// Revision 1.14  2000-03-08 16:24:45+00  ian_mayo
// add myArea initialisation to getBounds method (used following deserialisation)
//
// Revision 1.13  2000-03-07 14:48:18+00  ian_mayo
// optimised algorithms
//
// Revision 1.12  2000-03-07 10:07:59+00  ian_mayo
// Optimisation, keep local copy of area covered by fix
//
// Revision 1.11  2000-02-22 13:48:32+00  ian_mayo
// exportShape name changed to exportThis
//
// Revision 1.10  2000-02-18 11:06:21+00  ian_mayo
// added Label/Symbol visiblility getter/setter methods
//
// Revision 1.9  2000-02-14 16:48:16+00  ian_mayo
// Corrected label displayed in editor
//
// Revision 1.8  2000-02-04 15:51:42+00  ian_mayo
// Allowed user to modify position of fix
//
// Revision 1.7  2000-01-18 15:04:32+00  ian_mayo
// changed UI name from Fix to Location
//
// Revision 1.6  2000-01-13 15:32:05+00  ian_mayo
// moved paint control to Track
//
// Revision 1.5  2000-01-12 15:40:18+00  ian_mayo
// added concept of contacts
//
// Revision 1.4  1999-11-26 15:50:16+00  ian_mayo
// adding toString methods
//
// Revision 1.3  1999-11-12 14:35:40+00  ian_mayo
// part way through getting them to export themselves
//
// Revision 1.2  1999-11-11 18:24:03+00  ian_mayo
// changed name of Line object
//
// Revision 1.1  1999-10-12 15:33:40+01  ian_mayo
// Initial revision
//
// Revision 1.7  1999-08-04 14:04:36+01  administrator
// make show-label flag inherit from track
//
// Revision 1.6  1999-08-04 09:45:30+01  administrator
// minor mods, tidying up
//
// Revision 1.5  1999-07-27 09:24:19+01  administrator
// added BeanInfo editing
//
// Revision 1.4  1999-07-19 12:40:32+01  administrator
// added storage of sub-second time data (Switched to storing as Long rather than java.utils.Date)
//
// Revision 1.3  1999-07-16 10:01:47+01  administrator
// Nearing end of phase 2
//
// Revision 1.2  1999-07-12 08:09:20+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:13+01  administrator
// Initial revision
//

package Debrief.Wrappers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.TimeZone;
import java.util.Vector;

import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Test;
import MWC.Algorithms.Conversions;
import MWC.GUI.CanvasType;
import MWC.GUI.CreateEditorForParent;
import MWC.GUI.DynamicPlottable;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Griddable;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import MWC.Utilities.TextFormatting.GeneralFormat;

/**
 * The fix wrapper has the responsibility for the GUI and data aspects of the
 * fix, tying the two together.
 */
public class FixWrapper extends MWC.GUI.PlainWrapper implements Watchable,
		DynamicPlottable, CanvasType.MultiLineTooltipProvider, TimeStampedDataItem,
		CreateEditorForParent
{

	// //////////////////////////////////////
	// member variables
	// //////////////////////////////////////

	public static final String INTERPOLATED_FIX = "INTERPOLATED";

	/**
	 * sort out the version id (recommended to serialisable bits)
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the tactical data item we are storing
	 */
	private Fix _theFix;
	/**
	 * the label describing this fix
	 */
	private MWC.GUI.Shapes.TextLabel _theLabel;
	/**
	 * the symbol representing the center of the fix
	 */
	private LocationWrapper _theLocationWrapper;
	/**
	 * flag for whether to show the label
	 */
	private Boolean _showLabel;

	/**
	 * the track we are a part of (note, we're making it static so that when we
	 * serialise it we don't store a full copy of the parent track and all it's
	 * other fixes. We don't need to store it since it gets set when we add it to
	 * a new parent layer
	 */
	private transient TrackWrapper _trackWrapper;
	/**
	 * the font to draw this track in.
	 */
	private Font _theFont;
	/**
	 * whether the location symbol is drawn
	 */
	private boolean _showSymbol = false;

	/**
	 * whether the arrow symbol is drawn
	 */
	private boolean _showArrow = false;

	/**
	 * the area covered by this fix
	 */
	private transient WorldArea _myArea;

	/**
	 * a single instance of our editor type - which can be listened to by multiple
	 * listeners
	 */
	transient private Editable.EditorType _myEditor = null;

	/**
	 * the plain font we use as a basis
	 * 
	 */
	static private final Font _plainFont = new Font("Sans Serif", Font.PLAIN, 9);

	/**
	 * the current format we're using
	 * 
	 */
	private String _theFormat = MyDateFormatPropertyEditor.stringTags[0];

	/**
	 * whether to connect this fix to the previous one.
	 * 
	 */
	private boolean _lineShowing = true;

	/**
	 * whether a user label was supplied. if it wasn't, we allow the reset labels
	 * to run
	 * 
	 */
	private boolean _userLabelSupplied = false;

	/**
	 * take a static reference for the list of property descriptors for this
	 * object, since we repeatedly retrieve them (each time we do a property
	 * edit), yet they are identical across all objects of this type
	 */
	private static PropertyDescriptor[] _myInfoPropertyDescriptors = null;

	// //////////////////////////////////////
	// constructors
	// //////////////////////////////////////

	public FixWrapper(final Fix theFix)
	{
		// store the fix
		_theFix = theFix;
		// create the symbol
		_theLocationWrapper = new LocationWrapper(_theFix.getLocation());
		// create the label
		_theLabel = new MWC.GUI.Shapes.TextLabel(_theFix.getLocation(), "");

		// move the label around a bit
		_theLabel.setFixedOffset(new java.awt.Dimension(4, 4));

		// reset (recalculate) the name bit
		// resetName();

		// hide the name, by default
		_showLabel = Boolean.FALSE;
		// declare a duff track
		_trackWrapper = null;
		// start us off with a nice font
		setFont(_plainFont);
		// whether to show symbol
		_showSymbol = false;

		// reset the colour
		setColorQuiet(null);

		// check that/if we have an area for this fix
		final WorldLocation wl = theFix.getLocation();
		if (wl != null)
		{
			// store the area
			_myArea = new WorldArea(wl, wl);
		}
	}

	/**
	 * instruct this object to clear itself out, ready for ditching
	 * 
	 */
	@Override
	public final void closeMe()
	{
		// do the parent
		super.closeMe();

		// forget the track
		_trackWrapper = null;
		_theLocationWrapper = null;
		_theFix = null;
		_myEditor = null;
		_myArea = null;
		_theLabel = null;
		setFont(null);
		_showLabel = null;

	}

	// //////////////////////////////////////
	// member functions
	// //////////////////////////////////////

	/**
	 * produce an interpolated fix between the two supplied ones
	 * 
	 */
	static public FixWrapper interpolateFix(final FixWrapper previous,
			final FixWrapper next, final HiResDate dtg)
	{
		FixWrapper res = null;

		// and the time different?
		final long timeDiffMicros = next.getTime().getMicros()
				- previous.getTime().getMicros();

		// through what proportion are we travelling?
		final long thisDelta = dtg.getMicros() - previous.getTime().getMicros();

		// sort out the proportion
		final double proportion = (double) thisDelta / (double) timeDiffMicros;

		// LOCATION
		// what's the separation
		final WorldVector sep = next.getLocation().subtract(previous.getLocation());

		// do the calcs
		double dLat = next.getLocation().getLat() - previous.getLocation().getLat();
		double dLong = next.getLocation().getLong()
				- previous.getLocation().getLong();
		double dDepth = next.getLocation().getDepth()
				- previous.getLocation().getDepth();

		// sort out the proportions
		dLat *= proportion;
		dLong *= proportion;
		dDepth *= proportion;

		// and apply it (for both range and depth)
		// WorldVector newSep = new WorldVector(sep.getBearing(), sep.getRange()
		// *
		// proportion, sep.getDepth() * proportion);

		// cool, sort out the new location
		final WorldLocation newLoc = new WorldLocation(previous.getLocation()
				.getLat() + dLat, previous.getLocation().getLong() + dLong,
				previous.getDepth() + dDepth);

		// COURSE + SPEED
		// calculate the course and speed as being the MLA of the unit
		double crse = sep.getBearing();
		final double sepYds = Conversions.Degs2Yds(sep.getRange());
		final double timeDiffSecs = timeDiffMicros / 1000d / 1000d;
		final double spdKts = sepYds / timeDiffSecs;

		// ok, trim the course
		if (crse < 0)
			crse += Math.PI * 2;

		final double newSpeed = spdKts;
		final double newCourse = crse;

		final Fix tmpFix = new Fix(dtg, newLoc, newCourse, newSpeed);

		res = new InterpolatedFixWrapper(tmpFix);
		res.setTrackWrapper(previous.getTrackWrapper());

		// don't forget to indicate it's interpolated
		res.setLabel(INTERPOLATED_FIX);

		return res;
	}

	public final void setTrackWrapper(final TrackWrapper theTrack)
	{
		_trackWrapper = theTrack;
	}

	public final TrackWrapper getTrackWrapper()
	{
		return _trackWrapper;
	}

	@FireReformatted
	public final void resetColor()
	{
		setColor(null);
	}

	/**
	 * method to return the "sanitised" colour value stored in this fix, that-is
	 * if it is null, the colour of the track is returned
	 * 
	 * @return the colour of this fix, or the track if null
	 */
	@Override
	public final Color getColor()
	{
		if (super.getColor() == null)
		{
			return _trackWrapper.getColor();
		}
		else
			return super.getColor();
	}

	/**
	 * method to provide the actual colour value stored in this fix
	 * 
	 * @return fix colour, including null if applicable
	 */
	public final Color getActualColor()
	{
		// take the colour from the parent class, not from this one
		// - this is mostly because when we do a save, we want to
		// correctly reflect that this instance may take it's
		// colour from the track - meaning it's storing a null value
		return super.getColor();
	}

	/**
	 * method to set the size of the symbol plotted
	 */
	public final void setSymbolScale(final Double val)
	{
		_theLocationWrapper.setSymbolScale(val);
	}

	/**
	 * method to get the size of the symbol plotted
	 */
	public final Double getSymbolScale()
	{
		return _theLocationWrapper.getSymbolScale();
	}

	@Override
	public final void paint(final CanvasType dest)
	{
		/**
		 * control of the painting functionality has been passed back to the Track
		 * object
		 */
	}

	@Override
	@FireReformatted
	public void setColor(final Color theColor)
	{
		// let the parent do the business
		super.setColor(theColor);

		// and update the color of the location wrapper
		_theLocationWrapper.setColor(getColor());
	}

	/**
	 * paint this shape
	 * 
	 * @param dest
	 * @param centre
	 */
	public final void paintMe(final CanvasType dest, final WorldLocation centre,
			Color theColor)
	{

		// take a copy of the color
		Color safeColor = _theLocationWrapper.getColor();

		// use the provided color
		_theLocationWrapper.setColor(theColor);
		_theLabel.setColor(theColor);

		// // check the color of the location wrapper
		// final Color locCol = _theLocationWrapper.getColor();
		// if (locCol != getColor())
		// {
		// _theLocationWrapper.setColor(getColor());
		// }

		if (getSymbolShowing() && !getArrowShowing())
		{
			// see if the symbol should be shaded (if the lable is showing)
			_theLocationWrapper.setFillSymbol(getLabelShowing());

			// override it's location
			_theLocationWrapper.setLocation(centre);

			// first draw the location (by calling the parenet
			_theLocationWrapper.paint(dest);
		}

		if (getArrowShowing())
		{
			// ok, have a go at drawing an arrow...
			double direction = (this.getFix().getCourse() + Math.PI / 2);

			double theScale = _theLocationWrapper.getSymbolScale();

			double len = 30d * theScale;
			double angle = MWC.Algorithms.Conversions.Degs2Rads(20);

			// move the start point forward, so the centre of the triangle is over the
			// point
			Point p0 = dest.toScreen(centre);
			Point p1 = new Point(p0);
			p1.translate(-(int) (len / 2d * Math.cos(direction)),
					-(int) (len / 2d * Math.sin(direction)));

			// now the back corners
			Point p2 = new Point(p1);
			p2.translate((int) (len * Math.cos(direction - angle)),
					(int) (len * Math.sin(direction - angle)));
			Point p3 = new Point(p1);
			p3.translate((int) (len * Math.cos(direction + angle)),
					(int) (len * Math.sin(direction + angle)));

			dest.fillPolygon(new int[]
			{ p1.x, p2.x, p3.x }, new int[]
			{ p1.y, p2.y, p3.y }, 3);
		}

		// override the label location
		_theLabel.setLocation(centre);

		// and paint the label - if we're asked nicely
		paintLabel(dest, theColor);

		_theLocationWrapper.setColor(safeColor);
	}

	/**
	 * paint the label using the current settings.
	 * 
	 * @param dest
	 *          the destination to paint to
	 */
	public void paintLabel(final CanvasType dest, final Color theCol)
	{
		// now draw the label
		if (getLabelShowing())
		{
			_theLabel.setColor(theCol);
			_theLabel.paint(dest);
		}
	}

	public final Font getFont()
	{
		return _theFont;
	}

	public final void setLabelLocation(final Integer loc)
	{
		_theLabel.setRelativeLocation(loc);
	}

	public final Integer getLabelLocation()
	{
		return _theLabel.getRelativeLocation();
	}

	public final void setFont(final Font theFont)
	{
		_theFont = theFont;

		if (_theLabel != null)
			_theLabel.setFont(getFont());
	}

	@Override
	public final WorldArea getBounds()
	{
		// check that our bounds have been defined
		if (_myArea == null)
		{
			_myArea = new WorldArea(this.getLocation(), this.getLocation());
		}

		// get the bounds from the data object (or its location object)
		return _myArea;
	}

	public final Fix getFix()
	{
		return _theFix;
	}

	@FireReformatted
	public void resetName()
	{
		// do we have a time?
		if (_theFix.getTime() != null)
		{
			_theLabel.setString(FormatRNDateTime.toShortString(_theFix.getTime()
					.getDate().getTime()));
		}
		else
		{
			_theLabel.setString("Pending");
		}

		// forget if there was a user label supplied
		this.setUserLabelSupplied(false);
	}

	@Override
	public final String toString()
	{
		return getName();
	}

	public final String getLabel()
	{
		return _theLabel.getString();
	}

	@FireReformatted
	public final void setLabel(final String val)
	{
		_theLabel.setString(val);
	}

	@Override
	public final String getName()
	{
		return getLabel();
	}

	public String getMultiLineName()
	{
		return "<u>"
				+ _trackWrapper.getName()
				+ ":"
				+ getName()
				+ "</u>\n"
				+ GeneralFormat.formatStatus(
						MWC.Algorithms.Conversions.Rads2Degs(_theFix.getCourse()),
						getSpeed(), _theFix.getLocation().getDepth());
	}

	public final boolean getLabelShowing()
	{
		if (_showLabel == null)
			_showLabel = Boolean.FALSE;

		return _showLabel.booleanValue();
	}

	@FireReformatted
	public final void setLabelShowing(final boolean val)
	{
		_showLabel = new Boolean(val);
	}

	public final boolean getSymbolShowing()
	{
		return _showSymbol;
	}

	public final boolean getArrowShowing()
	{
		return _showArrow;
	}

	public void setArrowShowing(boolean val)
	{
		_showArrow = val;
	}

	public final void setSymbolShowing(final boolean val)
	{
		_showSymbol = val;
	}

	public final boolean visibleBetween(final HiResDate start, final HiResDate end)
	{
		return ((this.getTime().greaterThan(start)) && (getTime().lessThan(end)));
	}

	/**
	 * get the editing information for this type
	 */
	@Override
	public final Editable.EditorType getInfo()
	{
		String trkName = "Track unset";

		if (_trackWrapper != null)
		{
			trkName = _trackWrapper.getName();
		}

		if (_myEditor == null)
			_myEditor = new fixInfo(this, this.getName(), trkName);

		return _myEditor;
	}

	@Override
	public final boolean hasEditor()
	{
		return true;
	}

	/**
	 * how far away are we from this point? or return null if it can't be
	 * calculated
	 */
	@Override
	public final double rangeFrom(final WorldLocation other)
	{
		return _theFix.getLocation().rangeFrom(other);
	}

	// ////////////////////////////////////////////////////
	// watchable (tote) information for this class
	// ///////////////////////////////////////////////////
	public final WorldLocation getLocation()
	{
		return _theFix.getLocation();
	}

	/**
	 * return the course (in radians)
	 */
	public final double getCourse()
	{
		return _theFix.getCourse();
	}

	/**
	 * set the course for this observation
	 * 
	 * @param val
	 *          the course (rads)
	 */
	public void setCourse(final double val)
	{
		_theFix.setCourse(val);
	}

	/**
	 * return the course (in radians)
	 */
	public final double getCourseDegs()
	{
		return MWC.Algorithms.Conversions.Rads2Degs(_theFix.getCourse());
	}

	/**
	 * change the course
	 * 
	 */
	public void setCourseDegs(final double val)
	{
		_theFix.setCourse(MWC.Algorithms.Conversions.Degs2Rads(val));
	}

	/**
	 * set the speed of this participant (in knots)
	 * 
	 * @param val
	 *          the speed (knots)
	 */
	public void setSpeed(final double val)
	{
		_theFix.setSpeed(MWC.Algorithms.Conversions.Kts2Yps(val));
	}

	/**
	 * return the speed (in knots)
	 */
	public final double getSpeed()
	{
		return MWC.Algorithms.Conversions.Yps2Kts(_theFix.getSpeed());
	}

	/**
	 * return the depth (in metres)
	 */
	public final double getDepth()
	{
		return _theFix.getLocation().getDepth();
	}

	/**
	 * return the time of the fix (as long)
	 */
	public final HiResDate getTime()
	{
		return _theFix.getTime();
	}

	public void setDepth(final double val)
	{
		_theFix.getLocation().setDepth(val);
	}

	/**
	 * set the current location of the fix
	 */
	public final void setFixLocation(final WorldLocation val)
	{
		// set the central bits
		setFixLocationSilent(val);

		// also, fire the parent's updated method
		super.getSupport().firePropertyChange(PlainWrapper.LOCATION_CHANGED, null,
				val);
	}

	/**
	 * set the current location of the fix
	 */
	public final void setFixLocationSilent(final WorldLocation val)
	{
		_theFix.setLocation(val);
		_theLabel.setLocation(val);
		_theLocationWrapper.setLocation(val);

		// try to reduce object allocation, if we can...
		if (_myArea == null)
			_myArea = new WorldArea(val, val);
		else
		{
			// just reuse our current object
			_myArea.setTopLeft(val);
			_myArea.setBottomRight(val);
		}
	}

	/**
	 * return the current location of the fix (as a world location). Keep this
	 * method, since it's used from the fix property editors
	 */
	public final WorldLocation getFixLocation()
	{
		return _theFix.getLocation();
	}

	public final HiResDate getDateTimeGroup()
	{
		return _theFix.getTime();
	}

	@FireReformatted
	public final void setDateTimeGroup(final HiResDate val)
	{
		_theFix.setTime(val);
	}

	public final String getLabelFormat()
	{
		return _theFormat;
		/**
		 * note, we return null, not the "N/A" value, so that none of the values in
		 * the tag list are designated as "current value"
		 */
	}

	@FireReformatted
	public final void setLabelFormat(final String format)
	{
		_theFormat = format;

		// just check that the user isn't keeping the value as null
		if (format == null)
			return;

		if (!format.equals(MyDateFormatPropertyEditor.stringTags[0]))
		{
			// so, reformat the label to this format
			final java.text.DateFormat df = new java.text.SimpleDateFormat(format);
			df.setTimeZone(TimeZone.getTimeZone("GMT"));
			this.setLabel(df.format(this.getTime().getDate()));
			// this.setLabel(DebriefFormatDateTime.toStringHiRes(this.getTime()));
		}
	}

	/**
	 * meet the requirements of the comparable interface
	 * 
	 */
	@Override
	public final int compareTo(final Plottable o)
	{
		int res = 0;

		if (o instanceof FixWrapper)
		{
			final FixWrapper f = (FixWrapper) o;

			// cool, use our HiResDate comparator
			res = getTime().compareTo(f.getTime());

		}
		else
		{
			// just put it first
			res = 1;
		}

		return res;
	}

	public void setLineShowing(final boolean val)
	{
		_lineShowing = val;
	}

	public boolean getLineShowing()
	{
		return _lineShowing;
	}

	public void setLocation(final WorldLocation val)
	{
		_theFix.setLocation(val);
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public final class fixInfo extends Griddable
	{

		public fixInfo(final FixWrapper data, final String theName,
				final String trackName)
		{
			super(data, theName, trackName + ": theName");
		}

		@Override
		public final String getDisplayName()
		{
			return getTrackWrapper().getName() + ":" + super.getName();
		}

		@Override
		public PropertyDescriptor[] getGriddablePropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						prop("Label", "the label for this data item"),
						prop("Depth", "depth of this position"),
						prop("Visible", "whether this position is visible"),
						prop("FixLocation", "the location for this position", OPTIONAL),
						prop("CourseDegs", "current course of this platform (degs)",
								SPATIAL),
						prop("Speed", "current speed of this vehicle", SPATIAL) };

				return res;

			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}

		public final BeanInfo[] getAdditionalBeanInfo()
		{
			final BeanInfo[] res =
			{ getTrackWrapper().getInfo() };
			return res;
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				if (_myInfoPropertyDescriptors == null)
				{
					final PropertyDescriptor[] res =
					{
							prop("SymbolScale", "the scale of the symbol", FORMAT),
							prop("DateTimeGroup", "the DTG for the fix"),
							prop("Color", "the position color", FORMAT),
							prop("Label", "the position label", FORMAT),
							prop("Font", "the label font", FORMAT),
							prop("LabelShowing", "whether the label is showing", VISIBILITY),
							prop("SymbolShowing", "whether the symbol is showing", VISIBILITY),
							prop("ArrowShowing", "whether the arrow is showing", VISIBILITY),
							prop("LineShowing",
									"whether the to join this position it's predecessor",
									VISIBILITY),
							prop("FixLocation", "the location of the fix", SPATIAL),
							prop("Visible", "whether the whole fix is visible", VISIBILITY),
							longProp("LabelFormat",
									"the time format of the label, or N/A to leave as-is",
									MyDateFormatPropertyEditor.class, SPATIAL),
							longProp("LabelLocation", "the label location",
									MWC.GUI.Properties.LocationPropertyEditor.class, FORMAT) };
					res[0]
							.setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.class);
					_myInfoPropertyDescriptors = res;
				}
			}
			catch (final IntrospectionException e)
			{
				_myInfoPropertyDescriptors = super.getPropertyDescriptors();
			}
			return _myInfoPropertyDescriptors;
		}

		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class<FixWrapper> c = FixWrapper.class;
			final MethodDescriptor[] mds =
			{ method(c, "resetColor", null, "Reset Color"),
					method(c, "resetName", null, "Reset Label"),
					method(c, "exportThis", null, "Export Shape") };
			return mds;
		}

		public final SubjectAction[] getUndoableActions()
		{
			final FixWrapper fw = (FixWrapper) getData();
			final String lbl = fw.getLabel();
			final SubjectAction[] res = new SubjectAction[]
			{ new SplitTrack(true, "Split track before " + lbl),
					new SplitTrack(false, "Split track after " + lbl) };
			return res;
		}

		@Override
		public NonBeanPropertyDescriptor[] getNonBeanGriddableDescriptors()
		{
			// don't worry - we provide the bean-based model
			return null;
		}

	}

	// //////////////////////////////////////////////////////////////
	// and a class representing interpolated fixes
	// //////////////////////////////////////////////////////////////
	public static class InterpolatedFixWrapper extends FixWrapper implements
			PlainWrapper.InterpolatedData
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * constructor - just pass the child fix back to the parent
		 * 
		 * @param fixData
		 */
		public InterpolatedFixWrapper(final Fix fixData)
		{
			super(fixData);
		}

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// property editor which looks just like the one provided in MWC.GUI, but
	// which also has
	// a N/A property - which means leave the label as it is
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static final class MyDateFormatPropertyEditor extends
			MWC.GUI.Properties.DateFormatPropertyEditor
	{
		static private final String NULL_VALUE = "N/A";

		static final String[] stringTags =
		{ NULL_VALUE, "mm:ss.SSS", "HHmm.ss", "HHmm", "ddHHmm", "ddHHmm.ss",
				"yy/MM/dd HH:mm", };

		public final String[] getTags()
		{
			return stringTags;
		}

		public void setAsText(final String val)
		{
			_myFormat = getMyIndexOf(val);
		}

		private int getMyIndexOf(final String val)
		{
			int res = INVALID_INDEX;

			// cycle through the tags until we get a matching one
			for (int i = 0; i < getTags().length; i++)
			{
				final String thisTag = getTags()[i];
				if (thisTag.equals(val))
				{
					res = i;
					break;
				}

			}
			return res;
		}

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		public final void testMyParams()
		{
			final Fix fx = new Fix(new HiResDate(12, 0),
					new WorldLocation(2d, 2d, 2d), 2d, 2d);
			final TrackWrapper tw = new TrackWrapper();
			tw.setName("here ew arw");
			FixWrapper ed = new FixWrapper(fx);
			ed.setTrackWrapper(tw);
			editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		/**
		 * Test method for
		 * {@link Debrief.Wrappers.TrackWrapper#add(MWC.GUI.Editable)} .
		 */
		public void testInterpolate()
		{
			FixWrapper fw1 = TrackWrapper_Test.createFix(100, 1, 1, 2, 3);
			FixWrapper fw2 = TrackWrapper_Test.createFix(200, 2, 1, 2, 3);
			FixWrapper fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(150));
			assertEquals("right time", 150, fw3.getTime().getDate().getTime());
			assertEquals("right lat", 1.5, fw3.getLocation().getLat(), 0001);
			assertEquals("right lat", 1, fw3.getLocation().getLong(), 0.0001);

			fw1 = TrackWrapper_Test.createFix(100, 1, 1, 2, 3);
			fw2 = TrackWrapper_Test.createFix(200, 2, 2, 2, 3);
			fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(150));
			assertEquals("right time", 150, fw3.getTime().getDate().getTime());
			assertEquals("right lat", 1.5, fw3.getLocation().getLat(), 0001);
			assertEquals("right lat", 1.5, fw3.getLocation().getLong(), 0.0001);

			fw1 = TrackWrapper_Test.createFix(100, 1, 1, 2, 3);
			fw2 = TrackWrapper_Test.createFix(200, 2, 1, 2, 3);
			fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(140));
			assertEquals("right time", 140, fw3.getTime().getDate().getTime());
			assertEquals("right lat", 1.4, fw3.getLocation().getLat(), 0001);
			assertEquals("right lat", 1, fw3.getLocation().getLong(), 0.0001);

			fw1 = TrackWrapper_Test.createFix(100, 1, 21, 2, 3);
			fw2 = TrackWrapper_Test.createFix(200, 2, 21, 2, 3);
			fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(140));
			assertEquals("right time", 140, fw3.getTime().getDate().getTime());
			assertEquals("right lat", 1.4, fw3.getLocation().getLat(), 0001);
			assertEquals("right lat", 21, fw3.getLocation().getLong(), 0.0001);

			fw1 = TrackWrapper_Test.createFix(100, 41, 21, 2, 3);
			fw2 = TrackWrapper_Test.createFix(200, 42, 21, 2, 3);
			fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(140));
			assertEquals("right time", 140, fw3.getTime().getDate().getTime());
			assertEquals("right lat", 41.4, fw3.getLocation().getLat(), 0001);
			assertEquals("right lat", 21, fw3.getLocation().getLong(), 0.0001);

			fw1 = TrackWrapper_Test.createFix(100, 60, 30, 0, 31, 0, 0, 2, 3);
			fw2 = TrackWrapper_Test.createFix(200, 60, 15, 0, 31, 30, 0, 2, 3);
			fw3 = FixWrapper.interpolateFix(fw1, fw2, new HiResDate(150));

			System.out.println(fw1.getLocation() + ": " + fw1.getLocation().getLat()
					+ ", " + fw1.getLocation().getLong());
			System.out.println(fw2.getLocation() + ": " + fw2.getLocation().getLat()
					+ ", " + fw2.getLocation().getLong());
			System.out.println(fw3.getLocation() + ": " + fw3.getLocation().getLat()
					+ ", " + fw3.getLocation().getLong());

			assertEquals("right time", 150, fw3.getTime().getDate().getTime());
			assertEquals("right long", 31.25, fw3.getLocation().getLong(), 0.0001);
			assertEquals("right lat", 60.375, fw3.getLocation().getLat(), 0.001);
		}

	}

	private static class SplitTrack implements SubjectAction
	{
		private final boolean _splitBefore;
		private final String _title;
		private Vector<TrackSegment> _splitSections;

		/**
		 * create an instance of this operation
		 * 
		 * @param keepPort
		 *          whether to keep the port removal
		 * @param title
		 *          what to call ourselves
		 */
		public SplitTrack(final boolean splitBefore, final String title)
		{
			_splitBefore = splitBefore;
			_title = title;
		}

		public String toString()
		{
			return _title;
		}

		public void execute(final Editable subject)
		{
			final FixWrapper fix = (FixWrapper) subject;
			final TrackWrapper parent = fix.getTrackWrapper();
			_splitSections = parent.splitTrack(fix, _splitBefore);
		}

		public void undo(final Editable subject)
		{
			final FixWrapper fix = (FixWrapper) subject;
			final TrackWrapper parent = fix.getTrackWrapper();
			parent.combineSections(_splitSections);
		}

		public boolean isRedoable()
		{
			return true;
		}

		public boolean isUndoable()
		{
			return true;
		}

	}

	public static void main(final String[] args)
	{
		final testMe tm = new testMe("scrap");
		tm.testMyParams();
	}

	public HiResDate getDTG()
	{
		return _theFix.getTime();
	}

	public void setDTG(final HiResDate date)
	{
		_theFix.setTime(date);
	}

	@Override
	public Editable getParent()
	{
		return getTrackWrapper();
	}

	/**
	 * indicate that the user has supplied a label for this position fix
	 * 
	 * @param yesNo
	 *          whether a user label was supplied
	 */
	public void setUserLabelSupplied(boolean yesNo)
	{
		_userLabelSupplied = yesNo;
	}

	/**
	 * indicate that the user has supplied a label for this position fix
	 * 
	 * @return whether a user label was supplied
	 */
	public boolean getUserLabelSupplied()
	{
		return _userLabelSupplied;

	}
}
