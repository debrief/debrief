package info.limpet.impl;

import info.limpet.ICommand;
import info.limpet.operations.spatial.GeoSupport;
import info.limpet.operations.spatial.IGeoCalculator;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Iterator;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class LocationDocument extends Document<Point2D>
{
  
  private Unit<?> _distanceUnits;

  public LocationDocument(ObjectDataset dataset, ICommand predecessor)
  {
    this(dataset, predecessor, SampleData.DEGREE_ANGLE);
  }

  public LocationDocument(ObjectDataset dataset, ICommand predecessor, Unit<?> units)
  {
    super(dataset, predecessor);
    _distanceUnits = units;
  }

  public IGeoCalculator getCalculator()
  {
    return GeoSupport.calculatorFor(_distanceUnits);
  }
  
  public boolean isQuantity()
  {
    return false;
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

  @Override
  public void setDataset(IDataset dataset)
  {
    if (dataset instanceof ObjectDataset)
    {
      super.setDataset(dataset);
    }
    else
    {
      throw new IllegalArgumentException("We only store object datasets");
    }
  }

  @UIProperty(name = "Value", category = UIProperty.CATEGORY_VALUE,
      visibleWhen = "showRange == true")
  public String getValue()
  {
    ObjectDataset data = (ObjectDataset) getDataset();
    Point2D point = (Point2D) data.get();
    return point.getY() + "," + point.getX();
  }

  public void setValue(String val)
  {
    // try to parse it
    String[] items = val.split(",");
    if (items.length == 2)
    {
      try
      {
        double y = Double.parseDouble(items[0]);
        double x = Double.parseDouble(items[1]);
        ObjectDataset data = (ObjectDataset) getDataset();
        Point2D point = (Point2D) data.get();
        point.setLocation(x, y);
        
        // successful, fire modified
        this.fireDataChanged();

      }
      catch (NumberFormatException dd)
      {
        dd.printStackTrace();
      }
    }
    
  }

  public String toListing()
  {
    StringBuffer res = new StringBuffer();

    ObjectDataset dataset = (ObjectDataset) this.getDataset();
    final AxesMetadata axesMetadata =
        dataset.getFirstMetadata(AxesMetadata.class);
    final IndexIterator iterator = dataset.getIterator();

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

      res.append(indexVal + " : " + dataset.get(iterator.index));
      res.append(";");
    }
    res.append("\n");

    return res.toString();
  }

  /**
   * retrieve the location at the specified time (even if it's a non-temporal collection)
   * 
   * @param iCollection
   *          set of locations to use
   * @param thisTime
   *          time we're need a location for
   * @return
   */
  public Point2D locationAt(double thisTime)
  {
    Point2D res = null;
    if (isIndexed())
    {
      res = interpolateValue(thisTime);
    }
    else
    {
      res = getLocationIterator().next();
    }
    return res;
  }

  //
  // public Point2D interpolateValue(long i, InterpMethod linear)
  // {
  // Point2D res = null;
  //
  // // do we have axes?
  // AxesMetadata index = dataset.getFirstMetadata(AxesMetadata.class);
  // ILazyDataset indexDataLazy = index.getAxes()[0];
  // try
  // {
  // Dataset indexData = DatasetUtils.sliceAndConvertLazyDataset(indexDataLazy);
  //
  // // check the target index is within the range
  // double lowerIndex = indexData.getDouble(0);
  // int indexSize = indexData.getSize();
  // double upperVal = indexData.getDouble(indexSize - 1);
  // if(i >= lowerIndex && i <= upperVal)
  // {
  // // ok, create an dataset that captures this specific time
  // LongDataset indexes = (LongDataset) DatasetFactory.createFromObject(new Long[]{i});
  //
  // // perform the interpolation
  // Dataset dOut = Maths.interpolate(indexData, ds, indexes, 0, 0);
  //
  // // get the single matching value out
  // res = dOut.getDouble(0);
  // }
  // }
  // catch (DatasetException e)
  // {
  // e.printStackTrace();
  // }
  //
  // return res;
  // }

  private Point2D interpolateValue(double time)
  {
    final Point2D res;
    
    final IGeoCalculator calculator = getCalculator();

    // ok, find the values either side
    int beforeIndex = -1, afterIndex = -1;
    double beforeTime = 0, afterTime = 0;

    Iterator<Double> tIter = getIndexIterator();
    int ctr = 0;
    while (tIter.hasNext())
    {
      Double thisT = tIter.next();
      if (thisT <= time)
      {
        beforeIndex = ctr;
        beforeTime = thisT;
      }
      if (thisT >= time)
      {
        afterIndex = ctr;
        afterTime = thisT;
        break;
      }

      ctr++;
    }

    if (beforeIndex >= 0 && afterIndex == 0)
    {
      ObjectDataset od = (ObjectDataset) getDataset();
      res = (Point2D) od.get(beforeIndex);
    }
    else if (beforeIndex >= 0 && afterIndex >= 0)
    {
      if (beforeIndex == afterIndex)
      {
        // special case - it falls on one of our values
        ObjectDataset od = (ObjectDataset) getDataset();
        res = (Point2D) od.get(beforeIndex);
      }
      else
      {
        final ObjectDataset od = (ObjectDataset) getDataset();
        final Point2D beforeVal = (Point2D) od.get(beforeIndex);
        final Point2D afterVal = (Point2D) od.get(afterIndex);

        double latY0 = beforeVal.getY();
        double latY1 = afterVal.getY();

        double longY0 = beforeVal.getX();
        double longY1 = afterVal.getX();

        double x0 = beforeTime;
        double x1 = afterTime;
        double x = time;

        double newResLat = latY0 + (latY1 - latY0) * (x - x0) / (x1 - x0);
        double newResLong = longY0 + (longY1 - longY0) * (x - x0) / (x1 - x0);

        // ok, we can do the calc
        res = calculator.createPoint(newResLong, newResLat);
      }
    }
    else
    {
      res = null;
    }

    return res;
  }

  public MyStats stats()
  {
    return new MyStats();
  }

  public class MyStats
  {
    public double min()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.min(true);
    }

    public double max()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.max();

    }

    public double mean()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.mean(true);
    }

    public double variance()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.variance(true);
    }

    public double sd()
    {
      DoubleDataset ds = (DoubleDataset) dataset;
      return (Double) ds.stdDeviation(true);
    }
  }

  public Iterator<Point2D> getLocationIterator()
  {
    final Iterator<?> oIter = getObjectIterator();
    return new Iterator<Point2D>()
    {

      @Override
      public boolean hasNext()
      {
        return oIter.hasNext();
      }

      @Override
      public Point2D next()
      {
        return (Point2D) oIter.next();
      }

      @Override
      public void remove()
      {
        oIter.remove();
      }
    };
  }

  public Iterator<?> getObjectIterator()
  {
    ObjectDataset od = (ObjectDataset) dataset;
    Object[] strings = od.getData();
    Iterable<Object> iterable = Arrays.asList(strings);
    return iterable.iterator();
  }

  @Override
  public Iterator<Point2D> getIterator()
  {
    return getLocationIterator();
  }

  public Unit<?> getUnits()
  {
    return _distanceUnits;
  }
}
