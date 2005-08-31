package org.mwc.cmap.TimeController.views;

import java.beans.*;
import java.text.*;
import java.util.*;

import junit.framework.TestCase;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.TimeController.TimeControllerPlugin;
import org.mwc.cmap.TimeController.preferences.PreferenceConstants;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.editors.painters.*;

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

public class TimeController extends ViewPart
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
				_tNowSlider.setThumb(drag);
			}

			public void setEnabled(boolean val)
			{
				_tNowSlider.setEnabled(val);
			}
		};

		// get the actions sorted
		createActions();

		// and fill in the interface
		buildInterface(parent);

		// of course though, we start off with the buttons not enabled
		_wholePanel.setEnabled(false);

		// and start listing for any part action
		setupListeners();

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());

	}

	private void createActions()
	{
	}

	SliderRangeManagement _slideManager = null;

	/**
	 * the slider control - remember it because we're always changing the limits,
	 * etc
	 */
	private Slider _tNowSlider;

	private ComboViewer _painterSelector;

	protected LayerPainterManager _myLayerPainterManager;

	private void buildInterface(Composite parent)
	{
		// ok, draw our wonderful GUI.

		_wholePanel = new Composite(parent, SWT.BORDER);
		GridLayout onTop = new GridLayout();
//		onTop.type = SWT.VERTICAL;
		onTop.numColumns = 1;
		_wholePanel.setLayout(onTop);

		// first create the button holder
		Composite _btnPanelHolder = new Composite(_wholePanel, SWT.NONE);
		_btnPanelHolder.setLayout(new FillLayout());
		Composite _btnPanel = new Composite(_btnPanelHolder, SWT.NONE);

		GridLayout grid = new GridLayout();
		grid.numColumns = 4;
		_btnPanel.setLayout(grid);
		// put some bits in. First the BWD buttons
		Composite LH = new Composite(_btnPanel, SWT.NONE);
		GridLayout lhGrid = new GridLayout();
		lhGrid.numColumns = 3;
		LH.setLayout(lhGrid);
		Button eBwd = new Button(LH, SWT.NONE);
		eBwd.setText("<-");
		eBwd.addSelectionListener(new TimeButtonSelectionListener(false, 0));
		Button lBwd = new Button(LH, SWT.NONE);
		lBwd.setText("<<");
		lBwd.addSelectionListener(new TimeButtonSelectionListener(false, 10));
		Button sBwd = new Button(LH, SWT.NONE);
		sBwd.setText("<");
		sBwd.addSelectionListener(new TimeButtonSelectionListener(false, 1));
		_timeLabel = new Label(_btnPanel, SWT.NONE);
		_timeLabel.setText("-------------------");
		Composite RH = new Composite(_btnPanel, SWT.NONE);
		GridLayout rhGrid = new GridLayout();
		rhGrid.numColumns = 3;
		RH.setLayout(rhGrid);
		Button sFwd = new Button(RH, SWT.NONE);
		sFwd.setText(">");
		sFwd.addSelectionListener(new TimeButtonSelectionListener(true, 1));
		Button lFwd = new Button(RH, SWT.NONE);
		lFwd.setText(">>");
		lFwd.addSelectionListener(new TimeButtonSelectionListener(true, 10));
		// lFwd.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
		// ISharedImages.IMG_TOOL_FORWARD));
		Button eFwd = new Button(RH, SWT.NONE);
		eFwd.setText("->");
		eFwd.addSelectionListener(new TimeButtonSelectionListener(true, 0));

		RowLayout otherBitsLayout = new RowLayout();
		 otherBitsLayout.type = SWT.HORIZONTAL;
		Composite otherBitsPanel = new Composite(_wholePanel, SWT.NONE);
		otherBitsPanel.setLayout(otherBitsLayout);

		// next create the time slider holder
		_tNowSlider = new Slider(otherBitsPanel, SWT.NONE);
		_tNowSlider.setMinimum(0);
		_tNowSlider.setMaximum(100);
		_tNowSlider.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int index = _tNowSlider.getSelection();
				HiResDate newDTG = _slideManager.fromSliderUnits(index,
						_myTemporalDataset.getPeriod().getStartDTG());
				fireNewTime(newDTG);
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		// lastly the painter cursor selector
		_painterSelector = new ComboViewer(otherBitsPanel, SWT.READ_ONLY);
		_painterSelector
				.addSelectionChangedListener(new ISelectionChangedListener()
				{
					public void selectionChanged(SelectionChangedEvent event)
					{
						if (_myLayerPainterManager != null)
						{
							// ok, get what's selected
							ISelection sel = _painterSelector.getSelection();
							IStructuredSelection sel2 = (IStructuredSelection) sel;
							TemporalLayerPainter newOne = (TemporalLayerPainter) sel2
									.getFirstElement();
							_myLayerPainterManager.setCurrent(newOne);
						}

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
			if (event.getPropertyName().equals(
					TimeProvider.TIME_CHANGED_PROPERTY_NAME))
			{
				// ok, use the new time
				HiResDate newDTG = (HiResDate) event.getNewValue();
				timeUpdated(newDTG);
			}
			else if (event.getPropertyName().equals(
					TimeProvider.PERIOD_CHANGED_PROPERTY_NAME))
			{
				TimePeriod newPeriod = (TimePeriod) event.getNewValue();
				_slideManager
						.resetRange(newPeriod.getStartDTG(), newPeriod.getEndDTG());
			}

			// also double-check if it's time to enable our interface
			checkTimeEnabled();
		}
	}

	private class TimeButtonSelectionListener implements SelectionListener
	{
		private boolean _fwd;

		private int _scale;

		public TimeButtonSelectionListener(boolean fwd, int scale)
		{
			_fwd = fwd;
			_scale = scale;
		}

		public void widgetSelected(SelectionEvent e)
		{
			processClick(_scale, _fwd);
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}
	}

	private void processClick(int scale, boolean fwd)
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
			if (scale == 0.0)
			{
				// right, fwd or bwd
				if (fwd)
					micros = _myTemporalDataset.getPeriod().getEndDTG().getMicros();
				else
					micros = _myTemporalDataset.getPeriod().getStartDTG().getMicros();
			}
			else
			{
				// normal processing..
				int size = scale * 1000 * 1000;

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

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());

		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
								_slideManager.resetRange(firstDTG.getStartDTG(), firstDTG
										.getEndDTG());
							}

							checkTimeEnabled();
						}
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
		_myPartMonitor.addPartListener(ControllableTime.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
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
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						_controllableTime = null;
						checkTimeEnabled();
					}
				});
		_myPartMonitor.addPartListener(LayerPainterManager.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						_myLayerPainterManager = (LayerPainterManager) part;
						TemporalLayerPainter[] list = _myLayerPainterManager.getList();

						// clear the list
						_painterSelector.getCombo().removeAll();

						// add the items
						for (int i = 0; i < list.length; i++)
						{
							TemporalLayerPainter painter = list[i];
							_painterSelector.add(painter);
						}
						// and activate it
						_painterSelector.getCombo().setEnabled(true);

						// aaah, and select the first one
						_painterSelector.getCombo().select(0);

					}

				});
		_myPartMonitor.addPartListener(LayerPainterManager.class,
				PartMonitor.CLOSED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						_painterSelector.getCombo().setEnabled(false);
						_myLayerPainterManager = null;
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
		
		if (!_wholePanel.isDisposed())
		{
			Display.getDefault().asyncExec(new Runnable(){
				public void run()
				{
					_wholePanel.setEnabled(finalEnabled);
				}
			});
		}
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
		// todo: sort out where to place the focus
		// viewer.getControl().setFocus();
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

				// updating the text items has to be done in the UI thread.  make it so
				Display.getDefault().asyncExec(new Runnable(){
					public void run()
					{
						// display the correct time.
						String newVal = getFormattedDate(newDTG);

						_timeLabel.setText(newVal);
						
						TimePeriod dataPeriod = _myTemporalDataset.getPeriod();
						if (dataPeriod != null)
						{
							int newIndex = _slideManager.toSliderUnits(newDTG, dataPeriod
									.getStartDTG());
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
		IPreferenceStore store = TimeControllerPlugin.getDefault()
				.getPreferenceStore();
		String dateFormat = store.getString(PreferenceConstants.P_STRING);
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
//		long wholeSeconds = micros / 1000000;

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

	// //////////////////////////////
	// slider scale management bits
	// ////////////////////////////////
	private abstract static class SliderRangeManagement
	{

		// only let slider work in micros if there is under a second of data
		private final int TIME_SPAN_TO_USE_MICROS = 1000000;

		private boolean _useMicros = false;

		public abstract void setMinVal(int min);

		public abstract void setMaxVal(int max);

		public abstract void setTickSize(int small, int large, int drag);

		public abstract void setEnabled(boolean val);

		public void resetRange(HiResDate min, HiResDate max)
		{
			if ((min != null) && (max != null))
			{
				// yes - initialise the ranges
				long range = max.getMicros() - min.getMicros();

				if (range > 0)
				{
					// double-check the min value
					setMinVal(0);

					if (range < TIME_SPAN_TO_USE_MICROS)
					{
						setMaxVal((int) range);
						setEnabled(true);
						_useMicros = true;
					}
					else
					{
						long rangeMillis = range / 1000;
						long rangeSecs = rangeMillis / 1000;
						if (rangeMillis < Integer.MAX_VALUE)
						{
							// ok, we're going to run in millisecond resolution
							setMaxVal((int) rangeSecs);
							_useMicros = false;
							setEnabled(true);
						}
						else
						{
							// hey, we must be running in units which are too large.
							setEnabled(false);
						}
					}

					// ok. just sort out the step size when the user clicks on the slider
					int smallTick;
					int largeTick;
					int dragSize;
					int NUM_MILLIS_FOR_STEP;
					if (_useMicros)
					{
						dragSize = 500; // 500 microseconds
						NUM_MILLIS_FOR_STEP = dragSize * 20; // 10 millis
					}
					else
					{
						dragSize = 1; // one second
						NUM_MILLIS_FOR_STEP = dragSize * 60; // one minute
					}
					smallTick = NUM_MILLIS_FOR_STEP;
					largeTick = smallTick * 10;

					setTickSize(smallTick, largeTick, dragSize);

					// ok, we've finished updating the form. back to normal processing
					// _updatingForm = false;
				}
			}
		}

		public int toSliderUnits(HiResDate now, HiResDate startDTG)
		{
			int res = 0;
			long offset = now.getMicros() - startDTG.getMicros();

			if (!_useMicros)
			{
				offset /= 1000000;
			}

			res = (int) offset;

			return res;

		}

		public HiResDate fromSliderUnits(int value, HiResDate startDTG)
		{
			long newValue = value;

			if (!_useMicros)
			{
				newValue *= 1000000;
			}

			long newDate = startDTG.getMicros() + newValue;

			return new HiResDate(0, newDate);
		}
	}

	// //////////////////////////////
	// testing
	// //////////////////////////////

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
			assertEquals("large tick set",  600, _largeTick);
			
		}

	}

	private class WheelMovedEvent implements Listener
	{
		public void handleEvent(Event event)
		{
			int count = event.count;
			boolean fwd;
			int scale = 1;

			// is the control button down?
			int keys = event.stateMask;
			if ((keys & SWT.CONTROL) != 0)
				scale *= 10;
			else
				scale *= 1;

			if ((keys & SWT.SHIFT) != 0)
				scale *= 60;
			else
				scale *= 1;

			if (count < 0)
				fwd = true;
			else
				fwd = false;

			processClick(scale, fwd);
		}
	}
}
