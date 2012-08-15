package org.mwc.cmap.grideditor.table.actons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.TableItem;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.grideditor.table.GridEditorTable;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

public class ExportToClipboardAction extends Action
{

	private static final String ACTION_TEXT = "Export data to clipboard";

	private final GridEditorTable myTableUI;

	private final ImageDescriptor exportImage;

	public ExportToClipboardAction(GridEditorTable tableUI)
	{
		super(ACTION_TEXT, AS_PUSH_BUTTON);
		myTableUI = tableUI;
		exportImage = CorePlugin.getImageDescriptor("icons/copy.png");
		setToolTipText(ACTION_TEXT);
		setEnabled(true);
		refreshWithTableUI();
	}

	@Override
	public void run()
	{
		int colCount = myTableUI.getTableViewer().getTable().getColumnCount();
		final String newline = System.getProperty("line.separator");

		StringBuffer outS = new StringBuffer();

		// headings
		for (int j = 1; j < colCount; j++)
		{
			GriddableItemDescriptor descriptor = myTableUI.getTableModel()
					.getColumnData(j).getDescriptor();
			String name = "Time";
			if (descriptor != null)
			{
				name = descriptor.getName();
			}
			outS.append(name + ",");
		}
		outS.append(newline);

		// ok, try to get teh data
		TableItem[] data = myTableUI.getTableViewer().getTable().getItems();
		for (int i = data.length - 1; i >= 0; i--)
		{
			TableItem tableItem = data[i];

			for (int j = 1; j < colCount; j++)
			{
				outS.append(tableItem.getText(j) + ",");
			}
			outS.append(newline);
		}

		// put the string on the clipboard
		CorePlugin.writeToClipboard(outS.toString());

	}

	public void refreshWithTableUI()
	{
		boolean isVis = myTableUI.isOnlyShowVisible();
		setChecked(isVis);
		setImageDescriptor(exportImage);
	}

}
