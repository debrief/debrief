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
package info.limpet.operations.admin;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.arithmetic.BinaryQuantityOperation;
import info.limpet.operations.arithmetic.InterpolatedMaths;
import info.limpet.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class CreateNewIndexedDatafileOperation extends BinaryQuantityOperation
{

  public class NewIndexedDatasetCommand extends BinaryQuantityCommand
  {
    public NewIndexedDatasetCommand(final String name,
        final List<IStoreItem> selection, final IStoreGroup store,
        final IContext context)
    {
      this(name, selection, store, null, context);
    }

    public NewIndexedDatasetCommand(final String name,
        final List<IStoreItem> selection, final IStoreGroup destination,
        final IDocument<?> timeProvider, final IContext context)
    {
      super(name, "Reindex dataset", destination, false, false, selection,
          timeProvider, context);
    }

    @Override
    protected void assignOutputIndices(final IDataset output,
        final Dataset outputIndices)
    {
      // ok, we don't do this, we want to take charge of the output indices
    }

    @Override
    protected String getBinaryNameFor(final String name1, final String name2)
    {
      return "Composite of " + name1 + " and " + name2;
    }

    @Override
    protected Unit<?> getBinaryOutputUnit(final Unit<?> first,
        final Unit<?> second)
    {
      // return product of units
      return first.times(second);
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
          // ok, we're going to use dataset a as the new index units,
          // so we've just got to set them in a copy of b
          final Dataset output = b.clone();

          // clear any existing metadata
          output.clearMetadata(AxesMetadata.class);

          // now store the new metadata
          final AxesMetadata am = new AxesMetadataImpl();
          am.initialize(1);
          am.setAxis(0, a);
          output.addMetadata(am);

          return output;
        }
      };
    }

    @Override
    protected Unit<?> getUnits()
    {
      // ok, now set the index units
      final NumberDocument index = (NumberDocument) getInputs().get(1);
      return index.getUnits();
    }

    @Override
    protected void tidyOutput(final NumberDocument output)
    {
      super.tidyOutput(output);

      // ok, now set the index units
      final NumberDocument index = (NumberDocument) getInputs().get(0);
      final Unit<?> indUnits = index.getUnits();

      // and store them
      output.setIndexUnits(indUnits);
    }
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      // aah, what about temporal (interpolated) values?
      addInterpolatedCommands(selection, destination, res, context);
    }
    return res;
  }

  @Override
  protected void addIndexedCommands(final List<IStoreItem> selection,
      final IStoreGroup destination, final Collection<ICommand> res,
      final IContext context)
  {
    throw new RuntimeException(
        "This operation doesn't support indexed operations");
  }

  @Override
  protected void addInterpolatedCommands(final List<IStoreItem> selection,
      final IStoreGroup destination, final Collection<ICommand> res,
      final IContext context)
  {
    final IDocument<?> longest = getLongestIndexedCollection(selection);

    if (longest != null)
    {
      final NumberDocument coll1 = (NumberDocument) selection.get(0);
      final NumberDocument coll2 = (NumberDocument) selection.get(1);

      ICommand newC =
          new NewIndexedDatasetCommand("Create new document, indexed on:"
              + coll1, selection, destination, longest, context);
      res.add(newC);

      final ArrayList<IStoreItem> newSel = new ArrayList<IStoreItem>(selection);
      Collections.reverse(newSel);

      // ok, now reverse the selection
      newC =
          new NewIndexedDatasetCommand("Create new document, indexed on:"
              + coll2, newSel, destination, longest, context);
      res.add(newC);

    }
  }

  @Override
  protected boolean appliesTo(final List<IStoreItem> selection)
  {
    final boolean nonEmpty = getATests().nonEmpty(selection);
    final boolean correctNum = getATests().exactNumber(selection, 2);
    final boolean allQuantity = getATests().allQuantity(selection);
    final boolean commonIndex = getATests().allEqualIndexed(selection);

    return nonEmpty && correctNum && allQuantity && commonIndex;
  }

}
