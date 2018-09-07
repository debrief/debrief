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

public class SVGEllipse extends SVGElement
{

  private double _x;

  private double _y;

  private double _rx;

  private double _ry;

  public SVGEllipse(Element dom)
  {
    super(dom);
    try
    {
      _x = Double.parseDouble(getDom().getAttribute("cx"));
      _y = Double.parseDouble(getDom().getAttribute("cy"));
      _rx = Double.parseDouble(getDom().getAttribute("rx"));
      _ry = Double.parseDouble(getDom().getAttribute("ry"));
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
    // We want the icon to be aligned with the track
    double rotation = rotation_degs + 90.0 / 180.0 * Math.PI;
    
    // Lets assume that the viewbox is 0 0 100 100
    double magnitude = Math.sqrt(100 * 100 + 100 * 100);
    
    // centering and scaling to 1.0

    final double rx = _rx / magnitude * wid;
    final double ry = _ry / magnitude * wid;

    Point2D centerPoint = new Point2D.Double( (_x - rotationPoint.x)
        / magnitude * wid,  (_y - rotationPoint.y) / magnitude * wid);
    
    final AffineTransform thisRotation = AffineTransform.getRotateInstance(
        rotation, 0, 0); 
    
    thisRotation.transform(centerPoint, centerPoint);
    
    final double diameterX = rx * sym_size * 2;
    final double diameterY = ry * sym_size * 2;
    final double x = centerPoint.getX() * sym_size + origin_coords.getX() - diameterX / 2;
    final double y = centerPoint.getY() * sym_size + origin_coords.getY() - diameterY / 2;

    if (_fill != null)
    {
      dest.fillOval((int) x, (int) y, (int) diameterX, (int) diameterY);
    }
    else
    {
      dest.drawOval((int) x, (int) y, (int) diameterX, (int) diameterY);
    }
  }

}
