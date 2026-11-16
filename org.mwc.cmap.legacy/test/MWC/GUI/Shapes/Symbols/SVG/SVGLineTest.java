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

import static MWC.GUI.Shapes.Symbols.SVG.SVGLine.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.GUI.Shapes.Symbols.SVG.SVGLine.*;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import MWC.GUI.CanvasType;

public class SVGLineTest {

	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	@Test
	public void testBasicSVGLine() {
		try {
			final String contentToLoad = "<line style=\"stroke: rgb(0, 0, 0); fill: rgb(10, 0, 0); stroke-width: 6;\" x1=\"91.425\" y1=\"27.093\" x2=\"59.992\" y2=\"57.483\"/>";
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
					contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

			final SVGLine newSVGLine = new SVGLine(doc.getDocumentElement());
			assertTrue(newSVGLine._intX.length == 2);
			assertTrue(newSVGLine._intY.length == 2);
			assertTrue(newSVGLine._originalCoordinates.length == 2);
			assertEquals(91.425, newSVGLine._originalCoordinates[0].getX(), 1e-5);
			assertEquals(27.093, newSVGLine._originalCoordinates[0].getY(), 1e-5);
			assertEquals(59.992, newSVGLine._originalCoordinates[1].getX(), 1e-5);
			assertEquals(57.483, newSVGLine._originalCoordinates[1].getY(), 1e-5);
		} catch (final Exception e) {
			fail("failed to load a basic line.");
		}
	}

}
