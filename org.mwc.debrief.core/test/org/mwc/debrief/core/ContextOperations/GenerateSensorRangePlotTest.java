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
import static org.mwc.debrief.core.ContextOperations.GenerateSensorRangePlot.*;
import org.mwc.debrief.core.ContextOperations.GenerateSensorRangePlot.*;
import java.awt.Color;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.xyplot.views.XYPlotView;
import org.mwc.debrief.core.DebriefPlugin;
import Debrief.Tools.Tote.Calculations.rangeCalc;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.SplittableLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.TacticalData.temporal.TimeProvider;
import org.junit.jupiter.api.Test;

public class GenerateSensorRangePlotTest {
	private static SensorContactWrapper getContact(final long time, final SensorWrapper host) {
		final SensorContactWrapper res = new SensorContactWrapper();
		res.setDTG(new HiResDate(time));
		res.setSensor(host);
		return res;
	}

	private static FixWrapper getFix(final long time, final WorldLocation loc, final TrackWrapper track) {
		final Fix theFix = new Fix(new HiResDate(time), loc, 0, 0);
		final FixWrapper res = new FixWrapper(theFix);
		res.setTrackWrapper(track);
		return res;
	}

	protected Vector<SensorWrapper> _candidates;

	protected Vector<TrackWrapper> _primary;

	// TODO FIX-TEST
	public void NtestIt() {
		final TrackWrapper ownship = new TrackWrapper();
		ownship.setName("ownship");
		for (int i = 0; i < 35; i++) {
			final FixWrapper scw = getFix(i * 4000, new WorldLocation(1, i, 0), ownship);
			ownship.add(scw);
		}

		final SensorWrapper sw = new SensorWrapper("s1");
		sw.setHost(ownship);
		for (int i = 0; i < 5; i++) {
			final SensorContactWrapper scw = getContact(i * 5000, sw);
			sw.add(scw);
		}
		final SensorWrapper s2 = new SensorWrapper("s12");
		s2.setHost(ownship);
		for (int i = 0; i < 7; i++) {
			final SensorContactWrapper scw = getContact(i * 6000, s2);
			s2.add(scw);
		}

		final TrackWrapper target = new TrackWrapper();
		target.setName("target");

		for (int i = 0; i < 25; i++) {
			final FixWrapper scw = getFix(i * 5000, new WorldLocation(1, i, 0), target);
			target.add(scw);
		}

		final Vector<SensorWrapper> sensors = new Vector<SensorWrapper>();
		sensors.add(sw);
		sensors.add(s2);
		final HiResDate start_time = target.getStartDTG();
		final HiResDate end_time = target.getEndDTG();

		final TimeSeriesCollection data = new TimeSeriesCollection();
		GenerateSensorRangePlot.addDataSeries(target, sensors, start_time, end_time, data, true);

		assertNotNull(data, "got dataset");
		assertEquals(2, data.getSeriesCount(), "got correct num of series");
		final TimeSeries first = data.getSeries(0);
		assertNotNull(first, "got first series");
		assertEquals(5, first.getItemCount(), "got data");
		final TimeSeries second = data.getSeries(1);
		assertNotNull(second, "got second series");
		assertEquals(7, second.getItemCount(), "got data");
	}

	// TODO FIX-TEST
	public void NtestMenu() {
		final TrackWrapper ownship = new TrackWrapper();
		ownship.setName("trackA");
		for (int i = 0; i < 5; i++) {
			final FixWrapper scw = getFix(i * 5000, new WorldLocation(1, i, 0), ownship);
			ownship.add(scw);
		}

		final SensorWrapper sw = new SensorWrapper("s1");
		sw.setHost(ownship);
		sw.setSensorOffset(new ArrayLength(200));
		for (int i = 0; i < 5; i++) {
			final SensorContactWrapper scw = getContact(i * 5000, sw);
			sw.add(scw);
		}

		final TrackWrapper target = new TrackWrapper();
		target.setName("trackB");

		for (int i = 0; i < 5; i++) {
			final FixWrapper scw = getFix(i * 5000, new WorldLocation(1, i, 0), target);
			target.add(scw);
		}

		final GenerateSensorRangePlot plot = new GenerateSensorRangePlot() {
			@Override
			protected Action getAction(final Vector<SensorWrapper> candidates, final Vector<TrackWrapper> primary) {
				_candidates = candidates;
				_primary = primary;
				return new Action("some") {
				};
			}
		};
		final Editable[] subjects = new Editable[2];
		subjects[0] = sw;
		subjects[1] = target;
		final IMenuManager parent = new MenuManager();
		plot.generate(parent, null, null, subjects);

		assertNotNull(parent, "got menu back");
		final IContributionItem firstOne = parent.getItems()[0];
		assertNotNull(firstOne, "got menu item back");
		assertTrue(firstOne instanceof Separator, "got separator");
		final IContributionItem secondOne = parent.getItems()[1];
		assertNotNull(secondOne, "got menu item back");
		assertTrue(secondOne instanceof ActionContributionItem, "got menu item");

		// check items got set
		assertNotNull(_candidates);
		assertEquals(1, _candidates.size());
	}

	@Test
	public void testDumy() {

	}
}
