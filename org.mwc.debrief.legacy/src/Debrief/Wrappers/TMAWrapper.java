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

// $RCSfile: TMAWrapper.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.14 $
// $Log: TMAWrapper.java,v $
// Revision 1.14  2006/09/25 14:51:16  Ian.Mayo
// Respect new "has children" property of Layers
//
// Revision 1.13  2005/06/08 08:49:37  Ian.Mayo
// Correctly reflect user preference for showing bearing lines
//
// Revision 1.12  2005/06/06 14:45:08  Ian.Mayo
// Refactor how we support tma & sensor data
//
// Revision 1.11  2005/06/06 14:00:42  Ian.Mayo
// Tidy how we calculate TMA sensor-click range
//
// Revision 1.10  2005/05/06 15:16:23  Ian.Mayo
// Allow user to hide all labels for track
//
// Revision 1.9  2005/02/28 14:57:07  Ian.Mayo
// Handle situation when we have sensor & TUA data outside track period.
//
// Revision 1.8  2005/02/22 09:31:59  Ian.Mayo
// Refactor snail plotting sensor & tma data - so that getting & managing valid data points are handled in generic fashion.  We did have two very similar implementations, tracking errors introduced after hi-res-date changes was proving expensive/unreliable.  All fine now though.
//
// Revision 1.7  2005/01/28 10:52:59  Ian.Mayo
// Fix problems where last data point not shown.
//
// Revision 1.6  2004/11/25 10:24:51  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.5  2004/11/22 13:41:06  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.4  2004/09/10 09:11:29  Ian.Mayo
// Correct prior mistaken implementation of add(Editable) - we should have just changed the signature of add(Plottable) et al
//
// Revision 1.3  2004/09/09 10:51:57  Ian.Mayo
// Provide missing methods from Layers structure.  Don't know why they had been missing for so long.  Poss disconnect between ASSET/Debrief development trees
//
// Revision 1.2  2004/09/09 10:23:15  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:49:29  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-06-25 08:49:12+01  ian_mayo
// More implementation detail
//
// Revision 1.3  2003-06-23 13:40:30+01  ian_mayo
// Handle line widths, etc.
//
// Revision 1.2  2003-06-23 08:39:17+01  ian_mayo
// Changed method signature & testing
//
// Revision 1.1  2003-06-19 16:17:47+01  ian_mayo
// Initial revision
//

package Debrief.Wrappers;

import java.awt.Color;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Iterator;

import Debrief.GUI.Tote.Painters.SnailDrawTacticalContact.PlottableWrapperWithTimeAndOverrideableColor;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;

/**
 * Class to store a series of TMA solutions in a track
 */

public final class TMAWrapper extends TacticalDataWrapper {

	// //////////////////////////////////////////////////////////////////////////
	// embedded class, used for editing the projection
	// //////////////////////////////////////////////////////////////////////////
	/**
	 * the definition of what is editable about this object
	 */
	public final class SolutionInfo extends MWC.GUI.Editable.EditorType {

		/**
		 * constructor for editable details of a set of Layers
		 *
		 * @param data the Layers themselves
		 */
		public SolutionInfo(final TMAWrapper data) {
			super(data, data.getName(), "TMA Solution");
		}

		/**
		 * The things about these Layers which are editable. We don't really use this
		 * list, since we have our own custom editor anyway
		 *
		 * @return property descriptions
		 */
		@Override
		public final PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] res = { prop("Name", "the name for this tma solution"),
						prop("Visible", "whether this solution data is visible"),
						displayProp("LineThickness", "Line thickness", "the thickness to draw these solution lines"),
						displayProp("DefaultColor", "Default color",
								"the default colour to plot this set of solution data"),
						displayProp("ShowBearingLines", "Show bearing lines", "whether to show bearing lines to TUAs"),
						displayProp("ShowLabels", "Show labels", "whether to show labels on TUAs") };

				res[2].setPropertyEditorClass(MWC.GUI.Properties.LineWidthPropertyEditor.class);

				return res;
			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	// //////////////////////////////////////
	// member variables
	// //////////////////////////////////////

	static public final class testSolutions extends junit.framework.TestCase {
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testSolutions(final String val) {
			super(val);
		}

		public final void testDuplicates() {
			// ok, create the test object
			final TMAWrapper sensor = new TMAWrapper("tester");

			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
					null, null, null, null));

			cal.set(2001, 10, 4, 4, 4, 23);
			sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
					null, null, null, null));

			cal.set(2001, 10, 4, 4, 4, 24);
			sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
					null, null, null, null));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
					null, null, null, null));

			cal.set(2001, 10, 4, 4, 4, 25);
			sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
					null, null, null, null));

			cal.set(2001, 10, 4, 4, 4, 01);
			sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
					null, null, null, null));

			cal.set(2001, 10, 4, 4, 4, 05);
			sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
					null, null, null, null));

			cal.set(2001, 10, 4, 4, 4, 55);
			sensor.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), -1, 0, 0, 0, 0,
					null, null, null, null));

			// so, we've now build up the list
			// check it has the correct quantity
			assertEquals("Count of items", 8, sensor._myContacts.size());

			// check the correct number get returned
			cal.set(2001, 10, 4, 4, 4, 25);
			final Watchable[] list = sensor.getNearestTo(new HiResDate(cal.getTime().getTime()));
			assertEquals("after end of data", 2, list.length);

		}

		public void testMultipleContacts() {
			final TMAContactWrapper sc1 = new TMAContactWrapper("aaa", "bbb", new HiResDate(12), 0, 0, 0, 0, 0, null,
					"first", null, null);
			final TMAContactWrapper sc2 = new TMAContactWrapper("aaa", "bbb", new HiResDate(13), 0, 0, 0, 0, 0, null,
					"first", null, null);
			final TMAContactWrapper sc3 = new TMAContactWrapper("aaa", "bbb", new HiResDate(12), 0, 0, 0, 0, 0, null,
					"first", null, null);
			final TMAContactWrapper sc4 = new TMAContactWrapper("aaa", "bbb", new HiResDate(15), 0, 0, 0, 0, 0, null,
					"first", null, null);

			final TMAWrapper sw = new TMAWrapper("bbb");
			sw.add(sc1);
			sw.add(sc2);
			sw.add(sc3);
			sw.add(sc4);

			assertEquals("four contacts loaded", 4, sw._myContacts.size());

			// check we can delete from it
			sw.removeElement(sc3);

			assertEquals("now only three contacts loaded", 3, sw._myContacts.size());

		}

		public final void testValues() {
			// ok, create the test object
			final TMAWrapper solution = new TMAWrapper("tester");

			final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

			int solutionCounter = 1;

			// and create the list of sensor contact data items
			cal.set(2001, 10, 4, 4, 4, 0);
			final long start_time = cal.getTime().getTime();
			solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
					0, null, "sol:" + solutionCounter++, null, null));

			cal.set(2001, 10, 4, 4, 4, 23);
			solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
					0, null, "sol:" + solutionCounter++, null, null));

			cal.set(2001, 10, 4, 4, 4, 25);
			solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
					0, null, "sol:" + solutionCounter++, null, null));

			cal.set(2001, 10, 4, 4, 4, 27);
			solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
					0, null, "sol:" + solutionCounter++, null, null));

			cal.set(2001, 10, 4, 4, 4, 02);
			solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
					0, null, "sol:" + solutionCounter++, null, null));

			cal.set(2001, 10, 4, 4, 4, 01);
			solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
					0, null, "sol:" + solutionCounter++, null, null));

			cal.set(2001, 10, 4, 4, 4, 05);
			solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
					0, null, "sol:" + solutionCounter++, null, null));

			cal.set(2001, 10, 4, 4, 4, 55);
			final long end_time = cal.getTime().getTime();
			solution.add(new TMAContactWrapper("tester", "tester", new HiResDate(cal.getTime().getTime()), 0, 0, 0, 0,
					0, null, null, null, null));

			// so, we've now build up the list
			// check it has the correct quantity
			assertTrue("Count of items", (solution._myContacts.size() == 8));

			// check the outer limits
			final HiResDate start = solution.getStartDTG();
			final HiResDate end = solution.getEndDTG();
			assertTrue("first time", (start_time == start.getDate().getTime()));
			assertTrue("last time", (end_time == end.getDate().getTime()));

			// //////////////////////////////////////////////////////////////////////
			// finding the nearest entry
			cal.set(2001, 10, 4, 4, 4, 05);
			Watchable[] list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
			TMAContactWrapper nearest = (TMAContactWrapper) list[0];
			assertEquals("Nearest matching fix", nearest.getDTG().getDate().getTime(), cal.getTime().getTime());

			final java.util.Calendar cal_other = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);
			cal_other.set(2001, 10, 4, 4, 4, 03);
			list = solution.getNearestTo(new HiResDate(cal_other.getTime().getTime()));
			nearest = (TMAContactWrapper) list[0];
			assertTrue("Nearest or greater than fix",
					(nearest.getDTG().getDate().getTime() == cal.getTime().getTime()));

			// ///////////////////////////////////////////////////////////////////
			// filter the list
			cal.set(2001, 10, 4, 4, 4, 22);
			cal_other.set(2001, 10, 4, 4, 4, 25);

			// ////////////////////////////////////////////////////////////////////////
			// do the filter
			solution.filterListTo(new HiResDate(cal.getTime().getTime()), new HiResDate(cal_other.getTime().getTime()));

			// see how many remain visible
			java.util.Enumeration<Editable> iter = solution.elements();
			int counter = 0;
			while (iter.hasMoreElements()) {
				final TMAContactWrapper contact = (TMAContactWrapper) iter.nextElement();
				if (contact.getVisible())
					counter++;
			}
			// check that the correct number are visible
			assertTrue("Correct filtering of list", (counter == 2));

			// clear the filter
			solution.filterListTo(solution.getStartDTG(), solution.getEndDTG());
			// see how many remain visible
			iter = solution.elements();
			counter = 0;
			while (iter.hasMoreElements()) {
				final TMAContactWrapper contact = (TMAContactWrapper) iter.nextElement();
				if (contact.getVisible())
					counter++;
			}
			// check that the correct number are visible
			assertTrue("Correct removal of list filter", (counter == 8));

			// //////////////////////////////////////////////////////
			// get items between
			java.util.Collection<Editable> res = solution.getItemsBetween(new HiResDate(cal.getTime().getTime()),
					new HiResDate(cal_other.getTime().getTime()));
			assertTrue("get items between", (res.size() == 2));

			// do recheck, since this time we will be resetting the working variables,
			// rather and creating them
			cal.set(2001, 10, 4, 4, 4, 5);
			cal_other.set(2001, 10, 4, 4, 4, 27);
			res = solution.getItemsBetween(new HiResDate(cal.getTime().getTime()),
					new HiResDate(cal_other.getTime().getTime()));
			assertEquals("recheck get items between:" + res.size(), 4, res.size());

			// and show all of the data
			res = solution.getItemsBetween(solution.getStartDTG(), solution.getEndDTG());
			assertTrue("recheck get items between:" + res.size(), (res.size() == 8));

			// /////////////////////////////////////////////////////////
			// test the position related stuff
			final TrackWrapper track = new TrackWrapper();

			// and add the fixes
			cal.set(2001, 10, 4, 4, 4, 0);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new WorldLocation(2.0, 2.0, 0.0), 12, 12)));

			cal.set(2001, 10, 4, 4, 4, 01);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new WorldLocation(2.0, 2.25, 0.0), 12, 12)));

			cal.set(2001, 10, 4, 4, 4, 02);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new WorldLocation(2.0, 2.5, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 05);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new WorldLocation(2.0, 2.75, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 23);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new WorldLocation(2.25, 2.0, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 25);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new WorldLocation(2.5, 2.0, 0.0), 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 28);
			final WorldLocation theLoc = new WorldLocation(2.75d, 2.0, 0.0);
			track.addFix(new FixWrapper(
					new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0), theLoc, 12, 12)));
			cal.set(2001, 10, 4, 4, 4, 55);
			track.addFix(new FixWrapper(new MWC.TacticalData.Fix(new HiResDate(cal.getTime().getTime(), 0),
					new WorldLocation(2.25, 2.25, 0.0), 12, 12)));

			// ok, put the sensor data into the track
			track.add(solution);

			// now find the location of an item, any item!
			cal.set(2001, 10, 4, 4, 4, 27);
			list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
			nearest = (TMAContactWrapper) list[0];
			final WorldLocation theCentre = nearest.getCentre(track);
			assertEquals("first test", new WorldLocation(2.75, 2.0, 0.0), theCentre);

			// ah-ha! what about a contact between two fixes
			cal.set(2001, 10, 4, 4, 4, 26);
			list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
			nearest = (TMAContactWrapper) list[0];
			assertEquals("test mid way", nearest.getCentre(track), new WorldLocation(2.75, 2.0, 0.0));

			// ok, that was half-way, what making it nearer to one of the fixes
			cal.set(2001, 10, 4, 4, 4, 25);
			list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
			nearest = (TMAContactWrapper) list[0];
			assertEquals("test nearer first point", nearest.getCentre(track), (new WorldLocation(2.5, 2.0, 0.0)));

			// start point?
			cal.set(2001, 10, 4, 4, 4, 0);
			list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
			nearest = (TMAContactWrapper) list[0];
			assertEquals("test start point", nearest.getCentre(track), new WorldLocation(2.0, 2.0, 0.0));

			// end point?
			cal.set(2001, 10, 4, 4, 4, 55);
			list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
			nearest = (TMAContactWrapper) list[0];
			assertEquals("test end point", nearest.getCentre(track), new WorldLocation(2.25, 2.25, 0.0));

			// before start of track data?
			cal.set(2001, 10, 4, 4, 3, 0);
			list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
			assertEquals("before range of data", list.length, 0);

			// after end of track data?
			cal.set(2001, 10, 4, 4, 7, 0);
			list = solution.getNearestTo(new HiResDate(cal.getTime().getTime()));
			assertEquals("after end of data", 1, list.length);

		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		final testSolutions ts = new testSolutions("Ian");
		ts.testDuplicates();
		ts.testValues();
	}

	/**
	 * whether to show bearing lines to TUAs
	 *
	 */
	private boolean _showBearingLines = true;

	// //////////////////////////////////////
	// member methods to meet plain wrapper responsibilities
	// //////////////////////////////////////

	// //////////////////////////////////////
	// member methods to meet Layer responsibilities

	/**
	 * whether to show labels in TUAs
	 *
	 */
	private boolean _showLabels = true;

	/**
	 * more optimisatons
	 */
	private TMAContactWrapper nearestSolution;

	// //////////////////////////////////////
	// constructors
	/**
	 * ////////////////////////////////////////
	 */
	public TMAWrapper(final String title) {
		super(title);
	}

	/**
	 * add
	 *
	 * @param plottable parameter for add
	 */
	@Override
	public final void add(final Editable plottable) {
		// check it's a tma contact entry
		if (plottable instanceof TMAContactWrapper) {
			_myContacts.add(plottable);

			final TMAContactWrapper scw = (TMAContactWrapper) plottable;
			final HiResDate thisT = scw.getDTG();

			// maintain our time period
			if (_timePeriod == null)
				_timePeriod = new TimePeriod.BaseTimePeriod(thisT, thisT);
			else
				_timePeriod.extend(scw.getDTG());
		}
	}

	/**
	 * create a new instance of an entity of this type, interpolated between the
	 * supplied sample objects
	 *
	 */
	@Override
	protected PlottableWrapperWithTimeAndOverrideableColor createItem(
			final PlottableWrapperWithTimeAndOverrideableColor last,
			final PlottableWrapperWithTimeAndOverrideableColor next, final LinearInterpolator interp, final long tNow) {
		final TMAContactWrapper _next = (TMAContactWrapper) next;
		final TMAContactWrapper _last = (TMAContactWrapper) last;

		final double courseDegs = MWC.Algorithms.Conversions
				.Rads2Degs(interp.interp(_last.getCourse(), _next.getCourse()));
		final double speedKts = interp.interp(_last.getSpeed(), _next.getSpeed());
		final double depthM = interp.interp(_last.getDepth(), _next.getDepth());

		final double minima = interp.interp(_last.getMinima().getValueIn(WorldDistance.DEGS),
				_next.getMinima().getValueIn(WorldDistance.DEGS));
		final double maxima = interp.interp(_last.getMaxima().getValueIn(WorldDistance.DEGS),
				_next.getMaxima().getValueIn(WorldDistance.DEGS));
		final double orient = interp.interp(_last.getOrientation(), _next.getOrientation());
		final EllipseShape theEllipse = new EllipseShape(new WorldLocation(2,2,0), orient, new WorldDistance(maxima, WorldDistance.DEGS),
				new WorldDistance(minima, WorldDistance.DEGS));

		// do we have an origin?
		WorldLocation origin = null;
		double rangeYds = 0, bearingDegs = 0;
		if ((_last.getOrigin() != null) && (_next.getOrigin() != null)) {
			final double orLat = interp.interp(_last.getOrigin().getLat(), _next.getOrigin().getLat());
			final double orLong = interp.interp(_last.getOrigin().getLong(), _next.getOrigin().getLong());
			origin = new WorldLocation(orLat, orLong, 0);
		} else if ((_last.getRange() != null) && (_next.getRange() != null)) {
			// yes we have range?
			rangeYds = interp.interp(_last.getRange().getValueIn(WorldDistance.YARDS),
					_last.getRange().getValueIn(WorldDistance.YARDS));

			final double bearingRads = interp.interp(_last.getBearingRads(), _next.getBearingRads());
			bearingDegs = MWC.Algorithms.Conversions.Rads2Degs(bearingRads);
		}

		// now, go create the new data item
		// right, do we have an origin?
		TMAContactWrapper newS = null;
		if (origin != null) {
			newS = new TMAContactWrapper(_last.getSolutionName(), _last.getTrackName(), new HiResDate(0, tNow), origin,
					courseDegs, speedKts, depthM, _last.getActualColor(), _last.getLabel(), theEllipse,
					_last.getSymbol());
		} else {
			newS = new TMAContactWrapper(_last.getSolutionName(), _last.getTrackName(), new HiResDate(0, tNow),
					rangeYds, bearingDegs, courseDegs, speedKts, depthM, _last.getActualColor(), _last.getLabel(),
					theEllipse, _last.getSymbol());
		}

		return newS;
	}

	/**
	 * the real getBounds object, which uses properties of the parent
	 */
	@Override
	public final WorldArea getBounds() {
		// we no longer just return the bounds of the track, because a portion
		// of the track may have been made invisible.
		// instead, we will pass through the full dataset and find the outer bounds
		// of the visible area
		WorldArea res = null;

		if (!getVisible()) {
			// hey, we're invisible, return null
		} else {
			final Iterator<Editable> it = this._myContacts.iterator();
			while (it.hasNext()) {
				final TMAContactWrapper fw = (TMAContactWrapper) it.next();

				// is this point visible?
				if (fw.getVisible()) {

					// get the bounds of this wrapper
					final WorldArea thisA = fw.getBounds();

					// has our data been initialised?
					if (res == null) {
						if (thisA != null) {
							// no, initialise it
							res = new WorldArea(thisA);
						}
					} else {
						// yes, extend to include the new area
						res.extend(fw.getCentre(_myHost));
						res.extend(fw.getSensorEnd(_myHost));
					}
				}
			}
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
	public Color getDefaultColor() {
		return super.getColor();
	}

	/**
	 * getInfo
	 *
	 * @return the returned MWC.GUI.Editable.EditorType
	 */
	@Override
	public final MWC.GUI.Editable.EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new SolutionInfo(this);

		return _myEditor;
	}

	/**
	 * get the watchable in this list nearest to the specified DTG - we take most of
	 * this processing from the similar method in TrackWrappper
	 *
	 * @param DTG the DTG to search for
	 * @return the nearest Watchable
	 */
	@Override
	public final Watchable[] getNearestTo(final HiResDate DTG) {

		/**
		 * we need to end up with a watchable, not a fix, so we need to work our way
		 * through the fixes
		 */
		Watchable[] res = new Watchable[] {};

		// check that we do actually contain some data
		if (_myContacts.size() == 0)
			return res;

		// see if this is the DTG we have just requestsed
		if ((DTG.equals(lastDTG)) && (lastContact != null)) {
			res = lastContact;
		} else {
			// see if this DTG is inside our data range
			// in which case we will just return null
			final TMAContactWrapper theFirst = (TMAContactWrapper) _myContacts.first();
			final TMAContactWrapper theLast = (TMAContactWrapper) _myContacts.last();

			if ((DTG.greaterThanOrEqualTo(theFirst.getDTG())) && (DTG.lessThanOrEqualTo(theLast.getDTG()))) {
				// yes it's inside our data range, find the first fix
				// after the indicated point

				// see if we have to create our local temporary fix
				if (nearestSolution == null) {
					nearestSolution = new TMAContactWrapper();
					nearestSolution.setDTG(DTG);
				} else
					nearestSolution.setDTG(DTG);

				// get the data..
				final java.util.Vector<TMAContactWrapper> list = new java.util.Vector<TMAContactWrapper>(0, 1);
				boolean finished = false;
				final Iterator<Editable> it = _myContacts.iterator();
				while ((it.hasNext()) && (!finished)) {
					final TMAContactWrapper scw = (TMAContactWrapper) it.next();
					final HiResDate thisD = scw.getTime();
					if (thisD.lessThan(DTG)) {
						// before it, ignore!
					} else if (thisD.greaterThan(DTG)) {
						// hey, it's a possible - if we haven't found an exact match
						if (list.size() == 0) {
							list.add(scw);
						} else {
							// hey, we're finished!
							finished = true;
						}
					} else {
						// hey, it must be at the same time!
						list.add(scw);
					}

				}

				if (!list.isEmpty()) {
					final Watchable[] dummy = new Watchable[] { null };
					res = list.toArray(dummy);
				}
			} else if (DTG.greaterThanOrEqualTo(theLast.getDTG())) {
				// is it after the last one? If so, just plot the last one. This helps
				// us when we're doing snail trails.
				final java.util.Vector<TMAContactWrapper> list = new java.util.Vector<TMAContactWrapper>(0, 1);
				list.add(theLast);
				final MWC.GenericData.Watchable[] dummy = new MWC.GenericData.Watchable[] { null };
				res = list.toArray(dummy);
			}

			// and remember this fix
			lastContact = res;
			lastDTG = DTG;
		}

		return res;

	}

	public boolean getShowBearingLines() {
		return _showBearingLines;
	}

	public boolean getShowLabels() {
		return _showLabels;
	}

	/**
	 * just pass the property onto the parent
	 *
	 * @param defaultColor
	 */
	public void setDefaultColor(final Color defaultColor) {
		super.setColor(defaultColor);
	}

	public void setShowBearingLines(final boolean showBearingLines) {
		// store the val
		setUnderlyingBearingLineVisibility(showBearingLines);

		// also reset the children's bearing line data
		final Iterator<Editable> iter = _myContacts.iterator();
		while (iter.hasNext()) {
			final TMAContactWrapper nextSol = (TMAContactWrapper) iter.next();
			nextSol.clearLineVisibleFlag();
		}
	}

	// /////////////////////////////////////////////////////////////////
	// support for WatchableList interface (required for Snail Trail plotting)
	// //////////////////////////////////////////////////////////////////

	public void setShowLabels(final boolean showLabels) {
		// store the val
		_showLabels = showLabels;

		// also reset the children's bearing line data
		final Iterator<Editable> iter = _myContacts.iterator();
		while (iter.hasNext()) {
			final TMAContactWrapper nextSol = (TMAContactWrapper) iter.next();
			nextSol.setLabelVisible(showLabels);
		}
	}

	/**
	 * utility method which stores the show bearing lines value, but doesn't
	 * override (clear) the value in the children
	 *
	 * @param showBearingLines yes/no
	 */
	public void setUnderlyingBearingLineVisibility(final boolean showBearingLines) {
		this._showBearingLines = showBearingLines;
	}

	// ////////////////////////////////////////////////////
	// nested class for testing
	// /////////////////////////////////////////////////////

	/**
	 * utility method which stores the show labels value, but doesn't override
	 * (clear) the value in the children
	 *
	 * @param showLabels yes/no
	 */
	public void setUnderlyingLabelVisibility(final boolean showLabels) {
		this._showLabels = showLabels;
	}

	/**
	*/

	@Override
	public final String toString() {
		return "TMA:" + getName() + " (" + _myContacts.size() + " items)";
	}

	public boolean isEmpty() {
		return _myContacts==null?true:this._myContacts.isEmpty();
	}
}
