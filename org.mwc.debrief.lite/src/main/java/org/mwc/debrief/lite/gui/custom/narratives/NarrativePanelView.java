package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class NarrativePanelView extends JPanel
{

  /**
   *
   */
  private static final long serialVersionUID = 1218759123615315561L;

  public NarrativePanelView(final NarrativePanelToolbar toolbar,
      final AbstractNarrativeConfiguration model)
  {
    super();

    final NarrativePanelToolbar _toolbar = toolbar;

    setLayout(new BorderLayout());

    add(_toolbar, BorderLayout.NORTH);
    final JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    final JScrollPane scrollPane = new JScrollPane();
    scrollPane.setVerticalScrollBarPolicy(
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setViewportView(mainPanel);

    mainPanel.add(toolbar.getNarrativeList());

    add(scrollPane, BorderLayout.CENTER);

  }
}
