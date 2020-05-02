package Debrief.Tools.Tote;

import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;

public interface DeltaRateToteCalculation extends toteCalculation {

	/**
	 * Produce the calculated average using the time windows given
	 * 
	 * @param primary
	 * @param thisTime
	 * @param windowSizeMillis
	 * @return
	 */
	public double[] calculate(Watchable[] primary, HiResDate[] thisTime, final long windowSizeMillis);
}
