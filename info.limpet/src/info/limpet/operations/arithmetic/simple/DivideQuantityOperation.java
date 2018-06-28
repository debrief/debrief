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
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.operations.arithmetic.BinaryQuantityOperation;
import info.limpet.operations.arithmetic.InterpolatedMaths;
import info.limpet.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.Collection;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.Maths;

public class DivideQuantityOperation extends BinaryQuantityOperation
{

  public class DivideQuantityValues extends BinaryQuantityCommand
  {
    public DivideQuantityValues(final String name,
        final List<IStoreItem> selection, final IStoreGroup store,
        final IContext context)
    {
      this(name, selection, store, null, context);
    }

    public DivideQuantityValues(final String name,
        final List<IStoreItem> selection, final IStoreGroup destination,
        final IDocument<?> timeProvider, final IContext context)
    {
      super(name, "Divide datasets", destination, false, false, selection,
          timeProvider, context);
    }

    @Override
    protected String getBinaryNameFor(final String name1, final String name2)
    {
      return "Quotient of " + name1 + " + " + name2;
    }

    @Override
    protected Unit<?> getBinaryOutputUnit(final Unit<?> first,
        final Unit<?> second)
    {
      // return product of units
      return first.divide(second);
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
          return Maths.divide(a, b, o);
        }
      };
    }
  }

  @Override
  protected void addIndexedCommands(final List<IStoreItem> selection,
      final IStoreGroup destination, final Collection<ICommand> res,
      final IContext context)
  {
    final IStoreItem doc1 = selection.get(0);
    final IStoreItem doc2 = selection.get(1);
    ICommand newC =
        new DivideQuantityValues("Divide " + doc1 + " by " + doc2
            + " (indexed)", selection, destination, context);
    res.add(newC);
    newC =
        new DivideQuantityValues("Divide " + doc2 + " by " + doc1
            + " (indexed)", reverse(selection), destination, context);
    res.add(newC);
  }

  @Override
  protected void addInterpolatedCommands(final List<IStoreItem> selection,
      final IStoreGroup destination, final Collection<ICommand> res,
      final IContext context)
  {
    final IDocument<?> longest = getLongestIndexedCollection(selection);

    if (longest != null)
    {
      final IStoreItem doc1 = selection.get(0);
      final IStoreItem doc2 = selection.get(1);

      ICommand newC =
          new DivideQuantityValues("Divide " + doc1 + " by " + doc2
              + " (interpolated)", selection, destination, longest, context);

      res.add(newC);

      // provide a reversed list
      newC =
          new DivideQuantityValues("Divide " + doc2 + " by " + doc1
              + " (interpolated)", reverse(selection), destination, longest,
              context);

      res.add(newC);

    }
  }

  @Override
  protected boolean appliesTo(final List<IStoreItem> selection)
  {
    final boolean nonEmpty = getATests().nonEmpty(selection);
    final boolean allQuantity = getATests().allQuantity(selection);
    final boolean twoItems = selection.size() == 2;
    final boolean suitableLength =
        getATests().allEqualIndexed(selection)
            || getATests().allEqualLengthOrSingleton(selection);

    return nonEmpty && allQuantity && suitableLength && twoItems;
  }

}
