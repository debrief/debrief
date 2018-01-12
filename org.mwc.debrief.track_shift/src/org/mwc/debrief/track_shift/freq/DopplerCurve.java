package org.mwc.debrief.track_shift.freq;

import java.util.ArrayList;

public class DopplerCurve
{
  private final ArrayList<Long> _times;
  private final ArrayList<Double> _freqs;

  public DopplerCurve(final ArrayList<Long> times, final ArrayList<Double> freqs)
  {
    _times = times;
    _freqs = freqs;
    
    // ok, fit the data
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

  public double valueAt(final long t)
  {
    return 149.5 + Math.random() * 1d;
  }
}
