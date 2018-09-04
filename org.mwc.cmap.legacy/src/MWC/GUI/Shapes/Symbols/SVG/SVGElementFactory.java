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

import org.jsoup.nodes.Element;

public class SVGElementFactory
{
  private Element _svgElement;
  
  /**
   * It creates a factory given the svg dom Element
   * @param svgElement SVG Dom Element
   */
  public SVGElementFactory(final Element svgElement)
  {
    this._svgElement = svgElement;
  }
  
  
  
  public Element get_svgElement()
  {
    return _svgElement;
  }



  public void set_svgElement(Element _svgElement)
  {
    this._svgElement = _svgElement;
  }



  public SVGElement getInstance() {
    SVGElement answer = null;
    switch (get_svgElement().nodeName())
    {
      case "rect":
        answer = new SVGRectangle(get_svgElement());
        break;

      default:
        // We have an unknown type.
        break;
    }
    return answer;
  }
}
