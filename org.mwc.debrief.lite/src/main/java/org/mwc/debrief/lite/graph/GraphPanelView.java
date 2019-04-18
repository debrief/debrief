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
package org.mwc.debrief.lite.graph;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.mwc.debrief.lite.gui.LiteStepControl;
import org.mwc.debrief.lite.gui.custom.SimpleEditablePropertyPanel;

public class GraphPanelView extends JPanel
{
  /**
   *
   */
  private static final long serialVersionUID = 5203809173295266164L;

  private final GraphPanelToolbar toolbar;

  /**
   * the chart we need to update
   */
  final MWC.GUI.PlainChart _theChart;

  public GraphPanelView(final LiteStepControl stepControl)
  {
    super();
    setLayout(new BorderLayout());
    final SimpleEditablePropertyPanel xyPanel =
        new SimpleEditablePropertyPanel();
    toolbar = new GraphPanelToolbar(stepControl, xyPanel);
    _theChart = stepControl.getXYChart();

    add(toolbar, BorderLayout.NORTH);
    add(xyPanel, BorderLayout.CENTER);
  }
}
