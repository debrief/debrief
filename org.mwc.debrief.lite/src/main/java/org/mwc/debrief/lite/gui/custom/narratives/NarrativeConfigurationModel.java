/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.lite.gui.custom.narratives;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;

import MWC.GUI.Editable;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;

public class NarrativeConfigurationModel implements
    AbstractNarrativeConfiguration
{

  public static final String NARRATIVE_SELECTION = "NARRATIVE_SELECTION";

  public static final String NARRATIVE_HIGHLIGHT = "NARRATIVE_HIGHLIGHT";

  public static final String NARRATIVE_CHANGE = "NARRATIVE_CHANGED";

  private NarrativeEntry _currentHighLight;

  private String _filterText;

  private final ArrayList<PropertyChangeListener> _stateListeners =
      new ArrayList<>();

  private List<AbstractSelection<NarrativeEntry>> _narrativeSelection =
      new ArrayList<>();
  
  private HashMap<NarrativeWrapper, Set<NarrativeEntry>> _narrativeWrappers = new HashMap<>();

  public NarrativeConfigurationModel()
  {

  }

  @Override
  public void addPropertyChangeListener(final PropertyChangeListener listener)
  {
    this._stateListeners.add(listener);
  }

  @Override
  public void setNarrativeWrapper(final NarrativeWrapper narrativeWrapper)
  {
    final Enumeration<Editable> iter = narrativeWrapper.elements();
    while (iter.hasMoreElements())
    {
      final Editable nextItem = iter.nextElement();
      if (nextItem instanceof NarrativeEntry)
      {
        _narrativeSelection.add(new AbstractSelection<NarrativeEntry>(
            (NarrativeEntry) nextItem, true));
      }
    }
  }

  @Override
  public void setActiveNarrative(final NarrativeEntry narrative,
      final boolean check)
  {
    // TODO We should move this out of here
    Boolean oldValue = null;
    Boolean newValue = null;
    for (final AbstractSelection<NarrativeEntry> currentNarrative : _narrativeSelection)
    {
      if (currentNarrative.getItem().equals(narrative))
      {
        newValue = check;
        oldValue = currentNarrative.isSelected();
        currentNarrative.setSelected(newValue);
      }
    }

    if (newValue != null && !oldValue.equals(newValue))
    {
      // we have the element changed.
      notifyListenersStateChanged(narrative, NARRATIVE_SELECTION, oldValue,
          check);
    }
  }

  private void notifyListenersStateChanged(final Object source,
      final String property, final Object oldValue, final Object newValue)
  {
    for (final PropertyChangeListener event : _stateListeners)
    {
      event.propertyChange(new PropertyChangeEvent(source, property, oldValue,
          newValue));
    }
  }

  @Override
  public List<AbstractSelection<NarrativeEntry>> getNarratives()
  {
    return _narrativeSelection;
  }

  @Override
  public void setFilterText(final String text)
  {
    this._filterText = text;
  }

  @Override
  public String getFilterText()
  {
    return _filterText;
  }

  @Override
  public void highlightNarrative(final NarrativeEntry narrative)
  {
    boolean update = _currentHighLight == null || !_currentHighLight.equals(
        narrative);

    if (update)
    {
      final NarrativeEntry oldValue = _currentHighLight;
      final NarrativeEntry newValue = narrative;
      notifyListenersStateChanged(narrative, NARRATIVE_HIGHLIGHT, oldValue,
          newValue);
    }
  }
  
  public void addNarrativeWrapper(final NarrativeWrapper narrativeWrapper)
  {
    _narrativeWrappers.put(narrativeWrapper, new HashSet<NarrativeEntry>());
  }

  public Set<NarrativeWrapper> getRegisteredNarrativeWrapper()
  {
    return _narrativeWrappers.keySet();
  }

  @Override
  public Set<NarrativeEntry> getCurrentNarrativeEntries(
      NarrativeWrapper narrativeWrapper)
  {
    return _narrativeWrappers.get(narrativeWrapper);
  }
}
