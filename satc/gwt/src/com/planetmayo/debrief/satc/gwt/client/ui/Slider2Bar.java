/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.planetmayo.debrief.satc.gwt.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesChangeEvents;

/**
 * A widget that allows the user to select a value within a range of possible
 * values using a sliding bar that responds to mouse events.
 * 
 * 
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-SliderBar-shell { primary style } </li>
 * <li>.gwt-SliderBar-shell-focused { primary style when focused } </li>
 * <li>.gwt-SliderBar-shell gwt-SliderBar-line { the line that the knob moves
 * along } </li>
 * <li>.gwt-SliderBar-shell gwt-SliderBar-line-sliding { the line that the knob
 * moves along when sliding } </li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-knob { the sliding knob } </li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-knob-sliding { the sliding knob when
 * sliding } </li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-tick { the ticks along the line }
 * </li>
 * <li>.gwt-SliderBar-shell .gwt-SliderBar-label { the text labels along the
 * line } </li>
 * </ul>
 * <h4>This is my setup</h4>
 * <pre>
 * .gwt-SliderBar-shell {
 *         border-style: inset;
 *         border-width: 1px;
 * 	background-color: white;
 * }
 * .gwt-SliderBar-line {
 *         border-style: outset;
 *         border-width: 1px;
 * 	background-color: #EEE;
 * 	overflow: hidden;
 * }
 * .gwt-SliderBar-knob {
 * 	z-index: 1;
 * 	cursor: pointer;
 * }
 * .gwt-SliderBar-label {
 * 	font-size: 8pt;
 * 	cursor: default;
 * 	font-weight: bold;
 * }
 * .gwt-SliderBar-label.dimm {
 * 	color: #CCC;
 * 	font-weight: normal;
 * }
 * .gwt-SliderBar-tick {
 * 	width: 1px;
 * 	height: 5px;
 * 	background: black;
 * 	overflow: hidden;
 * }
 * .gwt-SliderBar-tick.dimm {
 * 	width: 1px;
 * 	background: #CCC;
 * }
 * .gwt-SliderBar-line-sliding {
 * 	background-color: #A00;
 * 	cursor: pointer;
 * }
 * </pre>
 * <h3>Example of a 24 range with two sliders and notifications on mouse up only</h4>
 * <pre>
 * 	Slider2Bar.LabelFormatter labelFormater	= new Slider2Bar.LabelFormatter() {
 * 		public String formatLabel(Slider2Bar slider, double value) {
 * 		    return Double.toString(value);
 * 		}
 * 	    };
 * 
 * 	Slider2Bar slider			= new Slider2Bar(0, 24, .5, 320, 12, 24, true, labelFormater);
 * 	slider.addChangeListener(		this);
 * 	slider.setNotifyOnMouseUp(		true);
 * 	slider.setNotifyOnMouseMove(		false);
 * </pre>
 */

public class Slider2Bar extends Composite implements SourcesChangeEvents {
        
    private AbsolutePanel _composite	= null;
    private SimplePanel _knobsLine	= null;
    private Image _knob1Image		= null;
    private Image _knob2Image		= null;
    private List _labelsLst		= new ArrayList();
    private List _ticksLst		= new ArrayList();
    
    private double _current1Value	= 0;
    private double _current2Value	= 1;
    private double _minValue		= 0;
    private double _maxValue		= 1;
    private double _stepSize		= 1;
    private int _numLabels		= 2;
    private int _numTicks		= 2;

    private int _HorizontalOffset 	= 15;
    private int _lineWidth		= 250;
    private int _lineHeight		= 4;
    private int _labelsHeight		= 0;
    private int _ticksHeight		= 0;

    private boolean _slidingMouse1	= false;
    private boolean _slidingMouse2	= false;

    private boolean _has2Knobs		= true;
    private boolean _notifyOnMouseUp	= true;
    private boolean _notifyOnMouseMove	= true;
    
    private SliderBarImageBundle
	_imageBundle			= null;
    private LabelFormatter
	_labelFormatter			= null;
    private ChangeListenerCollection
	_changeListeners		= null;
    
    private EventPreview _eventPreviewer = null;
    
    public void init() {
	// Create the line
	_knobsLine			= new SimplePanel();
	_knobsLine.addStyleName(	"gwt-SliderBar-line");
	
	// Create the knobs
	if (_imageBundle == null)
	    _imageBundle		= (SliderBarImageBundle)GWT.create(SliderBarImageBundle.class);

	_knob1Image			= new Image() {
		public void onBrowserEvent(Event event) {
		    super.onBrowserEvent(event);
		    switch (DOM.eventGetType(event)) {
		    case Event.ONMOUSEDOWN:
			_slidingMouse1			= true;
			DOM.setCapture(			getElement());
			DOM.eventPreventDefault(	event);
			startSliding1();
			break;
		    case Event.ONMOUSEMOVE:
			if (_slidingMouse1)
			    slideKnob1(			event, _notifyOnMouseMove);
			break;
		    case Event.ONMOUSEUP:
			if (_slidingMouse1) {
			    DOM.releaseCapture(		getElement());
			    slideKnob1(			event, _notifyOnMouseUp);
			    stopSliding1();
			    _slidingMouse1		= false;
			}
			break;
		    }
		}
	    };
	_knob1Image.addStyleName(	"gwt-SliderBar-knob");

	if (_has2Knobs) {
	    _knob2Image			= new Image() {
		public void onBrowserEvent(Event event) {
		    super.onBrowserEvent(event);
		    switch (DOM.eventGetType(event)) {
		    case Event.ONMOUSEDOWN:
			_slidingMouse2			= true;
			DOM.setCapture(			getElement());
			DOM.eventPreventDefault(	event);
			startSliding2();
			break;
		    case Event.ONMOUSEMOVE:
			if (_slidingMouse2) {
			    slideKnob2(			event, _notifyOnMouseMove);
			}
			break;
		    case Event.ONMOUSEUP:
			if (_slidingMouse2) {
			    DOM.releaseCapture(		getElement());
			    _slidingMouse2		= false;
			    slideKnob2(			event, _notifyOnMouseUp);
			    stopSliding2();
			}
			break;
		    }
		}
	    };
	    _knob2Image.addStyleName(	"gwt-SliderBar-knob");
	}

	// Create the outer shell
	_composite			= new AbsolutePanel() {
		protected void onLoad() {

		    DeferredCommand.addPause();
		    DeferredCommand.addCommand(new Command() {
			    public void execute() {
				drawAll();
			    }
			});
		}
	    };
	_composite.setStyleName(	"gwt-SliderBar-shell");
	_composite.add(			_knobsLine);
	_composite.add(			_knob1Image);
	if (_has2Knobs)
	    _composite.add(		_knob2Image);


	initWidget(			_composite);

	/*
	_eventPreviewer			= new EventPreview() {
		public boolean onEventPreview(Event event) {
		    if (isVisible()) {
			drawAll();
			DOM.removeEventPreview(_eventPreviewer);
		    }
		    return true;
		}
	    };
	DOM.addEventPreview(_eventPreviewer);
	*/
	//ForceOnFocusStyle.add(		_composite);

	setCurrent1Value(		_minValue);
	if (_has2Knobs)
	    setCurrent2Value(		_maxValue);
	else
	    _current2Value		= _maxValue + _stepSize;
    }

    public Slider2Bar() {
	init();
    }

    public Slider2Bar(double minValue, double maxValue, double stepSize, boolean isDouble) {
	_minValue			= minValue;
	_maxValue			= maxValue;
	_stepSize			= stepSize;
	_has2Knobs			= isDouble;
	init();
    }
    
    public Slider2Bar(double minValue, double maxValue, double stepSize, int lineWidth, int numLabels, int numTicks, boolean isDouble,
		      LabelFormatter labelFormatter) {
		      
	_minValue			= minValue;
	_maxValue			= maxValue;
	_stepSize			= stepSize;
	_lineWidth			= lineWidth;
	_numLabels			= numLabels;
	_numTicks			= numTicks;
	_has2Knobs			= isDouble;
	_labelFormatter			= labelFormatter;
	init();
    }
    
    public Slider2Bar(double minValue, double maxValue, double stepSize, int lineWidth, int numLabels, int numTicks, boolean isDouble,
		      LabelFormatter labelFormatter, SliderBarImageBundle imageBundle) {
	_minValue			= minValue;
	_maxValue			= maxValue;
	_stepSize			= stepSize;
	_lineWidth			= lineWidth;
	_numLabels			= numLabels;
	_numTicks			= numTicks;
	_has2Knobs			= isDouble;
	_labelFormatter			= labelFormatter;
	_imageBundle			= imageBundle;
	init();
    }
    

    public void drawAll() {

	_knobsLine.setWidth(		_lineWidth+"px");
	_knobsLine.setHeight(		_lineHeight+"px");

	drawLabels();
	drawTicks();

	_composite.setWidgetPosition(	_knobsLine, _HorizontalOffset, _labelsHeight + _ticksHeight + 2);
	_composite.setWidth(		(_knobsLine.getOffsetWidth() + (_HorizontalOffset * 2))+"px");
	_composite.setHeight(		(_labelsHeight + _ticksHeight + 2 + _knobsLine.getOffsetHeight() + 4)+"px");

	_imageBundle.knobNormal().applyTo(_knob1Image);
	drawKnob1();
	if (_has2Knobs) {
	    _imageBundle.knobNormal().applyTo(_knob2Image);
	    drawKnob2();
	}

    }

    private void drawLabels() {
	
	// Create the labels or make them visible
	if (_numLabels == 0)
	    return;
	for (int cii = 0; cii <= _numLabels; cii++) {
	    HTML label			= null;
	    if (cii < _labelsLst.size()) {
		label			= (HTML)_labelsLst.get(cii);
	    } else {
		label			= new HTML();
		label.addStyleName(	"gwt-SliderBar-label");
		_composite.add(		label);
		_labelsLst.add(		label);
	    }
	    
	    label.setHTML(		formatLabel(_minValue + (getTotalRange() * cii / _numLabels)));
	    
	    // Position the label
	    int lineWidth		= _knobsLine.getOffsetWidth();
	    int labelLeftOffset		= _HorizontalOffset + (lineWidth * cii / _numLabels) - (label.getOffsetWidth() / 2);
	    _composite.setWidgetPosition(label, labelLeftOffset, 0);
	    _labelsHeight		= Math.max(_labelsHeight, label.getOffsetHeight());
	}
	
	// remove unused labels
	for (int cii = (_numLabels + 1); cii < _labelsLst.size(); cii++)
	    _composite.remove((HTML)_labelsLst.get(cii));
    }

    protected String formatLabel(double value) {
	if (_labelFormatter != null) {
	    return _labelFormatter.formatLabel(this, value);
	} else {
	    return Double.toString((double)(10 * value) / 10.0);
	}
    }

    
    private void drawTicks() {
	
	// Create the ticks or make them visible
	if (_numTicks == 0)
	    return;
	for (int cii = 0; cii <= _numTicks; cii++) {
	    SimplePanel tick			= null;
	    if (cii < _ticksLst.size()) {
		tick			= (SimplePanel)_ticksLst.get(cii);
	    } else {
		tick			= new SimplePanel();
		tick.addStyleName(	"gwt-SliderBar-tick");
		_composite.add(		tick);
		_ticksLst.add(		tick);
	    }
	    
	    // Position the tick and make it visible
	    int lineWidth		= _knobsLine.getOffsetWidth();
	    int tickWidth		= tick.getOffsetWidth();
	    int tickLeftOffset		= _HorizontalOffset + (lineWidth * cii / _numTicks) - (tickWidth / 2);
	    _composite.setWidgetPosition(tick, tickLeftOffset, _labelsHeight);
	    _ticksHeight		= Math.max(_ticksHeight, tick.getOffsetHeight());


	}
	
	// remove unused ticks
	for (int cii = (_numTicks + 1); cii < _ticksLst.size(); cii++)
	    _composite.remove((SimplePanel)_ticksLst.get(cii));
    }


    private void dimmOuterElements() {
	int lineWidth		= _knobsLine.getOffsetWidth();
	int knobWidth		= _knob1Image.getOffsetWidth();
	int knob1LeftOffset	= (int) (_HorizontalOffset + (getKnob1Percent() * lineWidth) - (knobWidth / 2));
	knob1LeftOffset		= Math.min(knob1LeftOffset, _HorizontalOffset + lineWidth - (knobWidth / 2));
	int knob2LeftOffset	= (int) (_HorizontalOffset + (getKnob2Percent() * lineWidth) - (knobWidth / 2));
	knob2LeftOffset		= Math.min(knob2LeftOffset, _HorizontalOffset + lineWidth - (knobWidth / 2));

	knob1LeftOffset	       += knobWidth / 2;
	knob2LeftOffset	       += knobWidth / 2 + 1;

	for (int cii = 0; cii < _labelsLst.size(); cii++) {
	    HTML label		= (HTML)_labelsLst.get(cii);
	    int labelLeftOffset	= _composite.getWidgetLeft(label) + label.getOffsetWidth() / 2;
	    if (labelLeftOffset < knob1LeftOffset  ||  labelLeftOffset > knob2LeftOffset)
		label.addStyleName(	"dimm");
	    else
		label.removeStyleName(	"dimm");
	}
	for (int cii = 0; cii < _ticksLst.size(); cii++) {
	    SimplePanel tick	= (SimplePanel)_ticksLst.get(cii);
	    int tickLeftOffset	= _composite.getWidgetLeft(tick) + tick.getOffsetWidth() / 2;
	    if (tickLeftOffset < knob1LeftOffset  ||  tickLeftOffset > knob2LeftOffset)
		tick.addStyleName(	"dimm");
	    else
		tick.removeStyleName(	"dimm");
	}

    }

    private void drawKnob(double knobPercent, Image knobImage) {
	int lineWidth		= _knobsLine.getOffsetWidth();
	int knobWidth		= knobImage.getOffsetWidth();
	int knobLeftOffset	= (int) (_HorizontalOffset + (knobPercent * lineWidth) - (knobWidth / 2));
	knobLeftOffset		= Math.min(knobLeftOffset, _HorizontalOffset + lineWidth - (knobWidth / 2));

	_composite.setWidgetPosition(knobImage, knobLeftOffset, _labelsHeight + _ticksHeight + 2 - knobImage.getOffsetHeight() / 2);

	dimmOuterElements();
    }


    private void drawKnob1() {

	drawKnob(getKnob1Percent(), _knob1Image);
    }
    private void drawKnob2() {

	drawKnob(getKnob2Percent(), _knob2Image);
    }
    
    // Getters				///////////////////////////////////////////////////////////////////////////////////////////////////

    public double getCurrent1Value() {
	return _current1Value;
    }
    public double getCurrent2Value() {
	return _current2Value;
    }

    public double getMinValue() {
	return _minValue;
    }
    public double getMaxValue() {
	return _maxValue;
    }
    
    public int getNumLabels() {
	return _numLabels;
    }
    
    public int getNumTicks() {
	return _numTicks;
    }
    
    public double getStepSize() {
	return _stepSize;
    }
    
    public LabelFormatter getLabelFormatter() {
	return _labelFormatter;
    }

    public boolean getHas2Knobs() {
	return _has2Knobs;
    }
    public boolean getNotifyOnMouseUp() {
	return _notifyOnMouseUp;
    }
    public boolean getNotifyOnMouseMove() {
	return _notifyOnMouseMove;
    }


    /**
     * Return the total range between the minimum and maximum values.
     * 
     * @return the total range
     */
    public double getTotalRange() {
	if (_minValue > _maxValue) {
	    return 0;
	} else {
	    return _maxValue - _minValue;
	}
    }
    
    /**
     * Get the percentage of the knob's position relative to the size of the line.
     * The return value will be between 0.0 and 1.0.
     * 
     * @return the current percent complete
     */
    protected double getKnob1Percent() {
	// If we have no range
	if (_maxValue <= _minValue)
	    return 0;
	
	// Calculate the relative progress
	double percent			= (_current1Value - _minValue) / (_maxValue - _minValue);
	return Math.max(0.0, Math.min(1.0, percent));
    }
    
    protected double getKnob2Percent() {
	// If we have no range
	if (_maxValue <= _minValue)
	    return 0;
	
	// Calculate the relative progress
	double percent			= (_current2Value - _minValue) / (_maxValue - _minValue);
	return Math.max(0.0, Math.min(1.0, percent));
    }
    
    
    // Setters				///////////////////////////////////////////////////////////////////////////////////////////////////
    
    public void setCurrent1Value(double current1Value) {
	setCurrent1Value(current1Value, true);
    }
    public void setCurrent1Value(double current1Value, boolean fireEvent) {
	// Confine the value to the range
	_current1Value			= Math.max(_minValue, Math.min(_maxValue, current1Value));
	double remainder		= (_current1Value - _minValue) % _stepSize;
	_current1Value		       -= remainder;
	
	// Go to next step if more than halfway there
	if (remainder  >  (_stepSize / 2)  &&  (_current1Value + _stepSize) <= _maxValue)
	    _current1Value	       += _stepSize;
	
	drawKnob1();
	
	if (fireEvent  &&  _changeListeners != null)
	    _changeListeners.fireChange(this);
    }

    public void setCurrent2Value(double current2Value) {
	setCurrent2Value(current2Value, true);
    }
    public void setCurrent2Value(double current2Value, boolean fireEvent) {
	// Confine the value to the range
	_current2Value			= Math.max(_minValue, Math.min(_maxValue, current2Value));
	double remainder		= (_current2Value - _minValue) % _stepSize;
	_current2Value		       -= remainder;
	
	// Go to next step if more than halfway there
	if (remainder  >  (_stepSize / 2)  &&  (_current2Value + _stepSize) <= _maxValue)
	    _current2Value	       += _stepSize;
	
	drawKnob2();
	
	if (fireEvent  &&  _changeListeners != null)
	    _changeListeners.fireChange(this);
    }
    
    public void setMinValue(double _minValue) {
	_minValue			= _minValue;
	drawAll();
    }
    
    public void setMaxValue(double _maxValue) {
	_maxValue			= _maxValue;
	drawAll();
    }
    
    public void setStepSize(double stepSize) {
	_stepSize = stepSize;
	drawAll();
    }
    
    /**
     * Set the number of labels to show on the line. Labels indicate the value of
     * the slider at that point. Use this method to enable labels.
     * 
     * If you set the number of labels equal to the total range divided by the
     * step size, you will get a properly aligned "jumping" effect where the knob
     * jumps between labels.
     * 
     * Note that the number of labels displayed will be one more than the number
     * you specify, so specify 1 labels to show labels on either end of the line.
     * In other words, numLabels is really the number of slots between the labels.
     * 
     * setNumLabels(0) will disable labels.
     * 
     * @param numLabels the number of labels to show
     */
    public void setNumLabels(int numLabels) {
	_numLabels			= numLabels;
	drawAll();
    }
    
    /**
     * Set the number of ticks to show on the line. A tick is a vertical line that
     * represents a division of the overall line. Use this method to enable ticks.
     * 
     * If you set the number of ticks equal to the total range divided by the step
     * size, you will get a properly aligned "jumping" effect where the knob jumps
     * between ticks.
     * 
     * Note that the number of ticks displayed will be one more than the number
     * you specify, so specify 1 tick to show ticks on either end of the line. In
     * other words, numTicks is really the number of slots between the ticks.
     * 
     * setNumTicks(0) will disable ticks.
     * 
     * @param numTicks the number of ticks to show
     */
    public void setNumTicks(int numTicks) {
	_numTicks = numTicks;
	drawAll();
    }
    
    
    public void setLabelFormatter(LabelFormatter labelFormatter) {
	_labelFormatter = labelFormatter;
	drawAll();
    }
    
    public void setHas2Knobs(boolean has2Knobs) {
	_has2Knobs			= has2Knobs;
    }
    public void setNotifyOnMouseUp(boolean notifyOnMouseUp) {
	_notifyOnMouseUp		= notifyOnMouseUp;
    }
    public void setNotifyOnMouseMove(boolean notifyOnMouseMove) {
	_notifyOnMouseMove		= notifyOnMouseMove;
    }

    //					///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Slide the knob to a new location.
     * 
     * @param event the mouse event
     */
    private void slideKnob1(Event event, boolean update) {
	int x			= DOM.eventGetClientX(event);
	if (x > 0) {
	    int lineWidth	= _knobsLine.getOffsetWidth();
	    int lineLeft	= _knobsLine.getAbsoluteLeft();
	    double percent	= (double)(x - lineLeft) / lineWidth * 1.0;
	    setCurrent1Value(Math.min(getCurrent2Value() - _stepSize, getTotalRange() * percent + _minValue), update);
	}
    }
    private void slideKnob2(Event event, boolean update) {
	int x			= DOM.eventGetClientX(event);
	if (x > 0) {
	    int lineWidth	= _knobsLine.getOffsetWidth();
	    int lineLeft	= _knobsLine.getAbsoluteLeft();
	    double percent	= (double) (x - lineLeft) / lineWidth * 1.0;
	    setCurrent2Value(Math.max(getCurrent1Value() + _stepSize, getTotalRange() * percent + _minValue), update);
	}
    }
    
    private void startSliding1() {
	_knobsLine.addStyleName("gwt-SliderBar-line-sliding");
	_knob1Image.addStyleName("gwt-SliderBar-knob-sliding");
	_imageBundle.knobClicked().applyTo(_knob1Image);
    }
    private void startSliding2() {
	_knobsLine.addStyleName("gwt-SliderBar-line-sliding");
	_knob2Image.addStyleName("gwt-SliderBar-knob-sliding");
	_imageBundle.knobClicked().applyTo(_knob2Image);
    }
    
    private void stopSliding1() {
	_knobsLine.removeStyleName("gwt-SliderBar-line-sliding");

	_knob1Image.removeStyleName("gwt-SliderBar-knob-sliding");
	_imageBundle.knobNormal().applyTo(_knob1Image);

    }
    private void stopSliding2() {
	_knobsLine.removeStyleName("gwt-SliderBar-line-sliding");

	_knob2Image.removeStyleName("gwt-SliderBar-knob-sliding");
	_imageBundle.knobNormal().applyTo(_knob2Image);
    }
    

    // Interface SourcesChangeEvents		/////////////////////////////////////////////////////////////////////

    public void addChangeListener(ChangeListener listener) {
	if (_changeListeners == null) {
	    _changeListeners = new ChangeListenerCollection();
	}
	_changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
	if (_changeListeners != null) {
	    _changeListeners.remove(listener);
	}
    }

    //				 		/////////////////////////////////////////////////////////////////////
    /**
     * An {@link ImageBundle} that provides images for {@link Slider2Bar}.
     */
    public static interface SliderBarImageBundle extends ImageBundle {
	/**
	 * An image used for the sliding knob.
	 * 
	 * @return a prototype of this image
	 */
	AbstractImagePrototype knobNormal();
	
	/**
	 * An image used for the sliding knob while sliding.
	 * 
	 * @return a prototype of this image
	 */
	AbstractImagePrototype knobClicked();
    }


    //				 		/////////////////////////////////////////////////////////////////////
    /**
     * A formatter used to format the labels displayed in the widget.
     */
    public static interface LabelFormatter {
	/**
	 * Generate the text to display in each label based on the label's value.
	 * 
	 * Override this method to change the text displayed within the Slider2Bar.
	 * 
	 * @param slider the Slider bar
	 * @param value the value the label displays
	 * @return the text to display for the label
	 */
	public abstract String formatLabel(Slider2Bar slider, double value);
    }
    

}
