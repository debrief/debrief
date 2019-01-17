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
import java.awt.Image;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;

import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;
import org.pushingpixels.flamingo.api.common.FlamingoCommand.FlamingoCommandBuilder;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class MenuUtils
{
  public static FlamingoCommand addCommand(final String commandName,final String imagePath, final ActionListener actionToAdd,final JRibbonBand mapBand,RibbonElementPriority priority) {
    ImageWrapperResizableIcon imageIcon = null;
    if(imagePath!=null) {
      Image zoominImage = createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, new Dimension(16,16));
    }
    FlamingoCommand command = new FlamingoCommandBuilder()
        .setTitle(commandName)
        .setIcon(imageIcon)
        .setAction(actionToAdd)
        .setTitleClickAction().build();
    mapBand.addRibbonCommand(command,priority==null?RibbonElementPriority.TOP:priority);
    return command;
  }
  
  public static JCommandButton addCommandButton(final String commandName,final String imagePath, final ActionListener actionToAdd,CommandButtonDisplayState priority) {
    ImageWrapperResizableIcon imageIcon = null;
    if(imagePath!=null) {
      Image zoominImage = createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, new Dimension(16,16));
    }
    JCommandButton commandButton = new JCommandButton(commandName,imageIcon);
    commandButton.addActionListener(actionToAdd);
    commandButton.setDisplayState(priority);
    return commandButton;
  }
  
  public static Image createImage(String imageName)
  {
    final URL iconURL = MenuUtils.class.getClassLoader().
                            getResource(imageName);
    
    if(iconURL != null) {
      ImageIcon icon = new ImageIcon(iconURL);
      return icon.getImage();
    }
    return null;
    
  }
}
