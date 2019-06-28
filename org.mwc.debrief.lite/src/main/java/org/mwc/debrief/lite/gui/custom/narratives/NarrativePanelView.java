package org.mwc.debrief.lite.gui.custom.narratives;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
    // final JScrollPane scrollPane = new JScrollPane(mainPanel);

    final JTextField filterTextField = new JTextField();
    filterTextField.setPreferredSize(new Dimension(30, 20));
    mainPanel.add(filterTextField);

    mainPanel.add(toolbar.getNarrativeList());

    add(toolbar.getNarrativeList(), BorderLayout.CENTER);

  }
}
