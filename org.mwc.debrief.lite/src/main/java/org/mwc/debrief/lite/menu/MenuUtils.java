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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import org.mwc.debrief.lite.util.ResizableIconFactory;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandButtonPresentationState;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.model.Command;
import org.pushingpixels.flamingo.api.common.model.CommandButtonPresentationModel;
import org.pushingpixels.flamingo.api.common.model.CommandToggleGroupModel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.common.popup.model.CommandPopupMenuPresentationModel;
import org.pushingpixels.flamingo.api.common.projection.CommandButtonProjection;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand.PresentationPriority;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;



/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class MenuUtils
{
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

  public static final Dimension ICON_SIZE_16 = new Dimension(16, 16);
  public static final Dimension ICON_SIZE_24 = new Dimension(24, 24);
  public static final Dimension ICON_SIZE_32 = new Dimension(32, 32);
  public static final Dimension ICON_SIZE_64 = new Dimension(64, 64);

  public static CommandButtonProjection<Command> addCommand(final String commandName,
      final String imagePath, final CommandAction actionToAdd,
      final JRibbonBand mapBand, final PresentationPriority priority)
  {
    return addCommand(commandName, imagePath, actionToAdd, mapBand, priority,
        null);
  }
  
  /**
   * 
   * @param commandName
   * @param imagePath
   * @param actionToAdd
   * @param priority
   * @returns the command object, without adding it to a band.
   */
  public static Command createCommandObject(final String commandName,
      final String imagePath, final CommandAction actionToAdd,
      final PresentationPriority priority,String description)
  {
    ImageWrapperResizableIcon imageIcon = null;
    if (imagePath != null)
    {
      final Image zoominImage = createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, new Dimension(
          16, 16));
    }
    
    final Command.Builder builder = Command.builder()
        .setText(commandName).setIconFactory(ResizableIconFactory.factory(imageIcon))
        .setActionRichTooltip(RichTooltip.builder().setTitle(description).build())
        .setAction(actionToAdd);
        /*;*/

    final Command command = builder.build();
    return command;
  }

  /**
   * 
   * @param commandName
   * @param imagePath
   * @param actionToAdd
   * @param mapBand
   * @param priority
   * @param popupCallback
   * @return CommandButtonProjection the projected command button
   */
  public static CommandButtonProjection<Command> addCommand(final String commandName,
      final String imagePath, final CommandAction actionToAdd,
      final JRibbonBand mapBand, final PresentationPriority priority,
      final PopupPanelCallback popupCallback)
  {
    final CommandButtonProjection<Command> command = createCommand(commandName, imagePath,
        actionToAdd, priority, popupCallback);

    mapBand.addRibbonCommand(command, priority == null
        ? PresentationPriority.TOP : priority);
    return command;
  }

  public static CommandButtonProjection<Command> addCommandButton(final String commandName,
      final String imagePath, final CommandAction actionToAdd,
      final CommandButtonPresentationState priority, final String description)
  {
    /*
     * ImageWrapperResizableIcon imageIcon = null; if (imagePath != null) { final Image zoominImage
     * = createImage(imagePath); imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, new
     * Dimension( 16, 16)); }
     */
    CommandButtonProjection<Command> projectionModel = createCommand(commandName,imagePath,actionToAdd,PresentationPriority.MEDIUM,null);
    JCommandButton commandButton = new JCommandButton(projectionModel);
    final RichTooltip.Builder builder = RichTooltip.builder();

    final String desc = description != null ? description
        : "Description pending";

    final RichTooltip richTooltip = builder.setTitle(commandName)
        .addDescriptionSection(desc).build();
    commandButton.setActionRichTooltip(richTooltip);
    commandButton.setName(commandName);
    commandButton.addCommandListener(actionToAdd);
    if (priority != null)
    {
      commandButton.setPresentationState(priority);
    }
    return projectionModel;
  }

  public static JCommandToggleButton addCommandToggleButton(
      final String commandName, final String imagePath,
      final CommandAction actionToAdd,
      final CommandButtonPresentationState priority)
  {
    CommandButtonProjection<Command> projectionModel = createCommand(commandName,imagePath,actionToAdd,PresentationPriority.MEDIUM,null);
    
    final JCommandToggleButton commandButton = new JCommandToggleButton(
        projectionModel);
    commandButton.setName(commandName);
    commandButton.setPresentationState(priority);
    return commandButton;
  }

  public static Command addCommandToggleButton(final String commandName,
      final String imagePath, final CommandAction actionToAdd,
      final JRibbonBand mapBand, final PresentationPriority priority,
      final boolean isToggle, final CommandToggleGroupModel group,
      final boolean toggleSelected)
  {
    ImageWrapperResizableIcon imageIcon = null;
    if (imagePath != null)
    {
      final Image zoominImage = createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, ICON_SIZE_16);
    }
    final Command.Builder builder = Command.builder()
        .setText(commandName).setIconFactory(ResizableIconFactory.factory(imageIcon)).setAction(actionToAdd);
        //.setTitleClickAction();

    if (isToggle)
    {
      builder.setToggle();
      builder.setToggleSelected(toggleSelected);
      if (group != null)
      {
        builder.inToggleGroup(group);
      }
    }
    final Command command = builder.build();
    CommandButtonProjection<Command> projectionModel =  command.project(CommandButtonPresentationModel.builder()
        .setActionKeyTip("NA")
        //.setPopupCallback(popupCallback)
        .setPopupMenuPresentationModel(CommandPopupMenuPresentationModel.builder()
            .setMaxVisibleMenuCommands(4)
            .build())
        .build());
    mapBand.addRibbonCommand(projectionModel, priority == null
        ? PresentationPriority.TOP : priority);
    return command;
  }

  public static CommandButtonProjection<Command> createCommand(final String commandName,
      final String imagePath, final CommandAction actionToAdd,
      final PresentationPriority priority,
      final PopupPanelCallback popupCallback)
  {
    ImageWrapperResizableIcon imageIcon = null;
    if (imagePath != null)
    {
      final Image zoominImage = createImage(imagePath);
      imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, new Dimension(
          16, 16));
    }
    
    final Command.Builder builder = Command.builder()
        .setText(commandName).setIconFactory(ResizableIconFactory.factory(imageIcon))
        .setAction(actionToAdd).setActionRichTooltip(RichTooltip.builder().setTitle(commandName).build());
        /*;*/

    if (popupCallback != null)
    {
      //builder.setPopupCallback(popupCallback);
    }
    final Command command = builder.build();
    return command.project(CommandButtonPresentationModel.builder()
        .setActionKeyTip("NA")
        //.setPopupCallback(popupCallback)
        .setPopupMenuPresentationModel(CommandPopupMenuPresentationModel.builder()
            .setMaxVisibleMenuCommands(4)
            .build())
        .build());
   
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
    policies.add(new CoreRibbonResizePolicies.Mid2Low(ribbonBand));
    policies.add(new CoreRibbonResizePolicies.High2Low(ribbonBand));
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
    policies.add(new CoreRibbonResizePolicies.IconRibbonBandResizePolicy(
        ribbonBand));
    return policies;
  }

}