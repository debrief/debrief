package org.mwc.debrief.track_shift.freq;

import java.util.ArrayList;

public class DopplerCurve
{
  @SuppressWarnings("unused")
  private final ArrayList<Long> _times;
  
  @SuppressWarnings("unused")
  private final ArrayList<Double> _freqs;

  public DopplerCurve(final ArrayList<Long> times, final ArrayList<Double> freqs)
  {
    _times = times;
    _freqs = freqs;
    
    // ok, fit the data
    
    // now check if there's an inflection point
    
    // and store the equation parameters
  }
  
  public boolean hasInflection()
  {
    return false;
  }
  
  public double inflectionFreq()
  {
    return -1;
  }
  
  public double inflectionTime()
  {
    return -1;
  }

  /** calculate the value on the curve at this time
   * 
   * @param t time
   * @return frequency at this time
   */
  public double valueAt(final long t)
  {
    return 149.5 + Math.random() * 1d;
  }
}
