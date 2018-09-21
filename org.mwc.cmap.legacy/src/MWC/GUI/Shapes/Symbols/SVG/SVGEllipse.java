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

public class SVGEllipse extends SVGElement
{
  private double _rx;

  private double _ry;

  public SVGEllipse(Element dom)
  {
    super(dom);
    try
    {
      _originalCoordinates = new java.awt.geom.Point2D[1];

      _originalCoordinates[0] = new java.awt.geom.Point2D.Double(Double
          .parseDouble(getDom().getAttribute("cx")), Double.parseDouble(getDom()
              .getAttribute("cy")));

      _rx = Double.parseDouble(getDom().getAttribute("rx"));
      _ry = Double.parseDouble(getDom().getAttribute("ry"));

      _intX = new int[_originalCoordinates.length];
      _intY = new int[_originalCoordinates.length];
    }
    catch (Exception e)
    {
      MWC.Utilities.Errors.Trace.trace("Invalid SVG Format");
    }
  }

  @Override
  public void render(CanvasType dest, double sym_size,
      java.awt.Point origin_coords, double rotation_degs,
      final java.awt.Point rotationPoint, final java.awt.Color defaultColor)
  {
    super.render(dest, sym_size, origin_coords, rotation_degs, rotationPoint,
        defaultColor);

    double magnitude = Math.sqrt(100 * 100 + 100 * 100);

    final double rx = _rx / magnitude * wid;
    final double ry = _ry / magnitude * wid;
    final double diameterX = rx * sym_size * 2;
    final double diameterY = ry * sym_size * 2;

    if (_fill != null)
    {
      dest.fillOval((int) (_intX[0] - diameterX / 2), (int) (_intY[0]
          - diameterY / 2), (int) diameterX, (int) diameterY);
    }
    else
    {
      dest.drawOval((int) (_intX[0] - diameterX / 2), (int) (_intY[0]
          - diameterY / 2), (int) diameterX, (int) diameterY);
    }
  }

  public static class SVGEllipseTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public void testBasicSVGLine()
    {
      try
      {
        final String contentToLoad =
            "<ellipse id=\"Oval\" fill=\"#000000\" cx=\"50.0111093\" cy=\"44.2182422\" rx=\"31\" ry=\"30.7304336\" transform=\"matrix(1, 0, 0, 1, 0, 0)\"/>";
        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory
            .newInstance();
        final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        final Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(
            contentToLoad.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

        SVGEllipse newSVGEllipse = new SVGEllipse(doc.getDocumentElement());
        assert (newSVGEllipse._intX.length == 1);
        assert (newSVGEllipse._intY.length == 1);
        assert (newSVGEllipse._originalCoordinates.length == 1);
        assertEquals(50.0111093, newSVGEllipse._originalCoordinates[0].getX(),
            1e-5);
        assertEquals(44.2182422, newSVGEllipse._originalCoordinates[0].getY(),
            1e-5);
        assertEquals(31.0, newSVGEllipse._rx, 1e-5);
        assertEquals(30.7304336, newSVGEllipse._ry, 1e-5);
      }
      catch (Exception e)
      {
        fail("failed to load a basic ellipse.");
      }
    }

  }
}
