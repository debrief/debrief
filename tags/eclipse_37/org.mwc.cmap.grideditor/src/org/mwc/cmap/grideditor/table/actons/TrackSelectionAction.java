package org.mwc.cmap.grideditor.table.actons;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.mwc.cmap.grideditor.GridEditorPlugin;
import org.mwc.cmap.grideditor.table.GridEditorTable;


public class TrackSelectionAction extends Action {

	private static final String ACTION_TEXT = "Track selection";

	private final GridEditorTable myTableUI;

	private final ImageDescriptor myLockedImage;

	private final ImageDescriptor myUnlockedImage;

	public TrackSelectionAction(GridEditorTable tableUI) {
		super(ACTION_TEXT, AS_PUSH_BUTTON);
		myTableUI = tableUI;
		myLockedImage = GridEditorPlugin.getInstance().getImageRegistry().getDescriptor(GridEditorPlugin.IMG_LOCKED);
		myUnlockedImage = GridEditorPlugin.getInstance().getImageRegistry().getDescriptor(GridEditorPlugin.IMG_UNLOCKED);
		setToolTipText(ACTION_TEXT);
		setEnabled(true);
		refreshWithTableUI();
	}

	@Override
	public void run() {
		boolean wasTrackingSelection = myTableUI.isTrackingSelection();
		myTableUI.setTrackingSelection(!wasTrackingSelection);
		refreshWithTableUI();
	}

	public void refreshWithTableUI() {
		boolean isTracking = myTableUI.isTrackingSelection();
		setChecked(isTracking);
		setImageDescriptor(isTracking ? myLockedImage : myUnlockedImage);
	}

}
