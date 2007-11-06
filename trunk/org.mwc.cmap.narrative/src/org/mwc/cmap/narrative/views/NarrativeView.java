package org.mwc.cmap.narrative.views;

import java.beans.*;
import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.narrative.NarrativePlugin;

import MWC.GenericData.HiResDate;
import MWC.TacticalData.*;
import MWC.TacticalData.IRollingNarrativeProvider.INarrativeListener;
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

	/**
	 * the action which stores the current DTG as a bookmark
	 */
	private Action _setAsBookmarkAction;

	/**
	 * toggle to indicate whether user wants narrative to always highlight to
	 * current DTG
	 */
	private Action _followTimeToggle;

	/**
	 * toggle to indicate whether user wants narrative to always jump to
	 * highlighted entry
	 */
	private Action _jumpToTimeToggle;

	/**
	 * toggle to indicate whether user wants rest of app to jump to highlighted
	 * entry
	 */
	private Action _controllingTimeToggle;

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;

	/**
	 * the listener we use to track time changes
	 */
	private PropertyChangeListener _temporalListener = null;

	/**
	 * the provider for our narrative data
	 */
	NarrativeContentProvider _content = new NarrativeContentProvider();

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

	protected IRollingNarrativeProvider _myRollingNarrative;

	protected INarrativeListener _myRollingNarrListener;

	protected Action _trackNewNarratives;

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

			if (element instanceof NarrativeEntry)
			{
				NarrativeEntry ne = (NarrativeEntry) element;
				String thisType = ne.getType();

				// hmm, but does this include type data?
				// if it does, then it's not for us to export it.
				if (thisType != null)
				{
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
	public NarrativeView()
	{

	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());

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

		// ok, listen out for part changes
		setupPartListeners();

	}

	/**
	 * 
	 */
	private void setupPartListeners()
	{
		_myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						loadNarrative(part, parentPart);
					}
				});

		// unusually, we are also going to track the open event for narrative data
		// so that we can start off with some data
		_myPartMonitor.addPartListener(IRollingNarrativeProvider.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						loadNarrative(part, parentPart);
					}

				});

		_myPartMonitor.addPartListener(IRollingNarrativeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
						if (newNarr == _myRollingNarrative)
						{
							// stop listening to old narrative
							_myRollingNarrative.removeNarrativeListener(
									IRollingNarrativeProvider.ALL_CATS, _myRollingNarrListener);
							viewer.setInput(null);
							_currentEditor = null;
							_myRollingNarrative = null;
						}
					}
				});

		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// just check we're not already looking at it
						if (part != _myTemporalDataset)
						{
							// ok, better stop listening to the old one
							if (_myTemporalDataset != null)
							{
								// yup, better ignore it
								_myTemporalDataset.removeListener(_temporalListener,
										TimeProvider.TIME_CHANGED_PROPERTY_NAME);

								_myTemporalDataset = null;
							}

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
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						if (part == _myTemporalDataset)
						{
							_myTemporalDataset.removeListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
						}
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
		String[] STD_HEADINGS = { "DTG", "Track", "Type", "Entry" };

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
		filterToggleAction = new Action("Only show entries with Type data",
				Action.AS_CHECK_BOX)
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
		filterToggleAction.setToolTipText("Hide anything without type data");
		filterToggleAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));

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
		manager.add(filterToggleAction);
		manager.add(new Separator());
		manager.add(_followTimeToggle);
		manager.add(_jumpToTimeToggle);
		manager.add(_controllingTimeToggle);

		// and the help link
		manager.add(new Separator());
		manager.add(CorePlugin.createOpenHelpAction("org.mwc.debrief.help.Narrative", null, this));

		
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(_setAsBookmarkAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		// manager.add(action1);
		// manager.add(action2);

		manager.add(_followTimeToggle);
		manager.add(_jumpToTimeToggle);
		manager.add(_controllingTimeToggle);
		manager.add(_trackNewNarratives);
	}

	private void makeActions()
	{
		_trackNewNarratives = _myPartMonitor.createSyncedAction("Track new narratives",
				"Always show narratives for selected provider", getSite());

		_followTimeToggle = new Action("Follow time", Action.AS_CHECK_BOX)
		{
		};
		_followTimeToggle.setText("Follow time");
		_followTimeToggle.setChecked(true);
		_followTimeToggle.setToolTipText("Highlight entry nearest current DTG");
		_followTimeToggle.setImageDescriptor(NarrativePlugin
				.getImageDescriptor("icons/history.png"));

		_jumpToTimeToggle = new Action("Jump to time", Action.AS_CHECK_BOX)
		{
		};
		_jumpToTimeToggle.setText("Jump to current");
		_jumpToTimeToggle.setChecked(true);
		_jumpToTimeToggle.setToolTipText("Ensure highlighted entry is always visible");
		_jumpToTimeToggle.setImageDescriptor(NarrativePlugin
				.getImageDescriptor("icons/magic-wand.png"));

		_controllingTimeToggle = new Action("Control time", Action.AS_CHECK_BOX)
		{
		};
		_controllingTimeToggle.setText("Control time");
		_controllingTimeToggle.setChecked(true);
		_controllingTimeToggle.setToolTipText("Make rest of application follow our time");
		_controllingTimeToggle.setImageDescriptor(NarrativePlugin
				.getImageDescriptor("icons/history_add.png"));

		_setAsBookmarkAction = new Action("Add DTG to bookmarks", Action.AS_PUSH_BUTTON)
		{

			public void run()
			{
				super.run();

				// get the current selection
				NarrativeEntry current = getCurrentEntry();

				addMarker(current);
			}

		};
		_setAsBookmarkAction.setText("Add bookmark");

	}

	protected void addMarker(NarrativeEntry entry)
	{
		try
		{
			// right, do we have an editor with a file?
			IEditorInput input = _currentEditor.getEditorInput();
			if (input instanceof IFileEditorInput)
			{
				// aaah, and is there a file present?
				IFileEditorInput ife = (IFileEditorInput) input;
				IResource file = ife.getFile();
				String currentText = entry.getEntry();
				if (file != null)
				{
					// yup, get the description
					InputDialog inputD = new InputDialog(getViewSite().getShell(),
							"Add bookmark at this DTG", "Enter description of this bookmark",
							currentText, null);
					inputD.open();

					String content = inputD.getValue();
					if (content != null)
					{
						IMarker marker = file.createMarker(IMarker.BOOKMARK);
						Map attributes = new HashMap(4);
						attributes.put(IMarker.MESSAGE, entry.getDTGString() + ":" + content);
						attributes.put(IMarker.LOCATION, "plot title");
						attributes.put(IMarker.LINE_NUMBER, "" + entry.getDTG().getMicros());
						attributes.put(IMarker.USER_EDITABLE, Boolean.FALSE);
						marker.setAttributes(attributes);
					}
				}

			}
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}

	}

	private void hookDoubleClickAction()
	{
		viewer.addDoubleClickListener(new IDoubleClickListener()
		{
			public void doubleClick(DoubleClickEvent event)
			{
				// hmm, are we controlling the narrative time?
				if (_controllingTimeToggle.isChecked())
				{
					NarrativeEntry ne = getCurrentEntry();
					_controllableTime.setTime(this, ne.getDTG(), true);
				}
			}

		});
	}

	/**
	 * @return
	 */
	private NarrativeEntry getCurrentEntry()
	{
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		NarrativeEntry ne = (NarrativeEntry) obj;
		return ne;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	/**
	 * @param part
	 * @param parentPart
	 */
	private void loadNarrative(Object part, IWorkbenchPart parentPart)
	{

		// check if we have our rolling narrative listener
		if (_myRollingNarrListener == null)
		{
			_myRollingNarrListener = new INarrativeListener()
			{
				public void newEntry(final NarrativeEntry entry)
				{
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							// ok, sort it.
							viewer.add(entry);
						}
					});
				}

                public void entryRemoved(NarrativeEntry entry)
                {
                    // ignore - we manage this differently
                }
			};
		}

		// start listening to the new provider
		IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
		if (newNarr != _myRollingNarrative)
		{
			if (_trackNewNarratives.isChecked())
			{
				if (_myRollingNarrative != null)
				{
					// stop listening to old narrative
					_myRollingNarrative.removeNarrativeListener(IRollingNarrativeProvider.ALL_CATS,
							_myRollingNarrListener);
				}

				// store the new one
				_myRollingNarrative = newNarr;

				_myRollingNarrative.addNarrativeListener(IRollingNarrativeProvider.ALL_CATS,
						_myRollingNarrListener);

				// and load the back-history
				viewer.setInput(_myRollingNarrative.getNarrativeHistory(null));
				if (parentPart instanceof IEditorPart)
				{
					_currentEditor = (IEditorPart) parentPart;
				}
			}
		}
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
		if (_followTimeToggle.isChecked())
		{
			// retrieve our data
			NarrativeEntry[] narr = (NarrativeEntry[]) viewer.getInput();

			// do we have any?
			if (narr != null)
			{
				NarrativeEntry res = null;

				// just check we're not looking for a time after the available data
			  if(newDTG.greaterThan(narr[narr.length-1].getDTG()))
			  {
			  	// cool, store it
			  	res = narr[narr.length-1];
			  }
			  else
			  {
			    // aah, but is it before?
			  	if(newDTG.lessThan(narr[0].getDTG()))
			  	{
			  		// yup, store it.
			  		res = narr[0];
			  	}
			  	else
			  	{
			  		// nope, better do it the hard way
			  		for (int i = 0; i < narr.length; i++)
						{
							NarrativeEntry thisE = narr[i];
							HiResDate dtg = thisE.getDTG();
							
							// right, are we still before the tgt dtg?
							// note: we want to do less than or equal to, but we can't so we'll just do
							// not greater than, which is equivalent
							if(!dtg.greaterThan(newDTG))
							{
								// yup, remember this one and move one
								res = thisE;
							}
							else
							{
								// nope.  cool. we've know the last matching dtg, just
								// drop out of the loop.
								break;
							}
							
						}
			  	}
			  }
				
				
//				NarrativeEntry currentItem = narr.getEntryNearestTo(newDTG);

				// did we find one?
				if (res != null)
				{
					// yup, store it in a selection
					IStructuredSelection sel = new StructuredSelection(res);

					// and update the table
					viewer.setSelection(sel, _jumpToTimeToggle.isChecked());
				}
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
		private NarrativeEntry[] currentNarrative;

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

				if (newInput != null)
				{
					// store the new narrative
					currentNarrative = (NarrativeEntry[]) newInput;
				}
			}
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object parent)
		{
			Object[] res = null;

			res = (Object[]) parent;

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
	static class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{

		/**
		 * keep track of which date format we're plotting with
		 */
		private String _myDateFormat = dateFormats[1];

		private static String[] dateFormats = { "yyyy MMM dd HH:mm", "ddHHmm ss", "HH:mm:ss",
				"HH:mm:ss.SSS" };

		/**
		 * ok, output the time component
		 * 
		 * @param dtg
		 * @return
		 */
		public String formattedDTG(HiResDate dtg)
		{
			return DebriefFormatDateTime.toStringHiRes(dtg, _myDateFormat);
		}

		public String getColumnText(Object obj, int index)
		{
			String res = null;
			NarrativeEntry ne = (NarrativeEntry) obj;

			switch (index)
			{
			case 0:
				res = ne.getDTGString();
				break;
			case 1:
				res = ne.getTrackName();
				break;
			case 2:
				res = ne.getType();
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

}
