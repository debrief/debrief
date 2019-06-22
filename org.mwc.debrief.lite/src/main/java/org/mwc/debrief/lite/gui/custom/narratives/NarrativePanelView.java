package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class NarrativePanelView extends JPanel
{

  /**
   *
   */
  private static final long serialVersionUID = 1218759123615315561L;

  private final AbstractNarrativeConfiguration _model;

  public NarrativePanelView(final NarrativePanelToolbar toolbar,
      final AbstractNarrativeConfiguration model)
  {
    super();

    final NarrativePanelToolbar _toolbar = toolbar;
    _model = model;

    setLayout(new BorderLayout());

    add(_toolbar, BorderLayout.NORTH);
    final Box mainPanel = Box.createVerticalBox();
    final JScrollPane scrollPane = new JScrollPane(mainPanel);
    scrollPane.setHorizontalScrollBarPolicy(
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(
        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

    final JTextField filterTextField = new JTextField();
    filterTextField.setPreferredSize(new Dimension(30, 20));
    filterTextField.setMaximumSize(new Dimension(300, 20));
    mainPanel.add(filterTextField);

    mainPanel.add(toolbar.getNarrativeList());

    add(scrollPane, BorderLayout.CENTER);
  }
}
