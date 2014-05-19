package MWC.GUI;

import MWC.GenericData.HiResDate;

public interface TimeStampedDataItem {
	public HiResDate getDTG();
	public void setDTG(HiResDate date);
}
