package info.limpet.impl;

import info.limpet.ICommand;
import info.limpet.IDocument;
import info.limpet.IDocumentBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

abstract class CoreDocumentBuilder<T extends Object, D extends IDocument<T>>
    implements IDocumentBuilder<T>
{

  protected final String _name;
  private ArrayList<Double> _indices;
  protected final ICommand _predecessor;
  protected Unit<?> _indexUnits;
  protected final List<T> _values = new ArrayList<T>();

  public CoreDocumentBuilder(final String name, final ICommand predecessor,
      final Unit<?> indexUnits)
  {
    _name = name;
    _predecessor = predecessor;
    _indexUnits = indexUnits;
  }
  
  public List<T> getValues()
  {
    return _values;
  }

  public ArrayList<Double> getIndices()
  {
    return _indices;
  }
  
  @Override
  public void add(final double index, final T value)
  {
    // check we know about the index
    if (_indexUnits == null)
    {
      throw new IllegalArgumentException("We don't know index units");
    }

    // sort out the observation
    add(value);

    // and now the index
    if (_indices == null)
    {
      _indices = new ArrayList<Double>();
    }

    _indices.add(index);
  }

  @Override
  public void add(final T point)
  {
    _values.add(point);
  }

  /**
   * remove all data (including indices, if necessary)
   * 
   */
  public void clear()
  {
    _values.clear();

    if (_indices != null)
    {
      _indices.clear();
      _indices = null;
    }
  }

  /**
   * do any last modifications to the output document
   * 
   * @param res
   */
  protected void finishOff(final D res)
  {
    // no default processing required
  }

  /**
   * get the output dataset
   * 
   * @param values
   * @return
   */
  abstract protected IDataset getDataset(List<T> values);

  /**
   * get the output document, populated with the output dataset
   * 
   * @param dataset
   * @param predecessor
   * @return
   */
  abstract protected D getDocument(IDataset dataset, ICommand predecessor);

  /**
   * get the index units
   * 
   * @return
   */
  public Unit<?> getIndexUnits()
  {
    return _indexUnits;
  }

  /** override the index units
   * 
   */
  public void setIndexUnits(Unit<?> units)
  {
    _indexUnits = units;
    
    // oh, and clear the indices - we can't use them anyway
    _indices = null;
  }
  
  @Override
  public D toDocument()
  {
    final D res;
    if (_values.size() > 0)
    {
      // ok, start with the dataset
      final IDataset dataset = getDataset(_values);
      dataset.setName(_name);

      // do we have any indices to add?
      if (_indices != null)
      {
        // yes, store them
        final DoubleDataset indexData =
            (DoubleDataset) DatasetFactory.createFromObject(_indices);
        final AxesMetadata index = new AxesMetadataImpl();
        index.initialize(1);
        index.setAxis(0, indexData);
        dataset.addMetadata(index);
      }

      // get the output document
      res = getDocument(dataset, _predecessor);

      // do we indices?
      if (_indices != null)
      {
        if (_indexUnits == null)
        {
          System.err.println("Setting index, but do not have units");
        }

        // ok, set the index units
        res.setIndexUnits(_indexUnits);
      }
      else
      {
        if (_indexUnits != null)
        {
          throw new IllegalArgumentException("Have index units, but no index");
        }
      }

      // ok, any last minute tidying
      finishOff(res);
    }
    else
    {
      res = null;
    }
    return res;
  }

}