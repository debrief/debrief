/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI.Shapes.Symbols.SVG;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.CanvasType;

public class SVGLine extends SVGElement
{

  public SVGLine(Element dom)
  {
    super(dom);
    try
    {
      _originalCoordinates = new java.awt.geom.Point2D[2];

      _originalCoordinates[0] = new java.awt.geom.Point2D.Double(Double
          .parseDouble(getDom().getAttribute("x1")), Double.parseDouble(getDom()
              .getAttribute("y1")));
      _originalCoordinates[1] = new java.awt.geom.Point2D.Double(Double
          .parseDouble(getDom().getAttribute("x2")), Double.parseDouble(getDom()
              .getAttribute("y2")));

      _intX = new int[_originalCoordinates.length];
      _intY = new int[_originalCoordinates.length];
    }
    catch (Exception e)
    {
      MWC.Utilities.Errors.Trace.trace("Invalid SVG Format");
    }
  }

  @Override
  public void render(final CanvasType dest, final double sym_size,
      final java.awt.Point origin_coords, final double rotation_degs,
      final java.awt.Point rotationPoint, final java.awt.Color defaultColor)
  {
    super.render(dest, sym_size, origin_coords, rotation_degs, rotationPoint,
        defaultColor);

    dest.drawLine((int) _intX[0], (int) _intY[0], (int) _intX[1],
        (int) _intY[1]);
  }


  public static class SVGLineTest extends junit.framework.TestCase
  {

    static public final String TEST_ALL_TEST_TYPE = "UNIT";
    public void testBasicSVGLine()
    {
      try
      {
        final String contentToLoad =
            "<line style=\"stroke: rgb(0, 0, 0); fill: rgb(10, 0, 0); stroke-width: 6;\" x1=\"91.425\" y1=\"27.093\" x2=\"59.992\" y2=\"57.483\"/>";
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
            .newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
            contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

        SVGLine newSVGLine = new SVGLine(doc.getDocumentElement());
        assertTrue(newSVGLine._intX.length == 2);
        assertTrue(newSVGLine._intY.length == 2);
        assertTrue(newSVGLine._originalCoordinates.length == 2);
        assertEquals(91.425, newSVGLine._originalCoordinates[0].getX(), 1e-5);
        assertEquals(27.093, newSVGLine._originalCoordinates[0].getY(), 1e-5);
        assertEquals(59.992, newSVGLine._originalCoordinates[1].getX(), 1e-5);
        assertEquals(57.483, newSVGLine._originalCoordinates[1].getY(), 1e-5);
      }
      catch (Exception e)
      {
        fail("failed to load a basic line.");
      }
    }

  }
}
