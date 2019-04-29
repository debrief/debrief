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
package MWC.GUI.Undo;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: UndoBuffer.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: UndoBuffer.java,v $
// Revision 1.2  2004/05/25 15:37:11  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:26  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:47  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:26:03+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:00+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 13:03:32+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-02-22 16:39:59+00  administrator
// Check that there are operations on the stack
//
// Revision 1.1  2001-08-17 07:58:15+01  administrator
// Provide method to clear up memory leaks
//
// Revision 1.0  2001-07-17 08:42:51+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-05 10:39:07+00  novatech
// white space
//
// Revision 1.2  2001-01-05 09:54:50+00  novatech
// Zero is a valid index for the current item in the queue, it points to the first item -- since the first item in the que was being ignored
//
// Revision 1.1  2001-01-03 13:41:37+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:52:03  ianmayo
// initial version
//
// Revision 1.3  2000-10-16 11:11:54+01  ian_mayo
// Correctly handle UNDO operation at start of session (Before we have made any edits)
//
// Revision 1.2  2000-09-27 15:38:06+01  ian_mayo
// notify any observers after buffer has been modified
//
// Revision 1.1  1999-10-12 15:36:16+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:31+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-23 14:03:45+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.1  1999-07-16 10:01:54+01  administrator
// Initial revision
//

import java.util.Observable;
import java.util.Vector;

import MWC.GUI.Tools.Action;

public final class UndoBuffer extends Observable
{
  static protected final int undo = 1;
  static protected final int redo = 2;
  private final Vector<Action> theActions;
  private int presentAction;

  public UndoBuffer()
  {
    theActions = new Vector<Action>(0, 1);
  }

  /**
   * add a new action to the buffer
   */
  public void add(final Action newAction)
  {
    if (newAction != null)

      // see if it is worth adding, (is it undoable)
      if (newAction.isUndoable())
      {

        theActions.addElement(newAction);
        presentAction = theActions.indexOf(newAction);
        bufferChanged();
      }
  }

  private void bufferChanged()
  {
    setChanged();
    notifyObservers();
  }

  public boolean canRedo()
  {
    return presentAction < theActions.size() - 1;
  }

  public boolean canUndo()
  {
    return presentAction >= 0;
  }

  /**
   * close, and clear the buffer
   *
   */
  public void close()
  {
    // ok, empty it out!
    theActions.removeAllElements();
  }

  public boolean containsActions()
  {
    return !theActions.isEmpty();
  }

  /**
   * get the name of the next operation
   *
   * @param source
   * @param data
   * @return
   */
  public String getText(final Observable source, final int type)
  {
    String res = null;

    // find out whether this is undo or redo
    switch (type)
    {
      case undo:
        res = undoLabel();
        break;
      case redo:
        res = redoLabel();
        break;
    }

    return res;
  }

  /**
   * redo the last operation 'undone'
   */
  public void redo()
  {
    // check that we are not at the start of the list
    if (presentAction < theActions.size() - 1)
    {
      final Action act = theActions.elementAt(presentAction + 1);

      // check we have found it correctly
      if (act != null)
      {
        if (act.isRedoable())
        {
          // do the undo
          act.execute();
          // and move left right
          presentAction += 1;

          bufferChanged();
        }
      }
    }
  }

  /**
   * get a label describing the next thing which may be redone
   * 
   * @return a String describing the next thing which may be redone
   */
  public final String redoLabel()
  {
    final String res = null;

    return res;
  }

  /**
   * do the next undo we need
   */
  public void undo()
  {

    // check that we have some actions at all
    if (theActions.size() == 0)
      return;

    // check that we are not at the start of the list
    if (presentAction >= 0)
    {
      final Action act = theActions.elementAt(presentAction);

      // check we have found it correctly
      if (act != null)
      {

        if (act.isUndoable())
        {
          // do the undo
          act.undo();
          // and move left one
          presentAction -= 1;

          bufferChanged();
        }
      }
    }
  }

  /**
   * get a label describing the next thing which may be undone
   * 
   * @return a String describing the next thing which may be undone
   */
  public final String undoLabel()
  {
    final String res = null;

    return res;
  }
}
