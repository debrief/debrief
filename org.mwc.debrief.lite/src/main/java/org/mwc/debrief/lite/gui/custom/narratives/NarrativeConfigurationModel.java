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
import java.util.concurrent.Callable;

import org.mwc.debrief.lite.gui.custom.AbstractSelection;

import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.NarrativeWrapper;
import MWC.TacticalData.temporal.TimeManager;
import MWC.TacticalData.temporal.TimeProvider;

public class NarrativeConfigurationModel implements
    AbstractNarrativeConfiguration
{

  public static final String NARRATIVE_SELECTION = "NARRATIVE_SELECTION";

  public static final String NARRATIVE_HIGHLIGHT = "NARRATIVE_HIGHLIGHT";

  public static final String NARRATIVE_CHANGE = "NARRATIVE_CHANGED";

  private NarrativeEntry _currentHighLight;

  private boolean _wrapping = true;

  private int panelWidth = 0;

  private String _filterText;

  private Callable<Void> _repaintMethod;

  private final TimeManager _timeManager;

  private final ArrayList<PropertyChangeListener> _stateListeners =
      new ArrayList<>();

  private final List<AbstractSelection<NarrativeEntry>> _narrativeSelection =
      new ArrayList<>();

  private final HashMap<NarrativeWrapper, Set<NarrativeEntry>> _narrativeWrappers =
      new HashMap<>();

  public NarrativeConfigurationModel(final TimeManager timeManager)
  {
    this._timeManager = timeManager;

    final PropertyChangeListener timeChangedListener =
        new PropertyChangeListener()
        {

          @Override
          public void propertyChange(final PropertyChangeEvent evt)
          {
            highlightNarrative((HiResDate) evt.getNewValue());
          }
        };

    _timeManager.addListener(timeChangedListener,
        TimeProvider.TIME_CHANGED_PROPERTY_NAME);
  }

  @Override
  public void addNarrativeWrapper(final NarrativeWrapper narrativeWrapper)
  {
    _narrativeWrappers.put(narrativeWrapper, new HashSet<NarrativeEntry>());
  }

  @Override
  public void addPropertyChangeListener(final PropertyChangeListener listener)
  {
    this._stateListeners.add(listener);
  }

  @Override
  public NarrativeEntry getCurrentHighLight()
  {
    return _currentHighLight;
  }

  @Override
  public Set<NarrativeEntry> getCurrentNarrativeEntries(
      final NarrativeWrapper narrativeWrapper)
  {
    return _narrativeWrappers.get(narrativeWrapper);
  }

  @Override
  public String getFilterText()
  {
    return _filterText;
  }

  @Override
  public List<AbstractSelection<NarrativeEntry>> getNarratives()
  {
    return _narrativeSelection;
  }

  @Override
  public int getPanelWidth()
  {
    return panelWidth;
  }

  @Override
  public Set<NarrativeWrapper> getRegisteredNarrativeWrapper()
  {
    return _narrativeWrappers.keySet();
  }

  @Override
  public TimeManager getTimeManager()
  {
    return _timeManager;
  }

  @Override
  public void highlightNarrative(final HiResDate object)
  {
    NarrativeEntry narrative = null;
    long closestDistance = Long.MAX_VALUE;
    for (final Set<NarrativeEntry> narrativeEntries : _narrativeWrappers
        .values())
    {
      for (final NarrativeEntry narrativeEntry : narrativeEntries)
      {
        if (narrative == null || closestDistance > Math.abs(narrativeEntry
            .getDTG().getMicros() - object.getMicros()))
        {
          closestDistance = Math.abs(narrativeEntry.getDTG().getMicros()
              - object.getMicros());

          narrative = narrativeEntry;
        }
      }
    }
    if (narrative != null)
    {
      final boolean update = _currentHighLight == null || !_currentHighLight
          .equals(narrative);

      if (update)
      {
        final NarrativeEntry oldValue = _currentHighLight;
        final NarrativeEntry newValue = narrative;
        _currentHighLight = narrative;
        notifyListenersStateChanged(narrative, NARRATIVE_HIGHLIGHT, oldValue,
            newValue);
      }
    }
  }

  @Override
  public boolean isWrapping()
  {
    return _wrapping;
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
  public void registerNewNarrativeEntry(final NarrativeWrapper wrapper,
      final NarrativeEntry entry)
  {
    if (!_narrativeWrappers.containsKey(wrapper))
    {
      addNarrativeWrapper(wrapper);
    }
    _narrativeWrappers.get(wrapper).add(entry);
  }

  @Override
  public void removeNarrativeWrapper(final NarrativeWrapper narrativeWrapper)
  {
    _narrativeWrappers.remove(narrativeWrapper);
  }

  @Override
  public void repaintView()
  {
    try
    {
      _repaintMethod.call();
    }
    catch (final Exception e)
    {
      // It should never happen
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

  @Override
  public void setFilterText(final String text)
  {
    this._filterText = text;
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
  public void setPanelWidth(final int width)
  {
    this.panelWidth = width;
  }

  @Override
  public void setRepaintMethod(final Callable<Void> repaint)
  {
    this._repaintMethod = repaint;
  }

  @Override
  public void setWrapping(final boolean wrapping)
  {
    _wrapping = wrapping;
  }

  @Override
  public void unregisterNarrativeEntry(final NarrativeWrapper wrapper,
      final NarrativeEntry entry)
  {
    if (_narrativeWrappers.containsKey(wrapper))
    {
      _narrativeWrappers.get(wrapper).remove(entry);
    }
  }
}
