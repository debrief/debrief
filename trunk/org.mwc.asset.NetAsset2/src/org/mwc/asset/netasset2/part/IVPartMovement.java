package org.mwc.asset.netasset2.part;

import ASSET.Participants.Status;

public interface IVPartMovement extends IVPart
{
	void moved(Status status);
}
