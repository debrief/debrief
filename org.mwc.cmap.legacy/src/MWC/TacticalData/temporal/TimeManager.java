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
package MWC.TacticalData.temporal;

import java.beans.*;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
public class TimeManager implements ControllableTime, TimeProvider
{

  /**
   * manage all of the listeners, etc.
   */
  private PropertyChangeSupport _pSupport;

  /**
   * the time period covered by the data
   */
  private TimePeriod _timePeriod;

  /**
   * the current time
   */
  private HiResDate _currentTime;

  /**
   * the id of this provider
   * 
   */
  private String _myId;

  public TimeManager()
  {
    _myId = "" + System.currentTimeMillis();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.cmap.core.DataTypes.Temporal.ControllableTime#setTime(java.lang.Object,
   * MWC.GenericData.HiResDate)
   */
  public void setTime(final Object origin, final HiResDate newDate,
      final boolean fireUpdate)
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
        _pSupport.firePropertyChange(TIME_CHANGED_PROPERTY_NAME, oldTime,
            _currentTime);
      }
    }

    // done.
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.mwc.cmap.core.DataTypes.Temporal.TimeProvider#getPeriod()
   */
  public TimePeriod getPeriod()
  {
    return _timePeriod;
  }

  public void fireTimePropertyChange()
  {
    // do we have any listeners?
    if (_pSupport != null)
    {
      _pSupport.firePropertyChange(TIME_CHANGED_PROPERTY_NAME, null,
          _currentTime);
    }
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

  /**
   * let somebody start listening to our changes
   * 
   * @param listener
   *          the new listener
   * @param propertyType
   *          the (optional) property to listen to. Use null if you don't mind
   */
  public void addListener(final PropertyChangeListener listener,
      final String propertyType)
  {
    if (_pSupport == null)
      _pSupport = new PropertyChangeSupport(this);

    _pSupport.addPropertyChangeListener(propertyType, listener);
  }

  /**
   * let somebody stop listening to our changes
   * 
   * @param listener
   *          the old listener
   * @param propertyType
   *          the (optional) property to stop listening to. Use null if you don't mind
   */
  public void removeListener(final PropertyChangeListener listener,
      final String propertyType)
  {
    _pSupport.removePropertyChangeListener(propertyType, listener);
  }

  /**
   * let somebody specify the time period we're managing.
   * 
   * @param origin
   *          - whoever is setting the time period (so that they can optionally ignore changes they
   *          triggered)
   * @param period
   *          - the new time period
   */
  public void setPeriod(final Object origin, final TimePeriod period)
  {
    // remember the old period
    final TimePeriod oldPeriod = _timePeriod;

    // store the new time
    _timePeriod = period;

    // do we have any listeners?
    if (_pSupport != null)
    {
      _pSupport.firePropertyChange(PERIOD_CHANGED_PROPERTY_NAME, oldPeriod,
          _timePeriod);
    }

  }

  public String getId()
  {
    return _myId;
  }

  public void setId(final String val)
  {
    _myId = val;
  }

}
