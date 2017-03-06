package Debrief.Wrappers.Extensions.Measurements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoreDataset<IndexType extends Number, ValueType> implements DataItem, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final List<IndexType> _indices = new ArrayList<IndexType>();
  private final List<ValueType> _values = new ArrayList<ValueType>();
  private String _name;
  private String _units;
  
  
  public CoreDataset(final String name, final String units)
  {
    _name = name;
    _units = units;
  }
  
  public void add(IndexType index, ValueType value)
  {
    _indices.add(index);
    _values.add(value);
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
    System.out.println(":"  + getName());
    final int len = _indices.size();
    for(int i=0;i<len;i++)
    {
      System.out.println("i:" + _indices.get(i) + " v:" + _values.get(i));
    }
  }

  public int size()
  {
    return _indices.size();
  }

}
