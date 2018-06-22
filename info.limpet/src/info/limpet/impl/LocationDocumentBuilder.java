package info.limpet.impl;

import info.limpet.ICommand;
import info.limpet.IDocumentBuilder;
import info.limpet.operations.spatial.GeoSupport;
import info.limpet.operations.spatial.IGeoCalculator;

import java.awt.geom.Point2D;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ObjectDataset;

public class LocationDocumentBuilder extends
    CoreDocumentBuilder<Point2D, LocationDocument> implements
    IDocumentBuilder<Point2D>
{
  final private Unit<?> _distanceUnits;
  
  public LocationDocumentBuilder(final String name, final ICommand predecessor,
      final Unit<?> indexUnits)
  {
    this(name, predecessor, indexUnits, SampleData.DEGREE_ANGLE);
  }
 
  public IGeoCalculator getCalculator()
  {
    return GeoSupport.calculatorFor(_distanceUnits);
  }

  public LocationDocumentBuilder(final String name, final ICommand predecessor,
      final Unit<?> indexUnits, final Unit<?> distanceUnits)
  {
    super(name, predecessor, indexUnits);
    _distanceUnits = distanceUnits;
  }

  @Override
  protected IDataset getDataset(final List<Point2D> values)
  {
    final Object[] arr = values.toArray();
    return DatasetFactory.createFromObject(ObjectDataset.class, arr, null);
  }

  @Override
  protected LocationDocument getDocument(final IDataset dataset,
      final ICommand predecessor)
  {
    return new LocationDocument((ObjectDataset) dataset, predecessor,
        _distanceUnits);
  }

}