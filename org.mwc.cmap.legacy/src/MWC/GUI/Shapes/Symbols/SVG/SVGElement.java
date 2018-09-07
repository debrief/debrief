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

  /**
   * the size of the symbol
   */
  protected final int wid = 40;

  /**
   * Color of the object
   */
  protected Color _fill;

  public SVGElement(final Element dom)
  {
    this._dom = dom;

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

  public Element getDom()
  {
    return _dom;
  }

  public void setDom(Element _dom)
  {
    this._dom = _dom;
  }

  public abstract void render(final CanvasType dest, final double sym_size,
      final java.awt.Point origin_coords, final double rotation_degs,
      final java.awt.Point rotationPoint);

  /**
   * https://stackoverflow.com/questions/4129666/how-to-convert-hex-to-rgb-using-java
   * 
   * @param colorStr
   *          e.g. "#FFFFFF"
   * @return
   */
  public Color hex2Rgb(String colorStr)
  {
    return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer
        .valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr
            .substring(5, 7), 16));
  }
}
