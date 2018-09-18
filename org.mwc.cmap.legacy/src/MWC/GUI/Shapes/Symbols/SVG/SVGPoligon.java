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

public class SVGPoligon extends SVGPolyline
{

  public SVGPoligon(Element dom)
  {
    super(dom);
  }

  @Override
  public void render(CanvasType dest, double sym_size, java.awt.Point origin_coords,
      double rotation_degs, final java.awt.Point rotationPoint, final java.awt.Color defaultColor)
  {
    super.render(dest, sym_size, origin_coords, rotation_degs, rotationPoint, defaultColor);
    if (!dontFillObject)
    {
      dest.fillPolygon(_intX, _intY, _intX.length);
    }
    else
    {
      dest.drawPolygon(_intX, _intY, _intX.length);
    }
  }


  public static class SVGPolygoneTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public void testBasicSVGPolygone()
    {
      try
      {
        final String contentToLoad =
            "<polygon id=\"Rectangle\" fill=\"#000000\" points=\"7 42.5 95 42.5 87 74 14.5 74\" style=\"pointer-events: none;\"/>";
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
            .newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
            contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

        SVGPoligon newPoligone = new SVGPoligon(doc.getDocumentElement());
        assertTrue(newPoligone._intX.length == 4);
        assertTrue(newPoligone._intY.length == 4);
        assertTrue(newPoligone._originalCoordinates.length == 4);
        assertEquals(7.0, newPoligone._originalCoordinates[0].getX(), 1e-5);
        assertEquals(42.5, newPoligone._originalCoordinates[0].getY(), 1e-5);
        assertEquals(95.0, newPoligone._originalCoordinates[1].getX(), 1e-5);
        assertEquals(42.5, newPoligone._originalCoordinates[1].getY(), 1e-5);
        assertEquals(87.0, newPoligone._originalCoordinates[2].getX(), 1e-5);
        assertEquals(74.0, newPoligone._originalCoordinates[2].getY(), 1e-5);
        assertEquals(14.5, newPoligone._originalCoordinates[3].getX(), 1e-5);
        assertEquals(74.0, newPoligone._originalCoordinates[3].getY(), 1e-5);
      }
      catch (Exception e)
      {
        assertTrue("failed to load a basic poligon.", false);
      }
    }

  }
}
