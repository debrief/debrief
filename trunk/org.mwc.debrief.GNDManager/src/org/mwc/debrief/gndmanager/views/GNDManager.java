package org.mwc.debrief.gndmanager.views;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.debrief.gndmanager.Activator;
import org.mwc.debrief.gndmanager.Tracks.TrackStoreWrapper;
import org.mwc.debrief.gndmanager.views.ManagerView.Listener;
import org.mwc.debrief.gndmanager.views.io.ESearch;
import org.mwc.debrief.gndmanager.views.io.SearchModel.Facet;
import org.mwc.debrief.gndmanager.views.io.SearchModel.MatchList;

import MWC.GUI.Layers;
import MWC.TacticalData.GND.GPackage;

public class GNDManager extends ViewPart implements Listener
{

	public static final String SEARCH_URL = "http://localhost:9200/ais";
	public static final String DB_URL = "http://gnd.iriscouch.com/tracks";
//	public static final String SEARCH_URL = "http://localhost:9200/gnd";
//	public static final String DB_URL = "http://localhost:5984/tracks";

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.mwc.debrief.gndmanager.views.GNDManager";

	private Action action1;
	private Action action2;

	private ManagerView view;

	private ESearch _search;

	/**
	 * The constructor.
	 */
	public GNDManager()
	{
		_search = new ESearch();
	}

	private static String getDBUrl()
	{
		return Activator.getDefault().getPreferenceStore().getString(TrackStoreWrapper.COUCHDB_LOCATION);
	}
	
	

	private static String getIndexUrl()
	{
		return Activator.getDefault().getPreferenceStore().getString(TrackStoreWrapper.ES_LOCATION);
	}

	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		view = new ManagerViewImpl(parent, SWT.NONE);
		view.setListener(this);
		
		view.enableControls(false);

		// Create the help context id for the viewer's control
		makeActions();
		contributeToActionBars();
		
		// let the table be a selection provider
	//	getSite().setSelectionProvider(view.getSelectionProvider());
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

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions()
	{
		action1 = new Action()
		{
			public void run()
			{
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
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		view.setFoxus();
	}

	@Override
	public void doSearch()
	{
		// ok, get the selections
		MatchList res;
		try
		{
			res = _search.getMatches(getIndexUrl(),getDBUrl(), view);
			view.setResults(res);
		}
		catch (ConnectException e)
		{
			Activator.showMessage("Could not connect to server", e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Activator.showMessage("Trouble retrieving search", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void doReset()
	{
	}

	@Override
	public void doImport(ArrayList<String> items)
	{
		// find out the name to use
		
		InputDialog dlg = new InputDialog(this.getViewSite().getShell(), "Import data","Please provide a name for the dataset", "Imported tracks", null);
		if(dlg.open() == IStatus.OK)
		{
			final String name = dlg.getValue();
			// find the active editor
			IWorkbenchPage page = this.getViewSite().getPage();
			if (page != null)
			{
				IEditorPart editor = page.getActiveEditor();
				if (editor != null)
				{

					// ok, convert them to URLs
					GPackage data = new GPackage(name, getDBUrl(), items);

					// and insert the data
					Layers layers = (Layers) editor.getAdapter(Layers.class);
					if (layers != null)
					{
						layers.addThisLayer(data);
					}
				}
			}
		}
		

	}

	@Override
	public void doConnect()
	{
		MatchList list;
		try
		{
			list = _search.getAll(getIndexUrl(), getDBUrl());
			Facet platforms = list.getFacet("platform");
			Facet platformTypes = list.getFacet("platform_type");
			Facet trials = list.getFacet("trial");
			if (platforms != null)
				view.getPlatforms().setItems(platforms.toList(), false);
			if (platformTypes != null)
				view.getPlatformTypes().setItems(platformTypes.toList(), false);
			if (trials != null)
				view.getTrials().setItems(trials.toList(), false);
			
			// did it work?
			view.enableControls(true);
		}
		catch (ConnectException e)
		{
			Activator.showMessage("Could not connect to server", e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Activator.showMessage("Trouble retrieving search", e.getMessage());
			e.printStackTrace();
		}
	}

}