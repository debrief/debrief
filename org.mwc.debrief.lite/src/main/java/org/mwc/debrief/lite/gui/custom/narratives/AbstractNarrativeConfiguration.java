package org.mwc.debrief.lite.gui.custom.narratives;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;

import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

public interface AbstractNarrativeConfiguration
{
  public void addPropertyChangeListener(final PropertyChangeListener listener);

  public void setNarrativeWrapper(final NarrativeWrapper narrativeWrapper);

  public void setActiveNarrative(final NarrativeEntry narrative,
      final boolean highlight);

  public void highlightNarrative(final NarrativeEntry narrative);

  public List<AbstractSelection<NarrativeEntry>> getNarratives();

  public void setFilterText(final String text);

  public String getFilterText();

  public Set<NarrativeWrapper> getRegisteredNarrativeWrapper();

  public void addNarrativeWrapper(final NarrativeWrapper narrativeWrapper);

  public Set<NarrativeEntry> getCurrentNarrativeEntries(
      final NarrativeWrapper narrativeWrapper);

  public void registerNewNarrativeEntry(final NarrativeWrapper wrapper,
      final NarrativeEntry entry);

  public void removeNarrativeWrapper(final NarrativeWrapper narrativeWrapper);

  public void unregisterNarrativeEntry(final NarrativeWrapper wrapper,
      final NarrativeEntry entry);
}
