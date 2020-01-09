package org.mwc.debrief.lite.gui.custom.narratives;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.TacticalData.temporal.TimeManager;

public interface AbstractNarrativeConfiguration
{
  public void addNarrativeWrapper(final NarrativeWrapper narrativeWrapper);

  public void addPropertyChangeListener(final PropertyChangeListener listener);

  public NarrativeEntry getCurrentHighLight();

  public Set<NarrativeEntry> getCurrentNarrativeEntries(
      final NarrativeWrapper narrativeWrapper);

  public String getFilterText();

  public List<AbstractSelection<NarrativeEntry>> getNarratives();

  public int getPanelWidth();

  public Set<NarrativeWrapper> getRegisteredNarrativeWrapper();

  public TimeManager getTimeManager();

  public void highlightNarrative(final HiResDate date);

  public boolean isWrapping();

  public void registerNewNarrativeEntry(final NarrativeWrapper wrapper,
      final NarrativeEntry entry);

  public void removeNarrativeWrapper(final NarrativeWrapper narrativeWrapper);

  public void repaintView();

  public void setActiveNarrative(final NarrativeEntry narrative,
      final boolean highlight);

  public void setFilterText(final String text);

  public void setNarrativeWrapper(final NarrativeWrapper narrativeWrapper);

  public void setPanelWidth(final int width);

  public void setRepaintMethod(final Callable<Void> repaint);

  public void setWrapping(final boolean wrapping);

  public void unregisterNarrativeEntry(final NarrativeWrapper wrapper,
      final NarrativeEntry entry);
}
