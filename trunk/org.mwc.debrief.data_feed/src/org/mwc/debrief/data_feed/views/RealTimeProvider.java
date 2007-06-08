package org.mwc.debrief.data_feed.views;

/** interface representing classes that are capable of providing a 
 * live data feed.
 * @author ian.mayo
 *
 */
public interface RealTimeProvider
{
	/** connect to the data-feed
	 * 
	 * @param host
	 */
	public void connect(LiveFeedViewer host);
	
	/** disconnect from the data-feed
	 * 
	 * @param host
	 */
	public void disconnect(LiveFeedViewer host);
	
	/** provide the name of this data-feed
	 * 
	 * @return
	 */
	public String getName();
}
