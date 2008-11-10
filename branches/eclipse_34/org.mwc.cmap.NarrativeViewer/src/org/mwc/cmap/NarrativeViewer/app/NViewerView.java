package org.mwc.cmap.NarrativeViewer.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.NarrativeViewer.NarrativeViewer;
import org.mwc.cmap.NarrativeViewer.model.TimeFormatter;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;

import MWC.GUI.Properties.DateFormatPropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.TacticalData.IRollingNarrativeProvider;
import MWC.TacticalData.NarrativeEntry;
import MWC.TacticalData.IRollingNarrativeProvider.INarrativeListener;
import de.kupzog.ktable.KTableCellDoubleClickAdapter;

public class NViewerView extends ViewPart implements PropertyChangeListener
{
	public static final String VIEW_ID = "com.borlander.ianmayo.nviewer.app.view";

	private NarrativeViewer myViewer;

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor;
	private IRollingNarrativeProvider _myRollingNarrative;

	protected INarrativeListener _myRollingNarrListener;

	/**
	 * whether to clip text to the visible size
	 * 
	 */
	private Action _clipText;

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

	public void createPartControl(Composite parent)
	{

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		parent.setLayout(new GridLayout(1, false));
		Composite rootPanel = new Composite(parent, SWT.BORDER);
		rootPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		StackLayout rootPanelLayout = new StackLayout();
		rootPanel.setLayout(rootPanelLayout);

		myViewer = new NarrativeViewer(rootPanel, Activator.getInstance()
				.getPreferenceStore());
		rootPanelLayout.topControl = myViewer;

		// sort out the initial time format
		final String startFormat = DateFormatPropertyEditor.getTagList()[3];
		myViewer.setTimeFormatter(new TimeFormatter()
		{
			public String format(HiResDate time)
			{
				String res = toStringHiRes(time, startFormat);
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
			public void cellDoubleClicked(int col, int row, int statemask)
			{
				NarrativeEntry[] entries = myViewer.getModel().getInput()
						.getNarrativeHistory(new String[]
						{});
				HiResDate newTime = entries[row - 1].getDTG();
				fireNewTime(newTime);
			}
		});

	}

	/**
	 * send this new time to the time controller
	 * 
	 * @param newTime
	 */
	protected void fireNewTime(HiResDate newTime)
	{
		if (_controlTime.isChecked())
			if (_controllableTime != null)
				_controllableTime.setTime(this, newTime, true);
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
				.getImageDescriptor("icons/wrap.gif"));
		_clipText.setToolTipText("Whether to clip to visible space");
		_clipText.setChecked(true);

		menuManager.add(_clipText);
		toolManager.add(_clipText);

		_followTime = new Action("Follow current time", Action.AS_CHECK_BOX)
		{
		};
		_followTime.setImageDescriptor(org.mwc.cmap.core.CorePlugin
				.getImageDescriptor("icons/synced.gif"));
		_followTime.setToolTipText("Whether to listen to the time controller");
		_followTime.setChecked(true);

		menuManager.add(_followTime);
		toolManager.add(_followTime);

		_controlTime = new Action("Control current time", Action.AS_CHECK_BOX)
		{
		};
		_controlTime.setImageDescriptor(org.mwc.cmap.core.CorePlugin
				.getImageDescriptor("icons/clock.png"));
		_controlTime.setToolTipText("Whether to control the current time");
		_controlTime.setChecked(true);
		menuManager.add(_controlTime);
		toolManager.add(_controlTime);

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
		MenuManager formatMenu = new MenuManager("DTG Format");

		// and store it
		menuManager.add(formatMenu);

		// and now the date formats
		String[] formats = DateFormatPropertyEditor.getTagList();
		for (int i = 0; i < formats.length; i++)
		{
			final String thisFormat = formats[i];

			// the properties manager is expecting the integer index of the new
			// format, not the string value.
			// so store it as an integer index
			final Integer thisIndex = new Integer(i);

			// and create a new action to represent the change
			Action newFormat = new Action(thisFormat, Action.AS_RADIO_BUTTON)
			{
				public void run()
				{
					super.run();
					final String theFormat = DateFormatPropertyEditor.getTagList()[thisIndex];

					myViewer.setTimeFormatter(new TimeFormatter()
					{
						public String format(HiResDate time)
						{
							String res = toStringHiRes(time, theFormat);
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

	/** flag for if we're currently in update
	 * 
	 */
	private static boolean _amUpdating = false;

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
				} finally
				{
					// clear the updating lock
					_amUpdating = false;
				}
			}
		}
	}

	protected void setInput(IRollingNarrativeProvider newNarr)
	{
		// check it has some data
		NarrativeEntry[] entries = newNarr.getNarrativeHistory(new String[]
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

				public void entryRemoved(NarrativeEntry entry)
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
		_myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
						setInput(newNarr);
					}
				});

		// unusually, we are also going to track the open event for narrative
		// data so that we can start off with some data
		_myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
				PartMonitor.OPENED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
						setInput(newNarr);
					}

				});

		_myPartMonitor.addPartListener(IRollingNarrativeProvider.class,
				PartMonitor.CLOSED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						IRollingNarrativeProvider newNarr = (IRollingNarrativeProvider) part;
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

		// ///////////////////////////////////////////////
		// now for time provider support
		// ///////////////////////////////////////////////
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// implementation here.
						_controllableTime = (ControllableTime) part;
					}
				});
		_myPartMonitor.addPartListener(ControllableTime.class,
				PartMonitor.DEACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());
	}

	@Override
	public void dispose()
	{

		// and stop listening for part activity
		_myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());

		if (_controllableTime != null)
		{
			_myTemporalDataset.removeListener(_temporalListener,
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);

			_myTemporalDataset = null;
			_controllableTime = null;
		}

		// let the parent do it's bit
		super.dispose();

	}

	private static SimpleDateFormat _myFormat;
	private static String _myFormatString;

	public static String toStringHiRes(HiResDate time, String pattern)
			throws IllegalArgumentException
	{
		// so, have a look at the data
		long micros = time.getMicros();
		// long wholeSeconds = micros / 1000000;

		StringBuffer res = new StringBuffer();

		java.util.Date theTime = new java.util.Date(micros / 1000);

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

		DecimalFormat microsFormat = new DecimalFormat("000000");
		DecimalFormat millisFormat = new DecimalFormat("000");

		// do we have micros?
		if (micros % 1000 > 0)
		{
			// yes
			res.append(".");
			res.append(microsFormat.format(micros % 1000000));
		} else
		{
			// do we have millis?
			if (micros % 1000000 > 0)
			{
				// yes, convert the value to millis

				long millis = micros = (micros % 1000000) / 1000;

				res.append(".");
				res.append(millisFormat.format(millis));
			} else
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
	public void propertyChange(PropertyChangeEvent evt)
	{
		// are we syncing with time?
		if (_followTime.isChecked())
		{

		}
	}

}
