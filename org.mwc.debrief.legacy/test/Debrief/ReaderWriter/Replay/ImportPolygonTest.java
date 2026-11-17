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

import static Debrief.ReaderWriter.Replay.ImportPolygon.*;
import static org.junit.jupiter.api.Assertions.*;

import Debrief.ReaderWriter.Replay.ImportPolygon.*;
import java.text.ParseException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import Debrief.Wrappers.PolygonWrapper;
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

public class ImportPolygonTest {

	@Test
	public void testImport() throws ParseException {
		final String line = ";POLY: @@ 49 43 49.08 N 004 10 11.60 E 49 38 25.80 N 004 23 58.02 E label";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper sw = (ShapeWrapper) ip.readThisLine(line);
		assertEquals(line, ip.exportThis(sw));
		final PolygonShape polygon = (PolygonShape) sw.getShape();
		assertNotNull(polygon, "found shape");
		assertEquals(2, polygon.getPoints().size(), "correct numb nodes");
	}

	@Test
	public void testImportAlphaLabel() throws ParseException {
		final String line = ";POLY: @@ 120505 140011 120505 150011 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E numlabel";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("numlabel", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");

		final Vector<PolygonNode> nodes = polygon.getPoints();
		assertEquals(2, nodes.size());

		assertEquals("1", nodes.get(0).getName());
		WorldLocation loc = nodes.get(0).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "correct long");

		assertEquals("2", nodes.get(1).getName());
		loc = nodes.get(1).getLocation();
		assertEquals(49.6405, loc.getLat(), 0.0001, "correct long");
		assertEquals(4.39945, loc.getLong(), 0.0001, "correct lat");
	}

	// supported format
	// ;POLY: @@ YYMMDD HHMMSS YYMMDD HHMMSS DD MM SS.S H DDD MM SS.S H DD MM SS.S H
	// DDD MM SS.S H
	// DD MM SS.S H DDD MM SS.S H
	@Test
	public void testImportAlphaNumericLabel() throws ParseException {
		final String line = ";POLY: @@ 120505 140011 120505 150011 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E 22numlabel";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("22numlabel", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");

		final Vector<PolygonNode> nodes = polygon.getPoints();
		assertEquals(2, nodes.size());

		assertEquals("1", nodes.get(0).getName());
		WorldLocation loc = nodes.get(0).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "correct long");

		assertEquals("2", nodes.get(1).getName());
		loc = nodes.get(1).getLocation();
		assertEquals(49.6405, loc.getLat(), 0.0001, "correct long");
		assertEquals(4.39945, loc.getLong(), 0.0001, "correct lat");
	}

	@Test
	public void testImportMultiWordAlphaLabel() throws ParseException {
		final String line = ";POLY: @@ 120505 140011 120505 150011 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E numlabel second third";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("numlabel second third", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");

		final Vector<PolygonNode> nodes = polygon.getPoints();
		assertEquals(2, nodes.size());

		assertEquals("1", nodes.get(0).getName());
		WorldLocation loc = nodes.get(0).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "correct long");

		assertEquals("2", nodes.get(1).getName());
		loc = nodes.get(1).getLocation();
		assertEquals(49.6405, loc.getLat(), 0.0001, "correct long");
		assertEquals(4.39945, loc.getLong(), 0.0001, "correct lat");
	}

	@Test
	public void testImportMultiWordAlphaNumericLabel() throws ParseException {
		final String line = ";POLY: @@ 120505 140011 120505 150011 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E 22numlabel second third";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("22numlabel second third", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");

		final Vector<PolygonNode> nodes = polygon.getPoints();
		assertEquals(2, nodes.size());

		assertEquals("1", nodes.get(0).getName());
		WorldLocation loc = nodes.get(0).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "correct long");

		assertEquals("2", nodes.get(1).getName());
		loc = nodes.get(1).getLocation();
		assertEquals(49.6405, loc.getLat(), 0.0001, "correct long");
		assertEquals(4.39945, loc.getLong(), 0.0001, "correct lat");
	}

	@Test
	public void testIsName() {
		assertTrue(isNameLabel("23A"));
		assertTrue(isNameLabel("A23"));
		assertTrue(isNameLabel("23-A"));
		assertTrue(isNameLabel("A-23"));
		assertFalse(isNameLabel("23"));
		assertFalse(isNameLabel("23.5"));
	}

	@Test
	public void testLeadingSpace() throws ParseException {
		final String line = "    	;POLY: @J 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E 49.7303 0 0 N 4.16989 0 0 E";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");

		final Vector<PolygonNode> nodes = polygon.getPoints();
		assertEquals(3, nodes.size());

		assertEquals("1", nodes.get(0).getName());
		WorldLocation loc = nodes.get(0).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "correct long");

		assertEquals("2", nodes.get(1).getName());
		loc = nodes.get(1).getLocation();
		assertEquals(49.6405, loc.getLat(), 0.0001, "correct long");
		assertEquals(4.39945, loc.getLong(), 0.0001, "correct lat");

		assertEquals("3", nodes.get(2).getName());
		loc = nodes.get(2).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "correct long");
	}

	@Test
	public void testNoLabel() throws ParseException {
		final String line = ";POLY: @@ 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");

		final Vector<PolygonNode> nodes = polygon.getPoints();
		assertEquals(2, nodes.size());

		assertEquals("1", nodes.get(0).getName());
		WorldLocation loc = nodes.get(0).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "correct long");

		assertEquals("2", nodes.get(1).getName());
		loc = nodes.get(1).getLocation();
		assertEquals(49.6405, loc.getLat(), 0.0001, "correct long");
		assertEquals(4.39945, loc.getLong(), 0.0001, "correct lat");
	}

	@Test
	public void testWeirdImport1() throws ParseException {
		final String line = ";POLY: @EA0 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E 51.6405 0 0 N 3.39945 0 0 E numlabel";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("numlabel", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");
		assertEquals(3, polygon.getPoints().size(), "correct nodes");
	}

	@Test
	public void testWeirdImport2() throws ParseException {
		final String line = ";POLY: @EA0 49 0 0.7303 N 4 0 0.16989 E 49 0 0.6405 N 4 0 0.39945 E 51 0 0.6405 N 3 0 0.39945 E numlabel";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("numlabel", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");
		assertEquals(3, polygon.getPoints().size(), "correct nodes");
	}

	@Test
	public void testWithCompoundLabel() throws ParseException {
		final String line = ";POLY: @J 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E test long label";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("test long label", res.getLabel());
	}

	@Test
	public void testWithLabel() throws ParseException {
		final String line = " ;POLY: @J 120505 120505 120505 130505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E label";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
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
	}

	@Test
	public void testWithoutDates() throws ParseException {
		final String line = ";POLY: @@ 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");

		final Vector<PolygonNode> nodes = polygon.getPoints();
		assertEquals(2, nodes.size());

		assertEquals("1", nodes.get(0).getName());
		WorldLocation loc = nodes.get(0).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "correct long");

		assertEquals("2", nodes.get(1).getName());
		loc = nodes.get(1).getLocation();
		assertEquals(49.6405, loc.getLat(), 0.0001, "correct long");
		assertEquals(4.39945, loc.getLong(), 0.0001, "correct lat");
	}

	@Test
	public void testWithoutEndDate() throws ParseException {
		final String line = ";POLY: @@ 120505 120505 49.7303 0 0 N 4.16989 0 0 E 49.6405 0 0 N 4.39945 0 0 E";
		final ImportPolygon ip = new ImportPolygon();
		final ShapeWrapper res = (ShapeWrapper) ip.readThisLine(line);
		assertEquals("", res.getLabel());
		assertNotNull(res, "read it in");
		final PolygonShape polygon = (PolygonShape) res.getShape();
		assertNotNull(polygon, "found shape");

		final Vector<PolygonNode> nodes = polygon.getPoints();
		assertEquals(2, nodes.size());

		assertEquals("1", nodes.get(0).getName());
		WorldLocation loc = nodes.get(0).getLocation();
		assertEquals(49.7303, loc.getLat(), 0.0001, "correct lat");
		assertEquals(4.16989, loc.getLong(), 0.0001, "correct long");

		assertEquals("2", nodes.get(1).getName());
		loc = nodes.get(1).getLocation();
		assertEquals(49.6405, loc.getLat(), 0.0001, "correct long");
		assertEquals(4.39945, loc.getLong(), 0.0001, "correct lat");
	}
}
