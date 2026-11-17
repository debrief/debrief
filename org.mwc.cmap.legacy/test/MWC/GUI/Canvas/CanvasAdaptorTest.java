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

package MWC.GUI.Canvas;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GUI.Canvas.CanvasAdaptor.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import MWC.GUI.Canvas.CanvasAdaptor.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.ImageObserver;
import java.util.Vector;
import MWC.GUI.CanvasType;
import MWC.GenericData.WorldLocation;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class CanvasAdaptorTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public final void testPolygonMgt() {
		final int[] points = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		// convert to normal format
		final int len = points.length / 2;
		final int[] xP = new int[len];
		final int[] yP = new int[len];

		for (int i = 0; i < points.length; i += 2) {
			xP[i / 2] = points[i];
			yP[i / 2] = points[i + 1];
		}
		assertEquals(len, 5, "array wrong length");
		assertEquals(xP[0], 1, "wrong first x");
		assertEquals(yP[0], 2, "wrong first y");
		assertEquals(xP[len - 1], 9, "wrong last x");
		assertEquals(yP[len - 1], 10, "wrong last y");
	}
}
