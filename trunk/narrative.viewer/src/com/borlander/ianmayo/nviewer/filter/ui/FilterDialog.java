package com.borlander.ianmayo.nviewer.filter.ui;

import java.util.TreeSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;

import com.borlander.ianmayo.nviewer.Column;
import com.borlander.ianmayo.nviewer.ColumnFilter;

public class FilterDialog extends Dialog {

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

	public FilterDialog(Shell parent, IRollingNarrativeProvider dataSource, Column column) {
		super(parent);
		myCaption = "Filter by: " + column.getColumnName();
		myFilter = column.getFilter(); 

		mySelectedItems = new TreeSet<String>(myFilter.getAllowedValues());
		myItemsToSelect = new TreeSet<String>();

		if (dataSource != null){
			for (NarrativeEntry entry : dataSource.getNarrativeHistory(new String[]{})) {
				String nextValue = myFilter.getFilterValue(entry);
				if (!mySelectedItems.contains(nextValue)) {
					myItemsToSelect.add(nextValue);
				}
			}
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		getShell().setText(myCaption);
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		dialogArea.setLayout(new GridLayout(3, false));

		myItemsToSelectList = createItemsList(dialogArea, myItemsToSelect);

		Composite moveButtonsBar = new Composite(dialogArea, SWT.NONE);
		moveButtonsBar.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		moveButtonsBar.setLayout(new GridLayout(1, true));

		createMoveButton(moveButtonsBar, ">", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addOne();
			}
		});
		createMoveButton(moveButtonsBar, "<", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeOne();
			}
		});
		createMoveButton(moveButtonsBar, ">>", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addAll();
			}
		});
		createMoveButton(moveButtonsBar, "<<", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeAll();
			}
		});

		mySelectedItemsList = createItemsList(dialogArea, mySelectedItems);
		return dialogArea;
	}

	private void addOne() {
		moveOne(myItemsToSelect, mySelectedItems, myItemsToSelectList, mySelectedItemsList);
	}

	private void removeOne() {
		moveOne(mySelectedItems, myItemsToSelect, mySelectedItemsList, myItemsToSelectList);
	}

	private void moveOne(TreeSet<String> itemsFrom, TreeSet<String> itemsTo, List listForm, List listTo) {
		if (listForm.getSelectionIndex() == -1) {
			return;
		}
		String movedItem = listForm.getItem(listForm.getSelectionIndex());

		itemsTo.add(movedItem);
		int i = 0;
		for (String selectedItem : itemsTo) {
			if (selectedItem == movedItem) {
				listTo.add(movedItem, i);
				break;
			}
			i++;
		}

		itemsFrom.remove(movedItem);
		listForm.remove(listForm.getSelectionIndex());
	}

	private void addAll() {
		moveAll(myItemsToSelect, mySelectedItems, myItemsToSelectList, mySelectedItemsList);
	}

	private void removeAll() {
		moveAll(mySelectedItems, myItemsToSelect, mySelectedItemsList, myItemsToSelectList);
	}

	private void moveAll(TreeSet<String> itemsFrom, TreeSet<String> itemsTo, List listForm, List listTo) {
		itemsTo.addAll(itemsFrom);
		itemsFrom.clear();

		listForm.removeAll();

		listTo.removeAll();
		for (String item : itemsTo) {
			listTo.add(item);
		}
	}

	private List createItemsList(Composite parent, Iterable<String> items) {
		List result = new List(parent, SWT.BORDER);
		result.setLayoutData(new GridData(ITEM_LIST_WIDTH, ITEM_LIST_HEIGHT));
		for (String item : items) {
			result.add(item);
		}
		return result;
	}

	private Button createMoveButton(Composite parent, String caption, SelectionListener selectionListener) {
		Button result = new Button(parent, SWT.PUSH);
		result.setText(caption);
		result.setLayoutData(new GridData(MOVE_BUTTON_WIDTH, MOVE_BUTTON_HEIGHT));
		result.addSelectionListener(selectionListener);
		return result;
	}

	public void commitFilterChanges() {
		myFilter.setAllowedValues(mySelectedItems);
	}

}
