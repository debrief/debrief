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

import static Debrief.ReaderWriter.Replay.ImportDynamicPolygon.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import Debrief.ReaderWriter.Replay.ImportDynamicPolygon.*;
import java.text.ParseException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import Debrief.Wrappers.DynamicPolygonShapeWrapper;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Shapes.PolygonShape;
import MWC.GUI.Shapes.PolygonShape.PolygonNode;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.AbstractPlainLineImporter;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;
import org.junit.jupiter.api.Test;

public class ImportDynamicPolygonTest {

	@Test
	public void testWithLabel() throws ParseException {
		final String line = " ;POLY_RECT: @J \"Dynamic A\" 951212 051000.000 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E label";
		final ImportDynamicPolygon ip = new ImportDynamicPolygon();
		final DynamicPolygonShapeWrapper res = (DynamicPolygonShapeWrapper) ip.readThisLine(line);
		assertEquals("label", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");

		final Vector<PolygonNode> nodes = polygon.getPoints();
		assertEquals(2, nodes.size());

		assertEquals("1", nodes.get(0).getName());
		WorldLocation loc = nodes.get(0).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "1 correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "1 correct long");

		assertEquals("2", nodes.get(1).getName());
		loc = nodes.get(1).getLocation();
		assertEquals(49.6405, loc.getLat(), 0.0001, "2 correct long");
		assertEquals(4.39945, loc.getLong(), 0.0001, "2 correct lat");
		assertEquals("Dynamic A", res.getTrackName(), "Track name isn't correct");
		assertEquals(818745000000000l, res.getStartDTG().getMicros(), "Start DTG isn't correct");
	}
}
