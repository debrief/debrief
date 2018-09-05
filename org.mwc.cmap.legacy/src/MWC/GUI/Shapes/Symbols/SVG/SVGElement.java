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

import MWC.GUI.CanvasType;

import java.awt.Color;

import org.w3c.dom.Element;

abstract public class SVGElement
{

  /**
   * XML section that describes the SVG Element
   */
  private Element _dom;

  public SVGElement(final Element dom)
  {
    this._dom = dom;
  }

  public Element get_dom()
  {
    return _dom;
  }

  public void set_dom(Element _dom)
  {
    this._dom = _dom;
  }

  public abstract void render(final CanvasType dest, final double sym_size,
      final java.awt.Point origin_coords, final double rotation_degs);
  
  /**
   * https://stackoverflow.com/questions/4129666/how-to-convert-hex-to-rgb-using-java
   * @param colorStr e.g. "#FFFFFF"
   * @return 
   */
  public Color hex2Rgb(String colorStr) {
      return new Color(
              Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
              Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
              Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
  }
}
