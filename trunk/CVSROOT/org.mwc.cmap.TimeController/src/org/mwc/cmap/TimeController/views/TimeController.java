package org.mwc.cmap.TimeController.views;

import java.beans.*;
import java.text.*;
import java.util.*;

import junit.framework.TestCase;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.TimeController.TimeControllerPlugin;
import org.mwc.cmap.TimeController.properties.StepperProperties;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.editors.painters.*;

import MWC.GUI.Properties.DateFormatPropertyEditor;
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

public class TimeController extends ViewPart implements ISelectionProvider
{
	private PartMonitor _myPartMonitor;

	final private PropertyChangeListener _temporalListener = new NewTimeListener();

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
	 * label showing the current time
	 */
	private Label _timeLabel;

	/**
	 * the parent object for the time controller. It is at this level that we
	 * enable/disable the controls
	 */
	private Composite _wholePanel;

	/**
	 * the people listening to us
	 */
	private Vector _selectionListeners;

	/**
	 * the thing we're currently displaying
	 */
	private ISelection _currentSelection;

	final private StepperProperties _myStepperProperties = new StepperProperties();


	/** module to look after the limits of the slider
	 * 
	 */
	SliderRangeManagement _slideManager = null;
	
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		// also sort out the slider conversion bits. We do it at the start, because
		// callbacks
		// created during initialisation may need to use/reset it
		_slideManager = new SliderRangeManagement()
		{
			public void setMinVal(int min)
			{
				_tNowSlider.setMinimum(min);
			}

			public void setMaxVal(int max)
			{
				_tNowSlider.setMaximum(max);
			}

			public void setTickSize(int small, int large, int drag)
			{
				_tNowSlider.setIncrement(small);
				_tNowSlider.setPageIncrement(large);
//				_tNowSlider.setThumb(drag);
			}

			public void setEnabled(boolean val)
			{
				_tNowSlider.setEnabled(val);
			}
		};

		// and fill in the interface
		buildInterface(parent);

		// of course though, we start off with the buttons not enabled
		_wholePanel.setEnabled(false);

		// and start listing for any part action
		setupListeners();
		
		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());

		// say that we're a selection provider
		getSite().setSelectionProvider(this);

		// also, listen out for changes in the DTG formatter
		_myStepperProperties.addPropertyChangeListener(new PropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent evt)
			{
				// right, see if the user is changing the DTG format
				if (evt.getPropertyName().equals(StepperProperties.DTG_FORMAT_ID))
				{
					// ok, refresh the DTG
					String newVal = getFormattedDate(_myTemporalDataset.getTime());
					_timeLabel.setText(newVal);
				}
			}
		});

	}

	/**
	 * the slider control - remember it because we're always changing the limits,
	 * etc
	 */
	private Scale _tNowSlider;

	/**
	 * when the user clicks on us, we set our properties as a selection. Remember
	 * the set of properties
	 */
	private StructuredSelection _propsAsSelection = null;

	
	/** ok - put in our bits
	 * 
	 * @param parent
	 */
	private void buildInterface(Composite parent)
	{
		// ok, draw our wonderful GUI.
		_wholePanel = new Composite(parent, SWT.BORDER);
		
		FillLayout onTop = new FillLayout(SWT.VERTICAL);
		_wholePanel.setLayout(onTop);

		// first create the button holder
		Composite _btnPanel = new Composite(_wholePanel, SWT.NONE);
		FillLayout btnFiller = new FillLayout(SWT.HORIZONTAL);
		btnFiller.marginHeight = 0;
		_btnPanel.setLayout(btnFiller);
		

		Button eBwd = new Button(_btnPanel, SWT.NONE);
		eBwd.addSelectionListener(new TimeButtonSelectionListener(false, null));
		eBwd.setImage(TimeControllerPlugin.getImageDescriptor("icons/VCRBegin.gif")
				.createImage());
		Button lBwd = new Button(_btnPanel, SWT.NONE);
		lBwd.setText("<<");
		lBwd.setImage(TimeControllerPlugin.getImageDescriptor("icons/VCRRewind.gif")
				.createImage());
		lBwd.addSelectionListener(new TimeButtonSelectionListener(false, new Boolean(true)));
		Button sBwd = new Button(_btnPanel, SWT.NONE);
		sBwd.setText("<");
		sBwd.setImage(TimeControllerPlugin.getImageDescriptor("icons/VCRBack.gif")
				.createImage());
		sBwd.addSelectionListener(new TimeButtonSelectionListener(false, new Boolean(false)));

		Button play = new Button(_btnPanel, SWT.NONE);
		play.setImage(TimeControllerPlugin.getImageDescriptor("icons/VCRPlay.gif")
				.createImage());

		Button sFwd = new Button(_btnPanel, SWT.NONE);
		sFwd.setImage(TimeControllerPlugin.getImageDescriptor("icons/VCRForward.gif")
				.createImage());
		sFwd.addSelectionListener(new TimeButtonSelectionListener(true, new Boolean(false)));
		Button lFwd = new Button(_btnPanel, SWT.NONE);
		lFwd.setImage(TimeControllerPlugin.getImageDescriptor("icons/VCRFastForward.gif")
				.createImage());
		lFwd.addSelectionListener(new TimeButtonSelectionListener(true, new Boolean(true)));
		Button eFwd = new Button(_btnPanel, SWT.NONE);
		eFwd.setImage(TimeControllerPlugin.getImageDescriptor("icons/VCREnd.gif")
				.createImage());
		eFwd.addSelectionListener(new TimeButtonSelectionListener(true, null));

		// and the current time label
		Composite timeContainer = new Composite(_wholePanel, SWT.NONE);
		FillLayout timeFiller = new FillLayout(SWT.HORIZONTAL);
		timeContainer.setLayout(timeFiller);
		_timeLabel = new Label(timeContainer, SWT.NONE);
		_timeLabel.setAlignment(SWT.CENTER);
		_timeLabel.setText("--------------------------");
		_timeLabel.setFont(new Font(Display.getDefault(), "OCR A Extended", 16, SWT.NONE));
		_timeLabel.setForeground(new Color(Display.getDefault(), 33, 255, 22));
		_timeLabel.setBackground(new Color(Display.getDefault(), 0, 0, 0));

		// next create the time slider holder
		_tNowSlider = new Scale(_wholePanel, SWT.NONE);
		_tNowSlider.setMinimum(0);
		_tNowSlider.setMaximum(100);
		_tNowSlider.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int index = _tNowSlider.getSelection();
				HiResDate newDTG = _slideManager.fromSliderUnits(index, _myTemporalDataset
						.getPeriod().getStartDTG());
				fireNewTime(newDTG);
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});


		_wholePanel.addListener(SWT.MouseWheel, new WheelMovedEvent());

		/*
		 * the next bit is a fudge (taken from "How to scroll a canvas" on Eclipse
		 * newsgroups
		 */
		_wholePanel.addListener(SWT.MouseDown, new Listener()
		{

			public void handleEvent(Event event)
			{
				Control focus = event.display.getFocusControl();
				while (focus != null)
				{
					focus = focus.getParent();
				}
				_wholePanel.setFocus();
			}
		});
	}

	private final class NewTimeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			// see if it's the time or the period which
			// has changed
			if (event.getPropertyName().equals(TimeProvider.TIME_CHANGED_PROPERTY_NAME))
			{
				// ok, use the new time
				HiResDate newDTG = (HiResDate) event.getNewValue();
				timeUpdated(newDTG);
			}
			else if (event.getPropertyName().equals(TimeProvider.PERIOD_CHANGED_PROPERTY_NAME))
			{
				TimePeriod newPeriod = (TimePeriod) event.getNewValue();
				_slideManager.resetRange(newPeriod.getStartDTG(), newPeriod.getEndDTG());
			}

			// also double-check if it's time to enable our interface
			checkTimeEnabled();
		}
	}

	private class TimeButtonSelectionListener implements SelectionListener
	{
		private boolean _fwd;

		private Boolean _large;

		public TimeButtonSelectionListener(boolean fwd, Boolean large)
		{
			_fwd = fwd;
			_large = large;
		}

		public void widgetSelected(SelectionEvent e)
		{
			processClick(_large, _fwd);
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}
	}

	private void processClick(Boolean large, boolean fwd)
	{
		// check that we have a current time (on initialisation some plots may not
		// contain data)
		HiResDate tNow = _myTemporalDataset.getTime();
		if (tNow != null)
		{
			// yup, time is there. work with it baby
			long micros = tNow.getMicros();

			// right, special case for when user wants to go straight to the end - in
			// which
			// case there is a zero in the scale
			if (large == null)
			{
				// right, fwd or bwd
				if (fwd)
					micros = _myTemporalDataset.getPeriod().getEndDTG().getMicros();
				else
					micros = _myTemporalDataset.getPeriod().getStartDTG().getMicros();
			}
			else
			{
				long size;

				// normal processing..
				if (large.booleanValue())
				{
					// do large step
					size = (long) _myStepperProperties.getLargeStepSize().getValueIn(
							Duration.MICROSECONDS);
				}
				else
				{
					// and the small size step
					size = (long) _myStepperProperties.getSmallStepSize().getValueIn(
							Duration.MICROSECONDS);
				}

				// right, either move forwards or backwards.
				if (fwd)
					micros += size;
				else
					micros -= size;
			}

			HiResDate newDTG = new HiResDate(0, micros);

			// find the extent of the current dataset
			TimePeriod timeP = _myTemporalDataset.getPeriod();

			// do we represent a valid time?
			if (timeP.contains(newDTG))
			{
				// yes, fire the new DTG
				fireNewTime(newDTG);
			}
		}
	}

	private void fireNewTime(HiResDate dtg)
	{
		_controllableTime.setTime(this, dtg);
	}

	private void setupListeners()
	{

		// try to add ourselves to listen out for page changes
		// getSite().getWorkbenchWindow().getPartService().addPartListener(this);

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());

		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						if (_myTemporalDataset != part)
						{
							// implementation here.
							_myTemporalDataset = (TimeProvider) part;
							_myTemporalDataset.addListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);

							// also configure for the current time
							HiResDate newDTG = _myTemporalDataset.getTime();
							timeUpdated(newDTG);

							// and initialise the current time
							TimePeriod firstDTG = _myTemporalDataset.getPeriod();
							if (firstDTG != null)
							{
								_slideManager.resetRange(firstDTG.getStartDTG(), firstDTG.getEndDTG());
							}

							checkTimeEnabled();
						}
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// are we still listening?
						if (_myTemporalDataset != null)
						{
							_myTemporalDataset.removeListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
						}
						checkTimeEnabled();
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
						checkTimeEnabled();
					}

				});
		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						_controllableTime = null;
						checkTimeEnabled();
					}
				});
		_myPartMonitor.addPartListener(LayerPainterManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// ok, insert the painter mode actions, together with our standard ones
						populateDropDownList((LayerPainterManager) part);
					}



				});
		_myPartMonitor.addPartListener(LayerPainterManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
//						_painterSelector.getCombo().setEnabled(false);
//						_myLayerPainterManager = null;
					}
				});

	}

	/**
	 * convenience method to make the panel enabled if we have a time controller
	 * and a valid time
	 */
	private void checkTimeEnabled()
	{
		boolean enable = false;

		if (_myTemporalDataset != null)
		{
			if ((_controllableTime != null) && (_myTemporalDataset.getTime() != null))
				enable = true;
		}

		final boolean finalEnabled = enable;

		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!_wholePanel.isDisposed())
				{
					_wholePanel.setEnabled(finalEnabled);
				}
			}
		});
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
		if (_myTemporalDataset != null)
		{
			_myTemporalDataset.removeListener(_temporalListener,
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		// get the editable thingy
		if (_propsAsSelection == null)
			_propsAsSelection = new StructuredSelection(_myStepperProperties);

		fireSelectionChanged(_propsAsSelection);
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
		if (newDTG != null)
		{
			if (!_timeLabel.isDisposed())
			{

				// updating the text items has to be done in the UI thread. make it so
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						// display the correct time.
						String newVal = getFormattedDate(newDTG);

						_timeLabel.setText(newVal);

						TimePeriod dataPeriod = _myTemporalDataset.getPeriod();
						if (dataPeriod != null)
						{
							int newIndex = _slideManager
									.toSliderUnits(newDTG, dataPeriod.getStartDTG());
							_tNowSlider.setSelection(newIndex);
						}
					}
				});
			}
		}
		else
		{
			System.out.println("null DTG received by time controller");
		}

	}

	private String getFormattedDate(HiResDate newDTG)
	{

		// IPreferenceStore store = TimeControllerPlugin.getDefault()
		// .getPreferenceStore();
		String dateFormat = _myStepperProperties.getDTGFormat();

		// store.getString(PreferenceConstants.P_STRING);
		String newVal = "n/a";
		try
		{
			newVal = toStringHiRes(newDTG, dateFormat);
		}
		catch (IllegalArgumentException e)
		{
			System.err.println("Invalid date format in preferences");
		}
		return newVal;
	}

	public static String toStringHiRes(HiResDate time, String pattern)
			throws IllegalArgumentException
	{
		// so, have a look at the data
		long micros = time.getMicros();
		// long wholeSeconds = micros / 1000000;

		StringBuffer res = new StringBuffer();

		java.util.Date theTime = new java.util.Date(micros / 1000);

		SimpleDateFormat selectedFormat = new SimpleDateFormat(pattern);
		selectedFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		res.append(selectedFormat.format(theTime));

		DecimalFormat microsFormat = new DecimalFormat("000000");
		DecimalFormat millisFormat = new DecimalFormat("000");

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

				long millis = micros = (micros % 1000000) / 1000;

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

	public static class TestTimeController extends TestCase
	{
		private int _min, _max, _smallTick, _largeTick, _dragSize;

		private boolean _enabled;

		public void testSliderScales()
		{
			SliderRangeManagement range = new SliderRangeManagement()
			{
				public void setMinVal(int min)
				{
					_min = min;
				}

				public void setMaxVal(int max)
				{
					_max = max;
				}

				public void setTickSize(int small, int large, int drag)
				{
					_smallTick = small;
					_largeTick = large;
					_dragSize = drag;
				}

				public void setEnabled(boolean val)
				{
					_enabled = val;
				}
			};

			// initialise our testing values
			_min = _max = _smallTick = _largeTick = -1;
			_enabled = false;

			HiResDate starter = new HiResDate(0, 100);
			HiResDate ender = new HiResDate(0, 200);
			range.resetRange(starter, ender);

			assertEquals("min val set", 0, _min);
			assertEquals("max val set", 100, _max);
			assertEquals("sml tick set", 10000, _smallTick);
			assertEquals("drag size set", 500, _dragSize);
			assertEquals("large tick set", 100000, _largeTick);

			assertTrue("slider should be enabled", _enabled);

			// ok, see how the transfer goes
			HiResDate newToSlider = new HiResDate(0, 130);
			int res = range.toSliderUnits(newToSlider, starter);
			assertEquals("correct to slider units", 30, res);

			// and backwards
			newToSlider = range.fromSliderUnits(res, starter);
			assertEquals("correct from slider units", 130, newToSlider.getMicros());

			// right, now back to millis
			Date starterD = new Date(2005, 3, 3, 12, 1, 1);
			Date enderD = new Date(2005, 3, 12, 12, 1, 1);
			starter = new HiResDate(starterD.getTime());
			ender = new HiResDate(enderD.getTime());
			range.resetRange(starter, ender);

			long diff = (enderD.getTime() - starterD.getTime()) / 1000;
			assertEquals("correct range in secs", diff, _max);
			assertEquals("sml tick set", 60, _smallTick);
			assertEquals("large tick set", 600, _largeTick);

		}

	}

	private class WheelMovedEvent implements Listener
	{
		public void handleEvent(Event event)
		{
			int count = event.count;
			boolean fwd;
			Boolean large = new Boolean(false);

			// is the control button down?
			int keys = event.stateMask;

			if ((keys & SWT.SHIFT) != 0)
				large = new Boolean(true);

			if (count < 0)
				fwd = true;
			else
				fwd = false;

			processClick(large, fwd);
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	public ISelection getSelection()
	{
		return _currentSelection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection)
	{
		_currentSelection = selection;
	}

	protected void fireSelectionChanged(ISelection sel)
	{
		// just double-check that we're not already processing this
		if (sel != _currentSelection)
		{
			_currentSelection = sel;
			if (_selectionListeners != null)
			{
				SelectionChangedEvent sEvent = new SelectionChangedEvent(this, sel);
				for (Iterator stepper = _selectionListeners.iterator(); stepper.hasNext();)
				{
					ISelectionChangedListener thisL = (ISelectionChangedListener) stepper.next();
					if (thisL != null)
					{
						thisL.selectionChanged(sEvent);
					}
				}
			}
		}
	}
	
	/** ok - put in the stepper mode buttons - and any others we think of.
	 * 
	 */
	private void populateDropDownList(final LayerPainterManager myLayerPainterManager )
	{
		// clear the list
		final IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
		final IToolBarManager toolManager = getViewSite().getActionBars().getToolBarManager();		
		
		// ok, remove the existing items
		menuManager.removeAll();
		toolManager.removeAll();
		
		// ok, what are the painters we know about
		TemporalLayerPainter[] list = myLayerPainterManager.getList();

		// add the items
		for (int i = 0; i < list.length; i++)
		{
			// ok, next painter
			final TemporalLayerPainter painter = list[i];
			
			// create an action for it
			Action thisOne = new Action(painter.toString(), Action.AS_RADIO_BUTTON)
			{
				public void runWithEvent(Event event)
				{
					myLayerPainterManager.setCurrent(painter);
				}
			};

			// and store it on both menus
			menuManager.add(thisOne);			
			toolManager.add(thisOne);
		}
		
		// ok, let's have a separator
		menuManager.add(new Separator());
		
		// ok, second menu for the DTG formats
		MenuManager formatMenu = new MenuManager("DTG Format");
		
		// and store it
		menuManager.add(formatMenu);
		
		// and now the date formats
		String[] formats = DateFormatPropertyEditor.getTagList();
		for (int i = 0; i < formats.length; i++)
		{
			final String thisFormat = formats[i];
			
			// the properties manager is expecting the integer index of the new format, not the string value.
			// so store it as an integer index
			final Integer thisIndex = new Integer(i);
			
			// and create a new action to represent the change
			Action newFormat = new Action(thisFormat, Action.AS_RADIO_BUTTON)
			{
				public void run()
				{
					super.run();
					_myStepperProperties.setPropertyValue(StepperProperties.DTG_FORMAT_ID,thisIndex);
				}
				
			};
			formatMenu.add(newFormat);
		}
	}	
	
	// /////////////////////////////////////////////////////////////////
	// AND PROPERTY EDITORS FOR THE
	// /////////////////////////////////////////////////////////////////
}
