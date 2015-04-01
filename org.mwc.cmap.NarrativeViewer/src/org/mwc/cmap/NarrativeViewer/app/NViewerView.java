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
package org.mwc.cmap.NarrativeViewer.app;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.NarrativeViewer.NarrativeViewer;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.gridharness.data.FormatDateTime;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Layers.DataListener;
import MWC.GUI.Properties.DateFormatPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.IRollingNarrativeProvider.INarrativeListener;
import de.kupzog.ktable.KTableCellDoubleClickAdapter;

public class NViewerView extends ViewPart implements PropertyChangeListener,
		ISelectionProvider
{
	public static final String VIEW_ID = "com.borlander.ianmayo.nviewer.app.view";

	NarrativeViewer myViewer;

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;

	/**
	 * help out with listening for selection changes
	 * 
	 */
	ISelectionChangedListener _selectionChangeListener;

	IRollingNarrativeProvider _myRollingNarrative;

	protected INarrativeListener _myRollingNarrListener;

	/**
	 * whether to clip text to the visible size
	 * 
	 */
	Action _clipText;

	private static SimpleDateFormat _myFormat;
	private static String _myFormatString;

	/**
	 * whether to follow the controllable time
	 * 
	 */
	private Action _followTime;

	/**
	 * whether to control the controllable time
	 * 
	 */
	private Action _controlTime;

	protected TimeProvider _myTemporalDataset;

	protected PropertyChangeListener _temporalListener;

	protected ControllableTime _controllableTime;

	/**
	 * the current editor (we store this so we can create bookmarks
	 * 
	 */
	private IEditorPart _currentEditor;

	/**
	 * the people listening to us
	 */
	Vector<ISelectionChangedListener> _selectionListeners;

	private Action _setAsBookmarkAction;

	protected Layers _myLayers;

	/**
	 * we need to listen out for layer modifications
	 * 
	 */
	protected final DataListener _layerListener;

	public NViewerView()
	{
		_layerListener = new DataListener()
		{

			@Override
			public void dataModified(final Layers theData, final Layer changedLayer)
			{
				if (changedLayer == _myRollingNarrative)
					setInput(_myRollingNarrative);
			}

			@Override
			public void dataExtended(final Layers theData)
			{

			}

			@Override
			public void dataReformatted(final Layers theData, final Layer changedLayer)
			{
				if (changedLayer == _myRollingNarrative)
					setInput(_myRollingNarrative);
				refreshColor(changedLayer);
			}

		};
	}

	private boolean internalEquals(Color color1, Color color2) {
		if (color1 == null && color2 == null) {
			return true;
		}
		if (color1 != null) {
			return color1.equals(color2);
		}
		return color2.equals(color1);
	}
	
	public void createPartControl(final Composite parent)
	{

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		parent.setLayout(new GridLayout(1, false));
		final Composite rootPanel = new Composite(parent, SWT.BORDER);
		rootPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final StackLayout rootPanelLayout = new StackLayout();
		rootPanel.setLayout(rootPanelLayout);

		myViewer = new NarrativeViewer(rootPanel, Activator.getInstance()
				.getPreferenceStore());
		rootPanelLayout.topControl = myViewer;
		
		getSite().setSelectionProvider(this);

		// sort out the initial time format
		final String startFormat = DateFormatPropertyEditor.getTagList()[3];
		myViewer.setTimeFormatter(new TimeFormatter()
		{
			public String format(final HiResDate time)
			{
				final String res = toStringHiRes(time, startFormat);
				return res;
			}
		});

		/**
		 * sort out the view menu & toolbar
		 * 
		 */
		populateMenu();

		/**
		 * and start listening out for new panels to open
		 * 
		 */
		setupPartListeners();

		myViewer.addCellDoubleClickListener(new KTableCellDoubleClickAdapter()
		{
			public void cellDoubleClicked(final int col, final int row, final int statemask)
			{
				final NarrativeEntry theEntry = myViewer.getModel()
						.getEntryAt(col, row);
				fireNewSeletion(theEntry);
			}
		});

		_selectionChangeListener = new ISelectionChangedListener()
		{

			public void selectionChanged(final SelectionChangedEvent event)
			{
				// right, see what it is
				final ISelection sel = event.getSelection();
				if (sel instanceof StructuredSelection)
				{
					final StructuredSelection ss = (StructuredSelection) sel;
					final Object datum = ss.getFirstElement();
					if (datum instanceof EditableWrapper)
					{
						final EditableWrapper pw = (EditableWrapper) datum;

						// now see if it's a narrative entry
						if (pw.getEditable() instanceof NarrativeEntry)
						{
							final NarrativeEntry entry = (NarrativeEntry) pw.getEditable();
							timeUpdated(entry.getDTG());
						}
					}
				}
			}
		};

	}

	/**
	 * send this new time to the time controller
	 * 
	 * @param newEntry
	 */
	protected void fireNewSeletion(final NarrativeEntry newEntry)
	{
		// first update the time
		if (_controlTime.isChecked())
			if (_controllableTime != null)
				_controllableTime.setTime(this, newEntry.getDTG(), true);

		// now update the selection
		final EditableWrapper wrappedEntry = new EditableWrapper(newEntry);
		final StructuredSelection structuredItem = new StructuredSelection(wrappedEntry);
		setSelection(structuredItem);
	}

	private void populateMenu()
	{
		// clear the list
		final IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
		final IToolBarManager toolManager = getViewSite().getActionBars()
				.getToolBarManager();

		// the line below contributes the predefined viewer actions onto the
		// view action bar
		myViewer.getViewerActions().fillActionBars(getViewSite().getActionBars());

		// and another separator
		menuManager.add(new Separator());

		// add some more actions
		_clipText = new Action("Wrap entry text", Action.AS_CHECK_BOX)
		{
			public void run()
			{
				super.run();
				myViewer.setWrappingEntries(!_clipText.isChecked());
			}
		};
		_clipText.setImageDescriptor(org.mwc.cmap.core.CorePlugin
				.getImageDescriptor("icons/16/wrap.png"));
		_clipText.setToolTipText("Whether to clip to visible space");
		_clipText.setChecked(true);

		menuManager.add(_clipText);
		toolManager.add(_clipText);

		_followTime = new Action("Follow current time", Action.AS_CHECK_BOX)
		{
		};
		_followTime.setImageDescriptor(org.mwc.cmap.core.CorePlugin
				.getImageDescriptor("icons/16/follow_time.png"));
		_followTime.setToolTipText("Whether to listen to the time controller");
		_followTime.setChecked(true);

		menuManager.add(_followTime);
		toolManager.add(_followTime);

		_controlTime = new Action("Control current time", Action.AS_CHECK_BOX)
		{
		};
		_controlTime.setImageDescriptor(org.mwc.cmap.core.CorePlugin
				.getImageDescriptor("icons/16/control_time.png"));
		_controlTime.setToolTipText("Whether to control the current time");
		_controlTime.setChecked(true);
		menuManager.add(_controlTime);
		toolManager.add(_controlTime);

		// now the add-bookmark item
		_setAsBookmarkAction = new Action("Add DTG as bookmark",
				Action.AS_PUSH_BUTTON)
		{
			public void runWithEvent(final Event event)
			{
				addMarker();
			}
		};
		_setAsBookmarkAction.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/16/add_bookmark.png"));
		_setAsBookmarkAction
				.setToolTipText("Add this DTG to the list of bookmarks");
		menuManager.add(_setAsBookmarkAction);

		// and the DTG formatter
		addDateFormats(menuManager);

		menuManager.add(new Separator());
		menuManager.add(CorePlugin.createOpenHelpAction(
				"org.mwc.debrief.help.Narrative", null, this));

	}

	/**
	 * @param menuManager
	 */
	private void addDateFormats(final IMenuManager menuManager)
	{
		// ok, second menu for the DTG formats
		final MenuManager formatMenu = new MenuManager("DTG Format");

		// and store it
		menuManager.add(formatMenu);

		// and now the date formats
		final String[] formats = DateFormatPropertyEditor.getTagList();
		for (int i = 0; i < formats.length; i++)
		{
			final String thisFormat = formats[i];

			// the properties manager is expecting the integer index of the new
			// format, not the string value.
			// so store it as an integer index
			final Integer thisIndex = new Integer(i);

			// and create a new action to represent the change
			final Action newFormat = new Action(thisFormat, Action.AS_RADIO_BUTTON)
			{
				public void run()
				{
					super.run();
					final String theFormat = DateFormatPropertyEditor.getTagList()[thisIndex];

					myViewer.setTimeFormatter(new TimeFormatter()
					{
						public String format(final HiResDate time)
						{
							final String res = toStringHiRes(time, theFormat);
							return res;
						}
					});
				}

			};
			formatMenu.add(newFormat);
		}
	}

	public void setFocus()
	{
		myViewer.setFocus();
	}

	/**
	 * flag for if we're currently in update
	 * 
	 */
	private static boolean _amUpdating = false;

	protected void entryUpdated(final NarrativeEntry entry)
	{
		if (_amUpdating)
		{
			// don't worry, we'll be finished soon
			System.err.println("already doing update");
		}
		else
		{
			// ok, remember that we're updating
			_amUpdating = true;

			// get on with the update
			try
			{
				Display.getDefault().asyncExec(new Runnable()
				{

					public void run()
					{
						// ok, tell the model to move to the relevant item
						myViewer.setEntry(entry);
					}
				});
			}
			finally
			{
				// clear the updating lock
				_amUpdating = false;
			}

		}
	}

	protected void setInput(final IRollingNarrativeProvider newNarr)
	{

		if (newNarr != _myRollingNarrative)
			if (_myRollingNarrative != null)
			{
				
				// clear what's displayed
				myViewer.setInput(null);
				
				// stop listening to old narrative
				_myRollingNarrative.removeNarrativeListener(
						IRollingNarrativeProvider.ALL_CATS, _myRollingNarrListener);
			}

		// check it has some data
		final NarrativeEntry[] entries = newNarr.getNarrativeHistory(new String[]
		{});

		_myRollingNarrative = newNarr;
		if (entries.length > 0)
			myViewer.setInput(_myRollingNarrative);
		else
			myViewer.setInput(null);

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
							// ok, sort it - get the view to refresh itself
							setInput(_myRollingNarrative);
						}
					});
				}

				public void entryRemoved(final NarrativeEntry entry)
				{
					// update our list...
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							// ok, sort it - get the view to refresh itself
							setInput(_myRollingNarrative);
						}
					});
				}
			};
		}
		// and start listening to it..
		_myRollingNarrative.addNarrativeListener(
				IRollingNarrativeProvider.ALL_CATS, _myRollingNarrListener);
	}

	/**
	 * 
	 */
	private void setupPartListeners()
	{

		final NViewerView me = this;

		_myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
						if (newNarr != _myRollingNarrative)
						{
							setInput(newNarr);
						}
					}
				});

		// unusually, we are also going to track the open event for narrative
		// data so that we can start off with some data
		_myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
				PartMonitor.OPENED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
						if (newNarr != _myRollingNarrative)
						{
							setInput(newNarr);
						}
					}

				});

		_myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
				PartMonitor.CLOSED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
						if (newNarr == _myRollingNarrative)
						{
							// stop listening to old narrative
							_myRollingNarrative.removeNarrativeListener(
									IRollingNarrativeProvider.ALL_CATS, _myRollingNarrListener);
							myViewer.setInput(null);
							_myRollingNarrative = null;
						}
					}
				});

		// //////////////////////////////////////////
		// and the layers - to hear about refresh
		// //////////////////////////////////////////

		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final Layers layer = (Layers) part;
						if (layer != _myLayers)
						{
							// ditch to old layers
							ditchOldLayers();

							_myLayers = layer;
							refreshColors();
							_myLayers.addDataModifiedListener(_layerListener);
							_myLayers.addDataReformattedListener(_layerListener);
						}
					}

				});

		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						final Layers layer = (Layers) part;
						if (layer == _myLayers)
						{
							// ditch to old layers
							ditchOldLayers();
						}
					}
				});

		// ///////////////////////////////////////////////
		// now for time provider support
		// ///////////////////////////////////////////////
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{

					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
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
									public void propertyChange(final PropertyChangeEvent event)
									{
										// ok, use the new time
										final HiResDate newDTG = (HiResDate) event.getNewValue();
										timeUpdated(newDTG);
									}
								};
							}
							_myTemporalDataset.addListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);

							// and is it an editor we want to remember?
							// hmm, do we want to store this part?
							if (parentPart instanceof IEditorPart)
							{
								_currentEditor = (IEditorPart) parentPart;
							}
						}
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part == _myTemporalDataset)
						{
							_myTemporalDataset.removeListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
						}
					}
				});
		_myPartMonitor.addPartListener(ControllableTime.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// implementation here.
						_controllableTime = (ControllableTime) part;
					}
				});
		_myPartMonitor.addPartListener(ControllableTime.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// no, don't bother clearing the controllable time when
						// the plot is
						// de-activated,
						// - since with the highlight on the narrative, we want
						// to be able
						// to control the time still.
						// _controllableTime = null;
					}
				});

		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != me)
						{
							final ISelectionProvider iS = (ISelectionProvider) part;
							iS.addSelectionChangedListener(_selectionChangeListener);
						}
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// aah, just check it's not is
						if (part != me)
						{
							final ISelectionProvider iS = (ISelectionProvider) part;
							iS.removeSelectionChangedListener(_selectionChangeListener);
						}
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}

	protected void ditchOldLayers()
	{
		if (_myLayers != null)
		{
			_myLayers.removeDataModifiedListener(_layerListener);
			_myLayers.removeDataReformattedListener(_layerListener);
			_myLayers = null;
		}
	}

	@Override
	public void dispose()
	{

		// and stop listening for part activity
		_myPartMonitor.ditch();

		if (_controllableTime != null)
		{
			_myTemporalDataset.removeListener(_temporalListener,
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);

			_myTemporalDataset = null;
			_controllableTime = null;
		}

		ditchOldLayers();
		// let the parent do it's bit
		super.dispose();

	}

	public synchronized static String toStringHiRes(final HiResDate time, final String pattern)
			throws IllegalArgumentException
	{
		// so, have a look at the data
		long micros = time.getMicros();
		// long wholeSeconds = micros / 1000000;

		final StringBuffer res = new StringBuffer();

		final java.util.Date theTime = new java.util.Date(micros / 1000);

		// do we already know about a date format?
		if (_myFormatString != null)
		{
			// right, see if it's what we're after
			if (_myFormatString != pattern)
			{
				// nope, it's not what we're after. ditch gash
				_myFormatString = null;
				_myFormat = null;
			}
		}

		// so, we either don't have a format yet, or we did have, and now we
		// want to
		// forget it...
		if (_myFormat == null)
		{
			_myFormatString = pattern;
			_myFormat = new SimpleDateFormat(pattern);
			_myFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		}

		res.append(_myFormat.format(theTime));

		final DecimalFormat microsFormat = new DecimalFormat("000000");
		final DecimalFormat millisFormat = new DecimalFormat("000");

		// do we have micros?
		if (micros % 1000 > 0)
		{
			// yes
			res.append(".");
			res.append(microsFormat.format(micros % 1000000));
		}
		else
		{
			// do we have millis?
			if (micros % 1000000 > 0)
			{
				// yes, convert the value to millis

				final long millis = micros = (micros % 1000000) / 1000;

				res.append(".");
				res.append(millisFormat.format(millis));
			}
			else
			{
				// just use the normal output
			}
		}

		return res.toString();
	}

	/**
	 * the user has selected a new time
	 * 
	 */
	public void propertyChange(final PropertyChangeEvent evt)
	{
		// are we syncing with time?
		if (_followTime.isChecked())
		{

		}
	}

	public void addSelectionChangedListener(final ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector<ISelectionChangedListener>(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	public ISelection getSelection()
	{
		return null;
	}

	public void removeSelectionChangedListener(final ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(final ISelection selection)
	{
		// tell everybody about us
		for (final Iterator<ISelectionChangedListener> iterator = _selectionListeners
				.iterator(); iterator.hasNext();)
		{
			final ISelectionChangedListener type = iterator.next();
			final SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
			type.selectionChanged(event);
		}
	}

	protected void timeUpdated(final HiResDate dtg)
	{
		if (_followTime.isChecked())
		{
			if (_amUpdating)
			{
				// don't worry, we'll be finished soon
				System.err.println("already doing update");
			}
			else
			{
				// ok, remember that we're updating
				_amUpdating = true;

				// get on with the update
				try
				{
					Display.getDefault().asyncExec(new Runnable()
					{

						public void run()
						{
							// ok, tell the model to move to the relevant item
							myViewer.setDTG(dtg);
						}
					});
				}
				finally
				{
					// clear the updating lock
					_amUpdating = false;
				}
			}
		}
	}

	protected void addMarker()
	{
		try
		{
			// right, do we have an editor with a file?
			final IEditorInput input = _currentEditor.getEditorInput();
			if (input instanceof IFileEditorInput)
			{
				// aaah, and is there a file present?
				final IFileEditorInput ife = (IFileEditorInput) input;
				final IResource file = ife.getFile();

				// check we have a selection
				final int[] rows = myViewer.getRowSelection();
				if (rows.length == 1)
				{
					final NarrativeEntry entry = myViewer.getModel().getEntryAt(0, rows[0]);
					final long tNow = entry.getDTG().getMicros();
					final String currentText = FormatDateTime.toString(tNow / 1000);
					if (file != null)
					{
						// yup, get the description
						final InputDialog inputD = new InputDialog(getViewSite().getShell(),
								"Add bookmark at this DTG",
								"Enter description of this bookmark", currentText, null);
						inputD.open();

						final String content = inputD.getValue();
						if (content != null)
						{
							final IMarker marker = file.createMarker(IMarker.BOOKMARK);
							final Map<String, Object> attributes = new HashMap<String, Object>(4);
							attributes.put(IMarker.MESSAGE, content);
							attributes.put(IMarker.LOCATION, currentText);
							attributes.put(IMarker.LINE_NUMBER, "" + tNow);
							attributes.put(IMarker.USER_EDITABLE, Boolean.FALSE);
							marker.setAttributes(attributes);
						}
					}
				}

			}
		}
		catch (final CoreException e)
		{
			e.printStackTrace();
		}

	}

	private void refreshColors() {
		if (_myLayers == null) {
			return;
		}
		Enumeration<Editable> elements = _myLayers.elements();
		while (elements.hasMoreElements()) {
			Editable element = elements.nextElement();
			if (element instanceof Layer) {
				refreshColor((Layer) element);
			}
		}
		
	}
	
	private void refreshColor(final Layer layer) {
		if (layer instanceof TrackWrapper) {
			TrackWrapper track = (TrackWrapper) layer;
			String name = track.getName();
			Color color = track.getColor();
			boolean refresh = false;
			NarrativeEntry[] entries = _myRollingNarrative.getNarrativeHistory(new String[] {});
			for (NarrativeEntry entry : entries) {
				if (entry.getTrackName() != null
						&& entry.getTrackName().equals(name)) {
					if (!internalEquals(color, entry.getColor())) {
						if (color == null) {
							entry.setColor(Color.white);
						} else {
							entry.setColor(color);
						}
						refresh = true;
					}
				}
			}
			if (refresh) {
				myViewer.refresh();
			}
		}
	}

}
