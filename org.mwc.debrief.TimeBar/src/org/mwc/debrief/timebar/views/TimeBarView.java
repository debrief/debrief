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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.TimeController.views.TimeController;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.timebar.Activator;
import org.mwc.debrief.timebar.model.TimeBarPrefs;

import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;

public class TimeBarView extends ViewPart implements TimeBarPrefs
{

  protected final class NewTimeListener implements PropertyChangeListener
  {
    @Override
    public void propertyChange(final PropertyChangeEvent event)
    {
      // see if it's the time or the period which
      // has changed
      if (event.getPropertyName().equals(
          TimeProvider.TIME_CHANGED_PROPERTY_NAME))
      {
        // ok, use the new time
        final HiResDate newDTG = (HiResDate) event.getNewValue();
        final HiResDate oldDTG = (HiResDate) event.getOldValue();
        final Runnable nextEvent = new Runnable()
        {
          @Override
          public void run()
          {
            _viewer._painter
                .drawDebriefTime(oldDTG.getDate(), newDTG.getDate());
          }
        };
        Display.getDefault().syncExec(nextEvent);
      }
    }
  }

  private static final String COLLAPSE_SEGMENTS = "COLLAPSE_SEGMENTS";

  private static final String COLLAPSE_SENSORS = "COLLAPSE_SENSORS";

  TimeBarViewer _viewer;

  /**
   * helper application to help track creation/activation of new plots
   */
  private PartMonitor _myPartMonitor;

  /**
   * Debrief data
   */
  private Layers _myLayers;
  TimeProvider _timeProvider;

  /**
   * listen out for new times
   */
  private PropertyChangeListener _temporalListener = new NewTimeListener();

  private Layers.DataListener _myLayersListener;

  private ISelectionChangedListener _selectionChangeListener;

  /**
   * Provider listening to us
   */
  private ISelectionProvider _selectionProvider;
  /**
   * Actions to zoom around the time bars
   */
  private Action _zoomInAction;
  private Action _zoomOutAction;

  private Action _fitToWindowAction;
  /**
   * visiblity settings
   * 
   */
  private Action _collapseSegments;

  private Action _collapseSensors;

  private Boolean _defaultCollapseSegments = null;

  private Boolean _defaultCollapseSensors = null;

  /**
   * stop listening to the layer, if necessary
   */
  void clearLayerListener()
  {
    if (_myLayers != null)
    {
      // de-register listeners from the layer
      _myLayers.removeDataExtendedListener(_myLayersListener);
      _myLayers.removeDataReformattedListener(_myLayersListener);
      _myLayersListener = null;
      _myLayers = null;
    }
  }

  void clearTimeListener()
  {
    if (_timeProvider != null)
    {
      _timeProvider.removeListener(_temporalListener,
          TimeProvider.TIME_CHANGED_PROPERTY_NAME);
      _temporalListener = null;
      _timeProvider = null;
    }
  }

  @Override
  public boolean collapseSegments()
  {
    return _collapseSegments.isChecked();
  }

  @Override
  public boolean collapseSensors()
  {
    return _collapseSensors.isChecked();
  }

  private void contributeToActionBars()
  {
    final IActionBars bars = getViewSite().getActionBars();
    fillLocalToolBar(bars.getToolBarManager());
  }

  @Override
  public void createPartControl(final Composite parent)
  {
    _viewer = new TimeBarViewer(parent, _myLayers);

    getSite().setSelectionProvider(_viewer);

    _myPartMonitor =
        new PartMonitor(getSite().getWorkbenchWindow().getPartService());

    listenToMyParts();
    makeActions();
    contributeToActionBars();

    _selectionChangeListener = new ISelectionChangedListener()
    {

      @Override
      public void selectionChanged(final SelectionChangedEvent event)
      {
        final ISelection sel = event.getSelection();
        if (!(sel instanceof IStructuredSelection))
        {
          return;
        }
        final IStructuredSelection ss = (IStructuredSelection) sel;
        final Object o = ss.getFirstElement();
        if (o instanceof EditableWrapper)
        {
          final EditableWrapper pw = (EditableWrapper) o;
          editableSelected(sel, pw);
        }
      }
    };
    _viewer.addSelectionChangedListener(_selectionChangeListener);
  }

  @Override
  public void dispose()
  {
    super.dispose();

    // make sure we close the listeners
    clearLayerListener();
    if (_viewer != null)
    {
      _viewer.removeSelectionChangedListener(_selectionChangeListener);
      _viewer.dispose();
    }

    if (_selectionProvider != null)
    {
      _selectionProvider
          .removeSelectionChangedListener(_selectionChangeListener);
      _selectionProvider = null;
    }

    _selectionChangeListener = null;

    clearTimeListener();
    if (_myPartMonitor != null)
    {
      _myPartMonitor.ditch();
    }
  }

  public void editableSelected(final ISelection sel, final EditableWrapper pw)
  {

    // ahh, just check if this is a whole new layers object
    if (pw.getEditable() instanceof Layers)
    {
      processNewLayers(pw.getEditable());
      return;
    }

    // just check that this is something we can work with
    if (sel instanceof StructuredSelection)
    {
      final StructuredSelection str = (StructuredSelection) sel;

      // hey, is there a payload?
      if (str.getFirstElement() != null)
      {
        // sure is. we only support single selections, so get the first
        // element
        final Object first = str.getFirstElement();
        if (first instanceof EditableWrapper)
        {
          _viewer.setSelectionToWidget((StructuredSelection) sel);
        }
      }
    }

  }

  private void fillLocalToolBar(final IToolBarManager manager)
  {
    manager.add(_zoomInAction);
    manager.add(_zoomOutAction);
    manager.add(_fitToWindowAction);
    manager.add(new Separator());
    manager.add(_collapseSegments);
    manager.add(_collapseSensors);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Object getAdapter(final Class adapter)
  {
    Object res = null;

    if (adapter == ISelectionProvider.class)
    {
      res = _viewer;
    }
    else
    {
      res = super.getAdapter(adapter);
    }

    return res;
  }

  private void listenToMyParts()
  {
    // Listen to Layers
    _myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            processNewLayers(part);
          }
        });
    _myPartMonitor.addPartListener(Layers.class, PartMonitor.OPENED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            processNewLayers(part);
          }
        });
    _myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // is this our set of layers?
            if (part == _myLayers)
            {
              // stop listening to this layer
              clearLayerListener();
            }
          }

        });

    _myPartMonitor.addPartListener(TimeController.class, PartMonitor.ACTIVATED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TimeProvider provider =
                ((TimeController) part).getTimeProvider();
            if (provider != null && provider.equals(_timeProvider))
            {
              return;
            }
            if (provider == null)
            {
              return;
            }
            _timeProvider = provider;
            _timeProvider.addListener(_temporalListener,
                TimeProvider.TIME_CHANGED_PROPERTY_NAME);
          }
        });
    _myPartMonitor.addPartListener(TimeController.class, PartMonitor.OPENED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TimeProvider provider =
                ((TimeController) part).getTimeProvider();
            if (provider != null)
            {
              if (provider.equals(_timeProvider))
              {
                return;
              }
              else
              {
                _timeProvider = provider;
                _timeProvider.addListener(_temporalListener,
                    TimeProvider.TIME_CHANGED_PROPERTY_NAME);
              }
            }
          }
        });

    _myPartMonitor.addPartListener(TimeController.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            final TimeProvider provider =
                ((TimeController) part).getTimeProvider();
            if (provider != null && provider.equals(_timeProvider))
            {
              _timeProvider.removeListener(_temporalListener,
                  TimeProvider.TIME_CHANGED_PROPERTY_NAME);
            }
          }

        });

    _myPartMonitor.addPartListener(ISelectionProvider.class,
        PartMonitor.ACTIVATED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // aah, just check it's not is
            if (part != _viewer)
            {
              final ISelectionProvider iS = (ISelectionProvider) part;
              if (!iS.equals(_selectionProvider))
              {
                _selectionProvider = iS;
                if (_selectionChangeListener != null)
                {
                  _selectionProvider
                      .addSelectionChangedListener(_selectionChangeListener);
                }
              }
            }
          }
        });
    _myPartMonitor.addPartListener(ISelectionProvider.class,
        PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // aah, just check it's not is
            if (!part.equals(_viewer) && _selectionProvider != null
                && _selectionChangeListener != null)
            {
              _selectionProvider
                  .removeSelectionChangedListener(_selectionChangeListener);
            }
          }
        });
    _myPartMonitor.addPartListener(ISelectionProvider.class,
        PartMonitor.CLOSED, new PartMonitor.ICallback()
        {
          @Override
          public void eventTriggered(final String type, final Object part,
              final IWorkbenchPart parentPart)
          {
            // if we are closed
            if (part == _viewer)
            {
              _viewer.removeSelectionChangedListener(_selectionChangeListener);
              if (_selectionProvider != null
                  && _selectionChangeListener != null)
              {
                _selectionProvider
                    .removeSelectionChangedListener(_selectionChangeListener);
              }

              clearLayerListener();
              clearTimeListener();
            }
          }
        });

    _myPartMonitor.addPartListener(TimeBarView.class, PartMonitor.CLOSED,
        new PartMonitor.ICallback()
        {

          @Override
          public void eventTriggered(String type, Object instance,
              IWorkbenchPart parentPart)
          {
            Activator.getDefault().getPreferenceStore().setValue(
                COLLAPSE_SEGMENTS, _collapseSegments.isChecked());
            Activator.getDefault().getPreferenceStore().setValue(
                COLLAPSE_SENSORS, _collapseSensors.isChecked());
          }
        });

    // ok we're all ready now. just try and see if the current part is valid
    _myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
        .getActivePage());
  }

  private void makeActions()
  {
    _zoomInAction = new Action("Zoom in", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        _viewer.zoomIn();
      }
    };
    _zoomInAction.setText("Zoom in");
    _zoomInAction.setToolTipText("Zoom in");
    _zoomInAction.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/zoomin.png"));

    _zoomOutAction = new Action("Zoom out", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        _viewer.zoomOut();
      }
    };
    _zoomOutAction.setText("Zoom out");
    _zoomOutAction.setToolTipText("Zoom out");
    _zoomOutAction.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/zoomout.png"));

    _fitToWindowAction = new Action("Fit to Window", IAction.AS_PUSH_BUTTON)
    {
      @Override
      public void run()
      {
        _viewer.fitToWindow();
      }
    };
    _fitToWindowAction.setText("Fit to Window");
    _fitToWindowAction.setToolTipText("Fit to Window");
    _fitToWindowAction.setImageDescriptor(CorePlugin
        .getImageDescriptor("icons/16/fit_to_win.png"));

    _collapseSensors = new Action("Collapse sensors", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        // force rescan
        processNewData(_myLayers, null, null);
      }
    };
    _collapseSensors.setText("Collapse sensors");
    _collapseSensors.setImageDescriptor(DebriefPlugin
        .getImageDescriptor("icons/16/sensor.png"));
    if (_defaultCollapseSensors != null)
    {
      _collapseSensors.setChecked(_defaultCollapseSensors);
    }

    _collapseSegments = new Action("Collapse segments", IAction.AS_CHECK_BOX)
    {
      @Override
      public void run()
      {
        // force rescan
        processNewData(_myLayers, null, null);
      }
    };
    _collapseSegments.setText("Collapse segments");
    _collapseSegments.setImageDescriptor(DebriefPlugin
        .getImageDescriptor("icons/16/tma_segment.png"));
    if (_defaultCollapseSegments != null)
    {
      _collapseSegments.setChecked(_defaultCollapseSegments);
    }

  }

  void processNewData(final Layers theData, final Editable newItem,
      final HasEditables parentLayer)
  {
    final TimeBarPrefs prefs = this;

    Display.getDefault().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        if (_viewer == null || _viewer.isDisposed())
        {
          return;
        }
        // ok, fire the change in the UI thread
        _viewer.drawDiagram(theData, true, prefs /* jump to begin */);
        // hmm, do we know about the new item? If so, better select it
        if (newItem != null)
        {
          // wrap the plottable
          final EditableWrapper parentWrapper =
              new EditableWrapper((Editable) parentLayer, null, theData);
          final EditableWrapper wrapped =
              new EditableWrapper(newItem, parentWrapper, theData);
          final ISelection selected = new StructuredSelection(wrapped);

          // and select it
          editableSelected(selected, wrapped);
        }
      }
    });
  }

  void processNewLayers(final Object part)
  {
    // just check we're not already looking at it
    if (!part.equals(_myLayers))
    {
      // de-register current layers before tracking the new one
      clearLayerListener();
    }
    else
    {
      return;
    }

    _myLayers = (Layers) part;
    if (_myLayersListener == null)
    {
      _myLayersListener = new Layers.DataListener2()
      {

        @Override
        public void dataExtended(final Layers theData)
        {
          dataExtended(theData, null, null);
        }

        @Override
        public void dataExtended(final Layers theData, final Plottable newItem,
            final HasEditables parentLayer)
        {
          processNewData(theData, newItem, parentLayer);
        }

        @Override
        public void
            dataModified(final Layers theData, final Layer changedLayer)
        {
        }

        @Override
        public void dataReformatted(final Layers theData,
            final Layer changedLayer)
        {
          processReformattedLayer(theData, changedLayer);
        }
      };
    }
    // right, listen for data being added
    _myLayers.addDataExtendedListener(_myLayersListener);

    // and listen for items being reformatted
    _myLayers.addDataReformattedListener(_myLayersListener);

    // do an initial population.
    processNewData(_myLayers, null, null);
  }

  void processReformattedLayer(final Layers theData, final Layer changedLayer)
  {
    final TimeBarPrefs prefs = this;
    Display.getDefault().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        if (_viewer == null || _viewer.isDisposed())
        {
          return;
        }
        _viewer.drawDiagram(theData, prefs);
      }
    });
  }

  @Override
  public void setFocus()
  {
    _viewer.setFocus();
  }

  /**
   * @param site
   * @param memento
   * @throws PartInitException
   */
  public void init(final IViewSite site, final IMemento memento)
      throws PartInitException
  {
    super.init(site, memento);

    if (memento != null)
    {
      // try the slider step size
      _defaultCollapseSegments = memento.getBoolean(COLLAPSE_SEGMENTS);
      _defaultCollapseSensors = memento.getBoolean(COLLAPSE_SENSORS);
    }
    else
    {
      // look in the prefs store
      _defaultCollapseSegments =
          Activator.getDefault().getPreferenceStore().getBoolean(
              COLLAPSE_SEGMENTS);
      _defaultCollapseSensors =
          Activator.getDefault().getPreferenceStore().getBoolean(
              COLLAPSE_SENSORS);
    }
  }

  @Override
  public void saveState(IMemento memento)
  {
    super.saveState(memento);

    // app closing, store our state
    memento.putBoolean(COLLAPSE_SEGMENTS, _collapseSegments.isChecked());
    memento.putBoolean(COLLAPSE_SENSORS, _collapseSensors.isChecked());
  }

}
