package Debrief.Wrappers.Measurements;

import java.util.ArrayList;
import java.util.List;

import MWC.GUI.CanvasType;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;

public class CoreDataset<IndexType extends Number, ValueType> extends PlainWrapper implements DataItem, Plottable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final List<Measurement> _data = new ArrayList<Measurement>();
  private String _name;
  
  
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
  
  public CoreDataset(String name)
  {
    _name = name;
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

  /**
   * convenience function, to describe this plottable as a string
   */
  public String toString()
  {
    return getName() + " (" + size() + " items)";
  }
  
  @Override
  public void paint(CanvasType dest)
  {
  }

  @Override
  public boolean hasEditor()
  {
    return false;
  }

  @Override
  public WorldArea getBounds()
  {
    return null;
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
