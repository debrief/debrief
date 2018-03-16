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
package org.mwc.debrief.core.ContextOperations;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TacticalDataWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.TimeFrequencyPropertyEditor;
import MWC.GenericData.TimePeriod;

/**
 * @author ian.mayo
 *
 */
public class SelectAllChildren implements RightClickContextItemGenerator
{
  private static class SelectChildrenAtFrequencyOperation extends
      SelectChildrenOperation
  {

    @SuppressWarnings("unused")
    private final long _interval;

    public SelectChildrenAtFrequencyOperation(final Layers theLayers,
        final HasEditables selected, final long interval)
    {
      super(theLayers, selected);
      _interval = interval;
    }

    @Override
    protected List<EditableWrapper> itemsFor(final HasEditables parent)
    {
      final List<EditableWrapper> res = new ArrayList<>();

      final TrackSegment wrap = (TrackSegment) parent;
      final EditableWrapper track = new EditableWrapper(wrap.getWrapper(), null,
          _theLayers);
      final EditableWrapper segment = new EditableWrapper(wrap, track,
          _theLayers);

      final Enumeration<Editable> items = wrap.elements();
      long lastStamp = -1;
      while (items.hasMoreElements())
      {
        final Editable item = items.nextElement();

        final FixWrapper fix = (FixWrapper) item;
        final long time = fix.getDateTimeGroup().getDate().getTime();

        // is this the first element?
        if (lastStamp == -1)
        {
          // wrap the fix
          res.add(new EditableWrapper(item, segment, _theLayers));

          // remember how many intervals there have been
          lastStamp = time / _interval;
        }
        else
        {
          // find the new number of intervals
          final long hour = time / _interval;

          // have we passed a new interval?
          if (hour > lastStamp)
          {
            // store this new interval
            lastStamp = hour;

            // wrap the fix
            res.add(new EditableWrapper(item, segment, _theLayers));
          }
        }
      }
      return res;
    }
  }

  private static class SelectChildrenOperation extends CMAPOperation
  {
    final private HasEditables _hasEditables;
    protected final Layers _theLayers;

    public SelectChildrenOperation(final Layers theLayers,
        final HasEditables selected)
    {
      super("Select all children");
      _hasEditables = selected;
      _theLayers = theLayers;
    }

    @Override
    public boolean canExecute()
    {
      return true;
    }

    @Override
    public boolean canRedo()
    {
      return false;
    }

    @Override
    public boolean canUndo()
    {
      return false;
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {
      final List<EditableWrapper> selection = itemsFor(_hasEditables);

      if (selection != null && selection.size() > 0)
      {
        // ok, get the editor
        final IWorkbench wb = PlatformUI.getWorkbench();
        final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        final IWorkbenchPage page = win.getActivePage();
        final IEditorPart editor = page.getActiveEditor();

        if (editor != null)
        {
          final IContentOutlinePage outline = (IContentOutlinePage) editor
              .getAdapter(IContentOutlinePage.class);
          if (outline != null)
          {
            // now set the selection
            final IStructuredSelection str = new StructuredSelection(selection);

            outline.setSelection(str);
          }
        }
      }

      return Status.OK_STATUS;
    }

    protected List<EditableWrapper> itemsFor(final HasEditables parent)
    {
      final List<EditableWrapper> res;
      if (parent instanceof TacticalDataWrapper)
      {
        final TacticalDataWrapper wrap = (TacticalDataWrapper) parent;
        final EditableWrapper track = new EditableWrapper(wrap.getHost(), null,
            _theLayers);
        final EditableWrapper thisList = new EditableWrapper(wrap, track,
            _theLayers);

        res = new ArrayList<EditableWrapper>();

        final Enumeration<Editable> items = wrap.elements();
        while (items.hasMoreElements())
        {
          final Editable item = items.nextElement();
          res.add(new EditableWrapper(item, thisList, _theLayers));
        }
      }
      else if (parent instanceof BaseLayer)
      {
        final BaseLayer wrap = (BaseLayer) parent;
        final EditableWrapper track = new EditableWrapper(wrap, null,
            _theLayers);
        final EditableWrapper thisList = new EditableWrapper(wrap, track,
            _theLayers);

        res = new ArrayList<EditableWrapper>();

        final Enumeration<Editable> items = wrap.elements();
        while (items.hasMoreElements())
        {
          final Editable item = items.nextElement();
          res.add(new EditableWrapper(item, thisList, _theLayers));
        }
      }
      else if (parent instanceof TrackSegment)
      {
        final TrackSegment wrap = (TrackSegment) parent;
        final EditableWrapper track = new EditableWrapper(wrap.getWrapper(),
            null, _theLayers);
        final EditableWrapper thisList = new EditableWrapper(wrap, track,
            _theLayers);

        res = new ArrayList<EditableWrapper>();

        final Enumeration<Editable> items = wrap.elements();
        while (items.hasMoreElements())
        {
          final Editable item = items.nextElement();
          res.add(new EditableWrapper(item, thisList, _theLayers));
        }
      }
      else if (parent instanceof SegmentList)
      {
        final SegmentList wrap = (SegmentList) parent;
        final EditableWrapper track = new EditableWrapper(wrap.getWrapper(),
            null, _theLayers);
        final EditableWrapper segList = new EditableWrapper(wrap.getWrapper()
            .getSegments(), track, _theLayers);
        final EditableWrapper thisList = new EditableWrapper(wrap, segList,
            _theLayers);

        res = new ArrayList<EditableWrapper>();

        final Enumeration<Editable> items = wrap.elements();
        while (items.hasMoreElements())
        {
          final Editable item = items.nextElement();
          res.add(new EditableWrapper(item, thisList, _theLayers));
        }
      }
      else
      {
        System.out.println("not handling:" + parent.getClass());
        res = null;
      }
      return res;
    }

    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      return Status.OK_STATUS;
    }

  }

  /**
   * the list of tags shown in the drop-down list
   */
  private final String _stringTags[] =
  {"5 Mins", "15 Mins", "30 Mins", "60 Mins", "2 Hours", "6 Hours", "12 Hours",
      "24 Hours", "48 Hours", "72 Hours"};

  /**
   * the values to use for the tags in the list
   */
  private final long _freqs[] =
  {TimeFrequencyPropertyEditor._5_MINS, TimeFrequencyPropertyEditor._15_MINS,
      TimeFrequencyPropertyEditor._30_MINS,
      TimeFrequencyPropertyEditor._60_MINS, 2
          * TimeFrequencyPropertyEditor._60_MINS, 6
              * TimeFrequencyPropertyEditor._60_MINS, 12
                  * TimeFrequencyPropertyEditor._60_MINS, 24
                      * TimeFrequencyPropertyEditor._60_MINS, 48
                          * TimeFrequencyPropertyEditor._60_MINS, 72
                              * TimeFrequencyPropertyEditor._60_MINS};

  /**
   * @param parent
   * @param theLayers
   * @param parentLayers
   * @param subjects
   */
  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {

    // so, see if it's something we can do business with
    if (subjects.length == 1)
    {

      final Editable selected = subjects[0];

      // does it have any children?
      if (selected instanceof HasEditables)
      {   
        // are the children time-stamped?
        final TimePeriod coverage = timePeriodFor(selected);
        if (coverage != null)
        {
          // ok, create the children at freq action

          // and the new drop-down list of interpolation frequencies
          final MenuManager newMenu = new MenuManager(
              "Select child elements with this interval");
          
          // ok, generate the operation
          final IUndoableOperation action = getOperation(theLayers,
              (HasEditables) selected);

          // and now wrap it in an action
          final Action doIt = new Action("All")
          {
            @Override
            public void run()
            {
              runIt(action);
            }
          };
          doIt.setImageDescriptor(DebriefPlugin.getImageDescriptor(
              "icons/16/show.png"));

          // ok, go for it
          newMenu.add(doIt);
          

          for (int i = 0; i < _freqs.length; i++)
          {
            // convert from microseconds to milliseconds
            final long thisLen = _freqs[i] / 1000;

            if (thisLen < coverage.getExtent())
            {
              final IUndoableOperation op = getTimedOperation(theLayers,
                  (HasEditables) selected, thisLen);
              // create the new menu item
              final Action selectItem = new Action(_stringTags[i])
              {
                @Override
                public void run()
                {
                  runIt(op);
                }
              };

              newMenu.add(selectItem);
              selectItem.setImageDescriptor(DebriefPlugin.getImageDescriptor(
                  "icons/16/show.png"));
            }
          }
          parent.add(newMenu);
        }
        else
        {
          // ok, generate the operation
          final IUndoableOperation action = getOperation(theLayers,
              (HasEditables) selected);

          // and now wrap it in an action
          final Action doIt = new Action("Select all child elements")
          {
            @Override
            public void run()
            {
              runIt(action);
            }
          };
          doIt.setImageDescriptor(DebriefPlugin.getImageDescriptor(
              "icons/16/show.png"));

          // ok, go for it
          parent.add(doIt);
        }
      }
    }
  }

  /**
   * move the operation generation to a method, so it can be overwritten (in testing)
   *
   *
   * @param theLayers
   * @param suitableSegments
   * @param commonParent
   * @return
   */
  protected IUndoableOperation getOperation(final Layers theLayers,
      final HasEditables selected)
  {
    return new SelectChildrenOperation(theLayers, selected);
  }

  /**
   * move the operation generation to a method, so it can be overwritten (in testing)
   *
   *
   * @param theLayers
   * @param suitableSegments
   * @param commonParent
   * @return
   */
  protected IUndoableOperation getTimedOperation(final Layers theLayers,
      final HasEditables selected, final long interval)
  {
    return new SelectChildrenAtFrequencyOperation(theLayers, selected,
        interval);
  }

  /**
   * put the operation firer onto the undo history. We've refactored this into a separate method so
   * testing classes don't have to simulate the CorePlugin
   *
   * @param operation
   */
  protected void runIt(final IUndoableOperation operation)
  {
    CorePlugin.run(operation);
  }

  private TimePeriod timePeriodFor(final Editable selected)
  {
    TimePeriod res;
    if (selected instanceof TrackSegment)
    {
      final TrackSegment track = (TrackSegment) selected;
      res = new TimePeriod.BaseTimePeriod(track.startDTG(), track.endDTG());
    }
    else
    {
      res = null;
    }

    return res;
  }

}
