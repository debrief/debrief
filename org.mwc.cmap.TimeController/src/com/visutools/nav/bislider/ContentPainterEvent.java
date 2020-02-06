
package com.visutools.nav.bislider;

import java.awt.*;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/

public class ContentPainterEvent
   extends AWTEvent
   implements Cloneable {

  //---------- MODIFIERS|-------------------- Type|----------------------------------------------- Name = Init value
  protected final static  javax.swing.text.html.parser.ParserDelegator  MAXIMUM_VARIABLE_SIZE_FOR_NAME  = null;

  // the data
  protected               Graphics                                      Graphics1;
  protected               double                                        Min1;
  protected               double                                        Max1;
  protected               Color                                         Color1;
  protected               int                                           SegmentIndex;
  protected               Rectangle                                     Rectangle1;
  protected               Rectangle                                     BoundingRectangle1;
  final static            long                                          serialVersionUID                = 6868457033542496338L;


  /**
   * duplicate the object
   *
   * @return                                Description of the Return Value
   * @exception CloneNotSupportedException  Description of the Exception
   */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }


  /**
   * Constructor
   *
   * @param Source_Arg     Source of the event. Should be the BiSlider instance
   * @param Graphics_Arg   The graphics object to draw with
   * @param Min_Arg        The minimum value of this piece of data
   * @param Max_Arg        The maximum value of this piece of data
   * @param Index_Arg      The index of the segment
   * @param Color_Arg      The color normally associated with these values
   * @param Rectangle_Arg  The rectangle to fill with painting
   * @param BoundingRectangle_Arg  The rectangle in which the other rectangle is
   */
  public ContentPainterEvent(
    Object Source_Arg,
    Graphics Graphics_Arg,
    double Min_Arg,
    double Max_Arg,
    int Index_Arg,
    Color Color_Arg,
    Rectangle Rectangle_Arg,
    Rectangle BoundingRectangle_Arg) {

    super(Source_Arg, 0);
    
    SegmentIndex       = Index_Arg;
    Graphics1          = Graphics_Arg;
    Min1               = Min_Arg;
    Max1               = Max_Arg;
    Color1             = Color_Arg;
    Rectangle1         = Rectangle_Arg;
    BoundingRectangle1 = BoundingRectangle_Arg;
  } // Constructor


  /**
   * @return   the Graphics to draw the rectangle with
   */
  public Graphics getGraphics() {
    return Graphics1;
  } // getGraphics()
  
  /**
   * @return the Index of the segment we are drawing
   */
  public int getSegmentIndex() {
    return SegmentIndex;
  } // getSegmentIndex()  


  /**
   * @return   the minimum value to paint
   */
  public double getMinimum() {
    return Min1;
  } // getMinimum()


  /**
   * @return   the maximum value to paint
   */
  public double getMaximum() {
    return Max1;
  } // getMaximum()


  /**
   * @return   the default color to paint the rectangle
   */
  public Color getColor() {
    return Color1;
  } // getColor()


  /**
   * @return   the Rectangle, bounding box to paint something in
   */
  public Rectangle getRectangle() {
    return Rectangle1;
  } // getRectangle()


  /**
   * @return   the Bounding Rectangle, the segment in witch the rectangle is painted
   */
  public Rectangle getBoundingRectangle() {
    return BoundingRectangle1;
  } // getRectangle()


  /**
   * display the content of the color table
   *
   * @return   Description of the Return Value
   */
  public String toString() {
    return "ContentPainterEvent@" + hashCode() + " to paint " + Rectangle1 + " with " + Color1 + " to represent " + Min1 + "-" + Max1;
  } // toString()
} // ContentPainterEvent.java

