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

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.custom.JRibbonLabel;
import org.mwc.debrief.lite.custom.JRibbonSlider;
import org.mwc.debrief.lite.custom.LabelComponentContentModel;
import org.mwc.debrief.lite.custom.RibbonLabelProjection;
import org.mwc.debrief.lite.custom.RibbonSliderProjection;
import org.mwc.debrief.lite.custom.SliderComponentContentModel;
import org.mwc.debrief.lite.gui.FitToWindow;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.gui.ZoomOut;
import org.mwc.debrief.lite.gui.custom.SubstanceCommandToggleWithMenuButtonUI;
import org.mwc.debrief.lite.map.AdvancedZoomInAction;
import org.mwc.debrief.lite.map.DragElementAction;
import org.mwc.debrief.lite.map.DragElementTool;
import org.mwc.debrief.lite.map.DragWholeFeatureElementTool;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.RangeBearingAction;
import org.mwc.debrief.lite.map.RangeBearingTool;
import org.mwc.debrief.lite.util.ResizableIconFactory;
import org.mwc.debrief.lite.view.actions.PanCommandAction;
import org.opengis.referencing.operation.MathTransform;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandMenuButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleButton;
import org.pushingpixels.flamingo.api.common.JCommandToggleMenuButton;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.model.Command;
import org.pushingpixels.flamingo.api.common.model.Command.Builder;
import org.pushingpixels.flamingo.api.common.model.CommandButtonPresentationModel;
import org.pushingpixels.flamingo.api.common.model.CommandToggleGroupModel;
import org.pushingpixels.flamingo.api.common.popup.model.CommandPopupMenuPresentationModel;
import org.pushingpixels.flamingo.api.common.projection.CommandButtonProjection;
import org.pushingpixels.flamingo.api.common.projection.Projection;
import org.pushingpixels.flamingo.api.common.projection.Projection.ComponentSupplier;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand.PresentationPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.synapse.model.ComponentPresentationModel;
import org.pushingpixels.flamingo.api.ribbon.synapse.projection.ComponentProjection;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.Layers;
import MWC.GenericData.WorldDistance;

public class DebriefRibbonView {
	public static class CustomBuilder extends Builder {
		private JPopupMenu popMenu;

		public CustomBuilder() {

		}

		@Override
		public Command build() {
			final Command command = new CustomCommand();
			configureBaseCommand(command);

			return command;
		}

		@Override
		protected void configureBaseCommand(final Command command) {
			super.configureBaseCommand(command);
			if (command instanceof CustomCommand) {
				((CustomCommand) command).setPopMenu(this.popMenu);
			}
		}

		public Builder setPopMenu(final JPopupMenu popMenu) {
			this.popMenu = popMenu;
			return this;
		}

	}

	public static class CustomCommand extends Command {
		private JPopupMenu popMenu;

		public CustomCommand() {

		}

		public JPopupMenu getPopMenu() {
			return popMenu;
		}

		@Override
		public CommandButtonProjection<Command> project(final CommandButtonPresentationModel commandPresentation) {
			return new CustomCommandButtonProjection(this, commandPresentation);
		}

		public void setPopMenu(final JPopupMenu popMenu) {
			this.popMenu = popMenu;
		}
	}

	public static class CustomCommandButtonProjection extends CommandButtonProjection {

		private static <M extends Command> ComponentSupplier<AbstractCommandButton, M, CommandButtonPresentationModel> getDefaultSupplier() {
			return (final Projection<AbstractCommandButton, M, CommandButtonPresentationModel> projection) -> {
				if (projection.getPresentationModel().isMenu()) {
					return projection.getContentModel().isToggle() ? JCommandToggleMenuButton::new
							: JCommandMenuButton::new;
				} else {
					return projection.getContentModel().isToggle() ? JCommandToggleWithMenuButton::new
							: JCommandButton::new;
				}
			};
		}

		public CustomCommandButtonProjection(final Command command,
				final CommandButtonPresentationModel commandPresentation) {
			super(command, commandPresentation, getDefaultSupplier());
		}
	}

	public static class JCommandToggleWithMenuButton extends JCommandToggleButton {

		/**
		 *
		 */
		private static final long serialVersionUID = -4029977054020995335L;

		public JCommandToggleWithMenuButton(
				final Projection<AbstractCommandButton, ? extends Command, CommandButtonPresentationModel> projection) {
			super(projection);

			addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					if (!SubstanceCommandToggleWithMenuButtonUI.MENU_INDICATOR_POLYGON.contains(e.getPoint())) {
						super.mouseClicked(e);
					} else {
						final JPopupMenu popMenu = getPopupMenu();

						if (popMenu != null) {
							final Component component = (Component) e.getSource();
							popMenu.show(component, 0, 0);

							final Point p = component.getLocationOnScreen();
							popMenu.setLocation(p.x, p.y + component.getHeight());
						}
					}
				}

			});
		}

		public JPopupMenu getPopupMenu() {
			if (command instanceof CustomCommand) {
				return ((CustomCommand) command).getPopMenu();
			}
			return null;
		}

		@Override
		public void updateUI() {
			this.setUI(SubstanceCommandToggleWithMenuButtonUI.createUI(this));
		}

	}

	private static Command zoominButton;
	private static AdvancedZoomInAction zoomInAction;

	private static ComponentProjection<JRibbonSlider, SliderComponentContentModel> addAlphaSlider(
			final ChangeListener alphaListener, final float alpha) {

		final SliderComponentContentModel sliderModel = SliderComponentContentModel.builder().setEnabled(true)
				.setMinimum(0).setMaximum(100).setMajorTickSpacing(20).setPaintTickSpacing(true).setPaintLabels(false)
				.setChangeListener(alphaListener).build();
		// set the values for the slider here.
		final ComponentSupplier<JRibbonSlider, SliderComponentContentModel, ComponentPresentationModel> jribbonSlider = (
				final Projection<JRibbonSlider, SliderComponentContentModel, ComponentPresentationModel> projection) -> JRibbonSlider::new;
		final ComponentProjection<JRibbonSlider, SliderComponentContentModel> projection = new RibbonSliderProjection(
				sliderModel, ComponentPresentationModel.withDefaults(), jribbonSlider);
		final JSlider slider = projection.buildComponent();
		slider.setToolTipText("Modify transparency");
		slider.setBackground(Color.DARK_GRAY);
		slider.setName("transparencyslider");
		slider.setValue((int) (alpha * 100f));
		return projection;
	}

	protected static void addViewTab(final JRibbon ribbon, final GeoToolMapRenderer geoMapRenderer, final Layers layers,
			final JLabel statusBar, final GeoToolMapProjection projection, final MathTransform transform,
			final ChangeListener alphaListener, final float alpha) {
		final JRibbonBand mouseMode = createMouseModes(geoMapRenderer, statusBar, layers, projection, transform);
		final JRibbonBand mapCommands = createMapCommands(geoMapRenderer, layers, projection);

		// and the slider
		final JRibbonBand layersMenu = new JRibbonBand("Background", null);
		final ComponentProjection<JRibbonSlider, SliderComponentContentModel> slider = addAlphaSlider(alphaListener,
				alpha);
		layersMenu.addRibbonComponent(slider);

		final LabelComponentContentModel timeLabelModel = LabelComponentContentModel.builder().setText("Transparency")
				.build();
		final ComponentSupplier<JRibbonLabel, LabelComponentContentModel, ComponentPresentationModel> jTimeLabel = (
				final Projection<JRibbonLabel, LabelComponentContentModel, ComponentPresentationModel> projection2) -> JRibbonLabel::new;
		final RibbonLabelProjection timeLabelProjection = new RibbonLabelProjection(timeLabelModel,
				ComponentPresentationModel.withDefaults(), jTimeLabel);
		// final JLabel timeLabel = timeLabelProjection.buildComponent();
		// timeLabel.setPreferredSize(new Dimension(40,18));
		layersMenu.addRibbonComponent(timeLabelProjection);
		final RibbonTask viewTask = new RibbonTask("View", mouseMode, mapCommands, layersMenu);
		ribbon.addTask(viewTask);
	}

	private static JRibbonBand createMapCommands(final GeoToolMapRenderer geoMapRenderer, final Layers layers,
			final PlainProjection projection) {
		final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();
		final JRibbonBand commandBand = new JRibbonBand("Map commands", null);
		commandBand.startGroup();
		MenuUtils.addCommand("Zoom Out", "icons/24/zoomout.png", new ZoomOut(mapPane), commandBand,
				PresentationPriority.TOP, "Zoom out to reduce size of plot");
		final FitToWindow doFit = new FitToWindow(layers, mapPane, projection);
		MenuUtils.addCommand("Fit to Window", "icons/24/fit_to_win.png", doFit, commandBand, null,
				"Fit the plot to available space");
		commandBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(commandBand));
		return commandBand;
	}

	private static JRibbonBand createMouseModes(final GeoToolMapRenderer geoMapRenderer, final JLabel statusBar,
			final Layers layers, final GeoToolMapProjection projection, final MathTransform transform) {
		final JRibbonBand viewBand = new JRibbonBand("Mouse mode", null);
		final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();

		// group for the mosue mode radio buttons
		final CommandToggleGroupModel mouseModeGroup = new CommandToggleGroupModel();

		viewBand.startGroup();
		MenuUtils.addCommandToggleButton("Pan", "icons/24/hand.png", new PanCommandAction(mapPane), viewBand,
				PresentationPriority.TOP, true, mouseModeGroup, false);
		zoomInAction = new AdvancedZoomInAction(mapPane);
		zoominButton = MenuUtils.addCommandToggleButton("Zoom In", "icons/24/zoomin.png", zoomInAction, viewBand,
				PresentationPriority.TOP, true, mouseModeGroup, true);
		final RangeBearingAction rangeAction = new RangeBearingAction(mapPane, false, statusBar, transform);

		final JPopupMenu menu = new JPopupMenu();
		// ButtonGroup for radio buttons
		final ButtonGroup unitsGroup = new ButtonGroup();

		final ActionListener changeUnits = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final String unit = e.getActionCommand();
				RangeBearingTool.setBearingUnit(WorldDistance.getUnitIndexFor(unit));
				geoMapRenderer.getMap().repaint();
			}
		};

		for (int i = 0; i < WorldDistance.UnitLabels.length; i++) {
			final JRadioButtonMenuItem unitRadioButton = new JRadioButtonMenuItem(WorldDistance.UnitLabels[i]);
			unitRadioButton.setSelected(RangeBearingTool.getBearingUnit() == i);
			unitRadioButton.addActionListener(changeUnits);
			menu.add(unitRadioButton);
			unitsGroup.add(unitRadioButton);
		}

		ImageWrapperResizableIcon imageIcon = null;
		final Image zoominImage = MenuUtils.createImage("icons/24/rng_brg.png");
		imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, MenuUtils.ICON_SIZE_16);

		// final Command.Builder builder = Command.builder()
		final CustomBuilder builder = new CustomBuilder();
		builder.setPopMenu(menu).setText("Rng/Brg").setIconFactory(ResizableIconFactory.factory(imageIcon))
				.setAction(rangeAction);
		// .setTitleClickAction();

		builder.setToggle();
		builder.setToggleSelected(false);
		builder.inToggleGroup(mouseModeGroup);
		final Command command = builder.build();
		final CommandButtonProjection<Command> projectionModel = command
				.project(CommandButtonPresentationModel.builder().setActionKeyTip("NA")
						// .setPopupCallback(popupCallback)
						.setPopupMenuPresentationModel(
								CommandPopupMenuPresentationModel.builder().setMaxVisibleMenuCommands(4).build())
						.build());
		viewBand.addRibbonCommand(projectionModel, PresentationPriority.TOP);

		final DragElementAction dragWholeFeatureInAction = new DragElementAction(mapPane,
				new DragWholeFeatureElementTool(layers, projection, mapPane));
		MenuUtils.addCommandToggleButton("Drag Whole Feature", "icons/24/select_feature.png", dragWholeFeatureInAction,
				viewBand, PresentationPriority.TOP, true, mouseModeGroup, false);
		final DragElementAction dragElementInAction = new DragElementAction(mapPane,
				new DragElementTool(layers, projection, mapPane));
		MenuUtils.addCommandToggleButton("Drag Element", "icons/24/select_component.png", dragElementInAction, viewBand,
				PresentationPriority.TOP, true, mouseModeGroup, false);

		// tell the zoom in action that it's live
		zoomInAction.actionPerformed(null);

		viewBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(viewBand));
		return viewBand;
	}

	public static void resetToggleMenuStates() {
		zoominButton.setToggleSelected(true);
		zoomInAction.actionPerformed(null);
		
	}
}
