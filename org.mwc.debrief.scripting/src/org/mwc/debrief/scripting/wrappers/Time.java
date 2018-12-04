/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, Deep Blue C Technology Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.debrief.scripting.wrappers;

import org.eclipse.ease.modules.WrapToScript;

public class Time
{
  public static class DTime
  {
    /**
     * the automatic timer we are using
     */
    //private final MWC.Utilities.Timer.Timer _myTimer;
    
    

    @WrapToScript
    public static long getTime()
    {
      return 0;
    }

    @WrapToScript
    public static long getStart()
    {
      return 0;
    }

    @WrapToScript
    public static long finishTime()
    {
      return 0;
    }

    @WrapToScript
    public static long getSmall()
    {
      return 0;
    }

    @WrapToScript
    public static long largeStep()
    {
      return 0;
    }

    @WrapToScript
    public static long setTime(final long _time)
    {
      return 0;
    }

    @WrapToScript
    public static long setSmall()
    {
      return 0;
    }

    @WrapToScript
    public static long largeStep(final long _step)
    {
      return 0;
    }

    @WrapToScript
    public static long doStep()
    {
      return 0;
    }

    @WrapToScript
    public static long play()
    {
      // ok - make sure the time has the right time
      //getTimer().setDelay(delayToUse);

      //getTimer().start();
      return 0;
    }

    @WrapToScript
    public static long stop()
    {
      return 0;
    }
    
    
  }
  
}
