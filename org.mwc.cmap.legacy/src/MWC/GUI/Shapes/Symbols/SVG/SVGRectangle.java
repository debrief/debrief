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

public class SVGRectangle extends SVGElement
{

  public SVGRectangle(Element dom)
  {
    super(dom);
    try
    {
      double x = Double.parseDouble(getDom().getAttribute("x"));
      double y = Double.parseDouble(getDom().getAttribute("y"));
      double width = Double.parseDouble(getDom().getAttribute("width"));
      double height = Double.parseDouble(getDom().getAttribute("height"));

      _originalCoordinates = new java.awt.geom.Point2D[4];

      _originalCoordinates[0] = new java.awt.geom.Point2D.Double(x, y);
      _originalCoordinates[1] = new java.awt.geom.Point2D.Double(x + width, y);
      _originalCoordinates[2] = new java.awt.geom.Point2D.Double(x + width, y
          + height);
      _originalCoordinates[3] = new java.awt.geom.Point2D.Double(x, y + height);

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

    if (!dontFillObject)
    {
      dest.fillPolygon(_intX, _intY, _intX.length);
    }
    else
    {
      dest.drawPolygon(_intX, _intY, _intX.length);
    }

  }

  public static class SVGRectangleTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public void testBasicRectangle()
    {
      try
      {
        final String contentToLoad =
            "<rect id=\"Rectangle-2\" fill=\"#000000\" x=\"24.5\" y=\"23\" width=\"52\" height=\"21\"/>";
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
            .newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
            contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

        SVGRectangle newRectangle = new SVGRectangle(doc.getDocumentElement());
        assertTrue(newRectangle._intX.length == 4);
        assertTrue(newRectangle._intY.length == 4);
        assertTrue(newRectangle._originalCoordinates.length == 4);
        assertEquals(24.5, newRectangle._originalCoordinates[0].getX(), 1e-5);
        assertEquals(23.0, newRectangle._originalCoordinates[0].getY(), 1e-5);
        assertEquals(76.5, newRectangle._originalCoordinates[1].getX(), 1e-5);
        assertEquals(23.0, newRectangle._originalCoordinates[1].getY(), 1e-5);
        assertEquals(76.5, newRectangle._originalCoordinates[2].getX(), 1e-5);
        assertEquals(44.0, newRectangle._originalCoordinates[2].getY(), 1e-5);
        assertEquals(24.5, newRectangle._originalCoordinates[3].getX(), 1e-5);
        assertEquals(44.0, newRectangle._originalCoordinates[3].getY(), 1e-5);
      }
      catch (Exception e)
      {
        fail("failed to load a basic rectangle.");
      }
    }

  }
}
