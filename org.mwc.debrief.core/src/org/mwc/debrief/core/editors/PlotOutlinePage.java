/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.core.editors;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.CopyItem;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.CutItem;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.DeleteItem;
import org.mwc.cmap.core.operations.RightClickCutCopyAdaptor.EditableTransfer;
import org.mwc.cmap.core.operations.RightClickPasteAdaptor;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.property_support.RightClickSupport;
import org.mwc.cmap.core.ui_support.CoreViewLabelProvider;
import org.mwc.cmap.core.ui_support.DragDropSupport;
import org.mwc.cmap.core.ui_support.OutlineNameSorter;
import org.mwc.debrief.core.DebriefPlugin;
import org.mwc.debrief.core.ContextOperations.GeneratePasteRepClipboard;

import Debrief.ReaderWriter.Replay.ImportReplay;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Editable.EditorType;
import MWC.GUI.HasEditables;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainWrapper;
import MWC.GUI.Plottable;
import MWC.GenericData.WatchableList;

public class PlotOutlinePage extends Page implements IContentOutlinePage {

	private static class MyTreeViewer extends TreeViewer {
		public MyTreeViewer(final Composite parent, final int style) {
			super(parent, style);
		}

		public MyTreeViewer(final Tree parent) {
			super(parent);
		}

		public Widget findEditable(final Editable item) {
			return super.findItem(item);
		}
	}

	private static class SelectionContext {

		static SelectionContext create(final StructuredSelection sel) {
			return new SelectionContext(sel);
		}

		final Editable[] eList;
		final HasEditables[] parentLayers;

		final Layer[] updateLayers;

		private SelectionContext(final StructuredSelection sel) {
			eList = new Editable[sel.size()];
			parentLayers = new HasEditables[sel.size()];
			updateLayers = new Layer[sel.size()];

			// right, now populate them
			final Object[] oList = sel.toArray();
			for (int i = 0; i < oList.length; i++) {
				final EditableWrapper wrapper = (EditableWrapper) oList[i];
				eList[i] = wrapper.getEditable();

				// sort out the parent layer
				final EditableWrapper theParent = wrapper.getParent();

				// hmm, did we find one?
				if (theParent != null) {
					// yes, store it
					parentLayers[i] = (HasEditables) wrapper.getParent().getEditable();
				} else {
					// nope - store a null (to indicate it's a top-level layer)
					parentLayers[i] = null;
				}

				updateLayers[i] = wrapper.getTopLevelLayer();
			}
		}

	}

	/**
	 * class that embodies applying an operation to a series of selected points. the
	 * series of points are remembered, so that they can be undone/redone
	 *
	 * @author ian.mayo
	 */
	private static final class SelectionOperation extends AbstractOperation {
		/**
		 * the selected items
		 */
		StructuredSelection _theSelection;

		/**
		 * what we are going to execute
		 */
		private final IOperateOn _execute;

		/**
		 * what we are going to undo
		 */
		private final IOperateOn _undo;

		/**
		 * what we are going to redo
		 */
		private final IOperateOn _redo;

		/**
		 * who is giving us the selection
		 */
		private final ISelectionProvider _provider;

		/**
		 * who we fire the update to
		 */
		private final Layers _destination;

		/**
		 * define our operation
		 *
		 * @param label       what to label it
		 * @param execute     the operation to execute
		 * @param undo        how to do an undo
		 * @param redo        how to do a redo
		 * @param provider    who is going to provide the selection
		 * @param destination what to update on completion
		 */

		SelectionOperation(final String label, final IOperateOn execute, final IOperateOn undo, final IOperateOn redo,
				final ISelectionProvider provider, final Layers destination) {
			super(label);

			// get remembering
			_provider = provider;
			_execute = execute;
			_undo = undo;
			_redo = redo;
			_destination = destination;

			if (CorePlugin.getUndoContext() != null) {
				addContext(CorePlugin.getUndoContext());
			}
		}

		/**
		 * @return
		 */
		@Override
		public boolean canExecute() {
			return _execute != null;
		}

		/**
		 * @return
		 */
		@Override
		public boolean canRedo() {
			return _redo != null;
		}

		/**
		 * @return
		 */
		@Override
		public boolean canUndo() {
			return _undo != null;
		}

		/**
		 * ok, do the operation
		 */
		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			// ok, take a copy of the selection - for our undo/redo operations
			_theSelection = (StructuredSelection) _provider.getSelection();

			// cool, go for it
			applyOperation(_execute, _theSelection, _destination);

			return Status.OK_STATUS;
		}

		/**
		 * ok, redo the operation
		 */
		@Override
		public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			applyOperation(_redo, _theSelection, _destination);
			return Status.OK_STATUS;
		}

		/**
		 * ok, undo the operation
		 */

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
			applyOperation(_undo, _theSelection, _destination);
			return Status.OK_STATUS;
		}

	}

	private static final String SENSOR_SORT_BY_DTG = "SENSOR_SORT_BY_DTG";

	public static final String NAME_COLUMN_NAME = "Name";

	public static final String VISIBILITY_COLUMN_NAME = "Visibility";

	/**
	 * whether we are already ignoring firing messages
	 */
	private static boolean _alreadyDeferring = false;

	/*
	 * don't bother with the drill-down adapter. we've removed it to save space in
	 * the local toolbar private DrillDownAdapter drillDownAdapter;
	 */

	private static Set<Layer> _pendingLayers = new TreeSet<Layer>(new Comparator<Layer>() {
		@Override
		public int compare(final Layer arg0, final Layer arg1) {
			return arg0.compareTo(arg1);
		}

	});

	@SuppressWarnings("rawtypes")
	static void applyOperation(final IOperateOn operation, final IStructuredSelection selection,
			final Layers myLayers) {

		final Iterator iterator = selection.iterator();
		boolean madeChange = false;
		Layer parentLayer = null;
		boolean multiLayer = false;

		while (iterator.hasNext()) {
			final Object obj = iterator.next();

			final EditableWrapper thisP = (EditableWrapper) obj;
			final Editable res = thisP.getEditable();
			final Editable thisOne = res;
			Layer thisParentLayer = null;

			// ok - do the business
			operation.doItTo(thisOne);

			// and remember that it worked
			madeChange = true;

			thisParentLayer = thisP.getTopLevelLayer();

			// ok. we've now got the parent layer
			// - is it the first one?
			if (parentLayer == null) {
				// yup. just store it
				parentLayer = thisParentLayer;
			} else {
				// nope, we've had at least one of these before
				if (parentLayer != thisParentLayer) {
					multiLayer = true;
				}
			}

		}

		// right. has a change been made?
		if (madeChange) {
			// yup. does it apply to just on layer - or all of them
			if (multiLayer) {
				// ok - update all layers
				triggerChartUpdate(null, myLayers);
			} else {
				// ok - just update the one layer
				triggerChartUpdate(parentLayer, myLayers);
			}
		}

		// ok, and update the layers
	}

	protected static void setPlottableVisible(final Editable item, final boolean on) {
		if (item instanceof Plottable) {
			final Plottable pl = (Plottable) item;
			pl.setVisible(on);

			// and update property
			final EditorType info = item.getInfo();
			if (info != null) {
				info.fireChanged(item, PlainWrapper.VISIBILITY_CHANGED, !on, on);
			}
		}
	}

	private static void triggerChartUpdate(final Layer changedLayer, final Layers myLayers) {
		myLayers.fireReformatted(changedLayer);
	}

	MyTreeViewer _treeViewer;

	private CoreViewLabelProvider _myLabelProvider;

	private DragDropSupport _dragDropSupport;

	private ISelectionChangedListener _selectionChangeListener;

	Layers _myLayers;

	/**
	 * create a new top-level layer
	 */
	private Action _createLayer;

	/**
	 * make the current item the primary track
	 */
	Action _makePrimary;

	/**
	 * set the current item as the secondary track
	 */
	Action _makeSecondary;

	/**
	 * add the current item to the secondary track
	 */
	Action _addAsSecondary;

	/**
	 * hide the selected item(s)
	 */
	private Action _hideAction;

	/**
	 * reveal the selected item(s)
	 */
	private Action _revealAction;

	/**
	 * toggle to indicate whether user wants narrative to always jump to highlighted
	 * entry
	 */
	private Action _followSelectionToggle;

	/**
	 * action to allow user to collapse all layer manager nodes
	 */
	private Action _collapseAllAction;

	/**
	 * action to allow user to expand all layer manager nodes
	 */
	private Action _expandAllAction;

	private Layers.DataListener _myLayersListener;

	protected TrackManager _theTrackDataListener;

	private PlotEditor _plotEditor;

	/**
	 * The job used to refresh the grid.
	 */
	private Job refreshOnDeleteJob;

	private Action _sortSensorsDTG;

	private Action _sortSensorsName;

	private final ViewerComparator _myComparator;

	public PlotOutlinePage(final PlotEditor _plotEditor, final Layers _myLayers) {
		this._plotEditor = _plotEditor;
		this._myLayers = _myLayers;
		this._theTrackDataListener = (TrackManager) _plotEditor.getAdapter(TrackManager.class);

		_myComparator = new OutlineNameSorter(new OutlineNameSorter.NameSortHelper() {

			@Override
			public boolean sortByDate() {
				return _sortSensorsDTG.isChecked();
			}
		});

	}

	/**
	 * recursive class used to build up a list containing the item together with all
	 * child items
	 *
	 * @param list the list we're building up
	 * @param item the item to add (together with its children)
	 */
	private void addItemAndChildrenToList(final Vector<Object> list, final TreeItem item) {
		final Object myData = item.getData();
		if (myData != null) {
			list.add(item.getData());
		}
		final TreeItem[] children = item.getItems();
		if (children.length > 0) {
			for (int i = 0; i < children.length; i++) {
				final TreeItem thisChild = children[i];
				addItemAndChildrenToList(list, thisChild);
			}
		}
	}

	@Override
	public void addSelectionChangedListener(final ISelectionChangedListener listener) {
		_treeViewer.addSelectionChangedListener(listener);
	}

	/**
	 * user has double-clicked on an item. process.
	 *
	 * @param operation the thingy we're doing
	 */
	void applyOperationToSelection(final IOperateOn operation) {
		final IStructuredSelection selection = (IStructuredSelection) _treeViewer.getSelection();
		applyOperation(operation, selection, _myLayers);

	}

	private void assignCorrectSortMode() {
		final Boolean sortByDTGPref = CorePlugin.getDefault().getPreferenceStore().getBoolean(SENSOR_SORT_BY_DTG);
		final boolean byD = (sortByDTGPref == null || sortByDTGPref.booleanValue());
		_sortSensorsDTG.setChecked(byD);
		_sortSensorsName.setChecked(!byD);
	}

	/**
	 * stop listening to the layer, if necessary
	 */
	void clearLayerListener() {
		if (_myLayers != null) {
			_myLayers.removeDataExtendedListener(_myLayersListener);
			_myLayers.removeDataReformattedListener(_myLayersListener);
			_myLayersListener = null;
			_myLayers = null;
		}
	}

	public void connectActions() {
		final IActionBars actionBars = getSite().getActionBars();

		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), new Action() {
			@Override
			public boolean isEnabled() {
				final Tree control = (Tree) _treeViewer.getControl();
				final int count = control.getSelectionCount();
				return count > 0;
			}

			@Override
			public void run() {
				// get the selected item
				final StructuredSelection sel = (StructuredSelection) _treeViewer.getSelection();

				if (!sel.isEmpty()) {
					final SelectionContext selectionContext = SelectionContext.create(sel);
					final DeleteItem deleteItem = new DeleteItem(selectionContext.eList, selectionContext.parentLayers,
							_myLayers, selectionContext.updateLayers);
					deleteItem.run();
				}

			}

		});
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), new Action() {
			@Override
			public boolean isEnabled() {
				final Tree control = (Tree) _treeViewer.getControl();
				final int count = control.getSelectionCount();
				return count > 0;
			}

			@Override
			public void run() {
				// get the selected item
				final StructuredSelection sel = (StructuredSelection) _treeViewer.getSelection();

				if (!sel.isEmpty()) {
					final Clipboard theClipboard = CorePlugin.getDefault().getClipboard();

					final SelectionContext selectionContext = SelectionContext.create(sel);
					final CopyItem copyItem = new CopyItem(selectionContext.eList, theClipboard,
							selectionContext.parentLayers, _myLayers, selectionContext.updateLayers);
					copyItem.run();
				}

			}

		});
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), new Action() {
			@Override
			public boolean isEnabled() {
				final Tree control = (Tree) _treeViewer.getControl();
				final int count = control.getSelectionCount();
				return count > 0;
			}

			@Override
			public void run() {
				// get the selected item
				final StructuredSelection sel = (StructuredSelection) _treeViewer.getSelection();

				if (!sel.isEmpty()) {
					final Clipboard theClipboard = CorePlugin.getDefault().getClipboard();

					final SelectionContext selectionContext = SelectionContext.create(sel);
					final CutItem cutItem = new CutItem(selectionContext.eList, theClipboard,
							selectionContext.parentLayers, _myLayers, selectionContext.updateLayers);
					cutItem.run();
				}

			}

		});

		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), new Action() {
			@Override
			public boolean isEnabled() {
				final Clipboard _clipboard = CorePlugin.getDefault().getClipboard();
				final Object val = _clipboard.getContents(TextTransfer.getInstance());
				if (val != null) {
					final String clipBoardContent = (String) val;
					// See if there is plain text on the clipboard
					if (ImportReplay.isContentImportable(clipBoardContent)) {
						return true;
					}
				}
				final EditableTransfer transfer = EditableTransfer.getInstance();
				final Editable[] tr = (Editable[]) _clipboard.getContents(transfer);
				if (tr == null || tr.length == 0) {
					return false;
				}

				final StructuredSelection sel = (StructuredSelection) _treeViewer.getSelection();
				final SelectionContext selectionContext = SelectionContext.create(sel);

				Editable selectedItem = null;
				if (selectionContext.eList.length == 1) {
					selectedItem = selectionContext.eList[0];
				}
				if ((selectedItem instanceof MWC.GUI.Layer) || (selectedItem == null)) {
					return RightClickPasteAdaptor.createAction(selectedItem, _myLayers, tr) != null;
				}

				return false;
			}

			@Override
			public void run() {
				final Clipboard _clipboard = CorePlugin.getDefault().getClipboard();
				final Object val = _clipboard.getContents(TextTransfer.getInstance());
				if (val != null) {
					final String clipBoardContent = (String) val;
					// See if there is plain text on the clipboard
					if (ImportReplay.isContentImportable(clipBoardContent)) {
						GeneratePasteRepClipboard.createAction(_myLayers, clipBoardContent).run();
					}
				}
				final EditableTransfer transfer = EditableTransfer.getInstance();
				final Editable[] tr = (Editable[]) _clipboard.getContents(transfer);
				if (tr == null || tr.length == 0) {
					return;
				}

				final StructuredSelection sel = (StructuredSelection) _treeViewer.getSelection();
				final SelectionContext selectionContext = SelectionContext.create(sel);

				Editable selectedItem = null;
				if (selectionContext.eList.length == 1) {
					selectedItem = selectionContext.eList[0];
				}
				if ((selectedItem instanceof MWC.GUI.Layer) || (selectedItem == null)) {
					RightClickPasteAdaptor.createAction(selectedItem, _myLayers, tr).run();
				}

			}

		});

		actionBars.updateActionBars();
	}

	private void contributeToActionBars() {
		final IActionBars bars = getSite().getActionBars();
		fillLocalPullDown(bars);
		fillLocalToolBar(bars);
	}

	@Override
	public void createControl(final Composite parent) {

		_treeViewer = new MyTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		_treeViewer.setUseHashlookup(true);
		// drillDownAdapter = new DrillDownAdapter(_treeViewer);
		_treeViewer.setContentProvider(new ViewContentProvider());
		_myLabelProvider = new CoreViewLabelProvider();
		_treeViewer.setLabelProvider(_myLabelProvider);
		_treeViewer.setComparator(_myComparator);
		_treeViewer.setInput(_myLayers);
		// this will eliminate the need for user to do a ctrl+click to deselect.
		_treeViewer.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
				final Tree tree = (Tree) e.getSource();
				if (tree.getItem(new Point(e.x, e.y)) == null) {
					// no item was clicked.
					tree.deselectAll();
				}
			}
		});
		_treeViewer.setComparer(new IElementComparer() {
			@Override
			public boolean equals(final Object a, final Object b) {
				Object obj1 = a;
				Object obj2 = b;

				// do our special case for comparing plottables
				if (obj1 instanceof EditableWrapper) {
					final EditableWrapper pw = (EditableWrapper) obj1;
					// does it have an editable?
					if (pw.getEditable() != null) {
						obj1 = pw.getEditable();
					}
				}

				if (obj2 instanceof EditableWrapper) {
					final EditableWrapper pw = (EditableWrapper) obj2;
					// does it have an editable?
					if (pw.getEditable() != null) {
						obj2 = pw.getEditable();
					}
				}

				return obj1.equals(obj2);
			}

			@Override
			public int hashCode(final Object element) {
				int res = 0;

				if (element instanceof EditableWrapper) {
					final EditableWrapper pw = (EditableWrapper) element;
					final Editable pl = pw.getEditable();
					if (pl != null) {
						res += pw.getEditable().hashCode();
					}
				} else {
					res = element.hashCode();
				}

				return res;
			}

		});

		_dragDropSupport = new DragDropSupport(_treeViewer);
		_treeViewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, _dragDropSupport.getDragTypes(), _dragDropSupport);
		_treeViewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, _dragDropSupport.getDropTypes(), _dragDropSupport);

		// and format the tree
		final Tree tree = _treeViewer.getTree();
		tree.setHeaderVisible(true);
		formatTree(tree);

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// set ourselves as selection source
		getSite().setSelectionProvider(_treeViewer);

		_selectionChangeListener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				// right, see what it is
				final ISelection sel = event.getSelection();
				if (sel instanceof StructuredSelection) {
					final StructuredSelection ss = (StructuredSelection) sel;
					final Object datum = ss.getFirstElement();
					if (datum instanceof EditableWrapper) {
						final EditableWrapper pw = (EditableWrapper) datum;
						editableSelected(sel, pw);
					}
				}
				connectActions();
			}
		};

		_plotEditor.addSelectionChangedListener(_selectionChangeListener);

		// also listen out ourselves to any changes, so we can update the button
		// enablement
		_treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final ISelection isel = event.getSelection();
				if (isel instanceof StructuredSelection) {
					final StructuredSelection ss = (StructuredSelection) isel;

					// right, see if this is an item we can make primary
					_makePrimary.setEnabled(isValidPrimary(ss));

					// and see if we can make it a secondary
					_makeSecondary.setEnabled(isValidSecondary(ss));

					// and see if we can make it a secondary
					_addAsSecondary.setEnabled(isValidSecondary(ss));

					enableVisibilityActions(ss);
				}
				connectActions();
			}

		});

		/**
		 * Intentional not used TableEditor which would create lot of objects as this
		 * view usually has more data.
		 */
		_treeViewer.getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(final MouseEvent e) {
				final Point pt = new Point(e.x, e.y);
				if (e.count > 1) {
					return;
				}

				final ViewerCell viewerCell = _treeViewer.getCell(pt);
				// click on visibility column.
				if (viewerCell != null && viewerCell.getColumnIndex() == 1) {
					final Object element = viewerCell.getElement();
					if (element instanceof EditableWrapper) {
						final EditableWrapper pw = (EditableWrapper) element;
						final Editable ed = pw.getEditable();
						if (ed instanceof Plottable) {
							final Plottable pl = (Plottable) ed;
							// toggle
							if (pl.getVisible()) {
								_hideAction.run();
							} else {
								_revealAction.run();
							}
						}
					}
				}
			}
		});

		_treeViewer.getTree().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {

				connectActions();
			}
		});
		// and declare our context sensitive help
		// FIXME
		CorePlugin.declareContextHelp(parent, "org.mwc.debrief.help.LayerMgr");

		processNewLayers();

		connectActions();

	}

	/**
	 * produce a job to force an outline UI refresh
	 *
	 * @return
	 */
	private Job createRefreshOnDeleteJob() {
		return new WorkbenchJob("Refresh Filter") //$NON-NLS-1$
		{
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				if (_treeViewer.getControl().isDisposed()) {
					return Status.CANCEL_STATUS;
				}

				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						final TreePath[] paths = _treeViewer.getExpandedTreePaths();

						// ok, fire the change in the UI thread
						_treeViewer.setInput(_myLayers);

						// open up the paths that were previously open
						_treeViewer.setExpandedTreePaths(paths);
					}
				});

				return Status.OK_STATUS;
			}
		};
	}

	@Override
	public void dispose() {
		super.dispose();

		// make sure we close the listeners
		clearLayerListener();

		// remove selection listeners
		if (_plotEditor != null) {
			_plotEditor.removeSelectionChangedListener(_selectionChangeListener);
			_plotEditor.outlinePageClosed();
			_plotEditor = null;
		}

		_selectionChangeListener = null;
		if (_myLabelProvider != null) {
			_myLabelProvider.disposeImages();
		}
	}

	public void editableSelected(final ISelection sel, final EditableWrapper pw) {
		if (_treeViewer == null || _treeViewer.getControl().isDisposed()) {
			return;
		}
		if (_followSelectionToggle.isChecked()) {
			// ahh, just check if this is a whole new layers object
			if (pw.getEditable() instanceof Layers) {
				if (pw.getEditable() != _myLayers) {
					_myLayers = (Layers) pw.getEditable();
					processNewLayers();
				}
			} else {
				// just check that this is something we can work with
				if (sel instanceof StructuredSelection) {
					final StructuredSelection str = (StructuredSelection) sel;

					// hey, is there a payload?
					if (str.getFirstElement() != null) {
						// sure is. we only support single selections, so get the first
						// element
						final Object first = str.getFirstElement();
						if (first instanceof EditableWrapper) {
							final EditableWrapper ew = (EditableWrapper) first;

							// ensure the parent levels are visible
							_treeViewer.expandToLevel(ew, 0);

							// now just display it. This part of the tree may not have been
							// loaded before,
							// but we're sure it is now.
							_treeViewer.setSelection(sel, _followSelectionToggle.isChecked());

							// the API indicates the "reveal" parameter in setSelection
							// is ignored. So, let's try to force it
							_treeViewer.reveal(first);
						}
					}
				}

			}
		}

	}

	private void enableVisibilityActions(final StructuredSelection ss) {
		// enable hide action if atleast one selected plottable element is visible
		_hideAction.setEnabled(isAtleastOnePlottableCheck(ss, true));

		// enable hide action if atleast one selected plottable element is hidden
		_revealAction.setEnabled(isAtleastOnePlottableCheck(ss, false));
	}

	void fillContextMenu(final IMenuManager manager) {
		// get the selected item
		final StructuredSelection sel = (StructuredSelection) _treeViewer.getSelection();

		// right, we only worry about primary, secondary, hide, reveal if something
		// is selected
		if (sel.size() > 0) {

			// have a look at the data-types to sort out whether to primary/secondary
			if (isValidPrimary(sel)) {
				manager.add(_makePrimary);
			}
			if (isValidSecondary(sel)) {
				manager.add(_makeSecondary);
				manager.add(_addAsSecondary);
			}

			// now stick in the separator anyway
			manager.add(new Separator());
		}

		// drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		final SelectionContext selectionContext = SelectionContext.create(sel);

		// ok, sort out what we can do with all of this...
		RightClickSupport.getDropdownListFor(manager, selectionContext.eList, selectionContext.updateLayers,
				selectionContext.parentLayers, _myLayers, false);

	}

	private void fillLocalPullDown(final IActionBars bars) {
		final IMenuManager manager = bars.getMenuManager();
		manager.add(_followSelectionToggle);
		manager.add(new Separator());
		manager.add(_makePrimary);
		manager.add(_makeSecondary);
		manager.add(_addAsSecondary);
		manager.add(new Separator());
		manager.add(_expandAllAction);
		manager.add(_collapseAllAction);
		manager.add(new Separator());
		manager.add(_createLayer);

		final MenuManager sub = new MenuManager("Sort sensors");
		sub.add(_sortSensorsDTG);
		sub.add(_sortSensorsName);

		manager.add(new Separator());
		manager.add(sub);
	}

	private void fillLocalToolBar(final IActionBars bars) {
		final IToolBarManager manager = bars.getToolBarManager();
		manager.add(_followSelectionToggle);
		manager.add(_collapseAllAction);
		manager.add(_makePrimary);
		manager.add(_makeSecondary);
		manager.add(_addAsSecondary);
		manager.add(_hideAction);
		manager.add(_revealAction);
		// manager.add(_trackNewLayers);
	}

	private void formatTree(final Tree tree) {
		// define the columns
		final TreeColumn nameCol = new TreeColumn(tree, SWT.NONE);
		nameCol.setText(NAME_COLUMN_NAME);
		nameCol.setWidth(280);
		final TreeColumn visibleCol = new TreeColumn(tree, SWT.NONE);
		visibleCol.setText(VISIBILITY_COLUMN_NAME);
		visibleCol.setWidth(50);
	}

	@Override
	public Control getControl() {
		return _treeViewer.getControl();
	}

	@Override
	public ISelection getSelection() {
		return _treeViewer.getSelection();
	}

	/**
	 * one or more layers have been changed. This method may get called lots of
	 * times. Stack up the events - and just call our UI update method once at the
	 * end
	 *
	 * @param changedLayer the layer which has changed
	 */
	protected void handleReformattedLayer(final Layer changedLayer) {
		// right - store this layer (if we have one)
		if (changedLayer != null) {
			_pendingLayers.add(changedLayer);
		}

		if (_alreadyDeferring) {
			// hey - already processing - add this layer to the pending ones
		} else {
			_alreadyDeferring = true;

			// right. we're not already doing some processing
			final Display dis = Display.getDefault();
			dis.asyncExec(new Runnable() {
				@Override
				public void run() {
					processReformattedLayers();
				}
			});
		}
	}

	private void hookContextMenu() {
		final MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager manager) {
				PlotOutlinePage.this.fillContextMenu(manager);
			}
		});
		final Menu menu = menuMgr.createContextMenu(_treeViewer.getControl());
		_treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu("#PopupMenu", menuMgr, _treeViewer);
	}

	private void hookDoubleClickAction() {
		_treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent event) {

				CorePlugin.openView(IPageLayout.ID_PROP_SHEET);
			}
		});
	}

	@Override
	public void init(final IPageSite pageSite) {
		super.init(pageSite);
		final IActionBars actionBars = pageSite.getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), _plotEditor.getUndoAction());
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), _plotEditor.getRedoAction());
		actionBars.updateActionBars();
	}

	/**
	 * Checks atleast one plottable element is visible or not based on parameter
	 *
	 * @param ss
	 * @param visible
	 * @return
	 */
	protected boolean isAtleastOnePlottableCheck(final StructuredSelection ss, final boolean visible) {
		boolean atleastOnePlottableCheck = false;
		if (!ss.isEmpty()) {
			for (final Object element : ss.toList()) {
				final EditableWrapper pw = (EditableWrapper) element;
				final Editable ed = pw.getEditable();
				if (ed instanceof Plottable) {
					final Plottable pl = (Plottable) ed;
					if (pl.getVisible() == visible) {
						atleastOnePlottableCheck = true;
						break;
					}
				}
			}
		}
		return atleastOnePlottableCheck;
	}

	/**
	 * find out if the selection is valid for setting as primary
	 *
	 * @param ss
	 * @return
	 */
	protected boolean isValidPrimary(final StructuredSelection ss) {
		boolean res = false;
		// we can only do this for one entry!
		if (ss.size() == 1) {
			final EditableWrapper pw = (EditableWrapper) ss.getFirstElement();
			final Editable pl = pw.getEditable();

			// hey, first see if it's even a candidate
			if (pl instanceof WatchableList) {
				// do we have a track data listener?
				if (_theTrackDataListener != null) {
					// now see if it's already the primary
					if (pl != _theTrackDataListener.getPrimaryTrack()) {
						res = true;
					}
				}
			}
		}
		return res;
	}

	/**
	 * find out if the selection is valid for setting as primary
	 *
	 * @param ss
	 * @return
	 */
	protected boolean isValidSecondary(final StructuredSelection ss) {
		boolean res = false;
		if (ss.size() >= 1) {
			final Iterator<?> iter = ss.iterator();
			while (iter.hasNext()) {
				final EditableWrapper pw = (EditableWrapper) iter.next();
				final Editable pl = pw.getEditable();
				if (!(pl instanceof WatchableList)) {
					// nope - we can just make them all secondaries! drop out
					res = false;
					break;
				} else {
					// hey, it's a maybe.
					res = true;

					// ok, it's a candidate. now see if it's already one of the secondaries
					if (_theTrackDataListener == null) {
						CorePlugin.logError(IStatus.INFO,
								"PROBLEM: Outline View does not hold track data listener.  Maintaner to track this occurrence",
								null);
					} else {
						final WatchableList[] secs = _theTrackDataListener.getSecondaryTracks();
						if (secs != null) {
							for (int i = 0; i < secs.length; i++) {
								final WatchableList thisList = secs[i];
								if (thisList == pl) {
									res = false;
									break;
								}
							}
						}
					}
				}
			}
		}
		return res;
	}

	private void makeActions() {

		// _trackNewLayers =
		// _myPartMonitor.createSyncedAction("Link to current plot",
		// "Always show layers for selected Plot", getSite());

		_followSelectionToggle = new Action("Jump to selection", IAction.AS_CHECK_BOX) {
		};
		_followSelectionToggle.setText("Follow selection");
		_followSelectionToggle.setChecked(true);
		_followSelectionToggle.setToolTipText("Ensure selected item in plot is always visible");
		_followSelectionToggle.setImageDescriptor(DebriefPlugin.getImageDescriptor("icons/16/followselection.png"));

		_collapseAllAction = new Action("Collapse all", IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				// go for it.
				try {
					// workaround to avoid NPE on windows
					_treeViewer.setSelection(new StructuredSelection());
					_treeViewer.getTree().setRedraw(true);
					_treeViewer.collapseAll();
				} finally {

					_treeViewer.getTree().setRedraw(true);
				}
			}
		};

		_collapseAllAction.setText("Collapse all layers");
		_collapseAllAction.setToolTipText("Collapse all layers in the Outline View");
		_collapseAllAction.setImageDescriptor(DebriefPlugin.getImageDescriptor("icons/16/collapse_all.png"));
		_collapseAllAction.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_COLLAPSE_ALL);

		_expandAllAction = new Action("Expand all", IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				// go for it.
				_treeViewer.expandAll();
			}
		};
		_expandAllAction.setText("Expand all layers");
		_expandAllAction.setToolTipText("Expand all layers in the Outline View");
		_expandAllAction.setImageDescriptor(DebriefPlugin.getImageDescriptor("icons/16/expand_all.png"));

		_createLayer = new Action() {
			@Override
			public void run() {
				// ask the user
				final InputDialog id = new InputDialog(null, "Create new layer", "Layer name:", "", null);
				final int res = id.open();

				// was ok pressed?
				if (res == Window.OK) {
					// yup, create the layer
					final String newName = id.getValue();

					// yes, create the layer
					final BaseLayer bl = new BaseLayer();
					bl.setName(newName);

					// and add it
					_myLayers.addThisLayer(bl);
				}
			}
		};
		_createLayer.setText("Create new layer");
		_createLayer.setToolTipText("Create a new top-level layer");
		_createLayer.setImageDescriptor(DebriefPlugin.getImageDescriptor("icons/16/new_layer.png"));

		_makePrimary = new Action() {
			@Override
			public void run() {
				final AbstractOperation doIt = new SelectionOperation("Make primary", new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						// is it a watchable-list?
						if (item instanceof WatchableList) {
							final WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null) {
								_theTrackDataListener.setPrimary(list);
							}
						}
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						// is it a watchable-list?
						if (item instanceof WatchableList) {
							// make it the primary
							if (_theTrackDataListener != null) {
								_theTrackDataListener.setPrimary(null);
							}
						}
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						// is it a watchable-list?
						if (item instanceof WatchableList) {
							final WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null) {
								_theTrackDataListener.setPrimary(list);
							}
						}
					}
				}, _treeViewer, _myLayers);
				CorePlugin.run(doIt);
			}
		};
		_makePrimary.setText("Make Primary");
		_makePrimary.setToolTipText("Make this item the primary ");
		_makePrimary.setImageDescriptor(DebriefPlugin.getImageDescriptor("icons/16/make_primary.png"));
		_makePrimary.setEnabled(false);

		_makeSecondary = new Action() {
			@Override
			public void run() {

				final AbstractOperation doIt = new SelectionOperation("Make secondary", new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						// is it a watchable-list?
						if (item instanceof WatchableList) {
							final WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null) {
								_theTrackDataListener.setSecondary(list);
							}
						}
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						// is it a watchable-list?
						if (item instanceof WatchableList) {
							final WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null) {
								_theTrackDataListener.removeSecondary(list);
							}
						}
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						// is it a watchable-list?
						if (item instanceof WatchableList) {
							final WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null) {
								_theTrackDataListener.setSecondary(list);
							}
						}
					}
				}, _treeViewer, _myLayers);
				CorePlugin.run(doIt);

			}
		};
		_makeSecondary.setText("Make Secondary");
		_makeSecondary.setToolTipText("Set this item as the secondary track");
		_makeSecondary.setImageDescriptor(DebriefPlugin.getImageDescriptor("icons/16/make_secondary.png"));
		_makeSecondary.setEnabled(false);

		_addAsSecondary = new Action() {
			@Override
			public void run() {

				final AbstractOperation doIt = new SelectionOperation("Add as secondary", new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						// is it a watchable-list?
						if (item instanceof WatchableList) {
							final WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null) {
								_theTrackDataListener.addSecondary(list);
							}
						}
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						// is it a watchable-list?
						if (item instanceof WatchableList) {
							final WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null) {
								_theTrackDataListener.removeSecondary(list);
							}
						}
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						// is it a watchable-list?
						if (item instanceof WatchableList) {
							final WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null) {
								_theTrackDataListener.addSecondary(list);
							}
						}
					}
				}, _treeViewer, _myLayers);
				CorePlugin.run(doIt);

			}
		};
		_addAsSecondary.setText("Add as Secondary");
		_addAsSecondary.setToolTipText("Add this item to the secondary tracks");
		_addAsSecondary.setImageDescriptor(DebriefPlugin.getImageDescriptor("icons/16/add_secondary.png"));
		_addAsSecondary.setEnabled(false);

		_hideAction = new Action() {

			@Override
			public void run() {
				final AbstractOperation doIt = new SelectionOperation("Hide item", new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						setPlottableVisible(item, false);
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						setPlottableVisible(item, true);
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						setPlottableVisible(item, false);
					}
				}, _treeViewer, _myLayers);
				CorePlugin.run(doIt);
				enableVisibilityActions((StructuredSelection) getSelection());
			}
		};
		_hideAction.setText("Hide item");
		_hideAction.setToolTipText("Hide selected items");
		_hideAction.setImageDescriptor(DebriefPlugin.getImageDescriptor("icons/16/hide.png"));

		_revealAction = new Action() {
			@Override
			public void run() {
				final AbstractOperation doIt = new SelectionOperation("reveal item", new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						setPlottableVisible(item, true);
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						setPlottableVisible(item, false);
					}
				}, new IOperateOn() {
					@Override
					public void doItTo(final Editable item) {
						setPlottableVisible(item, true);
					}
				}, _treeViewer, _myLayers);
				CorePlugin.run(doIt);
				enableVisibilityActions((StructuredSelection) getSelection());
			}
		};
		_revealAction.setText("Reveal item");
		_revealAction.setToolTipText("Reveal selected items");
		_revealAction.setImageDescriptor(DebriefPlugin.getImageDescriptor("icons/16/show.png"));

		_sortSensorsDTG = new Action("By DTG", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				super.run();

				// and flip the other item
				_sortSensorsName.setChecked(!_sortSensorsDTG.isChecked());

				// ok, regenerate the list
				refreshUI();
			}
		};
		_sortSensorsDTG.setToolTipText("Sort sensors chronologically");
		_sortSensorsDTG.setChecked(true);

		_sortSensorsName = new Action("By Name", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				super.run();

				// and flip the other item
				_sortSensorsDTG.setChecked(!_sortSensorsName.isChecked());

				// ok, regenerate the list
				refreshUI();

			}
		};
		_sortSensorsName.setToolTipText("Sort sensors alphabetically");
		_sortSensorsName.setChecked(false);

		// assign the correct sensor
		assignCorrectSortMode();

		final IHandlerService handlerService = getSite().getService(IHandlerService.class);
		handlerService.activateHandler(CollapseAllHandler.COMMAND_ID, new ActionHandler(_collapseAllAction));
	}

	void processNewData(final Layers theData, final Editable newItem, final HasEditables parentLayer) {
		if (!_treeViewer.getTree().isDisposed()) {
			// hmm, if newItem is null, it's probably because lots of things have
			// changed. We may have some more updates coming.
			// Let's group up such updates
			if (newItem == null) {
				// do we need to create the job?
				if (refreshOnDeleteJob == null) {
					// ok, create it
					refreshOnDeleteJob = createRefreshOnDeleteJob();
				}

				// ok, cancel any existing scheduled instance
				refreshOnDeleteJob.cancel();

				// and request one after a pause
				refreshOnDeleteJob.schedule(200);
			} else {
				// ok, it's a specific new item that we wish to display. run
				// a dedicated UI refresh
				Display.getDefault().asyncExec(new Runnable() {
					@SuppressWarnings("unlikely-arg-type")
					@Override
					public void run() {
						if (_treeViewer.getControl().isDisposed()) {
							return;
						}
						final TreePath[] paths = _treeViewer.getExpandedTreePaths();

						// ok, fire the change in the UI thread
						_treeViewer.setInput(theData);

						// open up the paths that were previously open
						_treeViewer.setExpandedTreePaths(paths);

						// hmm, do we know about the new item? If so, better select it
						if (newItem != null) {
							final ISelection selected;
							final EditableWrapper wrapped;
							if (newItem.equals(parentLayer)) {
								// ok, it's a top-level layer.
								wrapped = new EditableWrapper(newItem, null, theData);
								selected = new StructuredSelection(wrapped);
							} else {
								// wrap the plottable
								final EditableWrapper parentWrapper = new EditableWrapper((Editable) parentLayer, null,
										theData);
								wrapped = new EditableWrapper(newItem, parentWrapper, theData);
								selected = new StructuredSelection(wrapped);
							}

							// ok, now select it
							editableSelected(selected, wrapped);
						}
					}
				});
			}
		}

	}

	void processNewLayers() {
		if (_myLayersListener == null) {
			_myLayersListener = new Layers.DataListener2() {

				@Override
				public void dataExtended(final Layers theData) {
					dataExtended(theData, null, null);
				}

				@Override
				public void dataExtended(final Layers theData, final Plottable newItem,
						final HasEditables parentLayer) {
					processNewData(theData, newItem, parentLayer);
				}

				@Override
				public void dataModified(final Layers theData, final Layer changedLayer) {
				}

				@Override
				public void dataReformatted(final Layers theData, final Layer changedLayer) {
					handleReformattedLayer(changedLayer);
				}
			};
		}
		// right, listen for data being added
		_myLayers.addDataExtendedListener(_myLayersListener);

		// and listen for items being reformatted
		_myLayers.addDataReformattedListener(_myLayersListener);

		// do an initial population.
		processNewData(_myLayers, null, null);
	}

	protected void processReformattedLayers() {

		try {
			// right, we'll be building up a list of objects to refresh (all of the
			// objects in the indicated layer)
			final Vector<Object> newList = new Vector<Object>(0, 1);
			Widget changed = null;

			if (_pendingLayers.size() > 0) {
				for (final Iterator<Layer> iter = _pendingLayers.iterator(); iter.hasNext();) {
					final Layer changedLayer = iter.next();

					changed = _treeViewer.findEditable(changedLayer);
					// see if we can find the element related to the indicated layer
					final TreeItem thisItem = (TreeItem) changed;

					if (thisItem != null) {
						// add the item and its children to the list
						addItemAndChildrenToList(newList, thisItem);
					}
				}
			} else {
				// hey, all of the layers need updating.
				// better get on with it.
				changed = _treeViewer.findEditable(_myLayers);

				final Tree theTree = (Tree) changed;
				final TreeItem[] children = theTree.getItems();
				for (int i = 0; i < children.length; i++) {
					final TreeItem thisItem = children[i];
					addItemAndChildrenToList(newList, thisItem);
				}
			}

			// delete the images for the specified items from the image cache
			// right, tell our label generator to ditch it's cache, since one or more
			// of the images may have changed
			// Issue #533
			// _myLabelProvider.resetCacheFor(newList);
			_myLabelProvider.resetCacheFor(_treeViewer);

			// and do the update
			final Object[] itemsToUpdate = newList.toArray();
			_treeViewer.update(itemsToUpdate, new String[] { VISIBILITY_COLUMN_NAME });
		} catch (final Exception e) {
			CorePlugin.getDefault().getLog().log(new Status(IStatus.WARNING, CorePlugin.PLUGIN_ID, "Tree warning", e));
		} finally {
			_alreadyDeferring = false;
			_pendingLayers.clear();
		}
	}

	private final void refreshUI() {
		_treeViewer.refresh(false);

		// and store the preference
		CorePlugin.getDefault().getPreferenceStore().setValue(SENSOR_SORT_BY_DTG, _sortSensorsDTG.isChecked());
	}

	@Override
	public void removeSelectionChangedListener(final ISelectionChangedListener listener) {
		_treeViewer.removeSelectionChangedListener(listener);
	}

	@Override
	public void setFocus() {
		if (_treeViewer != null) {
			_treeViewer.getControl().setFocus();
		}
	}

	@Override
	public void setSelection(final ISelection selection) {
		if (_treeViewer != null && !_treeViewer.getControl().isDisposed()) {
			_treeViewer.setSelection(selection);
		}
	}

}
