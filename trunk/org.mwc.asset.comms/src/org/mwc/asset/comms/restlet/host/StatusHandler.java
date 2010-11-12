package org.mwc.asset.comms.restlet.host;

import java.util.Map;

import org.mwc.asset.comms.restlet.data.DemandedStatusResource;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import ASSET.Models.Movement.SimpleDemandedStatus;
import ASSET.Participants.DemandedStatus;
import ASSET.Participants.Status;

public class StatusHandler extends ServerResource implements
		DemandedStatusResource
{

	private static DemandedStatus _thisD;

	@Get
	public DemandedStatus retrieve()
	{
		Map<String, Object> attrs = this.getRequestAttributes();
		Object thisP = attrs.get("participant");
		Status status = new Status(12, 34);
//		if(_thisD == null)
		{
			int theId = Integer.parseInt((String) thisP);
			_thisD = new SimpleDemandedStatus(theId, status );
		}
		return _thisD;
	}

	@Put
	public void store(DemandedStatus demState)
	{
		Map<String, Object> attrs = this.getRequestAttributes();
		Object thisS = attrs.get("scenario");
		Object thisP = attrs.get("participant");
		_thisD = demState;
		System.out.println("redirect " + thisS + ", " + thisP + " to:" + demState);
	}
}