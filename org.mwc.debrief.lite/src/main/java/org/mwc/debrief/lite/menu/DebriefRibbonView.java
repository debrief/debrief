package org.mwc.debrief.lite.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.geotools.swing.JMapPane;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;
import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.gui.FitToWindow;
import org.mwc.debrief.lite.gui.ZoomOut;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

import MWC.GenericData.WorldArea;
import MWC.GUI.Layers;

public class DebriefRibbonView
{
  protected static void addViewTab(final JRibbon ribbon,
      final GeoToolMapRenderer _geoMapRenderer, final GeoToolMapProjection projection, final Layers layers)
  {
    final JRibbonBand viewBand = new JRibbonBand("View", null);
    final JMapPane mapPane = (JMapPane) _geoMapRenderer.getMap();
    final AbstractAction doFit = new FitToWindow(layers, mapPane);
    viewBand.startGroup();
    MenuUtils.addCommand("Pan", "images/16/hand.png", new PanAction(mapPane),
        viewBand, RibbonElementPriority.TOP);
    final ZoomInAction zoomInAction = new ZoomInAction(mapPane);
    MenuUtils.addCommand("Zoom In", "images/16/zoomin.png", zoomInAction,
        viewBand, RibbonElementPriority.TOP);

    MenuUtils.addCommand("Zoom Out", "images/16/zoomout.png", new ZoomOut(
        mapPane), viewBand, RibbonElementPriority.TOP);
    MenuUtils.addCommand("Fit to Window", "images/16/fit_to_win.png",
        doFit, viewBand, null);
    viewBand.startGroup();
    final List<RibbonBandResizePolicy> policies = new ArrayList<>();
    policies.add(new CoreRibbonResizePolicies.Mirror(viewBand));
    policies.add(new CoreRibbonResizePolicies.Mid2Low(viewBand));
    policies.add(new IconRibbonBandResizePolicy(viewBand));
    viewBand.setResizePolicies(policies);
    final RibbonTask fileTask = new RibbonTask("View", viewBand);
    ribbon.addTask(fileTask);

    // lastly, run the zoom-in event, to put the map into that mode
    zoomInAction.actionPerformed(new ActionEvent(viewBand,
        ActionEvent.ACTION_PERFORMED, "Click"));
  }
}
