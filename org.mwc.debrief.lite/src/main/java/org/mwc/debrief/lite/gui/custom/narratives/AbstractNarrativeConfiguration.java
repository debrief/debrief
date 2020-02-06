/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
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
