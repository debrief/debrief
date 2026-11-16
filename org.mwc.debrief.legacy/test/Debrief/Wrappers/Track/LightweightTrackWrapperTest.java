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

package Debrief.Wrappers.Track;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.Wrappers.Track.LightweightTrackWrapper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.Wrappers.Track.LightweightTrackWrapper.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.FixSetter;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.FireExtended;
import MWC.GUI.FireReformatted;
import MWC.GUI.ICantContainLayers;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Properties.LineWidthPropertyEditor;
import MWC.GUI.Properties.NullableLocationPropertyEditor;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor;
import MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor;
import MWC.GUI.Shapes.Symbols.Vessels.WorldScaledSym;
import MWC.GUI.Tools.Operations.RightClickCutCopyAdaptor.IsTransientForChildren;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import MWC.Utilities.Errors.Trace;
import org.junit.jupiter.api.Test;

public class LightweightTrackWrapperTest {
	private static interface IsValid {
		boolean isValid(FixWrapper fix);
	}

	private static FixWrapper create(final long date, final double lat, final double lon) {
		return new FixWrapper(new Fix(new HiResDate(date), new WorldLocation(lat, lon, 0), 0d, 0d));
	}

	private static int doCount(final LightweightTrackWrapper track, final IsValid aTest) {
		int ctr = 0;
		final Enumeration<Editable> pIter = track.getPositionIterator();
		while (pIter.hasMoreElements()) {
			final FixWrapper fix = (FixWrapper) pIter.nextElement();
			if (aTest.isValid(fix)) {
				ctr++;
			}
		}
		return ctr;
	}

	private static LightweightTrackWrapper getTrack() {
		final LightweightTrackWrapper track = new LightweightTrackWrapper();
		track.setName("light");
		track.addFix(create(10000, 2d, 2d));
		track.addFix(create(20000, 2d, 2d));
		track.addFix(create(30000, 2d, 2d));
		track.addFix(create(40000, 2d, 2d));
		track.addFix(create(50000, 2d, 2d));
		track.addFix(create(60000, 2d, 2d));
		track.addFix(create(70000, 2d, 2d));
		track.addFix(create(80000, 2d, 2d));
		track.addFix(create(90000, 2d, 2d));
		track.addFix(create(100000, 2d, 2d));
		return track;
	}

	@Test
	public final void testMyParams1() {
		Editable ed = new LightweightTrackWrapper("name", true, true, Color.red, 1);
		editableTesterSupport.testParams(ed);
		ed = null;
	}

	@Test
	public final void testMyParams2() {
		Editable ed = new LightweightTrackWrapper();
		editableTesterSupport.testParams(ed);
		ed = null;
	}

	@Test
	public void testResampleDown() {
		final LightweightTrackWrapper track = getTrack();
		track.setResampleDataAt(new HiResDate(5000));
		assertEquals(19, track._thePositions.size(), "correct size");
	}

	@Test
	public void testResampleUp() {
		final LightweightTrackWrapper track = getTrack();
		track.setResampleDataAt(new HiResDate(20000));
		assertEquals(5, track._thePositions.size(), "correct size");
	}

	@Test
	public void testSetFreq() {
		final LightweightTrackWrapper track = getTrack();

		final IsValid aTest = new IsValid() {
			@Override
			public boolean isValid(final FixWrapper fix) {
				return fix.getArrowShowing();
			}
		};
		final IsValid lTest = new IsValid() {
			@Override
			public boolean isValid(final FixWrapper fix) {
				return fix.getLabelShowing();
			}
		};
		final IsValid sTest = new IsValid() {
			@Override
			public boolean isValid(final FixWrapper fix) {
				return fix.getSymbolShowing();
			}
		};

		track.setArrowFrequency(new HiResDate(20000));
		int ctr = doCount(track, aTest);
		assertEquals(5, ctr, "correct arrows");

		track.setArrowFrequency(new HiResDate(0, TimePeriod.INVALID_TIME));
		ctr = doCount(track, aTest);
		assertEquals(10, ctr, "correct arrows");

		track.setArrowFrequency(new HiResDate(15000));
		ctr = doCount(track, aTest);
		assertEquals(6, ctr, "correct arrows");

		track.setSymbolFrequency(new HiResDate(10000));
		ctr = doCount(track, sTest);
		assertEquals(10, ctr, "correct symbols");

		track.setSymbolFrequency(new HiResDate(0, TimePeriod.INVALID_TIME));
		ctr = doCount(track, sTest);
		assertEquals(10, ctr, "correct symbols");

		track.setSymbolFrequency(new HiResDate(20000));
		ctr = doCount(track, sTest);
		assertEquals(5, ctr, "correct symbols");

		track.setLabelFrequency(new HiResDate(12000));
		ctr = doCount(track, lTest);
		assertEquals(8, ctr, "correct labels");

		track.setLabelFrequency(new HiResDate(0, TimePeriod.INVALID_TIME));
		ctr = doCount(track, lTest);
		assertEquals(10, ctr, "correct labels");

		track.setLabelFrequency(new HiResDate(20000));
		ctr = doCount(track, lTest);
		assertEquals(5, ctr, "correct labels");

	}
}
