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
    super.render(dest, sym_size, origin_coords, rotation_degs, rotationPoint);
    if (_fill != null)
    {
      dest.setColor(_fill);
      dest.fillPolygon(_intX, _intY, _intX.length);
    }
    else
    {
      dest.drawPolygon(_intX, _intY, _intX.length);
    }
  }

}
