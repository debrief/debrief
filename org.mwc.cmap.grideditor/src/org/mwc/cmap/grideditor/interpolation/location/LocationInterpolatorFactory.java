/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
	public Object getAdapter(final Object adaptableObject, final Class adapterType) {
		if (false == adaptableObject instanceof GriddableItemDescriptorExtension) {
			return null;
		}
		if (!ItemsInterpolatorFactory.class.isAssignableFrom(adapterType)) {
			return null;
		}
		final GriddableItemDescriptorExtension descriptor = (GriddableItemDescriptorExtension) adaptableObject;
		if (WorldLocation.class.isAssignableFrom(descriptor.getType())) {
			return this;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { ItemsInterpolatorFactory.class };
	}

	public ItemsInterpolator createItemsInterpolator(final GriddableItemDescriptor descriptor, final TimeStampedDataItem... baseItems) {
		if (baseItems.length == 2) {
			return new LinearLocationInterpolator(descriptor, baseItems);
		}
		if (baseItems.length > 2) {
			return new CubicLocationInterpolator(descriptor, baseItems);
		}
		return null;
	}

}
