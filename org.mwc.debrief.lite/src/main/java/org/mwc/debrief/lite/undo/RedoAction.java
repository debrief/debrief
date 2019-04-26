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

import org.mwc.debrief.lite.DebriefLiteApp;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;

import MWC.GUI.Tools.Action;
import MWC.GUI.Undo.UndoBuffer;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class RedoAction extends AbstractAction implements Action,Observer
{
  private FlamingoCommand actionCommand;
  public RedoAction()
  {
  }
  
  public void setActionCommand(FlamingoCommand command)
  {
    actionCommand = command;
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  
  @Override
  public void execute()
  {
    final UndoBuffer undoBuffer = DebriefLiteApp.getInstance().getUndoBuffer();
    if(undoBuffer!=null) {
      undoBuffer.redo();
      //_theParent.getCurrentSession().repaint();
    }
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    execute();
    
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
  

  @Override
  public void undo()
  {
    
  }

  @Override
  public void update(Observable o, Object arg)
  {
    if(o instanceof UndoBuffer && actionCommand!=null) {
      final UndoBuffer undoBuff = (UndoBuffer)o;
      if(undoBuff.canRedo()) {
        actionCommand.setEnabled(true);
      }
      else {
        actionCommand.setEnabled(false);
      }
    }
    
  }

}
