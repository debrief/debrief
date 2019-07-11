package org.mwc.debrief.lite.menu;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import org.geotools.swing.JMapPane;
import org.geotools.swing.action.PanAction;
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
import org.opengis.referencing.operation.MathTransform;
import org.pushingpixels.flamingo.api.common.FlamingoCommand.FlamingoCommandToggleGroup;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonComponent;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import MWC.GUI.Layers;

public class DebriefRibbonView
{
  public static interface NewTransparencyAction
  {
    void updated(float alpha);
  }

  protected static void addViewTab(final JRibbon ribbon,
      final GeoToolMapRenderer geoMapRenderer, final Layers layers,
      final JLabel statusBar, final GeoToolMapProjection projection,
      final MathTransform transform, final ChangeListener alphaListener)
  {
    final JRibbonBand mouseMode = createMouseModes(geoMapRenderer, statusBar,
        layers, projection, transform);
    final JRibbonBand mapCommands = createMapCommands(geoMapRenderer, layers);
    final JRibbonBand layersMenu = new JRibbonBand("Background", null);
    addDropDown(alphaListener,layersMenu,RibbonElementPriority.TOP,null);
    final RibbonTask fileTask = new RibbonTask("View", mouseMode, mapCommands, layersMenu);
    ribbon.addTask(fileTask);
  }

  private static JRibbonBand createMapCommands(
      final GeoToolMapRenderer geoMapRenderer, final Layers layers)
  {
    final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();
    final JRibbonBand commandBand = new JRibbonBand("Map commands", null);
    commandBand.startGroup();
    MenuUtils.addCommand("Zoom Out", "icons/24/zoomout.png", new ZoomOut(
        mapPane), commandBand, RibbonElementPriority.TOP);
    final AbstractAction doFit = new FitToWindow(layers, mapPane);
    MenuUtils.addCommand("Fit to Window", "icons/24/fit_to_win.png", doFit,
        commandBand, null);
    commandBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        commandBand));
    return commandBand;
  }
  
  
  private static JRibbonComponent addDropDown(final ChangeListener alphaListener,
      final JRibbonBand mapBand, final RibbonElementPriority priority,final Layers theLayers)
  {
    final DefaultComboBoxModel<String>  selectLayerModel = new DefaultComboBoxModel<String>();
    for(int i=0;i<=10;i+=2)
    {
      selectLayerModel.addElement(i * 10 + "%");
    }
    JSlider slider = new JSlider(0, 100);
    slider.setPreferredSize(new Dimension(200,40));
    slider.setMajorTickSpacing(20);
    slider.setPaintTicks(true);
    slider.setForeground(Color.DARK_GRAY);
    slider.addChangeListener(alphaListener);
    JRibbonComponent component = new JRibbonComponent(null,"Transparency",slider);
    component.setDisplayPriority(priority);
    mapBand.addRibbonComponent(component);
    return component;
  }

  private static JRibbonBand createMouseModes(
      final GeoToolMapRenderer geoMapRenderer, final JLabel statusBar,
      final Layers layers, final GeoToolMapProjection projection,
      final MathTransform transform)
  {
    final JRibbonBand viewBand = new JRibbonBand("Mouse mode", null);
    final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();

    // group for the mosue mode radio buttons
    final FlamingoCommandToggleGroup mouseModeGroup =
        new FlamingoCommandToggleGroup();

    viewBand.startGroup();
    MenuUtils.addCommandToggleButton("Pan", "icons/24/hand.png", new PanAction(
        mapPane), viewBand, RibbonElementPriority.TOP, true, mouseModeGroup,
        false);
    final ZoomInAction zoomInAction = new AdvancedZoomInAction(mapPane);
    MenuUtils.addCommandToggleButton("Zoom In", "icons/24/zoomin.png",
        zoomInAction, viewBand, RibbonElementPriority.TOP, true, mouseModeGroup,
        true);
    final RangeBearingAction rangeAction = new RangeBearingAction(mapPane,
        false, statusBar, transform);
    MenuUtils.addCommandToggleButton("Rng/Brg", "icons/24/rng_brg.png",
        rangeAction, viewBand, RibbonElementPriority.TOP, true, mouseModeGroup,
        false);
    final DragElementAction dragWholeFeatureInAction = new DragElementAction(
        mapPane, new DragWholeFeatureElementTool(layers, projection, mapPane));
    MenuUtils.addCommandToggleButton("Drag Whole Feature",
        "icons/24/select_feature.png", dragWholeFeatureInAction, viewBand,
        RibbonElementPriority.TOP, true, mouseModeGroup, false);
    final DragElementAction dragElementInAction = new DragElementAction(mapPane,
        new DragElementTool(layers, projection, mapPane));
    MenuUtils.addCommandToggleButton("Drag Element",
        "icons/24/select_component.png", dragElementInAction, viewBand,
        RibbonElementPriority.TOP, true, mouseModeGroup, false);

    // tell the zoom in action that it's live
    zoomInAction.actionPerformed(null);

    viewBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        viewBand));
    return viewBand;
  }
}
