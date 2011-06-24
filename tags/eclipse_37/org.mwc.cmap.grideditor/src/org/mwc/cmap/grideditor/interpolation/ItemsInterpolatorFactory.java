package org.mwc.cmap.grideditor.interpolation;

import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;

import MWC.GUI.TimeStampedDataItem;

public interface ItemsInterpolatorFactory {

	public ItemsInterpolator createItemsInterpolator(GriddableItemDescriptor descriptor, TimeStampedDataItem... baseItems);

	public static final ItemsInterpolatorFactory DEFAULT = new ItemsInterpolatorFactory() {

		public ItemsInterpolator createItemsInterpolator(GriddableItemDescriptor descriptor, TimeStampedDataItem... baseItems) {
			if (!AbstractItemsInterpolator.canInterpolate(descriptor)) {
				return null;
			}
			if (baseItems.length == 2) {
				return new LinearItemsInterpolator(baseItems[0], baseItems[1], descriptor);
			}
			if (baseItems.length > 2) {
				return new CubicItemsInterpolator(descriptor, baseItems);
			}
			return null;
		}
	};
}
