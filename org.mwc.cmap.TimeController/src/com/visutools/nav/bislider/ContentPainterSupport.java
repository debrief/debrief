
package com.visutools.nav.bislider;

import java.util.Vector;
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

public class ContentPainterSupport implements java.io.Serializable {
  //---------- MODIFIERS|-------------------- Type|----------------------------------------------- Name = Init value
  protected final static  javax.swing.text.html.parser.ParserDelegator  MAXIMUM_VARIABLE_SIZE_FOR_NAME  = null;

  protected               Vector<ContentPainterListener>                                        ContentPainterListeners         = new Vector<ContentPainterListener>();

  final static            long                                          serialVersionUID                = -1497023291489642695L;


  /**
   * Adds a feature to the ContentPainterListener attribute of the ContentPainterSupport object
   *
   * @param l  The feature to be added to the ContentPainterListener attribute
   */
  public synchronized void addContentPainterListener(ContentPainterListener l) {
    // add a listener if it is not already registered
    if (!ContentPainterListeners.contains(l)) {
      ContentPainterListeners.addElement(l);
    }
  } // addContentPainterListener()


  /**
   * Description of the Method
   *
   * @param l  Description of the Parameter
   */
  public synchronized void removeContentPainterListener(ContentPainterListener l) {
    // remove it if it is registered
    if (ContentPainterListeners.contains(l)) {
      ContentPainterListeners.removeElement(l);
    }
  } // removeContentPainterListener()


  /**
   * return the number of listeners
   */
  public synchronized int getPainterListenerNumber() {
    return ContentPainterListeners.size();
  } // removeContentPainterListener()


  /**
   * fire the event asynchronously
   *
   * @param Source_Arg             Source of the event. Should be the BiSlider instance
   * @param Graphics_Arg           The graphics object to draw with
   * @param Min_Arg                The minimum value of this piece of data
   * @param Max_Arg                The maximum value of this piece of data
   * @param Index_Arg              The index of the segment
   * @param Color_Arg              The color normally associated with these values
   * @param Rectangle_Arg          The rectangle to fill with painting
   * @param BoundingRectangle_Arg  The rectangle in which the other rectangle is
   */
  public void fireAsyncPaint(
    final Object Source_Arg,
    final Graphics Graphics_Arg,
    final double Min_Arg,
    final double Max_Arg,
    final int Index_Arg,        
    final Color Color_Arg,
    final Rectangle Rectangle_Arg,
    final Rectangle BoundingRectangle_Arg) {
    Thread  Thread1  =
      new Thread() {
        public void run() {
          internFireAsyncPaint(Source_Arg, Graphics_Arg, Min_Arg, Max_Arg, Index_Arg, Color_Arg, Rectangle_Arg, BoundingRectangle_Arg);
        }
      };
    Thread1.start();
  } // fireAsyncPaint()


  /**
   * fire the event asynchronously
   *
   * @param Source_Arg      Source of the event. Should be the BiSlider
   * @param Graphics_Arg    
   * @param Min_Arg                The minimum value of this piece of data
   * @param Max_Arg                The maximum value of this piece of data
   * @param Index_Arg              The index of the segment
   * @param Color_Arg              The color normally associated with these values
   * @param Rectangle_Arg          The rectangle to fill with painting
   * @param BoundingRectangle_Arg  The rectangle in which the other rectangle is
   */
  @SuppressWarnings("unchecked")
	void internFireAsyncPaint(
    final Object Source_Arg,
    Graphics Graphics_Arg,
    double Min_Arg,
    double Max_Arg,
    int Index_Arg,    
    Color Color_Arg,
    Rectangle Rectangle_Arg,
    Rectangle BoundingRectangle_Arg) {

    Vector<ContentPainterListener>  Vector1;
    synchronized (this) {
      Vector1 = (Vector<ContentPainterListener>)ContentPainterListeners.clone();
    }

    // Fire the event to all listeners.
    int     count    = Vector1.size();
    for (int i = 0; i < count; i++) {
      ContentPainterListener  listener  = Vector1.elementAt(i);
      listener.paint(new ContentPainterEvent(Source_Arg, Graphics_Arg, Min_Arg, Max_Arg, Index_Arg, Color_Arg, Rectangle_Arg, BoundingRectangle_Arg));
    }
  } // internFireAsyncPaint()


  /**
   * fire the event
   *
   * @param Source_Arg      Source of the event. Should be the BiSlider
   * @param Graphics_Arg    
   * @param Min_Arg                The minimum value of this piece of data
   * @param Max_Arg                The maximum value of this piece of data
   * @param Index_Arg              The index of the segment
   * @param Color_Arg              The color normally associated with these values
   * @param Rectangle_Arg          The rectangle to fill with painting
   * @param BoundingRectangle_Arg  The rectangle in which the other rectangle is
   */
  @SuppressWarnings("unchecked")
	public void firePaint(
    Object Source_Arg,
    Graphics Graphics_Arg,
    double Min_Arg,
    double Max_Arg,
    int Index_Arg,        
    Color Color_Arg,
    Rectangle Rectangle_Arg,
    Rectangle BoundingRectangle_Arg) {

    // Make a copy of the listener object vector so that
    // it cannot be changed while we are firing events.
    Vector<ContentPainterListener>  Vector1;
    synchronized (this) {
      Vector1 = (Vector<ContentPainterListener>)ContentPainterListeners.clone();
    }

    // Fire the event to all listeners.
    int     count    = Vector1.size();
    for (int i = 0; i < count; i++) {
      ContentPainterListener  listener  = Vector1.elementAt(i);
      listener.paint(new ContentPainterEvent(Source_Arg, Graphics_Arg, Min_Arg, Max_Arg, Index_Arg, Color_Arg, Rectangle_Arg, BoundingRectangle_Arg));
    }
  } // firePaint()
}

