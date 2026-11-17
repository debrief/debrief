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
import static org.mwc.debrief.core.ContextOperations.CopyBearingsToClipboard.*;
import org.mwc.debrief.core.ContextOperations.CopyBearingsToClipboard.*;
import java.awt.Color;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.ContextOperations.GenerateInfillSegment.GenerateInfillOperation;
import org.mwc.debrief.core.editors.PlotEditor;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class CopyBearingsToClipboardTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@SuppressWarnings("deprecation")
	private void doTestCopyBearings(final int startIndex, final int endIndex) throws ExecutionException {
		final Layers layers = new Layers();
		final TrackWrapper host = new TrackWrapper();
		host.setName("Host");
		final SensorWrapper sensor = new SensorWrapper("Sensor");
		host.add(sensor);
		layers.addThisLayer(host);

		for (int i = startIndex; i < endIndex; i++) {
			final Date newDate = new Date(2018, 01, 01, 02, i * 2, 0);
			final WorldLocation loc = new WorldLocation(12, 12 + i / 60, 0d);
			final Fix newF = new Fix(new HiResDate(newDate.getTime()), loc, 0, 12);
			final FixWrapper fix = new FixWrapper(newF);
			host.add(fix);

			final Date newDate2 = new Date(2018, 01, 01, 02, i * 2, 10);

			// public SensorContactWrapper(final String trackName, final HiResDate dtg,
			// final WorldDistance range, final Double bearingDegs,
			// final WorldLocation origin, final java.awt.Color color,
			// final String label, final int style, final String sensorName)
			if (i % 9 != 0) {
				final SensorContactWrapper contact = new SensorContactWrapper(host.getName(),
						new HiResDate(newDate2.getTime()), null, 12d, null, Color.red, "Some label", 1, "Sensor");
				sensor.add(contact);
			}
		}

		final TrackWrapper subject = new TrackWrapper();
		subject.setName("subject");
		layers.addThisLayer(subject);

		final SensorContactWrapper[] obs = new SensorContactWrapper[sensor.size()];
		Enumeration<Editable> ele = sensor.elements();
		int ctr = 0;
		while (ele.hasMoreElements()) {
			final SensorContactWrapper cut = (SensorContactWrapper) ele.nextElement();
			obs[ctr++] = cut;
		}

		final WorldVector offset = new WorldVector(12, 0.002, 0.0d);
		final RelativeTMASegment seg = new RelativeTMASegment(obs, offset, new WorldSpeed(12, WorldSpeed.Kts), 12d,
				layers, Color.green);
		subject.add(seg);

		assertEquals(53, seg.size(), "have valid segment");

		final FixWrapper split1 = (FixWrapper) seg.getData().toArray()[12];
		final FixWrapper split2 = (FixWrapper) seg.getData().toArray()[21];

		subject.splitTrack(split1, true);

		assertEquals(2, subject.getSegments().size(), "have legs");

		subject.splitTrack(split2, true);

		assertEquals(3, subject.getSegments().size(), "have legs");

		final Editable[] subjects = new Editable[3];
		int ctr2 = 0;
		ele = subject.getSegments().elements();
		while (ele.hasMoreElements()) {
			subjects[ctr2++] = ele.nextElement();
		}

		final GenerateInfillOperation operation = new GenerateInfillOperation("title", subjects, layers, subject,
				getMyLogger(), true);

		assertEquals(3, subject.getSegments().size(), "before infills");

		operation.execute(null, null);

		assertEquals(5, subject.getSegments().size(), "has infills");

		final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
		tracks.add(subject);

		// ok, now we can run the get bearings
		final CopyBearingData oper = new CopyBearingData("title", tracks, host);

		// check bearings are on clipboard.
		oper.execute(null, null);
	}

	@SuppressWarnings("deprecation")
	private void doTestPasteBearings(final int startIndex, final int endIndex, final int numPoints,
			final int menuItems) {
		final Layers layers = new Layers();
		final TrackWrapper host = new TrackWrapper();
		host.setName("Host");
		final SensorWrapper sensor = new SensorWrapper("Sensor");
		host.add(sensor);
		layers.addThisLayer(host);

		for (int i = startIndex; i < endIndex; i++) {
			final Date newDate = new Date(2018, 01, 01, 02, i * 2, 0);
			final WorldLocation loc = new WorldLocation(12, 12 + i / 60, 0d);
			final Fix newF = new Fix(new HiResDate(newDate.getTime()), loc, 0, 12);
			final FixWrapper fix = new FixWrapper(newF);
			host.add(fix);
		}

		final IMenuManager menu = new MenuManager();
		new CopyBearingsToClipboard().generatePasteAction(menu, layers, host, false);

		assertEquals(menuItems, menu.getItems().length, "have items");

		if (menu.getItems().length == 0) {
			// ok, we didn't create menu items, just leave
			return;
		}

		final ActionContributionItem first = (ActionContributionItem) menu.getItems()[1];
		final IAction action = first.getAction();

		assertEquals(1, layers.size(), "just one layer");

		action.run();

		assertEquals(2, layers.size(), "now two layers");

		final TrackWrapper dropped = (TrackWrapper) layers.findLayer("subject");
		assertNotNull(dropped, "found new layer");

		int ctr = 0;
		final Enumeration<Editable> iter = dropped.getPositionIterator();
		while (iter.hasMoreElements()) {
			ctr++;
			final FixWrapper thisF = (FixWrapper) iter.nextElement();
			assertNotNull(thisF.getName(), "has a name");
			assertTrue(thisF.getName().length() > 0, "name is non-null");
		}
		assertEquals(numPoints, ctr, "correct size");

	}

	private ErrorLogger getMyLogger() {
		return new ErrorLogger() {

			@Override
			public void logError(final int status, final String text, final Exception e) {
				CorePlugin.logError(status, text, e);
			}

			@Override
			public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
				logError(status, text, e);
			}

			@Override
			public void logStack(final int status, final String text) {
				logError(status, text, null);
			}
		};
	}

	@Test
	public void testCopyPaste() throws ExecutionException {
		doTestCopyBearings(0, 60);
		doTestPasteBearings(0, 60, 56, 3);
	}

	@Test
	public void testCopyTrackEarly() throws ExecutionException {
		doTestCopyBearings(10, 69);
		doTestPasteBearings(5, 55, 44, 3);
	}

	@Test
	public void testCopyTrackLate() throws ExecutionException {
		doTestCopyBearings(0, 60);
		doTestPasteBearings(5, 70, 52, 3);
	}

	@Test
	public void testCopyTrackNoOverlap() throws ExecutionException {
		doTestCopyBearings(0, 60);
		doTestPasteBearings(20, 70, 0, 0);
	}

}
