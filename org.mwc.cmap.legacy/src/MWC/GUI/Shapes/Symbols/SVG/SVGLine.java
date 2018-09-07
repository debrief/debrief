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

import java.awt.Color;
import java.awt.Point;

import org.w3c.dom.Element;

import MWC.GUI.CanvasType;

public class SVGLine extends SVGElement
{

  private double _x1;

  private double _y1;

  private double _x2;

  private double _y2;
  
  private Color _fill;
  
  public SVGLine(Element dom)
  {
    super(dom);
    try
    {
      _x1 = Double.parseDouble(getDom().getAttribute("x1"));
      _y1 = Double.parseDouble(getDom().getAttribute("y1"));
      _x2 = Double.parseDouble(getDom().getAttribute("x2"));
      _y2 = Double.parseDouble(getDom().getAttribute("y2"));

      if (getDom().hasAttribute("fill"))
      {
        // We have a color.
        String colorString = getDom().getAttribute("fill");
        if (colorString.matches("#[0-9A-Fa-f]{6}"))
        {
          _fill = hex2Rgb(colorString);
        }
        else
        {
          MWC.Utilities.Errors.Trace.trace("SVG contains a non-valid fill "
              + colorString);
        }
      }
    }
    catch (Exception e)
    {
      MWC.Utilities.Errors.Trace.trace("Invalid SVG Format");
    }
  }

  @Override
  public void render(CanvasType dest, double sym_size, Point origin_coords,
      double rotation_degs)
  {
    final double x1 = _x1 * sym_size + origin_coords.getX();
    final double y1 = _y1 * sym_size + origin_coords.getY();
    final double x2 = _x2 * sym_size + origin_coords.getX();
    final double y2 = _y2 * sym_size + origin_coords.getY();
    
    if (_fill != null)
    {
      dest.setColor(_fill);
    }
    dest.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
  }
  

}
