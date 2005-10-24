package org.mwc.cmap.layer_manager.views;

import java.util.*;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.property_support.PlottableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.layer_manager.Layer_managerPlugin;
import org.mwc.cmap.layer_manager.views.support.*;

import Debrief.Tools.Tote.WatchableList;
import MWC.GUI.*;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
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
	 * make the current item the primary track
	 */
	private Action _makePrimary;

	/**
	 * add the current item to the secondary track
	 */
	private Action _makeSecondary;

	/** hide the selected item(s)
	 * 
	 */
	private Action _hideAction;

	/** reveal the selected item(s)
	 * 
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
	
	/** action to allow user to collapse all layer manager nodes
	 * 
	 */
	private Action _collapseAllAction;

	
	/** action to allow user to expand all layer manager nodes
	 * 
	 */
	private Action _expandAllAction;

	
	protected TrackManager _theTrackDataListener;

	class NameSorter extends ViewerSorter
	{
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
		_treeViewer = new MyTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
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
					if(pl != null)
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
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						processNewLayers(part);
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						processNewLayers(part);
					}
				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// is this our set of layers?
						if (part == _myLayers)
						{
							// stop listening to this layer
							clearLayerListener();

							// and clear the tree
							if(_treeViewer.getContentProvider() != null)
								_treeViewer.setInput(null);
						}
					}

				});

		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						ISelectionProvider iS = (ISelectionProvider) part;
						iS.addSelectionChangedListener(_selectionChangeListener);
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						ISelectionProvider iS = (ISelectionProvider) part;
						iS.removeSelectionChangedListener(_selectionChangeListener);
					}
				});

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// cool, remember about it.
						_theTrackDataListener = (TrackManager) part;
					}
				});
		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// ok, ditch it.
						_theTrackDataListener = null;
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());

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
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(_makePrimary);
		manager.add(_makeSecondary);
		manager.add(_hideAction);
		manager.add(_revealAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_followSelectionToggle);
		manager.add(_makePrimary);
		manager.add(_makeSecondary);
		manager.add(_hideAction);
		manager.add(_revealAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions()
	{

		_followSelectionToggle = new Action("Jump to selection",
				Action.AS_CHECK_BOX)
		{
		};
		_followSelectionToggle.setText("Follow selection");
		_followSelectionToggle.setChecked(true);
		_followSelectionToggle
				.setToolTipText("Ensure selected item in plot is always visible");
		_followSelectionToggle.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/follow_selection.gif"));

		_collapseAllAction = new Action("Collapse all",
				Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				// go for it.
				_treeViewer.collapseAll();
			}
		};

		_collapseAllAction.setText("Collapse all layers");
		_collapseAllAction
				.setToolTipText("Collapse all layers in the layer manager");
    _collapseAllAction
    .setImageDescriptor(Layer_managerPlugin.getImageDescriptor("icons/collapseall.gif"));
		
    _expandAllAction = new Action("Expand all",
				Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				// go for it.
				_treeViewer.expandAll();
			}
		};
		_expandAllAction.setText("Expand all layers");
		_expandAllAction
				.setToolTipText("Expand all layers in the layer manager");
		_expandAllAction.setImageDescriptor(Layer_managerPlugin.getImageDescriptor("icons/expandall.gif"));

		
		_makePrimary = new Action()
		{
			public void run()
			{
				applyOperationToSelection(new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						// is it a watchable-list?
						if (item instanceof WatchableList)
						{
							WatchableList list = (WatchableList) item;

							// make it the primary
							if (_theTrackDataListener != null)
								_theTrackDataListener.primaryUpdated(list);
						}
					}
				});

			}
		};
		_makePrimary.setText("Make Primary");
		_makePrimary.setToolTipText("Make this item the primary ");
		_makePrimary.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));

		_makeSecondary = new Action()
		{
			public void run()
			{
				applyOperationToSelection(new IOperateOn()
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
				});
			}
		};
		_makeSecondary.setText("Make Secondary");
		_makeSecondary.setToolTipText("Add this item to the secondary tracks");
		_makeSecondary.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

		_hideAction = new Action()
		{
			public void run()
			{
				applyOperationToSelection(new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						item.setVisible(false);
					}
				});
			}
		};
		_hideAction.setText("Hide item");
		_hideAction.setToolTipText("Stop selected items from being visible");
		_hideAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));
		

		_revealAction = new Action()
		{
			public void run()
			{
				applyOperationToSelection(new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						item.setVisible(true);
					}
				});
			}
		};
		_revealAction.setText("Refeal item");
		_revealAction.setToolTipText("Reveal selected items");
		_revealAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
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

	private static boolean _alreadyDeferring = false;

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
		// right - store this layer
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
			_treeViewer
					.update(itemsToUpdate, new String[] { VISIBILITY_COLUMN_NAME });
		} catch (Exception e)
		{

		} finally
		{
			_alreadyDeferring = false;
			_pendingLayers.clear();
		}
	}

	private void processNewData(final Layers theData, final Plottable newItem, final Layer parentLayer)
	{
		if (!_treeViewer.getTree().isDisposed())
		{
			Display.getDefault().asyncExec(new Runnable(){
				public void run()
				{
					// ok, fire the change in the UI thread
					_treeViewer.setInput(theData);
					
					// hmm, do we know about the new item? If so, better select it
					if(newItem != null)
					{
						// wrap the plottable
						PlottableWrapper parentWrapper = new PlottableWrapper(parentLayer, null, theData);
						PlottableWrapper wrapped = new PlottableWrapper(newItem, parentWrapper,theData);
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

	/**
	 * user has double-clicked on an item. process.
	 * 
	 * @param operation
	 *          TODO
	 */
	private void applyOperationToSelection(IOperateOn operation)
	{
		IStructuredSelection selection = (IStructuredSelection) _treeViewer
				.getSelection();
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
				triggerChartUpdate(null);
			}
			else
			{
				// ok - just update the one layer
				triggerChartUpdate(parentLayer);
			}
		}
	}

	private void triggerChartUpdate(Layer changedLayer)
	{
		_myLayers.fireReformatted(changedLayer);
	}

	public void plottableSelected(ISelection sel, PlottableWrapper pw)
	{
		if (_followSelectionToggle.isChecked())
			_treeViewer.setSelection(sel, _followSelectionToggle.isChecked());
	}

}
