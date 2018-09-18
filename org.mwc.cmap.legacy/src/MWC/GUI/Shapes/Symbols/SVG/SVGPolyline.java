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

public class SVGPolyline extends SVGElement
{
  public SVGPolyline(Element dom)
  {
    super(dom);
    try
    {
      String points = getDom().getAttribute("points");

      points = points.replace(',', ' ');
      // We have the format "x1 y1 x2 y2 x3 y3 ... xn yn"
      String[] pointsSplitted = points.split(" ");
      double[] _points = new double[pointsSplitted.length];

      for (int i = 0; i < pointsSplitted.length; i++)
      {
        _points[i] = Double.parseDouble(pointsSplitted[i]);
      }

      _originalCoordinates = new java.awt.geom.Point2D[_points.length / 2];
      for (int i = 0; i < _points.length; i += 2)
      {
        _originalCoordinates[i / 2] = new java.awt.geom.Point2D.Double(
            _points[i], _points[i + 1]);
      }
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
    
    if (dontFillObject)
    {
      dest.drawPolyline(_intX, _intY, _intX.length);
    }
    else
    {
      dest.fillPolygon(_intX, _intY, _intX.length);
    }
  }

}
