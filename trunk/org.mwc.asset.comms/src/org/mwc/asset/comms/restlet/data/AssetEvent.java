/**
 * 
 */
package org.mwc.asset.comms.restlet.data;

public class AssetEvent
{
	public static final String JOINED = "JOINED";

	public static final String LEFT = "LEFT";

	final public long time;
	public AssetEvent(long timeVal)
	{
		time = timeVal;
	}
}