package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class NarrativeEntryItemRenderer extends JPanel implements
    TableCellRenderer
{

  /**
   *
   */
  private static final long serialVersionUID = -2227870470228775898L;

  @Override
  public Component getTableCellRendererComponent(final JTable table,
      final Object value, final boolean isSelected, final boolean hasFocus,
      final int row, final int column)
  {
    final Color selectedColor = new Color(229, 229, 229);

    if (value instanceof NarrativeEntryItem)
    {
      final NarrativeEntryItem valueItem = (NarrativeEntryItem) value;
      final String text = valueItem.getEntry().getEntry();
      final JPanel mainPanel = new JPanel();
      mainPanel.setLayout(new BorderLayout());

      final JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
      header.setPreferredSize(new Dimension(300, 18));

      final JLabel time = new JLabel(valueItem.getEntry().getDTGString());
      final Font originalFont = time.getFont();
      final Font smallFont = new Font(originalFont.getName(), originalFont
          .getStyle(), 8);
      final Font bigFont = new Font(originalFont.getName(), originalFont
          .getStyle(), 12);
      time.setFont(smallFont);
      final JLabel trackName = new JLabel(valueItem.getEntry().getTrackName());
      trackName.setFont(smallFont);
      final JLabel typeName = new JLabel(valueItem.getEntry().getType());
      typeName.setFont(smallFont);
      header.add(Box.createHorizontalStrut(12));
      header.add(time);
      header.add(Box.createHorizontalStrut(3));
      header.add(trackName);
      header.add(Box.createHorizontalStrut(3));
      header.add(typeName);

      final JTextArea name = new JTextArea();
      name.setWrapStyleWord(false);
      name.setLineWrap(valueItem.getModel().isWrapping());
      name.setOpaque(false);
      name.setEditable(false);
      name.setFocusable(false);
      name.setText(text);

      name.setBackground(UIManager.getColor("Label.background"));
      name.setFont(UIManager.getFont("Label.font"));
      name.setBorder(UIManager.getBorder("Label.border"));
      name.setFont(bigFont);
      final int width = table.getWidth();
      if (width > 0)
      {
        name.setSize(width, Short.MAX_VALUE);
      }

      final JPanel innerPanel = new JPanel();
      innerPanel.setLayout(new BorderLayout());
      innerPanel.add(header, BorderLayout.NORTH);
      innerPanel.add(name, BorderLayout.CENTER);
      mainPanel.add(innerPanel, BorderLayout.CENTER);

      JLabel highlightIcon;
      if (isSelected)
      {
        highlightIcon = new JLabel(Utils.getIcon("icons/16/highlight.png"));
      }
      else
      {
        highlightIcon = new JLabel(Utils.getIcon("icons/16/blank.png"));
      }

      mainPanel.add(highlightIcon, BorderLayout.WEST);

      if (hasFocus)
      {
        mainPanel.setBackground(selectedColor);
        name.setBackground(selectedColor);
        header.setBackground(selectedColor);
        innerPanel.setBackground(selectedColor);
        final JLabel editNarrative = new JLabel(Utils.getIcon(
            "icons/16/edit_narrative.png"));
        final JLabel removeNarrative = new JLabel(Utils.getIcon(
            "icons/16/remove_narrative.png"));

        final JPanel iconsPanel = new JPanel();
        iconsPanel.setBackground(selectedColor);
        iconsPanel.add(editNarrative);
        iconsPanel.add(removeNarrative);
        mainPanel.add(iconsPanel, BorderLayout.EAST);
      }

      return mainPanel;
    }
    else
    {
      return null;
    }
  }
}
