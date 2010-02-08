package org.mwc.cmap.tote.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider.TrackDataListener;
import org.mwc.cmap.core.property_support.ColorHelper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.tote.calculations.CalculationLoaderManager;

import Debrief.Tools.Tote.toteCalculation;
import Debrief.Wrappers.FixWrapper;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

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

	private static final String SHOW_UNITS = "SHOW_UNITS";

	/**
	 * the table showing the calcs
	 */
	TableViewer _tableViewer;

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
	PropertyChangeListener _temporalListener = null;

	/**
	 * where we get our track data from
	 */
	TrackManager _trackData = null;

	/**
	 * our current set of calculations
	 */
	Vector<toteCalculation> _myCalculations = null;

	/**
	 * the temporal dataset controlling the narrative entry currently displayed
	 */
	TimeProvider _myTemporalDataset;

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

	ToteLabelProvider _labelProvider;

	/**
	 * action to put watchables on the tote
	 */
	private Action _autoGenerate;

	/**
	 * action to put tracks on the tote
	 */
	private Action _autoGenerateJustTracks;

	/**
	 * action to hide/reveal the units column
	 */
	Action _showUnits;

	/**
	 * The constructor.
	 */
	public ToteView()
	{
		_myCalculations = new Vector<toteCalculation>(0, 1);
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

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(TrackManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// was it our one?
						if (_myTemporalDataset == part)
						{
							// ok, stop listening to this object (just in case we were,
							// anyway).
							_myTemporalDataset.removeListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);

							_myTemporalDataset = null;
						}
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());

		// and declare our context sensitive help
		CorePlugin.declareContextHelp(parent, "org.mwc.debrief.help.TrackTote");

	}

	private void updateTableLayout()
	{
		// check we have some data
		// if (_trackData == null)
		// {
		// // aah = no track data. better disable the table
		// timeUpdated(null);
		// // _tableViewer.getTable().setEnabled(false);
		// // return;
		// }

		Table tbl = _tableViewer.getTable();
		tbl.setEnabled(true);

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

		if (_trackData != null)
		{
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
					if (secTrack != null)
					{
						layout.addColumnData(new ColumnWeightData(10, true));
						TableColumn thisSec = new TableColumn(tbl, SWT.NONE);
						thisSec.setText(secTrack.getName());
					}
				}
			}

		}

		if (_showUnits.isChecked())
		{
			// and the units column
			layout.addColumnData(new ColumnWeightData(5, true));
			TableColumn thisSec = new TableColumn(tbl, SWT.NONE);
			thisSec.setText("Units");
		}
	}

	/**
	 * @param parent
	 *          what we have to fit into
	 */
	private static Table createTableWithColumns(Composite parent)
	{
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);

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
	 * org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		// let the parent do its bits
		super.init(site, memento);

		_showUnits = new Action("Show calc units column.", Action.AS_CHECK_BOX)
		{
			public void run()
			{
				redoTableAfterTrackChanges();
			}
		};

		// are we showing the units column?
		if (memento != null)
		{
			String unitsVal = memento.getString(SHOW_UNITS);
			if (unitsVal != null)
			{
				_showUnits
						.setChecked(Boolean.getBoolean(memento.getString(SHOW_UNITS)));
			}
		}

		// ok - declare and load the supplemental plugins which can load datafiles
		initialiseCalcLoaders();

		toteCalculation[] calcs = _loader.findCalculations();
		for (int i = 0; i < calcs.length; i++)
		{
			toteCalculation thisCalc = calcs[i];
			_myCalculations.add(thisCalc);
		}
	}

	/**
	 * right - store ourselves into the supplied memento object
	 * 
	 * @param memento
	 */
	public void saveState(IMemento memento)
	{
		// let our parent go for it first
		super.saveState(memento);

		String unitsVal = Boolean.toString(_showUnits.isChecked());
		memento.putString(SHOW_UNITS, unitsVal);
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
			 * @see
			 * org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets
			 * .Event)
			 */
			public void runWithEvent(Event event)
			{
				// cool. sorted.
				int index = findSelectedColumn(event.x, event.y, _tableViewer
						.getTable());
				if (index != -1)
				{
					System.out.println("removing col number:" + index);
				}
			}
		};
		_removeTrackAction.setToolTipText("Remove this track from the tote");
		_removeTrackAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));

		// -------------------------------------------------------
		// put watchables on the tote
		// -------------------------------------------------------
		_autoGenerate = new Action("Auto-populate tote", Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				autoGenerate(false);
			}
		};
		// -------------------------------------------------------
		// put watchables on the tote
		// -------------------------------------------------------
		_autoGenerateJustTracks = new Action("Auto-populate tote (tracks only)",
				Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
				autoGenerate(true);
			}
		};

		// #### show units is generated in the "init" method, since it uses
		// part of the memento

	}

	/**
	 * automatically pass through the data, and automatically assign the relevant
	 * watchable items to primary, secondary, etc.
	 * 
	 * @param onlyAssignTracks
	 *          - as we scan through the layers, only put TrackWrappers onto the
	 *          tote
	 */
	protected void autoGenerate(boolean onlyAssignTracks)
	{
		if (_trackData != null)
			_trackData.autoAssign(onlyAssignTracks);
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
		// manager.add(new Separator());
		manager.add(_autoGenerate);
		manager.add(_autoGenerateJustTracks);
		manager.add(_showUnits);
		// and the help link
		manager.add(new Separator());
		manager.add(CorePlugin.createOpenHelpAction(
				"org.mwc.debrief.help.TrackTote", null, this));

	}

	void fillContextMenu(IMenuManager manager, final int index)
	{
		// -------------------------------------------------------
		// Toggle filter action
		_removeTrackAction = new Action("Remove this track", Action.AS_PUSH_BUTTON)
		{
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets
			 * .Event)
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
		_removeTrackAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));

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
	void storeDetails(TrackManager part, IWorkbenchPart parentPart)
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
				public void tracksUpdated(WatchableList primary,
						WatchableList[] secondaries)
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
	void redoTableAfterTrackChanges()
	{
		// suspend updates
		_tableViewer.getTable().setRedraw(false);

		// and update the table column layout
		updateTableLayout();

		// and fire the update
		_tableViewer.getTable().layout(true);

		Color greyCol = Display.getCurrent().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND);
		_tableViewer.getTable().setBackground(greyCol);

		// hmm, check if we have any track data
		if (_trackData == null)
		{
			// don't bother with any further processing.
			_tableViewer.getTable().setRedraw(true);
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
				if (thisSec != null)
				{
					thisCol = ColorHelper.getColor(thisSec.getColor());
					for (int j = 0; j < items.length; j++)
					{
						TableItem thisRow = items[j];
						thisRow.setForeground(2 + i, thisCol);
						Color whiteCol = ColorHelper.getColor(new java.awt.Color(255, 255,
								255));
						thisRow.setBackground(2 + i, whiteCol);
					}
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
				Color lightCol = ColorHelper
						.getColor(new java.awt.Color(240, 240, 245));
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
	void timeUpdated(final HiResDate newDTG)
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

	public class ToteLabelProvider implements ITableLabelProvider,
			ITableFontProvider
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
			_interpolatedSecondaryFont = new Font(Display.getCurrent(), theOnly
					.getName(), theOnly.getHeight(), theOnly.getStyle() | SWT.ITALIC);
			_interpolatedPrimaryFont = new Font(Display.getCurrent(), theOnly
					.getName(), theOnly.getHeight(), theOnly.getStyle() | SWT.ITALIC
					| SWT.BOLD);

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
								if (wList != null)
								{
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
				if (_showUnits.isChecked())
				{
					// hmm, could it be the units column?
					int numCols = _tableViewer.getTable().getColumnCount();
					if (columnIndex == numCols - 1)
					{
						res = tc.getUnits();
					}
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
						Watchable[] nearestPrimaries = _thePrimary.getNearestTo(_theDTG);
						Watchable primaryFix = null;
						Watchable[] nearestSecondaries = null;
						Watchable secondaryFix = null;

						// do we have any primary data?
						if (nearestPrimaries.length > 0)
							primaryFix = nearestPrimaries[0];

						// do we have any secondary data?
						if (secLists != null)
						{
							WatchableList wList = null;

							// are we doing the primary track?
							if (columnIndex == 1)
							{
								// do we have more than one secondary?
								if (secLists.length > 1)
								{
									// ignore - we don't plot against a secondary track if there's
									// more than one
								}
								else
								{
									// aah, do we have at least one target track?
									if (secLists.length > 0)
									{
										// get the data for the first secondary column
										wList = secLists[0];
									}
								}
							}
							else
							{
								// we're in a secondary track, retrieve it's data
								wList = secLists[columnIndex - 2];
							}

							if (wList != null)
							{
								nearestSecondaries = wList.getNearestTo(_theDTG);
								if (nearestSecondaries != null)
								{
									if (nearestSecondaries.length > 0)
										secondaryFix = nearestSecondaries[0];
								}

								// yup, in that case let's switch the perspective, if we have to
								if (columnIndex > 1)
								{
									// right we're working on the secondary lists, swap the
									// primary
									// and secondary around,
									// so we see the value from their perspective
									Watchable tmpItem = primaryFix;
									tmpItem = primaryFix;
									primaryFix = secondaryFix;
									secondaryFix = tmpItem;
									tmpItem = null;
								}
							}
						}

						res = tc.update(primaryFix, secondaryFix, _theDTG);

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

	int findSelectedColumn(int x, int y, Table table)
	{
		int index = -1;
		TableItem[] selectedCols = table.getSelection();
		if (selectedCols != null)
		{
			TableItem selection = selectedCols[0];
			TableColumn[] tc = table.getColumns();

			// sort out how many c
			int numCols = tc.length;
			if (_showUnits.isChecked())
				numCols--;

			for (int i = 1; i < numCols; i++)
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
