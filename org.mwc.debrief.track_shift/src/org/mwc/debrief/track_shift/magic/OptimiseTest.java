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
package org.mwc.debrief.track_shift.magic;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.mockito.Mockito;
import org.mwc.cmap.core.interfaces.IControllableViewport;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.loaders.xml_handlers.DebriefEclipseXMLReaderWriter;
import org.mwc.debrief.track_shift.views.StackedDotHelper;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.Doublet;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import flanagan.math.Minimisation;
import flanagan.math.MinimisationFunction;

public class OptimiseTest
{
	private static final String ONLY_ONE_TRACK = "Only one whole Track may be selected for the optimisation";
	private static final String ONLY_SECONDARY = "Optimisation is only supported for denoted Secondary Track, or it's child segments";
	private static final String INVALID_SELECTION = "Only Track or Track Segment items may be selected";
	private static final String NO_VALID_SEL = "Please select a single Track, or multiple Track Segments from the Outline View to optimise";
	final static private String NO_MIXED_ITEMS = "Sorry, you can't mix Track and Track-Segment items in the optimisation";

	public static class testMe extends TestCase
	{
		public void testDraggables()
		{
			Vector<EditableWrapper> items = new Vector<EditableWrapper>();
			Vector<DraggableItem> resList = new Vector<DraggableItem>();
			TrackWrapper theTrack = new TrackWrapper();
			items.add(new EditableWrapper(theTrack));
			items.add(new EditableWrapper(new TrackSegment(TrackSegment.ABSOLUTE)));
			IStructuredSelection sel = new StructuredSelection(items);

			assertEquals("data in there", 2, sel.size());
			String res = getDraggables(resList, sel, theTrack);

			assertEquals("failed, mixed", 0, resList.size());
			assertEquals("failed, mixed", NO_MIXED_ITEMS, res);

			// try an empty list
			items = new Vector<EditableWrapper>();
			resList = new Vector<DraggableItem>();
			sel = new StructuredSelection(items);

			assertEquals("no data in there", 0, sel.size());
			res = getDraggables(resList, sel, theTrack);

			assertEquals("failed, mixed", 0, resList.size());
			assertEquals("failed, mixed", NO_VALID_SEL, res);

			// swap the items around
			items = new Vector<EditableWrapper>();
			resList = new Vector<DraggableItem>();
			theTrack = new TrackWrapper();
			items.add(new EditableWrapper(new TrackSegment(TrackSegment.ABSOLUTE)));
			items.add(new EditableWrapper(theTrack));
			sel = new StructuredSelection(items);

			assertEquals("data in there", 2, sel.size());
			res = getDraggables(resList, sel, theTrack);

			assertEquals("failed, mixed", 0, resList.size());
			assertEquals("failed, mixed", NO_MIXED_ITEMS, res);

			// try a working list
			items = new Vector<EditableWrapper>();
			resList = new Vector<DraggableItem>();
			theTrack = new TrackWrapper();
			items.add(new EditableWrapper(new TrackSegment(TrackSegment.ABSOLUTE)));
			items.add(new EditableWrapper(new TrackSegment(TrackSegment.ABSOLUTE)));
			sel = new StructuredSelection(items);

			assertEquals("data in there", 2, sel.size());
			res = getDraggables(resList, sel, theTrack);

			assertEquals("worked, found 2", 2, resList.size());
			assertEquals("worked, no errors", null, res);

			// try two tracks
			items = new Vector<EditableWrapper>();
			resList = new Vector<DraggableItem>();
			theTrack = new TrackWrapper();
			items.add(new EditableWrapper(theTrack));
			items.add(new EditableWrapper(new TrackWrapper()));
			sel = new StructuredSelection(items);

			assertEquals("data in there", 2, sel.size());
			res = getDraggables(resList, sel, theTrack);

			assertEquals("failed, two tracks", 0, resList.size());
			assertEquals("failed, two tracks", ONLY_ONE_TRACK, res);

		}

		// TODO FIX-TEST
		public void testLoad() throws FileNotFoundException
		{
			// get some data
			final Layers layers = getTheData();

			assertNotNull("should have loaded data", layers);

			// ok, sort out the two tracks
			assertEquals("wrong num tracks", 2, layers.size());

			// ok, go for the primary
			final Layer layerPri = layers.elementAt(0);
			final Layer layerSec = layers.elementAt(1);

			final TrackWrapper pri = (TrackWrapper) layerPri;
			final TrackWrapper sec = (TrackWrapper) layerSec;
			
			List<TrackWrapper> primaries = new ArrayList<TrackWrapper>();
			primaries.add(pri);

			assertEquals("NONSUCH", pri.getName());
			assertEquals("TMA_TGT", sec.getName());

			// can we get some doublets?
			final TreeSet<Doublet> doublets = StackedDotHelper.getDoublets(primaries, sec, true,
					true, false);

			assertNotNull("found some", doublets);
			assertEquals("right num", 18, doublets.size());

			// Create instance of Minimisation
			final Minimisation min = new Minimisation();
			final MinimisationFunction funct = new TryOffsetFunction(doublets);

			// initial estimates
			final double[] start =
			{ 0, 0 };

			// initial step sizes
			final double[] step =
			{ 20, 400 };

			// convergence tolerance
			final double ftol = 1e-8;

			// set the min/max bearing
			min.addConstraint(0, -1, 0d);
			min.addConstraint(0, 1, 360d);

			// set the min/max ranges
			min.addConstraint(1, -1, 0d);
			min.addConstraint(1, 1, 6000d);

			// Nelder and Mead minimisation procedure
			min.nelderMead(funct, start, step, ftol, 500);

			// get the results out
			final double[] param = min.getParamValues();

			final double bearing = param[0];
			final double range = param[1];

			System.err.println("answer is:" + bearing + " degs" + range + "m");
		}

		public void testPermutations()
		{

			final SensorWrapper sensor = new SensorWrapper("nane");
			final SensorContactWrapper sensorCut = new SensorContactWrapper("name", new HiResDate(1000),
					null, 100d, new WorldLocation(1d, 1d, 1d), null, null, 1, null);
			sensor.add(sensorCut);
			final TrackSegment parent = null;
			final FixWrapper hostFix = null;
			final WorldLocation theLoc = new WorldLocation(0, 0, 0);
			final Fix newFix = new Fix(new HiResDate(1000), theLoc, 0, 0);
			final FixWrapper targetFix = new FixWrapper(newFix);
			final Doublet dt = new Doublet(sensorCut, targetFix, parent, hostFix);

			final TreeSet<Doublet> doublets = new TreeSet<Doublet>();
			doublets.add(dt);

			// Create instance of Minimisation
			final Minimisation min = new Minimisation();
			final MinimisationFunction funct = new TryOffsetFunction(doublets);

			// initial estimates
			final double[] start =
			{ 0, 0 };

			// initial step sizes
			final double[] step =
			{ 20, 400 };

			// convergence tolerance
			final double ftol = 1e-8;

			// set the min/max bearing
			min.addConstraint(0, -1, 0d);
			min.addConstraint(0, 1, 360d);

			// set the min/max ranges
			min.addConstraint(1, -1, 0d);
			min.addConstraint(1, 1, 6000d);

			// Nelder and Mead minimisation procedure
			min.nelderMead(funct, start, step, ftol, 500);

			// get the results out
			final double[] param = min.getParamValues();

			final double bearing = param[0];
			final double range = param[1];

			System.err.println("answer is:" + bearing + " degs" + range + "m");

			// assertEquals("wrong bearing", Math.PI/2, bearing / 180 * Math.PI, 0.001
			// );
			// assertEquals("wrong range", 0.001, MWC.Algorithms.Conversions.Degs2m(
			// range), 0.001 );
		}

		public void testDummyPermutations()
		{
			// Create instance of Minimisation
			final Minimisation min = new Minimisation();
			final MinimisationFunction funct = new DummyOffsetFunction(100, 2000);

			// initial estimates
			final double[] start =
			{ 0, 0 };

			// initial step sizes
			final double[] step =
			{ 20, 400 };

			// convergence tolerance
			final double ftol = 1e-8;

			// set the min/max bearing
			min.addConstraint(0, -1, 0d);
			min.addConstraint(0, 1, 360d);

			// set the min/max ranges
			min.addConstraint(1, -1, 0d);
			min.addConstraint(1, 1, 6000d);

			// Nelder and Mead minimisation procedure
			min.nelderMead(funct, start, step, ftol, 500);

			// get the results out
			final double[] param = min.getParamValues();

			final double bearing = param[0];
			final double range = param[1];

			System.err.println("answer is:" + bearing + " degs" + range + "m");

			assertEquals("wrong bearing", 100, bearing, 0.0001);
			assertEquals("wrong range", 2000, range, 0.0001);
		}

		public void testShiftSingle() throws FileNotFoundException
		{
			final SensorContactWrapper sensor = new SensorContactWrapper("track", new HiResDate(1100), 
          null, 100d, null, null, null, Color.RED, "label", 1, "Some name");
			SensorWrapper sensorW = new SensorWrapper("sensor");
			sensorW.add(sensor);
			final TrackSegment parent = null;
			final FixWrapper hostFix = null;
			final WorldLocation theLoc = new WorldLocation(0, 0, 0);
			final Fix newFix = new Fix(new HiResDate(1000), theLoc, 0, 0);
			final FixWrapper targetFix = new FixWrapper(newFix);
			final Doublet dt = new Doublet(sensor, targetFix, parent, hostFix);

			final TreeSet<Doublet> doublets = new TreeSet<Doublet>();
			doublets.add(dt);

			assertEquals("right num", 1, doublets.size());
			WorldArea coverage = areaFor(doublets);
			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			final TryOffsetFunction func = new TryOffsetFunction(doublets);

			WorldVector thisOffset = new WorldVector(0, 0, 0);
			TreeSet<Doublet> res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);

			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			// apply some kind of offset - move it 1 deg north
			thisOffset = new WorldVector(0, 1, 0);
			res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);
			assertEquals("right tl lat", 1.0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			// apply some kind of offset - move it 1 deg north
			thisOffset = new WorldVector(Math.PI / 2, 1, 0);
			res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);
			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 1, coverage.getTopLeft().getLong(), 0.001);
		}

		public void testShiftMultiple() throws FileNotFoundException
		{
      final SensorContactWrapper sensor = new SensorContactWrapper("name", new HiResDate(1000),
          null, 100d, new WorldLocation(1d, 1d, 1d), null, null, 1, null);
      final SensorWrapper sw = new SensorWrapper("sensor");
      sw.add(sensor);
			final TrackSegment parent = null;
			final FixWrapper hostFix1 = new FixWrapper(new Fix(new HiResDate(1000), null,
					0, 0));

			final WorldLocation theLoc = new WorldLocation(0, 0, 0);
			final Fix newFix = new Fix(new HiResDate(1000), theLoc, 0, 0);
			final FixWrapper targetFix = new FixWrapper(newFix);
			final WorldLocation theLoc2 = new WorldLocation(0, 0, 0);
			final Fix newFix2 = new Fix(new HiResDate(2000), theLoc2, 0, 0);
			final FixWrapper targetFix2 = new FixWrapper(newFix2);
			final Doublet dt = new Doublet(sensor, targetFix, parent, hostFix1);
			final Doublet dt2 = new Doublet(sensor, targetFix2, parent, hostFix1);

			final TreeSet<Doublet> doublets = new TreeSet<Doublet>();
			doublets.add(dt);
			doublets.add(dt2);

			assertEquals("right num", 2, doublets.size());
			WorldArea coverage = areaFor(doublets);
			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			WorldVector thisOffset = new WorldVector(0, 0, 0);
			final TryOffsetFunction func = new TryOffsetFunction(doublets);
			TreeSet<Doublet> res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);

			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			// apply some kind of offset - move it 1 deg north
			thisOffset = new WorldVector(0, 1, 0);
			res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);
			assertEquals("right tl lat", 1.0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 0, coverage.getTopLeft().getLong(), 0.001);

			// apply some kind of offset - move it 1 deg north
			thisOffset = new WorldVector(Math.PI / 2, 1, 0);
			res = func.shiftDoublets(thisOffset);
			coverage = areaFor(res);
			assertEquals("right tl lat", 0, coverage.getTopLeft().getLat(), 0.001);
			assertEquals("right tl lon", 1, coverage.getTopLeft().getLong(), 0.001);
		}

		private WorldArea areaFor(final TreeSet<Doublet> doublets)
		{
			WorldArea res = null;
			final Iterator<Doublet> iter = doublets.iterator();
			while (iter.hasNext())
			{
				final Doublet doublet = (Doublet) iter.next();
				final WorldLocation loc = doublet.getTarget().getLocation();
				if (res == null)
					res = new WorldArea(loc, loc);
				else
					res.extend(loc);
			}
			return res;
		}

	}

	private static Layers getTheData() throws FileNotFoundException
	{
		final DebriefEclipseXMLReaderWriter reader = new DebriefEclipseXMLReaderWriter();
		final Layers res = new Layers();
		final String path = "../org.mwc.debrief.track_shift/src/org/mwc/debrief/track_shift/magic/";
		final String fName = "midflow2.xml";
		final InputStream is = new FileInputStream(path + fName);
		final IControllableViewport view = Mockito.mock(IControllableViewport.class);
		final PlotEditor plot = Mockito.mock(PlotEditor.class);
		reader.importThis(fName, is, res, view, plot);
		return res;
	}

	public static class TryOffsetFunction implements MinimisationFunction
	{
		private final TreeSet<Doublet> _doublets;

		public TryOffsetFunction(final TreeSet<Doublet> doublets)
		{
			_doublets = doublets;
		}

		@Override
		public double function(final double[] param)
		{
			// ok, generate bearing
			final double brgDegs = param[0];
			final double brgRads = MWC.Algorithms.Conversions.Degs2Rads(brgDegs);
			final double rngM = param[1];
			final double rngDegs = MWC.Algorithms.Conversions.m2Degs(rngM);

			// and the world vector
			final WorldVector offset = new WorldVector(brgRads, rngDegs, 0);

			// get shifting
			final TreeSet<Doublet> newD = shiftDoublets(offset);

			// do the math
			double res = 0;
			for (final Iterator<Doublet> iterator = newD.iterator(); iterator.hasNext();)
			{
				final Doublet thisD = iterator.next();
				final double measuredBearing = thisD.getMeasuredBearing();
				final double calculatedBearing = thisD.getCalculatedBearing(null, null);
				final double thisError = thisD.calculateBearingError(measuredBearing,
						calculatedBearing);
				final double score = Math.abs(thisError * thisError);

				res += score;
			}
			
			// do the calc
			return res;
		}

		/**
		 * produce a new set of positions by applying an offset to ourst
		 * 
		 * @param thisOffset
		 * @return
		 */
		private TreeSet<Doublet> shiftDoublets(final WorldVector thisOffset)
		{
			final TreeSet<Doublet> res = new TreeSet<Doublet>();

			// loop through the doublets
			final Iterator<Doublet> iter = _doublets.iterator();
			while (iter.hasNext())
			{
				final Doublet doublet = (Doublet) iter.next();

				// get this fix
				final FixWrapper fix = doublet.getTarget();

				// clone it
				final FixWrapper newF = new FixWrapper(fix.getFix().makeCopy());

				// move it
				final WorldLocation newLoc = new WorldLocation(newF.getLocation());
				newLoc.addToMe(thisOffset);
				newF.setLocation(newLoc);

				// create a new doublet
				final Doublet newD = new Doublet(doublet.getSensorCut(), newF,
						doublet.getTargetTrack(), doublet.getHost());

				res.add(newD);
			}

			return res;
		}

	}

	public static class DummyOffsetFunction implements MinimisationFunction
	{

		private final double _val1;
		private final double _val2;

		public DummyOffsetFunction(final double val1, final double val2)
		{
			_val1 = val1;
			_val2 = val2;
		}

		@Override
		public double function(final double[] param)
		{
			// ok, generate bearing
			final double brgDegs = param[0];
			final double rngM = param[1];

			final double res = (Math.abs(_val1 - brgDegs)) + Math.abs(_val2 - rngM);

			// do the calc
			return res;
		}

	}

	/** go through the selection, pull out things we may optimise
	 * 
	 * @param resultsList placeholder for the results
	 * @param selection the current selection
	 * @param secTrack the current secondary track
	 * @return
	 */
	public static String getDraggables(final Vector<DraggableItem> resultsList,
			final IStructuredSelection selection, final TrackWrapper secTrack)
	{
		String res = null;

		TrackWrapper track = null;

		final Vector<DraggableItem> tmpList = new Vector<DraggableItem>();

		// ok, get looping
		final Iterator<?> iter = selection.iterator();
		while (iter.hasNext())
		{
			final Object object = (Object) iter.next();
			if (object instanceof EditableWrapper)
			{
				final EditableWrapper ew = (EditableWrapper) object;
				final Editable obj = ew.getEditable();
				if (obj instanceof TrackWrapper)
				{
					// is this something other than the secondary track?
					final TrackWrapper thisTrack = (TrackWrapper) obj;

					// is this our first item?
					if (tmpList.size() == 0)
					{
						if (!thisTrack.equals(secTrack))
						{
							return ONLY_SECONDARY;
						}

						track = thisTrack;
						tmpList.add(track);
					}
					else
					{
						// do we alreay have a track
						if (track != null)
							return ONLY_ONE_TRACK;
						else
							return NO_MIXED_ITEMS;
					}
				}
				else if (obj instanceof TrackSegment)
				{
					// do we already have a track?
					if (track != null)
					{
						return NO_MIXED_ITEMS;
					}
					tmpList.add((DraggableItem) obj);
				}
				else
				{
					return INVALID_SELECTION;
				}
			}
		}

		if (tmpList.size() == 0)
		{
			res = NO_VALID_SEL;
		}
		else
		{
			resultsList.addAll(tmpList);
		}

		return res;
	}
}
