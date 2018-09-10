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

}
