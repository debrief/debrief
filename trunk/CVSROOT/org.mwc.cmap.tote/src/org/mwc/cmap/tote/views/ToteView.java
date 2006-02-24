package org.mwc.cmap.tote.views;

import java.beans.*;
import java.util.Vector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.DataTypes.TrackData.*;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;
import org.mwc.cmap.core.property_support.ColorHelper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.tote.calculations.CalculationLoaderManager;

import Debrief.Tools.Tote.*;
import Debrief.Wrappers.FixWrapper;
import MWC.GenericData.HiResDate;

/**
 * View which provides a track tote. The track tote is a table of values who are
 * calculated using the current status of one or more vessel tracks
 * <p>
 */

public class ToteView extends ViewPart
{

	// private Action _followTimeToggle;

	private Action _removeTrackAction;

	// Extension point tag and attributes in plugin.xml
	private static final String EXTENSION_POINT_ID = "ToteCalculation";

	private static final String EXTENSION_TAG = "calculation";

	private static final String EXTENSION_TAG_LABEL_ATTRIB = "name";

	private static final String EXTENSION_TAG_ICON_ATTRIB = "icon";

	// Plug-in ID from <plugin> tag in plugin.xml
	private static final String PLUGIN_ID = "org.mwc.cmap.tote";

	/**
	 * the table showing the calcs
	 */
	private TableViewer _tableViewer;

	/**
	 * the table content provider (containing both the calculations and the
	 * tracks)
	 */
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
	TrackManager _trackData = null;

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

	private ToteLabelProvider _labelProvider;

	/**
	 * The constructor.
	 */
	public ToteView()
	{
		_myCalculations = new Vector(0, 1);
	}

	/**
	 * This is a callback that will allow us to create the _tableViewer and
	 * initialize it.
	 */
	public void createPartControl(Composite parent)
	{
		// _tempStatus = new Label(parent, SWT.NONE);
		// _tempStatus.setText("pending");
		//
		_tableViewer = new TableViewer(createTableWithColumns(parent));
		_content = new ToteContentProvider();
		_tableViewer.setContentProvider(_content);
		_labelProvider = new ToteLabelProvider(parent.getFont());
		_tableViewer.setLabelProvider(_labelProvider);

		_tableViewer.setInput(this);
		// _tableViewer.setSorter(new NameSorter());

		// Create Action instances
		createViewActions();

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		// try to add ourselves to listen out for page changes
		// getSite().getWorkbenchWindow().getPartService().addPartListener(this);

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						TrackManager provider = (TrackManager) part;

						// is this different to our current one?
						if (provider != _trackData)
							storeDetails(provider, parentPart);
					}
				});

		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						if (part == _trackData)
						{
							_trackData = null;
							redoTableAfterTrackChanges();
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

							// ok, stop listening to the old one
							if (_myTemporalDataset != null)
								_myTemporalDataset.removeListener(_temporalListener,
										TimeProvider.TIME_CHANGED_PROPERTY_NAME);

							// store the new one
							_myTemporalDataset = (TimeProvider) part;

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

							// artificially fire time updated event
							timeUpdated(_myTemporalDataset.getTime());
						}
					}
				});

		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						if (_myTemporalDataset != null)
						{
							// ok, stop listening to this object (just in case we were,
							// anyway).
							_myTemporalDataset.removeListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);

							// was it our one?
							if (_myTemporalDataset == part)
							{
								_myTemporalDataset = null;
							}
						}
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());

	}

	private void updateTableLayout()
	{
		// check we have some data
		if (_trackData == null)
		{
			// aah = no track data. better clear the table
			_tableViewer.getTable().removeAll();
			return;
		}

		Table tbl = _tableViewer.getTable();

		// ok, remove all of the columns
		TableColumn[] cols = tbl.getColumns();
		for (int i = 0; i < cols.length; i++)
		{
			TableColumn column = cols[i];
			column.dispose();
		}

		TableLayout layout = new TableLayout();
		tbl.setLayout(layout);

		// first put in the labels
		layout.addColumnData(new ColumnWeightData(5, true));
		TableColumn tc0 = new TableColumn(tbl, SWT.NONE);
		tc0.setText("Calculation");

		// first sort out the primary track column
		WatchableList priTrack = _trackData.getPrimaryTrack();
		// if (priTrack != null)
		// {

		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn pri = new TableColumn(tbl, SWT.NONE);

		if (priTrack != null)
			pri.setText(priTrack.getName());
		else
			pri.setText("n/a");

		// and now the secondary track columns
		WatchableList[] secTracks = _trackData.getSecondaryTracks();

		if (secTracks != null)
		{
			for (int i = 0; i < secTracks.length; i++)
			{
				WatchableList secTrack = secTracks[i];
				layout.addColumnData(new ColumnWeightData(10, true));
				TableColumn thisSec = new TableColumn(tbl, SWT.NONE);
				thisSec.setText(secTrack.getName());
			}
		}

		// and the units column
		layout.addColumnData(new ColumnWeightData(5, true));
		TableColumn thisSec = new TableColumn(tbl, SWT.NONE);
		thisSec.setText("Units");
		// }
	}

	/**
	 * @param parent
	 *          what we have to fit into
	 */
	private static Table createTableWithColumns(Composite parent)
	{
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);

		table.setLinesVisible(true);

		TableLayout layout = new TableLayout();
		table.setLayout(layout);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		layout.addColumnData(new ColumnWeightData(5, 40, true));
		TableColumn tc0 = new TableColumn(table, SWT.NONE);
		tc0.setText("Calculation");
		tc0.setAlignment(SWT.LEFT);
		tc0.setResizable(true);

		return table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite,
	 *      org.eclipse.ui.IMemento)
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

		// -------------------------------------------------------
		// Toggle filter action
		_removeTrackAction = new Action("Remove this track", Action.AS_PUSH_BUTTON)
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
			 */
			public void runWithEvent(Event event)
			{
				// cool. sorted.
				int index = findSelectedColumn(event.x, event.y, _tableViewer.getTable());
				if (index != -1)
				{
					System.out.println("removing col number:" + index);
				}
			}
		};
		_removeTrackAction.setToolTipText("Remove this track from the tote");
		_removeTrackAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
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
		Table theTable = _tableViewer.getTable();
		theTable.addMouseListener(new MouseAdapter()
		{
			public void mouseUp(MouseEvent e)
			{
				// so, right-click>
				if (e.button == 3)
				{
					// cool. sorted.
					int index = findSelectedColumn(e.x, e.y, _tableViewer.getTable());
					if (index != -1)
					{
						MenuManager mmgr = new MenuManager();
						fillContextMenu(mmgr, index);
						Menu thisM = mmgr.createContextMenu(_tableViewer.getTable());
						thisM.setVisible(true);
					}

				}
			}

		});
		// MenuManager menuMgr = new MenuManager("#PopupMenu");
		// menuMgr.setRemoveAllWhenShown(true);
		// menuMgr.addMenuListener(new IMenuListener()
		// {
		// public void menuAboutToShow(IMenuManager manager)
		// {
		// ToteView.this.fillContextMenu(manager);
		// }
		// });
		// menuMgr.addMenuListener(new IMenuListener(){
		//
		// public void menuAboutToShow(IMenuManager manager)
		// {
		// TableItem[] ti = _tableViewer.getTable().getSelection();
		// System.out.println("ti is:" + ti);
		// }});
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

	private void fillContextMenu(IMenuManager manager, final int index)
	{
		// -------------------------------------------------------
		// Toggle filter action
		_removeTrackAction = new Action("Remove this track", Action.AS_PUSH_BUTTON)
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
			 */
			public void runWithEvent(Event event)
			{
				// ok, is this the primary?
				if (index == 1)
				{
					// yes, go for it
					_trackData.setPrimary(null);
				}
				else
				{
					// ok, inform the removal of the secondary
					WatchableList thisSec = _trackData.getSecondaryTracks()[index - 2];
					_trackData.removeSecondary(thisSec);
				}
			}
		};
		_removeTrackAction.setToolTipText("Remove this track from the tote");
		_removeTrackAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(_removeTrackAction);
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		// manager.add(action1);
		// manager.add(action2);
		// manager.add(_followTimeToggle);
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
	private void storeDetails(TrackManager part, IWorkbenchPart parentPart)
	{
		// hmm - are we already looking at this one?
		if (part != _trackData)
		{

			// ok, store it
			_trackData = part;

			_tableViewer.setInput(this);

			// ok - now update the content of our table
			redoTableAfterTrackChanges();

			// lastly listen out for any future changes
			part.addTrackDataListener(new TrackDataListener()
			{
				public void tracksUpdated(WatchableList primary, WatchableList[] secondaries)
				{
					// ok - now update the content of our table
					redoTableAfterTrackChanges();
				}
			});
		}
	}

	/**
	 * 
	 */
	private void redoTableAfterTrackChanges()
	{
		// suspend updates
		_tableViewer.getTable().setRedraw(false);

		// and update the table column layout
		updateTableLayout();

		// and fire the update
		_tableViewer.getTable().layout(true);
		
		Color greyCol = ColorHelper.getColor(new java.awt.Color(225, 225, 220));		
		_tableViewer.getTable().setBackground(greyCol);

		// hmm, check if we have any track data
		if (_trackData == null)
		{
			// don't bother with any further processing.
			return;
		}

		// lastly color-code the columns
		TableItem[] items = _tableViewer.getTable().getItems();
		Color thisCol = null;

		WatchableList[] secs = _trackData.getSecondaryTracks();
		if (secs != null)
		{
			for (int i = 0; i < secs.length; i++)
			{
				WatchableList thisSec = secs[i];
				thisCol = ColorHelper.getColor(thisSec.getColor());
				for (int j = 0; j < items.length; j++)
				{
					TableItem thisRow = items[j];
					thisRow.setForeground(2 + i, thisCol);
					Color whiteCol = ColorHelper.getColor(new java.awt.Color(255, 255, 255));
					thisRow.setBackground(2 + i, whiteCol);
				}
			}
		}

		WatchableList pri = _trackData.getPrimaryTrack();
		if (pri != null)
		{
			thisCol = ColorHelper.getColor(pri.getColor());
			for (int j = 0; j < items.length; j++)
			{
				TableItem thisRow = items[j];
				thisRow.setForeground(1, thisCol);
				 Color lightCol = ColorHelper.getColor(new java.awt.Color(240, 240,
				 245));
				 thisRow.setBackground(1, lightCol);
			}
		}

		// lastly, fire a time-update to fill in the calcs
		if (_myTemporalDataset != null)
		{
			timeUpdated(_myTemporalDataset.getTime());
		}

		// resume updates
		_tableViewer.getTable().setRedraw(true);
	}

	// //////////////////////////////
	// temporal data management
	// //////////////////////////////

	/**
	 * the data we are looking at has updated. If we're set to follow that time,
	 * update ourselves
	 */
	private void timeUpdated(final HiResDate newDTG)
	{
		if (!_tableViewer.getTable().isDisposed())
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					// double-check that we haven't lost the table.
					if (!_tableViewer.getTable().isDisposed())
					{
						_labelProvider.setDTG(newDTG);
						_tableViewer.refresh(true);
					}
				}
			});
		}
		else
			System.out.println("not updating. table is disposed");
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
		_loader = new CalculationLoaderManager(EXTENSION_POINT_ID, EXTENSION_TAG, PLUGIN_ID)
		{

			public toteCalculation createInstance(IConfigurationElement configElement,
					String label)
			{
				// get the attributes
				label = configElement.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);
				String icon = configElement.getAttribute(EXTENSION_TAG_ICON_ATTRIB);

				// create the instance
				toteCalculation res = null;

				// create the instance
				res = new CalculationLoaderManager.DeferredCalculation(configElement, label, icon);

				// and return it.
				return res;
			}

		};
	}

	public class ToteContentProvider implements IStructuredContentProvider
	{

		public ToteContentProvider()
		{
		}

		public Object[] getElements(Object inputElement)
		{
			return _myCalculations.toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	public class ToteLabelProvider implements ITableLabelProvider, ITableFontProvider
	{
		/**
		 * the DTG we're updating for.
		 */
		private HiResDate _theDTG;

		/**
		 * remember the fonts we're going to use Start off with the font for
		 * secondary tracks
		 */
		private final Font _secondaryFont;

		/**
		 * and now the font for the primary track
		 */
		private final Font _primaryFont;

		/**
		 * and the font for interpolated data-sets
		 */
		private final Font _interpolatedSecondaryFont;

		/**
		 * and the font for interpolated data-sets
		 */
		private final Font _interpolatedPrimaryFont;

		/**
		 * constructor - base the primary /secondary fonts on the supplied font
		 * 
		 * @param coreFont
		 *          the font to base ourselves upon
		 */
		public ToteLabelProvider(Font coreFont)
		{
			// ok, just take a copy for the sec font
			_secondaryFont = coreFont;

			// but now generate a changed font for the primary
			FontData[] fontData = _secondaryFont.getFontData();
			FontData theOnly = fontData[0];
			_primaryFont = new Font(Display.getCurrent(), theOnly.getName(), theOnly
					.getHeight(), theOnly.getStyle() | SWT.BOLD);
			_interpolatedSecondaryFont = new Font(Display.getCurrent(), theOnly.getName(),
					theOnly.getHeight(), theOnly.getStyle() | SWT.ITALIC);
			_interpolatedPrimaryFont = new Font(Display.getCurrent(), theOnly.getName(),
					theOnly.getHeight(), theOnly.getStyle() | SWT.ITALIC | SWT.BOLD);

		}

		/**
		 * store the new DTG (ready for our updates)
		 * 
		 * @param theDTG
		 */
		public void setDTG(HiResDate theDTG)
		{
			_theDTG = theDTG;
		}

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public Font getFont(Object element, int columnIndex)
		{
			final Font res;

			boolean isPrimary = false;
			boolean isInterpolated = false;

			// is this already sorted?
			if (_theDTG != null)
			{
				if (_trackData != null)
				{
					WatchableList _thePrimary = _trackData.getPrimaryTrack();
					WatchableList[] secLists = _trackData.getSecondaryTracks();

					// get the data for the right col
					if (columnIndex == 0)
					{
						// ignore, we just use the primary font anyway
						isPrimary = true;
					}
					else if (columnIndex == 1)
					{
						// check that we've got a primary
						if (_thePrimary != null)
						{
							isPrimary = true;
							// so, we the calculations have been added to the tote list
							// in order going across the page

							// get the primary ready,
							Watchable[] list = _thePrimary.getNearestTo(_theDTG);
							Watchable pw = null;
							if (list.length > 0)
								pw = list[0];
							if (pw instanceof FixWrapper.InterpolatedFixWrapper)
								isInterpolated = true;
						}
					}
					else
					{
						if (secLists != null)
						{
							if (columnIndex - 2 < secLists.length)
							{
								// prepare the list of secondary watchables
								WatchableList wList = secLists[columnIndex - 2];
								Watchable[] list = wList.getNearestTo(_theDTG);

								Watchable nearest = null;
								if (list.length > 0)
								{
									nearest = list[0];
									if (nearest instanceof FixWrapper.InterpolatedFixWrapper)
										isInterpolated = true;
								}
							}
						}
					}
				}
			}

			if (isPrimary)
			{
				if (isInterpolated)
					res = _interpolatedPrimaryFont;
				else
					res = _primaryFont;
			}
			else
			{
				if (isInterpolated)
					res = _interpolatedSecondaryFont;
				else
					res = _secondaryFont;
			}

			return res;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			String res = null;
			toteCalculation tc = (toteCalculation) element;

			// right, is this the title column?
			if (columnIndex == 0)
			{
				res = tc.getTitle();
			}
			else
			{
				// hmm, could it be the units column?
				int numCols = _tableViewer.getTable().getColumnCount();
				if (columnIndex == numCols - 1)
				{
					res = tc.getUnits();
				}
			}

			// is this already sorted?
			if ((res == null) && (_theDTG != null))
			{
				if (_trackData != null)
				{
					WatchableList _thePrimary = _trackData.getPrimaryTrack();
					WatchableList[] secLists = _trackData.getSecondaryTracks();

					// check that we've got a primary
					if (_thePrimary != null)
					{

						// so, we the calculations have been added to the tote list
						// in order going across the page

						// get the primary ready,
						Watchable[] list = _thePrimary.getNearestTo(_theDTG);
						Watchable pw = null;
						if (list.length > 0)
							pw = list[0];

						// are we only looking at the primary?
						if (columnIndex == 1)
						{
							res = tc.update(null, pw, _theDTG);
						}
						else
						{
							if (secLists != null)
							{
								if (columnIndex - 2 < secLists.length)
								{
									// prepare the list of secondary watchables
									WatchableList wList = secLists[columnIndex - 2];
									list = wList.getNearestTo(_theDTG);

									Watchable nearest = null;
									if (list.length > 0)
										nearest = list[0];
									res = tc.update(pw, nearest, _theDTG);
								}
							}
						}
					}
				}
			}
			return res;
		}

		public void addListener(ILabelProviderListener listener)
		{
		}

		public void dispose()
		{
		}

		public boolean isLabelProperty(Object element, String property)
		{
			return true;
		}

		public void removeListener(ILabelProviderListener listener)
		{
		}

	}

	private static int findSelectedColumn(int x, int y, Table table)
	{
		int index = -1;
		TableItem[] selectedCols = table.getSelection();
		if (selectedCols != null)
		{
			TableItem selection = selectedCols[0];
			TableColumn[] tc = table.getColumns();
			for (int i = 1; i < tc.length - 1; i++)
			{
				Rectangle bounds = selection.getBounds(i);
				if (bounds.contains(x, bounds.y))
				{
					index = i;
				}
			}
		}
		return index;
	}
}
