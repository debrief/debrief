package Debrief.Wrappers.Extensions.Measurements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract public class TimeSeriesCore implements DataItem, Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /** value used to indicate an invalid index
   * 
   */
  public static int INVALID_INDEX = -1;
 
  
  /**
   * track the parent folder. It's transient since the parent gets assigned as part of XML restore
   */
  private transient DataFolder _parent;
  protected final List<Long> _indices = new ArrayList<Long>();
  protected final String _name;
  protected final String _units;

  public TimeSeriesCore(String name, String units)
  {
    _name = name;
    _units = units;
  }
  
  /** get the index on (or after) the specified time
   * 
   * @param time
   * @return
   */
  public int getIndexNearestTo(long time)
  {
    int ctr = 0;
    for(Long val: _indices)
    {
      if(val >= time)
      {
        return ctr;
      }
      ctr++;
    }
    
    // ok, didn't work. return negative index
    return INVALID_INDEX;
  }

  /**
   * sometimes it's useful to know the parent folder for a dataset
   * 
   * @param parent
   */
  public void setParent(DataFolder parent)
  {
    _parent = parent;
  }

  public DataFolder getParent()
  {
    return _parent;
  }

  public Iterator<Long> getIndices()
  {
    return _indices.iterator();
  }

  @Override
  public String getName()
  {
    return _name;
  }

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

  public int size()
  {
    return _indices.size();
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
}
