package org.mwc.debrief.core.providers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.debrief.core.editors.ViewContentProvider.ExtensionContentProvider;

import Debrief.Wrappers.Extensions.Measurements.CoreDataset;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class MeasuredDataProvider implements ExtensionContentProvider
{

  @Override
  public List<EditableWrapper> itemsFor(Object subject, EditableWrapper parent, Layers layers)
  {
    List<EditableWrapper> res = null;
    
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
        
        
        // ok, wrap this item.
        EditableWrapper wr = new EditableWrapper(plottable, parent, layers);
        
        if(res == null)
        {
          res = new ArrayList<EditableWrapper>();
          res.add(wr);
        }
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

  protected static class FolderWrapper implements Editable
  {
    
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
  }

}
