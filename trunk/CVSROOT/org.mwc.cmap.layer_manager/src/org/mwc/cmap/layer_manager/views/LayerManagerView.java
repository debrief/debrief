package org.mwc.cmap.layer_manager.views;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.layer_manager.views.support.PlottableWrapper;
import org.mwc.cmap.layer_manager.views.support.ViewContentProvider;
import org.mwc.cmap.layer_manager.views.support.ViewLabelProvider;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;

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

	private Action action1;

	private Action action2;

	private Action doubleClickAction;

	private Layers _myLayers;

	private Layers.DataListener _myLayersListener;

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
		_treeViewer.setLabelProvider(new ViewLabelProvider(this));
		_treeViewer.setSorter(new NameSorter());
		_treeViewer.setInput(getViewSite());
		_treeViewer.setComparer(new IElementComparer(){

			public boolean equals(Object a, Object b)
			{
				// do our special case for comparing plottables
				if(a instanceof PlottableWrapper)
				{
					PlottableWrapper pw = (PlottableWrapper) a;
					a = pw.getPlottable();
				}
				
				if(b instanceof PlottableWrapper)
				{
					PlottableWrapper pw = (PlottableWrapper) b;
					b = pw.getPlottable();
				}
				
				
				return a == b;
			}

			public int hashCode(Object element)
			{
				int res = 0;
				
				if(element instanceof PlottableWrapper)
				{
					PlottableWrapper pw = (PlottableWrapper) element;				
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
							_treeViewer.setInput(null);
						}
					}

				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());

		// set ourselves as selection source
		getSite().setSelectionProvider(_treeViewer);

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
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions()
	{
		action1 = new Action()
		{
			public void run()
			{
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action()
		{
			public void run()
			{
				applyOperationToSelection(new IOperateOn()
				{
					public void doItTo(Plottable item)
					{
						item.setVisible(!item.getVisible());
					}
				});
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action()
		{
			public void run()
			{
				applyOperationToSelection(new IOperateOn()
				{

					public void doItTo(Plottable item)
					{
						item.setVisible(!item.getVisible());
					}
				});
			}
		};
	}

	private void hookDoubleClickAction()
	{
		_treeViewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message)
	{
		MessageDialog.openInformation(_treeViewer.getControl().getShell(),
				"Layer Manager", message);
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
				_myLayersListener = new Layers.DataListener()
				{

					public void dataModified(Layers theData, Layer changedLayer)
					{
					}

					public void dataExtended(Layers theData)
					{
						processNewData(theData);
					}

					public void dataReformatted(Layers theData, Layer changedLayer)
					{
						processReformattedLayer(changedLayer);
					}
				};
			}
			// right, listen for data being added
			_myLayers.addDataExtendedListener(_myLayersListener);

			// and listen for items being reformatted
			_myLayers.addDataReformattedListener(_myLayersListener);

			// do an initial population.
			processNewData(_myLayers);
		}
	}

	/** recursive class used to build up a list containing the item together
	 * with all child items
	 * @param list the list we're building up
	 * @param item the item to add (together with its children)
	 */
	private void addItemAndChildrenToList(Vector list, TreeItem item)
	{
		Object myData = item.getData();
		if(myData != null)
			list.add(item.getData());
		TreeItem[] children = item.getItems();
		if(children.length > 0)
		{
			for (int i = 0; i < children.length; i++)
			{
				TreeItem thisChild = children[i];
				addItemAndChildrenToList(list, thisChild);
			}
		}
	}
	
	protected void processReformattedLayer(Layer changedLayer)
	{
		System.out.println("re-presenting layer after formatting:" + changedLayer);

		// right, we'll be building up a list of objects to refresh (all of the objects in the indicated layer)
		Vector newList = new Vector(0,1);
		
		Widget changed = null;
		
		// right. has just one layer updated?
		if(changedLayer != null)
		{
			changed = _treeViewer.findPlottable(changedLayer);	
			// see if we can find the element related to the indicated layer
			TreeItem thisItem = (TreeItem) changed;

			// add the item and its children to the list
			addItemAndChildrenToList(newList, thisItem);
		}
		else
		{
			changed = _treeViewer.findPlottable(_myLayers);
			
			Tree theTree = (Tree) changed;
			TreeItem[] children = theTree.getItems();
			for (int i = 0; i < children.length; i++)
			{
				TreeItem thisItem = children[i];
				addItemAndChildrenToList(newList, thisItem);
			}
		}

		// and do the update
		Object[] itemsToUpdate = newList.toArray();
		_treeViewer.update(itemsToUpdate, new String[]{VISIBILITY_COLUMN_NAME});
	}

	private void processNewData(Layers theData)
	{
		if (!_treeViewer.getTree().isDisposed())
			_treeViewer.setInput(theData);
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

      parentLayer = thisP.getTopLevelLayer();

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

}
