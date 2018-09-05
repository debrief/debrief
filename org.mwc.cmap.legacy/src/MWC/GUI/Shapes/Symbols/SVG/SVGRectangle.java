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

import org.w3c.dom.Element;

import MWC.GUI.CanvasType;

public class SVGRectangle extends SVGElement
{

  private double _x;

  private double _y;

  private double _width;

  private double _height;

  private Color _fill;

  public SVGRectangle(Element dom)
  {
    super(dom);
    _x = Double.parseDouble(getDom().getAttribute("x"));
    _y = Double.parseDouble(getDom().getAttribute("y"));
    _width = Double.parseDouble(getDom().getAttribute("width"));
    _height = Double.parseDouble(getDom().getAttribute("height"));

    if (getDom().hasAttribute("fill"))
    {
      // We have a color.
      String colorString = getDom().getAttribute("fill");
      if (colorString.matches("#[0-9A-Fa-f]{3}"))
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

  @Override
  public void render(CanvasType dest, double sym_size,
      java.awt.Point origin_coords, double rotation_degs)
  {
    final double x = _x * sym_size;
    final double y = _y * sym_size;
    final double width = _width * sym_size;
    final double height = _height * sym_size;

    if (_fill != null)
    {
      // TODO
    }

    dest.drawRect((int) x, (int) y, (int) width, (int) height);
  }

}
