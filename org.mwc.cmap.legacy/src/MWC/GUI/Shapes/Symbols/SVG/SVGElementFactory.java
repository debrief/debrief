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

import org.w3c.dom.Element;

/**
 * It creates a factory given the svg dom Element
 */
public class SVGElementFactory
{

  public SVGElement getInstance(Element svgElement)
  {
    SVGElement answer = null;
    switch (svgElement.getNodeName())
    {
      case "rect":
        answer = new SVGRectangle(svgElement);
        break;

      case "polygon":
        answer = new SVGPoligon(svgElement);
        break;

      case "circle":
        answer = new SVGCircle(svgElement);
        break;

      case "line":
        answer = new SVGLine(svgElement);
        break;

      case "ellipse":
        answer = new SVGEllipse(svgElement);
        break;

      case "polyline":
        answer = new SVGPolyline(svgElement);
        break;
        
      default:
        // We have an unknown type.
        break;
    }
    return answer;
  }
}
