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

package Debrief.ReaderWriter.Replay;

import static org.junit.jupiter.api.Assertions.*;

import static Debrief.ReaderWriter.Replay.ImportEllipse2.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Debrief.ReaderWriter.Replay.ImportEllipse2.*;
import java.text.ParseException;
import java.util.StringTokenizer;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.EllipseShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

// ////////////////////////////////////////////////
// testing code...
// ////////////////////////////////////////////////
public class ImportEllipse2Test {
	static public final String TEST_ALL_TEST_TYPE = "CONV";


	@Test
	public void testValues() throws ParseException {
		final String iLine = ";ELLIPSE2: @F 951212 060200 951212 061200 21.8 0 0 N 21.5 0 0 W 45.0 5000 3000 test ellipse";
		final AbstractPlainLineImporter iff = new ImportEllipse2();
		final ShapeWrapper res = (ShapeWrapper) iff.readThisLine(iLine);

		// and check the result
		assertNotNull(res, "read it in");
		assertEquals("test ellipse", res.getLabel(), "right track");
		assertEquals(new java.awt.Color(255, 150, 0), res.getColor(), "right color");
		assertEquals("test ellipse", res.getName(), "right name");
		assertNotNull(res.getStartDTG(), "start present");
		assertNotNull(res.getEndDTG(), "end present");
		final long diff = (res.getEndDTG().getMicros() - res.getStartDTG().getMicros()) / 1000;
		assertEquals(600000, diff, "correct interval");
		final EllipseShape ell = (EllipseShape) res.getShape();
		assertEquals(45.0, ell.getOrientation(), "right orient");
		assertEquals(5000, ell.getMaxima().getValueIn(WorldDistance.YARDS), 0.001, "right maxima");
		assertEquals(3000, ell.getMinima().getValueIn(WorldDistance.YARDS), 0.001, "right minima");
	}
}
