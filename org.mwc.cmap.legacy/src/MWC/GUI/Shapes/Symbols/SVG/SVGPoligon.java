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

  
  /**
   * the size of the symbol
   */
  protected final int wid = 40;
  
  public SVGPoligon(Element dom)
  {
    super(dom);
  }

  @Override
  public void render(CanvasType dest, double sym_size, Point origin_coords,
      double rotation_degs, final java.awt.Point rotationPoint)
  {

    // Lets assume that the viewbox is 0 0 100 100
    double magnitude = Math.sqrt(100 * 100 + 100 * 100);

    // centering and scaling to 1.0
    double[] x = new double[_x.length];
    double[] y = new double[_y.length];
    for (int i = 0; i < _x.length; i++)
    {
      x[i] = (_x[i] - rotationPoint.x) / magnitude * wid;
      y[i] = (_y[i] - rotationPoint.y) / magnitude * wid;
    }

    int [] intX = new int[x.length];
    int [] intY = new int[y.length];
    for (int i = 0; i < _x.length; i++)
    {
      intX[i] = (int)(x[i] * sym_size + origin_coords.getX());
      intY[i] = (int)(y[i] * sym_size + origin_coords.getY());
    }

    if (_fill != null)
    {
      dest.setColor(_fill);
      dest.fillPolygon(intX, intY, intX.length);
    }
    else
    {
      dest.drawPolygon(intX, intY, intX.length);
    }
  }

}
