package org.mwc.debrief.lite.narratives;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class NarrativePanelView extends JPanel
{

  /**
   * 
   */
  private static final long serialVersionUID = 1218759123615315561L;

  private final NarrativePanelToolbar _toolbar;
  
  public NarrativePanelView(final NarrativePanelToolbar toolbar)
  {
    super();
    
    _toolbar = toolbar;
    
    setLayout(new BorderLayout());
    
    add(_toolbar, BorderLayout.NORTH);
  }
}
