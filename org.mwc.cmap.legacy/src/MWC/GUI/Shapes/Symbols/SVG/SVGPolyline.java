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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.w3c.dom.Element;

import MWC.GUI.CanvasType;

public class SVGPolyline extends SVGElement
{
  protected double[] _x;

  protected double[] _y;

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

      _x = new double[_points.length / 2];
      _y = new double[_points.length / 2];
      for (int i = 0; i < _points.length; i += 2)
      {
        _x[i / 2] = _points[i];
        _y[i / 2] = _points[i + 1];
      }
    }
    catch (Exception e)
    {
      MWC.Utilities.Errors.Trace.trace("Invalid SVG Format");
    }
  }

  @Override
  public void render(CanvasType dest, double sym_size, Point origin_coords,
      double rotation_degs, final java.awt.Point rotationPoint)
  {
    // TODO Same code as Render SVG Poligon. It must be handled by only 1 function

    // We want the icon to be aligned with the track
    rotation_degs += 90.0 / 180.0 * Math.PI;

    // Lets assume that the viewbox is 0 0 100 100
    double magnitude = Math.sqrt(100 * 100 + 100 * 100);

    // centering and scaling to 1.0

    Point2D[] polygonPoints = new Point2D[_x.length];
    for (int i = 0; i < _x.length; i++)
    {
      polygonPoints[i] = new Point2D.Double((_x[i] - rotationPoint.x)
          / magnitude * wid, (_y[i] - rotationPoint.y) / magnitude * wid);
    }

    final AffineTransform thisRotation = AffineTransform.getRotateInstance(
        rotation_degs, 0, 0);

    // We rotate
    for (int i = 0; i < _x.length; i++)
    {
      // final Point2D postTurn = new Point2D.Double();
      thisRotation.transform(polygonPoints[i], polygonPoints[i]);
    }

    int[] intX = new int[_x.length];
    int[] intY = new int[_y.length];
    for (int i = 0; i < _x.length; i++)
    {
      intX[i] = (int) (polygonPoints[i].getX() * sym_size + origin_coords
          .getX());
      intY[i] = (int) (polygonPoints[i].getY() * sym_size + origin_coords
          .getY());
    }

    dest.drawPolyline(intX, intY, intX.length);
  }

}
