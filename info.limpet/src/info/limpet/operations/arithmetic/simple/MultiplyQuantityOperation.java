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

public class MultiplyQuantityOperation extends BulkQuantityOperation
{

  public class MultiplyQuantityValues extends BulkQuantityCommand
  {
    public MultiplyQuantityValues(final String name,
        final List<IStoreItem> selection, final IStoreGroup destination,
        final IContext context)
    {
      super(name, "Multiply datasets", destination, false, false, selection,
          context);
    }

    @Override
    protected String getBulkNameFor(List<IStoreItem> items)
    {
      String res = "";
      for (IStoreItem item : items)
      {
        if (!"".equals(res))
        {
          res += " * ";
        }
        res += item.getName();
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
      return DatasetFactory.ones(shape);
    }

    @Override
    protected IOperationPerformer getOperation()
    {
      return new InterpolatedMaths.IOperationPerformer()
      {
        @Override
        public Dataset
            perform(final Dataset a, final Dataset b, final Dataset o)
        {
          final Dataset res = Maths.multiply(a, b, o);
          return res;
        }
      };
    }
  }

  @Override
  protected void addIndexedCommands(final List<IStoreItem> selection,
      final IStoreGroup destination, final Collection<ICommand> res,
      final IContext context)
  {
    final ICommand newC =
        new MultiplyQuantityValues(
            "Multiply numeric values in provided series (indexed)", selection,
            destination, context);
    res.add(newC);
  }

  @Override
  protected void addInterpolatedCommands(final List<IStoreItem> selection,
      final IStoreGroup destination, final Collection<ICommand> res,
      final IContext context)
  {
    final ICommand newC =
        new MultiplyQuantityValues(
            "Multiply numeric values in provided series (interpolated)",
            selection, destination, context);
    res.add(newC);
  }

  @Override
  protected boolean appliesTo(final List<IStoreItem> selection)
  {
    final boolean nonEmpty = getATests().nonEmpty(selection);
    final boolean allQuantity = getATests().allQuantity(selection);
    final boolean suitableLength =
        getATests().allEqualIndexed(selection)
            || getATests().allEqualLengthOrSingleton(selection);
    final boolean hasMoreThanOne = selection.size() > 1;

    return nonEmpty && allQuantity && suitableLength && hasMoreThanOne;
  }

}
