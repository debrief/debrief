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
package info.limpet.operations.arithmetic;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IDocument;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.arithmetic.InterpolatedMaths.IOperationPerformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Comparisons;
import org.eclipse.january.dataset.Comparisons.Monotonicity;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.metadata.AxesMetadata;

public abstract class BinaryQuantityOperation implements IOperation
{
  
  
  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();
  
  /**
   * the command that actually produces data
   * 
   * @author ian
   * 
   */
  public abstract class BinaryQuantityCommand extends CoreQuantityCommand
  {

    @SuppressWarnings("unused")
    private final IDocument<?> timeProvider;

    public BinaryQuantityCommand(final String title, final String description,
        final IStoreGroup store, final boolean canUndo, final boolean canRedo,
        final List<IStoreItem> inputs, final IContext context)
    {
      this(title, description, store, canUndo, canRedo, inputs, null, context);
    }

    public BinaryQuantityCommand(final String title, final String description,
        final IStoreGroup store, final boolean canUndo, final boolean canRedo,
        final List<IStoreItem> inputs, final IDocument<?> timeProvider,
        final IContext context)
    {
      super(title, description, store, canUndo, canRedo, inputs, context);

      this.timeProvider = timeProvider;
    }



    protected String generateName()
    {
      // get the unit
      final NumberDocument first = (NumberDocument) getInputs().get(0);
      final NumberDocument second = (NumberDocument) getInputs().get(1);

      return getBinaryNameFor(first.getName(), second.getName());
    }

    /**
     * provide the name for the product dataset
     * 
     * @param name
     * @param name2
     * @return
     */
    abstract protected String getBinaryNameFor(String name, String name2);

    /**
     * determine the units of the product
     * 
     * @param first
     * @param second
     * @return
     */
    abstract protected Unit<?>
        getBinaryOutputUnit(Unit<?> first, Unit<?> second);

    /**
     * provide class that can perform required operation
     * 
     * @return
     */
    abstract protected IOperationPerformer getOperation();

    protected Unit<?> getUnits()
    {
      // get the unit
      final NumberDocument first = (NumberDocument) getInputs().get(0);
      final NumberDocument second = (NumberDocument) getInputs().get(1);

      return getBinaryOutputUnit(first.getUnits(), second.getUnits());
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates. That is, we need to create it when
     * run initially, then re-generate it on data updates
     * 
     * @param unit
     *          the units to use
     * @param outputs
     *          the list of output series
     */
    protected IDataset performCalc()
    {
      final IDataset res;

      final DoubleDataset in1 =
          (DoubleDataset) ((NumberDocument) getInputs().get(0)).getDataset();
      final DoubleDataset in2 =
          (DoubleDataset) ((NumberDocument) getInputs().get(1)).getDataset();

      // look for axes metadata
      final AxesMetadata axis1 = in1.getFirstMetadata(AxesMetadata.class);

      // keep track of the indices to use in the output
      final Dataset outputIndices;

      AxesMetadata axis2 = null;

      final boolean doInterp;

      if (axis1 != null)
      {
        // ok, is that axis monotonic?
        final Monotonicity axis1Mono =
            Comparisons.findMonotonicity(axis1.getAxes()[0]);

        if (axis1Mono.equals(Monotonicity.NOT_ORDERED))
        {
          // ok, not ordered. we can't use it
          doInterp = false;

          // see if we have a set of output indices we can use
          outputIndices = findIndexDataset();
        }
        else
        {
          axis2 = in2.getFirstMetadata(AxesMetadata.class);

          if (axis2 == null || axis2.getAxes() == null
              || axis2.getAxes().length == 0)
          {
            // the axes don't match. We can't do interp
            doInterp = false;
            // we'll use A indices in the output
            outputIndices = (Dataset) axis1.getAxes()[0];
          }
          else
          {
            final Monotonicity axis2Mono =
                Comparisons.findMonotonicity(axis2.getAxes()[0]);

            if (axis1.getAxes()[0].equals(axis2.getAxes()[0]))
            {
              // identical indexes, we don't need to intepolate
              doInterp = false;
              outputIndices = (Dataset) axis1.getAxes()[0];
            }
            else if (axis2Mono.equals(Monotonicity.NOT_ORDERED))
            {
              // ok, not ordered. we can't use it
              throw new IllegalArgumentException(
                  "Axes must be ordered. Cannot progress");
            }
            else
            {
              // ok, are they in the same direction?
              if (axis1Mono.equals(axis2Mono))
              {
                // fake index
                outputIndices = (Dataset) axis1.getAxes()[0];

                // ok, do an interpolated add operation.
                doInterp = true;

              }
              else
              {
                // wrong directions, can't do
                doInterp = false;
                outputIndices = null;
              }
            }
          }
        }
      }
      else
      {
        // first dataset doesn't have units. We aren't going to be interpolating.
        // see if the second axis does have some
        doInterp = false;

        axis2 = in2.getFirstMetadata(AxesMetadata.class);

        if (axis2 != null && axis2.getAxes() != null
            && axis2.getAxes().length != 0)
        {
          // we'll use A indices in the output
          outputIndices = (Dataset) axis2.getAxes()[0];
        }
        else
        {
          outputIndices = null;
        }
      }

      if (doInterp)
      {
        final InterpolatedMaths.IOperationPerformer doAdd = getOperation();

        // apply our operation to the two datasets
        res = InterpolatedMaths.performWithInterpolation(in1, in2, null, doAdd);
      }
      else if (getATests().allEqualLengthOrSingleton(getInputs()))
      {
        res = getOperation().perform(in1, in2, null);

        // if there are indices, store them
        assignOutputIndices(res, outputIndices);
      }
      else
      {
        res = null;
      }

      // done
      return res;
    }


  }
  
  
  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {

      // aah, what about temporal (interpolated) values?
      final boolean allIndexed = getATests().allEqualIndexed(selection);
      final boolean suitableForIndexedInterpolation =
          getATests().suitableForIndexedInterpolation(selection);
      // final boolean hasIndexed = getATests().hasIndexed(selection);
      if (allIndexed && suitableForIndexedInterpolation /* || hasIndexed */)
      {
        addInterpolatedCommands(selection, destination, res, context);
      }
      else if (getATests().allEqualLengthOrSingleton(selection))
      {
        // instead, offer our indexed commands?
        addIndexedCommands(selection, destination, res, context);
      }
    }
    return res;
  }

  /**
   * produce any new commands for this s election
   * 
   * @param selection
   *          current selection
   * @param destination
   *          where the results will end up
   * @param commands
   *          the list of commands
   */
  protected abstract void addIndexedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> commands, IContext context);

  /**
   * add any commands that require temporal interpolation
   * 
   * @param selection
   * @param destination
   * @param res
   */
  protected abstract void addInterpolatedCommands(List<IStoreItem> selection,
      IStoreGroup destination, Collection<ICommand> res, IContext context);

  /**
   * determine if this dataset is suitable
   * 
   * @param selection
   * @return
   */
  protected abstract boolean appliesTo(List<IStoreItem> selection);

  protected IDocument<?> getLongestIndexedCollection(
      final List<IStoreItem> selection)
  {
    // find the longest time series.
    IDocument<?> longest = null;

    for (final IStoreItem sItem : selection)
    {
      if (sItem instanceof IDocument)
      {
        final IDocument<?> doc = (IDocument<?>) sItem;
        if (doc.isIndexed() || longest == null)
        {
          longest = doc;
        }
        else
        {
          // store the longest one
          longest = doc.size() > longest.size() ? doc : longest;
        }
      }
    }

    return longest;
  }

  /**
   * check if any of the data is decibels - since we can't do traditional add/subtract to them
   * 
   * @param selection
   * @return yes/no
   */
  protected boolean hasLogData(final List<IStoreItem> selection)
  {
    return  aTests.isUnitPresent(selection, NonSI.DECIBEL);
  }

  /**
   * produce a reversed version of the supplied list
   * 
   * @param list
   * @return
   */
  protected List<IStoreItem> reverse(final List<IStoreItem> list)
  {
    final ArrayList<IStoreItem> res = new ArrayList<IStoreItem>(list);
    Collections.reverse(res);
    return res;
  }

}
