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

}
