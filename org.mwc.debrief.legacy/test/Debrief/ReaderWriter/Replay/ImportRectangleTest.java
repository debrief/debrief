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

import static Debrief.ReaderWriter.Replay.ImportRectangle.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Replay.ImportRectangle.*;
import java.awt.Color;
import java.text.ParseException;
import java.util.StringTokenizer;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.RectangleShape;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.junit.jupiter.api.Test;

public class ImportRectangleTest {

	// TODO FIX-TEST
	public void NtestLeadingSpace() {
		final String line1 = "	;RECT: @J  49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
		final ImportRectangle ir = new ImportRectangle();
		final ShapeWrapper res = (ShapeWrapper) ir.readThisLine(line1);
		assertNotNull(res, "read it in");
		assertNull(res.getLabel());
		final RectangleShape rect = (RectangleShape) res.getShape();
		assertNotNull(rect, "found shape");
		assertEquals(49.7303, rect.getCorner_TopLeft().getLat(), 0.0001, "correct tl lat");
		assertEquals(4.16989, rect.getCorner_TopLeft().getLong(), 0.0001, "correct tl long");
		assertEquals(49.6405, rect.getCornerBottomRight().getLat(), 0.0001, "correct br long");
		assertEquals(4.39945, rect.getCornerBottomRight().getLong(), 0.0001, "correct br lat");
	}

	// TODO FIX-TEST
	public void NtestNoLabel() {
		final String line1 = ";RECT: @J  49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
		final ImportRectangle ir = new ImportRectangle();
		final ShapeWrapper res = (ShapeWrapper) ir.readThisLine(line1);
		assertNotNull(res, "read it in");
		assertNull(res.getLabel());
		final RectangleShape rect = (RectangleShape) res.getShape();
		assertNotNull(rect, "found shape");
		assertEquals(49.7303, rect.getCorner_TopLeft().getLat(), 0.0001, "correct tl lat");
		assertEquals(4.16989, rect.getCorner_TopLeft().getLong(), 0.0001, "correct tl long");
		assertEquals(49.6405, rect.getCornerBottomRight().getLat(), 0.0001, "correct br long");
		assertEquals(4.39945, rect.getCornerBottomRight().getLong(), 0.0001, "correct br lat");
	}

	@Test
	public void testWithLabel() {
		final String line1 = ";RECT: @J  49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E label";
		final ImportRectangle ir = new ImportRectangle();
		final ShapeWrapper res = (ShapeWrapper) ir.readThisLine(line1);
		assertNotNull(res, "read it in");
		assertNotNull(res.getLabel());
		final RectangleShape rect = (RectangleShape) res.getShape();
		assertNotNull(rect, "found shape");
		assertEquals(49.7303, rect.getCorner_TopLeft().getLat(), 0.0001, "correct tl lat");
		assertEquals(4.16989, rect.getCorner_TopLeft().getLong(), 0.0001, "correct tl long");
		assertEquals(49.6405, rect.getCornerBottomRight().getLat(), 0.0001, "correct br long");
		assertEquals(4.39945, rect.getCornerBottomRight().getLong(), 0.0001, "correct br lat");
	}
}
