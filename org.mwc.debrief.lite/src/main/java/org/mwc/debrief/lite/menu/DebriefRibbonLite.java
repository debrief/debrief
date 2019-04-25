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
package org.mwc.debrief.lite.menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.undo.RedoAction;
import org.mwc.debrief.lite.undo.UndoAction;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import Debrief.GUI.Frames.Session;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class DebriefRibbonLite
{
  
  private static class ExitLiteApp extends AbstractAction
  {

   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Override
    public void actionPerformed(ActionEvent e)
    {
      DebriefLiteApp.getInstance().exit();
    }
    
  }
  private static class HelpAction extends AbstractAction
  {


    public HelpAction()
    {
      super();
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

   
    @Override
    public void actionPerformed(ActionEvent e)
    {
      System.out.println("Not implemented yet");
    }

  }


  private static RibbonTask liteTask;
  

  protected static void addLiteTab(final JRibbon ribbon,
      final Session session, final Runnable resetAction)
  {
    final JRibbonBand liteMenu = new JRibbonBand("Lite", null);
    liteMenu.startGroup();
    FlamingoCommand undoCommand = MenuUtils.createCommand("Undo", "icons/24/undo.png", new UndoAction(),
        RibbonElementPriority.TOP);
    ribbon.addTaskbarCommand(undoCommand);
    FlamingoCommand redoCommand = MenuUtils.createCommand("Redo", "icons/24/redo.png", 
        new RedoAction(),
        RibbonElementPriority.TOP);
    ribbon.addTaskbarCommand(redoCommand);
    liteMenu.startGroup();
    MenuUtils.addCommand("Help", "icons/24/help.png", new HelpAction(), liteMenu,
        RibbonElementPriority.TOP);
    MenuUtils.addCommand("Exit", "icons/24/exit.png", 
        new ExitLiteApp(),
        liteMenu, RibbonElementPriority.TOP);
    
    liteMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(liteMenu));
    liteTask = new RibbonTask("Lite", liteMenu);
    ribbon.addTask(liteTask);
  }


  public static RibbonTask getLiteTask()
  {
    return liteTask;
  }
}
