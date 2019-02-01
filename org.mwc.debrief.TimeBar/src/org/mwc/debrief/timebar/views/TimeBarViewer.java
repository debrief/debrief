/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.timebar.views;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.mwc.debrief.timebar.model.IEventEntry;
import org.mwc.debrief.timebar.model.TimeBar;
import org.mwc.debrief.timebar.model.TimeBarPrefs;
import org.mwc.debrief.timebar.model.TimeSpot;
import org.mwc.debrief.timebar.painter.ITimeBarsPainter;
import org.mwc.debrief.timebar.painter.ITimeBarsPainterListener;
import org.mwc.debrief.timebar.painter.NebulaGanttPainter;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.TacticalData.NarrativeWrapper;
import MWC.TacticalData.temporal.ControllableTime;

public class TimeBarViewer implements ISelectionProvider,
    ITimeBarsPainterListener
{

  /**
   * the people listening to us
   */
  List<ISelectionChangedListener> _listeners =
      new ArrayList<ISelectionChangedListener>();

  /**
   * The current selection for this provider
   */
  ISelection _theSelection = null;

  private final Layers _myLayers;

  // GanttChart _chart;

  List<IEventEntry> _timeBars = new ArrayList<IEventEntry>();
  List<IEventEntry> _timeSpots = new ArrayList<IEventEntry>();

  ITimeBarsPainter _painter;

  public TimeBarViewer(final Composite parent, final Layers theLayers)
  {
    _myLayers = theLayers;
    _painter = new NebulaGanttPainter(parent);
    _painter.addListener(this);
  }

  @Override
  public void addSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    if (!_listeners.contains(listener))
    {
      _listeners.add(listener);
    }
  }

  @Override
  public void chartDoubleClicked(final Date clickedAt)
  {
    final HiResDate newDTG = new HiResDate(clickedAt);
   
    final IWorkbench wb = PlatformUI.getWorkbench();
    final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
    final IWorkbenchPage page = win.getActivePage();
    if (page != null)
    {
      final IEditorPart editor = page.getActiveEditor();
      if (editor != null)
      {
        final ControllableTime timer = (ControllableTime) editor.getAdapter(
            ControllableTime.class);
        if (timer != null)
        {
          timer.setTime(this, newDTG, true);
        }
      }
    }
  }

  protected void dispose()
  {
    if (_painter != null)
    {
      _painter.removeListener(this);
      _painter = null;
    }
  }

  /**
   * Runs through the layers, extracts the required elements: track segments, sensor wrappers for a
   * track, annotations/shapes with the time. Draw these elements as Gantt Events (time bars) on the
   * GanttChart control. Extracts narrative entries and annotations/shapes with single time to
   * display them as point markers.
   * 
   * @param theLayers
   *          - Debrief data.
   */
  public void drawDiagram(final Layers theLayers, final boolean jumpToBegin,
      final TimeBarPrefs prefs)
  {
    _timeBars.clear();
    _timeSpots.clear();

    _painter.clear();

    walkThrough(theLayers, prefs);
    for (final IEventEntry barEvent : _timeBars)
    {
      _painter.drawBar(barEvent);
    }
    for (final IEventEntry spotEvent : _timeSpots)
    {
      _painter.drawSpot(spotEvent);
    }
    // move chart start date to the earliest event
    if (jumpToBegin)
    {
      _painter.jumpToBegin();
    }
  }

  public void drawDiagram(final Layers theLayers, final TimeBarPrefs prefs)
  {
    this.drawDiagram(theLayers, false, prefs);
  }

  @Override
  public void eventDoubleClicked(final Object eventEntry)
  {
    CorePlugin.openView(IPageLayout.ID_OUTLINE);
    CorePlugin.openView(IPageLayout.ID_PROP_SHEET);
  }

  @Override
  public void eventSelected(final Object eventEntry)
  {
    setSelectionToObject(eventEntry);
  }

  public void fitToWindow()
  {
    _painter.fitToWindow();
  }

  @Override
  public ISelection getSelection()
  {
    return _theSelection;
  }

  public boolean isDisposed()
  {
    return _painter == null || _painter.isDisposed();
  }

  @Override
  public void removeSelectionChangedListener(
      final ISelectionChangedListener listener)
  {
    _listeners.remove(listener);
  }

  public void setFocus()
  {
    _painter.setFocus();
  }

  @Override
  public void setSelection(final ISelection selection)
  {
    _theSelection = selection;
    final SelectionChangedEvent e = new SelectionChangedEvent(this, selection);

    for (final ISelectionChangedListener l : _listeners)
    {
      SafeRunner.run(new SafeRunnable()
      {
        @Override
        public void run()
        {
          l.selectionChanged(e);
        }
      });
    }
  }

  public void setSelectionToObject(final Object modelEntry)
  {
    if (modelEntry instanceof Editable)
    {
      final Editable ed = (Editable) modelEntry;
      setSelection(new StructuredSelection(new EditableWrapper(ed, null,
          _myLayers)));
    }
  }

  public void setSelectionToWidget(final StructuredSelection selection)
  {
    final Object o = selection.getFirstElement();
    if (!(o instanceof EditableWrapper))
    {
      return;
    }
    final EditableWrapper element = (EditableWrapper) o;
    final Editable selectedItem = element.getEditable();
    _painter.selectTimeBar(selectedItem);
  }

  private void walkThrough(final Object root, final TimeBarPrefs prefs)
  {
    Enumeration<Editable> numer;
    if (root instanceof Layer)
    {
      numer = ((Layer) root).elements();
    }
    else if (root instanceof Layers)
    {
      numer = ((Layers) root).elements();
    }
    else
    {
      return;
    }

    while (numer.hasMoreElements())
    {
      final Editable next = numer.nextElement();

      if (next instanceof WatchableList)
      {
        final WatchableList wlist = (WatchableList) next;
        if (wlist.getStartDTG() != null)
        {
          if (wlist.getEndDTG() != null)
          {
            if (wlist instanceof TrackWrapper)
            {
              _timeBars.add(new TimeBar((TrackWrapper) next, prefs));
            }
            else
            {
              _timeBars.add(new TimeBar(wlist));
            }
          }
          else
          {
            _timeSpots.add(new TimeSpot(wlist));
          }
        }
      }
      else if (next instanceof Watchable)
      {
        final Watchable wb = (Watchable) next;
        if (wb.getTime() != null)
        {
          _timeSpots.add(new TimeSpot(wb));
        }
      }
      else if (next instanceof NarrativeWrapper)
      {
        _timeBars.add(new TimeBar((NarrativeWrapper) next));
      }
      else if (next instanceof SATC_Solution)
      {
        final SATC_Solution solution = (SATC_Solution) next;
        if (solution.getStartDTG() != null)
        {
          _timeBars.add(new TimeBar(solution));
        }
      }
      else if (!(next instanceof WatchableList))
      {
        walkThrough(next, prefs);
      }
    }
  }

  public void zoomIn()
  {
    _painter.zoomIn();
  }

  public void zoomOut()
  {
    _painter.zoomOut();
  }

}
