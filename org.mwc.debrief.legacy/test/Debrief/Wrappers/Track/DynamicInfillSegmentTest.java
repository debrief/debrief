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

import static Debrief.Wrappers.Track.DynamicInfillSegment.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.Wrappers.Track.DynamicInfillSegment.*;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Enumeration;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.junit.jupiter.api.Test;
import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support.BaseItemLayer;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottables.DeleteWithCare;
import MWC.GUI.ToolParent;
import MWC.GUI.Properties.DebriefColors;
import MWC.GenericData.ColoredWatchable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public class DynamicInfillSegmentTest {
	@Test
	public void testRound() {
		assertEquals(6000, roundToInterval(6100));
		assertEquals(100, roundToInterval(100));
		assertEquals(1000, roundToInterval(1100));
		assertEquals(59000, roundToInterval(59900));
		assertEquals(60000, roundToInterval(69900));
		assertEquals(2220000, roundToInterval(2269900));
	}

	@Test
	public void testSimple() {
		// generate the location spline
		final double[] times = new double[] { 500, 2000, 4000, 5000 };
		final double[] longs = new double[] { 1d, 1d, 3d, 4d };
		final double[] lats = new double[] { 1d, 2d, 4d, 5d };

		final UnivariateInterpolator interpolator = new SplineInterpolator();
		final UnivariateFunction latInterp = interpolator.interpolate(times, lats);
		final UnivariateFunction longInterp = interpolator.interpolate(times, longs);

		assertEquals(2.447, latInterp.value(2500), 0.01);
		assertEquals(1.3421, longInterp.value(2500), 0.001);
	}
}
