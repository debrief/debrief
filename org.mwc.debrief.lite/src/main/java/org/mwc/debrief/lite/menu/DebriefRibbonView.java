package org.mwc.debrief.lite.menu;

import java.util.ArrayList;
import java.util.List;

import org.geotools.swing.JMapPane;
import org.geotools.swing.action.InfoAction;
import org.geotools.swing.action.NoToolAction;
import org.geotools.swing.action.PanAction;
import org.geotools.swing.action.ResetAction;
import org.geotools.swing.action.ZoomInAction;
import org.geotools.swing.action.ZoomOutAction;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

public class DebriefRibbonView
{
  protected static void addViewTab(final JRibbon ribbon,
      final GeoToolMapRenderer _geoMapRenderer)
  {
    final JRibbonBand viewBand = new JRibbonBand("View", null);
    final JMapPane mapPane = (JMapPane) _geoMapRenderer.getMap();

    MenuUtils.addCommand("Selector", null, new NoToolAction(mapPane), viewBand,
        null);
    MenuUtils.addCommand("Zoom In", "images/16/zoomin.png", new ZoomInAction(
        mapPane), viewBand, RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Zoom Out", "images/16/zoomout.png", new ZoomOutAction(
        mapPane), viewBand, RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Pan", "images/16/hand.png", new PanAction(mapPane), viewBand, null);
    MenuUtils.addCommand("Info", null, new InfoAction(mapPane), viewBand, null);
    MenuUtils.addCommand("Reset", null, new ResetAction(mapPane), viewBand,
        null);
    final List<RibbonBandResizePolicy> policies = new ArrayList<>();
    policies.add(new CoreRibbonResizePolicies.Mirror(viewBand));
    policies.add(new CoreRibbonResizePolicies.Mid2Low(viewBand));
    policies.add(new IconRibbonBandResizePolicy(viewBand));
    viewBand.setResizePolicies(policies);
    final RibbonTask fileTask = new RibbonTask("View", viewBand);
    ribbon.addTask(fileTask);
  }
}
