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
import info.limpet.impl.Range;
import info.limpet.impl.UIProperty;
import info.limpet.operations.AbstractCommand;
import info.limpet.operations.CollectionComplianceTests;
import info.limpet.operations.RangedEntity;

import java.util.ArrayList;
import java.util.List;

public class SimpleMovingAverageOperation implements IOperation
{
  public static class SimpleMovingAverageCommand extends AbstractCommand implements RangedEntity
  {

    private int winSize;

    public SimpleMovingAverageCommand(final String operationName,
        final List<IStoreItem> selection, final IStoreGroup store,
        final int windowSize, final IContext context)
    {
      super(operationName, "Calculates a Simple Moving Average", store, false,
          false, selection, context);
      winSize = windowSize;
    }

    protected String getOutputName()
    {
      return getContext().getInput("Generate simple moving average",
          NEW_DATASET_MESSAGE, "Moving average of " + super.getSubjectList());
    }

    @UIProperty(name = "Window", category = UIProperty.CATEGORY_CALCULATION,
        min = 1, max = 20)
    public int getWindowSize()
    {
      return winSize;
    }

    @Override
    public void recalculate(final IStoreItem subject)
    {
      // TODO Auto-generated method stub

    }

    public void setWindowSize(final int winSize)
    {
      this.winSize = winSize;

      // ok, we now need to update!
      super.dataChanged(this.getOutputs().iterator().next());
    }
    //
    // protected NumberDocument getOutputFor(NumberDocument input, String outName)
    // {
    // final IQuantityCollection<?> res;
    //
    // if(input.isTemporal())
    // {
    // @SuppressWarnings("unchecked")
    // Unit<Quantity> units = (Unit<Quantity>) input.getUnits();
    // res = new TemporalQuantityCollection<Quantity>(outName, this, units);
    // }
    // else
    // {
    // res = new QuantityCollection<>(outName, this, input.getUnits());
    // }
    //
    // return res;
    // }
    //
    // @Override
    // public void execute()
    // {
    // IQuantityCollection<?> input =
    // (IQuantityCollection<?>) getInputs().get(0);
    //
    // List<ICollection> outputs = new ArrayList<ICollection>();
    //
    // // ok, generate the new series
    // IQuantityCollection<?> target = getOutputFor(input, getOutputName());
    //
    // outputs.add(target);
    //
    // // store the output
    // super.addOutput(target);
    //
    // // start adding values.
    // performCalc(outputs);
    //
    // // tell each series that we're a dependent
    // Iterator<ICollection> iter = getInputs().iterator();
    // while (iter.hasNext())
    // {
    // ICollection iCollection = iter.next();
    // iCollection.addDependent(this);
    // }
    //
    // // ok, done
    // List<IStoreItem> res = new ArrayList<IStoreItem>();
    // res.add(target);
    // getStore().addAll(res);
    // }
    //
    // @Override
    // public void recalculate(IStoreItem subject)
    // {
    // // update the results
    // performCalc(getOutputs());
    // }

    @Override
    public double getValue()
    {
      return getWindowSize();
    }

    @Override
    public void setValue(double value)
    {
      setWindowSize((int) value);
    }

    @Override
    public Range getRange()
    {
      return new Range(1, 20);
    }

    // /**
    // * wrap the actual operation. We're doing this since we need to separate it from the core
    // * "execute" operation in order to support dynamic updates
    // *
    // * @param unit
    // * @param outputs
    // */
    // private void performCalc(List<ICollection> outputs)
    // {
    // IQuantityCollection<?> target =
    // (IQuantityCollection<?>) outputs.iterator().next();
    //
    // // clear out the lists, first
    // Iterator<ICollection> iter = outputs.iterator();
    // while (iter.hasNext())
    // {
    // IQuantityCollection<?> qC = (IQuantityCollection<?>) iter.next();
    // qC.clearQuiet();
    // }
    //
    // SimpleMovingAverage sma = new SimpleMovingAverage(winSize);
    // @SuppressWarnings("unchecked")
    //
    // IQuantityCollection<Quantity> input =
    // (IQuantityCollection<Quantity>) getInputs().get(0);
    //
    // if(input.isTemporal())
    // {
    // // use temporal data
    // ITemporalQuantityCollection<?> outT = (ITemporalQuantityCollection<?>) target;
    // ITemporalQuantityCollection<?> inT = (ITemporalQuantityCollection<?>) input;
    //
    // // we need our time data
    // List<Long> times = inT.getTimes();
    // Iterator<Long> tIter = times.iterator();
    // for(Measurable<Quantity> quantity : input.getValues())
    // {
    // sma.newNum(quantity.doubleValue(input.getUnits()));
    // outT.add(tIter.next(), sma.getAvg());
    // }
    // }
    // else
    // {
    // // ok, plain values
    // for (Measurable<Quantity> quantity : input.getValues())
    // {
    // sma.newNum(quantity.doubleValue(input.getUnits()));
    // target.add(sma.getAvg());
    // }
    // }
    //
    //
    //
    // // and fire the update
    // target.fireDataChanged();
    //
    // }
  }

  public static final String SERIES_NAME_TEMPLATE = "Simple Moving Average";

  private final CollectionComplianceTests aTests =
      new CollectionComplianceTests();

  private final int _windowSize;

  public SimpleMovingAverageOperation(final int windowSize)
  {
    this._windowSize = windowSize;
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();
    if (appliesTo(selection))
    {
      final ICommand newC =
          new SimpleMovingAverageCommand(SERIES_NAME_TEMPLATE, selection,
              destination, _windowSize, context);
      res.add(newC);
    }

    return res;
  }

  private boolean appliesTo(final List<IStoreItem> selection)
  {
    final boolean singleSeries = selection.size() == 1;
    final boolean allQuantity = aTests.allQuantity(selection);
    return singleSeries && allQuantity;
  }

}
