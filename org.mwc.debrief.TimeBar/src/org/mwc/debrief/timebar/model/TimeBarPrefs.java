package org.mwc.debrief.timebar.model;

public interface TimeBarPrefs
{
  /** whether track segments should be collapsed
   * 
   * @return
   */
  public boolean collapseSegments();
  
  /** whether individual sensors should be collapsed
   * 
   * @return
   */
  public boolean collapseSensors();
}
