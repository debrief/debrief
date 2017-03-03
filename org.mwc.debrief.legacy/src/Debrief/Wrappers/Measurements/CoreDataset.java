package Debrief.Wrappers.Measurements;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import MWC.GUI.CanvasType;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldArea;

public class CoreDataset<IndexType extends Number, ValueType> extends PlainWrapper implements DataItem
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final List<Measurement> _data = new ArrayList<Measurement>();
  private String _name;
  
  
  private class Measurement
  {
    private IndexType _index;
    private ValueType _value;
    
    @SuppressWarnings("unused")
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
  
  public void add(Measurement measurement)
  {
    _data.add(measurement);
  }
  
  @Override
  public String getName()
  {
    return _name;
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
  
  public class TestCore extends TestCase
  {
    public void testMe()
    {
      CoreDataset<Long, Double> timeD = new CoreDataset<Long, Double>("TimeDouble");
      timeD.printAll();
      
  //    CoreDataset<Long, Double>.Measurement newM = new CoreDataset<Long, Double>.Measurement(new Long(12), new Double(44.0));
  //    timeD.add(newM);     
    }
  }

  public void printAll()
  {
    for(final Measurement m:_data)
    {
      System.out.println("i:" + m._index + " v:" + m._value);
    }
  }

}
