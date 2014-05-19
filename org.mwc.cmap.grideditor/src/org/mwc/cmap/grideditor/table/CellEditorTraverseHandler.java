package org.mwc.cmap.grideditor.table;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

public class CellEditorTraverseHandler implements TraverseListener {

	private final EditableTarget myActivateOnTab;

	private final Object myActuallyEdited;

	public CellEditorTraverseHandler(final EditableTarget activateOnTab, final Object actuallyEdited) {
		myActivateOnTab = activateOnTab;
		myActuallyEdited = actuallyEdited;
	}

	public void keyTraversed(final TraverseEvent e) {
		if (e.detail == SWT.TRAVERSE_TAB_NEXT) {
			final ColumnViewer viewer = myActivateOnTab.getColumnViewer();
			final Object element = myActivateOnTab.getElementToEdit(myActuallyEdited);
			if (element != null) {
				final int columnIndex = myActivateOnTab.getColumnIndex();
				viewer.editElement(element, columnIndex);
			}
		}
	}
}
