package org.mwc.cmap.core.property_support;

public interface IDebriefProperty
{

	public void setValue(Object text);

	Object getValue();
	Object getRawValue();

	public String getDisplayName();

	public EditorHelper getHelper();

}
