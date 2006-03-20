package org.mwc.cmap.layer_manager.views;

import java.util.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.property_support.*;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.layer_manager.Layer_managerPlugin;
import org.mwc.cmap.layer_manager.views.support.*;

import Debrief.Tools.Tote.WatchableList;
import MWC.GUI.*;

/**
 * provide a tree of items on the plot.
 */

public class LayerManagerView extends ViewPart
{

	public static final String NAME_COLUMN_NAME = "Name";

	public static final String VISIBILITY_COLUMN_NAME = "Visibility";

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;

	private MyTreeViewer _treeViewer;

	private DrillDownAdapter drillDownAdapter;

	/**
	 * create a new top-level layer
	 */
	private Action _createLayer;

	/**
	 * make the current item the primary track
	 */
	private Action _makePrimary;

	/**
	 * add the current item to the secondary track
	 */
	private Action _makeSecondary;

	/**
	 * hide the selected item(s)
	 */
	private Action _hideAction;

	/**
	 * reveal the selected item(s)
	 */
	private Action _revealAction;

	private Layers _myLayers;

	private Layers.DataListener _myLayersListener;

	private ISelectionChangedListener _selectionChangeListener;

	/**
	 * toggle to indicate whether user wants narrative to always jump to
	 * highlighted entry
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

	protected TrackManager _theTrackDataListener;

	/**
	 * whether we are already ignoring firing messages
	 */
	private static boolean _alreadyDeferring = false;

	/**
	 * class that embodies applying an operation to a series of selected points.
	 * the series of points are remembered, so that they can be undone/redone
	 * 
	 * @author ian.mayo
	 */
	private static final class SelectionOperation extends AbstractOperation
	{
		/**
		 * the selected items
		 */
		StructuredSelection _theSelection;

		/**
		 * what we are going to execute
		 */
		private IOperateOn _execute;

		/**
		 * what we are going to undo
		 */
		private IOperateOn _undo;

		/**
		 * what we are going to redo
		 */
		private IOperateOn _redo;

		/**
		 * who is giving us the selection
		 */
		private ISelectionProvider _provider;

		/**
		 * who we fire the update to
		 */
		private Layers _destination;

		/**
		 * define our operation
		 * 
		 * @param label
		 *          what to label it
		 * @param execute
		 *          the operation to execute
		 * @param undo
		 *          how to do an undo
		 * @param redo
		 *          how to do a redo
		 * @param provider
		 *          who is going to provide the selection
		 * @param destination
		 *          what to update on completion
		 */

		private SelectionOperation(String label, IOperateOn execute, IOperateOn undo,
				IOperateOn redo, ISelectionProvider provider, Layers destination)
		{
			super(label);

			// get remembering
			_provider = provider;
			_execute = execute;
			_undo = undo;
			_redo = redo;
			_destination = destination;

			// put in the global context, for some reason
			addContext(CorePlugin.CMAP_CONTEXT);
		}

		/**
		 * ok, do the operation
		 */
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			// ok, take a copy of the selection - for our undo/redo operations
			_theSelection = (StructuredSelection) _provider.getSelection();

			// cool, go for it
			applyOperation(_execute, _theSelection, _destination);

			return Status.OK_STATUS;
		}

		/**
		 * ok, redo the operation
		 */
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			applyOperation(_redo, _theSelection, _destination);
			return Status.OK_STATUS;
		}

		/**
		 * ok, undo the operation
		 */

		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
		{
			applyOperation(_undo, _theSelection, _destination);
			return Status.OK_STATUS;
		}

		/**
		 * @return
		 */
		public boolean canExecute()
		{
			return _execute != null;
		}

		/**
		 * @return
		 */
		public boolean canRedo()
		{
			return _redo != null;
		}

		/**
		 * @return
		 */
		public boolean canUndo()
		{
			return _undo != null;
		}

	}

	class NameSorter extends ViewerSorter
	{
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			int res = 0;
			PlottableWrapper p1 = (PlottableWrapper) e1;
			PlottableWrapper p2 = (PlottableWrapper) e2;
			if ((p1.getPlottable() instanceof Comparable)
					&& (p2.getPlottable() instanceof Comparable))
			{
				Comparable w1 = (Comparable) p1.getPlottable();
				Comparable w2 = (Comparable) p2.getPlottable();
				res = w1.compareTo(w2);
			}
			else
				res = super.compare(viewer, e1, e2);

			return res;
		}
	}

	public void dispose()
	{
		// TODO Auto-generated method stub
		super.dispose();

		// make sure we close the listeners
		clearLayerListener();

	}

	/**
	 * stop listening to the layer, if necessary
	 */
	private void clearLayerListener()
	{
		if (_myLayers != null)
		{
			_myLayers.removeDataExtendedListener(_myLayersListener);
			_myLayersListener = null;
			_myLayers = null;
		}
	}

	private static class MyTreeViewer extends TreeViewer
	{
		public MyTreeViewer(Tree parent)
		{
			super(parent);
		}

		public MyTreeViewer(Composite parent, int style)
		{
			super(parent, style);
		}

		public Widget findPlottable(Plottable item)
		{
			return super.findItem(item);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		
//
//    =================================
//    NOTE: the following block of commented out code shows tick-boxes by the 
//		Tree myTree = new Tree(parent, SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
//
//		// listen out for selection events
//		myTree.addListener(SWT.Selection, new Listener()
//		{
//			public void handleEvent(Event event)
//			{
//				if (event.detail == SWT.CHECK)
//				{
//					TreeItem ti = (TreeItem) event.item;
//					boolean isChecked = ti.getChecked();
//					PlottableWrapper pw = (PlottableWrapper) ti.getData();
//					if (pw != null)
//					{
//						pw.getPlottable().setVisible(isChecked);
//						Layer parent = pw.getTopLevelLayer();
//						_myLayers.fireModified(parent);
//					}
//				}
//			}
//		});
//		_treeViewer = new MyTreeViewer(myTree);

		 _treeViewer = new MyTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		_treeViewer.setUseHashlookup(true);
		drillDownAdapter = new DrillDownAdapter(_treeViewer);
		_treeViewer.setContentProvider(new ViewContentProvider(this));
		_treeViewer.setLabelProvider(new ViewLabelProvider());
		_treeViewer.setSorter(new NameSorter());
		_treeViewer.setInput(getViewSite());
		_treeViewer.setComparer(new IElementComparer()
		{
			public boolean equals(Object a, Object b)
			{
				// do our special case for comparing plottables
				if (a instanceof PlottableWrapper)
				{
					PlottableWrapper pw = (PlottableWrapper) a;
					a = pw.getPlottable();
				}

				if (b instanceof PlottableWrapper)
				{
					PlottableWrapper pw = (PlottableWrapper) b;
					b = pw.getPlottable();
				}

				return a == b;
			}

			public int hashCode(Object element)
			{
				int res = 0;

				if (element instanceof PlottableWrapper)
				{
					PlottableWrapper pw = (PlottableWrapper) element;
					Plottable pl = pw.getPlottable();
					if (pl != null)
						res += pw.getPlottable().hashCode();
				}
				else
					res = element.hashCode();

				return res;
			}

		});

		// and format the tree
		Tree tree = _treeViewer.getTree();
		tree.setHeaderVisible(true);
		formatTree(tree);

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// and setup the part monitoring
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						processNewLayers(part);
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						processNewLayers(part);
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// is this our set of layers?
						if (part == _myLayers)
						{
							// stop listening to this layer
							clearLayerListener();

							// and clear the tree
							if (_treeViewer.getContentProvider() != null)
								_treeViewer.setInput(null);
						}
					}

				});

		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						ISelectionProvider iS = (ISelectionProvider) part;
						iS.addSelectionChangedListener(_selectionChangeListener);
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.DEACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						ISelectionProvider iS = (ISelectionProvider) part;
						iS.removeSelectionChangedListener(_selectionChangeListener);
					}
				});

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// cool, remember about it.
						_theTrackDataListener = (TrackManager) part;
					}
				});
		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// ok, ditch it.
						_theTrackDataListener = null;
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());

		// set ourselves as selection source
		getSite().setSelectionProvider(_treeViewer);

		_selectionChangeListener = new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				// right, see what it is
				ISelection sel = event.getSelection();
				if (sel instanceof StructuredSelection)
				{
					StructuredSelection ss = (StructuredSelection) sel;
					Object datum = ss.getFirstElement();
					if (datum instanceof PlottableWrapper)
					{
						PlottableWrapper pw = (PlottableWrapper) datum;
						plottableSelected(sel, pw);
					}
				}
			}
		};

		// also listen out ourselves to any changes, so we can update the button
		// enablement
		_treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				ISelection isel = event.getSelection();
				if (isel instanceof StructuredSelection)
				{
					StructuredSelection ss = (StructuredSelection) isel;

					// right, see if this is an item we can make primary
					_makePrimary.setEnabled(isValidPrimary(ss));

					// and see if we can make it a secondary
					_makeSecondary.setEnabled(isValidSecondary(ss));
				}
			}

		});

	}

	/**
	 * find out if the selection is valid for setting as primary
	 * 
	 * @param ss
	 * @return
	 */
	protected boolean isValidPrimary(StructuredSelection ss)
	{
		boolean res = false;
		if (ss.size() == 1)
		{
			PlottableWrapper pw = (PlottableWrapper) ss.getFirstElement();
			Plottable pl = pw.getPlottable();

			// hey, first see if it's even a candidate
			if (pl instanceof WatchableList)
				// now see if it's already the primary
				if (pl != _theTrackDataListener.getPrimaryTrack())
				{
					res = true;
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
	protected boolean isValidSecondary(StructuredSelection ss)
	{
		boolean res = false;
		if (ss.size() >= 1)
		{
			PlottableWrapper pw = (PlottableWrapper) ss.getFirstElement();
			Plottable pl = pw.getPlottable();
			if (pl instanceof WatchableList)
			{
				// hey, it's a maybe.
				res = true;

				// ok, it's a candidate. now see if it's already one of the secondaries
				WatchableList[] secs = _theTrackDataListener.getSecondaryTracks();
				if (secs != null)
				{
					for (int i = 0; i < secs.length; i++)
					{
						WatchableList thisList = secs[i];
						if (thisList == pl)
						{
							res = false;
							break;
						}
					}
				}
			}
		}
		return res;
	}

	private void formatTree(Tree tree)
	{
		// define the columns
		TreeColumn nameCol = new TreeColumn(tree, SWT.NONE);
		nameCol.setText(NAME_COLUMN_NAME);
		nameCol.setWidth(180);
		TreeColumn visibleCol = new TreeColumn(tree, SWT.NONE);
		visibleCol.setText(VISIBILITY_COLUMN_NAME);
		visibleCol.setWidth(50);
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				LayerManagerView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(_treeViewer.getControl());
		_treeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, _treeViewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(_followSelectionToggle);
		manager.add(new Separator());
		manager.add(_makePrimary);
		manager.add(_makeSecondary);
		manager.add(new Separator());
		manager.add(_revealAction);
		manager.add(_hideAction);
		manager.add(new Separator());
		manager.add(_expandAllAction);
		manager.add(_collapseAllAction);
		manager.add(new Separator());
		manager.add(_createLayer);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		// get the selected item
		StructuredSelection sel = (StructuredSelection) _treeViewer.getSelection();

		// right, we only worry about primary, secondary, hide, reveal if something
		// is selected
		if (sel.size() > 0)
		{
			// ok, allow hide/reveal
			manager.add(_hideAction);
			manager.add(_revealAction);

			// have a look at the data-types to sort out whether to primary/secondary
			if (isValidPrimary(sel))
				manager.add(_makePrimary);
			if (isValidSecondary(sel))
				manager.add(_makeSecondary);

			// now stick in the separator anyway
			manager.add(new Separator());
		}

		// drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		// hey, sort out the data-specific items
		// build up a list of menu items

		// create some lists to store our selected items
		Editable[] eList = new Editable[sel.size()];
		Layer[] parentLayers = new Layer[sel.size()];
		Layer[] updateLayers = new Layer[sel.size()];

		// right, now populate them
		Object[] oList = sel.toArray();
		for (int i = 0; i < oList.length; i++)
		{
			PlottableWrapper wrapper = (PlottableWrapper) oList[i];
			eList[i] = wrapper.getPlottable();

			// sort out the parent layer
			PlottableWrapper theParent = wrapper.getParent();

			// hmm, did we find one?
			if (theParent != null)
				// yes, store it
				parentLayers[i] = (Layer) wrapper.getParent().getPlottable();
			else
				// nope - store a null
				parentLayers[i] = null;

			updateLayers[i] = wrapper.getTopLevelLayer();
		}

		// ok, sort out what we can do with all of this...
		RightClickSupport.getDropdownListFor(manager, eList, updateLayers, parentLayers,
				_myLayers, false);

	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_followSelectionToggle);
		manager.add(_makePrimary);
		manager.add(_makeSecondary);
		manager.add(_hideAction);
		manager.add(_revealAction);
	//	manager.add(new Separator());
	//	drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions()
	{

		_followSelectionToggle = new Action("Jump to selection", Action.AS_CHECK_BOX)
		{
		};
		_followSelectionToggle.setText("Follow selection");
		_followSelectionToggle.setChecked(true);
		_followSelectionToggle
				.setToolTipText("Ensure selected item in plot is always visible");
		_followSelectionToggle.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/follow_selection.gif"));

		_collapseAllAction = new Action("Collapse all", Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				// go for it.
				_treeViewer.collapseAll();
			}
		};

		_collapseAllAction.setText("Collapse all layers");
		_collapseAllAction.setToolTipText("Collapse all layers in the layer manager");
		_collapseAllAction.setImageDescriptor(Layer_managerPlugin
				.getImageDescriptor("icons/collapseall.gif"));

		_expandAllAction = new Action("Expand all", Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				// go for it.
				_treeViewer.expandAll();
			}
		};
		_expandAllAction.setText("Expand all layers");
		_expandAllAction.setToolTipText("Expand all layers in the layer manager");
		_expandAllAction.setImageDescriptor(Layer_managerPlugin
				.getImageDescriptor("icons/expandall.gif"));

		_createLayer = new Action()
		{
			public void run()
			{
				// ask the user
				InputDialog id = new InputDialog(null, "Create new layer", "Layer name:", "",
						null);
				int res = id.open();

				// was ok pressed?
				if (res == InputDialog.OK)
				{
					// yup, create the layer
					String newName = id.getValue();

					// yes, create the layer
					BaseLayer bl = new BaseLayer();
					bl.setName(newName);

					// and add it
					_myLayers.addThisLayer(bl);
				}
			}
		};
		_createLayer.setText("Create layer");
		_createLayer.setToolTipText("Create a new top-level layer");
		_createLayer.setImageDescriptor(Layer_managerPlugin
				.getImageDescriptor("icons/new_layer.gif"));

		_makePrimary = new Action()
		{
			public void run()
			{
				AbstractOperation doIt = new SelectionOperation("Make primary", new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						// is it a watchable-list?
						if (item instanceof WatchableList)
						{
							WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null)
								_theTrackDataListener.setPrimary(list);
						}
					}
				}, new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						// is it a watchable-list?
						if (item instanceof WatchableList)
						{
							// make it the primary
							if (_theTrackDataListener != null)
								_theTrackDataListener.setPrimary(null);
						}
					}
				}, new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						// is it a watchable-list?
						if (item instanceof WatchableList)
						{
							WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null)
								_theTrackDataListener.setPrimary(list);
						}
					}
				}, _treeViewer, _myLayers);
				CorePlugin.run(doIt);

				applyOperationToSelection(new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
					}
				});

			}
		};
		_makePrimary.setText("Make Primary");
		_makePrimary.setToolTipText("Make this item the primary ");
		_makePrimary.setImageDescriptor(CorePlugin.getImageDescriptor("icons/primary.gif"));
		_makePrimary.setEnabled(false);

		_makeSecondary = new Action()
		{
			public void run()
			{

				AbstractOperation doIt = new SelectionOperation("Make secondary",
						new IOperateOn()
						{
							public void doItTo(Plottable item)
							{
								// is it a watchable-list?
								if (item instanceof WatchableList)
								{
									WatchableList list = (WatchableList) item;

									// make it the primary
									if (_theTrackDataListener != null)
										_theTrackDataListener.addSecondary(list);
								}
							}
						}, new IOperateOn()
						{
							public void doItTo(Plottable item)
							{
								// is it a watchable-list?
								if (item instanceof WatchableList)
								{
									WatchableList list = (WatchableList) item;

									// make it the primary
									if (_theTrackDataListener != null)
										_theTrackDataListener.removeSecondary(list);
								}
							}
						}, new IOperateOn()
						{
							public void doItTo(Plottable item)
							{
								// is it a watchable-list?
								if (item instanceof WatchableList)
								{
									WatchableList list = (WatchableList) item;

									// make it the primary
									if (_theTrackDataListener != null)
										_theTrackDataListener.addSecondary(list);
								}
							}
						}, _treeViewer, _myLayers);
				CorePlugin.run(doIt);

			}
		};
		_makeSecondary.setText("Make Secondary");
		_makeSecondary.setToolTipText("Add this item to the secondary tracks");
		_makeSecondary.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/secondary.gif"));
		_makeSecondary.setEnabled(false);

		_hideAction = new Action()
		{
			public void run()
			{
				AbstractOperation doIt = new SelectionOperation("Hide item", new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						item.setVisible(false);
					}
				}, new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						item.setVisible(true);
					}
				}, new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						item.setVisible(false);
					}
				}, _treeViewer, _myLayers);
				CorePlugin.run(doIt);
			}
		};
		_hideAction.setText("Hide item");
		_hideAction.setToolTipText("Hide selected items");
		_hideAction.setImageDescriptor(CorePlugin.getImageDescriptor("icons/hide.gif"));

		_revealAction = new Action()
		{
			public void run()
			{
				AbstractOperation doIt = new SelectionOperation("reveal item", new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						item.setVisible(true);
					}
				}, new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						item.setVisible(false);
					}
				}, new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						item.setVisible(true);
					}
				}, _treeViewer, _myLayers);
				CorePlugin.run(doIt);
			}
		};
		_revealAction.setText("Reveal item");
		_revealAction.setToolTipText("Reveal selected items");
		_revealAction.setImageDescriptor(CorePlugin.getImageDescriptor("icons/reveal.gif"));
	}

	private void hookDoubleClickAction()
	{
		_treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				// doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		_treeViewer.getControl().setFocus();
	}

	private void processNewLayers(Object part)
	{
		// just check we're not already looking at it
		if (part != _myLayers)
		{
			// implementation here.
			_myLayers = (Layers) part;
			if (_myLayersListener == null)
			{
				_myLayersListener = new Layers.DataListener2()
				{

					public void dataModified(Layers theData, Layer changedLayer)
					{
					}

					public void dataExtended(Layers theData)
					{
						dataExtended(theData, null, null);
					}

					public void dataReformatted(Layers theData, Layer changedLayer)
					{
						handleReformattedLayer(changedLayer);
					}

					public void dataExtended(Layers theData, Plottable newItem, Layer parentLayer)
					{
						processNewData(theData, newItem, parentLayer);
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
	}

	/**
	 * recursive class used to build up a list containing the item together with
	 * all child items
	 * 
	 * @param list
	 *          the list we're building up
	 * @param item
	 *          the item to add (together with its children)
	 */
	private void addItemAndChildrenToList(Vector list, TreeItem item)
	{
		Object myData = item.getData();
		if (myData != null)
			list.add(item.getData());
		TreeItem[] children = item.getItems();
		if (children.length > 0)
		{
			for (int i = 0; i < children.length; i++)
			{
				TreeItem thisChild = children[i];
				addItemAndChildrenToList(list, thisChild);
			}
		}
	}

	private static Set _pendingLayers = new TreeSet(new Comparator()
	{
		public int compare(Object arg0, Object arg1)
		{
			int res = 1;

			if (arg0.equals(arg1))
				res = 0;

			if (arg0.getClass() == arg1.getClass())
			{
				if (arg0 instanceof Comparable)
				{
					Comparable c0 = (Comparable) arg0;
					Comparable c1 = (Comparable) arg1;
					res = c0.compareTo(c1);
				}
			}

			return res;
		}

	});

	/**
	 * one or more layers have been changed. This method may get called lots of
	 * times. Stack up the events - and just call our UI update method once at the
	 * end
	 * 
	 * @param changedLayer
	 *          the layer which has changed
	 */
	protected void handleReformattedLayer(Layer changedLayer)
	{
		// right - store this layer (if we have one)
		if (changedLayer != null)
			_pendingLayers.add(changedLayer);

		if (_alreadyDeferring)
		{
			// hey - already processing - add this layer to the pending ones
		}
		else
		{
			_alreadyDeferring = true;

			// right. we're not already doing some processing
			Display dis = Display.getCurrent();
			dis.asyncExec(new Runnable()
			{
				public void run()
				{
					processReformattedLayers();
				}
			});
		}
	}

	protected void processReformattedLayers()
	{
		try
		{
			// right, we'll be building up a list of objects to refresh (all of the
			// objects in the indicated layer)
			Vector newList = new Vector(0, 1);
			Widget changed = null;

			for (Iterator iter = _pendingLayers.iterator(); iter.hasNext();)
			{
				Layer changedLayer = (Layer) iter.next();

				// right. has just one layer updated?
				if (changedLayer != null)
				{
					changed = _treeViewer.findPlottable(changedLayer);
					// see if we can find the element related to the indicated layer
					TreeItem thisItem = (TreeItem) changed;

					// add the item and its children to the list
					addItemAndChildrenToList(newList, thisItem);
				}
				else
				{
					// hey, all of the layers need updating.
					// better get on with it.
					changed = _treeViewer.findPlottable(_myLayers);

					Tree theTree = (Tree) changed;
					TreeItem[] children = theTree.getItems();
					for (int i = 0; i < children.length; i++)
					{
						TreeItem thisItem = children[i];
						addItemAndChildrenToList(newList, thisItem);
					}
				}

			}

			// and do the update
			Object[] itemsToUpdate = newList.toArray();
			_treeViewer.update(itemsToUpdate, new String[] { VISIBILITY_COLUMN_NAME });
		}
		catch (Exception e)
		{

		}
		finally
		{
			_alreadyDeferring = false;
			_pendingLayers.clear();
		}
	}

	private void processNewData(final Layers theData, final Plottable newItem,
			final Layer parentLayer)
	{
		if (!_treeViewer.getTree().isDisposed())
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					// ok, fire the change in the UI thread
					_treeViewer.setInput(theData);

					// hmm, do we know about the new item? If so, better select it
					if (newItem != null)
					{
						// wrap the plottable
						PlottableWrapper parentWrapper = new PlottableWrapper(parentLayer, null,
								theData);
						PlottableWrapper wrapped = new PlottableWrapper(newItem, parentWrapper,
								theData);
						ISelection selected = new StructuredSelection(wrapped);

						// and select it
						plottableSelected(selected, wrapped);
					}
				}
			});
		}

	}

	private static interface IOperateOn
	{
		public void doItTo(Plottable item);
	}

	private static void applyOperation(IOperateOn operation,
			IStructuredSelection selection, Layers myLayers)
	{

		Iterator iterator = selection.iterator();
		boolean madeChange = false;
		Layer parentLayer = null;
		boolean multiLayer = false;

		while (iterator.hasNext())
		{
			Object obj = iterator.next();

			PlottableWrapper thisP = (PlottableWrapper) obj;
			Plottable res = thisP.getPlottable();
			Plottable thisOne = (Plottable) res;
			Layer thisParentLayer = null;

			// ok - do the business
			operation.doItTo(thisOne);

			// and remember that it worked
			madeChange = true;

			thisParentLayer = thisP.getTopLevelLayer();

			// ok. we've now got the parent layer
			// - is it the first one?
			if (parentLayer == null)
			{
				// yup. just store it
				parentLayer = thisParentLayer;
			}
			else
			{
				// nope, we've had at least one of these before
				if (parentLayer != thisParentLayer)
				{
					multiLayer = true;
				}
			}

		}

		// right. has a change been made?
		if (madeChange)
		{
			// yup. does it apply to just on layer - or all of them
			if (multiLayer)
			{
				// ok - update all layers
				triggerChartUpdate(null, myLayers);
			}
			else
			{
				// ok - just update the one layer
				triggerChartUpdate(parentLayer, myLayers);
			}
		}
	}

	/**
	 * user has double-clicked on an item. process.
	 * 
	 * @param operation
	 *          TODO
	 */
	private void applyOperationToSelection(IOperateOn operation)
	{
		IStructuredSelection selection = (IStructuredSelection) _treeViewer.getSelection();
		applyOperation(operation, selection, _myLayers);

	}

	private static void triggerChartUpdate(Layer changedLayer, Layers myLayers)
	{
		myLayers.fireReformatted(changedLayer);
	}

	public void plottableSelected(ISelection sel, PlottableWrapper pw)
	{
		if (_followSelectionToggle.isChecked())
			_treeViewer.setSelection(sel, _followSelectionToggle.isChecked());

	}

}
