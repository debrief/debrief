/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)

 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
