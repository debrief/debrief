package Debrief.Wrappers.Extensions.Measurements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** time series that stores double measurements
 * 
 * @author ian
 *
 */
public class TimeSeries2Double extends TimeSeriesCore
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final List<Double> _values1 = new ArrayList<Double>();
  private final List<Double> _values2 = new ArrayList<Double>();

  private final String _value1Name;

  private final String _value2Name;
  
  public TimeSeries2Double(final String name, final String units, final String value1Name, final  String value2Name)
  {
    super(name, units);
    _value1Name = value1Name;
    _value2Name = value2Name;
  }
  
  public void add(Long index, Double value1, Double value2)
  {
    _indices.add(index);
    _values1.add(value1);
    _values2.add(value2);
  }
  
  public Iterator<Double> getValues1()
  {
    return _values1.iterator();
  }
  
  public Iterator<Double> getValues2()
  {
    return _values2.iterator();
  }

  public String getValue1Name()
  {
    return _value1Name;
  }
  
  public String getValue2Name()
  {
    return _value2Name;
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
      System.out.println("i:" + _indices.get(i) + " v1:" + _values1.get(i) + " v2:" + _values2.get(i));
    }
  }
}
