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

package org.mwc.debrief.track_shift.magic;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mwc.debrief.track_shift.magic.OptimiseTest.*;
import org.mwc.debrief.track_shift.magic.OptimiseTest.*;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.track_shift.views.StackedDotHelper;
import Debrief.ReaderWriter.XML.DebriefXMLReaderWriter;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class OptimiseTestTest {
	private WorldArea areaFor(final TreeSet<Doublet> doublets) {
		WorldArea res = null;
		final Iterator<Doublet> iter = doublets.iterator();
		while (iter.hasNext()) {
			final Doublet doublet = iter.next();
			final WorldLocation loc = doublet.getTarget().getLocation();
			if (res == null)
				res = new WorldArea(loc, loc);
			else
				res.extend(loc);
		}
		return res;
	}

	@Test
	public void testDraggables() {
		Vector<EditableWrapper> items = new Vector<EditableWrapper>();
		Vector<DraggableItem> resList = new Vector<DraggableItem>();
		TrackWrapper theTrack = new TrackWrapper();
		items.add(new EditableWrapper(theTrack));
		items.add(new EditableWrapper(new TrackSegment(TrackSegment.ABSOLUTE)));
		IStructuredSelection sel = new StructuredSelection(items);

		assertEquals(2, sel.size(), "data in there");
		String res = getDraggables(resList, sel, theTrack);

		assertEquals(0, resList.size(), "failed, mixed");
		assertEquals(NO_MIXED_ITEMS, res, "failed, mixed");

		// try an empty list
		items = new Vector<EditableWrapper>();
		resList = new Vector<DraggableItem>();
		sel = new StructuredSelection(items);

		assertEquals(0, sel.size(), "no data in there");
		res = getDraggables(resList, sel, theTrack);

		assertEquals(0, resList.size(), "failed, mixed");
		assertEquals(NO_VALID_SEL, res, "failed, mixed");

		// swap the items around
		items = new Vector<EditableWrapper>();
		resList = new Vector<DraggableItem>();
		theTrack = new TrackWrapper();
		items.add(new EditableWrapper(new TrackSegment(TrackSegment.ABSOLUTE)));
		items.add(new EditableWrapper(theTrack));
		sel = new StructuredSelection(items);

		assertEquals(2, sel.size(), "data in there");
		res = getDraggables(resList, sel, theTrack);

		assertEquals(0, resList.size(), "failed, mixed");
		assertEquals(NO_MIXED_ITEMS, res, "failed, mixed");

		// try a working list
		items = new Vector<EditableWrapper>();
		resList = new Vector<DraggableItem>();
		theTrack = new TrackWrapper();
		items.add(new EditableWrapper(new TrackSegment(TrackSegment.ABSOLUTE)));
		items.add(new EditableWrapper(new TrackSegment(TrackSegment.ABSOLUTE)));
		sel = new StructuredSelection(items);

		assertEquals(2, sel.size(), "data in there");
		res = getDraggables(resList, sel, theTrack);

		assertEquals(2, resList.size(), "worked, found 2");
		assertEquals(null, res, "worked, no errors");

		// try two tracks
		items = new Vector<EditableWrapper>();
		resList = new Vector<DraggableItem>();
		theTrack = new TrackWrapper();
		items.add(new EditableWrapper(theTrack));
		items.add(new EditableWrapper(new TrackWrapper()));
		sel = new StructuredSelection(items);

		assertEquals(2, sel.size(), "data in there");
		res = getDraggables(resList, sel, theTrack);

		assertEquals(0, resList.size(), "failed, two tracks");
		assertEquals(ONLY_ONE_TRACK, res, "failed, two tracks");

	}

	@Test
	public void testDummyPermutations() {
		// Create instance of Minimisation
		final Minimisation min = new Minimisation();
		final MinimisationFunction funct = new DummyOffsetFunction(100, 2000);

		// initial estimates
		final double[] start = { 0, 0 };

		// initial step sizes
		final double[] step = { 20, 400 };

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

		assertEquals(100, bearing, 0.0001, "wrong bearing");
		assertEquals(2000, range, 0.0001, "wrong range");
	}

	// TODO FIX-TEST
	@Disabled("Its plot file needs the Eclipse plot reader and a plot editor (it used a Mockito mock until Phase E); it was in no suite before the JUnit 6 migration, so it never ran")
	@Test
	public void testLoad() throws FileNotFoundException {
		// get some data
		final Layers layers = getTheData();

		assertNotNull(layers, "should have loaded data");

		// ok, sort out the two tracks
		assertEquals(2, layers.size(), "wrong num tracks");

		// ok, go for the primary
		final Layer layerPri = layers.elementAt(0);
		final Layer layerSec = layers.elementAt(1);

		final TrackWrapper pri = (TrackWrapper) layerPri;
		final TrackWrapper sec = (TrackWrapper) layerSec;

		final List<TrackWrapper> primaries = new ArrayList<TrackWrapper>();
		primaries.add(pri);

		assertEquals("NONSUCH", pri.getName());
		assertEquals("TMA_TGT", sec.getName());

		// can we get some doublets?
		final TreeSet<Doublet> doublets = StackedDotHelper.getDoublets(primaries, sec, true, true, false);

		assertNotNull(doublets, "found some");
		assertEquals(18, doublets.size(), "right num");

		// Create instance of Minimisation
		final Minimisation min = new Minimisation();
		final MinimisationFunction funct = new TryOffsetFunction(doublets);

		// initial estimates
		final double[] start = { 0, 0 };

		// initial step sizes
		final double[] step = { 20, 400 };

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

	@Test
	public void testPermutations() {

		final SensorWrapper sensor = new SensorWrapper("nane");
		final SensorContactWrapper sensorCut = new SensorContactWrapper("name", new HiResDate(1000), null, 100d,
				new WorldLocation(1d, 1d, 1d), null, null, 1, null);
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
		final double[] start = { 0, 0 };

		// initial step sizes
		final double[] step = { 20, 400 };

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

	@Test
	public void testShiftMultiple() throws FileNotFoundException {
		final SensorContactWrapper sensor = new SensorContactWrapper("name", new HiResDate(1000), null, 100d,
				new WorldLocation(1d, 1d, 1d), null, null, 1, null);
		final SensorWrapper sw = new SensorWrapper("sensor");
		sw.add(sensor);
		final TrackSegment parent = null;
		final FixWrapper hostFix1 = new FixWrapper(new Fix(new HiResDate(1000), null, 0, 0));

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

		assertEquals(2, doublets.size(), "right num");
		WorldArea coverage = areaFor(doublets);
		assertEquals(0, coverage.getTopLeft().getLat(), 0.001, "right tl lat");
		assertEquals(0, coverage.getTopLeft().getLong(), 0.001, "right tl lon");

		WorldVector thisOffset = new WorldVector(0, 0, 0);
		final TryOffsetFunction func = new TryOffsetFunction(doublets);
		TreeSet<Doublet> res = func.shiftDoublets(thisOffset);
		coverage = areaFor(res);

		assertEquals(0, coverage.getTopLeft().getLat(), 0.001, "right tl lat");
		assertEquals(0, coverage.getTopLeft().getLong(), 0.001, "right tl lon");

		// apply some kind of offset - move it 1 deg north
		thisOffset = new WorldVector(0, 1, 0);
		res = func.shiftDoublets(thisOffset);
		coverage = areaFor(res);
		assertEquals(1.0, coverage.getTopLeft().getLat(), 0.001, "right tl lat");
		assertEquals(0, coverage.getTopLeft().getLong(), 0.001, "right tl lon");

		// apply some kind of offset - move it 1 deg north
		thisOffset = new WorldVector(Math.PI / 2, 1, 0);
		res = func.shiftDoublets(thisOffset);
		coverage = areaFor(res);
		assertEquals(0, coverage.getTopLeft().getLat(), 0.001, "right tl lat");
		assertEquals(1, coverage.getTopLeft().getLong(), 0.001, "right tl lon");
	}

	@Test
	public void testShiftSingle() throws FileNotFoundException {
		final SensorContactWrapper sensor = new SensorContactWrapper("track", new HiResDate(1100), null, 100d, null,
				null, null, Color.RED, "label", 1, "Some name");
		final SensorWrapper sensorW = new SensorWrapper("sensor");
		sensorW.add(sensor);
		final TrackSegment parent = null;
		final FixWrapper hostFix = null;
		final WorldLocation theLoc = new WorldLocation(0, 0, 0);
		final Fix newFix = new Fix(new HiResDate(1000), theLoc, 0, 0);
		final FixWrapper targetFix = new FixWrapper(newFix);
		final Doublet dt = new Doublet(sensor, targetFix, parent, hostFix);

		final TreeSet<Doublet> doublets = new TreeSet<Doublet>();
		doublets.add(dt);

		assertEquals(1, doublets.size(), "right num");
		WorldArea coverage = areaFor(doublets);
		assertEquals(0, coverage.getTopLeft().getLat(), 0.001, "right tl lat");
		assertEquals(0, coverage.getTopLeft().getLong(), 0.001, "right tl lon");

		final TryOffsetFunction func = new TryOffsetFunction(doublets);

		WorldVector thisOffset = new WorldVector(0, 0, 0);
		TreeSet<Doublet> res = func.shiftDoublets(thisOffset);
		coverage = areaFor(res);

		assertEquals(0, coverage.getTopLeft().getLat(), 0.001, "right tl lat");
		assertEquals(0, coverage.getTopLeft().getLong(), 0.001, "right tl lon");

		// apply some kind of offset - move it 1 deg north
		thisOffset = new WorldVector(0, 1, 0);
		res = func.shiftDoublets(thisOffset);
		coverage = areaFor(res);
		assertEquals(1.0, coverage.getTopLeft().getLat(), 0.001, "right tl lat");
		assertEquals(0, coverage.getTopLeft().getLong(), 0.001, "right tl lon");

		// apply some kind of offset - move it 1 deg north
		thisOffset = new WorldVector(Math.PI / 2, 1, 0);
		res = func.shiftDoublets(thisOffset);
		coverage = areaFor(res);
		assertEquals(0, coverage.getTopLeft().getLat(), 0.001, "right tl lat");
		assertEquals(1, coverage.getTopLeft().getLong(), 0.001, "right tl lon");
	}

}
