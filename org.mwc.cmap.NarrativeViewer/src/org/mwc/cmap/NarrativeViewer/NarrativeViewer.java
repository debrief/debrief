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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.mwc.cmap.NarrativeViewer.actions.NarrativeViewerActions;
import org.mwc.cmap.NarrativeViewer.filter.ui.FilterDialog;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;

public class NarrativeViewer
{

  final NarrativeViewerModel myModel;
  private NarrativeViewerActions myActions;

  final TableViewer viewer;

  public NarrativeViewer(final Composite parent,
      final IPreferenceStore preferenceStore)
  {
    TableColumnLayout layout = new TableColumnLayout(); 
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(layout);
    viewer = new TableViewer(composite, SWT.V_SCROLL | SWT.FULL_SELECTION);
    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().setLinesVisible(true);
    myModel = new NarrativeViewerModel(preferenceStore);

   
    myModel.createTable(viewer,layout);

    
    //
    // addCellDoubleClickListener(new KTableCellDoubleClickAdapter()
    // {
    // public void fixedCellDoubleClicked(final int col, final int row, final int statemask)
    // {
    // final Column column = myModel.getVisibleColumn(col);
    // showFilterDialog(column);
    // final ColumnFilter filter = column.getFilter();
    // if (filter != null)
    // {
    //
    // }
    // }
    // });
  }

 

  public TableViewer getViewer()
  {
    return viewer;
  }
  public Composite getControl()
  {
    return viewer.getTable().getParent();
  }

  public NarrativeViewerActions getViewerActions()
  {
    if (myActions == null)
    {
      myActions = new NarrativeViewerActions(this);
    }
    return myActions;
  }

  public void showFilterDialog(final Column column)
  {
    if (!myModel.hasInput())
    {
      return;
    }

    final ColumnFilter filter = column.getFilter();
    if (filter == null)
    {
      return;
    }

    final FilterDialog dialog =
        new FilterDialog(viewer.getTable().getShell(), myModel.getInput(),
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
    viewer.setInput(new Object());
    refresh();
  }

  public void setTimeFormatter(final TimeFormatter timeFormatter)
  {
    myModel.setTimeFormatter(timeFormatter);
    viewer.refresh();
  }

  public void refresh()
  {
    viewer.refresh();
  }

  public boolean isWrappingEntries()
  {
    return myModel.isWrappingEntries();
  }

  public void setWrappingEntries(final boolean shouldWrap)
  {
    if (myModel.setWrappingEntries(shouldWrap))
    {
      refresh();
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
    viewer.setSelection(new StructuredSelection(entry));

  }

  public NarrativeViewerModel getModel()
  {
    return myModel;
  }

}
