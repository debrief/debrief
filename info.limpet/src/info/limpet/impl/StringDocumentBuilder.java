package info.limpet.impl;

import info.limpet.ICommand;
import info.limpet.IDocumentBuilder;

import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.StringDataset;

public class StringDocumentBuilder extends
    CoreDocumentBuilder<String, StringDocument> implements
    IDocumentBuilder<String>
{
  public StringDocumentBuilder(final String name, final ICommand predecessor,
      final Unit<?> indexUnits)
  {
    super(name, predecessor, indexUnits);
  }

  @Override
  protected IDataset getDataset(final List<String> values)
  {
    final String[] arr = values.toArray(new String[]
    {});
    return DatasetFactory.createFromObject(StringDataset.class, arr, null);
  }

  @Override
  protected StringDocument getDocument(final IDataset dataset,
      final ICommand _predecessor2)
  {
    return new StringDocument((StringDataset) dataset, _predecessor2);
  }
}
