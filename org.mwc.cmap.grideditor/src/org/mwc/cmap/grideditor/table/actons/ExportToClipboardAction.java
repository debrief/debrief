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

	public ExportToClipboardAction(final GridEditorTable tableUI)
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
		final int colCount = myTableUI.getTableViewer().getTable().getColumnCount();
		final String newline = System.getProperty("line.separator");

		final StringBuffer outS = new StringBuffer();

		// headings
		for (int j = 1; j < colCount; j++)
		{
			final GriddableItemDescriptor descriptor = myTableUI.getTableModel()
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
		final TableItem[] data = myTableUI.getTableViewer().getTable().getItems();
		for (int i = data.length - 1; i >= 0; i--)
		{
			final TableItem tableItem = data[i];

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
		final boolean isVis = myTableUI.isOnlyShowVisible();
		setChecked(isVis);
		setImageDescriptor(exportImage);
	}

}
