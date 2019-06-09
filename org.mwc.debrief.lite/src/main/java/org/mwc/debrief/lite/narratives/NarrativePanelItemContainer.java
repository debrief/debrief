package org.mwc.debrief.lite.narratives;

import javax.swing.JPanel;

import org.mwc.debrief.lite.gui.custom.AbstractNarrativeConfiguration;

public class NarrativePanelItemContainer extends JPanel
{

  final AbstractNarrativeConfiguration _model;
  
  /**
   * 
   */
  private static final long serialVersionUID = -5684229169343801657L;

  public NarrativePanelItemContainer(final AbstractNarrativeConfiguration model)
  {
    this._model = model;
  }
}
