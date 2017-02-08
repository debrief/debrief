package org.mwc.debrief.core.ContextOperations;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.core.editors.PlotOutlinePage;

import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.TimePeriod;

public class SelectCutsForThisTMASegment extends ShowCutsForThisTMASegment
{
  private static class SelectCutsOperation extends CMAPOperation
  {
    final private Map<SensorWrapper, ArrayList<TimePeriod>> _periods;
    final private Layers _theLayers;

    public SelectCutsOperation(Layers theLayers,
        Map<SensorWrapper, ArrayList<TimePeriod>> periods)
    {
      super("Select sensor cuts for selected segment(s)");
      _periods = periods;
      _theLayers = theLayers;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      Set<SensorWrapper> sensors = _periods.keySet();

      List<EditableWrapper> selection = new ArrayList<EditableWrapper>();

      // start off by hiding all cuts, for all sensors (not just selected ones)
      Iterator<SensorWrapper> sIter = sensors.iterator();
      while (sIter.hasNext())
      {
        SensorWrapper sensorWrapper = (SensorWrapper) sIter.next();

        // get the data
        ArrayList<TimePeriod> list = _periods.get(sensorWrapper);

        Enumeration<Editable> cIter = sensorWrapper.elements();
        while (cIter.hasMoreElements())
        {
          SensorContactWrapper thisS =
              (SensorContactWrapper) cIter.nextElement();

          // loop through the periods
          Iterator<TimePeriod> pIter = list.iterator();
          while (pIter.hasNext())
          {
            if (pIter.next().contains(thisS.getDTG()))
            {
              final EditableWrapper track =
                  new EditableWrapper(thisS.getSensor().getHost(), null, _theLayers);
              final EditableWrapper parentP =
                  new EditableWrapper(thisS.getSensor(), track, _theLayers);
              final EditableWrapper wrapped =
                  new EditableWrapper(thisS, parentP, _theLayers);

              selection.add(wrapped);
            }
          }
        }
      }

      if (selection.size() > 0)
      {
        // ok, get the editor
        final IWorkbench wb = PlatformUI.getWorkbench();
        final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        final IWorkbenchPage page = win.getActivePage();
        final IEditorPart editor = page.getActiveEditor();

        IContentOutlinePage outline =
            (IContentOutlinePage) editor.getAdapter(IContentOutlinePage.class);
        if (outline != null)
        {
          // now set the selection
          IStructuredSelection str = new StructuredSelection(selection);

          outline.setSelection(str);

          // see uf we can expand the selection
          if (outline instanceof PlotOutlinePage)
          {
            PlotOutlinePage plotOutline = (PlotOutlinePage) outline;
            EditableWrapper ew = (EditableWrapper) str.getFirstElement();
            plotOutline.editableSelected(str, ew);
          }
        }
      }

      return Status.OK_STATUS;
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

  protected String getTitlePrefix()
  {
    return "Select sensor cuts for selected ";
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
  protected IUndoableOperation getOperation(Layers theLayers,
      Map<SensorWrapper, ArrayList<TimePeriod>> periods)
  {
    return new SelectCutsOperation(theLayers, periods);
  }
}
