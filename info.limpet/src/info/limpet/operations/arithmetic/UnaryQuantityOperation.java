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
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.Document;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.CollectionComplianceTests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.metadata.AxesMetadata;

public abstract class UnaryQuantityOperation implements IOperation
{
  /**
   * the command that actually produces data
   * 
   * @author ian
   * 
   */
  public class UnaryQuantityCommand extends CoreQuantityCommand
  {

    public UnaryQuantityCommand(final String title, final String description,
        final IStoreGroup store, final List<IStoreItem> inputs,
        final IContext context)
    {
      super(title, description, store, true, true, inputs, context);
    }

    @Override
    public void execute()
    {
      // clear the results sets
      clearOutputs(getOutputs());

      // we may be acting separately on multiple inputs.
      // so, loop through them
      for (final IStoreItem input : getInputs())
      {
        final NumberDocument inputDoc = (NumberDocument) input;

        // ok, process this one.
        // sort out the output unit
        final Unit<?> unit = getUnits(inputDoc);

        // start adding values.
        final IDataset dataset = performCalc(inputDoc);

        // store the name
        dataset.setName(generateName(inputDoc));

        // ok, wrap the dataset
        final NumberDocument output =
            new NumberDocument((DoubleDataset) dataset, this, unit);
        
        // also store the index units
        if(output.isIndexed())
        {
          output.setIndexUnits(inputDoc.getIndexUnits());
        }
        
        // and fire out the update
        output.fireDataChanged();

        // store the output
        super.addOutput(output);

        // tell the series that we're a dependent
        inputDoc.addDependent(this);

        // ok, store the results
        getStore().add(output);

      }

    }

    protected String generateName(final NumberDocument inputDoc)
    {
      // get the name
      return getUnaryNameFor(inputDoc.getName());
    }

    protected Unit<?> getUnits(final NumberDocument inputDoc)
    {
      // get the unit
      return getUnaryOutputUnit(inputDoc.getUnits());
    }

    /**
     * wrap the actual operation. We're doing this since we need to separate it from the core
     * "execute" operation in order to support dynamic updates
     * 
     * @param nd
     * 
     * @param unit
     *          the units to use
     * @param outputs
     *          the list of output series
     */
    protected IDataset performCalc(final NumberDocument nd)
    {
      final DoubleDataset ds = (DoubleDataset) nd.getDataset();

      // ok, re-calculate this
      Dataset res = calculate(ds);

      // store the axes
      final AxesMetadata axis1 = ds.getFirstMetadata(AxesMetadata.class);

      // if there are indices, store them
      if (axis1 != null)
      {
        res.addMetadata(axis1.clone());
//        final AxesMetadata am = new AxesMetadataImpl();
//        // keep track of the indices to use in the output
//        final DoubleDataset outputIndices = (DoubleDataset) axis1.getAxes()[0];
//        am.initialize(1);
//        am.setAxis(0, outputIndices);
//        res.addMetadata(am);
      }

      // done
      return res;
    }

    /**
     * for unitary operations we only act on a single input. We may be acting on an number of
     * datasets, so find the relevant one, and re-calculate it
     */
    @Override
    protected void recalculate(final IStoreItem subject)
    {
      // TODO: change logic, we should only re-generate the
      // single output

      // workaround: we don't know which output derives
      // from this input. So, we will have to regenerate
      // all outputs

      final Iterator<Document<?>> oIter = getOutputs().iterator();

      // we may be acting separately on multiple inputs.
      // so, loop through them
      for (final IStoreItem input : getInputs())
      {
        final NumberDocument inputDoc = (NumberDocument) input;
        final NumberDocument outputDoc = (NumberDocument) oIter.next();

        // ok, process this one.
        final Unit<?> unit = getUnits(inputDoc);

        // update the units
        if (outputDoc.getUnits() != unit)
        {
          outputDoc.setUnits(unit);
        }

        // clear the results sets
        clearOutputs(getOutputs());

        // start adding values.
        final IDataset dataset = performCalc(inputDoc);

        // update the name
        dataset.setName(generateName(inputDoc));

        // store the data
        outputDoc.setDataset(dataset);

        // and fire out the update
        outputDoc.fireDataChanged();
      }
    }

    @Override
    protected Unit<?> getUnits()
    {
      // warning - this is only present to meet the API requirements.
      // Since we over-ride the execute() method, we call our own performCalc() 
      // operation.
      
      throw new IllegalArgumentException("This method should not get called");
    }

    @Override
    protected String generateName()
    {
      // warning - this is only present to meet the API requirements.
      // Since we over-ride the execute() method, we call our own performCalc() 
      // operation.
      
      throw new IllegalArgumentException("This method should not get called");
    }

    @Override
    protected IDataset performCalc()
    {
      // warning - this is only present to meet the API requirements.
      // Since we over-ride the execute() method, we call our own performCalc() 
      // operation.
      
      throw new IllegalArgumentException("This method should not get called");
    }

  }

  private final String _opName;

  protected final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  public UnaryQuantityOperation(final String opName)
  {
    _opName = opName;
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      final ICommand newC =
          new UnaryQuantityCommand("Math - " + _opName, "description here",
              destination, selection, context);

      res.add(newC);
    }

    return res;
  }

  /**
   * determine if this dataset is suitable
   * 
   * @param selection
   * @return
   */
  protected abstract boolean appliesTo(List<IStoreItem> selection);

  /**
   * perform the operation on the subject dataset
   * 
   * @param input
   * @return
   */
  abstract public Dataset calculate(Dataset input);

  public CollectionComplianceTests getATests()
  {
    return aTests;
  }

  public String getName()
  {
    return _opName;
  }

  /**
   * provide the name for the product dataset
   * 
   * @param name
   * @param name2
   * @return
   */
  protected String getUnaryNameFor(final String name)
  {
    return name + ": " + _opName;
  }

  /**
   * determine the units of the product
   * 
   * @param first
   * @param second
   * @return
   */
  abstract protected Unit<?> getUnaryOutputUnit(Unit<?> first);

}
