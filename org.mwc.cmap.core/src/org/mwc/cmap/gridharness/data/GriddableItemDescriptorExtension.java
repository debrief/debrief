
package org.mwc.cmap.gridharness.data;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.mwc.cmap.core.property_support.EditorHelper;


public class GriddableItemDescriptorExtension extends GriddableItemDescriptor implements IAdaptable {

	private final String mySampleString;

	public GriddableItemDescriptorExtension(final String name, final String displayName, final Class<?> typeClass, final EditorHelper editor, final String sampleString) {
		super(name, displayName, typeClass, editor);
		mySampleString = sampleString;
	}

	public String getSampleString() {
		return mySampleString;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

}
