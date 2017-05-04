/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.NarrativeViewer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.NarrativeViewer.actions.NarrativeViewerActions;
import org.mwc.cmap.NarrativeViewer.filter.ui.FilterDialog;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;

public class NarrativeViewer
{

  public static int getVisibleRows(final Grid grid)
  {
    final Rectangle rect = grid.getClientArea();
    final int iItemHeight = grid.getItemHeight();
    final int iHeaderHeight = grid.getHeaderHeight();
    final int iVisibleCount =
        (rect.height - iHeaderHeight + iItemHeight - 1) / iItemHeight;
    return iVisibleCount;
  }

  private final NarrativeViewerModel myModel;

  private NarrativeViewerActions myActions;
  private final GridTableViewer viewer;

  private final FilteredGrid filterGrid;

  public NarrativeViewer(final Composite parent,
      final IPreferenceStore preferenceStore)
  {
    final GridColumnLayout layout = new GridColumnLayout();

    filterGrid =
        new FilteredGrid(parent, SWT.V_SCROLL | SWT.MULTI | SWT.VIRTUAL, true)
        {

          @Override
          protected void updateGridData(final String text)
          {

            myModel.updateFilters();
            refresh();

          }

        };
    filterGrid.getGridComposite().setLayout(layout);
    viewer = filterGrid.getViewer();
    viewer.getGrid().setHeaderVisible(true);
    viewer.getGrid().setLinesVisible(true);

    viewer.setAutoPreferredHeight(true);

    myModel =
        new NarrativeViewerModel(viewer, preferenceStore, new EntryFilter()
        {

          @Override
          public boolean accept(final NarrativeEntry entry)
          {
            if (viewer != null && !viewer.getGrid().isDisposed())
            {
              final String filterString = filterGrid.getFilterString();
              if (filterString != null && !filterString.trim().isEmpty())
              {
                final Pattern pattern =
                    Pattern.compile(filterString, Pattern.CASE_INSENSITIVE);

                final AbstractColumn[] allColumns = myModel.getAllColumns();
                for (final AbstractColumn abstractColumn : allColumns)
                {
                  if (abstractColumn.isVisible())
                  {
                    final Object property = abstractColumn.getProperty(entry);
                    if (property instanceof String)
                    {
                      final Matcher matcher =
                          pattern.matcher((CharSequence) property);
                      if (matcher.find())
                      {
                        return true;
                      }

                    }
                  }
                }
                return false;
              }
            }

            return true;
          }
        });

    myModel.createTable(this, layout);

  }

  private void calculateGridRawHeight()
  {
    viewer.getGrid().setRedraw(false);
    viewer.setInput(new Object());
    viewer.getGrid().setRedraw(true);
  }

  public Composite getControl()
  {
    return filterGrid;
  }

  FilteredGrid getFilterGrid()
  {
    return filterGrid;
  }

  public NarrativeViewerModel getModel()
  {
    return myModel;
  }

  public GridTableViewer getViewer()
  {
    return viewer;
  }

  public NarrativeViewerActions getViewerActions()
  {
    if (myActions == null)
    {
      myActions = new NarrativeViewerActions(this);
    }
    return myActions;
  }

  public boolean isWrappingEntries()
  {
    return myModel.isWrappingEntries();
  }

  public void refresh()
  {
    // check we're not closing
    if (!viewer.getGrid().isDisposed() && viewer.getContentProvider() != null)
    {
      viewer.setInput(new Object());
    }
  }

  /**
   * the controlling time has updated
   * 
   * @param dtg
   *          the selected dtg
   */
  public void setDTG(final HiResDate dtg)
  {
    // find the previous entry (so we know if we're going up or down
    final ISelection curSel = getViewer().getSelection();
    Boolean goingDown = null;
    NarrativeEntry existingItem = null;
    if (curSel instanceof StructuredSelection)
    {
      final StructuredSelection sel = (StructuredSelection) curSel;
      if (sel.size() == 1)
      {
        final Object item = sel.getFirstElement();
        if (item instanceof NarrativeEntry)
        {
          existingItem = (NarrativeEntry) item;
          goingDown = existingItem.getDTG().lessThanOrEqualTo(dtg);
        }
      }
    }

    // find the table entry immediately after or on this DTG
    NarrativeEntry entry = null;

    // retrieve the list of visible rows
    final LinkedList<NarrativeEntry> rows = myModel.myVisibleRows;

    // step through them
    int index = 0;
    for (final Iterator<NarrativeEntry> entryIterator = rows.iterator(); entryIterator
        .hasNext();)
    {
      final NarrativeEntry narrativeEntry = entryIterator.next();

      // get the date
      final HiResDate dt = narrativeEntry.getDTG();

      // is this what we're looking for?
      if (dt.greaterThanOrEqualTo(dtg))
      {
        entry = narrativeEntry;
        break;
      }

      // let ourselves use the last entry, if we don't find one
      entry = narrativeEntry;

      index++;
    }

    // ok, try to select this entry
    if (entry != null)
    {
      if (!entry.equals(existingItem))
      {
        // ok, find the item we want to reveal
        final NarrativeEntry toReveal;

        // allow 1/3 of the visible rows either side
        final int offset = getVisibleRows(viewer.getGrid()) / 3;

        final int totalRows = rows.size();

        // do we know the previous entry?
        if (goingDown != null)
        {
          if (goingDown)
          {
            // ok, going down. We need to show a few rows after the entry
            if (index < totalRows - offset)
            {
              toReveal = rows.get(index + offset);
            }
            else
            {
              toReveal = null;
            }
          }
          else
          {
            // going up, we need to show a few rows before the entry
            if (index > offset)
            {
              final int toRetreive = index - offset;
              toReveal = rows.get(toRetreive);
            }
            else
            {
              toReveal = null;
            }
          }
        }
        else
        {
          toReveal = null;
        }

        // ok, go for it.
        setEntry(entry, toReveal);
      }
    }

  }

  /**
   * the controlling time has updated
   * 
   * @param dtg
   *          the selected dtg
   */
  public void
      setEntry(final NarrativeEntry entry, final NarrativeEntry toReveal)
  {
    try
    {
      viewer.getGrid().setRedraw(false);
      // select this item
      viewer.setSelection(new StructuredSelection(entry));

      // make sure the new item is visible
      if (toReveal != null)
      {
        viewer.reveal(toReveal);
      }
      else
      {
        viewer.reveal(entry);
      }
    }
    finally
    {
      viewer.getGrid().setRedraw(true);
    }
  }

  public void setInput(final IRollingNarrativeProvider entryWrapper)
  {
    myModel.setInput(entryWrapper);
    Display.getDefault().asyncExec(new Runnable()
    {
      @Override
      public void run()
      {
        refresh();
      }
    });
  }

  public void setSearchMode(final boolean checked)
  {
    filterGrid.setFilterMode(checked);
  }

  public void setTimeFormatter(final TimeFormatter timeFormatter)
  {
    myModel.setTimeFormatter(timeFormatter);
    viewer.refresh();
  }

  public void setWrappingEntries(final boolean shouldWrap)
  {
    if (myModel.setWrappingEntries(shouldWrap))
    {
      final GridColumn[] columns = getViewer().getGrid().getColumns();
      for (final GridColumn gridColumn : columns)
      {
        gridColumn.setWordWrap(shouldWrap);
      }
      calculateGridRawHeight();
    }
  }

  public void showFilterDialog(final Column column)
  {
    if (!myModel.hasInput())
    {
      return;
    }

    final EntryFilter filter = column.getFilter();
    if (filter == null)
    {
      return;
    }

    final FilterDialog dialog =
        new FilterDialog(viewer.getGrid().getShell(), myModel.getInput(),
            column);

    if (Window.OK == dialog.open())
    {
      dialog.commitFilterChanges();
      refresh();
    }
  }

}
