package info.limpet.impl;

import info.limpet.ICommand;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.measure.unit.Unit;

import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.ObjectDataset;
import org.eclipse.january.metadata.AxesMetadata;

public class DoubleListDocument extends Document<List<Double>>
{

  private final Unit<?> units;

  public DoubleListDocument(ObjectDataset dataset, ICommand predecessor,
      Unit<?> units)
  {
    super(dataset, predecessor);
    this.units = units;
  }

  public Unit<?> getUnits()
  {
    return units;
  }

  public boolean isQuantity()
  {
    return false;
  }

  public String toListing()
  {
    StringBuffer res = new StringBuffer();

    ObjectDataset dataset = (ObjectDataset) this.getDataset();
    final AxesMetadata axesMetadata =
        dataset.getFirstMetadata(AxesMetadata.class);

    DoubleDataset axisOne = null;
    DoubleDataset axisTwo = null;
    if (axesMetadata != null && axesMetadata.getAxes().length > 0)
    {
      axisOne = (DoubleDataset) axesMetadata.getAxes()[0];
      axisTwo = (DoubleDataset) axesMetadata.getAxes()[1];
    }

    res.append(dataset.getName() + "\n");

    int[] dims = dataset.getShape();
    if (dims.length != 2)
    {
      throw new IllegalArgumentException(
          "Should contain 2d data, but contains " + dims.length + " dims");
    }
    int xDim = dims[0];
    int yDim = dims[1];

    NumberFormat nf = new DecimalFormat(" 000.0;-000.0");
    res.append("       ");
    for (int j = 0; j < yDim; j++)
    {
      res.append(nf.format(axisTwo.get(0, j)) + "  ");
    }
    res.append("\n");

    for (int i = 0; i < xDim; i++)
    {
      res.append(nf.format(axisOne.get(i, 0)) + ": ");
      for (int j = 0; j < yDim; j++)
      {
        @SuppressWarnings("unchecked")
        List<Double> vals = (List<Double>) dataset.get(i, j);
        if (vals == null)
        {
          res.append("        ");
        }
        else
        {
          res.append(vals.size() + " items ");
//          for (Double d : vals)
//          {
//            res.append(d + ", ");
//          }
        }
      }
      res.append("\n");
    }

    res.append("\n");

    return res.toString();
  }

  public Iterator<?> getObjectIterator()
  {
    ObjectDataset od = (ObjectDataset) dataset;
    Object[] lists = od.getData();
    Iterable<Object> iterable = Arrays.asList(lists);
    return iterable.iterator();
  }

  @Override
  public Iterator<List<Double>> getIterator()
  {
    throw new IllegalArgumentException("We can't just iterate through a 2-D dataset");
  }
}
