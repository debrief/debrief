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

package MWC.GUI.Shapes.Symbols.SVG;

import static MWC.GUI.Shapes.Symbols.SVG.SVGCircle.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.GUI.Shapes.Symbols.SVG.SVGCircle.*;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import MWC.GUI.CanvasType;

public class SVGCircleTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	@Test
	public void testBasicSVGCircle() {
		try {
			final String contentToLoad = "<circle id=\"origin\" cx=\"50\" cy=\"50\" r=\"2.5\" style=\"fill: rgb(255, 0, 0);\"/>";
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
					contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

			final SVGCircle newSVGCircle = new SVGCircle(doc.getDocumentElement());
			assertTrue(newSVGCircle._intX.length == 1);
			assertTrue(newSVGCircle._intY.length == 1);
			assertTrue(newSVGCircle._originalCoordinates.length == 1);
			assertEquals(50.0, newSVGCircle._originalCoordinates[0].getX(), 1e-5);
			assertEquals(50.0, newSVGCircle._originalCoordinates[0].getY(), 1e-5);
			assertEquals(2.5, newSVGCircle._r, 1e-5);
		} catch (final Exception e) {
			fail("failed to load a basic circle.");
		}
	}

	@Test
	public void testBasicSVGElementFilled() {
		try {
			final String contentToLoad = "<circle id=\"origin\" cx=\"50\" cy=\"50\" r=\"2.5\" style=\"fill: rgb(255, 0, 0);\"/>";
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
					contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

			final SVGElement newSVGCircle = new SVGCircle(doc.getDocumentElement());
			assertFalse(newSVGCircle.dontFillObject);
		} catch (final Exception e) {
			fail("failed to load a basic element - circle filled.");
		}
	}

	@Test
	public void testBasicSVGElementNotFilled() {
		try {
			final String contentToLoad = "<circle id=\"origin\" cx=\"50\" cy=\"50\" r=\"2.5\" style=\"fill: none;\"/>";
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
					contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

			final SVGElement newSVGCircle = new SVGCircle(doc.getDocumentElement());
			assert (newSVGCircle.dontFillObject);
		} catch (final Exception e) {
			fail("failed to load a basic element - circle filled.");
		}
	}

	@Test
	public void testBasicSVGElementNotFilledSecondWay() {
		try {
			final String contentToLoad = "<circle id=\"origin\" cx=\"50\" cy=\"50\" r=\"2.5\" fill-opacity=\"0\"/>";
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
					contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

			final SVGElement newSVGCircle = new SVGCircle(doc.getDocumentElement());
			assert (newSVGCircle.dontFillObject);
		} catch (final Exception e) {
			fail("failed to load a basic element - circle filled.");
		}
	}

}
