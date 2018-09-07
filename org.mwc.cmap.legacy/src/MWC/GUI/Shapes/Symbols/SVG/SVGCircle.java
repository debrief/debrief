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

public class SVGCircle extends SVGElement
{

  private double _x;

  private double _y;

  private double _r;

  public SVGCircle(Element dom)
  {
    super(dom);
    try
    {
      _x = Double.parseDouble(getDom().getAttribute("cx"));
      _y = Double.parseDouble(getDom().getAttribute("cy"));
      _r = Double.parseDouble(getDom().getAttribute("r"));
    }
    catch (Exception e)
    {
      MWC.Utilities.Errors.Trace.trace("Invalid SVG Format");
    }
  }

  @Override
  public void render(final CanvasType dest, final double sym_size,
      final Point origin_coords, final double rotation_degs,
      final java.awt.Point rotationPoint)
  {
    // We want the icon to be aligned with the track
    double rotation = rotation_degs + 90.0 / 180.0 * Math.PI;
    
    // Lets assume that the viewbox is 0 0 100 100
    double magnitude = Math.sqrt(100 * 100 + 100 * 100);
    
    // centering and scaling to 1.0

    final double r = _r / magnitude * wid;
    
    Point2D centerPoint = new Point2D.Double( (_x - rotationPoint.x)
        / magnitude * wid,  (_y - rotationPoint.y) / magnitude * wid);
    

    final AffineTransform thisRotation = AffineTransform.getRotateInstance(
        rotation, 0, 0); 
    
    thisRotation.transform(centerPoint, centerPoint);
    
    final double diameter = r * sym_size * 2;
    final double x = centerPoint.getX() * sym_size + origin_coords.getX() - diameter / 2 ;
    final double y = centerPoint.getY() * sym_size + origin_coords.getY() - diameter / 2 ;

    if (_fill != null)
    {
      dest.fillOval((int) x, (int) y, (int) diameter, (int) diameter);
    }
    else
    {
      dest.drawOval((int) x, (int) y, (int) diameter, (int) diameter);
    }
  }

}
