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

import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.mwc.debrief.lite.undo.RedoAction;
import org.mwc.debrief.lite.undo.UndoAction;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;
import org.pushingpixels.flamingo.api.common.FlamingoCommand.FlamingoCommandBuilder;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import Debrief.GUI.Frames.Application;
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
    @SuppressWarnings("unused")
    private final String _path;

    public HelpAction(String path)
    {
      _path = path;
    }

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      if (Desktop.isDesktopSupported())
      {
//        JOptionPane.showMessageDialog(null, "Please see the file titled ReadMe.pdf");

        try
        {
          final File myFile = new File(_path);
          Desktop.getDesktop().open(myFile);
        }
        catch (Exception ex)
        {
          Application.logError2(Application.ERROR, "Failed to open PDF", ex);
          SwingUtilities.invokeLater(new Runnable()
          {
            @Override
            public void run()
            {
              JOptionPane.showMessageDialog(null, "Failed to find help file:"
                  + _path);
            }
          });
        }
      }
    }

  }

  protected static void addLiteTab(final JRibbon ribbon, final Session session,
      final Runnable resetAction, final Runnable exitAction,
      final Runnable collapseAction, final String path)
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
    
    final FlamingoCommand collapseCommand = addToggleCommand("Collapse",
        "icons/24/fit_to_win.png", collapsePopup);
    // so that action has the command it has to enable/disable
    collapseCommand.setEnabled(true);
    ribbon.addTaskbarCommand(collapseCommand);

    // add the action as observer of undobuffer
    session.getUndoBuffer().addObserver(redoAction);
    liteMenu.startGroup();
    MenuUtils.addCommand("Help", "icons/24/help.png", new HelpAction(path),
        liteMenu, RibbonElementPriority.TOP);
    MenuUtils.addCommand("Exit", "icons/24/exit.png", new ExitLiteApp(
        exitAction), liteMenu, RibbonElementPriority.TOP);

    liteMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        liteMenu));
    final RibbonTask liteTask = new RibbonTask("Lite", liteMenu);
    ribbon.addTask(liteTask);
  }
  
  private static FlamingoCommand addToggleCommand(String commandName,String imagePath,ActionListener actionToAdd) {
    ImageWrapperResizableIcon imageIcon = null;
    if (imagePath != null)
    {
      final Image zoominImage = MenuUtils.createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, MenuUtils.ICON_SIZE_16);
    }
    final FlamingoCommandBuilder builder = new FlamingoCommandBuilder()
        .setTitle(commandName).setIcon(imageIcon).setAction(actionToAdd)
        .setTitleClickAction();

      builder.setToggle();
      builder.setToggleSelected(false);
      final FlamingoCommand command = builder.build();

      return command;
  }
}
