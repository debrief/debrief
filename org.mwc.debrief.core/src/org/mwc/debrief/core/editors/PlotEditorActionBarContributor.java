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
package org.mwc.debrief.core.editors;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;
/**
 * Plot editor action bar contributor.
 */
public class PlotEditorActionBarContributor extends EditorActionBarContributor {
	private static final String EDITOR_VIEW_MENU_ID = "/org.mwc.debrief.core.EditorView";

	// current editor
	protected PlotEditor _myEditor;

	public PlotEditorActionBarContributor() {
		super();
	}

	/**
	 * Sets the active editor for the contributor.
	 * <p>
	 * The <code>EditorActionBarContributor</code> implementation of this method does
	 * nothing. Subclasses may reimplement. This generally entails disconnecting
	 * from the old editor, connecting to the new editor, and updating the actions
	 * to reflect the new editor.
	 * </p>
	 * 
	 * @param targetEditor the new target editor
	 */
	public void setActiveEditor(IEditorPart targetEditor) {
		if (targetEditor instanceof PlotEditor) {
			_myEditor = (PlotEditor) targetEditor;
		} else {
			_myEditor = null;
		}
		IActionBars bars = getActionBars();
		if (bars == null)
			return;
		//bars.setGlobalActionHandler(ActionFactory.REVERT.getId(), getRevertAction());
		bars.updateActionBars();	
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public void init(IActionBars bars)
	{
		super.init(bars);
	}

	@Override
	public void contributeToMenu(IMenuManager menuManager)
	{
		super.contributeToMenu(menuManager);
		IMenuManager editorViewMenu = menuManager.findMenuUsingPath(EDITOR_VIEW_MENU_ID);
		// TODO: verify what is happening with the above item. Do we need it?
	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager)
	{
	}
}