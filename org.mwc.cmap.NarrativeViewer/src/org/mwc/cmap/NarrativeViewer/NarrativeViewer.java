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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridColumnLayout;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
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

  private final NarrativeViewerModel myModel;
  private final NarrativeViewerActions myActions;

  private final GridTableViewer viewer;
  private final FilteredGrid filterGrid;

  public NarrativeViewer(final Composite parent,
      final IPreferenceStore preferenceStore)
  {
    GridColumnLayout layout = new GridColumnLayout();

    filterGrid =
        new FilteredGrid(parent, SWT.V_SCROLL | SWT.BORDER | SWT.MULTI |SWT.VIRTUAL , true)
        {

          @Override
          protected void updateGridData(String text)
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
    
    myActions = new NarrativeViewerActions(this);

    myModel = new NarrativeViewerModel(preferenceStore, new EntryFilter()
    {

      @Override
      public boolean accept(NarrativeEntry entry)
      {

        
        if (viewer != null && !viewer.getGrid().isDisposed())
        {
          String filterString = filterGrid.getFilterString();
          if (filterString != null && !filterString.trim().isEmpty())
          {
            Pattern pattern = Pattern.compile(filterString, Pattern.CASE_INSENSITIVE);
          
            AbstractColumn[] allColumns = myModel.getAllColumns();
            for (AbstractColumn abstractColumn : allColumns)
            {
              if (abstractColumn.isVisible())
              {
                Object property = abstractColumn.getProperty(entry);
                if(property instanceof String)
                {
                  Matcher matcher = pattern.matcher((CharSequence) property);
                  if(matcher.find())
                    return true;
                        
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

  public GridTableViewer getViewer()
  {
    return viewer;
  }

  public Composite getControl()
  {
    return filterGrid;
  }

  FilteredGrid getFilterGrid()
  {
    return filterGrid;
  }

  public NarrativeViewerActions getViewerActions()
  {
    return myActions;
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

    if (Dialog.OK == dialog.open())
    {
      dialog.commitFilterChanges();
      refresh();
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

  public void setTimeFormatter(final TimeFormatter timeFormatter)
  {
    myModel.setTimeFormatter(timeFormatter);
    viewer.refresh();
  }

  public void refresh()
  {
    // check we're not closing
    if (!viewer.getGrid().isDisposed()
        && viewer.getContentProvider() != null)
    {
      viewer.setInput(new Object());
    }
  }

  private void calculateGridRawHeight()
  {
    ISelection selection = viewer.getSelection();
    viewer.getGrid().setRedraw(false);
    viewer.setInput(new Object());
    viewer.setSelection(selection);
    viewer.getGrid().setRedraw(true);
  }

  public boolean isWrappingEntries()
  {
    return myModel.isWrappingEntries();
  }

  public void setWrappingEntries(final boolean shouldWrap)
  {
    if (myModel.setWrappingEntries(shouldWrap))
    {
      GridColumn[] columns = getViewer().getGrid().getColumns();
      for (GridColumn gridColumn : columns)
      {
        gridColumn.setWordWrap(shouldWrap);
      }
      calculateGridRawHeight();
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
    // find the table entry immediately after or on this DTG
    NarrativeEntry entry = null;

    // retrieve the list of visible rows
    final LinkedList<NarrativeEntry> visEntries = myModel.myVisibleRows;

    // step through them
    for (final Iterator<NarrativeEntry> entryIterator = visEntries.iterator(); entryIterator
        .hasNext();)
    {
      final NarrativeEntry narrativeEntry =
          (NarrativeEntry) entryIterator.next();

      // get the date
      final HiResDate dt = narrativeEntry.getDTG();

      // is this what we're looking for?
      if (dt.greaterThanOrEqualTo(dtg))
      {
        entry = narrativeEntry;
        break;
      }

    }

    // ok, try to select this entry
    if (entry != null)
    {
      setEntry(entry);
    }

  }

  /**
   * the controlling time has updated
   * 
   * @param dtg
   *          the selected dtg
   */
  public void setEntry(final NarrativeEntry entry)
  {
    // select this item
    viewer.setSelection(new StructuredSelection(entry));

    // make sure the new item is visible
    viewer.reveal(entry);
  }

  public NarrativeViewerModel getModel()
  {
    return myModel;
  }

}
