package Debrief.Wrappers.Extensions.Measurements;

import java.util.Iterator;
import java.util.List;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;

/** time series that stores double measurements
 * 
 * @author ian
 *
 */
public class TimeSeriesDatasetDouble extends TimeSeriesDatasetCore
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public TimeSeriesDatasetDouble(final String name, final String units, final List<Long> times, final List<Double> values)
  {
    super(name, units);
    
    // ok create the data items
    LongDataset dTimes = (LongDataset) DatasetFactory.createFromList(times);
    _data = (DoubleDataset)DatasetFactory.createFromList(values);
    
    // put the time in as an axis
    AxesMetadata axis = new AxesMetadataImpl();
    axis.initialize(1);
    axis.setAxis(0, dTimes);
    _data.addMetadata(axis);
  }

  
  public Iterator<Double> getValues()
  {
    return new DoubleIterator((DoubleDataset) _data);
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
    DoubleDataset dd = (DoubleDataset) _data;
    System.out.println(":" + getName());
    final int len = getTimes().getSize();
    for (int i = 0; i < len; i++)
    {
      System.out.println("i:" + getTimes().get(i) + " v1:" + dd.get(i));
    }
  }
}
