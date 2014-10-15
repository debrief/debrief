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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

	public TrackSelectionAction(final GridEditorTable tableUI) {
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
		final boolean wasTrackingSelection = myTableUI.isTrackingSelection();
		myTableUI.setTrackingSelection(!wasTrackingSelection);
		refreshWithTableUI();
	}

	public void refreshWithTableUI() {
		final boolean isTracking = myTableUI.isTrackingSelection();
		setChecked(isTracking);
		setImageDescriptor(isTracking ? myLockedImage : myUnlockedImage);
	}

}
