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
package org.mwc.debrief.lite.outline;

import MWC.GUI.LayerManager.Swing.SwingLayerManager;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class OutlinePanelView extends SwingLayerManager
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public OutlinePanelView() {
    super();
  }

  @Override
  protected void initForm()
  {
    super.initForm();
    setCellRenderer(new OutlineRenderer());
  }
}
