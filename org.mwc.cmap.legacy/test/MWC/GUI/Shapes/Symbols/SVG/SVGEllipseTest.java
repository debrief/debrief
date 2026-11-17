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

import static org.junit.jupiter.api.Assertions.*;

import static MWC.GUI.Shapes.Symbols.SVG.SVGEllipse.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import MWC.GUI.Shapes.Symbols.SVG.SVGEllipse.*;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import MWC.GUI.CanvasType;

public class SVGEllipseTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	@Test
	public void testBasicSVGLine() {
		try {
			final String contentToLoad = "<ellipse id=\"Oval\" fill=\"#000000\" cx=\"50.0111093\" cy=\"44.2182422\" rx=\"31\" ry=\"30.7304336\" transform=\"matrix(1, 0, 0, 1, 0, 0)\"/>";
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
					contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

			final SVGEllipse newSVGEllipse = new SVGEllipse(doc.getDocumentElement());
			assert (newSVGEllipse._intX.length == 1);
			assert (newSVGEllipse._intY.length == 1);
			assert (newSVGEllipse._originalCoordinates.length == 1);
			assertEquals(50.0111093, newSVGEllipse._originalCoordinates[0].getX(), 1e-5);
			assertEquals(44.2182422, newSVGEllipse._originalCoordinates[0].getY(), 1e-5);
			assertEquals(31.0, newSVGEllipse._rx, 1e-5);
			assertEquals(30.7304336, newSVGEllipse._ry, 1e-5);
		} catch (final Exception e) {
			fail("failed to load a basic ellipse.");
		}
	}

}
