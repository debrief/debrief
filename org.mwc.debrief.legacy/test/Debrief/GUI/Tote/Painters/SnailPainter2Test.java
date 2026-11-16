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

package Debrief.GUI.Tote.Painters;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.GUI.Tote.Painters.SnailPainter2.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Debrief.GUI.Tote.Painters.SnailPainter2.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Vector;
import Debrief.GUI.Tote.AnalysisTote;
import Debrief.Wrappers.ShapeWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.CanvasType.PaintListener;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class SnailPainter2Test {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testFade() {
		final ColorFadeCalculator fader = new ColorFadeCalculator(1000, new HiResDate(10000));
		assertEquals(0f, fader.fadeAt(new HiResDate(9000)));
		assertEquals(1f, fader.fadeAt(new HiResDate(10000)));
		assertEquals(0.5f, fader.fadeAt(new HiResDate(9500)));

		final Color base = Color.red;
		final Color faded = fader.fadeColorAt(base, new HiResDate(9500));
		assertEquals(127, faded.getAlpha(), "half fade");
		assertEquals(255, faded.getRed(), "right red");
		assertEquals(0, faded.getGreen(), "right greem");
		assertEquals(0, faded.getBlue(), "right blue");
	}

	@Test
	public final void testMyParams() {
		Editable ed = new SnailPainter2(null, null, null);
		editableTesterSupport.testParams(ed);
		ed = null;
	}

}
