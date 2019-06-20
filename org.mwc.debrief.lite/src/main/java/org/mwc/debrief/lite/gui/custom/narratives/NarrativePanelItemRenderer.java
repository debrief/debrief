package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import MWC.TacticalData.NarrativeEntry;

public class NarrativePanelItemRenderer extends JPanel implements
    ListCellRenderer<NarrativeEntry>
{

  /**
   *
   */
  private static final long serialVersionUID = -2227870470228775898L;

  @Override
  public Component getListCellRendererComponent(
      final JList<? extends NarrativeEntry> list, final NarrativeEntry value,
      final int index, final boolean isSelected, final boolean cellHasFocus)
  {
    final JPanel mainPanel = new JPanel();
    final JPanel content = new JPanel();
    content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
    content.setPreferredSize(new Dimension(300, 50));
    final JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
    header.setPreferredSize(new Dimension(300, 13));
    final JPanel body = new JPanel(new FlowLayout(FlowLayout.LEFT));
    final JLabel time = new JLabel(value.getDTGString());
    final Font originalFont = time.getFont();
    final Font smallFont = new Font(originalFont.getName(), originalFont
        .getStyle(), 8);
    final Font bigFont = new Font(originalFont.getName(), originalFont
        .getStyle(), 12);
    time.setFont(smallFont);
    final JLabel trackName = new JLabel(value.getTrackName());
    trackName.setFont(smallFont);
    final JLabel typeName = new JLabel(value.getType());
    typeName.setFont(smallFont);
    header.add(time);
    header.add(Box.createHorizontalStrut(3));
    header.add(trackName);
    header.add(Box.createHorizontalStrut(3));
    header.add(typeName);

    final JTextArea name = new JTextArea(2, 20);
    name.setWrapStyleWord(true);
    name.setLineWrap(true);
    name.setOpaque(false);
    name.setEditable(false);
    name.setFocusable(false);
    name.setText(value.getEntry());
    name.setBackground(UIManager.getColor("Label.background"));
    name.setFont(UIManager.getFont("Label.font"));
    name.setBorder(UIManager.getBorder("Label.border"));
    body.add(name);
    name.setFont(bigFont);

    content.add(header);
    content.add(body);

    JLabel highlightIcon;
    if (isSelected)
    {
      highlightIcon = new JLabel(Utils.getIcon("icons/16/highlight.png"));
    }
    else
    {
      highlightIcon = new JLabel(Utils.getIcon("icons/16/blank.png"));
    }

    mainPanel.add(highlightIcon);
    mainPanel.add(content);

    if (cellHasFocus)
    {
      mainPanel.setBackground(new Color(229, 229, 229));
      body.setBackground(new Color(229, 229, 229));
      header.setBackground(new Color(229, 229, 229));
      final JLabel editNarrative = new JLabel(Utils.getIcon(
          "icons/16/edit_narrative.png"));
      final JLabel removeNarrative = new JLabel(Utils.getIcon(
          "icons/16/remove_narrative.png"));

      body.add(editNarrative);
      body.add(removeNarrative);

    }

    return mainPanel;
  }
}
