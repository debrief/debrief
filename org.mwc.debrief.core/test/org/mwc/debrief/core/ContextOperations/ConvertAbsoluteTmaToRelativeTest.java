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

package org.mwc.debrief.core.ContextOperations;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mwc.debrief.core.ContextOperations.ConvertAbsoluteTmaToRelative.*;
import org.mwc.debrief.core.ContextOperations.ConvertAbsoluteTmaToRelative.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this public class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public final class ConvertAbsoluteTmaToRelativeTest {
		private static class TestOperation extends AbstractOperation {

			@SuppressWarnings("unused")
			final private Layers _theLayers;
			@SuppressWarnings("unused")
			final private List<SuitableSegment> _suitableSements;
			@SuppressWarnings("unused")
			private final TrackWrapper _parent;

			public TestOperation(final String label, final Layers theLayers,
					final List<SuitableSegment> suitableSegments, final TrackWrapper parent) {
				super(label);
				_theLayers = theLayers;
				_suitableSements = suitableSegments;
				_parent = parent;
			}

			@Override
			public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
				// TODO Auto-generated method stub
				return null;
			}

		}

		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		List<IAction> actions = new ArrayList<IAction>();

		@SuppressWarnings({ "deprecation" })
		private TrackWrapper getLongerTrack() {
			final TrackWrapper tw = new TrackWrapper();
			tw.setName("Some Name");

			final TrackSegment ts = new TrackSegment(false);

			final WorldLocation loc_1 = new WorldLocation(0.00000001, 0.000000001, 0);
			WorldLocation lastLoc = loc_1;

			for (int i = 0; i < 50; i++) {

				final long thisTime = new Date(2016, 1, 14, 12, i, 0).getTime();
				final FixWrapper fw = new FixWrapper(new Fix(new HiResDate(thisTime), lastLoc.add(getVector(25, 0)),
						MWC.Algorithms.Conversions.Degs2Rads(0), 110));
				fw.setLabel("fw1");
				ts.addFix(fw);

				lastLoc = new WorldLocation(fw.getLocation());
			}

			tw.add(ts);

			final SensorWrapper swa = new SensorWrapper("title one");
			tw.add(swa);
			swa.setSensorOffset(new ArrayLength(-400));

			for (int i = 0; i < 500; i += 3) {
				final long thisTime = new Date(2016, 1, 14, 12, i, 30).getTime();
				final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa", new HiResDate(thisTime), null, null,
						null, null, null, 0, null);
				swa.add(scwa1);
			}

			return tw;
		}

		/**
		 * @return
		 */
		private WorldVector getVector(final double courseDegs, final double distM) {
			return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(courseDegs),
					new WorldDistance(distM, WorldDistance.METRES), null);
		}

		@SuppressWarnings("deprecation")
		@Test
		public void testApplicable() throws ExecutionException {
			actions.clear();

			final TrackWrapper tw = getLongerTrack();
			final SensorWrapper sw = new SensorWrapper("name");
			final SensorWrapper sw2 = new SensorWrapper("un-name");
			tw.add(sw);
			tw.add(sw2);

			final TrackWrapper tw2 = getLongerTrack();
			tw2.setName("target track");

			final ConvertAbsoluteTmaToRelative op = new ConvertAbsoluteTmaToRelative() {
				@Override
				protected IUndoableOperation getOperation(final Layers theLayers,
						final List<SuitableSegment> suitableSegments, final TrackWrapper parent) {
					return new TestOperation("Label", theLayers, suitableSegments, parent);
				}
			};

			final MenuManager menu = new MenuManager("some name") {
				@Override
				public void add(final IAction action) {
					super.add(action);
					actions.add(action);
				}
			};

			final Layers theLayers = new Layers();
			theLayers.addThisLayer(tw);
			theLayers.addThisLayer(tw2);
			Editable[] subjects = new Editable[] { tw, sw };
			op.generate(menu, theLayers, null, subjects);

			assertEquals(0, actions.size(), "no items added");

			final WorldSpeed a1Speed = new WorldSpeed(12, WorldSpeed.Kts);

			final WorldLocation a1Origin1 = new WorldLocation(12, 12, 0);
			final HiResDate a1Start1 = new HiResDate(new Date(2016, 1, 14, 12, 30, 0).getTime());
			final HiResDate a1end1 = new HiResDate(new Date(2016, 1, 14, 12, 40, 0).getTime());
			final AbsoluteTMASegment a11 = new AbsoluteTMASegment(12, a1Speed, a1Origin1, a1Start1, a1end1);
			a11.setWrapper(tw);

			final WorldLocation a1Origin = new WorldLocation(12, 12, 0);
			final HiResDate a1Start = new HiResDate(new Date(2016, 1, 14, 12, 30, 0).getTime());
			final HiResDate a1end = new HiResDate(new Date(2016, 1, 14, 12, 40, 0).getTime());
			final AbsoluteTMASegment a1 = new AbsoluteTMASegment(12, a1Speed, a1Origin, a1Start, a1end);
			a1.setWrapper(tw2);

			subjects = new Editable[] { a1 };

			actions.clear();

			op.generate(menu, theLayers, null, subjects);

			assertEquals(0, actions.size(), "no items added");

			// ok, remove the track segment
			tw.removeElement(tw.getSegments().first());

			// check it worked
			assertEquals(0, tw.getSegments().size(), "empty segs");

			final WorldVector theOffset = new WorldVector(12, 0.002, 0);
			// ok, add a relative segment before it
			final RelativeTMASegment r1 = new RelativeTMASegment(13, a1Speed, theOffset, theLayers, tw.getName(),
					"other sensor");

			long thisTime = new Date(2016, 1, 14, 12, 22, 0).getTime();
			FixWrapper fw = new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin.add(getVector(25, 50)),
					MWC.Algorithms.Conversions.Degs2Rads(0), 110));
			fw.setLabel("fw1");
			r1.addFix(fw);

			thisTime = new Date(2016, 1, 14, 12, 24, 0).getTime();
			fw = new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin.add(getVector(23, 200)),
					MWC.Algorithms.Conversions.Degs2Rads(0), 110));
			fw.setLabel("fw2");
			r1.addFix(fw);
			r1.setLayers(theLayers);

			tw.add(r1);

			actions.clear();
			op.generate(menu, theLayers, null, subjects);
			assertEquals(0, actions.size(), "no items added");

			// now correct the sensor name
			final RelativeTMASegment r2 = new RelativeTMASegment(13, a1Speed, theOffset, theLayers, tw.getName(),
					sw.getName());
			final Enumeration<Editable> enumer = r1.elements();
			while (enumer.hasMoreElements()) {
				final Editable editable = enumer.nextElement();
				r2.add(editable);
			}
			r2.setLayers(theLayers);

			// tw.removeElement(r1);
			tw2.add(r2);

			System.out.println("r1 ends at:" + r2.getDTG_End() + " a1 starts at:" + a1.getDTG_Start());

			actions.clear();
			op.generate(menu, theLayers, null, subjects);
			assertEquals(1, actions.size(), "generated an action items added");

			// - add later track
			// ok, add a relative segment before it
			final RelativeTMASegment r3 = new RelativeTMASegment(13, a1Speed, theOffset, theLayers, tw.getName(),
					sw2.getName());

			thisTime = new Date(2016, 1, 14, 12, 50, 0).getTime();
			fw = new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin.add(getVector(25, 50)),
					MWC.Algorithms.Conversions.Degs2Rads(0), 110));
			fw.setLabel("fw3_1");
			r3.addFix(fw);

			thisTime = new Date(2016, 1, 14, 12, 59, 0).getTime();
			fw = new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin.add(getVector(23, 200)),
					MWC.Algorithms.Conversions.Degs2Rads(0), 110));
			fw.setLabel("fw3_2");
			r3.addFix(fw);
			r3.setLayers(theLayers);

//      tw.add(r3);
//
//      actions.clear();
//      op.generate(menu, theLayers, null, subjects);
//      assertEquals("no items added", 0, actions.size());
//
//      tw.removeElement(r3);
//
//      // - add later track
//      // ok, add a relative segment before it
//      final RelativeTMASegment r4 =
//          new RelativeTMASegment(13, a1Speed, theOffset, theLayers, tw
//              .getName(), sw.getName());
//
//      thisTime = new Date(2016, 1, 14, 12, 50, 0).getTime();
//      fw =
//          new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin
//              .add(getVector(25, 50)), MWC.Algorithms.Conversions.Degs2Rads(0),
//              110));
//      fw.setLabel("fw1");
//      r4.addFix(fw);
//
//      thisTime = new Date(2016, 1, 14, 12, 59, 0).getTime();
//      fw =
//          new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin
//              .add(getVector(23, 200)),
//              MWC.Algorithms.Conversions.Degs2Rads(0), 110));
//      fw.setLabel("fw2");
//      r4.addFix(fw);
//      r3.setLayers(theLayers);
//      tw.add(r4);
//
//      actions.clear();
//      op.generate(menu, theLayers, null, subjects);
//      assertEquals("items added", 1, actions.size());
		}
	}
