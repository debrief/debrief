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
package org.mwc.cmap.core.DataTypes.Temporal;

import java.beans.PropertyChangeEvent;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.*;
import org.mwc.cmap.core.property_support.DurationHelper;

import MWC.GUI.Properties.*;
import MWC.GenericData.*;

public class TimeControlProperties extends java.beans.PropertyChangeSupport
		implements IPropertySource2, TimeControlPreferences
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the small step size
	 */
	private Duration _smallStep, _defaultSmallStep;

	/**
	 * and the large step size
	 */
	private Duration _largeStep, _defaultLargeStep;

	/**
	 * and the time interval between auto-steps
	 */
	private Duration _autoInterval, _defaultAutoInterval;

	/**
	 * and the format for the DTG
	 */
	private Integer _dtgFormat, _defaultFormat;

	static private String[] DTG_FORMAT_STRINGS;

	/**
	 * and the slider limits (which may be different to the period of the data
	 * 
	 */
	private HiResDate _sliderStart, _sliderEnd;

	/**
	 * the property names
	 */
	final public static String LARGE_STEP_ID = "Large Step";

	final public static String SMALL_STEP_ID = "Small Step";

	final public static String STEP_INTERVAL_ID = "Step Interval";

	final public static String DTG_FORMAT_ID = "DTG Format";

	final public static String SLIDER_LIMITS_ID = "Slider limits";

	/**
	 * static instances of our properties
	 */
	private PropertyDescriptor LARGE_STEP = null;

	private PropertyDescriptor SMALL_STEP = null;

	private PropertyDescriptor AUTO_STEP = null;

	private PropertyDescriptor DTG_FORMAT = null;

	private PropertyDescriptor[] PROPERTY_DESCRIPTORS = null;

	// //////////////////////////////////////////////////////////////
	// constructor
	// ///////////////////////////////////////////////////////////////

	/**
	 * create/initialise our set of properties
	 */
	public TimeControlProperties()
	{
		super(LARGE_STEP_ID);

		// ok, set the default values
		_smallStep = _defaultSmallStep = new Duration(1, Duration.MINUTES);
		_largeStep = _defaultLargeStep = new Duration(10, Duration.MINUTES);
		_autoInterval = _defaultAutoInterval = new Duration(1, Duration.SECONDS);
		_dtgFormat = _defaultFormat = new Integer(3);

		// see if our tags need declaring
		if (DTG_FORMAT_STRINGS == null)
			DTG_FORMAT_STRINGS = DateFormatPropertyEditor.getTagList();

	}

	public boolean isPropertyResettable(final Object id)
	{
		return true;
	}

	public Object getEditableValue()
	{
		return this;
	}

	public synchronized IPropertyDescriptor[] getPropertyDescriptors()
	{
		if (LARGE_STEP == null)
		{

			// ok, better create our property descriptors

			LARGE_STEP = new PropertyDescriptor(LARGE_STEP_ID, LARGE_STEP_ID)
			{
				public CellEditor createPropertyEditor(final Composite parent)
				{
					return new DurationHelper.DurationCellEditor(parent);
				}
			};
			LARGE_STEP.setAlwaysIncompatible(true);
			LARGE_STEP.setDescription("The size of the large time step");

			SMALL_STEP = new PropertyDescriptor(SMALL_STEP_ID, SMALL_STEP_ID)
			{
				public CellEditor createPropertyEditor(final Composite parent)
				{
					return new DurationHelper.DurationCellEditor(parent);
				}

			};
			SMALL_STEP.setAlwaysIncompatible(true);
			SMALL_STEP.setDescription("The size of the small time step");

			// AUTO_STEP = new PropertyDescriptor(STEP_INTERVAL_ID, STEP_INTERVAL_ID)
			// {
			// public CellEditor createPropertyEditor(Composite parent)
			// {
			// return new DurationHelper.DurationCellEditor(parent);
			// }
			// };
			AUTO_STEP = new PropertyDescriptor(STEP_INTERVAL_ID, STEP_INTERVAL_ID)
			{
				public CellEditor createPropertyEditor(final Composite parent)
				{
					return new DurationHelper.TimeIntervalEditor(parent);
				}

				/**
				 * @return
				 */
				public ILabelProvider getLabelProvider()
				{
					final ILabelProvider provider = new LabelProvider()
					{
						public String getText(final Object element)
						{
							// ok, this is a duration. get the duration itself
							final Duration dur = (Duration) element;

							// find which is the matching entry
							final TimeIntervalPropertyEditor ep = new TimeIntervalPropertyEditor();
							ep.setValue(new Integer((int) dur
									.getValueIn(Duration.MILLISECONDS)));

							return ep.getAsText();
						}

					};
					return provider;
				}

			};
			AUTO_STEP.setAlwaysIncompatible(true);
			AUTO_STEP.setDescription("The interval between automatic time steps");

			DTG_FORMAT = new ComboBoxPropertyDescriptor(DTG_FORMAT_ID, DTG_FORMAT_ID,
					DTG_FORMAT_STRINGS)
			{
			};
			DTG_FORMAT.setAlwaysIncompatible(true);
			DTG_FORMAT.setDescription("The format to use to display the DTG");

			// hey, don't bother putting the DTG format in the properties window
			// anymore - we've got it in the time-controller's
			// drop-down menu
			PROPERTY_DESCRIPTORS = new PropertyDescriptor[]
			{ LARGE_STEP, SMALL_STEP, AUTO_STEP };

		}

		return PROPERTY_DESCRIPTORS;
	}

	public Object getPropertyValue(final Object id)
	{
		Object res = null;
		if (id == LARGE_STEP_ID)
			res = _largeStep;
		else if (id == SMALL_STEP_ID)
			res = _smallStep;
		else if (id == STEP_INTERVAL_ID)
			res = _autoInterval;
		else if (id == DTG_FORMAT_ID)
			res = _dtgFormat;

		return res;
	}

	public void resetPropertyValue(final Object id)
	{
		Object oldVal = null;
		Object newVal = null;

		if (id == LARGE_STEP_ID)
		{
			oldVal = _largeStep;
			newVal = _largeStep = _defaultLargeStep;
		}
		else if (id == SMALL_STEP_ID)
		{
			oldVal = _smallStep;
			newVal = _smallStep = _defaultSmallStep;
		}
		else if (id == STEP_INTERVAL_ID)
		{
			oldVal = _autoInterval;
			newVal = _autoInterval = _defaultAutoInterval;
		}
		else if (id == DTG_FORMAT_ID)
		{
			oldVal = _dtgFormat;
			newVal = _dtgFormat = _defaultFormat;
		}

		fireChange((String) id, oldVal, newVal);
	}

	public void setPropertyValue(final Object id, final Object value)
	{
		Object oldVal = null;
		Object newVal = null;

		if (id == LARGE_STEP_ID)
		{
			oldVal = _largeStep;
			newVal = _largeStep = (Duration) value;
		}
		else if (id == SMALL_STEP_ID)
		{
			oldVal = _smallStep;
			newVal = _smallStep = (Duration) value;
		}
		else if (id == STEP_INTERVAL_ID)
		{
			oldVal = _autoInterval;
			newVal = _autoInterval = (Duration) value;
		}
		else if (id == DTG_FORMAT_ID)
		{
			oldVal = _dtgFormat;
			newVal = _dtgFormat = (Integer) value;
		}

		fireChange((String) id, oldVal, newVal);

	}

	public boolean isPropertySet(final Object id)
	{
		boolean res = false;
		if (id == LARGE_STEP_ID)
			res = _largeStep == _defaultLargeStep;
		else if (id == SMALL_STEP_ID)
			res = _smallStep == _defaultSmallStep;
		else if (id == STEP_INTERVAL_ID)
			res = _autoInterval == _defaultAutoInterval;
		else if (id == DTG_FORMAT_ID)
			res = _dtgFormat.equals(_defaultFormat);

		return res;
	}

	private void fireChange(final String name, final Object oldValue, final Object newValue)
	{
		final PropertyChangeEvent pe = new PropertyChangeEvent(this, name, oldValue,
				newValue);
		super.firePropertyChange(pe);
	}

	public String getDTGFormat()
	{
		return DTG_FORMAT_STRINGS[_dtgFormat.intValue()];
	}

	// ////////////////////////////////////////////////////
	// AND ACCESSING THE PROPERTIES THEMSELVES
	// ////////////////////////////////////////////////////

	public Duration getSmallStepSize()
	{
		return _smallStep;
	}

	public Duration getLargeStepSize()
	{
		return _largeStep;
	}

	public void setDTGFormat(final String format)
	{
		final int index = DateFormatPropertyEditor.getIndexOf(format);

		// ok, what's the index
		// if (index == DateFormatPropertyEditor.INVALID_INDEX)
		// {
		// // bugger, didn't find it.
		// index = 0;
		// }

		// ok, sorted.
		_dtgFormat = new Integer(index);
	}

	public Duration getSmallStep()
	{
		return _smallStep;
	}

	public Duration getLargeStep()
	{
		return _largeStep;
	}

	public void setSmallStep(final Duration step)
	{
		if (step != null)
			_smallStep = step;

	}

	public void setLargeStep(final Duration step)
	{
		if (step != null)
			_largeStep = step;
	}

	public Duration getAutoInterval()
	{
		return _autoInterval;
	}

	public void setAutoInterval(final Duration duration)
	{
		_autoInterval = duration;
	}

	public HiResDate getSliderStartTime()
	{
		return _sliderStart;
	}

	public HiResDate getSliderEndTime()
	{
		return _sliderEnd;
	}

	public void setSliderStartTime(final HiResDate dtg)
	{
		// take a copy, to start with
		final HiResDate oldDTG = dtg;

		// update the value
		_sliderStart = dtg;

		// and fire the update
		firePropertyChange(SLIDER_LIMITS_ID, oldDTG, _sliderStart);

	}

	public void setSliderEndTime(final HiResDate dtg)
	{
		// take a copy, to start with
		final HiResDate oldDTG = dtg;

		// update the value
		_sliderEnd = dtg;

		// and fire the update
		firePropertyChange(SLIDER_LIMITS_ID, oldDTG, _sliderEnd);
	}
}
