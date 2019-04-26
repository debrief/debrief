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
package org.mwc.debrief.lite.undo;

import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;

import org.pushingpixels.flamingo.api.common.FlamingoCommand;

import MWC.GUI.Tools.Action;
import MWC.GUI.Undo.UndoBuffer;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class RedoAction extends AbstractAction implements Action, Observer
{
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private FlamingoCommand actionCommand;
  private final UndoBuffer _buffer;

  public RedoAction(final UndoBuffer undoBuffer)
  {
    _buffer = undoBuffer;
  }

  @Override
  public void actionPerformed(final ActionEvent e)
  {
    execute();

  }

  @Override
  public void execute()
  {
    if (_buffer != null)
    {
      _buffer.redo();
      // _theParent.getCurrentSession().repaint();
    }
  }

  @Override
  public boolean isRedoable()
  {
    return false;
  }

  @Override
  public boolean isUndoable()
  {
    return false;
  }

  public void setActionCommand(final FlamingoCommand command)
  {
    actionCommand = command;
  }

  @Override
  public void undo()
  {

  }

  @Override
  public void update(final Observable o, final Object arg)
  {
    if (o instanceof UndoBuffer && actionCommand != null)
    {
      final UndoBuffer undoBuff = (UndoBuffer) o;
      if (undoBuff.canRedo())
      {
        actionCommand.setEnabled(true);
      }
      else
      {
        actionCommand.setEnabled(false);
      }
    }

  }

}
