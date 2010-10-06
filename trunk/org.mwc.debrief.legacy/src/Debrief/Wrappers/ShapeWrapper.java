package Debrief.Wrappers;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ShapeWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.13 $
// $Log: ShapeWrapper.java,v $
// Revision 1.13  2007/03/12 11:40:25  ian.mayo
// Change default font size to 9px
//
// Revision 1.12  2007/01/05 15:13:16  ian.mayo
// Provide support for line-styles for shapes
//
// Revision 1.11  2006/10/03 08:23:45  Ian.Mayo
// Switch to Java 5. Use better compareTo methods
//
// Revision 1.10  2006/05/02 14:07:19  Ian.Mayo
// Draggable components aswell as features
//
// Revision 1.9  2006/04/21 08:18:31  Ian.Mayo
// Implement drag support
//
// Revision 1.8  2006/03/22 10:56:47  Ian.Mayo
// Reflect property name changes
//
// Revision 1.7  2005/12/13 09:05:00  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.6  2005/05/25 08:39:03  Ian.Mayo
// Minor tidying from Eclipse
//
// Revision 1.5  2005/05/19 14:48:13  Ian.Mayo
// Add more categorised properties
//
// Revision 1.4  2004/11/25 11:04:39  Ian.Mayo
// More test fixing after hi-res switch, largely related to me removing some unused accessors which were property getters
//
// Revision 1.3  2004/11/25 10:24:49  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.2  2003/10/27 12:58:58  Ian.Mayo
// Update the color of the label as well as the shape
//
// Revision 1.1.1.2  2003/07/21 14:49:26  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.21  2003-07-04 10:59:23+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.20  2003-03-27 11:22:54+00  ian_mayo
// reflect new strategy where we return all data when asked to filter by invalid time
//
// Revision 1.19  2003-03-25 15:55:44+00  ian_mayo
// working on Chuck's problems with lots of annotations on 3-d plot.
//
// Revision 1.18  2003-03-20 15:16:32+00  ian_mayo
// fix problem managing time periods
//
// Revision 1.17  2003-03-19 15:36:49+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.16  2003-03-10 14:12:23+00  ian_mayo
// Tidy unused imports
//
// Revision 1.15  2003-03-03 14:04:25+00  ian_mayo
// When label colour updated, don't pass the colour back up to the parent
//
// Revision 1.14  2003-02-21 11:13:45+00  ian_mayo
// Set the colour in the parent
//
// Revision 1.13  2003-02-05 15:56:28+00  ian_mayo
// Correctly return the depth of the object
//
// Revision 1.12  2003-02-03 14:11:17+00  ian_mayo
// Improve support when DTG missing
//
// Revision 1.11  2003-01-30 16:07:44+00  ian_mayo
// Minor tidying, unused property
//
// Revision 1.10  2003-01-21 16:30:08+00  ian_mayo
// Some refactoring, move setColor management back into this component from the shape itself (so we can fire the property change)
//
// Revision 1.9  2003-01-17 15:08:38+00  ian_mayo
// Correct unit test to reflect new functionality
//
// Revision 1.8  2003-01-15 15:48:24+00  ian_mayo
// With getNearestTo, return annotation when no DTG supplied
//
// Revision 1.7  2002-10-01 15:41:38+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.6  2002-07-23 08:48:39+01  ian_mayo
// Return the correct type of object for getWatchable
//
// Revision 1.5  2002-07-10 14:59:00+01  ian_mayo
// correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.4  2002-07-09 15:28:29+01  ian_mayo
// Return zero-length list instead of null
//
// Revision 1.3  2002-07-08 09:41:17+01  ian_mayo
// Don't return null, return zero length array
//
// Revision 1.2  2002-05-28 09:25:14+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:38+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:27+01  ian_mayo
// Initial revision
//
// Revision 1.7  2002-03-19 11:08:48+00  administrator
// Name the editor according to the shape, not the rectangle.  Specify that additional items should be shown alongside basic items
//
// Revision 1.6  2002-03-13 19:40:47+00  administrator
// Add visible and show label flags
//
// Revision 1.5  2002-03-13 08:58:42+00  administrator
// Correctly implement start and finish times
//
// Revision 1.4  2002-02-25 13:21:41+00  administrator
// Don't fix anchor point, keep it flexible
//
// Revision 1.3  2001-10-01 12:49:52+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.2  2001-08-29 19:18:02+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.1  2001-08-06 16:58:16+01  administrator
// Fire property change event to indicate that object has been filtered
//
// Revision 1.0  2001-07-17 08:41:09+01  administrator
// Initial revision
//
// Revision 1.6  2001-06-01 13:49:38+01  novatech
// handled instances where Data isn't available (return -1)
//
// Revision 1.5  2001-01-22 12:30:02+00  novatech
// added JUnit testing code
//
// Revision 1.4  2001-01-17 09:47:31+00  novatech
// implement time support properly
//
// Revision 1.3  2001-01-15 11:19:28+00  novatech
// store the symbol for this shape
//
// Revision 1.2  2001-01-09 10:29:45+00  novatech
// add extra parameters to allow WatchableLists to be used instead of TrackWrappers
//
// Revision 1.1  2001-01-03 13:40:23+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:26  ianmayo
// initial import of files
//
// Revision 1.23  2000-11-17 09:32:58+00  ian_mayo
// tidied up processing of moving Label when shape moves
//
// Revision 1.22  2000-10-16 11:51:26+01  ian_mayo
// tidied up retrieval of time threshold (now allowing retrieval from properties)
//
// Revision 1.21  2000-09-22 11:45:31+01  ian_mayo
// handle time-related plotting correctly
//
// Revision 1.20  2000-09-21 09:05:24+01  ian_mayo
// make Editable.EditorType a transient parameter, to save it being written to file
//
// Revision 1.19  2000-08-18 13:34:02+01  ian_mayo
// Editable.EditorType
//
// Revision 1.18  2000-08-14 15:50:18+01  ian_mayo
// GUI name changes
//
// Revision 1.17  2000-08-11 08:41:04+01  ian_mayo
// tidy beaninfo
//
// Revision 1.16  2000-08-09 16:04:00+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.15  2000-05-23 13:40:24+01  ian_mayo
// switch to Arial
//
// Revision 1.14  2000-04-03 10:47:19+01  ian_mayo
// add filtering functionality
//
// Revision 1.13  2000-02-22 13:48:31+00  ian_mayo
// exportShape name changed to exportThis
//
// Revision 1.12  2000-02-14 16:48:49+00  ian_mayo
// insert code to set name of shape component into constructor
//
// Revision 1.11  2000-01-20 10:09:57+00  ian_mayo
// experimenting with copy functionality
//
// Revision 1.10  1999-11-26 15:50:16+00  ian_mayo
// adding toString methods
//
// Revision 1.9  1999-11-15 15:42:38+00  ian_mayo
// checking whether shape & label is in the current data area
//
// Revision 1.8  1999-11-12 14:35:39+00  ian_mayo
// part way through getting them to export themselves
//
// Revision 1.7  1999-11-11 18:23:22+00  ian_mayo
// added DTG
//
// Revision 1.6  1999-11-11 10:34:08+00  ian_mayo
// changed signature of ShapeWrapper constructor
//
// Revision 1.5  1999-10-15 12:36:51+01  ian_mayo
// improved relative label locating
//
// Revision 1.4  1999-10-14 16:32:26+01  ian_mayo
// update label location on creation
//
// Revision 1.3  1999-10-14 12:03:12+01  ian_mayo
// made 'Label' reflect changes in shape location
//
// Revision 1.2  1999-10-12 16:27:18+01  ian_mayo
// tidied up implementation of 'get additional bean info'
//
// Revision 1.1  1999-10-12 15:34:03+01  ian_mayo
// Initial revision
//

import java.awt.Font;
import java.awt.Point;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Vector;

import Debrief.GUI.Tote.Painters.SnailPainter.DoNotHighlightMe;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.HasDraggableComponents;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class ShapeWrapper extends MWC.GUI.PlainWrapper implements java.beans.PropertyChangeListener, MWC.GenericData.WatchableList,
		MWC.GenericData.Watchable, DraggableItem, HasDraggableComponents,
		DoNotHighlightMe
{
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * property name to indicate that the symbol visibility has been changed
	 */
	public static final String LABEL_VIS_CHANGED = "LABEL_VIS_CHANGE";

	/**
	 * keep track of versions
	 */
	static final long serialVersionUID = 1;

	/**
	 * the label
	 */
	private final MWC.GUI.Shapes.TextLabel _theLabel;

	/**
	 * the symbol for this label
	 */
	final MWC.GUI.Shapes.PlainShape _theShape;

	/**
	 * the start dtg for this label (although) this will frequently be null, for
	 * non time-related entities. Where no end DTG is provided, this will be used
	 * as a centre point
	 */
	private HiResDate _theStartDTG = null;

	/**
	 * the end dtg for this label (although) this will frequently be null, for non
	 * time-related entities. Where no end DTG is provided, this will be used as a
	 * centre point
	 */
	private HiResDate _theEndDTG = null;

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor = null;

	/**
	 * the style of line to use for this feature
	 * 
	 */
	private int _lineStyle;

	/**
	 * the width to draw this line
	 */
	private int _lineWidth;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public ShapeWrapper(final String label,
			final MWC.GUI.Shapes.PlainShape theShape, final java.awt.Color theColor,
			final HiResDate theDate)
	{
		_theLabel = new MWC.GUI.Shapes.TextLabel(theShape, label);
		// store the shape
		_theShape = theShape;

		// store the name of the shape
		_theShape.setName(getName());

		_theLabel.setFont(new Font("Sans Serif", Font.PLAIN, 9));
		_theLabel.setColor(theColor);

		// override the shape, just to be sure...
		_theShape.setColor(theColor);

		// tell the parent object what colour we are
		super.setColor(theColor);

		// store the date (which is initially used as a centre time)
		setTime_Start(theDate);
		setTimeEnd(null);

		// also update the location of the text anchor
		updateLabelLocation();

		// add ourselves as the listener to the shape and
		// the label
		_theShape.addPropertyListener(this);
		_theLabel.addPropertyListener(this);
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public final void paint(final CanvasType dest)
	{
		// check if we are visible
		if (getVisible())
		{
			// sort out the line style
			dest.setLineStyle(_lineStyle);

			// store the current line width
			float lineWid = dest.getLineWidth();

			// and the line width
			dest.setLineWidth(_lineWidth);

			// first paint the symbol
			_theShape.paint(dest);

			// and restore the line style
			dest.setLineStyle(CanvasType.SOLID);

			// and restore the line width
			dest.setLineWidth(lineWid);

			// now paint the text
			_theLabel.paint(dest);
		}
	}

	//
	// /** get the start time for this shape
	// */
	// public final HiResDate xxxgetTimeStart()
	// {
	// // HI-RES NOT DONE - ditch this
	// return _theStartDTG;
	// }
	//
	// /** get the end time for this shape
	// */
	// public HiResDate getTime_End()
	// {
	// return _theEndDTG;
	// }
	//

	// ///////////////////////////////////
	// manage the time period
	// ///////////////////////////////////
	public final TimePeriod getTimePeriod()
	{
		TimePeriod res = null;

		if (getStartDTG() != null)
		{
			res = new TimePeriod.BaseTimePeriod(getStartDTG(), getEndDTG());
		}
		return res;
	}

	public final void setTimePeriod(final TimePeriod val)
	{
		this.setTime_Start(val.getStartDTG());
		this.setTimeEnd(val.getEndDTG());
	}

	// ////////////////////////////////////////////////
	// PROPERTY ACCESSORS - we're providing DTG accessor methods
	// which start with the same phrase so that in an alphabetically
	// sorted property editor they appear together
	// ////////////////////////////////////////////////
	/**
	 * get the start time for this shape
	 */
	public final HiResDate getTime_Start()
	{
		return getStartDTG();
	}

	/**
	 * get the end time for this shape
	 */
	public final HiResDate getTimeEnd()
	{
		return getEndDTG();
	}

	/**
	 * set the start time for this shape
	 */
	public final void setTime_Start(HiResDate val)
	{
		_theStartDTG = val;
	}

	/**
	 * set the start time for this shape
	 */
	public final void setTimeEnd(HiResDate val)
	{
		_theEndDTG = val;
	}

	/**
	 * get the shape we are containing
	 */
	public final MWC.GUI.Shapes.PlainShape getShape()
	{
		return _theShape;
	}

	public final WorldArea getBounds()
	{
		// get the bounds from the data object (or its location object)
		final WorldArea res = new WorldArea(_theLabel.getBounds());
		res.extend(_theShape.getBounds());
		return res;
	}

	public final String toString()
	{		
		return _theShape.getName() + ":" + _theLabel.getString();
	}

	public final String getName()
	{
		return _theLabel.getString();
	}

	/**
	 * does this item have an editor?
	 */
	public final boolean hasEditor()
	{
		return true;
	}

	public final Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new ShapeInfo(this, this.getName());

		return _myEditor;
	}

	public final void setFont(final Font theFont)
	{
		_theLabel.setFont(theFont);
	}

	public final Font getFont()
	{
		return _theLabel.getFont();
	}

	public final void setLabelColor(final java.awt.Color theCol)
	{
		_theLabel.setColor(theCol);

		// also set the colour in the parent
		// super.setColor(theCol);
	}

	public final java.awt.Color getLabelColor()
	{
		return _theLabel.getColor();
	}

	public final void setLineStyle(final int style)
	{
		_lineStyle = style;
	}

	public final int getLineStyle()
	{
		return _lineStyle;
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
	public void setLineThickness(int val)
	{
		_lineWidth = val;
	}

	/**
	 * whether to show the label for this shape
	 */
	public final boolean getLabelVisible()
	{
		return _theLabel.getVisible();
	}

	/**
	 * whether to show the label for this shape
	 */
	public final void setLabelVisible(final boolean val)
	{
		_theLabel.setVisible(val);

		// ok, inform any listeners
		getSupport().firePropertyChange(ShapeWrapper.LABEL_VIS_CHANGED, null,
				new Boolean(val));

	}

	public final String getLabel()
	{
		return _theLabel.getString();
	}

	public final void setLabel(final String val)
	{
		_theLabel.setString(val);
	}

	public final void setLabelLocation(final Integer val)
	{
		_theLabel.setRelativeLocation(val);

		// and update the label relative to the shape if necessary
		updateLabelLocation();
	}

	public final Integer getLabelLocation()
	{
		return _theLabel.getRelativeLocation();
	}

	/**
	 * find the range from this shape to some point
	 */
	public final double rangeFrom(final WorldLocation other)
	{
		return Math.min(_theLabel.rangeFrom(other), _theShape.rangeFrom(other));
	}

	// ////////////////////////////////////////////////////
	// property change support
	// ///////////////////////////////////////////////////
	public final void propertyChange(final PropertyChangeEvent p1)
	{
		if (p1.getSource() == _theShape)
		{
			if (p1.getPropertyName().equals("Location"))
			{
				updateLabelLocation();
			}
		}
	}

	private void updateLabelLocation()
	{
		final WorldLocation newLoc = _theShape.getAnchor(_theLabel
				.getRelativeLocation().intValue());
		_theLabel.setLocation(newLoc);
	}

	// ////////////////////////////////////////////////////
	// watchableList support
	// ///////////////////////////////////////////////////

	public final MWC.GUI.Shapes.Symbols.PlainSymbol getSnailShape()
	{
		return null;
	}

	public final HiResDate getStartDTG()
	{
		// return value, or -1 to indicate not time related
		return _theStartDTG;
	}

	public final HiResDate getEndDTG()
	{
		// return value, or -1 to indicate not time related
		return _theEndDTG;
	}

	/**
	 * get the threshold for which points should be visible
	 * 
	 * @return time either side in milliseconds
	 */
	protected long getThreshold()
	{
		long res = MWC.GenericData.WatchableList.TIME_THRESHOLD;
		final String appThreshold = Debrief.GUI.Frames.Application
				.getThisProperty("STEP_THRESHOLD");

		if (appThreshold != null)
		{
			if (appThreshold.length() > 0)
			{
				try
				{
					// get actual value (in seconds)
					res = Long.parseLong(appThreshold);
					// convert to micros
					res *= 1000000;
				} catch (Exception e)
				{
					MWC.Utilities.Errors.Trace.trace(e,
							"Retrieving step threshold from properties");
				}
			}

		}

		return res;
	}

	public final MWC.GenericData.Watchable[] getNearestTo(final HiResDate DTG)
	{

		MWC.GenericData.Watchable[] res = new MWC.GenericData.Watchable[]
		{};
		

		// special case, have we been asked for an invalid time period?
		if (DTG == TimePeriod.INVALID_DATE)
		{
			// yes, just return ourselves
			return new Watchable[]
			{ this };
		} else
		{
			// do we know about time?
			if (this.getStartDTG() != null)
			{
				// do we have a finish time
				if (getEndDTG() != null)
				{
					if ((getStartDTG().lessThan(DTG)) && (getEndDTG().greaterThan(DTG)))
					{
						// yes, it's within our time period
						res = new MWC.GenericData.Watchable[]
						{ this };
					} else
					{
						// no, it's outside our time period
						// - just return our default res
					}
				} else
				{
					// no end value, see if we are within time threshold of
					// supplied time

					// find how far we are from the DTG
					final long diff = Math.abs(DTG.getMicros()
							- this.getStartDTG().getMicros());

					// is this within our threshold?
					if (diff <= getThreshold())
						res = new MWC.GenericData.Watchable[]
						{ this };
					else
						res = new MWC.GenericData.Watchable[]
						{};
				}
			} else
			{
				// so, we don't have a start time.
				// are we also missing the end time?
				if (_theEndDTG == null)
				{
					// yup, say we were nearest
					res = new MWC.GenericData.Watchable[]
					{ this };
				}
			}// this whole object is a watchable

		}

		return res;
	}

	/**
	 * method to fulfil requirements of WatchableList
	 */
	public final java.awt.Color getColor()
	{
		return super.getColor();
	}

	@FireReformatted
	public final void setColor(final java.awt.Color theCol)
	{
		super.setColor(theCol);

		// and set the colour of the shape
		_theShape.setColor(theCol);

		// don't forget the color of the label
		_theLabel.setColor(theCol);
	}

	/**
	 * method to fulfil requirements of Watchable
	 */
	public final HiResDate getTime()
	{
		return getStartDTG();
	}

	/**
	 * filter the list to the specified time period
	 * 
	 * @param start
	 *          the start dtg of the period
	 * @param end
	 *          the end dtg of the period
	 */
	public final void filterListTo(final HiResDate start, final HiResDate end)
	{
		// do we have a DTG?
		if (getStartDTG() == null)
		{
			// don't bother, we don't have a DTG
			return;
		}

		// see if we are visible between the period
		final Collection<Editable> list = getItemsBetween(start, end);

		this.setVisible(false);

		if (list != null)
		{
			if (list.size() > 0)
				this.setVisible(true);
		}

		// if we have a property support class, fire the filtered event
		if (getSupport() != null)
			getSupport().firePropertyChange(
					MWC.GenericData.WatchableList.FILTERED_PROPERTY, null, null);

	}

	public final java.util.Collection<Editable> getItemsBetween(final HiResDate start,
			final HiResDate end)
	{
		java.util.Vector<Editable> res = null;

		final HiResDate myStart = getStartDTG();
		final HiResDate myEnd = getEndDTG();

		// do we have any time data at all?
		if (myStart == null)
		{
			// no, so just return ourselves anyway
			res = new Vector<Editable>(0, 1);
			res.add(this);
			return res;
		}

		boolean done = false; // whether we have been able to process the
		// times

		// do we have an end time?
		if (myEnd != null)
		{
			// see if our time period overlaps
			if ((myStart.lessThan(end)) && (myEnd.greaterThan(start)))
			{
				res = new Vector<Editable>(0, 1);
				res.addElement(this);
			}

			done = true;
		}

		// ok, handled instance where we have start and end times. if we just
		// have a start time, see if it is inside the valid period
		if (!done)
		{

			// do we have a start time even?
			if (myStart == null)
			{
				// no start time, just return ourselves anyway.
				res = new Vector<Editable>(0, 1);
				res.addElement(this);
			} else
			{
				// use the start time as a centre time
				if ((myStart.greaterThanOrEqualTo(start))
						&& (myStart.lessThanOrEqualTo(end)))
				{
					res = new Vector<Editable>(0, 1);
					res.addElement(this);
				} else
				{
					// no, it's outside the period
				}
			}
		}

		return res;
	}

	// ////////////////////////////////////////////////////
	// watchable support
	// ///////////////////////////////////////////////////

	public final WorldLocation getLocation()
	{
		return this.getBounds().getCentre();
	}

	public final double getCourse()
	{
		// null implementation, not valid
		return 0;
	}

	public final double getSpeed()
	{
		// null implementation, not valid
		return 0;
	}

	/**
	 * get the depth of the object. In this case, return the depth of the centre
	 * of the area covered by the shape
	 * 
	 * @return the depth
	 */
	public final double getDepth()
	{
		// return the centre of the shape
		return _theShape.getBounds().getCentre().getDepth();
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public final class ShapeInfo extends Editable.EditorType
	{

		public ShapeInfo(final ShapeWrapper data, final String theName)
		{
			super(data, theName, data._theShape.getType() + ":");
		}

		/**
		 * whether the normal editable properties should be combined with the
		 * additional editable properties into a single list. This is typically used
		 * for a composite object which has two lists of editable properties but
		 * which is seen by the user as a single object To be overwritten to change
		 * it
		 */
		public final boolean combinePropertyLists()
		{
			return true;
		}

		public final String getName()
		{
			return getLabel();
		}

		public final BeanInfo[] getAdditionalBeanInfo()
		{
			// get our shape back
			final ShapeWrapper sp = (ShapeWrapper) super.getData();
			final MWC.GUI.Shapes.PlainShape ps = sp._theShape;
			if (sp instanceof MWC.GUI.Editable)
			{
				final MWC.GUI.Editable et = (MWC.GUI.Editable) ps;
				if (et.hasEditor() == true)
				{
					final BeanInfo[] res =
					{ et.getInfo() };
					return res;
				}
			}

			return null;
		}

		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class<?> c = ShapeWrapper.class;
			final MethodDescriptor[] mds =
			{ method(c, "exportThis", null, "Export Shape") };
			return mds;
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] myRes =
				{
						prop("LabelColor", "the text color", FORMAT),
						prop("Label", "the text showing", FORMAT),
						prop("Font", "the label font", FORMAT),
						prop("LabelLocation", "the relative location of the label", FORMAT),
						prop("Visible", "whether this shape is visible", VISIBILITY),
						prop("LabelVisible", "whether the label is visible", VISIBILITY),
						prop("Time_Start", "the start date time group", TEMPORAL),
						longProp("LineStyle",
								"the dot-dash style to use for plotting this shape",
								LineStylePropertyEditor.class, FORMAT),
						longProp("LineThickness",
								"the line-thickness to use for this shape",
								MWC.GUI.Properties.LineWidthPropertyEditor.class),
						prop("Color", "the color of the shape itself", FORMAT),
						prop(
								"TimeEnd",
								"the end date time group \n\r(or leave blank for to use Start as Centre time)",
								TEMPORAL), };
				myRes[3]
						.setPropertyEditorClass(MWC.GUI.Properties.LocationPropertyEditor.class);

				return myRes;

			} catch (IntrospectionException e)
			{
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
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
			final MWC.GUI.Shapes.PlainShape ps = new MWC.GUI.Shapes.CircleShape(
					new WorldLocation(2d, 2d, 2d), 12);
			MWC.GUI.Editable ed = new ShapeWrapper("", ps, java.awt.Color.red,
					new HiResDate(0));
			editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		public final void testTimes()
		{

			final WorldLocation scrapLoc = new WorldLocation(1, 1, 1);
			final WorldLocation scrapLoc2 = new WorldLocation(1, 3, 1);

			final WatchableList.TestWatchables tw = new WatchableList.TestWatchables()
			{
				public WatchableList getBothDates(final HiResDate startDate,
						final HiResDate endDate)
				{
					final ShapeWrapper sw = new ShapeWrapper("blank",
							new MWC.GUI.Shapes.LineShape(scrapLoc, scrapLoc2),
							java.awt.Color.red, null)
					{
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						protected long getThreshold()
						{
							return 5000 * 1000;
						}
					};
					sw.setTime_Start(new HiResDate(startDate));
					sw.setTimeEnd(new HiResDate(endDate));
					return sw;
				}

				public WatchableList getNullDates()
				{
					final ShapeWrapper sw = new ShapeWrapper("blank",
							new MWC.GUI.Shapes.LineShape(scrapLoc, scrapLoc2),
							java.awt.Color.red, null)
					{
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						protected long getThreshold()
						{
							return 5000 * 1000;
						}
					};
					sw.setTime_Start(null);
					sw.setTimeEnd(null);
					return sw;
				}

				public WatchableList getStartDateOnly(final HiResDate startDate)
				{
					final ShapeWrapper sw = new ShapeWrapper("blank",
							new MWC.GUI.Shapes.LineShape(scrapLoc, scrapLoc2),
							java.awt.Color.red, null)
					{
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

						protected long getThreshold()
						{
							return 5000 * 1000;
						}
					};
					sw.setTime_Start(new HiResDate(startDate));
					sw.setTimeEnd(null);
					return sw;
				}
			};

			tw.doTest(this);
		}
	}

	public static void main(final String[] args)
	{
		final testMe tm = new testMe("scrap");
		tm.testTimes();
	}

	public void shift(WorldVector vector)
	{
		// ok - apply the offset
		DraggableItem dragee = (DraggableItem) _theShape;
		dragee.shift(vector);
	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			LocationConstruct currentNearest, Layer parentLayer, Layers theData)
	{
		_theShape.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest,
				parentLayer, theData);

	}

	public void shift(WorldLocation feature, WorldVector vector)
	{
		// ok, move the indicated point
		WorldLocation theLoc = (WorldLocation) feature;
		theLoc.addToMe(vector);
	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			ComponentConstruct currentNearest, Layer parentLayer)
	{
		if (_theShape instanceof HasDraggableComponents)
		{
			HasDraggableComponents dragger = (HasDraggableComponents) _theShape;
			dragger.findNearestHotSpotIn(cursorPos, cursorLoc, currentNearest,
					parentLayer);
		}
	}
}
