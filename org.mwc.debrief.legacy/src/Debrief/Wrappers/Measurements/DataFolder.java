package Debrief.Wrappers.Measurements;

import java.util.Enumeration;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GenericData.WorldArea;

/** hold a set of datasets, or child stores
 * 
 * @author ian
 *
 */
public class DataFolder extends Plottables implements Plottable, DataItem, Layer
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
    Enumeration<Editable> numer = elements();
    while (numer.hasMoreElements())
    {
      Editable editable = (Editable) numer.nextElement();
      if(editable instanceof DataFolder)
      {
        DataFolder df = (DataFolder) editable;
        df.printAll();
      }
      else if (editable instanceof CoreDataset)
      {
        CoreDataset<?,?> dd = (CoreDataset<?,?>) editable;
        dd.printAll();
      }
    }
  }
  
  @Override
  public String getName()
  {
    return _name;
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

  public DataItem get(String string)
  {
    DataItem res = null;
    Enumeration<Editable> numer = elements();
    while (numer.hasMoreElements())
    {
      DataItem thisE = (DataItem) numer.nextElement();
      if(thisE.getName().equals(string))
      {
        res = thisE;
        break;
      }
    }
    return res;
  }

  @Override
  public void exportShape()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void append(Layer other)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean hasOrderedChildren()
  {
    return false;
  }

  @Override
  public int getLineThickness()
  {
    // TODO Auto-generated method stub
    return 0;
  }
}
