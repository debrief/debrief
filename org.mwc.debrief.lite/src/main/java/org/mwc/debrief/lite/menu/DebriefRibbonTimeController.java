package org.mwc.debrief.lite.menu;

import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

public class DebriefRibbonTimeController
{
  protected static void addTimeControllerTab(final JRibbon ribbon,
      final GeoToolMapRenderer _geoMapRenderer)
  {
    final JRibbonBand timeMenu = new JRibbonBand("Time Controller", null);
    MenuUtils.addCommand("Play", null, new MenuUtils.TODOAction(), timeMenu,
        RibbonElementPriority.MEDIUM);
    MenuUtils.addCommand("Record", "images/16/zoomin.png",
        new MenuUtils.TODOAction(), timeMenu, RibbonElementPriority.MEDIUM);
    timeMenu.setResizePolicies(MenuUtils.getStandardRestrictivePolicies(
        timeMenu));
    final RibbonTask timeTask = new RibbonTask("Time Controller", timeMenu);
    ribbon.addTask(timeTask);
  }
}
