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
package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class TrackPanelItemRenderer extends JPanel implements
    ListCellRenderer<TrackNameColor>
{

  /**
   *
   */
  private static final long serialVersionUID = 1963635319483966831L;

  @Override
  public Component getListCellRendererComponent(
      final JList<? extends TrackNameColor> list, final TrackNameColor value,
      final int index, final boolean isSelected, final boolean cellHasFocus)
  {
    final Box mainBox = Box.createHorizontalBox();
    final JPanel coloredPane = new JPanel();
    coloredPane.setBackground(value.getColor());
    coloredPane.setPreferredSize(new Dimension(12, 12));

    mainBox.add(new JCheckBox(value.getTrackName()));
    mainBox.add(coloredPane);
    return mainBox;
  }

}
