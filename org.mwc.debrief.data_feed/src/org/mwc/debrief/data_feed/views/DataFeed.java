/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.data_feed.views;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.ui_support.PartMonitor;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.ReaderWriter.Replay.ImportReplay.Runner;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.temporal.ControllableTime;
import MWC.Utilities.Errors.Trace;

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

	private final ArrayList<RealTimeProvider> _dataProviders;

	/**
	 * The constructor.
	 */
	public DataFeed()
	{  
		_importer = new ImportReplay(getSWTRunner());

	//	_provider = new DummyDataProvider();

		// sort out the list of data-sources
		CorePlugin.logError(Status.INFO, "Starting to load data providers", null);

		_dataProviders = new ArrayList<RealTimeProvider>();
		final IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(
				"org.mwc.debrief.data_feed", "RealTimeProvider");

		// check: Any <extension> tags for our extension-point?
		if (point != null)
		{
			final IExtension[] extensions = point.getExtensions();

			for (int i = 0; i < extensions.length; i++)
			{
				final IConfigurationElement[] ces = extensions[i].getConfigurationElements();

				for (int j = 0; j < ces.length; j++)
				{
					// if this is the tag we want ("tool") create a descriptor
					// for it
					if (ces[j].getName().equals("provider"))
					{
						System.out.println("found new data-feed provider:" + ces[j].getName());
						final IConfigurationElement thisEl = ces[j];
						RealTimeProvider cl;
						try
						{
							cl = (RealTimeProvider) thisEl.createExecutableExtension("class");
							_dataProviders.add(cl);
						}
						catch (final CoreException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		CorePlugin.logError(Status.INFO, "Finished loading data providers", null);
	}


	/** copy of implementation in DebriefPlugin
	 * 
	 * @return
	 */
  public static Runner getSWTRunner()
  {
    return new ImportReplay.Runner()
    {
      @Override
      public void run(Runnable runnable)
      {
        Display.getDefault().asyncExec(runnable);
      }
    };
  }
  
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent)
	{
		final Composite topHolder = new Composite(parent, SWT.NONE);
		topHolder.setLayout(new RowLayout(SWT.VERTICAL));

		final Composite btnHolder = new Composite(topHolder, SWT.NONE);
		final RowLayout btnRow = new RowLayout();
		btnRow.type = SWT.HORIZONTAL;
		btnRow.wrap = false;
		btnRow.pack = true;
		btnHolder.setLayout(btnRow);
		_sourceList = new ComboViewer(btnHolder);
		_sourceList.setLabelProvider(new LabelProvider(){

			public String getText(final Object element)
			{
				final RealTimeProvider prov = (RealTimeProvider) element;
				return prov.getName();
			}});
		// add them
		for (final Iterator<RealTimeProvider> iter = _dataProviders.iterator(); iter.hasNext();)
		{
			final RealTimeProvider prov = iter.next();
			_sourceList.add(prov);
		}

		_sourceList.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(final SelectionChangedEvent event)
			{
				sourceChanged();
			}});
		
		_connectToggle = new Button(btnHolder, SWT.TOGGLE);
		_connectToggle.setText("    Connect    ");
		_connectToggle.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
			}

			public void widgetSelected(final SelectionEvent e)
			{
				connectPressed();
			}
		});

		_myState = new Label(btnHolder, SWT.NONE);
		_myState.setText("==================");

		// fire our UI components
		final FillLayout fill = new FillLayout();
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
					public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
					{
						_myLayers = (Layers) part;
					}
				});

		// unusually, we are also going to track the open event for narrative data
		// so that we can start off with some data
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
					{
						final Layers newLayers = (Layers) part;
						if (newLayers == _myLayers)
							_myLayers = null;
					}

				});

		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
					{
						// implementation here.
						final ControllableTime ct = (ControllableTime) part;
						_controllableTime = ct;
					}
				});
		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.DEACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part, final IWorkbenchPart parentPart)
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
		final IStructuredSelection sel = (IStructuredSelection) _sourceList.getSelection();
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
		_myPartMonitor.ditch();

		// also stop listening for time events
		if (_controllableTime != null)
		{
		}
	}

	private void contributeToActionBars()
	{
		final IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(final IMenuManager manager)
	{
		manager.add(_liveUpdate);
		manager.add(new Separator());
	}

	private void fillLocalToolBar(final IToolBarManager manager)
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

	public void insertData(final String data)
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
				final HiResDate dtg = _importer.readLine(data);

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
		catch (final IOException e)
		{
			CorePlugin.logError(Status.ERROR, "failed whilst reading from real-time data feed",
					e);
		}
    catch (ParseException e)
    {
      Trace.trace(e, "While parsing date from real-time data feed");
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
					final String dtg = new Date().toString() + ":" + msg;

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
