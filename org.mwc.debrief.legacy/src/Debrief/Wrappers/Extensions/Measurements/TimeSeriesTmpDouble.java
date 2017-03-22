package Debrief.Wrappers.Extensions.Measurements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** time series that stores double measurements
 * 
 * @author ian
 *
 */
public class TimeSeriesTmpDouble extends TimeSeriesTmpCore
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private final List<Double> _values1 = new ArrayList<Double>();


  
  public TimeSeriesTmpDouble(final String name, final String units)
  {
    super(name, units);
  }
  
  public void add(Long index, Double value1)
  {
    _indices.add(index);
    _values1.add(value1);
  }
  
  public Iterator<Double> getValues()
  {
    return _values1.iterator();
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
      System.out.println("i:" + _indices.get(i) + " v1:" + _values1.get(i));
    }
  }
}
