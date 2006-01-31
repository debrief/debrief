package org.mwc.cmap.TimeController.views;

import java.awt.event.ActionEvent;
import java.beans.*;
import java.text.*;
import java.util.*;

import junit.framework.TestCase;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.TimeController.TimeControllerPlugin;
import org.mwc.cmap.TimeController.controls.DTGBiSlider;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.debrief.core.editors.painters.*;

import MWC.GUI.Layers;
import MWC.GUI.Properties.DateFormatPropertyEditor;
import MWC.GenericData.*;
import MWC.Utilities.Timer.TimerListener;

/**
 * View performing time management: show current time, allow control of time,
 * allow selection of time periods
 */

public class TimeController extends ViewPart implements ISelectionProvider, TimerListener
{
	private PartMonitor _myPartMonitor;

	/**
	 * the automatic timer we are using
	 */
	private MWC.Utilities.Timer.Timer _theTimer;

	/**
	 * the editor the user is currently working with (assigned alongside the
	 * time-provider object)
	 */
	protected IEditorPart _currentEditor;

	/**
	 * listen out for new times
	 */
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
	 * the "write" interface for indicating a selected time period
	 */
	private ControllablePeriod _controllablePeriod;

	/**
	 * label showing the current time
	 */
	private Label _timeLabel;

	/**
	 * the set of layers we control through the range selector
	 */
	private Layers _myLayers;

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
	 * and the preferences for time control
	 */
	private TimeControlProperties _myStepperProperties;

	/**
	 * module to look after the limits of the slider
	 */
	SliderRangeManagement _slideManager = null;

	/**
	 * the action which stores the current DTG as a bookmark
	 */
	private Action _setAsBookmarkAction;

	/**
	 * when the user clicks on us, we set our properties as a selection. Remember
	 * the set of properties
	 */
	private StructuredSelection _propsAsSelection = null;

	/**
	 * our fancy time range selector
	 */
	private DTGBiSlider _dtgRangeSlider;

	/**
	 * whether the user wants to trim to time period after bislider change
	 */
	private Action _filterToSelectionAction;

	/**
	 * the slider control - remember it because we're always changing the limits,
	 * etc
	 */
	private Scale _tNowSlider;

	/**
	 * the play button, obviously.
	 */
	private Button _playButton;

	private PropertyChangeListener _myDateFormatListener = null;

	/**
	 * name of property storing slider step size, used for saving state
	 */
	private final String SLIDER_STEP_SIZE = "SLIDER_STEP_SIZE";

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

		/**
		 * the timer-related settings
		 */
		_theTimer = new MWC.Utilities.Timer.Timer();
		_theTimer.stop();
		_theTimer.setDelay(1000);
		_theTimer.addTimerListener(this);

	}

	/**
	 * ok - put in our bits
	 * 
	 * @param parent
	 */
	private void buildInterface(Composite parent)
	{
		// ok, draw our wonderful GUI.
		_wholePanel = new Composite(parent, SWT.BORDER);

		GridLayout onTop = new GridLayout();
		onTop.horizontalSpacing = 0;
		onTop.verticalSpacing = 0;
		onTop.marginHeight = 0;
		onTop.marginWidth = 0;
		_wholePanel.setLayout(onTop);

		// stick in the long list of VCR buttons
		createVCRbuttons();

		// and the current time label
		// Composite timeContainer = new Composite(_wholePanel, SWT.NONE);
		// GridData timeGrid = new GridData(GridData.FILL_HORIZONTAL);
		// timeContainer.setLayoutData(timeGrid);
		//		
		// FillLayout timeFiller = new FillLayout(SWT.HORIZONTAL);
		// timeContainer.setLayout(timeFiller);

		_timeLabel = new Label(_wholePanel, SWT.NONE);
		GridData labelGrid = new GridData(GridData.FILL_HORIZONTAL);
		_timeLabel.setLayoutData(labelGrid);
		_timeLabel.setAlignment(SWT.CENTER);
		_timeLabel.setText("--------------------------");
		// _timeLabel.setFont(new Font(Display.getDefault(), "OCR A Extended", 16,
		// SWT.NONE));
		_timeLabel.setFont(new Font(Display.getDefault(), "Arial", 16, SWT.NONE));
		_timeLabel.setForeground(new Color(Display.getDefault(), 33, 255, 22));
		_timeLabel.setBackground(new Color(Display.getDefault(), 0, 0, 0));

		// next create the time slider holder
		_tNowSlider = new Scale(_wholePanel, SWT.NONE);
		GridData sliderGrid = new GridData(GridData.FILL_HORIZONTAL);
		_tNowSlider.setLayoutData(sliderGrid);
		_tNowSlider.setMinimum(0);
		_tNowSlider.setMaximum(100);
		_tNowSlider.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (!_alreadyProcessingChange)
				{
					_alreadyProcessingChange = true;
					int index = _tNowSlider.getSelection();
					HiResDate newDTG = _slideManager.fromSliderUnits(index, _dtgRangeSlider
							.getStepSize());
					fireNewTime(newDTG);
					_alreadyProcessingChange = false;
				}
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		_wholePanel.addListener(SWT.MouseWheel, new WheelMovedEvent());

		_dtgRangeSlider = new DTGBiSlider(_wholePanel)
		{
			public void rangeChanged(TimePeriod period)
			{
				super.rangeChanged(period);

				selectPeriod(period);
			}

		};
		if (_defaultSliderResolution != null)
			_dtgRangeSlider.setStepSize(_defaultSliderResolution.intValue());

		// hmm, do we have a default step size for the slider?

		GridData biGrid = new GridData(GridData.FILL_BOTH);
		_dtgRangeSlider.getControl().setLayoutData(biGrid);
	}

	/**
	 * 
	 */
	private void createVCRbuttons()
	{
		// first create the button holder
		Composite _btnPanel = new Composite(_wholePanel, SWT.NONE);
		GridData btnGrid = new GridData(GridData.FILL_HORIZONTAL);
		_btnPanel.setLayoutData(btnGrid);
		FillLayout btnFiller = new FillLayout(SWT.HORIZONTAL);
		btnFiller.marginHeight = 0;
		_btnPanel.setLayout(btnFiller);

		Button eBwd = new Button(_btnPanel, SWT.NONE);
		eBwd.addSelectionListener(new TimeButtonSelectionListener(false, null));
		eBwd.setImage(TimeControllerPlugin.getImage("icons/media_beginning.png"));
		Button lBwd = new Button(_btnPanel, SWT.NONE);
		lBwd.setText("<<");
		lBwd.setImage(TimeControllerPlugin.getImage("icons/media_rewind.png")
			);
		lBwd.addSelectionListener(new TimeButtonSelectionListener(false, new Boolean(true)));
		Button sBwd = new Button(_btnPanel, SWT.NONE);
		sBwd.setText("<");
		sBwd.setImage(TimeControllerPlugin.getImage("icons/media_back.png")
			);
		sBwd.addSelectionListener(new TimeButtonSelectionListener(false, new Boolean(false)));

		_playButton = new Button(_btnPanel, SWT.TOGGLE | SWT.NONE);
		_playButton.setImage(TimeControllerPlugin.getImage("icons/media_play.png")
				);
		_playButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				boolean playing = _playButton.getSelection();
				ImageDescriptor thisD;
				if (playing)
				{
					thisD = TimeControllerPlugin.getImageDescriptor("icons/media_pause.png");
					startPlaying();
				}
				else
				{
					thisD = TimeControllerPlugin.getImageDescriptor("icons/media_play.png");
					stopPlaying();
				}
				_playButton.setImage(thisD.createImage());
			}
		});

		Button sFwd = new Button(_btnPanel, SWT.NONE);
		sFwd.setImage(TimeControllerPlugin.getImage("icons/media_forward.png")
				);
		sFwd.addSelectionListener(new TimeButtonSelectionListener(true, new Boolean(false)));
		Button lFwd = new Button(_btnPanel, SWT.NONE);
		lFwd.setImage(TimeControllerPlugin.getImage("icons/media_fast_forward.png")
				);
		lFwd.addSelectionListener(new TimeButtonSelectionListener(true, new Boolean(true)));
		Button eFwd = new Button(_btnPanel, SWT.NONE);
		eFwd.setImage(TimeControllerPlugin.getImage("icons/media_end.png")
				);
		eFwd.addSelectionListener(new TimeButtonSelectionListener(true, null));
	}

	boolean _alreadyProcessingChange = false;

	/**
	 * user has selected a time period, indicate it to the controllable
	 * 
	 * @param period
	 */
	protected void selectPeriod(final TimePeriod period)
	{
		if (_controllablePeriod != null)
		{

			// updating the text items has to be done in the UI thread. make it so
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					_controllablePeriod.setPeriod(period);

					// are we set to filter?
					if (_filterToSelectionAction.isChecked())
					{
						_controllablePeriod
								.performOperation(ControllablePeriod.FILTER_TO_TIME_PERIOD);

						// and trim down the range of our slider manager

						// hey, what's the current dtg?
						HiResDate currentDTG = _slideManager.fromSliderUnits(_tNowSlider
								.getSelection(), _dtgRangeSlider.getStepSize());

						// update the range of the slider
						_slideManager.resetRange(period.getStartDTG(), period.getEndDTG());

						// hey - remember the updated time range (largely so that we can
						// restore from file later on)
						_myStepperProperties.setSliderStartTime(period.getStartDTG());
						_myStepperProperties.setSliderEndTime(period.getEndDTG());

						// do we need to move the slider back into a valid point?
						// hmm, was it too late?
						HiResDate trimmedDTG = null;
						if (currentDTG.greaterThan(period.getEndDTG()))
						{
							trimmedDTG = period.getEndDTG();
						}
						else if (currentDTG.lessThan(period.getStartDTG()))
						{
							trimmedDTG = period.getStartDTG();
						}
						else
						{
							if (!_alreadyProcessingChange)
								_tNowSlider.setSelection(_slideManager.toSliderUnits(currentDTG));
						}

						// did we have to move them?
						if (trimmedDTG != null)
						{
							fireNewTime(trimmedDTG);
						}
					}
				}
			});
		}

	}

	protected void stopPlaying()
	{
		_theTimer.stop();
	}

	/**
	 * ok, start auto-stepping forward through the serial
	 */
	protected void startPlaying()
	{
		// hey - set a practical minimum step size, 1/4 second is a fair start point
		final long delayToUse = Math.max(_myStepperProperties.getAutoInterval().getMillis(),
				250);

		// ok - make sure the time has the right time
		_theTimer.setDelay(delayToUse);

		_theTimer.start();
	}

	public void onTime(ActionEvent event)
	{

		// temporarily remove ourselves, to prevent being called twice
		_theTimer.removeTimerListener(this);

		// catch any exceptions raised here, it doesn't really
		// matter if we miss a time step
		try
		{
			// pass the step operation on to our parent
			processClick(Boolean.FALSE, true);
		}
		catch (Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Error on auto-time stepping", e);
		}

		// register ourselves as a time again
		_theTimer.addTimerListener(this);

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
				System.out.println("heard about new time period!");
				_slideManager.resetRange(newPeriod.getStartDTG(), newPeriod.getEndDTG());
			}

			// also double-check if it's time to enable our interface
			checkTimeEnabled();
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
					size = (long) _myStepperProperties.getLargeStep().getValueIn(
							Duration.MICROSECONDS);
				}
				else
				{
					// and the small size step
					size = (long) _myStepperProperties.getSmallStep().getValueIn(
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

	private boolean _firingNewTime = false;

	/**
	 * any default size to use for the slider threshold (read in as part of the
	 * 'init' operation before we actually create the slider)
	 */
	private Integer _defaultSliderResolution;

	private void fireNewTime(HiResDate dtg)
	{
		if (!_firingNewTime)
		{
			_firingNewTime = true;
			_controllableTime.setTime(this, dtg, true);
			_firingNewTime = false;
		}
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
							// ok, stop listening to the old one
							if (_myTemporalDataset != null)
							{
								// right, we were looking at something, and now we're not.

								// stop playing (if we were)
								if (_theTimer.isRunning())
								{
									// un-depress the play button
									_playButton.setSelection(false);

									// and tell the button's listeners (which will stop the timer
									// and update the image)
									_playButton.notifyListeners(SWT.Selection, new Event());
								}

								// stop listening to that dataset
								_myTemporalDataset.removeListener(_temporalListener,
										TimeProvider.TIME_CHANGED_PROPERTY_NAME);
							}

							// implementation here.
							_myTemporalDataset = (TimeProvider) part;

							// and start listening to the new one
							_myTemporalDataset.addListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);

							// also configure for the current time
							HiResDate newDTG = _myTemporalDataset.getTime();

							timeUpdated(newDTG);

							// and initialise the current time
							TimePeriod timeRange = _myTemporalDataset.getPeriod();
							if (timeRange != null)
							{
								// don't update the slider here, take it's value from the
								// TimeControlPreferences
								_slideManager.resetRange(timeRange.getStartDTG(), timeRange.getEndDTG());

								// and our range selector
								_dtgRangeSlider.updateOuterRanges(timeRange);
							}

							checkTimeEnabled();

							// hmm, do we want to store this part?
							_currentEditor = (IEditorPart) parentPart;
						}

					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
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

						// and sort out whether we should be active or not.
						checkTimeEnabled();
					}
				});
		// _myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
		// new PartMonitor.ICallback()
		// {
		// public void eventTriggered(String type, Object part, IWorkbenchPart
		// parentPart)
		// {
		// // are we still listening?
		// if (_myTemporalDataset != null)
		// {
		// _myTemporalDataset.removeListener(_temporalListener,
		// TimeProvider.TIME_CHANGED_PROPERTY_NAME);
		// _myTemporalDataset = null;
		// }
		// checkTimeEnabled();
		// }
		// });

		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// implementation here.
						Layers newLayers = (Layers) part;
						if (newLayers != _myLayers)
						{
							_myLayers = newLayers;
						}
					}

				});
		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						if (part == _myLayers)
							_myLayers = null;
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
						if (part == _controllableTime)
						{
							_controllableTime = null;
							checkTimeEnabled();
						}
					}
				});
		_myPartMonitor.addPartListener(ControllablePeriod.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// implementation here.
						ControllablePeriod ct = (ControllablePeriod) part;
						_controllablePeriod = ct;
						checkTimeEnabled();
					}

				});
		_myPartMonitor.addPartListener(ControllablePeriod.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						if (part == _controllablePeriod)
						{
							_controllablePeriod = null;
							checkTimeEnabled();
						}
					}
				});
		_myPartMonitor.addPartListener(LayerPainterManager.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// ok, insert the painter mode actions, together with our standard
						// ones
						populateDropDownList((LayerPainterManager) part);
					}

				});
		_myPartMonitor.addPartListener(LayerPainterManager.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// _painterSelector.getCombo().setEnabled(false);
						// _myLayerPainterManager = null;
					}
				});
		_myPartMonitor.addPartListener(TimeControlPreferences.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						// just check we're not already managing this plot
						if (part != _myStepperProperties)
						{
							// ok, ignore the old one, if we have one
							if (_myStepperProperties != null)
							{
								_myStepperProperties.removePropertyChangeListener(_myDateFormatListener);
								_myStepperProperties = null;
							}

							_myStepperProperties = (TimeControlProperties) part;

							if (_myDateFormatListener == null)
								_myDateFormatListener = new PropertyChangeListener()
								{
									public void propertyChange(PropertyChangeEvent evt)
									{
										// right, see if the user is changing the DTG format
										if (evt.getPropertyName().equals(TimeControlProperties.DTG_FORMAT_ID))
										{
											// ok, refresh the DTG
											String newVal = getFormattedDate(_myTemporalDataset.getTime());
											_timeLabel.setText(newVal);

											// hmm, also set the bi-slider to repaint so we get fresh
											// labels
											_dtgRangeSlider.update();
										}
										else if (evt.getPropertyName().equals(
												TimeControlProperties.STEP_INTERVAL_ID))
										{
											// hey, if we're stepping, we'd better change the size of
											// the time step
											if (_theTimer.isRunning())
											{
												Duration theDelay = (Duration) evt.getNewValue();
												_theTimer.setDelay((long) theDelay
														.getValueIn(Duration.MILLISECONDS));
											}
										}
									}
								};

							// also, listen out for changes in the DTG formatter
							_myStepperProperties.addPropertyChangeListener(_myDateFormatListener);

							// and update the slider ranges
							// do we have start/stop times?
							HiResDate startDTG = _myStepperProperties.getSliderStartTime();
							if ((startDTG != null) && (_myTemporalDataset != null))
							{
								// cool - update the slider to our data settings

								// aaah, first check that the time period isn't greater than our
								// data period
								TimePeriod dataPeriod = _myTemporalDataset.getPeriod();
								HiResDate startTime, endTime;
								startTime = _myStepperProperties.getSliderStartTime();
								endTime = _myStepperProperties.getSliderEndTime();

								// trim slider start time
								if (startTime.lessThan(dataPeriod.getStartDTG()))
									startTime = dataPeriod.getStartDTG();
								else if (startTime.greaterThan(dataPeriod.getEndDTG()))
									startTime = dataPeriod.getEndDTG();

								// trim slider end time
								if (endTime.lessThan(dataPeriod.getStartDTG()))
									endTime = dataPeriod.getStartDTG();
								else if (endTime.greaterThan(dataPeriod.getEndDTG()))
									endTime = dataPeriod.getEndDTG();

								_slideManager.resetRange(startTime, endTime);

								// right, trim the DTG to the current slider settings
								HiResDate tNow = _myTemporalDataset.getTime();
								HiResDate tNew = null;
								if (tNow != null)
								{
									TimePeriod sliderPeriod = new TimePeriod.BaseTimePeriod(
											_myStepperProperties.getSliderStartTime(), _myStepperProperties
													.getSliderEndTime());
									if (!sliderPeriod.contains(tNow))
									{
										if (tNow.lessThan(_myStepperProperties.getSliderStartTime()))
											tNew = _myStepperProperties.getSliderStartTime();
										else
											tNew = _myStepperProperties.getSliderEndTime();
									}

									if (tNew != null)
										_controllableTime.setTime(this, tNew, false);

								}

								// and set the time again - the slider has probably forgotten
								// it.
								timeUpdated(_myTemporalDataset.getTime());

							}
							else
							{
								// we don't have an existing period - reset it from the data
								// provided

								// do we have a dataset even?
								if (_myTemporalDataset != null)
								{
									TimePeriod period = _myTemporalDataset.getPeriod();
									if (period != null)
										_slideManager.resetRange(period.getStartDTG(), period.getEndDTG());
								}
							}

						}

					}
				});

		_myPartMonitor.addPartListener(TimeControlPreferences.class, PartMonitor.DEACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
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
					// aaah, if we're clearing the panel, set the text to "pending"
					if (!finalEnabled)
					{
						_timeLabel.setText("-----");
					}

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
	public void editMeInProperties()
	{
		// do we have any data?
		if (_myStepperProperties != null)
		{
			// get the editable thingy
			if (_propsAsSelection == null)
				_propsAsSelection = new StructuredSelection(_myStepperProperties);

			if (_selectionListeners != null)
			{
				SelectionChangedEvent sEvent = new SelectionChangedEvent(this, _propsAsSelection);
				for (Iterator stepper = _selectionListeners.iterator(); stepper.hasNext();)
				{
					ISelectionChangedListener thisL = (ISelectionChangedListener) stepper.next();
					if (thisL != null)
					{
						thisL.selectionChanged(sEvent);
					}
				}
			}
			_propsAsSelection = null;
		}
		else
		{
			System.out.println("we haven't got any properties yet");
		}
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
				// note - we use 'syncExec'. When we were using asyncExec, we would have a back-log 
				// of events waiting to fire.
				
				Runnable nextEvent = new Runnable()
				{
					public void run()
					{					
						// display the correct time.
						String newVal = getFormattedDate(newDTG);

						_timeLabel.setText(newVal);

						if (!_alreadyProcessingChange)
						{

							// there's a (slim) chance that the temp dataset has already been
							// cleared, or
							// hasn't been caught yet. just check we still know about it
							if (_myTemporalDataset != null)
							{
								TimePeriod dataPeriod = _myTemporalDataset.getPeriod();
								if (dataPeriod != null)
								{
									int newIndex = _slideManager.toSliderUnits(newDTG);
									// did we find a valid time?
									if(newIndex != -1)
									{
										// yes, go for it.
										_tNowSlider.setSelection(newIndex);
									}
								}
							}
						}
					}
				};
				
				Display.getDefault().syncExec(nextEvent);

			}
		}
		else
		{
			System.out.println("null DTG received by time controller");
		}

	}

	private String getFormattedDate(HiResDate newDTG)
	{
		String newVal = "n/a";
		// hmm, we may have heard about the new date before hearing about the
		// plot's stepper properties. check they arrived
		if (_myStepperProperties != null)
		{
			String dateFormat = _myStepperProperties.getDTGFormat();

			// store.getString(PreferenceConstants.P_STRING);
			try
			{
				newVal = toStringHiRes(newDTG, dateFormat);
			}
			catch (IllegalArgumentException e)
			{
				System.err.println("Invalid date format in preferences");
			}
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
			int res = range.toSliderUnits(newToSlider);
			assertEquals("correct to slider units", 30, res);

			// and backwards
			newToSlider = range.fromSliderUnits(res, 1000);
			assertEquals("correct from slider units", 130, newToSlider.getMicros());

			// right, now back to millis
			Calendar cal = new GregorianCalendar();

			cal.set(2005, 3, 3, 12, 1, 1);
			Date starterD = cal.getTime();

			cal.set(2005, 3, 12, 12, 1, 1);
			Date enderD = cal.getTime();

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
		return null;
	}

	/**
	 * accessor to the slider (used for testing the view)
	 * 
	 * @return the slider control
	 */
	public DTGBiSlider getSlider()
	{
		return _dtgRangeSlider;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(ISelection selection)
	{
	}

	/**
	 * ok - put in the stepper mode buttons - and any others we think of.
	 */
	private void populateDropDownList(final LayerPainterManager myLayerPainterManager)
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
					myLayerPainterManager.setCurrentPainter(painter);
				}
			};
			String descPath = "icons/" + painter.toString().toLowerCase() + ".gif";
			thisOne.setImageDescriptor(org.mwc.debrief.core.CorePlugin
					.getImageDescriptor(descPath));

			// hmm, and see if this is our current painter
			if (painter.getName().equals(myLayerPainterManager.getCurrentPainter().getName()))
			{
				thisOne.setChecked(true);
			}

			// and store it on both menus
			menuManager.add(thisOne);
			toolManager.add(thisOne);
		}

		// ok, let's have a separator
		menuManager.add(new Separator());
		toolManager.add(new Separator());

		// add the list of DTG formats for the DTG slider
		addDateFormats(menuManager);

		// add the list of DTG formats for the DTG slider
		addBiSliderResolution(menuManager);

		// now the add-bookmark item
		_setAsBookmarkAction = new Action("Add DTG as bookmark", Action.AS_PUSH_BUTTON)
		{
			public void runWithEvent(Event event)
			{
				addMarker();
			}
		};
		_setAsBookmarkAction.setImageDescriptor(org.mwc.debrief.core.CorePlugin
				.getImageDescriptor("icons/bkmrk_nav.gif"));
		_setAsBookmarkAction.setToolTipText("Add this DTG to the list of bookmarks");
		menuManager.add(_setAsBookmarkAction);

		// let user indicate whether we should be filtering to window
		_filterToSelectionAction = new Action("Filter to time", Action.AS_CHECK_BOX)
		{

		};
		_filterToSelectionAction.setImageDescriptor(org.mwc.debrief.core.CorePlugin
				.getImageDescriptor("icons/filter_to_period.gif"));
		_filterToSelectionAction
				.setToolTipText("Filter to time period on time-range slider update");
		menuManager.add(_filterToSelectionAction);
		toolManager.add(_filterToSelectionAction);

		// // and the
		// Action expandTimeSliders = new Action("Expand slider to full period",
		// Action.AS_PUSH_BUTTON)
		// {
		// public void runWithEvent(Event event)
		// {
		// expandTimeSliderRangeToFull();
		// }
		// };
		// expandTimeSliders.setImageDescriptor(org.mwc.debrief.core.CorePlugin
		// .getImageDescriptor("icons/expand_time_period.gif"));
		// expandTimeSliders.setToolTipText("Expand time-slider to full period");
		// menuManager.add(expandTimeSliders);
		// toolManager.add(expandTimeSliders);

		// and a properties editor
		Action toolboxProperties = new Action("Properties...", Action.AS_PUSH_BUTTON)
		{
			public void runWithEvent(Event event)
			{
				editMeInProperties();
			}
		};
		toolboxProperties.setToolTipText("Edit Time Controller properties");
		toolboxProperties.setImageDescriptor(org.mwc.debrief.core.CorePlugin
				.getImageDescriptor("icons/properties.gif"));
		menuManager.add(new Separator());
		menuManager.add(toolboxProperties);

		// ok - get the action bars to re-populate themselves, otherwise we don't
		// see our changes
		getViewSite().getActionBars().updateActionBars();
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
					_myStepperProperties.setPropertyValue(TimeControlProperties.DTG_FORMAT_ID,
							thisIndex);

					// todo: we need to tell the plot that it's changed - fake this by
					// firing a quick formatting change
					_myLayers.fireReformatted(null);

				}

			};
			formatMenu.add(newFormat);
		}
	}

	/**
	 * @param menuManager
	 */
	private void addBiSliderResolution(final IMenuManager menuManager)
	{
		// ok, second menu for the DTG formats
		MenuManager formatMenu = new MenuManager("Range selector resolution");

		// and store it
		menuManager.add(formatMenu);

		// and now the date formats
		Object[][] stepSizes = { 
				{ "1 sec", new Long(1000) },
				{ "1 min", new Long(60 * 1000) }, 
				{ "5 min", new Long(5 * 60 * 1000) },
				{ "15 min", new Long(15 * 60 * 1000) },
				{ "1 hour", new Long(60 * 60 * 1000) }, };

		for (int i = 0; i < stepSizes.length; i++)
		{

			final String sizeLabel = (String) stepSizes[i][0];
			final Long thisSize = (Long) stepSizes[i][1];

			// and create a new action to represent the change
			Action newFormat = new Action(sizeLabel, Action.AS_RADIO_BUTTON)
			{
				public void run()
				{
					super.run();
					_dtgRangeSlider.setStepSize(thisSize.longValue());

					// ok, update the ranges of the slider
					_dtgRangeSlider.updateOuterRanges(_myTemporalDataset.getPeriod());
				}

			};
			formatMenu.add(newFormat);
		}
	}

	protected void expandTimeSliderRangeToFull()
	{
		TimePeriod period = _myTemporalDataset.getPeriod();
		_slideManager.resetRange(period.getStartDTG(), period.getEndDTG());
	}

	protected void addMarker()
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
				String currentText = _timeLabel.getText();
				long tNow = _myTemporalDataset.getTime().getMicros();
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
						attributes.put(IMarker.MESSAGE, content);
						attributes.put(IMarker.LOCATION, currentText);
						attributes.put(IMarker.LINE_NUMBER, "" + tNow);
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

	/**
	 * convenience class to help us manage the fwd/bwd step buttons
	 * 
	 * @author Ian.Mayo
	 */
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

	// /////////////////////////////////////////////////////////////////
	// AND PROPERTY EDITORS FOR THE
	// /////////////////////////////////////////////////////////////////

	public void setFocus()
	{
		// ok - put the cursor on the time sldier
		_tNowSlider.setFocus();

	}

	/**
	 * @param memento
	 */
	public void saveState(IMemento memento)
	{

		super.saveState(memento);

		// // ok, store me bits
		// start off with the time step
		memento.putInteger(SLIDER_STEP_SIZE, (int) _dtgRangeSlider.getStepSize());

		// first the
	}

	/**
	 * @param site
	 * @param memento
	 * @throws PartInitException
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{

		super.init(site, memento);

		if (memento != null)
		{

			// try the slider step size
			Integer stepSize = memento.getInteger(SLIDER_STEP_SIZE);
			if (stepSize != null)
			{
				_defaultSliderResolution = stepSize;
			}
		}
	}

	/** provide the currently selected period
	 * 
	 * @return
	 */
	public TimePeriod getPeriod()
	{
		return getSlider().getPeriod();
	}
}
