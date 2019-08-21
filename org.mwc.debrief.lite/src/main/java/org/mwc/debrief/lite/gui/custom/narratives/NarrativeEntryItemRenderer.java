package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class NarrativeEntryItemRenderer extends JPanel implements
    TableCellRenderer
{

  private static final ImageIcon HIGHLIGHT_ICON = Utils.getIcon(
      "icons/16/highlight.png");
  private static final ImageIcon BLANK_ICON = Utils.getIcon(
      "icons/16/blank.png");
  private static final ImageIcon REMOVE_NARRATIVE_ICON = Utils.getIcon(
      "icons/16/remove_narrative.png");
  private static final ImageIcon EDIT_NARRATIVE_ICON = Utils.getIcon(
      "icons/16/edit_narrative.png");


  private final int HEIGHT_FIXED_SIZE = 42;
  /**
   *
   */
  private static final long serialVersionUID = -2227870470228775898L;

  private final AbstractNarrativeConfiguration _model;

  public NarrativeEntryItemRenderer(final AbstractNarrativeConfiguration model)
  {
    this._model = model;
  }

  private JPanel getHeader(final NarrativeEntryItem valueItem,
      final JLabel time, final Font originalFont)
  {
    final JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
    header.setPreferredSize(new Dimension(300, 18));

    final Font smallFont = new Font(originalFont.getName(), originalFont
        .getStyle(), 10);
    time.setFont(smallFont);
    final JLabel trackName = new JLabel(valueItem.getEntry().getTrackName());
    trackName.setFont(smallFont);
    final JLabel typeName = new JLabel(valueItem.getEntry().getType());
    typeName.setFont(smallFont);
    header.add(time);
    header.add(Box.createHorizontalStrut(3));
    header.add(trackName);
    header.add(Box.createHorizontalStrut(3));
    header.add(typeName);
    header.add(Box.createHorizontalBox());
    return header;
  }

  private JPanel getName(final String text, final boolean hasFocus,
      final Font originalFont, final JTable table)
  {
    final JPanel pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
    final JLabel name = new JLabel();
    final String html = "<html><body style='width: %1spx'>%1s";

    name.setOpaque(false);
    name.setFocusable(false);
    if (_model.isWrapping())
    {
      final int emptySpace;
      if (hasFocus)
      {
        emptySpace = 120;
      }
      else
      {
        emptySpace = 80;
      }
      name.setText(String.format(html, _model.getPanelWidth() - emptySpace, text));
    }
    else
    {
      name.setText(text);
    }

    final Font bigFont = new Font(originalFont.getName(), originalFont
        .getStyle(), 12);
    name.setFont(bigFont);
    final int width = table.getWidth();
    if (width > 0)
    {
      name.setSize(width, Short.MAX_VALUE);
    }

    pane.add(name);
    return pane;
  }

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

      final JLabel time = new JLabel(valueItem.getEntry().getDTGString().trim());
      final Font originalFont = time.getFont();

      // the header bar, with the metadata
      final JPanel header = getHeader(valueItem, time, originalFont);

      // the content of the narrative entry
      final JPanel name = getName(text, hasFocus, originalFont, table);

      final JPanel innerPanel = new JPanel();
      innerPanel.setLayout(new BorderLayout());
      innerPanel.add(header, BorderLayout.NORTH);
      innerPanel.add(name, BorderLayout.CENTER);
      mainPanel.add(innerPanel, BorderLayout.CENTER);

      JLabel highlightIcon;
      if (valueItem.getModel().getCurrentHighLight() != null && valueItem
          .getModel().getCurrentHighLight().equals(valueItem.getEntry()))
      {
        highlightIcon = new JLabel(HIGHLIGHT_ICON);
      }
      else
      {
        highlightIcon = new JLabel(BLANK_ICON);
      }

      mainPanel.add(highlightIcon, BorderLayout.WEST);

      if (hasFocus)
      {
        mainPanel.setBackground(selectedColor);
        name.setBackground(selectedColor);
        header.setBackground(selectedColor);
        innerPanel.setBackground(selectedColor);
        final JLabel editNarrative = new JLabel(EDIT_NARRATIVE_ICON);
        final JLabel removeNarrative = new JLabel(REMOVE_NARRATIVE_ICON);

        final JPanel iconsPanel = new JPanel();
        iconsPanel.setBackground(selectedColor);
        iconsPanel.add(editNarrative);
        iconsPanel.add(removeNarrative);
        mainPanel.add(iconsPanel, BorderLayout.EAST);
      }

      if (_model.isWrapping())
      {
        final int currentRowHeight = table.getRowHeight(row);
        if (currentRowHeight != mainPanel.getPreferredSize().height)
        {
          table.setRowHeight(row, mainPanel.getPreferredSize().height);
        }
      }
      else
      {
        if (table.getRowHeight(row) != HEIGHT_FIXED_SIZE)
        {
          table.setRowHeight(row, HEIGHT_FIXED_SIZE);
        }
      }

      return mainPanel;
    }
    else
    {
      return null;
    }
  }

}
