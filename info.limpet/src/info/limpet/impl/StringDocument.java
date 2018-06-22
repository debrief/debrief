package info.limpet.impl;

import info.limpet.ICommand;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.dataset.StringDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class StringDocument extends Document<String>
{
  
  public StringDocument(StringDataset dataset, ICommand predecessor)
  {
    super(dataset, predecessor);
  }

  public boolean isQuantity()
  {
    return false;
  }
  
  @Override
  public void setDataset(IDataset dataset)
  {
    if (dataset instanceof ObjectDataset)
    {
      super.setDataset(dataset);
    }
    else
    {
      throw new IllegalArgumentException("We only store object datasets");
    }
  }

  @Override
  public String toListing()
  {
    StringBuffer res = new StringBuffer();
    
    StringDataset dataset = (StringDataset) this.getDataset();
    final AxesMetadata axesMetadata =
        dataset.getFirstMetadata(AxesMetadata.class);
    final IndexIterator iterator = dataset.getIterator();

    final DoubleDataset axisDataset;
    if (axesMetadata != null && axesMetadata.getAxes().length > 0)
    {
      DoubleDataset doubleAxis = (DoubleDataset) axesMetadata.getAxes()[0];
      axisDataset = doubleAxis != null ? doubleAxis : null;
    }
    else
    {
      axisDataset = null;
    }

    res.append(dataset.getName() + ":\n");
    while (iterator.hasNext())
    {
      final String indexVal;
      if (axisDataset != null)
      {
        indexVal = "" + axisDataset.getString(iterator.index);
      }
      else
      {
        indexVal = "N/A";
      }

      res.append(indexVal + " : "
          + dataset.getString(iterator.index));
      res.append(";");
    }
    res.append("\n");
    
    return res.toString();
  }

  public Double interpolateValue(long i, InterpMethod linear)
  {
    throw new IllegalArgumentException("Not valid for collections of Strings");
  }

  public String getString(int i)
  {
    StringDataset od = (StringDataset) dataset;
    return od.getString(i);
  }

  @Override
  public Iterator<String> getIterator()
  {
    StringDataset od = (StringDataset) dataset;
    String[] strings = od.getData();
    Iterable<String> iterable = Arrays.asList(strings);
    return iterable.iterator();
  }

}
