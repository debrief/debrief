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

}
