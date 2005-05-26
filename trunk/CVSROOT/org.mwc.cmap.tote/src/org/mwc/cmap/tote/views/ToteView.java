package org.mwc.cmap.tote.views;

import java.beans.*;
import java.util.Vector;

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

import Debrief.Wrappers.NarrativeWrapper;
import Debrief.Wrappers.NarrativeWrapper.NarrativeEntry;
import MWC.GenericData.HiResDate;

/**
 * View which provides a track tote. The track tote is a table of values who are
 * calculated using the current status of one or more vessel tracks
 * <p>
 */

public class ToteView extends ViewPart
{
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

	public class Type1_Filter extends ViewerFilter
	{

		/**
		 * Return true if the political unit is county or smaller
		 * 
		 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public boolean select(Viewer viewer, Object parentElement, Object element)
		{
			boolean res = false;

			if (element instanceof NarrativeWrapper.NarrativeEntry)
			{
				NarrativeWrapper.NarrativeEntry ne = (NarrativeEntry) element;
				String thisType = ne.getType();
				if (thisType != null)
				{
					if (thisType.equals("type_1"))
						res = true;
				}
			}

			return res;
		}

		/**
		 * @see org.eclipse.jface.viewers.ViewerFilter#isFilterProperty(java.lang.Object,
		 *      java.lang.String)
		 */
		public boolean isFilterProperty(Object element, String property)
		{
			// Say yes to political unit
			// return (property.equals(ILocation.POLITICAL_CHANGED));
			return false;
		}
	}

	/**
	 * The constructor.
	 */
	public ToteView()
	{

	}

	/**
	 * This is a callback that will allow us to create the _tableViewer and
	 * initialize it.
	 */
	public void createPartControl(Composite parent)
	{
		Button tester = new Button(parent, SWT.NONE);
		tester.setText("and here we are");
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
//		MenuManager menuMgr = new MenuManager("#PopupMenu");
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener()
//		{
//			public void menuAboutToShow(IMenuManager manager)
//			{
//				ToteView.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(_tableViewer.getControl());
//		_tableViewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuMgr, _tableViewer);
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
	 * @return
	 */
	private NarrativeWrapper.NarrativeEntry getCurrentEntry()
	{
		ISelection selection = _tableViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		NarrativeWrapper.NarrativeEntry ne = (NarrativeEntry) obj;
		return ne;
	}

	/**
	 * Passing the focus request to the _tableViewer's control.
	 */
	public void setFocus()
	{
	//	_tableViewer.getControl().setFocus();
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
		System.out.println("time updated");
	}

	// //////////////////////////////
	// selection listener bits
	// //////////////////////////////

}
