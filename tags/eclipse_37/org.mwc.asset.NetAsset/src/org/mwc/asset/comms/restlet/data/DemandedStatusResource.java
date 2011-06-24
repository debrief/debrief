package org.mwc.asset.comms.restlet.data;

import java.io.Serializable;

import org.restlet.resource.Put;


/**
 * The resource associated to a contact.
 */
public interface DemandedStatusResource
{
	public static class NetDemStatus implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final double course;
		public final double speed;
		public final double depth;
		public NetDemStatus(double courseVal, double speedVal, double depthVal)
		{
			course = courseVal;
			speed = speedVal;
			depth = depthVal;
		}
	}

	@Put
	public void store(NetDemStatus newState);
}
