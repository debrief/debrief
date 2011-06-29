package org.mwc.asset.comms.kryo.common;

/** callback for network data calls
 * 
 * @author ianmayo
 *
 * @param <T>
 */
public abstract class ACallback<T>
{
	public void onFailure(Throwable caught)
	{
		System.err.println(caught);
	}
	
	abstract public void onSuccess(T result);
}
