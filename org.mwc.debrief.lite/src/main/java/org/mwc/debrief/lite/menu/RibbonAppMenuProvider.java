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

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.mwc.debrief.lite.about.AboutAction;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;
import org.pushingpixels.flamingo.api.common.FlamingoCommand.FlamingoCommandBuilder;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuPrimaryCommand;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuPrimaryCommand.PrimaryClearRolloverCallback;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuPrimaryCommand.RibbonApplicationMenuPrimaryCommandBuilder;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class RibbonAppMenuProvider
{
  public RibbonApplicationMenu createApplicationMenu(JRibbonFrame theFrame) {
    RibbonApplicationMenu appMenu = new RibbonApplicationMenu("Lite");
    RibbonApplicationMenuPrimaryCommand contentsMenu = new
        RibbonApplicationMenuPrimaryCommandBuilder()
        .setTitle("Help Contents")
        .setIcon(ImageWrapperResizableIcon.getIcon(MenuUtils.createImage("images/icon.png"), new Dimension(16,16)))
        .setAction(new AbstractAction()
        {

          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e)
          {
            System.out.println("event clicked");

          }
        })
        .setActionKeyTip("S").build();

    FlamingoCommand aboutMenu = new FlamingoCommandBuilder()
        .setTitle("About Us")
        .setIcon(ImageWrapperResizableIcon.getIcon(MenuUtils.createImage("images/icon.png"), new Dimension(16,16)))
        .setAction(new AboutAction("About",theFrame))
        .setActionKeyTip("W").build();

    RibbonApplicationMenuPrimaryCommand helpAppMenu = new
        RibbonApplicationMenuPrimaryCommandBuilder()
        .setTitle("Help")
        .setIcon(ImageWrapperResizableIcon.getIcon(MenuUtils.createImage("images/icon.png"), new Dimension(16,16)))
        .setAction(new AbstractAction("Help")
        {

          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e)
          {
            System.out.println("event clicked");

          }
        })
        .setActionKeyTip("A").setPopupKeyTip("F").setTitleClickAction()
        .addSecondaryMenuGroup(
            "Help Menu",
            contentsMenu,aboutMenu)
        .build();
    RibbonApplicationMenuPrimaryCommand exitAction = new
        RibbonApplicationMenuPrimaryCommandBuilder()
        .setTitle("Exit")
        .setIcon(
            ImageWrapperResizableIcon.getIcon(
                MenuUtils.createImage("images/16/exit.png"),new Dimension(16,16)))
        .setAction(new AbstractAction()
        {

          /**
           * 
           */
          private static final long serialVersionUID = 1L;

          @Override
          public void actionPerformed(ActionEvent e)
          {
            System.exit(0);
          }
        })
        .setActionKeyTip("X").setRolloverCallback(new PrimaryClearRolloverCallback())
        .build();
    appMenu.addMenuCommand(helpAppMenu);
    appMenu.addMenuSeparator();
    appMenu.addMenuCommand(exitAction);

    return appMenu;
  }
}
