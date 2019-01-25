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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.FlamingoCommand;
import org.pushingpixels.flamingo.api.common.FlamingoCommand.FlamingoCommandBuilder;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class MenuUtils
{
  public static final Dimension ICON_SIZE_16 = new Dimension(16,16);
  public static final Dimension ICON_SIZE_24 = new Dimension(24,24);
  public static final Dimension ICON_SIZE_32 = new Dimension(32,32);
  public static final Dimension ICON_SIZE_48 = new Dimension(48,48);
  protected static class TODOAction extends AbstractAction
  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(final ActionEvent e)
    {
      System.out.println("Action TODO");

    }
  }

  public static FlamingoCommand addCommand(final String commandName,
      final String imagePath, final ActionListener actionToAdd,
      final JRibbonBand mapBand, final RibbonElementPriority priority)
  {
    ImageWrapperResizableIcon imageIcon = null;
    if (imagePath != null)
    {
      final Image zoominImage = createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, ICON_SIZE_16);
    }
    final FlamingoCommand command = new FlamingoCommandBuilder().setTitle(
        commandName).setIcon(imageIcon).setAction(actionToAdd)
        .setTitleClickAction().build();
    mapBand.addRibbonCommand(command, priority == null
        ? RibbonElementPriority.TOP : priority);
    return command;
  }

  public static JCommandButton addCommandButton(final String commandName,
      final String imagePath, final ActionListener actionToAdd,
      final CommandButtonDisplayState priority)
  {
    ImageWrapperResizableIcon imageIcon = null;
    if (imagePath != null)
    {
      final Image zoominImage = createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, ICON_SIZE_16);
    }
    final JCommandButton commandButton = new JCommandButton(commandName,
        imageIcon);
    commandButton.addActionListener(actionToAdd);
    commandButton.setDisplayState(priority);
    return commandButton;
  }
  
  public static JCommandToggleButton addCommandToggleButton(final String commandName,
      final String imagePath, final ActionListener actionToAdd,
      final CommandButtonDisplayState priority)
  {
    ImageWrapperResizableIcon imageIcon = null;
    if (imagePath != null)
    {
      final Image zoominImage = createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, ICON_SIZE_16);
    }
    final JCommandToggleButton commandButton = new JCommandToggleButton(commandName,
        imageIcon);
    commandButton.addActionListener(actionToAdd);
    commandButton.setDisplayState(priority);
    return commandButton;
  }

  public static Image createImage(final String imageName)
  {
    final URL iconURL = MenuUtils.class.getClassLoader().getResource(imageName);

    if (iconURL != null)
    {
      final ImageIcon icon = new ImageIcon(iconURL);
      return icon.getImage();
    }
    return null;

  }

  protected static void exit()
  {
    // _dropSupport.removeFileDropListener(this);
    System.exit(0);

  }

  public static List<RibbonBandResizePolicy> getStandardRestrictivePolicies(
      final JRibbonBand ribbonBand)
  {
    final List<RibbonBandResizePolicy> policies = new ArrayList<>();
    policies.add(new CoreRibbonResizePolicies.Mirror(ribbonBand));
    // policies.add(new CoreRibbonResizePolicies.Mid2Low(ribbonBand));
    policies.add(new IconRibbonBandResizePolicy(ribbonBand));
    return policies;
  }
  public static List<RibbonBandResizePolicy> getStandardRestrictivePolicies2(
      final JRibbonBand ribbonBand)
  {
    final List<RibbonBandResizePolicy> policies = new ArrayList<>();
    policies.add(new CoreRibbonResizePolicies.None(ribbonBand));
    policies.add(new CoreRibbonResizePolicies.Mirror(ribbonBand));
    policies.add(new CoreRibbonResizePolicies.Mid2Low(ribbonBand));
    policies.add(new CoreRibbonResizePolicies.High2Low(ribbonBand));
    policies.add(new CoreRibbonResizePolicies.IconRibbonBandResizePolicy(ribbonBand));
    return policies;
  }
}