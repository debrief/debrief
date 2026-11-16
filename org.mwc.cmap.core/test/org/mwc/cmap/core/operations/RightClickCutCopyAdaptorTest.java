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

package org.mwc.cmap.core.operations;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.*;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.ui_support.OutlineNameSorter;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.NeedsToBeInformedOfRemove;
import MWC.GUI.Plottable;
import MWC.GUI.PlottablesType;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor.IsTransientForChildren;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.ReaderWriter.ClipboardInputFilter;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class RightClickCutCopyAdaptorTest {

	private static class MyCutItem extends CutItem {

		public MyCutItem(final Editable[] data, final Clipboard clipboard, final Layer[] theParent,
				final Layers theLayers, final Layer[] updateLayer) {
			super(data, clipboard, theParent, theLayers, updateLayer);
		}

		@Override
		protected void setImageIcon() {
			// don't bother, we haven't got enough platform running
		}

	}

	private static void doUndo() {
		final IOperationHistory history = CorePlugin.getHistory();
		try {
			history.undo(CorePlugin.getUndoContext(), null, null);
		} catch (final ExecutionException e) {
			CorePlugin.logError(IStatus.ERROR, "Problem with undo for test", e);
			assertTrue(e == null, "threw assertion");
		}
	}

	private static boolean isContactThere(final TrackWrapper tw, final TMAContactWrapper scwa1) {
		boolean itemFound;
		final Enumeration<Editable> enumer = tw.getSolutions().elements();
		itemFound = false;
		while (enumer.hasMoreElements()) {
			final TMAWrapper ee = (TMAWrapper) enumer.nextElement();
			final Enumeration<Editable> contacts = ee.elements();
			while (contacts.hasMoreElements()) {
				final Editable thisC = contacts.nextElement();
				if (thisC.equals(scwa1)) {
					itemFound = true;
					continue;
				}
			}
		}
		return itemFound;
	}

	private static boolean isPositionThere(final TrackWrapper tw, final FixWrapper fw2) {
		boolean itemFound;
		final Enumeration<Editable> enumer = tw.getPositionIterator();
		itemFound = false;
		while (enumer.hasMoreElements()) {
			final Editable ee = enumer.nextElement();
			if (ee.equals(fw2)) {
				itemFound = true;
				continue;
			}
		}
		return itemFound;
	}

	private static boolean isSensorThere(final TrackWrapper tw, final SensorContactWrapper scwa1) {
		boolean itemFound;
		final Enumeration<Editable> enumer = tw.getSensors().elements();
		itemFound = false;
		while (enumer.hasMoreElements()) {
			final SensorWrapper ee = (SensorWrapper) enumer.nextElement();
			final Enumeration<Editable> contacts = ee.elements();
			while (contacts.hasMoreElements()) {
				final Editable thisC = contacts.nextElement();
				if (thisC.equals(scwa1)) {
					itemFound = true;
					continue;
				}
			}
		}
		return itemFound;
	}

	// TODO FIX-TEST
	public void NtestCut() {
		// create the data
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0, 0, 0);
		final FixWrapper fw1 = new FixWrapper(new Fix(new HiResDate(100, 10000),
				loc_1.add(new WorldVector(33, new WorldDistance(100, WorldDistance.METRES), null)), 10, 110));
		fw1.setLabel("fw1");
		final FixWrapper fw2 = new FixWrapper(new Fix(new HiResDate(200, 20000),
				loc_1.add(new WorldVector(33, new WorldDistance(200, WorldDistance.METRES), null)), 20, 120));
		fw2.setLabel("fw2");
		final FixWrapper fw3 = new FixWrapper(new Fix(new HiResDate(300, 30000),
				loc_1.add(new WorldVector(33, new WorldDistance(300, WorldDistance.METRES), null)), 30, 130));
		fw3.setLabel("fw3");
		final FixWrapper fw4 = new FixWrapper(new Fix(new HiResDate(400, 40000),
				loc_1.add(new WorldVector(33, new WorldDistance(400, WorldDistance.METRES), null)), 40, 140));
		fw4.setLabel("fw4");
		final FixWrapper fw5 = new FixWrapper(new Fix(new HiResDate(500, 50000),
				loc_1.add(new WorldVector(33, new WorldDistance(500, WorldDistance.METRES), null)), 50, 150));
		fw5.setLabel("fw5");
		tw.addFix(fw1);
		tw.addFix(fw2);
		tw.addFix(fw3);
		tw.addFix(fw4);
		tw.addFix(fw5);
		// also give it some sensor data
		final SensorWrapper swa = new SensorWrapper("title one");
		final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa", new HiResDate(150, 0), null, null, null,
				null, null, 0, null);
		final SensorContactWrapper scwa2 = new SensorContactWrapper("bbb", new HiResDate(180, 0), null, null, null,
				null, null, 0, null);
		final SensorContactWrapper scwa3 = new SensorContactWrapper("ccc", new HiResDate(250, 0), null, null, null,
				null, null, 0, null);
		swa.add(scwa1);
		swa.add(scwa2);
		swa.add(scwa3);
		tw.add(swa);
		final SensorWrapper sw = new SensorWrapper("title two");
		final SensorContactWrapper scw1 = new SensorContactWrapper("ddd", new HiResDate(260, 0), null, null, null,
				null, null, 0, null);
		final SensorContactWrapper scw2 = new SensorContactWrapper("eee", new HiResDate(280, 0), null, null, null,
				null, null, 0, null);
		final SensorContactWrapper scw3 = new SensorContactWrapper("fff", new HiResDate(350, 0), null, null, null,
				null, null, 0, null);
		sw.add(scw1);
		sw.add(scw2);
		sw.add(scw3);
		tw.add(sw);

		final TMAWrapper mwa = new TMAWrapper("bb");
		final TMAContactWrapper tcwa1 = new TMAContactWrapper("aaa", "bbb", new HiResDate(130), null, 0, 0, 0, null,
				null, null, null);
		final TMAContactWrapper tcwa2 = new TMAContactWrapper("bbb", "bbb", new HiResDate(190), null, 0, 0, 0, null,
				null, null, null);
		final TMAContactWrapper tcwa3 = new TMAContactWrapper("ccc", "bbb", new HiResDate(230), null, 0, 0, 0, null,
				null, null, null);
		mwa.add(tcwa1);
		mwa.add(tcwa2);
		mwa.add(tcwa3);
		tw.add(mwa);
		final TMAWrapper mw = new TMAWrapper("cc");
		final TMAContactWrapper tcw1 = new TMAContactWrapper("ddd", "bbb", new HiResDate(230), null, 0, 0, 0, null,
				null, null, null);
		final TMAContactWrapper tcw2 = new TMAContactWrapper("eee", "bbb", new HiResDate(330), null, 0, 0, 0, null,
				null, null, null);
		final TMAContactWrapper tcw3 = new TMAContactWrapper("fff", "bbb", new HiResDate(390), null, 0, 0, 0, null,
				null, null, null);
		mw.add(tcw1);
		mw.add(tcw2);
		mw.add(tcw3);
		tw.add(mw);

		// now fiddle with it
		final Layers updateLayers = new Layers();
		updateLayers.addThisLayer(tw);
		final Clipboard clipboard = new Clipboard(Display.getDefault());
		Layer[] parentLayer = new Layer[] { tw };
		final CutItem ci = new MyCutItem(new Editable[] { fw2 }, clipboard, parentLayer, updateLayers, parentLayer);
		// check our item's in there
		assertTrue(isPositionThere(tw, fw2), "item there before op");
		assertTrue(isPositionThere(tw, fw3), "item there before op");

		// now do the cut
		ci.run();
		assertFalse(isPositionThere(tw, fw2), "item gone after op");
		assertTrue(isPositionThere(tw, fw3), "item there after op");

		doUndo();
		assertTrue(isPositionThere(tw, fw2), "item back again after op");

		// now let's try two items
		parentLayer = new Layer[] { tw, tw };
		final CutItem c2 = new MyCutItem(new Editable[] { fw2, fw4 }, clipboard, parentLayer, updateLayers,
				parentLayer);
		assertTrue(isPositionThere(tw, fw2), "item there before op");
		assertTrue(isPositionThere(tw, fw3), "item there before op");
		assertTrue(isPositionThere(tw, fw4), "item there before op");
		// now do the cut
		c2.run();
		assertFalse(isPositionThere(tw, fw2), "item gone after op");
		assertTrue(isPositionThere(tw, fw3), "item there after op");
		assertFalse(isPositionThere(tw, fw4), "item gone after op");

		doUndo();
		assertTrue(isPositionThere(tw, fw2), "item back again after op");
		assertTrue(isPositionThere(tw, fw3), "item still there after op");
		assertTrue(isPositionThere(tw, fw4), "item back again after op");

		// right, now let's try to delete a sensor item
		parentLayer = new Layer[] { swa };
		CutItem c3 = new MyCutItem(new Editable[] { scwa1 }, clipboard, parentLayer, updateLayers, parentLayer);
		assertTrue(isSensorThere(tw, scwa1), "item there before op");
		assertTrue(isSensorThere(tw, scwa2), "item there before op");
		assertTrue(isSensorThere(tw, scwa3), "item there before op");
		c3.run();
		assertFalse(isSensorThere(tw, scwa1), "item not there after op");
		assertTrue(isSensorThere(tw, scwa2), "item there after op");
		assertTrue(isSensorThere(tw, scwa3), "item there after op");
		doUndo();
		assertTrue(isSensorThere(tw, scwa1), "item back again after op");
		assertTrue(isSensorThere(tw, scwa2), "item back again after op");
		assertTrue(isSensorThere(tw, scwa3), "item back again after op");
		// now let's try two items
		parentLayer = new Layer[] { swa, swa };
		c3 = new MyCutItem(new Editable[] { scwa1, scwa2 }, clipboard, parentLayer, updateLayers, parentLayer);
		assertTrue(isSensorThere(tw, scwa1), "item there before op");
		assertTrue(isSensorThere(tw, scwa2), "item there before op");
		assertTrue(isSensorThere(tw, scwa3), "item there before op");
		c3.run();
		assertFalse(isSensorThere(tw, scwa1), "item not there after op");
		assertFalse(isSensorThere(tw, scwa2), "item not there after op");
		assertTrue(isSensorThere(tw, scwa3), "item there after op");
		doUndo();
		assertTrue(isSensorThere(tw, scwa1), "item back again after op");
		assertTrue(isSensorThere(tw, scwa2), "item back again after op");
		assertTrue(isSensorThere(tw, scwa3), "item back again after op");
		// now let's try two items in different layers
		parentLayer = new Layer[] { swa, sw };
		c3 = new MyCutItem(new Editable[] { scwa1, scw2 }, clipboard, parentLayer, updateLayers, parentLayer);
		assertTrue(isSensorThere(tw, scwa1), "item there before op");
		assertTrue(isSensorThere(tw, scw2), "item there before op");
		assertTrue(isSensorThere(tw, scwa3), "item there before op");
		c3.run();
		assertFalse(isSensorThere(tw, scwa1), "item not there after op");
		assertFalse(isSensorThere(tw, scw2), "item not there after op");
		assertTrue(isSensorThere(tw, scwa3), "item there after op");
		doUndo();
		assertTrue(isSensorThere(tw, scwa1), "item back again after op");
		assertTrue(isSensorThere(tw, scw2), "item back again after op");
		assertTrue(isSensorThere(tw, scwa3), "item back again after op");

		// //////////////////////////
		// now for TMA!

		// right, now let's try to delete a sensor item
		parentLayer = new Layer[] { mwa };
		c3 = new MyCutItem(new Editable[] { tcwa1 }, clipboard, parentLayer, updateLayers, parentLayer);
		assertTrue(isContactThere(tw, tcwa1), "item there before op");
		assertTrue(isContactThere(tw, tcwa2), "item there before op");
		assertTrue(isContactThere(tw, tcwa3), "item there before op");
		c3.run();
		assertFalse(isContactThere(tw, tcwa1), "item not there after op");
		assertTrue(isContactThere(tw, tcwa2), "item there after op");
		assertTrue(isContactThere(tw, tcwa3), "item there after op");
		doUndo();
		assertTrue(isContactThere(tw, tcwa1), "item back again after op");
		assertTrue(isContactThere(tw, tcwa2), "item back again after op");
		assertTrue(isContactThere(tw, tcwa3), "item back again after op");
		// now let's try two items
		parentLayer = new Layer[] { mwa, mwa };
		c3 = new MyCutItem(new Editable[] { tcwa1, tcwa2 }, clipboard, parentLayer, updateLayers, parentLayer);
		assertTrue(isContactThere(tw, tcwa1), "item there before op");
		assertTrue(isContactThere(tw, tcwa2), "item there before op");
		assertTrue(isContactThere(tw, tcwa3), "item there before op");
		c3.run();
		assertFalse(isContactThere(tw, tcwa1), "item not there after op");
		assertFalse(isContactThere(tw, tcwa2), "item not there after op");
		assertTrue(isContactThere(tw, tcwa3), "item there after op");
		doUndo();
		assertTrue(isContactThere(tw, tcwa1), "item back again after op");
		assertTrue(isContactThere(tw, tcwa2), "item back again after op");
		assertTrue(isContactThere(tw, tcwa3), "item back again after op");
		// now let's try two items in different layers
		parentLayer = new Layer[] { mwa, mw };
		c3 = new MyCutItem(new Editable[] { tcwa1, tcw2 }, clipboard, parentLayer, updateLayers, parentLayer);
		assertTrue(isContactThere(tw, tcwa1), "item there before op");
		assertTrue(isContactThere(tw, tcw2), "item there before op");
		assertTrue(isContactThere(tw, tcwa3), "item there before op");
		c3.run();
		assertFalse(isContactThere(tw, tcwa1), "item not there after op");
		assertFalse(isContactThere(tw, tcw2), "item not there after op");
		assertTrue(isContactThere(tw, tcwa3), "item there after op");
		doUndo();
		assertTrue(isContactThere(tw, tcwa1), "item back again after op");
		assertTrue(isContactThere(tw, tcw2), "item back again after op");
		assertTrue(isContactThere(tw, tcwa3), "item back again after op");

	}

	@Test
	public void testDummy() {

	}

}
