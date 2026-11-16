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
import static org.mwc.debrief.core.ContextOperations.GenerateTrackFromActiveCuts.*;
import org.mwc.debrief.core.ContextOperations.GenerateTrackFromActiveCuts.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import org.junit.jupiter.api.Test;

public final class GenerateTrackFromActiveCutsTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testIWork() {
		final TrackWrapper track = new TrackWrapper();

		// and add the fixes
		final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10, 4, 4, 4, 0);

		cal.set(2001, 10, 4, 4, 4, 0);
		track.addFix(new FixWrapper(
				new Fix(new HiResDate(cal.getTime().getTime(), 0), new WorldLocation(2.0, 2.0, 0.0), 12, 12)));

		cal.set(2001, 10, 4, 4, 4, 01);
		track.addFix(new FixWrapper(
				new Fix(new HiResDate(cal.getTime().getTime(), 0), new WorldLocation(2.0, 2.25, 0.0), 12, 12)));

		cal.set(2001, 10, 4, 4, 4, 02);
		track.addFix(new FixWrapper(
				new Fix(new HiResDate(cal.getTime().getTime(), 0), new WorldLocation(2.0, 2.5, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 05);
		track.addFix(new FixWrapper(
				new Fix(new HiResDate(cal.getTime().getTime(), 0), new WorldLocation(2.0, 2.75, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 23);
		track.addFix(new FixWrapper(
				new Fix(new HiResDate(cal.getTime().getTime(), 0), new WorldLocation(2.25, 2.0, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 25);
		track.addFix(new FixWrapper(
				new Fix(new HiResDate(cal.getTime().getTime(), 0), new WorldLocation(2.5, 2.0, 0.0), 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 28);
		final WorldLocation theLoc = new WorldLocation(2.75d, 2.0, 0.0);
		track.addFix(new FixWrapper(new Fix(new HiResDate(cal.getTime().getTime(), 0), theLoc, 12, 12)));
		cal.set(2001, 10, 4, 4, 4, 55);
		track.addFix(new FixWrapper(
				new Fix(new HiResDate(cal.getTime().getTime(), 0), new WorldLocation(2.25, 2.25, 0.0), 12, 12)));

		// and some sensor data, which goes past the end of O/S track
		final SensorWrapper sensor = new SensorWrapper("SensorName");

		cal.set(2001, 10, 4, 4, 4, 25);
		sensor.add(new SensorContactWrapper("track", new HiResDate(cal.getTime().getTime()),
				new WorldDistance(1000, WorldDistance.YARDS), 23.3d, null, Color.RED, "Some lable", 1,
				sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 27);
		sensor.add(new SensorContactWrapper("track", new HiResDate(cal.getTime().getTime()),
				new WorldDistance(1000, WorldDistance.YARDS), 23.3d, null, Color.RED, "Some lable", 1,
				sensor.getName()));

		cal.set(2001, 10, 4, 4, 4, 55);
		sensor.add(new SensorContactWrapper("track", new HiResDate(cal.getTime().getTime()),
				new WorldDistance(1000, WorldDistance.YARDS), 23.3d, null, Color.RED, "Some lable", 1,
				sensor.getName()));

		cal.set(2001, 10, 4, 4, 15, 05);
		sensor.add(new SensorContactWrapper("track", new HiResDate(cal.getTime().getTime()),
				new WorldDistance(1000, WorldDistance.YARDS), 23.3d, null, Color.RED, "Some lable", 1,
				sensor.getName()));

		track.add(sensor);
		final Layers layers = new Layers();
		layers.addThisLayer(track);

		final AtomicInteger ctr = new AtomicInteger(0);

		final ErrorLogger logger = new ErrorLogger() {

			@Override
			public void logError(final int status, final String text, final Exception e) {
				ctr.getAndIncrement();
			}

			@Override
			public void logError(final int status, final String text, final Exception e, final boolean revealLog) {
				ctr.getAndIncrement();
			}

			@Override
			public void logStack(final int status, final String text) {
				ctr.getAndIncrement();
			}
		};
		// now try to offer cuts
		final GenerateTrackFromActiveCuts genny = new GenerateTrackFromActiveCuts(logger);
		final Layer[] parentLayers = new Layer[] { track };
		final Editable[] subjects = new Editable[] { sensor };
		final DummyMenu parent = new DummyMenu();
		genny.generate(parent, layers, parentLayers, subjects);

		assertEquals(1, parent.items.size(), "items added");

		final Action item = (Action) parent.items.firstElement();
		item.run();

		assertEquals(1, ctr.get(), "Errors reported");
		assertEquals(2, layers.size(), "now has new track");

	}
}
