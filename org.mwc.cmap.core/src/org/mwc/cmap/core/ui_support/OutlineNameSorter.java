package org.mwc.cmap.core.ui_support;

import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.mwc.cmap.core.property_support.EditableWrapper;

import Debrief.Wrappers.SensorWrapper;
import MWC.GUI.Editable;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

public class OutlineNameSorter extends ViewerComparator
{

  public static class EditableComparer implements Comparator<Editable>
  {
    @Override
    public int compare(final Editable arg0, final Editable arg1)
    {
      return compareEditables(arg0, arg1);
    }
  }

  public static interface NameSortHelper
  {
    boolean sortByDate();
  }

  @SuppressWarnings("unchecked")
  protected static int compareEditables(final Editable e1, final Editable e2)
  {
    final int res;

    // ha. if they're watchables, sort them in time order
    if ((e1 instanceof Watchable) && (e2 instanceof Watchable))
    {
      final Watchable wa = (Watchable) e1;
      final Watchable wb = (Watchable) e2;

      // hmm, just check we have times
      final HiResDate ha = wa.getTime();
      final HiResDate hb = wb.getTime();

      if ((ha != null) && (hb != null))
      {
        res = wa.getTime().compareTo(wb.getTime());
      }
      else
      {
        res = e1.getName().compareTo(e2.getName());
      }
    }
    else if ((e1 instanceof Comparable) && (e2 instanceof Comparable))
    {
      @SuppressWarnings("rawtypes")
      final Comparable p1c = (Comparable) e1;
      @SuppressWarnings("rawtypes")
      final Comparable p2c = (Comparable) e2;
      res = p1c.compareTo(p2c);
    }
    else
    {
      final String p1Name = e1.getName();
      final String p2Name = e2.getName();
      res = p1Name.compareTo(p2Name);
    }

    return res;
  }

  private final NameSortHelper _sortHelper;

  public OutlineNameSorter()
  {
    this(new NameSortHelper()
    {

      @Override
      public boolean sortByDate()
      {
        return false;
      }
    });
  }

  public OutlineNameSorter(final NameSortHelper helper)
  {
    _sortHelper = helper;
  }

  @SuppressWarnings("unchecked")
  public int compare(final Object e1, final Object e2)
  {
    final int res;

    if ((e1 instanceof Comparable) && (e2 instanceof Comparable))
    {
      // special case. Just double-check they aren't sensor wrappers
      if (e1 instanceof EditableWrapper && e2 instanceof EditableWrapper)
      {
        final EditableWrapper p1 = (EditableWrapper) e1;
        final EditableWrapper p2 = (EditableWrapper) e2;

        if (p1.getEditable() instanceof SensorWrapper
            && p2.getEditable() instanceof SensorWrapper)
        {
          return compareSensors((SensorWrapper) p1.getEditable(),
              (SensorWrapper) p2.getEditable());
        }
        else
        {
          return compareEditables(p1.getEditable(), p2.getEditable());
        }
      }
      else
      {
        // just see if we have sorted editables
        final Comparable<Object> w1 = (Comparable<Object>) e1;
        final Comparable<Object> w2 = (Comparable<Object>) e2;
        res = w1.compareTo(w2);
      }
    }
    else
    {
      if (e1 instanceof EditableWrapper && e2 instanceof EditableWrapper)
      {
        final EditableWrapper p1 = (EditableWrapper) e1;
        final EditableWrapper p2 = (EditableWrapper) e2;

        return compareEditables(p1.getEditable(), p2.getEditable());
      }
      else
      {
        return e1.toString().compareTo(e2.toString());
      }
    }

    return res;
  }

  @Override
  public int compare(final Viewer viewer, final Object e1, final Object e2)
  {
    return compare(e1, e2);
  }

  private int compareSensors(final SensorWrapper s1, final SensorWrapper s2)
  {
    // hmm, just check we have times
    final HiResDate ha = s1.getStartDTG();
    final HiResDate hb = s2.getStartDTG();

    final int res;
    if (_sortHelper.sortByDate() && (ha != null) && (hb != null))
    {
      res = ha.compareTo(hb);
    }
    else
    {
      res = s1.getName().compareTo(s2.getName());
    }

    return res;
  }
}
