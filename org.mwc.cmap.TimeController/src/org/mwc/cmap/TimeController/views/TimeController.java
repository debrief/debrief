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
package org.mwc.cmap.TimeController.views;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.TimeController.TimeControllerPlugin;
import org.mwc.cmap.TimeController.controls.DTGBiSlider;
import org.mwc.cmap.TimeController.controls.DTGBiSlider.DoFineControl;
import org.mwc.cmap.TimeController.properties.FineTuneStepperProps;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.Temporal.ControllablePeriod;
import org.mwc.cmap.core.DataTypes.Temporal.ControllableTime;
import org.mwc.cmap.core.DataTypes.Temporal.SteppableTime;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlPreferences;
import org.mwc.cmap.core.DataTypes.Temporal.TimeControlProperties;
import org.mwc.cmap.core.DataTypes.Temporal.TimeProvider;
import org.mwc.cmap.core.DataTypes.TrackData.TrackDataProvider;
import org.mwc.cmap.core.interfaces.TimeControllerOperation;
import org.mwc.cmap.core.interfaces.TimeControllerOperation.TimeControllerOperationStore;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.plotViewer.editors.CorePlotEditor;
import org.mwc.debrief.core.editors.PlotEditor;
import org.mwc.debrief.core.editors.painters.LayerPainterManager;
import org.mwc.debrief.core.editors.painters.TemporalLayerPainter;
import org.mwc.debrief.core.editors.painters.highlighters.SWTPlotHighlighter;

import MWC.Algorithms.PlainProjection;
import MWC.Algorithms.PlainProjection.RelativeProjectionParent;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DateFormatPropertyEditor;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Timer.TimerListener;

/**
 * View performing time management: show current time, allow control of time,
 * allow selection of time periods
 */

public class TimeController extends ViewPart implements ISelectionProvider,
		TimerListener, RelativeProjectionParent
{
	private static final String ICON_BKMRK_NAV = "icons/bkmrk_nav.gif";

	private static final String ICON_FILTER_TO_PERIOD = "icons/filter_to_period.gif";

	private static final String ICON_LOCK_VIEW2 = "icons/lock_view2.png";

	private static final String ICON_LOCK_VIEW1 = "icons/lock_view1.png";

	private static final String ICON_LOCK_VIEW = "icons/lock_view.png";

	private static final String ICON_PROPERTIES = "icons/properties.gif";

	private static final String ICON_MEDIA_END = "icons/24/media_end.png";

	private static final String ICON_MEDIA_FAST_FORWARD = "icons/24/media_fast_forward.png";

	private static final String ICON_MEDIA_FORWARD = "icons/24/media_forward.png";

	private static final String ICON_MEDIA_BACK = "icons/24/media_back.png";

	private static final String ICON_MEDIA_REWIND = "icons/24/media_rewind.png";

	private static final String ICON_MEDIA_BEGINNING = "icons/24/media_beginning.png";

	private static final String ICON_MEDIA_PAUSE = "icons/24/media_pause.png";

	private static final String ICON_MEDIA_PLAY = "icons/24/media_play.png";

	private static final String DUFF_TIME_TEXT = "--------------------------";

	private static final String PAUSE_TEXT = "Pause automatically moving forward";

	private static final String PLAY_TEXT = "Start automatically moving forward";

	private static final String OP_LIST_MARKER_ID = "OPERATION_LIST_MARKER";

	private PartMonitor _myPartMonitor;

	/**
	 * the automatic timer we are using
	 */
	MWC.Utilities.Timer.Timer _myTimer;

	/**
	 * the editor the user is currently working with (assigned alongside the
	 * time-provider object)
	 */
	protected transient IEditorPart _currentEditor;

	/**
	 * listen out for new times
	 */
	final PropertyChangeListener _temporalListener = new NewTimeListener();

	/**
	 * the temporal dataset controlling the narrative entry currently displayed
	 */
	TimeProvider _myTemporalDataset;

	/**
	 * the "write" interface for the plot which tracks the narrative, where
	 * avaialable
	 */
	ControllableTime _controllableTime;

	/**
	 * an object that gets stepped, not one that we can slide backwards & forwards
	 * through
	 * 
	 */
	SteppableTime _steppableTime;

	/**
	 * the "write" interface for indicating a selected time period
	 */
	ControllablePeriod _controllablePeriod;

	/**
	 * label showing the current time
	 */
	Label _timeLabel;

	/**
	 * the set of layers we control through the range selector
	 */
	Layers _myLayers;

	/**
	 * the parent object for the time controller. It is at this level that we
	 * enable/disable the controls
	 */
	Composite _wholePanel;

	/**
	 * the holder for the VCR controls
	 * 
	 */
	private Composite _btnPanel;

	/**
	 * the people listening to us
	 */
	Vector<ISelectionChangedListener> _selectionListeners;

	/**
	 * and the preferences for time control
	 */
	TimeControlProperties _myStepperProperties;

	/**
	 * module to look after the limits of the slider
	 */
	SliderRangeManagement _slideManager = null;

	/**
	 * when the user clicks on us, we set our properties as a selection. Remember
	 * the set of properties
	 */
	private StructuredSelection _propsAsSelection = null;

	/**
	 * our fancy time range selector
	 */
	DTGBiSlider _dtgRangeSlider;

	/**
	 * whether the user wants to trim to time period after bislider change
	 */
	Action _filterToSelectionAction;

	/**
	 * the slider control - remember it because we're always changing the limits,
	 * etc
	 */
	Scale _tNowSlider;

	/**
	 * the play button, obviously.
	 */
	Button _playButton;

	PropertyChangeListener _myDateFormatListener = null;

	/**
	 * name of property storing slider step size, used for saving state
	 */
	private final String SLIDER_STEP_SIZE = "SLIDER_STEP_SIZE";

	/**
	 * make the forward button visible at a class level so that we can fire it in
	 * testing
	 */
	private Button _forwardButton;

	/**
	 * utility class to help us plot relative plots
	 */
	RelativeProjectionParent _relativeProjector;

	/**
	 * the projection we're going to set to relative mode, as we wish
	 */
	PlainProjection _targetProjection;

	TrackDataProvider.TrackDataListener _theTrackDataListener;

	/**
	 * keep track of the list of play buttons, since on occasion we may want to
	 * hide some of them
	 * 
	 */
	private HashMap<String, Button> _buttonList;

	/**
	 * listener for the play button
	 * 
	 */
	private SelectionAdapter _playListener;

	// private static final Color bColor = new Color(Display.getDefault(), 0, 0,
	// 0);
	private static final Color bColor = Display.getCurrent().getSystemColor(
			SWT.COLOR_BLACK);

	private static final Color fColor = new Color(Display.getDefault(), 33, 255,
			22);

	private static final Font arialFont = new Font(Display.getDefault(), "Arial",
			16, SWT.NONE);

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(final Composite parent)
	{

		// and declare our context sensitive help
		CorePlugin
				.declareContextHelp(parent, "org.mwc.debrief.help.TimeController");

		// also sort out the slider conversion bits. We do it at the start,
		// because
		// callbacks
		// created during initialisation may need to use/reset it
		_slideManager = new SliderRangeManagement()
		{
			public void setMinVal(final int min)
			{
				if (!_tNowSlider.isDisposed())
				{
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							_tNowSlider.setMinimum(min);
						}
					});
				}
			}

			public void setMaxVal(final int max)
			{
				if (!_tNowSlider.isDisposed())
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							_tNowSlider.setMaximum(max);
						}
					});
			}

			public void setTickSize(final int small, final int large, final int drag)
			{
				if (!_tNowSlider.isDisposed())
				{
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							_tNowSlider.setIncrement(small);
							_tNowSlider.setPageIncrement(large);
						}
					});
				}
			}

			public void setEnabled(final boolean val)
			{
				if (!_tNowSlider.isDisposed())
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							_tNowSlider.setEnabled(val);
						}
					});
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

		// say that we're a selection provider
		getSite().setSelectionProvider(this);
	}

	private MWC.Utilities.Timer.Timer getTimer()
	{
		// have we created it yet?
		if (_myTimer == null)
		{
			// nope, better go for it.
			/**
			 * the timer-related settings
			 */
			_myTimer = new MWC.Utilities.Timer.Timer();
			_myTimer.stop();
			_myTimer.setDelay(1000);
			_myTimer.addTimerListener(this);
		}

		return _myTimer;
	}

	/**
	 * ok - put in our bits
	 * 
	 * @param parent
	 */
	private void buildInterface(final Composite parent)
	{
		// ok, draw our wonderful GUI.
		_wholePanel = new Composite(parent, SWT.BORDER);

		final GridLayout onTop = new GridLayout();
		onTop.horizontalSpacing = 0;
		onTop.verticalSpacing = 0;
		onTop.marginHeight = 0;
		onTop.marginWidth = 0;
		_wholePanel.setLayout(onTop);

		// stick in the long list of VCR buttons
		createVCRbuttons();

		_timeLabel = new Label(_wholePanel, SWT.NONE);
		final GridData labelGrid = new GridData(GridData.FILL_HORIZONTAL);
		_timeLabel.setLayoutData(labelGrid);
		_timeLabel.setAlignment(SWT.CENTER);
		_timeLabel.setText(DUFF_TIME_TEXT);
		// _timeLabel.setFont(new Font(Display.getDefault(), "OCR A Extended",
		// 16,
		// SWT.NONE));
		_timeLabel.setFont(arialFont);
		_timeLabel.setForeground(fColor);
		_timeLabel.setBackground(bColor);

		// next create the time slider holder
		_tNowSlider = new Scale(_wholePanel, SWT.NONE);
		final GridData sliderGrid = new GridData(GridData.FILL_HORIZONTAL);
		_tNowSlider.setLayoutData(sliderGrid);
		_tNowSlider.setMinimum(0);
		_tNowSlider.setMaximum(100);
		_tNowSlider.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				_alreadyProcessingChange = true;
				try
				{
					final int index = _tNowSlider.getSelection();
					final HiResDate newDTG = _slideManager.fromSliderUnits(index,
							_dtgRangeSlider.getStepSize());
					fireNewTime(newDTG);
				}
				catch (final Exception ex)
				{
					System.err.println("Tripped in step forward:" + ex);
				}
				finally
				{
					_alreadyProcessingChange = false;
				}
			}

			public void widgetDefaultSelected(final SelectionEvent e)
			{
			}
		});
		_tNowSlider.addListener(SWT.MouseWheel, new WheelMovedEvent());

		/**
		 * declare the handler we use for if the user double-clicks on a slider
		 * marker
		 * 
		 */
		final DoFineControl fineControl = new DoFineControl()
		{
			public void adjust(final boolean isMax)
			{
				doFineControl(isMax);
			}
		};

		_dtgRangeSlider = new DTGBiSlider(_wholePanel, fineControl)
		{
			public void rangeChanged(final TimePeriod period)
			{
				super.rangeChanged(period);

				selectPeriod(period);
			}

		};
		if (_defaultSliderResolution != null)
			_dtgRangeSlider.setStepSize(_defaultSliderResolution.intValue());

		// hmm, do we have a default step size for the slider?
		final GridData biGrid = new GridData(GridData.FILL_BOTH);
		_dtgRangeSlider.getControl().setLayoutData(biGrid);
	}

	/**
	 * user has double-clicked on one of the slider markers, allow detailed edit
	 * 
	 * @param doMinVal
	 *          whether it was the min or max value
	 */
	public void doFineControl(final boolean doMinVal)
	{
		final FineTuneStepperProps fineTunerProperties = new FineTuneStepperProps(
				_dtgRangeSlider, doMinVal);
		final EditableWrapper wrappedEditable = new EditableWrapper(
				fineTunerProperties);
		final StructuredSelection _propsAsSelection1 = new StructuredSelection(
				wrappedEditable);
		CorePlugin.editThisInProperties(_selectionListeners, _propsAsSelection1,
				this, this);
	}

	/**
	 * 
	 */
	private void createVCRbuttons()
	{
		// first create the button holder
		_btnPanel = new Composite(_wholePanel, SWT.BORDER);
		_btnPanel.setLayout(new GridLayout(7, true));

		final Button eBwd = new Button(_btnPanel, SWT.NONE);
		addTimeButtonListener(eBwd, new RepeatingTimeButtonListener(false, null, false));
		eBwd.setImage(TimeControllerPlugin.getImage(ICON_MEDIA_BEGINNING));
		eBwd.setToolTipText("Move to start of dataset");

		final Button lBwd = new Button(_btnPanel, SWT.NONE);
		lBwd.setToolTipText("Move backward large step (hold to repeat)");
		lBwd.setImage(TimeControllerPlugin.getImage(ICON_MEDIA_REWIND));
		RepeatingTimeButtonListener listener = new RepeatingTimeButtonListener(false, new Boolean(true), true);
		addTimeButtonListener(lBwd, listener);
		
		final Button sBwd = new Button(_btnPanel, SWT.NONE);
		sBwd.setToolTipText("Move backward small step (hold to repeat)");
		sBwd.setImage(TimeControllerPlugin.getImage(ICON_MEDIA_BACK));
		listener = new RepeatingTimeButtonListener(false, new Boolean(false), true);
		addTimeButtonListener(sBwd, listener);
		
		_playButton = new Button(_btnPanel, SWT.TOGGLE | SWT.NONE);
		_playButton.setImage(TimeControllerPlugin.getImage(ICON_MEDIA_PLAY));
		_playButton.setToolTipText(PLAY_TEXT);
		_playListener = new SelectionAdapter()
		{
			public void widgetSelected(final SelectionEvent e)
			{
				final boolean playing = _playButton.getSelection();
				final String tipTxt;
				final String imageTxt;
				// ImageDescriptor thisD;
				if (playing)
				{
					startPlaying();
					tipTxt = PAUSE_TEXT;
					imageTxt = ICON_MEDIA_PAUSE;
				}
				else
				{
					stopPlaying();
					tipTxt = PLAY_TEXT;
					imageTxt = ICON_MEDIA_PLAY;

				}
				
				// ok, set the tooltip & image
				_playButton.setToolTipText(tipTxt);
				_playButton.setImage(TimeControllerPlugin
						.getImage(imageTxt));

				
			}
		};
		_playButton.addSelectionListener(_playListener);

		_forwardButton = new Button(_btnPanel, SWT.NONE);
		_forwardButton.setImage(TimeControllerPlugin
				.getImage(ICON_MEDIA_FORWARD));
		listener = new RepeatingTimeButtonListener(true, new Boolean(false), true);
		addTimeButtonListener(_forwardButton, listener);
		
		_forwardButton.setToolTipText("Move forward small step (hold to repeat)");

		final Button lFwd = new Button(_btnPanel, SWT.NONE);
		lFwd.setImage(TimeControllerPlugin.getImage(ICON_MEDIA_FAST_FORWARD));
		lFwd.setToolTipText("Move forward large step (hold to repeat)");
		listener = new RepeatingTimeButtonListener(true, new Boolean(true), true);
		addTimeButtonListener(lFwd, listener);
		
		
		final Button eFwd = new Button(_btnPanel, SWT.NONE);
		addTimeButtonListener(eFwd, new RepeatingTimeButtonListener(true, null, false));
		eFwd.setImage(TimeControllerPlugin.getImage(ICON_MEDIA_END));
		eFwd.setToolTipText("Move to end of dataset");

		final GridDataFactory btnGd = GridDataFactory.fillDefaults().grab(true,
				false);
		btnGd.applyTo(eBwd);
		btnGd.applyTo(lBwd);
		btnGd.applyTo(sBwd);
		btnGd.applyTo(_playButton);
		btnGd.applyTo(_forwardButton);
		btnGd.applyTo(lFwd);
		btnGd.applyTo(eFwd);

		// and apply it to the whole panel
		btnGd.applyTo(_btnPanel);

		_buttonList = new HashMap<String, Button>();
		_buttonList.put("eBwd", eBwd);
		_buttonList.put("lBwd", lBwd);
		_buttonList.put("sBwd", sBwd);
		_buttonList.put("play", _playButton);
		_buttonList.put("sFwd", _forwardButton);
		_buttonList.put("lFwd", lFwd);
		_buttonList.put("eFwd", eFwd);
	}

	private void addTimeButtonListener(final Button button,
			final RepeatingTimeButtonListener listener)
	{
		button.addListener(SWT.MouseDown, listener);
		button.addListener(SWT.MouseUp, listener);
		button.addDisposeListener(new DisposeListener()
		{
			
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				listener.purge();
				button.removeListener(SWT.MouseDown, listener);
				button.removeListener(SWT.MouseUp, listener);
			}
		});
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

			// updating the text items has to be done in the UI thread. make it
			// so
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					// just do a double-check that we have a controllable
					// period,
					// - after this process is being run as async, we may have
					// lost the controllable
					// period since starting the manoeuvre.
					if (_controllablePeriod == null)
					{
						CorePlugin
								.logError(
										Status.ERROR,
										"Maintainer problem: In TimeController, we have lost our controllable period in async call",
										null);
						return;
					}

					_controllablePeriod.setPeriod(period);

					// are we set to filter?
					if (_filterToSelectionAction.isChecked())
					{
						_controllablePeriod
								.performOperation(ControllablePeriod.FILTER_TO_TIME_PERIOD);

						// and trim down the range of our slider manager

						// hey, what's the current dtg?
						final HiResDate currentDTG = _slideManager.fromSliderUnits(
								_tNowSlider.getSelection(), _dtgRangeSlider.getStepSize());

						// update the range of the slider
						_slideManager.resetRange(period.getStartDTG(), period.getEndDTG());

						// hey - remember the updated time range (largely so
						// that we can
						// restore from file later on)
						_myStepperProperties.setSliderStartTime(period.getStartDTG());
						_myStepperProperties.setSliderEndTime(period.getEndDTG());

						// do we need to move the slider back into a valid
						// point?
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
								if (!_tNowSlider.isDisposed())
								{
									_tNowSlider.setSelection(_slideManager
											.toSliderUnits(currentDTG));
								}
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

	void stopPlaying()
	{
		/**
		 * give the event to our child class, in case it's a scenario
		 * 
		 */
		if (_steppableTime != null)
			_steppableTime.pause(this, true);

		getTimer().stop();
	}

	/**
	 * ok, start auto-stepping forward through the serial
	 */
	void startPlaying()
	{
		// hey - set a practical minimum step size, 1/4 second is a fair start
		// point
		final long delayToUse = Math.max(_myStepperProperties.getAutoInterval()
				.getMillis(), 250);

		// ok - make sure the time has the right time
		getTimer().setDelay(delayToUse);

		getTimer().start();
	}

	public void onTime(final ActionEvent event)
	{

		// temporarily remove ourselves, to prevent being called twice
		getTimer().removeTimerListener(this);

		// catch any exceptions raised here, it doesn't really
		// matter if we miss a time step
		try
		{
			// pass the step operation on to our parent
			processClick(Boolean.FALSE, true);
		}
		catch (final Exception e)
		{
			CorePlugin.logError(Status.ERROR, "Error on auto-time stepping", e);
		}

		// register ourselves as a time again
		getTimer().addTimerListener(this);

	}

	protected final class NewTimeListener implements PropertyChangeListener
	{
		public void propertyChange(final PropertyChangeEvent event)
		{
			// see if it's the time or the period which
			// has changed
			if (event.getPropertyName().equals(
					TimeProvider.TIME_CHANGED_PROPERTY_NAME))
			{
				// ok, use the new time
				final HiResDate newDTG = (HiResDate) event.getNewValue();
				timeUpdated(newDTG);
			}
			else if (event.getPropertyName().equals(
					TimeProvider.PERIOD_CHANGED_PROPERTY_NAME))
			{
				final TimePeriod newPeriod = (TimePeriod) event.getNewValue();
				if (newPeriod == null)
				{
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{

							// hey, we haven't got a time period- disable
							_wholePanel.setEnabled(false);

							// hey - remember the updated time range (largely so
							// that we can
							// restore from file later on)
							_myStepperProperties.setSliderStartTime(null);
							_myStepperProperties.setSliderEndTime(null);

						}
					});
				}
				else
				{

					// extend the slider
					_slideManager.resetRange(newPeriod.getStartDTG(),
							newPeriod.getEndDTG());

					// now the slider selector bar thingy
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							// ok, double-check we're enabled
							_wholePanel.setEnabled(true);

							// and our range selector - first the outer
							// ranges
							_dtgRangeSlider.updateOuterRanges(newPeriod);

							// ok - we no longer reset the range limits on a data change,
							// since it's proving inconvenient to have to reset the sliders
							// after some data is dropped in.

							// ok, now the user ranges...
							// _dtgRangeSlider.updateSelectedRanges(newPeriod.getStartDTG(),
							// newPeriod.getEndDTG());
						}
					});
				}
			}

			// also double-check if it's time to enable our interface
			checkTimeEnabled();
		}
	}

	void processClick(final Boolean large, final boolean fwd)
	{

		// CorePlugin.logError(Status.INFO, "Starting step", null);

		// check that we have a current time (on initialisation some plots may
		// not
		// contain data)
		final HiResDate tNow = _myTemporalDataset.getTime();
		if (tNow != null)
		{
			// just check if we've got a simulation running, in which case we just
			// fire a step
			if (_steppableTime != null)
				// see if the user has pressed 'back to start', in which case we will
				// rewind
				if ((large == null) && (fwd == false))
					_steppableTime.restart(this, true);
				else
					_steppableTime.step(this, true);
			else
			{

				// yup, time is there. work with it baby
				long micros = tNow.getMicros();

				// right, special case for when user wants to go straight to the end
				// - in
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

				final HiResDate newDTG = new HiResDate(0, micros);

				// find the extent of the current dataset

				/*
				 * RIGHT, until JAN 2007 this next line had been commented out -
				 * replaced by the line immediately after it. We've switched back to
				 * this implementation. This implementation lets the time-slider select
				 * a time for which there aren't any points visible. This makes sense
				 * because in it's successor implementation when the DTG slipped outside
				 * the visible time period, the event was rejected, and the time-
				 * controller buttons appeared to break. It remains responsive this
				 * way...
				 */
				TimePeriod timeP = _myTemporalDataset.getPeriod();
				if (_filterToSelectionAction != null && _filterToSelectionAction.isChecked()) {
					TimePeriod filteredPeriod = _controllablePeriod.getPeriod();
					if (filteredPeriod != null) {
						timeP = filteredPeriod;
					}
				}

				if (timeP != null)
				{
					// do we represent a valid time?
					if (timeP.contains(newDTG))
					{
						// yes, fire the new DTG
						fireNewTime(newDTG);
					}
					else
					{
						if (newDTG.greaterThan(timeP.getEndDTG()))
						{
							fireNewTime(timeP.getEndDTG());
						}
						else
						{
							fireNewTime(timeP.getStartDTG());
						}
						stopPlayingTimer();
					}
				}
			}
		}

		// CorePlugin.logError(Status.INFO, "Step complete", null);

	}

	private void stopPlayingTimer()
	{
		if (getTimer().isRunning())
		{
			stopPlaying();
			Display.getDefault().asyncExec(new Runnable()
			{

				@Override
				public void run()
				{
					_playButton.setToolTipText(PLAY_TEXT);
					_playButton.setImage(TimeControllerPlugin
							.getImage(ICON_MEDIA_PLAY));
				}
			});
		}
	}

	private boolean _firingNewTime = false;

	/**
	 * any default size to use for the slider threshold (read in as part of the
	 * 'init' operation before we actually create the slider)
	 */
	private Integer _defaultSliderResolution;

	/**
	 * keep track of what tracks are open - we may want to use them for our
	 * exporting calc data to clipboard
	 */
	protected TrackDataProvider _myTrackProvider;

	/**
	 * the list of operations that the current plot wants us to display
	 * 
	 */
	protected TimeControllerOperation.TimeControllerOperationStore _timeOperations;

	private Vector<Action> _legacyTimeOperations;

	/**
	 * the first of the relative plotting modes - absolute north oriented plt
	 */
	private Action _normalPlottingMode;

	/**
	 * first custom plotting mode: - always centre the plot on ownship, and orient
	 * the plot along ownship heading
	 */
	private Action _primaryCentredPrimaryOrientedPlottingMode;

	/**
	 * second custom plotting mode: always centre the plot on ownship, but keep
	 * north-oriented.
	 */
	private Action _primaryCentredNorthOrientedPlottingMode;

	protected LayerPainterManager _layerPainterManager;

	public void fireNewTime(final HiResDate dtg)
	{
		if (!_firingNewTime)
		{
			_firingNewTime = true;
			try
			{
				_controllableTime.setTime(this, dtg, true);
			}
			finally
			{
				_firingNewTime = false;
			}
		}
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
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (_myTemporalDataset != part)
						{
							// ok, stop listening to the old one
							if (_myTemporalDataset != null)
							{
								// right, we were looking at something, and now
								// we're not.

								// stop playing (if we were)
								if (getTimer().isRunning())
								{
									// un-depress the play button
									_playButton.setSelection(false);

									// and tell the button's listeners (which
									// will stop the timer
									// and update the image)
									_playButton.notifyListeners(SWT.Selection, new Event());
								}

								// stop listening to that dataset
								_myTemporalDataset.removeListener(_temporalListener,
										TimeProvider.TIME_CHANGED_PROPERTY_NAME);
								_myTemporalDataset.removeListener(_temporalListener,
										TimeProvider.PERIOD_CHANGED_PROPERTY_NAME);
							}

							// implementation here.
							_myTemporalDataset = (TimeProvider) part;

							// and start listening to the new one
							_myTemporalDataset.addListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
							_myTemporalDataset.addListener(_temporalListener,
									TimeProvider.PERIOD_CHANGED_PROPERTY_NAME);

							// also configure for the current time
							final HiResDate newDTG = _myTemporalDataset.getTime();

							timeUpdated(newDTG);

							// and initialise the current time
							final TimePeriod timeRange = _myTemporalDataset.getPeriod();
							if (timeRange != null)
							{
								// and our range selector - first the outer
								// ranges
								_dtgRangeSlider.updateOuterRanges(timeRange);

								// ok, now the user ranges...
								_dtgRangeSlider.updateSelectedRanges(timeRange.getStartDTG(),
										timeRange.getEndDTG());

								// and the time slider range
								_slideManager.resetRange(timeRange.getStartDTG(),
										timeRange.getEndDTG());

							}

							checkTimeEnabled();

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
						// was it our one?
						if (_myTemporalDataset == part)
						{
							// ok, stop listening to this object (just in case
							// we were,
							// anyway).
							_myTemporalDataset.removeListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
							_myTemporalDataset.removeListener(_temporalListener,
									TimeProvider.PERIOD_CHANGED_PROPERTY_NAME);

							_myTemporalDataset = null;
						}

						// and sort out whether we should be active or not.
						checkTimeEnabled();
					}
				});

		_myPartMonitor.addPartListener(TimeControllerOperationStore.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part != _timeOperations)
						{
							_timeOperations = (TimeControllerOperationStore) part;

							// and refresh the dropdown menu
							refreshTimeOperations();
						}
					}

				});

		_myPartMonitor.addPartListener(SteppableTime.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (_steppableTime != part)
						{
							_steppableTime = (SteppableTime) part;

							// enable the ui, if we have to.
							checkTimeEnabled();
						}
					}

				});
		_myPartMonitor.addPartListener(SteppableTime.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (_steppableTime != part)
						{
							_steppableTime = null;
							// disable the ui, if we have to.
							checkTimeEnabled();
						}
					}
				});

		_myPartMonitor.addPartListener(Layers.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// implementation here.
						final Layers newLayers = (Layers) part;
						if (newLayers != _myLayers)
						{
							_myLayers = newLayers;
						}
					}

				});
		_myPartMonitor.addPartListener(Layers.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part == _myLayers)
							_myLayers = null;
					}
				});

		_myPartMonitor.addPartListener(RelativeProjectionParent.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// implementation here.
						final RelativeProjectionParent relProjector = (RelativeProjectionParent) part;
						if (relProjector != _relativeProjector)
						{
							// ok, better store it
							storeProjectionParent(relProjector);
						}
					}
				});
		_myPartMonitor.addPartListener(RelativeProjectionParent.class,
				PartMonitor.CLOSED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part == _relativeProjector)
							_relativeProjector = null;
					}
				});

		_myPartMonitor.addPartListener(PlainProjection.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// implementation here.
						final PlainProjection newProjection = (PlainProjection) part;
						if (newProjection != _targetProjection)
						{
							storeNewProjection(newProjection);
						}
					}
				});
		_myPartMonitor.addPartListener(PlainProjection.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part == _targetProjection)
							_targetProjection = null;
					}
				});

		_myPartMonitor.addPartListener(ControllableTime.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{

						if (_controllableTime != part)
						{
							// implementation here.
							final ControllableTime ct = (ControllableTime) part;
							_controllableTime = ct;
							checkTimeEnabled();
						}
					}

				});
		_myPartMonitor.addPartListener(ControllableTime.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part == _controllableTime)
						{
							_controllableTime = null;
							checkTimeEnabled();
						}
					}
				});
		_myPartMonitor.addPartListener(ControllablePeriod.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{

						if (_controllablePeriod != part)
						{
							// right, we're clearly not running a simulation here, clear the
							// simulation object
							// that gets uses as a flag
							_steppableTime = null;

							// implementation here.
							final ControllablePeriod ct = (ControllablePeriod) part;
							_controllablePeriod = ct;
							checkTimeEnabled();

							// ok, we've got all the normal controls, make the ui do it's
							// stuff
							reformatUI(true);
						}
					}

				});
		_myPartMonitor.addPartListener(ControllablePeriod.class,
				PartMonitor.CLOSED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part == _controllablePeriod)
						{
							_controllablePeriod = null;
							checkTimeEnabled();
						}
					}
				});

		_myPartMonitor.addPartListener(TrackDataProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// sort out our listener
						if (_theTrackDataListener == null)
							_theTrackDataListener = new TrackDataProvider.TrackDataListener()
							{

								public void tracksUpdated(final WatchableList primary,
										final WatchableList[] secondaries)
								{
									// ok - make sure we're seeing the full time
									// period
									// NO: don't bother, since adding an annotation causes the
									// filter to reset
									// expandTimeSliderRangeToFull();

									// and the controls are enabled (if we know
									// time data)
									checkTimeEnabled();
								}
							};

						final TrackDataProvider thisTrackProvider = (TrackDataProvider) part;
						if (thisTrackProvider != _myTrackProvider)
						{
							// do we have one already?
							if (_myTrackProvider != null)
							{
								// ok, ditch existing provider
								_myTrackProvider.removeTrackDataListener(_theTrackDataListener);
							}

							// remember the new one
							_myTrackProvider = thisTrackProvider;
							_myTrackProvider.addTrackDataListener(_theTrackDataListener);
						}
					}

				});
		_myPartMonitor.addPartListener(TrackDataProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (part == _myTrackProvider)
						{
							_myTrackProvider = null;
						}
					}
				});

		_myPartMonitor.addPartListener(LayerPainterManager.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (_layerPainterManager != part)
						{
							// ok, insert the painter mode actions, together with
							// our standard
							// ones

							_layerPainterManager = (LayerPainterManager) part;
							populateDropDownList(_layerPainterManager);
						}
					}

				});
		_myPartMonitor.addPartListener(LayerPainterManager.class,
				PartMonitor.CLOSED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						if (_layerPainterManager == part)
						{
							_layerPainterManager = null;
						}
					}
				});

		_myPartMonitor.addPartListener(TimeControlPreferences.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(final String type, final Object part,
							final IWorkbenchPart parentPart)
					{
						// just check we're not already managing this plot
						if (part != _myStepperProperties)
						{
							// ok, ignore the old one, if we have one
							if (_myStepperProperties != null)
							{
								_myStepperProperties
										.removePropertyChangeListener(_myDateFormatListener);
								_myStepperProperties = null;
							}

							_myStepperProperties = (TimeControlProperties) part;

							if (_myDateFormatListener == null)
								_myDateFormatListener = new PropertyChangeListener()
								{
									public void propertyChange(final PropertyChangeEvent evt)
									{
										// right, see if the user is changing
										// the DTG format
										if (evt.getPropertyName().equals(
												TimeControlProperties.DTG_FORMAT_ID))
										{
											// ok, refresh the DTG
											final String newVal = getFormattedDate(_myTemporalDataset
													.getTime());
											_timeLabel.setText(newVal);

											// hmm, also set the bi-slider to
											// repaint so we get fresh
											// labels
											_dtgRangeSlider.update();
										}
										else if (evt.getPropertyName().equals(
												TimeControlProperties.STEP_INTERVAL_ID))
										{
											// hey, if we're stepping, we'd
											// better change the size of
											// the time step
											if (getTimer().isRunning())
											{
												final Duration theDelay = (Duration) evt.getNewValue();
												getTimer().setDelay(
														(long) theDelay.getValueIn(Duration.MILLISECONDS));
											}
										}
									}
								};

							// also, listen out for changes in the DTG formatter
							_myStepperProperties
									.addPropertyChangeListener(_myDateFormatListener);

							// and update the slider ranges
							// do we have start/stop times?
							final HiResDate startDTG = _myStepperProperties
									.getSliderStartTime();
							if ((startDTG != null) && (_myTemporalDataset != null))
							{
								// cool - update the slider to our data settings

								HiResDate startTime, endTime;
								startTime = _myStepperProperties.getSliderStartTime();
								endTime = _myStepperProperties.getSliderEndTime();

								_slideManager.resetRange(startTime, endTime);

								// ok, set the slider ranges...
								_dtgRangeSlider.updateSelectedRanges(startTime, endTime);

								// and set the time again - the slider has
								// probably forgotten
								// it.
								timeUpdated(_myTemporalDataset.getTime());

							}
						}
					}
				});
	}

	protected void refreshTimeOperations()
	{
		// ok, loop through them, deleting them
		final IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();

		// do we have any legacy time operations
		if (_legacyTimeOperations == null)
		{
			_legacyTimeOperations = new Vector<Action>();
		}
		else
		{
			// yup, we do have one - better ditch the old ones
			final Iterator<Action> iter = _legacyTimeOperations.iterator();
			while (iter.hasNext())
			{
				final Action action = iter.next();
				menuManager.remove((IContributionItem) action);
			}

			// and clear the list
			_legacyTimeOperations.removeAllElements();
		}

		// ok, now add the new ones
		if (_timeOperations != null)
		{
			final Iterator<TimeControllerOperation> newOps = _timeOperations
					.iterator();
			while (newOps.hasNext())
			{
				final TimeControllerOperation newOp = newOps.next();
				final Action newAction = new Action(newOp.getName())
				{

					@Override
					public void run()
					{
						newOp.run(_myTrackProvider.getPrimaryTrack(),
								_myTrackProvider.getSecondaryTracks(), getPeriod());
					}
				};

				// give it an id, so we can look for it shortly.
				newAction.setId(newAction.getText());

				final ImageDescriptor id = newOp.getDescriptor();
				if (id != null)
					newAction.setImageDescriptor(id);

				// see if we already contain it
				if (menuManager.find(newAction.getId()) != null)
				{
					// ignore, we already know about it
				}
				else
					menuManager.insertBefore(OP_LIST_MARKER_ID, newAction);
			}

		}
	}

	/**
	 * we may be listening to an object that cannot be rewound, that does not
	 * support backward stepping. If so, let us reformat ourselves accordingly
	 * 
	 * @param canRewind
	 *          whether this time-dataset can rewind
	 */
	protected void reformatUI(final boolean canRewind)
	{
		_tNowSlider.setVisible(canRewind);
		_dtgRangeSlider.getControl().setVisible(canRewind);

		// and now the play buttons
		_buttonList.get("lBwd").setVisible(canRewind);
		_buttonList.get("sBwd").setVisible(canRewind);
		_buttonList.get("lFwd").setVisible(canRewind);
		_buttonList.get("eFwd").setVisible(canRewind);

		final GridData gd1 = (GridData) _buttonList.get("lBwd").getLayoutData();
		final GridData gd2 = (GridData) _buttonList.get("sBwd").getLayoutData();
		final GridData gd3 = (GridData) _buttonList.get("lFwd").getLayoutData();
		final GridData gd4 = (GridData) _buttonList.get("eFwd").getLayoutData();
		gd1.exclude = !canRewind;
		gd2.exclude = !canRewind;
		gd3.exclude = !canRewind;
		gd4.exclude = !canRewind;

		// also reduce the number of columns if we have to
		final GridLayout gl = (GridLayout) _btnPanel.getLayout();
		if (canRewind)
		{
			gl.numColumns = 7;
		}
		else
		{
			gl.numColumns = 3;
		}

		// tell the parent that some buttons have changed, and that it probably
		// wants to do a re-layout
		_btnPanel.pack(true);

		// sort out the dropdowns
		populateDropDownList(null);

	}

	/**
	 * remember the new relative projection provider
	 * 
	 * @param relProjector
	 */
	protected void storeProjectionParent(
			final RelativeProjectionParent relProjector)
	{
		// ok, this isn't us, is it?
		if (relProjector != this)
		{
			// ok, store it
			_relativeProjector = relProjector;
		}
	}

	protected void storeNewProjection(final PlainProjection newProjection)
	{
		// ok, remember the projection
		_targetProjection = newProjection;

		// and tell it we're here
		_targetProjection.setRelativeProjectionParent(this);

		// and reflect it's current status
		if (_targetProjection.getNonStandardPlotting())
		{
			if (_targetProjection.getPrimaryOriented())
			{
				_primaryCentredPrimaryOrientedPlottingMode.setChecked(true);
			}
			else
			{
				_primaryCentredNorthOrientedPlottingMode.setChecked(true);
			}

		}
		else
			_normalPlottingMode.setChecked(true);
	}

	/**
	 * convenience method to make the panel enabled if we have a time controller
	 * and a valid time
	 */
	void checkTimeEnabled()
	{
		boolean enable = false;

		if (_steppableTime != null)
		{
			// this is our 'fancy' situation - just enable it
			enable = true;
		}
		else
		{
			// normal, Debrief-style situation, check we've got
			// what we're after
			if (_myTemporalDataset != null)
			{
				if ((_controllableTime != null)
						&& (_myTemporalDataset.getTime() != null))
					enable = true;
			}
		}

		final boolean finalEnabled = enable;

		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!_wholePanel.isDisposed())
				{
					// aaah, if we're clearing the panel, set the text to
					// "pending"
					if (_myTemporalDataset == null)
					{
						_timeLabel.setText("-----");
						_dtgRangeSlider.setShowLabels(false);
						_dtgRangeSlider.resetMinMaxPointers();
					}

					_wholePanel.setEnabled(finalEnabled);
					_dtgRangeSlider.setShowLabels(finalEnabled);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@SuppressWarnings("deprecation")
	public void dispose()
	{
		super.dispose();

		// and ditch the timer
		_myTimer = null;

		// and stop listening for part activity
		_myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());

		// also stop listening for time events
		if (_myTemporalDataset != null)
		{
			_myTemporalDataset.removeListener(_temporalListener,
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
			_myTemporalDataset.removeListener(_temporalListener,
					TimeProvider.PERIOD_CHANGED_PROPERTY_NAME);
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	void editMeInProperties(final PropertyChangeSupport props)
	{
		// do we have any data?
		if (props != null)
		{
			// get the editable thingy
			if (_propsAsSelection == null)
				_propsAsSelection = new StructuredSelection(props);

			CorePlugin.editThisInProperties(_selectionListeners, _propsAsSelection,
					this, this);
			_propsAsSelection = null;
		}
		else
		{
			System.out.println("we haven't got any properties yet");
		}
	}

	/**
	 * the data we are looking at has updated. If we're set to follow that time,
	 * update ourselves
	 */
	void timeUpdated(final HiResDate newDTG)
	{
		if (newDTG != null)
		{
			// display the correct time.
			if (!_timeLabel.isDisposed())
			{
				// updating the text items has to be done in the UI thread. make
				// it so
				// note - we use 'syncExec'. When we were using asyncExec, we
				// would have
				// a back-log
				// of events waiting to fire.

				final Runnable nextEvent = new Runnable()
				{
					public void run()
					{
						// display the correct time.
						final String newVal = getFormattedDate(newDTG);

						if (!_timeLabel.isDisposed())
							_timeLabel.setText(newVal);

						if (!_alreadyProcessingChange)
						{

							// there's a (slim) chance that the temp dataset has
							// already been
							// cleared, or
							// hasn't been caught yet. just check we still know
							// about it
							if (_myTemporalDataset != null)
							{
								final TimePeriod dataPeriod = _myTemporalDataset.getPeriod();
								if (dataPeriod != null)
								{
									final int newIndex = _slideManager.toSliderUnits(newDTG);
									// did we find a valid time?
									if (newIndex != -1)
									{
										// yes, go for it.
										if (!_tNowSlider.isDisposed())
										{
											_tNowSlider.setSelection(newIndex);
										}
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
			// updating the text items has to be done in the UI thread. make it
			// so
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{

					_timeLabel.setText(DUFF_TIME_TEXT);
				}
			});
		}

	}

	String getFormattedDate(final HiResDate newDTG)
	{
		String newVal = "n/a";
		// hmm, we may have heard about the new date before hearing about the
		// plot's stepper properties. check they arrived
		if (_myStepperProperties != null)
		{
			final String dateFormat = _myStepperProperties.getDTGFormat();

			// store.getString(PreferenceConstants.P_STRING);
			try
			{
				newVal = toStringHiRes(newDTG, dateFormat);
			}
			catch (final IllegalArgumentException e)
			{
				System.err.println("Invalid date format in preferences");
			}
		}
		return newVal;
	}

	private static SimpleDateFormat _myFormat = null;

	private static String _myFormatString = null;

	public synchronized static String toStringHiRes(final HiResDate time, final String pattern)
			throws IllegalArgumentException
	{
		// so, have a look at the data
		final long micros = time.getMicros();
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
		// DecimalFormat millisFormat = new DecimalFormat("000");

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
			// if (micros % 1000000 > 0)
			// {
			// // yes, convert the value to millis
			//
			// long millis = micros = (micros % 1000000) / 1000;
			//
			// res.append(".");
			// res.append(millisFormat.format(millis));
			// }
			// else
			// {
			// // just use the normal output
			// }
		}

		return res.toString();
	}

	public static class TestTimeController extends TestCase
	{
		int _min;

		int _max;

		int _smallTick;

		int _largeTick;

		int _dragSize;

		boolean _enabled;

		public void testSliderScales()
		{
			final SliderRangeManagement range = new SliderRangeManagement()
			{
				public void setMinVal(final int min)
				{
					_min = min;
				}

				public void setMaxVal(final int max)
				{
					_max = max;
				}

				public void setTickSize(final int small, final int large, final int drag)
				{
					_smallTick = small;
					_largeTick = large;
					_dragSize = drag;
				}

				public void setEnabled(final boolean val)
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
			assertEquals("sml tick set", 1, _smallTick);
			assertEquals("drag size set", 0, _dragSize);
			assertEquals("large tick set", 1000, _largeTick);

			assertTrue("slider should be enabled", _enabled);

			// ok, see how the transfer goes
			HiResDate newToSlider = new HiResDate(0, 130);
			final int res = range.toSliderUnits(newToSlider);
			assertEquals("correct to slider units", 30, res);

			// and backwards
			newToSlider = range.fromSliderUnits(res, 1000);
			assertEquals("correct from slider units", 130, newToSlider.getMicros());

			// right, now back to millis
			final Calendar cal = new GregorianCalendar();

			cal.set(2005, 3, 3, 12, 1, 1);
			final Date starterD = cal.getTime();

			cal.set(2005, 3, 12, 12, 1, 1);
			final Date enderD = cal.getTime();

			starter = new HiResDate(starterD.getTime());
			ender = new HiResDate(enderD.getTime());
			range.resetRange(starter, ender);

			final long diff = (enderD.getTime() - starterD.getTime()) / 1000;
			assertEquals("correct range in secs", diff, _max);
			assertEquals("sml tick set", 1, _smallTick);
			assertEquals("large tick set", 60000, _largeTick);

		}

	}

	protected class WheelMovedEvent implements Listener
	{
		public void handleEvent(final Event event)
		{
			// find out what keys are pressed
			final int keys = event.stateMask;

			// is is the control button?
			if ((keys & SWT.CTRL) != 0)
			{
				final double zoomFactor;
				// decide if we're going in or out
				if (event.count > 0)
					zoomFactor = 0.9;
				else
					zoomFactor = 1.1;

				// and request the zoom
				doZoom(zoomFactor);
			}
			else
			{
				// right, we're not zooming, we must be time-stepping
				final int count = event.count;
				boolean fwd;
				Boolean large = new Boolean(false);

				if ((keys & SWT.SHIFT) != 0)
					large = new Boolean(true);

				if (count < 0)
					fwd = true;
				else
					fwd = false;

				processClick(large, fwd);
			}
		}
	}

	public void addSelectionChangedListener(
			final ISelectionChangedListener listener)
	{
		if (_selectionListeners == null)
			_selectionListeners = new Vector<ISelectionChangedListener>(0, 1);

		// see if we don't already contain it..
		if (!_selectionListeners.contains(listener))
			_selectionListeners.add(listener);
	}

	/**
	 * zoom the plot (in response to a control-mouse drag)
	 * 
	 * @param zoomFactor
	 */
	public void doZoom(final double zoomFactor)
	{
		// ok, get the plot, and do some zooming
		if (_currentEditor instanceof PlotEditor)
		{
			final PlotEditor plot = (PlotEditor) _currentEditor;
			plot.getChart().getCanvas().getProjection().zoom(zoomFactor);
			plot.getChart().update();
		}

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
	public DTGBiSlider getPeriodSlider()
	{
		return _dtgRangeSlider;
	}

	public Scale getTimeSlider()
	{
		return _tNowSlider;
	}

	public void removeSelectionChangedListener(
			final ISelectionChangedListener listener)
	{
		_selectionListeners.remove(listener);
	}

	public void setSelection(final ISelection selection)
	{
	}

	/**
	 * ok - put in the stepper mode buttons - and any others we think of.
	 */
	void populateDropDownList(final LayerPainterManager myLayerPainterManager)
	{
		// clear the list
		final IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
		final IToolBarManager toolManager = getViewSite().getActionBars()
				.getToolBarManager();

		// ok, remove the existing items
		menuManager.removeAll();
		toolManager.removeAll();

		// create a host for when we're populating the properties window
		final ISelectionProvider provider = this;

		// right, do we have something with editable layer details?
		if (myLayerPainterManager != null)
		{

			// ok - add the painter selectors/editors
			createPainterOptions(myLayerPainterManager, menuManager, toolManager,
					provider);

			// ok, let's have a separator
			toolManager.add(new Separator());

			// now add the highlighter options/editors
			createHighlighterOptions(myLayerPainterManager, menuManager, provider);

			// and another separator
			menuManager.add(new Separator());
		}

		// add the list of DTG formats for the DTG slider
		addDateFormats(menuManager);

		// add the list of DTG formats for the DTG slider
		addBiSliderResolution(menuManager);

		// and another separator
		menuManager.add(new Separator());

		// and another separator
		toolManager.add(new Separator());
		
		// let user indicate whether we should be filtering to window
		_filterToSelectionAction = new Action("Filter to period",
				Action.AS_CHECK_BOX)
		{

			@Override
			public void run()
			{
				super.run();
				if (isChecked())
				{
					final HiResDate tNow = _myTemporalDataset.getTime();
					if (tNow != null)
					{
						TimePeriod period = _controllablePeriod.getPeriod();
						if (period != null)
						{
							if (!period.contains(tNow))
							{
								if (tNow.greaterThan(period.getEndDTG()))
								{
									fireNewTime(period.getEndDTG());
								}
								else
								{
									fireNewTime(period.getStartDTG());
								}
							}
							stopPlayingTimer();
						}
					}
				}
			}

		};
		_filterToSelectionAction
				.setImageDescriptor(org.mwc.debrief.core.DebriefPlugin
						.getImageDescriptor(ICON_FILTER_TO_PERIOD));
		_filterToSelectionAction
				.setToolTipText("Filter plot data to selected time period");
		menuManager.add(_filterToSelectionAction);
		toolManager.add(_filterToSelectionAction);

		// and another separator
		menuManager.add(new Separator());

		// now the add-bookmark item
		final Action _setAsBookmarkAction = new Action("Add DTG as bookmark",
				Action.AS_PUSH_BUTTON)
		{
			public void runWithEvent(final Event event)
			{
				addMarker();
			}
		};
		_setAsBookmarkAction.setImageDescriptor(CorePlugin
				.getImageDescriptor(ICON_BKMRK_NAV));
		_setAsBookmarkAction
				.setToolTipText("Add this DTG to the list of bookmarks");
		_setAsBookmarkAction.setId(OP_LIST_MARKER_ID); // give it an id, so we can
		menuManager.add(_setAsBookmarkAction);
		// refer to this later on.

		// and another separator
		menuManager.add(new Separator());

		// now our own menu editor
		final Action toolboxProperties = new Action(
				"Edit Time controller properties", Action.AS_PUSH_BUTTON)
		{
			public void runWithEvent(final Event event)
			{
				editMeInProperties(_myStepperProperties);
			}
		};
		toolboxProperties.setToolTipText("Edit Time Controller properties");
		toolboxProperties.setImageDescriptor(org.mwc.debrief.core.DebriefPlugin
				.getImageDescriptor(ICON_PROPERTIES));

		menuManager.add(toolboxProperties);
		toolManager.add(toolboxProperties);

		// and sort out our specific items
		refreshTimeOperations();

		// and the help link
		menuManager.add(new Separator());
		menuManager.add(CorePlugin.createOpenHelpAction(
				"org.mwc.debrief.help.TimeController", null, this));

		// ok - get the action bars to re-populate themselves, otherwise we
		// don't
		// see our changes
		getViewSite().getActionBars().updateActionBars();
	}

	/**
	 * @param myLayerPainterManager
	 * @param menuManager
	 * @param provider
	 */
	private void createHighlighterOptions(
			final LayerPainterManager myLayerPainterManager,
			final IMenuManager menuManager, final ISelectionProvider provider)
	{
		// right, first the drop-down for the display-er
		// ok, second menu for the DTG formats
		final MenuManager highlighterMenu = new MenuManager("Highlight Mode");

		// and store it
		menuManager.add(highlighterMenu);

		// and the range highlighters
		final SWTPlotHighlighter[] highlighterList = myLayerPainterManager
				.getHighlighterList();
		final String curHighlighterName = myLayerPainterManager
				.getCurrentHighlighter().getName();

		// add the items
		for (int i = 0; i < highlighterList.length; i++)
		{
			// ok, next painter
			final SWTPlotHighlighter highlighter = highlighterList[i];

			// create an action for it
			final Action thisOne = new Action(highlighter.toString(),
					Action.AS_RADIO_BUTTON)
			{
				public void runWithEvent(final Event event)
				{
					myLayerPainterManager.setCurrentHighlighter(highlighter);

					// and redo this list (deferred until the current processing
					// is complete...
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							populateDropDownList(myLayerPainterManager);
						}
					});

				}
			};
			String descPath = "icons/" + highlighter.toString().toLowerCase() + ".png";
			descPath = descPath.replace(" ", "_");
			thisOne.setImageDescriptor(org.mwc.debrief.core.DebriefPlugin
					.getImageDescriptor(descPath));

			// hmm, and see if this is our current painter
			if (highlighter.getName().equals(curHighlighterName))
				thisOne.setChecked(true);

			// and store it on both menus
			highlighterMenu.add(thisOne);
		}

		// ok, now for the current highlighter
		final SWTPlotHighlighter currentHighlighter = myLayerPainterManager
				.getCurrentHighlighter();

		// create an action for it
		final IWorkbenchPart myPart = this;
		final Action highlighterProperties = new Action("Edit current highlighter:"
				+ currentHighlighter.getName(), Action.AS_PUSH_BUTTON)
		{
			public void runWithEvent(final Event event)
			{
				// ok - get the info object for this painter
				if (currentHighlighter.hasEditor())
				{
					final EditableWrapper pw = new EditableWrapper(currentHighlighter,
							_myLayers);
					CorePlugin.editThisInProperties(_selectionListeners,
							new StructuredSelection(pw), provider, myPart);
				}
			}
		};
		highlighterProperties.setImageDescriptor(org.mwc.debrief.core.DebriefPlugin
				.getImageDescriptor(ICON_PROPERTIES));

		// and store it on both menus
		highlighterMenu.add(highlighterProperties);
	}

	/**
	 * @param myLayerPainterManager
	 * @param menuManager
	 * @param toolManager
	 * @param provider
	 */
	private void createPainterOptions(
			final LayerPainterManager myLayerPainterManager,
			final IMenuManager menuManager, final IToolBarManager toolManager,
			final ISelectionProvider provider)
	{
		// right, first the drop-down for the display-er
		// ok, second menu for the DTG formats
		MenuManager displayMenu = new MenuManager("Display Mode");

		// and store it
		menuManager.add(displayMenu);

		// ok, what are the painters we know about
		final TemporalLayerPainter[] painterList = myLayerPainterManager
				.getPainterList();

		// add the items
		for (int i = 0; i < painterList.length; i++)
		{
			// ok, next painter
			final TemporalLayerPainter painter = painterList[i];

			// create an action for it
			final Action changePainter = new Action(painter.toString(),
					Action.AS_RADIO_BUTTON)
			{
				public void runWithEvent(final Event event)
				{
					myLayerPainterManager.setCurrentPainter(painter);

					// and redo this list (deferred until the current processing
					// is complete...
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							populateDropDownList(myLayerPainterManager);
						}
					});
				}
			};
			final String descPath = "icons/16/" + painter.toString().toLowerCase()
					+ ".png";
			changePainter.setImageDescriptor(org.mwc.debrief.core.DebriefPlugin
					.getImageDescriptor(descPath));

			// hmm, and see if this is our current painter
			if (painter.getName().equals(
					myLayerPainterManager.getCurrentPainter().getName()))
			{
				changePainter.setChecked(true);
			}

			// and store it on both menus
			displayMenu.add(changePainter);
			toolManager.add(changePainter);
		}

		// put the display painter property editor into this one
		final TemporalLayerPainter currentPainter = myLayerPainterManager
				.getCurrentPainter();
		// create an action for it
		final IWorkbenchPart myPart = this;
		final Action currentPainterProperties = new Action("Edit current painter:"
				+ currentPainter.getName(), Action.AS_PUSH_BUTTON)
		{
			public void runWithEvent(final Event event)
			{
				// ok - get the info object for this painter
				if (currentPainter.hasEditor())
				{
					final EditableWrapper pw = new EditableWrapper(currentPainter,
							_myLayers);
					CorePlugin.editThisInProperties(_selectionListeners,
							new StructuredSelection(pw), provider, myPart);
				}
			}
		};
		currentPainterProperties
				.setImageDescriptor(org.mwc.debrief.core.DebriefPlugin
						.getImageDescriptor(ICON_PROPERTIES));

		// and store it on both menus
		displayMenu.add(currentPainterProperties);

		// lastly, sort out the relative projection mode
		// right, first the drop-down for the display-er
		// ok, second menu for the DTG formats
		displayMenu = new MenuManager("Plotting mode");

		// and store it
		menuManager.add(displayMenu);

		_normalPlottingMode = new Action("Normal", Action.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				setRelativeMode(false, false);
			}
		};
		_normalPlottingMode.setImageDescriptor(TimeControllerPlugin
				.getImageDescriptor(ICON_LOCK_VIEW));
		displayMenu.add(_normalPlottingMode);

		_primaryCentredNorthOrientedPlottingMode = new Action(
				"Primary centred/North oriented", Action.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				// see if we have a primary track...
				if (_myTrackProvider != null)
				{
					if (_myTrackProvider.getPrimaryTrack() == null)
					{
						CorePlugin.showMessage("Primary Centred Plotting",
								"A Primary Track must be specified to use this mode");
						_normalPlottingMode.setChecked(true);
						_primaryCentredNorthOrientedPlottingMode.setChecked(false);
					}
					else
						setRelativeMode(true, false);
				}
			}
		};
		_primaryCentredNorthOrientedPlottingMode
				.setImageDescriptor(TimeControllerPlugin
						.getImageDescriptor(ICON_LOCK_VIEW1));
		displayMenu.add(_primaryCentredNorthOrientedPlottingMode);

		_primaryCentredPrimaryOrientedPlottingMode = new Action(
				"Primary centred/Primary oriented", Action.AS_RADIO_BUTTON)
		{

			@Override
			public void run()
			{
				setRelativeMode(true, true);
			}
		};
		_primaryCentredPrimaryOrientedPlottingMode
				.setImageDescriptor(TimeControllerPlugin
						.getImageDescriptor(ICON_LOCK_VIEW2));
		// no, let's not offer primary centred, primary oriented view
		// displayMenu.add(_primaryCentredPrimaryOrientedPlottingMode);

	}

	private void setRelativeMode(final boolean primaryCentred,
			final boolean primaryOriented)
	{
		_targetProjection.setRelativeMode(primaryCentred, primaryOriented);
		// and trigger redraw
		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		final IWorkbenchPage page = win.getActivePage();
		final IEditorPart editor = page.getActiveEditor();
		if (editor instanceof CorePlotEditor)
		{
			final CorePlotEditor plot = (CorePlotEditor) editor;
			plot.update();
		}

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
					_myStepperProperties.setPropertyValue(
							TimeControlProperties.DTG_FORMAT_ID, thisIndex);

					// todo: we need to tell the plot that it's changed - fake
					// this by
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
		final MenuManager formatMenu = new MenuManager("Time slider increment");

		// and store it
		menuManager.add(formatMenu);

		// and now the date formats
		final Object[][] stepSizes =
		{
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
			final Action newFormat = new Action(sizeLabel, Action.AS_RADIO_BUTTON)
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
		// hey - check we've got some data first....
		if (_myTemporalDataset != null)
		{
			final TimePeriod period = _myTemporalDataset.getPeriod();

			// do we know our period?
			if (period != null)
			{
				_slideManager.resetRange(period.getStartDTG(), period.getEndDTG());
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
				final String currentText = _timeLabel.getText();
				final long tNow = _myTemporalDataset.getTime().getMicros();
				if (file != null)
				{
					// yup, get the description
					final InputDialog inputD = new InputDialog(getViewSite().getShell(),
							"Add bookmark at this DTG", "Enter description of this bookmark",
							currentText, null);
					inputD.open();

					final String content = inputD.getValue();
					if (content != null)
					{
						final IMarker marker = file.createMarker(IMarker.BOOKMARK);
						final Map<String, Object> attributes = new HashMap<String, Object>(
								4);
						attributes.put(IMarker.MESSAGE, content);
						attributes.put(IMarker.LOCATION, currentText);
						attributes.put(IMarker.LINE_NUMBER, "" + tNow);
						attributes.put(IMarker.USER_EDITABLE, Boolean.FALSE);
						marker.setAttributes(attributes);
					}
				}

			}
		}
		catch (final CoreException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * convenience class to help us manage the fwd/bwd step buttons
	 * 
	 * issue https://github.com/debrief/debrief/issues/710
	 * 
	 * @author Ian.Mayo
	 * @author snpe
	 */
	private class RepeatingTimeButtonListener implements Listener
	{
		private static final long DELAY = 300;
		
		protected final boolean _fwd;

		protected final Boolean _large;

		private boolean _doRepeat;

		private java.util.Timer _timer;
		
		
		/**
		 * 
		 * @param fwd whether this button moves forward or backwards
		 * @param large whether this button moves in large or small steps
		 * @param doRepeat whether this button repeats if held down
		 */
		public RepeatingTimeButtonListener(final boolean fwd, final Boolean large, final boolean doRepeat)
		{
			_fwd = fwd;
			_large = large;
			_doRepeat = doRepeat;
		}

		public void process()
		{
			try
			{
				processClick(_large, _fwd);
			}
			catch (final RuntimeException e1)
			{
				CorePlugin
						.logError(Status.ERROR, "Failed when trying to time step", e1);
				purge();
			}
		}

		public void handleEvent(Event event)
		{
			int type = event.type;
			if (type == SWT.MouseDown) {
				// clear any previous timers
				purge();
				
				// fire a single step
				process();
				
				// do we need a repeating behaviour?
				if(_doRepeat)
				{
					// initiate a timer to give repeating behaviour
					_timer = new java.util.Timer();
					TimerTask task = new TimerTask()
					{						
						@Override
						public void run()
						{
							// fire a single step
							process();
						}
					};
					_timer.scheduleAtFixedRate(task, DELAY, DELAY);					
				}
			}
			if (type == SWT.MouseUp) {
				purge();
			}
		}
		
		/** clear any active timer
		 * 
		 */
		public void purge() {
			if (_timer != null) {
				_timer.cancel();
				_timer.purge();
				_timer = null;
			}
		}
	}

	// /////////////////////////////////////////////////////////////////
	// AND PROPERTY EDITORS FOR THE
	// /////////////////////////////////////////////////////////////////

	public void setFocus()
	{
		// ok - put the cursor on the time sldier
		if (!_tNowSlider.isDisposed())
		{
			_tNowSlider.setFocus();
		}

	}

	/**
	 * @param memento
	 */
	public void saveState(final IMemento memento)
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
	public void init(final IViewSite site, final IMemento memento)
			throws PartInitException
	{

		super.init(site, memento);

		if (memento != null)
		{

			// try the slider step size
			final Integer stepSize = memento.getInteger(SLIDER_STEP_SIZE);
			if (stepSize != null)
			{
				_defaultSliderResolution = stepSize;
			}
		}
	}

	/**
	 * provide the currently selected period
	 * 
	 * @return
	 */
	public TimePeriod getPeriod()
	{
		return getPeriodSlider().getPeriod();
	}

	/**
	 * provide some support for external testing
	 */
	public void doTests()
	{
		// check we have some data
		TestCase.assertNotNull("check we have time to control", _controllableTime);
		TestCase.assertNotNull("check we have time provider", _myTemporalDataset);
		TestCase.assertNotNull("check we have period to control",
				_controllablePeriod);

		Object oldDtgFormat = _myStepperProperties
				.getPropertyValue(TimeControlProperties.DTG_FORMAT_ID);

		try
		{
			_myStepperProperties.setPropertyValue(
					TimeControlProperties.DTG_FORMAT_ID, new Integer(4));

			final HiResDate tDemanded = new HiResDate(0, 818748000000000L);
			// note - time equates to: 120600:00

			// ok, try stepping forward. get the current time
			final HiResDate tNow = _myTemporalDataset.getTime();

			// step forward one
			final Event ev = new Event();
			_forwardButton.notifyListeners(SWT.Selection, ev);

			// find the new time
			final HiResDate tNew = _myTemporalDataset.getTime();

			TestCase.assertNotSame("time has changed", "" + tNew.getMicros(), ""
					+ tNow.getMicros());

			// ok, go back to the demanded time (in case we loaded the plot with a
			// different saved time)
			_controllableTime.setTime(new Integer(111), tDemanded, true);

			// have a look at the date
			final String timeStr = _timeLabel.getText();

			// check it's what we're expecting
			TestCase.assertEquals("time is correct", timeStr, "120600:00");

		}
		finally
		{
			_myStepperProperties.setPropertyValue(
					TimeControlProperties.DTG_FORMAT_ID, oldDtgFormat);
		}
	}

	// /////////////////////////////////////////////////
	// RELATIVE PROJECTION-RELATED BITS
	// /////////////////////////////////////////////////

	public double getHeading()
	{
		double res = 0;
		if (_relativeProjector != null)
			res = _relativeProjector.getHeading();
		return res;
	}

	public WorldLocation getLocation()
	{
		WorldLocation res = null;
		if (_relativeProjector != null)
			res = _relativeProjector.getLocation();
		return res;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter)
	{
		Object res = null;
		if (adapter == TimePeriod.class)
		{
			// NOTE: xy plot plugin relies on getting this time period value from the
			// time controller
			res = getPeriod();
		}
		else
			res = super.getAdapter(adapter);

		return res;
	}

	public TimeProvider getTimeProvider()
	{
		return _myTemporalDataset;
	}

}
