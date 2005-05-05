package org.mwc.cmap.narrative.views;

import java.beans.*;
import java.util.*;

import junit.framework.TestCase;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.Narrative.*;
import org.mwc.cmap.core.DataTypes.Narrative.NarrativeData.NarrativeEntry;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;

import MWC.GenericData.HiResDate;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

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

public class NarrativeView extends ViewPart
{
	private TableViewer viewer;

	private ViewerFilter filter = null;

	private Action filterToggleAction;

	private Action action1;

	private Action action2;

	private PartMonitor _myPartMonitor;

	private PropertyChangeListener _temporalListener = null;

	/**
	 * the provider for our narrative data
	 */
	NarrativeContentProvider _content = new NarrativeContentProvider();

	/**
	 * whether we are following the time set by the narrative provider
	 */
	private boolean _followingTime = true;

	/**
	 * whether we are controlling the time in the dataset from the narrative
	 */
	private boolean _controllingTime = true;

	/**
	 * the temporal dataset controlling the narrative entry currently displayed
	 */
	private TimeProvider _myTemporalDataset;

	/**
	 * the "write" interface for the plot which tracks the narrative, where
	 * avaialable
	 */
	private ControllableTime _controllableTime;

	/** whether we should make sure the current narrative entry is visible, or let it
	 * scroll out of sight
	 */
	private boolean _jumpToCurrent = true;

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

			if (element instanceof NarrativeData.NarrativeEntry)
			{
				NarrativeData.NarrativeEntry ne = (NarrativeEntry) element;
				if (ne.getEntryType().equals("type_1"))
					res = true;
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
	public NarrativeView()
	{

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		viewer = createTableWithColumns(parent);
		viewer.setContentProvider(_content);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());

		// Create Action instances
		createViewActions();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// try to add ourselves to listen out for page changes
		// getSite().getWorkbenchWindow().getPartService().addPartListener(this);

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(NarrativeProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part)
					{
						// implementation here.
						NarrativeProvider np = (NarrativeProvider) part;
						viewer.setInput(np.getNarrative());
					}
				});
		_myPartMonitor.addPartListener(NarrativeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part)
					{
						// implementation here.
						NarrativeProvider provider = (NarrativeProvider) part;
						// yes, but is it our current one?
						if (_content.isCurrentDocument(provider.getNarrative()))
						{
							// yes, better clear the view then
							viewer.setInput(null);
						}
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part)
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
					public void eventTriggered(String type, Object part)
					{
						_myTemporalDataset.removeListener(_temporalListener, TimeProvider.TIME_CHANGED_PROPERTY_NAME);
						_myTemporalDataset = null;
						_temporalListener = null;
					}
				});		
		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part)
					{
						// implementation here.
						ControllableTime ct = (ControllableTime) part;
						_controllableTime = ct;
					}
				});
		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part)
					{
						ControllableTime ct = (ControllableTime) part;
						_controllableTime = null;
					}
				});			

		// ok we're all ready now.  just try and see if the current part is valid		
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());
		
	}

	/**
	 * @param parent
	 *          what we have to fit into
	 */
	private static TableViewer createTableWithColumns(Composite parent)
	{
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION);

		TableLayout layout = new TableLayout();
		table.setLayout(layout);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		String[] STD_HEADINGS =
		{ "DTG", "Track", "Type", "Entry" };

		layout.addColumnData(new ColumnWeightData(5, 40, true));
		TableColumn tc0 = new TableColumn(table, SWT.NONE);
		tc0.setText(STD_HEADINGS[0]);
		tc0.setAlignment(SWT.LEFT);
		tc0.setResizable(true);

		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc1 = new TableColumn(table, SWT.NONE);
		tc1.setText(STD_HEADINGS[1]);
		tc1.setAlignment(SWT.LEFT);
		tc1.setResizable(true);

		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc2 = new TableColumn(table, SWT.NONE);
		tc2.setText(STD_HEADINGS[2]);
		tc2.setAlignment(SWT.LEFT);
		tc2.setResizable(true);

		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc3 = new TableColumn(table, SWT.NONE);
		tc3.setText(STD_HEADINGS[3]);
		tc3.setAlignment(SWT.LEFT);
		tc3.setResizable(true);
		return new TableViewer(table);
	}

	private void createViewActions()
	{

		// -------------------------------------------------------
		// Toggle filter action
		filterToggleAction = new Action("Only show Type_1", Action.AS_CHECK_BOX)
		{

			public void run()
			{
				// Use default political type for simplicity
				if (isChecked())
				{
					if (filter == null)
						filter = new Type1_Filter();
					viewer.addFilter(filter);
				}
				else
					viewer.removeFilter(filter);
			}
		};
		filterToggleAction.setToolTipText("Hide anything other than type_1");
		filterToggleAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
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
			_myTemporalDataset.removeListener(_temporalListener,
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
		}
	}

	private void hookContextMenu()
	{
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				NarrativeView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
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
		manager.add(new Separator());
		manager.add(filterToggleAction);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
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
				System.out.println("Action 1 executed");
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
				System.out.println("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

	}

	private void hookDoubleClickAction()
	{
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				// hmm, are we controlling the narrative time?
				if (_controllingTime)
				{
					ISelection selection = viewer.getSelection();
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					NarrativeData.NarrativeEntry ne = (NarrativeEntry) obj;
					_controllableTime.setTime(this, ne.getDTG());
				}
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
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
		if (_followingTime)
		{
			// move our list to the correct DTG
			
			// first find the current DTG
			NarrativeData narr =  (NarrativeData) viewer.getInput();
			NarrativeEntry currentItem = narr.getEntryNearestTo(newDTG);
			
			// did we find one?
			if(currentItem != null)
			{
				// yup, store it in a selection
				IStructuredSelection sel = new StructuredSelection(currentItem);
				
				// and update the table
				viewer.setSelection(sel, _jumpToCurrent );
			}
		}
		else
		{
			// hey, just ignore it
		}
	}

	// //////////////////////////////
	// selection listener bits
	// //////////////////////////////

	/**
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 * 
	 * @author ian.mayo created: {date}
	 */

	private class NarrativeContentProvider implements IStructuredContentProvider,
			PropertyChangeListener
	{
		private NarrativeData currentNarrative;

		private StructuredViewer viewer;

		public final boolean isCurrentDocument(final Object testDocument)
		{
			return testDocument == currentNarrative;
		}

		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
			if (viewer == null)
				viewer = (StructuredViewer) v;

			if (newInput != oldInput)
			{
				if (currentNarrative != null)
				{
					currentNarrative.removePropertyChangeListener(this);
				}

				if (newInput != null)
				{
					// store the new narrative
					currentNarrative = (NarrativeData) newInput;

					// and listen to the new one
					currentNarrative.addPropertyChangeListener(this);
				}
			}
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object parent)
		{
			Object[] res = null;
			// ok, cn
			String first = null;
			if (parent != null)
			{
				NarrativeData narr = (NarrativeData) parent;
				if (narr != null)
					first = "one:" + narr.getName();
				else
					first = "One";

				Vector theNarrs = new Vector(10, 10);
				Iterator iter = narr.getData().iterator();
				while (iter.hasNext())
				{
					NarrativeData.NarrativeEntry ne = (NarrativeEntry) iter.next();
					theNarrs.add(ne);
				}
				res = theNarrs.toArray();
			}

			return res;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent arg0)
		{
			// JDG2E: 10b - Content Provider tells viewer about model change
			// Make sure control exists - no sense telling a disposed widget to
			// react
			Control ctrl = viewer.getControl();
			if (ctrl != null && !ctrl.isDisposed())
			{
				ctrl.getDisplay().asyncExec(new Runnable()
				{
					public void run()
					{
						viewer.refresh();
					}
				});
			}
		}
	}

	/**
	 * how to show a narrative as a series of labels
	 * 
	 * @author ian.mayo
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		private String[] dateFormats =
		{ "yyyy MMM dd HH:mm", "ddHHmm ss", "HH:mm:ss", "HH:mm:ss.SSS" };

		public String formattedDTG(HiResDate dtg)
		{
			return DebriefFormatDateTime.toStringHiRes(dtg);
		}

		public String getColumnText(Object obj, int index)
		{
			String res = null;
			NarrativeData.NarrativeEntry ne = (NarrativeEntry) obj;

			switch (index)
			{
			case 0:
				res = ne.getDTGString();
				break;
			case 1:
				res = ne.getTrackName();
				break;
			case 2:
				res = ne.getEntryType();
				break;
			case 3:
				res = ne.getEntry();
				break;
			default:
				res = "n/a";
				break;
			}

			return res;
		}

		public Image getColumnImage(Object obj, int index)
		{
			return null;
		}
	}

	class NameSorter extends ViewerSorter
	{
	}

	public class TestNarrativeView extends TestCase
	{

	}

}
