package org.mwc.debrief.lite.menu;

import javax.swing.AbstractAction;
import javax.swing.JLabel;

import org.geotools.swing.JMapPane;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ZoomInAction;
import org.mwc.debrief.lite.gui.FitToWindow;
import org.mwc.debrief.lite.gui.ZoomOut;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.mwc.debrief.lite.map.RangeBearingAction;
import org.pushingpixels.flamingo.api.common.FlamingoCommand.FlamingoCommandToggleGroup;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

import MWC.GUI.Layers;

public class DebriefRibbonView
{
  
  private static JRibbonBand createMouseModes(
      final GeoToolMapRenderer geoMapRenderer, final Layers layers,
      JLabel statusBar)
  {
    final JRibbonBand viewBand = new JRibbonBand("Mouse mode", null);
    final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();

    // group for the mosue mode radio buttons
    final FlamingoCommandToggleGroup mouseModeGroup =
        new FlamingoCommandToggleGroup();

    viewBand.startGroup();
    MenuUtils.addCommandToggleButton("Pan", "images/16/hand.png", new PanAction(
        mapPane), viewBand, RibbonElementPriority.TOP, true, mouseModeGroup,
        false);
    final ZoomInAction zoomInAction = new ZoomInAction(mapPane);
    MenuUtils.addCommandToggleButton("Zoom In", "images/16/zoomin.png",
        zoomInAction, viewBand, RibbonElementPriority.TOP, true, mouseModeGroup,
        true);
    final RangeBearingAction rangeAction = new RangeBearingAction(mapPane,
        statusBar);
    MenuUtils.addCommandToggleButton("Rne/Brg", "images/16/rng_brg.png",
        rangeAction, viewBand, RibbonElementPriority.TOP, true, mouseModeGroup,
        false);
    viewBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        viewBand));
    return viewBand;
  }
  
  private static JRibbonBand createMapCommands(
      final GeoToolMapRenderer geoMapRenderer, final Layers layers)
  {
    final JMapPane mapPane = (JMapPane) geoMapRenderer.getMap();
    final JRibbonBand commandBand = new JRibbonBand("Map commands", null);
    commandBand.startGroup();
    MenuUtils.addCommand("Zoom Out", "images/16/zoomout.png", new ZoomOut(
        mapPane), commandBand, RibbonElementPriority.TOP);
    final AbstractAction doFit = new FitToWindow(layers, mapPane);
    MenuUtils.addCommand("Fit to Window", "images/16/fit_to_win.png", doFit,
        commandBand, null);
    commandBand.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        commandBand));
    return commandBand;
  }
  
  protected static void addViewTab(final JRibbon ribbon,
      final GeoToolMapRenderer geoMapRenderer, final Layers layers, JLabel statusBar)
  {
    final JRibbonBand mouseMode = createMouseModes(geoMapRenderer, layers, statusBar);
    final JRibbonBand mapCommands = createMapCommands(geoMapRenderer, layers);
    final RibbonTask fileTask = new RibbonTask("View", mouseMode, mapCommands);
    ribbon.addTask(fileTask);
  }
}
