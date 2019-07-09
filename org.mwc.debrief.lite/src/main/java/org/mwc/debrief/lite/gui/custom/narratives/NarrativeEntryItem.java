package org.mwc.debrief.lite.gui.custom.narratives;

import MWC.TacticalData.NarrativeEntry;

public class NarrativeEntryItem
{
  private final NarrativeEntry _entry;
  private final AbstractNarrativeConfiguration _model;
  public NarrativeEntryItem(NarrativeEntry _entry,
      AbstractNarrativeConfiguration _model)
  {
    super();
    this._entry = _entry;
    this._model = _model;
  }
  public NarrativeEntry getEntry()
  {
    return _entry;
  }
  public AbstractNarrativeConfiguration getModel()
  {
    return _model;
  }
  
  
}
