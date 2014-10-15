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
package org.mwc.cmap.NarrativeViewer.actions;

import org.mwc.cmap.NarrativeViewer.Column;

public class SwitchColumnVisibilityAction extends AbstractDynamicAction {
	private final Column myColumn;

	public SwitchColumnVisibilityAction(final Column column, final String name){
		myColumn = column;
		setText(name);
	}
	
	public void refresh(){
		setChecked(myColumn.isVisible());
	}
	
	@Override
	public void run() {
		final boolean wasVisible = myColumn.isVisible();
		myColumn.setVisible(!wasVisible);
	}

}
