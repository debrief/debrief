package Debrief.Wrappers.Extensions.Measurements;

import java.util.Iterator;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.metadata.AxesMetadata;

abstract public class TimeSeriesDatasetCore extends TimeSeriesCore
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /** where our dataset is stored
   * 
   */
  protected Dataset _data;

  /** easy-access copy of the time indices
   * 
   */
  private LongDataset _times;

  public TimeSeriesDatasetCore(String units)
  {
    super(units);
  }

  @Override
  public int size()
  {
    return _data.getSize();
  }
  
  @Override
  public String getName()
  {
    return _data.getName();
  }
  
  @Override
  public void setName(String name)
  {
    _data.setName(name);
  }


  private static class LongIterator implements Iterator<Long>
  {
    final private LongDataset _lData;
    int index = 0;

    public LongIterator(LongDataset dataset)
    {
      _lData = dataset;
    }

    @Override
    public boolean hasNext()
    {
      return index != _lData.getSize();
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException(
          "remove() not supported in this iterator");
    }

    @Override
    public Long next()
    {
      return _lData.get(index++);
    }
  }

  protected static class DoubleIterator implements Iterator<Double>
  {
    final private DoubleDataset _lData;
    int index = 0;

    public DoubleIterator(DoubleDataset dataset)
    {
      _lData = dataset;
    }

    @Override
    public boolean hasNext()
    {
      return index != _lData.getSize();
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException(
          "remove() not supported in this iterator");
    }

    @Override
    public Double next()
    {
      return _lData.get(index++);
    }
  }

  protected LongDataset getTimes()
  {
    if (_times == null)
    {
      // get the axes metadata
      AxesMetadata axes = _data.getFirstMetadata(AxesMetadata.class);
      _times = (LongDataset) axes.getAxis(0)[0];
    }

    return _times;
  }

  @Override
  public Iterator<Long> getIndices()
  {
    // create an iterator for this data
    return new LongIterator(getTimes());
  }

  /**
   * get the index on (or after) the specified time
   * 
   * @param time
   * @return
   */
  @Override
  public int getIndexNearestTo(long time)
  {
    Iterator<Long> timeIter = getIndices();
    int ctr = 0;
    while(timeIter.hasNext())
    {
      Long val = timeIter.next();
      if (val >= time)
      {
        return ctr;
      }
      ctr++;
    }

    // ok, didn't work. return negative index
    return INVALID_INDEX;
  }

}
