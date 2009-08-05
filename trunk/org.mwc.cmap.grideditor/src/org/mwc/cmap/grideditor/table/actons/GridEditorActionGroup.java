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
import org.mwc.cmap.grideditor.GridEditorView.GriddableWrapper;
import org.mwc.cmap.grideditor.table.GridEditorTable;

import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

public class GridEditorActionGroup extends ActionGroup {

	private final GridEditorView myView;

	private boolean myActionsInitialized;

	private InsertRowAction myInsertRowAction;

	private DeleteRowAction myDeleteRowAction;

	private TrackSelectionAction myTrackSelectionAction;

	private InterpolateAction myInterpolateAction;

	private OnlyShowVisibleAction myShowVisItemsAction;

	public GridEditorActionGroup(GridEditorView view,
			GridEditorActionContext context) {
		myView = view;
		super.setContext(context);
		getContext().setListener(new GridEditorActionContext.Listener() {

			@Override
			public void tableInputChanged() {
				myView.refreshUndoContext();
				contextChanged();
			}

			@Override
			public void selectionChanged() {
				contextChanged();
			}

			@Override
			public void chartInputChanged() {
				contextChanged();
			}
		});
	}

	@Override
	public void setContext(ActionContext context) {
		throw new UnsupportedOperationException(
				"I am managing my context myself");
	}

	@Override
	public GridEditorActionContext getContext() {
		return (GridEditorActionContext) super.getContext();
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		initActions();
		IToolBarManager toolbar = actionBars.getToolBarManager();
		toolbar.add(myInsertRowAction);
		toolbar.add(myDeleteRowAction);
		toolbar.add(myInterpolateAction);
		toolbar.add(new Separator());
		toolbar.add(myShowVisItemsAction);
		toolbar.add(myTrackSelectionAction);
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		initActions();
		menu.add(myInsertRowAction);
		menu.add(myDeleteRowAction);
		menu.add(myInterpolateAction);
		menu.add(new Separator());
		menu.add(myShowVisItemsAction);
		menu.add(myTrackSelectionAction);
		menu.add(new Separator());

		// right, find the selection
		StructuredSelection sel = (StructuredSelection) myView.getUI()
				.getTable().getTableViewer().getSelection();

		// do we have something?
		if (sel.size() > 0) {
			
			// create an array of the items being edited
			Editable[] items = new Editable[sel.size()];
			int index = 0;
			@SuppressWarnings("unchecked")
			Iterator iter = sel.iterator();
			while(iter.hasNext())
			{
				items[index++] = (Editable) iter.next();
			}
			
			// collate the other metadata
			GriddableWrapper wrapper = (GriddableWrapper) myView.getUI().getTable().getTableViewer().getInput();
			
			// fill the layers objects
			Layer[] topLayers = new Layer[sel.size()];
			Layer[] parentLayers = new Layer[sel.size()];
			for(int i=0;i<sel.size();i++)
			{
				topLayers[i] = wrapper.getWrapper().getTopLevelLayer();
				parentLayers[i] = wrapper.getWrapper().getTopLevelLayer();
			}
			Layers theLayers = wrapper.getWrapper().getLayers();
			
			// create a drop-down menu for this item
			RightClickSupport.getDropdownListFor(menu, items, topLayers,
					parentLayers, theLayers, true);
		}
	}

	private void initActions() {
		if (myActionsInitialized) {
			return;
		}
		if (myView.getUI() == null) {
			return;
		}
		GridEditorTable tableUI = myView.getUI().getTable();
		myTrackSelectionAction = new TrackSelectionAction(tableUI);
		myShowVisItemsAction = new OnlyShowVisibleAction(tableUI);
		myInsertRowAction = new InsertRowAction();
		myDeleteRowAction = new DeleteRowAction();
		myInterpolateAction = new InterpolateAction();
		myActionsInitialized = true;
	}

	private void contextChanged() {
		if (!myActionsInitialized) {
			return;
		}
		myTrackSelectionAction.refreshWithTableUI();
		myShowVisItemsAction.refreshWithTableUI();
		GridEditorActionContext contextImpl = getContext();
		myInsertRowAction.refreshWithActionContext(contextImpl);
		myDeleteRowAction.refreshWithActionContext(contextImpl);
		myInterpolateAction.refreshWithActionContext(contextImpl);
	}
}
