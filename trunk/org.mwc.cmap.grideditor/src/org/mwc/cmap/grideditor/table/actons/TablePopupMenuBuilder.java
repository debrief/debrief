package org.mwc.cmap.grideditor.table.actons;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Table;
import org.mwc.cmap.grideditor.table.GridEditorTable;


public class TablePopupMenuBuilder {

	private MenuManager myMenuManager;

	private final GridEditorActionGroup myActionGroup;

	public TablePopupMenuBuilder(GridEditorTable tableUI, GridEditorActionGroup actionGroup) {
		myActionGroup = actionGroup;
		myMenuManager = new MenuManager();
		myMenuManager.setRemoveAllWhenShown(true);
		myMenuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				myActionGroup.fillContextMenu(manager);
			}
		});
		Table table = tableUI.getTableViewer().getTable();
		table.setMenu(myMenuManager.createContextMenu(table));
	}
}
