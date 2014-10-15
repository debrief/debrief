/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
