/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.operations.arithmetic.simple;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.operations.arithmetic.BulkQuantityOperation;
import info.limpet.operations.arithmetic.InterpolatedMaths;
import info.limpet.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.Collection;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.Maths;

public class AddQuantityOperation extends BulkQuantityOperation
{

  public class AddQuantityValues extends BulkQuantityCommand
  {
    final private String outputSuffix;
    final private IOperationPerformer operation;

    public AddQuantityValues(final String name,
        final List<IStoreItem> selection, final IStoreGroup destination,
        final IContext context, final String outputPrefix,
        final IOperationPerformer operation)
    {
      super(name, "Add datasets", destination, false, false, selection, context);
      this.outputSuffix = outputPrefix;
      this.operation = operation;
    }

    @Override
    protected IOperationPerformer getOperation()
    {
      return operation;
    }

    @Override
    protected String getBulkNameFor(List<IStoreItem> items)
    {
      String res = "";
      for (IStoreItem item : items)
      {
        if (!"".equals(res))
        {
          res += " + ";
        }
        res += item.getName();
      }

      if (outputSuffix != null && outputSuffix.length() > 0)
      {
        res += " (" + outputSuffix + ")";
      }

      return res;
    }

    @Override
    protected Unit<?> getBulkOutputUnit(List<Unit<?>> units)
    {
      return units.get(0);
    }

    @Override
    protected DoubleDataset getInitial(int shape)
    {
      return DatasetFactory.zeros(shape);
    }
  }

  private static class LogAdder implements
      InterpolatedMaths.IOperationPerformer
  {
    @Override
    public Dataset perform(final Dataset a, final Dataset b, final Dataset o)
    {
      // ok, convert them to alog
      final Dataset aNon = toNonLog(a);
      final Dataset bNon = toNonLog(b);
      final Dataset sum = Maths.add(aNon, bNon);
      final Dataset res = toLog(sum);
      return res;
    }

    private Dataset toLog(final Dataset sum)
    {
      final Dataset log10 = Maths.log10(sum);
      final Dataset times10 = Maths.multiply(log10, 10);
      return times10;
    }

    private Dataset toNonLog(final Dataset d)
    {
      final Dataset div10 = Maths.divide(d, 10);
      final Dataset raised = Maths.power(10, div10);
      return raised;
    }
  }

  private static class PowerAdder implements
      InterpolatedMaths.IOperationPerformer
  {
    @Override
    public Dataset perform(final Dataset a, final Dataset b, final Dataset o)
    {
      final Dataset res = Maths.add(a, b, o);
      return res;
    }
  };

  @Override
  protected void addIndexedCommands(final List<IStoreItem> selection,
      final IStoreGroup destination, final Collection<ICommand> res,
      final IContext context)
  {
    final PowerAdder powerAdder = new PowerAdder();

    if (hasLogData(selection))
    {
      // ok, we need to offer log and non-log operations
      final LogAdder logAdder = new LogAdder();

      // just offer the log operation
      ICommand newC =
          new AddQuantityValues(
              "Logarithmic Add for provided series (indexed)", selection,
              destination, context, "Log ", logAdder);
      res.add(newC);

      // just offer the log operation
      newC =
          new AddQuantityValues("Power Add for provided series (indexed)",
              selection, destination, context, "Power ", powerAdder);
      res.add(newC);

    }
    else
    {
      // just offer the log operation
      final ICommand newC =
          new AddQuantityValues(
              "Add numeric values in provided series (indexed)", selection,
              destination, context, "", powerAdder);
      res.add(newC);
    }

  };

  @Override
  protected void addInterpolatedCommands(final List<IStoreItem> selection,
      final IStoreGroup destination, final Collection<ICommand> res,
      final IContext context)
  {
    final PowerAdder powerAdder = new PowerAdder();

    if (hasLogData(selection))
    {
      final LogAdder logAdder = new LogAdder();

      ICommand newC =
          new AddQuantityValues(
              "Logarithmic Add for provided series (interpolated)", selection,
              destination, context, "Log ", logAdder);
      res.add(newC);

      newC =
          new AddQuantityValues("Power Add for provided series (interpolated)",
              selection, destination, context, "Power ", powerAdder);
      res.add(newC);

    }
    else
    {
      final ICommand newC =
          new AddQuantityValues(
              "Add numeric values in provided series (interpolated)",
              selection, destination, context, "", powerAdder);
      res.add(newC);
    }
  }

  @Override
  protected boolean appliesTo(final List<IStoreItem> selection)
  {
    final boolean atLeastTwoItems = selection.size() >= 2;
    final boolean nonEmpty = getATests().nonEmpty(selection);
    final boolean allQuantity = getATests().allQuantity(selection);
    final boolean suitableLength =
        getATests().allEqualIndexedOrSingleton(selection)
            || getATests().allEqualLengthOrSingleton(selection);
    final boolean equalDimensions = getATests().allEqualDimensions(selection);
    final boolean equalUnits = getATests().allEqualUnits(selection);

    return atLeastTwoItems && nonEmpty && allQuantity && suitableLength
        && equalDimensions && equalUnits;
  }

}
