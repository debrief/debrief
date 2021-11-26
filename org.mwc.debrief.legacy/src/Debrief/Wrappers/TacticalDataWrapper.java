/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

// $RCSfile: TacticalDataWrapper.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: TacticalDataWrapper.java,v $
// Revision 1.2  2005/06/07 08:38:31  Ian.Mayo
// Provide efficiency to stop millions of popup menu items representing hidden tma solutions.
//
// Revision 1.1  2005/06/06 14:45:08  Ian.Mayo
// Refactor how we support tma & sensor data
//

package Debrief.Wrappers;

import java.awt.Color;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact;
import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import MWC.GUI.Defaults;
import MWC.GUI.Editable;
import MWC.GUI.FireReformatted;
import MWC.GUI.HasEditables;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.TextFormatting.GMTDateFormat;

abstract public class TacticalDataWrapper extends MWC.GUI.PlainWrapper
		implements SnailDrawTacticalContact.HostedList, HasEditables {

	// //////////////////////////////////////////////////////////////////
	// embedded class to allow us to pass the local iterator (Iterator) used
	// internally
	// outside as an Enumeration
	// /////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	public static final class IteratorWrapper implements java.util.Enumeration<Editable> {
		/**
		 * java.util.Iterator _val
		 */
		private final java.util.Iterator<Editable> _val;

		/**
		 * <init>
		 *
		 * @param iterator parameter for <init>
		 */
		public IteratorWrapper(final java.util.Iterator<Editable> iterator) {
			_val = iterator;
		}

		/**
		 * hasMoreElements
		 *
		 * @return the returned boolean
		 */
		@Override
		public final boolean hasMoreElements() {
			return _val.hasNext();

		}

		/**
		 * nextElement
		 *
		 * @return the returned Object
		 */
		@Override
		public final Editable nextElement() {
			return _val.next();
		}
	}

	/**
	 * a specialist linear interpolator class that allows quick (repeated)
	 * interpolations for a set of similar items
	 *
	 * @author ianmayo
	 *
	 */
	protected static class LinearInterpolator {

		private final double _startTime;
		private final double _desiredTime;
		private final long _timeDelta;

		/**
		 * Prepare the temporal domain data
		 */
		public LinearInterpolator(final Watchable startValue, final Watchable endValue, final double desiredTime) {
			_desiredTime = desiredTime;
			_startTime = startValue.getTime().getMicros();
			_timeDelta = endValue.getTime().getMicros() - startValue.getTime().getMicros();
		}

		/**
		 * Return the interpolated value in the supplied domain.
		 */
		public double interp(final double startVariable, final double endVariable) {
			double start = startVariable;
			double end = endVariable;
			// do a quick sanity to check to verify if we're an angle passing through
			// zero
			// if we don't do this check then it will try to do it the 'short-way'
			// through 180 degs
			if (Math.abs(end - start) > 180) {
				if (start > 180)
					start -= 360;
				if (end > 180)
					end -= 360;
			}

			final double gradient = (end - start) / (_timeDelta);
			return start + (_desiredTime - _startTime) * gradient;
		}
	}

	protected interface ObjectManipulator {
		void apply(Object subject, boolean value);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase {
		private static SensorWrapper getList() throws ParseException {
			final SensorWrapper sw = new SensorWrapper("mySensor");

			SensorContactWrapper scw = new SensorContactWrapper("parent",
					new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:34:04 PM GMT")
							.getTime()),
					new WorldDistance(2, WorldDistance.NM), 13.1, new WorldLocation(1, 3, 0), Color.RED, "the label", 0,
					"other label");
			sw.add(scw);

			scw = new SensorContactWrapper("parent",
					new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:34:54 PM GMT")
							.getTime()),
					new WorldDistance(2, WorldDistance.NM), 7.9, new WorldLocation(1, 3, 0), Color.RED, "the label", 0,
					"other label");
			sw.add(scw);

			scw = new SensorContactWrapper("parent",
					new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:35:32 PM GMT")
							.getTime()),
					new WorldDistance(2, WorldDistance.NM), 4.8, new WorldLocation(1, 3, 0), Color.RED, "the label", 0,
					"other label");
			sw.add(scw);

			scw = new SensorContactWrapper("parent",
					new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:36:36 PM GMT")
							.getTime()),
					new WorldDistance(2, WorldDistance.NM), 359.1, new WorldLocation(1, 3, 0), Color.RED, "the label",
					0, "other label");
			sw.add(scw);

			scw = new SensorContactWrapper("parent",
					new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:37:24 PM GMT")
							.getTime()),
					new WorldDistance(2, WorldDistance.NM), 355.2, new WorldLocation(1, 3, 0), Color.RED, "the label",
					0, "other label");
			sw.add(scw);

			scw = new SensorContactWrapper("parent",
					new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:38:18 PM GMT")
							.getTime()),
					new WorldDistance(2, WorldDistance.NM), 348.6, new WorldLocation(1, 3, 0), Color.RED, "the label",
					0, "other label");
			sw.add(scw);

			scw = new SensorContactWrapper("parent",
					new HiResDate(new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:39:04 PM GMT")
							.getTime()),
					new WorldDistance(2, WorldDistance.NM), 345.1, new WorldLocation(1, 3, 0), Color.RED, "the label",
					0, "other label");
			sw.add(scw);
			return sw;
		}

		public static void testDecimate() {
			final SensorWrapper sw = new SensorWrapper("mySensor");
			long nowMillis = 3000;
			for (int i = 0; i < 6; i++) {
				final SensorContactWrapper scw = new SensorContactWrapper("parent", new HiResDate(nowMillis),
						new WorldDistance(i, WorldDistance.NM), (double) i, new WorldLocation(1, i, 0), Color.RED,
						"the label", 0, "other label");
				sw.add(scw);
				nowMillis += 16 * 1000;
			}

			// jump forward a while
			nowMillis += 5 * 60 * 1000;

			for (int j = 0; j < 6; j++) {
				final SensorContactWrapper scw = new SensorContactWrapper("parent", new HiResDate(nowMillis),
						new WorldDistance(j, WorldDistance.NM), (double) j, new WorldLocation(1, j, 0), Color.RED,
						"the label", 0, "other label");
				sw.add(scw);
				nowMillis += 16 * 1000;
			}

			assertEquals("correct number before", 12, sw._myContacts.size());

			sw.decimate(new HiResDate(20000), 4000000);

			assertEquals("correct number of decimated", 24, sw._myContacts.size());
		}

		public static void testDecimateThroughZero() throws ParseException {
			final SensorWrapper sw = getList();

			Iterator<Editable> iter = sw._myContacts.iterator();
			while (iter.hasNext()) {
				final SensorContactWrapper sc = (SensorContactWrapper) iter.next();
				// System.out.println(sc.getDTG().getDate() + ", " + sc.getBearing());
			}

			// System.out.println("=================");

			assertEquals("correct number before", 7, sw._myContacts.size());

			final Date startDate = new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z")
					.parse("July 7, 2011 12:34:00 PM GMT");

			sw.decimate(new HiResDate(60000), startDate.getTime());

			assertEquals("correct number of decimated", 6, sw._myContacts.size());

			iter = sw._myContacts.iterator();
			while (iter.hasNext()) {
				final SensorContactWrapper sc = (SensorContactWrapper) iter.next();
				// System.out.println(sc.getDTG().getDate() + ", " + sc.getBearing());
			}
		}

		public static void testTrim() throws ParseException {
			SensorWrapper ts0 = getList();

			TimePeriod newP = new TimePeriod.BaseTimePeriod(
					new HiResDate(
							new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:34:04 PM GMT")),
					new HiResDate(
							new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:39:04 PM GMT")));
			assertEquals("correct len", 7, ts0._myContacts.size());
			ts0.trimTo(newP);
			assertEquals("correct new len", 7, ts0._myContacts.size());

			ts0 = getList();
			newP = new TimePeriod.BaseTimePeriod(
					new HiResDate(
							new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:34:04 PM GMT")),
					new HiResDate(
							new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:35:32 PM GMT")));
			assertEquals("correct len", 7, ts0._myContacts.size());
			ts0.trimTo(newP);
			assertEquals("correct new len", 3, ts0._myContacts.size());

			ts0 = getList();
			newP = new TimePeriod.BaseTimePeriod(
					new HiResDate(
							new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:35:32 PM GMT")),
					new HiResDate(
							new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:38:18 PM GMT")));
			assertEquals("correct len", 7, ts0._myContacts.size());
			ts0.trimTo(newP);
			assertEquals("correct new len", 4, ts0._myContacts.size());

			ts0 = getList();
			newP = new TimePeriod.BaseTimePeriod(
					new HiResDate(
							new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2011 12:41:04 PM GMT")),
					new HiResDate(
							new GMTDateFormat("MMMM d, yyyy HH:mm:ss aa z").parse("July 7, 2012 12:39:04 PM GMT")));
			assertEquals("correct len", 7, ts0._myContacts.size());
			ts0.trimTo(newP);
			assertEquals("correct new len", 0, ts0._myContacts.size());

		}

	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// //////////////////////////////////////
	// member variables
	// //////////////////////////////////////
	/**
	 * the name of this sensor
	 */
	private String _myName = "blank sensor";

	/**
	 * how often items are displayed
	 *
	 */
	private HiResDate _lastVisibleFrequency = new HiResDate(0, TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY);

	/**
	 * the track representing our host vessel
	 */
	private String _myTrackName;

	/**
	 * our editor
	 */
	protected transient MWC.GUI.Editable.EditorType _myEditor;

	/**
	 * our list of contacts
	 */
	protected final java.util.SortedSet<Editable> _myContacts;

	/**
	 * the track of our host
	 */
	protected transient TrackWrapper _myHost;

	/**
	 * manage the start/stop times for this period
	 */
	protected MWC.GenericData.TimePeriod _timePeriod;

	/**
	 * local copy of the last fix found, to speed up some getNearestTo(...)
	 * operations
	 */
	protected transient MWC.GenericData.Watchable[] lastContact;

	/**
	 * the DTG last searched for in getNearestTo()
	 */
	protected transient HiResDate lastDTG;

	/**
	 * thickness of line to draw
	 */
	private int _lineWidth = 1;

	// //////////////////////////////////////
	// constructors
	/**
	 * ////////////////////////////////////////
	 */
	public TacticalDataWrapper(final String title) {
		_myName = title;
		_myContacts = new java.util.TreeSet<Editable>();
		setVisible(false);
	}


	// //////////////////////////////////////
	// member methods to meet plain wrapper responsibilities
	// //////////////////////////////////////

	/**
	 * instruct this object to clear itself out, ready for ditching
	 */
	@Override
	public final void closeMe() {
		// do the parent
		super.closeMe();

		// and the fixes
		// first ask them to close themselves
		final java.util.Iterator<Editable> it = _myContacts.iterator();
		while (it.hasNext()) {
			final Object val = it.next();
			if (val instanceof MWC.GUI.PlainWrapper) {
				final MWC.GUI.PlainWrapper pw = (MWC.GUI.PlainWrapper) val;
				pw.closeMe();
			}
		}

		// now ditch them
		_myContacts.clear();

		// and the other stuff
		_myEditor = null;
		_myHost = null;
		_myTrackName = null;

	}

	@Override
	public final int compareTo(final Plottable o) {
		final int res;
		if (o instanceof TacticalDataWrapper) {
			// check they're both of the same class (one might be sensor whilst
			// the other's tma)
			if (o.getClass().toString().equals(this.getClass().toString())) {
				// compare the names
				final TacticalDataWrapper sw = (TacticalDataWrapper) o;
				res = this.getName().compareTo(sw.getName());
			} else {
				// they're different types, point that out.
				res = -1;
			}
		} else {
			// just put it after us
			res = -1;
		}
		return res;
	}

	abstract protected PlottableWrapperWithTimeAndOverrideableColor createItem(
			PlottableWrapperWithTimeAndOverrideableColor last, PlottableWrapperWithTimeAndOverrideableColor next,
			LinearInterpolator interp2, long tNow);

	/**
	 * switch the sample rate of this track to the supplied frequency
	 *
	 * @param theVal       the step interval to use
	 * @param theStartTime the start time (micros), to control where the resamples
	 *                     fall ('on the minute')
	 */
	public void decimate(final HiResDate theVal, final long startTime) {
		final Vector<PlottableWrapperWithTimeAndOverrideableColor> newItems = new Vector<PlottableWrapperWithTimeAndOverrideableColor>();

		// get the time interval
		final long interval = theVal.getMicros();

		// round myStart time to the supplied interval
		long myStart = this.getStartDTG().getMicros();
		myStart = (myStart / interval) * interval;

		// set the start time to be the later of our start time and the provided
		// time
		final long theStartTime = Math.max(startTime, myStart);

		// long startTime = this.getStartDTG().getMicros();
		final long endTime = this.getEndDTG().getMicros();

		final Enumeration<Editable> _cuts = elements();
		if (!_cuts.hasMoreElements())
			return;

		PlottableWrapperWithTimeAndOverrideableColor _last = (PlottableWrapperWithTimeAndOverrideableColor) _cuts
				.nextElement();

		// check we've got a single point
		if (_last == null)
			return;

		// well, we found our first element - see if there's another one
		if (!_cuts.hasMoreElements())
			return;

		PlottableWrapperWithTimeAndOverrideableColor _next = (PlottableWrapperWithTimeAndOverrideableColor) _cuts
				.nextElement();

		// have we got two?
		if (_next == null)
			return;

		// right - sort out what time period we're working through
		for (long tNow = theStartTime; tNow <= endTime; tNow += theVal.getMicros()) {
			if (_next == null) {
				// hey, this really shouldn't have happened, prob ignore it
			}

			// loop through to find this item. are we looking before the first item?
			while ((_next != null) && (_next.getDTG().getMicros() < tNow)) {
				_last = _next;
				_next = null;
				if (_cuts.hasMoreElements())
					_next = (PlottableWrapperWithTimeAndOverrideableColor) _cuts.nextElement();
			}

			// have we overshot?
			if (_next == null) {
				// bugger, drop out
			} else {

				// right, we appear to be either side of the relevant item
				// interpolate the values
				// go for the freq first
				final LinearInterpolator interp = new LinearInterpolator((Watchable) _last, (Watchable) _next, tNow);

				// create a new data value
				final PlottableWrapperWithTimeAndOverrideableColor newItem = createItem(_last, _next, interp, tNow);

				// and store it.
				newItems.add(newItem);
			}
		}

		// ditch our positions
		_myContacts.clear();

		// store the new sensor items
		for (final Iterator<PlottableWrapperWithTimeAndOverrideableColor> iterator = newItems.iterator(); iterator
				.hasNext();) {
			final PlottableWrapperWithTimeAndOverrideableColor fix = iterator.next();
			this.add(fix);
		}
	}

	/**
	 * ////////////////////////////////////////
	 */

	@Override
	public final java.util.Enumeration<Editable> elements() {
		return new IteratorWrapper(_myContacts.iterator());
	}

	
	/**
	 * filter the list to the specified time period.
	 */
	@Override
	public final void filterListTo(final HiResDate start, final HiResDate end) {
		final Iterator<Editable> contactWrappers = _myContacts.iterator();
		while (contactWrappers.hasNext()) {

			/**
			 * note, we had trouble with unpredicable behaviour when testing this one. We
			 * overcame the problem by initialising the dates in the two Gregorian
			 * calendars.
			 */
			final PlottableWrapperWithTimeAndOverrideableColor fw = (PlottableWrapperWithTimeAndOverrideableColor) contactWrappers
					.next();
			final HiResDate dtg = fw.getDTG();
			if ((dtg.greaterThanOrEqualTo(start)) && (dtg.lessThanOrEqualTo(end))) {
				fw.setVisible(true);
			} else {
				fw.setVisible(false);
			}
		}

		// do we have any property listeners?
		if (getSupport() != null) {
			final Debrief.GUI.Tote.StepControl.somePeriod newPeriod = new Debrief.GUI.Tote.StepControl.somePeriod(start,
					end);
			getSupport().firePropertyChange(MWC.GenericData.WatchableList.FILTERED_PROPERTY, null, newPeriod);
		}

	}

	/**
	 * get the end DTG of this list
	 *
	 * @return the end DTG, or -1 if not time-related
	 */
	@Override
	public final HiResDate getEndDTG() {
		if (_timePeriod == null)
			recalcTimePeriod();

		final HiResDate res;
		if (_timePeriod != null) {
			res = _timePeriod.getEndDTG();
		} else {
			res = null;
		}

		return res;
	}

	/**
	 * set our host track
	 */
	@Override
	public final TrackWrapper getHost() {
		return _myHost;
	}

	/**
	 * return the set of items which fall inside the indicated period
	 */
	@Override
	public final java.util.Collection<Editable> getItemsBetween(final HiResDate start, final HiResDate end) {
		// see if we have any points
		if ((_myContacts == null) || (_myContacts.size() == 0))
			return null;

		// see if we have _any_ points in range
		if ((getStartDTG().greaterThan(end)) || (getEndDTG().lessThan(start)))
			return null;

		// hey, we can do this on our own!
		java.util.Vector<Editable> res = new java.util.Vector<Editable>(0, 1);
		final java.util.Iterator<Editable> it = _myContacts.iterator();
		boolean finished = false;
		while ((it.hasNext()) && (!finished)) {
			final PlottableWrapperWithTimeAndOverrideableColor scw = (PlottableWrapperWithTimeAndOverrideableColor) it
					.next();

			// is it visible?
			if (scw.getVisible()) {
				final HiResDate thisD = scw.getTime();
				if (thisD.lessThan(start)) {
					// hey, ditch it!
				} else if (thisD.greaterThan(end)) {
					// hey, ditch it, we must be past the end
					finished = true;
				} else {
					// this must be in range
					res.add(scw);
				}
			}
		}

		if (res.size() == 0)
			res = null;

		return res;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 *
	 * @return
	 */
	
	public final int getLineThickness() {
		return _lineWidth;
	}

	/**
	 *
	 *
	 */
	@Override
	public final String getName() {
		return _myName;
	}

	/**
	 * find out the symbol to use for plotting this list in Snail mode
	 */
	@Override
	public final MWC.GUI.Shapes.Symbols.PlainSymbol getSnailShape() {
		return null;
	}

	/**
	 * get the start DTG of this list
	 *
	 * @return the start DTG, or -1 if not time-related
	 */
	@Override
	public final HiResDate getStartDTG() {
		if (_timePeriod == null)
			recalcTimePeriod();

		final HiResDate res;
		if (_timePeriod != null) {
			res = _timePeriod.getStartDTG();
		} else {
			res = null;
		}

		return res;
	}

	/**
	 * getTrack
	 *
	 * @return the returned TrackWrapper
	 */
	public final String getTrackName() {
		return _myTrackName;
	}

	public HiResDate getVisibleFrequency() {
		return _lastVisibleFrequency;
	}

	// ///////////////////////////////////////
	// other member functions
	// ///////////////////////////////////////

	/**
	 * hasEditor
	 *
	 * @return the returned boolean
	 */
	@Override
	public final boolean hasEditor() {
		return true;
	}

	// /////////////////////////////////////////////////////////////////
	// support for WatchableList interface (required for Snail Trail plotting)
	// //////////////////////////////////////////////////////////////////

	@Override
	public final boolean hasOrderedChildren() {
		return false;
	}

	/**
	 * paint
	 *
	 * @param canvas parameter for paint
	 */
	@Override
	public final void paint(final MWC.GUI.CanvasType canvas) {
		if (!getVisible())
			return;

		// remember the current line width
		final float oldLineWidth = canvas.getLineWidth();

		// set the line width
		canvas.setLineWidth(_lineWidth);

		// sort out the alpha
		final String alphaStr = Defaults.getPreference(SensorContactWrapper.TRANSPARENCY);
		int alpha;
		if (alphaStr != null && alphaStr.length() > 0) {
			try {
				alpha = Integer.parseInt(alphaStr);
			} catch (final NumberFormatException e) {
				alpha = 255;
				e.printStackTrace();
			}
		} else {
			alpha = 255;
		}

		// trigger our child sensor contact data items to plot themselves
		final java.util.Iterator<Editable> it = _myContacts.iterator();
		while (it.hasNext()) {
			final Object next = it.next();
			final PlottableWrapperWithTimeAndOverrideableColor con = (PlottableWrapperWithTimeAndOverrideableColor) next;

			if (con.getVisible()) {
				// ok, plot it - and don't make it keep it simple, lets really go
				// for it man!
				con.paint(_myHost, canvas, false, alpha);
			}
		}

		// and restore the line width
		canvas.setLineWidth(oldLineWidth);
	}

	/**
	 * how far away are we from this point? or return -1 if it can't be calculated
	 * We don't search through our child objects, the searching algorithms do that
	 * for themselves, since we are a layer
	 */
	@Override
	public double rangeFrom(final MWC.GenericData.WorldLocation other) {
		double res = -1;
		if (!getVisible()) {
			// hey, we're invisible, return null
		} else {
			final WorldArea area = this.getBounds();

			// did we find a range? we may not have done if all of the
			// individual sensor lines are not visible
			if (area != null)
				res = area.rangeFrom(other);
		}
		return res;
	}

	/**
	 * we've forgotten our time period, recalc
	 *
	 */
	private void recalcTimePeriod() {
		if (!_myContacts.isEmpty()) {
			final PlottableWrapperWithTimeAndOverrideableColor first = (PlottableWrapperWithTimeAndOverrideableColor) _myContacts
					.first();
			final PlottableWrapperWithTimeAndOverrideableColor last = (PlottableWrapperWithTimeAndOverrideableColor) _myContacts
					.last();

			_timePeriod = new TimePeriod.BaseTimePeriod(first.getDTG(), last.getDTG());
		}
	}

	/**
	 * removeElement
	 *
	 * @param plottable parameter for removeElement
	 */
	@Override
	public final void removeElement(final MWC.GUI.Editable plottable) {
		_myContacts.remove(plottable);

		// we also need to update the start/end time
		_timePeriod = null;
	}
	
	/**
	 * removeAllElements
	 *
	 */
	public final void removeAllElements() {
		_myContacts.clear();

		// we also need to update the start/end time
		_timePeriod = null;
	}
	
	

	/**
	 * the setter function which passes through the track
	 */
	private void setFixes(final ObjectManipulator setter, final HiResDate theVal) {
		final long freq = theVal.getMicros();

		// briefly check if we are revealing/hiding all times (ie if freq is 1
		// or 0)
		if (freq == TimeFrequencyPropertyEditor.SHOW_ALL_FREQUENCY) {
			// show all of the labels
			final Enumeration<Editable> iter = elements();
			while (iter.hasMoreElements()) {
				setter.apply(iter.nextElement(), true);
			}
		} else {
			// no, we're not just blindly doing all of them. do them at the
			// correct
			// frequency

			// hide all of the labels/symbols first
			final Enumeration<Editable> enumA = elements();
			while (enumA.hasMoreElements()) {
				setter.apply(enumA.nextElement(), false);
			}

			if (freq == 0) {
				// we can ignore this, since we have just hidden all of the
				// points
			} else {

				// pass through the track setting the values

				// sort out the start and finish times
				long start_time = getStartDTG().getMicros();
				final long end_time = getEndDTG().getMicros();

				// first check that there is a valid time period between start
				// time
				// and end time
				if (start_time + freq < end_time) {
					long num = start_time / freq;

					// we need to add one to the quotient if it has rounded down
					if (start_time % freq == 0) {
						// start is at our freq, so we don't need to increment
						// it
					} else {
						num++;
					}

					// calculate new start time
					start_time = num * freq;
				} else {
					// there is not one of our 'intervals' between the start and
					// the end,
					// so use the start time
				}

				while (start_time <= end_time) {
					// right, increment the start time by one, because we were
					// getting the
					// fix immediately before the requested time
					final HiResDate thisDTG = new HiResDate(0, start_time);
					final MWC.GenericData.Watchable[] list = this.getNearestTo(thisDTG);
					// check we found some
					if (list.length > 0) {
						setter.apply(list[0], true);
					}
					// produce the next time step
					start_time += freq;
				}
			}

		}
	}

	/**
	 * set our host track
	 */
	public void setHost(final TrackWrapper host) {
		_myHost = host;
	}

	/**
	 * the line thickness (convenience wrapper around width)
	 */
	@FireReformatted
	public final void setLineThickness(final int val) {
		_lineWidth = val;
	}

	/**
	 * setName
	 *
	 * @param name parameter for setName
	 */
	@Override
	@FireReformatted
	public final void setName(final String name) {
		_myName = name;
	}

	/**
	 * setTrack
	 *
	 * @param name parameter for setTrack
	 */
	@FireReformatted
	public final void setTrackName(final String name) {
		_myTrackName = name;
	}

	/**
	 * change how frequently our data items are dislayed
	 *
	 * @param visibleFrequency
	 */
	public void setVisibleFrequency(final HiResDate visibleFrequency) {
		_lastVisibleFrequency = visibleFrequency;

		setFixes(new ObjectManipulator() {
			@Override
			public void apply(final Object subject, final boolean value) {
				final PlottableWrapperWithTimeAndOverrideableColor pl = (PlottableWrapperWithTimeAndOverrideableColor) subject;
				pl.setVisible(value);
			}
		}, visibleFrequency);

	}

	/**
	 * return the amount of data we hold
	 *
	 * @return
	 */
	public final int size() {
		return _myContacts.size();
	}

	public void trimTo(final TimePeriod period) {
		final java.util.SortedSet<Editable> newList = new java.util.TreeSet<Editable>();

		final Iterator<Editable> iter = _myContacts.iterator();
		while (iter.hasNext()) {
			final PlottableWrapperWithTimeAndOverrideableColor thisE = (PlottableWrapperWithTimeAndOverrideableColor) iter
					.next();
			if (period.contains(thisE.getTime())) {
				newList.add(thisE);
			}
		}

		// ok, copy over the items
		_myContacts.clear();

		_myContacts.addAll(newList);
	}
}
