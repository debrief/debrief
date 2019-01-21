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

import org.mwc.debrief.lite.gui.DebriefLiteToolParent;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;

import MWC.GUI.Layers;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class DebriefRibbon
{
  public DebriefRibbon(final JRibbon ribbon, final Layers layers,
      final DebriefLiteToolParent parent,
      final GeoToolMapRenderer geoMapRenderer)
  {
    // add menus here
    DebriefRibbonFile.addFileTab(ribbon, geoMapRenderer);
    DebriefRibbonView.addViewTab(ribbon, geoMapRenderer);
    DebriefRibbonInsert.addInsertTab(ribbon, geoMapRenderer, layers,
        null, parent);
    DebriefRibbonTimeController.addTimeControllerTab(ribbon, geoMapRenderer);
  }
}