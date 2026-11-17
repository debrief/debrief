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

import static MWC.GUI.Shapes.Symbols.SVG.SVGPolyline.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.GUI.Shapes.Symbols.SVG.SVGPolyline.*;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import MWC.GUI.CanvasType;

public class SVGPolylineTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	@Test
	public void testBasicSVGPolyline() {
		try {
			final String contentToLoad = "<polyline fill=\"#000000\" fill-opacity=\"0\" fill-rule=\"nonzero\" stroke=\"#000000\" stroke-width=\"5\" points=\"5 76 20 47 35 76 50 47 65 76 80 47 95 76\"/>";
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
					contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

			final SVGPolyline newPolyline = new SVGPolyline(doc.getDocumentElement());
			assertTrue(newPolyline._intX.length == 7);
			assertTrue(newPolyline._intY.length == 7);
			assertTrue(newPolyline._originalCoordinates.length == 7);
			assertEquals(5.0, newPolyline._originalCoordinates[0].getX(), 1e-5);
			assertEquals(76.0, newPolyline._originalCoordinates[0].getY(), 1e-5);
			assertEquals(20.0, newPolyline._originalCoordinates[1].getX(), 1e-5);
			assertEquals(47.0, newPolyline._originalCoordinates[1].getY(), 1e-5);
			assertEquals(35.0, newPolyline._originalCoordinates[2].getX(), 1e-5);
			assertEquals(76.0, newPolyline._originalCoordinates[2].getY(), 1e-5);
			assertEquals(50.0, newPolyline._originalCoordinates[3].getX(), 1e-5);
			assertEquals(47.0, newPolyline._originalCoordinates[3].getY(), 1e-5);
			assertEquals(65.0, newPolyline._originalCoordinates[4].getX(), 1e-5);
			assertEquals(76.0, newPolyline._originalCoordinates[4].getY(), 1e-5);
			assertEquals(80.0, newPolyline._originalCoordinates[5].getX(), 1e-5);
			assertEquals(47.0, newPolyline._originalCoordinates[5].getY(), 1e-5);
			assertEquals(95.0, newPolyline._originalCoordinates[6].getX(), 1e-5);
			assertEquals(76.0, newPolyline._originalCoordinates[6].getY(), 1e-5);
		} catch (final Exception e) {
			fail("failed to load a basic polyline.");
		}
	}

}
