package org.mwc.cmap.TimeController.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import junit.framework.TestCase;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.ui_support.PartMonitor;

import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
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

public class TimeController extends ViewPart
{

	private PartMonitor _myPartMonitor;

	private PropertyChangeListener _temporalListener = null;

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

			public void setTickSize(int small, int large)
			{
				_tNowSlider.setIncrement(small);
				_tNowSlider.setPageIncrement(large);
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
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());

	}

	SliderRangeManagement _slideManager = null;

	/**
	 * the slider control - remember it because we're always changing the limits,
	 * etc
	 */
	private Slider _tNowSlider;

	private void buildInterface(Composite parent)
	{
		// ok, draw our wonderful GUI.

		_wholePanel = new Composite(parent, SWT.NONE);
		RowLayout onTop = new RowLayout();
		onTop.type = SWT.VERTICAL;
		_wholePanel.setLayout(onTop);

		// first create the button holder
		Composite _btnPanel = new Composite(_wholePanel, SWT.NONE);
		GridLayout grid = new GridLayout();
		grid.numColumns = 3;
		_btnPanel.setLayout(grid);
		// put some bits in. First the BWD buttons
		Composite LH = new Composite(_btnPanel, SWT.NONE);
		GridLayout lhGrid = new GridLayout();
		lhGrid.numColumns = 2;
		LH.setLayout(lhGrid);
		Button lBwd = new Button(LH, SWT.NONE);
		lBwd.setText("<<");
		lBwd.addSelectionListener(new TimeButtonSelectionListener(false, false));
		Button sBwd = new Button(LH, SWT.NONE);
		sBwd.setText("<");
		sBwd.addSelectionListener(new TimeButtonSelectionListener(false, true));
		_timeLabel = new Label(_btnPanel, SWT.NONE);
		_timeLabel.setText("-------------------");
		Composite RH = new Composite(_btnPanel, SWT.NONE);
		GridLayout rhGrid = new GridLayout();
		rhGrid.numColumns = 2;
		RH.setLayout(rhGrid);
		Button sFwd = new Button(RH, SWT.NONE);
		sFwd.setText(">");
		sFwd.addSelectionListener(new TimeButtonSelectionListener(true, true));
		Button lFwd = new Button(RH, SWT.NONE);
		lFwd.setText(">>");
		lFwd.addSelectionListener(new TimeButtonSelectionListener(true, false));
		lFwd.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_FORWARD));

		
		// next create the time slider holder
		_tNowSlider = new Slider(_wholePanel, SWT.NONE);
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
	}

	private class TimeButtonSelectionListener implements SelectionListener
	{
		private boolean _fwd;

		private boolean _small;

		public TimeButtonSelectionListener(boolean fwd, boolean small)
		{
			_fwd = fwd;
			_small = small;
		}

		public void widgetSelected(SelectionEvent e)
		{
			processClick(_small, _fwd);
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}
	}

	private void processClick(boolean small, boolean fwd)
	{
		// check that we have a current time (on initialisation some plots may not contain data)
		HiResDate tNow = _myTemporalDataset.getTime();
		if (tNow != null)
		{
			// yup, time is there.  work with it baby
			long micros = tNow.getMicros();
			int scale;
			if (small)
				scale = 1;
			else
				scale = 10;

			int size = scale * 1000 * 1000;

			if (fwd)
				micros += size;
			else
				micros -= size;

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
										_slideManager.resetRange(newPeriod.getStartDTG(), newPeriod
												.getEndDTG());
									}
									
									// also double-check if it's time to enable our interface
									checkTimeEnabled();
								}
							};
						}
						_myTemporalDataset.addListener(_temporalListener,
								TimeProvider.TIME_CHANGED_PROPERTY_NAME);

						// also configure for the current time
						HiResDate newDTG = _myTemporalDataset.getTime();
						timeUpdated(newDTG);

						// and initialise the current time
						TimePeriod firstDTG = _myTemporalDataset.getPeriod();
						_slideManager.resetRange(firstDTG.getStartDTG(), firstDTG
								.getEndDTG());
						
					  checkTimeEnabled();
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part)
					{
						_myTemporalDataset.removeListener(_temporalListener,
								TimeProvider.TIME_CHANGED_PROPERTY_NAME);
					  checkTimeEnabled();
					}
				});
		_myPartMonitor.addPartListener(ControllableTime.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part)
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
					public void eventTriggered(String type, Object part)
					{
						ControllableTime ct = (ControllableTime) part;
						_controllableTime = null;
					  checkTimeEnabled();
					}
				});

	}


	/** convenience method to make the panel enabled if we have a time controller and a valid time
	 * 
	 *
	 */
	private void checkTimeEnabled()
	{
		boolean enable = false;
		
		if(_myTemporalDataset != null)
		{
			if((_controllableTime != null) && (_myTemporalDataset.getTime() != null))
				enable = true;
		}

		_wholePanel.setEnabled(enable);
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
	private void timeUpdated(HiResDate newDTG)
	{
		HiResDate tNow = newDTG;
		if (tNow != null)
		{
			// display the correct time.
			String newVal = DebriefFormatDateTime.toStringHiRes(newDTG);
			_timeLabel.setText(newVal);

			TimePeriod dataPeriod = _myTemporalDataset.getPeriod();
			if (dataPeriod != null)
			{
				int newIndex = _slideManager.toSliderUnits(newDTG, dataPeriod
						.getStartDTG());
				_tNowSlider.setSelection(newIndex);
			}
		}
		else
		{
			System.out.println("null DTG received by time controller");
		}
	}

	// //////////////////////////////
	// slider scale management bits
	// ////////////////////////////////
	private abstract static class SliderRangeManagement
	{
		private boolean _useMicros = false;

		public abstract void setMinVal(int min);

		public abstract void setMaxVal(int max);

		public abstract void setTickSize(int small, int large);

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

					// remember that we are updating the form. don't bother processing
					// state changed events for a bit
					// _updatingForm = true;

					if (range < Integer.MAX_VALUE)
					{
						setMaxVal((int) range);
						setEnabled(true);
						_useMicros = true;
					}
					else
					{
						long rangeMillis = range / 1000;
						if (rangeMillis < Integer.MAX_VALUE)
						{
							// ok, we're going to run in millisecond resolution
							setMaxVal((int) rangeMillis);
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
					int NUM_MILLIS_FOR_STEP;
					if (_useMicros)
					{
						NUM_MILLIS_FOR_STEP = 500;
						smallTick = NUM_MILLIS_FOR_STEP * 1000;
					}
					else
					{
						NUM_MILLIS_FOR_STEP = 1000 * 60 * 1;
						smallTick = NUM_MILLIS_FOR_STEP * 1000;
					}
					largeTick = smallTick * 10;

					setTickSize(smallTick, largeTick);

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
				offset /= 1000;
			}

			res = (int) offset;

			return res;

		}

		public HiResDate fromSliderUnits(int value, HiResDate startDTG)
		{
			if (!_useMicros)
			{
				value *= 1000;
			}

			long newDate = startDTG.getMicros() + value;

			return new HiResDate(0, newDate);
		}
	}

	// //////////////////////////////
	// testing
	// //////////////////////////////

	public static class TestTimeController extends TestCase
	{
		private int _min, _max, _smallTick, _largeTick;

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

				public void setTickSize(int small, int large)
				{
					_smallTick = small;
					_largeTick = large;
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
			assertEquals("sml tick set", 500000, _smallTick);
			assertEquals("large tick set", 5000000, _largeTick);

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

			long diff = enderD.getTime() - starterD.getTime();
			assertEquals("correct range in secs", diff, _max);
			assertEquals("sml tick set", 60 * 1000 * 1000, _smallTick);
			assertEquals("large tick set", 10 * 60 * 1000 * 1000, _largeTick);

		}

	}

}
