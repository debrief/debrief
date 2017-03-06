package org.mwc.debrief.core.providers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import Debrief.Wrappers.Extensions.ExtensionContentProvider;
import Debrief.Wrappers.Extensions.Measurements.CoreDataset;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class MeasuredDataProvider implements ExtensionContentProvider
{


  @Override
  public List<Editable> itemsFor(Object subject)
  {
    return getItemsFor(subject);
  }
  
  private static List<Editable> getItemsFor(Object subject)
  {
    List<Editable> res = null;
    
    if(subject instanceof DataFolder)
    {
      DataFolder folder = (DataFolder) subject;
      
      // ok, go for it.
      for(DataItem item: folder)
      {
        // produce an editable from this data item
        final Editable plottable;

        if(item instanceof DataFolder)
        {
          DataFolder df = (DataFolder) item;
          plottable = new FolderWrapper(df);
        }
        else if(item instanceof CoreDataset<?,?>)
        {
          CoreDataset<?,?> set = (CoreDataset<?, ?>) item;
          plottable = new DatasetWrapper(set);
        }
        else
        {
          System.err.println("unexpected data found in data folder");
          plottable = null;
        }
        
        if(res == null)
        {
          res = new ArrayList<Editable>();
        }
        res.add(plottable);
      }
    }
    
    return res;
  }
  
  protected static class DatasetWrapper implements Editable
  {
    
    private CoreDataset<?,?> _data;

    public DatasetWrapper(CoreDataset<?,?> folder)
    {
      _data = folder;
    }

    @Override
    public String getName()
    {
      return _data.getName();
    }

    public String toString()
    {
      return getName() + " (" + _data.size() + " items)";
    }
    
    @Override
    public boolean hasEditor()
    {
      return false;
    }

    @Override
    public EditorType getInfo()
    {
      return null;
    }
  }

  protected static class FolderWrapper implements Editable, Layer
  {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DataFolder _folder;

    public FolderWrapper(DataFolder folder)
    {
      _folder = folder;
    }

    public String toString()
    {
      return getName() + " (" + _folder.size() + " items)";
    }
    
    @Override
    public String getName()
    {
      return _folder.getName();
    }

    @Override
    public boolean hasEditor()
    {
      return false;
    }

    @Override
    public EditorType getInfo()
    {
      return null;
    }

    @Override
    public boolean getVisible()
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public double rangeFrom(WorldLocation other)
    {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public int compareTo(Plottable o)
    {
      // TODO Auto-generated method stub
      return 0;
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
    public void paint(CanvasType dest)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public WorldArea getBounds()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void setName(String val)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public boolean hasOrderedChildren()
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public int getLineThickness()
    {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public void add(Editable point)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void removeElement(Editable point)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public Enumeration<Editable> elements()
    {
      Vector<Editable> res = new Vector<Editable>();

      // get the folder contents
      List<Editable> items = getItemsFor(_folder);
      
      // put into our vector
      res.addAll(items);
      
      return res.elements();
    }

    @Override
    public void setVisible(boolean val)
    {
    }
  }

}
