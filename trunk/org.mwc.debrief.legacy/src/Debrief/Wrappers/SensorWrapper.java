// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SensorWrapper.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.17 $
// $Log: SensorWrapper.java,v $
// Revision 1.17  2006/09/25 14:51:15  Ian.Mayo
// Respect new "has children" property of Layers
//
// Revision 1.16  2006/02/13 16:19:07  Ian.Mayo
// Sort out problem with creating sensor data
//
// Revision 1.15  2006/01/06 10:37:42  Ian.Mayo
// Reflect tidying of sensor wrapper naming
//
// Revision 1.14  2005/06/06 14:45:06  Ian.Mayo
// Refactor how we support tma & sensor data
//
// Revision 1.13  2005/06/06 14:17:32  Ian.Mayo
// Reproduce TMAWrapper workaround for sensor data where track visible but none of the individual items
//
// Revision 1.12  2005/02/28 14:57:05  Ian.Mayo
// Handle situation when we have sensor & TUA data outside track period.
//
// Revision 1.11  2005/02/22 09:31:58  Ian.Mayo
// Refactor snail plotting sensor & tma data - so that getting & managing valid data points are handled in generic fashion.  We did have two very similar implementations, tracking errors introduced after hi-res-date changes was proving expensive/unreliable.  All fine now though.
//
// Revision 1.10  2005/01/28 10:52:57  Ian.Mayo
// Fix problems where last data point not shown.
//
// Revision 1.9  2005/01/24 10:30:42  Ian.Mayo
// Provide accessor for host track - to help snail plotting
//
// Revision 1.8  2004/12/17 15:54:00  Ian.Mayo
// Get on top of some problems plotting sensor & tma data.
//
// Revision 1.7  2004/11/25 11:04:38  Ian.Mayo
// More test fixing after hi-res switch, largely related to me removing some unused accessors which were property getters
//
// Revision 1.6  2004/11/25 10:24:48  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.5  2004/11/22 13:41:05  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.4  2004/09/10 09:11:28  Ian.Mayo
// Correct prior mistaken implementation of add(Editable) - we should have just changed the signature of add(Plottable) et al
//
// Revision 1.3  2004/09/09 10:51:56  Ian.Mayo
// Provide missing methods from Layers structure.  Don't know why they had been missing for so long.  Poss disconnect between ASSET/Debrief development trees
//
// Revision 1.2  2004/09/09 10:23:13  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:49:25  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.12  2003-06-23 13:40:12+01  ian_mayo
// Change line width, if necessary
//
// Revision 1.11  2003-06-16 11:57:33+01  ian_mayo
// Improve tests to check we can add/remove sensor contact data
//
// Revision 1.10  2003-03-27 11:22:54+00  ian_mayo
// reflect new strategy where we return all data when asked to filter by invalid time
//
// Revision 1.9  2003-03-19 15:36:52+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.8  2003-01-15 15:48:23+00  ian_mayo
// With getNearestTo, return annotation when no DTG supplied
//
// Revision 1.7  2002-10-30 16:27:25+00  ian_mayo
// tidy up (shorten) display names of editables
//
// Revision 1.6  2002-10-28 09:04:34+00  ian_mayo
// provide support for variable thickness of lines in tracks, etc
//
// Revision 1.5  2002-10-01 15:41:40+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.4  2002-07-10 14:58:57+01  ian_mayo
// correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.3  2002-07-09 15:27:28+01  ian_mayo
// Return zero-length list instead of null
//
// Revision 1.2  2002-05-28 09:25:13+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:38+01  ian_mayo
// Initial revision
//
// Revision 1.0  2002-04-30 09:14:54+01  ian
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:27+01  ian_mayo
// Initial revision
//
// Revision 1.9  2001-10-02 09:32:15+01  administrator
// Use new methods for supporting sorted-lists, we aren't getting correct values for tailSet and subSet now that we have changed the comparable implementation within SensorContactWrapper.  We had to do this to allow more than one contact per DTG
//
// Revision 1.8  2001-10-01 12:49:50+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.7  2001-10-01 11:21:39+01  administrator
// Add tests to check we correctly add/manage multiple contacts with the same DTG
//
// Revision 1.6  2001-08-29 19:17:50+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.5  2001-08-24 12:40:25+01  administrator
// Implement remove method
//
// Revision 1.4  2001-08-21 15:19:06+01  administrator
// Improve RangeFrom method
//
// Revision 1.3  2001-08-21 12:05:01+01  administrator
// getFarEnd no longer tries to get its location from the parent
// class testing extended
//
// Revision 1.2  2001-08-17 07:59:19+01  administrator
// Tidying up comments
//
// Revision 1.1  2001-08-14 14:08:17+01  administrator
// finish the implementation
//
// Revision 1.0  2001-08-09 14:16:50+01  administrator
// Initial revision
//
// Revision 1.1  2001-07-31 16:37:21+01  administrator
// show the length of the narrative list when we get its name
//
// Revision 1.0  2001-07-17 08:41:10+01  administrator
// Initial revision
//
// Revision 1.3  2001-07-16 15:02:10+01  novatech
// provide methods to meet new Plottable signature (setVisible)
//
// Revision 1.2  2001-07-09 14:02:47+01  novatech
// let SensorWrapper handle the stepper control
//
// Revision 1.1  2001-07-06 16:00:27+01  novatech
// Initial revision
//

package Debrief.Wrappers;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;

import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.MessageProvider;
import MWC.GUI.TimeStampedDataItem;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

public class SensorWrapper extends TacticalDataWrapper implements
		GriddableSeriesMarker, Cloneable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * more optimisatons
	 */
	transient private SensorContactWrapper nearestContact;

	/**
	 * the (optional) sensor offset value, indicating the forward/backward offset
	 * compared to the attack datum of the platform.
	 */
	private WorldDistance.ArrayLength _sensorOffset = new WorldDistance.ArrayLength(
			0);

	/**
	 * the (optional) indicator for whether the centre of this sensor is in a
	 * straight line fwd/backward of the attack datum, or whether it's a dragged
	 * sensor that follows the track of it's host platform (like a towed array).
	 */
	private boolean _wormInHole = true;

	private HiResDate _lastDataFrequency = new HiResDate(0,
			TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

	// //////////////////////////////////////
	// constructors
	/**
	 * ////////////////////////////////////////
	 */
	public SensorWrapper(final String title)
	{
		super(title);
	}

	/**
	 * create a copy of the supplied sensor wrapper
	 * 
	 * @param other
	 *          wrapper to copy
	 */
	public SensorWrapper(final SensorWrapper other)
	{
		super(other.getName());
		this.setTrackName(other.getTrackName());
		this.setHost(other.getHost());
		this.setColor(other.getColor());
		this.setVisible(other.getVisible());
		this.setWormInHole(other.getWormInHole());
		this.setSensorOffset(other.getSensorOffset());
		this.setLineThickness(other.getLineThickness());
	}

	// //////////////////////////////////////
	// member methods to meet plain wrapper responsibilities
	// //////////////////////////////////////

	/**
	 * the real getBounds object, which uses properties of the parent
	 */
	public final MWC.GenericData.WorldArea getBounds()
	{
		// we no longer just return the bounds of the track, because a portion
		// of the track may have been made invisible.
		// instead, we will pass through the full dataset and find the outer
		// bounds of the visible area
		WorldArea res = null;

		if (!getVisible())
		{
			// hey, we're invisible, return null
		}
		else
		{
			final java.util.Iterator<Editable> it = this._myContacts.iterator();
			while (it.hasNext())
			{
				final SensorContactWrapper fw = (SensorContactWrapper) it.next();

				// is this point visible?
				if (fw.getVisible())
				{

					// has our data been initialised?
					if (res == null)
					{
						// no, initialise it
						WorldLocation startOfLine = fw.getCalculatedOrigin(_myHost);

						// we may not have a sensor-data origin, since the
						// sensor may be out of the time period of the track
						if (startOfLine != null)
							res = new WorldArea(startOfLine, startOfLine);
					}
					else
					{
						// yes, extend to include the new area
						res.extend(fw.getCalculatedOrigin(_myHost));
					}

					// do we have a far end?

					if (fw.getRange() != null)
					{
						WorldLocation farEnd = fw.getFarEnd(null);
						if (farEnd != null)
						{
							if (res == null)
								res = new WorldArea(farEnd, farEnd);
							else
								res.extend(fw.getFarEnd(null));
						}
					}
				}
			}
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
			_myEditor = new SensorInfo(this);

		return _myEditor;
	}

	/**
	 * add
	 * 
	 * @param plottable
	 *          parameter for add
	 */
	public final void add(final MWC.GUI.Editable plottable)
	{
		// check it's a sensor contact entry
		if (plottable instanceof SensorContactWrapper)
		{
			_myContacts.add(plottable);

			final SensorContactWrapper scw = (SensorContactWrapper) plottable;

			// maintain our time period
			if (_timePeriod == null)
				_timePeriod = new MWC.GenericData.TimePeriod.BaseTimePeriod(
						scw.getDTG(), scw.getDTG());
			else
				_timePeriod.extend(scw.getDTG());

			// and tell the contact about us
			scw.setSensor(this);
		}
	}

	public final void append(Layer theLayer)
	{
		if (theLayer instanceof SensorWrapper)
		{
			SensorWrapper other = (SensorWrapper) theLayer;
			SortedSet<Editable> otherC = other._myContacts;
			for (Iterator<Editable> iterator = otherC.iterator(); iterator.hasNext();)
			{
				SensorContactWrapper thisC = (SensorContactWrapper) iterator.next();
				this.add(thisC);
			}

			// and clear him out...
			otherC.clear();
		}
	}

	public boolean hasOrderedChildren()
	{
		return false;
	}

	// ///////////////////////////////////////
	// other member functions
	// ///////////////////////////////////////

	/**
   */

	public final String toString()
	{
		return "Sensor:" + getName() + " (" + _myContacts.size() + " items)";
	}

	/**
	 * method to allow the setting of data sampling frequencies for the track &
	 * sensor data
	 * 
	 * @return frequency to use
	 */
	public final HiResDate getResampleDataAt()
	{
		return this._lastDataFrequency;
	}

	/**
	 * set the data frequency (in seconds) for the track & sensor data
	 * 
	 * @param theVal
	 *          frequency to use
	 */
	@FireExtended
	public final void setResampleDataAt(final HiResDate theVal)
	{
		this._lastDataFrequency = theVal;

		// have a go at trimming the start time to a whole number of intervals
		final long interval = theVal.getMicros();

		// do we have a start time (we may just be being tested...)
		if (this.getStartDTG() == null)
		{
			return;
		}

		final long currentStart = this.getStartDTG().getMicros();
		long startTime = (currentStart / interval) * interval;

		// just check we're in the range
		if (startTime < currentStart)
			startTime += interval;

		// just check it's not a barking frequency
		if (theVal.getDate().getTime() <= 0)
		{
			// ignore, we don't need to do anything for a zero or a -1
		}
		else
		{
			decimate(theVal, startTime);
		}
	}

	/**
	 * how far away are we from this point? or return null if it can't be
	 * calculated
	 */
	public final double rangeFrom(final WorldLocation other)
	{
		double res = INVALID_RANGE;

		// if we have a nearest contact, see how far away it is.
		if (nearestContact != null)
			res = nearestContact.rangeFrom(other);

		return res;
	}

	// /////////////////////////////////////////////////////////////////
	// support for WatchableList interface (required for Snail Trail plotting)
	// //////////////////////////////////////////////////////////////////

	/**
	 * get the watchable in this list nearest to the specified DTG - we take most
	 * of this processing from the similar method in TrackWrappper. If the DTG is
	 * after our end, return our last point
	 * 
	 * @param DTG
	 *          the DTG to search for
	 * @return the nearest Watchable
	 */
	public final MWC.GenericData.Watchable[] getNearestTo(final HiResDate DTG)
	{

		/**
		 * we need to end up with a watchable, not a fix, so we need to work our way
		 * through the fixes
		 */
		MWC.GenericData.Watchable[] res = new MWC.GenericData.Watchable[]
		{};

		// check that we do actually contain some data
		if (_myContacts.size() == 0)
			return res;

		// see if this is the DTG we have just requestsed
		if ((DTG.equals(lastDTG)) && (lastContact != null))
		{
			res = lastContact;
		}
		else
		{
			// see if this DTG is inside our data range
			// in which case we will just return null
			final SensorContactWrapper theFirst = (SensorContactWrapper) _myContacts
					.first();
			final SensorContactWrapper theLast = (SensorContactWrapper) _myContacts
					.last();

			if ((DTG.greaterThanOrEqualTo(theFirst.getDTG()))
					&& (DTG.lessThanOrEqualTo(theLast.getDTG())))
			{
				// yes it's inside our data range, find the first fix
				// after the indicated point

				// see if we have to create our local temporary fix
				if (nearestContact == null)
				{
					nearestContact = new SensorContactWrapper(null, DTG, null, null,
							null, null, null, 0, getName());
				}
				else
					nearestContact.setDTG(DTG);

				// get the data..
				final java.util.Vector<SensorContactWrapper> list = new java.util.Vector<SensorContactWrapper>(
						0, 1);
				boolean finished = false;
				final java.util.Iterator<Editable> it = _myContacts.iterator();
				while ((it.hasNext()) && (!finished))
				{
					final SensorContactWrapper scw = (SensorContactWrapper) it.next();
					HiResDate thisDate = scw.getTime();
					if (thisDate.lessThan(DTG))
					{
						// before it, ignore!
					}
					else if (thisDate.greaterThan(DTG))
					{
						// hey, it's a possible - if we haven't found an exact
						// match
						if (list.size() == 0)
						{
							list.add(scw);
						}
						else
						{
							// hey, we're finished!
							finished = true;
						}
					}
					else
					{
						// hey, it must be at the same time!
						list.add(scw);
					}

				}

				if (list.size() > 0)
				{
					final MWC.GenericData.Watchable[] dummy = new MWC.GenericData.Watchable[]
					{ null };
					res = list.toArray(dummy);
				}
			}
			else if (DTG.greaterThanOrEqualTo(theLast.getDTG()))
			{
				// is it after the last one? If so, just plot the last one. This
				// helps us when we're doing snail trails.
				final java.util.Vector<SensorContactWrapper> list = new java.util.Vector<SensorContactWrapper>(
						0, 1);
				list.add(theLast);
				final MWC.GenericData.Watchable[] dummy = new MWC.GenericData.Watchable[]
				{ null };
				res = list.toArray(dummy);
			}

			// and remember this fix
			lastContact = res;
			lastDTG = DTG;
		}

		return res;

	}

	/**
	 * get the parent's color Note: we're wrapping the color paramter with
	 * defaultColor so that we can provide more understable attribute names in
	 * property editor
	 * 
	 * @return
	 */
	public Color getDefaultColor()
	{
		return super.getColor();
	}

	/**
	 * just pass the property onto the parent
	 * 
	 * @param defaultColor
	 */
	public void setDefaultColor(Color defaultColor)
	{
		super.setColor(defaultColor);
	}

	// ///////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////

	public Boolean getWormInHole()
	{
		return _wormInHole;
	}

	public void setWormInHole(Boolean wormInHole)
	{
		// see if this is a changed setting
		if (wormInHole != _wormInHole)
		{
			// remember the new value
			_wormInHole = wormInHole;

			// we've got to recalculate our positions now, really.
			clearChildOffsets();
		}
	}

	public WorldDistance.ArrayLength getSensorOffset()
	{
		return _sensorOffset;
	}

	public void setSensorOffset(WorldDistance.ArrayLength sensorOffset)
	{
		_sensorOffset = sensorOffset;

		if (_sensorOffset != null)
		{
			clearChildOffsets();
		}
	}

	/**
	 * our parent has changed, clear data that depends on it
	 * 
	 */
	private void clearChildOffsets()
	{
		// we also need to reset the origins on our child elements, since
		// the offset will have changed
		final java.util.Iterator<Editable> it = this._myContacts.iterator();
		while (it.hasNext())
		{
			final SensorContactWrapper fw = (SensorContactWrapper) it.next();
			fw.clearCalculatedOrigin();

			// and tell it we're the boss
			fw.setSensor(this);
		}
	}

	/**
	 * override the parent method - since we want to reset the origin for our
	 * child sensor data items
	 */
	public void setHost(TrackWrapper host)
	{
		super.setHost(host);

		// and clear offsets
		clearChildOffsets();
	}

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public final class SensorInfo extends Editable.EditorType
	{

		/**
		 * constructor for editable details of a set of Layers
		 * 
		 * @param data
		 *          the Layers themselves
		 */
		public SensorInfo(final SensorWrapper data)
		{
			super(data, data.getName(), "Sensor");
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
						prop("Name", "the name for this sensor"),
						prop("Visible", "whether this sensor data is visible"),
						prop("LineThickness", "the thickness to draw these sensor lines"),
						prop("DefaultColor",
								"the default colour to plot this set of sensor data"),
						prop("SensorOffset",
								"the forward/backward offset (m) of this sensor from the attack datum"),
						prop(
								"WormInHole",
								"whether the origin of this sensor is offset in straight line, or back along the host track"),
						longProp("VisibleFrequency",
								"How frequently to display sensor cuts",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class),
						expertLongProp("ResampleDataAt", "the sensor cut sample rate",
								MWC.GUI.Properties.TimeFrequencyPropertyEditor.class) };

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
	}

	// ////////////////////////////////////////////////////
	// nested class for testing
	// /////////////////////////////////////////////////////

	static public final class testSensors extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testSensors(final String val)
		{
			super(val);
		}

		public final void testValues()
		{
			// ok, create the test object
			final SensorWrapper sensor = new SensorWrapper("tester");

			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10,
					4, 4, 4, 0);

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			final long start_time = cal.getTime().getTime();
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 23);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 27);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 02);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 01);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 05);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 55);
			final long end_time = cal.getTime().getTime();
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			// so, we've now build up the list
			// check it has the correct quantity
			assertTrue("Count of items", (sensor._myContacts.size() == 8));

			// check the outer limits
			final HiResDate start = sensor.getStartDTG();
			final HiResDate end = sensor.getEndDTG();
			assertEquals("first time", start.getDate().getTime(), start_time);
			assertEquals("last time", end.getDate().getTime(), end_time);

			// //////////////////////////////////////////////////////////////////////
			// finding the nearest entry
			cal.set(2001, 10, 4, 4, 4, 05);
			MWC.GenericData.Watchable[] list = sensor.getNearestTo(new HiResDate(cal
					.getTime().getTime()));
			SensorContactWrapper nearest = (SensorContactWrapper) list[0];
			assertEquals("Nearest matching fix",
					nearest.getDTG().getDate().getTime(), cal.getTime().getTime());

			final java.util.Calendar cal_other = new java.util.GregorianCalendar(
					2001, 10, 4, 4, 4, 0);
			cal_other.set(2001, 10, 4, 4, 4, 03);
			list = sensor.getNearestTo(new HiResDate(cal_other.getTime().getTime()));
			nearest = (SensorContactWrapper) list[0];
			assertTrue("Nearest or greater than fix",
					(nearest.getDTG().getMicros() / 1000 == cal.getTime().getTime()));

			// ///////////////////////////////////////////////////////////////////
			// filter the list
			cal.set(2001, 10, 4, 4, 4, 22);
			cal_other.set(2001, 10, 4, 4, 4, 25);

			// ////////////////////////////////////////////////////////////////////////
			// do the filter
			sensor.filterListTo(new HiResDate(cal.getTime().getTime()),
					new HiResDate(cal_other.getTime().getTime()));

			// see how many remain visible
			java.util.Enumeration<Editable> iter = sensor.elements();
			int counter = 0;
			while (iter.hasMoreElements())
			{
				final SensorContactWrapper contact = (SensorContactWrapper) iter
						.nextElement();
				if (contact.getVisible())
					counter++;
			}
			// check that the correct number are visible
			assertTrue("Correct filtering of list", (counter == 2));

			// clear the filter
			sensor.filterListTo(sensor.getStartDTG(), sensor.getEndDTG());
			// see how many remain visible
			iter = sensor.elements();
			counter = 0;
			while (iter.hasMoreElements())
			{
				final SensorContactWrapper contact = (SensorContactWrapper) iter
						.nextElement();
				if (contact.getVisible())
					counter++;
			}
			// check that the correct number are visible
			assertTrue("Correct removal of list filter", (counter == 8));

			// //////////////////////////////////////////////////////
			// get items between
			java.util.Collection<Editable> res = sensor.getItemsBetween(
					new HiResDate(cal.getTime().getTime()), new HiResDate(cal_other
							.getTime().getTime()));
			assertTrue("get items between", (res.size() == 2));

			// do recheck, since this time we will be resetting the working
			// variables, rather and creating them
			cal.set(2001, 10, 4, 4, 4, 5);
			cal_other.set(2001, 10, 4, 4, 4, 27);
			res = sensor.getItemsBetween(new HiResDate(cal.getTime().getTime()),
					new HiResDate(cal_other.getTime().getTime()));
			assertEquals("recheck get items between:" + res.size(), 4, res.size());

			// and show all of the data
			res = sensor.getItemsBetween(sensor.getStartDTG(), sensor.getEndDTG());
			assertTrue("recheck get items between:" + res.size(), (res.size() == 8));

			// /////////////////////////////////////////////////////////
			// test the position related stuff
			final TrackWrapper track = new TrackWrapper();

			// and add the fixes
			cal.set(2001, 10, 4, 4, 4, 0);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal
					.getTime().getTime(), 0), new MWC.GenericData.WorldLocation(2.0, 2.0,
					0.0), 12, 12)));

			cal.set(2001, 10, 4, 4, 4, 01);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal
					.getTime().getTime(), 0), new MWC.GenericData.WorldLocation(2.0,
					2.25, 0.0), 12, 12)));

			cal.set(2001, 10, 4, 4, 4, 02);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal
					.getTime().getTime(), 0), new MWC.GenericData.WorldLocation(2.0, 2.5,
					0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 05);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal
					.getTime().getTime(), 0), new MWC.GenericData.WorldLocation(2.0,
					2.75, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 23);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal
					.getTime().getTime(), 0), new MWC.GenericData.WorldLocation(2.25,
					2.0, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 25);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal
					.getTime().getTime(), 0), new MWC.GenericData.WorldLocation(2.5, 2.0,
					0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 28);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal
					.getTime().getTime(), 0), new MWC.GenericData.WorldLocation(2.75,
					2.0, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 55);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal
					.getTime().getTime(), 0), new MWC.GenericData.WorldLocation(2.25,
					2.25, 0.0), 12, 12)));

			// ok, put the sensor data into the track
			track.add(sensor);

			track.setInterpolatePoints(false);

			// now find the location of an item, any item!
			cal.set(2001, 10, 4, 4, 4, 27);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			nearest = (SensorContactWrapper) list[0];
			WorldLocation nearestPoint = nearest.getCalculatedOrigin(track);
			WorldLocation tgtLoc = new MWC.GenericData.WorldLocation(2.66666, 2.0,
					0.0);
			assertEquals("first test", 0, tgtLoc.rangeFrom(nearestPoint), 0.001);

			// ah-ha! what about a contact between two fixes
			cal.set(2001, 10, 4, 4, 4, 26);
			HiResDate theTime = new HiResDate(cal.getTime().getTime(), 0);
			list = sensor.getNearestTo(theTime);
			nearest = (SensorContactWrapper) list[0];
			nearestPoint = nearest.getCalculatedOrigin(track);
			assertEquals("test mid way", 0, tgtLoc.rangeFrom(nearestPoint), 0.001);

			// ok, that was half-way, what making it nearer to one of the fixes
			cal.set(2001, 10, 4, 4, 4, 25);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			nearest = (SensorContactWrapper) list[0];
			nearestPoint = nearest.getCalculatedOrigin(track);
			tgtLoc = new MWC.GenericData.WorldLocation(2.5, 2.0, 0.0);
			assertEquals("test nearer first point", 0,
					tgtLoc.rangeFrom(nearestPoint), 0.001);

			// start point?
			cal.set(2001, 10, 4, 4, 4, 0);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			nearest = (SensorContactWrapper) list[0];
			nearestPoint = nearest.getCalculatedOrigin(track);
			assertEquals("test start point", new MWC.GenericData.WorldLocation(2.0,
					2.0, 0.0), nearestPoint);

			// end point?
			cal.set(2001, 10, 4, 4, 4, 55);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			nearest = (SensorContactWrapper) list[0];
			nearestPoint = nearest.getCalculatedOrigin(track);
			assertEquals("test end point", nearestPoint,
					new MWC.GenericData.WorldLocation(2.25, 2.25, 0.0));

			// before start of track data?
			cal.set(2001, 10, 4, 4, 3, 0);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			assertEquals("before range of data", list.length, 0);

			// after end of track data?
			cal.set(2001, 10, 4, 4, 7, 0);
			list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			assertEquals("after end of data", list.length, 1);

		}

		public final void testDuplicates()
		{
			// ok, create the test object
			final SensorWrapper sensor = new SensorWrapper("tester");

			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10,
					4, 4, 4, 0);

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 23);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 24);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 01);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 05);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			cal.set(2001, 10, 4, 4, 4, 55);
			sensor.add(new SensorContactWrapper("tester", new HiResDate(cal.getTime()
					.getTime()), null, null, null, null, null, 1, sensor.getName()));

			// so, we've now build up the list
			// check it has the correct quantity
			assertEquals("Count of items", 8, sensor._myContacts.size());

			// check the correct number get returned
			cal.set(2001, 10, 4, 4, 4, 25);
			final MWC.GenericData.Watchable[] list = sensor
					.getNearestTo(new HiResDate(cal.getTime().getTime(), 0));
			assertEquals("after end of data", 2, list.length);

		}

		public void testMultipleContacts()
		{
			SensorWrapper sw = new SensorWrapper("bbb");
			SensorContactWrapper sc1 = new SensorContactWrapper("bbb", new HiResDate(
					0, 9), null, null, null, null, "first", 0, sw.getName());
			SensorContactWrapper sc2 = new SensorContactWrapper("bbb", new HiResDate(
					0, 12), null, null, null, null, "first", 0, sw.getName());
			SensorContactWrapper sc3 = new SensorContactWrapper("bbb", new HiResDate(
					0, 7), null, null, null, null, "first", 0, sw.getName());
			SensorContactWrapper sc4 = new SensorContactWrapper("bbb", new HiResDate(
					0, 13), null, null, null, null, "first", 0, sw.getName());

			sw.add(sc1);
			sw.add(sc2);
			sw.add(sc3);
			sw.add(sc4);

			assertEquals("four contacts loaded", 4, sw._myContacts.size());

			// check we can delete from it
			sw.removeElement(sc3);

			assertEquals("now only three contacts loaded", 3, sw._myContacts.size());

		}
	}

	public static void main(final String[] args)
	{
		final testSensors ts = new testSensors("Ian");
		ts.testDuplicates();
		ts.testValues();
	}

	public Editable getSampleGriddable()
	{
		Editable res = null;

		// check we have an item before we edit it
		Enumeration<Editable> eles = this.elements();
		if (eles.hasMoreElements())
			res = eles.nextElement();
		return res;
	}

	public TimeStampedDataItem makeCopy(TimeStampedDataItem item)
	{
		if (false == item instanceof SensorContactWrapper)
		{
			throw new IllegalArgumentException(
					"I am expecting the Observation's, don't know how to copy " + item);
		}
		SensorContactWrapper template = (SensorContactWrapper) item;
		SensorContactWrapper result = new SensorContactWrapper();
		result.setAmbiguousBearing(template.getAmbiguousBearing());
		result.setBearing(template.getBearing());
		result.setColor(template.getColor());
		result.setDTG(template.getDTG());
		result.setFrequency(template.getFrequency());
		result.setHasAmbiguousBearing(template.getHasAmbiguousBearing());
		result.setHasFrequency(template.getHasFrequency());
		result.setLabel(template.getLabel());
		result.setLabelLocation(template.getLabelLocation());
		result.setLabelVisible(template.getLabelVisible());
		result.setLineStyle(template.getLineStyle());
		result.setOrigin(template.getOrigin());
		result.setPutLabelAt(template.getPutLabelAt());
		result.setRange(template.getRange());
		result.setSensor(template.getSensor());
		return result;
	}

	/**
	 * create a new instance of an entity of this type, interpolated between the
	 * supplied sample objects
	 * 
	 */
	protected PlottableWrapperWithTimeAndOverrideableColor createItem(
			PlottableWrapperWithTimeAndOverrideableColor last,
			PlottableWrapperWithTimeAndOverrideableColor next,
			LinearInterpolator interp, long tNow)
	{
		SensorContactWrapper _next = (SensorContactWrapper) next;
		SensorContactWrapper _last = (SensorContactWrapper) last;

		double brg = interp.interp(_last.getBearing(), _next.getBearing());
		double ambig = 0;
		// note - don't bother checking for has ambig, just do the interpolation
		ambig = interp.interp(_last.getAmbiguousBearing(),
				_next.getAmbiguousBearing());

		double freq = interp.interp(_last.getFrequency(), _next.getFrequency());
		// do we have range?
		WorldDistance theRng = null;
		if ((_last.getRange() != null) && (_next.getRange() != null))
		{
			// are they both in the same units?
			if (_last.getRange().getUnits() == _last.getRange().getUnits())
			{
				// they're in the same units, stick with it.
				int theUnits = _last.getRange().getUnits();
				double theVal = interp.interp(
						_last.getRange().getValue(), _next.getRange().getValue());
				theRng = new WorldDistance(theVal, theUnits);
			}
			else
			{
				// they're in different units, do it all in degrees
				double rngDegs = interp.interp(
						_last.getRange().getValueIn(WorldDistance.DEGS), _next.getRange()
								.getValueIn(WorldDistance.DEGS));
				theRng = new WorldDistance(rngDegs, WorldDistance.DEGS);
			}
		}
		// do we have an origin?
		WorldLocation origin = null;
		if ((_last.getOrigin() != null) && (_next.getOrigin() != null))
		{
			double orLat = interp.interp(_last.getOrigin().getLat(), _next
					.getOrigin().getLat());
			double orLong = interp.interp(_last.getOrigin().getLong(), _next
					.getOrigin().getLong());
			origin = new WorldLocation(orLat, orLong, 0);
		}

		// now, go create the new data item
		SensorContactWrapper newS = new SensorContactWrapper(_last.getTrackName(),
				new HiResDate(0, tNow), theRng, brg, ambig, freq, origin,
				_last.getActualColor(), _last.getName(), _last.getLineStyle()
						.intValue(), _last.getSensorName());

		// sort out the ambiguous data
		newS.setHasAmbiguousBearing(_last.getHasAmbiguousBearing());

		return newS;
	}

	/**
	 * perform a merge of the supplied tracks.
	 * 
	 * @param target
	 *          the final recipient of the other items
	 * @param theLayers
	 * @param parent
	 *          the parent tracks for the supplied items
	 * @param subjects
	 *          the actual selected items
	 * @return sufficient information to undo the merge
	 */
	public static int mergeSensors(final Editable targetE, Layers theLayers,
			final Layer parent, final Editable[] subjects)
	{
		SensorWrapper target = (SensorWrapper) targetE;

		for (int i = 0; i < subjects.length; i++)
		{
			SensorWrapper sensor = (SensorWrapper) subjects[i];
			if (sensor != target)
			{
				// ok, append the items in this layer to the target
				target.append(sensor);
				parent.removeElement(sensor);
			}
		}

		return MessageProvider.OK;
	}

	@Override
	public boolean supportsAddRemove()
	{
		return true;
	}

	@Override
	public boolean requiresManualSave()
	{
		return false;
	}

	@Override
	public void doSave(String message)
	{
		throw new RuntimeException("should not have called manual save for Sensor Wrapper");
	}

}
