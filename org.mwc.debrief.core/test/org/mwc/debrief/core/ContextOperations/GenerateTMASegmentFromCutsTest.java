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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mwc.debrief.core.ContextOperations.GenerateTMASegmentFromCuts.*;
import org.mwc.debrief.core.ContextOperations.GenerateTMASegmentFromCuts.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IContributionManagerOverrides;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.core.wizards.RangeBearingPage;
import org.mwc.cmap.core.wizards.SelectColorPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage.SolutionDataItem;
import org.mwc.debrief.core.wizards.s2r.TMAFromSensorWizard;
import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class GenerateTMASegmentFromCutsTest {
	private static class DummyMenuManager implements IMenuManager {
		final private List<IAction> actions = new ArrayList<IAction>();

		@Override
		public void add(final IAction action) {
			actions.add(action);
		}

		@Override
		public void add(final IContributionItem item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void addMenuListener(final IMenuListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void appendToGroup(final String groupName, final IAction action) {
			// TODO Auto-generated method stub

		}

		@Override
		public void appendToGroup(final String groupName, final IContributionItem item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void fill(final Composite parent) {
			// TODO Auto-generated method stub

		}

		@Override
		public void fill(final CoolBar parent, final int index) {
			// TODO Auto-generated method stub

		}

		@Override
		public void fill(final Menu parent, final int index) {
			// TODO Auto-generated method stub

		}

		@Override
		public void fill(final ToolBar parent, final int index) {
			// TODO Auto-generated method stub

		}

		@Override
		public IContributionItem find(final String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IMenuManager findMenuUsingPath(final String path) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IContributionItem findUsingPath(final String path) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IContributionItem[] getItems() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IContributionManagerOverrides getOverrides() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getRemoveAllWhenShown() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void insertAfter(final String id, final IAction action) {
			// TODO Auto-generated method stub

		}

		@Override
		public void insertAfter(final String id, final IContributionItem item) {
			// TODO Auto-generated method stub

		}

		@Override
		public void insertBefore(final String id, final IAction action) {
			// TODO Auto-generated method stub

		}

		@Override
		public void insertBefore(final String id, final IContributionItem item) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isDirty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isDynamic() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isGroupMarker() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isSeparator() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isVisible() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void markDirty() {
			// TODO Auto-generated method stub

		}

		@Override
		public void prependToGroup(final String groupName, final IAction action) {
			// TODO Auto-generated method stub

		}

		@Override
		public void prependToGroup(final String groupName, final IContributionItem item) {
			// TODO Auto-generated method stub

		}

		@Override
		public IContributionItem remove(final IContributionItem item) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IContributionItem remove(final String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void removeAll() {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeMenuListener(final IMenuListener listener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void saveWidgetState() {
			// TODO Auto-generated method stub

		}

		@Override
		public void setParent(final IContributionManager parent) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setRemoveAllWhenShown(final boolean removeAll) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setVisible(final boolean visible) {
			// TODO Auto-generated method stub

		}

		@Override
		public void update() {
			// TODO Auto-generated method stub

		}

		@Override
		public void update(final boolean force) {
			// TODO Auto-generated method stub

		}

		@Override
		public void update(final String id) {
			// TODO Auto-generated method stub

		}

		@Override
		public void updateAll(final boolean force) {
			// TODO Auto-generated method stub

		}

	}

	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@SuppressWarnings("deprecation")
	private TrackWrapper getLongerTrack() {
		final TrackWrapper tw = new TrackWrapper();

		final WorldLocation loc_1 = new WorldLocation(0.00000001, 0.000000001, 0);
		WorldLocation lastLoc = loc_1;

		for (int i = 0; i < 50; i++) {
			final long thisTime = new Date(2016, 1, 14, 12, i, 0).getTime();
			final FixWrapper fw = new FixWrapper(new Fix(new HiResDate(thisTime), lastLoc.add(getVector(25, 0)),
					MWC.Algorithms.Conversions.Degs2Rads(0), 110));
			fw.setLabel("fw1");
			tw.addFix(fw);

			lastLoc = new WorldLocation(fw.getLocation());
		}

		final SensorWrapper swa = new SensorWrapper("title one");
		tw.add(swa);
		swa.setSensorOffset(new ArrayLength(-400));

		for (int i = 0; i < 50; i += 3) {
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

	@Test
	public void testGenerateMultiSensor() {
		final TrackWrapper host = new TrackWrapper();
		host.setName("host");

		final SensorWrapper sensor1 = new SensorWrapper("sensor1");
		host.add(sensor1);
		final SensorWrapper sensor2 = new SensorWrapper("sensor2");
		host.add(sensor2);

		host.addFix(new FixWrapper(new Fix(new HiResDate(1000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(2000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(3000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(4000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(5000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(6000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(7000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(8000), new WorldLocation(0, 0, 0), 10, 20)));

		sensor1.add(new SensorContactWrapper("host", new HiResDate(800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor1.add(new SensorContactWrapper("host", new HiResDate(1800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor1.add(new SensorContactWrapper("host", new HiResDate(2800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor1.add(new SensorContactWrapper("host", new HiResDate(5800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor1.add(new SensorContactWrapper("host", new HiResDate(7800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor1.add(new SensorContactWrapper("host", new HiResDate(8800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor1.add(new SensorContactWrapper("host", new HiResDate(9800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor2.add(new SensorContactWrapper("host", new HiResDate(800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor2.add(new SensorContactWrapper("host", new HiResDate(1800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor2.add(new SensorContactWrapper("host", new HiResDate(2800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor2.add(new SensorContactWrapper("host", new HiResDate(5800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));

		final GenerateTMASegmentFromCuts genny = new GenerateTMASegmentFromCuts();
		final Editable[] sensors = new SensorWrapper[] { sensor1, sensor2 };
		final Layers theLayers = new Layers();

		final DummyMenuManager parent = new DummyMenuManager();
		genny.generate(parent, theLayers, null, sensors);
		assertEquals(parent.actions.size(), 1, "has action");
		final TMAAction theAction = (TMAAction) parent.actions.get(0);
		assertEquals(11, theAction._items.length, "correct cuts");

	}

	@Test
	public void testSplitWithOffset() throws ExecutionException {
		final TrackWrapper tw = getLongerTrack();

		assertNotNull(tw);

		// get the sensor data
		final SensorWrapper sw = (SensorWrapper) tw.getSensors().elements().nextElement();

		assertNotNull(sw);

		// create a list of cuts (to simulate the selection)
		final SensorContactWrapper[] items = new SensorContactWrapper[sw.size()];
		final Enumeration<Editable> numer = sw.elements();
		int ctr = 0;
		while (numer.hasMoreElements()) {
			final SensorContactWrapper cut = (SensorContactWrapper) numer.nextElement();
			items[ctr++] = cut;
		}

		final Layers theLayers = new Layers();
		final WorldVector worldOffset = new WorldVector(Math.PI, 0.002, 0);
		final double tgtCourse = 0;
		final WorldSpeed tgtSpeed = new WorldSpeed(3, WorldSpeed.Kts);

		final Color newColor = Color.GREEN;

		// check we haven't got the new color
		final Color oldColor = items[0].getColor();
		assertEquals(Color.YELLOW, oldColor, "correct original color");

		// ok, generate the target track
		final CMAPOperation op = new TMAfromCuts(items, theLayers, worldOffset, tgtCourse, tgtSpeed, newColor);

		// and run it
		op.execute(null, null);

		assertEquals(1, theLayers.size(), "has new data");

		final TrackWrapper sol = (TrackWrapper) theLayers.elementAt(0);
		assertNotNull(sol, "new layer not found");

		// ok, now try to split it
		assertEquals(1, sol.getSegments().size(), "only has one segment");

		final RelativeTMASegment seg = (RelativeTMASegment) sol.getSegments().elements().nextElement();

		assertNotNull(seg, "new seg not found");

		// check the color
		final SensorContactWrapper first = items[0];
		assertEquals(newColor, first.getColor(), "new color:");

		// ok, and we split it.
		int ctr2 = 0;
		FixWrapper beforeF = null;
		FixWrapper afterF = null;
		final Enumeration<Editable> eF = seg.elements();
		while (eF.hasMoreElements()) {
			final FixWrapper fix = (FixWrapper) eF.nextElement();
			ctr2++;
			if (ctr2 > seg.size() / 2) {
				if (beforeF == null) {
					beforeF = fix;
				} else {
					afterF = fix;
					break;
				}
			}
		}

		assertNotNull(beforeF, "fix not found");

		// ok, what's the time offset
		final WorldLocation afterBeforeSplit = afterF.getLocation();

		// ok, time to split
		final SubjectAction[] actions = beforeF.getInfo().getUndoableActions();
		final SubjectAction doSplit = actions[1];
		doSplit.execute(beforeF);

		// ok, have another look
		assertEquals(2, sol.getSegments().size(), "now has two segments");
		Enumeration<Editable> aNum = sol.getSegments().elements();
		aNum.nextElement();
		TrackSegment afterSeg = (TrackSegment) aNum.nextElement();
		WorldLocation locAfterSplit = afterSeg.getTrackStart();

		assertEquals(afterBeforeSplit, locAfterSplit, "origin remains valid");

		// hey, try the undo
		doSplit.undo(beforeF);

		assertEquals(1, sol.getSegments().size(), "now has one segment again");

		// hey, try the undo
		doSplit.execute(beforeF);
		assertEquals(2, sol.getSegments().size(), "now has two segments");

		aNum = sol.getSegments().elements();
		aNum.nextElement();
		afterSeg = (TrackSegment) aNum.nextElement();
		locAfterSplit = afterSeg.getTrackStart();
		assertEquals(afterBeforeSplit, locAfterSplit, "origin remains valid, after undo/redo");

	}

	@Test
	public void testTrimmingTrack() {
		final TrackWrapper host = new TrackWrapper();
		host.setName("host");

		final SensorWrapper sensor = new SensorWrapper("sensor");
		host.add(sensor);

		host.addFix(new FixWrapper(new Fix(new HiResDate(1000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(2000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(3000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(4000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(5000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(6000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(7000), new WorldLocation(0, 0, 0), 10, 20)));
		host.addFix(new FixWrapper(new Fix(new HiResDate(8000), new WorldLocation(0, 0, 0), 10, 20)));

		sensor.add(new SensorContactWrapper("host", new HiResDate(800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor.add(new SensorContactWrapper("host", new HiResDate(1800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor.add(new SensorContactWrapper("host", new HiResDate(2800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor.add(new SensorContactWrapper("host", new HiResDate(5800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor.add(new SensorContactWrapper("host", new HiResDate(7800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor.add(new SensorContactWrapper("host", new HiResDate(8800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));
		sensor.add(new SensorContactWrapper("host", new HiResDate(9800), null, 100d, null, Color.RED, "label", 12,
				"sensor"));

		final Enumeration<Editable> cuts = sensor.elements();
		final SensorContactWrapper[] cutArr = new SensorContactWrapper[sensor.size()];
		int ctr = 0;
		while (cuts.hasMoreElements()) {
			final SensorContactWrapper cut = (SensorContactWrapper) cuts.nextElement();
			cutArr[ctr++] = cut;
		}

		assertEquals(7, cutArr.length, "expected number of cuts");

		final SensorContactWrapper[] trimmed = TMAfromCuts.trimToHost(cutArr);
		assertEquals(4, trimmed.length, "expected number of cuts");
	}

	@Test
	public void testUndo() throws ExecutionException {
		final TrackWrapper tw = getLongerTrack();

		assertNotNull(tw);

		// get the sensor data
		final SensorWrapper sw = (SensorWrapper) tw.getSensors().elements().nextElement();

		assertNotNull(sw);

		// create a list of cuts (to simulate the selection)
		final SensorContactWrapper[] items = new SensorContactWrapper[sw.size()];
		final Enumeration<Editable> numer = sw.elements();
		int ctr = 0;
		while (numer.hasMoreElements()) {
			final SensorContactWrapper cut = (SensorContactWrapper) numer.nextElement();
			items[ctr++] = cut;
		}

		final Layers theLayers = new Layers();
		final WorldVector worldOffset = new WorldVector(Math.PI, 0.002, 0);
		final double tgtCourse = 0;
		final WorldSpeed tgtSpeed = new WorldSpeed(3, WorldSpeed.Kts);

		final Color newColor = Color.GREEN;

		theLayers.addThisLayer(tw);

		// check we haven't got the new color
		final Color oldColor = items[0].getColor();
		assertEquals(Color.YELLOW, oldColor, "correct original color");

		// ok, generate the target track
		final CMAPOperation op = new TMAfromCuts(items, theLayers, worldOffset, tgtCourse, tgtSpeed, newColor);

		// and run it
		op.execute(null, null);

		assertEquals(2, theLayers.size(), "has new data");

		final TrackWrapper sol = (TrackWrapper) theLayers.elementAt(1);
		assertNotNull(sol, "new layer not found");

		// ok, now undo the operation
		op.undo(null, null);

		assertEquals(1, theLayers.size(), "new track didn't get deleted");
	}

}
