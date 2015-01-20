/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// $RCSfile: Layer.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.6 $
// $Log: Layer.java,v $
// Revision 1.6  2006/09/25 14:51:14  Ian.Mayo
// Respect new "has children" property of Layers
//
// Revision 1.5  2004/09/09 10:52:03  Ian.Mayo
// Provide missing methods from Layers structure.  Don't know why they had been missing for so long.  Poss disconnect between ASSET/Debrief development trees
//
// Revision 1.4  2004/09/06 14:04:41  Ian.Mayo
// Switch to supporting editables in Layer Manager, and showing icon for any editables which have one
//
// Revision 1.3  2004/09/03 15:13:32  Ian.Mayo
// Reflect refactored plottable getElements
//
// Revision 1.2  2004/05/25 15:45:34  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:03  Ian.Mayo
// Initial import
//
// Revision 1.3  2002-10-28 09:23:29+00  ian_mayo
// support line widths
//
// Revision 1.2  2002-05-28 09:25:35+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:12+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:28+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:35+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:43:07+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:47  ianmayo
// initial version
//
// Revision 1.10  2000-11-08 11:50:42+00  ian_mayo
// tidying up
//
// Revision 1.9  2000-11-02 16:44:37+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer
//
// Revision 1.8  2000-09-21 09:06:46+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.7  2000-08-18 13:36:05+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.6  2000-08-11 08:42:01+01  ian_mayo
// tidy beaninfo
//
// Revision 1.5  2000-02-22 13:51:21+00  ian_mayo
// add version id, and make exportable
//
// Revision 1.4  2000-01-20 10:16:16+00  ian_mayo
// removed d-lines
//
// Revision 1.3  1999-12-02 09:48:11+00  ian_mayo
// make into Editable
//
// Revision 1.2  1999-11-26 15:45:08+00  ian_mayo
// adding toString method
//
// Revision 1.1  1999-10-12 15:37:08+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:50+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-12 08:09:20+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:08+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:00+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-04 08:02:30+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-01-31 13:33:14+00  sm11td
// Initial revision
//


package MWC.GUI;

import java.io.Serializable;
import java.util.Enumeration;

/**
 * this class is a collection of objects which
 * may be plotted to a Chart
 *
 * @see Plottables
 * @see Plottable
 */
public interface Layer extends Serializable, Plottable
{
  /////////////////////////////////////////////////////////////
  // member interfaces
  ////////////////////////////////////////////////////////////
	
	/** interface for class that normally provides it's elements in a tiered fashion,
	 * but is able to provide them as a single list (for when an external class wants to
	 * process all of them as one list - double-click nearest testing).
	 */
	public interface ProvidesContiguousElements
	{
		public Enumeration<Editable> contiguousElements();
	}
	
  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public boolean hasEditor();

  public Editable.EditorType getInfo();

  public void exportShape();

  public void append(Layer other);

  public void paint(MWC.GUI.CanvasType dest);

  public MWC.GenericData.WorldArea getBounds();

  public void setName(String val);
  
  public String getName();
  
  /** indicator flag for if the children of this layer should be displayed in 
   * an non-alphanumeric order
   * @return
   */
  public boolean hasOrderedChildren();

  /**
   * get the width for objects in this layer
   *
   * @return the line width
   */
  public int getLineThickness();

  /**
   * add this element to ourselves
   *
   * @param point
   */
  public void add(MWC.GUI.Editable point);

  /**
   * remove this element from this layer
   *
   * @param point
   */
  public void removeElement(Editable point);

  public java.util.Enumeration<Editable> elements();

  public void setVisible(boolean val);

  /**
   * marker interface to indicate that this layer wants to be the very first layer
   */
  public static interface BackgroundLayer
  {

  }

}
