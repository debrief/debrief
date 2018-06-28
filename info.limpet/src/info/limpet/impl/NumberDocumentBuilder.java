package info.limpet.impl;

import info.limpet.ICommand;

import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;

public class NumberDocumentBuilder extends
    CoreDocumentBuilder<Double, NumberDocument>
{
  private final Unit<?> _units;
  private Range _range;

  public NumberDocumentBuilder(final String name, final Unit<?> valueUnits,
      final ICommand predecessor, final Unit<?> indexUnits)
  {
    super(name, predecessor, indexUnits);
    _units = valueUnits;
  }

  @Override
  protected void finishOff(final NumberDocument res)
  {
    res.setRange(_range);
    
    // let the parent clear itself
    super.finishOff(res);
  }

  @Override
  protected IDataset getDataset(final List<Double> values)
  {
    final Double[] arr = values.toArray(new Double[]
    {});
    return DatasetFactory.createFromObject(DoubleDataset.class, arr, null);
  }

  @Override
  protected NumberDocument getDocument(final IDataset dataset,
      final ICommand predecessor)
  {
    return new NumberDocument((DoubleDataset) dataset, predecessor, _units);
  }

  public void setRange(final Range range)
  {
    _range = range;
  }
}
