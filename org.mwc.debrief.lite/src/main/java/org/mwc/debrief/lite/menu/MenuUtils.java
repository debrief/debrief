/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

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
import org.pushingpixels.radiance.theming.api.RadianceThemingSlices.IconFilterStrategy;
import org.pushingpixels.radiance.component.api.common.CommandAction;
import org.pushingpixels.radiance.component.api.common.CommandButtonPresentationState;
import org.pushingpixels.radiance.component.api.common.RichTooltip;
import org.mwc.debrief.lite.util.ScaledImageIcon;
import org.pushingpixels.radiance.component.api.common.model.Command;
import org.pushingpixels.radiance.component.api.common.model.CommandButtonPresentationModel;
import org.pushingpixels.radiance.component.api.common.model.CommandToggleGroupModel;
import org.pushingpixels.radiance.component.api.common.popup.model.CommandPopupMenuPresentationModel;
import org.pushingpixels.radiance.component.api.common.projection.CommandButtonProjection;
import org.pushingpixels.radiance.component.api.ribbon.JRibbonBand;
import org.pushingpixels.radiance.component.api.ribbon.JRibbonBand.PresentationPriority;
import org.pushingpixels.radiance.component.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.radiance.component.api.ribbon.resize.RibbonBandResizePolicy;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class MenuUtils {
	protected static class TODOAction extends AbstractAction {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			System.out.println("Action TODO");

		}
	}

	public static final Dimension ICON_SIZE_16 = new Dimension(16, 16);
	public static final Dimension ICON_SIZE_24 = new Dimension(24, 24);
	public static final Dimension ICON_SIZE_32 = new Dimension(32, 32);
	public static final Dimension ICON_SIZE_64 = new Dimension(64, 64);

	/**
	 *
	 * @param commandName
	 * @param imagePath
	 * @param actionToAdd
	 * @param mapBand
	 * @param priority
	 * @return CommandButtonProjection the projected command button
	 */
	public static CommandButtonProjection<Command> addCommand(final String commandName, final String imagePath,
			final CommandAction actionToAdd, final JRibbonBand mapBand, final PresentationPriority priority,
			final String tooltip) {
		final CommandButtonProjection<Command> command = createCommand(commandName, imagePath, actionToAdd, tooltip);

		mapBand.addRibbonCommand(command, priority == null ? PresentationPriority.TOP : priority);
		return command;
	}

	public static CommandButtonProjection<Command> addCommandButton(final String commandName, final String imagePath,
			final CommandAction actionToAdd, final CommandButtonPresentationState priority, final String description) {
		/*
		 * ImageWrapperResizableIcon imageIcon = null; if (imagePath != null) { final
		 * Image zoominImage = createImage(imagePath); imageIcon =
		 * ImageWrapperResizableIcon.getIcon(zoominImage, new Dimension( 16, 16)); }
		 */
		final String desc = description != null ? description : "Description pending";
		final RichTooltip richTooltip = RichTooltip.builder().setTitle(commandName).addDescriptionSection(desc)
				.build();
		final Command command = createCommand(commandName, imagePath, actionToAdd, richTooltip).getContentModel();
		// the presentation state belongs on the projection: a flow band shows the
		// button as its projection says (Radiance 9 defaults to icon only)
		final CommandButtonPresentationModel.Builder presentation = buttonPresentation().setActionKeyTip("NA");
		if (priority != null) {
			presentation.setPresentationState(priority);
		}
		return command.project(presentation.build());
	}

	/*
	 * public static JCommandToggleButton addCommandToggleButton(final String
	 * commandName, final String imagePath, final CommandAction actionToAdd, final
	 * CommandButtonPresentationState priority, final String tooltip) { final
	 * CommandButtonProjection<Command> projectionModel = createCommand(commandName,
	 * imagePath, actionToAdd, tooltip);
	 * 
	 * final JCommandToggleButton commandButton = new
	 * JCommandToggleButton(projectionModel); commandButton.setName(commandName);
	 * commandButton.setPresentationState(priority); return commandButton; }
	 */
	public static Command addCommandToggleButton(final String commandName, final String imagePath,
			final CommandAction actionToAdd, final JRibbonBand mapBand, final PresentationPriority priority,
			final boolean isToggle, final CommandToggleGroupModel group, final boolean toggleSelected) {
		ScaledImageIcon imageIcon = null;
		if (imagePath != null) {
			final Image zoominImage = createImage(imagePath);
			imageIcon = ScaledImageIcon.getIcon(zoominImage, ICON_SIZE_16);
		}
		final Command.Builder builder = Command.builder().setText(commandName)
				.setIconFactory(ResizableIconFactory.factory(imageIcon)).setAction(actionToAdd)
				.setActionRichTooltip(RichTooltip.builder().setTitle(commandName).build());
		// .setTitleClickAction();

		if (isToggle) {
			builder.setToggle();
			builder.setToggleSelected(toggleSelected);
			if (group != null) {
				builder.inToggleGroup(group);
			}
		}
		final Command command = builder.build();
		final CommandButtonProjection<Command> projectionModel = command
				.project(buttonPresentation().setActionKeyTip("NA")
						.setPopupMenuPresentationModel(
								CommandPopupMenuPresentationModel.builder().setMaxVisibleItems(4).build())
						.build());
		mapBand.addRibbonCommand(projectionModel, priority == null ? PresentationPriority.TOP : priority);
		return command;
	}

	public static CommandButtonProjection<Command> createCommand(final String commandName, final String imagePath,
			final CommandAction actionToAdd, final String tooltip) {
		return createCommand(commandName, imagePath, actionToAdd, RichTooltip.builder().setTitle(tooltip).build());
	}

	/** a command with its icon, action and (action) rich tooltip, projected as a button */
	static CommandButtonProjection<Command> createCommand(final String commandName, final String imagePath,
			final CommandAction actionToAdd, final RichTooltip tooltip) {
		ScaledImageIcon imageIcon = null;
		if (imagePath != null) {
			final Image zoominImage = createImage(imagePath);
			imageIcon = ScaledImageIcon.getIcon(zoominImage, new Dimension(16, 16));
		}

		final Command.Builder builder = Command.builder().setText(commandName)
				.setIconFactory(ResizableIconFactory.factory(imageIcon)).setAction(actionToAdd)
				.setActionRichTooltip(tooltip);

		final Command command = builder.build();
		return command.project(buttonPresentation().setActionKeyTip("NA")
				.setPopupMenuPresentationModel(
						CommandPopupMenuPresentationModel.builder().setMaxVisibleItems(4).build())
				.build());

	}

	/**
	 *
	 * @param commandName
	 * @param imagePath
	 * @param actionToAdd
	 * @param priority
	 * @returns the command object, without adding it to a band.
	 */
	public static Command createCommandObject(final String commandName, final String imagePath,
			final CommandAction actionToAdd, final PresentationPriority priority, final String description) {
		ScaledImageIcon imageIcon = null;
		if (imagePath != null) {
			final Image zoominImage = createImage(imagePath);
			imageIcon = ScaledImageIcon.getIcon(zoominImage, new Dimension(16, 16));
		}

		final Command.Builder builder = Command.builder().setText(commandName)
				.setIconFactory(ResizableIconFactory.factory(imageIcon))
				.setActionRichTooltip(RichTooltip.builder().setTitle(description).build()).setAction(actionToAdd);
		/* ; */

		final Command command = builder.build();
		return command;
	}

	/**
	 * the icon filters for a button: disabled icons take the colour of disabled
	 * text (light grey), as in Radiance 3. Radiance 9's default greys them much
	 * darker, so that disabled buttons looked enabled.
	 */
	public static final IconFilterStrategy[] ICON_FILTERS = { IconFilterStrategy.ORIGINAL,
			IconFilterStrategy.ORIGINAL, IconFilterStrategy.THEMED_FOLLOW_TEXT };

	/** a button presentation model builder with Lite's icon filters */
	public static CommandButtonPresentationModel.Builder buttonPresentation() {
		return CommandButtonPresentationModel.builder().setIconFilterStrategies(ICON_FILTERS[0], ICON_FILTERS[1],
				ICON_FILTERS[2]);
	}

	public static Image createImage(final String imageName) {
		final URL iconURL = MenuUtils.class.getClassLoader().getResource(imageName);

		if (iconURL != null) {
			final ImageIcon icon = new ImageIcon(iconURL);
			return icon.getImage();
		}
		return null;

	}

	protected static void exit() {
		// _dropSupport.removeFileDropListener(this);
		System.exit(0);

	}

	public static List<RibbonBandResizePolicy> getStandardRestrictivePolicies(final JRibbonBand ribbonBand) {
		final List<RibbonBandResizePolicy> policies = new ArrayList<>();
		policies.add(new CoreRibbonResizePolicies.Mirror(ribbonBand));
		policies.add(new CoreRibbonResizePolicies.Mid2Low(ribbonBand));
		policies.add(new CoreRibbonResizePolicies.High2Low(ribbonBand));
		return policies;
	}

	public static List<RibbonBandResizePolicy> getStandardRestrictivePolicies2(final JRibbonBand ribbonBand) {
		final List<RibbonBandResizePolicy> policies = new ArrayList<>();
		policies.add(new CoreRibbonResizePolicies.None(ribbonBand));
		policies.add(new CoreRibbonResizePolicies.Mirror(ribbonBand));
		policies.add(new CoreRibbonResizePolicies.Mid2Low(ribbonBand));
		policies.add(new CoreRibbonResizePolicies.High2Low(ribbonBand));
		policies.add(new CoreRibbonResizePolicies.IconRibbonBandResizePolicy(ribbonBand));
		return policies;
	}

}