package Debrief.Wrappers.Extensions.Measurements;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import org.eclipse.january.dataset.CompoundDataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.LongDataset;
import org.eclipse.january.metadata.AxesMetadata;
import org.eclipse.january.metadata.internal.AxesMetadataImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * time series that stores double measurements
 * 
 * @author ian
 * 
 */
public class TimeSeriesDatasetDouble2 extends TimeSeriesDatasetCore
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final String _value1Name;

  private final String _value2Name;

  public TimeSeriesDatasetDouble2(final String name, final String units,
      final String value1Name, final String value2Name, final long[] times,
      final double[] values1, final double[] values2)
  {
    super(name, units);
    _value1Name = value1Name;
    _value2Name = value2Name;
    
    // ok create the data items
    LongDataset dTimes = (LongDataset) DatasetFactory.createFromObject(times);
    DoubleDataset data1 = (DoubleDataset)DatasetFactory.createFromObject(values1);
    DoubleDataset data2 = (DoubleDataset)DatasetFactory.createFromObject(values2);
    
    // combine the two into a single compound dataset
    _data = DatasetUtils.createCompoundDataset(data1, data2);
    
    // put the time in as an axis
    AxesMetadata axis = new AxesMetadataImpl();
    axis.initialize(1);
    axis.setAxis(0, dTimes);
    _data.addMetadata(axis);

  }

  public Iterator<Double> getValues1()
  {
    CompoundDataset cd = (CompoundDataset) _data; 
    DoubleDataset dataset = (DoubleDataset) cd.getElements(0);
    return new DoubleIterator(dataset);
  }

  public Iterator<Double> getValues2()
  {
    CompoundDataset cd = (CompoundDataset) _data; 
    DoubleDataset v2Dataset = (DoubleDataset) cd.getElements(1);
    return new DoubleIterator(v2Dataset);
  }

  public double getValue1At(final int index)
  {
    CompoundDataset cd = (CompoundDataset) _data; 
    DoubleDataset dataset = (DoubleDataset) cd.getElements(0);
    return dataset.get(index);
  }

  public double getValue2At(final int index)
  {
    CompoundDataset cd = (CompoundDataset) _data; 
    DoubleDataset dataset = (DoubleDataset) cd.getElements(1);
    return dataset.get(index);
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
    LongDataset times = getTimes();
    System.out.println(":" + getName());
    final int len = times.getSize();
    for (int i = 0; i < len; i++)
    {
      System.out.println("i:" + times.get(i) + " v1:" + getValue1At(i)
          + " v2:" + getValue2At(i));
    }
  }
  
  public static class TestMe
  {
    @Before
    public void suppressSLF4JError() {
      PrintStream saved = System.err;
      try {
        System.setErr(new PrintStream(new OutputStream() {
          public void write(int b) {
          }
        }));
    
        LoggerFactory.getLogger(String.class);
    
      } finally {
        System.setErr(saved);
      }
    }
   
    @Test
    public void testCreate()
    {
      long[] times = new long[]{0L, 100L, 200L, 300L};
      double[] v1 = new double[]{12.2, 12.3, 12.4, 12.5};
      double[] v2 = new double[]{22.2, 22.3, 22.4, 22.5};
      
      LongDataset ld = (LongDataset) DatasetFactory.createFromObject(times);
      System.out.println(ld.toString(true));
      
      TimeSeriesDatasetDouble2 dd = new TimeSeriesDatasetDouble2("Test data", "units", "val1 name", "val2 name", times, v1, v2);
      dd.printAll();
    }
  }
}
