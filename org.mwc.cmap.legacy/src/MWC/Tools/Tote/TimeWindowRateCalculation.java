package MWC.Tools.Tote;

/**
 * Tagging interface used to indicate if we are showing the Time Window property
 * in the outline.
 *
 */
public interface TimeWindowRateCalculation extends DeltaRateToteCalculation {
	public long getWindowSizeMillis();
	
	public void setWindowSizeMillis(final long newWindowSize);
}
