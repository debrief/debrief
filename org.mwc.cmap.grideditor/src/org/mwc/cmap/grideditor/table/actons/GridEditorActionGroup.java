/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.grideditor.table.actons;

import java.util.Iterator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.grideditor.GridEditorActionContext;
import org.mwc.cmap.grideditor.GridEditorView;
import org.mwc.cmap.grideditor.data.GriddableWrapper;
import org.mwc.cmap.grideditor.table.GridEditorTable;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

public class GridEditorActionGroup extends ActionGroup
{

	private final GridEditorView myView;

	private boolean myActionsInitialized;

	private InsertRowAction myInsertRowAction;

	private DeleteRowAction myDeleteRowAction;

	private TrackSelectionAction myTrackSelectionAction;

	private InterpolateAction myInterpolateAction;

	private OnlyShowVisibleAction myShowVisItemsAction;

	private ExportToClipboardAction myExportAction;

	public GridEditorActionGroup(final GridEditorView view,
			final GridEditorActionContext context)
	{
		myView = view;
		super.setContext(context);
		getContext().setListener(new GridEditorActionContext.Listener()
		{

			public void tableInputChanged()
			{
				myView.refreshUndoContext();
				contextChanged();
			}

			public void selectionChanged()
			{
				contextChanged();
			}

			public void chartInputChanged()
			{
				contextChanged();
			}
		});
	}

	@Override
	public void setContext(final ActionContext context)
	{
		throw new UnsupportedOperationException("I am managing my context myself");
	}

	@Override
	public GridEditorActionContext getContext()
	{
		return (GridEditorActionContext) super.getContext();
	}

	@Override
	public void fillActionBars(final IActionBars actionBars)
	{
		initActions();
		final IToolBarManager toolbar = actionBars.getToolBarManager();
		toolbar.add(myInsertRowAction);
		toolbar.add(myDeleteRowAction);
		toolbar.add(myInterpolateAction);
		toolbar.add(new Separator());
		toolbar.add(myExportAction);
		toolbar.add(new Separator());
		toolbar.add(myShowVisItemsAction);
		toolbar.add(myTrackSelectionAction);
	}

	@Override
	public void fillContextMenu(final IMenuManager menu)
	{
		initActions();
		menu.add(myInsertRowAction);
		menu.add(myDeleteRowAction);
		menu.add(myInterpolateAction);
		menu.add(new Separator());
		menu.add(myExportAction);
		menu.add(new Separator());
		menu.add(myShowVisItemsAction);
		menu.add(myTrackSelectionAction);
		menu.add(new Separator());

		// right, find the selection
		final StructuredSelection sel = (StructuredSelection) myView.getUI().getTable()
				.getTableViewer().getSelection();

		// do we have something?
		if (sel.size() > 0)
		{

			// create an array of the items being edited
			final Editable[] items = new Editable[sel.size()];
			int index = 0;
			@SuppressWarnings("rawtypes")
			final
			Iterator iter = sel.iterator();
			while (iter.hasNext())
			{
				items[index++] = (Editable) iter.next();
			}

			// collate the other metadata
			final GriddableWrapper wrapper = (GriddableWrapper) myView.getUI().getTable()
					.getTableViewer().getInput();

			// fill the layers objects
			final Layer[] topLayers = new Layer[sel.size()];
			final Layer[] parentLayers = new Layer[sel.size()];
			for (int i = 0; i < sel.size(); i++)
			{
				topLayers[i] = wrapper.getWrapper().getTopLevelLayer();
				parentLayers[i] = wrapper.getWrapper().getTopLevelLayer();
			}
			final Layers theLayers = wrapper.getWrapper().getLayers();

			// create a drop-down menu for this item
			RightClickSupport.getDropdownListFor(menu, items, topLayers,
					parentLayers, theLayers, true);
		}
	}

	private void initActions()
	{
		if (myActionsInitialized)
		{
			return;
		}
		if (myView.getUI() == null)
		{
			return;
		}
		final GridEditorTable tableUI = myView.getUI().getTable();
		myTrackSelectionAction = new TrackSelectionAction(tableUI);
		myShowVisItemsAction = new OnlyShowVisibleAction(tableUI);
		myInsertRowAction = new InsertRowAction();
		myDeleteRowAction = new DeleteRowAction();
		myExportAction = new ExportToClipboardAction(tableUI);
		myInterpolateAction = new InterpolateAction();
		myActionsInitialized = true;
	}

	private void contextChanged()
	{
		if (!myActionsInitialized)
		{
			return;
		}
		myTrackSelectionAction.refreshWithTableUI();
		myShowVisItemsAction.refreshWithTableUI();
		final GridEditorActionContext contextImpl = getContext();
		myInsertRowAction.refreshWithActionContext(contextImpl);
		myExportAction.refreshWithTableUI();
		myDeleteRowAction.refreshWithActionContext(contextImpl);
		myInterpolateAction.refreshWithActionContext(contextImpl);
	}
}
