package Debrief.Wrappers.Extensions.Measurements;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * hold a set of datasets, or child stores
 * 
 * @author ian
 * 
 */
public class DataFolder extends ArrayList<DataItem> implements DataItem,
    Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static final String DEFAULT_NAME = "Additional data";

  private String _name;

  private DataFolder _parent;

  public DataFolder()
  {
    this(DEFAULT_NAME);
  }

  public static interface DatasetOperator
  {
    void process(ITimeSeriesCore dataset);
  }
  
  public void walkThisDataset(DatasetOperator operator)
  {
    for(DataItem item: this)
    {
      if(item instanceof TimeSeriesTmpDouble2)
      {
        ITimeSeriesCore ts = (ITimeSeriesCore) item;
        operator.process(ts);
      }
      else if(item instanceof DataFolder)
      {
        DataFolder folder = (DataFolder) item;
        folder.walkThisDataset(operator);
      }
    }
  }
  
  
  @Override
  public boolean add(DataItem item)
  {
    // hey, set the parent folder for it
    item.setParent(this);

    // and handle the store
    return super.add(item);
  }
  
  @Override
  public boolean remove(Object item)
  {
    // clear the subject's parent link
    if(item instanceof DataItem)
    {
      DataItem di = (DataItem) item;
      di.setParent(null);
    }
    
    // ok, do the remove
    return super.remove(item);
  }

  public DataFolder(String name)
  {
    _name = name;
  }

  public void printAll()
  {
    System.out.println("==" + _name);
    for (DataItem item : this)
    {
      item.printAll();
    }
  }

  @Override
  public String getName()
  {
    return _name;
  }

  public DataItem get(String string)
  {
    DataItem res = null;
    for (DataItem item : this)
    {
      if (item.getName().equals(string))
      {
        res = item;
        break;
      }
    }
    return res;
  }

  public void setName(String name)
  {
    _name = name;
  }

  /**
   * sometimes it's useful to know the parent folder for a dataset
   * 
   * @param parent
   */
  public void setParent(DataFolder parent)
  {
    _parent = parent;
  }

  public DataFolder getParent()
  {
    return _parent;
  }
}
