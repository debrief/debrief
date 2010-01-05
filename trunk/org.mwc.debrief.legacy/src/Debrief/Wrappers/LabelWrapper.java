package Debrief.Wrappers;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: LabelWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.5 $
// $Log: LabelWrapper.java,v $
// Revision 1.5  2007/03/12 11:40:25  ian.mayo
// Change default font size to 9px
//
// Revision 1.4  2006/04/21 08:18:30  Ian.Mayo
// Implement drag support
//
// Revision 1.3  2005/12/13 09:04:58  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:46  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:22  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.20  2003-07-04 10:59:28+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.19  2003-06-03 16:25:54+01  ian_mayo
// Lots of testing (main method - using Swing)
//
// Revision 1.18  2003-03-27 16:51:10+00  ian_mayo
// switch 3d graph to reflect 2d object hierarchy (including layers)
//
// Revision 1.17  2003-03-27 11:22:55+00  ian_mayo
// reflect new strategy where we return all data when asked to filter by invalid time
//
// Revision 1.16  2003-03-25 15:55:45+00  ian_mayo
// working on Chuck's problems with lots of annotations on 3-d plot.
//
// Revision 1.15  2003-03-19 15:36:55+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.14  2003-02-07 09:02:37+00  ian_mayo
// remove unnecessary toda comments
//
// Revision 1.13  2003-02-05 15:56:29+00  ian_mayo
// Correctly return the depth of the object
//
// Revision 1.12  2003-02-03 14:11:32+00  ian_mayo
// Improve support when DTG missing
//
// Revision 1.11  2003-01-30 16:08:06+00  ian_mayo
// add Accessor for the symbol
//
// Revision 1.10  2003-01-21 16:29:03+00  ian_mayo
// Some refactoring (out to Label3d)
//
// Revision 1.9  2003-01-20 10:47:00+00  ian_mayo
// Add property to indicate that symbol visibility has changed, and fire event to indicate this
//
// Revision 1.8  2003-01-15 15:29:14+00  ian_mayo
// Return as NearestTo if no DTG present
//
// Revision 1.7  2002-10-01 15:41:44+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.6  2002-09-24 10:55:25+01  ian_mayo
// Add Visible property
//
// Revision 1.5  2002-07-10 14:58:59+01  ian_mayo
// correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.4  2002-07-08 09:41:12+01  ian_mayo
// Don't return null, return zero length array
//
// Revision 1.3  2002-05-28 09:25:13+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:40+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-05-08 14:39:54+01  ian_mayo
// Implement property change support (largely for 3d listeners)
//
// Revision 1.1  2002-04-23 12:28:22+01  ian_mayo
// Initial revision
//
// Revision 1.3  2001-10-01 12:49:51+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.2  2001-08-29 19:18:29+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.1  2001-08-06 16:58:29+01  administrator
// Fire property change event to indicate that object has been filtered
//
// Revision 1.0  2001-07-17 08:41:08+01  administrator
// Initial revision
//
// Revision 1.12  2001-05-09 06:19:33+01  novatech
// Improvements to the way getItemsBetween returns data.  If the Label is in a valid time period, instead of just returning a this pointer, it now returns a list of one-minute spaced data points.  Because of this, we can now use a LabelWrapper in the time-plots - since it we can now measure range against a series of Labels, not just the single one
//
// Revision 1.11  2001-01-24 11:36:58+00  novatech
// setString has changed to setLabel in label
//
// Revision 1.10  2001-01-22 12:31:46+00  novatech
// change name of StartTime to TimeStart, Time_End
//
// Revision 1.9  2001-01-18 13:19:10+00  novatech
// switch editors from Boolean to boolean
//
// Revision 1.8  2001-01-17 13:24:35+00  novatech
// reflect name change from Scale to Size
//
// Revision 1.7  2001-01-17 09:46:57+00  novatech
// provide edit support if symbol has additional editable properties
//
// Revision 1.6  2001-01-15 11:22:03+00  novatech
// return the shape to use for this track
//
// Revision 1.5  2001-01-11 15:35:31+00  novatech
// more fixes
//
// Revision 1.4  2001-01-11 11:53:00+00  novatech
// Before we switch from long dates to java.util.date dates.
//
// Revision 1.3  2001-01-09 10:29:54+00  novatech
// add extra parameters to allow WatchableLists to be used instead of TrackWrappers
//
// Revision 1.2  2001-01-04 14:03:29+00  novatech
// allow selection of whether to show label, plus tidy up some date formatting
//
// Revision 1.1  2001-01-03 13:40:22+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:20  ianmayo
// initial import of files
//
// Revision 1.15  2000-11-22 10:50:57+00  ian_mayo
// allow editing of location of label
//
// Revision 1.14  2000-10-16 11:51:36+01  ian_mayo
// tidied up retrieval of time threshold (now allowing retrieval from properties)
//
// Revision 1.13  2000-10-09 13:37:30+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.12  2000-09-22 11:46:07+01  ian_mayo
// Improve time-related plotting & allow user setting of whether to plot symbol or not
//
// Revision 1.11  2000-09-21 09:05:23+01  ian_mayo
// make Editable.EditorType a transient parameter, to save it being written to file
//
// Revision 1.10  2000-08-18 13:34:03+01  ian_mayo
// Editable.EditorType
//
// Revision 1.9  2000-08-11 08:41:03+01  ian_mayo
// tidy beaninfo
//
// Revision 1.8  2000-08-09 16:03:59+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.7  2000-05-23 13:40:34+01  ian_mayo
// switch to Arial
//
// Revision 1.6  2000-04-19 11:25:01+01  ian_mayo
// make it time aware
//
// Revision 1.5  2000-02-22 13:48:32+00  ian_mayo
// exportShape name changed to exportThis
//
// Revision 1.4  2000-02-02 14:28:58+00  ian_mayo
// minor tidying up
//
// Revision 1.3  1999-11-26 15:50:15+00  ian_mayo
// adding toString methods
//
// Revision 1.2  1999-11-12 14:35:40+00  ian_mayo
// part way through getting them to export themselves
//
// Revision 1.1  1999-10-12 15:34:02+01  ian_mayo
// Initial revision
//

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Vector;

import javax.swing.JFrame;

import Debrief.GUI.Tote.Painters.SnailPainter.DoNotHighlightMe;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Chart.Swing.SwingChart;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class LabelWrapper extends MWC.GUI.PlainWrapper implements MWC.GenericData.WatchableList, MWC.GenericData.Watchable,
		DraggableItem, DoNotHighlightMe
{
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * property name to indicate that the symbol visibility has been changed
	 */
	public static final String SYMBOL_VIS_CHANGED = "SYMBOL_VIS_CHANGE";

	/**
	 * property name to indicate that the symbol visibility has been changed
	 */
	public static final String LABEL_VIS_CHANGED = "LABEL_VIS_CHANGE";

	// keep track of versions
	static final long serialVersionUID = 1;

	/**
	 * the label
	 */
	private final MWC.GUI.Shapes.TextLabel _theLabel;

	/**
	 * the symbol for this label
	 */
	MWC.GUI.Shapes.Symbols.PlainSymbol _theShape;

	/**
	 * the origin for this text label
	 */
	private WorldLocation _theLocation;

	/**
	 * the time period this object covers
	 */
	private TimePeriod _theTimePeriod;

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor = null;

	/**
	 * whether we plot in the symbol
	 */
	private boolean _plotSymbol = true;

	/**
	 * an editable parent class, if applicable
	 */
	Editable _myParent = null;

	/**
	 * whether to show the label
	 */
	private boolean _showLabel = true;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public LabelWrapper(final String label, final WorldLocation location,
			final java.awt.Color theColor)
	{
		this(label, location, theColor, null, null);
	}

	/**
	 * constructor with date info
	 * 
	 * @param label
	 *          the text to display
	 * @param location
	 *          the location to centre the label on
	 * @param theColor
	 *          the colour to plot the text
	 * @param startDTG
	 *          the start (or centre) time of the label
	 * @param endDTG
	 *          the end time, or null if single date value
	 */
	public LabelWrapper(final String label, final WorldLocation location,
			final java.awt.Color theColor, final HiResDate startDTG,
			final HiResDate endDTG)
	{
		_theLocation = location;
		_theLabel = new MWC.GUI.Shapes.TextLabel(location, label);
		_theLabel.setColor(theColor);
		_theShape = MWC.GUI.Shapes.Symbols.SymbolFactory.createSymbol("Square");
		_theShape.setColor(theColor);
		_theLabel.setFont(new Font("Sans Serif", Font.PLAIN, 9));

		// and the dates
		_theTimePeriod = new TimePeriod.BaseTimePeriod(startDTG, endDTG);

	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public final void paint(final CanvasType dest)
	{
		if (getVisible())
		{
			if (_plotSymbol)
			{
				// first paint the symbol
				_theShape.paint(dest, _theLocation);

				// also indicate to the text that we are using an offset
				_theLabel.setFixedOffset(_theShape.getBounds());
			} else
			{
				// indicate to the shape that we don't need an offset
				_theLabel.setFixedOffset(new java.awt.Dimension(0, 0));
			}

			// do we want to paint the label?
			if (_showLabel == true)
			{
				// now paint the text
				_theLabel.paint(dest);
			}
		}
	}

	public final WorldArea getBounds()
	{
		// get the bounds from the data object (or its location object)
		return _theLabel.getBounds();
	}

	/**
	 * return the origin point for the label
	 */
	public final WorldLocation getLocation()
	{
		return _theLabel.getLocation();
	}

	/**
	 * set the origin point for the label
	 */
	public final void setLocation(final WorldLocation val)
	{
		// remember the existing location
		final WorldLocation oldVal = _theLocation;

		// set the new location
		_theLabel.setLocation(val);
		_theLocation = val;

		// fire the update event
		getSupport().firePropertyChange(PlainWrapper.LOCATION_CHANGED, oldVal, val);
	}

	public final String toString()
	{
		return "Label: " + getName();
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
			_myEditor = new labelInfo(this, this.getName());

		return _myEditor;
	}

	/**
	 * if this item has a parent class which should be returned within the
	 * editable data, then specify it here
	 */
	public final void setParent(final Editable parent)
	{
		_myParent = parent;
	}

	public final Editable getParent()
	{
		return _myParent;
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
		_theLabel.setFont(theFont);
	}

	public final Font getFont()
	{
		return _theLabel.getFont();
	}

	@FireReformatted
	public final void setColor(final java.awt.Color theCol)
	{
		_theLabel.setColor(theCol);
		_theShape.setColor(theCol);

		super.setColor(theCol);
	}

	public final java.awt.Color getColor()
	{
		return _theLabel.getColor();
	}

	public final String getLabel()
	{
		return _theLabel.getString();
	}

	public final void setLabel(final String val)
	{
		final String oldVal = _theLabel.getString();

		// do the update
		_theLabel.setString(val);

		// ok, inform any listeners
		getSupport().firePropertyChange(LabelWrapper.TEXT_CHANGED, oldVal, val);
	}

	public final double rangeFrom(final WorldLocation other)
	{
		return _theLocation.rangeFrom(other);
	}

	// ///////////////////////////////////
	// manage the time period
	// ///////////////////////////////////
	public final TimePeriod getTimePeriod()
	{
		return _theTimePeriod;
	}

	public final void setTimePeriod(final TimePeriod val)
	{
		_theTimePeriod = val;
	}

	// ///////////////////////////////////
	// watchable list implementations
	// ///////////////////////////////////

	/**
	 * return the symbol to be used for plotting this track in snail mode
	 */
	public final MWC.GUI.Shapes.Symbols.PlainSymbol getSnailShape()
	{
		return _theShape;
	}

	/**
	 * get the symbol itself
	 * 
	 * @return
	 */
	public final PlainSymbol getShape()
	{
		return _theShape;
	}

	public final HiResDate getStartDTG()
	{
		return _theTimePeriod.getStartDTG();
	}

	public final HiResDate getEndDTG()
	{
		return _theTimePeriod.getEndDTG();
	}

	/**
	 * get the threshold for which points should be visible
	 * 
	 * @return time either side in milliseconds
	 */
	private long getThreshold()
	{
		long res = MWC.GenericData.WatchableList.TIME_THRESHOLD;
		final String appThreshold = Debrief.GUI.Frames.Application
				.getThisProperty("STEP_THRESHOLD");

		if (appThreshold != null)
		{
			// aaah, we actually get a zero length string in SWT, check for that
			if (appThreshold.length() > 0)
			{
				try
				{
					// get actual value (in seconds)
					res = Long.parseLong(appThreshold);
					// convert to millis
					res *= 1000;
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
		boolean res = false;

		// special case, have we been asked for an invalid time period?
		if (DTG == TimePeriod.INVALID_DATE)
		{
			res = true;
		} else
		{

			// see if we are within the threshold of plotting
			final HiResDate myStart = getStartDTG();
			final HiResDate myEnd = getEndDTG();

			// do we have an end point?
			if (getEndDTG() != null)
			{
				// check if we are in the range
				if ((DTG.greaterThanOrEqualTo(myStart))
						&& (DTG.lessThanOrEqualTo(myEnd)))
					res = true;
			} else
			{
				// see if there is just a start (centre) time
				if (myStart != null)
				{
					final long sep = Math.abs(DTG.getMicros() - myStart.getMicros());
					if (sep <= getThreshold())
						res = true;
				} else
				{
					// start and end must equal -1
					res = true;
				}
			}
		}
		if (res == true)
		{
			// produce a new LabelWrapper, with the indicated time
			return new MWC.GenericData.Watchable[]
			{ this };
		} else
			return new MWC.GenericData.Watchable[]
			{};

	}

	/**
	 * method to fulfil requirements of Watchable
	 */
	public final HiResDate getTime()
	{
		return getStartDTG();
	}

	public final void filterListTo(final HiResDate start, final HiResDate end)
	{
		final HiResDate myStart = _theTimePeriod.getStartDTG();
		final HiResDate myEnd = _theTimePeriod.getEndDTG();

		// are we in the time period
		if (myStart != null)
		{
			// do we have an end time?
			if (myEnd != null)
			{
				// we must check both ends to see if we overlap
				if ((myStart.lessThan(end)) && (myEnd.greaterThan(start)))
				{
					setVisible(true);
				} else
				{
					setVisible(false);
				}
			} else
			{
				// we just have a centre time, see if it is in the area
				if ((myStart.greaterThan(start)) && (myStart.lessThan(end)))
				{
					setVisible(true);
				} else
				{
					setVisible(false);
				}
			}
		} else
		{
			// we must be visible if we are'nt time related
			setVisible(true);
		}

		// if we have a property support class, fire the filtered event
		if (getSupport() != null)
			getSupport().firePropertyChange(
					MWC.GenericData.WatchableList.FILTERED_PROPERTY, null, null);

	}

	/**
	 * return the set of items which fall inside the indicated period. If an items
	 * has an "alive" period which overlaps this period then it will be returned.
	 * If the item has no time set, then return it as being valid
	 */
	public final java.util.Collection<Editable> getItemsBetween(final HiResDate start,
			final HiResDate end)
	{
		java.util.Vector<Editable> res = null;

		if (this.getStartDTG() != null)
		{
			// are we inside this period
			final HiResDate startTime = getStartDTG();
			final HiResDate endTime = getEndDTG();

			// have any dates been set?
			if (startTime != null)
			{
				// do we have a finish time?
				if (endTime != null)
				{
					// we have start and finish times, see if we overlap the period at all
					if ((startTime.lessThanOrEqualTo(end))
							&& (endTime.greaterThanOrEqualTo(start)))
					{
						res = new Vector<Editable>(0, 1);
					}
				} else
				{
					// we don't have a finish time, see if we are inside the period
					if ((startTime.greaterThanOrEqualTo(start))
							&& (startTime.lessThanOrEqualTo(end)))
					{
						res = new Vector<Editable>(0, 1);
					}
				}

				// do we have any valid data?
				if (res != null)
				{
					// yes, do we have an end time?
					if (endTime != null)
					{
						// HI-RES NOT DONE - WHAT ON EARTH IS HAPPENING IN THIS NEXT
						// SECTION?

						// produce data from the last of our start time and the period
						// start, to the first
						// of our finish time and the period end
						final long st = Math.max(startTime.getMicros(), start.getMicros());
						final long en = Math.min(endTime.getMicros(), end.getMicros());

						// work through this dataset, in minutes
						for (long i = st; i <= en; i += 60 * 1000 * 1000)
						{
							res.addElement(new LabelWrapper(this.getLabel(), this
									.getLocation(), this.getColor(), new HiResDate(0, i),
									new HiResDate(0, i)));
						}
					} else
					{
						// HI-RES NOT DONE - WHAT ON EARTH IS HAPPENING IN THIS NEXT
						// SECTION?

						// produce data from the last of our start time and the data start
						// time, to the end time of the
						// data
						final long st = Math.max(startTime.getMicros(), start.getMicros());

						// work through this dataset, in minutes
						for (long i = st; i <= end.getMicros(); i += 60 * 1000 * 1000)
						{
							res.addElement(new LabelWrapper(this.getLabel(), this
									.getLocation(), this.getColor(), new HiResDate(0, i),
									new HiResDate(0, i)));
						}
					}
				}

			} else
			{
				// no dates have been set - just say yes we are visible
				res = new Vector<Editable>(0, 1);
				res.add(this);
			}
		} else
		{
			// no times are set - just return ourselves
			res = new Vector<Editable>(0, 1);
			res.add(this);
		}

		return res;
	}

	// /////////////////////////////////////
	// watchable implementations
	// /////////////////////////////////////

	public final double getCourse()
	{
		return 0;
	}

	public final double getSpeed()
	{
		return 0;
	}

	public final double getDepth()
	{
		return _theLocation.getDepth();
	}

	// ////////////////////////////////////////////////////
	// bean accessor methods
	// ///////////////////////////////////////////////////

	/**
	 * we've got this extra time accessor so we can have nicely named properties
	 * in the editor
	 */
	public final void setTimeStart(final HiResDate val)
	{
		_theTimePeriod.setStartDTG(val);
	}

	/**
	 * we've got this extra time accessor so we can have nicely named properties
	 * in the editor
	 */
	public final void setTime_End(final HiResDate val)
	{
		_theTimePeriod.setEndDTG(val);
	}

	/**
	 * we've got this extra time accessor so we can have nicely named properties
	 * in the editor
	 */
	public final HiResDate getTimeStart()
	{
		return _theTimePeriod.getStartDTG();
	}

	/**
	 * we've got this extra time accessor so we can have nicely named properties
	 * in the editor
	 */
	public final HiResDate getTime_End()
	{
		return _theTimePeriod.getEndDTG();
	}

	public final void setLabelVisible(final Boolean val)
	{
		setLabelVisible(val.booleanValue());
	}

	public final boolean getLabelVisible()
	{
		return _showLabel;
	}

	public final void setLabelVisible(final boolean val)
	{
		_showLabel = val;

		// ok, inform any listeners
		getSupport().firePropertyChange(LabelWrapper.LABEL_VIS_CHANGED, null,
				new Boolean(val));
	}

	public final void setSymbolVisible(final boolean val)
	{
		_plotSymbol = val;

		// ok, inform any listeners
		getSupport().firePropertyChange(LabelWrapper.SYMBOL_VIS_CHANGED, null,
				new Boolean(val));
	}

	public final boolean getSymbolVisible()
	{
		return _plotSymbol;
	}

	public final String getSymbolType()
	{
		return _theShape.getType();
	}

	public final void setSymbolType(final String val)
	{
		// is this the type of our symbol?
		if (val.equals(_theShape.getType()))
		{
			// don't bother we're using it already
		} else
		{
			// remember the size of the symbol
			final double scale = _theShape.getScaleVal();
			// replace our symbol with this new one
			_theShape = null;
			_theShape = MWC.GUI.Shapes.Symbols.SymbolFactory.createSymbol(val);
			if (_theShape == null)
			{
				MWC.Utilities.Errors.Trace.trace("Unable to create symbol of type:"
						+ val);
			} else
			{
				_theShape.setColor(this.getColor());

				// update the size
				_theShape.setScaleVal(scale);
			}
		}
	}

	public final void setSymbolSize(final Double val)
	{
		_theShape.setScaleVal(val.doubleValue());
	}

	public final Double getSymbolSize()
	{
		return new Double(_theShape.getScaleVal());
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public final class labelInfo extends Editable.EditorType
	{

		public labelInfo(final LabelWrapper data, final String theName)
		{
			super(data, theName, "Label:");
		}

		public final BeanInfo[] getAdditionalBeanInfo()
		{

			java.util.Vector<BeanInfo> list = null;

			// see if we have a parent class
			BeanInfo[] res = null;
			BeanInfo sampler = null;

			// see if our parent has any properties
			if (_myParent != null)
			{
				// create list
				list = new Vector<BeanInfo>(0, 1);

				// add this item to the list
				list.addElement(_myParent.getInfo());

				// take a copy of the data, for us to use as a sample later on
				sampler = _myParent.getInfo();
			}

			// see if our symbol has any properties

			// has it got an editor?
			if (_theShape.hasEditor())
			{
				final BeanInfo info = _theShape.getInfo();

				// do we need to create our list?
				if (list == null)
					list = new Vector<BeanInfo>(0, 1);

				// remember it
				list.addElement(info);
			}

			if (list != null)
			{
				// create the dummy list to show toArray what we are after
				final BeanInfo[] dummy = new BeanInfo[]
				{ sampler };

				// put the list onto the array
				res = list.toArray(dummy);
			}

			return res;
		}

		public final PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{ prop("Color", "the label color"), prop("Label", "the text showing"),
						prop("Font", "the label font"),
						prop("SymbolVisible", "whether a symbol is plotted"),
						prop("LabelLocation", "the relative location of the label"),
						prop("Location", "the location of the origin of the label"),
						prop("SymbolType", "the type of symbol plotted for this label"),
						prop("SymbolSize", "the scale of the symbol"),
						prop("LabelVisible", "whether the label is plotted"),
						prop("Visible", "whether the label and symbol are plotted"),
						prop("TimeStart", "the start DTG"),
						prop("Time_End", "the end DTG"), };

				// set the custom editors
				res[4]
						.setPropertyEditorClass(MWC.GUI.Properties.LocationPropertyEditor.class);
				res[6]
						.setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.class);
				res[7]
						.setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.class);

				return res;

			} catch (IntrospectionException e)
			{
				System.err.println("Problem generating property editors (see below)");
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}

		public final MethodDescriptor[] getMethodDescriptors()
		{
			// just add the reset color field first
			final Class<ShapeWrapper> c = ShapeWrapper.class;
			final MethodDescriptor[] mds =
			{ method(c, "exportThis", null, "Export Shape") };
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
			MWC.GUI.Editable ed = new LabelWrapper(null, null, null);
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		public final void testWatchables()
		{
			final WatchableList.TestWatchables tw = new WatchableList.TestWatchables()
			{
				/**
				 * get an example of this kind of list with both dates set
				 * 
				 * @return
				 */
				public WatchableList getBothDates(final HiResDate startDate,
						final HiResDate endDate)
				{
					return new LabelWrapper("both", null, Color.red, startDate, endDate)
					{
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

					};
				}

				/**
				 * get an example of this kind of list with no dates set
				 * 
				 * @return
				 */
				public WatchableList getNullDates()
				{
					return new LabelWrapper("both", null, Color.red, null, null)
					{
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;
					};
				}

				/**
				 * get an example of this kind of list with only start date set
				 * 
				 * @return
				 */
				public WatchableList getStartDateOnly(final HiResDate startDate)
				{
					return new LabelWrapper("both", null, Color.red, startDate, null)
					{
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;

					};
				}
			};

			tw.doTest(this);
		}
	}

	public static void main(final String[] args)
	{
		final testMe tm = new testMe("testing");
		tm.testWatchables();

		JFrame jf = new JFrame("here");
		Layers theData = new Layers();
		SwingChart sc = new SwingChart(theData);
		RectangleShape rect = new RectangleShape(new WorldLocation(50.679199,
				-1.0351547, 0), new WorldLocation(50.4545845, -0.6318624, 0));
		ShapeWrapper sw = new ShapeWrapper("here", rect, Color.blue, null);
		sw.setLabelVisible(false);
		LabelWrapper lw = new LabelWrapper("there\nand here\nand there again",
				new WorldLocation(50.6114132, -0.7973965, 0), Color.red);
		Layer misc = new BaseLayer();
		lw.setLabelLocation(new Integer(
				MWC.GUI.Properties.LocationPropertyEditor.TOP));
		misc.setName("misc");
		misc.add(sw);
		misc.add(lw);
		theData.addThisLayer(misc);

		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add("Center", sc.getPanel());

		SwingPropertiesPanel props = new SwingPropertiesPanel(sc, null, null, null);
		props.addEditor(lw.getInfo(), misc);
		jf.getContentPane().add("West", props);

		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(900, 600);
		jf.setVisible(true);

	}

	public void shift(WorldVector vector)
	{
		// ok, shift it
		WorldLocation oldCentre = getLocation();
		WorldLocation newCentre = oldCentre.add(vector);
		setLocation(newCentre);
	}

	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			LocationConstruct currentNearest, Layer parentLayer, Layers theData)
	{
		// calculate the distance
		WorldDistance thisDist = new WorldDistance(rangeFrom(cursorLoc),
				WorldDistance.DEGS);

		// see if we're closer
		currentNearest.checkMe(this, thisDist, this.getLocation(), parentLayer);
	}

}
