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

import javax.swing.JLabel;

import org.mwc.debrief.lite.gui.GeoToolMapProjection;
import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.map.GeoToolMapRenderer;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;

import Debrief.GUI.Frames.Session;
import MWC.GUI.Layers;
import MWC.GUI.ToolParent;
import MWC.TacticalData.temporal.PlotOperations;
import MWC.TacticalData.temporal.TimeManager;

/**
 * @author Ayesha <ayesha.ma@gmail.com>
 *
 */
public class DebriefRibbon
{
  public DebriefRibbon(final JRibbon ribbon, final Layers layers,
      final ToolParent parent, final GeoToolMapRenderer geoMapRenderer,
      final LiteStepControl stepControl, final TimeManager timeManager,
      final PlotOperations operations, final Session session,
      final Runnable resetAction, final Runnable normalPainter,
      final Runnable snailPainter, final JLabel statusBar,
      final Runnable exitAction, final GeoToolMapProjection projection)
  {
    // add menus here
    DebriefRibbonLite.addLiteTab(ribbon, session, resetAction, exitAction);
    DebriefRibbonFile.addFileTab(ribbon, geoMapRenderer, session, resetAction);
    DebriefRibbonView.addViewTab(ribbon, geoMapRenderer, layers, statusBar,
        projection);
    DebriefRibbonInsert.addInsertTab(ribbon, geoMapRenderer, layers, null,
        parent);
    DebriefRibbonTimeController.addTimeControllerTab(ribbon, geoMapRenderer,
        stepControl, timeManager, operations, layers, session.getUndoBuffer(),
        normalPainter, snailPainter);
  }
}