package Debrief.Wrappers.Extensions.Measurements.Wrappers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import Debrief.Wrappers.Extensions.ExtensionContentProvider;
import Debrief.Wrappers.Extensions.Measurements.DataFolder;
import Debrief.Wrappers.Extensions.Measurements.DataItem;
import Debrief.Wrappers.Extensions.Measurements.TimeSeriesCore;
import MWC.GUI.Editable;

public class MeasuredDataProvider implements ExtensionContentProvider, Serializable
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static List<Editable> getItemsFor(final Object subject)
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
        else if (item instanceof TimeSeriesCore)
        {
          final TimeSeriesCore set = (TimeSeriesCore) item;
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
