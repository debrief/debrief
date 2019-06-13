package org.mwc.debrief.lite.gui.custom.narratives;

import javax.swing.JPopupMenu;

import org.mwc.debrief.lite.gui.custom.JPopupList;

import MWC.TacticalData.NarrativeEntry;

public class JSelectTrackFilter extends JPopupList<NarrativeEntry>
{

  /**
   * 
   */
  private static final long serialVersionUID = 7136974124331608166L;

  final AbstractNarrativeConfiguration _model;
  
  public JSelectTrackFilter(final AbstractNarrativeConfiguration model)
  {
    super(new NarrativePanelItemRenderer(), model.getNarratives());
    this._model = model;

  }
}
