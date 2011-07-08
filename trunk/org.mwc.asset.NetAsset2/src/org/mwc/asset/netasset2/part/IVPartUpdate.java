package org.mwc.asset.netasset2.part;

import ASSET.Participants.Status;

public interface IVPartUpdate extends IVPart
{
	void moved(Status status);
}
