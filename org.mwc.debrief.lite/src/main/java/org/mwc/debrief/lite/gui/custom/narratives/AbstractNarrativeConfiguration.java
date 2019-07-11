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
  public void addPropertyChangeListener(final PropertyChangeListener listener);

  public void setNarrativeWrapper(final NarrativeWrapper narrativeWrapper);

  public void setActiveNarrative(final NarrativeEntry narrative,
      final boolean highlight);

  public void highlightNarrative(final HiResDate date);

  public List<AbstractSelection<NarrativeEntry>> getNarratives();

  public void setFilterText(final String text);

  public String getFilterText();

  public Set<Object> getRegisteredNarrativeWrapper();

  public void addNarrativeLayer(final Object narrativeWrapper);

  public Set<NarrativeEntry> getCurrentNarrativeEntries(
      final Object narrativeWrapper);

  public void registerNewNarrativeEntry(final Object wrapper,
      final NarrativeEntry entry);

  public void removeNarrativeLayer(final Object narrativeWrapper);

  public void unregisterNarrativeEntry(final Object wrapper,
      final NarrativeEntry entry);
  
  public boolean isWrapping();
  
  public void setWrapping(final boolean wrapping);
  
  public TimeManager getTimeManager();
  
  public void setRepaintMethod(final Callable<Void> repaint);
  
  public void repaintView();
  
  public NarrativeEntry getCurrentHighLight();
  
  public int getPanelWidth();
  
  public void setPanelWidth(final int width);
}
