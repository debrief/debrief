package org.mwc.cmap.grideditor.interpolation.location;

import org.eclipse.core.runtime.IAdapterFactory;
import org.mwc.cmap.grideditor.interpolation.ItemsInterpolator;
import org.mwc.cmap.grideditor.interpolation.ItemsInterpolatorFactory;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptor;
import org.mwc.cmap.gridharness.data.GriddableItemDescriptorExtension;

import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.WorldLocation;


public class LocationInterpolatorFactory implements IAdapterFactory, ItemsInterpolatorFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (false == adaptableObject instanceof GriddableItemDescriptorExtension) {
			return null;
		}
		if (!ItemsInterpolatorFactory.class.isAssignableFrom(adapterType)) {
			return null;
		}
		GriddableItemDescriptorExtension descriptor = (GriddableItemDescriptorExtension) adaptableObject;
		if (WorldLocation.class.isAssignableFrom(descriptor.getType())) {
			return this;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { ItemsInterpolatorFactory.class };
	}

	public ItemsInterpolator createItemsInterpolator(GriddableItemDescriptor descriptor, TimeStampedDataItem... baseItems) {
		if (baseItems.length == 2) {
			return new LinearLocationInterpolator(descriptor, baseItems);
		}
		if (baseItems.length > 2) {
			return new CubicLocationInterpolator(descriptor, baseItems);
		}
		return null;
	}

}
