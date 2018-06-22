package info.limpet.operations.arithmetic.simple;

import info.limpet.ICommand;
import info.limpet.IContext;
import info.limpet.IOperation;
import info.limpet.IStoreGroup;
import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.impl.NumberDocumentBuilder;
import info.limpet.operations.arithmetic.CoreQuantityCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.IDataset;

public class RateOfChangeOperation implements IOperation
{

  public static class RateOfChangeCommand extends CoreQuantityCommand
  {
    public RateOfChangeCommand(final IStoreGroup store,
        final List<IStoreItem> inputs, final IContext context)
    {
      super("Rate of change", "Calculate rate of change across indexed value",
          store, false, false, inputs, context);
    }

    @Override
    protected String generateName()
    {
      final NumberDocument doc = (NumberDocument) getInputs().get(0);
      final Unit<?> indexUnits = doc.getIndexUnits();
      final Unit<?> valueUnits = doc.getUnits();
      return "\u0394 (" + valueUnits + "/" + indexUnits + ") for "
          + doc.getName();
    }

    @Override
    protected Unit<?> getUnits()
    {
      final NumberDocument doc = (NumberDocument) getInputs().get(0);

      final Unit<?> valueUnits = doc.getUnits();
      final Unit<?> indexUnits = doc.getIndexUnits();

      final Unit<? extends Quantity> outputUnits =
          valueUnits.divide(indexUnits);

      return outputUnits;
    }

    @Override
    protected IDataset performCalc()
    {
      final NumberDocument doc = (NumberDocument) getInputs().get(0);

      final NumberDocumentBuilder output =
          new NumberDocumentBuilder(generateName(), getUnits(), this, doc
              .getIndexUnits());

      // loop through the values
      final Iterator<Double> vIter = doc.getIterator();
      final Iterator<Double> iIter = doc.getIndexIterator();

      boolean first = true;
      double lastV = Double.NaN;
      double lastI = Double.NaN;
      while (vIter.hasNext() && (iIter.hasNext()))
      {
        final double thisValue = vIter.next();
        final double thisIndex = iIter.next();

        if (first)
        {
          first = false;
        }
        else
        {
          // ok, do the delta
          final double deltaV = thisValue - lastV;
          final double deltaI = thisIndex - lastI;
          final double rate = deltaV / deltaI;
          output.add(thisIndex, rate);
        }

        // store the values
        lastV = thisValue;
        lastI = thisIndex;
      }

      // ok, document done.
      return output.toDocument().getDataset();
    }
  }

  @Override
  public List<ICommand> actionsFor(final List<IStoreItem> selection,
      final IStoreGroup destination, final IContext context)
  {
    final List<ICommand> res = new ArrayList<ICommand>();

    if (selection.size() == 1)
    {
      final IStoreItem first = selection.get(0);
      if (first instanceof NumberDocument)
      {
        final NumberDocument doc = (NumberDocument) first;

        if (doc.isIndexed() && doc.isQuantity() && doc.size() > 1)
        {
          // ok, go for it.
          res.add(new RateOfChangeCommand(destination, selection, context));
        }
      }
    }

    return res;
  }
}
