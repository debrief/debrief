package org.mwc.cmap.gridharness.data;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.mwc.cmap.core.property_support.EditorHelper;


public class GriddableItemDescriptorExtension extends GriddableItemDescriptor implements IAdaptable {

	private final String mySampleString;

	public GriddableItemDescriptorExtension(String title, String name, Class<?> typeClass, EditorHelper editor, String sampleString) {
		super(title, name, typeClass, editor);
		mySampleString = sampleString;
	}

	public String getSampleString() {
		return mySampleString;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

}
