package Debrief.ReaderWriter.Replay;

import MWC.GenericData.HiResDate;

/**
 * Class used to indicate that the Track has to be split
 * at the time DTG.
 * 
 * https://github.com/debrief/debrief/issues/4680
 *
 */
public class TrackSplitOrder {
	
	/**
	 * Where we are going to split the track
	 */
	private final HiResDate DTG;
	
	/**
	 * Track to split.
	 */
	private final String trackName;

	public TrackSplitOrder(HiResDate dTG, String trackName) {
		DTG = dTG;
		this.trackName = trackName;
	}

	public HiResDate getDTG() {
		return DTG;
	}

	public String getTrackName() {
		return trackName;
	}
}
