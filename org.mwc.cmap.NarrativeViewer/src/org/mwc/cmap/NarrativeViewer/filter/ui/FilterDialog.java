/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.NarrativeViewer.filter.ui;

import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.NarrativeViewer.Column;
import org.mwc.cmap.NarrativeViewer.ColumnFilter;

import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;


public class FilterDialog extends Dialog
{

    private static final int ITEM_LIST_WIDTH = 150;
    private static final int ITEM_LIST_HEIGHT = 200;
    private static final int MOVE_BUTTON_WIDTH = 30;
    private static final int MOVE_BUTTON_HEIGHT = 20;

    private final String myCaption;
    private final TreeSet<String> myItemsToSelect;
    private final TreeSet<String> mySelectedItems;

    private final ColumnFilter myFilter;

    private List myItemsToSelectList;
    private List mySelectedItemsList;

    public FilterDialog(final Shell parent, final IRollingNarrativeProvider dataSource,
            final Column column)
    {
        super(parent);
        myCaption = "Filter by: " + column.getColumnName();
        myFilter = column.getFilter();

        mySelectedItems = new TreeSet<String>(myFilter.getAllowedValues());
        myItemsToSelect = new TreeSet<String>();

        // check we have some data
        if (dataSource != null)
        {
            for (final NarrativeEntry entry : dataSource
                    .getNarrativeHistory(new String[] {}))
            {
                final String nextValue = myFilter.getFilterValue(entry);
                // check that we have a value for this entry
                if (nextValue != null)
                {
                    if (!mySelectedItems.contains(nextValue))
                    {
                        myItemsToSelect.add(nextValue);
                    }
                }
            }
        }
    }

    @Override
    protected Control createContents(final Composite parent)
    {
        getShell().setText(myCaption);
        return super.createContents(parent);
    }

    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite dialogArea1 = (Composite) super.createDialogArea(parent);
        dialogArea1.setLayout(new GridLayout(3, false));
        
        Label lab1 = new Label(dialogArea1, SWT.NONE);
        lab1.setText("Available items:");
        lab1 = new Label(dialogArea1, SWT.NONE);
        lab1.setText(" ");
        lab1 = new Label(dialogArea1, SWT.NONE);
        lab1.setText("Show entries matching:");

        myItemsToSelectList = createItemsList(dialogArea1, myItemsToSelect);

        final Composite moveButtonsBar = new Composite(dialogArea1, SWT.NONE);
        moveButtonsBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
                false, false));
        moveButtonsBar.setLayout(new GridLayout(1, true));

        createMoveButton(moveButtonsBar, ">", new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                addOne();
            }
        });
        createMoveButton(moveButtonsBar, "<", new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                removeOne();
            }
        });
        createMoveButton(moveButtonsBar, ">>", new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                addAll();
            }
        });
        createMoveButton(moveButtonsBar, "<<", new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                removeAll();
            }
        });

        mySelectedItemsList = createItemsList(dialogArea1, mySelectedItems);
        
        // add double-click jumping for the lists
        myItemsToSelectList.addMouseListener(new MouseAdapter(){
            public void mouseDoubleClick(final MouseEvent e)
            {
                super.mouseDoubleClick(e);
                // and handle it
                addOne();
            }
        });
        mySelectedItemsList.addMouseListener(new MouseAdapter(){
            public void mouseDoubleClick(final MouseEvent e)
            {
                super.mouseDoubleClick(e);
                // and handle it
                removeOne();
            }
        });
        
        return dialogArea1;
    }

    void addOne()
    {
        moveOne(myItemsToSelect, mySelectedItems, myItemsToSelectList,
                mySelectedItemsList);
    }

    void removeOne()
    {
        moveOne(mySelectedItems, myItemsToSelect, mySelectedItemsList,
                myItemsToSelectList);
    }

    private void moveOne(final TreeSet<String> itemsFrom, final TreeSet<String> itemsTo,
            final List listForm, final List listTo)
    {
        if (listForm.getSelectionIndex() == -1)
        {
            return;
        }
        final String movedItem = listForm.getItem(listForm.getSelectionIndex());

        itemsTo.add(movedItem);
        int i = 0;
        for (final String selectedItem : itemsTo)
        {
            if (selectedItem == movedItem)
            {
                listTo.add(movedItem, i);
                break;
            }
            i++;
        }

        itemsFrom.remove(movedItem);
        listForm.remove(listForm.getSelectionIndex());
    }

    void addAll()
    {
        moveAll(myItemsToSelect, mySelectedItems, myItemsToSelectList,
                mySelectedItemsList);
    }

    void removeAll()
    {
        moveAll(mySelectedItems, myItemsToSelect, mySelectedItemsList,
                myItemsToSelectList);
    }

    private void moveAll(final TreeSet<String> itemsFrom, final TreeSet<String> itemsTo,
            final List listForm, final List listTo)
    {
        itemsTo.addAll(itemsFrom);
        itemsFrom.clear();

        listForm.removeAll();

        listTo.removeAll();
        for (final String item : itemsTo)
        {
            listTo.add(item);
        }
    }

    private List createItemsList(final Composite parent, final Iterable<String> items)
    {
        final List result = new List(parent, SWT.BORDER);
        result.setLayoutData(new GridData(ITEM_LIST_WIDTH, ITEM_LIST_HEIGHT));
        for (final String item : items)
        {
            result.add(item);
        }
        
        return result;
    }

    private Button createMoveButton(final Composite parent, final String caption,
            final SelectionListener selectionListener)
    {
        final Button result = new Button(parent, SWT.PUSH);
        result.setText(caption);
        result
                .setLayoutData(new GridData(MOVE_BUTTON_WIDTH,
                        MOVE_BUTTON_HEIGHT));
        result.addSelectionListener(selectionListener);
        return result;
    }

    public void commitFilterChanges()
    {
        myFilter.setAllowedValues(mySelectedItems);
    }

}
