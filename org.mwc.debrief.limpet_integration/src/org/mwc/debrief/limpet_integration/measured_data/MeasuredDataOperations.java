package org.mwc.debrief.limpet_integration.measured_data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.providers.measured_data.DatasetWrapper;

import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

public class MeasuredDataOperations implements RightClickContextItemGenerator
{

  protected interface CalcOperation
  {
    /**
     * perform a calculation
     * 
     * @param items
     * @return
     */
    TimeSeriesCore calculate(List<TimeSeriesDatasetDouble> items);
  }

  protected static class DatasetsOperation extends CMAPOperation
  {

    final private CalcOperation _operation;
    final private List<TimeSeriesDatasetDouble> _items;
    private TimeSeriesCore _newData;
    private DataFolder _target;

    public DatasetsOperation(String title, CalcOperation operation,
        List<TimeSeriesDatasetDouble> fWrappers)
    {
      super(title);
      _operation = operation;
      _items = fWrappers;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      // sort out the destination
      _target = getTarget();

      // calculate the dataset
      _newData = _operation.calculate(_items);

      // and store it
      _target.add(_newData);

      return Status.OK_STATUS;
    }

    private DataFolder getTarget()
    {
      // look at the first item
      TimeSeriesCore first = _items.get(0);

      DataFolder target = first.getParent();

      return target;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      // ok, delete the dataset
      _target.remove(_newData);

      return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      // ok, put the dataset back into the parent
      _target.add(_newData);

      return Status.OK_STATUS;
    }
  }

  private class DoAdd implements CalcOperation
  {

    @Override
    public TimeSeriesCore calculate(List<TimeSeriesDatasetDouble> items)
    {
      long[] times = new long[]
      {1000, 2000, 3000};
      double[] values = new double[]
      {44, 55, 66};
      
      // can't see org.eclipse.january.dataset.  I'm sure it's shared from org.mwc.debrief.legacy
      // Dataset d1 = items.get(0).getDataset();
      // Dataset d2 = items.get(1).getDataset();
      //
      // res = Maths.add(d1, d2, null);
      TimeSeriesDatasetDouble res =
          new TimeSeriesDatasetDouble("sum of other two", "m", times, values);

      return res;
    }

  }

  @Override
  public void generate(IMenuManager parent, Layers theLayers,
      Layer[] parentLayers, Editable[] subjects)
  {

    List<TimeSeriesCore> wrappers = null;

    // ok, let's have a look
    for (int i = 0; i < subjects.length; i++)
    {
      Editable thisE = subjects[i];
      if (thisE instanceof DatasetWrapper)
      {
        DatasetWrapper dw = (DatasetWrapper) thisE;
        TimeSeriesCore core = dw.getDataset();
        if (wrappers == null)
        {
          wrappers = new ArrayList<TimeSeriesCore>();
        }
        wrappers.add(core);
      }
    }

    // success?
    if (wrappers != null)
    {
      List<IAction> items = new ArrayList<IAction>();

      // extract the datasets
      final List<TimeSeriesDatasetDouble> fWrappers =
          new ArrayList<TimeSeriesDatasetDouble>();

      for (TimeSeriesCore dataset : wrappers)
      {
        if (dataset instanceof TimeSeriesDatasetDouble)
        {
          fWrappers.add((TimeSeriesDatasetDouble) dataset);
        }
      }

      // ok, let's have a go.
      if (fWrappers.size() == 2)
      {
        // ok, generate the action
        final Action doMerge = new Action("Add datasets")
        {
          public void run()
          {
            CalcOperation operation = new DoAdd();
            final IUndoableOperation theAction =
                new DatasetsOperation("Do add", operation, fWrappers);

            CorePlugin.run(theAction);
          }
        };
        // easy.
        items.add(doMerge);
      }

      // create any?
      if (!items.isEmpty())
      {

        parent.add(new Separator("Calculations"));
        // and add them all

        for (IAction item : items)
        {
          parent.add(item);
        }
      }
    }

  }

}
