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

import org.jsoup.nodes.Element;

import MWC.GUI.CanvasType;

public class SVGRectangle extends SVGElement
{
  
  public SVGRectangle(Element dom)
  {
    super(dom);
  }

  @Override
  public void render(CanvasType dest, double sym_size,
      java.awt.Point origin_coords, double rotation_degs)
  {
    final double x = Double.parseDouble(get_dom().attr("x")) * sym_size;
    final double y = Double.parseDouble(get_dom().attr("y")) * sym_size;
    final double width = Double.parseDouble(get_dom().attr("width"))* sym_size;
    final double height = Double.parseDouble(get_dom().attr("height")) * sym_size;

    /*final String fillColorAsString = get_dom().attr("fill");
    final Color fillColor = hex2Rgb(fillColorAsString);*/
        
    dest.drawRect((int)x, (int)y, (int)width, (int)height);
  }

}
