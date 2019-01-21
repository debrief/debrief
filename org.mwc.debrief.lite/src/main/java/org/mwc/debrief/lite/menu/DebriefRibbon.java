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
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;

import MWC.GUI.Layers;
import MWC.GUI.Properties.PropertiesPanel;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class DebriefRibbon
{
  private PropertiesPanel _theProperties;
  private final Layers _theLayers;
  private final DebriefLiteToolParent _toolParent;
  private final JRibbonFrame theFrame;
  private JRibbon theRibbon;
  private final GeoToolMapRenderer _geoMapRenderer;

  public DebriefRibbon(final JRibbonFrame frame, final Layers layers,
      final DebriefLiteToolParent parent,
      final GeoToolMapRenderer geoMapRenderer)
  {
    _theLayers = layers;
    _toolParent = parent;
    theFrame = frame;
    _geoMapRenderer = geoMapRenderer;
  }

  public void addMenus()
  {
    theRibbon = theFrame.getRibbon();

    // add menus here
    DebriefRibbonFile.addFileTab(theRibbon, _geoMapRenderer);
    DebriefRibbonView.addViewTab(theRibbon, _geoMapRenderer);
    DebriefRibbonInsert.addInsertTab(theRibbon, _geoMapRenderer, _theLayers,
        _theProperties, _toolParent);
    DebriefRibbonTimeController.addTimeControllerTab(theRibbon,
        _geoMapRenderer);
  }

  public void setProperties(final PropertiesPanel properties)
  {
    _theProperties = properties;
  }

}