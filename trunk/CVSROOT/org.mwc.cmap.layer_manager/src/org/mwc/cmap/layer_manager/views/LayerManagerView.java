package org.mwc.cmap.layer_manager.views;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeProvider;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.layer_manager.views.support.ViewContentProvider;
import org.mwc.cmap.layer_manager.views.support.ViewLabelProvider;
import org.mwc.cmap.layer_manager.views.support.ViewContentProvider.PlottableWrapper;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GenericData.HiResDate;

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
	

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;
	
	
	private TreeViewer _treeViewer;

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
	

	/** stop listening to the layer, if necessary
	 * 
	 *
	 */
	private void clearLayerListener()
	{
		if(_myLayers != null)
		{
		_myLayers.removeDataExtendedListener(_myLayersListener);
		_myLayersListener = null;
		_myLayers = null;
		}
	}	

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(_treeViewer);
		_treeViewer.setContentProvider(new ViewContentProvider(this));
		_treeViewer.setLabelProvider(new ViewLabelProvider(this));
		_treeViewer.setSorter(new NameSorter());
		_treeViewer.setInput(getViewSite());

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
						if(part == _myLayers)
						{
							clearLayerListener();
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
		nameCol.setText("Name");
		nameCol.setWidth(180);
		TreeColumn visibleCol = new TreeColumn(tree, SWT.NONE);
		visibleCol.setText("Visibility");
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
				showMessage("Action 2 executed");
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
				ISelection selection = _treeViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if(obj instanceof ViewContentProvider.PlottableWrapper)
				{
					ViewContentProvider.PlottableWrapper thisP =  (PlottableWrapper) obj;
					Plottable thePlottable = thisP.getPlottable();
					thePlottable.setVisible(!thePlottable.getVisible());
					
					// find the parent layer for this object
					Layer parentLayer = thisP.getParent();
					_myLayers.fireReformatted(parentLayer);
				}
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
			_myLayers.addDataExtendedListener(_myLayersListener);
			
			// do an initial population.
			processNewData(_myLayers);
		}
	}
	
	protected void processReformattedLayer(Layer changedLayer)
	{
		System.out.println("re-presenting layer after formatting:" + changedLayer);
		_treeViewer.refresh();
	}


	private void processNewData(Layers theData)
	{
		if(!_treeViewer.getTree().isDisposed())
		_treeViewer.setInput(theData);
	}
}