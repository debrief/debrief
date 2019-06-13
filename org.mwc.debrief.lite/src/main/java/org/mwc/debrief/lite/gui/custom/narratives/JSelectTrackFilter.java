package org.mwc.debrief.lite.gui.custom.narratives;

import javax.swing.JPopupMenu;

public class JSelectTrackFilter extends JPopupMenu
{

  /**
   * 
   */
  private static final long serialVersionUID = 7136974124331608166L;

  final AbstractNarrativeConfiguration _model;
  
  public JSelectTrackFilter(final AbstractNarrativeConfiguration model)
  {
    this._model = model;

  }
}
