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
// $RCSfile: PlainWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.7 $
// $Log: PlainWrapper.java,v $
// Revision 1.7  2007/06/01 13:46:05  ian.mayo
// Improve performance of export text to clipboard
//
// Revision 1.6  2006/10/03 08:20:55  Ian.Mayo
// Use better compareTo methods
//
// Revision 1.5  2006/05/25 14:10:41  Ian.Mayo
// Make plottables comparable
//
// Revision 1.4  2005/09/23 14:53:44  Ian.Mayo
// Introduce interpolated data-type
//
// Revision 1.3  2005/05/12 14:30:50  Ian.Mayo
// Minor tidying, to suit Eclipse
//
// Revision 1.2  2004/05/25 15:45:41  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:04  Ian.Mayo
// Initial import
//
// Revision 1.10  2003-06-11 16:01:00+01  ian_mayo
// Tidy javadoc comments
//
// Revision 1.9  2003-03-03 10:35:31+00  ian_mayo
// Introduce Filled property
//
// Revision 1.8  2003-01-21 16:31:35+00  ian_mayo
// Minor refactoring (property name moved here from sub-class)
//
// Revision 1.7  2003-01-09 16:20:03+00  ian_mayo
// Create visibility changed property
//
// Revision 1.6  2002-07-12 15:46:57+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.5  2002-07-09 15:29:24+01  ian_mayo
// Provide signatures for abstract methods
//
// Revision 1.4  2002-05-28 09:25:35+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:13+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-05-23 13:15:51+01  ian
// end of 3d development
//
// Revision 1.3  2002-05-08 14:37:52+01  ian_mayo
// Fire off appropriate property changes
//
// Revision 1.2  2002-05-07 08:53:13+01  ian_mayo
// Provide getName abstract method
//
// Revision 1.0  2002-04-30 09:17:02+01  ian
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:30+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-08-29 19:04:47+01  administrator
// Initial revision
//
// Revision 1.3  2001-08-21 15:19:41+01  administrator
// Don't give shapes a default colour
//
// Revision 1.2  2001-08-13 12:51:36+01  administrator
// provide better support for colours
//
// Revision 1.1  2001-08-06 16:59:02+01  administrator
// Add property change support to our children
//
// Revision 1.0  2001-07-17 08:41:09+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:40:23+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:23  ianmayo
// initial import of files
//
// Revision 1.8  2000-04-05 08:39:22+01  ian_mayo
// make get/set methods FINAL (to help optimisation)
//
// Revision 1.7  2000-04-03 10:19:38+01  ian_mayo
// add setVisible method
//
// Revision 1.6  2000-02-22 13:44:53+00  ian_mayo
// Added exportable interface, and changed export code
//
// Revision 1.5  2000-01-20 10:09:58+00  ian_mayo
// experimenting with copy functionality
//
// Revision 1.4  1999-11-12 14:35:39+00  ian_mayo
// part way through getting them to export themselves
//
// Revision 1.3  1999-11-11 10:41:18+00  ian_mayo
// tidied up source code
//
// Revision 1.2  1999-10-14 12:04:14+01  ian_mayo
// add property editing support, plus 'rangeFrom' and 'getAnchor' methods
//
// Revision 1.1  1999-10-12 15:34:03+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-07-27 09:28:07+01  administrator
// tidying up
//
// Revision 1.2  1999-07-12 08:09:21+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:13+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:04+01  sm11td
// Initial revision
//
// Revision 1.4  1999-06-04 08:45:27+01  sm11td
// Ending phase 1, adding colours to annotations
//
// Revision 1.3  1999-06-01 16:49:21+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.2  1999-02-01 16:08:48+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:07+00  sm11td
// Initial revision
//

package MWC.GUI;

import java.awt.Color;
import java.io.Serializable;
import java.lang.annotation.Annotation;

import MWC.GenericData.WorldArea;

/**
 * This class shows all the responsibilities of the wrapper items. In particular the fact that the
 * data items should be able to describe themselves, and create/fill which shows/describes the raw
 * data
 */
abstract public class PlainWrapper implements Plottable, Serializable,
    Exportable, SupportsPropertyListeners, Renamable
{

  // ///////////////////////////////////////////////////////////
  // member variables
  // //////////////////////////////////////////////////////////

  public static interface InterpolatedData
  {
  }

  /**
   * property name to indicate that the text in the label has changed
   *
   */
  public static final String TEXT_CHANGED = "TEXT_CHANGE";

  /**
   * the name of the property change event to fire should this object get moved
   *
   */
  public static final String LOCATION_CHANGED = "LOCATION_CHANGED";

  /**
   * the name of the property change event to fire should this object have its colour changed
   *
   */
  public static final String COLOR_CHANGED = "COLOR_CHANGED";

  /**
   * the name of the property change event to fire should this object change its visibility
   *
   */
  public static final String VISIBILITY_CHANGED = "VISIBILITY_CHANGED";

  // keep track of versions
  static final long serialVersionUID = 1;

  /**
   * convenience function, for determining if object has fire extended annotation
   *
   * @param ann
   *          set of annotations for an object
   * @return yes/no
   */
  public static boolean hasFireExtendedAnnotation(final Annotation[] ann)
  {
    if (ann != null)
    {
      for (final Annotation thisA : ann)
      {
        if (thisA.annotationType().equals(FireExtended.class))
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * convenience function, for determining if object has fire reformatted annotation
   *
   * @param ann
   *          set of annotations for an object
   * @return yes/no
   */
  public static boolean hasFireReformattedAnnotation(final Annotation[] ann)
  {
    if (ann != null)
    {
      for (final Annotation thisA : ann)
      {
        if (thisA.annotationType().equals(FireReformatted.class))
        {
          return true;
        }
      }
    }
    return false;
  }

  private java.awt.Color _theColor = Color.yellow;

  /**
   * (optional) comment field
   *
   */
  private String _theComment;

  /**
   * whether this shape is visible
   *
   */
  private boolean _visible;

  // ///////////////////////////////////////////////////////////
  // member functions
  // //////////////////////////////////////////////////////////

  /**
   * provide support for property changes, should we require it
   *
   */
  private transient java.beans.PropertyChangeSupport _pSupport = null;

  // ///////////////////////////////////////////////////////////
  // constructor
  // //////////////////////////////////////////////////////////
  protected PlainWrapper()
  {
    _visible = true;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * MWC.GUI.SupportsPropertyListeners#addPropertyChangeListener(java.beans.PropertyChangeListener)
   */
  @Override
  public void addPropertyChangeListener(
      final java.beans.PropertyChangeListener listener)
  {
    // add to our list
    getSupport().addPropertyChangeListener(listener);
  }

  /*
   * (non-Javadoc)
   *
   * @see MWC.GUI.SupportsPropertyListeners#addPropertyChangeListener(java.lang.String,
   * java.beans.PropertyChangeListener)
   */
  @Override
  public void addPropertyChangeListener(final String property,
      final java.beans.PropertyChangeListener listener)
  {
    // add to our list
    getSupport().addPropertyChangeListener(property, listener);
  }

  /**
   * instruct this object to clear itself out, ready for ditching
   *
   */
  public void closeMe()
  {
    _pSupport = null;
    _theColor = null;
  }

  @Override
  public int compareTo(final Plottable arg0)
  {
    // just do idiot check
    if ((getName() != null) && (arg0.getName() != null) && (!getName().equals(
        arg0.getName())))
    {
      return getName().compareTo(arg0.getName());
    }
    else
    {
      final int res;
      final int myCode = hashCode();
      final int otherCode = arg0.hashCode();
      if (myCode < otherCode)
        res = -1;
      else if (myCode > otherCode)
        res = 1;
      else
        res = 0;
      return res;
    }
  }

  // //////////////////////////////////////////////////
  // export this shape
  // /////////////////////////////////////////////////
  @Override
  public void exportThis()
  {
    // ok, export it.
    MWC.Utilities.ReaderWriter.ImportManager.exportThis(this);
  }

  @Override
  public void firePropertyChange(final String propertyChanged,
      final Object oldValue, final Object newValue)
  {
    getSupport().firePropertyChange(propertyChanged, oldValue, newValue);
  }

  /**
   * find the data area occupied by this item
   */
  @Override
  abstract public WorldArea getBounds();

  public java.awt.Color getColor()
  {
    return _theColor;
  }

  public String getComment()
  {
    return _theComment;
  }

  /**
   * get the editing information for this type
   */
  @Override
  public Editable.EditorType getInfo()
  {
    return null;
  }

  /**
   * get the name of this object
   *
   */
  @Override
  abstract public String getName();

  public java.beans.PropertyChangeSupport getSupport()
  {
    if (_pSupport == null)
      _pSupport = new java.beans.PropertyChangeSupport(this);

    return _pSupport;
  }

  /**
   * it this item currently visible?
   */
  @Override
  final public boolean getVisible()
  {
    return _visible;
  }

  /**
   * whether there is any edit information for this item this is a convenience function to save
   * creating the EditorType data first
   *
   * @return yes/no
   */
  @Override
  abstract public boolean hasEditor();

  @Override
  public abstract void paint(CanvasType dest);

  /**
   * Determine how far away we are from this point or return INVALID_RANGE if it can't be calculated
   *
   * @return distance in floating point degrees
   */
  @Override
  public double rangeFrom(final MWC.GenericData.WorldLocation other)
  {
    return INVALID_RANGE;
  }

  /*
   * (non-Javadoc)
   *
   * @see MWC.GUI.SupportsPropertyListeners#removePropertyChangeListener(java.beans.
   * PropertyChangeListener)
   */
  @Override
  public void removePropertyChangeListener(
      final java.beans.PropertyChangeListener listener)
  {
    getSupport().removePropertyChangeListener(listener);
  }

  /*
   * (non-Javadoc)
   *
   * @see MWC.GUI.SupportsPropertyListeners#removePropertyChangeListener(java.lang.String,
   * java.beans.PropertyChangeListener)
   */
  @Override
  public void removePropertyChangeListener(final String property,
      final java.beans.PropertyChangeListener listener)
  {
    getSupport().removePropertyChangeListener(property, listener);
  }

  /**
   * update the color for this item
   *
   * @param theColor
   */
  @FireReformatted
  public void setColor(final java.awt.Color theColor)
  {
    // do we need to change?
    if (theColor != _theColor)
    {
      // store the old colour
      final Color oldCol = _theColor;

      // update the value
      setColorQuiet(theColor);

      // and inform the listeners
      getSupport().firePropertyChange(COLOR_CHANGED, oldCol, theColor);
    }
  }

  protected void setColorQuiet(final Color theColor)
  {
    // do the update
    _theColor = theColor;
  }

  public void setComment(final String comment)
  {
    _theComment = comment;
  }

  /**
   * specify is this object is visible
   */
  @Override
  @FireReformatted
  final public void setVisible(final boolean val)
  {
    // is this a different value?
    if (val != _visible)
    {
      // store the old vis
      final boolean oldVis = _visible;

      // do the update
      _visible = val;

      // and inform the listeners, if we need to
      getSupport().firePropertyChange(VISIBILITY_CHANGED, oldVis, val);
    }
  }

}
