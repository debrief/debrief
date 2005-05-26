package org.mwc.cmap.tote.views;

import java.awt.Color;
import java.beans.*;
import java.util.*;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeProvider;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.DataTypes.TrackData.*;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.tote.calculations.CalculationLoaderManager;

import Debrief.Tools.Tote.*;
import MWC.GenericData.*;

/**
 * View which provides a track tote. The track tote is a table of values who are
 * calculated using the current status of one or more vessel tracks
 * <p>
 */

public class ToteView extends ViewPart
{
	// Extension point tag and attributes in plugin.xml
	private static final String EXTENSION_POINT_ID = "ToteCalculation";

	private static final String EXTENSION_TAG = "calculation";

	private static final String EXTENSION_TAG_LABEL_ATTRIB = "name";

	private static final String EXTENSION_TAG_ICON_ATTRIB = "icon";

	private static final String EXTENSION_TAG_CLASS_ATTRIB = "class";

	// Plug-in ID from <plugin> tag in plugin.xml
	private static final String PLUGIN_ID = "org.mwc.cmap.tote";

	/**
	 * the table showing the calcs
	 */
	private TableViewer _tableViewer;

	private IStructuredContentProvider _content;

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor = null;

	/**
	 * the listener we use to track time changes
	 */
	private PropertyChangeListener _temporalListener = null;

	/**
	 * where we get our track data from
	 */
	TrackDataProvider _trackData = null;

	/**
	 * where we get/store what the current set of calcs are
	 */
	ToteCalculationProvider _toteCalcs = null;

	/**
	 * our current set of calculations
	 */
	Vector _myCalculations = null;

	/**
	 * the temporal dataset controlling the narrative entry currently displayed
	 */
	private TimeProvider _myTemporalDataset;

	/**
	 * the "write" interface for the plot which tracks the narrative, where
	 * avaialable
	 */
	private ControllableTime _controllableTime;

	/**
	 * the editor currently providing our narrative
	 */
	protected IEditorPart _currentEditor;

	/**
	 * helper object which loads plugin file-loaders
	 */
	private CalculationLoaderManager _loader;

	private Label _tempStatus;

	/**
	 * The constructor.
	 */
	public ToteView()
	{
		_myCalculations = new Vector(0,1);
	}

	/**
	 * This is a callback that will allow us to create the _tableViewer and
	 * initialize it.
	 */
	public void createPartControl(Composite parent)
	{
		_tempStatus = new Label(parent, SWT.NONE);
		_tempStatus.setText("pending");
		//		
		// _tableViewer = createTableWithColumns(parent);
		// _tableViewer.setContentProvider(_content);
		// _tableViewer.setLabelProvider(new ViewLabelProvider());
		// _tableViewer.setSorter(new NameSorter());

		// Create Action instances
		createViewActions();

		makeActions();
		hookContextMenu();
		// hookDoubleClickAction();
		contributeToActionBars();

		// try to add ourselves to listen out for page changes
		// getSite().getWorkbenchWindow().getPartService().addPartListener(this);

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(NarrativeProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						storeDetails(part, parentPart);
					}
				});

		// unusually, we are also going to track the open event for narrative data
		// so that we can start off with some data
		_myPartMonitor.addPartListener(NarrativeProvider.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						storeDetails(part, parentPart);
					}

				});

		_myPartMonitor.addPartListener(NarrativeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// implementation here.
						NarrativeProvider provider = (NarrativeProvider) part;
						// yes, but is it our current one?
						// if (_content.isCurrentDocument(provider.getNarrative()))
						// {
						// // yes, better clear the view then
						// _tableViewer.setInput(null);
						// _currentEditor = null;
						// }
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// just check we're not already looking at it
						if (part != _myTemporalDataset)
						{
							// implementation here.
							_myTemporalDataset = (TimeProvider) part;
							if (_temporalListener == null)
							{
								_temporalListener = new PropertyChangeListener()
								{
									public void propertyChange(PropertyChangeEvent event)
									{
										// ok, use the new time
										HiResDate newDTG = (HiResDate) event.getNewValue();
										timeUpdated(newDTG);
									}
								};
							}
							_myTemporalDataset.addListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
						}
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// implementation here.
						_myTemporalDataset = (TimeProvider) part;
						if (_temporalListener == null)
						{
							_temporalListener = new PropertyChangeListener()
							{
								public void propertyChange(PropertyChangeEvent event)
								{
									// ok, use the new time
									HiResDate newDTG = (HiResDate) event.getNewValue();
									timeUpdated(newDTG);
								}
							};
						}
						_myTemporalDataset.addListener(_temporalListener,
								TimeProvider.TIME_CHANGED_PROPERTY_NAME);
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						_myTemporalDataset.removeListener(_temporalListener,
								TimeProvider.TIME_CHANGED_PROPERTY_NAME);
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());

	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		// let the parent do its bits
		super.init(site, memento);

		// ok - declare and load the supplemental plugins which can load datafiles
		initialiseCalcLoaders();		

		toteCalculation[] calcs = _loader.findCalculations();
		for (int i = 0; i < calcs.length; i++)
		{
			toteCalculation thisCalc = calcs[i];
			_myCalculations.add(thisCalc);
		}
	}

	private void createViewActions()
	{

		// // -------------------------------------------------------
		// // Toggle filter action
		// filterToggleAction = new Action("Only show Type_1", Action.AS_CHECK_BOX)
		// {
		//
		// public void run()
		// {
		// // Use default political type for simplicity
		// if (isChecked())
		// {
		// if (filter == null)
		// filter = new Type1_Filter();
		// _tableViewer.addFilter(filter);
		// }
		// else
		// _tableViewer.removeFilter(filter);
		// }
		// };
		// filterToggleAction.setToolTipText("Hide anything other than type_1");
		// filterToggleAction.setImageDescriptor(PlatformUI.getWorkbench()
		// .getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		super.dispose();

		if (_myPartMonitor != null)
		{
			// and stop listening for part activity
			_myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());
		}
		// also stop listening for time events
		if (_controllableTime != null)
		{
			_myTemporalDataset.removeListener(_temporalListener,
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
		}
	}

	private void hookContextMenu()
	{
		// MenuManager menuMgr = new MenuManager("#PopupMenu");
		// menuMgr.setRemoveAllWhenShown(true);
		// menuMgr.addMenuListener(new IMenuListener()
		// {
		// public void menuAboutToShow(IMenuManager manager)
		// {
		// ToteView.this.fillContextMenu(manager);
		// }
		// });
		// Menu menu = menuMgr.createContextMenu(_tableViewer.getControl());
		// _tableViewer.getControl().setMenu(menu);
		// getSite().registerContextMenu(menuMgr, _tableViewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(new Separator());

	}

	private void fillContextMenu(IMenuManager manager)
	{
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		// manager.add(action1);
		// manager.add(action2);

	}

	private void makeActions()
	{

	}

	/**
	 * Passing the focus request to the _tableViewer's control.
	 */
	public void setFocus()
	{
		// _tableViewer.getControl().setFocus();
	}

	/**
	 * @param part
	 * @param parentPart
	 */
	private void storeDetails(Object part, IWorkbenchPart parentPart)
	{
		// implementation here.
	}

	// //////////////////////////////
	// temporal data management
	// //////////////////////////////

	/**
	 * the data we are looking at has updated. If we're set to follow that time,
	 * update ourselves
	 */
	private void timeUpdated(HiResDate newDTG)
	{
		Watchable pri = new Watchable(){
			public WorldLocation getLocation()
			{
				double lon = Math.random();
				double lat = Math.random();
				return new WorldLocation(lat, lon, 0);
			}
			public double getCourse()
			{
				double crse = Math.random() * 360;
				return crse;
			}
			public double getSpeed()
			{
				return 2;
			}
			public double getDepth()
			{
				return 3;
			}
			public WorldArea getBounds()
			{
				return null;
			}
			public void setVisible(boolean val)
			{
			}
			public boolean getVisible()
			{
				return true;
			}
			public HiResDate getTime()
			{
				return null;
			}
			public String getName()
			{
				return "aa";
			}
			public Color getColor()
			{
				return null;
			}};
			Watchable sec = new Watchable(){
				public WorldLocation getLocation()
				{
					double lon = Math.random();
					double lat = Math.random();
					return new WorldLocation(lat, lon, 0);
				}
				public double getCourse()
				{
					double crse = Math.random() * 360;
					return crse;
				}
				public double getSpeed()
				{
					return 4;
				}
				public double getDepth()
				{
					return 5;
				}
				public WorldArea getBounds()
				{
					return null;
				}
				public void setVisible(boolean val)
				{
				}
				public boolean getVisible()
				{
					return true;
				}
				public HiResDate getTime()
				{
					return null;
				}
				public String getName()
				{
					return "aa";
				}
				public Color getColor()
				{
					return null;
				}};		
			
		String msg = "";
		for (Iterator iter = _myCalculations.iterator(); iter.hasNext();)
		{
			toteCalculation thisC = (toteCalculation) iter.next();
			msg += "" + thisC.getTitle() + "," + thisC.getUnits()  +" " + thisC.update(pri, sec, newDTG) + "\n";
		}
		_tempStatus.setText(msg);
	}

	// //////////////////////////////
	// selection listener bits
	// //////////////////////////////

	/**
	 * 
	 */
	private void initialiseCalcLoaders()
	{
		// hey - sort out our plot readers
		_loader = new CalculationLoaderManager(EXTENSION_POINT_ID, EXTENSION_TAG,
				PLUGIN_ID)
		{

			public toteCalculation createInstance(
					IConfigurationElement configElement, String label)
			{
				// get the attributes
				label = configElement.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);
				String icon = configElement.getAttribute(EXTENSION_TAG_ICON_ATTRIB);

				// create the instance
				toteCalculation res = null;

				// create the instance
				res = new CalculationLoaderManager.DeferredCalculation(configElement,
						label, icon);
				
				// and return it.
				return res;
			}

		};
	}

}
