package org.mwc.debrief.track_shift.freq;
public interface IDopplerCurve
{

  public abstract double inflectionFreq();

  public abstract long inflectionTime();

  /**
   * calculate the value on the curve at this time
   * 
   * @param t
   *          time
   * @return frequency at this time
   */
  public abstract double valueAt(long t);


  /** output the coordinates
   * 
   */
  public abstract void printCoords();

}