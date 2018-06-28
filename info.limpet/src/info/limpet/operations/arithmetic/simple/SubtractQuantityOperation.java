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

public class SubtractQuantityOperation extends BinaryQuantityOperation
{
  public class SubtractQuantityValues extends BinaryQuantityCommand
  {
    private final IOperationPerformer _performer;

    public SubtractQuantityValues(final String name,
        final List<IStoreItem> selection, final IStoreGroup destination,
        final IDocument<?> timeProvider, final IContext context,
        final IOperationPerformer performer)
    {
      super(name, "Subtract datasets", destination, false, false, selection,
          timeProvider, context);
      _performer = performer;
    }

    @Override
    protected String getBinaryNameFor(final String name1, final String name2)
    {
      return "[" + name1 + "] - [" + name2 + "]";
    }

    @Override
    protected Unit<?> getBinaryOutputUnit(final Unit<?> first,
        final Unit<?> second)
    {
      // Subtraction doesn't modify units, just use first ones
      return first;
    }

    @Override
    protected IOperationPerformer getOperation()
    {
      return _performer;
    }
  }

  private static class PowerPerformer implements
      InterpolatedMaths.IOperationPerformer
  {
    @Override
    public Dataset perform(final Dataset a, final Dataset b, final Dataset o)
    {
      return Maths.subtract(a, b, o);
    }
  };

  private static class LogPerformer implements
      InterpolatedMaths.IOperationPerformer
  {
    @Override
    public Dataset perform(final Dataset a, final Dataset b, final Dataset o)
    {
      // ok, convert them to alog
      final Dataset aNon = toNonLog(a);
      final Dataset bNon = toNonLog(b);

      final Dataset sum = Maths.subtract(aNon, bNon);

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
  };

  @Override
  protected void addIndexedCommands(final List<IStoreItem> selection,
      final IStoreGroup destination, final Collection<ICommand> res,
      final IContext context)
  {
    final IStoreItem doc1 = selection.get(0);
    final IStoreItem doc2 = selection.get(1);

    // this is indexed, so we don't provide a time-provider
    IDocument<?> timeProvider = null;

    final IOperationPerformer powerPerformer = new PowerPerformer();

    if (hasLogData(selection))
    {
      // ok, we need to provide log and power operators
      final IOperationPerformer logPerformer = new LogPerformer();

      ICommand newC =
          new SubtractQuantityValues("Power Subtract " + doc2 + " from " + doc1
              + "(indexed)", selection, destination, timeProvider, context,
              powerPerformer);
      res.add(newC);
      newC =
          new SubtractQuantityValues("Power Subtract " + doc1 + " from " + doc2
              + "(indexed)", reverse(selection), destination, timeProvider,
              context, powerPerformer);
      res.add(newC);

      newC =
          new SubtractQuantityValues("Log Subtract " + doc2 + " from " + doc1
              + "(indexed)", selection, destination, timeProvider, context,
              logPerformer);
      res.add(newC);
      newC =
          new SubtractQuantityValues("Log Subtract " + doc1 + " from " + doc2
              + "(indexed)", reverse(selection), destination, timeProvider,
              context, logPerformer);
      res.add(newC);

    }
    else
    {
      // ok, we don't need to detail them
      ICommand newC =
          new SubtractQuantityValues("Subtract " + doc2 + " from " + doc1
              + "(indexed)", selection, destination, timeProvider, context,
              powerPerformer);
      res.add(newC);
      newC =
          new SubtractQuantityValues("Subtract " + doc1 + " from " + doc2
              + "(indexed)", reverse(selection), destination, timeProvider,
              context, powerPerformer);
      res.add(newC);
    }
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

      final IOperationPerformer powerPerformer = new PowerPerformer();

      if (hasLogData(selection))
      {
        // ok, we need to provide log and power operators
        final IOperationPerformer logPerformer = new LogPerformer();
        ICommand newC =
            new SubtractQuantityValues("Power Subtract " + doc2 + " from "
                + doc1 + "(interpolated)", selection, destination, longest,
                context, powerPerformer);
        res.add(newC);
        newC =
            new SubtractQuantityValues("Power Subtract " + doc1 + " from "
                + doc2 + "(interpolated)", reverse(selection), destination,
                longest, context, powerPerformer);
        res.add(newC);

        // and log
        newC =
            new SubtractQuantityValues("Log Subtract " + doc2 + " from " + doc1
                + "(interpolated)", selection, destination, longest, context,
                logPerformer);
        res.add(newC);
        newC =
            new SubtractQuantityValues("Log Subtract " + doc1 + " from " + doc2
                + "(interpolated)", reverse(selection), destination, longest,
                context, logPerformer);
        res.add(newC);
      }
      else
      {
        // easy - just provide the plain operation
        ICommand newC =
            new SubtractQuantityValues("Subtract " + doc2 + " from " + doc1
                + "(interpolated)", selection, destination, longest, context,
                powerPerformer);
        res.add(newC);
        newC =
            new SubtractQuantityValues("Subtract " + doc1 + " from " + doc2
                + "(interpolated)", reverse(selection), destination, longest,
                context, powerPerformer);
        res.add(newC);
      }
    }
  }

  @Override
  protected boolean appliesTo(final List<IStoreItem> selection)
  {
    final boolean nonEmpty = getATests().nonEmpty(selection);
    final boolean allQuantity = getATests().allQuantity(selection);
    final boolean suitableLength =
        getATests().allEqualIndexed(selection)
            || getATests().allEqualLengthOrSingleton(selection);
    final boolean equalDimensions = getATests().allEqualDimensions(selection);
    final boolean equalUnits = getATests().allEqualUnits(selection);

    return nonEmpty && allQuantity && suitableLength && equalDimensions
        && equalUnits;
  }

}
