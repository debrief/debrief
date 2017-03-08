package Debrief.Wrappers.Extensions.Measurements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CoreDataset implements DataItem, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private transient DataFolder _parent;

  private final List<Long> _indices = new ArrayList<Long>();
  private final List<Double> _values = new ArrayList<Double>();
  private final String _name;
  private final String _units;

  public CoreDataset(final String name, final String units)
  {
    _name = name;
    _units = units;
  }
  
  /** sometimes it's useful to know the parent folder for a dataset
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

  public void add(Long index, Double value)
  {
    _indices.add(index);
    _values.add(value);
  }
  
  public Iterator<Long> getIndices()
  {
    return _indices.iterator();
  }
  
  public Iterator<Double> getValues()
  {
    return _values.iterator();
  }

  @Override
  public String getName()
  {
    return _name;
  }

  public String getUnits()
  {
    return _units;
  }

  /**
   * convenience function, to describe this plottable as a string
   */
  public String toString()
  {
    return getName() + " (" + size() + " items)";
  }

  public void printAll()
  {
    System.out.println(":" + getName());
    final int len = _indices.size();
    for (int i = 0; i < len; i++)
    {
      System.out.println("i:" + _indices.get(i) + " v:" + _values.get(i));
    }
  }

  public int size()
  {
    return _indices.size();
  }
}
