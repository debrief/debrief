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

import static MWC.GUI.Shapes.Symbols.SVG.SVGShape.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import MWC.GUI.Shapes.Symbols.SVG.SVGShape.*;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import MWC.GUI.CanvasType;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GUI.Shapes.Symbols.SymbolFactory;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;

public class SVGShapeTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";


	@Test
	public void testLoadingFileDoesntExit() {
		final String fileDoesntExist = "fileDoesntExit";
		final SVGShape svgShape = new SVGShape(fileDoesntExist);
		svgShape.paint(null, null, 0);

		assertTrue(svgShape.getElements() == null);
	}
}
