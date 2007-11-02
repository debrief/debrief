// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: TrackWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.33 $
// $Log: TrackWrapper.java,v $
// Revision 1.33  2007/02/08 09:27:34  ian.mayo
// JDK1.5 generic fixes, fix problem whereby list of positions didn't return any properties.
//
// Revision 1.32  2007/01/22 11:11:44  ian.mayo
// Improve getNearest algorithm
//
// Revision 1.31  2007/01/22 09:53:36  ian.mayo
// Better remove datum operation, slightly improved "get nearest"
//
// Revision 1.30  2007/01/19 11:19:57  ian.mayo
// Improve finding the point in question
//
// Revision 1.29  2006/12/12 11:14:38  Ian.Mayo
// Tidying, to make sure we include the fix on the requested DTG - not just the one immediately before it.
//
// Revision 1.28  2006/11/14 08:44:14  Ian.Mayo
// Tidying of getBounds, provide positions accessor
//
// Revision 1.27  2006/10/25 12:35:10  Ian.Mayo
// Handle when no matching items found (such as when doing set sym frequency at high rates)
//
// Revision 1.26  2006/10/11 12:38:55  Ian.Mayo
// Catch empty track condition
//
// Revision 1.25  2006/09/25 14:51:17  Ian.Mayo
// Respect new "has children" property of Layers
//
// Revision 1.24  2006/07/28 13:21:01  Ian.Mayo
// Handle object with duff bounds
//
// Revision 1.23  2006/06/27 10:06:16  Ian.Mayo
// Correct how we drag tracks (allow for fixes being in child layer of trck)
//
// Revision 1.22  2006/06/19 08:33:02  Ian.Mayo
// Check we have some data
//
// Revision 1.21  2006/06/12 10:14:13  Ian.Mayo
// Don't connect lines if previous point isn't visible.
//
// Revision 1.20  2006/06/02 12:24:17  Ian.Mayo
// Show points up to 'nearest' time, now immediately afterwards
//
// Revision 1.19  2006/05/25 13:42:11  Ian.Mayo
// Put positions in layer of their own
//
// Revision 1.18  2006/05/02 14:07:20  Ian.Mayo
// Draggable components aswell as features
//
// Revision 1.17  2006/04/21 08:18:32  Ian.Mayo
// Implement drag support
//
// Revision 1.16  2006/01/11 16:34:31  Ian.Mayo
// Tidying = whilst implementing track generation from a layer
//
// Revision 1.15  2006/01/10 12:19:47  Ian.Mayo
// Tidy how we generate interpolated points
//
// Revision 1.14  2006/01/10 11:22:43  Ian.Mayo
// Reflect user's request for interpolated points when returning a series of fixes (getBetween()), as well as getNearestTo().
//
// Revision 1.13  2005/09/23 14:56:05  Ian.Mayo
// Support generation of interpolated fixes
//
// Revision 1.12  2005/09/23 12:12:05  Ian.Mayo
// Start doing interpolate points
//
// Revision 1.11  2005/01/28 09:32:12  Ian.Mayo
// Categorise editable properties
//
// Revision 1.10  2004/11/25 11:04:40  Ian.Mayo
// More test fixing after hi-res switch, largely related to me removing some unused accessors which were property getters
//
// Revision 1.9  2004/11/25 10:24:52  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.8  2004/11/22 13:41:06  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.7  2004/09/10 09:11:31  Ian.Mayo
// Correct prior mistaken implementation of add(Editable) - we should have just changed the signature of add(Plottable) et al
//
// Revision 1.6  2004/09/09 10:51:58  Ian.Mayo
// Provide missing methods from Layers structure.  Don't know why they had been missing for so long.  Poss disconnect between ASSET/Debrief development trees
//
// Revision 1.5  2004/09/09 10:23:16  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.4  2004/07/07 13:31:24  Ian.Mayo
// Minor, only whitespace
//
// Revision 1.3  2004/07/06 10:40:07  Ian.Mayo
// Correct problem in filter to time period
//
// Revision 1.2  2004/07/06 09:44:18  Ian.Mayo
// Change how we move the sensor location when dragging track
//
// Revision 1.1.1.2  2003/07/21 14:49:30  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.26  2003-07-04 10:59:20+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.25  2003-07-01 14:26:01+01  ian_mayo
// Minor method name change - to reflect fact that method not just called from 3d anymore
//
// Revision 1.24  2003-06-23 08:38:59+01  ian_mayo
// Part way through addition of TMA solutions
//
// Revision 1.23  2003-06-10 15:35:16+01  ian_mayo
// Remove track dragging tests
//
// Revision 1.21  2003-05-12 12:01:13+01  ian_mayo
// Formatting as recommended by IntelliJ
//
// Revision 1.20  2003-05-08 13:50:14+01  ian_mayo
// Switch to using Color instead of TrackColor (since we now use a dedicated track menu)
//
// Revision 1.19  2003-03-27 11:15:50+00  ian_mayo
// Return valid data when asked to filter by an invalid date
//
// Revision 1.18  2003-03-25 15:53:52+00  ian_mayo
// Provide method to reset labels
//
// Revision 1.17  2003-03-19 15:36:49+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.16  2003-02-10 16:29:11+00  ian_mayo
// Set default line width to 2, not 1
//
// Revision 1.15  2003-01-17 15:08:20+00  ian_mayo
// Change method name for providing 3d data
//
// Revision 1.14  2003-01-09 16:18:18+00  ian_mayo
// remove old object read/write methods
//
// Revision 1.13  2003-01-09 16:17:10+00  ian_mayo
// Provide method to find unfiltered fixes within the track
//
// Revision 1.12  2002-12-16 15:10:43+00  ian_mayo
// Minor tidying
//
// Revision 1.11  2002-10-30 16:27:24+00  ian_mayo
// tidy up (shorten) display names of editables
//
// Revision 1.10  2002-10-28 09:04:32+00  ian_mayo
// provide support for variable thickness of lines in tracks, etc
//
// Revision 1.9  2002-10-01 15:41:39+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.8  2002-07-23 08:49:37+01  ian_mayo
// Return the correct type of object for getWatchable, and test the time retrieval
//
// Revision 1.7  2002-07-10 14:58:58+01  ian_mayo
// correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.6  2002-07-09 15:27:42+01  ian_mayo
// Return zero length list instead of null
//
// Revision 1.5  2002-07-08 09:47:04+01  ian_mayo
// <>
//
// Revision 1.4  2002-06-05 12:56:29+01  ian_mayo
// unnecessarily loaded
//
// Revision 1.3  2002-05-31 16:19:06+01  ian_mayo
// Provide support for shifting track
//
// Revision 1.2  2002-05-28 09:25:14+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:28+01  ian_mayo
// Initial revision
//
// Revision 1.11  2002-03-15 15:08:49+00  administrator
// Filter sensor data with fixes
//
// Revision 1.10  2002-02-26 09:43:41+00  administrator
// Reflect new signature of TextLabel, where we get it to retrieve it's anchor point each time
//
// Revision 1.9  2001-11-23 09:54:49+00  administrator
// Don't plot track name if no points are visible (they've been filtered out)
//
// Revision 1.8  2001-10-29 11:17:00+00  administrator
// improve comments
//
// Revision 1.7  2001-10-01 12:49:52+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.6  2001-08-31 09:56:28+01  administrator
// Handle when we haven't got our data yet
//
// Revision 1.5  2001-08-29 19:19:01+01  administrator
// Reflect package change of PlainWrapper, and tidying up
//
// Revision 1.4  2001-08-24 12:40:41+01  administrator
// Allow user to delete SensorWrapper
//
// Revision 1.3  2001-08-14 14:06:26+01  administrator
// finish up support for SensorWrappers
//
// Revision 1.2  2001-08-06 16:59:37+01  administrator
// In getBounds method, we will only return the area covered by visible objects, not just the outer boundary
//
// Revision 1.1  2001-08-06 12:44:56+01  administrator
// just check we have enough of a GUI to start plotting
//
// Revision 1.0  2001-07-17 08:41:10+01  administrator
// Initial revision
//
// Revision 1.9  2001-01-24 11:35:19+00  novatech
// more efficient point handling
//
// Revision 1.8  2001-01-22 12:30:02+00  novatech
// added JUnit testing code
//
// Revision 1.7  2001-01-21 21:33:54+00  novatech
// tidied up methods
//
// Revision 1.6  2001-01-18 13:18:52+00  novatech
// create internal ZERO location, to reduce object creation
//
// Revision 1.5  2001-01-17 13:22:01+00  novatech
// use expert properties to indicate which ones are to be edited from child
//
// Revision 1.4  2001-01-17 09:42:39+00  novatech
// support use of symbols
//
// Revision 1.3  2001-01-09 10:26:11+00  novatech
// switch to new collections package
//
// Revision 1.2  2001-01-04 14:01:15+00  novatech
// tidied formatting
//
// Revision 1.1  2001-01-03 13:40:23+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:38  ianmayo
// initial import of files
//
// Revision 1.33  2000-11-22 10:50:38+00  ian_mayo
// allow setting of fonts for Track
//
// Revision 1.32  2000-11-17 09:10:01+00  ian_mayo
// when fix is coloured, use this colour to colour the line to the next section
//
// Revision 1.31  2000-11-03 12:07:26+00  ian_mayo
// tidy up Layer support
//
// Revision 1.30  2000-11-02 16:45:46+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer, also changed TrackWrapper so that it implements Layer,  and as we read in files, we put them into track and add Track to Layers, not to Layer then Layers
//
// Revision 1.29  2000-09-21 09:05:25+01  ian_mayo
// make Editable.EditorType a transient parameter, to save it being written to file
//
// Revision 1.28  2000-08-18 13:33:43+01  ian_mayo
// switch to singleton Editable.EditorType
//
// Revision 1.27  2000-08-11 08:40:37+01  ian_mayo
// insert comments
//
// Revision 1.26  2000-08-09 16:04:01+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.25  2000-07-07 09:31:59+01  ian_mayo
// when the user sets a non-zero symbol interval, set the symbols to be visible
//
// Revision 1.24  2000-06-08 13:18:46+01  ian_mayo
// reformatting caused by viewing in NetBeans
//
// Revision 1.23  2000-05-22 10:07:10+01  ian_mayo
// remove debug line
//
// Revision 1.22  2000-05-19 11:24:08+01  ian_mayo
// improved processing for getNearest() method, to correctly handle DTG before start or after end of track
//
// Revision 1.21  2000-04-19 11:30:59+01  ian_mayo
// handle the track label, and the Position-related properties
//
// Revision 1.20  2000-04-05 08:40:08+01  ian_mayo
// remove getVisible method, since we have inherited it from PlainWrapper
//
// Revision 1.19  2000-04-03 10:46:45+01  ian_mayo
// add filtering functionality
//
// Revision 1.18  2000-03-27 14:41:37+01  ian_mayo
// remove storage of "ShowLabel" property, allow Fixes to do it for themselves
//
// Revision 1.17  2000-03-17 13:38:11+00  ian_mayo
// Wrap the setColor method with setTrackColor to make editing properties tidier
//
// Revision 1.16  2000-03-14 15:00:21+00  ian_mayo
// modifications to allow reading in of old files
//
// Revision 1.15  2000-03-14 09:52:09+00  ian_mayo
// Switch to use of collections, from VECTORS, NEEDS FURTHER WORK (opening old files)
//
// Revision 1.14  2000-03-09 11:26:04+00  ian_mayo
// better retrieval of points between time limits
//
// Revision 1.13  2000-03-08 14:27:35+00  ian_mayo
// return fixes between a specified period
//
// Revision 1.12  2000-03-07 14:48:19+00  ian_mayo
// optimised algorithms
//
// Revision 1.11  2000-02-29 08:46:33+00  ian_mayo
// make public method private
//
// Revision 1.10  2000-02-22 13:48:30+00  ian_mayo
// exportShape name changed to exportThis
//
// Revision 1.9  2000-02-21 16:38:02+00  ian_mayo
// More formatting of labels/symbols
//
// Revision 1.8  2000-01-18 15:04:32+00  ian_mayo
// changed UI name from Fix to Location
//
// Revision 1.7  2000-01-13 15:32:06+00  ian_mayo
// moved paint control to Track
//
// Revision 1.6  2000-01-12 15:40:20+00  ian_mayo
// added concept of contacts
//
// Revision 1.5  1999-12-03 14:38:21+00  ian_mayo
// added code to handle data area
//
// Revision 1.4  1999-12-02 09:47:18+00  ian_mayo
// add toString method
//
// Revision 1.3  1999-11-26 15:51:21+00  ian_mayo
// enabled joining lines
//
// Revision 1.2  1999-11-12 14:35:39+00  ian_mayo
// part way through getting them to export themselves
//
// Revision 1.1  1999-10-12 15:34:04+01  ian_mayo
// Initial revision
//
// Revision 1.6  1999-08-04 14:03:56+01  administrator
// add flag for showing time label
//
// Revision 1.5  1999-08-04 09:45:32+01  administrator
// minor mods, tidying up
//
// Revision 1.4  1999-07-27 09:24:18+01  administrator
// added BeanInfo editing
//
// Revision 1.3  1999-07-19 12:40:33+01  administrator
// added storage of sub-second time data (Switched to storing as Long rather than java.utils.Date)
//
// Revision 1.2  1999-07-12 08:09:20+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:14+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:04+01  sm11td
// Initial revision
//
// Revision 1.4  1999-06-04 08:45:28+01  sm11td
// Ending phase 1, adding colours to annotations
//
// Revision 1.3  1999-06-01 16:49:18+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.2  1999-02-01 14:25:02+00  sm11td
// Skeleton there, opening new sessions, window management.
//
// Revision 1.1  1999-01-31 13:33:08+00  sm11td
// Initial revision
//

package Debrief.Wrappers;

import java.awt.*;
import java.beans.*;
import java.io.Serializable;
import java.util.*;

import Debrief.ReaderWriter.Replay.FormatTracks;
import Debrief.Tools.Tote.*;
import MWC.Algorithms.Conversions;
import MWC.GUI.*;
import MWC.GUI.Canvas.MockCanvasType;
import MWC.GUI.Layer.ProvidesContiguousElements;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GUI.Shapes.*;
import MWC.GenericData.*;
import MWC.TacticalData.*;

// our old collections package used prior to JDK 1.2
// import com.sun.java.util.collections.*;

/**
 * the TrackWrapper maintains the GUI and data attributes of the whole track
 * iteself, but the responsibility for the fixes within the track are demoted to
 * the FixWrapper
 */
public final class TrackWrapper extends MWC.GUI.PlainWrapper implements
		Serializable, WatchableList, DynamicPlottable, MWC.GUI.Layer,
		DraggableItem, HasDraggableComponents, ProvidesContiguousElements
{

	// //////////////////////////////////////
	// member variables
	// //////////////////////////////////////

	/**
	 * the symbol to pass on to a snail plotter
	 */
	private MWC.GUI.Shapes.Symbols.PlainSymbol _theSnailShape;

	/**
	 * keep track of versions - version id
	 */
	static final long serialVersionUID = 1;

	/**
	 * the track we are storing
	 */
	private Track _theTrack;

	/**
	 * the list of wrappers we hold
	 */
	private PlottableLayer _thePositions;

	/**
	 * the list of contacts we hold
	 */
	private java.util.Vector<ContactWrapper> _theContacts;

	/**
	 * whether or not to link the Positions
	 */
	private boolean _linkPositions;

	/**
	 * whether or not to show the Positions
	 */
	private boolean _showPositions;

	private HiResDate _lastSymbolFrequency = new HiResDate(0,
			TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

	private HiResDate _lastLabelFrequency = new HiResDate(0,
			TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

	/**
	 * working parameters
	 */
	// for getFixesBetween
	transient private FixWrapper starter;

	transient private FixWrapper finisher;

	// for getNearestTo
	transient private FixWrapper nearestFix;

	transient private FixWrapper lastFix;

	transient private HiResDate lastDTG;

	/**
	 * the label describing this track
	 */
	private final MWC.GUI.Shapes.TextLabel _theLabel;

	/**
	 * the end of the track to plot the label
	 */
	private boolean _LabelAtStart = true;

	/**
	 * our editable details
	 */
	transient private Editable.EditorType _myEditor = null;

	/**
	 * working ZERO location value, to reduce number of working values
	 */
	final private WorldLocation _zeroLocation = new WorldLocation(0, 0, 0);

	/**
	 * the sensor tracks for this vessel
	 */
	private Vector<SensorWrapper> _mySensors = null;

	/**
	 * the TMA solutions for this vessel
	 */
	private Vector<TMAWrapper> _mySolutions = null;

	/**
	 * the width of this track
	 */
	private int _lineWidth = 2;

	/**
	 * whether to interpolate points in this track
	 */
	private boolean _interpolatePoints = false;

	// //////////////////////////////////////
	// constructors
	// //////////////////////////////////////
	/**
	 * Wrapper for a Track (a series of position fixes). It combines the data
	 * with the formatting details
	 */
	public TrackWrapper()
	{
		// declare our arrays
		_thePositions = new PlottableLayer();
		_thePositions.setName("Positions");

		// let's not create the contacts array until we have to
		_theContacts = null;

		_linkPositions = true;

		// start off with positions showing (although the default setting for a
		// fix
		// is to not show a symbol anyway). We need to make this "true" so that
		// when a fix position is set to visible it is not over-ridden by this
		// setting
		_showPositions = true;

		_theLabel = new MWC.GUI.Shapes.TextLabel(new WorldLocation(0, 0, 0),
				null);
		// set an initial location for the label
		_theLabel.setRelativeLocation(new Integer(
				MWC.GUI.Properties.LocationPropertyEditor.RIGHT));

		// initialise the symbol to use for plotting this track in snail mode
		_theSnailShape = MWC.GUI.Shapes.Symbols.SymbolFactory
				.createSymbol("Submarine");
	}

	/**
	 * instruct this object to clear itself out, ready for ditching
	 */
	public final void closeMe()
	{
		// do the parent
		super.closeMe();

		// and my objects
		// first ask them to close themselves
		Enumeration<Editable> it = _thePositions.elements();
		while (it.hasMoreElements())
		{
			final Object val = it.nextElement();
			if (val instanceof PlainWrapper)
			{
				final PlainWrapper pw = (PlainWrapper) val;
				pw.closeMe();
			}
		}

		// now ditch them
		_thePositions.removeAllElements();
		_thePositions = null;

		// and my objects
		// first ask the sensors to close themselves
		if (_mySensors != null)
		{
			Iterator<SensorWrapper> it2 = _mySensors.iterator();
			while (it2.hasNext())
			{
				final Object val = it2.next();
				if (val instanceof PlainWrapper)
				{
					final PlainWrapper pw = (PlainWrapper) val;
					pw.closeMe();
				}
			}
			// now ditch them
			_mySensors.clear();
		}

		// now ask the solutions to close themselves
		if (_mySolutions != null)
		{
			Iterator<TMAWrapper> it2 = _mySolutions.iterator();
			while (it2.hasNext())
			{
				final Object val = it2.next();
				if (val instanceof PlainWrapper)
				{
					final PlainWrapper pw = (PlainWrapper) val;
					pw.closeMe();
				}
			}
			// now ditch them
			_mySolutions.clear();
		}

		// and our utility objects
		finisher = null;
		lastFix = null;
		nearestFix = null;
		starter = null;

		// and our editor
		_myEditor = null;

		// and the track
		_theTrack.closeMe();
		_theTrack = null;

	}

	// //////////////////////////////////////
	// member functions
	// //////////////////////////////////////

	/**
	 * add the fix wrapper to the track
	 * 
	 * @param theFix
	 *            the Fix to be added
	 */
	public final void addFix(final FixWrapper theFix)
	{
		// add fix to the track
		_thePositions.add(theFix);

		// and add the fix wrapper to our data list
		_theTrack.addFix(theFix.getFix());

		// and extend the start/end DTGs

	}

	/**
	 * add a contact to the track
	 * 
	 * @param theContact
	 *            the contact to add
	 */
	private void addContact(final ContactWrapper theContact)
	{
		if (_theContacts == null)
		{
			_theContacts = new java.util.Vector<ContactWrapper>(0, 1);
		}

		// add to our list of contacts
		_theContacts.addElement(theContact);

	}

	/**
	 * store the actual track details for this wrapper
	 * 
	 * @param theTrack
	 *            the track data we are handling
	 */
	public final void setTrack(final Track theTrack)
	{
		_theTrack = theTrack;
	}

	/**
	 * keep a list of points waiting to be plotted
	 * 
	 */
	private transient int[] _myPts;

	/**
	 * keep track of how far we are through our array of points
	 * 
	 */
	private transient int _ptCtr = 0;

	/**
	 * paint any polyline that we've built up
	 * 
	 * @param dest -
	 *            where we're painting to
	 * @param thisCol
	 */
	private void paintTrack(final CanvasType dest, final Color thisCol)
	{
		if (_ptCtr > 0)
		{
			dest.setColor(thisCol);
			int[] poly = new int[_ptCtr];
			System.arraycopy(_myPts, 0, poly, 0, _ptCtr);
			dest.drawPolyline(poly);

			// and reset the counter
			_ptCtr = 0;
		}
	}

	/**
	 * draw this track (we can leave the Positions to draw themselves)
	 * 
	 * @param dest
	 *            the destination
	 */
	public final void paint(final CanvasType dest)
	{
		if (getVisible())
		{
			// set the thickness for this track
			dest.setLineWidth(_lineWidth);

			// and set the initial colour for this track
			dest.setColor(getColor());

			// is our points store long enough?
			if ((_myPts == null) || (_myPts.length < numFixes() * 2))
			{
				_myPts = new int[numFixes() * 2];
			}

			// reset the points counter
			_ptCtr = 0;

			// java.awt.Point lastP = null;
			Color lastCol = null;

			boolean locatedTrack = false;
			WorldLocation lastLocation = null;

			// just check if we are drawing anything at all
			if ((!_linkPositions) && (!_showPositions))
				return;

			// keep track of if we have plotted any points (since
			// we won't be plotting the name if none of the points are visible).
			// this typically occurs when filtering is applied and a short
			// track is completely outside the time period
			boolean plotted_anything = false;

			// ///////////////////////////////////////////
			// let the fixes draw themselves in
			// ///////////////////////////////////////////
			final Enumeration<Editable> fixWrappers = _thePositions.elements();
			while (fixWrappers.hasMoreElements())
			{
				final FixWrapper fw = (FixWrapper) fixWrappers.nextElement();

				// is this fix visible
				if (!fw.getVisible())
				{
					// nope. Don't join it to the last position.
					// ok, if we've built up a polygon, we need to write it now
					paintTrack(dest, lastCol);
				}
				else
				{
					// yup, it's visible. carry on.

					// ok, so we have plotted something
					plotted_anything = true;

					// this is a valid one, remember the details
					lastLocation = fw.getLocation();
					final java.awt.Point thisP = dest.toScreen(lastLocation);

					// just check that there's enough GUI to create the plot
					// (i.e. has a point been returned)
					if (thisP == null)
						return;

					// so, we're looking at the first data point. Do
					// we want to use this to locate the track name?
					if (_LabelAtStart)
					{
						// or have we already sorted out the location
						if (!locatedTrack)
						{
							locatedTrack = true;
							_theLabel.setLocation(new WorldLocation(
									lastLocation));
						}
					}

					// are we
					if (_linkPositions)
					{
						// right, just check if we're a different colour to the
						// previous one
						Color thisCol = fw.getColor();

						// do we know the previous colour
						if (lastCol == null)
						{
							lastCol = thisCol;
						}

						// is this to be joined to the previous one?
						if (fw.getLineShowing())
						{
							// so, grow the the polyline, unless we've got a colour
							// change...
							if (thisCol != lastCol)
							{
	                            // add our position to the list - so it finishes on us
	                            _myPts[_ptCtr++] = thisP.x;
	                            _myPts[_ptCtr++] = thisP.y;
	                            
								// yup, better get rid of the previous polygon
								paintTrack(dest, lastCol);
							}

							// add our position to the list - we'll output the
							// polyline at the end
							_myPts[_ptCtr++] = thisP.x;
							_myPts[_ptCtr++] = thisP.y;
						}
						else
						{

                            // nope, output however much line we've got so far -
							// since this
							// line won't be joined to future points
							paintTrack(dest, thisCol);
							
                            // start off the next line
                            _myPts[_ptCtr++] = thisP.x;
                            _myPts[_ptCtr++] = thisP.y;
							
						}

						// set the colour of the track from now on to this
						// colour, so that
						// the "link" to the next fix is set to this colour if
						// left
						// unchanged
						dest.setColor(fw.getColor());

						// and remember the last colour
						lastCol = thisCol;

					}

					if (_showPositions)
					{
						fw.paintMe(dest);
					}
				}

			}

			// ok, just see if we have any pending polylines to paint
			paintTrack(dest, lastCol);

			// are we trying to put the label at the end of the track?
			if (!_LabelAtStart)
			{
				// check that we have found at least one location to plot.
				if (lastLocation != null)
					_theLabel.setLocation(new WorldLocation(lastLocation));
			}

			// and draw the track label
			if (_theLabel.getVisible())
			{

				// still, we only plot the track label if we have plotted any
				// points
				if (plotted_anything)
				{

					// check that we have found a location for the lable
					if (_theLabel.getLocation() != null)
					{

						// check that we have set the name for the label
						if (_theLabel.getString() == null)
						{
							_theLabel.setString(getName());
						}

						if (_theLabel.getColor() == null)
						{
							_theLabel.setColor(getColor());
						}

						// and paint it
						_theLabel.paint(dest);

					} // if the label has a location
				}
			} // if the label is visible

			// /////////////////////////////////////////////
			// now plot the solutions
			// /////////////////////////////////////////////
			if (_mySolutions != null)
			{
				final Enumeration<TMAWrapper> iter = _mySolutions.elements();
				while (iter.hasMoreElements())
				{
					final TMAWrapper sw = iter.nextElement();
					sw.paint(dest);

				} // through the sensors
			} // whether we have any sensors

			// /////////////////////////////////////////////
			// lastly plot the sensors
			// /////////////////////////////////////////////
			if (_mySensors != null)
			{
				final Enumeration<SensorWrapper> iter = _mySensors.elements();
				while (iter.hasMoreElements())
				{
					final SensorWrapper sw = iter.nextElement();
					sw.paint(dest);

				} // through the sensors
			} // whether we have any sensors

		} // if visible
	}

	/**
	 * what geographic area is covered by this track?
	 * 
	 * @return get the outer bounds of the area
	 */
	public final WorldArea getBounds()
	{
		// we no longer just return the bounds of the track, because a portion
		// of the track may have been made invisible.

		// instead, we will pass through the full dataset and find the outer
		// bounds
		// of the visible area
		WorldArea res = null;

		if (!getVisible())
		{
			// hey, we're invisible, return null
		}
		else
		{
			final Enumeration<Editable> it = this._thePositions.elements();
			while (it.hasMoreElements())
			{
				final FixWrapper fw = (FixWrapper) it.nextElement();

				// is this point visible?
				if (fw.getVisible())
				{

					// has our data been initialised?
					if (res == null)
					{
						// no, initialise it
						res = new WorldArea(fw.getLocation(), fw.getLocation());
					}
					else
					{
						// yes, extend to include the new area
						res.extend(fw.getLocation());
					}
				}
			}

			// also extend to include our sensor data
			if (_mySensors != null)
			{
				final Enumeration<SensorWrapper> iter = _mySensors.elements();
				while (iter.hasMoreElements())
				{
					final PlainWrapper sw = iter.nextElement();
					WorldArea theseBounds = sw.getBounds();
					if (theseBounds != null)
					{
						if (res == null)
							res = new WorldArea(theseBounds);
						else
							res.extend(sw.getBounds());
					}
				} // step through the sensors
			} // whether we have any sensors

			// and our solution data
			if (_mySolutions != null)
			{
				final Enumeration<TMAWrapper> iter = _mySolutions.elements();
				while (iter.hasMoreElements())
				{
					final PlainWrapper sw = iter.nextElement();
					WorldArea theseBounds = sw.getBounds();
					if (theseBounds != null)
					{
						if (res == null)
							res = new WorldArea(theseBounds);
						else
							res.extend(sw.getBounds());
					}
				} // step through the sensors
			} // whether we have any sensors

		} // whether we're visible

		return res;
	}

	// //////////////////////////////////////
	// watchable (tote related) parameters
	// //////////////////////////////////////
	/**
	 * the earliest fix in the track
	 * 
	 * @return the DTG
	 */
	public final HiResDate getStartDTG()
	{
		return _theTrack.getStartDTG();
	}

	/**
	 * the time of the last fix
	 * 
	 * @return the DTG
	 */
	public final HiResDate getEndDTG()
	{
		return _theTrack.getEndDTG();
	}

	/**
	 * get the list of sensors for this track
	 */
	public final Enumeration<SensorWrapper> getSensors()
	{
		Enumeration<SensorWrapper> res = null;

		if (_mySensors != null)
			res = _mySensors.elements();

		return res;
	}

	/**
	 * get the list of sensors for this track
	 */
	public final Enumeration<TMAWrapper> getSolutions()
	{
		Enumeration<TMAWrapper> res = null;

		if (_mySolutions != null)
			res = _mySolutions.elements();

		return res;
	}

	/**
	 * filter the list to the specified time period, then inform any listeners
	 * (such as the time stepper)
	 * 
	 * @param start
	 *            the start dtg of the period
	 * @param end
	 *            the end dtg of the period
	 */
	public final void filterListTo(final HiResDate start, final HiResDate end)
	{
		final Enumeration<Editable> fixWrappers = _thePositions.elements();
		while (fixWrappers.hasMoreElements())
		{
			final FixWrapper fw = (FixWrapper) fixWrappers.nextElement();
			final HiResDate dtg = fw.getTime();
			if ((dtg.greaterThanOrEqualTo(start))
					&& (dtg.lessThanOrEqualTo(end)))
			{
				fw.setVisible(true);
			}
			else
			{
				fw.setVisible(false);
			}
		}

		// now do the same for our sensor data
		if (_mySensors != null)
		{
			final Enumeration<SensorWrapper> iter = _mySensors.elements();
			while (iter.hasMoreElements())
			{
				final WatchableList sw = iter.nextElement();
				sw.filterListTo(start, end);
			} // through the sensors
		} // whether we have any sensors

		// and our solution data
		if (_mySolutions != null)
		{
			final Enumeration<TMAWrapper> iter = _mySolutions.elements();
			while (iter.hasMoreElements())
			{
				final WatchableList sw = iter.nextElement();
				sw.filterListTo(start, end);
			} // through the sensors
		} // whether we have any sensors

		// do we have any property listeners?
		if (getSupport() != null)
		{
			final Debrief.GUI.Tote.StepControl.somePeriod newPeriod = new Debrief.GUI.Tote.StepControl.somePeriod(
					start, end);
			getSupport().firePropertyChange(WatchableList.FILTERED_PROPERTY,
					null, newPeriod);
		}
	}

	/**
	 * return the symbol to be used for plotting this track in snail mode
	 */
	public final MWC.GUI.Shapes.Symbols.PlainSymbol getSnailShape()
	{
		return _theSnailShape;
	}

	/**
	 * find the fix nearest to this time (or the first fix for an invalid time)
	 * 
	 * @param DTG
	 *            the time of interest
	 * @return the nearest fix
	 */
	public final Watchable[] getNearestTo(final HiResDate srchDTG)
	{
		/**
		 * we need to end up with a watchable, not a fix, so we need to work our
		 * way through the fixes
		 */
		FixWrapper res = null;

		// check that we do actually contain some data
		if (_thePositions.size() == 0)
			return new Debrief.Tools.Tote.Watchable[]
			{};

		// special case - if we've been asked for an invalid time value
		if (srchDTG == TimePeriod.INVALID_DATE)
		{
			// just return our first location
			return new Debrief.Tools.Tote.Watchable[]
			{ (Watchable) _thePositions.first() };
		}

		// see if this is the DTG we have just requestsed
		if ((srchDTG.equals(lastDTG)) && (lastFix != null))
		{
			res = lastFix;
		}
		else
		{
			// see if this DTG is inside our data range
			// in which case we will just return null
			final FixWrapper theFirst = (FixWrapper) _thePositions.first();
			final FixWrapper theLast = (FixWrapper) _thePositions.last();

			if ((srchDTG.greaterThanOrEqualTo(theFirst.getTime()))
					&& (srchDTG.lessThanOrEqualTo(theLast.getTime())))
			{
				// yes it's inside our data range, find the first fix
				// after the indicated point

				// right, increment the time, since we want to allow matching
				// points
				// HiResDate DTG = new HiResDate(0, srchDTG.getMicros() + 1);

				// see if we have to create our local temporary fix
				if (nearestFix == null)
				{
					nearestFix = new FixWrapper(new Fix(srchDTG, _zeroLocation,
							0.0, 0.0));
				}
				else
					nearestFix.getFix().setTime(srchDTG);

				// right, we really should be filtering the list according to if
				// the
				// points are visible.
				// how do we do filters?

				// get the data. use tailSet, since it's inclusive...
				SortedSet<Editable> set = _thePositions.tailSet(nearestFix);

				// see if the requested DTG was inside the range of the data
				if (!set.isEmpty())
				{
					res = (FixWrapper) set.first();

					// is this one visible?
					if (!res.getVisible())
					{
						// right, the one we found isn't visible. duplicate the
						// set, so that
						// we can remove items
						// without affecting the parent
						set = new TreeSet<Editable>(set);

						// ok, start looping back until we find one
						while ((!res.getVisible()) && (set.size() > 0))
						{

							// the first one wasn't, remove it
							set.remove(res);
							if (set.size() > 0)
								res = (FixWrapper) set.first();
						}
					}

				}

				// right, that's the first points on or before the indicated
				// DTG. Are we
				// meant
				// to be interpolating?
				if (res != null)
					if (getInterpolatePoints())
					{
						// right - just check that we aren't actually on the
						// correct time
						// point.
						// HEY, USE THE ORIGINAL SEARCH TIME, NOT THE
						// INCREMENTED ONE,
						// SINCE WE DON'T WANT TO COMPARE AGAINST A MODIFIED
						// TIME

						if (!res.getTime().equals(srchDTG))
						{

							// right, we haven't found an actual data point.
							// Better calculate
							// one

							// hmm, better also find the point before our one.
							// the
							// headSet operation is exclusive - so we need to
							// find the one
							// after the first
							final SortedSet<Editable> otherSet = _thePositions
									.headSet(nearestFix);

							FixWrapper previous = null;

							if (!set.isEmpty())
							{
								previous = (FixWrapper) otherSet.last();
							}

							// did it work?
							if (previous != null)
							{
								// cool, sort out the interpolated point USING
								// THE ORIGINAL
								// SEARCH TIME
								res = getInterpolatedFix(previous, res, srchDTG);
							}
						}
					}

			}

			// and remember this fix
			lastFix = res;
			lastDTG = srchDTG;
		}

		if (res != null)
			return new Debrief.Tools.Tote.Watchable[]
			{ res };
		else
			return new Debrief.Tools.Tote.Watchable[]
			{};

	}

	/**
	 * create a new, interpolated point between the two supplied
	 * 
	 * @param previous
	 *            the previous point
	 * @param next
	 *            the next point
	 * @return and interpolated point
	 */
	private final FixWrapper getInterpolatedFix(final FixWrapper previous,
			final FixWrapper next, HiResDate requestedDTG)
	{
		FixWrapper res = null;

		// do we have a start point
		if (previous == null)
			res = next;

		// hmm, or do we have an end point?
		if (next == null)
			res = previous;

		// did we find it?
		if (res == null)
		{
			res = FixWrapper.interpolateFix(previous, next, requestedDTG);
		}

		return res;
	}

	/**
	 * get the set of fixes contained within this time period (inclusive of both
	 * end values)
	 * 
	 * @param start
	 *            start DTG
	 * @param end
	 *            end DTG
	 * @return series of fixes
	 */
	public final Collection<Editable> getItemsBetween(final HiResDate start,
			final HiResDate end)
	{
		//
		SortedSet<Editable> set = null;

		// does our track contain any data at all
		if (_theTrack.hasFixes())
		{

			// see if we have _any_ points in range
			if ((getStartDTG().greaterThan(end))
					|| (getEndDTG().lessThan(start)))
			{
				// don't bother with it.
			}
			else
			{

				// SPECIAL CASE! If we've been asked to show interpolated data
				// points,
				// then
				// we should produce a series of items between the indicated
				// times. How
				// about 1 minute resolution?
				if (getInterpolatePoints())
				{
					long ourInterval = 1000 * 60; // one minute
					set = new TreeSet<Editable>();
					for (long newTime = start.getDate().getTime(); newTime < end
							.getDate().getTime(); newTime += ourInterval)
					{
						HiResDate newD = new HiResDate(newTime);
						Watchable[] nearestOnes = getNearestTo(newD);
						if (nearestOnes.length > 0)
						{
							FixWrapper nearest = (FixWrapper) nearestOnes[0];
							set.add(nearest);
						}
					}
				}
				else
				{
					// bugger that - get the real data

					// have a go..
					if (starter == null)
					{
						starter = new FixWrapper(new Fix((start),
								_zeroLocation, 0.0, 0.0));
					}
					else
					{
						starter.getFix().setTime(
								new HiResDate(0, start.getMicros() - 1));
					}

					if (finisher == null)
					{
						finisher = new FixWrapper(new Fix(new HiResDate(0, end
								.getMicros() + 1), _zeroLocation, 0.0, 0.0));
					}
					else
					{
						finisher.getFix().setTime(
								new HiResDate(0, end.getMicros() + 1));
					}

					// ok, ready, go for it.
					set = _thePositions.subSet(starter, finisher);
				}

			}
		}

		return set;
	}

	/**
	 * get the set of fixes contained within this time period which haven't been
	 * filtered, and which have valid depths. The isVisible flag indicates
	 * whether a track has been filtered or not. We also have the
	 * getVisibleFixesBetween method (below) which decides if a fix is visible
	 * if it is set to Visible, and it's label or symbol are visible. <p/> We
	 * don't have to worry about a valid depth, since 3d doesn't show points
	 * with invalid depth values
	 * 
	 * @param start
	 *            start DTG
	 * @param end
	 *            end DTG
	 * @return series of fixes
	 */
	public final Collection<Editable> getUnfilteredItems(final HiResDate start,
			final HiResDate end)
	{

		// if we have an invalid end point, just return the full track
		if (end == TimePeriod.INVALID_DATE)
		{
			return _thePositions.getData();
		}

		// see if we have _any_ points in range
		if ((getStartDTG().greaterThan(end)) || (getEndDTG().lessThan(start)))
			return null;

		if (this.getVisible() == false)
			return null;

		// get ready for the output
		final Vector<Editable> res = new Vector<Editable>(0, 1);

		// put the data into a period
		final TimePeriod thePeriod = new TimePeriod.BaseTimePeriod(start, end);

		// step through our fixes
		final Enumeration<Editable> iter = _thePositions.elements();
		while (iter.hasMoreElements())
		{
			final FixWrapper fw = (FixWrapper) iter.nextElement();
			if (fw.getVisible())
			{
				// is it visible?
				if (thePeriod.contains(fw.getTime()))
				{
					res.add(fw);
				}
			}
		}

		return res;
	}

	/**
	 * quick accessor for how many fixes we have
	 * 
	 * @return
	 */
	public int numFixes()
	{
		return _thePositions.size();
	}

	/**
	 * get the set of fixes contained within this time period
	 * 
	 * @param start
	 *            start DTG
	 * @param end
	 *            end DTG
	 * @return series of fixes
	 */
	public final Collection<Editable> getVisibleItemsBetween(
			final HiResDate start, final HiResDate end)
	{

		// see if we have _any_ points in range
		if ((getStartDTG().greaterThan(end)) || (getEndDTG().lessThan(start)))
			return null;

		if (this.getVisible() == false)
			return null;

		// get ready for the output
		final Vector<Editable> res = new Vector<Editable>(0, 1);

		// put the data into a period
		final TimePeriod thePeriod = new TimePeriod.BaseTimePeriod(start, end);

		// step through our fixes
		final Enumeration<Editable> iter = _thePositions.elements();
		while (iter.hasMoreElements())
		{
			final FixWrapper fw = (FixWrapper) iter.nextElement();
			if (fw.getVisible()
					&& (fw.getSymbolShowing() || fw.getLabelShowing()))
			{
				// is it visible?
				if (thePeriod.contains(fw.getTime()))
				{
					// hey, it's valid - continue
					res.add(fw);
				}
			}
		}
		return res;
	}

	/**
	 * get the details for this {@link MWC.TacticalData.Track track}
	 * 
	 * @return the Track object we represent
	 */
	public final Track getTrack()
	{
		return _theTrack;
	}

	// //////////////////////////////////////
	// editing parameters
	// //////////////////////////////////////

	/**
	 * name of this Track (normally the vessel name)
	 * 
	 * @return the name
	 */
	public final String getName()
	{
		return _theTrack.getName();
	}

	/**
	 * set the name of this track (normally the name of the vessel
	 * 
	 * @param theName
	 *            the name as a String
	 */
	public final void setName(final String theName)
	{
		_theTrack.setName(theName);
		_theLabel.setString(theName);
	}

	/**
	 * set the colour of this track label
	 * 
	 * @param theCol
	 *            the colour
	 */
	public final void setColor(final Color theCol)
	{
		// do the parent
		super.setColor(theCol);

		// now do our processing
		_theLabel.setColor(theCol);
		_theSnailShape.setColor(theCol);
	}

	// note we are putting a track-labelled wrapper around the colour
	// parameter, to make the properties window less confusing
	/**
	 * the colour of the points on the track
	 * 
	 * @param theCol
	 *            the colour to use
	 */
	public final void setTrackColor(final Color theCol)
	{
		setColor(theCol);
	}

	/**
	 * the colour of the points on the track
	 * 
	 * @return the colour
	 */
	public final Color getTrackColor()
	{
		return getColor();
	}

	/**
	 * determine whether we are linking the points on the track
	 * 
	 * @return yes/no
	 */
	public final boolean getPositionsLinked()
	{
		return _linkPositions;
	}

	/**
	 * indicate whether to join the points on the track
	 * 
	 * @param val
	 *            yes/no
	 */
	public final void setPositionsLinked(final boolean val)
	{
		_linkPositions = val;
	}

	/**
	 * get the position data, not all the sensor/contact/position data mixed
	 * together
	 * 
	 * @return
	 */
	public final Enumeration<Editable> getPositions()
	{
		Enumeration<Editable> res = null;
		if (_thePositions != null)
			res = _thePositions.elements();

		return res;
	}

	/**
	 * font handler
	 * 
	 * @param font
	 *            the font to use for the label
	 */
	public final void setTrackFont(final java.awt.Font font)
	{
		_theLabel.setFont(font);
	}

	/**
	 * font handler
	 * 
	 * @return the font to use for the label
	 */
	public final java.awt.Font getTrackFont()
	{
		return _theLabel.getFont();
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 * 
	 * @return
	 */
	public int getLineThickness()
	{
		return _lineWidth;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 */
	public void setLineThickness(final int val)
	{
		_lineWidth = val;
	}

	/**
	 * get the type of this symbol
	 */
	public final String getSymbolType()
	{
		return _theSnailShape.getType();
	}

	public final void setSymbolType(final String val)
	{
		// is this the type of our symbol?
		if (val.equals(_theSnailShape.getType()))
		{
			// don't bother we're using it already
		}
		else
		{
			// remember the size of the symbol
			final double scale = _theSnailShape.getScaleVal();
			// replace our symbol with this new one
			_theSnailShape = null;
			_theSnailShape = MWC.GUI.Shapes.Symbols.SymbolFactory
					.createSymbol(val);
			_theSnailShape.setColor(this.getColor());

			_theSnailShape.setScaleVal(scale);
		}
	}

	/**
	 * whether the track label is visible or not
	 * 
	 * @return yes/no
	 */
	public final boolean getNameVisible()
	{
		return _theLabel.getVisible();
	}

	/**
	 * whether to show the track name
	 * 
	 * @param val
	 *            yes/no
	 */
	public final void setNameVisible(final boolean val)
	{
		_theLabel.setVisible(val);
	}

	/**
	 * whether to show the track label at the start or end of the track
	 * 
	 * @return yes/no to indicate <I>At Start</I>
	 */
	public final boolean getNameAtStart()
	{
		return _LabelAtStart;
	}

	/**
	 * whether to show the track name at the start or end of the track
	 * 
	 * @param val
	 *            yes no for <I>show label at start</I>
	 */
	public final void setNameAtStart(final boolean val)
	{
		_LabelAtStart = val;
	}

	public boolean hasOrderedChildren()
	{
		return false;
	}

	/**
	 * the relative location of the label
	 * 
	 * @return the relative location
	 */
	public final Integer getNameLocation()
	{
		return _theLabel.getRelativeLocation();
	}

	/**
	 * the relative location of the label
	 * 
	 * @param val
	 *            the relative location
	 */
	public final void setNameLocation(final Integer val)
	{
		_theLabel.setRelativeLocation(val);
	}

	/**
	 * whether the individual fixes themselves are shown either by a symbol or
	 * label
	 * 
	 * @return yes/no
	 */
	public final boolean getPositionsVisible()
	{
		return _showPositions;
	}

	/**
	 * whether to show the position fixes
	 * 
	 * @param val
	 *            yes/no
	 */
	public final void setPositionsVisible(final boolean val)
	{
		_showPositions = val;
	}

	/**
	 * extra parameter, so that jvm can produce a sensible name for this
	 * 
	 * @return the track name, as a string
	 */
	public final String toString()
	{
		return "Track:" + getName();
	}

	/**
	 * return the range from the nearest corner of the track
	 * 
	 * @param other
	 *            the other location
	 * @return the range
	 */
	public final double rangeFrom(final WorldLocation other)
	{
		double nearest = -1;

		// do we have a track?
		if (_theTrack != null)
		{
			// find the nearest point on the track
			nearest = _theTrack.getDataArea().rangeFrom(other);
		}

		return nearest;
	}

	/**
	 * the editable details for this track
	 * 
	 * @return the details
	 */
	public final Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new trackInfo(this);

		return _myEditor;
	}

	/**
	 * whether this object has editor details
	 * 
	 * @return yes/no
	 */
	public final boolean hasEditor()
	{
		return true;
	}

	// /////////////////////////////////////////////////
	// nested interface which contains a single method, taking a
	// boolean parameter
	// ////////////////////////////////////////////////
	/**
	 * interface defining a boolean operation which is applied to all fixes in a
	 * track
	 */
	protected interface FixSetter
	{
		/**
		 * operation to apply to a fix
		 * 
		 * @param fix
		 *            subject of operation
		 * @param val
		 *            yes/no value to apply
		 */
		public void execute(FixWrapper fix, boolean val);
	}

	/**
	 * set the label frequency (in seconds)
	 * 
	 * @param theVal
	 *            frequency to use
	 */
	public final void setLabelFrequency(final HiResDate theVal)
	{
		this._lastLabelFrequency = theVal;

		final FixSetter setLabel = new FixSetter()
		{
			public void execute(final FixWrapper fix, final boolean val)
			{
				fix.setLabelShowing(val);
			}
		};
		setFixes(setLabel, theVal);
	}

	/**
	 * method to allow the setting of label frequencies for the track
	 * 
	 * @return frequency to use
	 */
	public final HiResDate getLabelFrequency()
	{
		return this._lastLabelFrequency;
	}

	/**
	 * how frequently symbols are placed on the track
	 * 
	 * @param theVal
	 *            frequency in seconds
	 */
	public final void setSymbolFrequency(final HiResDate theVal)
	{
		this._lastSymbolFrequency = theVal;

		// set the "showPositions" parameter, as long as we are
		// not setting the symbols off
		if (theVal.getMicros() != 0.0)
		{
			this.setPositionsVisible(true);
		}

		final FixSetter setSymbols = new FixSetter()
		{
			public void execute(final FixWrapper fix, final boolean val)
			{
				fix.setSymbolShowing(val);
			}
		};

		setFixes(setSymbols, theVal);
	}

	/**
	 * return the symbol frequencies for the track
	 * 
	 * @return frequency in seconds
	 */
	public final HiResDate getSymbolFrequency()
	{
		return _lastSymbolFrequency;
	}

	/**
	 * pass through the track, resetting the labels back to their original DTG
	 */
	public void resetLabels()
	{
		FormatTracks.formatTrack(this);
	}

	/**
	 * the setter function which passes through the track
	 */
	private void setFixes(final FixSetter setter, final HiResDate theVal)
	{
		final long freq = theVal.getMicros();

		// briefly check if we are revealing/hiding all times (ie if freq is 1
		// or 0)
		if (freq == TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY)
		{
			// show all of the labels
			final Enumeration<Editable> iter = _thePositions.elements();
			while (iter.hasMoreElements())
			{
				final FixWrapper fw = (FixWrapper) iter.nextElement();
				setter.execute(fw, true);
			}
		}
		else
		{
			// no, we're not just blindly doing all of them. do them at the
			// correct
			// frequency

			// hide all of the labels/symbols first
			final Enumeration<Editable> enumA = _thePositions.elements();
			while (enumA.hasMoreElements())
			{
				final FixWrapper fw = (FixWrapper) enumA.nextElement();
				setter.execute(fw, false);
			}

			if (freq == 0)
			{
				// we can ignore this, since we have just hidden all of the
				// points
			}
			else
			{

				// pass through the track setting the values

				// sort out the start and finish times
				long start_time = getStartDTG().getMicros();
				final long end_time = getEndDTG().getMicros();

				// first check that there is a valid time period between start
				// time
				// and end time
				if (start_time + freq < end_time)
				{
					long num = start_time / freq;

					// we need to add one to the quotient if it has rounded down
					if (start_time % freq == 0)
					{
						// start is at our freq, so we don't need to increment
						// it
					}
					else
					{
						num++;
					}

					// calculate new start time
					start_time = num * freq;
				}
				else
				{
					// there is not one of our 'intervals' between the start and
					// the end,
					// so use the start time
				}

				while (start_time <= end_time)
				{
					// right, increment the start time by one, because we were
					// getting the
					// fix immediately before the requested time
					HiResDate thisDTG = new HiResDate(0, start_time);
					final Debrief.Tools.Tote.Watchable[] list = this
							.getNearestTo(thisDTG);
					// check we found some
					if (list.length > 0)
					{
						final FixWrapper fw = (FixWrapper) list[0];
						setter.execute(fw, true);
					}
					// produce the next time step
					start_time += freq;
				}
			}

		}
	}

	/**
	 * is this track visible between these time periods?
	 * 
	 * @param start
	 *            start DTG
	 * @param end
	 *            end DTG
	 * @return yes/no
	 */
	public final boolean visibleBetween(final HiResDate start,
			final HiResDate end)
	{
		boolean visible = false;
		if (getStartDTG().lessThan(end) && (getEndDTG().greaterThan(start)))
		{
			visible = true;
		}

		return visible;
	}

	// ////////////////////////////////////////////////////
	// LAYER support methods
	// /////////////////////////////////////////////////////

	/**
	 * append this other layer to ourselves (although we don't really bother
	 * with it)
	 * 
	 * @param other
	 *            the layer to add to ourselves
	 */
	public final void append(final Layer other)
	{
		final java.util.Enumeration<Editable> iter = other.elements();
		while (iter.hasMoreElements())
		{
			add(iter.nextElement());
		}
	}

	/**
	 * add the indicated point to the track
	 * 
	 * @param point
	 *            the point to add
	 */
	public final void add(final MWC.GUI.Editable point)
	{
		boolean done = false;
		// see what type of object this is
		if (point instanceof FixWrapper)
		{
			final FixWrapper fw = (FixWrapper) point;
			fw.setTrackWrapper(this);
			addFix(fw);
			done = true;
		}
		else if (point instanceof ContactWrapper)
		{
			addContact((ContactWrapper) point);
			done = true;
		}
		// is this a sensor?
		else if (point instanceof SensorWrapper)
		{
			final SensorWrapper swr = (SensorWrapper) point;
			if (_mySensors == null)
			{
				_mySensors = new Vector<SensorWrapper>(0, 1);
			}
			// add to our list
			_mySensors.add(swr);

			// tell the sensor about us
			swr.setHost(this);

			// and the track name (if we're loading from REP it will already
			// know
			// the name, but if this data is being pasted in, it may start with
			// a different
			// parent track name - so override it here)
			swr.setTrackName(this.getName());

			// indicate success
			done = true;

		}
		// is this a TMA solution track?
		else if (point instanceof TMAWrapper)
		{
			final TMAWrapper twr = (TMAWrapper) point;
			if (_mySolutions == null)
			{
				_mySolutions = new Vector<TMAWrapper>(0, 1);
			}
			// add to our list
			_mySolutions.add(twr);

			// tell the sensor about us
			twr.setHost(this);

			// and the track name (if we're loading from REP it will already
			// know
			// the name, but if this data is being pasted in, it may start with
			// a different
			// parent track name - so override it here)
			twr.setTrackName(this.getName());

			// indicate success
			done = true;

		}

		if (!done)
			MWC.GUI.Dialogs.DialogFactory.showMessage("Add point",
					"Sorry it is not possible to add:" + point.getName()
							+ " to " + this.getName());
	}

	/**
	 * remove the requested item from the track
	 * 
	 * @param point
	 *            the point to remove
	 */
	public final void removeElement(final Editable point)
	{
		// just see if it's a sensor which is trying to be removed
		if (point instanceof SensorWrapper)
		{
			_mySensors.remove(point);
		}
		else if (point instanceof TMAWrapper)
		{
			_mySolutions.remove(point);
		}
		else if (point instanceof SensorContactWrapper)
		{
			// ok, cycle through our sensors, try to remove this contact...
			Iterator<SensorWrapper> iter = _mySensors.iterator();
			while (iter.hasNext())
			{
				SensorWrapper sw = iter.next();
				// try to remove it from this one...
				sw.removeElement(point);
			}
		}
		else
		{
			_thePositions.removeElement(point);
		}

	}

	/**
	 * return our tiered data as a single series of elements
	 * 
	 * @return
	 */
	public final Enumeration<Editable> contiguousElements()
	{
		final Vector<Editable> res = new Vector<Editable>(0, 1);

		if (_mySensors != null)
		{
			final Enumeration<SensorWrapper> iter = _mySensors.elements();
			while (iter.hasMoreElements())
			{
				res.add(iter.nextElement());
			}
		}

		if (_mySolutions != null)
		{
			final Enumeration<TMAWrapper> iter = _mySolutions.elements();
			while (iter.hasMoreElements())
			{
				res.add(iter.nextElement());
			}
		}

		if (_thePositions != null)
		{
			final Enumeration<Editable> iter = _thePositions.elements();
			while (iter.hasMoreElements())
			{
				res.add(iter.nextElement());
			}
		}

		return res.elements();
	}

	/**
	 * get an enumeration of the points in this track
	 * 
	 * @return the points in this track
	 */
	public final Enumeration<Editable> elements()
	{
		TreeSet<Editable> res = new TreeSet<Editable>();

		if (_mySensors != null)
		{
			final Enumeration<SensorWrapper> iter = _mySensors.elements();
			while (iter.hasMoreElements())
			{
				res.add(iter.nextElement());
			}
		}

		if (_mySolutions != null)
		{

			final Enumeration<TMAWrapper> iter = _mySolutions.elements();
			while (iter.hasMoreElements())
			{
				res.add(iter.nextElement());
			}
		}

		if (res == null)
		{
			final Enumeration<Editable> iter = _thePositions.elements();
			while (iter.hasMoreElements())
			{
				res.add(iter.nextElement());
			}
		}
		else
		{
			// ok, we want to wrap our fast-data as a set of plottables
			res.add(_thePositions);
		}

		return new IteratorWrapper(res.iterator());
	}

	/**
	 * export this track to REPLAY file
	 */
	public final void exportShape()
	{
		// call the method in PlainWrapper
		this.exportThis();
	}

	// ////////////////////////////////////////////////////
	// track-shifting operation
	// /////////////////////////////////////////////////////

	// /////////////////////////////////////////////////
	// support for dragging the track around
	// ////////////////////////////////////////////////

	public final void setDragTrack(TrackWrapper track)
	{
		//
	}

	public final TrackWrapper getDragTrack()
	{
		return this;
	}

	public final boolean getInterpolatePoints()
	{
		return _interpolatePoints;
	}

	public final void setInterpolatePoints(boolean val)
	{
		_interpolatePoints = val;
	}

	/**
	 * move the whole of the track be the provided offset
	 */
	public final void shiftTrack(Enumeration<Editable> theEnum,
			final WorldVector offset)
	{
		// keep track of if the track contains something that doesn't get
		// dragged
		boolean handledData = false;

		if (theEnum == null)
			theEnum = elements();

		while (theEnum.hasMoreElements())
		{
			final Object thisO = theEnum.nextElement();
			if (thisO instanceof FixWrapper)
			{
				final FixWrapper fw = (FixWrapper) thisO;

				WorldLocation copiedLoc = new WorldLocation(fw.getFix()
						.getLocation());
				copiedLoc.addToMe(offset);

				// and replace the location (this method updates all 3 location
				// contained
				// in the fix wrapper
				fw.setFixLocation(copiedLoc);

				// ok - job well done
				handledData = true;

			} // whether this was a fix wrapper
			else if (thisO instanceof SensorWrapper)
			{
				final SensorWrapper sw = (SensorWrapper) thisO;
				final Enumeration<Editable> enumS = sw.elements();
				while (enumS.hasMoreElements())
				{
					final SensorContactWrapper scw = (SensorContactWrapper) enumS
							.nextElement();
					// does this fix have it's own origin?
					final WorldLocation sensorOrigin = scw.getOrigin(null);

					if (sensorOrigin != null)
					{
						// create new object to contain the updated location
						WorldLocation newSensorLocation = new WorldLocation(
								sensorOrigin);
						newSensorLocation.addToMe(offset);

						// so the contact did have an origin, change it
						scw.setOrigin(newSensorLocation);
					}
				} // looping through the contacts

				// ok - job well done
				handledData = true;

			} // whether this is a sensor wrapper
			else if (thisO instanceof TrackWrapper.PlottableLayer)
			{
				final PlottableLayer tw = (PlottableLayer) thisO;
				final Enumeration<Editable> enumS = tw.elements();

				// fire recursively, smart-arse.
				shiftTrack(enumS, offset);

				// ok - job well done
				handledData = true;

			} // whether this is a sensor wrapper
		} // looping through this track

		// ok, did we handle the data?
		if (!handledData)
		{
			System.err.println("TrackWrapper problem; not able to shift:"
					+ theEnum);
		}
	}

	// ///////////////////////////////////////////////////////////////
	// read/write operations
	// //////////////////////////////////////////////////////////////
	// private void readObject(java.io.ObjectInputStream in)
	// throws IOException, ClassNotFoundException
	// {
	// in.defaultReadObject();
	//
	// // TOD: eventually, remove support for the old "_theData" type
	// // of storing tracks, leave to store in native (treeSet) storage
	//
	// // see if we are processing an old version of the file
	// /* if(this._fastData == null)
	// {
	// _fastData = new com.sun.java.util.collections.TreeSet(new
	// compareFixes());
	//
	// // move all of the contents of the vector to the fast wrapper
	// java.util.Enumeration enum = _theData.elements();
	// while(enum.hasMoreElements())
	// {
	// FixWrapper fw = (FixWrapper)enum.nextElement();
	// _fastData.add(fw);
	// }
	//
	//
	// // now remove all of the elements from the old structure
	// _theData.removeAllElements();
	// _theData = null;
	// }
	// */
	// // check that we have our track lable
	// if(_theLabel == null)
	// {
	// _theLabel = new MWC.GUI.Shapes.TextLabel(new WorldLocation(0,0,0), null);
	// _theLabel.setName(getName());
	// _theLabel.setColor(getColor());
	// }
	// }
	//
	//
	// private void writeObject(java.io.ObjectOutputStream out)
	// throws IOException
	// {
	// // put the fast data into the old vector
	//
	// // create the old vector
	// /* _theData = new java.util.Vector(_fastData.size(), 1);
	//
	// // get an iterator from the fast data
	// Iterator it = _fastData.iterator();
	//
	// // put the fast data into the old array
	// while(it.hasNext())
	// {
	// _theData.addElement(it.next());
	// }
	//
	// */
	// // allow the default write to copy the array to storage
	// out.defaultWriteObject();
	//
	//
	// }

	// //////////////////////////////////////
	// beaninfo
	// //////////////////////////////////////

	/**
	 * class containing editable details of a track
	 */
	public final class trackInfo extends Editable.EditorType
	{

		/**
		 * constructor for this editor, takes the actual track as a parameter
		 * 
		 * @param data
		 *            track being edited
		 */
		public trackInfo(final TrackWrapper data)
		{
			super(data, data.getName(), "");
		}

		public final String getName()
		{
			return getTrack().getName();
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						expertProp("SymbolType",
								"the type of symbol plotted for this label",
								FORMAT),
						legacyProp("DragTrack", "drag the track location"),
						expertProp("LineThickness",
								"the width to draw this track", FORMAT),
						expertProp("Name", "the track name"),
						expertProp(
								"InterpolatePoints",
								"whether to interpolate points between known data points",
								SPATIAL),
						expertProp("Color", "the track color", FORMAT),
						expertProp("TrackFont", "the track label font", FORMAT),
						expertProp("PositionsLinked",
								"link the track Positions"),
						expertProp("NameVisible", "show the track label",
								VISIBILITY),
						expertProp("PositionsVisible",
								"show individual Positions", VISIBILITY),
						expertProp(
								"NameAtStart",
								"whether to show the track name at the start (or end)",
								VISIBILITY),
						expertProp("Visible", "whether the track is visible",
								VISIBILITY),
						expertLongProp("NameLocation",
								"relative location of track label",
								MWC.GUI.Properties.LocationPropertyEditor.class),
						expertLongProp(
								"LabelFrequency",
								"the label frequency",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertLongProp(
								"SymbolFrequency",
								"the symbol frequency",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class)

				};
				res[0]
						.setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.class);
				res[1]
						.setPropertyEditorClass(Debrief.Tools.Reconstruction.DragTrackEditor.class);
				res[2]
						.setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);
				return res;
			}
			catch (IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}

		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class<TrackWrapper> c = TrackWrapper.class;

			final MethodDescriptor[] mds =
			{ method(c, "exportThis", null, "Export Shape"),
					method(c, "resetLabels", null, "Reset DTG Labels") };

			return mds;
		}

	}

	// //////////////////////////////////////////////////////////////////
	// embedded class to allow us to pass the local iterator (Iterator) used
	// internally
	// outside as an Enumeration
	// /////////////////////////////////////////////////////////////////
	private static final class IteratorWrapper implements
			java.util.Enumeration<Editable>
	{
		private final Iterator<Editable> _val;

		public IteratorWrapper(final Iterator<Editable> iterator)
		{
			_val = iterator;
		}

		public final boolean hasMoreElements()
		{
			return _val.hasNext();

		}

		public final Editable nextElement()
		{
			return _val.next();
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";
		
      
		private static class TestMockCanvas extends MockCanvasType
        {
            public void drawPolyline(int[] points)
            {
                callCount++;
                pointCount += points.length;
            }
        };
		
        /** utility to track number of calls
         * 
         */
        static int callCount = 0;
        
        /** utility to track number of points passed to paint polyline method
         * 
         */
        static int pointCount = 0;

		public testMe(final String val)
		{
			super(val);
		}

		public final void testMyParams()
		{
			TrackWrapper ed = new TrackWrapper();
			ed.setTrack(new Track());
			ed.setName("blank");

			editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		public void testPaintingColChange()
		{
			final TrackWrapper tw = new TrackWrapper();
			tw.setTrack(new Track());
			tw.setColor(Color.RED);
			tw.setName("test track");

			/** intention of this test:
             * line is broken into three segments (red, yellow, green).
             * - first of 2 points, next of 2 points, last of 3 points (14 values)
             */

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100,
					10000), loc_1.add(new WorldVector(33, new WorldDistance(
					100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			fw1.setColor(Color.red);
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200,
					20000), loc_1.add(new WorldVector(33, new WorldDistance(
					200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			fw2.setColor(Color.yellow);
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300,
					30000), loc_1.add(new WorldVector(33, new WorldDistance(
					300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			fw3.setColor(Color.green);
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400,
					40000), loc_1.add(new WorldVector(33, new WorldDistance(
					400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			fw4.setColor(Color.green);
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500,
					50000), loc_1.add(new WorldVector(33, new WorldDistance(
					500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			fw5.setColor(Color.green);
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			callCount = 0;
			pointCount = 0;

			assertNull("our array of points starts empty", tw._myPts);
			assertEquals("our point array counter is zero", tw._ptCtr, 0);

			CanvasType dummyDest = new TestMockCanvas();

			tw.paint(dummyDest);

			assertEquals("our array has correct number of points", 10,
					tw._myPts.length);
			assertEquals("the pointer counter has been reset", 0, tw._ptCtr);

			// check it got called the correct number of times
			assertEquals("We didnt paint enough polygons", 3, callCount);
            assertEquals("We didnt paint enough polygons points", 14, pointCount);
		}


		public void testPaintingLineJoinedChange()
		{
			final TrackWrapper tw = new TrackWrapper();
			tw.setTrack(new Track());
			tw.setColor(Color.RED);
			tw.setName("test track");

            /** intention of this test:
             * line is broken into two segments - one of two points, the next of three, thus two polygons should be drawn
             * - 10 points total (4 then 6).
             */
			
			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100,
					10000), loc_1.add(new WorldVector(33, new WorldDistance(
					100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			fw1.setColor(Color.red);
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200,
					20000), loc_1.add(new WorldVector(33, new WorldDistance(
					200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			fw2.setColor(Color.red);
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300,
					30000), loc_1.add(new WorldVector(33, new WorldDistance(
					300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			fw3.setColor(Color.red);
			fw3.setLineShowing(false);
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400,
					40000), loc_1.add(new WorldVector(33, new WorldDistance(
					400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			fw4.setColor(Color.red);
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500,
					50000), loc_1.add(new WorldVector(33, new WorldDistance(
					500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			fw5.setColor(Color.red);
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			callCount = 0;
            pointCount = 0;			

			assertNull("our array of points starts empty", tw._myPts);
			assertEquals("our point array counter is zero", tw._ptCtr, 0);

            CanvasType dummyDest = new TestMockCanvas();

			tw.paint(dummyDest);

			assertEquals("our array has correct number of points", 10,
					tw._myPts.length);
			assertEquals("the pointer counter has been reset", 0, tw._ptCtr);

			// check it got called the correct number of times
			assertEquals("We didnt paint enough polygons", 2, callCount);
            assertEquals("We didnt paint enough polygons points", 10, pointCount);
			
		}		
		
		
		public void testPaintingVisChange()
		{
			final TrackWrapper tw = new TrackWrapper();
			tw.setTrack(new Track());
			tw.setColor(Color.RED);
			tw.setName("test track");

			/** intention of this test:
			 * line is broken into two segments of two points, thus two polygons should be drawn, each with 4 points
			 * - 8 points total.
			 */
			
			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100,
					10000), loc_1.add(new WorldVector(33, new WorldDistance(
					100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			fw1.setColor(Color.red);
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200,
					20000), loc_1.add(new WorldVector(33, new WorldDistance(
					200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			fw2.setColor(Color.red);
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300,
					30000), loc_1.add(new WorldVector(33, new WorldDistance(
					300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			fw3.setColor(Color.red);
			fw3.setVisible(false);
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400,
					40000), loc_1.add(new WorldVector(33, new WorldDistance(
					400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			fw4.setColor(Color.red);
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500,
					50000), loc_1.add(new WorldVector(33, new WorldDistance(
					500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			fw5.setColor(Color.red);
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			callCount = 0;
            pointCount = 0;			

			assertNull("our array of points starts empty", tw._myPts);
			assertEquals("our point array counter is zero", tw._ptCtr, 0);

            CanvasType dummyDest = new TestMockCanvas();

			tw.paint(dummyDest);

			assertEquals("our array has correct number of points", 10,
					tw._myPts.length);
			assertEquals("the pointer counter has been reset", 0, tw._ptCtr);

			// check it got called the correct number of times
			assertEquals("We didnt paint enough polygons", 2, callCount);
            assertEquals("We didnt paint enough polygons points", 8, pointCount);
			
		}		
		


		public final void testInterpolation()
		{
			final TrackWrapper tw = new TrackWrapper();

			tw.setTrack(new Track());

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100,
					10000), loc_1.add(new WorldVector(33, new WorldDistance(
					100, WorldDistance.METRES), null)), 10, 110));
			fw1.setLabel("fw1");
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200,
					20000), loc_1.add(new WorldVector(33, new WorldDistance(
					200, WorldDistance.METRES), null)), 20, 120));
			fw2.setLabel("fw2");
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300,
					30000), loc_1.add(new WorldVector(33, new WorldDistance(
					300, WorldDistance.METRES), null)), 30, 130));
			fw3.setLabel("fw3");
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400,
					40000), loc_1.add(new WorldVector(33, new WorldDistance(
					400, WorldDistance.METRES), null)), 40, 140));
			fw4.setLabel("fw4");
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500,
					50000), loc_1.add(new WorldVector(33, new WorldDistance(
					500, WorldDistance.METRES), null)), 50, 150));
			fw5.setLabel("fw5");
			tw.addFix(fw1);
			tw.addFix(fw2);
			// tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);

			// check that we're not interpolating
			assertFalse("interpolating switched off by default", tw
					.getInterpolatePoints());

			// ok, get on with it.
			Watchable[] list = tw.getNearestTo(new HiResDate(200, 20000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw2);

			// and the end
			list = tw.getNearestTo(new HiResDate(500, 50000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw5);

			// and now an in-between point
			// ok, get on with it.
			list = tw.getNearestTo(new HiResDate(230, 23000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw4);

			// ok, with interpolation on
			tw.setInterpolatePoints(true);

			assertTrue("interpolating now switched on", tw
					.getInterpolatePoints());

			// ok, get on with it.
			list = tw.getNearestTo(new HiResDate(200, 20000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw2);

			// and the end
			list = tw.getNearestTo(new HiResDate(500, 50000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);
			assertEquals("right answer", list[0], fw5);

			// hey

			// and now an in-between point
			// ok, get on with it.
			list = tw.getNearestTo(new HiResDate(300, 30000));
			assertNotNull("found list", list);
			assertEquals("contains something", list.length, 1);

			// have a look at them
			FixWrapper res = (FixWrapper) list[0];
			WorldVector rangeError = res.getFixLocation().subtract(
					fw3.getFixLocation());
			assertEquals("right answer", 0, Conversions.Degs2m(rangeError
					.getRange()), 0.0001);
			// assertEquals("right speed", res.getSpeed(), fw3.getSpeed(), 0);
			// assertEquals("right course", res.getCourse(), fw3.getCourse(),
			// 0);

		}

		public final void testGettingTimes()
		{
			// Enumeration<SensorContactWrapper>
			final TrackWrapper tw = new TrackWrapper();

			tw.setTrack(new Track());

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final WorldLocation loc_2 = new WorldLocation(1, 1, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(
					new HiResDate(0, 100), loc_1, 0, 0));
			final FixWrapper fw2 = new FixWrapper(new Fix(
					new HiResDate(0, 300), loc_2, 0, 0));
			final FixWrapper fw3 = new FixWrapper(new Fix(
					new HiResDate(0, 500), loc_2, 0, 0));
			final FixWrapper fw4 = new FixWrapper(new Fix(
					new HiResDate(0, 700), loc_2, 0, 0));

			// check returning empty data
			Collection<Editable> coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 40));
			assertEquals("Return empty when empty", coll, null);

			tw.addFix(fw1);

			// check returning single field
			coll = tw
					.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0,
					540));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 140));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 100), new HiResDate(0,
					100));
			assertEquals("Return valid point", coll.size(), 1);

			tw.addFix(fw2);

			// check returning with fields
			coll = tw
					.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0,
					540));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 140));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 440));
			assertEquals("Return valid point", coll.size(), 2);

			coll = tw.getItemsBetween(new HiResDate(0, 150), new HiResDate(0,
					440));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0,
					440));
			assertEquals("Return valid point", coll.size(), 1);

			tw.addFix(fw3);

			// check returning with fields
			coll = tw
					.getItemsBetween(new HiResDate(0, 0), new HiResDate(0, 40));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 520), new HiResDate(0,
					540));
			assertEquals("Return empty when out of range", coll, null);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 140));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 0),
					new HiResDate(0, 440));
			assertEquals("Return valid point", coll.size(), 2);

			coll = tw.getItemsBetween(new HiResDate(0, 150), new HiResDate(0,
					440));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0,
					440));
			assertEquals("Return valid point", coll.size(), 1);

			coll = tw.getItemsBetween(new HiResDate(0, 100), new HiResDate(0,
					300));
			assertEquals("Return valid point", coll.size(), 2);

			coll = tw.getItemsBetween(new HiResDate(0, 300), new HiResDate(0,
					500));
			assertEquals("Return valid point", coll.size(), 2);

			tw.addFix(fw4);

		}

		public void testGetItemsBetween_Second()
		{
			final TrackWrapper tw = new TrackWrapper();

			tw.setTrack(new Track());

			final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
			final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(0, 1),
					loc_1, 0, 0));
			final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(0, 2),
					loc_1, 0, 0));
			final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(0, 3),
					loc_1, 0, 0));
			final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(0, 4),
					loc_1, 0, 0));
			final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(0, 5),
					loc_1, 0, 0));
			final FixWrapper fw6 = new FixWrapper(new Fix(new HiResDate(0, 6),
					loc_1, 0, 0));
			final FixWrapper fw7 = new FixWrapper(new Fix(new HiResDate(0, 7),
					loc_1, 0, 0));
			tw.addFix(fw1);
			tw.addFix(fw2);
			tw.addFix(fw3);
			tw.addFix(fw4);
			tw.addFix(fw5);
			tw.addFix(fw6);
			tw.addFix(fw7);
			fw1.setLabelShowing(true);
			fw2.setLabelShowing(true);
			fw3.setLabelShowing(true);
			fw4.setLabelShowing(true);
			fw5.setLabelShowing(true);
			fw6.setLabelShowing(true);
			fw7.setLabelShowing(true);

			Collection<Editable> col = tw.getItemsBetween(new HiResDate(0, 3),
					new HiResDate(0, 5));
			assertEquals("found correct number of items", 3, col.size());

			// make the fourth item not visible
			fw4.setVisible(false);

			col = tw.getVisibleItemsBetween(new HiResDate(0, 3), new HiResDate(
					0, 5));
			assertEquals("found correct number of items", 2, col.size());

			Watchable[] pts2 = tw.getNearestTo(new HiResDate(0, 3));
			assertEquals("found something", 1, pts2.length);
			assertEquals("found the third item", fw3, pts2[0]);

			Watchable[] pts = tw.getNearestTo(new HiResDate(0, 1));
			assertEquals("found something", 1, pts.length);
			assertEquals("found the first item", fw1, pts[0]);

		}
	}

	public static void main(final String[] args)
	{
		final testMe tm = new testMe("scrap");
		tm.testGettingTimes();
		tm.testGetItemsBetween_Second();
		tm.testMyParams();

	}

	public void shift(WorldVector vector)
	{
		this.shiftTrack(elements(), vector);
	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			LocationConstruct currentNearest, Layer parentLayer)
	{
		// initialise thisDist, since we're going to be over-writing it
		WorldDistance thisDist = new WorldDistance(0, WorldDistance.DEGS);

		// cycle through the fixes
		Enumeration<Editable> fixes = _thePositions.elements();
		while (fixes.hasMoreElements())
		{
			final FixWrapper thisF = (FixWrapper) fixes.nextElement();

			if (thisF.getVisible())
			{
				// how far away is it?
				thisDist = thisF.getLocation().rangeFrom(cursorLoc, thisDist);

				// is it closer?
				currentNearest.checkMe(this, thisDist, null, parentLayer);
			}
		}
	}

	public void shift(WorldLocation feature, WorldVector vector)
	{
		feature.addToMe(vector);
	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			ComponentConstruct currentNearest, Layer parentLayer)
	{
		// initialise thisDist, since we're going to be over-writing it
		WorldDistance thisDist = new WorldDistance(0, WorldDistance.DEGS);

		// cycle through the fixes
		Enumeration<Editable> fixes = _thePositions.elements();
		while (fixes.hasMoreElements())
		{
			final FixWrapper thisF = (FixWrapper) fixes.nextElement();

			// only check it if it's visible
			if (thisF.getVisible())
			{

				// how far away is it?
				thisDist = thisF.getLocation().rangeFrom(cursorLoc, thisDist);

				WorldLocation fixLocation = new WorldLocation(thisF
						.getLocation())
				{
					private static final long serialVersionUID = 1L;

					public void addToMe(WorldVector delta)
					{
						super.addToMe(delta);
						thisF.setFixLocation(this);
					}
				};

				// try range
				currentNearest.checkMe(this, thisDist, null, parentLayer,
						fixLocation);
			}
		}

	}

	/**
	 * convenience class that makes our plottables look like a layer
	 * 
	 * @author ian.mayo
	 */
	public class PlottableLayer extends Plottables implements Layer
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * only allow fixes to be added...
		 * 
		 * @param thePlottable
		 */
		public void add(Editable thePlottable)
		{
			if (thePlottable instanceof FixWrapper)
			{
				super.add(thePlottable);
			}
			else
			{
				System.err.println("Trying to add wront");
			}
		}

		/**
		 * get the editing information for this type
		 */
		public Editable.EditorType getInfo()
		{
			return new plottableLayerInfo(this);
		}

		public void append(Layer other)
		{
			// ok, pass through and add the items
			Enumeration<Editable> enumer = other.elements();
			while (enumer.hasMoreElements())
			{
				Plottable pl = (Plottable) enumer.nextElement();
				add(pl);
			}
		}

		public boolean hasOrderedChildren()
		{
			return true;
		}

		public void exportShape()
		{
			// ignore..
		}

		public int getLineThickness()
		{
			// ignore..
			return 1;
		}

		/**
		 * @return
		 */
		@Override
		public boolean getVisible()
		{
			return getPositionsLinked();
		}

		/**
		 * @param visible
		 */
		@Override
		public void setVisible(boolean visible)
		{
			setPositionsLinked(visible);
		}

		/**
		 * class containing editable details of a track
		 */
		public final class plottableLayerInfo extends Editable.EditorType
		{

			/**
			 * constructor for this editor, takes the actual track as a
			 * parameter
			 * 
			 * @param data
			 *            track being edited
			 */
			public plottableLayerInfo(final PlottableLayer data)
			{
				super(data, data.getName(), "");
			}

			public final String getName()
			{
				return getTrack().getName();
			}

			public final PropertyDescriptor[] getPropertyDescriptors()
			{
				try
				{
					final PropertyDescriptor[] res =
					{ expertProp("Visible", "whether this layer is visible",
							FORMAT), };
					return res;
				}
				catch (IntrospectionException e)
				{
					e.printStackTrace();
					return super.getPropertyDescriptors();
				}
			}
		}

	}

}
