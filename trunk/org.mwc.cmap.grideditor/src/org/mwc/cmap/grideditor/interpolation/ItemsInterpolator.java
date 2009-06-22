package org.mwc.cmap.grideditor.interpolation;

import MWC.GUI.TimeStampedDataItem;

public interface ItemsInterpolator {

	public boolean canInterpolate(TimeStampedDataItem item);

	public Object getInterpolatedValue(TimeStampedDataItem item);
}
