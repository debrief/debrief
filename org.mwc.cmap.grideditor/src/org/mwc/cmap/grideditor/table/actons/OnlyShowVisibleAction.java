/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

	public OnlyShowVisibleAction(final GridEditorTable tableUI) {
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
		final boolean showVis = myTableUI.isOnlyShowVisible();
		myTableUI.setOnlyShowVisible(!showVis);
		refreshWithTableUI();
	}

	public void refreshWithTableUI() {
		final boolean isVis = myTableUI.isOnlyShowVisible();
		setChecked(isVis);
		setImageDescriptor(isVis ? showVisImage : showAllImage);
	}

}
