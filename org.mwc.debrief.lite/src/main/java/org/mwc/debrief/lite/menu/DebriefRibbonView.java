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
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.ChangeListener;

import org.geotools.swing.JMapPane;
import org.mwc.debrief.lite.DebriefLiteApp;
import org.mwc.debrief.lite.custom.JRibbonLabel;
import org.mwc.debrief.lite.custom.JRibbonSlider;
import org.mwc.debrief.lite.custom.LabelComponentContentModel;
import org.mwc.debrief.lite.custom.RibbonLabelProjection;
import org.mwc.debrief.lite.custom.RibbonSliderProjection;
import org.mwc.debrief.lite.custom.SliderComponentContentModel;
import org.mwc.debrief.lite.gui.FitToWindow;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.gui.ZoomOut;
import org.mwc.debrief.lite.map.AdvancedZoomInAction;
import org.mwc.debrief.lite.map.DragElementAction;
import org.mwc.debrief.lite.map.DragElementTool;
import org.mwc.debrief.lite.map.DragWholeFeatureElementTool;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.RangeBearingTool;
import org.mwc.debrief.lite.util.ResizableIconFactory;
import org.mwc.debrief.lite.view.actions.PanCommandAction;
import org.opengis.referencing.operation.MathTransform;
import org.pushingpixels.flamingo.api.common.CommandAction;
import org.pushingpixels.flamingo.api.common.CommandActionEvent;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;
import org.pushingpixels.flamingo.api.common.model.Command;
import org.pushingpixels.flamingo.api.common.model.CommandButtonPresentationModel;
import org.pushingpixels.flamingo.api.common.model.CommandGroup;
import org.pushingpixels.flamingo.api.common.model.CommandMenuContentModel;
import org.pushingpixels.flamingo.api.common.model.CommandToggleGroupModel;
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
	private static Command zoominButton;
	private static AdvancedZoomInAction zoomInAction;
	final static List<Command> rangeBearingUnitPopupCommands = new ArrayList<>();
	private static JRibbonSlider transparencySlider;

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
		transparencySlider = projection.buildComponent();
		transparencySlider.setToolTipText("Modify transparency");
		transparencySlider.setBackground(Color.DARK_GRAY);
		transparencySlider.setName("transparencyslider");
		transparencySlider.setValue((int) (alpha * 100f));
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
		
		final CommandAction changeUnits = new CommandAction() {
			
			@Override
			public void commandActivated(CommandActionEvent e) {
				final String unit = e.getCommand().getText();
				RangeBearingTool.setBearingUnit(WorldDistance.getUnitIndexFor(unit));
				geoMapRenderer.getMap().repaint();
				setSelectedBearingUnit(WorldDistance.getUnitIndexFor(unit));
				
			}
		};
		ImageWrapperResizableIcon imageIcon = null;
		final Image zoominImage = MenuUtils.createImage("icons/24/rng_brg.png");
		imageIcon = ImageWrapperResizableIcon.getIcon(zoominImage, MenuUtils.ICON_SIZE_16);
		for (int i = 0; i < WorldDistance.UnitLabels.length; i++) {
			rangeBearingUnitPopupCommands.add(Command.builder().setText(WorldDistance.UnitLabels[i]).setToggle()
					.setAction(changeUnits).build());
			rangeBearingUnitPopupCommands.get(i).setToggleSelected(RangeBearingTool.getBearingUnit() == i);
		}

		

		CommandMenuContentModel popupMenuContentModel = new CommandMenuContentModel(
				new CommandGroup(rangeBearingUnitPopupCommands));

		viewBand.addRibbonCommand(
				Command.builder().setText("Rng/Brg").setIconFactory(ResizableIconFactory.factory(imageIcon))
						.setAction((CommandActionEvent e) -> System.out.println("Cut!"))
						.setActionRichTooltip(RichTooltip.builder().setTitle("Select Range Bearing").build())
						.setSecondaryContentModel(popupMenuContentModel).build().project(CommandButtonPresentationModel
								.builder().setPopupKeyTip("X").setTextClickAction().build()),
				PresentationPriority.TOP);

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
		if (zoominButton != null) {
			zoominButton.setToggleSelected(true);
			zoomInAction.actionPerformed(null);
		}
		if (!rangeBearingUnitPopupCommands.isEmpty()) {
			setSelectedBearingUnit(WorldDistance.YARDS);
		}
		if (transparencySlider != null) {
			transparencySlider.setValue((int) (DebriefLiteApp.getInstance().initialAlpha * 100f));
		}

	}

	private static void setSelectedBearingUnit(int i) {
		final String unit = WorldDistance.getLabelFor(i);
		rangeBearingUnitPopupCommands.forEach((c)->{
			boolean var = (c.getText().equals(unit))?true:false;
			c.setToggleSelected(var);
		});
		
	}
}
