package org.mwc.debrief.limpet_integration.measured_data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;
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

  protected static class DatasetsOperation extends CMAPOperation
  {

    final private Operation _operation;
    final private List<TimeSeriesDatasetDouble> _items;
    private TimeSeriesCore _newData;
    private DataFolder _target;
    final Layers _theLayers;
    private String _units;

    public DatasetsOperation(String title, Operation operation,
        List<TimeSeriesDatasetDouble> fWrappers, Layers theLayers,
        final String units)
    {
      super(title);
      _operation = operation;
      _items = fWrappers;
      _theLayers = theLayers;
      _units = units;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      // calculate the dataset
      _newData = calculate(_operation, _items);

      if (_newData != null)
      {
        // sort out the destination
        _target = getTarget();

        // and store it
        _target.add(_newData);

        // share the good news
        fireUpdated();

        return Status.OK_STATUS;
      }
      else
      {
        CorePlugin.logError(Status.WARNING,
            "Failed to perform calculation on measured data", null);
        return Status.CANCEL_STATUS;
      }

    }

    public TimeSeriesCore calculate(Operation operation,
        List<TimeSeriesDatasetDouble> items)
    {
      DoubleDataset d1 = (DoubleDataset) items.get(0).getDataset();
      DoubleDataset d2 = (DoubleDataset) items.get(1).getDataset();

      final DoubleDataset first;
      final DoubleDataset second;

      if (d1.getSize() == d2.getSize())
      {
        first = d1;
        second = d2;
      }
      else
      {
        // ok, do all that processing
        first = null;
        second = null;
      }

      // perform the calculation
      DoubleDataset dResult = operation.calc(first, second);

      // put the times back in
      AxesMetadata times = d1.getFirstMetadata(AxesMetadata.class);
      dResult.addMetadata(times);

      TimeSeriesDatasetDouble res =
          new TimeSeriesDatasetDouble(dResult, _units);

      // wrap it

      return res;
    }

    private DataFolder getTarget()
    {
       DataFolder folder1 = _items.get(0).getParent();
      DataFolder folder2 = _items.get(1).getParent();

      final DataFolder target;
      if (folder1.equals(folder2))
      {
        // ok, from same folder cool
        target = folder1;
      }
      else
      {
        // in different folders, move up a level
        if (folder1.getParent() != null)
        {
          target = folder1.getParent();
        }
        else
        {
          // ok, no parent. keep it in this folder
          target = folder1;
        }
      }

      return target;
    }

    @Override
    public boolean canRedo()
    {
      return _newData != null;
    }

    @Override
    public boolean canUndo()
    {
      return _newData != null;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      // ok, delete the dataset
      _target.remove(_newData);

      // share the good news
      fireUpdated();

      return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      // ok, put the dataset back into the parent
      _target.add(_newData);

      // share the good news
      fireUpdated();

      return Status.OK_STATUS;
    }

    void fireUpdated()
    {
      _theLayers.fireExtended(null, null);
    }
  }

  private interface Operation
  {
    DoubleDataset calc(DoubleDataset val1, DoubleDataset val2);
  }

  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {

    List<TimeSeriesCore> timeSeries = null;
    List<Editable> wrappers = null;
    List<Layer> parents = null;

    // ok, let's have a look
    for (int i = 0; i < subjects.length; i++)
    {
      Editable thisE = subjects[i];
      if (thisE instanceof DatasetWrapper)
      {
        DatasetWrapper dw = (DatasetWrapper) thisE;
        TimeSeriesCore core = dw.getDataset();
        if (timeSeries == null)
        {
          timeSeries = new ArrayList<TimeSeriesCore>();
          wrappers = new ArrayList<Editable>();
          parents = new ArrayList<Layer>();
        }
        timeSeries.add(core);
        wrappers.add(dw);
        parents.add(parentLayers[i]);
      }
    }

    // success?
    if (timeSeries != null)
    {
      List<IAction> items = new ArrayList<IAction>();

      // extract the datasets
      final List<TimeSeriesDatasetDouble> fWrappers =
          new ArrayList<TimeSeriesDatasetDouble>();
      final List<Editable> fEditables = new ArrayList<Editable>();
      final List<Layer> fParents = new ArrayList<Layer>();

      for (int i = 0; i < timeSeries.size(); i++)
      {
        TimeSeriesCore dataset = timeSeries.get(i);
        if (dataset instanceof TimeSeriesDatasetDouble)
        {
          fWrappers.add((TimeSeriesDatasetDouble) dataset);
          fEditables.add(wrappers.get(i));
          fParents.add(parents.get(i));
        }
      }

      // ok, let's have a go.
      if (fWrappers.size() == 2)
      {
        // ok, generate addition and subtraction
        Operation doAdd = new Operation()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1, DoubleDataset val2)
          {
            final DoubleDataset res =
                (DoubleDataset) Maths.add(val1, val2, null);
            res.setName("Sum of " + val1.getName() + " and " + val2.getName());
            return res;
          }
        };
        items.add(new DoAction("Add datasets", new DatasetsOperation("Do add",
            doAdd, fWrappers, theLayers, fWrappers.get(0).getUnits())));

        Operation doSubtract = new Operation()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1, DoubleDataset val2)
          {
            final DoubleDataset res =
                (DoubleDataset) Maths.subtract(val1, val2, null);
            res.setName(val1.getName() + " minus " + val2.getName());
            return res;
          }
        };
        items.add(new DoAction("Subtract datasets", new DatasetsOperation(
            "Do add", doSubtract, fWrappers, theLayers, fWrappers.get(0)
                .getUnits())));

        // multiply and divide
        Operation doMultiply = new Operation()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1, DoubleDataset val2)
          {
            final DoubleDataset res =
                (DoubleDataset) Maths.multiply(val1, val2, null);
            res.setName("Product of " + val1.getName() + " and "
                + val2.getName());
            return res;
          }
        };
        items.add(new DoAction("Multiply datasets", new DatasetsOperation(
            "Do add", doMultiply, fWrappers, theLayers, fWrappers.get(0)
                .getUnits()
                + "x" + fWrappers.get(1).getUnits())));

        // multiply and divide
        Operation doDivide = new Operation()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1, DoubleDataset val2)
          {
            final DoubleDataset res =
                (DoubleDataset) Maths.divide(val1, val2, null);
            res.setName(val1.getName() + " / " + val2.getName());
            return res;
          }
        };
        items.add(new DoAction("Divide datasets", new DatasetsOperation(
            "Do add", doDivide, fWrappers, theLayers, fWrappers.get(0)
                .getUnits()
                + "/" + fWrappers.get(1).getUnits())));

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

  /**
   * warp the process of calling an action
   * 
   * @author ian
   * 
   */
  protected class DoAction extends Action
  {
    final private IUndoableOperation _theAction;

    public DoAction(final String title, final IUndoableOperation theAction)
    {
      super(title);
      _theAction = theAction;
    }

    public void run()
    {
      CorePlugin.run(_theAction);
    }
  }

}
