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
import java.awt.geom.AffineTransform;

import org.w3c.dom.Element;

abstract public class SVGElement
{
  /**
   * List of coordinates of the Element. If it is a line, it contains two points. If it is a circle,
   * it contains the center etc
   */
  protected java.awt.geom.Point2D[] _originalCoordinates;

  /**
   * List of coordinates after scaling, rotation.
   */
  protected int[] _intX;
  
  protected int[] _intY;

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

  public void render(final CanvasType dest, final double sym_size,
      final java.awt.Point origin_coords, final double rotation_degs,
      final java.awt.Point rotationPoint)
  {
    java.awt.geom.Point2D[] tempCoord = new java.awt.geom.Point2D[_originalCoordinates.length];
    // We want the icon to be aligned with the track
    double initial_rotation_degs = 90.0 / 180.0 * Math.PI;
    double current_rotation_deg = initial_rotation_degs + rotation_degs;

    // Lets assume that the viewbox is 0 0 100 100
    double magnitude = Math.sqrt(100 * 100 + 100 * 100);

    // We initialize the transformers
    final AffineTransform normalize = AffineTransform.getScaleInstance(1.0
        / magnitude * wid, 1.0 / magnitude * wid);

    final AffineTransform scale = AffineTransform.getScaleInstance(sym_size,
        sym_size);
    
    java.awt.geom.Point2D rotationPointProp = new java.awt.geom.Point2D.Double();
    normalize.transform(rotationPoint, rotationPointProp);
    scale.transform(rotationPointProp, rotationPointProp);
    
    final AffineTransform rotate = AffineTransform.getRotateInstance(
        current_rotation_deg, rotationPointProp.getX(), rotationPointProp.getY());
    final AffineTransform move = AffineTransform.getTranslateInstance(origin_coords.getX(), origin_coords.getY());
    final AffineTransform moveOrigin = AffineTransform.getTranslateInstance(-rotationPointProp.getX(), -rotationPointProp.getY());

    // We rotate
    for (int i = 0; i < _originalCoordinates.length; i++)
    {
      tempCoord[i] = new java.awt.geom.Point2D.Double();
      normalize.transform(_originalCoordinates[i], tempCoord[i]);
      scale.transform(tempCoord[i], tempCoord[i]);
      rotate.transform(tempCoord[i], tempCoord[i]);
      move.transform(tempCoord[i], tempCoord[i]);
      moveOrigin.transform(tempCoord[i], tempCoord[i]);
    }

    for (int i = 0; i < _originalCoordinates.length; i++)
    {
      _intX[i] = (int) tempCoord[i].getX();
      _intY[i] = (int) tempCoord[i].getY();
    }
  }

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
