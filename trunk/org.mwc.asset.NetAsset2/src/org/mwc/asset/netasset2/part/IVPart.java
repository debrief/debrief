package org.mwc.asset.netasset2.part;


public interface IVPart
{

	public static interface NewDemStatus
	{
		public void demanded(double course, double speed, double depth);
	}
	
	String getDemDepth();

	void setActSpeed(String val);

	void setActCourse(String val);

	void setActDepth(String val);

	String getDemSpeed();

	String getDemCourse();

	void setEnabled(boolean val);

	void setParticipant(String name);

	void setDemStatusListener(NewDemStatus newDemStatus);

}
