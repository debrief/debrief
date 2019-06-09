package org.mwc.debrief.lite.gui.custom;

import java.beans.PropertyChangeListener;
import java.util.List;

import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

public interface AbstractNarrativeConfiguration
{
  static class NarrativeEntrySelect
  {
    public NarrativeEntry narrative;
    public Boolean selected;

    public NarrativeEntrySelect(final NarrativeEntry narrative, final Boolean selected)
    {
      super();
      this.narrative = narrative;
      this.selected = selected;
    }
  }
  
  public void addPropertyChangeListener(final PropertyChangeListener listener);
  
  public void setNarrativeWrapper(final NarrativeWrapper narrativeWrapper);
  
  public void setActiveNarrative(final NarrativeEntry narrative, final boolean hightlight);
  
  public List<NarrativeEntry> getNarratives();
  
  public void setFilterText(final String text);
  
  public String getFilterText();
}
