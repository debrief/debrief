package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.JLabel;
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
    final Box header = Box.createHorizontalBox();
    final JLabel time = new JLabel(value.getDTGString());
    final JLabel trackName = new JLabel(value.getTrackName());
    final JLabel typeName = new JLabel(value.getType());
    header.add(time);
    header.add(trackName);
    header.add(typeName);
    
    final JLabel name = new JLabel(value.getName());
    mainPanel.add(header);
    mainPanel.add(name);
    return mainPanel;
  }
}
