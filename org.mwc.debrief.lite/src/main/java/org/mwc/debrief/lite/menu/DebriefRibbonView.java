package org.mwc.debrief.lite.menu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import org.geotools.swing.JMapPane;
import org.geotools.swing.action.ZoomInAction;
import org.mwc.debrief.lite.gui.FitToWindow;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.gui.ZoomOut;
import org.mwc.debrief.lite.map.AdvancedZoomInAction;
import org.mwc.debrief.lite.map.DragElementAction;
import org.mwc.debrief.lite.map.DragElementTool;
import org.mwc.debrief.lite.map.DragWholeFeatureElementTool;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.RangeBearingAction;
import org.mwc.debrief.lite.view.actions.PanCommandAction;
import org.opengis.referencing.operation.MathTransform;
import org.pushingpixels.flamingo.api.common.CommandButtonPresentationState;
import org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon;
import org.pushingpixels.flamingo.api.common.model.Command;
import org.pushingpixels.flamingo.api.common.model.CommandGroup;
import org.pushingpixels.flamingo.api.common.model.CommandPanelContentModel;
import org.pushingpixels.flamingo.api.common.model.CommandPanelPresentationModel;
import org.pushingpixels.flamingo.api.common.model.CommandToggleGroupModel;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand.PresentationPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonComponent;

import MWC.GUI.Layers;

public class DebriefRibbonView
{
  private static JRibbonComponent addAlphaSlider(
      final ChangeListener alphaListener, final float alpha)
  {
    final JSlider slider = new JSlider(0, 100);
    slider.setMajorTickSpacing(20);
    slider.setPaintTicks(true);
    slider.setBackground(Color.DARK_GRAY);
    //slider.addChangeListener(alphaListener);
    slider.setValue((int)(alpha * 100f));
    
   
    CommandPanelPresentationModel panelPresentationModel;
    List<CommandGroup> commandGroups = new ArrayList<>();
    Command command = Command.builder()
        .setIconFactory(EmptyResizableIcon.factory())
        .setToggle()
        
        .build();
    command.addChangeListener(alphaListener);
    List<Command> commands = new ArrayList<>();
    commands.add(command);
    commandGroups.add(new CommandGroup("Alpha", commands));
    
    CommandPanelContentModel panelContentModel = new CommandPanelContentModel(commandGroups);
    panelContentModel.setSingleSelectionMode(true);
    CommandPanelPresentationModel.builder()
    .setToShowGroupLabels(false)
    .setCommandPresentationState(CommandButtonPresentationState.FIT_TO_ICON)
    .setCommandIconDimension(48)
    .build();
//    final JRibbonComponent component = new JRibbonComponent(null,
//        "Transparency:", slider);
//    return component;
    return null;
  }

  protected static void addViewTab(final JRibbon ribbon,
      final GeoToolMapRenderer geoMapRenderer, final Layers layers,
      final JLabel statusBar, final GeoToolMapProjection projection,
      final MathTransform transform, final ChangeListener alphaListener, final float alpha)
  {
//    final JRibbonBand mouseMode = createMouseModes(geoMapRenderer, statusBar,
//        layers, projection, transform);
//    final JRibbonBand mapCommands = createMapCommands(geoMapRenderer, layers);
//    
    // and the slider
//    final JRibbonBand layersMenu = new JRibbonBand("Background", null);
//    final JRibbonComponent slider = addAlphaSlider(alphaListener, alpha);
//    slider.setPresentationPriority(PresentationPriority.TOP);
//    layersMenu.addRibbonComponent(slider);

//    final RibbonTask viewTask = new RibbonTask("View", mouseMode, mapCommands,
//        layersMenu);
//    final RibbonTask viewTask = new RibbonTask("View", mouseMode, mapCommands);
//    ribbon.addTask(viewTask);
  }

  private static JRibbonBand createMapCommands(
      final GeoToolMapRenderer geoMapRenderer, final Layers layers)
  {
    final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();
    final JRibbonBand commandBand = new JRibbonBand("Map commands", null);
    commandBand.startGroup();
    MenuUtils.addCommand("Zoom Out", "icons/24/zoomout.png", new ZoomOut(
        mapPane), commandBand, PresentationPriority.TOP);
    final FitToWindow doFit = new FitToWindow(layers, mapPane);
    MenuUtils.addCommand("Fit to Window", "icons/24/fit_to_win.png", doFit,
        commandBand, null);
    commandBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        commandBand));
    return commandBand;
  }

  private static JRibbonBand createMouseModes(
      final GeoToolMapRenderer geoMapRenderer, final JLabel statusBar,
      final Layers layers, final GeoToolMapProjection projection,
      final MathTransform transform)
  {
    final JRibbonBand viewBand = new JRibbonBand("Mouse mode", null);
    final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();

    // group for the mosue mode radio buttons
    final CommandToggleGroupModel mouseModeGroup =
        new CommandToggleGroupModel();

    viewBand.startGroup();
    MenuUtils.addCommandToggleButton("Pan", "icons/24/hand.png", new PanCommandAction(
        mapPane), viewBand, PresentationPriority.TOP, true, mouseModeGroup,
        false);
    final AdvancedZoomInAction zoomInAction = new AdvancedZoomInAction(mapPane);
    MenuUtils.addCommandToggleButton("Zoom In", "icons/24/zoomin.png",
        zoomInAction, viewBand, PresentationPriority.TOP, true, mouseModeGroup,
        true);
    final RangeBearingAction rangeAction = new RangeBearingAction(mapPane,
        false, statusBar, transform);
    MenuUtils.addCommandToggleButton("Rng/Brg", "icons/24/rng_brg.png",
        rangeAction, viewBand, PresentationPriority.TOP, true, mouseModeGroup,
        false);
    final DragElementAction dragWholeFeatureInAction = new DragElementAction(
        mapPane, new DragWholeFeatureElementTool(layers, projection, mapPane));
    MenuUtils.addCommandToggleButton("Drag Whole Feature",
        "icons/24/select_feature.png", dragWholeFeatureInAction, viewBand,
        PresentationPriority.TOP, true, mouseModeGroup, false);
    final DragElementAction dragElementInAction = new DragElementAction(mapPane,
        new DragElementTool(layers, projection, mapPane));
    MenuUtils.addCommandToggleButton("Drag Element",
        "icons/24/select_component.png", dragElementInAction, viewBand,
        PresentationPriority.TOP, true, mouseModeGroup, false);

    // tell the zoom in action that it's live
    zoomInAction.actionPerformed(null);

    viewBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        viewBand));
    return viewBand;
  }
}
