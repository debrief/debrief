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
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesDatasetDouble;
import Debrief.Wrappers.Extensions.Measurements.Wrappers.DatasetWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

public class MeasuredDataOperations implements RightClickContextItemGenerator
{

  protected static class DatasetsOperation extends CMAPOperation
  {

    final private Calculate _operation;
    final private List<TimeSeriesDatasetDouble> _items;
    private TimeSeriesCore _newData;
    private DataFolder _target;
    final Layers _theLayers;
    private String _units;
    private List<Layer> _fParents;

    public DatasetsOperation(String title, Calculate operation,
        List<TimeSeriesDatasetDouble> fWrappers, List<Layer> fParents,
        Layers theLayers, final String units)
    {
      super(title);
      _operation = operation;
      _items = fWrappers;
      _theLayers = theLayers;
      _units = units;
      _fParents = fParents;
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

    public TimeSeriesCore calculate(Calculate _operation,
        List<TimeSeriesDatasetDouble> items)
    {
      // perform the calculation
      DoubleDataset dResult = _operation.calculate(items);

      // put the times back in
      AxesMetadata times =
          items.get(0).getDataset().getFirstMetadata(AxesMetadata.class);
      dResult.addMetadata(times);

      TimeSeriesDatasetDouble res =
          new TimeSeriesDatasetDouble(dResult, _units);

      // wrap it

      return res;
    }

    private DataFolder getTarget()
    {
      DataFolder target = _items.get(0).getParent();

      if (_items.size() == 2)
      {
        DataFolder folder2 = _items.get(1).getParent();

        if (target.equals(folder2))
        {
          // ok, keep the target, they're both in
          // the same directory
        }
        else
        {
          // in different folders, move up a level
          if (target.getParent() != null)
          {
            target = target.getParent();
          }
          else
          {
            // ok, no parent. keep it in this folder
          }
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
      // how many parents
      Layer singleLayer = null;
      for (Layer thisLayer : _fParents)
      {
        if (singleLayer == null)
        {
          singleLayer = thisLayer;
        }
        else if (thisLayer != singleLayer)
        {
          singleLayer = null;
          break;
        }
      }

      _theLayers.fireExtended(null, singleLayer);
    }
  }

  /**
   * perform operation on the set of time series datasets
   * 
   * @author ian
   * 
   */
  private interface Calculate
  {
    DoubleDataset calculate(List<TimeSeriesDatasetDouble> items);
  }

  /**
   * operate on two datasets
   * 
   * @author ian
   * 
   */
  abstract private class Operation2 implements Calculate
  {
    /**
     * do calculation on these two datasets
     * 
     * @param val1
     * @param val2
     * @return
     */
    abstract DoubleDataset calc(DoubleDataset val1, DoubleDataset val2);

    abstract String nameFor(String one, String two);

    private String nameFor(TimeSeriesCore one, TimeSeriesCore two)
    {
      // are they in the same folder?
      final String sOne;
      final String sTwo;
      if (one.getParent().equals(two.getParent()))
      {
        // ok, in the same folder, we don't need more metadata
        sOne = one.getName();
        sTwo = two.getName();
      }
      else
      {
        sOne = one.getPath();
        sTwo = two.getPath();
      }

      return nameFor(sOne, sTwo);
    }

    @Override
    public DoubleDataset calculate(List<TimeSeriesDatasetDouble> items)
    {
      final TimeSeriesDatasetDouble ts1 = items.get(0);
      final TimeSeriesDatasetDouble ts2 = items.get(1);
      final DoubleDataset d1 = (DoubleDataset) ts1.getDataset();
      final DoubleDataset d2 = (DoubleDataset) ts2.getDataset();

      final DoubleDataset first;
      final DoubleDataset second;

      if (d1.getSize() == d2.getSize())
      {
        first = d1;
        second = d2;
      }
      else
      {
        // ok, do all the interpolation processing
        first = null;
        second = null;
      }

      final DoubleDataset res;
      if (first != null)
      {
        // get the new dataset
        res = calc(first, second);

        // and the name
        res.setName(nameFor(ts1, ts2));
      }
      else
      {
        res = null;
      }
      return res;
    }

  }

  /**
   * operate on a single dataset
   * 
   * @author ian
   * 
   */
  abstract private class Operation1 implements Calculate
  {

    @Override
    public DoubleDataset calculate(List<TimeSeriesDatasetDouble> items)
    {
      DoubleDataset d1 = (DoubleDataset) items.get(0).getDataset();
      DoubleDataset res = calc(d1);
      return res;
    }

    /**
     * do a calculation with a single dataset
     * 
     * @param val1
     * @return
     */
    abstract DoubleDataset calc(DoubleDataset val1);
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
        Operation2 doAdd = new Operation2()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1, DoubleDataset val2)
          {
            final DoubleDataset res =
                (DoubleDataset) Maths.add(val1, val2, null);
            return res;
          }

          @Override
          String nameFor(String one, String two)
          {
            return "Sum of " + one + " and " + two;
          }
        };
        items
            .add(new DoAction("Add datasets", new DatasetsOperation("Do add",
                doAdd, fWrappers, fParents, theLayers, fWrappers.get(0)
                    .getUnits())));

        Operation2 doSubtract = new Operation2()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1, DoubleDataset val2)
          {
            final DoubleDataset res =
                (DoubleDataset) Maths.subtract(val1, val2, null);
            return res;
          }

          @Override
          String nameFor(String one, String two)
          {
            return one + " minus " + two;
          }
        };
        items.add(new DoAction("Subtract datasets", new DatasetsOperation(
            "Do add", doSubtract, fWrappers, fParents, theLayers, fWrappers
                .get(0).getUnits())));

        // multiply and divide
        Operation2 doMultiply = new Operation2()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1, DoubleDataset val2)
          {
            final DoubleDataset res =
                (DoubleDataset) Maths.multiply(val1, val2, null);
            return res;
          }

          @Override
          String nameFor(String one, String two)
          {
            return "Product of " + one + " and " + two;
          }
        };
        items.add(new DoAction("Multiply datasets", new DatasetsOperation(
            "Do add", doMultiply, fWrappers, fParents, theLayers, fWrappers
                .get(0).getUnits()
                + "x" + fWrappers.get(1).getUnits())));

        // multiply and divide
        Operation2 doDivide = new Operation2()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1, DoubleDataset val2)
          {
            final DoubleDataset res =
                (DoubleDataset) Maths.divide(val1, val2, null);
            return res;
          }

          @Override
          String nameFor(String one, String two)
          {
            return one + " / " + two;
          }
        };
        items.add(new DoAction("Divide datasets", new DatasetsOperation(
            "Do add", doDivide, fWrappers, fParents, theLayers, fWrappers
                .get(0).getUnits()
                + "/" + fWrappers.get(1).getUnits())));

      }
      // ok, let's have a go.
      if (fWrappers.size() == 1)
      {
        // abs
        Operation1 doAbs = new Operation1()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1)
          {
            final DoubleDataset res = (DoubleDataset) Maths.abs(val1, null);
            res.setName("Absolute of " + val1.getName());
            return res;
          }
        };
        items.add(new DoAction("Calculate Absolute", new DatasetsOperation(
            "Do add", doAbs, fWrappers, fParents, theLayers, fWrappers.get(0)
                .getUnits())));

        // inv
        Operation1 doInverse = new Operation1()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1)
          {
            final DoubleDataset res =
                (DoubleDataset) Maths.divide(1, val1, null);
            res.setName("Inverse of " + val1.getName());
            return res;
          }
        };
        items.add(new DoAction("Calculate inverse", new DatasetsOperation(
            "Do add", doInverse, fWrappers, fParents, theLayers, "1 / "
                + fWrappers.get(0).getUnits())));

        // sqrt
        Operation1 doSqrt = new Operation1()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1)
          {
            final DoubleDataset res = (DoubleDataset) Maths.sqrt(val1, null);
            res.setName("Square root of " + val1.getName());
            return res;
          }
        };
        items.add(new DoAction("Calculate square root", new DatasetsOperation(
            "Do add", doSqrt, fWrappers, fParents, theLayers, "sqrt "
                + fWrappers.get(0).getUnits())));

        // sqrt
        Operation1 doCbrt = new Operation1()
        {
          @Override
          public DoubleDataset calc(DoubleDataset val1)
          {
            final DoubleDataset res = (DoubleDataset) Maths.cbrt(val1, null);
            res.setName("Cube Root of " + val1.getName());
            return res;
          }
        };
        items.add(new DoAction("Calculate cube root", new DatasetsOperation(
            "Do add", doCbrt, fWrappers, fParents, theLayers, "cbrt "
                + fWrappers.get(0).getUnits())));
      }

      // create any?
      if (!items.isEmpty())
      {
        // marker, to separate the hard sums
        parent.add(new Separator());

        // ok, put new items into a child menu
        MenuManager childMenu = new MenuManager("Calculations");
        parent.add(childMenu);
        
        // and add them all

        for (IAction item : items)
        {
          childMenu.add(item);
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
