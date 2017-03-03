package Debrief.Wrappers.Measurements;

import java.util.ArrayList;
import java.util.List;

import MWC.GUI.CanvasType;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldArea;

/** hold a set of datasets, or child stores
 * 
 * @author ian
 *
 */
public class DataFolder extends PlainWrapper implements DataItem
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  List<DataItem> _items = new ArrayList<DataItem>();
  private String _name;

  public DataFolder(String name)
  {
    _name = name;
  }
  
  @Override
  public void paint(CanvasType dest)
  {
    // TODO Auto-generated method stub
    
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
}
