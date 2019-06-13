package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import MWC.TacticalData.NarrativeEntry;

public class NarrativePanelItemRenderer extends JPanel implements
    ListCellRenderer<NarrativeEntry>
{

  /**
   * 
   */
  private static final long serialVersionUID = -2227870470228775898L;

  @Override
  public Component getListCellRendererComponent(JList<? extends NarrativeEntry> list,
      NarrativeEntry value, int index, boolean isSelected, boolean cellHasFocus)
  {
    final JPanel mainPanel = new JPanel();
    mainPanel.add(new JCheckBox(value.getName(), isSelected));
    return mainPanel;
  }
}
