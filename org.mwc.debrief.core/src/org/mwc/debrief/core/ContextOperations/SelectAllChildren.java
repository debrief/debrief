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

import Debrief.Wrappers.TacticalDataWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * @author ian.mayo
 * 
 */
public class SelectAllChildren implements RightClickContextItemGenerator
{
  private static class SelectChildrenOperation extends CMAPOperation
  {
    final private HasEditables _hasEditables;
    final private Layers _theLayers;

    public SelectChildrenOperation(Layers theLayers, HasEditables selected)
    {
      super("Select all children");
      _hasEditables = selected;
      _theLayers = theLayers;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      List<EditableWrapper> selection = itemsFor(_hasEditables);

      if (selection != null && selection.size() > 0)
      {
        // ok, get the editor
        final IWorkbench wb = PlatformUI.getWorkbench();
        final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        final IWorkbenchPage page = win.getActivePage();
        final IEditorPart editor = page.getActiveEditor();
        
        if (editor != null)
        {
          IContentOutlinePage outline =
              (IContentOutlinePage) editor
                  .getAdapter(IContentOutlinePage.class);
          if (outline != null)
          {
            // now set the selection
            IStructuredSelection str = new StructuredSelection(selection);

            outline.setSelection(str);

            // see uf we can expand the selection
            // if (outline instanceof PlotOutlinePage)
            // {
            // PlotOutlinePage plotOutline = (PlotOutlinePage) outline;
            // EditableWrapper ew = (EditableWrapper) str.getFirstElement();
            // plotOutline.editableSelected(str, ew);
            // }
          }
        }
      }

      return Status.OK_STATUS;
    }

    private List<EditableWrapper> itemsFor(HasEditables parent)
    {
      final List<EditableWrapper> res;
      if (parent instanceof TacticalDataWrapper)
      {
        TacticalDataWrapper wrap = (TacticalDataWrapper) parent;
        EditableWrapper track =
            new EditableWrapper(wrap.getHost(), null, _theLayers);
        EditableWrapper thisList = new EditableWrapper(wrap, track, _theLayers);

        res = new ArrayList<EditableWrapper>();

        Enumeration<Editable> items = wrap.elements();
        while (items.hasMoreElements())
        {
          Editable item = items.nextElement();
          res.add(new EditableWrapper(item, thisList, _theLayers));
        }
      }
      else if (parent instanceof BaseLayer)
      {
        BaseLayer wrap = (BaseLayer) parent;
        EditableWrapper track = new EditableWrapper(wrap, null, _theLayers);
        EditableWrapper thisList = new EditableWrapper(wrap, track, _theLayers);

        res = new ArrayList<EditableWrapper>();

        Enumeration<Editable> items = wrap.elements();
        while (items.hasMoreElements())
        {
          Editable item = items.nextElement();
          res.add(new EditableWrapper(item, thisList, _theLayers));
        }
      }
      else if (parent instanceof TrackSegment)
      {
        TrackSegment wrap = (TrackSegment) parent;
        EditableWrapper track =
            new EditableWrapper(wrap.getWrapper(), null, _theLayers);
        EditableWrapper thisList = new EditableWrapper(wrap, track, _theLayers);

        res = new ArrayList<EditableWrapper>();

        Enumeration<Editable> items = wrap.elements();
        while (items.hasMoreElements())
        {
          Editable item = items.nextElement();
          res.add(new EditableWrapper(item, thisList, _theLayers));
        }
      }
      else if (parent instanceof SegmentList)
      {
        SegmentList wrap = (SegmentList) parent;
        EditableWrapper track =
            new EditableWrapper(wrap.getWrapper(), null, _theLayers);
        EditableWrapper segList =
            new EditableWrapper(wrap.getWrapper().getSegments(), track,
                _theLayers);
        EditableWrapper thisList =
            new EditableWrapper(wrap, segList, _theLayers);

        res = new ArrayList<EditableWrapper>();

        Enumeration<Editable> items = wrap.elements();
        while (items.hasMoreElements())
        {
          Editable item = items.nextElement();
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
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      return Status.OK_STATUS;
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

  }

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

      Editable selected = subjects[0];

      // does it have any children?
      if (selected instanceof HasEditables)
      {
        // ok, generate the operation
        final IUndoableOperation action =
            getOperation(theLayers, (HasEditables) selected);

        // and now wrap it in an action
        final Action doIt = new Action("Select all child elements")
        {
          @Override
          public void run()
          {
            runIt(action);
          }
        };
        doIt.setImageDescriptor(DebriefPlugin
        .getImageDescriptor("icons/16/show.png"));

        // ok, go for it
        parent.add(doIt);
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
   * put the operation firer onto the undo history. We've refactored this into a separate method so
   * testing classes don't have to simulate the CorePlugin
   * 
   * @param operation
   */
  protected void runIt(final IUndoableOperation operation)
  {
    CorePlugin.run(operation);
  }

}
