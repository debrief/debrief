package Debrief.GUI.Tote.Swing;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingStepControl.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.10 $
// $Log: SwingStepControl.java,v $
// Revision 1.10  2006/04/05 08:34:53  Ian.Mayo
// Minor error checking
//
// Revision 1.9  2005/12/13 09:04:28  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.8  2005/02/14 14:24:38  Ian.Mayo
// Only reset the slider date if we know the start time
//
// Revision 1.7  2005/02/10 09:55:44  Ian.Mayo
// When restoring a plot file from disk, there a (slim) chance that we have current toolbox slider times set, but no outer limits.  Put in handling to overcome these problems.
//
// Revision 1.6  2005/01/28 16:25:11  Ian.Mayo
// Include major-minor step sizes
//
// Revision 1.5  2005/01/24 10:30:08  Ian.Mayo
// Don't fire update event when we're first configuring the step control
//
// Revision 1.4  2004/12/01 09:14:02  Ian.Mayo
// Stop us from firing step changed twice
//
// Revision 1.3  2004/11/26 11:37:45  Ian.Mayo
// Moving closer, supporting checking for time resolution
//
// Revision 1.2  2004/11/25 10:24:08  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:47:28  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.14  2003-06-10 15:32:54+01  ian_mayo
// Remove unused code
//
// Revision 1.13  2003-06-06 15:22:23+01  ian_mayo
// Still maturing the keyboard handlers
//
// Revision 1.12  2003-05-14 16:12:49+01  ian_mayo
// Improved time slider processing
//
// Revision 1.11  2003-05-13 12:18:58+01  ian_mayo
// Put time slider into time stepper
//
// Revision 1.10  2003-05-02 11:26:24+01  ian_mayo
// minor tidying, and remember to set our start and end times when we receive the setStartTime etc calls
//
// Revision 1.9  2003-03-28 12:09:07+00  ian_mayo
// Correct tests to correct new time zero handling
//
// Revision 1.8  2003-03-25 15:55:21+00  ian_mayo
// better support for time-zero, including values on time-var graphs
//
// Revision 1.7  2003-03-21 15:43:02+00  ian_mayo
// Replace stuff which shouldn't have been deleted by IntelliJ inspector
//
// Revision 1.6  2003-03-19 15:37:52+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.5  2003-03-10 10:23:22+00  ian_mayo
// Class renamed
//
// Revision 1.4  2002-10-01 15:40:54+01  ian_mayo
// Improve testing
//
// Revision 1.3  2002-05-28 12:28:03+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:16+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-04-23 16:06:46+01  ian_mayo
// Switch to repeater buttons
//
// Revision 1.1  2002-04-23 12:29:52+01  ian_mayo
// Initial revision
//
// Revision 1.17  2002-02-19 20:22:39+00  administrator
// Set GUI component names to assist JFCUnit testing
//
// Revision 1.16  2002-02-18 20:16:04+00  administrator
// Name the TimeText label, and put the T+ in quotes in the testing
//
// Revision 1.15  2002-02-18 09:22:09+00  administrator
// Set the name of the GUI component (largely so that we can access it from JFCUnit)
//
// Revision 1.14  2002-01-29 07:53:13+00  administrator
// Use Trace method instead of System.out
//
// Revision 1.13  2002-01-24 14:23:36+00  administrator
// Reflect change in Layers reformat and modified events which take an indication of which layer has been modified - a step towards per-layer graphics repaints
//
// Revision 1.12  2002-01-22 15:29:23+00  administrator
// Reflect changed signature in Toolbar so that it can float
//
// Revision 1.11  2002-01-17 15:02:59+00  administrator
// Reflect new interface to hide StepperListener class
//
// Revision 1.10  2001-10-03 16:06:39+01  administrator
// Rename cursor to display
//
// Revision 1.9  2001-10-03 10:13:29+01  administrator
// Remove edit cursor button, add show layer manager button
//
// Revision 1.8  2001-08-31 16:19:40+01  administrator
// Add testing
//
// Revision 1.7  2001-08-31 13:26:05+01  administrator
// Add support for showing time as T-Zero rather than absolute time
//
// Revision 1.6  2001-08-31 11:13:52+01  administrator
// Remove commented-out code
//
// Revision 1.5  2001-08-31 09:50:25+01  administrator
// Comitted to opening Time/Track toolbox using new button
//
// Revision 1.4  2001-08-21 12:16:08+01  administrator
// Improve tidying
//
// Revision 1.3  2001-08-17 08:00:58+01  administrator
// Clear up memory leaks, and switch anonymous classes to local instances
//
// Revision 1.2  2001-08-06 16:58:44+01  administrator
// Offer the property change event to the parent
//
// Revision 1.1  2001-08-06 14:39:25+01  administrator
// set the UI of the Toolbar to our SPECIAL ui
//
// Revision 1.0  2001-07-17 08:41:37+01  administrator
// Initial revision
//
// Revision 1.4  2001-07-05 11:50:04+01  novatech
// we need to pass the properties panel as a Swing properties panel when we open the TimeFilter page, so make it so!
//
// Revision 1.3  2001-06-14 15:41:52+01  novatech
// reflect new format for toolbar constructor
//
// Revision 1.2  2001-01-17 09:43:29+00  novatech
// name change
//
// Revision 1.1  2001-01-03 13:40:51+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:46:07  ianmayo
// initial import of files
//
// Revision 1.23  2000-12-01 10:15:22+00  ian_mayo
// use parent date formatter
//
// Revision 1.22  2000-11-17 09:15:00+00  ian_mayo
// correction to definition of key handlers
//
// Revision 1.21  2000-10-09 13:37:32+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.20  2000-10-03 14:15:40+01  ian_mayo
// provide accessor for propertyPanel, together with abstract method to trigger edit of Highlighter
//
// Revision 1.19  2000-09-27 14:47:17+01  ian_mayo
// name changes
//
// Revision 1.18  2000-09-21 09:04:37+01  ian_mayo
// remove jdk1.3-dependent method call: setBorderPaintedFlat
//
// Revision 1.17  2000-09-18 09:13:59+01  ian_mayo
// GUI label changes
//
// Revision 1.16  2000-09-14 10:47:28+01  ian_mayo
// correct label for Filter button
//
// Revision 1.15  2000-08-21 15:45:38+01  ian_mayo
// tidying up, particularly listening to PainterManager changes
//
// Revision 1.14  2000-08-16 14:12:30+01  ian_mayo
// tidy up retrieval of images
//
// Revision 1.13  2000-08-07 14:05:38+01  ian_mayo
// correct image naming
//
// Revision 1.12  2000-08-07 12:23:19+01  ian_mayo
// tidy icon filename
//
// Revision 1.11  2000-05-19 11:24:46+01  ian_mayo
// pass undoBuffer around, to undo TimeFilter operations
//
// Revision 1.10  2000-04-19 11:31:14+01  ian_mayo
// put in more tooltips
//
// Revision 1.9  2000-04-03 10:46:18+01  ian_mayo
// add 'Filter' functionality, and store chart data so we can trigger an update on its completion
//
// Revision 1.8  2000-03-27 14:43:39+01  ian_mayo
// Show toolbuttons in two rows, and provide combo box to quickly select painter
//
// Revision 1.7  2000-03-17 13:37:04+00  ian_mayo
// Try to handle getting called repeatedly
//
// Revision 1.6  2000-03-14 15:01:06+00  ian_mayo
// switch to use of myJButton
//
// Revision 1.5  2000-03-14 09:46:57+00  ian_mayo
// switch to use of icons
//
// Revision 1.4  2000-02-02 14:29:15+00  ian_mayo
// minor tidying up
//
// Revision 1.3  1999-12-03 14:37:51+00  ian_mayo
// added keyboard shortcuts
//
// Revision 1.2  1999-11-23 10:23:35+00  ian_mayo
// switched to use of toolbar
//
// Revision 1.1  1999-11-18 11:12:22+00  ian_mayo
// new Swing versions
//

import Debrief.GUI.Tote.StepControl;
import Debrief.GUI.Tote.Painters.PainterManager;
import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.*;
import MWC.GUI.Properties.Swing.SwingPropertiesPanel;
import MWC.GUI.Tools.Swing.MyMetalToolBarUI;
import MWC.GUI.Tools.Swing.RepeaterButton;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public final class SwingStepControl extends StepControl implements
		java.awt.event.ActionListener, java.beans.PropertyChangeListener
{
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	private JToolBar _theToolbar;

	private JButton _smallFwd;

	private JButton _smallBwd;

	private JButton _largeFwd;

	private JButton _largeBwd;

	private JButton _editStepBtn;

	private JButton _showLayerMgrBtn;

	private JButton _startBtn;

	private JButton _endBtn;

	private JLabel _timeTxt;

	private SwingPropertiesPanel _theEditor;

	private JToggleButton _autoBtn;

	private JComboBox _thePainterSelector;

	/**
	 * slider to move quickly through the time period
	 */
	private JSlider _timeSlider;

	/**
	 * keep a reference to the listener for the combo box, so that we can later
	 * delete it
	 */
	private ActionListener _comboListener;

	/**
	 * keep a reference to the listener for the button, so that we can later
	 * delete it
	 */
	private ActionListener _stepActionListener;

	/**
	 * keep a reference to the listener for the button, so that we can later
	 * delete it
	 */
	private ActionListener _paintActionListener;

	/**
	 * keep a reference to the listener for the button, so that we can later
	 * delete it
	 */
	private ActionListener _filterActionListener;

	/**
	 * keep a reference to the listener for the combo box, so that we can later
	 * delete it
	 */
	private ItemListener _itemListener;

	// button for the filter
	private JButton _filterBtn;

	/**
	 * the data we need for the time filter
	 */
	private Layers _theData;

	/**
	 * the chart we need for the time filter
	 */
	private PlainChart _theChart;

	/**
	 * the undo buffer
	 */
	private MWC.GUI.Undo.UndoBuffer _theUndoBuffer;

	/**
	 * whether our time slider is running in milli or microseconds
	 */
	private boolean _sliderInMicros;

	/**
	 * the session name, used when we float our toolbar
	 */
	protected final MyMetalToolBarUI.ToolbarOwner _owner;

	/**
	 * busy flag. When we get a newTime instruction, we update the time and the
	 * slider position. Updating the slider position was firing a time-changed
	 * type event - so time gets called twice. we don't want that. So, when we are
	 * processing a time change we set _updatingForm to true, so that we know to
	 * ignore any slider position updates. cool.
	 */
	private boolean _updatingForm = false;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public SwingStepControl(final SwingPropertiesPanel theEditor, final Layers theData,
			final PlainChart theChart, final MWC.GUI.Undo.UndoBuffer theBuffer,
			final MyMetalToolBarUI.ToolbarOwner owner, ToolParent theParent)
	{

		super(theParent);

		_theEditor = theEditor;

		_theData = theData;

		_theChart = theChart;

		_theUndoBuffer = theBuffer;

		_owner = owner;

		initForm();

		formatTimeText();

	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////
	public final void closeMe()
	{
		// get the parent to close itself
		super.closeMe();

		// and clear the dangling references to the undo buffer
		if (_theUndoBuffer != null)
		{
			_theUndoBuffer.close();
			_theUndoBuffer = null;
		}

		// now the GUI components
		if (_theToolbar != null)
		{
			_theToolbar.removeAll();
			_theToolbar = null;
		}

		_thePainterSelector.removeActionListener(_comboListener);
		_thePainterSelector = null;
		_comboListener = null;

		_autoBtn.removeItemListener(_itemListener);
		_itemListener = null;
		_autoBtn = null;

		_filterBtn.removeActionListener(_filterActionListener);
		_filterBtn = null;
		_filterActionListener = null;

		_editStepBtn.removeActionListener(_stepActionListener);
		_editStepBtn = null;
		_stepActionListener = null;

		_showLayerMgrBtn.removeActionListener(_paintActionListener);
		_paintActionListener = null;
		_showLayerMgrBtn = null;

		_theData = null;
		_theChart = null;
		_theEditor = null;
		_timeFilter = null;
		_theUndoBuffer = null;
		_timeSlider = null;

	}

	protected final void initForm()
	{
		_theToolbar = new JToolBar();
		_theToolbar.setFloatable(true);
		// correct the UI for the component - the real one doesn't make the toolbar
		// stay on top!
		_theToolbar.setUI(new MWC.GUI.Tools.Swing.MyMetalToolBarUI(_owner));
		_theToolbar.setBorderPainted(true);
		_theToolbar.setLayout(new BorderLayout());
		_theToolbar.setName("Time controls");

		// create containers for the two rows of buttons
		final JPanel _topRow = new JPanel();
		final JPanel _bottomRow = new JPanel();
		final JPanel _middleRow = new JPanel();
		_middleRow.setLayout(new BorderLayout());
		_theToolbar.add("North", _topRow);
		_theToolbar.add("Center", _middleRow);
		_theToolbar.add("South", _bottomRow);

		// now create the time slider, and listen to it...
		_timeSlider = new JSlider(0, 100);
		_timeSlider.setToolTipText("Drag this to quickly move through time period");
		_timeSlider.setMinimum(0);

		_middleRow.add("Center", _timeSlider);
		_timeSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (!_updatingForm)
				{
					// process the slider
					newSliderTime();
				}
			}
		});

		final FlowLayout lm = new FlowLayout();
		lm.setVgap(0);
		lm.setHgap(0);
		_topRow.setLayout(lm);

		final FlowLayout lm2 = new FlowLayout();
		lm2.setVgap(0);
		lm2.setHgap(0);
		_bottomRow.setLayout(lm2);

		// reduce the size of the inserts for between the two components of the
		// toolbar
		_theToolbar.setMargin(new Insets(0, 0, 0, 0));

		// create the edit button
		_editStepBtn = new myJButton("Properties", "images/properties.gif");
		_stepActionListener = new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				doEditStep();
			}
		};
		_editStepBtn.addActionListener(_stepActionListener);

		// create the manual/auto button
		_autoBtn = new ImageCheckbox("Auto", "images/timer.gif", "images/timer_down.gif");
		_autoBtn.setSelected(false);
		_itemListener = new ItemListener()
		{
			public void itemStateChanged(final ItemEvent e)
			{
				doAuto(_autoBtn.isSelected());
			}
		};
		_autoBtn.addItemListener(_itemListener);
		_autoBtn.setToolTipText("Step Automatically");

		// create the layer manager viewer button
		_showLayerMgrBtn = new myJButton("View Layer Manager", "images/layer_mgr.gif");
		_paintActionListener = new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				doShowLayerManager();
			}
		};
		_showLayerMgrBtn.addActionListener(_paintActionListener);

		// create the simple buttons
		_startBtn = new myJButton("First", "images/first.gif");
		_largeBwd = new RepeaterButton("Large bwd", "images/double_prior.gif");
		_smallBwd = new RepeaterButton("Step bwd", "images/prior.gif");

		_timeTxt = new JLabel("-----", JLabel.CENTER);
		_timeTxt.setName("Time Text");
		_smallFwd = new RepeaterButton("Step fwd", ("images/next.gif"));
		_largeFwd = new RepeaterButton("Large fwd", "images/double_next.gif");
		_endBtn = new myJButton("Last", "images/last.gif");

		// add the buttons to the step panel
		_bottomRow.add(_editStepBtn);
		_bottomRow.add(_autoBtn);

		_thePainterSelector = new JComboBox();
		_thePainterSelector.setName("PainterSelector");
		// create our own (internal listener) for the combo box
		_comboListener = new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				setPainter((String) _thePainterSelector.getSelectedItem());
			}
		};
		_thePainterSelector.addActionListener(_comboListener);
		_thePainterSelector.setToolTipText("Current Display Mode");

		// the filtering support
		_filterBtn = new myJButton("Filter", "images/filter_up.gif");
		_filterBtn.setToolTipText("Track & Time toolbox");
		_filterActionListener = new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				doFilter();
			}
		};
		_filterBtn.addActionListener(_filterActionListener);

		_bottomRow.add(_thePainterSelector);
		_bottomRow.add(_filterBtn);
		_bottomRow.add(_showLayerMgrBtn);

		// now the left hand time step buttons
		_topRow.add(_startBtn);
		_topRow.add(_largeBwd);
		_topRow.add(_smallBwd);

		// add the other components
		_topRow.add(_timeTxt);

		// put the left hand components in
		_topRow.add(_smallFwd);
		_topRow.add(_largeFwd);
		_topRow.add(_endBtn);

		// and the event handlers
		_startBtn.addActionListener(this);
		_largeBwd.addActionListener(this);
		_smallBwd.addActionListener(this);
		_smallFwd.addActionListener(this);
		_largeFwd.addActionListener(this);
		_endBtn.addActionListener(this);

		// register the (fake) event handlers
		createKeystrokeHandlers();

	}

	/**
	 * add a single keystroke handler
	 */
	private void assignThisKeystrokeHandler(JComponent component, JButton source,
			int keyCode, int modifier)
	{
		KeyStroke newKeyStroke = KeyStroke.getKeyStroke(keyCode, modifier);
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(newKeyStroke, source);
		component.getActionMap().put(source, new FakeEvent(source));

	}

	/**
	 * add keystroke handlers
	 */
	private void createKeystrokeHandlers()
	{
		assignThisKeystrokeHandler(_theToolbar, _startBtn, KeyEvent.VK_HOME,
				InputEvent.SHIFT_MASK);
		assignThisKeystrokeHandler(_theToolbar, _largeBwd, KeyEvent.VK_PAGE_UP,
				InputEvent.SHIFT_MASK);
		assignThisKeystrokeHandler(_theToolbar, _smallBwd, KeyEvent.VK_PAGE_UP, 0);
		assignThisKeystrokeHandler(_theToolbar, _endBtn, KeyEvent.VK_END,
				InputEvent.SHIFT_MASK);
		assignThisKeystrokeHandler(_theToolbar, _largeFwd, KeyEvent.VK_PAGE_DOWN,
				InputEvent.SHIFT_MASK);
		assignThisKeystrokeHandler(_theToolbar, _smallFwd, KeyEvent.VK_PAGE_DOWN, 0);

		// assignThisKeystrokeHandler(_theToolbar, _startBtn, KeyEvent.VK_HOME);
		// assignThisKeystrokeHandler(_theToolbar, _largeBwd, KeyEvent.VK_UP);
		// assignThisKeystrokeHandler(_theToolbar, _smallBwd, KeyEvent.VK_LEFT);
		// assignThisKeystrokeHandler(_theToolbar, _endBtn, KeyEvent.VK_END);
		// assignThisKeystrokeHandler(_theToolbar, _largeFwd, KeyEvent.VK_DOWN);
		// assignThisKeystrokeHandler(_theToolbar, _smallFwd, KeyEvent.VK_RIGHT);

	}

	private void newSliderTime()
	{
		// calculate the new time
		HiResDate currentSliderTime = getSliderDate();

		if (currentSliderTime != null)
		{
			// and update our control to reflect this
			changeTime(currentSliderTime);
		}
	}

	/*****************************************************************************
	 * TIME SLIDER MANAGEMENT BITS
	 ****************************************************************************/

	/**
	 * find out what the current time is, according to the slider
	 * 
	 * @return hi-res date value
	 */
	private HiResDate getSliderDate()
	{
		HiResDate res = null;

		if ((_timeSlider != null) && (getStartTime() != null))
		{
			long curValue = _timeSlider.getValue();

			if (!_sliderInMicros)
			{
				curValue *= 1000;
			}

			long newDate = getStartTime().getMicros() + curValue;

			res = new HiResDate(0, newDate);
		}
		return res;
	}

	private void setSliderDate(HiResDate newDate)
	{

		long offset = newDate.getMicros() - getStartTime().getMicros();

		if (!_sliderInMicros)
		{
			offset /= 1000;
		}

		_timeSlider.setValue((int) offset);

	}

	/**
	 * have a look at the range, and decide if we are running in milli or micro
	 * second resolution
	 */
	private final void resetTimeSlider()
	{
		// do we know our limits?
		if ((getStartTime() != null) && (getEndTime() != null))
		{
			// yes - initialise the ranges
			long range = getEndTime().getMicros() - getStartTime().getMicros();

			if (range > 0)
			{
				// remember that we are updating the form. don't bother processing state
				// changed events for a bit
				_updatingForm = true;

				if (range < Integer.MAX_VALUE)
				{
					_timeSlider.setMaximum((int) range);
					_timeSlider.setEnabled(true);
					_sliderInMicros = true;
				}
				else
				{
					long rangeMillis = range / 1000;
					if (rangeMillis < Integer.MAX_VALUE)
					{
						// ok, we're going to run in millisecond resolution
						_timeSlider.setMaximum((int) rangeMillis);
						_sliderInMicros = false;
						_timeSlider.setEnabled(true);
					}
					else
					{
						// hey, we must be running in microseconds
						_timeSlider.setEnabled(false);
					}
				}

				// ok. just sort out the step size when the user clicks on the slider
				int smallTick;
				int largeTick;
				int NUM_MILLIS_FOR_STEP;
				if (_sliderInMicros)
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

				_timeSlider.setMinorTickSpacing(smallTick);
				_timeSlider.setMajorTickSpacing(largeTick);

				// ok, we've finished updating the form. back to normal processing
				_updatingForm = false;
			}
		}
	}

	/**
	 * edit step button has been pressed (even though we don't actually implement
	 * this button any more
	 */
	public void doEditPainter()
	{
		// has the editor been assigned?
		if (_theToolbar != null)
		{
			// can we get the painter
			StepperListener painter = super.getCurrentPainter();
			if (painter instanceof Editable)
			{
				Editable el = (Editable) painter;
				if (el.hasEditor())
				{
					Editable.EditorType et = el.getInfo();
					_theEditor.addEditor(et, null);
				}
			}

		}
	}

	/**
	 * register with the painter manager
	 */
	protected final void painterIsDefined()
	{
		_thePainterManager.addPropertyChangeListener(this);

		// add ourselves as a listener to the Editable object, which is favoured
		// over the above.
		_thePainterManager.getInfo().addPropertyChangeListener(this);

	}

	/**
	 * prepare the list of painters in the ComboBox
	 */
	private void prepareComboBox()
	{
		if (_thePainterSelector.getItemCount() > 0)
		{
			// clear the list
			_thePainterSelector.removeAllItems();
		}

		// check that the painter manager has been defined
		if (_thePainterManager == null)
			return;

		// get rid of the current listener for the paint selector
		_thePainterSelector.removeActionListener(_comboListener);

		// get the list of new items
		final String[] items = PainterManager.getListeners();

		// step through the list
		if (items != null)
		{
			for (int i = 0; i < items.length; i++)
			{
				// add the items
				_thePainterSelector.addItem(items[i]);
			}

			// set the current item
			_thePainterSelector.setSelectedItem(_thePainterManager.getDisplay());

		}

		_thePainterSelector.addActionListener(_comboListener);
	}

	/**
	 * method to reformat stepper text
	 */
	protected final void formatTimeText()
	{
		final Font ft = _timeTxt.getFont();
		final Font fNew = new Font(ft.getName(), ft.getStyle(), _fontSize);
		_timeTxt.setFont(fNew);
	}

	public final java.awt.Component getPanel()
	{
		return _theToolbar;
	}

	/**
	 * respond to update event as triggered by GUI-independent parent
	 */
	public final void updateForm(final HiResDate DTG)
	{
		String newTime = getNewTime(DTG);
		_timeTxt.setText(newTime);

		// and update the slider to reflect this new time
		if (!_timeSlider.getValueIsAdjusting())
		{
			// do we know our start time yet?
			if (getStartTime() != null)
			{
				_updatingForm = true;
				setSliderDate(DTG);
				_updatingForm = false;
			}
		}
	}

	private static boolean amRunning = false;

	/**
	 * one of our edit buttons has been pressed
	 */
	public final void actionPerformed(final java.awt.event.ActionEvent p1)
	{

		if (amRunning)
		{
			MWC.Utilities.Errors.Trace.trace("Hey, we're busy!");
			return;
		}

		// indicate that we are busy
		amRunning = true;

		try
		{

			// get the name of the control
			boolean fwd = true;
			boolean large = true;
			final JButton b = (JButton) p1.getSource();

			// first sort out which set it is
			if ((b == _startBtn) || (b == _endBtn))
			{
				if (b == _startBtn)
				{
					super.gotoStart();
				}
				else
					super.gotoEnd();
			}
			else
			{
				if (b == _largeBwd)
				{
					fwd = false;
					large = true;
				}
				if (b == _smallBwd)
				{
					fwd = false;
					large = false;
				}
				if (b == _smallFwd)
				{
					fwd = true;
					large = false;
				}
				if (b == _largeFwd)
				{
					fwd = true;
					large = true;
				}

				_goingForward = fwd;
				_largeSteps = large;

				super.doStep(fwd, large);
			}

		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}

		amRunning = false;
	}

	/**
	 * edit step button has been pressed
	 */
	private void doEditStep()
	{
		// has the editor been assigned?
		if (_theToolbar != null)
		{
			// get our edit info
			final Editable.EditorType et2 = getInfo();
			// and open it in the panel
			_theEditor.addEditor(et2, null);
		}
	}

	/**
	 * actually create the time toolbox#
	 */
	private void createToolbox()
	{
		if (_timeFilter == null)
		{
			// create the filter panel
			_timeFilter = new Debrief.GUI.Tote.Swing.TimeFilter.TimeEditorPanel(_theEditor,
					_theData, _theChart, this, _theUndoBuffer);
		}
	}

	/**
	 * handler for filter button being pressed
	 */
	private void doFilter()
	{
		createToolbox();

		// and show it
		_theEditor.show(_timeFilter);

	}

	// retrieve the time currently set in the toolbox
	public final HiResDate getToolboxStartTime()
	{
		HiResDate res = null;
		if (_timeFilter != null)
		{
			res = _timeFilter.getStartTime();
		}
		return res;
	}

	// retrieve the time currently set in the toolbox
	public final HiResDate getToolboxEndTime()
	{
		HiResDate res = null;
		if (_timeFilter != null)
		{
			res = _timeFilter.getEndTime();
		}
		return res;
	}

	/**
	 * set the time in the start slider in the toolbox
	 */
	public final void setToolboxStartTime(final HiResDate val)
	{
		// do we have our filter?
		createToolbox();

		_timeFilter.setStartTime(val);

		// and remember to set our own time
		this.setStartTime(val);
	}

	/**
	 * set the time in the start slider in the toolbox
	 */
	public final void setToolboxEndTime(final HiResDate val)
	{
		// do we have our toolbox?
		createToolbox();

		_timeFilter.setFinishTime(val);

		// and remember
		this.setEndTime(val);
	}

	public void setEndTime(HiResDate val)
	{
		super.setEndTime(val);

		resetTimeSlider();
	}

	public void setStartTime(HiResDate val)
	{
		super.setStartTime(val);

		resetTimeSlider();
	}

	/**
	 * edit step button has been pressed
	 */
	private void doShowLayerManager()
	{
		// do we have our information?
		final MWC.GUI.Tools.Operations.ShowLayers shower = new MWC.GUI.Tools.Operations.ShowLayers(
				null, null, _theEditor, _theData);

		shower.execute();
	}

	/**
	 * return the property panel we were informed about at initialisation
	 */
	protected final MWC.GUI.Properties.PropertiesPanel getPropertiesPanel()
	{
		return _theEditor;
	}

	/**
	 * set the automatic mode as indicated
	 * 
	 * @param go
	 *          boolean whether to go auto or not
	 */
	private void doAuto(final boolean go)
	{

		if (go)
			startTimer();
		else
			stopTimer();
	}

	static final class FakeEvent extends AbstractAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		final JButton _mySource;

		public FakeEvent(final JButton source)
		{
			_mySource = source;
		}

		public final void actionPerformed(final ActionEvent e)
		{
			_mySource.doClick();
		}

	}

	public final void propertyChange(final java.beans.PropertyChangeEvent p1)
	{
		// pass to the arent
		super.propertyChange(p1);

		// update the combo box: refresh the list and reset the currently
		// selected item
		prepareComboBox();
	}

	// ///////////////////////////////////////////////////////////
	// nested classes
	// ///////////////////////////////////////////////////////////
	static final class myJButton extends JButton
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public myJButton(final String name, final String theIcon)
		{
			// load the icon first
			final java.lang.ClassLoader loader = getClass().getClassLoader();
			java.net.URL myURL = null;
			if (loader != null)
			{
				myURL = loader.getResource(theIcon);
				if (myURL != null)
					setIcon(new ImageIcon(myURL));
			}

			super.setName(name);

			// see if we failed to find icon
			if (myURL == null)
				setText(name);

			setBorderPainted(false);
			setToolTipText(name);
			setMargin(new Insets(0, 0, 0, 0));
			addMouseListener(new MouseAdapter()
			{
				public void mouseEntered(final MouseEvent e)
				{
					setBorderPainted(true);
				}

				public void mouseExited(final MouseEvent e)
				{
					setBorderPainted(false);
				}
			});
		}
	}

	public static final class ImageCheckbox extends JCheckBox
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ImageCheckbox(final String name, final String theIcon,
				final String selectedIcon)
		{

			final java.lang.ClassLoader loader = getClass().getClassLoader();
			java.net.URL iconURL = null;
			java.net.URL selectedIconURL = null;

			if (loader != null)
			{
				iconURL = loader.getResource(theIcon);
				selectedIconURL = loader.getResource(selectedIcon);
				if (iconURL != null)
					setIcon(new ImageIcon(iconURL));
				if (selectedIconURL != null)
					setSelectedIcon(new ImageIcon(selectedIconURL));
			}

			// see if we failed to find icon
			if (iconURL == null)
				setText(name);

			setBorderPainted(false);
			setToolTipText(name);

			this.setName(name);

			// update the UI to show just a line instead of a raised border
			setBorder(new CompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory
					.createEmptyBorder(1, 1, 1, 1)));

			addMouseListener(new MouseAdapter()
			{
				public void mouseEntered(final MouseEvent e)
				{
					setBorderPainted(true);
				}

				public void mouseExited(final MouseEvent e)
				{
					setBorderPainted(false);
				}
			});
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testStepper extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testStepper(final String val)
		{
			super(val);
		}

		public final void testStepperFormatting()
		{

			// create some dummy data so the panel has start/stop times
			Layers layers = new Layers();
			BaseLayer base = new BaseLayer();
			base.setName("Ian's layer");
			layers.addThisLayer(base);
			LabelWrapper label = new LabelWrapper("my label", new WorldLocation(12, 12, 100),
					Color.red, new HiResDate(120000000), new HiResDate(150000000));
			base.add(label);

			// ok, create a new SwingStepControl
			final SwingStepControl ssc = new SwingStepControl(null, layers, null, null, null,
					null);

			// ok, set it's date value
			// get our date format to use
			final String format = "'T+' SSS";

			// check this is valid
			final MWC.GUI.Properties.DateFormatPropertyEditor pe = new MyDateEditor();

			final String[] tags = pe.getTags();

			// check we found tags
			assertNotNull("tags got found from property editor", tags);
			assertEquals("tags found from property editor", 8, tags.length);

			boolean found = false;

			for (int i = 0; i < tags.length; i++)
			{
				final String thisTag = tags[i];
				if (thisTag.equals(format))
				{
					found = true;
					break;
				}
			}
			// did we find this format?
			assertTrue("We are testing with valid format", found);

			// so, use it!
			ssc.setDateFormat(format);

			// now set a default time
			final java.util.Calendar start = new java.util.GregorianCalendar(2001, 5, 7, 12,
					22, 00);

			// and the current
			final java.util.Calendar current = new java.util.GregorianCalendar(2001, 5, 7, 12,
					26, 35);

			// and a previous time
			final java.util.Calendar earlier = new java.util.GregorianCalendar(2001, 5, 7, 12,
					17, 33);

			// check we handle missing start time
			final String blank = ssc.getNewTime(new HiResDate(current.getTime().getTime()));

			assertEquals("Managing fact that sliders not yet initialised", blank, "N/A");

			// set this as the current time
			ssc.setToolboxStartTime(new HiResDate(start.getTime().getTime()));

			// set the time zero
			ssc.setTimeZero(new HiResDate(start.getTime()));

			// and to the test

			// try output before and after our time
			assertEquals("before our time", ssc.getNewTime(new HiResDate(current.getTime()
					.getTime())), "T +275s");
			assertEquals("after our time", ssc.getNewTime(new HiResDate(earlier.getTime()
					.getTime())), "T -267s");

			// hey, try another format
			final String new_format = "'T+' MM:SS";

			ssc.setDateFormat(new_format);

			// try output before and after our time
			assertEquals("before our current time", ssc.getNewTime(new HiResDate(current
					.getTime().getTime())), "T +4:35");
			assertEquals("after our current time", ssc.getNewTime(new HiResDate(earlier
					.getTime().getTime())), "T -4:27");

			// and ditch the stuff
			ssc.closeMe();

		}
	}

	public static void main(final String[] args)
	{
		final testStepper st = new testStepper("here");
		st.testStepperFormatting();
	}

}
