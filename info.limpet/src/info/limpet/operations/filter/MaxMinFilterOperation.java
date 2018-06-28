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
package info.limpet.operations.filter;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

public class MaxMinFilterOperation implements IOperation
{
  public static class FilterCollectionCommand extends AbstractCommand
  {

    final private FilterOperation operation;

    final private NumberDocument filterValue;

    public FilterCollectionCommand(final String title,
        final List<IStoreItem> selection, final IStoreGroup store,
        final IContext context, final FilterOperation operation,
        final NumberDocument filterValue)
    {
      super(title, "Filter documents", store, false, false, selection, context);
      this.operation = operation;
      this.filterValue = filterValue;
    }

    @Override
    public void execute()
    {
      // tell each series that we're a dependent
      for (final IStoreItem t : getInputs())
      {
        t.addChangeListener(this);
      }

      // create a filtered set of inputs
      final List<IStoreItem> filteredInputs =
          getFilteredInputs(getInputs(), filterValue);

      // create the outputs
      for (final IStoreItem t : filteredInputs)
      {
        final NumberDocument thisN = (NumberDocument) t;
        final NumberDocument thisO =
            new NumberDocument(null, this, thisN.getUnits());
        this.addOutput(thisO);
        t.addChangeListener(this);
      }

      // ok, go for it
      performCalc();

      // specify the index units
      final Iterator<IStoreItem> iIter = filteredInputs.iterator();
      final Iterator<Document<?>> oIter = getOutputs().iterator();
      while (iIter.hasNext())
      {
        final NumberDocument thisI = (NumberDocument) iIter.next();
        final NumberDocument thisO = (NumberDocument) oIter.next();

        final Unit<?> inUnits = thisI.getIndexUnits();
        if (inUnits != null)
        {
          thisO.setIndexUnits(thisI.getIndexUnits());
        }

        // and the output name
        thisO.setName(nameFor(thisI));
      }

      // ok, put the outputs into the store
      for (final Document<?> out : getOutputs())
      {
        getStore().add(out);
        out.fireDataChanged();
      }
    }

    protected String nameFor(final NumberDocument input)
    {
      return operation.nameFor(input.getName(), (int) this.filterValue
          .getValue());
    }

    private void performCalc()
    {
      // ok, loop through the inputs
      final Iterator<IStoreItem> inpIter =
          getFilteredInputs(getInputs(), filterValue).iterator();
      final Iterator<Document<?>> outIter = getOutputs().iterator();

      while (inpIter.hasNext())
      {
        final NumberDocument thisIn = (NumberDocument) inpIter.next();
        final NumberDocument thisOut = (NumberDocument) outIter.next();

        final String outName = thisOut.getName();

        final List<Double> vOut = new ArrayList<Double>();
        final List<Double> iOut = new ArrayList<Double>();

        // loop through the values
        final Iterator<Double> vIter = thisIn.getIterator();

        final Iterator<Double> iIter;
        if (thisIn.isIndexed())
        {
          iIter = thisIn.getIndexIterator();
        }
        else
        {
          iIter = null;
        }

        while (vIter.hasNext() && (iIter == null || iIter.hasNext()))
        {
          final double thisValue = vIter.next();

          if (iIter != null)
          {
            final double thisIndex = iIter.next();

            if (operation.keep(thisIndex, thisValue, this.filterValue
                .getValue()))
            {
              vOut.add(thisValue);
              iOut.add(thisIndex);
            }
          }
          else
          {
            if (operation.keep(thisValue, this.filterValue.getValue()))
            {
              vOut.add(thisValue);
            }
          }
        }

        // ok, handle a zero length list

        final DoubleDataset outD;
        final DoubleDataset outIndex;

        // did we find any?
        if (vOut.size() > 0)
        {
          // yes.
          outD = (DoubleDataset) DatasetFactory.createFromObject(vOut);

          // is this dataset indexed?
          if (iIter != null)
          {
            outIndex = (DoubleDataset) DatasetFactory.createFromObject(iOut);
          }
          else
          {
            outIndex = null;
          }
        }
        else
        {
          // no, didn't create any. Generate empty list
          final List<Double> dList = new ArrayList<Double>();
          outD = DatasetFactory.createFromList(DoubleDataset.class, dList);
          if (iIter != null)
          {
            outIndex =
                DatasetFactory.createFromList(DoubleDataset.class, dList);
          }
          else
          {
            outIndex = null;
          }
        }

        // do we have any index data?
        if (outIndex != null)
        {
          final AxesMetadata am = new AxesMetadataImpl();
          am.initialize(1);
          am.setAxis(0, outIndex);
          outD.addMetadata(am);
        }

        // and provide the existing name
        outD.setName(outName);

        // and store it
        thisOut.setDataset(outD);
      }
    }

    @Override
    public void recalculate(final IStoreItem subject)
    {
      // ok, we need to recalculate
      performCalc();

      // tell the outputs they've changed
      for (final Document<?> out : getOutputs())
      {
        out.fireDataChanged();
      }
    }
  }

  private static interface FilterOperation
  {
    String getName();

    /**
     * should we keep this data value?
     * 
     * @param value
     * @param filterValue
     * @return
     */
    boolean keep(double value, double filterValue);

    /**
     * should we keep this data value?
     * 
     * @param index
     * @param value
     * @param filterValue
     * @return
     */
    boolean keep(double index, double value, double filterValue);

    /**
     * produce an output name for this input file and filter value
     * 
     * @param name
     * @param filterValue
     * @return
     */
    String nameFor(String name, Integer filterValue);
  }

  private static class MaxFilter implements FilterOperation
  {
    @Override
    public String getName()
    {
      return "Apply max filter";
    }

    @Override
    public boolean keep(final double value, final double filterValue)
    {
      return value <= filterValue;
    }

    @Override
    public boolean keep(final double index, final double value,
        final double filterValue)
    {
      return keep(value, filterValue);
    }

    @Override
    public String nameFor(final String name, final Integer filterValue)
    {
      return name + " Max Filtered";
    }
  };

  private static class MinFilter implements FilterOperation
  {
    @Override
    public String getName()
    {
      return "Apply min filter";
    }

    @Override
    public boolean keep(final double value, final double filterValue)
    {
      return value >= filterValue;
    }

    @Override
    public boolean keep(final double index, final double value,
        final double filterValue)
    {
      return keep(value, filterValue);
    }

    @Override
    public String nameFor(final String name, final Integer filterValue)
    {
      return name + " Min Filtered";
    }
  }

  /**
   * utility method to return a set of inputs, without the filter document
   * 
   * @param list
   * @param filterValue
   * @return
   */
  private static List<IStoreItem> getFilteredInputs(
      final List<IStoreItem> list, final NumberDocument filterValue)
  {
    final List<IStoreItem> filteredInputs = new ArrayList<IStoreItem>();
    filteredInputs.addAll(list);
    filteredInputs.remove(filterValue);
    return filteredInputs;
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      // create a cloned list of the selection
      final List<IStoreItem> filterSelection = new ArrayList<IStoreItem>();
      filterSelection.addAll(selection);

      // ok, find the singleton(s)
      NumberDocument singleton = null;
      for (final IStoreItem item : selection)
      {
        final NumberDocument doc = (NumberDocument) item;
        if (doc.size() == 1)
        {
          singleton = doc;
          break;
        }
      }
      final FilterOperation maxFilter = new MaxFilter();
      ICommand newC =
          new FilterCollectionCommand(maxFilter.getName(), filterSelection,
              destination, context, maxFilter, singleton);
      res.add(newC);

      // and the min filter
      final FilterOperation minFilter = new MinFilter();
      newC =
          new FilterCollectionCommand(minFilter.getName(), filterSelection,
              destination, context, minFilter, singleton);
      res.add(newC);
    }

    return res;
  }

  private boolean appliesTo(final List<IStoreItem> selection)
  {
    // check they all are numeric
    final CollectionComplianceTests aTests = new CollectionComplianceTests();

    final boolean allNumeric = aTests.allQuantity(selection);

    // check we have a singleton
    final boolean hasSingleton = aTests.hasSingleton(selection);

    return hasSingleton && allNumeric && selection.size() > 0;
  }

}
