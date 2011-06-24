package org.mwc.debrief.data_feed.views;

import java.io.IOException;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.ui_support.PartMonitor;

import Debrief.ReaderWriter.Replay.ImportReplay;
import MWC.GUI.Layers;
import MWC.GenericData.*;

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

public class DataFeed extends ViewPart implements LiveFeedViewer
{

	/**
	 * whether to update the plot centred on ownship
	 */
	private Action _liveUpdate;

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;

	/**
	 * the "write" interface for the plot which controls what the DTG is
	 */
	private ControllableTime _controllableTime;

	/**
	 * the set of Layers we're extending
	 */
	protected Layers _myLayers;

	private ListViewer _myList;

	private Label _myState;

	private Button _connectToggle;

	/**
	 * our data-provider
	 */
	RealTimeProvider _provider = null;

	/**
	 * helper to parse data in REPLAY format
	 */
	ImportReplay _importer = null;

	/**
	 * the list of available data-sources
	 */
	private ComboViewer _sourceList;

	private ArrayList<RealTimeProvider> _dataProviders;

	/**
	 * The constructor.
	 */
	public DataFeed()
	{
		_importer = new ImportReplay();

	//	_provider = new DummyDataProvider();

		// sort out the list of data-sources
		CorePlugin.logError(Status.INFO, "Starting to load data providers", null);

		_dataProviders = new ArrayList<RealTimeProvider>();
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(
				"org.mwc.debrief.data_feed", "RealTimeProvider");

		// check: Any <extension> tags for our extension-point?
		if (point != null)
		{
			IExtension[] extensions = point.getExtensions();

			for (int i = 0; i < extensions.length; i++)
			{
				IConfigurationElement[] ces = extensions[i].getConfigurationElements();

				for (int j = 0; j < ces.length; j++)
				{
					// if this is the tag we want ("tool") create a descriptor
					// for it
					if (ces[j].getName().equals("provider"))
					{
						System.out.println("found new data-feed provider:" + ces[j].getName());
						IConfigurationElement thisEl = ces[j];
						RealTimeProvider cl;
						try
						{
							cl = (RealTimeProvider) thisEl.createExecutableExtension("class");
							_dataProviders.add(cl);
						}
						catch (CoreException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		CorePlugin.logError(Status.INFO, "Finished loading data providers", null);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		Composite topHolder = new Composite(parent, SWT.NONE);
		topHolder.setLayout(new RowLayout(SWT.VERTICAL));

		Composite btnHolder = new Composite(topHolder, SWT.NONE);
		RowLayout btnRow = new RowLayout();
		btnRow.type = SWT.HORIZONTAL;
		btnRow.wrap = false;
		btnRow.pack = true;
		btnHolder.setLayout(btnRow);
		_sourceList = new ComboViewer(btnHolder);
		_sourceList.setLabelProvider(new LabelProvider(){

			public String getText(Object element)
			{
				RealTimeProvider prov = (RealTimeProvider) element;
				return prov.getName();
			}});
		// add them
		for (Iterator<RealTimeProvider> iter = _dataProviders.iterator(); iter.hasNext();)
		{
			RealTimeProvider prov = iter.next();
			_sourceList.add(prov);
		}

		_sourceList.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event)
			{
				sourceChanged();
			}});
		
		_connectToggle = new Button(btnHolder, SWT.TOGGLE);
		_connectToggle.setText("    Connect    ");
		_connectToggle.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(SelectionEvent e)
			{
			}

			public void widgetSelected(SelectionEvent e)
			{
				connectPressed();
			}
		});

		_myState = new Label(btnHolder, SWT.NONE);
		_myState.setText("==================");

		// fire our UI components
		FillLayout fill = new FillLayout();
		fill.type = SWT.VERTICAL;
		parent.setLayout(fill);
		_myList = new ListViewer(parent);

		// Create Action instances
		makeActions();
		contributeToActionBars();

		// try to add ourselves to listen out for page changes
		// getSite().getWorkbenchWindow().getPartService().addPartListener(this);

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						_myLayers = (Layers) part;
					}
				});

		// unusually, we are also going to track the open event for narrative data
		// so that we can start off with some data
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						Layers newLayers = (Layers) part;
						if (newLayers == _myLayers)
							_myLayers = null;
					}

				});

		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// implementation here.
						ControllableTime ct = (ControllableTime) part;
						_controllableTime = ct;
					}
				});
		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.DEACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// no, don't bother clearing the controllable time when the plot is
						// de-activated,
						// - since with the highlight on the narrative, we want to be able
						// to control the time still.
						// _controllableTime = null;
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());
	}

	protected void sourceChanged()
	{
		if(_provider != null)
		{
			_connectToggle.setSelection(false);
			connectPressed();
		}
		
		// ok, store the new data item
		IStructuredSelection sel = (IStructuredSelection) _sourceList.getSelection();
		_provider = (RealTimeProvider) sel.getFirstElement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		super.dispose();

		// and stop listening for part activity
		_myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());

		// also stop listening for time events
		if (_controllableTime != null)
		{
		}
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(_liveUpdate);
		manager.add(new Separator());
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_liveUpdate);
	}

	private void makeActions()
	{
		_liveUpdate = new Action("Live Update", Action.AS_CHECK_BOX)
		{
		};
		_liveUpdate.setText("Live Update");
		_liveUpdate.setChecked(true);
		_liveUpdate.setToolTipText("Keep plot centred on current position of primary track");
		_liveUpdate.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_UP));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{

	}

	// //////////////////////////////
	// temporal data management
	// //////////////////////////////

	public void insertData(String data)
	{
		// ok - fire the new string of data into our reader-writer
		try
		{
			// tell it where our data it
			if (_myLayers != null)
			{
				// ok, here's our data
				_importer.setLayers(_myLayers);

				// and let it import itself
				HiResDate dtg = _importer.readLine(data);

				if (_liveUpdate.isChecked())
				{
					// and tell the layers there's been an update
					_myLayers.fireExtended();

					// fire the new time, if we know it...
					if (dtg != null)
						_controllableTime.setTime(this, dtg, true);
				}

			}
		}
		catch (IOException e)
		{
			CorePlugin.logError(Status.ERROR, "failed whilst reading from real-time data feed",
					e);
		}

	}

	public void showMessage(final String msg)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!_myList.getControl().isDisposed())
				{
					// ok, generate the DTG.
					String dtg = new Date().toString() + ":" + msg;

					_myList.add(dtg);

					// hey, also select the last item
					_myList.reveal(dtg);
				}
			}
		});
	}

	protected void connectPressed()
	{
		if (_connectToggle.getSelection())
		{
			_connectToggle.setText("Disconnect");
		}
		else
		{
			_connectToggle.setText("Connect");
		}

		// do we have a data-provider
		if (_provider != null)
		{
			if (_connectToggle.getSelection())
				_provider.connect(this);
			else
				_provider.disconnect(this);
		}
	}

	public void showState(final String newState)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!_myState.isDisposed())
				{
					_myState.setText(newState);
				}
			}
		});
	}

}
