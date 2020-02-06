
package MWC.GUI;

import java.awt.Color;

import MWC.GenericData.HiResDate;

public interface TimeStampedDataItem {
	public HiResDate getDTG();
	public void setDTG(HiResDate date);
	public Color getColor();
}
