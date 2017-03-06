package org.mwc.debrief.core.providers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import Debrief.Wrappers.Extensions.ExtensionContentProvider;
import Debrief.Wrappers.Extensions.Measurements.CoreDataset;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
import MWC.GUI.Editable;
import MWC.GUI.HasChildData;

public class MeasuredDataProvider implements ExtensionContentProvider
{

  protected static class DatasetWrapper implements Editable
  {
    final private CoreDataset<?, ?> _data;

    public DatasetWrapper(final CoreDataset<?, ?> folder)
    {
      _data = folder;
    }

    @Override
    public EditorType getInfo()
    {
      return null;
    }

    @Override
    public String getName()
    {
      return _data.getName();
    }

    @Override
    public boolean hasEditor()
    {
      return false;
    }

    @Override
    public String toString()
    {
      return getName() + " (" + _data.size() + " items)";
    }
  }

  protected static class FolderWrapper implements Editable, HasChildData
  {
    final private DataFolder _folder;

    public FolderWrapper(final DataFolder folder)
    {
      _folder = folder;
    }

    @Override
    public Enumeration<Editable> elements()
    {
      final Vector<Editable> res = new Vector<Editable>();

      // get the folder contents
      final List<Editable> items = getItemsFor(_folder);

      // put into our vector
      res.addAll(items);

      return res.elements();
    }

    @Override
    public EditorType getInfo()
    {
      return null;
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
    public boolean hasOrderedChildren()
    {
      return false;
    }

    @Override
    public String toString()
    {
      return getName() + " (" + _folder.size() + " items)";
    }
  }

  private static List<Editable> getItemsFor(final Object subject)
  {
    List<Editable> res = null;

    if (subject instanceof DataFolder)
    {
      final DataFolder folder = (DataFolder) subject;

      // ok, go for it.
      for (final DataItem item : folder)
      {
        // produce an editable from this data item
        final Editable plottable;

        if (item instanceof DataFolder)
        {
          final DataFolder df = (DataFolder) item;
          plottable = new FolderWrapper(df);
        }
        else if (item instanceof CoreDataset<?, ?>)
        {
          final CoreDataset<?, ?> set = (CoreDataset<?, ?>) item;
          plottable = new DatasetWrapper(set);
        }
        else
        {
          System.err.println("unexpected data found in data folder");
          plottable = null;
        }

        if (res == null)
        {
          res = new ArrayList<Editable>();
        }
        res.add(plottable);
      }
    }

    return res;
  }

  @Override
  public List<Editable> itemsFor(final Object subject)
  {
    return getItemsFor(subject);
  }
}
