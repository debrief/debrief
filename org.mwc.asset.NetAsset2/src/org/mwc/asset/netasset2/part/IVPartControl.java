package org.mwc.asset.netasset2.part;

public interface IVPartControl extends IVPart
{

	public static interface NewDemStatus
	{
		public void demanded(double course, double speed, double depth);
	}

	String getDemDepth();

	String getDemSpeed();

	String getDemCourse();

	void setDemStatusListener(NewDemStatus newDemStatus);

	void setEnabled(boolean val);

}
