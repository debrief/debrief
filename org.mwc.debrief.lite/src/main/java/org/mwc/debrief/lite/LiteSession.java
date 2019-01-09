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
package org.mwc.debrief.lite;

import java.awt.datatransfer.Clipboard;

import Debrief.GUI.Frames.Session;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.GUI.Undo.UndoBuffer;

class LiteSession extends Session
{
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public LiteSession(final Clipboard clipboard, final Layers layers)
  {
    super(clipboard, layers);
  }

  @Override
  public void closeGUI()
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public void initialiseForm(final ToolParent theParent)
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  public void repaint()
  {
    throw new IllegalArgumentException("Not implemented");
  }

  @Override
  protected boolean wantsToClose()
  {
    throw new IllegalArgumentException("Not implemented");
  }

}