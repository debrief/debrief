package org.mwc.debrief.data_feed.views;

/** interface for class capable of providing UI control of a data-feed
 * 
 * @author ian.mayo
 *
 */
public interface LiveFeedViewer
{
	/** add a message to the viewer log
	 * 
	 * @param msg
	 */
  public void showMessage(String msg);
  
  /** update the state shown for this data source
   * 
   * @param newState
   */
  public void showState(String newState);
  
  /** extract data from this line of text, insert into plot
   * 
   * @param data
   */
  public void insertData(String data);
}
