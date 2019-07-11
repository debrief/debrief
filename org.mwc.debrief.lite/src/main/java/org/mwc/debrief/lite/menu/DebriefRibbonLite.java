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
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

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
    private final Runnable _action;

    public ExitLiteApp(Runnable exitAction)
    {
      _action = exitAction;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      _action.run();
    }

  }

  private static class HelpAction extends AbstractAction
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      System.out.println("Not implemented yet");
    }

  }

  protected static void addLiteTab(final JRibbon ribbon, final Session session,
      final Runnable resetAction, final Runnable exitAction,
      final Runnable collapseAction)
  {
    final JRibbonBand liteMenu = new JRibbonBand("Lite", null);
    liteMenu.startGroup();
    final UndoAction undoAction = new UndoAction(session.getUndoBuffer());
    final FlamingoCommand undoCommand = MenuUtils.createCommand("Undo",
        "icons/24/undo.png", undoAction, RibbonElementPriority.TOP, null);
    // so that action has the command it has to enable/disable
    undoAction.setActionCommand(undoCommand);
    undoCommand.setEnabled(false);
    // add the undoaction as observer for undobuffer
    session.getUndoBuffer().addObserver(undoAction);
    ribbon.addTaskbarCommand(undoCommand);
    final RedoAction redoAction = new RedoAction(session.getUndoBuffer());
    final FlamingoCommand redoCommand = MenuUtils.createCommand("Redo",
        "icons/24/redo.png", redoAction, RibbonElementPriority.TOP, null);
    // so that action has the command it has to enable/disable
    redoAction.setActionCommand(redoCommand);
    redoCommand.setEnabled(false);
    ribbon.addTaskbarCommand(redoCommand);

    ActionListener collapsePopup = new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        collapseAction.run();
      }
    };
    // and the expand/collapse button
    final FlamingoCommand collapseCommand = MenuUtils.createCommand("Collapse",
        "icons/24/fit_to_win.png", collapsePopup, RibbonElementPriority.TOP, null);
    // so that action has the command it has to enable/disable
    collapseCommand.setEnabled(true);
    ribbon.addTaskbarCommand(collapseCommand);

    // add the action as observer of undobuffer
    session.getUndoBuffer().addObserver(redoAction);
    liteMenu.startGroup();
    MenuUtils.addCommand("Help", "icons/24/help.png", new HelpAction(),
        liteMenu, RibbonElementPriority.TOP);
    MenuUtils.addCommand("Exit", "icons/24/exit.png", new ExitLiteApp(
        exitAction), liteMenu, RibbonElementPriority.TOP);

    liteMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        liteMenu));
    final RibbonTask liteTask = new RibbonTask("Lite", liteMenu);
    ribbon.addTask(liteTask);
  }
}
