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

import org.w3c.dom.Element;

import MWC.GUI.CanvasType;

public class SVGPoligon extends SVGPolyline
{

  public SVGPoligon(Element dom)
  {
    super(dom);
  }

  @Override
  public void render(CanvasType dest, double sym_size, Point origin_coords,
      double rotation_degs, final java.awt.Point rotationPoint)
  {
    int [] x = new int[_x.length];
    int [] y = new int[_y.length];
    
    for ( int i = 0 ; i < x.length ; i++ ) {
      x[i] = (int)(_x[i] * sym_size + origin_coords.getX());
      y[i] = (int)(_y[i] * sym_size + origin_coords.getY());
    }
    
    if (_fill != null)
    {
      dest.setColor(_fill);
      dest.fillPolygon(x, y, x.length);
    }
    else
    {
      dest.drawPolygon(x, y, x.length);
    }
  }

}
