package org.mwc.cmap.grideditor.table.actons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.grideditor.table.GridEditorTable;


public class OnlyShowVisibleAction extends Action {

	private static final String ACTION_TEXT = "Only show visible points";

	private final GridEditorTable myTableUI;

	private final ImageDescriptor showVisImage;

	private final ImageDescriptor showAllImage;

	public OnlyShowVisibleAction(GridEditorTable tableUI) {
		super(ACTION_TEXT, AS_PUSH_BUTTON);
		myTableUI = tableUI;
		showVisImage = CorePlugin.getImageDescriptor("icons/checked.gif");
		showAllImage = CorePlugin.getImageDescriptor("icons/unchecked.gif");
		setToolTipText(ACTION_TEXT);
		setEnabled(true);
		refreshWithTableUI();
	}

	@Override
	public void run() {
		boolean showVis = myTableUI.isOnlyShowVisible();
		myTableUI.setOnlyShowVisible(!showVis);
		refreshWithTableUI();
	}

	public void refreshWithTableUI() {
		boolean isVis = myTableUI.isOnlyShowVisible();
		setChecked(isVis);
		setImageDescriptor(isVis ? showVisImage : showAllImage);
	}

}
