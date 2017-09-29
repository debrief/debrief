package org.mwc.debrief.track_shift.ambiguity.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants
{
  public static final String MIN_ZIG = "CutOff";
  
  /** whether to output slicing diagnostics
   * 
   */
  public static final String DIAGNOSTICS = "OutputDiagnostics";
  
  /** whether to display slicing UI controls
   * 
   */
  public static final String DISPLAY = "ShowControls";

  /** value at which we decide platform is steady (degs/sec)
   * 
   */
  public static final String MAX_STEADY = "Max_Steady";
  
  /** minimum length of period of cuts to treat them as steady
   * 
   */
  public static final String MIN_LEG_LENGTH = "MinLegLength";
  
}
