package Debrief.Wrappers.Extensions.Measurements;

import java.io.Serializable;
import java.util.ArrayList;

/** hold a set of datasets, or child stores
 * 
 * @author ian
 *
 */
public class DataFolder extends ArrayList<DataItem> implements DataItem, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static final String DEFAULT_NAME = "Measurements";
  
  private String _name;

  public DataFolder()
  {
    this(DEFAULT_NAME);
  }
  
  public DataFolder(String name)
  {
    _name = name;
  }
  
  public void printAll()
  {
    System.out.println("=="  + _name);
    for(DataItem item: this)
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
    for(DataItem item: this)
    {
      if(item.getName().equals(string))
      {
        res = item;
        break;
      }
    }
    return res;
  }
}
