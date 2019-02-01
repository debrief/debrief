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
package org.mwc.asset.scenariocontroller2.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioSteppedListener;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.TacticalData.temporal.SteppableTime;
import MWC.TacticalData.temporal.TimeProvider;

public class WrappingSteppableTime implements SteppableTime, TimeProvider,
		ScenarioSteppedListener
{

	/**
	 * manage all of the listeners, etc.
	 */
	private PropertyChangeSupport _pSupport;

	private ScenarioType _myScenario;

	private HiResDate _currentTime;

	private final String _myId;

	public WrappingSteppableTime()
	{
		super();

		// and store the system time as an id
		_myId = "" + System.currentTimeMillis();
	}

	/**
	 * let somebody start listening to our changes
	 * 
	 * @param listener
	 *          the new listener
	 * @param propertyType
	 *          the (optional) property to listen to. Use null if you don't mind
	 */
	public void addListener(final PropertyChangeListener listener, final String propertyType)
	{
		if (_pSupport == null)
			_pSupport = new PropertyChangeSupport(this);

		_pSupport.addPropertyChangeListener(propertyType, listener);
	}

	public String getId()
	{
		return _myId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.cmap.core.DataTypes.Temporal.TimeProvider#getPeriod()
	 */
	public TimePeriod getPeriod()
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mwc.cmap.core.DataTypes.Temporal.TimeProvider#getTime()
	 */
	public HiResDate getTime()
	{
		return _currentTime;
	}

	public void pause(final Object origin, final boolean fireUpdate)
	{
		if (_myScenario != null)
		{
			_myScenario.pause();
		}
	}

	/**
	 * let somebody stop listening to our changes
	 * 
	 * @param listener
	 *          the old listener
	 * @param propertyType
	 *          the (optional) property to stop listening to. Use null if you
	 *          don't mind
	 */
	public void removeListener(final PropertyChangeListener listener,
			final String propertyType)
	{
		_pSupport.removePropertyChangeListener(propertyType, listener);
	}

	public void restart(final Object origin, final boolean fireUpdate)
	{
		if (_myScenario != null)
		{
			_myScenario.restart();
		}
	}

	public void restart(final ScenarioType scenario)
	{

	}

	public void run(final Object origin, final boolean fireUpdate)
	{
		if (_myScenario != null)
		{
			_myScenario.start();
		}
	}

	/**
	 * we're looking at a new scenario - go for it
	 * 
	 * @param scenario
	 *          the scenario to watch
	 */
	public void setCurrentScenario(final ScenarioType scenario)
	{
		if (scenario != _myScenario)
		{
			stopWatching();
		}

		_myScenario = scenario;

		startWatching();
	}

	/**
	 * let somebody specify the time period we're managing.
	 * 
	 * @param origin
	 *          - whoever is setting the time period (so that they can optionally
	 *          ignore changes they triggered)
	 * @param period
	 *          - the new time period
	 */
	public void setPeriod(final Object origin, final TimePeriod period)
	{
		// ignore, we don't handle this
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mwc.cmap.core.DataTypes.Temporal.ControllableTime#setTime(java.lang
	 * .Object, MWC.GenericData.HiResDate)
	 */
	public void setTime(final Object origin, final HiResDate newDate, final boolean fireUpdate)
	{
		// ok. remember the old time (if we have one)
		HiResDate oldTime = null;
		if (_currentTime != null)
			oldTime = new HiResDate(_currentTime);

		// store the new time
		_currentTime = newDate;

		// do we want to fire the update?
		if (fireUpdate)
		{
			// do we have any listeners?
			if (_pSupport != null)
			{
				final PropertyChangeListener[] theseListeners = _pSupport
						.getPropertyChangeListeners(TIME_CHANGED_PROPERTY_NAME);
				for (int i = 0; i < theseListeners.length; i++)
				{
					final PropertyChangeListener pcl = theseListeners[i];
					pcl.propertyChange(new PropertyChangeEvent(this,
							TIME_CHANGED_PROPERTY_NAME, oldTime, newDate));
				}
			}
		}

		// done.
	}

	private void startWatching()
	{
		_myScenario.addScenarioSteppedListener(this);
		final long curTime = _myScenario.getTime();
		if (curTime != -1)
		{
			setTime(_myScenario, new HiResDate(curTime), true);
		}
	}

	public void step(final Object origin, final boolean fireUpdate)
	{
		if (_myScenario != null)
		{
			_myScenario.step();
		}
	}

	public void step(final ScenarioType scenario, final long newTime)
	{
		setTime(scenario, new HiResDate(newTime), true);
	}

	public void stop(final Object origin, final boolean fireUpdate)
	{
		if (_myScenario != null)
		{
			_myScenario.stop("User request");
		}
	}

	private void stopWatching()
	{
		if (_myScenario != null)
		{
			_myScenario.removeScenarioSteppedListener(this);
		}
	}

}
