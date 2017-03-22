package Debrief.Wrappers.Extensions.Measurements;

import java.io.Serializable;

abstract public class TimeSeriesCore implements Serializable, ITimeSeriesCore
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * track the parent folder. It's transient since the parent gets assigned as part of XML restore
   */
  private transient DataFolder _parent;
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

  @Override
  public String getUnits()
  {
    return _units;
  }

  @Override
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
