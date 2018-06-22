package info.limpet.operations.arithmetic.simple;

import info.limpet.IStoreItem;
import info.limpet.impl.NumberDocument;
import info.limpet.operations.arithmetic.UnaryQuantityOperation;

import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;

public class UnitConversionOperation extends UnaryQuantityOperation
{

  final private Unit<?> targetUnit;

  public UnitConversionOperation(final Unit<?> newUnit)
  {
    super("Convert units to " + newUnit.toString());
    targetUnit = newUnit;
  }

  @Override
  protected boolean appliesTo(final List<IStoreItem> selection)
  {
    final boolean singleSeries = selection.size() == 1;
    final boolean allQuantity = aTests.allQuantity(selection);
    boolean sameDimension = false;
    boolean sameUnits = true;
    if (selection.size() > 0 && allQuantity)
    {
      final Unit<?> units = ((NumberDocument) selection.get(0)).getUnits();
      sameDimension = units.getDimension().equals(targetUnit.getDimension());

      // check they're different units. It's not worth offering the
      // operation
      // if
      // they're already in the same units
      sameUnits = units.equals(targetUnit);
    }
    return singleSeries && allQuantity && sameDimension && !sameUnits;
  }

  @Override
  public Dataset calculate(final Dataset input)
  {
    // TODO: once UoM is in dataset, store it there
    return input;
  }

  @Override
  protected String getUnaryNameFor(final String name)
  {
    return name + " converted to:" + targetUnit.toString();
  }

  @Override
  protected Unit<?> getUnaryOutputUnit(final Unit<?> first)
  {
    return targetUnit;
  }

}
