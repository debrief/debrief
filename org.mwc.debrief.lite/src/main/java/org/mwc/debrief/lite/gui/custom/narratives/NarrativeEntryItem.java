package org.mwc.debrief.lite.gui.custom.narratives;

import MWC.TacticalData.NarrativeEntry;

public class NarrativeEntryItem implements Comparable<NarrativeEntryItem>
{
  private final NarrativeEntry _entry;
  private final AbstractNarrativeConfiguration _model;

  public NarrativeEntryItem(final NarrativeEntry _entry,
      final AbstractNarrativeConfiguration _model)
  {
    super();
    this._entry = _entry;
    this._model = _model;
  }

  @Override
  public int compareTo(final NarrativeEntryItem o)
  {
    return _entry.getDTG().compareTo(o.getEntry().getDTG());
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
