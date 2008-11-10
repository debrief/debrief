// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: BuoyPatternWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.14 $
// $Log: BuoyPatternWrapper.java,v $
// Revision 1.14  2007/04/25 09:32:43  ian.mayo
// Prevent highlight being plotted for sensor & TMA data
//
// Revision 1.13  2007/04/16 09:48:08  ian.mayo
// Remove debug lines, slight JDK1.5 syntax updates (generics)
//
// Revision 1.12  2007/04/16 08:23:14  ian.mayo
// Include debug code
//
// Revision 1.11  2007/04/05 13:38:11  ian.mayo
// Improve how we decide whether to show plot highlight, make sure debrief legacy plugin gets installed
//
// Revision 1.10  2007/04/04 14:12:13  ian.mayo
// Correct how buoy patterns displayed by plot highlighter
//
// Revision 1.9  2006/09/25 14:51:14  Ian.Mayo
// Respect new "has children" property of Layers
//
// Revision 1.8  2005/12/13 09:04:57  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.7  2004/11/25 11:04:37  Ian.Mayo
// More test fixing after hi-res switch, largely related to me removing some unused accessors which were property getters
//
// Revision 1.6  2004/11/25 10:24:43  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.5  2004/11/22 14:05:06  Ian.Mayo
// Replace variable name previously used for counting through enumeration - now part of JDK1.5
//
// Revision 1.4  2004/09/10 09:11:25  Ian.Mayo
// Correct prior mistaken implementation of add(Editable) - we should have just changed the signature of add(Plottable) et al
//
// Revision 1.3  2004/09/09 10:51:53  Ian.Mayo
// Provide missing methods from Layers structure.  Don't know why they had been missing for so long.  Poss disconnect between ASSET/Debrief development trees
//
// Revision 1.2  2004/09/09 10:23:11  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:49:19  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.13  2003-07-04 10:59:30+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.12  2003-03-27 11:22:55+00  ian_mayo
// reflect new strategy where we return all data when asked to filter by invalid time
//
// Revision 1.11  2003-03-19 15:36:56+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.10  2003-03-18 16:08:08+00  ian_mayo
// Store & manage the kingpin
//
// Revision 1.9  2002-10-30 16:27:28+00  ian_mayo
// tidy up (shorten) display names of editables
//
// Revision 1.8  2002-10-28 09:04:35+00  ian_mayo
// provide support for variable thickness of lines in tracks, etc
//
// Revision 1.7  2002-10-01 15:41:58+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.6  2002-07-23 08:47:53+01  ian_mayo
// Implement additional time period methods
//
// Revision 1.5  2002-07-10 14:58:59+01  ian_mayo
// correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.4  2002-05-28 11:35:01+01  ian_mayo
// Implement exporting
//
// Revision 1.3  2002-05-28 09:25:12+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:41+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-04-23 16:05:34+01  ian_mayo
// Reflect new TimePeriod signature
//
// Revision 1.1  2002-04-23 12:28:19+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-10-01 12:49:51+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.1  2001-08-29 19:17:34+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.0  2001-07-17 08:41:10+01  administrator
// Initial revision
//
// Revision 1.5  2001-01-24 11:35:04+00  novatech
// consistent time values
//
// Revision 1.4  2001-01-22 12:31:45+00  novatech
// change name of StartTime to TimeStart, Time_End
//
// Revision 1.3  2001-01-21 21:34:41+00  novatech
// handle label colours
//
// Revision 1.2  2001-01-18 13:18:14+00  novatech
// - provide support for editing the buoys in the field
// - allow field to return ALL buoys if null-dates passed in
//
// Revision 1.1  2001-01-17 13:20:19+00  novatech
// Initial revision
//
// Revision 1.10  2001-01-17 09:46:26+00  novatech
// tidy up bounds checking
//
// Revision 1.9  2001-01-15 11:19:15+00  novatech
// return the shape for this track
//
// Revision 1.8  2001-01-11 15:36:51+00  novatech
// <>
//
// Revision 1.7  2001-01-11 11:53:01+00  novatech
// Before we switch from long dates to java.util.date dates.
//
// Revision 1.6  2001-01-09 10:29:15+00  novatech
// add extra parameters to allow WatchableLists to be used instead of TrackWrappers
//
// Revision 1.5  2001-01-08 14:14:06+00  novatech
// provide "Visible" property
//
// Revision 1.4  2001-01-05 09:14:26+00  novatech
// Provide more useful Layer name, which lists the number of buoys in the field
//
// Revision 1.3  2001-01-04 16:36:46+00  novatech
// handle missing data
//
// Revision 1.2  2001-01-04 14:02:03+00  novatech
// further implementation, including addition of time details
//
// Revision 1.1  2001-01-03 16:02:22+00  novatech
// Initial revision
//

package Debrief.Wrappers;

import Debrief.GUI.Tote.Painters.SnailPainter.DoNotHighlightMe;
import Debrief.Tools.Tote.Watchable;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.HiResDate;

import java.awt.*;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * the BuoyPatternWrapper is a Layer containing a buoypattern
 */
public final class BuoyPatternWrapper extends MWC.GUI.PlainWrapper implements
		java.io.Serializable, Debrief.Tools.Tote.WatchableList, 
		MWC.GUI.Layer, TimePeriod, DoNotHighlightMe
{

	// //////////////////////////////////////
	// member variables
	// //////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * our editable details
	 */
	transient private Editable.EditorType _myEditor = null;

	/**
	 * the label describing this pattern
	 */
	private final MWC.GUI.Shapes.TextLabel _theLabel;

	/**
	 * the buoys in this pattern
	 */
	private final java.util.Vector<Editable> _myBuoys;

	/**
	 * the area we cover
	 */
	private WorldArea _myArea;

	/**
	 * the time period we cover
	 */
	private final TimePeriod.BaseTimePeriod _thePeriod;

	/**
	 * the shape we use when in snail mode
	 */
	private final MWC.GUI.Shapes.Symbols.PlainSymbol _snailShape;

	/**
	 * the colour used for the buoys in this pattern
	 */
	private java.awt.Color _buoyPatternColour = Color.white;

	/**
	 * the symbol type used for the buoys in this pattern
	 */
	private String _buoySymbolType = "Square";

	/**
	 * the symbol size for the buoys in this pattern
	 */
	private double _buoySymbolSize = 1.0;

	/**
	 * the label visibility for the buoys in this pattern
	 */
	private boolean _buoyLabelVisible = false;

	/**
	 * the label location for the buoys in this pattern
	 */
	private int _buoyLabelLocation = MWC.GUI.Properties.LocationPropertyEditor.LEFT;

	/**
	 * width of line to draw
	 */
	private int _lineWidth = 1;

	/**
	 * the kingpin for this buoy pattern
	 */
	private LabelWrapper _theKingpin;

	// //////////////////////////////////////
	// constructors
	// //////////////////////////////////////
	/**
	 * Wrapper for a buoy pattern
	 */
	public BuoyPatternWrapper(final WorldLocation origin)
	{
		_myBuoys = new java.util.Vector<Editable>(0, 1);
		_theLabel = new MWC.GUI.Shapes.TextLabel(origin, "waiting");
		_thePeriod = new TimePeriod.BaseTimePeriod(null, null);
		// don't set a snail shape, we will stick to "highlighting" the field
		_snailShape = null;
	}

	/**
	 * no-op constructor, mainly for XML creation
	 */
	public BuoyPatternWrapper()
	{
		this(null);
	}

	// //////////////////////////////////////
	// member functions
	// //////////////////////////////////////

	/**
	 * draw this pattern
	 * 
	 * @param dest
	 *          the destination
	 */
	public void paint(final CanvasType dest)
	{
		if (getVisible())
		{

			dest.setColor(getColor());

			// start off with the buoys
			final Enumeration iter = _myBuoys.elements();
			while (iter.hasMoreElements())
			{
				final Plottable pl = (Plottable) iter.nextElement();
				pl.paint(dest);
			}

			// and draw the pattern label
			if (_theLabel.getVisible())
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

				}
			}
		}
	}

	/**
	 * what geographic area is covered by this pattern?
	 * 
	 * @return get the outer bounds of the area
	 */
	public WorldArea getBounds()
	{
		// get the bounds from the data object
		return _myArea;
	}

	private void updateLabelLocation()
	{
		final WorldLocation newLoc = getAnchor(_theLabel.getRelativeLocation().intValue());
		if (newLoc != null)
			_theLabel.setLocation(newLoc);
	}
	
	
	public boolean hasOrderedChildren()
	{
		return false;
	}
		

	private WorldLocation getAnchor(final int location)
	{
		WorldLocation loc = null;

		// find out the area
		final WorldArea wa = getBounds();

		// check that we've received data
		if (wa != null)
		{

			final WorldLocation centre = wa.getCentre();
			switch (location)
			{
			case MWC.GUI.Properties.LocationPropertyEditor.TOP:
			{
				final WorldLocation res = new WorldLocation(wa.getTopLeft().getLat(), centre
						.getLong(), 0);
				loc = res;
				break;
			}
			case MWC.GUI.Properties.LocationPropertyEditor.BOTTOM:
			{
				final WorldLocation res = new WorldLocation(wa.getBottomRight().getLat(), centre
						.getLong(), 0);
				loc = res;
				break;
			}
			case MWC.GUI.Properties.LocationPropertyEditor.LEFT:
			{
				final WorldLocation res = new WorldLocation(centre.getLat(), wa.getTopLeft()
						.getLong(), 0);
				loc = res;
				break;
			}
			case MWC.GUI.Properties.LocationPropertyEditor.RIGHT:
			{
				final WorldLocation res = new WorldLocation(centre.getLat(), wa.getBottomRight()
						.getLong(), 0);
				loc = res;
				break;
			}
			case MWC.GUI.Properties.LocationPropertyEditor.CENTRE:
			{
				loc = centre;
			}
			}
		} // whether we have any data yet

		return loc;
	}

	// //////////////////////////////////////
	// time period related methods
	// //////////////////////////////////////
	public void extend(final HiResDate date)
	{
		_thePeriod.extend(date);
	}

	// //////////////////////////////////////
	// watchable (tote related) parameters
	// //////////////////////////////////////
	/**
	 * the earliest fix in the pattern
	 * 
	 * @return the DTG
	 */
	public HiResDate getStartDTG()
	{
		return _thePeriod.getStartDTG();
	}

	/**
	 * the time of the last fix
	 * 
	 * @return the DTG
	 */
	public HiResDate getEndDTG()
	{
		return _thePeriod.getEndDTG();
	}

	/**
	 * set the start time
	 */
	public void setStartDTG(final HiResDate val)
	{
		_thePeriod.setStartDTG(val);
	}

	/**
	 * set the end time
	 */
	public void setEndDTG(final HiResDate val)
	{
		_thePeriod.setEndDTG(val);
	}

	public boolean contains(final HiResDate val)
	{
		return _thePeriod.contains(val);
	}

	/**
	 * see if this time period overlaps the one provided
	 */
	public boolean overlaps(final TimePeriod other)
	{
		return _thePeriod.overlaps(other);
	}

	/**
	 * see if this time period overlaps the one provided
	 */
	public boolean overlaps(final HiResDate start, final HiResDate end)
	{
		return _thePeriod.overlaps(start, end);
	}

	// //////////////////////////////////////
	// provide nicely named accessors so that they
	// appear together in the property editor
	// //////////////////////////////////////

	public HiResDate getTimeStart()
	{
		return getStartDTG();
	}

	public HiResDate getTime_End()
	{
		return getEndDTG();
	}

	public void setTimeStart(final HiResDate val)
	{
		setStartDTG(val);
	}

	public void setTime_End(final HiResDate val)
	{
		setEndDTG(val);
	}

	// //////////////////////////////////////
	// editing parameters
	// //////////////////////////////////////

	/**
	 * name of this pattern
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return _theLabel.getString();
	}

	/**
	 * set the name of this pattern
	 * 
	 * @param theName
	 *          the name as a String
	 */
	public void setName(final String theName)
	{
		_theLabel.setString(theName);
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
	 * set the colour of this pattern label
	 * 
	 * @param theCol
	 *          the colour
	 */
	public void setColor(final Color theCol)
	{
		super.setColor(theCol);
		_theLabel.setColor(theCol);
	}

	public void setNameColor(final Color theCol)
	{
		setColor(theCol);
	}

	/**
	 * get the colour of this pattern label
	 * 
	 * @return the colour
	 */
	public Color getColor()
	{
		return _theLabel.getColor();
	}

	public Color getNameColor()
	{
		return getColor();
	}

	/**
	 * font handler
	 * 
	 * @param font
	 *          font to use for the label
	 */
	public void setFont(final java.awt.Font font)
	{
		_theLabel.setFont(font);
	}

	/**
	 * font handler
	 * 
	 * @return the font to use for the label
	 */
	public java.awt.Font getFont()
	{
		return _theLabel.getFont();
	}

	/**
	 * whether the pattern label is visible or not
	 * 
	 * @return yes/no
	 */
	public boolean getNameVisible()
	{
		return _theLabel.getVisible();
	}

	/**
	 * whether to show the pattern name
	 * 
	 * @param val
	 *          yes/no
	 */
	public void setNameVisible(final boolean val)
	{
		_theLabel.setVisible(val);
	}

	/**
	 * the relative location of the label
	 * 
	 * @return the relative location
	 */
	public Integer getNameLocation()
	{
		return _theLabel.getRelativeLocation();
	}

	/**
	 * the relative location of the label
	 * 
	 * @param val
	 *          the relative location
	 */
	public void setNameLocation(final Integer val)
	{
		_theLabel.setRelativeLocation(val);
		// and update it relative to the current area
		updateLabelLocation();
	}

	/**
	 * extra parameter, so that jvm can produce a sensible name for this
	 * 
	 * @return the pattern name, as a string
	 */
	public String toString()
	{
		return "Pattern:" + getName() + " (" + _myBuoys.size() + " buoys)";
	}

	/**
	 * return the range from the nearest corner of the track
	 * 
	 * @param other
	 *          the other location
	 * @return the range
	 */
	public double rangeFrom(final WorldLocation other)
	{
		return _myArea.rangeFrom(other);
	}

	/**
	 * the editable details for this pattern
	 * 
	 * @return the details
	 */
	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new BuoyPatternInfo(this);

		return _myEditor;
	}

	/**
	 * whether this object has editor details
	 * 
	 * @return yes/no
	 */
	public boolean hasEditor()
	{
		return true;
	}

	// //////////////////////////////////////////////////
	// support for editing the buoys in this field
	// //////////////////////////////////////////////////

	private void setChildren(final String methodName, final Object[] args,
			final Class[] parameters)
	{
		try
		{
			final java.lang.reflect.Method setter = LabelWrapper.class.getMethod(methodName,
					parameters);

			final Enumeration iter = _myBuoys.elements();
			while (iter.hasMoreElements())
			{
				final Object nextO = iter.nextElement();
				if (nextO instanceof LabelWrapper)
				{
					final LabelWrapper lw = (LabelWrapper) nextO;
					setter.invoke(lw, args);
				}
			}
		}
		catch (java.lang.NoSuchMethodException nm)
		{
			MWC.Utilities.Errors.Trace.trace(nm, "Buoy setter unable to find method:"
					+ methodName);
		}
		catch (java.lang.reflect.InvocationTargetException te)
		{
			MWC.Utilities.Errors.Trace.trace(te, "Buoy setter unable execute method:"
					+ methodName);
		}
		catch (java.lang.IllegalAccessException ie)
		{
			MWC.Utilities.Errors.Trace.trace(ie, "Buoy setter illegally accessing method:"
					+ methodName);
		}
	}

	/**
	 * set the colour of this pattern label
	 * 
	 * @param theCol
	 *          the colour
	 */
	public void setBuoyColor(final Color theCol)
	{
		setChildren("setColor", new Object[] { theCol }, new Class[] { Color.class });
		_buoyPatternColour = theCol;
	}

	/**
	 * get the colour of this pattern label
	 * 
	 * @return the colour
	 */
	public Color getBuoyColor()
	{
		return _buoyPatternColour;
	}

	/**
	 * set the colour of this pattern label
	 * 
	 * @param theVal
	 *          the colour
	 */
	public void setBuoySymbolSize(final double theVal)
	{
		setChildren("setSymbolSize", new Object[] { new Double(theVal) },
				new Class[] { Double.class });
		_buoySymbolSize = theVal;
	}

	/**
	 * get the colour of this pattern label
	 * 
	 * @return the colour
	 */
	public double getBuoySymbolSize()
	{
		return _buoySymbolSize;
	}

	/**
	 * set the colour of this pattern label
	 * 
	 * @param theVal
	 *          the colour
	 */
	public void setBuoySymbolType(final String theVal)
	{
		setChildren("setSymbolType", new Object[] { theVal }, new Class[] { String.class });
		_buoySymbolType = theVal;
	}

	/**
	 * get the colour of this pattern label
	 * 
	 * @return the colour
	 */
	public String getBuoySymbolType()
	{
		return _buoySymbolType;
	}

	/**
	 * set the colour of this pattern label
	 * 
	 * @param theVal
	 *          the colour
	 */
	public void setBuoyLabelLocation(final Integer theVal)
	{
		setChildren("setLabelLocation", new Object[] { theVal },
				new Class[] { Integer.class });
		_buoyLabelLocation = theVal.intValue();
	}

	/**
	 * get the colour of this pattern label
	 * 
	 * @return the colour
	 */
	public Integer getBuoyLabelLocation()
	{
		return new Integer(_buoyLabelLocation);
	}

	/**
	 * set the colour of this pattern label
	 * 
	 * @param theVal
	 *          the colour
	 */
	public void setBuoyLabelVisible(final boolean theVal)
	{
		setChildren("setLabelVisible", new Object[] { new Boolean(theVal) },
				new Class[] { Boolean.class });
		_buoyLabelVisible = theVal;
	}

	/**
	 * get the colour of this pattern label
	 * 
	 * @return the colour
	 */
	public boolean getBuoyLabelVisible()
	{
		return _buoyLabelVisible;
	}

	// //////////////////////////////////////////////////
	// support for WatchableList
	// //////////////////////////////////////////////////
	public MWC.GUI.Shapes.Symbols.PlainSymbol getSnailShape()
	{
		return _snailShape;
	}

	/**
	 * get the set of fixes contained within this time period
	 * 
	 * @param start
	 *          start DTG
	 * @param end
	 *          end DTG
	 * @return series of fixes
	 */
	public Collection getItemsBetween(final HiResDate start, final HiResDate end)
	{
		final java.util.Vector<LabelWrapper> res = new Vector<LabelWrapper>(0, 1);
		final Enumeration iter = _myBuoys.elements();
		while (iter.hasMoreElements())
		{
			final LabelWrapper nextW = (LabelWrapper) iter.nextElement();
			final HiResDate startD = nextW.getStartDTG();
			// if(startD != -1)
			// {
			if ((startD.greaterThanOrEqualTo(start)) && (startD.lessThanOrEqualTo(end)))
			{
				res.addElement(nextW);
			}
			// }
		}

		return res;
	}

	public void filterListTo(final HiResDate start, final HiResDate end)
	{
		//
		final Iterator buoyWrappers = _myBuoys.iterator();
		while (buoyWrappers.hasNext())
		{
			final LabelWrapper fw = (LabelWrapper) buoyWrappers.next();
			final TimePeriod tp = new BaseTimePeriod(fw.getStartDTG(), fw.getEndDTG());
			fw.setVisible(tp.overlaps(start, end));
			// long dtg = fw.getTime();
			// if((dtg >= start) &&
			// (dtg <= end))
			// {
			// fw.setVisible(true);
			// }
			// else
			// {
			// fw.setVisible(false);
			// }
		}

	}

	public Debrief.Tools.Tote.Watchable[] getNearestTo(final HiResDate DTG)
	{
		Watchable res = null;

		// in a buoyfield, all if the buoys share a common time period.
		// see if our time-period contains this DTG
		if (_thePeriod.contains(DTG))
		{
			// we've got to create a dummy watchable which will draw a circle around
			// the whole field
			res = new BuoyPatternAdaptor(getBounds(), DTG, getColor(), getName(), this);
		}

		// special case, have we been asked for an invalid time period?
		if (DTG == TimePeriod.INVALID_DATE)
		{
			return new Watchable[] { res };
		}

		// back to normal processing
		if (res == null)
			return new Watchable[] {};
		else
			return new Watchable[] { res };
	}

	// ////////////////////////////////////////////////////
	// LAYER support methods
	// /////////////////////////////////////////////////////

	/**
	 * append this other layer to ourselves (although we don't really bother with
	 * it)
	 * 
	 * @param other
	 *          the layer to add to ourselves
	 */
	public void append(final Layer other)
	{

	}

	/**
	 * method to recalculate the area and time covered by the plottables
	 */
	private void recalculateAreaCovered()
	{
		// so, step through the array, and calculate the area
		WorldArea res = null;

		final Enumeration iter = _myBuoys.elements();
		while (iter.hasMoreElements())
		{
			final Plottable thisOne = (Plottable) iter.nextElement();

			final MWC.GenericData.WorldArea thisArea = thisOne.getBounds();

			// ////////////////////////////
			// first the area
			// ////////////////////////////
			// does this object have an area?
			if (thisArea != null)
			{
				// do we have an area already?
				if (res == null)
					res = new WorldArea(thisArea);
				else
					res.extend(thisArea);
			}

		}

		// store the result
		_myArea = res;

		// also update the location of the label
		updateLabelLocation();
	}

	/**
	 * add the indicated point to the pattern
	 * 
	 * @param point
	 *          point to add
	 */
	public void add(final MWC.GUI.Editable point)
	{
		// see if this is a buoy (symbol)
		if (point instanceof Debrief.Wrappers.LabelWrapper)
		{
			_myBuoys.addElement(point);

			// is this the kingpin?
			final LabelWrapper lw = (LabelWrapper) point;
			if (lw.getSymbolType().equals("Kingpin"))
			{
				setKingpin(lw);
			}
		}
		else
		{
			MWC.Utilities.Errors.Trace.trace("BuoyPattern not expecting to add: " + point);
		}

		// recalculate our area
		recalculateAreaCovered();
	}

	/**
	 * make a note of which is the kingpin
	 * 
	 * @param kingpin
	 */
	private void setKingpin(final LabelWrapper kingpin)
	{
		_theKingpin = kingpin;
	}

	/**
	 * retrieve the kingpin for this pattern
	 * 
	 * @return
	 */
	public final LabelWrapper getKingpin()
	{
		return _theKingpin;
	}

	/**
	 * remove the requested item from the pattern
	 * 
	 * @param point
	 *          point to remove
	 */
	public void removeElement(final Editable point)
	{
		if (_myBuoys.contains(point))
		{
			_myBuoys.remove(point);
		}
		else
		{
			MWC.Utilities.Errors.Trace.trace("BuoyPattern does not contain: " + point);
		}

		// recalculate our area
		recalculateAreaCovered();

	}

	/**
	 * get an enumeration of the points in this pattern
	 * 
	 * @return the points in this track
	 */
	public java.util.Enumeration elements()
	{
		return _myBuoys.elements();
	}

	/**
	 * get the list of buoys themselves
	 */
	public Vector getBuoys()
	{
		return _myBuoys;
	}

	/**
	 * export this pattern to REPLAY file
	 */
	public void exportShape()
	{
		// call the method in PlainWrapper
		this.exportThis();
	}

	// //////////////////////////////////////
	// simple watchable which draws a rectangle around this field
	// //////////////////////////////////////
	public static final class BuoyPatternAdaptor implements Watchable
	{
		private WorldArea _theArea = null;

		private final String _theName;

		private final Color _theColor;

		private boolean _visible = true;

		private HiResDate _theTime;

		private BuoyPatternWrapper _thePattern = null;

		public BuoyPatternAdaptor(final WorldArea theArea, final HiResDate theTime,
				final Color theColor, final String theName, final BuoyPatternWrapper thePattern)
		{
			_theArea = theArea;
			_theTime = theTime;
			_theColor = theColor;
			_theName = theName;
			_thePattern = thePattern;
		}

		/**
		 * get the pattern represented by this watchable
		 */
		public final BuoyPatternWrapper getPattern()
		{
			return _thePattern;
		}

		/**
		 * get the current location of the watchable
		 * 
		 * @return the location
		 */
		public final WorldLocation getLocation()
		{
			return _theArea.getCentre();
		}

		/**
		 * get the current course of the watchable (rads)
		 * 
		 * @return course in radians
		 */
		public final double getCourse()
		{
			return 0.0;
		}

		/**
		 * get the current speed of the watchable (kts)
		 * 
		 * @return speed in knots
		 */
		public final double getSpeed()
		{
			return 0.0;
		}

		/**
		 * get the current depth of the watchable (m)
		 * 
		 * @return depth in metres
		 */
		public final double getDepth()
		{
			return 0.0;
		}

		/**
		 * get the bounds of the object (used when we are painting it)
		 */
		public final WorldArea getBounds()
		{
			return _theArea;
		}

		/**
		 * specify if this Watchable is visible or not
		 * 
		 * @param val
		 *          whether it's visible
		 */
		public final void setVisible(final boolean val)
		{
			_visible = val;
		}

		/**
		 * determine if this Watchable is visible or not
		 * 
		 * @return boolean whether it's visible
		 */
		public final boolean getVisible()
		{
			return _visible;
		}

		/**
		 * find out the time of this watchable
		 */
		public final HiResDate getTime()
		{
			return _theTime;
		}

		/**
		 * find out the name of this watchable
		 */
		public final String getName()
		{
			return _theName;
		}

		/**
		 * find out the colour of this watchable
		 */
		public final java.awt.Color getColor()
		{
			return _theColor;
		}

	}

	// //////////////////////////////////////
	// beaninfo
	// //////////////////////////////////////

	/**
	 * class containing editable details of a pattern
	 */
	public final class BuoyPatternInfo extends Editable.EditorType
	{

		/**
		 * constructor for this editor, takes the actual pattern as a parameter
		 * 
		 * @param data
		 *          track being edited
		 */
		public BuoyPatternInfo(final BuoyPatternWrapper data)
		{
			super(data, data.getName(), "");
		}

		public final String getName()
		{
			return getData().toString();
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res = { expertProp("Name", "the pattern name"),
						expertProp("NameColor", "the colour of the label for this pattern"),
						expertProp("NameLocation", "the location of the pattern name"),
						expertProp("Font", "the font to use for the label"),
						expertProp("NameVisible", "the location of the pattern name"),
						expertProp("Visible", "whether to show this buoypattern"),
						expertProp("TimeStart", "time the pattern becomes active"),
						expertProp("Time_End", "time the pattern expires"),
						expertProp("BuoyColor", "colour of the individual buoys in this pattern"),
						expertProp("BuoySymbolSize", "size of the buoys symbols this pattern"),
						expertProp("BuoySymbolType", "symbol type of buoys in this pattern"),
						expertProp("BuoyLabelVisible", "label visibility for buoys in this pattern"),
						expertProp("BuoyLabelLocation", "label location for buoys in this pattern"),
						expertProp("LineThickness", "the thickness of to draw the lines"), };
				res[2].setPropertyEditorClass(MWC.GUI.Properties.LocationPropertyEditor.class);
				res[9]
						.setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.class);
				res[10]
						.setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.SymbolFactoryBuoyPropertyEditor.class);
				res[12].setPropertyEditorClass(MWC.GUI.Properties.LocationPropertyEditor.class);
				res[13].setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

				return res;
			}
			catch (IntrospectionException e)
			{
				// find out which property fell over
				MWC.Utilities.Errors.Trace.trace(e, "Creating editor for Arc Builder");

				// and return the parent's null set
				return super.getPropertyDescriptors();
			}
		}

		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class c = ShapeWrapper.class;
			final MethodDescriptor[] mds = { method(c, "exportThis", null, "Export Shape") };
			return mds;
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
			MWC.GUI.Editable ed = new BuoyPatternWrapper();
			editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		public final void testListManagement()
		{
			// ok, create our list of buoys
			final BuoyPatternWrapper bpw = new BuoyPatternWrapper(null);

			// check the area covered
			WorldArea wa = bpw.getBounds();
			assertTrue("null area returned for empty pattern", wa == null);

			// add first buoy
			final WorldLocation w1 = new WorldLocation(2, 1, 2);
			final LabelWrapper lw = new LabelWrapper("here", w1, Color.red, new HiResDate(100),
					new HiResDate(200));
			bpw.add(lw);

			// now recheck the area
			wa = bpw.getBounds();
			assertEquals("area of first buoy returned", wa.getTopLeft(), w1);
			assertEquals("area of first buoy returned", wa.getBottomRight(), w1);

			final WorldLocation w2 = new WorldLocation(1, 2, 1);
			final LabelWrapper lw2 = new LabelWrapper("here", w2, Color.red,
					new HiResDate(100), new HiResDate(200));
			bpw.add(lw2);

			wa = bpw.getBounds();
			assertEquals("area of first buoy returned", wa.getTopLeft(), w1);
			assertEquals("area of first buoy returned", wa.getBottomRight(), w2);

			// now mess with the times
			assertEquals("first buoy visible", lw.getVisible(), true);
			assertEquals("second buoy visible", lw2.getVisible(), true);

			bpw.filterListTo(new HiResDate(40), new HiResDate(50));
			assertEquals("first buoy not visible", lw.getVisible(), false);
			assertEquals("second buoy not visible", lw2.getVisible(), false);

			bpw.filterListTo(new HiResDate(50), new HiResDate(150));
			assertEquals("first buoy visible", lw.getVisible(), true);
			assertEquals("second buoy visible", lw2.getVisible(), true);

			bpw.filterListTo(new HiResDate(110), new HiResDate(150));
			assertEquals("first buoy visible", lw.getVisible(), true);
			assertEquals("second buoy visible", lw2.getVisible(), true);

			bpw.filterListTo(new HiResDate(90), new HiResDate(200));
			assertEquals("first buoy visible", lw.getVisible(), true);
			assertEquals("second buoy visible", lw2.getVisible(), true);

			// stick in the third buoy
			final WorldLocation w3 = new WorldLocation(2, 2, 1);
			final LabelWrapper lw3 = new LabelWrapper("here", w3, Color.red,
					new HiResDate(180), new HiResDate(260));
			bpw.add(lw3);

			bpw.filterListTo(new HiResDate(90), new HiResDate(200));
			assertEquals("third buoy visible", lw3.getVisible(), true);

			bpw.filterListTo(new HiResDate(90), new HiResDate(100));
			assertEquals("third buoy not visible", lw3.getVisible(), false);

			bpw.filterListTo(new HiResDate(290), new HiResDate(300));
			assertEquals("third buoy not visible", lw3.getVisible(), false);

			bpw.filterListTo(new HiResDate(190), new HiResDate(200));
			assertEquals("third buoy visible", lw3.getVisible(), true);

		}

		/**
		 * put test for BaseTimePeriod in here, since we can't put testing code
		 * (main method) into TimePeriod interface
		 */
		public final void testPeriod()
		{
			final BaseTimePeriod btp = new BaseTimePeriod(new HiResDate(500),
					new HiResDate(600));
			assertEquals("contains valid time", btp.contains(new HiResDate(550)), true);
			assertEquals("doesn't contain invalid time", btp.contains(new HiResDate(450)),
					false);
			assertEquals("doesn't contain invalid time", btp.contains(new HiResDate(650)),
					false);
			btp.extend(new HiResDate(650));
			assertEquals("contains valid time", btp.contains(new HiResDate(550)), true);
			assertEquals("contains valid time", btp.contains(new HiResDate(650)), true);
			assertEquals("doesn't contain invalid time", btp.contains(new HiResDate(450)),
					false);
			assertEquals("doesn't contain invalid time", btp.contains(new HiResDate(750)),
					false);
			assertEquals("overlaps valid period", btp.overlaps(new HiResDate(450),
					new HiResDate(550)), true);
			assertEquals("overlaps valid period", btp.overlaps(new HiResDate(550),
					new HiResDate(750)), true);
			assertEquals("overlaps valid period", btp.overlaps(new HiResDate(550),
					new HiResDate(580)), true);
			assertEquals("overlaps valid period", btp.overlaps(new HiResDate(450),
					new HiResDate(780)), true);
			assertEquals("overlaps valid period", btp.overlaps(new HiResDate(450),
					new HiResDate(480)), false);
			assertEquals("overlaps valid period", btp.overlaps(new HiResDate(650),
					new HiResDate(780)), false);
		}

	}

	public static void main(final String[] args)
	{
		final testMe tm = new testMe("scrap");
		tm.testPeriod();
		tm.testListManagement();
		tm.testMyParams();
	}
}
