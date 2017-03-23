package Debrief.Wrappers.Extensions.Measurements;

import java.io.Serializable;
import java.util.Iterator;

abstract public class TimeSeriesCore implements Serializable, DataItem
{

  /** value used to indicate an invalid index
   * 
   */
  public static final int INVALID_INDEX = -1;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * track the parent folder. It's transient since the parent gets assigned as part of XML restore
   */
  private transient DataFolder _parent;
  protected final String _units;

  public TimeSeriesCore(String units)
  {
    _units = units;
  }
  
  /** get the the number of elements in this time series
   * 
   * @return
   */
  abstract public int size();
  

  /**
   * sometimes it's useful to know the parent folder for a dataset
   * 
   * @param parent
   */
  @Override
  public void setParent(DataFolder parent)
  {
    _parent = parent;
  }

  @Override
  public DataFolder getParent()
  {
    return _parent;
  }


  @Override
  abstract public String getName();

  /**
   * convenience function, to describe this plottable as a string
   */
  public String toString()
  {
    return getName() + " (" + size() + " items)";
  }

  public String getUnits()
  {
    return _units;
  }

  public String getPath()
  {
    String name = "";
    DataFolder parent = this.getParent();
    while (parent != null)
    {
      name = parent.getName() + " // " + name;
      parent = parent.getParent();
    }
    name += getName();
    return name;
  }

  abstract public Iterator<Long> getIndices();

  abstract public int getIndexNearestTo(long time);
}
