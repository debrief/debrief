package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.Component;
import java.awt.Font;

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
    final Box mainPanel = Box.createVerticalBox();
    final Box header = Box.createHorizontalBox();
    final JPanel body = new JPanel();
    final JLabel time = new JLabel(value.getDTGString());
    final Font originalFont = time.getFont();
    final Font smallFont = new Font(originalFont.getName(), originalFont.getStyle(), 9);
    final Font bigFont = new Font(originalFont.getName(), originalFont.getStyle(), 15);
    time.setFont(smallFont);
    final JLabel trackName = new JLabel(value.getTrackName());
    trackName.setFont(smallFont);
    final JLabel typeName = new JLabel(value.getType());
    typeName.setFont(smallFont);
    header.add(time);
    header.add(Box.createHorizontalStrut(20));
    header.add(trackName);
    header.add(Box.createHorizontalStrut(20));
    header.add(typeName);
    
    final JLabel name = new JLabel(value.getName());
    body.add(name);
    //body.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
    //mainPanel.setBackground(Color.black);
    
    name.setFont(bigFont);
    mainPanel.add(Box.createVerticalStrut(20));
    mainPanel.add(header);
    mainPanel.add(Box.createVerticalStrut(5));
    mainPanel.add(body);
    return mainPanel;
  }
}
