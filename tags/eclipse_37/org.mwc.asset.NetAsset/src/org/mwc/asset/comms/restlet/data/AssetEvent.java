/**
 * 
 */
package org.mwc.asset.comms.restlet.data;

import java.io.Serializable;

public class AssetEvent implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String JOINED = "JOINED";

	public static final String LEFT = "LEFT";

	final public long time;

	protected AssetEvent(final long timeVal)
	{
		time = timeVal;
	}
}