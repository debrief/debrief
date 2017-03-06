package Debrief.Wrappers.Extensions.Measurements;

import java.util.ArrayList;
import java.util.List;

public class CoreDataset<IndexType extends Number, ValueType> implements DataItem
{
  private final List<Measurement> _data = new ArrayList<Measurement>();
  private String _name;
  private String _units;
  
  
  public class Measurement
  {
    private IndexType _index;
    private ValueType _value;
    
    public Measurement(final IndexType index, final ValueType value)
    {
      _index = index;
      _value = value;
    }
  }
  
  public CoreDataset(final String name, final String units)
  {
    _name = name;
    _units = units;
  }
  
  public void add(IndexType index, ValueType value)
  {
    Measurement m = new Measurement(index,value);
    _data.add(m);
  }
  
  public void add(Measurement measurement)
  {
    _data.add(measurement);
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
    for(final Measurement m:_data)
    {
      System.out.println("i:" + m._index + " v:" + m._value);
    }
  }

  public int size()
  {
    return _data.size();
  }

}
