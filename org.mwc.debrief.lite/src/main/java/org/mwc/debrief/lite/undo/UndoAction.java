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

import javax.swing.AbstractAction;

import org.mwc.debrief.lite.DebriefLiteApp;

import MWC.GUI.Tools.Action;
import MWC.GUI.Undo.UndoBuffer;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class UndoAction extends AbstractAction implements Action
{
  
  public UndoAction(){
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
      undoBuffer.undo();
      //_theParent.getCurrentSession().repaint();
    }
    
  }

  @Override
  public boolean isEnabled()
  {
    final UndoBuffer undoBuffer = DebriefLiteApp.getInstance().getUndoBuffer();
    return undoBuffer!=null && undoBuffer.hasChanged() && undoBuffer.containsActions();

  }
  

  @Override
  public void undo()
  {
    
  }



  @Override
  public void actionPerformed(ActionEvent e)
  {
    execute();
    
  }



  @Override
  public boolean isUndoable()
  {
    return false;
  }



  @Override
  public boolean isRedoable()
  {
    return false;
  }

  
}
