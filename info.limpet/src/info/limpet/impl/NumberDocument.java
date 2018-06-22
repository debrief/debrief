package info.limpet.impl;

import info.limpet.ICommand;
import info.limpet.operations.RangedEntity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

import javax.measure.quantity.Dimensionless;
import javax.measure.unit.Unit;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.Maths;
import org.eclipse.january.metadata.AxesMetadata;

public class NumberDocument extends Document<Double> implements RangedEntity
{
  public static class DoubleIterator implements Iterator<Double>
  {
    private final double[] _data;
    private int _ctr;

    public DoubleIterator(final double[] data)
    {
      _data = data;
      _ctr = 0;
    }

    @Override
    public boolean hasNext()
    {
      return _ctr < _data.length;
    }

    @Override
    public Double next()
    {
      return _data[_ctr++];
    }

    @Override
    public void remove()
    {
      throw new IllegalArgumentException(
          "Remove operation not provided for this iterator");
    }

  }

  public class MyStats
  {
    public double max()
    {
      final DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.max();
    }

    public double mean()
    {
      final DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.mean(true);
    }

    public double min()
    {
      final DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.min(true);
    }

    public double sd()
    {
      final DoubleDataset ds = (DoubleDataset) dataset;
      return ds.stdDeviation(true);
    }

    public double variance()
    {
      final DoubleDataset ds = (DoubleDataset) dataset;
      return ds.variance(true);
    }
  }

  private Unit<?> qType;

  private Range range;

  public NumberDocument(final DoubleDataset dataset,
      final ICommand predecessor, final Unit<?> qType)
  {
    super(dataset, predecessor);

    if (qType == null)
    {
      this.qType = Dimensionless.UNIT;
    }
    else
    {
      this.qType = qType;
    }
  }

  public void copy(final NumberDocument other)
  {
    this.dataset = other.dataset;
  }

  @Override
  public Iterator<Double> getIterator()
  {
    final DoubleDataset od = (DoubleDataset) dataset;
    final double[] data = od.getData();
    return new DoubleIterator(data);
  }

  @UIProperty(name = "Range", category = UIProperty.CATEGORY_METADATA,
      visibleWhen = "showRange == true")
  public Range getRange()
  {
    return range;
  }

  /**
   * we've introduced this method as a workaround. The "visibleWhen" operator for getRange doesn't
   * work with "size==1". Numerical comparisions don't seem to work. So, we're wrapping the
   * numberical comparison in this boolean method.
   * 
   * @return
   */
  public boolean getShowRange()
  {
    return size() == 1;
  }

  @UIProperty(name = "Size", category = UIProperty.CATEGORY_METADATA)
  public int getSize()
  {
    return size();
  }

  public Unit<?> getType()
  {
    return qType;
  }

  @UIProperty(name = "Units", category = "Label", visibleWhen = "units != null")
  public
      Unit<?> getUnits()
  {
    return qType;
  }

  @UIProperty(name = "Value", category = UIProperty.CATEGORY_VALUE,
      visibleWhen = "showRange == true")
  public double getValue()
  {
    final DoubleDataset data = (DoubleDataset) getDataset();
    return data.get(0);
  }

  public double getValueAt(final int i)
  {
    DoubleDataset ds = (DoubleDataset) dataset;
    return dataset.getDouble(ds.getOffset() + i);
  }

  public Double interpolateValue(final double i, final InterpMethod linear)
  {
    Double res = null;

    // do we have axes?
    final AxesMetadata index = dataset.getFirstMetadata(AxesMetadata.class);
    final Dataset indexData = (Dataset) index.getAxes()[0];

    // check the target index is within the range
    final double lowerIndex = indexData.getDouble(0);
    final int indexSize = indexData.getSize();
    final double upperVal = indexData.getDouble(indexSize - 1);
    if (i >= lowerIndex && i <= upperVal)
    {
      // ok, in range
      final DoubleDataset ds = (DoubleDataset) dataset;
      final DoubleDataset indexes =
          (DoubleDataset) DatasetFactory.createFromObject(new Double[]
          {i});

      // perform the interpolation
      final Dataset dOut = Maths.interpolate(indexData, ds, indexes, 0, 0);

      // get the single matching value out
      res = dOut.getDouble(0);
    }

    return res;
  }

  @Override
  @UIProperty(name = "Quantity", category = UIProperty.CATEGORY_LABEL)
  public boolean isQuantity()
  {
    return true;
  }

  public void replaceSingleton(final double val)
  {
    final DoubleDataset ds =
        (DoubleDataset) DatasetFactory.createFromObject(new double[]
        {val});
    ds.setName(getName());
    setDataset(ds);

    // ok share the good news
    fireDataChanged();
  }

  @Override
  public void setDataset(final IDataset dataset)
  {
    if (dataset instanceof DoubleDataset)
    {
      super.setDataset(dataset);
    }
    else
    {
      throw new IllegalArgumentException("We only store double datasets");
    }
  }

  public void setRange(final Range range)
  {
    this.range = range;
  }

  public void setUnits(final Unit<?> unit)
  {
    qType = unit;
  }

  /** easier support for singletons
   * 
   * @param value
   */
  public void setValue(final double value)
  {
    final DoubleDataset data = (DoubleDataset) getDataset();
    data.set(value, 0);

    // share the good news
    fireDataChanged();
  }

  public MyStats stats()
  {
    return new MyStats();
  }

  @Override
  public String toListing()
  {
    final StringBuffer res = new StringBuffer();

    final DoubleDataset dataset = (DoubleDataset) this.getDataset();
    final AxesMetadata axesMetadata =
        dataset.getFirstMetadata(AxesMetadata.class);

    final int[] dims = dataset.getShape();
    if (dims.length == 1)
    {
      toListing1D(res, dataset, axesMetadata);
    }
    else if (dims.length == 2)
    {
      toListing2D(res, dataset, axesMetadata, dims);
    }

    return res.toString();
  }

  private void toListing1D(final StringBuffer res, final DoubleDataset dataset,
      final AxesMetadata axesMetadata)
  {
    final DoubleDataset axisDataset;
    if (axesMetadata != null && axesMetadata.getAxes().length > 0)
    {
      DoubleDataset doubleAxis = (DoubleDataset) axesMetadata.getAxes()[0];
      axisDataset = doubleAxis != null ? doubleAxis : null;
    }
    else
    {
      axisDataset = null;
    }

    final IndexIterator iterator = dataset.getIterator();

    res.append(dataset.getName() + ":\n");
    while (iterator.hasNext())
    {
      final String indexVal;
      if (axisDataset != null)
      {
        indexVal = "" + axisDataset.getElementDoubleAbs(iterator.index);
      }
      else
      {
        indexVal = "N/A";
      }

      res.append(indexVal + " : " + dataset.getElementDoubleAbs(iterator.index));
      res.append(";");
    }
    res.append("\n");
  }

  private void toListing2D(final StringBuffer res, final DoubleDataset dataset,
      final AxesMetadata axesMetadata, final int[] dims)
  {
    DoubleDataset axisOne = null;
    DoubleDataset axisTwo = null;
    if (axesMetadata != null && axesMetadata.getAxes().length > 0)
    {
      axisOne = (DoubleDataset) axesMetadata.getAxes()[0];
      axisTwo = (DoubleDataset) axesMetadata.getAxes()[1];
    }

    res.append(dataset.getName() + "\n");

    final int xDim = dims[0];
    final int yDim = dims[1];

    final NumberFormat nf = new DecimalFormat(" 000.0;-000.0");

    res.append("        ");
    for (int j = 0; j < yDim; j++)
    {
      res.append(nf.format(axisTwo.get(0, j)) + " ");
    }
    res.append("\n");

    for (int i = 0; i < xDim; i++)
    {
      res.append(nf.format(axisOne.get(i, 0)) + ": ");
      for (int j = 0; j < yDim; j++)
      {
        final Double val = dataset.get(i, j);
        if (val.equals(Double.NaN))
        {
          res.append("       ");
        }
        else
        {
          res.append(nf.format(val) + " ");
        }
      }
      res.append("\n");
    }

    res.append("\n");
  }
}
